package com.about.mantle.cache.clearance;

import java.util.List;
import java.util.Set;

public class SecondaryCacheClearanceContainer {

    private List<CacheClearanceRequest> primaryRequests;
    private Set<String> secondaries;

    public SecondaryCacheClearanceContainer (List<CacheClearanceRequest> primaryRequests, Set<String> secondaries){
        this.primaryRequests = primaryRequests;
        this.secondaries = secondaries;
    }

    public List<CacheClearanceRequest> getPrimaryRequests() {
        return primaryRequests;
    }

    public Set<String> getSecondaries() {
        return secondaries;
    }
}
