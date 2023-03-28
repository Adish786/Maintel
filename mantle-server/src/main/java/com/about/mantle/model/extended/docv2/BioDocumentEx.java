package com.about.mantle.model.extended.docv2;

public class BioDocumentEx extends BaseDocumentEx {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String experience;
	private String education;
	private String excerpt;
	private String facebookUrl;
	private String honorificPrefix;
	private String honorificSuffix;
	private String jobTitle;
	private String instagramUrl;
	private String linkedinUrl;
	private String location;
	private String narrative;
	private String personalDetail;
	private String pinterestUrl;
	private String shortBio;
	private String twitterUrl;
	private String websiteUrl;
	private String authorRole;

	private SliceableListEx<String> alumniOf = SliceableListEx.emptyList();
	private SliceableListEx<String> knowsAbout = SliceableListEx.emptyList();
	private SliceableListEx<BusinessEx> worksFor = SliceableListEx.emptyList();

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public String getHonorificPrefix() {
		return honorificPrefix;
	}

	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}

	public String getHonorificSuffix() {
		return honorificSuffix;
	}

	public void setHonorificSuffix(String honorificSuffix) {
		this.honorificSuffix = honorificSuffix;
	}

	public String getInstagramUrl() {
		return instagramUrl;
	}

	public void setInstagramUrl(String instagramUrl) {
		this.instagramUrl = instagramUrl;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getLinkedinUrl() {
		return linkedinUrl;
	}

	public void setLinkedinUrl(String linkedinUrl) {
		this.linkedinUrl = linkedinUrl;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getPersonalDetail() {
		return personalDetail;
	}

	public void setPersonalDetail(String personalDetail) {
		this.personalDetail = personalDetail;
	}

	public String getPinterestUrl() {
		return pinterestUrl;
	}

	public void setPinterestUrl(String pinterestUrl) {
		this.pinterestUrl = pinterestUrl;
	}

	public String getShortBio() {
		return shortBio;
	}

	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
	}

	public String getTwitterUrl() {
		return twitterUrl;
	}

	public void setTwitterUrl(String twitterUrl) {
		this.twitterUrl = twitterUrl;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public SliceableListEx<String> getAlumniOf() {
		return alumniOf;
	}

	public void setAlumniOf(SliceableListEx<String> alumniOf) {
		this.alumniOf = alumniOf;
	}

	public SliceableListEx<String> getKnowsAbout() {
		return knowsAbout;
	}

	public void setKnowsAbout(SliceableListEx<String> knowsAbout) {
		this.knowsAbout = knowsAbout;
	}

	public SliceableListEx<BusinessEx> getWorksFor() {
		return worksFor;
	}

	public void setWorksFor(SliceableListEx<BusinessEx> worksFor) {
		this.worksFor = worksFor;
	}
		
	public String getAuthorRole() {
		return authorRole;
	}

	public void setAuthorRole(String authorRole) {
		this.authorRole = authorRole;
	}

}
