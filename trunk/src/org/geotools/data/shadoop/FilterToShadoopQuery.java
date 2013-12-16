package org.geotools.data.shadoop;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.geotools.data.shadoop.query.BaseShadoopQueryObject;
import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;



/**
 * The Class FilterToShadoopQuery.
 */
public class FilterToShadoopQuery implements FilterVisitor, ExpressionVisitor
{
    /**
     * Instantiates a new filter to shadoop query.
     */
    public FilterToShadoopQuery ()
    {
    }

    /**
     * As ds object.
     *
     * @param extraData the extra data
     * @return the base shadoop query object
     */
    protected BaseShadoopQueryObject asDSObject (Object extraData){
        if ((extraData != null) || (extraData instanceof BaseShadoopQueryObject))
        {
            return (BaseShadoopQueryObject) extraData;
        }
        return new BaseShadoopQueryObject();
    }

    /**
     * Visit.
     *
     * @param expression the expression
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Literal expression, Object extraData){
        Object literal = expression.getValue();
        String ret = literal.toString();
        return ret;
    }

    /**
     * Visit.
     *
     * @param expression the expression
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyName expression, Object extraData){
        return expression.getPropertyName();
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the base shadoop query object
     */
    public BaseShadoopQueryObject visit (ExcludeFilter filter, Object extraData){
    	BaseShadoopQueryObject output = asDSObject( extraData );
        output.put( "foo", "not_likely_to_exist" );
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (IncludeFilter filter, Object extraData){
    	BaseShadoopQueryObject output = asDSObject( extraData );
        return output;
    }

    // Expressions like ((A == 1) AND (B == 2)) are basically
    // implied. So just build up all sub expressions
    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (And filter, Object extraData){
    	BaseShadoopQueryObject output = asDSObject( extraData );

        List<Filter> children = filter.getChildren();
        if (children != null)
        {
            for (Iterator<Filter> i = children.iterator(); i.hasNext();)
            {
                Filter child = i.next();
                child.accept( this, output );
            }
        }

        return output;
    }

    /**
     * Encoding an Id filter is not supported by CQL.
     * <p>
     * This is because in the Catalog specification retrieving an object by an id is a distinct
     * operation separate from a filter based query.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Id filter, Object extraData){
        throw new IllegalStateException( "Cannot encode an Id as legal CQL" );
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Not filter, Object extraData){

    	BaseShadoopQueryObject output = asDSObject( extraData );
    	BaseShadoopQueryObject expr = (BaseShadoopQueryObject) filter.getFilter().accept( this, null );
        output.put( "$not", expr );
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Or filter, Object extraData){

    	return null;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsBetween filter, Object extraData){
    	return null;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsEqualTo filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr1 = filter.getExpression1().accept( this, null );
        Object expr2 = filter.getExpression2().accept( this, null );
        if ((expr1 instanceof String) && (expr2 instanceof String))
        {
            // try to determine if equality check against a number, and if so whether fp or int
            // assuming no units/currency markings present, e.g. "$" "ft." etc.
            String expr2Str = ((String) expr2).trim();
            try
            {
                if (expr2Str.matches( "-? ?(\\d+,)*\\d+" )) // integer
                {
                    output.put( (String) expr1, new Long( (String) expr2 ) );
                }
                else if (expr2Str.matches( "-? ?(\\d+,)*\\d+\\.\\d+" )) // floating point
                {
                    output.put( (String) expr1, new Double( (String) expr2 ) );
                }
                else
                {
                    output.put( (String) expr1, expr2 );
                }
            }
            catch (NumberFormatException e)
            {
                output.put( (String) expr1, expr2 );
            }
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsNotEqualTo filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr1 = filter.getExpression1().accept( this, null );
        Object expr2 = filter.getExpression2().accept( this, null );
        if ((expr1 instanceof String) && (expr2 instanceof String))
        {
            BaseShadoopQueryObject dbo = new BaseShadoopQueryObject();
            try
            {
                dbo.put( "$ne", new Double( (String) expr2 ) );
            }
            catch (NumberFormatException e)
            {
                dbo.put( "$ne", expr2 );
            }
            output.put( (String) expr1, dbo );
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsGreaterThan filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr1 = filter.getExpression1().accept( this, null );
        Object expr2 = filter.getExpression2().accept( this, null );
        if ((expr1 instanceof String) && (expr2 instanceof String))
        {
            BaseShadoopQueryObject dbo = new BaseShadoopQueryObject();
            try
            {
                dbo.put( "$gt", new Double( (String) expr2 ) );
            }
            catch (NumberFormatException e)
            {
                dbo.put( "$gt", expr2 );
            }
            output.put( (String) expr1, dbo );
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsGreaterThanOrEqualTo filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr1 = filter.getExpression1().accept( this, null );
        Object expr2 = filter.getExpression2().accept( this, null );
        if ((expr1 instanceof String) && (expr2 instanceof String))
        {
            BaseShadoopQueryObject dbo = new BaseShadoopQueryObject();
            try
            {
                dbo.put( "$gte", new Double( (String) expr2 ) );
            }
            catch (NumberFormatException e)
            {
                dbo.put( "$gte", expr2 );
            }
            output.put( (String) expr1, dbo );
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsLessThan filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr1 = filter.getExpression1().accept( this, null );
        Object expr2 = filter.getExpression2().accept( this, null );
        if ((expr1 instanceof String) && (expr2 instanceof String))
        {
            BaseShadoopQueryObject dbo = new BaseShadoopQueryObject();
            try
            {
                dbo.put( "$lt", new Double( (String) expr2 ) );
            }
            catch (NumberFormatException e)
            {
                dbo.put( "$lt", expr2 );
            }
            output.put( (String) expr1, dbo );
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsLessThanOrEqualTo filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr1 = filter.getExpression1().accept( this, null );
        Object expr2 = filter.getExpression2().accept( this, null );
        if ((expr1 instanceof String) && (expr2 instanceof String))
        {
            BaseShadoopQueryObject dbo = new BaseShadoopQueryObject();
            try
            {
                dbo.put( "$lte", new Double( (String) expr2 ) );
            }
            catch (NumberFormatException e)
            {
                dbo.put( "$lte", expr2 );
            }
            output.put( (String) expr1, dbo );
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsLike filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr = filter.getExpression();
        if (expr instanceof String)
        {
            String multi = filter.getWildCard();
            String single = filter.getSingleChar();
            int flags = (filter.isMatchingCase()) ? 0 : Pattern.CASE_INSENSITIVE;
            String cqlPattern = filter.getLiteral();
            cqlPattern.replaceAll( multi, ".*" );
            cqlPattern.replaceAll( single, "." );
            try
            {
                Pattern p = Pattern.compile( cqlPattern, flags );
                output.put( (String) expr, p );
            }
            catch (Throwable t)
            {
            }
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (PropertyIsNull filter, Object extraData){
        BaseShadoopQueryObject output = asDSObject( extraData );
        Object expr = filter.accept( this, null );
        if (expr instanceof String)
        {
            BaseShadoopQueryObject dbo = new BaseShadoopQueryObject();
            dbo.put( "$exists", false );
            output.put( (String) expr, dbo );
        }
        return output;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (BBOX filter, Object extraData){
    	BaseShadoopQueryObject output = asDSObject( extraData );

        double minX = 180;
        double minY = 90;
        double maxX = -180;
        double maxY = -90;

        Expression exp2 = filter.getExpression2();
        if (exp2 instanceof Literal)
        {
            Geometry bbox = (Geometry) ((Literal) exp2).getValue();
            Coordinate[] coords = bbox.getEnvelope().getCoordinates();
            minX = coords[0].x;
            minY = coords[0].y;
            maxX = coords[2].x;
            maxY = coords[2].y;
        }

        if (minX < -180)
            minX = -180;
        if (maxX > 180)
            maxX = 180;
        if (minY < -90)
            minY = -90;
        if (maxY > 90)
            maxY = 90;

        StringBuilder sb = new StringBuilder();
        sb.append( "gtmpGeoQuery([" );
        sb.append( minX );
        sb.append( "," );
        sb.append( minY );
        sb.append( "," );
        sb.append( maxX );
        sb.append( "," );
        sb.append( maxY );
        sb.append( "])" );
        output.put( "$where", sb.toString() );

        return output;
    }

    /**
     * Visit null filter.
     *
     * @param extraData the extra data
     * @return the object
     */
    public Object visitNullFilter (Object extraData){
        throw new NullPointerException( "Cannot encode null as a Filter" );
    }

    /**
     * Visit.
     *
     * @param expression the expression
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (NilExpression expression, Object extraData){
        return extraData;
    }

    /**
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */

    public Object visit (Beyond filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Contains filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Crosses filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Disjoint filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (DWithin filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Equals filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Intersects filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Overlaps filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Touches filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Within filter, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param expression the expression
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Add expression, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param expression the expression
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Divide expression, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param function the function
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Function function, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param expression the expression
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Multiply expression, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     *
     * @param expression the expression
     * @param extraData the extra data
     * @return the object
     */
    public Object visit (Subtract expression, Object extraData){
        return extraData;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsNil,java.lang.Object)
     * @param filter the filter
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (PropertyIsNil filter, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.After,java.lang.Object)
     * @param after the after
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (After after, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.AnyInteracts,java.lang.Object)
     * @param anyInteracts the any interacts
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (AnyInteracts anyInteracts, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Before,java.lang.Object)
     * @param before the before
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (Before before, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Begins,java.lang.Object)
     * @param begins the begins
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (Begins begins, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.BegunBy,java.lang.Object)
     * @param begunBy the begun by
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (BegunBy begunBy, Object extraData)
    {
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.During,java.lang.Object)
     * @param during the during
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (During during, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.EndedBy,java.lang.Object)
     * @param endedBy the ended by
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (EndedBy endedBy, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Ends,java.lang.Object)
     * @param ends the ends
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (Ends ends, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Meets,java.lang.Object)
     * @param meets the meets
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (Meets meets, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.MetBy,java.lang.Object)
     * @param metBy the met by
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (MetBy metBy, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.OverlappedBy,java.lang.Object)
     * @param overlappedBy the overlapped by
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (OverlappedBy overlappedBy, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.TContains,java.lang.Object)
     * @param contains the contains
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (TContains contains, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.TEquals,java.lang.Object)
     * @param equals the equals
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (TEquals equals, Object extraData){
        return null;
    }

    /**
     * Visit.
     * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.TOverlaps,java.lang.Object)
     * @param contains the contains
     * @param extraData the extra data
     * @return the object
     */
    @Override
    public Object visit (TOverlaps contains, Object extraData){
        return null;
    }
}
