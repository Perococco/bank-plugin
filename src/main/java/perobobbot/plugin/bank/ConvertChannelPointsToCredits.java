package perobobbot.plugin.bank;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import perobobbot.lang.Platform;
import perobobbot.lang.PointType;
import perobobbot.oauth.BroadcasterIdentifier;
import perobobbot.oauth.OAuthTokenIdentifierSetter;
import perobobbot.oauth.TokenIdentifier;
import perobobbot.twitch.api.RewardRedemptionStatus;
import perobobbot.twitch.client.api.TwitchService;
import perobobbot.twitch.eventsub.api.event.ChannelPointsCustomRewardRedemptionAddEvent;

@Log4j2
@RequiredArgsConstructor
public class ConvertChannelPointsToCredits {


    public interface Converter {

        void convert(ChannelPointsCustomRewardRedemptionAddEvent event);
    }

    private final @NonNull ChannelPointsCustomRewardRedemptionAddEvent event;
    private final @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter;
    private final @NonNull AddCredit.Adder creditAdder;
    private final @NonNull TwitchService twitchService;

    private void convert() {
        var redemptionStatus = addCredits();
        updateRedemptionStatus(redemptionStatus);
    }

    private void updateRedemptionStatus(@NonNull RewardRedemptionStatus status) {
        final TokenIdentifier tokenIdentifier = new BroadcasterIdentifier(Platform.TWITCH, event.getBroadcaster().getId());

        oAuthTokenIdentifierSetter.wrapRun(tokenIdentifier,
                () -> twitchService.updateOneRedemptionStatus(event.getReward().getId(), event.getId(), status)
                                   .subscribe());
    }


    private RewardRedemptionStatus addCredits() {
        final var channelName = event.getBroadcaster().getLogin();
        final var viewerId = event.getUser().getId();
        final var credits = event.getReward().getCost();
        try {
            creditAdder.execute(Platform.TWITCH, channelName, viewerId, PointType.CREDIT, credits);
            return RewardRedemptionStatus.FULFILLED;
        } catch (Throwable t) {
            LOG.warn("Fail to add point for '{}' redemption cancelled : {}", event.getUser().getLogin(), t.getMessage());
        }

        return RewardRedemptionStatus.CANCELED;
    }


    public static Converter createConverter(
            @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter,
            @NonNull AddCredit.Adder creditAdder,
            @NonNull TwitchService twitchService
    ) {
        return event -> new ConvertChannelPointsToCredits(event,oAuthTokenIdentifierSetter,creditAdder,twitchService).convert();
    }

    public static void convert(
            @NonNull ChannelPointsCustomRewardRedemptionAddEvent event,
            @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter,
            @NonNull AddCredit.Adder creditAdder,
            @NonNull TwitchService twitchService
    ) {
        new ConvertChannelPointsToCredits(event, oAuthTokenIdentifierSetter, creditAdder, twitchService).convert();
    }


}
