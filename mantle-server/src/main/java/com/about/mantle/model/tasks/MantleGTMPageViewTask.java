package com.about.mantle.model.tasks;

import com.about.globe.core.http.GeoData;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.model.EnvironmentConfig;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.globe.core.testing.GlobeBucket;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.gtm.ABTest;
import com.about.mantle.model.gtm.EnvironmentData;
import com.about.mantle.model.gtm.EnvironmentData.Client;
import com.about.mantle.model.gtm.EnvironmentData.Environment;
import com.about.mantle.model.gtm.EnvironmentData.Resources;
import com.about.mantle.model.gtm.EnvironmentData.Server;
import com.about.mantle.model.gtm.MantleGTM;
import com.about.mantle.model.gtm.MantleGTM.Builder;
import com.about.mantle.model.journey.JourneyRelationshipType;
import com.about.mantle.model.journey.JourneyRoot;
import com.about.mantle.model.journey.JourneyType;
import com.about.mantle.model.services.ExternalComponentService;
import com.about.mantle.render.MantleRenderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.about.mantle.model.journey.JourneyRelationshipType.LINKED;
import static com.about.mantle.model.journey.JourneyRelationshipType.MEMBER;
import static com.about.mantle.model.journey.JourneyRelationshipType.NONE;
import static com.about.mantle.model.journey.JourneyType.MINI;
import static com.about.mantle.model.journey.JourneyType.STANDARD;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Tasks
public class MantleGTMPageViewTask {
    private final Environment environment;
    private final ProjectInfo projectInfo;
    private final MantleRenderUtils utils;
    private final TaxeneRelationTask taxeneRelation;
    protected final Set<TemplateTypeEx> articleTemplates;
    protected final JourneyTask journeyTask;
    protected final String mantleVersion;
    protected final ExternalComponentService commerceService;

    private static Logger log = LoggerFactory.getLogger(MantleGTMPageViewTask.class);

    public MantleGTMPageViewTask(EnvironmentConfig environmentConfig, ProjectInfo projectInfo, MantleRenderUtils utils,
            TaxeneRelationTask taxeneRelation, Set<TemplateTypeEx> articleTemplates, JourneyTask journeyTask) {
        this(environmentConfig, projectInfo, utils,taxeneRelation, articleTemplates, journeyTask, null, null);
    }
    
    public MantleGTMPageViewTask(EnvironmentConfig environmentConfig, ProjectInfo projectInfo, MantleRenderUtils utils,
            TaxeneRelationTask taxeneRelation, Set<TemplateTypeEx> articleTemplates, JourneyTask journeyTask, ResourceTasks resourceTasks, ExternalComponentService commerceService) {
        this.environment = new Environment(environmentConfig.getEnvironment(), environmentConfig.getApplication(),
                environmentConfig.getDataCenter());
        this.projectInfo = projectInfo;
        this.utils = utils;
        this.taxeneRelation = taxeneRelation;
        this.articleTemplates = articleTemplates;
        this.journeyTask = journeyTask;
        this.commerceService = commerceService;
        
        if(resourceTasks!= null && resourceTasks.moduleVersions(true).containsKey("mantle-resource")){
        	this.mantleVersion = resourceTasks.moduleVersions(true).get("mantle-resource");
        }else{
        	this.mantleVersion = null;
        }
        
    }

    @Task(name = "gtmPageView")
    public MantleGTM createGTM(@RequestContextTaskParameter RequestContext requestContext) {
        return this.createGTM(requestContext, null, null);
    }

    @Task(name = "gtmPageView")
    public MantleGTM createGTM(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc) {
        return this.createGTM(requestContext, doc, null);
    }

    @Task(name = "gtmPageView")
    public MantleGTM createGTM(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "templateId") Integer templateId) {
        return this.createGTM(requestContext, null, templateId);
    }

    @Task(name = "gtmPageView")
    public MantleGTM createGTM(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "templateId") Integer templateId) {

        Builder builder = new Builder();
        return this.createGTM(requestContext, doc, templateId, builder);
    }

    @Task(name = "gtmPageViewWithJourneys")
    public MantleGTM createGTMWithJourneyRoot(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc) {
        return this.createGTMWithJourneyRoot(requestContext, doc, null, null);
    }

    @Task(name = "gtmPageViewWithJourneys")
    public MantleGTM createGTMWithJourneyRoot(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "templateId") Integer templateId) {
        return this.createGTMWithJourneyRoot(requestContext, doc, templateId, null);
    }

    @Task(name = "gtmPageViewWithJourneys")
    public MantleGTM createGTMWithJourneyRoot(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "journey") JourneyRoot journeyRoot) {
        return createGTMWithJourneyRoot(requestContext, doc, null, journeyRoot);
    }

    @Task(name = "gtmPageViewWithJourneys")
    public MantleGTM createGTMWithJourneyRoot(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "templateId") Integer templateId,
            @TaskParameter(name = "journey") JourneyRoot journeyRoot) {
    		return createGTMWithJourneyRootAndRecirc(requestContext, doc, templateId, journeyRoot, null);
    }

    @Task(name = "gtmPageViewWithRecirc")
    public MantleGTM createGTMWithRecirc(@RequestContextTaskParameter RequestContext requestContext,
                                                       @TaskParameter(name = "document") BaseDocumentEx doc,
                                                       @TaskParameter(name = "recircDocuments") SliceableListEx<BaseDocumentEx> recircList) {
        return createGTMWithJourneyRootAndRecirc(requestContext, doc, null, null, recircList);
    }
    
    @Task(name = "gtmPageViewWithJourneysAndRecirc")
    public MantleGTM createGTMWithJourneyRootAndRecirc(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "journey") JourneyRoot journeyRoot,
            @TaskParameter(name = "recircDocuments") SliceableListEx<BaseDocumentEx> recircList) {
        return createGTMWithJourneyRootAndRecirc(requestContext, doc, null, journeyRoot, recircList);
    }

    @Task(name = "gtmPageViewWithJourneysAndCompositeRecirc")
    public MantleGTM createGTMWithJourneyRootAndCompositeRecirc(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "journey") JourneyRoot journeyRoot,
            @TaskParameter(name = "recircCompositeDocuments") List<DocumentTaxeneComposite<BaseDocumentEx>> recircList) {
        return createGTMWithJourneyRootAndCompositeRecirc(requestContext, doc, null, journeyRoot, recircList);
    }

    @Task(name = "gtmPageViewWithJourneysAndRecirc")
    public MantleGTM createGTMWithJourneyRootAndRecirc(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "templateId") Integer templateId,
            @TaskParameter(name = "journey") JourneyRoot journeyRoot,
            @TaskParameter(name = "recircDocuments") SliceableListEx<BaseDocumentEx> recircList) {

        Builder builder = new Builder();

        boolean isJourney = journeyTask.validJourney(journeyRoot);

        builder = buildExperienceTypeForJourneys(builder, journeyRoot, doc);

        List<BaseDocumentEx> baseDocumentList = recircList != null ? recircList.getList() : null;

        return this.createGTM(requestContext,
                doc,
                templateId,
                builder,
                isJourney ? journeyRoot : null,
                baseDocumentList);
    }

    @Task(name = "gtmPageViewWithJourneysAndCompositeRecirc")
    public MantleGTM createGTMWithJourneyRootAndCompositeRecirc(@RequestContextTaskParameter RequestContext requestContext,
           @TaskParameter(name = "document") BaseDocumentEx doc,
           @TaskParameter(name = "templateId") Integer templateId,
           @TaskParameter(name = "journey") JourneyRoot journeyRoot,
           @TaskParameter(name = "recircCompositeDocuments") List<DocumentTaxeneComposite<BaseDocumentEx>> recircList) {

        SliceableListEx<BaseDocumentEx> baseDocumentRecircs = recircList != null ?
                SliceableListEx.of(recircList.stream().map(recirc -> recirc.getDocument()).collect(Collectors.toList())) : null;

        return this.createGTMWithJourneyRootAndRecirc(requestContext, doc, templateId, journeyRoot, baseDocumentRecircs);
    }

    @Task(name = "gtmPageView")
    protected MantleGTM createGTM(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "templateId") Integer templateId, Builder builder) {

        return createGTM(requestContext, doc, templateId, builder, null);
    }

    @Task(name = "gtmPageView")
    protected MantleGTM createGTM(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "document") BaseDocumentEx doc,
            @TaskParameter(name = "templateId") Integer templateId, Builder builder, JourneyRoot journeyRoot) {

    		return createGTM(requestContext, doc, templateId, builder, journeyRoot, null);
    }

    private MantleGTM createGTM(RequestContext requestContext,
                                BaseDocumentEx doc,
                                Integer templateId,
                                Builder builder,
                                JourneyRoot journeyRoot, // TODO deprecate this and just pull journey root from the task/svc
                                List<BaseDocumentEx> recircList) {

        builder = parseRequestContext(requestContext, builder);

        builder.mantleVersion(mantleVersion);

        if(commerceService != null){
        	addCommerceVersioningInformation(builder);
        }
        
        if (doc != null) {
            builder = parseDocument(requestContext, builder, doc);
        }

        if (templateId != null) {
            builder.templateId(Integer.toString(templateId));
        }

        if(recircList != null){
            builder.recircDocIdsFooter(recircList.stream().map(recirc -> getDocumentSourceTag(recirc) + "-" + recirc.getDocumentId().toString())
                    .collect(Collectors.joining("|")));
        }

        return builder.build();
    }

    /*
     * Was used to differentiate between different types of recirc documents
     * kept due to usage identified in GLBE-7270.
     * Please add to the logic here if new recirc types are added.
     */
    protected String getDocumentSourceTag(BaseDocumentEx document){
        return "S";
    }
    
    protected Builder addCommerceVersioningInformation(Builder builder){
    	builder.commerceVersion(commerceService.getVersion());

    	return builder;
    }
    
    protected Builder parseRequestContext(RequestContext requestContext, Builder builder) {
    	
        builder.abTests(createAbTests(requestContext))
                .environment(createEnvironmentData(requestContext))
                .country(requestContext.getGeoData().getIsoCode())
                .euTrafficFlag(requestContext.getGeoData().isInEuropeanUnion())
                .isGoogleBot(requestContext.getHeaders().isGoogleBot())
                .fullUrl(requestContext.getUrlData().toString())
                .internalSessionId(requestContext.getSessionId())
                .internalRequestId(requestContext.getRequestId())
                .muid(requestContext.getMuid())
                .hid(requestContext.getHashId());

        return builder;
    }

    protected Builder parseDocument(RequestContext requestContext, Builder builder, BaseDocumentEx doc) {
        JourneyRoot journeyRoot = journeyTask.getJourneyStructure(doc.getDocumentId());
        JourneyType journeyType = journeyTask.getJourneyType(journeyRoot);

        // If it's a _member_ of the journey and standard journey, use the journey root doc.  Otherwise use the document id.
        // for Mini Journeys we cannot evaluate the parent relationships since the root doc is a fake doc that selene doesn't
        // report parent relationships on it so use current document.
        Long docIdForBreadcrumbs =
                (journeyTask.validJourney(journeyRoot) && MEMBER.equals(journeyTask.getJourneyRelationshipType(doc.getDocumentId())) && journeyType == JourneyType.STANDARD) ?
                        journeyRoot.getDocument().getDocumentId() :
                        doc.getDocumentId();

        List<TaxeneNodeEx> taxonomyNodes = taxeneRelation.getAncestorNodes(taxeneRelation.getBreadcrumb(docIdForBreadcrumbs));
        StringBuilder taxonomyNameList = new StringBuilder();
        StringBuilder taxonomyIDList = new StringBuilder();
        
        for(TaxeneNodeEx taxonomyNode : taxonomyNodes){
        	
        	//Append pipes for separation of list items
        	if(taxonomyNameList.length() > 0){
        		taxonomyNameList.append("|");
        		taxonomyIDList.append("|");
        	}
        	
        	taxonomyNameList.append(taxonomyNode.getDocument().getShortHeading());
        	taxonomyIDList.append(taxonomyNode.getDocument().getDocumentId());
        }
        
        builder.authorId(doc.getAuthorKey())
        		.lastEditingAuthorId(doc.getLastEditingAuthorId())
                .lastEditingUserId(doc.getLastEditingUserId())
                .contentGroup(utils.getContentGroup(doc.getTemplateType().getTemplateId()))
                .description(doc.getSummary().getDescription())
                .documentId(doc.getDocumentId())
                .templateName(doc.getTemplateType().toString())
                .primaryTaxonomyIds(taxonomyIDList.toString())
                .primaryTaxonomyNames(taxonomyNameList.toString())
                .templateId(Integer.toString(doc.getTemplateType().getTemplateId()))
                .title(doc.getSummary().getTitle())
                .viewType(doc.getViewType())
                .revenueGroup(doc.getRevenueGroup());
        
        if (this.articleTemplates.contains(doc.getTemplateType())) {

            if (journeyRoot == null) {
                builder.experienceType("multipage");
            }

        }

        return builder;
    }

    // See https://dotdash.atlassian.net/browse/PCR-134?focusedCommentId=657691&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-657691
    protected Builder buildExperienceTypeForJourneys(Builder builder, JourneyRoot journeyRoot, BaseDocumentEx doc) {

        String experienceType = null;
        String experienceTypeName = null;

        JourneyRelationshipType journeyRelationshipType = journeyTask.getJourneyRelationshipType(doc.getDocumentId());

        if ((!(journeyTask.validJourney(journeyRoot))) || NONE.equals(journeyRelationshipType)) {
            // It's not a journey at all, linked or otherwise.
            experienceType = "single page";
        } else {

            JourneyType journeyType = journeyTask.getJourneyType(journeyRoot);
            experienceTypeName = journeyRoot.getShortHeading();

            if (LINKED.equals(journeyRelationshipType)) { // 'single page'
               if (MINI.equals(journeyType)) {
                   experienceType = "single page with minijourney";
               } else if (STANDARD.equals(journeyType)) {
                   experienceType = "single page with journey";
               } else {
                   experienceType = null; // error case
               }
            } else if (MEMBER.equals(journeyRelationshipType)) {
                if (MINI.equals(journeyType)) {
                    experienceType = "minijourney";
                } else if (STANDARD.equals(journeyType)) {
                    experienceType = "journey";
                } else {
                    experienceType = null; // error case
                }
            } else {
                experienceType = null; // error case
            }
        }

        if (experienceType == null) {
            log.error("Could not determine GTM 'experienceType' for document {}.  Sending empty string.",
                    doc.getDocumentId());
            experienceType = "";
        }

        builder.experienceType(experienceType);
        if (isNotBlank(experienceTypeName) && isNotBlank(experienceType)) {
            builder.experienceTypeName(experienceTypeName);
        }

        return builder;


	}

    public List<ABTest> createAbTests(RequestContext rc) {
        Map<String, GlobeBucket> tests = rc.getTests();
        return tests.keySet().stream().map((testName) -> createABTest(testName, tests.get(testName)))
                .collect(Collectors.toList());
    }

    public ABTest createABTest(String testName, GlobeBucket bucket) {
        return new ABTest.Builder().testName(testName).bucketDescription(bucket.getDescription())
                .bucketName(bucket.getName()).bucketTrackingId(bucket.getTrackingId()).bucketValue(bucket.getValue())
                .build();
    }

    public EnvironmentData createEnvironmentData(RequestContext rc) {
        return new EnvironmentData.Builder().environment(createEnvironment()).server(createServer())
                .resources(createResources(rc)).client(createClient(rc)).build();
    }

    public Environment createEnvironment() {
        return environment;
    }

    public Server createServer() {
        return new Server(projectInfo.getVersion(), projectInfo.getTitle());
    }

    public Resources createResources(RequestContext rc) {
        return new Resources(projectInfo.getVersion(), "0", "0");
    }

    public Client createClient(RequestContext rc) {
        GeoData geoData = rc.getGeoData();

        String usStateCode = ("US".equals(geoData.getIsoCode())) ? geoData.getSubdivisionCode() : "";
        return new Client(rc.getHeaders().getUserAgent(), rc.getUserAgent().getDeviceCategory().toString(), usStateCode);
    }
}
