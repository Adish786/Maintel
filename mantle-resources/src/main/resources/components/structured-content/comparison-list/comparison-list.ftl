<@component>
    <#if model.heading?has_content>
        <h3 class="${generateReferenceIdClasses('__comparison-list-heading')}">${model.heading}</h3>
    </#if>
    <@renderComparisonList model.headingA, model, model.listA />
    <@renderComparisonList model.headingB, model, model.listB />
    <#if model.summaryText?has_content>
        <div class="${generateReferenceIdClasses('__summary-text')}">${model.summaryText}</div>
    </#if>
</@component>

<#macro renderComparisonList heading, model, listItems=[]>
    <div class="${generateReferenceIdClasses('__wrapper')}">
		<#if heading??>
			<${model.headingTag!"span"} class="${generateReferenceIdClasses('__heading')}">${heading}</${model.headingTag!"span"}>
		</#if>

		<${model.listTag} class="${generateReferenceIdClasses('__list')}">
		    <#list listItems as item>
			    <li>${item}</li>
		    </#list>
		</${model.listTag}>
	</div>
</#macro>
