package com.about.mantle.infocat.model.product;

import java.util.Collection;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

public class ProductTypeResolver extends StdTypeResolverBuilder {
	@Override
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        return new ProductTypeDeserializer(baseType, _customIdResolver,
            _typeProperty, _typeIdVisible, config.constructType(_defaultImpl));
    }
}
