package perobobbot.plugin.bank;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import perobobbot.data.service.BankService;
import perobobbot.data.service.PlatformUserService;
import perobobbot.lang.Platform;
import perobobbot.lang.PlatformUser;
import perobobbot.lang.PointType;
import perobobbot.lang.Safe;

@Log4j2
@RequiredArgsConstructor
public class CreditAdder {

    public interface Adder {
        @NonNull PlatformUser execute(
                Platform platform,
                String channelName,
                String userInfo,
                PointType type,
                long amount
        );
    }

    private final @NonNull BankService bankService;
    private final @NonNull PlatformUserService platformUserService;

    private final @NonNull Platform platform;
    private final @NonNull String channelId;
    private final @NonNull String userInfo;
    private final @NonNull PointType type;
    private final long amount;

    private String sanitizedUserInfo;
    private PlatformUser platformUser;
    private Safe safe;

    private @NonNull PlatformUser execute() {
        this.sanitizeUserInfo();
        this.retrieveViewerIdentity();
        this.findViewerSafe();
        this.addPointsToViewerSafe();
        return platformUser;
    }

    private void sanitizeUserInfo() {
        final int idx = userInfo.startsWith("@") ? 1 : 0;
        sanitizedUserInfo = userInfo.substring(idx);
    }

    private void retrieveViewerIdentity() {
        platformUser = platformUserService.getPlatformUser(platform, sanitizedUserInfo);
    }

    private void findViewerSafe() {
        this.safe = bankService.findSafe(platformUser.getId(), channelId);
    }

    private void addPointsToViewerSafe() {
        bankService.addPoints(safe.getId(), type, amount);
    }


    public static @NonNull PlatformUser execute(
            @NonNull BankService bankService,
            @NonNull PlatformUserService platformUserService,
            @NonNull Platform platform,
            @NonNull String channelId,
            @NonNull String userInfo,
            @NonNull PointType type,
            long amount
    ) {
        return new CreditAdder(bankService, platformUserService, platform, channelId, userInfo, type, amount).execute();
    }


    public static Adder createAdder(@NonNull BankService bankService, @NonNull PlatformUserService platformUserService) {
        return (platform, channelId, userInfo, type, amount) -> execute(bankService, platformUserService, platform, channelId, userInfo, type, amount);
    }


}
