package com.about.mantle.model.mock;

import java.util.ArrayList;
import java.util.List;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tasks
public class MockTaxeneRelationTask {
	private final MockSource<TaxeneNodeEx> breadcrumbMocks;
	private final MockSource<List<TaxeneNodeEx>> breadcrumbSchemaMocks;
	private final MockSource<List<BaseDocumentEx>> descendantArticlesMocks;

	public MockTaxeneRelationTask(ObjectMapper objectMapper) {
		List<TaxeneNodeEx> list = new ArrayList<>();
		this.breadcrumbMocks = new MockSource<TaxeneNodeEx>("breadcrumb",
				objectMapper.getTypeFactory().constructType(TaxeneNodeEx.class));
		this.breadcrumbSchemaMocks = new MockSource<List<TaxeneNodeEx>>("breadcrumb",
				objectMapper.getTypeFactory().constructType(list.getClass()));
		this.descendantArticlesMocks = new MockSource<List<BaseDocumentEx>>("descendantArticles",
				objectMapper.getTypeFactory().constructParametricType(List.class, BaseDocumentEx.class));
	}

	/*
	 * FIXME: the following task is mostly for convenience but we can't add it because it collides with a task
	 *        signature that's already being defined by some verticals. re-enable with the following ticket
	 *        https://iacpublishing.atlassian.net/browse/GLBE-6156
	@Task(name = "mockBreadcrumb")
	public TaxeneNodeEx mockBreadcrumb() {
		return breadcrumbMocks.get("sample-breadcrumb");
	}
	 */

	@Task(name = "mockBreadcrumb")
	public TaxeneNodeEx mockBreadcrumbByName(@TaskParameter(name = "mockName") String mockName) {
		return breadcrumbMocks.get(mockName);
	}
	
	@Task(name = "mockSchemaBreadcrumb")
	public List<TaxeneNodeEx> mockSchemaBreadcrumbByName(@TaskParameter(name = "mockName") String mockName) {
		return breadcrumbSchemaMocks.get(mockName);
	}

	/*
	 * FIXME: the following task is mostly for convenience but we can't add it because it collides with a task
	 *        signature that's already being defined by some verticals. re-enable with the following ticket
	 *        https://iacpublishing.atlassian.net/browse/GLBE-6156
	@Task(name = "mockExtendedBreadcrumb")
	public TaxeneNodeEx mockExtendedBreadcrumb() {
		return breadcrumbMocks.get("sample-extended-breadcrumb");
	}
	 */

	@Task(name = "mockExtendedBreadcrumb")
	public TaxeneNodeEx mockExtendedBreadcrumbByName(@TaskParameter(name = "mockName") String mockName) {
		return breadcrumbMocks.get(mockName);
	}

	@Task(name = "mockDescendantArticles")
	public List<BaseDocumentEx> mockDescendantArticlesByName(@TaskParameter(name = "mockName") String mockName) {
		return descendantArticlesMocks.get(mockName);
	}
}