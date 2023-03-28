<@component tag="script" type="application/ld+json">
    <#compress>
        <#escape x as x?json_string>
            [
            {
            "@context": "http://schema.org",

            <@location name="rootSchema" tag="" />
            }
            <#if (manifest.locations['separateEntities']![])?size gt 0>
            ,<@location name="separateEntities" tag="" separator=","/>
            </#if>
            ]
        </#escape>
    </#compress>
</@component>