<#assign rating = (model.rating?has_content)?then(model.rating?number, 0) />
<#assign markerPoint = (rating - rating?floor) * 100 />

<#--
MarkerPoint - Percentage of the highligthed area of the last icon. (partial rating)
Multiplier - Setting a Multiplier rounds up the marking by the given number.
             i.e if the markerPoint is 10, and the multipier is 25, 
             the final markerPoint will be 25. 
CeilingException - if in any case we don't want to round up to a certain number, this can be       
             configured via ceilingException array.i.e if the markerPoint is 90, and the multiplier is 25, the final markerPoint will be 100.
             if we ONLY wanna show 100% when the markerPoint was originally 100, 
             then we would add this to the exception. (default exception is 50 and 100).
-->

<#if model.multiplier?has_content>
    <#assign multiplier = model.multiplier?number />
    <#assign ceiling = (markerPoint/multiplier)?ceiling * multiplier />
    <#assign markerPoint = (model.ceilingException![50,100])?seq_contains(ceiling)?then((markerPoint/multiplier)?floor * multiplier, ceiling) />
</#if>

<@component aria_label="${markerPoint?has_content?then(rating?floor + markerPoint / 100, rating)} out of ${model.count} stars">
<#compress>
    <#list 1..model.count as i>
        <div class="${generateReferenceIdClasses('__wrapper')}">
            <div class="${generateReferenceIdClasses('__background')}"></div>
            <div class="${generateReferenceIdClasses('__bar')}" style="width: ${(i <= rating?ceiling)?then((i <= rating)?then('100', markerPoint), '0')}%;"></div>
            <@svg name=model.icon classes="${generateReferenceIdClasses('__svg')}" />
        </div>
    </#list>
    <#if model.showLabel>
		<span class="${generateReferenceIdClasses('__label')}">${model.label!rating}</span>
	</#if>
</#compress>
</@component>