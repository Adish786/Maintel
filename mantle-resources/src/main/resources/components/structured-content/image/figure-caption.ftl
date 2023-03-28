<@component tag="figcaption" class="figure-article-caption">
    <#if (model.caption)?has_content>
    	<#if (model.showCaptionIcon)!true>
			<#-- The Icon-Camera SVG will be provided by the verticals. This svg doesn't live in Mantle -->
			<@svg name="icon-camera" classes="figure-article-caption-icon" />
	    </#if>
        <span class="figure-article-caption-text">${model.caption?ensure_ends_with(".")}</span>
    </#if>
    <#if (model.owner)?has_content>
    	<span class="figure-article-caption-owner">${model.owner}</span>
    </#if>
</@component>