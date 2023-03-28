<@component tracking_container=true>
    <button class="mntl-di-nav__header" aria-label="Open digital issue navigation">
        <span class="mntl-di-nav__header-title ${model.titleClass!'type--dog-link-bold'}">
            ${model.title!model.journeyRoot.shortHeading!model.journeyRoot.document.title}
        </span>

        <@svg classes="mntl-di-nav__header-icon" name=model.navIcon!'icon-caret' />
    </button>

    <div class="mntl-di-nav__content">
        <ul class="mntl-di-nav__list">
            <li class="mntl-di-nav__singular-container">
                <@a class="mntl-di-nav__overview" href=model.journeyRoot.document.url>
                    <@location
                            name="thumbnail"
                            tag=""
                            models={
                                'image': model.journeyRoot.document.getImageForUsage(model.overviewImageTag!'PRIMARY'),
                                'width': model.overviewThumbnailWidth!128,
                                'placeholderWidth': model.overviewThumbnailWidth!128,
                                'height': model.overviewThumbnailHeight!171,
                                'placeholderHeight': model.overviewThumbnailHeight!171
                    } />

                    <span class="mntl-di-nav__overview-text ${model.singularTitleClass!'type--monkey-link-bold'}">
                        ${model.overviewTitle!('Overview: ' + (model.journeyRoot.shortHeading!model.journeyRoot.document.title))}
                    </span>
                </@a>

                <#list model.journeySectionSizeMap.singleDocumentSections as singularSection>
                    <#assign firstChild = singularSection.journeyDocuments[0] />

                    <div class="mntl-di-nav__section--singular ${(model.currentJourneySection?has_content && singularSection.node.docId == model.currentJourneySection.node.docId)?then('is-highlighted', '')}">
                        <@a href=firstChild.document.url class="mntl-di-nav__section-link">
                            <@location
                                    name="thumbnail"
                                    tag=""
                                    models={
                                        'image': firstChild.document.getImageForUsage(model.singularSectionImageTag!'RECIRC'),
                                        'width': model.singularSectionThumbnailWidth!80,
                                        'placeholderWidth': model.singularSectionThumbnailWidth!80,
                                        'height': model.singularSectionThumbnailHeight!80,
                                        'placeholderHeight': model.singularSectionThumbnailHeight!80
                            } />

                            <span class="mntl-di-nav__singular-section-title ${model.singularTitleClass!'type--monkey-link-bold'}">
                                ${singularSection.shortHeading!firstChild.document.title}
                            </span>

                            <span class="mntl-di-nav__singular-document-title ${model.singularDocumentTitleClass!'type--cat-link'}">
                                ${firstChild.shortHeading!firstChild.document.title}
                            </span>
                        </@a>
                    </div>
                </#list>
            </li>

            <li class="mntl-di-nav__multiple-container">
                <#assign multipleSectionImageTag = model.multipleSectionImageTag!'PRIMARY' />
                <#assign multipleSectionImageTagFallback = (multipleSectionImageTag == 'PRIMARY')?then('RECIRC', 'PRIMARY') />

                <#list model.journeySectionSizeMap.multipleDocumentSections as multipleSection>
                    <#assign firstChild = multipleSection.journeyDocuments[0].document />

                    <div class="mntl-di-nav__section--multiple ${(model.currentJourneySection?has_content && multipleSection.node.docId == model.currentJourneySection.node.docId)?then('is-expanded', '')}">
                        <@location
                                name="thumbnail"
                                tag=""
                                models={
                                    'image': firstChild.getImageForUsage(multipleSectionImageTag).isNotEmpty()?then(firstChild.getImageForUsage(multipleSectionImageTag), firstChild.getImageForUsage(multipleSectionImageTagFallback)),
                                    'width': model.multipleSectionThumbnailWidth!80,
                                    'placeholderWidth': model.multipleSectionThumbnailWidth!80,
                                    'height': model.multipleSectionThumbnailHeight!80,
                                    'placeholderHeight': model.multipleSectionThumbnailHeight!80
                        } />

                        <div class="mntl-di-nav__multiple-section-title-wrapper">
                            <span class="mntl-di-nav__multiple-section-title ${model.multipleHeadingClass!'type--cat-bold'}">
                                ${multipleSection.shortHeading!multipleSection.document.title}
                            </span>

                            <@svg classes="mntl-di-nav__multiple-section-title-icon" name=model.sectionTitleIcon!'icon-caret' />
                        </div>

                        <ul class="mntl-di-nav__sublist">
                            <#list multipleSection.journeyDocuments as journeyDocument>
                                <li class="mntl-di-nav__sublist-item ${(journeyDocument.document.documentId == requestContext.urlData.docId)?then('is-highlighted', '')}">
                                    <@a class="mntl-di-nav__multiple-document-title ${model.multipleTitleClass!'type--cat-link'}" href=journeyDocument.document.url>
                                        ${journeyDocument.shortHeading!journeyDocument.document.title}
                                    </@a>
                                </li>
                            </#list>
                        </ul>
                    </div>
                </#list>
            </li>
        </ul>
    </div>
</@component>