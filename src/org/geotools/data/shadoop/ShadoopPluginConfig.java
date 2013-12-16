package org.geotools.data.shadoop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.DataAccessFactory.Param;
import org.geotools.util.logging.Logging;

/**
 * The Class ShadoopPluginConfig.
 */
public class ShadoopPluginConfig
{
    /** The Constant SHADOOP_HOST_PARAM. */
	private final static String SHADOOP_HOST_PARAM    = "shadoop_host";
    
    /** The Constant SHADOOP_PORT_PARAM. */
    private final static String SHADOOP_PORT_PARAM    = "shadoop_port";
    
    // AJG - This may not be a necessary parameter
    /** The Constant SHADOOP_DB_NAME_PARAM. */
    private final static String SHADOOP_DB_NAME_PARAM = "shadoop_db_name";
    
    /** The Constant NAMESPACE_PARAM. */
    private final static String NAMESPACE_PARAM     = "namespace";

    /** The shadoop host. */
    private String              shadoopHost;
    
    /** The shadoop port. */
    private int                 shadoopPort;
    
    /** The shadoop db. */
    private String              shadoopDB;
    
    /** The namespace. */
    private String              namespace;
    
    /** Log instance to be shared among package classes. */
    private final static Logger log                 = Logging
                                                            .getLogger( "org.geotools.data.shadoop" );
    // requires proper config of data/logging.xml, and addition of org.geotools.data.shadoop package
    // name
    // in log config/props file referenced by logging.xml

    /** The shadoop params. */
    private static List<Param>  shadoopParams         = null;

    /**
     * Gets the plugin params.
     *
     * @return the plugin params
     */
    public static List<Param> getPluginParams (){
        if (shadoopParams == null){
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

    /**
     * Instantiates a new shadoop plugin config.
     *
     * @param params the params
     * @throws ShadoopPluginException the shadoop plugin exception
     */
    public ShadoopPluginConfig (Map<String, Serializable> params) throws ShadoopPluginException{
        String msg = "SHADOOP Plugin Configuration Error";

        try{
            String param = params.get( NAMESPACE_PARAM ).toString();
            if (param == null){
                msg = "SHADOOP Plugin: Missing namespace param";
                throw new Exception();
            }
            namespace = param;
            param = params.get( SHADOOP_HOST_PARAM ).toString();
            if (param == null){
                msg = "SHADOOP Plugin: Missing server name param";
                throw new Exception();
            }
            shadoopHost = param;
            param = params.get( SHADOOP_PORT_PARAM ).toString();
            if (param == null){
                msg = "SHADOOP Plugin: Missing port param";
                throw new Exception();
            }
            msg = "SHADOOP Plugin: Error parsing port param";
            shadoopPort = Integer.parseInt( param );
            param = params.get( SHADOOP_DB_NAME_PARAM ).toString();
            if (param == null){
                msg = "SHADOOP Plugin: Missing database name param";
                throw new Exception();
            }
            shadoopDB = param;
        }
        catch (Throwable t){
            throw new ShadoopPluginException( msg );
        }
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost (){
        return shadoopHost;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort (){
        return shadoopPort;
    }

    /**
     * Gets the db.
     *
     * @return the db
     */
    public String getDB (){
        return shadoopDB;
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public String getNamespace (){
        return namespace;
    }

    /**
     * Get logger for use with this package.
     *
     * @return package logger
     */
    public static Logger getLog (){
        return log;
    }

}
