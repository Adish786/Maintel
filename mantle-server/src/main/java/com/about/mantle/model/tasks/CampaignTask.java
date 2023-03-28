package com.about.mantle.model.tasks;

import java.util.List;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.campaign.Campaign;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.CampaignService;

@Tasks
public class CampaignTask {

    private final CampaignService campaignService;
    private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);

    public CampaignTask(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Task(name="CAMPAIGN")
    public Campaign getCampaign(
        @RequestContextTaskParameter RequestContext requestContext,
        @TaskParameter(name = "document") BaseDocumentEx document,
        @TaskParameter(name = "ancestors") List<TaxeneNodeEx> ancestors
    ) {
        if (campaignService == null) return null;
        if (document == null) return null;
        if (isUserCookied(requestContext)) return null;
        return campaignService.getCampaign(requestContext.getGeoData().getIsoCode(), document, ancestors);
    }

    private boolean isUserCookied(RequestContext requestContext) {
        Cookie hid = requestContext.getCookie("hid");
        if (hid != null) return true;

        Cookie ddmCampaignSession = requestContext.getCookie("ddmCampaignSession");
        if (ddmCampaignSession != null) return true;

        Cookie ddmCampaignExtended = requestContext.getCookie("ddmCampaignExtended");
        if (ddmCampaignExtended != null) {
            try{
                int ddmCampaignExtendedValue = Integer.parseInt(ddmCampaignExtended.getValue());
                if (ddmCampaignExtendedValue >= 4) return true;
            }
            catch (NumberFormatException ex){
                logger.error("Failed to parse ddmCampaignExtended", ex);
            }
        }

        return false;
    }
}
