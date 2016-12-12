/**
 * Brave 8, Project SPrINg
 */
package brave8.spring;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertFalse;

public class LoginActivityTest {

    private LoginActivity mLoginActivity;

    @Before
    public void setUp() throws Exception {
        mLoginActivity = new LoginActivity();
    }

    @Test
    public void isEmpty() throws Exception {
        String string = "test";
        assertFalse(mLoginActivity.isEmpty(string));
    }

}