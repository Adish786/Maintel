package com.about.mantle.model.transformers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.GptAd;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;

@Tasks
public class TopicPageTransformer {

	@Task(name = "topicGroupsTransformed")
	public List<Group> transformTopicGroupTiles(
			@TaskParameter(name = "topicGroups") DocumentCuratedListOfListEx groupList, @TaskParameter(
					name = "columns") int columns, @TaskParameter(name = "adSlot") int adSlot, @TaskParameter(
					name = "gptAd") GptAd ad) {

		List<Group> transformedGroups = new ArrayList<Group>(groupList.getData().size());
		
		for (DocumentCuratedListEx group : groupList.getData()) {
			List<List<Item>> columnLists = constructColumnList(columns);

			int currentSlot = 1;
			for (Iterator<CuratedDocumentEx> iterator = group.getData().iterator(); iterator.hasNext();) {

				int columnIndex = (currentSlot % columns == 0 ? columns : currentSlot % columns) - 1;

				Item item = null;
				// Currently ad can only be placed in the first group
				if (currentSlot == adSlot && groupList.getData().indexOf(group) == 0) {
					item = new Item(Type.AD, ad);
				} else {
					item = new Item(Type.DOCUMENT, iterator.next());
				}
				columnLists.get(columnIndex).add(item);
				currentSlot++;
			}

			//After all documents are loaded, if next slot should be an ad, add it.
			if (currentSlot == adSlot) {
				Item item = new Item(Type.AD, ad);
				int columnIndex = (currentSlot % columns == 0 ? columns : currentSlot % columns) - 1;
				columnLists.get(columnIndex).add(item);
			}
			
			transformedGroups.add(new Group(group.getDisplayName(), group.getDescription(), columnLists));
		}
		return transformedGroups;
	}

	private List<List<Item>> constructColumnList(int columnNum) {
		List<List<Item>> columnLists = new ArrayList<List<Item>>(columnNum);
		for (int i = 0; i < columnNum; i++) {
			columnLists.add(new ArrayList<Item>());
		}
		return columnLists;
	}

	public static class Group {
		String groupName;
		String groupDescription;
		List<List<Item>> columns;

		private Group(String groupName, String groupDescription, List<List<Item>> columns) {
			this.groupName = groupName;
			this.groupDescription = groupDescription;
			this.columns = columns;
		}

		public String getGroupName() {
			return groupName;
		}

		public String getGroupDescription() {
			return groupDescription;
		}

		public List<List<Item>> getColumns() {
			return columns;
		}

	}

	public static class Item {
		Type type;
		Object value;

		private Item(Type type, Object value) {
			this.type = type;
			this.value = value;
		}

		public Type getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}
	}

	public static enum Type {
		DOCUMENT,
		AD;
	}
}
