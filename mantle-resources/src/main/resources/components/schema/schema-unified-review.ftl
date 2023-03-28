<#compress>
	<#escape x as x?json_string>
		<#assign entityData = model.document.entity.data />
		<#assign productRecord = (model.productRecords[0].product)!'' />
		
		<#if (productRecord)?has_content>
			<#assign rootCategory = productRecord.category.ancestors[0].name!'' />
		    <#assign vendorBrand = productRecord.getPropertyValue('brand').primaryValue!'' />
			<#if rootCategory == "Performance Marketing">
				<#assign productName = productRecord.recordName!'' />
		    	<#assign productUrl = productRecord.properties[1].value.destinationUrls[0].url!'' />
			<#else>
				<#assign productName = productRecord.shortTitle!'' />
		    	<#assign productUrl = productRecord.retailerList[0].url!'' />
			</#if>
		    <#assign productRating = productRecord.rating!'' />
		<#else>
		    <#if (entityData.name)?has_content>
		        <#assign productName = entityData.name!'' />
		    </#if>
		    <#if (entityData.url)?has_content>
		        <#assign productUrl = entityData.url!'' />
		    </#if>
		</#if>
		  
		<#if (model.document.getImageForUsage('PRIMARY').objectId)?has_content>
		    <#assign productImage = model.document.getImageForUsage('PRIMARY')!'' />
		<#elseif (entityData.image.objectId)?has_content>
		    <#assign productImage = entityData.image!'' />
		</#if>

		<#assign comparisonList = model.document.getIntroContentsListOfType("COMPARISONLIST")!'' />
		<#if comparisonList?has_content>
			<#assign pros = (comparisonList[0].data.listA.items.getList())!'' />
			<#assign cons = (comparisonList[0].data.listB.items.getList())!'' />
			<@renderNoteList listName="positiveNotes" listItems=pros />
			<@renderNoteList listName="negativeNotes" listItems=cons />
		</#if>

		,"itemReviewed": {
		    "@type": "${model.itemReviewedType}"
		    <#if (productName)?has_content>
		        ,"name": "${productName}"
		    </#if>
		    <#if (productUrl)?has_content>
		        ,"url": "${productUrl}"
		    </#if>
		    <#if (productImage)?has_content>
		        ,"image": {
				    "@type": "ImageObject",
				    <#if utils.uncappedImageWidthsEnabled()>
				        "url": "<@thumborUrl img=productImage maxWidth=(productImage.width)!10000 filters=model.filters ignoreMaxBytes=true />"
				    <#else>
				        "url": "<@thumborUrl img=productImage maxWidth=1500 filters=model.filters />"
				    </#if>

				}
		    </#if>
		    <@authorReview authorAttributions=(model.authorAttributions)!{} guestAuthorData=(model.document.guestAuthor)!{} />
		    <@location name="entityCustomFields-${model.entityType?lower_case}" tag="" />
		}
		
		
		<#-- 
		    entityRating: 
		        - Use the EntityRating override if it's not null (empty string removes the schema property and prevents fallbacks), 
		        - otherwise check and use the Document Rating (exclude zero rating value)
		        - before falling back to the Entity Overall Score.
		-->
		<#if productRating?has_content>
		    <#assign entityRating = productRating />
		<#elseif model.entityRating??>
		    <#assign entityRating = model.entityRating />
		<#elseif (model.document.rating.value)?has_content>
		    <#assign entityRating = model.document.rating.value />
		<#elseif entityData.overallScore?has_content>
		    <#assign entityRating = entityData.overallScore />
		</#if>
		
		<#-- Don't include schema property if EntityRating is an empty String or 0 -->
		<#if entityRating?has_content && !isZero(entityRating)>
		    ,"reviewRating": {
		        "@type": "Rating"
		        ,"ratingValue": "${entityRating}"
		    }
		</#if>
	</#escape>
</#compress>