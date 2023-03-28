<#if requestContext.requestUrl?ends_with("pattern-library")>
    <@component>
        <pre class="markdown" data-options="replace">
${model.brandGuidelines?trim}
        </pre>
    </@component>
<#else>
    <@component>
        <@location name="default" tag="" overrideManifest=model.pl.manifest models={'locations': model.pl.locations} />
    </@component>
</#if>
