package com.about.mantle.model.services.auth;

import static org.apache.commons.lang3.StringUtils.stripToNull;

import com.about.globe.core.http.AccountInfo;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.mantle.spring.interceptor.CookieInterceptor;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DDMAccountAuthService {

    private final String domain;
    private final ObjectMapper objectMapper;
    private final Auth0Verifier auth0Verifier;

    public DDMAccountAuthService(String domain, Auth0Verifier auth0Verifier){
        this.domain = domain;
        this.objectMapper = new StandardObjectMapperProvider().getContext(null);
        this.auth0Verifier = auth0Verifier;
    }

    public AccountInfo getAccountInfoFromRequest(HttpServletRequest request)  {
        DateTime now = DateTime.now();
        String type = extractParameter("type", request.getParameterMap());
        String hashId= extractParameter("hashId", request.getParameterMap());
        String idToken= extractParameter("idToken", request.getParameterMap()); //JWT Token

        if(idToken != null) {
            DecodedJWT decodedJWT = null;
            try {
                decodedJWT = JWT.decode(idToken);
            } catch (JWTDecodeException ex) {
                return null;
            }

            if(!auth0Verifier.verifyDecodedJWT(decodedJWT, idToken)){
                return null;
            }

            Map<String, Claim> idInfo = decodedJWT.getClaims();

            if(idInfo.containsKey(AccountInfo.KEY_TO_HASH_ID)){
                hashId = idInfo.get(AccountInfo.KEY_TO_HASH_ID).asString();
            }
            return new AccountInfo(type, now, now, hashId, decodedJWT.getToken(), idInfo);
        }

        return null;
    }

    /**
     * creates cookies involved with auth0
     *
     * Stops creating cookies if it can't write AccountInfo as a JSON String, relying on caller to handle it
     *
     * @param accountInfo
     * @throws JsonProcessingException
     */
    public List<Cookie> createCookiesForDDMAccountAuth(AccountInfo accountInfo) throws JsonProcessingException {
        List<Cookie> cookies = new ArrayList<>();

        Cookie ddm = makeCookie(CookieInterceptor.DDM_ACCOUNT_ID_COOKIE, Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(accountInfo).getBytes()));
        ddm.setSecure(true);

        cookies.add(ddm);
        cookies.add(makeCookie(CookieInterceptor.HASH_ID_COOKIE, accountInfo.getHashId()));

        return cookies;
    }

    public List<Cookie> createCookiesForDeletion (){
        List<Cookie> cookies = new ArrayList<>();

        Cookie ddm = makeCookie(CookieInterceptor.DDM_ACCOUNT_ID_COOKIE, "");
        ddm.setMaxAge(0);

        Cookie mdp = makeCookie(CookieInterceptor.MDP_ACCOUNT_ID_COOKIE, "");
        mdp.setMaxAge(0);

        Cookie hid = makeCookie(CookieInterceptor.HASH_ID_COOKIE, "");
        hid.setMaxAge(0);

        cookies.add(ddm);
        cookies.add(hid);
        cookies.add(mdp);
        return cookies;
    }

    private Cookie makeCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(this.domain);
        cookie.setMaxAge(CookieInterceptor.ONE_YEAR_IN_SECONDS);
        cookie.setPath("/");
        return cookie;
    }

    private String extractParameter(String parameter, Map<String, String[]> parametersMap) {
        String[] parameterValues = parametersMap.get(parameter);
        return ArrayUtils.isEmpty(parameterValues) ? null : stripToNull(parameterValues[0]);
    }
}
