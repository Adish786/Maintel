package com.about.mantle.model.tasks;

import static com.about.globe.core.util.CollectionsUtil.splitToSet;

import java.util.HashMap;
import java.util.Map;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.model.extended.Configs;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.TaxeneConfigValueEx;
import com.about.mantle.model.extended.TaxeneConfigs;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.Direction;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.TraverseStrategy;

@Tasks
public class TaxeneConfigurationTask implements NodeConfigurationTask {
	
	public static final TaxeneConfigs NULL = new TaxeneConfigs();

	private final TaxeneRelationService taxeneService;
	private final UrlDataFactory urlDataFactory;

	public TaxeneConfigurationTask(TaxeneRelationService taxeneService, UrlDataFactory urlDataFactory) {
		this.taxeneService = taxeneService;
		this.urlDataFactory = urlDataFactory;
	}

	@Override
	@Task(name = "TaxeneNodeConfigs")
	public Configs getNodeConfigs(@RequestContextTaskParameter RequestContext requestContext) {
		return getNodeConfigs(requestContext.getUrlData());
	}

	@Override
	@Task(name = "TaxeneNodeConfigs")
	@TimedComponent(category = "task")
	public Configs getNodeConfigs(@TaskParameter(name = "docId") Long docId) {
		return getNodeConfigsFromDocId(docId);
	}

	@Override
	@Task(name = "TaxeneNodeConfigs")
	@TimedComponent(category = "task")
	public Configs getNodeConfigs(@TaskParameter(name = "url") String url) {
		return getNodeConfigs(urlDataFactory.create(url));
	}

	@Override
	public Configs getNodeConfigs(UrlData urlData) {
		if (urlData instanceof VerticalUrlData) {
			Long docId = ((VerticalUrlData) urlData).getDocId();
			
			if (docId != null) {
				return getNodeConfigsFromDocId(docId);
			}
		}
		return TaxeneConfigs.NULL;
	}

	private Configs getNodeConfigsFromDocId(Long docId) {
		//@formatter:off
		TaxeneTraverseRequestContext reqCtx = new TaxeneTraverseRequestContext.Builder()
										.setDocId(docId)
										.setActiveOnly(true)
										.setDirection(Direction.OUT)
										.setIncludeConfigs(true)
										.setTraverseStrategy(TraverseStrategy.BREADTH_FIRST)
										.setRelationships(splitToSet("primaryParent"))
										.build();
		
		//@formatter:on
		return convert(taxeneService.traverse(reqCtx));
	}
 
	private Configs convert(TaxeneNodeEx taxeneNode) {
		if(taxeneNode == null) return new TaxeneConfigs();
		
		Map<String, TaxeneConfigValueEx> configsMap = new HashMap<>();
		aggregate(taxeneNode, configsMap);

		return new TaxeneConfigs(configsMap);
	}

	private void aggregate(TaxeneNodeEx taxeneNode, Map<String, TaxeneConfigValueEx> configsMap) {
		taxeneNode.getConfiguration().entrySet().stream().filter(e -> !configsMap.containsKey(e.getKey()))
				.forEach(e -> configsMap.put(e.getKey(), e.getValue()));

		if (taxeneNode.getPrimaryParent() != null) {
			aggregate(taxeneNode.getPrimaryParent(), configsMap);
		}
	}
	
}
