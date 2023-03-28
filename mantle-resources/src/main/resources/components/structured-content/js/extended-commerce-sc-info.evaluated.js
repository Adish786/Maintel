<#if !extendedCommerceEvaluatedExists??>
window.Mntl = window.Mntl || {};
Mntl.Commerce = Mntl.Commerce || {};
<#if model.priorityDomains?has_content>
Mntl.Commerce.priorityDomains = [<#list model.priorityDomains as domain>'${domain?js_string}'<#sep>,</#list>];
</#if>
<#if model.safelistDomains?has_content>
Mntl.Commerce.safelistDomains = [<#list model.safelistDomains as domain>'${domain?js_string}'<#sep>,</#list>];
</#if>
<#if model.pricingDisabled?has_content>
Mntl.Commerce.pricingDisabled = ${model.pricingDisabled?c};
</#if>
<#assign extendedCommerceEvaluatedExists=true>
</#if>