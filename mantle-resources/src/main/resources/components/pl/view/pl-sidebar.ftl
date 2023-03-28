<#macro buildNav categories class=''>
	<ul class="pl-categories ${class}">
		<#list categories as category>
			<#local categoryId = category.id?lower_case>
			<#local categoryId = categoryId?replace('\\s+', '-', 'rm') />

			<li data-test="${category.id}" data-category="${categoryId}">
				<@a type="nav.category" href="/pattern-library${category.uri}" class="pl-categories__link ${(category.children?size > 0)?then('accordion__trigger', '')} ${(requestContext.requestUri?split('/')?seq_index_of(categoryId) == -1)?then('', 'is-open is-selected')}">
                    <#if (category.children?size > 0)><span class="accordion__arrow"></span></#if>
                    ${category.displayName!category.id}
                </@a>
				<#if (category.children?size > 0)>
					<@buildNav categories=category.children?filter(c -> c.id != 'pattern-library')  class="pl-categories__children accordion__content ${(requestContext.requestUri?index_of('/' + categoryId) == -1)?then('is-closed', 'is-selected')}"/>
				</#if>
			</li>
		</#list>
	</ul>
</#macro>

<@component tag="nav">
	<#--  Filter is currently not working  -->
	<#--  <div class="mntl-pl-sidebar__search">
		<div class="filter">
			<div class="input-group">
				<label for="nav-search">
					<@svg name="icon-magnifying-glass" />
				</label>
				<input type="text" id="nav-search" />
			</div>
		</div>
	</div>  -->

    <@buildNav categories=model.plNavigation />

    <div class="categories-error is-hidden">No results found</div>
</@component>
