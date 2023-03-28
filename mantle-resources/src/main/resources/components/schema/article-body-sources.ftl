&lt;div&gt;
&lt;span&gt;${model.title?html}&lt;/span&gt;
&lt;ul&gt;
<#list model.document.sources.list as source>
&lt;li&gt;${source?html}&lt;/li&gt;
</#list>
&lt;/ul&gt;
&lt;/div&gt;