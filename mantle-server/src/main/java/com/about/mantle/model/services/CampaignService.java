package com.about.mantle.model.services;

import java.util.List;

import com.about.mantle.model.campaign.Campaign;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;

/**
* Service for retrieving campaign data that is utilized by `mntl-dialog--campaign` component
*/
public interface CampaignService {
    public Campaign getCampaign(String isoCode, BaseDocumentEx document, List<TaxeneNodeEx> ancestors);
}
