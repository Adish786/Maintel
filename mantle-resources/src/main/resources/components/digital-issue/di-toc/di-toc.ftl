<@component tracking_container=true style=getDigitalIssueCssVariables(model.digitalIssueCssVariables)>

    <div class="${tocContentClass!'mntl-di-toc__content'}">
        <div class="mntl-di-toc__title ${model.titleClass!'type--zebra'}">Table of Contents</div>
        <div class="mntl-di-toc__list">
            <#list model.tocItems as item>
                <#if model.editorLetterDisplayIndex?has_content && model.editorLetterDisplayIndex == item?index>
                    <@a class="mntl-di-toc__list-item ${model.listItemClass!'type--dog'}" href="#toc-letter-from-the-editor">${model.editorLetterTitle}</@a>
                </#if>

                <@a class="mntl-di-toc__list-item ${model.listItemClass!'type--dog'}" href="#toc-${utils.formatForHash(item.shortHeading)}">${item.shortHeading}</@a>
            </#list>

            <#if !model.editorLetterDisplayIndex?has_content>
                <@a class="mntl-di-toc__list-item ${model.listItemClass!'type--dog'}" href="#toc-letter-from-the-editor">${model.editorLetterTitle}</@a>
            </#if>
        </div>
    </div>

</@component>