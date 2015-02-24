package Integration;

import com.noelherrick.jell.Jell;
import com.noelherrick.jell.SillyPojo;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class ExecuteTests
{
    @Test
    public void SimpleExecute () throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        String driver = "org.h2.Driver";
        Class.forName(driver).newInstance();

        String url = "jdbc:h2:~/jell";
        String username = "sa";
        String password = "";

        Connection conn = DriverManager.getConnection(url, username, password);

        Jell jell = new Jell(conn);

        jell.execute("create table pojos (number int primary key, string varchar(50) not null)");

        int count = jell.execute("insert into pojos values (@number, @string)", new SillyPojo("a", 2));

        assertEquals(1, count);

        jell.execute("drop table pojos");

        conn.close();
    }
}
