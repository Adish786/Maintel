package com.about.mantle.model.transformers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.about.mantle.model.extended.GptAd;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.model.transformers.TopicPageTransformer.Group;
import com.about.mantle.model.transformers.TopicPageTransformer.Item;

public class TopicPageTransformerTest {

	TopicPageTransformer transformer;
	DocumentCuratedListOfListEx groups;

	@Before
	public void constructGroupList() {
		groups = new DocumentCuratedListOfListEx();
		DocumentCuratedListEx group1 = new DocumentCuratedListEx();
		group1.setDisplayName("Group 1");
		group1.setDescription("This is group 1");
		List<CuratedDocumentEx> list1 = new ArrayList<CuratedDocumentEx>();
		for (int i = 1; i <= 8; i++) {
			CuratedDocumentEx doc = new CuratedDocumentEx();
			doc.setSocialTitle("Social title of Group 1 - Document " + i);
			doc.setTitle("Group 1 - Document " + i);
			doc.setHeading("Heading of Group 1 - Document " + i);
			list1.add(doc);
		}
		group1.setData(list1);

		DocumentCuratedListEx group2 = new DocumentCuratedListEx();
		group2.setDisplayName("Group 2");
		group2.setDescription("This is group 2");
		group2.setData(new ArrayList<CuratedDocumentEx>());
		List<CuratedDocumentEx> list2 = new ArrayList<CuratedDocumentEx>();
		for (int i = 1; i <= 9; i++) {
			CuratedDocumentEx doc = new CuratedDocumentEx();
			doc.setSocialTitle("Social title of Group 2 - Document " + i);
			doc.setTitle("Group 2 - Document " + i);
			doc.setHeading("Heading of Group 2 - Document " + i);
			list2.add(doc);
		}
		group2.setData(list2);

		List<DocumentCuratedListEx> lists = new ArrayList<DocumentCuratedListEx>();
		lists.add(group1);
		lists.add(group2);
		groups.setData(lists);

		transformer = new TopicPageTransformer();
	}

	@Test
	public void testGroupTransformPC() throws Exception {
		TopicPageTransformer transformer = new TopicPageTransformer();
		int columns = 4;
		int adSlot = 4;

		List<Group> transformedGroups = transformer.transformTopicGroupTiles(groups, columns, adSlot,
				new GptAd.Builder().id("test").sizes("[[640, 325], [980, 425]]").type("billboard").pos("native")
						.priority(20).isDynamic(false).build());

		Assert.assertEquals("Group 1", transformedGroups.get(0).getGroupName());
		Assert.assertEquals("Group 2", transformedGroups.get(1).getGroupName());

		// Verify the document indexes are in the correct place
		verifyDocumentPlacement(transformedGroups, 0);
		verifyDocumentPlacement(transformedGroups, 1);

		// Verify the ad is in the correct slot
		verifyAdPlacement(transformedGroups, columns, adSlot);

	}

	@Test
	public void testGroupTransformTablet() throws Exception {
		TopicPageTransformer transformer = new TopicPageTransformer();
		int columns = 3;
		int adSlot = 3;

		List<Group> transformedGroups = transformer.transformTopicGroupTiles(groups, columns, adSlot,
				new GptAd.Builder().id("test").sizes("[[640, 325], [980, 425]]").type("billboard").pos("native")
						.priority(20).isDynamic(false).build());

		Assert.assertEquals("Group 1", transformedGroups.get(0).getGroupName());
		Assert.assertEquals("Group 2", transformedGroups.get(1).getGroupName());

		// Verify the document indexes are in the correct place
		verifyDocumentPlacement(transformedGroups, 0);
		verifyDocumentPlacement(transformedGroups, 1);

		// Verify the ad is in the correct slot
		verifyAdPlacement(transformedGroups, columns, adSlot);

	}

	@Test
	public void testGroupTransformMobile() throws Exception {

		int columns = 1;
		int adSlot = 5;

		List<Group> transformedGroups = transformer.transformTopicGroupTiles(groups, columns, adSlot,
				new GptAd.Builder().id("test").sizes("[[640, 325], [980, 425]]").type("billboard").pos("native")
						.priority(20).isDynamic(false).build());

		Assert.assertEquals("Group 1", transformedGroups.get(0).getGroupName());
		Assert.assertEquals("Group 2", transformedGroups.get(1).getGroupName());

		// Verify the document indexes are in the correct place
		verifyDocumentPlacement(transformedGroups, 0);
		verifyDocumentPlacement(transformedGroups, 1);

		// Verify the ad is in the correct slot
		verifyAdPlacement(transformedGroups, columns, adSlot);

	}

	private void verifyDocumentPlacement(List<Group> transformedGroups, int groupIndex) {
		int docIndex = 1;
		List<List<Item>> columnList = transformedGroups.get(groupIndex).getColumns();
		for (int i = 0; i < columnList.get(0).size(); i++) {
			for (int j = 0; j < columnList.size(); j++) {

				Item item = columnList.get(j).get(i);
				if (item.getType() != TopicPageTransformer.Type.AD) {
					Assert.assertNotEquals("Social title of Group " + (groupIndex + 1) + " - Document " + docIndex,
							((BaseDocumentEx) columnList.get(j).get(i).getValue()).getBestTitle());
					Assert.assertNotEquals("Group " + (groupIndex + 1) + " - Document " + docIndex,
							((BaseDocumentEx) columnList.get(j).get(i).getValue()).getBestTitle());
					Assert.assertEquals("Heading of Group " + (groupIndex + 1) + " - Document " + docIndex,
							((BaseDocumentEx) columnList.get(j).get(i).getValue()).getBestTitle());
					docIndex++;
					// If all document list is exhausted, verification is complete
					if (docIndex > groups.getData().get(0).getData().size()) return;
				}
			}
		}
	}

	private void verifyAdPlacement(List<Group> transformedGroups, int columns, int adSlot) {
		int columnIndex = adSlot % columns == 0 ? columns - 1 : (adSlot % columns) - 1;
		int rowIndex = (adSlot - 1) / columns;
		Assert.assertEquals(TopicPageTransformer.Type.AD,
				transformedGroups.get(0).getColumns().get(columnIndex).get(rowIndex).getType());
	}
}
