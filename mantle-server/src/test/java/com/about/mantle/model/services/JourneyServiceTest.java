package com.about.mantle.model.services;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.about.hippodrome.models.media.VersionedMediaTypes;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.responses.TaxeneExResponse;
import com.about.mantle.model.journey.JourneyRoot;
import com.about.mantle.model.journey.JourneySection;
import com.about.mantle.model.services.JourneyService.JourneyRequestContext;
import com.about.mantle.model.services.impl.JourneyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JourneyServiceTest {

	private final ObjectMapper objectMapper;

	private final String folderPath = "./src/test/resources/ServicesTest/JourneyServiceTest";
	private final String dataFileName = "journey-data.json";

	public JourneyServiceTest() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
	}

	@Test
	public void testCreateJourneyStructure() throws Exception {
		long docId = 2059461L;
		//@formatter:off
		JourneyRequestContext journeyReqCtx = new JourneyRequestContext.Builder()
											.setDocId(docId)
											.setIncludeDocumentSummaries(true)
											.build();
		//@formatter:on

		JourneyService journeyService = createJourneyService(dataFileName, journeyReqCtx);
		JourneyRoot journeyRoot = journeyService.getJourneyRootAndRelationship(journeyReqCtx).getLeft();

		Assert.assertNotNull(journeyRoot);
		// Test number of sections
		Assert.assertEquals(7, journeyRoot.getSections().size());
		// Test number of documents under each section
		int[] docCount = new int[] { 2, 7, 5, 4, 2, 5, 5 };
		for (int i = 0; i < docCount.length; i++) {
			JourneySection section = journeyRoot.getSections().get(i);

			Assert.assertNotNull(section.getDocument().getDocumentId());
			Assert.assertEquals(docCount[i], section.getJourneyDocuments().size());
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testInvalidOperation() throws Exception {
		long docId = 2059461L;
		//@formatter:off
		JourneyRequestContext journeyReqCtx = new JourneyRequestContext.Builder()
											.setDocId(docId)
											.setIncludeDocumentSummaries(true)
											.build();
		//@formatter:on

		JourneyService journeyService = createJourneyService(dataFileName, journeyReqCtx);
		JourneyRoot journeyRoot = journeyService.getJourneyRootAndRelationship(journeyReqCtx).getLeft();

		journeyRoot.getSections().add(new JourneySection(journeyRoot.getNode()));
	}

	private JourneyService createJourneyService(String fileName, JourneyRequestContext reqCtx) throws Exception {
		
		HttpServiceClientConfig.Builder httpConfigBuilder = new HttpServiceClientConfig.Builder();
		httpConfigBuilder.setMediaType(VersionedMediaTypes.V2_JSON_WITH_REDUCED_WEIGHT);
		httpConfigBuilder.setBaseUrl("http://journey-service-test");
		
		String filePath = folderPath + File.separator + fileName;
		TaxeneExResponse taxeneResponse = objectMapper.readValue(new File(filePath), TaxeneExResponse.class);

		WebTarget target = mock(WebTarget.class);
		Builder builder = mock(Invocation.Builder.class);
		Response response = mock(Response.class);

		// Mock Web Target
		when(target.path(anyString())).thenReturn(target);
		when(target.queryParam(Mockito.any(), Mockito.any())).thenReturn(target);
		when(target.getUri()).thenReturn(new URI("http://journey-service-test"));
		when(target.request()).thenReturn(builder);

		// Mock Builder
		when(builder.accept(anyString())).thenReturn(builder);
		when(builder.method("GET")).thenReturn(response);

		// Mock response
		when(response.getStatus()).thenReturn(200);
		when(response.readEntity(TaxeneExResponse.class)).thenReturn(taxeneResponse);

		return new JourneyServiceImpl(target, httpConfigBuilder.build());
	}
}
