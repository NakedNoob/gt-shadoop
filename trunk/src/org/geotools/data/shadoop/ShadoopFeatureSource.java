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

public class ShadoopFeatureSource implements SimpleFeatureSource {

	private ShadoopDataStore store;
	private ShadoopLayer layer = null;
	private ShadoopResultSet results = null;
	private ShadoopQueryCapabilities queryCaps = new ShadoopQueryCapabilities();
	private ShadoopResourceInfo info = null;

	public ShadoopFeatureSource(ShadoopDataStore store, ShadoopLayer layer) {
		init(store, layer, null);
	}

	public ShadoopFeatureSource(ShadoopDataStore store, ShadoopLayer layer,
			BaseShadoopQueryObject dbo) {
		init(store, layer, dbo);
	}

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

	public ShadoopLayer getLayer() {
		return layer;
	}

	public ReferencedEnvelope getBounds() {
		return results.getBounds();
	}

	public Set<String> getKeywords() {
		return layer.getKeywords();
	}

	public final Set<RenderingHints.Key> getSupportedHints() {
		return Collections.emptySet();
	}

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

	public final ReferencedEnvelope getBounds(final Query query) {
		FilterToShadoopQuery f2m = new FilterToShadoopQuery();
		Filter filter = query.getFilter();
		BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept(
				f2m, null);
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo);
		return rs.getBounds();
	}

	public final SimpleFeatureCollection getFeatures() {
		return new ShadoopFeatureCollection(results);
	}

	public final SimpleFeatureCollection getFeatures(final Filter filter) {
		FilterToShadoopQuery f2m = new FilterToShadoopQuery();
		BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept(
				f2m, null);
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo);
		return new ShadoopFeatureCollection(rs);
	}

	public final SimpleFeatureCollection getFeatures(final Query query) {
		FilterToShadoopQuery f2m = new FilterToShadoopQuery();
		Filter filter = query.getFilter();
		BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept(
				f2m, null);
		ShadoopResultSet rs = new ShadoopResultSet(layer, dbo);
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

	public final SimpleFeatureType getSchema() {
		return layer.getSchema();
	}

	public final void addFeatureListener(final FeatureListener listener) {
		store.addListener(this, listener);
	}

	public final void removeFeatureListener(final FeatureListener listener) {
		store.removeListener(this, listener);
	}

	public final DataStore getDataStore() {
		return store;
	}

	public QueryCapabilities getQueryCapabilities() {
		return queryCaps;
	}

	public ResourceInfo getInfo() {
		return info;
	}

	public Name getName() {
		return layer.getSchema().getName();
	}
}
