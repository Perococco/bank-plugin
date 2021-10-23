package perobobbot.plugin.bank;

import com.google.common.collect.ImmutableList;
import jplugman.api.ServiceProvider;
import lombok.NonNull;
import perobobbot.access.AccessRule;
import perobobbot.chat.core.IO;
import perobobbot.command.CommandDeclaration;
import perobobbot.data.service.BankService;
import perobobbot.data.service.ViewerIdentityService;
import perobobbot.extension.ExtensionFactory;
import perobobbot.lang.NotificationDispatcher;
import perobobbot.lang.Role;
import perobobbot.oauth.OAuthTokenIdentifierSetter;
import perobobbot.plugin.bank.action.ReadBalance;
import perobobbot.plugin.bank.action.ReadWriteBalance;
import perobobbot.twitch.client.api.TwitchService;

import java.time.Duration;

public class BankExtensionFactory implements ExtensionFactory<BankExtension> {

    @Override
    public @NonNull BankExtension createExtension(@NonNull ModuleLayer pluginLayer, @NonNull ServiceProvider serviceProvider) {
        return new BankExtension(serviceProvider.getSingleService(IO.class),
                serviceProvider.getSingleService(BankService.class),
                serviceProvider.getSingleService(TwitchService.class),
                serviceProvider.getSingleService(ViewerIdentityService.class),
                serviceProvider.getSingleService(NotificationDispatcher.class),
                serviceProvider.getSingleService(OAuthTokenIdentifierSetter.class));
    }

    @Override
    public @NonNull ImmutableList<CommandDeclaration> createCommandDefinitions(@NonNull BankExtension extension, @NonNull ServiceProvider serviceProvider, CommandDeclaration.@NonNull Factory factory) {
        final var readBalance = AccessRule.create(Role.ANY_USER, Duration.ofSeconds(30), Role.ADMINISTRATOR.cooldown(Duration.ZERO));
        final var writeBalance = AccessRule.create(Role.ADMINISTRATOR, Duration.ofSeconds(1));
        return ImmutableList.of(
                factory.create("credits", readBalance, new ReadBalance(extension)),
                factory.create("credits {%s} [%s]".formatted(ReadWriteBalance.USERINFO_PARAMETER, ReadWriteBalance.AMOUNT_PARAMETER), writeBalance, new ReadWriteBalance(extension))
        );
    }
}
