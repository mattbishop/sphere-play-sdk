package de.commercetools.internal;

import com.google.common.base.Strings;
import de.commercetools.sphere.client.FacetExpression;

public abstract class FacetExpressionBase implements FacetExpression {
    protected String attribute;

    protected FacetExpressionBase(String attribute) {
        if (Strings.isNullOrEmpty(attribute))
            throw new IllegalArgumentException("Please specify an attribute to facet on.");
        this.attribute = attribute;
    }

    public String getAttributeName() {
        return attribute;
    }
}