package org.geotools.data.shadoop;

import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;

public class ShadoopDataStoreFactory implements DataStoreFactorySpi
{

    private static final String FACTORY_DESCRIPTION  = "MongoDB GeoServer Plugin";
    private static final String FACTORY_DISPLAY_NAME = "MongoDB";
    /** Package logger */
    static private final Logger log                  = ShadoopPluginConfig.getLog();

    public DataStore createNewDataStore (Map<String, Serializable> map)
    {
        return createDataStore( map );
    }

    public DataStore createDataStore (Map<String, Serializable> params)
    {
        DataStore theStore = null;
        log.info( "DataStore.createDataStore()" );
        try
        {
            ShadoopPluginConfig config = new ShadoopPluginConfig( params );
            theStore = new ShadoopDataStore( config );
            log.info( "DataStore.createDataStore(); theStore=" + theStore );
        }
        catch (Throwable t)
        {
            log.severe( t.getLocalizedMessage() );
        }
        return theStore;
    }

    public boolean isAvailable ()
    {
        boolean result = false;
        try
        {
            // basic check to ensure shadoop jar available
            Class.forName( "com.shadoopdb.BaseDSObject" );
            result = true;
        }
        catch (Throwable t)
        {
            log.severe( "Mongo Plugin: The MongoDB JAR file was not found on the class path." );
        }
        return result;
    }

    public boolean canProcess (Map<String, Serializable> params)
    {

        boolean result = true;

        try
        {
            new ShadoopPluginConfig( params );
        }
        catch (ShadoopPluginException e)
        {
            result = false;
        }

        return result;
    }

    public DataStoreFactorySpi.Param[] getParametersInfo ()
    {
        List<Param> params = ShadoopPluginConfig.getPluginParams();
        return params.toArray( new Param[params.size()] );
    }

    public String getDescription ()
    {
        return FACTORY_DESCRIPTION;
    }

    public String getDisplayName ()
    {
        return FACTORY_DISPLAY_NAME;
    }

    public Map<RenderingHints.Key, ?> getImplementationHints ()
    {
        return Collections.emptyMap();
    }
}
