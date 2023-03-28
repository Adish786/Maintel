<@component tag="ul" class=(model.isExpanded)?then('', 'js-accordion')>

    <#list model.list as accordionItem>

        <#assign accordionItemClass = model.accordionItemClass!""/>

        <#-- 
            If the ID of the row is specified and an Active ID is provided, 
            the `is-active` class will be automatically attached to that item.
        -->
        <#if model.itemPropId?has_content && model.activeItemId?has_content && (accordionItem[model.itemPropId] == model.activeItemId)>
            <#assign accordionItemClass = "is-active is-current" />
        </#if>

        <#-- 
            This is specifically for a list of Document List.
            If the isListofDocumentList property is flaged true and a activeItemId is provided,
            the top level item will be given an `is-active` class.
        -->
        <#if model.itemContentMap?has_content && model.isListofDocumentList!false == true && accordionItem[model.itemContentMap]?has_content >
            <#list accordionItem[model.itemContentMap] as item>
                <#if (item.document.documentId == model.activeItemId)>
                    <#assign accordionItemClass = "is-active is-current" />
                    <#break>
                </#if>
            </#list>
        </#if>

        <#if model.enableClickTracking?has_content && model.enableClickTracking == true>
            <#assign clickTracking = "data-click-tracked='true' data-tracking-container='true'" />
        </#if>

        <#assign idSuffix = "-${model.sectionName!'section'}-${accordionItem?index}"/>

        <li class="accordion__item js-accordion-item ${accordionItemClass}">
            <#if model.showAccordianHeader!true>
                <div class="accordion__header js-accordion-trigger" ${clickTracking!''} id="${manifest.component.uniqueId}${idSuffix}">
                    <#if model.image?has_content && model.image.objectId?has_content>
                        <@thumborImg class=model.imgClass!'' img=model.image width=model.imgWidth!25 height=model.imgHeight!0 lazyload=model.lazyload!true />
                    </#if>
                    <#if model.icon?has_content >
                        <@svg name=model.icon classes="accordion__icon" />
                    </#if>
                    <@location name="accordion-header" tag="" idSuffix=idSuffix />
                    <${model.headingTag!"span"} class="accordion__title">${accordionItem[model.itemHeadingMap!'heading']}</${model.headingTag!"span"}>
                </div><!--end accordion__header-->
            </#if>

            <#--
                itemContenMap - to support dynamic content, this configuration will map which property of the list 
                                data will be passed to the body.
            -->
            <#if model.itemContentMap?has_content >
                <#assign itemContent = accordionItem[model.itemContentMap] />
            <#else>
                <#assign itemContent = accordionItem />
            </#if>

            <#--
                 itemContentDataWrapper - if the body requires a specific property name, this configuration can be 
                                used to wrap the data object. 
            -->
            <#if model.itemContentDataWrapper?has_content >
                <#assign itemContent = { model.itemContentDataWrapper : itemContent} />
            </#if>

            <div class="accordion__body">
                <@location name="accordion-content-${accordionItem?index}" tag="" models=itemContent idSuffix=idSuffix />
                <@location name="accordion-content" tag="" models=itemContent idSuffix=idSuffix />
            </div><!--end accordion__body-->

        </li><!--end accordion__item-->

    </#list>

</@component><!--end accordion -->
