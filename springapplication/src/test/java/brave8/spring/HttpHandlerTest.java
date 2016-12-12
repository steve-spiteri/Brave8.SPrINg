/**
 * Brave 8, Project SPrINg
 */
package brave8.spring;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class HttpHandlerTest {

    private HttpHandler mHttpHandler;

    @Before
    public void setUp() throws Exception {
        mHttpHandler = new HttpHandler();
    }

    @Test
    public void makeServiceCall() throws Exception {
        String url = "http://springdb.eu5.org/spring/login.php?user=invalid&pass=invalid";
        assertEquals("  {\"result\":[{\"id_login\":\"-1\"}]} \n",mHttpHandler.makeServiceCall(url));
    }

}