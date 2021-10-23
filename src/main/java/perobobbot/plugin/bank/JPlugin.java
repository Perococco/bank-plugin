package perobobbot.plugin.bank;

import com.google.common.collect.ImmutableSet;
import jplugman.api.Requirement;
import perobobbot.extension.ExtensionPlugin;

public class JPlugin extends ExtensionPlugin {

    public static final ImmutableSet<Requirement<?>> REQUIREMENTS = ImmutableSet.of(
            Requirements.NOTIFICATION_DISPATCHER,
            Requirements.O_AUTH_TOKEN_IDENTIFIER_SETTER,
            Requirements.IO,
            Requirements.TWITCH_SERVICE,
            Requirements.BANK_SERVICE,
            Requirements.VIEWER_IDENTITY_SERVICE
    );


    public JPlugin() {
        super(BankExtensionPlugin::new, REQUIREMENTS);
    }
}
