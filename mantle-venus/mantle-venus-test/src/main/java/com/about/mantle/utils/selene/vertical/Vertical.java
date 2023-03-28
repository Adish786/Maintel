package com.about.mantle.utils.selene.vertical;

import java.util.Random;

public enum Vertical {
	HEALTH("verywellhealth.com","health", 4014772L),
	MONEY("thebalancemoney.com","money", 4053121L),
	TECH("lifewire.com","tech", 4074445L),
	LIFESTYLE("thespruce.com","lifestyle", 4117266L),
	REFERENCE( "thoughtco.com","reference", 4122478L),
	TRAVEL("tripsavvy.com","travel", 4138131L),
	ESPANOL("aboutespanol.com","reference-espanol", 4145420L),
	FASHION("liveabout.com","reference-fashion", 4145295L),
	FITNESS("verywellfit.com","health-fitness", 4154488L),
	MIND("verywellmind.com","health-mind", 4156452L),
	FAMILY("verywellfamily.com","health-family", 4156450L),
	CAREERS("thebalancecareers.com","money-careers", 4161173L),
	BUSINESS("thebalancesmb.com", "money-business",4161174L),
	FOOD("thespruceeats.com","lifestyle-food", 4161185L),
	PETS("thesprucepets.com","lifestyle-pets", 4161184L),
	CRAFTS("thesprucecrafts.com","lifestyle-crafts", 4161186L),
	FINANCE("investopedia.com", "finance",4173514L),
	BEAUTY("byrdie.com","beauty", 4585073L),
	DESIGN("mydomaine.com", "design",4585074L),
	RELIGION("learnreligions.com","reference-religion", 4589228L),
	WEDDINGS("brides.com", "beauty-weddings",4690717L),
	LIQUOR("liquor.com", "liquor",4776263L),
	GREEN("treehugger.com", "green", 4802166L),
	SERIOUSEATS("seriouseats.com", "serious-eats", 4800000L),
	SIMPLYRECIPES("simplyrecipes.com", "simply-recipes", 4800000L);

	private String domain;
	private Long docId;
	//squadron app name for service discovery
	private String appName;
	private static final Random RANDOM = new Random();

	Vertical(String domain, String appName, Long docId) {
		this.domain = domain;
		this.docId = docId;
		this.appName = appName;
	}

	public String domainValue() {
		return domain;
	}

	public Long rootDocid() {
		return docId;
	}

	public String appName() { return appName; }

	/**
	 *
	 * @return a random vertical from Vertical enum
	 */
	public static Vertical randomVertical() {
		return values()[RANDOM.nextInt(values().length - 1)];
	}
}
