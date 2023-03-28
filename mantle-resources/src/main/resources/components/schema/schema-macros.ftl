<#macro itemListElement position name url description image={}>
<#compress>
<#escape x as x?json_string>
{
	"@type": "ListItem",
	"position": ${position}
	<#if name?has_content>
		,"name": "${name}"
	</#if>
	<#if url?has_content>
		,"url": "${url}"
	</#if>
	<#if description?has_content>
		,"description": "${description}"
	</#if>
	<#if (image.objectId)?has_content>
		,"image": {
			"@type": "ImageObject",
            "url": "<@thumborUrl img=image maxWidth=image.width maxHeight=image.height ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />",
			"height": ${image.height?c},
			"width": ${image.width?c}
		}
	</#if>
}
</#escape>
</#compress>
</#macro>

<#macro itemListScElement itemType position name url description images=[] >
<#compress>
<#escape x as x?json_string>
{
	"@type": "${itemType}",
	"position": ${position}
	<#if name?has_content>
		,"name": "${name}"
	</#if>
	<#if url?has_content>
		,"url": "${url}"
	</#if>
	<#if description?has_content>
		,"description": "${description}"
	</#if>
	<#if images?has_content>
		,"image": [
			<#list images as image>
				<#if (image.objectId)?has_content>
					{
						"@type": "ImageObject",
						"url": "<@thumborUrl img=image maxWidth=image.width maxHeight=image.height ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />",
						"height": ${image.height?c},
						"width": ${image.width?c}
					}<#if image_has_next>,</#if>
				</#if>
			</#list>
		]
	</#if>
    <#nested>
}
</#escape>
</#compress>
</#macro>

<#macro itemProductElement productType name url imageId authorAttributions description reviewRating vendorBrand award="" guestAuthorData={} offer={} gtins=[] mpns=[] pros=[] cons=[] hideReviewBlock=false>
<#compress>
<#escape x as x?json_string>
{
	"@type": "${productType}"
	<#if url?has_content>
		,"url": "${url}"
	</#if>
	<#if name?has_content>
		,"name": "${name}"
	</#if>
	<#if imageId?has_content>
	    <#if utils.uncappedImageWidthsEnabled()>
	        ,"image": "<@thumborUrl img=imageId maxWidth=(imageId.width)!10000 maxHeight=model.useImageHeight?then(imageId.height!0, 0) fitInStyle=model.fitInStyle filters=model.filters ignoreMaxBytes=true />"
	    <#else>
	        ,"image": "<@thumborUrl img=imageId maxWidth=1500 maxHeight=model.useImageHeight?then(imageId.height!0, 0) fitInStyle=model.fitInStyle filters=model.filters />"
	    </#if>

	</#if>
	<#if (authorAttributions?has_content || reviewRating?has_content) && !hideReviewBlock >
		,"review": {
            <#if (guestAuthorData.link.text)?has_content>
            "author": [<@rawAuthor guestAuthorData=(guestAuthorData) />]
            <#else>
            <@listAuthors attributionModels=(authorAttributions)!{} role="author" />
            </#if>
			<#if reviewRating?has_content && !isZero(reviewRating)>
				,"reviewRating": {
					"@type": "Rating",
					"ratingValue": "${reviewRating}",
					"worstRating": 0,
					"bestRating": 5
				}
			</#if>
			<#if (pros?has_content)>
				,"positiveNotes": {
                    "@type": "ItemList",
                    "itemListElement": [
                        <#list pros as pro>
                            {
                                "@type": "ListItem",
                                "position": ${pro?counter},
                                "name": "${processSchemaString(pro)}"
                            }<#sep>,
                        </#list>
                    ]
                }
			</#if>
			<#if (cons?has_content)>
				,"negativeNotes": {
                    "@type": "ItemList",
                    "itemListElement": [
                        <#list cons as con>
                            {
                                "@type": "ListItem",
                                "position": ${con?counter},
                                "name": "${processSchemaString(con)}"
                            }<#sep>,
                        </#list>
                    ]
                }
			</#if>
		}
	</#if>
	<#if vendorBrand?has_content>
		,"brand": {
			"@type": "Brand",
			"name" : "${vendorBrand}"
		}
	</#if>
	<#if offer.price?has_content>
		,"offers": {
			"@type": "Offer",
			"priceCurrency": "${offer.currency}",
			"price": "${offer.price}"
		}
	</#if>
	<#if gtins?has_content>
		,"gtin": "${gtins[0]}"
	</#if>
	<#if mpns?has_content>
		,"mpn": "${mpns[0]}"
	</#if>
	<#if description?has_content>
		,"description": "<#list description as content>${processSchemaString(content.data.html)}<#sep> </#sep></#list>"
	</#if>
	<#if award?has_content>
		,"award": "${award}"
	</#if>
}
</#escape>
</#compress>
</#macro>

<#macro itemList name description numberOfItems itemListOrder="ItemListOrderDescending">
<#compress>
<#escape x as x?json_string>
{
	"@context": "http://schema.org",
	"@type": "ItemList",
	<#if name?has_content>
		"name": "${name}",
	</#if>
	<#if description?has_content>
 		"description": "${description}",
 	</#if>
  	"itemListOrder": "http://schema.org/${itemListOrder}",
	"numberOfItems": ${numberOfItems?c},
	"itemListElement": [
		<#nested />
	]
}
</#escape>
</#compress>
</#macro>

<#function itemListOrder listOptions>
	<#if ((listOptions.markerType)!"") == "numbers">
		<#if ((listOptions.sortOrder)!"") == "asc">
			<#return "ItemListOrderAscending" />
		<#else>
			<#return "ItemListOrderDescending" />
		</#if>
	<#else>
		<#return "ItemListUnordered" />
	</#if>
</#function>

<#macro publisher width height verticalName imageId publishingPrinciples="" sameAs=[] orgData={} addressFlag=false>
<#compress>
<#escape x as x?json_string>
	"publisher": {
		"@type": "Organization",
		"name": "${verticalName}",
		"url": "https://www.${utils.getDomain('/')}",
		"logo": {
			"@type": "ImageObject",
			"url": "<@thumborUrl img=imageId maxWidth=width maxHeight=height ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />",
			"width": ${width?c},
			"height": ${height?c}
		},
		"brand": "${verticalName}"
		<#if publishingPrinciples?has_content>
		, "publishingPrinciples": "${publishingPrinciples}"
		</#if>
		<#if (sameAs!?size > 0)>
		, <@sameAsSchema sameAsData=sameAs />
		</#if>
		<#if (addressFlag)>
		, <@dotdashMeredithAddressSchema />
		</#if>
		<#if ((orgData)?has_content)>
		, <@parentOrganization data=orgData />
		</#if>
	}
</#escape>
</#compress>
</#macro>

<#macro dotdashMeredithAddressSchema>
<#compress>
"address": {
    "@type": "PostalAddress",
    "streetAddress": "225 Liberty Street, 4th Floor",
    "addressLocality": "New York",
    "addressRegion": "NY",
    "postalCode": "10281",
    "addressCountry": "USA"
}
</#compress>
</#macro>

<#macro sameAsSchema sameAsData=[]>
<#compress>
<#list sameAsData>
"sameAs" : [
    <#items as link>
        "${link}"<#sep>,</#sep>
    </#items>
]
</#list>
</#compress>
</#macro>

<#macro parentOrganization data>
<#compress>
"parentOrganization" : {
    "url": "https://www.dotdashmeredith.com",
    "brand": "Dotdash Meredith",
    "name": "Dotdash Meredith",
    <@dotdashMeredithAddressSchema />,
    "logo": {
        "@type": "ImageObject",
        "url": "<@thumborUrl img=data.imageId maxWidth=data.logoWidth maxHeight=data.logoHeight ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />",
        "width": ${data.logoWidth?c},
        "height": ${data.logoHeight?c}
    },
    "sameAs" : [
        "https://en.wikipedia.org/wiki/Dotdash_Meredith",
        "https://www.instagram.com/dotdashmeredith/",
        "https://www.linkedin.com/company/dotdashmeredith/",
        "https://www.facebook.com/dotdashmeredith/"
    ]
}
</#compress>
</#macro>

<#macro videoObject videoBlock>
<#compress>
<#escape x as x?json_string>
    <#if (videoBlock?has_content && videoBlock.data?has_content && videoBlock.data.document?has_content)>
        <#local videoDocument = videoBlock.data.document />
        <#local videoDuration = videoDocument.duration />

        <#if (videoDocument.templateType == 'BRIGHTCOVEVIDEO')>
            <#local videoDuration = (videoDuration/1000)?int />
        </#if>

        <#local mins = (videoDuration/60)?int />
        <#local seconds = (videoDuration%60) />

        <#if (videoDocument.contentUrl?has_content)>
            "contentUrl": "${videoDocument.contentUrl}",
        <#elseif (videoDocument.templateType == 'BRIGHTCOVEVIDEO')>
            "embedUrl": "https://players.brightcove.net/${videoDocument.accountId}/default_default/index.html?videoId=${videoDocument.videoId}",
        </#if>
        "description": "${(videoDocument.description!'')}",
        <#-- duration is per ISO8601 (https://en.wikipedia.org/wiki/ISO_8601#Durations) -->
        "duration": "${'PT' + (mins > 0)?then(mins + 'M','') + (seconds > 0)?then(seconds + 'S','') }",
        <#if (videoDocument.title?has_content)>
            "name": "${videoDocument.title}",
        <#elseif (videoDocument.name?has_content)>
            "name": "${videoDocument.name}",
        </#if>
        <#if (videoDocument.thumbnailUrl?has_content)>
            "thumbnailUrl": "${videoDocument.thumbnailUrl}",
        </#if>
        "uploadDate": "${videoDocument.dates.displayed.withZone(DateTimeZone.forID("America/New_York"))}"
    </#if>
</#escape>
</#compress>
</#macro>

<#macro imageObject image>
{
    "@type": "ImageObject",
    "url": "<@thumborUrl img=image maxWidth=image.width maxHeight=image.height filters=[imageFilter.noUpscale()] />",
    "height": ${image.height?c},
    "width": ${image.width?c}
}
</#macro>

<#macro tableObject title>
<#compress>
<#escape x as x?json_string>
{
    "@context": "http://schema.org",
    "@type": "Table",
    "about": "${title}"
}
</#escape>
</#compress>
</#macro>

<#function processSchemaString schemaString >
     <#return schemaString?replace('<[^>]+>',' ','r')?replace('&nbsp;', ' ')?trim>
</#function>

<#macro howToSteps steps listUrl=false>
<#compress>
<#escape x as x?json_string>
	<#list steps as step>
        <#local processedName = processSchemaString(step.name!'')>
        <#local processedText = processSchemaString(step.text!'')>
        <#if ((processedName?length) > 0) || ((step.images)?has_content) || ((processedText?length) > 0)>
            <#compress>
            {
                "@type": "HowToStep"
				<#if listUrl>
					<#assign hash = utils.formatForHash(processedName) />
					<#if hash?has_content>,"url": "${requestContext.requestUrl}#toc-${hash}"</#if>
				</#if>
				<#if processedName?has_content>,"name": "${processedName}"</#if>
                <#if (step.images)?has_content>,"image": [
                    <#list step.images as image>
                        {
                            "@type": "ImageObject",
                            <#if utils.uncappedImageWidthsEnabled()>
                                "url": "<@thumborUrl img=image maxWidth=(image.width)!10000 filters=model.filters ignoreMaxBytes=true />"
                            <#else>
                                "url": "<@thumborUrl img=image maxWidth=1500 filters=model.filters />"
                            </#if>

                        }
                        <#sep>,</#sep>
                    </#list>
                ]
                </#if>
                <#if processedText?has_content>,"text": "${processedText}"</#if>
            } <#sep>,
            </#compress>
        </#if>
    </#list>
</#escape>
</#compress>
</#macro>

<#macro authorReview authorAttributions={} guestAuthorData={}>
<#compress>
<#if authorAttributions?has_content || (guestAuthorData.link.text)?has_content>
    ,"review": {
        <#if (guestAuthorData.link.text)?has_content>
            "author": [<@rawAuthor guestAuthorData=(guestAuthorData) />]
        <#else>
            <@listAuthors attributionModels=(authorAttributions)!{} role="author" />
        </#if>
    }
</#if>
</#compress>
</#macro>

<#macro listAuthors attributionModels role>
<#compress>
<#if attributionModels?has_content>
"${role}": [
<#list attributionModels>
    <#items as attributionModel>
        <#if (attributionModel)?has_content>
            <@rawAuthor authorData=(attributionModel.author)!{} />
        <#sep>,</#sep>
        </#if>
    </#items>
</#list>
]
</#if>
</#compress>
</#macro>

<#macro author authorData={} guestAuthorData={} >
<#compress>
<#if ((authorData.displayName)?has_content && authorData.displayName != "Staff Author") || (guestAuthorData.link.text)?has_content>
    ,"author":
    <@rawAuthor authorData guestAuthorData />
</#if>
</#compress>
</#macro>

<#macro rawAuthor authorData={} guestAuthorData={} >
        {"@type": "Person"
        <#if (guestAuthorData.link.text)?has_content>
            <@personFields name=guestAuthorData.link.text
                           bioUrl=(guestAuthorData.link.uri)!"" />
        <#else>
            <@personFields name=authorData.displayName
                           bioUrl=(authorData.bioUrl)!"" />

        </#if>
        }
</#macro>

<#macro personFields name="" description="" socialPresence={} bioUrl="" jobTitle="" honorificPrefix="" honorificSuffix="" knowsAbout={} alumniOf={}>
<#compress>
<#escape x as x?json_string>
<#if name?has_content>
    ,"name": "${name}"
</#if>
<#if description?has_content>
    ,"description": "${description}"
</#if>
<#if jobTitle?has_content>
    ,"jobTitle": "${jobTitle}"
</#if>
<#if honorificPrefix?has_content>
    ,"honorificPrefix": "${honorificPrefix}"
</#if>
<#if honorificSuffix?has_content>
    ,"honorificSuffix": "${honorificSuffix}"
</#if>
<#if bioUrl?has_content>
    ,<@authorBioUrl bioUrl=bioUrl />
</#if>
<#if socialPresence?has_content>
    ,<@authorSameAs socialData=socialPresence />
</#if>
<#if (knowsAbout.list)?has_content>
    ,<@authorKnowsAbout knowsAboutData=knowsAbout.list />
</#if>
<#if (alumniOf.list)?has_content>
    ,<@authorAlumniOf alumniData=alumniOf.list />
</#if>
</#escape>
</#compress>
</#macro>

<#macro authorBioUrl bioUrl="">
<#compress>
<#escape x as x?json_string>
    "url": "${bioUrl}"
</#escape>
</#compress>
</#macro>

<#macro authorSameAs socialData=[]>
<#compress>
<#escape x as x?json_string>
"sameAs": [
    <#list socialData>
        <#items as link>
            <#if link?has_content>
                "${link.url}"<#sep>,</#sep>
            </#if>
        </#items>
    </#list>
]
</#escape>
</#compress>
</#macro>

<#macro authorAlumniOf alumniData>
<#compress>
<#escape x as x?json_string>
    "alumniOf": [
        <#list alumniData as orgName>
            <#if (orgName)?has_content >
                {
                    "@type": "Organization",
                    "Name": "${orgName}"
                }<#if orgName?has_next && alumniData[orgName_index+1]?has_content>,</#if>
            </#if>
    </#list>
    ]
</#escape>
</#compress>
</#macro>

<#macro authorWorksFor workData>
<#compress>
    "worksFor": [
        <#list workData>
        <#items as business>
            <#if (business.name)?has_content>
                {
                    "@type": "Organization",
                    "Name": "${business.name}"
                }<#sep>,</#sep>
            </#if>
        </#items>
    ]
</#list>
</#compress>
</#macro>

<#macro authorKnowsAbout knowsAboutData>
<#compress>
<#escape x as x?json_string>
<#assign knowsAboutString = "" />
<#list knowsAboutData>
    <#items as topic>
        <#if (topic)?has_content>
            <#assign knowsAboutString = knowsAboutString + topic />
            <#sep>
                <#assign knowsAboutString = knowsAboutString + ', ' />
            </#sep>
        </#if>
    </#items>
</#list>

<#if (knowsAboutString)?has_content >
    "knowsAbout": ["${knowsAboutString}"]
</#if>
</#escape>
</#compress>
</#macro>

<#macro listDelimitedValues itemList delimiter defaultVal>
<#compress>
<#list itemList?filter(t -> t?has_content)>
    <#items as itemVal>"${itemVal}"<#sep>${delimiter}</#items>
<#else>
   "${defaultVal}"
</#list>
</#compress>
</#macro>

<#macro toolMaterialGroup toolList>
<#compress>
<#escape x as x?json_string>
<#assign totalToolList = [] />
<#list toolList as tool>
    <#if tool.materials?has_content>
        <#assign totalToolList = totalToolList + tool.materials.list />
    </#if>
</#list>
<#if totalToolList?has_content>
    ,"tool": [
        <#list totalToolList as tool>
            {
                "@type": "HowToTool",
                "name": "${tool.description}"
                <#if (tool.quantityRange)?has_content>
                ,"requiredQuantity": "<@quantity quantityRange=tool.quantityRange /><#if (tool.unit?has_content)> ${tool.unit}</#if>"
                </#if>
            }<#sep>,
        </#list>
    ]
</#if>
</#escape>
</#compress>
</#macro>

<#macro renderNoteList listName="" listItems=[]>
<#compress>
    <#if (listItems?has_content)>
        ,"${listName}": {
            "@type": "ItemList",
            "itemListElement": [
                <#list listItems as item>
                    {
                        "@type": "ListItem",
                        "position": ${item?counter},
                        "name": "${processSchemaString(item)}"
                    }<#sep>,
                </#list>
            ]
        }
	</#if>
</#compress>
</#macro>