package com.about.mantle.model.tasks;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.about.globe.core.definition.common.TaskModel;
import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TaxeneRelationshipEx;
import com.about.mantle.model.utils.ComponentLoaderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JourneyTaxeneRelationTaskTest {

	private TaxeneRelationTask taxeneRelationTask;
	private ComponentLoaderUtils templateLoader;
	private RequestContext requestContext;
	private Map<String, TaskModel> taskModels;

	private final ObjectMapper objectMapper;

	public JourneyTaxeneRelationTaskTest() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
	}

	private final String testFilePath = "./src/test/resources/tasks/journeyTaxeneRelationTest/template.xml";
	private final String jsonFilePath = "./src/test/resources/tasks/journeyTaxeneRelationTest/journey-breadcrumb.json";

	@Before
	public void setUp() throws Exception {
		requestContext = mock(RequestContext.class);

		VerticalUrlData verticalUrl = mock(VerticalUrlData.class);
		when(verticalUrl.getDocId()).thenReturn(4135990L);
		when(requestContext.getUrlData()).thenReturn(verticalUrl);

		taxeneRelationTask = mock(TaxeneRelationTask.class);
		templateLoader = new ComponentLoaderUtils(Arrays.asList(new JourneyTaxeneRelationTask(taxeneRelationTask)));

		TaxeneNodeEx taxeneNode = objectMapper.readValue(new File(jsonFilePath), TaxeneNodeEx.class);
		when(taxeneRelationTask.traverse(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
				.thenReturn(taxeneNode);

		taskModels = templateLoader.loadTaskModels(FileUtils.readFileToString(new File(testFilePath)));
	}

	/*
	 * Test Journey Breadcrumb
	 */
	@Test
	public void testJourneyBreadcrumbByReqCtx() throws Exception {
		testJourneyBreadcrumbTask("breadcrumbByReqCtx");
	}

	@Test
	public void testJourneyBreadcrumbByProjection() throws Exception {
		testJourneyBreadcrumbTask("breadcrumbByProjection");
	}

	@Test
	public void testInvalidParametersJourneyBreadcrumb() throws Exception {
		TaskModel task = taskModels.get("invalidParameterBreadcrumb");
		Assert.assertNull(task.getTaskMethod());
	}

	/*
	 * Test Journey Ancestors
	 */
	@Test
	public void testInvalidParametersJourneyAncestors() throws Exception {
		TaskModel task = taskModels.get("invalidParameterAncestors");
		Assert.assertNull(task.getTaskMethod());
	}

	@Test
	public void testJourneyAncestorsByReqCtx() throws Exception {
		testJourneyAncestors("ancestorsByReqCtx", 5, "4053121,4072979,4074003,2059461,4135990");
	}

	@Test
	public void testJourneyAncestorsByProjection() throws Exception {
		testJourneyAncestors("ancestorsByProjection", 5, "4053121,4072979,4074003,2059461,4135990");
	}

	/*
	 * Test Journey Ancestors from Level
	 */
	@Test
	public void testJourneyAncestorsFromLevel() throws Exception {
		testJourneyAncestors("ancestorsFromLevel", 4, "4072979,4074003,2059461,4135990");
	}

	@Test
	public void testJourneyAncestorsFromLevelByProjection() throws Exception {
		testJourneyAncestors("ancestorsFromLevelAndProjection", 3, "4074003,2059461,4135990");
	}

	@Test
	public void testInvalidJourneyAncestorsFromLevel() throws Exception {
		testJourneyAncestors("invalidLevelAncestors", 5, "4053121,4072979,4074003,2059461,4135990");
	}

	private void testJourneyBreadcrumbTask(String modelId) {
		TaskModel task = taskModels.get(modelId);
		Assert.assertNotNull(task);
		Assert.assertNotNull(task.getTaskMethod());

		TaxeneNodeEx taxeneNode = (TaxeneNodeEx) task.getTaskMethod().invoke(requestContext,
				templateLoader.getParameterValues(task));
		Assert.assertNotNull(taxeneNode);
		Assert.assertEquals(Long.valueOf(4135990), taxeneNode.getDocId());

		List<TaxeneRelationshipEx> relationships = taxeneNode.getRelationships().getList();
		// Test relationship
		Assert.assertEquals(1, relationships.size());
		Assert.assertEquals("journey", relationships.get(0).getName());
		Assert.assertEquals(Long.valueOf(2059461), relationships.get(0).getTargetNode().getDocId());
		Assert.assertEquals(new Float(302.0), relationships.get(0).getWeight());
	}

	@SuppressWarnings("unchecked")
	private void testJourneyAncestors(String modelId, int expectedSize, String expectedDocIds) {
		TaskModel task = taskModels.get(modelId);
		Assert.assertNotNull(task);
		Assert.assertNotNull(task.getTaskMethod());

		List<TaxeneNodeEx> taxeneList = (List<TaxeneNodeEx>) task.getTaskMethod().invoke(requestContext,
				templateLoader.getParameterValues(task));
		System.out.println(getDocIdsString(taxeneList));
		Assert.assertEquals(expectedSize, taxeneList.size());
		Assert.assertEquals(expectedDocIds, getDocIdsString(taxeneList));
	}

	private String getDocIdsString(List<TaxeneNodeEx> taxeneList) {
		List<Long> docIds = taxeneList.stream().map(node -> node.getDocId()).collect(Collectors.toList());
		return StringUtils.join(docIds, ",");
	}

}
