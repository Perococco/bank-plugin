package perobobbot.plugin.bank.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.lang.PointType;
import perobobbot.lang.Safe;
import perobobbot.plugin.bank.BankDictionary;
import perobobbot.plugin.bank.Constants;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SafeContentDescription {

    private final @NonNull Safe safe;
    private final @NonNull Locale locale;

    public SafeContentDescription(@NonNull Safe safe) {
        this(safe, Constants.DEFAULT_LOCALE);
    }

    @Override
    public String toString() {
        if (safe.getCredits().isEmpty()) {
            return BankDictionary.INSTANCE.getMessage("safe-description.no-points", locale);
        }

        return safe.getCredits()
                   .entrySet()
                   .stream()
                   .map(this::formOneCreditMessage)
                   .flatMap(Optional::stream)
                   .collect(Collectors.joining(", "));
    }

    public @NonNull UserPointsDescription withUser(@NonNull String viewerPseudo) {
        return new UserPointsDescription(this, viewerPseudo, locale);
    }

    private Optional<String> formOneCreditMessage(@NonNull Map.Entry<PointType, Long> pointAmount) {
        final var amount = pointAmount.getValue();
        final var pointType = pointAmount.getKey();
        if (amount == null) {
            return Optional.empty();
        }
        final String singular = amount + " " + pointType.getIdentification();

        if (amount > 1) {
            return Optional.of(singular + "s");
        }
        return Optional.of(singular);
    }
}
