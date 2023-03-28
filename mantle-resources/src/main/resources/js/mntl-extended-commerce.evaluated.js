<#if !extendedCommerceEvaluatedExists ??>
    window.Mntl = window.Mntl || {};
window.Mntl.ExtCommerceConfig = window.Mntl.ExtCommerceConfig || {};

    <#if model.priorityDomains?has_content>
        window.Mntl.ExtCommerceConfig.priorityDomains = [<#list model.priorityDomains as domain>'${domain?js_string}'<#sep>,</#list>];
    </#if>

    <#if model.safelistDomains?has_content>
        window.Mntl.ExtCommerceConfig.safelistDomains = [<#list model.safelistDomains as domain>'${domain?js_string}'<#sep>,</#list>];
    </#if>

    <#if model.pricingDisabled?has_content>
        window.Mntl.ExtCommerceConfig.pricingDisabled = ${model.pricingDisabled?c};
    </#if>

    <#if model.filterOOS?has_content>
        window.Mntl.ExtCommerceConfig.filterOOS = ${model.filterOOS?c};
    </#if>

    window.Mntl.ExtCommerceConfig.retailerLimit = 3;
    <#assign extendedCommerceEvaluatedExists=true>
</#if>
