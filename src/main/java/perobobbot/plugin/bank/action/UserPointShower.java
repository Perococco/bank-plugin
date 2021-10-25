package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.lang.ExecutionContext;
import perobobbot.plugin.bank.BankRequirements;
import perobobbot.plugin.bank.Constants;

@RequiredArgsConstructor
public class UserPointShower {


    public interface Shower {
        void show(@NonNull ExecutionContext executionContext);
    }

    public static @NonNull Shower createShower(@NonNull BankRequirements bankRequirements) {
        return executionContext -> show(bankRequirements,executionContext);
    }

    public static void show(@NonNull BankRequirements bankRequirements, @NonNull ExecutionContext executionContext) {
        new UserPointShower(bankRequirements,executionContext).show();
    }

    private final @NonNull BankRequirements bankRequirements;
    private final @NonNull ExecutionContext context;

    private void show() {
        final var chatUser = context.getMessageOwner();
        final var viewerIdentity = bankRequirements.updateUserIdentity(context.getMessageOwner());
        final var safe = bankRequirements.getBankService().findSafe(viewerIdentity.getId(), context.getChannelName());
        final var description = new SafeContentDescription(safe, Constants.DEFAULT_LOCALE);

        bankRequirements.send(context, d -> chatUser.getHighlightedUserName() + ", " + description);
    }

}
