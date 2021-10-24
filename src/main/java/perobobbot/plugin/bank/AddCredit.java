package perobobbot.plugin.bank;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import perobobbot.data.com.ViewerIdentityNotFound;
import perobobbot.data.service.BankService;
import perobobbot.data.service.ViewerIdentityService;
import perobobbot.lang.Platform;
import perobobbot.lang.PointType;
import perobobbot.lang.Safe;
import perobobbot.lang.ViewerIdentity;

@Log4j2
@RequiredArgsConstructor
public class AddCredit {
    public interface Adder {
        @NonNull ViewerIdentity execute(
                Platform platform,
                String channelName,
                String userInfo,
                PointType type,
                long amount
        );
    }

    private final @NonNull BankService bankService;
    private final @NonNull ViewerIdentityService viewerIdentityService;

    private final @NonNull Platform platform;
    private final @NonNull String channelName;
    private final @NonNull String userInfo;
    private final @NonNull PointType type;
    private final long amount;

    private String sanitizedUserInfo;
    private ViewerIdentity viewerIdentity;
    private Safe safe;

    private @NonNull ViewerIdentity execute() {
        this.sanitizeUserInfo();
        this.retrieveViewerIdentity();
        this.findViewerSafe();
        this.addPointsToViewerSafe();
        return viewerIdentity;
    }

    private void sanitizeUserInfo() {
        final int idx = userInfo.startsWith("@") ? 1 : 0;
        sanitizedUserInfo = userInfo.substring(idx);
    }

    private void retrieveViewerIdentity() {
        viewerIdentity = viewerIdentityService.getIdentity(platform, sanitizedUserInfo);
    }

    private void findViewerSafe() {
        this.safe = bankService.findSafe(viewerIdentity.getId(), channelName);
    }

    private void addPointsToViewerSafe() {
        System.out.println("AddCredit.addPointsToViewerSafe");
        bankService.addPoints(safe.getId(), type, amount);
    }


    public static @NonNull ViewerIdentity execute(
            @NonNull BankService bankService,
            @NonNull ViewerIdentityService viewerIdentityService,
            @NonNull Platform platform,
            @NonNull String channelName,
            @NonNull String userInfo,
            @NonNull PointType type,
            long amount
    ) {
        return new AddCredit(bankService, viewerIdentityService, platform, channelName, userInfo, type, amount).execute();
    }


    public static Adder createAdder(@NonNull BankService bankService, @NonNull ViewerIdentityService viewerIdentityService) {
        return (platform, channelName, userInfo, type, amount) -> execute(bankService, viewerIdentityService, platform, channelName, userInfo, type, amount);
    }


}
