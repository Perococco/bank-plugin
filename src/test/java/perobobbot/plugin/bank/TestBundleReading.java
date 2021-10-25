package perobobbot.plugin.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBundleReading {

    @Test
    public void shouldFindBundle() {
        final String message = BankDictionary.INSTANCE.getMessage("safe-description.no-points");
        Assertions.assertNotNull(message);
    }
}
