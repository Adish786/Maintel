<@component>
    <#if model.heading?has_content>
        <h4 class="mntl-materials-list__heading">${model.heading}</h4>
    </#if>
    <ul class="mntl-materials-list__list">
        <#if model.materials??>
            <#list model.materials as material>
                <li class="mntl-materials-list__item">
                    <#if material.quantityRange?has_content><@quantity quantityRange=material.quantityRange /></#if>
                    <#if material.unit?has_content> ${material.unit}</#if>
                    ${material.description}
                </li>
            </#list>
        </#if>
    </ul>
</@component>