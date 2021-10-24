package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.chat.core.IO;
import perobobbot.command.CommandAction;
import perobobbot.command.CommandParsing;
import perobobbot.lang.ExecutionContext;
import perobobbot.lang.PointType;
import perobobbot.plugin.bank.BankExtension;

@RequiredArgsConstructor
public class GivePoints implements CommandAction {

    public static final String USERINFO_PARAMETER = "userInfo";
    public static final String AMOUNT_PARAMETER = "amount";

    private final @NonNull IO io;
    private final @NonNull BankExtension extension;

    @Override
    public void execute(@NonNull CommandParsing parsing, @NonNull ExecutionContext context) {
        final var userInfo = parsing.getParameter(USERINFO_PARAMETER);
        final var amount = parsing.getLongParameter(AMOUNT_PARAMETER);

        if (amount <= 0) {
            io.send(context.getChatConnectionInfo(), context.getChannelName(),"Nan Nan Nan "+context.getMessageOwner().getHighlightedUserName()+" !!");
            return;
        }
        extension.giveSomePoint(context,userInfo,PointType.CREDIT,amount);
    }
}
