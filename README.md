jell
=

Jell is a micro-ORM for Java. It allows you to easily execute SQL in Java, as well as automatically mapping parameters from an object and query results back to a specific class. If you don't specify a class, it will map to a dynamic object.

## Examples

### Creating a jell instance

A new Jell instance is created by passing in a JDBC connection.

    Connection conn = DriverManager.getConnection(url, username, password);

    Jell jell = new Jell(conn);

### Querying

ParamClass is a simple POJO with the fields "number" and "string".

	ParamClass param = new ParamClass("a", 2);

	String sql = "select 1 as number, 'b' as str where 'a' = @string and 2 = @number";

	Collection<ParamClass> results = jell.query(sql, ParamClass.class, param);

### Executing

If you have a simple SQL statement, executing is simple:

    jell.execute("create table basic_post (id int primary key, title varchar(50) not null)");

### Batch inserts

If you have a bunch of inserts, this is simple as well.

    List<ParamClass> pojos = new ArrayList<>();

    for (int i = 0; i < 1_000_000; i++)
    {
        pojos.add(new ParamClass("a", i));
    }

    jell.executeBatch("insert into basic_post values (@number, @string)", pojos);

### Hashes

I wanted something similar to dynamic objects in .NET, but Java does not help me here, so if you don't specify a class to map query results to, it will give you a map-like object.

    Collection<Dyno> dynos1 = jell.query("select * from basic_post limit 10", false);

    for (Dyno dyno : dynos1) {
        System.out.println(dyno.str("title"));
    }


## The origin

Whenever I start a .NET project that uses the database, I will almost always fall back to Dapper. It reduces the friction of using an RDBMS in C# code. The standard ADO.NET (.NET's JDBC) API is verbose and painful. With Dapper, I only need to create an ADO.NET connection and I can start writing SQL. Queries are automatically to the class or primitive you specify. Dapper is incredibly near the speed of using ADO.NET, so I choose it over other ORM's.

I wanted something similar for Java, so I built this.

## Limitations

Java has several limitations that preclude me from writing a full Dapper replacement in the language.

1. Java does not have reified generics (or type filters). I must do some reflection magic and hope it works. Unlike C#, I cannot guarantee at compile time that your passed-in class has a parameter-less constructor.
1. Java lacks the dynamic object. This disallows both dynamic parameter objects and dynamic result objects. I most often use the former.
1. Java has no viable replacement for extension methods. This means we have to create a separate object using the connection, instead of using a static method like Dapper does.

## The name

So you know the .NET library, Dapper. Well, what makes you dapper, at least in the 90s and the 00s? Gel, of course! And since it's a Java library, it's jell!
