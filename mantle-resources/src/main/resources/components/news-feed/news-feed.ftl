<@component>
    <h2 class="news-feed__title">${model.title}</h2>
    <@location name="feed" />
    <#if model.seeMoreLink?has_content>
        <@a class="news-feed__see-more-link" href="${model.seeMoreLink}">${model.seeMoreLinkText}</@a>
    </#if>
</@component>