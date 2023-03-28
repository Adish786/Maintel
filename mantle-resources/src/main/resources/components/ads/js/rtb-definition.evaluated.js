window.Mntl = window.Mntl || {};
Mntl.RTB = Mntl.RTB || {};
<#if (model.taxonomyStampValues)?has_content>
Mntl.RTB.setTaxonomyStampValues(${utils.toJSONString(model.taxonomyStampValues)});
</#if>
<#if model.indexFirstPartyData?has_content>
Mntl.RTB.indexFirstPartyData = '${model.indexFirstPartyData}';
</#if>
Mntl.RTB.setTimeoutLength(<#if model.rtbTimeout?is_number>${model.rtbTimeout?c}<#elseif model.rtbTimeout?is_enumerable>[<#list model.rtbTimeout as timeout>${timeout?c}<#sep>,</#list>]<#else>${model.rtbTimeout}</#if>);
<#if (model.amazonConfigs)?has_content>
Mntl.RTB.Plugins.amazon.amazonConfigs = ${utils.toJSONString(model.amazonConfigs)};
</#if>
<#-- Set RTB bidder configs -->
Mntl.RTB.initVideoBidders([{ type: 'amazon', id: '${model.rtbConfigIds['amazon']}' }<#if (model.prebidConfig['preroll'])?has_content>, { type: 'prebid', id: 'true' } </#if>]);
Mntl.RTB.initDisplayAndOutstreamBidders([<#list model.rtbConfigIds?keys as key>{ type: "${key}", id: <#if model.rtbConfigIds[key]?has_content>'${model.rtbConfigIds[key]?js_string}'<#else>null</#if>}<#sep>,</#list>]);
<#if model.trackEvents == "newEvents">
Mntl.RTBTracking.init();
</#if>
<#if (model.prebidConfig)?has_content>
Mntl.RTB.Plugins.prebid.setConfig(${utils.toJSONString(model.prebidConfig)});
</#if>
<#if (model.prebidPriceGranularity)?has_content>
Mntl.RTB.Plugins.prebid.setPriceGranularity(${utils.toJSONString(model.prebidPriceGranularity)});
</#if>
Mntl.RTB.setLatencyBuffer(${model.latencyBuffer?c});
