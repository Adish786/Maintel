package com.about.mantle.commons;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SubjectTerm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.fail;

public class Email {
	
	private final String id;
	private final String password;
	private final String imap;
	private Folder folder;
	private Message unreadMessage;
	private String msgBody;
	private static final int MAX_ATTEMPTS = 10;
	private static final String STORE_PROTOCOL = "mail.store.protocol";

	public Email(String id, String password, String imap) {
		this.id = id;
		this.password = password;
		this.imap = imap;
	}

	public Email folder(String folder) throws MessagingException {
		Properties props = System.getProperties();
		props.setProperty(STORE_PROTOCOL, "imaps");
		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("imaps");
		store.connect(imap, id, password);
		this.folder = store.getFolder(folder);
		return this;
	}
	
	public Email unreadMessage(String subject) throws MessagingException, InterruptedException {
		assertThat("must call folder before unreadMessage", this.folder, is(notNullValue()));
		folder.open(Folder.READ_WRITE);
		Message[] messages = folder.search(new SubjectTerm(subject), this.folder.getMessages());
		// wait for msgs to load
		for (int attempts = 0; messages.length <= 0; attempts++) {
			messages = folder.search(new SubjectTerm(subject), this.folder.getMessages());
			if (attempts >= MAX_ATTEMPTS) {
				fail("could not get messages after max attempts!");
			} else {
				Thread.sleep(10000);
			}

		}

		List<Message> unreadMsgs = new ArrayList<>();
		for (Message msg : messages) {
			if (!msg.isSet(Flags.Flag.SEEN)) {
				unreadMsgs.add(msg);
				msg.setFlag(Flags.Flag.SEEN, true);
			}
		}
		if (unreadMsgs.size() == 0)
			fail("no unread messages found!");
		this.unreadMessage = unreadMsgs.get(0);
		return this;
	}

	public Email body() throws IOException, MessagingException {
		assertThat("must call unreadMessage before readMessage", this.unreadMessage, is(notNullValue()));
		String line;
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.unreadMessage.getInputStream()));
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		this.msgBody = buffer.toString();
		return this;
	}

	public void test(String msg) {
		assertThat("msg body is empty or null", this.msgBody, not(isEmptyOrNullString()));
		assertThat("msg body is not correct", this.msgBody.contains(msg), is(true));
	}
}
