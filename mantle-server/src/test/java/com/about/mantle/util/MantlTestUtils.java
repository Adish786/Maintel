package com.about.mantle.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.about.globe.core.app.GlobeExternalConfigKeys;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.netflix.archaius.api.Property;
import com.netflix.archaius.api.PropertyContainer;
import com.netflix.archaius.api.PropertyFactory;

public class MantlTestUtils {

	public static final String APPLICATION_NAME = "mantle-ref"; 
	
	public static PropertyFactory generateMockPropertyFactory(Boolean shouldSuppressEndComment, Boolean shouldAddSubdomains, Boolean isCaesEnabled, Boolean uncappedImageWidthsEnabled) {
		PropertyFactory propertyFactory = mock(PropertyFactory.class);
		when(propertyFactory.getProperty(MantleExternalConfigKeys.SUPPRESS_END_COMMENTS)).then(new Answer<PropertyContainer>() {
			public PropertyContainer answer(InvocationOnMock invocation) throws Throwable {
				PropertyContainer mockPropertyContainer = mock(PropertyContainer.class);
				when(mockPropertyContainer.asBoolean(any())).then(new Answer<Property<Boolean>>() {
					@SuppressWarnings("unchecked")
					public Property<Boolean> answer(InvocationOnMock invocation) throws Throwable {
						Property<Boolean> mockProperty = mock(Property.class);
						when(mockProperty.get()).thenReturn(shouldSuppressEndComment);
						return mockProperty;
					}
				});
				return mockPropertyContainer;
			}			
		});
		// this is required so that CoreRenderUtils() doesn't crap the bed, but is irrelevant to the actual test
		when(propertyFactory.getProperty(GlobeExternalConfigKeys.INTERNAL_SUBDOMAINS)).then(new Answer<PropertyContainer>() {
			public PropertyContainer answer(InvocationOnMock invocation) throws Throwable {
				PropertyContainer mockPropertyContainer = mock(PropertyContainer.class);
				when(mockPropertyContainer.asString(any())).then(new Answer<Property<String>>() {
					@SuppressWarnings("unchecked")
					public Property<String> answer(InvocationOnMock invocation) throws Throwable {
						Property<String> mockProperty = mock(Property.class);
						when(mockProperty.get()).thenReturn(shouldAddSubdomains? "dotdash.com" : null);
						return mockProperty;
					}
				});
				return mockPropertyContainer;
			}			
		});
		// this is required so that MantleRenderUtils inits without error
		when(propertyFactory.getProperty(MantleExternalConfigKeys.SANDBOXED_CONTENT_BASE_URL)).then(new Answer<PropertyContainer>() {
			public PropertyContainer answer(InvocationOnMock invocation) throws Throwable {
				PropertyContainer mockPropertyContainer = mock(PropertyContainer.class);
				when(mockPropertyContainer.asString(any())).then(new Answer<Property<String>>() {
					@SuppressWarnings("unchecked")
					public Property<String> answer(InvocationOnMock invocation) throws Throwable {
						Property<String> mockProperty = mock(Property.class);
						when(mockProperty.get()).thenReturn("dotdash.com");
						return mockProperty;
					}
				});
				return mockPropertyContainer;
			}
		});

		// this is required so that MantleRenderUtils inits without error
		when(propertyFactory.getProperty(MantleExternalConfigKeys.SAFELIST_DOMAINS)).then(new Answer<PropertyContainer>() {
			public PropertyContainer answer(InvocationOnMock invocation) throws Throwable {
				PropertyContainer mockPropertyContainer = mock(PropertyContainer.class);
				when(mockPropertyContainer.asString(any())).then(new Answer<Property<String>>() {
					@SuppressWarnings("unchecked")
					public Property<String> answer(InvocationOnMock invocation) throws Throwable {
						Property<String> mockProperty = mock(Property.class);
						when(mockProperty.get()).thenReturn("dotdash.com");
						return mockProperty;
					}
				});
				return mockPropertyContainer;
			}
		});

		when(propertyFactory.getProperty(MantleExternalConfigKeys.CAES_ENABLED)).then(new Answer<PropertyContainer>() {
			public PropertyContainer answer(InvocationOnMock invocation) throws Throwable {
				PropertyContainer mockPropertyContainer = mock(PropertyContainer.class);
				when(mockPropertyContainer.asBoolean(any())).then(new Answer<Property<Boolean>>() {
					@SuppressWarnings("unchecked")
					public Property<Boolean> answer(InvocationOnMock invocation) throws Throwable {
						Property<Boolean> mockProperty = mock(Property.class);
						when(mockProperty.get()).thenReturn(isCaesEnabled);
						return mockProperty;
					}
				});
				return mockPropertyContainer;
			}
		});

		when(propertyFactory.getProperty(MantleExternalConfigKeys.UNCAPPED_IMAGE_WIDTHS_ENABLED)).then(new Answer<PropertyContainer>() {
			public PropertyContainer answer(InvocationOnMock invocation) throws Throwable {
				PropertyContainer mockPropertyContainer = mock(PropertyContainer.class);
				when(mockPropertyContainer.asBoolean(any())).then(new Answer<Property<Boolean>>() {
					@SuppressWarnings("unchecked")
					public Property<Boolean> answer(InvocationOnMock invocation) throws Throwable {
						Property<Boolean> mockProperty = mock(Property.class);
						when(mockProperty.get()).thenReturn(uncappedImageWidthsEnabled);
						return mockProperty;
					}
				});
				return mockPropertyContainer;
			}
		});
		return propertyFactory;
	}
	
}
