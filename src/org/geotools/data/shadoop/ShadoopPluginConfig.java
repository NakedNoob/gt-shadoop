package org.geotools.data.shadoop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.DataAccessFactory.Param;
import org.geotools.util.logging.Logging;

public class ShadoopPluginConfig
{
	// AJG - Review all of these parameters to make sure these are needed
    private final static String SHADOOP_HOST_PARAM    = "shadoop_host";
    private final static String SHADOOP_PORT_PARAM    = "shadoop_port";
    
    // AJG - This may not be a necessary parameter
    private final static String SHADOOP_DB_NAME_PARAM = "shadoop_db_name";
    private final static String NAMESPACE_PARAM     = "namespace";

    private String              shadoopHost;
    private int                 shadoopPort;
    private String              shadoopDB;
    private String              namespace;
    /** Log instance to be shared among package classes */
    private final static Logger log                 = Logging
                                                            .getLogger( "org.geotools.data.shadoop" );
    // requires proper config of data/logging.xml, and addition of org.geotools.data.shadoop package
    // name
    // in log config/props file referenced by logging.xml

    private static List<Param>  shadoopParams         = null;

    public static List<Param> getPluginParams ()
    {
        if (shadoopParams == null)
        {
            shadoopParams = new ArrayList<Param>();
            shadoopParams.add( new Param( NAMESPACE_PARAM, String.class,
                                        "Namespace associated with this data store", false ) );
            shadoopParams.add( new Param( SHADOOP_HOST_PARAM, String.class, "SHADOOPDB Server", true,
                                        "localhost" ) );
            shadoopParams.add( new Param( SHADOOP_PORT_PARAM, Integer.class, "SHADOOPDB Port", true,
                                        Integer.valueOf( 27017 ) ) );
            shadoopParams.add( new Param( SHADOOP_DB_NAME_PARAM, String.class, "SHADOOPDB Database",
                                        true, "db" ) );
        }
        return shadoopParams;
    }

    public ShadoopPluginConfig (Map<String, Serializable> params) throws ShadoopPluginException
    {
        String msg = "SHADOOP Plugin Configuration Error";

        try
        {
            String param = params.get( NAMESPACE_PARAM ).toString();
            if (param == null)
            {
                msg = "SHADOOP Plugin: Missing namespace param";
                throw new Exception();
            }
            namespace = param;
            param = params.get( SHADOOP_HOST_PARAM ).toString();
            if (param == null)
            {
                msg = "Mongo Plugin: Missing server name param";
                throw new Exception();
            }
            shadoopHost = param;
            param = params.get( SHADOOP_PORT_PARAM ).toString();
            if (param == null)
            {
                msg = "Mongo Plugin: Missing port param";
                throw new Exception();
            }
            msg = "Mongo Plugin: Error parsing port param";
            shadoopPort = Integer.parseInt( param );
            param = params.get( SHADOOP_DB_NAME_PARAM ).toString();
            if (param == null)
            {
                msg = "Mongo Plugin: Missing database name param";
                throw new Exception();
            }
            shadoopDB = param;
        }
        catch (Throwable t)
        {
            throw new ShadoopPluginException( msg );
        }
    }

    public String getHost ()
    {
        return shadoopHost;
    }

    public int getPort ()
    {
        return shadoopPort;
    }

    public String getDB ()
    {
        return shadoopDB;
    }

    public String getNamespace ()
    {
        return namespace;
    }

    /**
     * Get logger for use with this package
     * 
     * @return package logger
     */
    public static Logger getLog ()
    {
        return log;
    }

}
