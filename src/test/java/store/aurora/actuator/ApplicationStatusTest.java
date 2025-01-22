package store.aurora.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationStatusTest {

    private ApplicationStatus applicationStatus;

    @BeforeEach
    void setUp() {
        applicationStatus = new ApplicationStatus();
    }

    @Test
    void testInitialStatusIsTrue() {
        // given & when
        boolean status = applicationStatus.getStatus();

        // then
        assertTrue(status, "Initial status should be true");
    }

    @Test
    void testStopServiceChangesStatusToFalse() {
        // given
        applicationStatus.stopService();

        // when
        boolean status = applicationStatus.getStatus();

        // then
        assertFalse(status, "Status should be false after stopService is called");
    }
}
