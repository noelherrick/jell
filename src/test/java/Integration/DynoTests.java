package Integration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.noelherrick.jell.Jell;
import com.noelherrick.jell.containers.Dyno;
import com.noelherrick.jell.containers.SimpleDyno;
import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class DynoTests
{
    private static Connection conn;
    private static Jell jell;

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
    public void setup () throws SQLException
    {
        jell.execute("create table dynos (number int primary key, string varchar(50) not null)");
    }

    @After
    public void teardown () throws SQLException
    {
        jell.execute("Drop table dynos");
    }

    @Test
    public void saveOneDyno() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Dyno dyno = new SimpleDyno(ImmutableMap.of("number", 1, "string", "Nice"));

        int count = jell.execute("insert into dynos values (@number, @string)", dyno);

        assertEquals(1, count);

        Collection<Dyno> results = jell.query("select * from dynos");

        assertEquals(1, results.size());

        Dyno savedDyno = results.stream().findFirst().get();

        assertEquals(1, savedDyno.obj("number"));
        assertEquals("Nice", savedDyno.str("string"));
    }

    @Test
    public void saveSeveralDynos() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Collection<Dyno> lists = Lists.newArrayList(
                new SimpleDyno(ImmutableMap.of("number", 1, "string", "Nice")),
                new SimpleDyno(ImmutableMap.of("number", 2, "string", "Okay")));

        int count = jell.executeBatch("insert into dynos values (@number, @string)", lists);

        assertEquals(2, count);

        Collection<Dyno> results = jell.query("select * from dynos order by number");

        assertEquals(2, results.size());

        Dyno savedDyno = results.stream().findFirst().get();

        assertEquals(1, savedDyno.obj("number"));
        assertEquals("Nice", savedDyno.str("string"));
    }
}
