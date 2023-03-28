<#if model.networks?has_content && model.networks?filter(network -> network.url?has_content)?size gt 0 >

	<@component tag="nav" role="navigation">
		<#assign owner = model.owner?has_content?then(model.owner + "'s ", "")>
		<#if model.title?has_content>
			<div class="social-nav__title">${model.title}</div>
		</#if>
		
		<ul class="social-nav__list">
			<#list model.networks as network>
				<#if network.url?has_content>
					<#assign accessibleLabel = network.accessibleLabel!owner + network.networkName>
                    <#assign networkName = network.networkName?lower_case>
					
                    <li class="social-nav__item social-nav__item--${networkName}">
                        <@a class="social-nav__link social-nav__link--${networkName}" href="${network.url}" safelist=true data_text="${network.text!''}">
                            <@svg classes="social-nav__icon" name="icon-${networkName}" />
                            <span class="social-nav__accessible-label">${accessibleLabel}</span>
                        </@a>
                    </li>
				</#if>
			</#list>
		</ul>
	</@component>

</#if>