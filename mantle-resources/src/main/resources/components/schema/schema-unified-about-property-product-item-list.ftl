<@itemList name=model.name description=model.description numberOfItems=model.items?size itemListOrder=itemListOrder(model.listOptions!{})>
	<#assign isFirstRating = true />
	<#assign productRecordIndex = 0 />
    <#assign productType = model.productType!'Product' />
	<#assign hideReviewBlock = model.hideReviewBlock!false />
    <#assign hideReviewRating = model.hideReviewRating!false />

	<#list model.items as item>
	    <#assign numberOfProductRecordBlockInListItem = item.getContentsListOfType('PRODUCTRECORD')?size />
	    <#assign hasProductRecordBlock = (numberOfProductRecordBlockInListItem > 0)!false />
		<#if hasProductRecordBlock>
		    <#assign productRecord = model.productRecords[productRecordIndex]!'' />
        	<#assign productRecordIndex = productRecordIndex + numberOfProductRecordBlockInListItem />
		    <#if productRecord?has_content>
		        <#assign gtins = productRecord.product.primaryGtin![] />
                <#assign mpns = productRecord.product.primaryMpn![] />
                <#assign productRating = productRecord.structuredContentProductRecordDataEx.productRecordRating!'' />
                <#assign reviewRating = '' />
                <#if !hideReviewRating && productRating?has_content && !isZero(productRating) && isFirstRating>
                    <#assign reviewRating = productRating />
                    <#assign isFirstRating = false  />
                </#if>
                <#assign productImage = productRecord.structuredContentProductRecordDataEx.productRecordImage!'' />
                <#assign anchorId = productRecord.structuredContentProductRecordDataEx.generateAnchorId()!'' />
                <#assign url = model.document.url + '#' + anchorId />
                <#assign description = item.getContentsListOfType('HTML') />
                <#assign award = productRecord.structuredContentProductRecordDataEx.superlative!'' />
                <#assign pros = '' />
                <#assign cons = '' />
                <#assign comparisonList = item.getContentsListOfType("COMPARISONLIST")!'' />
                <#assign amazonPrice = productRecord.product.getPropertyValue('amazonPrice')!'' />
                <#assign offer = {} />
                <#if !amazonPrice.isEmpty()>
                    <#assign offer = {"price": amazonPrice.getFormattedCurrencyValue()!'', "currency": amazonPrice.getCurrencyCode()!'USD'} />
                </#if>
                <#if comparisonList?has_content>
										<#assign pros = (comparisonList[0].data.listA.items.getList())!'' />
		                <#assign cons = (comparisonList[0].data.listB.items.getList())!'' />
                </#if>
                <#if productRecord.product.category.ancestors[0].name == "Performance Marketing">
                    <#assign itemName = productRecord.product.recordName!'' />
                    <#assign vendorBrand = productRecord.product.getPropertyValue("company").getPrimaryValue()!'' />
                <#else>
                    <#assign itemName = productRecord.product.shortTitle!'' />
                    <#assign vendorBrand = productRecord.product.getPropertyValue('brand').primaryValue!'' />
                </#if>

                <@itemProductElement productType=productType name=itemName url=url gtins=gtins mpns=mpns description=description award=award imageId=productImage authorAttributions=model.authorAttributions guestAuthorData=(model.document.guestAuthor)!{} reviewRating=reviewRating vendorBrand=vendorBrand pros=pros cons=cons offer=offer hideReviewBlock=hideReviewBlock />
                <#-- Comma is inserted only when there is more productRecord/product to be listed -->
                <#if productRecordIndex < model.productRecords?size>
                    <#sep>,</#sep><#lt>
                </#if>
		    </#if>
		</#if>
	</#list>
</@itemList>
