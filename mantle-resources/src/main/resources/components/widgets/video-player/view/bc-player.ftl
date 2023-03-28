<#if model.accountId?has_content>
    <@component>
        <@location name="pre-video" tag="" />
        <#if model.nativePlayer!false>
            <div class="jumpstart-native-wrapper ${(model.lazyLoad!false)?then('jumpstart-js-wrapper--lazy', '')}">
                <video class="jumpstart-native-video jumpstart-gif-js"
                    data-src="https://playback.video.meredithcorp.io/${model.accountId!''}/${model.videoId!''}.mp4"
                    autoplay muted loop playsinline>
                </video>
            </div>
        <#else>
            <#if (model.thumbnailUrl)?has_content>
                <#assign thumbnailUrl = "https://imagesvc.meredithcorp.io/v3/jumpstartpure/image/?url=${(model.thumbnailUrl)?url}&w=640&h=360&q=90&c=cc" />
            <#else>
                <#assign thumbnailUrl = "" />
            </#if>    
            <div class="jumpstart-js-wrapper ${(model.lazyLoad!false)?then('jumpstart-js-wrapper--lazy', '')} ${extractClasses(model.innerClass)}">
                <#-- If autoplay is on we want to start muted so guarantee jumpstart-video-muted class is set -->
                <#-- If jumpstart-video-muted class is not set it causes some cases of the bug seen in GLBE-9572 where video volume toggle breaks -->
                <video class="jumpstart-js ${(model.autoplay!false)?then('jumpstart-video-muted', '')}"
                    data-brand="${model.brand!''}"
                    data-carbon="true"
                    data-click-load="${(model.clickLoad!false)?c}"
                    data-account="${model.accountId!''}"
                    data-video-id="${model.videoId!''}"
                    data-preventstart="${((model.preventStart || model.lazyLoad)!false)?c}"
                    data-autoplay="${(model.autoplay!false)?c}"
                    data-mobile-autoplay="${(model.mobileAutoplay!false)?c}"
                    data-stickyplay="${(model.stickyplay!false)?c}"
                    data-is-lead="${(model.isLeadVideo!false)?c}"
                    data-metadata="${model.videoMetadata!''}"
                    data-dynamic-embed="${(model.dynamicEmbed!'')?replace("'", "&quot;")}"
                    data-is-broad="${model.isBroadVideo?c}"
                    <#if thumbnailUrl?has_content && model.posterLazyLoad!false>
                    data-poster="${thumbnailUrl}"
                    <#elseif thumbnailUrl?has_content>
                    poster="${thumbnailUrl}"
                    </#if>
                    >
                </video>
                <#-- Spoof a play button in a style similar to bc-player's player -->
                <#if (model.clickLoad)!false>
                <div class="jumpstart-play-placeholder">
                    <button class="jumpstart-play-placeholder-icon" aria-label="Play Video"></button>
                </div>  
                </#if>
            </div>
        </#if>
    </@component>
</#if>
