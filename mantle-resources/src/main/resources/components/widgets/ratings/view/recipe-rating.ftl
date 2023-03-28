<@component>
	<#assign rating = (model.rating?has_content)?then(model.rating?number, 0) />
	<#assign rounded = (rating * 2)?round / 2 />
	<#assign isDecimal = rounded - (rounded / 1)?floor * 1 != 0 />
	<#assign fullStars = rounded?floor />
	<#assign stars = (5 - fullStars) />

	<#list 0..<fullStars as i>
		<@svg name="icon-star" />
	</#list>

	<#list 0..<stars as i>
		<#assign iconName = "icon-" + (i == 0)?then(isDecimal?then("star-half", "star-empty"), "star-empty") />
		<@svg name=iconName />
	</#list>
</@component>