package kr.hakdang.cadio;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * BaseTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public abstract class BaseTest {
}
