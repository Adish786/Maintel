package com.about.mantle.app.util;

import java.net.URI;

import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.url.VerticalUrlData;

/**
 * Consolidates logic and convenience functions regarding AMP URLs.
 * AMP is no longer supported and this class will be deleted once it's no longer referenced
 * by any verticals.
 */
@Deprecated
public class AmpUtils {

    private static final String AMP = "/amp/";

    private AmpUtils() {}

    @Deprecated
    public static boolean isAmpUrl(URI uri) {
        return (uri.getPath() != null) ? uri.getPath().startsWith(AMP) : false;
    }

    @Deprecated
    public static String getNonAmpUrl(String url) {
        return url.replace(AMP, "/");
    }

    @Deprecated
    public static String getNonAmpPath(URI uri) {
        return (uri.getPath() != null) ? uri.getPath().replace(AMP, "/") : uri.toString();
    }

    @Deprecated
    public static boolean isAmp(RequestContext requestContext) {
        return false;
    }

}