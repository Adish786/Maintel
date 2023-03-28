<@component data_doc_id=model.documentId?c>
<#compress>
	<#assign rating = (model.rating?has_content)?then(model.rating?number, 0) />
	<#list 1..model.count as i>
		<#assign className = (i <= rating)?then("active", "")+(rating?contains('.') && (i == rating?ceiling))?then(" half", "") />
		<#assign ariaLabel>Rate ${i?c}-star</#assign>
		<#if model.overrideWrapperTag?has_content> 
			<@optionalTag tag=model.overrideWrapperTag class=className data_rating=i data_ordinal=i aria_label=ariaLabel>
				${model.text!""}<@location name="unit" tag="" />
			</@optionalTag>
		<#else>
			<@a href="#" class=className data_rating=i data_ordinal=i aria_label=ariaLabel>${model.text!""}<@location name="unit" tag="" /></@a>
		</#if>
	</#list>
</#compress>
</@component>