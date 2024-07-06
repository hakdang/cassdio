package kr.hakdang.cadio.web;

import kr.hakdang.cadio.CoreRoot;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * IntegrationTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Tag("integration-test")
@SpringBootTest(classes = {CoreRoot.class})
public abstract class IntegrationTest extends BaseTest {
}
