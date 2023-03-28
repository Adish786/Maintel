package com.about.mantle.model.transformers;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.LinkboxEx;
import com.about.mantle.model.extended.docv2.Linkboxed;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.list.Link;

@Tasks
public class BulletedListTransformer {

	public static class BulletedList {

		public final static BulletedList EMPTY = new BulletedList();
		private List<Link> links;

		public BulletedList(List<Link> links) {
			this.links = links;
		}

		public List<Link> getLinks() {
			return links;
		}

		public void setLinks(List<Link> links) {
			this.links = links;
		}

		private BulletedList() {
			links = new ArrayList<>();
		}

	}

	@Task(name = "articleServiceLinkTransformer")
	public BulletedList transformArticleService(
			@TaskParameter(name = "articles") SliceableListEx<BaseDocumentEx> articles) {

		if (articles == null || articles.isEmpty()) return null;

		return new BulletedList(articles.getList().stream()
				.filter(doc -> doc != null && isNotEmpty(doc.getUrl()) && isNotEmpty(doc.getBestTitle()))
				.map((BaseDocumentEx article) -> new Link(article.getUrl(), article.getBestTitle(), false))
				.collect(Collectors.toList()));

	}

	@Task(name = "linkboxesLinkTransformer")
	public BulletedList transformLinkBoxes(@TaskParameter(name = "linkboxes") SliceableListEx<LinkboxEx> linkboxes) {
		if (linkboxes.isEmpty()) return null;

		List<Link> results = new ArrayList<>();
		for (LinkboxEx linkBox : linkboxes.getList()) {
			if (!linkBox.getLinks().isEmpty()) {
				linkBox.getLinks().getList().stream()
						.filter((link) -> StringUtils.isNotEmpty(link.getText())
								&& StringUtils.isNotEmpty(link.getUri()))
						.forEach((link) -> results.add(new Link(link.getUri(), link.getText(), link.getExternal())));
			}
		}
		if (results.size() == 0) return null;
		return new BulletedList(results);
	}

	@Task(name = "documentLinksTransformer")
	public BulletedList transformDocumentLinks(@TaskParameter(name = "document") BaseDocumentEx document) {
		if (!(document instanceof Linkboxed)) return null;

		SliceableListEx<LinkboxEx> linkboxes = ((Linkboxed) document).getLinkboxes();
		if (linkboxes.isEmpty()) return null;

		List<Link> results = new ArrayList<>();
		for (LinkboxEx linkBox : linkboxes.getList()) {
			if (!linkBox.getLinks().isEmpty()) {
				linkBox.getLinks().getList().stream()
						.filter((link) -> StringUtils.isNotEmpty(link.getText())
								&& StringUtils.isNotEmpty(link.getUri()))
						.forEach((link) -> results.add(new Link(link.getUri(), link.getText(), link.getExternal())));
			}
		}
		if (results.size() == 0) return null;
		return new BulletedList(results);
	}


	@Task(name = "truncateBulletedList")
	public BulletedList truncateList(@TaskParameter(name = "list") BulletedList list,
			@TaskParameter(name = "start") Integer start, @TaskParameter(name = "end") Integer end) {
		if (list == null || CollectionUtils.isEmpty(list.getLinks())) return null;
		if (start < 0) start = 0;
		if (end < 0 || end > list.getLinks().size()) end = list.getLinks().size();

		if (start > end) return null;
		return new BulletedList(list.getLinks().subList(start, end));
	}

	@Task(name = "sortBulletedList")
	public BulletedList sortList(@TaskParameter(name = "list") BulletedList list) {
		List<Link> bl = new ArrayList<Link>(list.getLinks());
		Collections.sort(bl, new Comparator<Link>() {
			@Override
			public int compare(Link link1, Link link2) {
				return link1.getTitle().toLowerCase().compareTo(link2.getTitle().toLowerCase());
			}
		});
		return new BulletedList(bl);
	}

	@Task(name = "aggregateLinks")
	public BulletedList aggregateAndDedupe(@TaskParameter(name = "input1") BulletedList input1,
			@TaskParameter(name = "input2") BulletedList input2) {
		return aggregateAndDedupeList(input1, input2);
	}

	@Task(name = "aggregateLinks")
	public BulletedList aggregateAndDedupe(@TaskParameter(name = "input1") BulletedList input1,
			@TaskParameter(name = "input2") BulletedList input2, @TaskParameter(name = "input3") BulletedList input3) {
		return aggregateAndDedupeList(input1, input2, input3);
	}

	@Task(name = "aggregateLinks")
	public BulletedList aggregateAndDedupe(@TaskParameter(name = "input1") BulletedList input1,
			@TaskParameter(name = "input2") BulletedList input2, @TaskParameter(name = "input3") BulletedList input3,
			@TaskParameter(name = "input4") BulletedList input4) {
		return aggregateAndDedupeList(input1, input2, input3, input4);
	}

	private BulletedList aggregateAndDedupeList(BulletedList... inputs) {

		List<Link> result = new ArrayList<>();
		Set<Link> articles = new HashSet<>();

		for (BulletedList currList : inputs) {
			if (currList != null) {
				for (Link currLink : currList.getLinks()) {
					if (!articles.contains(currLink)) {
						result.add(currLink);
						articles.add(currLink);
					}
				}
			}
		}

		return new BulletedList(result);
	}
}
