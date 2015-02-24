package com.noelherrick.jell;

import com.noelherrick.jell.containers.Dyno;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            String driver = "org.h2.Driver";
            Class.forName(driver).newInstance();

            String url = "jdbc:h2:~/jell";
            String username = "sa";
            String password = "";

            Connection conn = DriverManager.getConnection(url, username, password);

            Jell jell = new Jell(conn);

            List<SillyPojo> pojos = new ArrayList<>();

            for (int i = 0; i < 1_000_000; i++)
            {
                pojos.add(new SillyPojo("a", i));
            }

            jell.execute("create table basic_post (id int primary key, title varchar(50) not null)");

            long startTime = System.nanoTime();

            jell.executeBatch("insert into basic_post values (@number, @string)", pojos);

            long endTime = System.nanoTime();

            long timeTaken = (endTime-startTime) / 1_000_000;

            System.out.println("Took " + timeTaken + " ms");

            Collection<Dyno> dynos1 = jell.query("select * from basic_post limit 10", false);

            for (Dyno dyno : dynos1) {
                System.out.println(dyno.str("title"));
            }

            System.out.println("Do dynamic object creation.");

            Collection<Dyno> dynos2 = jell.query("select 1 as a union select 2");

            for (Dyno dyno : dynos2) {
                System.out.println(dyno.str("a"));
            }

            SillyPojo param = new SillyPojo("a", 2);

            Collection<SillyPojo> results = jell.query("select 1 as number, 'b' as str where 'a' = @string and 2 = @number and 3 = 3", SillyPojo.class, param);

            for (SillyPojo sp : results)
            {
                System.out.println(sp.number + " " + sp.string);
            }

            jell.execute("drop table basic_post");
        }
        catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}
