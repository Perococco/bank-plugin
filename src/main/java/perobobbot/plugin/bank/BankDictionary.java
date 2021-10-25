package perobobbot.plugin.bank;

import lombok.NonNull;

import java.util.Locale;
import java.util.ResourceBundle;

public enum BankDictionary {
    INSTANCE,
    ;

    public @NonNull String getMessage(@NonNull String key) {
        return getMessage(key, Locale.getDefault());
    }

    public @NonNull String getMessage(@NonNull String key, @NonNull Locale locale) {
        return ResourceBundle.getBundle("perobobbot.plugin.bank.bank", locale).getString(key);
    }

}
