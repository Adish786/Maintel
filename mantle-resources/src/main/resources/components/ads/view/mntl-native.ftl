<#if model.adHeight?has_content>
    <#assign adHeightCSSValue = model.adHeight + "px" />
<#else>
    <#assign adHeightCSSValue = "auto" />
</#if>
<@component style="--native-ad-height: ${adHeightCSSValue}" class="${model.labeled?then('mntl-native--labeled', '')}">
    <@location name="content" tag="" />
</@component>