package kafkawebclient;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class DummyTest {
    @Test
    public void testAnything() {
        assertThat(true, is(not(false)));
    }
}
