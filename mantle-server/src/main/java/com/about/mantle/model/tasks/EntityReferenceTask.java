package com.about.mantle.model.tasks;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.HashMap;

import javax.validation.constraints.NotNull;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.EntityReferenceDocumentEx;
import com.about.mantle.model.extended.docv2.sc.EntityReferenceInfo;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentEntityReferenceEx;
import com.about.mantle.model.json.JsonTask;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import freemarker.ext.dom.NodeModel;

/**
 * Tasks for working with entity references (both document and content blocks).
 */
@Tasks
public class EntityReferenceTask {

	private static final Logger logger = LoggerFactory.getLogger(EntityReferenceTask.class);

	public static final String BOVD_ROOT = "entityreference";

	private final BusinessOwnedVerticalDataService bovdService;
	private final JsonTask jsonTask;

	public EntityReferenceTask(BusinessOwnedVerticalDataService bovdService, JsonTask jsonTask) {
		this.bovdService = bovdService;
		this.jsonTask = jsonTask;
	}

	/**
	 * Gets the metadata for a specific entity.
	 * @param document {@link EntityReferenceDocumentEx}
	 * @return a free-form json object taken verbatim from the external data source associated with the entity reference
	 */
	@Task(name = "entityReferenceMetadata")
	public HashMap<String, Object> entityReferenceMetadata(@TaskParameter(name = "document") BaseDocumentEx document) {
		if (document instanceof EntityReferenceDocumentEx) {
			EntityReferenceDocumentEx entityReferenceDocument = (EntityReferenceDocumentEx) document;
			String path = resolveEntityReferenceInfoToPath(entityReferenceDocument.getEntityReferenceInfo());
			try {
				return jsonTask.json(getEntityReferenceFileContent(path, "metadata.json"));
			} catch (Exception e) {
				logger.error("Failed to get entity reference metadata for document [{}] due to exception", document.getUrl(), e);
			}
		}
		return null;
	}

	/**
	 * Gets the block content for a property of the entity reference.
	 * @param block {@link StructuredContentEntityReferenceEx}
	 * @return the raw content taken verbatim from the external data source associated with a specific property of the entity reference
	 */
	@Task(name = "entityReferenceBlockContent")
	public String entityReferenceBlockContent(@TaskParameter(name = "block") StructuredContentEntityReferenceEx block) {
		if (block != null) {
			String path = resolveEntityReferenceInfoToPath(block.getData().getEntityReferenceInfo());
			String file = resolvePropertyNameToFile(block);
			return getEntityReferenceFileContent(path, file);
		}
		return null;
	}

	/**
	 * Gets the XML for a property of the entity reference block.
	 * @param block {@link StructuredContentEntityReferenceEx}
	 * @return the parsed XML from the external data source associated with a specific property of the entity reference
	 */
	@Task(name = "entityReferenceBlockXML")
	public NodeModel entityReferenceBlockXML(@TaskParameter(name = "block") StructuredContentEntityReferenceEx block) {
		if (block != null) {
			String path = resolveEntityReferenceInfoToPath(block.getData().getEntityReferenceInfo());
			String file = resolvePropertyNameToFile(block);
			return getEntityReferenceXML(path, file);
		}
		return null;
	}

	@NotNull
	private byte[] getEntityReferenceResource(String path, String file) {
		return bovdService.getResource(Path.of(BOVD_ROOT, path, file).toString());
	}

	@NotNull
	private String getEntityReferenceFileContent(String path, String file) {
		return new String(getEntityReferenceResource(path, file));
	}

	@NotNull
	private NodeModel getEntityReferenceXML(String path, String file) {
		try {
			return NodeModel.parse(new InputSource(new ByteArrayInputStream(getEntityReferenceResource(path, file))));
		} catch (Exception e) {
			throw new GlobeException("Failed to parse XML from path: " + path + ", file: " + file, e);
		}
	}

	@NotNull
	private String resolveEntityReferenceInfoToPath(EntityReferenceInfo entityReferenceInfo) {
		String type = entityReferenceInfo.getType();
		String id = entityReferenceInfo.getId();
		String entityReferencePath = FilenameUtils.normalize(Path.of(type, id).toString());
		if (entityReferencePath == null) {
			throw new GlobeException("Failed to resolve to a valid path: " + entityReferenceInfo.toString());
		}
		return entityReferencePath;
	}

	@NotNull
	private String resolvePropertyNameToFile(StructuredContentEntityReferenceEx block) {
		String prop = block.getData().getPropertyName();
		if ("precautions".equalsIgnoreCase(prop)) {
			return "precautions.xml";
		} else if ("dosage".equalsIgnoreCase(prop)) {
			return "dosage.xml";
		}
		throw new GlobeException("Failed to resolve property name: " + block.toString());
	}

}
