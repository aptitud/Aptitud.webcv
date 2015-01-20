package se.webcv.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created by marcus on 20/01/15.
 */
@Configuration
@Import({WebConfig.class})
public class UnitTestConfig {
}
