package perobobbot.plugin.bank;

import com.google.common.collect.ImmutableList;
import jplugman.api.ServiceProvider;
import lombok.NonNull;
import perobobbot.access.AccessRule;
import perobobbot.chat.core.IO;
import perobobbot.command.CommandDeclaration;
import perobobbot.data.service.BankService;
import perobobbot.data.service.PlatformUserService;
import perobobbot.extension.ExtensionFactory;
import perobobbot.lang.NotificationDispatcher;
import perobobbot.lang.Role;
import perobobbot.oauth.OAuthTokenIdentifierSetter;
import perobobbot.plugin.bank.action.BalanceReader;
import perobobbot.plugin.bank.action.PointGiver;
import perobobbot.plugin.bank.action.ReadWriteBalance;
import perobobbot.twitch.client.api.TwitchService;

import java.time.Duration;

public class BankExtensionFactory implements ExtensionFactory<BankExtension> {

    @Override
    public @NonNull BankExtension createExtension(@NonNull ModuleLayer pluginLayer, @NonNull ServiceProvider serviceProvider) {
        return new BankExtension(serviceProvider.getSingleService(IO.class),
                serviceProvider.getSingleService(BankService.class),
                serviceProvider.getSingleService(TwitchService.class),
                serviceProvider.getSingleService(PlatformUserService.class),
                serviceProvider.getSingleService(NotificationDispatcher.class),
                serviceProvider.getSingleService(OAuthTokenIdentifierSetter.class));
    }

    @Override
    public @NonNull ImmutableList<CommandDeclaration> createCommandDefinitions(@NonNull BankExtension extension, @NonNull ServiceProvider serviceProvider, CommandDeclaration.@NonNull Factory factory) {
        final var bankRequirements = new BankRequirements(serviceProvider);
        final var readBalance = AccessRule.create(Role.ANY_USER, Duration.ofSeconds(30), Role.ADMINISTRATOR.cooldown(Duration.ZERO));
        final var writeBalance = AccessRule.create(Role.TRUSTED_USER, Duration.ofSeconds(10));
        final var givePoints = AccessRule.create(Role.ANY_USER, Duration.ofSeconds(30));
        return ImmutableList.of(
                factory.create("credits", readBalance, BalanceReader.asCommandAction(bankRequirements)),
                factory.create("credits {%s} [%s]".formatted(ReadWriteBalance.USERINFO_PARAMETER, ReadWriteBalance.AMOUNT_PARAMETER), writeBalance, ReadWriteBalance.asCommandAction(bankRequirements)),
                factory.create("give {%s} {%s}".formatted(ReadWriteBalance.USERINFO_PARAMETER, ReadWriteBalance.AMOUNT_PARAMETER), givePoints, PointGiver.asCommandAction(bankRequirements))
        );
    }
}
