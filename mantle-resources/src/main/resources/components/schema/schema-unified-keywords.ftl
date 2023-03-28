<#if model.nodes?has_content>
    <#-- Keywords are tax shortHeading values (filtering out null values) -->
    <#assign keywords = model.nodes?filter(node -> (node.document.shortHeading)??)?map(node -> (node.document.shortHeading)) />
    <#-- Add secondaryParents to list of keywords -->
    <#if model.secondaryParents??>
        <#assign keywords = keywords + model.secondaryParents?filter(node -> (node.targetNode.document.shortHeading)??)?map(node -> (node.targetNode.document.shortHeading)) />
    </#if>
    <#if keywords?has_content>
        <#-- If commerce, add 'affiliate' to keywords list -->
        <#if model.isCommerce>
            <#assign keywords = keywords + ["affiliate"] />
        </#if>
        <#-- Exclude tax0 -->
        ,"articleSection": "${keywords[1]}"
        ,"keywords": [<#list keywords[2 ..] as keyword>"${keyword}"<#sep>,</#sep><#lt></#list>]
    </#if>
</#if>
