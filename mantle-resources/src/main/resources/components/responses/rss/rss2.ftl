<@compress single_line=true>
    <#assign datetimeFormat="yyyy-MM-dd'T'HH:mm:ss.SSS"/>
    <#assign rssDateFormat="EEE, dd MMMM yyyy HH:mm:ss z"/>
    <#assign trackingParams>?utm_medium=rss<#if model.listName??>&utm_campaign=${model.listName}</#if></#assign>
    <?xml version="1.0"?>
    <rss version="2.0">
        <channel>
            <title>${model.rss2.title}</title>
            <link>${model.rss2.link}</link>
            <#if model.rss2.description??><description>${model.rss2.description?xml}</description></#if>
            <#if model.rss2.lastBuildDate??><lastBuildDate>${model.rss2.lastBuildDate?datetime(datetimeFormat)?string(rssDateFormat)}</lastBuildDate></#if>
            <#if model.rss2.items??>
                <#list model.rss2.items as item>
                    <item>
                        <#if item.title??><title>${item.title?xml}</title></#if>
                        <#if item.description??><description>${item.description?xml}</description></#if>
                        <#if item.link??>
                            <link>${item.link}${trackingParams?xml}</link>
                            <guid isPermaLink="true">${item.link?xml}</guid>
                        </#if>
                        <#if item.pubDate??><pubDate>${item.pubDate?datetime(datetimeFormat)?string(rssDateFormat)}</pubDate></#if>
                        <#if item.enclosure??>
                            <enclosure url="${item.enclosure.url}" length="${item.enclosure.length!'0'}" type="${item.enclosure.type}"/>
                        </#if>
                    </item>
                </#list>
            </#if>
        </channel>
    </rss>
</@compress>