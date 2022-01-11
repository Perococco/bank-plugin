package perobobbot.plugin.bank;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import perobobbot.chat.core.IO;
import perobobbot.data.service.BankService;
import perobobbot.data.service.PlatformUserService;
import perobobbot.extension.ExtensionBase;
import perobobbot.lang.*;
import perobobbot.oauth.OAuthTokenIdentifierSetter;
import perobobbot.twitch.client.api.TwitchService;
import perobobbot.twitch.eventsub.api.event.ChannelPointsCustomRewardRedemptionAddEvent;

@Log4j2
public class BankExtension extends ExtensionBase {

    public static final String REWARD_TO_CREDIT_TAG = "[-BK-]";

    public static final String MESSAGE = "%s %s";
    public static final String NO_POINT = "tu n'as aucun point";

    public static final String NAME = "Bank";

    private final @NonNull IO io;

    private final @NonNull BankService bankService;

    private final @NonNull PlatformUserService platformUserService;

    private final @NonNull CreditAdder.Adder creditAdder;
    private final @NonNull ConvertChannelPointsToCredits.Converter redemptionConverter;

    private final SubscriptionHolder subscriptionHolder = new SubscriptionHolder();

    public BankExtension(@NonNull IO io,
                         @NonNull BankService bankService,
                         @NonNull TwitchService twitchService,
                         @NonNull PlatformUserService platformUserService,
                         @NonNull NotificationDispatcher notificationDispatcher,
                         @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter) {
        super(NAME);
        this.io = io;
        this.creditAdder = CreditAdder.createAdder(bankService, platformUserService);
        this.redemptionConverter = ConvertChannelPointsToCredits.createConverter(oAuthTokenIdentifierSetter, creditAdder, twitchService);
        this.bankService = bankService;
        this.platformUserService = platformUserService;
        this.subscriptionHolder.replaceWith(() -> notificationDispatcher.addListener(this::onMessage));
    }

    @Override
    protected void onDisabled() {
        super.onDisabled();
        subscriptionHolder.unsubscribe();
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
