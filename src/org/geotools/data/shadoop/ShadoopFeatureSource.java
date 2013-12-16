package org.geotools.data.shadoop;

import java.awt.RenderingHints;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

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
	
	/** The info. */
	private ShadoopResourceInfo info = null;

	static private final Logger          log        = ShadoopPluginConfig.getLog();
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

        System.out.println("\nfilter: " + filter.toString());
        int pointsInfo[] = extract(filter.toString().trim());
        
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo, pointsInfo);
		// check for paging; maxFeatures and/or startIndex
		int maxFeatures = query.getMaxFeatures();
		System.out.println("MAX FEATURES: "+maxFeatures);
		if (maxFeatures > 0) {
			int startIndex = 0;
			System.out.println("STARTING INDEX: "+startIndex);
			if (query.getStartIndex() != null) {
				System.out.println("STARTING INDEX: "+startIndex);
				startIndex = query.getStartIndex().intValue();
			}
			rs.paginateFeatures(startIndex, maxFeatures);
		}
		
		return new ShadoopFeatureCollection(rs);
	}

	/**
	 * Takes in a filter string from Geoserver and builds the BBOX for Shadoop's rangequery
	 * element 0 is X1
	 * element 1 is Y1
	 * element 2 is the width
	 * element 3 is the height
	 * element 4 is a check to see if the bounds are correct (no negative numbers); 0 for invalid, 1 for valid
	 * 
	 * @param filter the String from which to extract the points information needed for the rangequery
	 * @return p[] an Integer array containing the information needed for the rangequery BBOX
	 * 109.16015625 : 138.515625, 4.306640625 : 19.16015625
	 */
	private int[] extract(String filter){
		int p[] = new int[5];
		String y[] = new String[2];
		String x[] = new String[2];
		p[4] = 1;
		double x1,x2,y1,y2;
		int X1,X2,Y1,Y2;
		String arr[] = filter.split("\\[")[2].toString().split("\\]")[0].toString().split(",");
		
		for(int a = 0; a < arr.length; a++){
			if(a == 0)
				x = arr[a].split(":");
			if(a == 1)
				y = arr[a].split(":");
		}
		
		if(x[0].equals(y[0]) && x[1].equals(y[1]))
			p[4] = 0;
		
		x1 = Double.parseDouble(x[0].toString());
		x2 = Double.parseDouble(x[1].toString());
		y1 = Double.parseDouble(y[0].toString());
		y2 = Double.parseDouble(y[1].toString());
		
		X1 = (int)Math.round(x1);
		X2 = (int)Math.round(x2);
		Y1 = (int)Math.round(y1);
		Y2 = (int)Math.round(y2);

		p[0] = Y1;
		p[1] = X1;
		p[2] = (Y2 - Y1);
		p[3] = (X2 - X1);
		System.out.println("##########################################");
		System.out.println("##########################################");
		System.out.println("##########################################");
		System.out.println("p[0] = "+p[0]);
		System.out.println("p[1] = "+p[1]);
		System.out.println("p[2] = "+p[2]);
		System.out.println("p[3] = "+p[3]);
		System.out.println("##########################################");
		System.out.println("##########################################");
		System.out.println("##########################################");
		log.warning("##########################################");
		log.warning("##########################################");
		log.warning("##########################################");
		log.warning("p[0] = "+p[0]);
		log.warning("p[1] = "+p[1]);
		log.warning("p[2] = "+p[2]);
		log.warning("p[3] = "+p[3]);
		log.warning("##########################################");
		log.warning("##########################################");
		log.warning("##########################################");
		for(int b=0; b<4;b++)
			if(p[b] < 0)
				p[4] = 0;
		
		return p;
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

	@Override
	public QueryCapabilities getQueryCapabilities() {
		return null;
	}
}
