package com.about.mantle.visual;

import org.openqa.selenium.Dimension;

public enum VisualDeviceType {
	PC(1400, 1700), 
	MOBILE(375, 667), 
	TABLET(1024, 1600);
	
	private Dimension dimention;

	public Dimension dimention() {
		return this.dimention;

	}

	private VisualDeviceType(int x, int y) {
		this.dimention = new Dimension(x, y);
	}
}
