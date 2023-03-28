<@itemList name=model.name description=model.description numberOfItems=model.items?size itemListOrder=itemListOrder(model.listOptions!{})>
	<#list model.items as item>
		<#assign position = item_index + 1 />
        <#assign itemType = model.itemType!'ListItem' />
		<#assign itemName = '' />
		<#assign url = '' />
		<#if item.item??><#-- LISTSC -->
			<#assign itemName = (item.item.getContentsListOfType('HEADING')[0].data.text)!'' />
			<#assign url = item.url!'' />
		<#elseif (item.link.text)?has_content><#-- LIST -->
			<#assign itemName = (item.link.text)!'' />
		</#if>
<#t><#-- NOTE: description, image, etc intentionally blank because ListItem in its current form does not support these properties-->
<#t><#-- TODO: make the ListItem a "Thing" to add support for those properties later -->
		<@itemListScElement itemType=itemType position=position name=itemName description='' url=url />
		<#sep>,</#sep><#lt>
	</#list>
</@itemList>