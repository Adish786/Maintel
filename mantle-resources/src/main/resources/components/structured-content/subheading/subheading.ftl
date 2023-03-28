<@compress single_line=true>
    <@location name="toc-anchor" tag=""/>
    <@component tag="${model.tag}">
        <#if model.uri?has_content>
            <@a class="mntl-sc-block-subheading__link" safelist=true href="${model.uri}">${model.text}</@a>
        <#else>
            <#--
                wrapping text with span to allow verticals apply styling to the text without needing to
                change the display of the header tag.
            -->
            <span class="mntl-sc-block-subheading__text">
                ${model.text}
            </span>
        </#if>
    </@component>
</@compress>