package perobobbot.plugin.bank;

import jplugman.annotation.Extension;
import jplugman.api.Disposable;
import jplugman.api.ServiceProvider;
import lombok.Getter;
import lombok.NonNull;
import perobobbot.extension.PerobobbotExtensionPluginBase;
import perobobbot.plugin.PerobobbotPlugin;

@Getter
@Extension(point = PerobobbotPlugin.class, version = "1.0.0")
public class BankExtensionPlugin extends PerobobbotExtensionPluginBase implements Disposable {

    public BankExtensionPlugin(@NonNull ModuleLayer pluginLayer, @NonNull ServiceProvider serviceProvider) {
        super(new BankExtensionFactory(), pluginLayer, serviceProvider);
    }

    @Override
    public void dispose() {

    }
}
