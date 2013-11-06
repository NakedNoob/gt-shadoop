package org.geotools.data.shadoop;

import org.geotools.data.FeatureReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopFeatureReader.
 */
public class ShadoopFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature>
{

    /** The results. */
    private ShadoopResultSet results;
    
    /** The next. */
    private int            next = 0;

    /**
     * Instantiates a new shadoop feature reader.
     *
     * @param rs the rs
     */
    public ShadoopFeatureReader (ShadoopResultSet rs)
    {
        results = rs;
    }

    /**
     * Close.
     */
    public void close ()
    {
    }

    /**
     * Checks for next.
     *
     * @return true, if successful
     */
    public boolean hasNext ()
    {
        return (next < results.getCount());
    }

    /**
     * Next.
     *
     * @return the simple feature
     */
    public SimpleFeature next ()
    {
        return results.getFeature( next++ );
    }

    /**
     * Gets the feature type.
     *
     * @return the feature type
     */
    public SimpleFeatureType getFeatureType ()
    {
        return results.getSchema();
    }

}
