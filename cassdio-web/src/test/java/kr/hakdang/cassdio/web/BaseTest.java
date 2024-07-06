package kr.hakdang.cassdio.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * BaseTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public abstract class BaseTest {
}
