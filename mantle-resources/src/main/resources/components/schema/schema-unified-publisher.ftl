<#if model.logoWidth?has_content && model.logoHeight?has_content && model.verticalName?has_content && model.imageId?has_content>
,<@publisher width=model.logoWidth height=model.logoHeight verticalName=model.verticalName imageId=model.imageId publishingPrinciples=model.publishingPrinciples sameAs=model.sameAs orgData=model.parentOrganization!{} addressFlag=model.addressFlag!false/>
</#if>