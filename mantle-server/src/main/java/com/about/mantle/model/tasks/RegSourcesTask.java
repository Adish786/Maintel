package com.about.mantle.model.tasks;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.shared_services.regsources.RegSourcesService;
import com.about.mantle.model.shared_services.regsources.response.RegSource;
import com.about.mantle.model.shared_services.regsources.response.RegistrationSourceTarget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Tasks
public class RegSourcesTask {
    private static final String FAMILY_ADDITIONAL_DATA_PROMPT = "family";
    private RegSourcesService regSourcesService;

    public RegSourcesTask(RegSourcesService regSourcesService) {
        this.regSourcesService = regSourcesService;
    }

    /**
     * Gets registration source data by id.
     * Currently setup to retrieve data for newsletters & objectors
     * {@link com.about.mantle.model.shared_services.regsources.response.RegistrationSourceTarget}
     */
    @Task(name="regSourceById")
    public RegSource getRegSourceById(@TaskParameter(name="regSourceId") String regSourceId) {
        RegSource regSource = regSourcesService.getRegSourceById(regSourceId);
        List<RegistrationSourceTarget> newsletterRegistrationSourceTargets = getSortedNewsletterRegistrationSourceTargets(regSource);

        List<RegistrationSourceTarget> registrationSourceTargetList = new ArrayList<>();
        registrationSourceTargetList.addAll(newsletterRegistrationSourceTargets);
        addOtherRegistrationSourceTargetsExcluding(registrationSourceTargetList, "newsletter", regSource);

        regSource.setRegistrationSourceTargets(registrationSourceTargetList);
        return regSource;
    }

    /**
     *
     * @param regSource
     * @return
     */
    @Task(name="shouldShowDobOnNewsletterSignup")
    public boolean getRegSourceById(@TaskParameter(name="regSource") RegSource regSource) {
        return regSource != null
            && regSource.getAdditionalDataPrompts() != null
            && regSource.getAdditionalDataPrompts().contains(FAMILY_ADDITIONAL_DATA_PROMPT);
    }

    /**
     * @param regSource Registration Source
     * @return SortedNewsletterRegistrationSourceTargets on sequence number
     */
    private List<RegistrationSourceTarget> getSortedNewsletterRegistrationSourceTargets(RegSource regSource) {
        List<RegistrationSourceTarget> newsletterRegistrationSourceTargets = regSource.getRegistrationSourceTargets("newsletter");

        if(newsletterRegistrationSourceTargets != null) {
            newsletterRegistrationSourceTargets = new ArrayList<>(newsletterRegistrationSourceTargets);
            newsletterRegistrationSourceTargets.sort(Comparator.comparingInt(RegistrationSourceTarget::getSequenceNum));
        }
        return newsletterRegistrationSourceTargets;
    }

    /**
     * @param registrationSourceTargetList List to add all registrationSourceTargets
     * @param excludedRegistrationSourceTargetType Excluded registration source type
     * @param regSource Registration Source
     */
    private void addOtherRegistrationSourceTargetsExcluding(List<RegistrationSourceTarget> registrationSourceTargetList, String excludedRegistrationSourceTargetType, RegSource regSource) {
        for(Map.Entry<String, List<RegistrationSourceTarget>> registrationSourceTargets : regSource.getRegistrationSourceTargetsGroupedByType().entrySet()) {
            if(registrationSourceTargets != null && excludedRegistrationSourceTargetType != null
                    && !excludedRegistrationSourceTargetType.equals(registrationSourceTargets.getKey())
                    && registrationSourceTargets.getValue() != null) {
                registrationSourceTargetList.addAll(registrationSourceTargets.getValue());
            }
        }
    }

}
