/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.querydsl.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.xpack.sql.expression.Expression;
import org.elasticsearch.xpack.sql.expression.Foldables;
import org.elasticsearch.xpack.sql.tree.Location;
import org.elasticsearch.xpack.sql.type.DataType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

public class TermsQuery extends LeafQuery {

    private final String term;
    private final LinkedHashSet<Object> values;

    public TermsQuery(Location location, String term, List<Expression> values) {
        super(location);
        this.term = term;
        values.removeIf(e -> e.dataType() == DataType.NULL);
        this.values = new LinkedHashSet<>(Foldables.valuesOf(values, values.get(0).dataType()));
        this.values.removeIf(Objects::isNull);
    }

    @Override
    public QueryBuilder asBuilder() {
        return termsQuery(term, values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, values);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TermsQuery other = (TermsQuery) obj;
        return Objects.equals(term, other.term)
            && Objects.equals(values, other.values);
    }

    @Override
    protected String innerToString() {
        return term + ":" + values;
    }
}
