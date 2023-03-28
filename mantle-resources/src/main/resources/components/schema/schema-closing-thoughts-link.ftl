<#compress>
<#if (model.document.userFeedback.link.uri)?has_content>
&lt;h3&gt;&lt;a href=&quot;${model.document.userFeedback.link.uri?html}&quot;&gt;${model.document.userFeedback.link.text?has_content?then(model.document.userFeedback.link.text?html,'') }&lt;/a&gt;&lt;/h3&gt;
<#elseif (model.document.userFeedback.link.text)?has_content>
&lt;h3&gt;${model.document.userFeedback.link.text}&lt;/h3&gt;
</#if>
</#compress>