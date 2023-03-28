package com.about.mantle.model.tasks;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.shared_services.registration.RegistrationService;
import com.about.mantle.model.shared_services.registration.request.RegistrationRequestBody;
import com.about.mantle.model.shared_services.registration.request.RegistrationRequestBody.RegistrationRequestBodyData;
import com.about.mantle.model.shared_services.registration.request.actions.Account;
import com.about.mantle.model.shared_services.registration.request.actions.AccountAction;
import com.about.mantle.model.shared_services.registration.request.actions.Action;
import com.about.mantle.model.shared_services.registration.request.actions.OptInAction;
import com.about.mantle.model.shared_services.registration.request.brands.parents.Child;
import com.about.mantle.model.shared_services.registration.request.brands.parents.Parents;
import com.about.mantle.model.shared_services.registration.response.RegistrationResponse.RegistrationResponseData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Tasks
public class RegistrationTask {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationTask.class);
    private final RegistrationService registrationService;
    private final String legacyMeredithBrand;

    public RegistrationTask(RegistrationService registrationService, String legacyMeredithBrand) {
        this.registrationService = registrationService;
        this.legacyMeredithBrand = legacyMeredithBrand;
    }

    /**
     * @param email Email address
     * @param regSourceId Registration source Id
     * @param newsletterObjectIds Newsletter object IDs separated by comma
     * @param objectorObjectIds Objector object IDs separated by comma
     * @return Response data that has hashId (on successful registration) & emailReputation (safe/non-safe email)
     */
    @Task(name="accountRegistration")
    public RegistrationResponseData postAccountRegistration(@TaskParameter(name="email") String email, @TaskParameter(name="regSourceId") String regSourceId,
                                                            @TaskParameter(name="newsletterObjectIds") String newsletterObjectIds, @TaskParameter(name="objectorObjectIds") String objectorObjectIds,
                                                            @TaskParameter(name="birthDate") String birthDate) {

        validateTaskParameters(email, regSourceId, newsletterObjectIds, objectorObjectIds);

        List<Action> actions = new ArrayList<>();

        if(newsletterObjectIds != null) {
            addOptInActions(newsletterObjectIds, "newsletter", actions);
        }

        if(objectorObjectIds != null) {
            addOptInActions(objectorObjectIds, "objector", actions);
        }

        if (!StringUtils.isBlank(birthDate)) {
            // if a birthDate is provided we always add it as part of brandData for Parents
            Account newAccount = new Account(new Parents(List.of(new Child(birthDate))));
            actions.add(new AccountAction(newAccount));
        }

        if (legacyMeredithBrand == null) {
            throw new GlobeException("Unable to use post account registration for newsletters if legacy Meredith brand not set");
        }

        RegistrationRequestBodyData registrationRequestBodyData = new RegistrationRequestBodyData(email, regSourceId, legacyMeredithBrand, actions, birthDate);

        return registrationService.postRegistration(new RegistrationRequestBody(registrationRequestBodyData));
    }

    /**
     * @param actionObjectIds A String of actions object Ids e.g. newsletterObjectorIds, objectorObjectIds
     * @param actionType Action type e.g. newsletter, objector
     * @param actions List of {@link Action}s
     */
    private void addOptInActions(String actionObjectIds, String actionType, List<Action> actions) {
        String[] actionObjectIdArray = actionObjectIds.split(",");

        if(actionObjectIdArray != null) {
            for(int i=0; i < actionObjectIdArray.length; i++) {
                Action action = new OptInAction(actionType, true, actionObjectIdArray[i]);
                actions.add(action);
            }
        }
    }

    private void validateTaskParameters(String email, String regSourceId, String newsletterObjectIds, String objectorObjectIds) {
        if(email == null) {
            throw new GlobeInvalidTaskParameterException("Email is required for account registration but is null");
        }

        if(regSourceId == null) {
            throw new GlobeInvalidTaskParameterException("Registration Source ID is required for account registration but is null");
        }

        if(newsletterObjectIds == null && objectorObjectIds == null) {
            throw new GlobeInvalidTaskParameterException("Either newsletterObjectIds or objectorObjectIds is required for account registration. Both cannot be null");
        }
    }

    @Task(name="accountRegistration")
    public RegistrationResponseData postAccountRegistrationWithObjectors(@TaskParameter(name="email") String email, @TaskParameter(name="regSourceId") String regSourceId,
                                                            @TaskParameter(name="objectorObjectIds") String objectorObjectIds) {
        return postAccountRegistration(email, regSourceId, null, objectorObjectIds, null);
    }

    @Task(name="accountRegistration")
    public RegistrationResponseData postAccountRegistrationWithNewsletters(@TaskParameter(name="email") String email, @TaskParameter(name="regSourceId") String regSourceId,
                                                            @TaskParameter(name="newsletterObjectIds") String newsletterObjectIds) {
        return postAccountRegistration(email, regSourceId, newsletterObjectIds, null, null);
    }

    @Task(name="accountRegistration")
    public RegistrationResponseData postAccountRegistrationWithNewslettersAndObjectors(@TaskParameter(name="email") String email, @TaskParameter(name="regSourceId") String regSourceId,
                                                            @TaskParameter(name="newsletterObjectIds") String newsletterObjectIds, @TaskParameter(name="objectorObjectIds") String objectorObjectIds) {
        return postAccountRegistration(email, regSourceId, newsletterObjectIds, objectorObjectIds, null);
    }

    @Task(name="accountRegistration")
    public RegistrationResponseData postAccountRegistrationWithNewslettersAndParentsDob(@TaskParameter(name="email") String email, @TaskParameter(name="regSourceId") String regSourceId,
                                                                           @TaskParameter(name="newsletterObjectIds") String newsletterObjectIds, @TaskParameter(name="birthDate") String birthDate) {
        return postAccountRegistration(email, regSourceId, newsletterObjectIds, null, birthDate);
    }
}
