package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.command.CommandAction;
import perobobbot.command.CommandParsing;
import perobobbot.lang.ExecutionContext;
import perobobbot.lang.PointType;
import perobobbot.plugin.bank.BankExtension;

@RequiredArgsConstructor
public class ReadWriteBalance implements CommandAction {

    public static final String USERINFO_PARAMETER = "userInfo";
    public static final String AMOUNT_PARAMETER = "amount";

    private final @NonNull BankExtension extension;

    @Override
    public void execute(@NonNull CommandParsing parsing, @NonNull ExecutionContext context) {
        final var userInfo = parsing.getParameter(USERINFO_PARAMETER);
        final var amount = parsing.findIntParameter(AMOUNT_PARAMETER);
        if (amount.isPresent()) {
            extension.addSomePoint(
                    context.getChatConnectionInfo(),
                    context.getChannelName(),
                    userInfo, PointType.CREDIT, amount.get());
        } else {
            extension.showUserPoint(context.getChatConnectionInfo(), context.getChannelName(), userInfo);
        }
    }
}
