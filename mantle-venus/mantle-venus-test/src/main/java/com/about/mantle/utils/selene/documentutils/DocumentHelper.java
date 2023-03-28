package com.about.mantle.utils.selene.documentutils;

import com.about.mantle.utils.dateutils.DateUtils;
import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.about.mantle.utils.selene.document.api.Document;
import com.about.mantle.utils.selene.document.api.SCDocument;
import com.about.mantle.utils.selene.document.api.listsc.ListSC;
import com.about.mantle.utils.selene.document.api.recipe.RecipeSCDocument;
import com.about.mantle.utils.selene.document.client.DocumentClient;
import com.about.mantle.utils.selene.meta.api.Meta;
import com.about.mantle.utils.selene.meta.client.MetaClient;
import com.about.mantle.utils.selene.vertical.Vertical;
import com.about.mantle.utils.servicediscovery.ApplicationServiceDiscovery;

import java.io.IOException;

import static javax.ws.rs.core.Response.Status.OK;

/*
convenience methods to work with Selene document
 */
public class DocumentHelper {
	private final String templateType;
	private Document document;

	/*
	supported templateType names: STRUCTUREDCONTENT, RECIPESC, LISTSC
	 */
	public DocumentHelper(String templateType) {
		this.templateType = templateType.toUpperCase();
	}

	public DocumentClient documentWithMeta(String documentFilePath, String metaFilePath)
			throws IOException {
		DocumentClient client = createDocument(documentFilePath);
		setMeta(metaFilePath);
		return client;
	}

	public DocumentClient getDocumentClient() {
		DocumentClient client = null;
		switch (templateType) {
		case "RECIPESC":
			return new DocumentClient<>(RecipeSCDocument.class);
		case "STRUCTUREDCONTENT":
			return new DocumentClient<>(SCDocument.class);
		case "LISTSC":
			return new DocumentClient<>(ListSC.class);
		}
		throw new RuntimeException(templateType + " is not supported yet!");
	}

	public DocumentClient createDocument(String documentFilePath) throws IOException {
		DocumentClient client = null;
		switch (templateType) {
		case "RECIPESC":
			client = new DocumentClient(documentFilePath, RecipeSCDocument.class);
			break;
		case "STRUCTUREDCONTENT":
			client = new DocumentClient<SCDocument>(documentFilePath, SCDocument.class);
			break;
		case "LISTSC":
			client = new DocumentClient<ListSC>(documentFilePath, ListSC.class);
			break;
		default:
			throw new RuntimeException(templateType + " is not supported yet!");
		}
		DataWithStatus<Document> dataWithStatus = client.create(OK);
		document = dataWithStatus.getData();
		return client;
	}

	public void setMeta(Meta meta) throws IOException {
		MetaClient metaClient = new MetaClient();
		metaClient.setDocumentMeta(document.getDocId(), meta);
	}

	private void setMeta(String metaFilePath) throws IOException {
		MetaClient metaClient = new MetaClient(metaFilePath);
		metaClient.setDocumentMeta(document.getDocId());
	}

	public String getDisplayedDate(String format) {
		if(document != null) {
			DateUtils dateUtils = new DateUtils(document.getDates().getDisplayed(), format);
			return dateUtils.getDateInGivenFormat();
		}
		throw new RuntimeException("document must not be null");
	}

	public String getUrl(){
		if(document != null) {
			String appName = Vertical.valueOf(document.getVertical()).appName();
			return ApplicationServiceDiscovery.getUrl(appName) + "/" + document.getSlug() + "-" + document.getDocId();
		}
		throw new RuntimeException("document must not be null");
	}
}
