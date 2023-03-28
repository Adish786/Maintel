<@component>
	<#if model.title?has_content>
		<${model.headingTag!"span"} class="mntl-sc-block-product__title">${model.title}</${model.headingTag!"span"}>
	</#if>
	<@location name="image" tag="" />
	<div class="mntl-sc-block-product__details">
		<span class="mntl-sc-block-product__brand">${model.brand}</span>
		<span class="mntl-sc-block-product__style-name">${model.styleName}</span>
		<#if model.retailPrice?has_content || model.salePrice?has_content>
			<span class="mntl-sc-block-product__prices"><#compress>
				<#if model.retailPrice?has_content><span class="mntl-sc-block-product__retail-price">${model.retailPrice}</span></#if>
				<#if model.salePrice?has_content><span class="mntl-sc-block-product__sale-price">${model.salePrice}</span></#if>
			</#compress></span>
		</#if>
	</div>
	<@location name="button" tag="" />
</@component>