package com.about.mantle.utils.selene.document.client;

import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.about.mantle.utils.selene.document.api.Document;
import com.about.mantle.utils.selene.document.request.DocumentReadRequest;
import com.about.mantle.utils.selene.documentutils.DocumentHelper;
import com.about.mantle.utils.selene.utils.UpdateAndRestore;

import static javax.ws.rs.core.Response.Status.OK;

public class UpdateAndRestoreDocument<T extends Document> extends UpdateAndRestore<T> {
	private final Long docId;
	private final DocumentClient<T> client;
	private T documentBefore;

	public UpdateAndRestoreDocument(Long docId, String template) {
		this.docId = docId;
		client = new DocumentHelper(template).getDocumentClient();
	}

	@Override
	public void getDataBeforeUpdate() {
		DocumentReadRequest request = DocumentReadRequest.builder().docId(docId).build();
		DataWithStatus<T> dataWithStatus = client.read(request, OK);
		documentBefore = dataWithStatus.getData();
	}

	@Override
	protected DataWithStatus<T> update(T data) {
		return client.update(OK, (T) data);
	}

	@Override
	protected void restore() {
		client.update(OK, documentBefore);
	}

}
