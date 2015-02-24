package Integration;

import com.noelherrick.jell.Jell;
import com.noelherrick.jell.containers.Dyno;
import com.noelherrick.jell.SillyPojo;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class UnbufferedTests
{
    private static Connection conn;
    private static Jell jell;
    private List<SillyPojo> pojos;

    @BeforeClass
    public static void suiteSetup () throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException
    {
        String driver = "org.h2.Driver";
        Class.forName(driver).newInstance();

        String url = "jdbc:h2:~/jell";
        String username = "sa";
        String password = "";

        conn = DriverManager.getConnection(url, username, password);

        jell = new Jell(conn);
    }

    @AfterClass
    public static void suiteTeardown () throws SQLException
    {
        conn.close();
    }

    @Before
    public void setup () throws SQLException, NoSuchFieldException, IllegalAccessException {
        jell.execute("create table pojos (number int primary key, string varchar(50) not null)");

        pojos = new ArrayList<>();

        for (int i = 0; i < 100; i++)
        {
            pojos.add(new SillyPojo("a", i));
        }

        jell.executeBatch("insert into pojos values (@number, @string)", pojos);
    }

    @After
    public void teardown () throws SQLException
    {
        jell.execute("Drop table pojos");
    }

    @Test
    public void UnbufferedQueryWithoutParameter()
            throws IllegalAccessException, SQLException, InstantiationException, NoSuchFieldException
    {
        Collection<SillyPojo> results = jell.query("Select string, number from pojos order by number", SillyPojo.class, false);

        Iterator<SillyPojo> targetIt = results.iterator();
        for (SillyPojo obj : pojos)
        {
            SillyPojo target = targetIt.next();

            assertEquals(obj.number, target.number);
            assertEquals(obj.string, target.string);
        }
    }

    @Test
    public void UnbufferedQueryWithParameter()
            throws IllegalAccessException, SQLException, InstantiationException, NoSuchFieldException
    {
        SillyPojo param = new SillyPojo("a", 1);

        Collection<SillyPojo> results = jell.query("Select string, number from pojos where string = @string order by number", SillyPojo.class, param, false);

        Iterator<SillyPojo> targetIt = results.iterator();
        for (SillyPojo obj : pojos)
        {
            SillyPojo target = targetIt.next();

            assertEquals(obj.number, target.number);
            assertEquals(obj.string, target.string);
        }
    }

    @Test
    public void UnbufferedQueryDyno() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Collection<Dyno> results = jell.query("select * from pojos order by number", false);

        for (Dyno savedDyno : results)
        {
            assertEquals(0, savedDyno.obj("number"));
            assertEquals("a", savedDyno.str("string"));

            break;
        }
    }
}
