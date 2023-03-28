package com.about.mantle.utils.selene.utils;

import com.about.mantle.utils.selene.document.client.DocumentClient;

import static javax.ws.rs.core.Response.Status.OK;

public class DocumentTestUtils {

	public static void documentTestWithDelete(DocumentClient client, Runnable test) {
		try {
			test.run();
		} finally {
			client.delete(OK);
		}
	}

}
