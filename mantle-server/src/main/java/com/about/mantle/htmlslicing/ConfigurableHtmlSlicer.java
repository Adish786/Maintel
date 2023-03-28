package com.about.mantle.htmlslicing;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

/**
 * We need to break the content to slices so Globe can properly display ads, images and other within the content.
 */
public class ConfigurableHtmlSlicer extends AbstractHtmlSlicer {

	private static Logger logger = LoggerFactory.getLogger(ConfigurableHtmlSlicer.class);

	public ConfigurableHtmlSlicer(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	@Override
	public List<HtmlSlice> applyFormatting(String content, HtmlSlicerConfig config) {

		List<HtmlSlice> answer;

		content = stripToNull(content);

		if (content == null) {
			answer = ImmutableList.of();
		} else if (config.isNonclosing()) {
			answer = new NonclosingSlicerHelper(config, content).slice();
		} else {
			answer = new TagClosingSlicerHelper(config, content).slice();
		}

		return answer;
	}

	@Override
	public List<HtmlSlice> applyFormatting(List<HtmlSlice> blocks, HtmlSlicerConfig config) {

		List<HtmlSlice> answer;

		if (isEmpty(blocks)) {
			answer = null;
		} else {

			if (blocks.size() > 1) {
				// Because of the way upstream code is calling selene, we're only _supposed_ to be getting
				// a single content block. It's unclear what's supposed to happen if we get more than one, so
				// for now we'll log it. If we're able to determine that we should never get more than one, we
				// should override `applyFormatting` to accept only a single `HtmlSlice` and the calling code
				// should throw an error if we get multiple blocks

				logger.info("Expected a html slice but got {}. Only applying formatting to the first slice.", blocks.size());
			}

			answer = applyFormatting(blocks.get(0).getContent(), HtmlSlicerConfig.emptyIfNull(config));

		}

		return answer;
	}


	public List<HtmlSlice> removeFormatting(List<HtmlSlice> blocks, HtmlSlicerConfig config) {

		if (isEmpty(blocks)) return null;

		StringBuilder builder = new StringBuilder();
		for (HtmlSlice block : blocks) {
			builder.append(block.getContent());
		}

		blocks = new ArrayList<>(1);
		blocks.add(new HtmlSlice(builder.toString(), null, null, null));

		return blocks;
	}

}
