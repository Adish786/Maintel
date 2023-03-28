<@component tag=model.tag!"button">
    ${model.text!''}
    <#if model.svgIcon?has_content>
    	<@svg name=model.svgIcon classes="mntl-button__icon ${model.svgIconClasses!''}" />
    </#if>
</@component>