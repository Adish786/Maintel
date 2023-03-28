<@component>
    <#if model.heading??>
        <h3 class="mntl-sc-block-profile__heading">${model.heading}</h3> 
    </#if>

    <table class="mntl-sc-block-profile__table">
        <tbody>
            <#list model.propertyOrder as propertyName>
                <#if model.profile.getParsedPropertyValue(propertyName)??>
                    <tr>
                        <td class="mntl-sc-block-profile__name">${utils.formatCamelCaseToSentenceCase(propertyName)}</td>
                        <td class="mntl-sc-block-profile__value">${model.profile.getParsedPropertyValue(propertyName)}</td>
                    </tr>
                </#if>
            </#list>
        </tbody>
    </table>
</@component>