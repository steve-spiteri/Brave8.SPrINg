/**
 * Brave 8, Project SPrINg
 */
package brave8.spring;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageFragmentTest {
    private PageFragment mPageFragment;

    @Before
    public void setUp() throws Exception {
        mPageFragment = new PageFragment();
    }

    @Test
    public void getMax() throws Exception {
        double[] d = new double[] {1.0,5,0,10.0};
        assertEquals(10.0,mPageFragment.getMax(d,3), 0);
    }

}