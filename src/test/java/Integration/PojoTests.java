package Integration;

import com.noelherrick.jell.Jell;
import com.noelherrick.jell.SillyPojo;
import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.time.LocalDate;

public class PojoTests
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
        jell.execute("create table pojos (number int primary key, string varchar(50) not null)");
    }

    @After
    public void teardown () throws SQLException
    {
        jell.execute("Drop table pojos");
    }

    @Test
    public void SavePojo () throws NoSuchFieldException, IllegalAccessException, SQLException {
        int count = jell.execute("insert into pojos values (@number, @string)", new SillyPojo("a", 2));

        assertEquals(1, count);
    }

    @Test
    public void SavePojos () throws NoSuchFieldException, IllegalAccessException, SQLException {
        List<SillyPojo> pojos = new ArrayList<>();

        for (int i = 0; i < 100; i++)
        {
            pojos.add(new SillyPojo("a", i));
        }

        int count = jell.executeBatch("insert into pojos values (@number, @string)", pojos);

        assertEquals(100, count);
    }

    @Test
    public void GetPojo () throws NoSuchFieldException, IllegalAccessException, SQLException, InstantiationException {
        int count = jell.execute("insert into pojos values (@number, @string)", new SillyPojo("a", 2));

        assertEquals(1, count);

        Collection<SillyPojo> results = jell.query("Select string, number from pojos", SillyPojo.class);

        assertEquals(1, results.size());

        SillyPojo result = results.stream().findFirst().get();

        assertEquals("a", result.string);
        assertEquals(2, result.number);
    }

    @Test
    public void GetPojos () throws NoSuchFieldException, IllegalAccessException, SQLException, InstantiationException {
        List<SillyPojo> pojos = new ArrayList<>();

        for (int i = 0; i < 100; i++)
        {
            pojos.add(new SillyPojo("a", i));
        }

        int count = jell.executeBatch("insert into pojos values (@number, @string)", pojos);

        assertEquals(100, count);

        Collection<SillyPojo> results = jell.query("Select string, number from pojos order by number", SillyPojo.class);

        Iterator<SillyPojo> targetIt = results.iterator();
        for (SillyPojo obj : pojos)
        {
            SillyPojo target = targetIt.next();

            assertEquals(obj.number, target.number);
            assertEquals(obj.string, target.string);
        }
    }

    @Test
    public void UsePojoQuery () throws NoSuchFieldException, IllegalAccessException, SQLException, InstantiationException {
        List<SillyPojo> pojos = new ArrayList<>();

        for (int i = 0; i < 100; i++)
        {
            pojos.add(new SillyPojo("a", i));
        }

        int count = jell.executeBatch("insert into pojos values (@number, @string)", pojos);

        assertEquals(100, count);

        SillyPojo param = new SillyPojo("a", 1);

        Collection<SillyPojo> results = jell.query("Select string, number from pojos where string = @string order by number", SillyPojo.class, param);

        Iterator<SillyPojo> targetIt = results.iterator();
        for (SillyPojo obj : pojos)
        {
            SillyPojo target = targetIt.next();

            assertEquals(obj.number, target.number);
            assertEquals(obj.string, target.string);
        }
    }

    @Test
    public void GetPojoWithDate () throws NoSuchFieldException, IllegalAccessException, SQLException, InstantiationException {
        DateClass dateClass = new DateClass();
        dateClass.dateField = new Date();

        Collection<DateClass> results = jell.query("Select @dateField as dateField", DateClass.class, dateClass);

        assertEquals(1, results.size());

        DateClass result = results.stream().findFirst().get();

        assertEquals(dateClass.dateField, result.dateField);
    }

    @Test
    public void GetAllTypesPojo () throws NoSuchFieldException, IllegalAccessException, SQLException, InstantiationException {
        TypeTestClass typeTestClass = new TypeTestClass();
        typeTestClass.booleanField = true;
        typeTestClass.byteField = 122;
        typeTestClass.longField = Long.MAX_VALUE;
        typeTestClass.floatField = 3.44f;
        typeTestClass.doubleField = 213213.33;
        typeTestClass.dateField = new Date();
        typeTestClass.localDateField = LocalDate.now();

        Collection<TypeTestClass> results = jell.query("Select @booleanField as booleanField, @byteField as byteField, @longField as longField, @floatField as floatField, @doubleField as doubleField, @dateField as dateField, @localDateField as localDateField", TypeTestClass.class, typeTestClass);

        assertEquals(1, results.size());

        TypeTestClass result = results.stream().findFirst().get();

        assertEquals(typeTestClass.booleanField, result.booleanField);
        assertEquals(typeTestClass.byteField, result.byteField);
        assertEquals(typeTestClass.longField, result.longField);
        assertTrue(typeTestClass.floatField == result.floatField);
        assertTrue(typeTestClass.doubleField == result.doubleField);
        assertEquals(typeTestClass.dateField, result.dateField);
        assertEquals(typeTestClass.localDateField, result.localDateField);
    }
}
