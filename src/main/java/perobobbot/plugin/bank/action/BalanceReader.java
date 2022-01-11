package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.command.CommandAction;
import perobobbot.command.CommandParsing;
import perobobbot.lang.ExecutionContext;
import perobobbot.plugin.bank.BankRequirements;

@RequiredArgsConstructor
public class BalanceReader {

    public static @NonNull CommandAction asCommandAction(@NonNull BankRequirements bankRequirements) {
        return (parsing, context) -> new BalanceReader(bankRequirements, parsing, context).read();
    }

    private final @NonNull BankRequirements bankRequirements;
    private final @NonNull CommandParsing parsing;
    private final @NonNull ExecutionContext context;

    public void read() {
        final var chatUser = context.getMessageOwner();
        final var viewerIdentity = bankRequirements.updateUserIdentity(chatUser);
        final var safe = bankRequirements.findSafe(context, viewerIdentity.getId());
        final var message = new SafeContentDescription(safe).withUser(chatUser.getHighlightedUserName());

        bankRequirements.send(context, message.toString());
    }
}
