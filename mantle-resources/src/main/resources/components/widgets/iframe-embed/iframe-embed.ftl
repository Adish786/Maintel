<iframe-embed inline-template vertical="${model.vertical!''}" tool-id="${model.toolId!''}">
    <@component>
        <a :class="['iframe-embed__toggle', show ? 'opened' : 'closed']" @click='show = !show' v-cloak>
            ${model.btnText!'Embed This Tool'}
            <@svg name="${model.svgName!''}" classes="${model.svgClasses!''} iframe-embed__toggle-icon" />
        </a>
        <transition name="${model.transition!''}">
            <div class="iframe-embed__form" v-if="show" v-cloak> 
                <p>${model.instructionsText!'To embed this tool on your site, copy and paste the code below:'}</p>
                <textarea class="iframe-embed-form__script-textbox" @mouseup.stop.prevent="selectAllText" @touchend.stop.prevent="selectAllText" rows="4"><script type="text/javascript" async id="{{toolId}}" data-type="dotdash-tool" data-vertical="{{vertical}}" src="https://www.{{vertical}}.com/static/mantle/${projectInfo.version}/static/mantle/components/widgets/iframe-embed/embed.min.js?id={{toolId}}"></script></textarea>

                <p class="iframe-embed-form__copied-notification" v-show="copy">The code has been copied to your clipboard.</p>
                <button class="iframe-embed-form__copy-button" @click="selectAllText">Copy Code</button>
            </div>
        </transition>
    
    </@component>
</iframe-embed>