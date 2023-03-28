package com.about.mantle.htmlslicing;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurableHtmlSlicerTest {

	private HtmlSlicerConfig htmlSlicerConfig = new HtmlSlicerConfig("chars:500;tags:p,ul,ol;");
	private HtmlSlicer htmlSlicer = new ConfigurableHtmlSlicer(new ObjectMapper());

	@Test
	public void testXhtmlSelfClosingTags() {
		String sentence = "<p>The quick brown fox<br/> jumped over the lazy dog</p>";
		HtmlSlice htmlslice = new HtmlSlice(sentence, 44, 9, null);
		Assert.assertTrue(htmlSlicer.applyFormatting(sentence, htmlSlicerConfig).contains(htmlslice));
	}

	@Test
	public void testHtmlSelfClosingTags() {
		String sentence = "<p>The quick brown fox<br> jumped over the lazy dog</p>";
		HtmlSlice htmlslice = new HtmlSlice(sentence, 44, 9, null);
		Assert.assertTrue(htmlSlicer.applyFormatting(sentence, htmlSlicerConfig).contains(htmlslice));
	}

	@Test
	public void testDefaultConfigUsesTagClosingSlicer() {
		String sentence = "<p>The quick brown fox<br> jumped over the lazy dog. Then it ate all my chickens.</p>";
		List<HtmlSlice> result = htmlSlicer.applyFormatting(sentence, new HtmlSlicerConfig("chars:30,30;tags:p,ul,ol;"));
		HtmlSlice slice1 = new HtmlSlice("<p>The quick brown fox<br> jumped over the lazy dog.</p>", 45, 9, null);
		HtmlSlice slice2 = new HtmlSlice("<p class=\"html-slice\"> Then it ate all my chickens.</p>", 29, 6, null);
		Assert.assertEquals("default config uses tag-closing slicer", ImmutableList.of(slice1, slice2), result);
	}

	@Test
	public void testNonclosingConfigSlicer() {
		String sentence = "<p>The quick brown fox<br> jumped over the lazy dog. Then it ate all my chickens.</p>";
		List<HtmlSlice> result = htmlSlicer.applyFormatting(sentence, new HtmlSlicerConfig("nonclosing;chars:30,30;tags:p,ul,ol;"));
		HtmlSlice slice1 = new HtmlSlice("<p>The quick brown fox<br> jumped over the lazy dog. ", 45, 9, null);
		HtmlSlice slice2 = new HtmlSlice("Then it ate all my chickens.</p>", 28, 5, null);
		Assert.assertEquals("nonclosing config uses nonclosing slicer", ImmutableList.of(slice1, slice2), result);
	}

	@Test
	public void testNonclosingConfigSlicerHandlingNbspAtEnd() {
		String sentence = "<p>North of the Arctic Circle, the sun barely sets in summer, and night seems unending in winter. This phenomenon creates what is known as Midnight Sun and Polar Nights. During the time of Midnight Sun, you can often see the sun at midnight and, most definitely, will experience daytime light all night long. So, draw your blackout curtains should you need some shut-eye. During Polar Nights, conversely, nighttime skies exist even in the daytime, making indoor lighting a must and flashlights and headlamps a necessity for getting around outside.&nbsp;</p>";

		/**
		 * copy and pasted logic from {@link com.about.mantle.model.tasks.StructuredContentDocumentProcessor} buildUniformHtmlSlicerConfig().
		 */
		int[] chars = new int[(557 / 500) + 1];
		Arrays.fill(chars, 500);
		HtmlSlicerConfig config = new HtmlSlicerConfig(chars, ImmutableSet.of("p", "ul", "ol"), null, true);

		List<HtmlSlice> result = htmlSlicer.applyFormatting(sentence, config);
		HtmlSlice slice1 = new HtmlSlice("<p>North of the Arctic Circle, the sun barely sets in summer, and night seems unending in winter. This phenomenon creates what is known as Midnight Sun and Polar Nights. During the time of Midnight Sun, you can often see the sun at midnight and, most definitely, will experience daytime light all night long. So, draw your blackout curtains should you need some shut-eye. During Polar Nights, conversely, nighttime skies exist even in the daytime, making indoor lighting a must and flashlights and headlamps a necessity for getting around outside.&nbsp;", 544, 89, null);
		HtmlSlice slice2 = new HtmlSlice("</p>", 0, 0, null);
		Assert.assertEquals("nonclosing config uses nonclosing slicer and does not split &nbsp; character", ImmutableList.of(slice1, slice2), result);
	}

	@Test
	public void testNonclosingConfigSlicerHandlingNbspAtEndExample2() {

		String sentence = "<p>Sweden, overall, boasts very considerable snowfall amounts, especially in the northernmost regions where snow covers the ground in a thick blanket for up to six months. Stockholm's more southerly location makes it ideal for those wanting to avoid threatening winter weather. However, strong winter storms can still affect the city, shutting down public transportation and delaying plane flights. Still, if you want to see snow in Stockholm (where storms move in and out quickly and snow sticks around for only a few days) the chance is good in winter.&nbsp;</p>";

		/**
		 * copy and pasted logic from {@link com.about.mantle.model.tasks.StructuredContentDocumentProcessor} buildUniformHtmlSlicerConfig().
 		 */
		int[] chars = new int[(557 / 500) + 1];
		Arrays.fill(chars, 500);
		HtmlSlicerConfig config = new HtmlSlicerConfig(chars, ImmutableSet.of("p", "ul", "ol"), null, true);

		List<HtmlSlice> result = htmlSlicer.applyFormatting(sentence, config);
		HtmlSlice slice1 = new HtmlSlice("<p>Sweden, overall, boasts very considerable snowfall amounts, especially in the northernmost regions where snow covers the ground in a thick blanket for up to six months. Stockholm's more southerly location makes it ideal for those wanting to avoid threatening winter weather. However, strong winter storms can still affect the city, shutting down public transportation and delaying plane flights. Still, if you want to see snow in Stockholm (where storms move in and out quickly and snow sticks around for only a few days) the chance is good in winter.&nbsp;", 551, 89, null);
		HtmlSlice slice2 = new HtmlSlice("</p>", 0, 0, null);
		Assert.assertEquals("nonclosing config uses nonclosing slicer and does not split &nbsp; character", ImmutableList.of(slice1, slice2), result);
	}

	@Test
	public void testNonclosingConfigSlicerHandlingNbspInMiddle() {

		String sentence = "<p>If you haven't tried brunch at <a href=\"http://beatriceandwoodsley.com\" data-component=\"link\" data-source=\"inlineLink\" data-type=\"externalLink\" data-ordinal=\"1\" rel=\"nofollow\">Beatrice and Woodsley</a> on <a href=\"https://www.denver.org/about-denver/neighborhood-guides/south-broadway/\" data-component=\"link\" data-source=\"inlineLink\" data-type=\"externalLink\" data-ordinal=\"2\">south Broadway</a>, make it a top priority. And plan ahead. Because this intimate, unique restaurant only has a handful of tables that you can only get in by reservation. If you're lucky, you can score a spot at the bar counter without reservation. B&amp;W, as it's known, could make a killing cramming in extra tables and packing the bar counter, but instead it preserves a slow-paced, leisurely atmosphere with lengthy reservation slots and plenty of time to unwind and catch up with your friends or date.&nbsp;Two other features set B&amp;W apart from the other brunch options in the area: its completely unique atmosphere and its menu—equally as innovative.</p>";

		/**
		 * copy and pasted logic from {@link com.about.mantle.model.tasks.StructuredContentDocumentProcessor} buildUniformHtmlSlicerConfig().
		 */
		int[] chars = new int[(557 / 500) + 1];
		Arrays.fill(chars, 500);
		HtmlSlicerConfig config = new HtmlSlicerConfig(chars, ImmutableSet.of("p", "ul", "ol"), null, true);

		List<HtmlSlice> result = htmlSlicer.applyFormatting(sentence, config);
		HtmlSlice slice1 = new HtmlSlice("<p>If you haven't tried brunch at <a href=\"http://beatriceandwoodsley.com\" data-component=\"link\" data-source=\"inlineLink\" data-type=\"externalLink\" data-ordinal=\"1\" rel=\"nofollow\">Beatrice and Woodsley</a> on <a href=\"https://www.denver.org/about-denver/neighborhood-guides/south-broadway/\" data-component=\"link\" data-source=\"inlineLink\" data-type=\"externalLink\" data-ordinal=\"2\">south Broadway</a>, make it a top priority. And plan ahead. Because this intimate, unique restaurant only has a handful of tables that you can only get in by reservation. If you're lucky, you can score a spot at the bar counter without reservation. B&amp;W, as it's known, could make a killing cramming in extra tables and packing the bar counter, but instead it preserves a slow-paced, leisurely atmosphere with lengthy reservation slots and plenty of time to unwind and catch up with your friends or date.&nbsp;", 555, 97, null);
		HtmlSlice slice2 = new HtmlSlice("Two other features set B&amp;W apart from the other brunch options in the area: its completely unique atmosphere and its menu—equally as innovative.</p>", 144, 22, null);
		Assert.assertEquals("nonclosing config uses nonclosing slicer and does not split &nbsp; character", ImmutableList.of(slice1, slice2), result);
	}

	@Test
	public void testNonclosingConfigSlicerHandlingDoubleNbsp() {

		String sentence = "<p>The show addressed many difficult topics. In a time when the U.S. government <a href=\\\"https://www.nbcnews.com/feature/nbc-out/lgbtq-history-month-early-days-america-s-aids-crisis-n919701\\\" data-component=\\\"link\\\" data-source=\\\"inlineLink\\\" data-type=\\\"externalLink\\\" data-ordinal=\\\"1\\\" rel=\\\"noopener noreferrer\\\">was ignoring the HIV/AIDS pandemic</a>, <em>Golden Girls</em> addressed how this disease was causing social fear and anxiety (season five, episode 19). Was the show perfect? Not at all. As Steven W. Thrasher addressed in his recent <a href=\\\"https://www.vulture.com/2020/07/the-real-mud-on-golden-girls.html\\\" data-component=\\\"link\\\" data-source=\\\"inlineLink\\\" data-type=\\\"externalLink\\\" data-ordinal=\\\"2\\\" rel=\\\"noopener noreferrer\\\">Vulture article</a>, the show has its moments that would make 2020 audiences squirm in their seats. That being said, the joke at the heart of the controversy is not problematic. To truly understand it, it must be looked at with context and, more importantly, intent.&nbsp;&nbsp;</p>";

		/**
		 * copy and pasted logic from {@link com.about.mantle.model.tasks.StructuredContentDocumentProcessor} buildUniformHtmlSlicerConfig().
		 */
		int[] chars = new int[(557 / 500) + 1];
		Arrays.fill(chars, 500);
		HtmlSlicerConfig config = new HtmlSlicerConfig(chars, ImmutableSet.of("p", "ul", "ol"), null, true);

		List<HtmlSlice> result = htmlSlicer.applyFormatting(sentence, config);
		HtmlSlice slice1 = new HtmlSlice("<p>The show addressed many difficult topics. In a time when the U.S. government <a href=\\\"https://www.nbcnews.com/feature/nbc-out/lgbtq-history-month-early-days-america-s-aids-crisis-n919701\\\" data-component=\\\"link\\\" data-source=\\\"inlineLink\\\" data-type=\\\"externalLink\\\" data-ordinal=\\\"1\\\" rel=\\\"noopener noreferrer\\\">was ignoring the HIV/AIDS pandemic</a>, <em>Golden Girls</em> addressed how this disease was causing social fear and anxiety (season five, episode 19). Was the show perfect? Not at all. As Steven W. Thrasher addressed in his recent <a href=\\\"https://www.vulture.com/2020/07/the-real-mud-on-golden-girls.html\\\" data-component=\\\"link\\\" data-source=\\\"inlineLink\\\" data-type=\\\"externalLink\\\" data-ordinal=\\\"2\\\" rel=\\\"noopener noreferrer\\\">Vulture article</a>, the show has its moments that would make 2020 audiences squirm in their seats. That being said, the joke at the heart of the controversy is not problematic. To truly understand it, it must be looked at with context and, more importantly, intent.&nbsp;", 558, 96, null);
		HtmlSlice slice2 = new HtmlSlice("&nbsp;</p>", 1, 0, null);
		Assert.assertEquals("nonclosing config uses nonclosing slicer and does not split &nbsp; character", ImmutableList.of(slice1, slice2), result);
	}


}
