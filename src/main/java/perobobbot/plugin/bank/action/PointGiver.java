package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import perobobbot.command.CommandAction;
import perobobbot.command.CommandParsing;
import perobobbot.lang.ExecutionContext;
import perobobbot.lang.PlatformUser;
import perobobbot.lang.fp.Function0;
import perobobbot.plugin.bank.BankDictionary;
import perobobbot.plugin.bank.BankRequirements;
import perobobbot.plugin.bank.Constants;

@Log4j2
@RequiredArgsConstructor
public class PointGiver {

    public static @NonNull CommandAction asCommandAction(@NonNull BankRequirements bankRequirements) {
        return (parsing, context) -> new PointGiver(bankRequirements,parsing,context).give();
    }

    public static final String USERINFO_PARAMETER = "userInfo";
    public static final String AMOUNT_PARAMETER = "amount";

    private final @NonNull BankRequirements bankRequirements;
    private final @NonNull CommandParsing parsing;
    private final @NonNull ExecutionContext context;

    private void give() {
        final var receiver = parsing.getParameter(USERINFO_PARAMETER);
        final var amount = parsing.getLongParameter(AMOUNT_PARAMETER);

        if (amount <= 0) {
            final var message = BankDictionary.INSTANCE.getMessage("point-giver.message-for-amount-not-positive").formatted(context.getMessageOwner().getHighlightedUserName());
            bankRequirements.send(context,message);
            return;
        }
        this.giveSomePoint(context,receiver,amount);
    }

    public void giveSomePoint(@NonNull ExecutionContext context, @NonNull String receiver, long amount) {
        try {
            final var giverSafe = bankRequirements.findChatterSafe(context);
            final var transaction = bankRequirements.createTransaction(giverSafe,amount);

            final Function0<PlatformUser> fundTransfert = () -> bankRequirements.addCredit(context, receiver, amount);

            final var viewerIdentity = transaction.getAndRollBackOnError(fundTransfert);
            final var viewerPseudo = viewerIdentity.getPseudo();

            final var message = BankDictionary.INSTANCE.getMessage("give.point-added-to", Constants.DEFAULT_LOCALE).formatted(amount,viewerPseudo);

            bankRequirements.send(context, message);

        } catch (Throwable t) {
            LOG.warn("Could not add credit for '{}' on platform '{}' : {}", receiver, context.getPlatform(), t.getMessage());
        }
    }

}
