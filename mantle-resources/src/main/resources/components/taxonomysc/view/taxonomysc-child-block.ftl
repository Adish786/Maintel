<@component>
    <#if model.heading?has_content>
        <h3 class="mntl-taxonomysc-child-block__heading">${model.heading}</h3>
    </#if>

    <div class="mntl-taxonomysc-child-block__links">
        <@location name="links" tag="" />
    </div>
</@component>