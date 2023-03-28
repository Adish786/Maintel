package com.about.mantle.utils.jsonfileutils;

import com.about.mantle.utils.selene.meta.api.Meta;

public class MetaFromFile {
	private final String filePath;
	private Meta meta;

	public MetaFromFile(String filePath) {
		this.filePath = filePath;
	}

	public Meta meta() {
		return meta(true);
	}

	public Meta meta(boolean readFromJar) {
		if(meta == null) {
			ObjectFromFile<Meta> metaObj = new ObjectFromFile(filePath, Meta.class);
			if(readFromJar) {
				meta = metaObj.getObject();
			} else {
				meta = metaObj.getObject(false);
			}
		}
		return meta;
	}
}
