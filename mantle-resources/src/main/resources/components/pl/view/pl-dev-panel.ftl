<@component>
	<ul class="tabs mntl-pl-component-tabs" data-tabs id="${model.c.id}-code-tabs">
		<li class="tabs-title is-active"><a href="#${model.c.id}-usage">Usage</a></li>

        <li class="tabs-title"><a href="#${model.c.id}-xml">Effective XML</a></li>

		<#if (model.c.dependencies?size > 0) && model.isInternal>
            <li class="tabs-title"><a href="#${model.c.id}-dependencies">Models</a></li>
        </#if>

		<#if model.locations[model.c.id]?has_content>
            <li class="tabs-title"><a href="#${model.c.id}-locations">Locations</a></li>
        </#if>

		<#list model.c.stylesheets as stylesheet>
			<#assign stylesheetPath = stylesheet.path?replace("/static", "") />
			<#assign stylesheetPath = stylesheetPath?replace(".css", ".scss") />
			<li class="tabs-title"><a href="#${model.c.id}-style${stylesheet_index + 1}">${stylesheetPath[(stylesheetPath?last_index_of("/") + 1)..]}</a></li>
		</#list>

        <#-- This functionality is currently breaking certain components due to unescaped HTML   -->
        <#--  <#list model.c.scripts as script>
			<li class="tabs-title"><a href="#${model.c.id}-script${script_index + 1}">${script.path[(script.path?last_index_of("/") + 1)..]}</a></li>
		</#list>  -->
	</ul>

	<div class="tabs-content active" data-tabs-content="${model.c.id}-code-tabs">
        <div class="tabs-panel is-active" id="${model.c.id}-usage">
            <div class="usage__repository">
                <#assign repositoryLink = getRepositoryLink(model.c.module.definition.id, model.c.sourceLocation.uri) />

                <div class="usage__repository-title">Code</div>
                <a class="usage__repository-link" href="${repositoryLink}" target="_blank">${repositoryLink}</a>
                <span class="usage__branch-warning">
                    (You may need to change branches)
                </span>
            </div>

            <div class="usage__templates">
                <div class="usage__template-title">Templates</div>
                <ul class="usage__template-list">
                    <#list model.templates as template>
                        <li class="usage__template">${template}</li>
                    </#list>
                </ul>

                <#if model.templates?size == 0>
                    <div class="usage__no-results">No direct usages found</div>
                </#if>
            </div>
		</div>

		<div class="tabs-panel" id="${model.c.id}-xml">
			<pre><code data-xml-prettyprint data-xml-code>${model.c.toXmlString()?replace('<info>.*?</info>', (c.previewType=='markup')?string('<info><previewType>markup</previewType></info>', ''), 'rms')?html}</code></pre>
		</div>

		<#if model.isInternal && (model.c.dependencies?size > 0)>
            <#assign canUpdate = false />
            <div class="tabs-panel dependencies" id="${model.c.id}-dependencies">
                <#macro listDependencies dependencies parent="">
                    <#if dependencies?has_content>
                        <#local prefix = parent />
                        <#if prefix?has_content>
                            <#local prefix = prefix + "_" />
                        </#if>
                        <ul>
                            <#list dependencies as d>
                                <#local dValue = (d.getValue(requestContext).value)!"" />
                                <#if dValue?is_boolean || dValue?is_number>
                                    <#local dValue = dValue?c />
                                </#if>
                                <#local editable = dValue?has_content && dValue?is_string && dValue?index_of("com.about") == -1 && !d.dependencies?has_content />
                                <#if editable && !canUpdate>
                                    <#assign canUpdate = true />
                                </#if>
                                <li<#if !editable> class="<#if d.dependencies?has_content>no-border<#else>disabled</#if>"</#if>>
                                    <#if editable>
                                        <label for="${prefix}${d.id}">${d.displayName!d.id}</label><input id="${prefix}${d.id}" type="text" name="${d.id}" placeholder="${dValue}" /><div class="pl-edit-highlight"></div>
                                    <#else>
                                        <#if d.dependencies?has_content><h3>${d.displayName!d.id}</h3><#else>${d.displayName!d.id}</#if>
                                        <@listDependencies dependencies=d.dependencies![] parent=prefix+d.displayName!d.id />
                                    </#if>
                                </li>
                                <#local prefix = parent + "_" />
                            </#list>
                        </ul>
                    </#if>
                </#macro>

                <#macro listComponents components parent="">
                    <#if components?has_content>
                        <#local prefix = parent />
                        <#if prefix?has_content>
                            <#local prefix = prefix + "_" />
                        </#if>
                        <ul>
                            <#list components as component>
                                <li class="no-border">
                                    <h3>${component.id!''}</h3>
                                    <@listDependencies dependencies=component.dependencies![] parent=prefix+component.id!'' />
                                    <@listComponents components=component.components![] parent=prefix+component.id!'' />
                                </li>
                            </#list>
                        </ul>
                    </#if>
                </#macro>

                <form id="pl-edit-${model.c.id}" class="pl-edit">
                    <div class="pl-edit-window">
                        <@listDependencies dependencies=(model.c.dependencies)![] parent="pl-edit_${model.c.id}" />
                        <@listComponents components=(model.c.components)![] parent="pl-edit_${model.c.id}" />
                    </div>
                    <#if canUpdate><input type="submit" class="pl-button" value="Update" /></#if>
                </form>
            </div>
		</#if>

		<#if model.locations[model.c.id]?has_content>
			<div class="tabs-panel" id="${model.c.id}-locations">
				<ul>
					<#list model.locations[model.c.id] as location>
						<li>${location}</li>
					</#list>
				</ul>
			</div>
		</#if>

		<#list model.c.stylesheets as stylesheet>
			<div class="tabs-panel code" id="${model.c.id}-style${stylesheet_index + 1}">
				<pre><code class="language-css"><#include utils.getScssPath(stylesheet.path) parse=false /></code></pre>
			</div>
		</#list>

        <#-- This functionality is currently breaking certain components due to unescaped HTML   -->
		<#--  <#list model.c.scripts as script>
			<div class="tabs-panel code" id="${model.c.id}-script${script_index + 1}">
				<pre><code class="language-js"><#include script.path parse=false /></code></pre>
			</div>
		</#list>  -->
	</div>
</@component>

<#macro listDependencies dependencies parent="">
    <#if dependencies?has_content>
        <#local prefix = parent />
        <#if prefix?has_content>
            <#local prefix = prefix + "_" />
        </#if>
        <ul>
            <#list dependencies as d>
                <#local dValue = (d.getValue(requestContext).value)!"" />
                <#if dValue?is_boolean || dValue?is_number>
                    <#local dValue = dValue?c />
                </#if>
                <#local editable = dValue?has_content && dValue?is_string && dValue?index_of("com.about") == -1 && !d.dependencies?has_content />
                <#if editable && !canUpdate>
                    <#assign canUpdate = true />
                </#if>
                <li<#if !editable> class="<#if d.dependencies?has_content>no-border<#else>disabled</#if>"</#if>>
                    <#if editable>
                        <label for="${prefix}${d.id}">${d.displayName!d.id}</label><input id="${prefix}${d.id}" type="text" name="${d.id}" placeholder="${dValue}" /><div class="pl-edit-highlight"></div>
                    <#else>
                        <#if d.dependencies?has_content><h3>${d.displayName!d.id}</h3><#else>${d.displayName!d.id}</#if>
                        <@listDependencies dependencies=d.dependencies![] parent=prefix+d.displayName!d.id />
                    </#if>
                </li>
                <#local prefix = parent + "_" />
            </#list>
        </ul>
    </#if>
</#macro>

<#macro listComponents components parent="">
    <#if components?has_content>
        <#local prefix = parent />
        <#if prefix?has_content>
            <#local prefix = prefix + "_" />
        </#if>
        <ul>
            <#list components as component>
                <li class="no-border">
                    <h3>${component.id!''}</h3>
                    <@listDependencies dependencies=component.dependencies![] parent=prefix+component.id!'' />
                    <@listComponents components=component.components![] parent=prefix+component.id!'' />
                </li>
            </#list>
        </ul>
    </#if>
</#macro>

<#--  Return the bitbucket link to the xml file for a component  -->
<#function getRepositoryLink module sourceLocationUri>
    <#assign definitionsIndex = sourceLocationUri?index_of("definitions/") />
    <#assign componentsIndex = sourceLocationUri?index_of("components/") />

    <#--  Get the file path that comes after <vertical>/<vertical>-resources/src/main/resources,  -->
    <#--  which could either be a template (definitions) or component (components)  -->
    <#if definitionsIndex != -1>
        <#assign filePath = sourceLocationUri[definitionsIndex..] />
    <#elseif componentsIndex != -1>
        <#assign filePath = sourceLocationUri[componentsIndex..] />
    <#else>
        <#assign jarIndex = sourceLocationUri?index_of(".jar!") />
        <#return sourceLocationUri[(jarIndex + 5)..] />
    </#if>

    <#assign repoName = module?replace("-resources", "") />

    <#return "https://bitbucket.prod.aws.about.com/projects/FRON/repos/" + repoName + "/browse/" + module + "/src/main/resources/" + filePath />
</#function>