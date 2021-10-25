package perobobbot.plugin.bank;

import jplugman.api.ServiceProvider;
import lombok.Getter;
import lombok.NonNull;
import perobobbot.chat.core.IO;
import perobobbot.data.service.BankService;
import perobobbot.data.service.BankTransaction;
import perobobbot.data.service.ViewerIdentityService;
import perobobbot.lang.*;
import perobobbot.lang.fp.Function1;
import perobobbot.oauth.OAuthTokenIdentifierSetter;
import perobobbot.twitch.client.api.TwitchService;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Getter
public class BankRequirements {

    private final @NonNull IO io;
    private final @NonNull BankService bankService;
    private final @NonNull TwitchService twitchService;
    private final @NonNull ViewerIdentityService viewerIdentityService;
    private final @NonNull NotificationDispatcher notificationDispatcher;
    private final @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter;
    private final @NonNull CreditAdder.Adder creditAdder;
    private final @NonNull ConvertChannelPointsToCredits.Converter redemptionConverter;


    public BankRequirements(@NonNull ServiceProvider serviceProvider) {
        this.io = serviceProvider.getAnyService(Requirements.IO);
        this.bankService = serviceProvider.getAnyService(Requirements.BANK_SERVICE);
        this.twitchService = serviceProvider.getAnyService(Requirements.TWITCH_SERVICE);
        this.viewerIdentityService = serviceProvider.getAnyService(Requirements.VIEWER_IDENTITY_SERVICE);
        this.notificationDispatcher = serviceProvider.getAnyService(Requirements.NOTIFICATION_DISPATCHER);
        this.oAuthTokenIdentifierSetter = serviceProvider.getAnyService(Requirements.O_AUTH_TOKEN_IDENTIFIER_SETTER);

        this.creditAdder = CreditAdder.createAdder(bankService, viewerIdentityService);
        this.redemptionConverter = ConvertChannelPointsToCredits.createConverter(oAuthTokenIdentifierSetter, creditAdder, twitchService);

    }

    public @NonNull ViewerIdentity updateUserIdentity(@NonNull ChatUser chatUser) {
        return viewerIdentityService.updateIdentity(chatUser.getPlatform(), chatUser.getUserId(), chatUser.getUserName().toLowerCase(), chatUser.getUserName());
    }

    public void send(@NonNull ExecutionContext context, @NonNull Function1<? super DispatchContext,String> messageBuilder) {
        io.send(context.getChatConnectionInfo(),context.getChannelName(),messageBuilder);
    }

    public void send(@NonNull ExecutionContext context, @NonNull String message) {
        io.send(context.getChatConnectionInfo(),context.getChannelName(),message);
    }

    public @NonNull Safe findChatterSafe(ExecutionContext context) {
        return bankService.findSafe(context.getPlatform(), context.getMessageOwner().getUserId(), context.getChannelName());
    }

    public @NonNull Safe findSafe(ExecutionContext context, @NonNull UUID viewerIdentityId) {
        return bankService.findSafe(viewerIdentityId, context.getChannelName());
    }

    public @NonNull Transaction createTransaction(@NonNull Safe giverSafe, long amount) {
        return new BankTransaction(bankService, giverSafe.getId(), Constants.DEFAULT_POINT, amount, Duration.ofSeconds(2));
    }

    public @NonNull ViewerIdentity addCredit(@NonNull ExecutionContext context, @NonNull String receiver, long amount) {
        return this.creditAdder.execute(context.getPlatform(),context.getChannelName(),receiver,Constants.DEFAULT_POINT,amount);
    }

    public @NonNull Optional<ViewerIdentity> findIdentity(@NonNull Platform platform, @NonNull String userInfo) {
        return this.viewerIdentityService.findIdentity(platform,userInfo);
    }

    public @NonNull ViewerIdentity updateIdentity(ChatUser chatUser) {
        return viewerIdentityService.updateIdentity(chatUser.getPlatform(), chatUser.getUserId(), chatUser.getUserName().toLowerCase(), chatUser.getUserName());
    }
}
