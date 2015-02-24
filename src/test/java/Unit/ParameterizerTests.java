package Unit;

import com.google.common.collect.Lists;
import com.noelherrick.jell.sql.ParameterizedSql;
import com.noelherrick.jell.sql.Parameterizer;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

public class ParameterizerTests
{
    @Test
    public void ParserFindsAllParameters ()
    {
        Parameterizer parameterizer = new Parameterizer();

        ParameterizedSql sql = parameterizer.parameterizeSql("select @param1, @param2");

        assertEquals(2, sql.parameterNames.size());
    }

    @Test
    public void ParserFindsAllParametersParens ()
    {
        Parameterizer parameterizer = new Parameterizer();

        ParameterizedSql sql = parameterizer.parameterizeSql("insert into thing values (@param1, @param2)");

        assertEquals(2, sql.parameterNames.size());
    }

    @Test
    public void ParserOrdersParametersCorrectly ()
    {
        Collection<String> expectedParameters = Lists.newArrayList("this", "param", "param2");

        Parameterizer parameterizer = new Parameterizer();

        ParameterizedSql sql = parameterizer.parameterizeSql("insert into @this values (@param, @param2)");

        assertEquals(3, sql.parameterNames.size());

        Iterator targetIt = expectedParameters.iterator();
        for (Object obj : sql.parameterNames)
        {
            assertEquals(obj, targetIt.next());
        }
    }

    @Test
    public void ParameterizerReturnsAllSql ()
    {
        Parameterizer parameterizer = new Parameterizer();

        ParameterizedSql sql = parameterizer.parameterizeSql("insert into @this values (@param, @param2) ");

        String expected = "insert into ? values (?, ?) ";

        assertEquals(expected, sql.sql);
    }


}
