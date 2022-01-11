package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.command.CommandAction;
import perobobbot.command.CommandParsing;
import perobobbot.lang.*;
import perobobbot.plugin.bank.BankDictionary;
import perobobbot.plugin.bank.BankExtension;
import perobobbot.plugin.bank.BankRequirements;
import perobobbot.plugin.bank.Constants;

import java.util.UUID;

public class ReadWriteBalance {

    public static @NonNull CommandAction asCommandAction(@NonNull BankRequirements bankRequirements) {
        return (parsing, context) -> new ReadWriteBalance(bankRequirements,parsing,context).execute();
    }

    public static final String USERINFO_PARAMETER = "userInfo";
    public static final String AMOUNT_PARAMETER = "amount";

    private final @NonNull BankRequirements bankRequirements;
    private final @NonNull ExecutionContext context;
    private final @NonNull String userInfo;
    private final Long amount;

    public ReadWriteBalance(@NonNull BankRequirements bankRequirements, @NonNull CommandParsing parsing, @NonNull ExecutionContext context) {
        this.bankRequirements = bankRequirements;
        this.context = context;
        this.userInfo = parsing.getParameter(USERINFO_PARAMETER);
        this.amount = parsing.findLongParameter(AMOUNT_PARAMETER).orElse(null);
    }

    private void execute() {
        if (amount != null) {
            this.giveSomePoints();
        } else {
            this.showUserPoint();
        }
    }

    private void giveSomePoints() {
        assert amount!=null:"Bug, amount should not be null here";
        bankRequirements.addCredit(context,userInfo,amount);
    }

    public void showUserPoint() {
        final var userName = removeAtSignPrefix(userInfo);
        final var identity = bankRequirements.findIdentity(context.getPlatform(), userName);

        final var message = identity.map(PlatformUser::getId)
                                    .map(this::findSafeFromViewerIdentity)
                                    .map(SafeContentDescription::new)
                                    .map(s -> s.withUser(userName))
                                    .map(UserPointsDescription::toString)
                                    .orElseGet(this::getUnknownUserMessage);

        bankRequirements.send(context, message);
    }

    private @NonNull Safe findSafeFromViewerIdentity(@NonNull UUID viewerIdentityId) {
        return bankRequirements.findSafe(context,viewerIdentityId);
    }

    private @NonNull String getUnknownUserMessage() {
        return BankDictionary.INSTANCE.getMessage("safe-description.unknown-user", Constants.DEFAULT_LOCALE).formatted(userInfo);
    }

    private @NonNull String removeAtSignPrefix(@NonNull String string) {
        if (string.startsWith("@")) {
            return string.substring(1);
        } else {
            return string;
        }
    }

}
