<@component>
    <#if model.heading?has_content>
        <h3 class="mntl-sc-block-featuredquote__heading">
            ${model.heading}
        </h3>
    </#if>
    
    <div class="mntl-sc-block-featuredquote__quote">
        ${model.quote}
    </div>

    <#if model.credit?has_content>
        <div class="mntl-sc-block-featuredquote__credit-wrapper">
            <div class="mntl-sc-block-featuredquote__credit">
                <#if model.symbol?has_content>
                    <span> ${model.symbol} </span>
                </#if>
                <#if model.uri?has_content>
                    <@a href="${model.uri}">${model.credit}</@a>
                <#else>
                    <span>${model.credit}</span>
                </#if>
            </div>
            <@location name="image" />
        </div>
    </#if>
</@component>