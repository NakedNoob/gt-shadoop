package org.geotools.data.shadoop;

import org.geotools.data.QueryCapabilities;
import org.opengis.filter.sort.SortBy;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopQueryCapabilities.
 */
public class ShadoopQueryCapabilities extends QueryCapabilities
{

    /**
     * Instantiates a new shadoop query capabilities.
     */
    public ShadoopQueryCapabilities ()
    {
    }

    // TODO implement sorting...
    /**
     * Supports sorting.
     *
     * @param sortAttributes the sort attributes
     * @return true, if successful
     */
    @Override
    public boolean supportsSorting (SortBy[] sortAttributes)
    {
        return false;
    }

}
