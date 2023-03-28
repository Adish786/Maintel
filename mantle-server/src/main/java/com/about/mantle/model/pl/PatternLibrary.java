package com.about.mantle.model.pl;

import java.util.HashMap;
import java.util.List;

import com.about.globe.core.definition.template.RenderManifest;

public class PatternLibrary {

	private RenderManifest manifest;
	private HashMap<String, List<String>> locations;
	
	public PatternLibrary(RenderManifest manifest, HashMap<String, List<String>> locations) {
		this.manifest = manifest;
		this.locations = locations;
	}

	public RenderManifest getManifest() {
		return manifest;
	}

	public HashMap<String, List<String>> getLocations() {
		return locations;
	}
	
	
}
