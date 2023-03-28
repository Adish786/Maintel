package com.about.mantle.model.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.campaign.Campaign;
import com.about.mantle.model.campaign.CampaignGeoTargeting;
import com.about.mantle.model.campaign.CampaignTargetingRule;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.about.mantle.model.services.CampaignService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CampaignServiceImpl implements CampaignService {
    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BusinessOwnedVerticalDataService bovdService;
    private final String campaignsConfigPath;

    public CampaignServiceImpl(BusinessOwnedVerticalDataService bovdService, String campaignsConfigPath) {
        this.campaignsConfigPath = campaignsConfigPath;
        this.bovdService = bovdService;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Campaign getCampaign(String isoCode, BaseDocumentEx document, List<TaxeneNodeEx> ancestors) {
        List<Campaign> campaigns = getCampaigns();

        final List<String> taxonomyShortHeadings = ancestors.stream()
            .filter(ancestor -> ancestor.getDocument().getShortHeading() != null)
            .map(ancestor -> ancestor.getDocument().getShortHeading().toUpperCase()).collect(Collectors.toList());


        campaigns = campaigns.stream()
            .filter(campaign -> {
                return processGeoTargeting(isoCode, campaign.getGeoTargeting()) &&
                    campaign.getTargeting().stream()
                        .anyMatch(rule -> {
                            return processTargetingRules(document, rule, taxonomyShortHeadings);
                        });
            }).collect(Collectors.toList());

        if (campaigns.isEmpty()) return null;

        if (!taxonomyShortHeadings.isEmpty()) {
            boolean hasValidTaxonomiesTargetingRules = campaigns.stream()
                .anyMatch(campaign -> {
                    return campaign.getTargeting().stream()
                        .anyMatch(rule -> {
                            return rule.getTaxonomies() != null
                                && rule.getTaxonomies().stream().anyMatch(taxonomy -> taxonomyShortHeadings.contains(taxonomy));
                        });
                });

            if (hasValidTaxonomiesTargetingRules) {
                return prioritizeClosestTaxonomy(taxonomyShortHeadings, campaigns);
            }
        }

        return campaigns.get(0);
    }

    private Campaign prioritizeClosestTaxonomy(List<String> taxonomyShortHeadings, List<Campaign> campaigns) {
        Collections.reverse(taxonomyShortHeadings);
        for (String taxonomyShortHeading : taxonomyShortHeadings) {
            Optional<Campaign> validCampaign = campaigns.stream()
                .filter(campaign -> {
                    return campaign.getTargeting().stream()
                        .filter(rule -> {
                            return rule.getTaxonomies() != null && rule.getTaxonomies().contains(taxonomyShortHeading);
                        }).findAny().isPresent();
                }).findFirst();

            if (validCampaign.isPresent()) {
                return validCampaign.get();
            }
        }

        return null;
    }

    private boolean processGeoTargeting(String isoCode, CampaignGeoTargeting geoTargeting) {
        if (geoTargeting == null) return true;
        boolean isISOCodeIncluded = geoTargeting.getCountries().contains(isoCode);
        return geoTargeting.getIsInclusive() ? isISOCodeIncluded : !isISOCodeIncluded;
    }

    private boolean processTargetingRules(BaseDocumentEx document, CampaignTargetingRule rule, List<String> taxonomyShortHeadings) {
        String templateType = document.getTemplateType().toString().toUpperCase();
        if (rule.getTemplates() == null) return false;
        if (!rule.getTemplates().contains(templateType)) return false;

        if (rule.getViewTypes() != null) {
            String viewType = document.getViewType() == null
                ? ""
                : document.getViewType().toString().toUpperCase();

            if (!rule.getViewTypes().contains(viewType)) return false;
        }

        if (rule.getRevenueGroups() != null) {
            String revenueGroup = document.getRevenueGroup() == null
                ? "NONE"
                : document.getRevenueGroup().toString().toUpperCase();

            if (!rule.getRevenueGroups().contains(revenueGroup)) return false;
        }

        if (rule.getTaxonomies() != null && !taxonomyShortHeadings.isEmpty()) {
            if (!rule.getTaxonomies().stream().anyMatch(taxonomy -> taxonomyShortHeadings.contains(taxonomy))) return false;
        }

        return true;
    }

    private List<Campaign> getCampaigns() {
        List<Campaign> mappedJsonObject = new ArrayList<>();
        String jsonString = null;

        try {
            byte[] bytes = bovdService.getResource(campaignsConfigPath);
            jsonString = new String(bytes, StandardCharsets.UTF_8);
        } catch (GlobeException ex) {
            logger.error("Can not read Json data from BOVD service.", ex);
        }

        if (StringUtils.isNotBlank(jsonString)) {
            try {
                mappedJsonObject = objectMapper.readValue(jsonString, new TypeReference<List<Campaign>>() {});
            } catch (IOException e) {
                logger.error("Json object mapping failed.", e);
            }
        }

        return mappedJsonObject;
    }
}
