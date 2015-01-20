package se.webcv;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import se.webcv.config.UnitTestConfig;

/**
 * Created by marcus on 20/01/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UnitTestConfig.class})
@WebAppConfiguration
@ActiveProfiles({"unit-test", "in-mem"})
public abstract class UnitTest {
}
