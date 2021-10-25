package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.plugin.bank.BankDictionary;

import java.util.Locale;

@RequiredArgsConstructor
public class UserPointsDescription {

    private final @NonNull SafeContentDescription safeContentDescription;
    private final @NonNull String viewerPseudo;
    private final @NonNull Locale locale;

    @Override
    public String toString() {
        final var header = BankDictionary.INSTANCE.getMessage("safe-description.you-have",locale).formatted(viewerPseudo);
        return viewerPseudo+", "+header+" "+safeContentDescription;
    }
}
