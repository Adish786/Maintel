<#-- This was originally cut and pasted from white-label's `figure-article` component. -->

<@component tag="figure" class="figure-" + (model.imageOrientation?lower_case) + " figure-" + (model.imageResolution?lower_case?replace("_", "-"))>
    <div class="figure-media">
		<@location name="overlay" tag="" />
        <#if (model.image!model.objectId)?has_content> <#-- Shouldn't be necessary with component collapsing but just in case -->
            <@thumborImg
				img=(model.image!model.objectId)
				width=model.width
				height=model.height
				alt=model.alt!''
				lazyload=model.lazyLoad!false
				placeholder=model.placeholder!true
				originalWidth=model.width
				originalHeight=model.height
				customPlaceholder=model.customPlaceholder!''
				srcset=model.srcset!{}
				data_expand="300"
				data_img_lightbox=model.lightbox?c
				data_click_tracked=(model.clickTracked!true)?c
				data_tracking_container="true"
				data_caption=model.caption!''
				data_owner=model.owner!''
				id="${'mntl-sc-block-image_' + manifest.instanceId?split('_')[1]}"
            />
		<#elseif model.src?has_content> <#-- will be used as a fallback in case we want to substitute with a static image -->
			<img src='${model.src}'
				data-click-tracked='${(model.clickTracked!true)?c}'
				data-tracking-container='true'
				data_caption=model.caption!''
				data_owner=model.owner!''
				data-expand="300"
				data-img-lightbox=${model.lightbox?c}
				id="${'mntl-sc-block-image_' + manifest.instanceId?split('_')[3]}"
			/>
        </#if>
    </div>
    <@location name="bottom" tag="" />
</@component>
