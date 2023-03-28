<#assign resultsCalc = model.document.resultsCalc />
<#assign backupImg = '' />
<#if (model.document.getImageForUsage('PRIMARY').objectId)?has_content>
    <#assign backupImg><@thumborUrl img=model.document.getImageForUsage('PRIMARY') maxWidth=model.document.getImageForUsage('PRIMARY').width!750 /></#assign>
<#elseif model.defaultImageId?has_content>
    <#assign backupImg><@thumborUrl img=model.defaultImageId maxWidth=model.defaultImageWidth!750 /></#assign>
</#if>

<#list model.document.results.list as result>
	<#assign hasResultImage = (result.image.objectId)?has_content>
	<#if hasResultImage>
		<#assign image><@thumborUrl img=result.image maxWidth=model.imageWidth /></#assign>
	<#else>
		<#assign image = backupImg />
	</#if>

	<#if (resultsCalc == "BEST_FIT" || resultsCalc == "PERCENT_CORRECT")>
		<#assign bestFitValues = "" />
		<#list result.dimensions.list as dimension>
			<#assign bestFitValues = bestFitValues + dimension.key + "," + dimension.min + "," + dimension.max />

			<#if dimension_index < result.dimensions?size - 1>
				<#assign bestFitValues = bestFitValues + "." />
			</#if>
		</#list>
	</#if>

    <#assign fbRedirectUri><@absoluteHref href="/facebookShareRedirect.htm" /></#assign>
    <#assign socialDescription><@stripHtml string=result.description!''/></#assign>
    <#assign socialDescription><@truncateText string=socialDescription?url!'' count=500/></#assign>

    <section data-value="${result.id}" data-dimensions="${bestFitValues!''}" class="result" id="result-${result_index}">

        <${model.headingTag!"span"} class="result__heading--document"><span class="p-col">${model.document.heading}</span></${model.headingTag!"span"}>

		<#if resultsCalc == "PERCENT_CORRECT">
			<${model.resultHeadingTag!"span"} class="result__heading">You got: <span class="percent-display">%</span> Correct. ${result.title!''}</${model.resultHeadingTag!"span"}>
		<#elseif result.title?has_content>
			<${model.resultHeadingTag!"span"} class="result__heading">You got: ${result.title}</${model.resultHeadingTag!"span"}>
		</#if>

        <#if hasResultImage>
            <figure class="result-image">
                <img src="${image}" alt="${(result.title?has_content)?then('I got ' + result.title + '. ', '')}${model.document.heading}" data_description="${model.quizShare}${(result.title?has_content)?then(' ' + result.title, '')} ${model.document.heading}" />
                <figcaption>
                    <#if result.image.caption?has_content>${result.image.caption}.</#if>
                    <#if result.image.owner?has_content>${result.image.owner}</#if>
                </figcaption>
            </figure>
        </#if>

		<@location name="mrb" tag="" />
		<@location name="updatedDate" tag="" />

        <p>${result.description}</p>

        <@location name="disclaimer" tag="" />

        <#if image?has_content>
            <${model.shareHeadingTag!"span"} class="share-text">Share Your Results</${model.shareHeadingTag!"span"}>
            <@location name="social" tag="" />
        </#if>
    </section>
</#list>
