package com.about.mantle.model.tasks;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.GeoData;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.services.ConsentBannerService;
import com.about.mantle.model.services.ConsentBannerService.Template;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Intended to store all compliance-related tasks, eg CCPA, GDPR, Nevada's SB 220, etc.
 */
@Tasks
public class ComplianceTask {

    private static Logger log = LoggerFactory.getLogger(ComplianceTask.class);

    private static Set<String> blacklistedPrivacyRequestParameters = buildBlacklistedPrivacyRequestParameters();

    private final AmazonSimpleEmailService sesClient;
    private final String privacyReqSenderEmail;
    private final String privacyReqRecipientEmail;
    private final String domain;
    private final ConsentBannerService consentBannerService;

    public ComplianceTask(AmazonSimpleEmailService sesClient, String privacyReqSenderEmail,
            String privacyReqRecipientEmail, String domain, ConsentBannerService consentBannerService) {
        this.sesClient = sesClient;
        this.privacyReqSenderEmail = privacyReqSenderEmail;
        this.privacyReqRecipientEmail = privacyReqRecipientEmail;
        this.domain = domain;
        this.consentBannerService = consentBannerService;
    }

    /**
     * Returns `true` if the request is covered by CCPA laws. Otherwise, false.
     *
     * @param reqCtx
     * @return
     */
    @Task(name = "isCcpaApplicableRequest")
    public boolean isCcpaApplicableRequest(@RequestContextTaskParameter RequestContext reqCtx) {
        // This is currently a best effort to determine if CCPA is applicable.  CCPA applies to residents of
        // California, but we can not determine that just from the request.  We are instead using Fastly's
        // to determine if a request originated from California, and marking it as CCPA if so.

        GeoData geoData = reqCtx != null ? reqCtx.getGeoData() : null;
        String usStateCode = (geoData != null && "US".equals(geoData.getIsoCode())) ?
                geoData.getSubdivisionCode() : null;

        return "CA".equals(usStateCode);
    }

    @Task(name = "privacyRequestSubmission")
    public boolean privacyRequestSubmission(HttpServletRequest request, HttpServletResponse response) {

        boolean answer = true;

        String requestedAction = extractRequestType(request);

        String body = privacyRequestEmailBody(domain, request.getParameterMap());
        String subject = "Privacy Request: " + requestedAction + " for vertical " + domain.toString();
        try {
            SendEmailRequest mailReq = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(privacyReqRecipientEmail))
                    .withSource(privacyReqSenderEmail)
                    .withMessage(new Message()
                            .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(body)))
                            .withSubject(new Content().withCharset("UTF-8").withData(subject)));
            sesClient.sendEmail(mailReq);
        } catch (Exception e) {
            log.error("Couldn't send privacy request email.  Body was: " + body, e);
            answer = false;
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return answer;
    }

    /**
     * Encapsulates the rules for showing the banner.
     *
     * @param reqCtx
     * @return
     */
    @Task(name = "showConsentBanner")
    public boolean showConsentBanner(@RequestContextTaskParameter RequestContext reqCtx, 
        @TaskParameter(name = "showAlertNotice") Boolean showAlertNotice) {
        return isConsentRequired(reqCtx) && !isBannerDismissed(reqCtx) && showAlertNotice;
    }

    /**
     *
     * @param reqCtx
     * @return
     */
    @Task(name = "oneTrustTemplateName")
    public String expectedTemplate(@RequestContextTaskParameter RequestContext reqCtx) {
        Template template = ConsentBannerService.getExpectedTemplate(reqCtx.getGeoData());
        return (template != null) ? template.toString().toLowerCase() : null;
    }

    /**
     * Predicts when consent is required based on geolocation. Returns true if user is a geolocation
     * where consent is required.
     *
     * @param reqCtx
     * @return
     */
    @Task(name = "isConsentRequired")
    public boolean isConsentRequired(@RequestContextTaskParameter RequestContext reqCtx) {
        return ConsentBannerService.getExpectedTemplate(reqCtx.getGeoData()) != null;
    }

    /**
     * Returns true if the cookie "OptanonAlertBoxClosed" has already been set, indicating that
     * the user has previously dismissed the CMP banner
     *
     * @param reqCtx
     * @return
    */
    @Task(name = "isBannerDismissed")
    public boolean isBannerDismissed(@RequestContextTaskParameter RequestContext reqCtx) {
        boolean answer = false;

        if (reqCtx != null) {
            boolean hasConsentCookie = reqCtx.getCookie("OptanonAlertBoxClosed") != null;
            if (hasConsentCookie) {
                answer = true;
            }
        }

        return answer;
    }

    /**
     * Get the OneTrust domain data used for rendering the consent banner component.
     *
     * @param reqCtx
     * @return
     */
    @Task(name = "oneTrustDomainData")
    public Map<String, Object> getOneTrustDomainData(@RequestContextTaskParameter RequestContext reqCtx) {
        return this.consentBannerService.getDomainData(reqCtx.getGeoData());
    }

    private String extractRequestType(HttpServletRequest request) {
        String[] actionArray = request.getParameterValues("Request Type");
        if (actionArray == null || actionArray.length != 1) {
            throw new GlobeException("Cannot determine action for privacy request.  Got action: " + actionArray);
        }

        return actionArray[0];
    }

    private String privacyRequestEmailBody(String domain, Map<String, String[]> paramMap) {

        StringBuilder sb = new StringBuilder("Vertical: ");
        sb.append(domain);
        sb.append("\n");

        Set<String> keys = paramMap.keySet();

        for (String key : keys) {

            if (!blacklistedPrivacyRequestParameters.contains(key)) {

                sb.append(key);
                sb.append(": ");
                sb.append(StringUtils.join(paramMap.get(key), ','));
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private static Set<String> buildBlacklistedPrivacyRequestParameters() {
        return new HashSet<>(Arrays.asList("CSRFToken"));
    }


    // TODO pull GDPR logic out of xml/spel and into here for consistency.
}
