package com.about.mantle.model.services.document.preprocessor;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * If the document has NLP data in it's meta data we parse it into a better format.
 *
 * These values are put into selene from google NLP services and legacy meredith services. These values get updated often.
 */
public class NLPPreprocessor implements DocumentPreprocessor {

	@Override
	public BaseDocumentEx preProcessDocument(BaseDocumentEx document) {
		MetaDataEx metaData = document.getMetaData();
		if (metaData == null) return document;
		MetaDataEx.NLP nlp = metaData.getNlp();
		if (nlp == null) return document;

		// get concepts
		SliceableListEx<MetaDataEx.NLP.NLPEntity> nlpEntities = nlp.getEntities();
		if (nlpEntities != null) {
			List<String> concepts = new ArrayList<>();
			for (MetaDataEx.NLP.NLPEntity nlpEntity : nlpEntities.getList()) {
				concepts.addAll(getSplitNameList(nlpEntity.getName()));
			}
			nlp.setConcepts(concepts);
		}

		// get taxons
		SliceableListEx<MetaDataEx.NLP.NLPCategory> nlpCategories = nlp.getCategories();
		if (nlpCategories != null) {
			List<String> taxons = new ArrayList<>();
			for (MetaDataEx.NLP.NLPCategory nlpCategory : nlpCategories.getList()) {
				// Remove any spaces or "&" characters
				taxons.addAll(getSplitNameList(StringUtils.replaceChars(nlpCategory.getName(), " &", "")));
			}
			nlp.setTaxons(taxons);
		}

		return document;
	}

	/**
	 * Splits on "/" or "|"
	 */
	private List<String> getSplitNameList(String nlpName) {
		return List.of(StringUtils.split(nlpName, "/|"));
	}


}