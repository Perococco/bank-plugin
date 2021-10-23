package perobobbot.plugin.bank;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import perobobbot.chat.core.IO;
import perobobbot.data.service.BankService;
import perobobbot.data.service.ViewerIdentityService;
import perobobbot.extension.ExtensionBase;
import perobobbot.lang.*;
import perobobbot.oauth.OAuthTokenIdentifierSetter;
import perobobbot.twitch.client.api.TwitchService;
import perobobbot.twitch.eventsub.api.event.ChannelPointsCustomRewardRedemptionAddEvent;

import java.util.stream.Collectors;

@Log4j2
public class BankExtension extends ExtensionBase {

    public static final String REWARD_TO_CREDIT_TAG = "[-BK-]";

    public static final String MESSAGE = "%s %s";
    public static final String NO_POINT = "tu n'as aucun point";

    public static final String NAME = "Bank";

    private final @NonNull IO io;

    private final @NonNull BankService bankService;

    private final @NonNull ViewerIdentityService viewerIdentityService;

    private final @NonNull AddCredit.Adder creditAdder;
    private final @NonNull ConvertChannelPointsToCredits.Converter redemptionConverter;

    private final SubscriptionHolder subscriptionHolder = new SubscriptionHolder();

    public BankExtension(@NonNull IO io,
                         @NonNull BankService bankService,
                         @NonNull TwitchService twitchService,
                         @NonNull ViewerIdentityService viewerIdentityService,
                         @NonNull NotificationDispatcher notificationDispatcher,
                         @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter) {
        super(NAME);
        this.io = io;
        this.creditAdder = AddCredit.createAdder(bankService,viewerIdentityService);
        this.redemptionConverter = ConvertChannelPointsToCredits.createConverter(oAuthTokenIdentifierSetter,creditAdder,twitchService);
        this.bankService = bankService;
        this.viewerIdentityService = viewerIdentityService;
        this.subscriptionHolder.replaceWith(() -> notificationDispatcher.addListener(this::onMessage));
    }

    @Override
    protected void onDisabled() {
        super.onDisabled();
    }


    public void showMyPoint(@NonNull ChatConnectionInfo chatConnectionInfo,
                            @NonNull ChatUser chatUser,
                            @NonNull String channelName) {
        final var viewerIdentity = viewerIdentityService.updateIdentity(chatUser.getPlatform(), chatUser.getUserId(), chatUser.getUserName().toLowerCase(), chatUser.getUserName());
        final var safe = bankService.findSafe(viewerIdentity.getId(), channelName);
        final var message = formMessage(safe);
        io.send(chatConnectionInfo, channelName, MESSAGE.formatted(chatUser.getHighlightedUserName(), message));
    }


    public void showUserPoint(@NonNull ChatConnectionInfo chatConnectionInfo, @NonNull String channelName, @NonNull String userInfo) {
        final String userName;
        if (userInfo.startsWith("@")) {
            userName = userInfo.substring(1);
        } else {
            userName = userInfo;
        }
        final var identity = viewerIdentityService.findIdentity(chatConnectionInfo.getPlatform(), userName);


        final var message = identity.map(ViewerIdentity::getId)
                                    .map(id -> bankService.findSafe(id, channelName))
                                    .map(this::formMessage)
                                    .map(sm -> userName + ", " + sm)
                                    .orElseGet(() -> "Utilisateur '" + userName + "' inconnu");

        io.send(chatConnectionInfo, channelName, message);
    }


    private String formMessage(@NonNull Safe safe) {
        if (safe.getCredits().isEmpty()) {
            return NO_POINT;
        }

        return safe.getCredits()
                   .entrySet()
                   .stream()
                   .map(e -> e.getValue() + " " + e.getKey().getIdentification())
                   .collect(Collectors.joining(", ", "tu as ", "."));
    }


    public void addSomePoint(
            @NonNull ChatConnectionInfo chatConnectionInfo, @NonNull String channelName, @NonNull String userInfo, @NonNull PointType type, int amount) {
        final var platform = chatConnectionInfo.getPlatform();
        try {
            final var viewerIdentity = creditAdder.execute(platform, channelName, userInfo, type, amount);
            final var viewerPseudo = viewerIdentity.getPseudo();
            io.send(chatConnectionInfo, channelName, amount + " points added to " + viewerPseudo + " ");
        } catch (Throwable t) {
            LOG.warn("Could not add credit for '{}' on platform '{}' : {}", userInfo, platform, t.getMessage());
        }
    }

    public void onMessage(@NonNull Notification notification) {
        if (notification instanceof ChannelPointsCustomRewardRedemptionAddEvent event) {
            handleChannelPointRedemption(event);
        }
    }

    private void handleChannelPointRedemption(ChannelPointsCustomRewardRedemptionAddEvent event) {
        if (!event.getReward().getPrompt().contains(REWARD_TO_CREDIT_TAG)) {
            return;
        }
        redemptionConverter.convert(event);
    }
}
