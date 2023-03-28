<@component tag="ul" class="share">
    <#list model.networks?split(",") as network>
        <#if network?has_content>
            <#assign
                classSuffix = (messages['socialShareClassSuffix_' + network])!network?lower_case
                iconSuffix = (messages['socialShareIconSuffix_' + network])!network?lower_case
                title = (messages['socialShareTitle_' + network])!network?lower_case?cap_first
                label = (messages['socialShareLabel_' + network])!network
            >
            <li class="share-item share-item-${classSuffix}">
                <span data-href="${model.socialLinks[network]}"
                   data-network="${network?lower_case}"
                   data-click-tracked="true"
                   class="share-link share-link-${classSuffix}"
                   tabindex="0"
                   title="${title}">
                    <@svg name="icon-${iconSuffix}" />
                    ${label}
                </span>
            </li>
        </#if>
    </#list>
</@component>