package com.about.mantle.model.shared_services.regsources.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RegSource or Registration Source is a key identifier in the Growth ecosystem which identifies the origin of a registration
 * Documentation: https://confluence.meredith.com/display/MDCPSS/Registration+Source
 */
public class RegSource implements Serializable {

    private String id;

    private String name;

    private String title;

    @JsonProperty(value = "subheading")
    private String subtitle;

    private String description;

    @JsonProperty(value = "submit_text")
    private String submitButtonText;

    @JsonProperty(value = "mobile_creative_url")
    private String backgroundUrl;

    @JsonProperty(value = "registration_source_targets")
    private List<RegistrationSourceTarget> registrationSourceTargets;

    @JsonIgnore
    Map<String, List<RegistrationSourceTarget>> registrationSourceTargetsGroupedByType;

    @JsonProperty(value = "additional_data_prompts")
    private List<String> additionalDataPrompts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmitButtonText() {
        return submitButtonText;
    }

    public void setSubmitButtonText(String submitButtonText) {
        this.submitButtonText = submitButtonText;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public List<RegistrationSourceTarget> getRegistrationSourceTargets() {
        return registrationSourceTargets;
    }

    public void setRegistrationSourceTargets(List<RegistrationSourceTarget> registrationSourceTargets) {
        this.registrationSourceTargets = registrationSourceTargets;
    }

    public List<String> getAdditionalDataPrompts() {
        return additionalDataPrompts;
    }

    public void setAdditionalDataPrompts(List<String> additionalDataPrompts) {
        this.additionalDataPrompts = additionalDataPrompts;
    }

    @JsonIgnore
    public List<RegistrationSourceTarget> getRegistrationSourceTargets(String type) {
        if (type == null) return null;
        Map<String, List<RegistrationSourceTarget>> registrationSourceTargetsGroupedByType = getRegistrationSourceTargetsGroupedByType();
        return registrationSourceTargetsGroupedByType == null ? null : registrationSourceTargetsGroupedByType.get(type);
    }

    @JsonIgnore
    public Map<String, List<RegistrationSourceTarget>> getRegistrationSourceTargetsGroupedByType() {
        if(registrationSourceTargetsGroupedByType != null) return registrationSourceTargetsGroupedByType;
        if (registrationSourceTargets == null) return null;
        registrationSourceTargetsGroupedByType = registrationSourceTargets.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(RegistrationSourceTarget::getType));
        return registrationSourceTargetsGroupedByType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegSource{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", subtitle='").append(subtitle).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", submitButtonText='").append(submitButtonText).append('\'');
        sb.append(", backgroundUrl='").append(backgroundUrl).append('\'');
        sb.append(", registrationSourceTargets=").append(registrationSourceTargets).append('\'');
        sb.append(", additionalDataPrompts='").append(additionalDataPrompts);
        sb.append('}');
        return sb.toString();
    }
}
