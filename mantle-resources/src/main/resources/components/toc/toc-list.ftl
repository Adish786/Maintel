 <@component class="toc-list${model.jsScroll?then(' js-toc-scroll','')}">

    <#assign tocColSize = (model.tocItems?size/model.columnNum)?ceiling />
    <#assign index = 1 />

	 <#list model.tocItems?chunk(tocColSize) as column>
		<div class="toc-list__col">
			<#list column as item>
		  		<#assign className>toc-list__item ${(index > model.limit)?then('toc-item__secondary','')}</#assign>
	        	<@a class=className href="#toc-${item.id}">${item.heading}</@a>
	            <#assign index = index + 1 />
			</#list>
		</div>
	</#list>

</@component>