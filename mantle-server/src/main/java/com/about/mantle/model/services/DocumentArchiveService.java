package com.about.mantle.model.services;

import java.util.Collections;
import java.util.Set;

public interface DocumentArchiveService {
	Set<String> getArchivedDocuments();

	static DocumentArchiveService emptyArchiveService() {
		return () -> Collections.emptySet();
	}
}
