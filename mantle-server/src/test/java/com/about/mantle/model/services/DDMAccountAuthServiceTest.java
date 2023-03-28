package com.about.mantle.model.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import com.about.globe.core.http.AccountInfo;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.mantle.model.services.auth.Auth0Verifier;
import com.about.mantle.model.services.auth.DDMAccountAuthService;
import com.about.mantle.spring.interceptor.CookieInterceptor;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DDMAccountAuthServiceTest {

    private DDMAccountAuthService ddmAccountAuthService;
    private ObjectMapper objectMapper;

    private static final String domain = "testing.com/";
    private static final String type = "social";
    private static final String hashId = "36012c6b6142c5c5fa7ddeb7450ce1c5e355e313";
    private static final String idToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1FSkNNVVl4UlVOQ1JERXdRVFpCTURkQ1FqRXdNekpFUmpJM1F6UTVSVFUyT1VSQ05VRXlSUSJ9.eyJodHRwOi8vbWVyZWRpdGgvSGFzaElkIjoiMzYwMTJjNmI2MTQyYzVjNWZhN2RkZWI3NDUwY2UxYzVlMzU1ZTMxMyIsImh0dHA6Ly9tZXJlZGl0aC9pc1JlZ2lzdHJhdGlvbiI6ImZhbHNlIiwiZ2l2ZW5fbmFtZSI6IllpbmciLCJmYW1pbHlfbmFtZSI6IlpoYW5nIiwibmlja25hbWUiOiJkb2RvemhhbmcyMSIsIm5hbWUiOiJZaW5nIFpoYW5nIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BRmRadWNxMUFRclJGQ3UxR3RqVTZ3aFV2ODF1RXFyckNFOEF2bzI3TVliRzM5MD1zOTYtYyIsImxvY2FsZSI6ImVuIiwidXBkYXRlZF9hdCI6IjIwMjItMDctMTlUMTQ6Mzk6MTMuMzc0WiIsImVtYWlsIjoiZG9kb3poYW5nMjFAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImlzcyI6Imh0dHBzOi8vYmhnLWRldi5hdXRoMC5jb20vIiwic3ViIjoiZ29vZ2xlLW9hdXRoMnwxMDA5MDQ4NzkwMTM2NDUwNTk2MDAiLCJhdWQiOiI1djJ3N0xZZmd0djVYZlU3ZHRBdVl2OW5zUDFzRFMxcSIsImlhdCI6MTY1ODI0MTU1NSwiZXhwIjoxNjU4Mjc3NTU1LCJub25jZSI6IllrNVpORE5yTGpsdVJVZGplRjg1UzJjMmZrMDBkamxETkRSc1JXMXBkekZHYWxvMGREa3dNakUxZFE9PSJ9.ORwPseHI_7IrrXEuyUEYd9eEYZzzqaFxE0EsDdk43Im0ecdM-rKo6ui1kpMpWPqC6TnRbEJykZIs2kJuEIBdnbuwdxDZ-97zG1n90inp-ra4l34Hkju3cO581nnkOafrC91hEWgIlKSNIpfdTBcqI1bFkArqDIxagv3CoXNQPxu-zSpNuWnztPVPaL5XJrxEynIbnDsN3l6qVTtnxzzh29LggsYBxNG2Sm10e-qAHfj1p04u55ex4GPQUXaksfcKeYxgNi_xFlUGypcS8PKd95yMqS6DwBuXI24aljHe9RqrSP3SoO-rTV3NsWuV6rLNtb_p0GbsR39Wxxe0d-68FQ";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Auth0Verifier auth0Verifier;

    private Map<String, String []> parameters;

    @Before
    public void before() {
        objectMapper = new StandardObjectMapperProvider().getContext(null);
        parameters = new HashMap<>();
        addParameterToMap(parameters, "type", type);
        addParameterToMap(parameters, "idToken", idToken);
        addParameterToMap(parameters, "hashId", hashId);

        MockitoAnnotations.initMocks(this);
        Mockito.when(request.getParameterMap()).thenReturn(parameters);
        Mockito.when(auth0Verifier.verifyDecodedJWT(any(DecodedJWT.class),anyString())).thenReturn(true);

        ddmAccountAuthService = new DDMAccountAuthService(domain, auth0Verifier);
    }

    @Test
    public void ddmAccountAuthServiceAccountInfoTest(){
        AccountInfo accountInfo = ddmAccountAuthService.getAccountInfoFromRequest(request);
        Assert.assertEquals(accountInfo.getType(), type);
        Assert.assertEquals(accountInfo.getIdToken(), idToken);
        Assert.assertEquals(accountInfo.getHashId(), hashId);

        //Verify it is using idToken hashId instead of post hashId
        addParameterToMap(parameters, "hashId", hashId+"www999");
        accountInfo = ddmAccountAuthService.getAccountInfoFromRequest(request);
        Assert.assertEquals(accountInfo.getType(), type);
        Assert.assertEquals(accountInfo.getIdToken(), idToken);
        Assert.assertEquals(accountInfo.getHashId(), hashId);
    }

    @Test
    public void ddmAccountAuthServiceCookieTest() throws JsonProcessingException {
        AccountInfo accountInfo = ddmAccountAuthService.getAccountInfoFromRequest(request);

        //This line throws the json processing exception
        List<Cookie> cookies = ddmAccountAuthService.createCookiesForDDMAccountAuth(accountInfo);

        Assert.assertEquals(cookies.size(), 2);

        Cookie mdpaCookie = cookies.get(0);

        Assert.assertEquals(mdpaCookie.getMaxAge(), CookieInterceptor.ONE_YEAR_IN_SECONDS);
        Assert.assertEquals(mdpaCookie.getDomain(), domain);
        Assert.assertEquals(mdpaCookie.getPath(), "/");
        Assert.assertEquals(mdpaCookie.getName(), CookieInterceptor.DDM_ACCOUNT_ID_COOKIE);
        Assert.assertEquals(mdpaCookie.getValue(), Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(accountInfo).getBytes()));

        Cookie hidCookie = cookies.get(1);
        Assert.assertEquals(hidCookie.getName(), CookieInterceptor.HASH_ID_COOKIE);
        Assert.assertEquals(hidCookie.getValue(), accountInfo.getHashId());

    }

    private void addParameterToMap (Map<String, String []> parameters, String key, String value){
        String [] toAdd = new String[]{value};
        parameters.put(key, toAdd);
    }
}
