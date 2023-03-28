package com.about.mantle.model.services.impl;

import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.services.NewsletterValidationService;

public class NewsletterValidationServiceImpl implements NewsletterValidationService {

	private Set<String> mailboxBlocklist = new HashSet<>();
	
	public NewsletterValidationServiceImpl(InputStream mailboxBlocklistDataSource) {
		if (mailboxBlocklistDataSource != null) {
			try {
				IOUtils.readLines(mailboxBlocklistDataSource).stream().forEach(line -> mailboxBlocklist.add(line));
			} catch (IOException e) {
				throw new GlobeException("Failed to parse the mailbox blocklist input", e);
			}
		}
	}

	@Override
	public boolean isValidMailbox(String email) {
		if (email == null) return false;
		return !mailboxBlocklist.contains(substringBefore(email, "@"));
	}

}
