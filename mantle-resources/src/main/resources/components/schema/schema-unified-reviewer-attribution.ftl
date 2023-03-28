<#include "schema-macros.ftl" />
<#compress>
	<#escape x as x?json_string>
	<#if (model.reviewers)?has_content>
        ,"reviewedBy": [
            <#list model.reviewers>
                <#items as reviewer>
                    <#if (reviewer)?has_content>
                      <@rawAuthor authorData=(reviewer.author)!{} />
                    <#sep>,</#sep>
                    </#if>
                </#items>
            </#list>
        ]
        <#if (model.lastReviewedDate)?has_content >
            ,"lastReviewed": "${model.lastReviewedDate}"
        </#if>
    </#if>
	</#escape>
</#compress>