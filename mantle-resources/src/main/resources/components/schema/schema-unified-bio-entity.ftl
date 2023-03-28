<#include "schema-macros.ftl" />
<#compress>
,"about": {
    "@type": "Person"

    <@personFields name=(model.author.displayName)!""
                   description=processSchemaString((model.author.shortBio)!"")
                   socialPresence=(model.author.socialPresence)!{}
                   bioUrl=(model.author.bioUrl)!"" />

    <#if (model.document.honorificPrefix)?has_content>
        ,"honorificPrefix": "${model.document.honorificPrefix}"
    </#if>
    <#if (model.document.honorificSuffix)?has_content>
        ,"honorificSuffix": "${model.document.honorificSuffix}"
    </#if>
    <#if (model.document.jobTitle)?has_content >
        ,"jobTitle": "${model.document.jobTitle}"
    </#if>
    <#if (model.document.location)?has_content >
        ,"homeLocation": {
            "@type": "Place",
            "Name": "${model.document.location}"
        }
    </#if>
    <#if (model.document.knowsAbout.list)?has_content>
        ,<@authorKnowsAbout knowsAboutData=model.document.knowsAbout.list />
    </#if>
    <#if (model.document.alumniOf.list)?has_content >
        ,<@authorAlumniOf alumniData=model.document.alumniOf.list />
    </#if>
    <#if (model.document.worksFor.list)?has_content >
        ,<@authorWorksFor workData=model.document.worksFor.list />
    </#if>
}
</#compress>