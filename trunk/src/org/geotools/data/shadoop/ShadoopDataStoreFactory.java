package org.geotools.data.shadoop;

import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ShadoopDataStore objects.
 */
public class ShadoopDataStoreFactory implements DataStoreFactorySpi
{

    /** The Constant FACTORY_DESCRIPTION. */
    private static final String FACTORY_DESCRIPTION  = "MongoDB GeoServer Plugin";
    
    /** The Constant FACTORY_DISPLAY_NAME. */
    private static final String FACTORY_DISPLAY_NAME = "MongoDB";
    
    /** Package logger. */
    static private final Logger log                  = ShadoopPluginConfig.getLog();

    /**
     * Creates a new ShadoopDataStore object.
     *
     * @param map the map
     * @return the data store
     */
    public DataStore createNewDataStore (Map<String, Serializable> map)
    {
        return createDataStore( map );
    }

    /**
     * Creates a new ShadoopDataStore object.
     *
     * @param params the params
     * @return the data store
     */
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

    /**
     * Checks if is available.
     *
     * @return true, if is available
     */
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

    /**
     * Can process.
     *
     * @param params the params
     * @return true, if successful
     */
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

    /**
     * Gets the parameters info.
     *
     * @return the parameters info
     */
    public DataStoreFactorySpi.Param[] getParametersInfo ()
    {
        List<Param> params = ShadoopPluginConfig.getPluginParams();
        return params.toArray( new Param[params.size()] );
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription ()
    {
        return FACTORY_DESCRIPTION;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName ()
    {
        return FACTORY_DISPLAY_NAME;
    }

    /**
     * Gets the implementation hints.
     *
     * @return the implementation hints
     */
    public Map<RenderingHints.Key, ?> getImplementationHints ()
    {
        return Collections.emptyMap();
    }
}
