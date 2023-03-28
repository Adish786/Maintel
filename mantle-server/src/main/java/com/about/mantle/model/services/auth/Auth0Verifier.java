package com.about.mantle.model.services.auth;

import java.security.interfaces.RSAPublicKey;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/**
 * Used to verify Auth0 tokens used throughout globe
 */
public class Auth0Verifier {
    private static final Logger logger = LoggerFactory.getLogger(Auth0Verifier.class);
    private final String authTenant;
    private final String tokenIssuer;

    public Auth0Verifier (String authTenant) {
        this.authTenant = authTenant;
        this.tokenIssuer = getTokenIssuer(authTenant);
    }

    /**
     * Verifies the JWT token used for logging in
     *
     * @param decodedJWT - the decoded idtoken
     * @param idToken - the idtoken that was returned from Auth0
     * @return true if the decodedJWT token was signed by Auth0, false otherwise
     */
    public boolean verifyDecodedJWT(DecodedJWT decodedJWT, String idToken){

        if(authTenant == null){
            throw new GlobeException(MantleExternalConfigKeys.AUTH0_DOMAIN +" was not set");
        }
        
        try {
            JwkProvider provider = new UrlJwkProvider(authTenant);

            Jwk jwk = provider.get(decodedJWT.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(tokenIssuer)
                    .build();

            verifier.verify(idToken);
        } catch (JWTVerificationException ex){
            logger.error("Failed to Verify JWT Token", ex);
            return false;
        } catch (JwkException ex) {
            logger.error("JWK failure", ex);
            return false;
        }
        return true;
    }

    /**
     * For verification purposes the issuer needs to be an exact match with what's in the token.
     * Trying to align with how the auth0-spa-js library defines the issuer as a function of domain.
     * https://github.com/auth0/auth0-spa-js/blob/master/src/Auth0Client.ts#L147
     * @param domain
     * @return
     */
    private static String getTokenIssuer(String domain) {
        if (domain == null) return null;
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.startsWith(domain, "https://")) {
            sb.append("https://");
        }
        sb.append(domain);
        if (!StringUtils.endsWith(domain, "/")) {
            sb.append("/");
        }
        return sb.toString();
    }
}
