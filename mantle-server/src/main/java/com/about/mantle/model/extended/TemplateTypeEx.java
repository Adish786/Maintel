package com.about.mantle.model.extended;

public enum TemplateTypeEx {
	// documents not intended to be rendered as templates use templateId -1
	REDIRECT(-1, ""),
	PROGRAMMEDSUMMARY(-1, ""),
	LEGACY(0, ""),
	LANDINGPAGE(1, ""),
	LIST(3, "List"),
	CATEGORY(14, "Category"),
	BIO(17, "Bio"),
	RECIPE(28, "Recipe"),
	STEPBYSTEP(29, "List"),
	IMAGEGALLERY(33, "List"),
	JWPLAYERVIDEO(137, "JWPlayer"),
	USERPATH(39, ""),
	FLEXIBLEARTICLE(65, "Article"),
	TOPIC(69, "Spotlight"),
	TOPICMAP(70, "Topic Map"),
	QUIZ(80, "Quiz"),
	TAXONOMY(90, "Category"),
	BRIGHTCOVEVIDEO(95, "BrightcoveVideo"),
	// Structured content start at 110 and are incremented by 5 as they're added.
	STRUCTUREDCONTENT(110, "Article"),
	LISTSC(115, "List"),
	RECIPESC(120, "Recipe"),
	TERMSC(125, "Term"),
	REVIEWSC(130, "Review"),
	HOWTO(135, "How To"), //HOWTO is breaking with convention, in Selene it is HOWTO instead of HOWTOSC.
	TAXONOMYSC(140, "Taxonomy"),
	ENTITYREFERENCE(145, "Reference"),
	AMAZONOSP(150, "AmazonOSP");

	private final int templateId;
	private final String displayName;

	private TemplateTypeEx(int templateId, String displayName) {
		this.templateId = templateId;
		this.displayName = displayName;
	}

	public int getTemplateId() {
		return templateId;
	}

	public String getDisplayName() {
		return displayName;
	}
}
