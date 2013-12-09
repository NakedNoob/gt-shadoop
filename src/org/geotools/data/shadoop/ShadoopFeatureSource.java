package org.geotools.data.shadoop;

import java.awt.RenderingHints;
import java.util.Collections;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.shadoop.query.BaseShadoopQueryObject;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.BBOX;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopFeatureSource.
 */
public class ShadoopFeatureSource implements SimpleFeatureSource {

	/** The store. */
	private ShadoopDataStore store;
	
	/** The layer. */
	private ShadoopLayer layer = null;
	
	/** The results. */
	private ShadoopResultSet results = null;
	
	/** The query caps. */
	private ShadoopQueryCapabilities queryCaps = new ShadoopQueryCapabilities();
	
	/** The info. */
	private ShadoopResourceInfo info = null;

	/**
	 * Instantiates a new shadoop feature source.
	 *
	 * @param store the store
	 * @param layer the layer
	 */
	public ShadoopFeatureSource(ShadoopDataStore store, ShadoopLayer layer) {
		init(store, layer, null);
	}

	/**
	 * Instantiates a new shadoop feature source.
	 *
	 * @param store the store
	 * @param layer the layer
	 * @param dbo the dbo
	 */
	public ShadoopFeatureSource(ShadoopDataStore store, ShadoopLayer layer,
			BaseShadoopQueryObject dbo) {
		init(store, layer, dbo);
	}

	/**
	 * Inits the.
	 *
	 * @param store the store
	 * @param layer the layer
	 * @param dbo the dbo
	 */
	private void init(ShadoopDataStore store, ShadoopLayer layer,
			BaseShadoopQueryObject dbo) {
		this.store = store;
		this.layer = layer;
		BaseShadoopQueryObject query = dbo;
		if (query == null) {
			query = new BaseShadoopQueryObject();
		}
		results = new ShadoopResultSet(layer, query);
		info = new ShadoopResourceInfo(this);
	}

	/**
	 * Gets the layer.
	 *
	 * @return the layer
	 */
	public ShadoopLayer getLayer() {
		return layer;
	}

	/**
	 * Gets the bounds.
	 *
	 * @return the bounds
	 */
	public ReferencedEnvelope getBounds() {
		return results.getBounds();
	}

	/**
	 * Gets the keywords.
	 *
	 * @return the keywords
	 */
	public Set<String> getKeywords() {
		return layer.getKeywords();
	}

	/**
	 * Gets the supported hints.
	 *
	 * @return the supported hints
	 */
	public final Set<RenderingHints.Key> getSupportedHints() {
		return Collections.emptySet();
	}

	/**
	 * Gets the count.
	 *
	 * @param query the query
	 * @return the count
	 */
	public final int getCount(final Query query) {
		int res = 0;
		FilterToShadoopQuery f2m = new FilterToShadoopQuery();
		Filter filter = query.getFilter();
		BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept(
				f2m, null);
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo);
		res = rs.getCount();
		return res;
	}

	/**
	 * Gets the bounds.
	 *
	 * @param query the query
	 * @return the bounds
	 */
	public final ReferencedEnvelope getBounds(final Query query) {
		FilterToShadoopQuery f2m = new FilterToShadoopQuery();
		Filter filter = query.getFilter();
		BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept(
				f2m, null);
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo);
		return rs.getBounds();
	}

	/**
	 * Gets the features.
	 *
	 * @return the features
	 */
	public final SimpleFeatureCollection getFeatures() {
		return new ShadoopFeatureCollection(results);
	}

	/**
	 * Gets the features.
	 *
	 * @param filter the filter
	 * @return the features
	 */
	public final SimpleFeatureCollection getFeatures(final Filter filter) {
		FilterToShadoopQuery f2m = new FilterToShadoopQuery();
		BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept(
				f2m, null);
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo);
		return new ShadoopFeatureCollection(rs);
	}

	/**
	 * Gets the features.
	 *
	 * @param query the query
	 * @return the features
	 */
	public final SimpleFeatureCollection getFeatures(final Query query) {
		FilterToShadoopQuery f2m = new FilterToShadoopQuery();
		Filter filter = query.getFilter();
		BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept(
				f2m, null);

        System.out.println("############################################# START" + 
        		"\nfilter: " + filter.toString());
        // TODO - This is where GeoServer goes through for OpenLayers. 

        System.out.println("############################################# END");
        int pointsInfo[] = CoordUtil.extract(filter.toString().trim());
        
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo, pointsInfo);
		// check for paging; maxFeatures and/or startIndex
		int maxFeatures = query.getMaxFeatures();
		if (maxFeatures > 0) {
			int startIndex = 0;
			if (query.getStartIndex() != null) {
				startIndex = query.getStartIndex().intValue();
			}
			rs.paginateFeatures(startIndex, maxFeatures);
		}
		return new ShadoopFeatureCollection(rs);
	}

	/**
	 * Gets the schema.
	 *
	 * @return the schema
	 */
	public final SimpleFeatureType getSchema() {
		return layer.getSchema();
	}

	/**
	 * Adds the feature listener.
	 *
	 * @param listener the listener
	 */
	public final void addFeatureListener(final FeatureListener listener) {
		store.addListener(this, listener);
	}

	/**
	 * Removes the feature listener.
	 *
	 * @param listener the listener
	 */
	public final void removeFeatureListener(final FeatureListener listener) {
		store.removeListener(this, listener);
	}

	/**
	 * Gets the data store.
	 *
	 * @return the data store
	 */
	public final DataStore getDataStore() {
		return store;
	}

	/**
	 * Gets the query capabilities.
	 *
	 * @return the query capabilities
	 */
	public QueryCapabilities getQueryCapabilities() {
		return queryCaps;
	}

	/**
	 * Gets the info.
	 *
	 * @return the info
	 */
	public ResourceInfo getInfo() {
		return info;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public Name getName() {
		return layer.getSchema().getName();
	}
}
