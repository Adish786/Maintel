<#assign iframeID=manifest.instanceId + '-iframe' />
<#-- we convert the options map to json and base64encode so that we can use it as a query parameter -->
<#assign encodedOptions=utils.base64encode(utils.toJSONString(model.options!{})) />
<@component>
<#compress>
<iframe id="${iframeID}" <#t>
        class="mntl-sc-block-embed--iframe lazyload" <#t>
        allowfullscreen="false" <#t>
        frameborder="0" <#t>
        sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" <#t>
        title="${(model.title)!''}" <#t>
        data-expand="${model.expand!300}" <#t>
        data-src="/embed?url=${(model.url)?url}&id=${iframeID?url}&options=${encodedOptions?url}&docId=${requestContext.urlData.docId?c}"><#t>
</iframe>
</#compress>
</@component>
