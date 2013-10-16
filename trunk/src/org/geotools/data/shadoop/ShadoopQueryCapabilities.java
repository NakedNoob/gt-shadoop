package org.geotools.data.shadoop;

import org.geotools.data.QueryCapabilities;
import org.opengis.filter.sort.SortBy;

public class ShadoopQueryCapabilities extends QueryCapabilities
{

    public ShadoopQueryCapabilities ()
    {
    }

    // TODO implement sorting...
    @Override
    public boolean supportsSorting (SortBy[] sortAttributes)
    {
        return false;
    }

}
