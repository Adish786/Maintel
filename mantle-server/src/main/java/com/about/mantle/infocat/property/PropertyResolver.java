package com.about.mantle.infocat.property;

import java.util.Collection;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

public class PropertyResolver extends StdTypeResolverBuilder {
	@Override
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        return new PropertyDeserializer(baseType, _customIdResolver,
            _typeProperty, _typeIdVisible, _defaultImpl == null ? null : config.constructType(_defaultImpl));
    }
}
