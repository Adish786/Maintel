<#compress>
	<#escape x as x?json_string>
	    <#assign liveBlogPost = model.document.liveBlogPost />
        <#if liveBlogPost?has_content>
        {
            "@context": "http://schema.org"
            ,"@type": "LiveBlogPosting"
            ,"headline": "${(model.document.heading!model.document.title)}"
            ,"url": "${model.document.url}"

            <#if (model.datePublished)?has_content>
                ,"datePublished": "${model.datePublished}"
            </#if>

            <#if (liveBlogPost.coverageStartDate)?has_content>
                ,"coverageStartTime": "${liveBlogPost.coverageStartDate.withZone(DateTimeZone.forID("America/New_York"))}"
            </#if>

            <#if (model.dateModified)?has_content>
                ,"dateModified": "${model.dateModified}"
            </#if>

            <#if (liveBlogPost.coverageEndDate)?has_content>
                ,"coverageEndTime": "${liveBlogPost.coverageEndDate.withZone(DateTimeZone.forID("America/New_York"))}"
            </#if>

            <#if (model.document.summary.description)?has_content>
                ,"description": "${model.document.summary.description}"
            </#if>

            <#if (model.document.guestAuthor.link.text)?has_content>
                ,"author": [<@rawAuthor guestAuthorData=(model.document.guestAuthor) />]
            <#elseif model.authorAttributions?has_content>
                ,<@listAuthors attributionModels=(model.authorAttributions)!{} role="author" />
            </#if>

            <@location name="extraProperties" tag="" />

            <@location name="mainEntityOfPage" tag=""/>

            <#if (model.document.liveBlogPost.articleBodyUpdatedSummaries)?has_content>
                ,"liveBlogUpdate": [
                    <#list model.document.liveBlogPost.articleBodyUpdatedSummaries.list as articleBodyUpdatedSummary>
                        {
                            "@type": "BlogPosting"
                            ,"headline": "${(model.document.heading!model.document.title)}"
                            ,"articleBody": "${articleBodyUpdatedSummary.updatedSummary}"
                            ,"dateModified": "${articleBodyUpdatedSummary.updatedSummaryDate.withZone(DateTimeZone.forID("America/New_York"))}"
                            <#if (model.document.guestAuthor.link.text)?has_content>
                                ,"author": [<@rawAuthor guestAuthorData=(model.document.guestAuthor) />]
                            <#elseif model.authorAttributions?has_content>
                                ,<@listAuthors attributionModels=(model.authorAttributions)!{} role="author" />
                            </#if>

                            <@location name="extraProperties" tag="" />
                            <@location name="mainEntityOfPage" tag=""/>
                        }
                        <#sep>,</#sep><#lt>
                    </#list>
                ]
            </#if>
        }
        </#if>
	</#escape>
</#compress>

