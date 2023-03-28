package com.about.mantle.model.transformers;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.RSS2.Channel;
import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.RSS2.Item.Enclosure;
import com.about.mantle.render.image.MantleImageRenderUtils;
import com.squareup.pollexor.ThumborUrlBuilder.FitInStyle;

@Tasks
public class RSS2Transformer {

	private MantleImageRenderUtils imageUtils;

	public RSS2Transformer(MantleImageRenderUtils imageUtils) {
		this.imageUtils = imageUtils;
	}

	@Task(name = "propertyRSS2Transformer")
	public Channel transformProperties(@TaskParameter(name = "title") String title,
			@TaskParameter(name = "link") String link, @TaskParameter(name = "description") String description,
			@TaskParameter(name = "lastBuildDate") DateTime lastBuildDate,
			@TaskParameter(name = "items") List<Map<String, String>> items) {
		return new Channel(title, link, description, lastBuildDate,
				items.stream().map(new Function<Map<String, String>, Item>() {
					@Override
					public Item apply(Map<String, String> m) {
						return new Item(m.get("title"), m.get("link"), m.get("description"), m.get("author"), new DateTime(), null);
					}
				}).collect(Collectors.toList()));
	}

	@Task(name = "curatedRSS2Transformer")
	public Channel transformCuratedList(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "curatedList") DocumentCuratedListEx curatedList,
			@TaskParameter(name = "maxItems") Integer maxItems) {
		return new Channel(curatedList.getDisplayName(), curatedList.getName(),
				curatedList.getDescription(), curatedList.getActiveDate(),
				curatedList.getData().stream().map(new Function<CuratedDocumentEx, Item>() {
					@Override
					public Item apply(CuratedDocumentEx curatedDocumentEx) {
						Enclosure enclosure = null;
						
						ImageEx bestIntroImage = curatedDocumentEx.getProgrammingImage();
						
						if(!(bestIntroImage.equals(ImageEx.EMPTY))) {
							bestIntroImage = curatedDocumentEx.getImageForUsage(UsageFlag.PRIMARY);
						}
						
						if (bestIntroImage != null && isNotBlank(bestIntroImage.getObjectId())) {
							String imageUrl = imageUtils.getImage(bestIntroImage.getObjectId(),
									bestIntroImage.getWidth(400), bestIntroImage.getHeight(300),
									FitInStyle.NORMAL.toString(), Boolean.TRUE, requestContext, null, null);
							String mimeType = URLConnection.guessContentTypeFromName(imageUrl);
							if (mimeType == null && isNotBlank(bestIntroImage.getUrl()))
								mimeType = URLConnection.guessContentTypeFromName(bestIntroImage.getUrl());
							if (mimeType != null) enclosure = new Enclosure(imageUrl, "0", mimeType);
						}

						return new Item(curatedDocumentEx.getBestTitle(), curatedDocumentEx.getUrl(), curatedDocumentEx.getDescription(), null,
								defaultIfNull(curatedDocumentEx.getAddedDate(), curatedDocumentEx.getDates().getUpdated()),
								enclosure);
					}
				}).sorted(Item.ItemComparator).limit(maxItems).collect(Collectors.toList()));
	}

}
