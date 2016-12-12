/**
 * Brave 8, Project SPrINg
 */
package brave8.spring;

import org.junit.Test;
import org.junit.Before;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SolarDataSourceTest {

    private SolarDataSource mSolarDataSource;

    @Before
    public void setUp() throws Exception {
        mSolarDataSource = new SolarDataSource();
    }

    @Test
    public void createSolarList() throws Exception {
        String jsonData = "{\"result\":[{\"id_data\":\"1\",\"id_login\":\"2\",\"power\":\"3.6\",\"temperature\":\"3\",\"light\":\"3040\",\"bar_pressure\":\"99.5\",\"humidity\":\"42\",\"time\":\"01:00:00\",\"date\":\"2016-11-11\"}]}";
        List<Solar> solarList = mSolarDataSource.createSolarList(jsonData);
        assertEquals(3.6,solarList.get(0).getPower(),0);
        assertEquals(3,solarList.get(0).getTemperature(),0);
        assertEquals(3040,solarList.get(0).getLight(),0);
        assertEquals(99.5,solarList.get(0).getBarometric(),0);
        assertEquals(42,solarList.get(0).getHumidity(),0);
    }
}