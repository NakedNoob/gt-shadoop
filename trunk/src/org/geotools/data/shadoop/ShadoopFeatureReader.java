package org.geotools.data.shadoop;

import org.geotools.data.FeatureReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class ShadoopFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature>
{

    private ShadoopResultSet results;
    private int            next = 0;

    public ShadoopFeatureReader (ShadoopResultSet rs)
    {
        results = rs;
    }

    public void close ()
    {
    }

    public boolean hasNext ()
    {
        return (next < results.getCount());
    }

    public SimpleFeature next ()
    {
        return results.getFeature( next++ );
    }

    public SimpleFeatureType getFeatureType ()
    {
        return results.getSchema();
    }

}
