{
	"title": "${projectInfo.title}",
	"version": "${projectInfo.version}",
	"meta-data": {
	    "resource-modules": {
            <#if model.moduleVersions?has_content>
	            <#list model.moduleVersions?keys as key>
                    "${key}":"${model.moduleVersions[key]}"<#sep>,</#sep>
                </#list>
            </#if>
	    },
	    "build-modules": {
            <#if model.buildToolVersions?has_content>
        	    <#list model.buildToolVersions?keys as key>
                   "${key}":"${model.buildToolVersions[key]}"<#sep>,</#sep>
                </#list>
            </#if>
        }
	}
}
