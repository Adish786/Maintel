package com.about.mantle.utils.selene.meta.client;

import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.about.mantle.utils.selene.document.api.Document;
import com.about.mantle.utils.selene.document.client.DocumentClient;
import com.about.mantle.utils.selene.document.request.DocumentReadRequest;
import com.about.mantle.utils.selene.meta.api.Meta;
import com.about.mantle.utils.selene.meta.request.ReadRequest;
import com.about.mantle.utils.selene.utils.UpdateAndRestore;

import static javax.ws.rs.core.Response.Status.OK;

public class UpdateAndRestoreMeta<T extends Document> extends UpdateAndRestore<Meta> {

	private final Long docId;
	private final MetaClient metaClient = new MetaClient();
	private final DocumentClient<T> documentClient;
	private Meta metaBefore;
	private T documentBefore;

	public UpdateAndRestoreMeta(Long docId, DocumentClient documentClient) {
		this.docId = docId;
		this.documentClient = documentClient;
	}

	@Override
	public void getDataBeforeUpdate() {
		DataWithStatus<T> documentDataBefore = documentClient.read(DocumentReadRequest.builder().docId(docId).build(), OK);
		documentBefore = documentDataBefore.getData();
		metaBefore = metaClient.read(OK, ReadRequest.builder().docId(docId).build()).getData();
	}

	@Override
	protected DataWithStatus<Meta> update(Meta data) {
		DataWithStatus<Meta> meta = metaClient.put(OK, (Meta) data);
		documentClient.update(OK, documentBefore);
		return meta;
	}

	@Override
	protected void restore() {
		metaClient.put(OK, metaBefore);
		documentClient.update(OK, documentBefore);
	}

}
