<@component data_lat=model.lat data_lng=model.lng>

	<@location name="mntl-sc-block-location__media" />
	
	<div class="mntl-sc-block-location__content">

		<@location name="mntl-sc-block-location__pre-content" tag="" />

		<#if (model.adrAddress)?has_content>
			
			<#if (model.addressHeader)?has_content>
			    <${model.headingTag!"span"} class="mntl-sc-block-location__address-label mntl-sc-block-location__label">${model.addressHeader}</${model.headingTag!"span"}>
			</#if>
			
			<address class="mntl-sc-block-location__address">
				${model.adrAddress}
			</address>
			
		</#if>
		<#if (model.url)?has_content>
			<div class="mntl-sc-block-location__url">
				<@a class="mntl-sc-block-location__url-label" external=true safelist=true href="${model.url}">Get directions</@a>
			</div>
		</#if>
		<#if (model.phone)?has_content>
			<div class="mntl-sc-block-location__phone">
				<${model.headingTag!"span"} class="mntl-sc-block-location__phone-label mntl-sc-block-location__label">Phone</${model.headingTag!"span"}>
				<span class="mntl-sc-block-location__phone-text">
					${model.phone}
				</span>
			</div>
		</#if>	
		<#if (model.website)?has_content>
			<div class="mntl-sc-block-location__website">
				<${model.headingTag!"span"} class="mntl-sc-block-location__website-label mntl-sc-block-location__label">Web</${model.headingTag!"span"}>
				<@a class="mntl-sc-block-location__website-text" external=true safelist=true href="${model.website}">Visit website</@a>
			</div>
		</#if>

	</div>
</@component>
