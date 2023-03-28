package com.about.mantle.model.tasks;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestContextImpl;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.journey.AbstractJourneyNode;
import com.about.mantle.model.journey.JourneyRelationshipType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.about.mantle.model.extended.NodeAttribute;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.FlexibleArticleDocumentEx;
import com.about.mantle.model.extended.docv2.ProgrammedSummaryDocumentEx;
import com.about.mantle.model.journey.JourneyDocument;
import com.about.mantle.model.journey.JourneyRoot;
import com.about.mantle.model.journey.JourneySection;
import com.about.mantle.model.services.JourneyService;
import com.about.mantle.model.services.JourneyService.JourneyRequestContext;
import com.google.common.collect.Lists;

public class JourneyTaskTest {

	private JourneyService journeyService;
	private JourneyTask journeyTask;

	@Before
	public void setUp() {
		// Create Journey Section (1)
		JourneyDocument document1Section1 = createJourneyDocument(11L, "Document 1 Section 1");
		JourneyDocument document2Section1 = createJourneyDocument(12L, "Document 2 Section 1");
		JourneyDocument document3Section1 = createJourneyDocument(13L, "Document 3 Section 1");
		JourneyDocument document4Section1 = createJourneyDocument(14L, "Document 4 Section 1");

		JourneySection section1 = createJourneySection(1L, "Section 1", document1Section1, document2Section1,
				document3Section1, document4Section1);

		// Create Journey Section (2)
		JourneyDocument document1Section2 = createJourneyDocument(21L, "Document 1 Section 2");
		JourneyDocument document2Section2 = createJourneyDocument(22L, "Document 2 Section 2");
		JourneyDocument document3Section2 = createJourneyDocument(23L, "Document 3 Section 2");
		JourneyDocument document4Section2 = createJourneyDocument(24L, "Document 4 Section 2");
		JourneyDocument document5Section2 = createJourneyDocument(25L, "Document 5 Section 2");
		JourneyDocument document6Section2 = createJourneyDocument(26L, "Document 6 Section 2");

		JourneySection section2 = createJourneySection(2L, "Section 2", document1Section2, document2Section2,
				document3Section2, document4Section2, document5Section2, document6Section2);

		// Create Journey Section (3)
		JourneyDocument document1Section3 = createJourneyDocument(31L, "Document 1 Section 3");
		JourneyDocument document2Section3 = createJourneyDocument(32L, "Document 2 Section 3");
		JourneyDocument document3Section3 = createJourneyDocument(33L, "Document 3 Section 3");

		JourneySection section3 = createJourneySection(3L, "Section 3", document1Section3, document2Section3,
				document3Section3);

		// Create Journey Section (4)
		JourneyDocument document1Section4 = createJourneyDocument(41L, "Document 1 Section 4");
		JourneyDocument document2Section4 = createJourneyDocument(42L, "Document 2 Section 4");
		JourneyDocument document3Section4 = createJourneyDocument(43L, "Document 3 Section 4");
		JourneyDocument document4Section4 = createJourneyDocument(44L, "Document 4 Section 4");
		JourneyDocument document5Section4 = createJourneyDocument(45L, "Document 5 Section 4");

		JourneySection section4 = createJourneySection(4L, "Section 4", document1Section4, document2Section4,
				document3Section4, document4Section4, document5Section4);

		// Create Journey Section (5) [Null Short Heading fields]
		JourneyDocument document1Section5 = createJourneyDocument(51L, null);
		JourneyDocument document2Section5 = createJourneyDocument(52L, null);

		JourneySection section5 = createJourneySection(5L, null, document1Section5, document2Section5);

		JourneyRoot journeyRoot = new JourneyRoot(
				createTaxeneNode(1234L, TemplateTypeEx.FLEXIBLEARTICLE, "Root Short Heading"),
				Lists.newArrayList(section1, section2, section3, section4, section5));

		journeyService = mock(JourneyService.class);
		journeyTask = new JourneyTask(journeyService);

		when(journeyService.getJourneyRootAndRelationship(any(JourneyRequestContext.class))).thenReturn(Pair.of(journeyRoot, JourneyRelationshipType.MEMBER));
	}

	@Test
	public void testGetJourneyStructure() {
		JourneyRoot root = getJourneyRootWithDocId(1234L);

		Assert.assertEquals(Long.valueOf(1234), root.getNode().getDocId());
		Assert.assertEquals(5, root.getSections().size());
		Assert.assertEquals("Root Short Heading", root.getShortHeading());

		JourneySection section1 = root.getSections().get(0);
		Assert.assertEquals(Long.valueOf(1), section1.getDocument().getDocumentId());
		Assert.assertEquals("Section 1", section1.getShortHeading());
		Assert.assertEquals(4, section1.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 1", section1.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 1", section1.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 1", section1.getJourneyDocuments().get(2).getShortHeading());
		Assert.assertEquals("Document 4 Section 1", section1.getJourneyDocuments().get(3).getShortHeading());

		JourneySection section2 = root.getSections().get(1);
		Assert.assertEquals(Long.valueOf(2), section2.getDocument().getDocumentId());
		Assert.assertEquals("Section 2", section2.getShortHeading());
		Assert.assertEquals(6, section2.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 2", section2.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 2", section2.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 2", section2.getJourneyDocuments().get(2).getShortHeading());
		Assert.assertEquals("Document 4 Section 2", section2.getJourneyDocuments().get(3).getShortHeading());
		Assert.assertEquals("Document 5 Section 2", section2.getJourneyDocuments().get(4).getShortHeading());
		Assert.assertEquals("Document 6 Section 2", section2.getJourneyDocuments().get(5).getShortHeading());

		JourneySection section3 = root.getSections().get(2);
		Assert.assertEquals(Long.valueOf(3), section3.getDocument().getDocumentId());
		Assert.assertEquals("Section 3", section3.getShortHeading());
		Assert.assertEquals(3, section3.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 3", section3.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 3", section3.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 3", section3.getJourneyDocuments().get(2).getShortHeading());

		JourneySection section4 = root.getSections().get(3);
		Assert.assertEquals(Long.valueOf(4), section4.getDocument().getDocumentId());
		Assert.assertEquals("Section 4", section4.getShortHeading());
		Assert.assertEquals(5, section4.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 4", section4.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 4", section4.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 4", section4.getJourneyDocuments().get(2).getShortHeading());
		Assert.assertEquals("Document 4 Section 4", section4.getJourneyDocuments().get(3).getShortHeading());
		Assert.assertEquals("Document 5 Section 4", section4.getJourneyDocuments().get(4).getShortHeading());

		JourneySection section5 = root.getSections().get(4);
		Assert.assertEquals(Long.valueOf(5), section5.getDocument().getDocumentId());
		Assert.assertEquals(StringUtils.EMPTY, section5.getShortHeading());
		Assert.assertEquals(2, section5.getJourneyDocuments().size());
		Assert.assertEquals(StringUtils.EMPTY, section5.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals(StringUtils.EMPTY, section5.getJourneyDocuments().get(1).getShortHeading());
	}

	@Test
	public void testGetJourneyStructureWithRequestContext() {
		JourneyRoot root = journeyTask.getJourneyStructure(buildRequestContextWithDocId(1234L), new JourneyTask.JourneyStructureModel.Builder().build());


		Assert.assertEquals(Long.valueOf(1234), root.getNode().getDocId());
		Assert.assertEquals(5, root.getSections().size());
		Assert.assertEquals("Root Short Heading", root.getShortHeading());

		JourneySection section1 = root.getSections().get(0);
		Assert.assertEquals(Long.valueOf(1), section1.getDocument().getDocumentId());
		Assert.assertEquals("Section 1", section1.getShortHeading());
		Assert.assertEquals(4, section1.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 1", section1.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 1", section1.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 1", section1.getJourneyDocuments().get(2).getShortHeading());
		Assert.assertEquals("Document 4 Section 1", section1.getJourneyDocuments().get(3).getShortHeading());

		JourneySection section2 = root.getSections().get(1);
		Assert.assertEquals(Long.valueOf(2), section2.getDocument().getDocumentId());
		Assert.assertEquals("Section 2", section2.getShortHeading());
		Assert.assertEquals(6, section2.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 2", section2.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 2", section2.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 2", section2.getJourneyDocuments().get(2).getShortHeading());
		Assert.assertEquals("Document 4 Section 2", section2.getJourneyDocuments().get(3).getShortHeading());
		Assert.assertEquals("Document 5 Section 2", section2.getJourneyDocuments().get(4).getShortHeading());
		Assert.assertEquals("Document 6 Section 2", section2.getJourneyDocuments().get(5).getShortHeading());

		JourneySection section3 = root.getSections().get(2);
		Assert.assertEquals(Long.valueOf(3), section3.getDocument().getDocumentId());
		Assert.assertEquals("Section 3", section3.getShortHeading());
		Assert.assertEquals(3, section3.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 3", section3.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 3", section3.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 3", section3.getJourneyDocuments().get(2).getShortHeading());

		JourneySection section4 = root.getSections().get(3);
		Assert.assertEquals(Long.valueOf(4), section4.getDocument().getDocumentId());
		Assert.assertEquals("Section 4", section4.getShortHeading());
		Assert.assertEquals(5, section4.getJourneyDocuments().size());
		Assert.assertEquals("Document 1 Section 4", section4.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals("Document 2 Section 4", section4.getJourneyDocuments().get(1).getShortHeading());
		Assert.assertEquals("Document 3 Section 4", section4.getJourneyDocuments().get(2).getShortHeading());
		Assert.assertEquals("Document 4 Section 4", section4.getJourneyDocuments().get(3).getShortHeading());
		Assert.assertEquals("Document 5 Section 4", section4.getJourneyDocuments().get(4).getShortHeading());

		JourneySection section5 = root.getSections().get(4);
		Assert.assertEquals(Long.valueOf(5), section5.getDocument().getDocumentId());
		Assert.assertEquals(StringUtils.EMPTY, section5.getShortHeading());
		Assert.assertEquals(2, section5.getJourneyDocuments().size());
		Assert.assertEquals(StringUtils.EMPTY, section5.getJourneyDocuments().get(0).getShortHeading());
		Assert.assertEquals(StringUtils.EMPTY, section5.getJourneyDocuments().get(1).getShortHeading());
	}

	@Test
	public void testGetJourneyStructureWithDocIdAndRequestContext() {
		JourneyRoot root = journeyTask.getJourneyStructure(buildRequestContextWithDocId(null), new JourneyTask.JourneyStructureModel.Builder().setDocId(1234L).build());
		Assert.assertEquals("The task method should not pay attention to the request context in this instance",
				Long.valueOf(1234), root.getNode().getDocId());
	}

	@Test
	public void testGetJourneyStructureNullParameter() {
		JourneyRoot root = journeyTask.getJourneyStructure(buildRequestContextWithDocId(null), new JourneyTask.JourneyStructureModel.Builder().build());
		Assert.assertNull(root.getNode());
	}

	@Test
	public void testCurrentSectionUsingSectionDocId() {
		// This test uses the section's document Id
		JourneyTask.CurrentJourneySectionModel model = new JourneyTask.CurrentJourneySectionModel();
		model.setDocId(4L);
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		JourneySection section = journeyTask.getCurrentJourneySection(null, model);

		Assert.assertEquals(Long.valueOf(4), section.getNode().getDocId());
		Assert.assertEquals(5, section.getJourneyDocuments().size());
	}

	@Test
	public void testIncorrectCurrentSectionUsingRequestContext() {
		JourneySection section = journeyTask.getCurrentJourneySection(buildRequestContextWithDocId(4567L), new JourneyTask.CurrentJourneySectionModel());

		Assert.assertNull(section);
	}

	@Test
	public void testCurrentSectionUsingRequestContext() {
		JourneySection section = journeyTask.getCurrentJourneySection(buildRequestContextWithDocId(4L), new JourneyTask.CurrentJourneySectionModel());

		Assert.assertEquals(Long.valueOf(4), section.getNode().getDocId());
		Assert.assertEquals(5, section.getJourneyDocuments().size());
	}

	@Test
	public void testCurrentSectionUsingSectionDocIdAndRequestContext() {
		// This test uses the section's document Id
		JourneyTask.CurrentJourneySectionModel model = new JourneyTask.CurrentJourneySectionModel();
		model.setDocId(4L);
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		JourneySection section = journeyTask.getCurrentJourneySection(buildRequestContextWithDocId(4567L), model);

		Assert.assertEquals("The task method should not pay attention to the request context in this instance",
				Long.valueOf(4), section.getNode().getDocId());
		Assert.assertEquals("The task method should not pay attention to the request context in this instance",
				5, section.getJourneyDocuments().size());
	}

	@Test
	public void testInvalidShortHeading() {
		// This test uses the section's document Id
		JourneyTask.CurrentJourneySectionModel model = new JourneyTask.CurrentJourneySectionModel();
		model.setDocId(4L);
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		JourneySection section = journeyTask.getCurrentJourneySection(null, model);

		Assert.assertEquals(Long.valueOf(4), section.getNode().getDocId());
		Assert.assertEquals(5, section.getJourneyDocuments().size());
	}

	@Test
	public void testCurrentSectionUsingDocumentDocId() {
		// This test uses a document id of one of the section's journey documents
		JourneyTask.CurrentJourneySectionModel model = new JourneyTask.CurrentJourneySectionModel();
		model.setDocId(22L);
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		JourneySection section = journeyTask.getCurrentJourneySection(null, model);

		Assert.assertEquals(Long.valueOf(2), section.getNode().getDocId());
		Assert.assertEquals(6, section.getJourneyDocuments().size());
	}

	@Test
	public void testGetCurrentSectionInvalidDocId() {
		JourneyRoot root = getJourneyRootWithDocId(1234L);
		JourneyTask.CurrentJourneySectionModel model = new JourneyTask.CurrentJourneySectionModel();
		model.setDocId(999L);
		model.setJourneyRoot(root);
		JourneySection section = journeyTask.getCurrentJourneySection(null, model);

		Assert.assertNull(section);
	}

	@Test
	public void testGetCurrentSectionNullParameter() {
		JourneyTask.CurrentJourneySectionModel modelA = new JourneyTask.CurrentJourneySectionModel();
		modelA.setDocId(12L);
		JourneySection nullRootSection = journeyTask.getCurrentJourneySection(null, modelA);
		Assert.assertNull(nullRootSection);

		JourneyTask.CurrentJourneySectionModel modelB = new JourneyTask.CurrentJourneySectionModel();
		modelB.setJourneyRoot(getJourneyRootWithDocId(1234L));
		JourneySection nullDocIdSection = journeyTask.getCurrentJourneySection(null, modelB);
		Assert.assertNull(nullDocIdSection);
	}

	@Test
	public void testIsJourneyRoot() {
		JourneyTask.IsJourneyRootDocumentModel model = new JourneyTask.IsJourneyRootDocumentModel();
		model.setDocId(1234L);
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		Assert.assertEquals(true, journeyTask.isJourneyRoot(null, model));
	}

	@Test
	public void testIsJourneyRootWithRequestContext() {
		Assert.assertEquals(true, journeyTask.isJourneyRoot(buildRequestContextWithDocId(1234L), new JourneyTask.IsJourneyRootDocumentModel()));
	}

	@Test
	public void testIsNotJourneyRootWithRequestContext() {
		Assert.assertEquals(false, journeyTask.isJourneyRoot(buildRequestContextWithDocId(5678L), new JourneyTask.IsJourneyRootDocumentModel()));
	}

	@Test
	public void testIsJourneyRootWithBuilderModelAndRequestParam() {
		JourneyTask.IsJourneyRootDocumentModel model = new JourneyTask.IsJourneyRootDocumentModel();
		model.setDocId(1234L);
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));

		Assert.assertEquals("The task method should not pay attention to the request context in this instance",
				true, journeyTask.isJourneyRoot(buildRequestContextWithDocId(5678L), model));
	}

	@Test
	public void testIsJourneyRootNullParameter() {
		JourneyRoot root = getJourneyRootWithDocId(1234L);
		JourneyTask.IsJourneyRootDocumentModel modelA = new JourneyTask.IsJourneyRootDocumentModel();
		modelA.setJourneyRoot(root);
		Assert.assertEquals(false, journeyTask.isJourneyRoot(null, modelA));

		JourneyTask.IsJourneyRootDocumentModel modelB = new JourneyTask.IsJourneyRootDocumentModel();
		modelB.setDocId(1234L);
		Assert.assertEquals(false, journeyTask.isJourneyRoot(null, modelB));

		JourneyTask.IsJourneyRootDocumentModel modelC = new JourneyTask.IsJourneyRootDocumentModel();
		modelC.setJourneyRoot(root);
		Assert.assertEquals("The task method should not pay attention to the request context in this instance",
				false, journeyTask.isJourneyRoot(buildRequestContextWithDocId(1234L), modelC));

		JourneyTask.IsJourneyRootDocumentModel modelD = new JourneyTask.IsJourneyRootDocumentModel();
		modelD.setDocId(1234L);
		Assert.assertEquals("The task method should not pay attention to the request context in this instance",
				false, journeyTask.isJourneyRoot(buildRequestContextWithDocId(1234L), modelD));
	}

	@Test
	public void testIsJourneyRootInvalidDocId() {
		JourneyTask.IsJourneyRootDocumentModel model = new JourneyTask.IsJourneyRootDocumentModel();
		model.setDocId(123424L);
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		Assert.assertEquals(false, journeyTask.isJourneyRoot(null, model));
	}

	@Test
	public void testGetAllDocuments() {
		List<AbstractJourneyNode> documents = journeyTask.getAllJourneyDocuments(
				new JourneyTask.GetAllJourneyDocumentsModel.Builder().setJourneyRoot(getJourneyRootWithDocId(1234L)).build());

		Assert.assertEquals(20, documents.size());
		Assert.assertEquals(Long.valueOf(11), documents.get(0).getDocument().getDocumentId());
		Assert.assertEquals(Long.valueOf(31), documents.get(10).getDocument().getDocumentId());
		Assert.assertEquals(Long.valueOf(52), documents.get(19).getDocument().getDocumentId());
	}

	@Test
	public void testInvalidGetAllDocuments() {
		List<AbstractJourneyNode> documents = journeyTask.getAllJourneyDocuments(new JourneyTask.GetAllJourneyDocumentsModel.Builder().build());

		Assert.assertEquals(0, documents.size());
	}

	@Test
	public void testGetAllDocumentsIncludeRoot() {
		List<AbstractJourneyNode> documents = journeyTask.getAllJourneyDocuments(
				new JourneyTask.GetAllJourneyDocumentsModel.Builder().setJourneyRoot(getJourneyRootWithDocId(1234L)).setIncludeRoot(true).build());

		Assert.assertEquals(21, documents.size());
		Assert.assertEquals(Long.valueOf(1234), documents.get(0).getDocument().getDocumentId());
	}

	@Test
	public void testIsJourneyDocumentWithRoot() {
		JourneyTask.IsJourneyDocumentModel model = new JourneyTask.IsJourneyDocumentModel();
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));

		Assert.assertTrue(journeyTask.isJourneyDocument(null, model));
	}

	@Test
	public void testIsJourneyDocumentWithRequestContext() {
		Assert.assertTrue(journeyTask.isJourneyDocument(buildRequestContextWithDocId(1234L), new JourneyTask.IsJourneyDocumentModel()));
	}

	@Test
	public void testIsJourneyDocumentWithRootAndRequestContext() {
		JourneyTask.IsJourneyDocumentModel model = new JourneyTask.IsJourneyDocumentModel();
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));

		Assert.assertTrue("The task method should not pay attention to the request context in this instance",
				journeyTask.isJourneyDocument(buildRequestContextWithDocId(null), model));
	}

	@Test
	public void testGetDocumentsAfterToBeforeInclusiveWithRootAndDocId() {
		JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel model = new JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel();
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		model.setDocId(1234L);

		List<AbstractJourneyNode> documents = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(21, documents.size());
		Assert.assertEquals(Long.valueOf(11), documents.get(0).getDocument().getDocumentId());
		Assert.assertEquals(Long.valueOf(1234), documents.get(20).getDocument().getDocumentId());
	}

	@Test
	public void testGetDocumentsAfterToBeforeInclusiveWithRootAndDocIdAbsentDocument() {
		JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel model = new JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel();
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		model.setDocId(2468L);

		List<AbstractJourneyNode> documents = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(0, documents.size());
	}

	@Test
	public void testGetDocumentsAfterToBeforeInclusiveWithRootAndDocIdAndIgnoreAbsence() {
		JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel model = new JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel();
		model.setJourneyRoot(getJourneyRootWithDocId(1234L));
		model.setDocId(2468L);

		model.setIgnoreDocumentAbsence(true);
		List<AbstractJourneyNode> documentsA = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(21, documentsA.size());
		Assert.assertEquals(Long.valueOf(1234), documentsA.get(0).getDocument().getDocumentId());

		model.setIgnoreDocumentAbsence(false);
		List<AbstractJourneyNode> documentsB = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(0, documentsB.size());


		model.setDocId(1234L);
		model.setJourneyRoot(null);
		List<AbstractJourneyNode> documentsC = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(0, documentsC.size());

		model.setJourneyRoot(new JourneyRoot(new TaxeneNodeEx()));
		List<AbstractJourneyNode> documentsD = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(0, documentsD.size());
	}

	@Test
	public void testGetDocumentsAfterToBeforeInclusiveWithDocumentListAndDocId() {
		JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel model = new JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel();
		List<AbstractJourneyNode> documentsList = journeyTask.getAllJourneyDocuments(
				new JourneyTask.GetAllJourneyDocumentsModel.Builder().setJourneyRoot(getJourneyRootWithDocId(1234L)).build());
		model.setDocumentList(documentsList);
		model.setDocId(11L);

		List<AbstractJourneyNode> documentsA = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(20, documentsA.size());
		Assert.assertEquals(Long.valueOf(12), documentsA.get(0).getDocument().getDocumentId());
		Assert.assertEquals(Long.valueOf(11), documentsA.get(19).getDocument().getDocumentId());

		model.setDocId(9876L);
		List<AbstractJourneyNode> documentsB = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(0, documentsB.size());
	}

	@Test
	public void testGetDocumentsAfterToBeforeInclusiveWithDocumentListAndDocIdAndIgnoreAbsence() {
		JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel model = new JourneyTask.JourneyDocumentsAfterToBeforeInclusiveModel();
		List<AbstractJourneyNode> documentsList = journeyTask.getAllJourneyDocuments(
				new JourneyTask.GetAllJourneyDocumentsModel.Builder().setJourneyRoot(getJourneyRootWithDocId(1234L)).build());
		model.setDocumentList(documentsList);
		model.setDocId(9876L);

		model.setIgnoreDocumentAbsence(false);
		List<AbstractJourneyNode> documentsA = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(0, documentsA.size());

		model.setIgnoreDocumentAbsence(true);
		List<AbstractJourneyNode> documentsB = journeyTask.getDocumentsAfterToBeforeInclusive(model);
		Assert.assertEquals(20, documentsB.size());
	}

	@Test
	public void testJourneySectionSizeMapWithDocId() {
		JourneyTask.JourneySectionSizeMapModel model = new JourneyTask.JourneySectionSizeMapModel();
		model.setDocId(1234L);

		Map<String, List<JourneySection>> sections = journeyTask.journeySectionSizeMap(null, model);
		Assert.assertEquals(0, sections.get("singleDocumentSections").size());
		Assert.assertEquals(5, sections.get("multipleDocumentSections").size());
	}

	@Test
	public void testJourneySectionSizeMapWithRequestContext() {
		Map<String, List<JourneySection>> sections = journeyTask.journeySectionSizeMap(buildRequestContextWithDocId(1234L), new JourneyTask.JourneySectionSizeMapModel());
		Assert.assertEquals(0, sections.get("singleDocumentSections").size());
		Assert.assertEquals(5, sections.get("multipleDocumentSections").size());
	}

	@Test
	public void testJourneySectionSizeMapWithRequestContextAndDocId() {
		JourneyTask.JourneySectionSizeMapModel model = new JourneyTask.JourneySectionSizeMapModel();
		model.setDocId(1234L);

		Map<String, List<JourneySection>> sections = journeyTask.journeySectionSizeMap(buildRequestContextWithDocId(null), model);
		Assert.assertEquals(0, sections.get("singleDocumentSections").size());
		Assert.assertEquals(5, sections.get("multipleDocumentSections").size());
	}

	private JourneyDocument createJourneyDocument(long docId, String shortHeading) {
		return new JourneyDocument(createTaxeneNode(docId, TemplateTypeEx.FLEXIBLEARTICLE, shortHeading));
	}

	private JourneySection createJourneySection(long docId, String shortHeading, JourneyDocument... documents) {
		return new JourneySection(createTaxeneNode(docId, TemplateTypeEx.PROGRAMMEDSUMMARY, shortHeading),
				Lists.newArrayList(documents));
	}

	@SuppressWarnings("incomplete-switch")
	private TaxeneNodeEx createTaxeneNode(long docId, TemplateTypeEx templateType, String shortHeading) {
		BaseDocumentEx document;

		switch (templateType) {
		case FLEXIBLEARTICLE:
			document = new FlexibleArticleDocumentEx();
			break;
		case PROGRAMMEDSUMMARY:
			document = new ProgrammedSummaryDocumentEx();
			break;
		default:
			document = new FlexibleArticleDocumentEx();
			break;
		}
		document.setDocumentId(docId);

		TaxeneNodeEx node = new TaxeneNodeEx();
		Map<String, String> nodeAttributes = new HashMap<>();
		nodeAttributes.put(NodeAttribute.SHORT_HEADING.key(), shortHeading);
		node.setDocId(docId);
		node.setDocument(document);
		node.setNodeAttributes(nodeAttributes);
		return node;
	}

	private JourneyRoot getJourneyRootWithDocId(Long docId) {
		return journeyTask.getJourneyStructure(null, new JourneyTask.JourneyStructureModel.Builder().setDocId(docId).build());
	}

	private static RequestContext buildRequestContextWithDocId(Long docId) {
		RequestContext.Builder requestContextBuilder = new RequestContextImpl.Builder();
		return requestContextBuilder.setUrlData(new VerticalUrlData(VerticalUrlData.builder("people","people.com").docId(docId))).build();
	}
}
