package kr.hakdang.cassdio;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * BaseTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(SpringExtension.class)
public abstract class BaseTest {

    protected final static String CLUSTER_ID = "12345";
}
