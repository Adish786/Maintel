<@component tag="div">
    <h1>Model Dumper</h1>
    <p>Dumps out everything Freemarker knows about</p>
    <h2>Model<h2>
    <table>
        <thead>
            <tr>
                <td>Name</td>
                <td>Value</td>
            </tr>
        </thead>
        <tbody>
        <#list model?keys?sort as key>
            <tr>
                <td>${key}</td>
                <td>${toString(model[key])}</td>
            </tr>
        </tbody>
        </#list>
    </table>
    <h2>Full Freemarker Context</h2>
    In addition to `model`, freemarker has the following variables in its context
    <table>
        <thead>
            <tr>
                <td>Name</td>
            </tr>
        </thead>
        <tbody>
        <#list .data_model?keys?sort as key>
            <tr>
                <td>${key}</td>
                <td>${toString(.data_model[key])}</td>
            </tr>
        </tbody>
        </#list>
    </table>
    <hr>
</@component>

<#-- Convert anything to a string -->
<#function toString x>
    <#assign answer>
        <#if !(x?has_content)>
            No Content
        <#elseif x?is_enumerable>
            Enumerable with ${x?size} elements.
        <#elseif x?is_hash>
            <#if x['class']?has_content>
                <#-- classes show up as hashes -->
                Class of type: ${x['class']}.  toString: ${x}
            <#else>
                <i>Hash with ${x?size} elements.  Keys: ${x?keys?join(", ")}</i>
            </#if>
        <#else>
            ${x}
        </#if>
    </#assign>
    <#return answer>
</#function>
