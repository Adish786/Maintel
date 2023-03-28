package com.about.mantle.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used for logging what would be blocked by new safelistinterceptor changes
 * Temp for GLBE-8768
 */
public class SafeListParamaterFailedLogger {

    private Map <String, List<String>> pathAndParametersMap;

    public SafeListParamaterFailedLogger () {
        pathAndParametersMap = new ConcurrentHashMap<>();
    }

    public void log (String path, String parameter){

        if(!pathAndParametersMap.containsKey(path) && pathAndParametersMap.size() < 10001){
            pathAndParametersMap.put(path, Collections.synchronizedList(new ArrayList<String>()));
        }

        if(pathAndParametersMap.containsKey(path) &&
                !pathAndParametersMap.get(path).contains(parameter) && pathAndParametersMap.get(path).size() < 51){
            pathAndParametersMap.get(path).add(parameter);
        }
    }

    public Map <String, List<String>> getMapForPrinting(){
        return pathAndParametersMap;
    }
}
