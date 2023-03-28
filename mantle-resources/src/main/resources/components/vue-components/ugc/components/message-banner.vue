<template>
    <div
        ref="messageBanner"
        :class="`message-banner message-banner--${alert.status}`"
    >
        <ugc-svg
            :id="`icon-${alert.status}`"
            class="message-banner__icon"
        />
        <span 
            ref="messageBannerText"
            class="message-banner__text" 
            v-html="alert.message">
        </span>
        <div class="message-banner__button-wrapper">
            <button
                class="message-banner__close-button"
                aria-label="Close Message"
                @click="closeBanner"
            >
                <ugc-svg
                    id="icon-close"
                    class="message-banner__icon"
                />
            </button>
        </div>
    </div>    
</template>

<script>
import UgcSvg from './ugc-svg.vue';
import {
    dataLayerPush
} from '../utils';

export default {
    name: 'MessageBanner',
    components: {
        UgcSvg
    },
    data() {
        return {
            auth0SignOutLink: null
        };
    },
    computed: {
        state() {
            return this.$store.state;
        },
        alert() {
            return this.state.messageBannerStore.alert;
        }
    },
    mounted() {
        this.auth0SignOutLink = document.querySelector('.mntl-ugc-vue-with-auth-auth0-sign-out');

        const logoutLinkInBanner = this.$refs.messageBannerText.querySelector('.ugc-logout');
        
        if(logoutLinkInBanner){
            logoutLinkInBanner.addEventListener('click', (event) => {
                event.preventDefault();
                const relativePath = window.location.href.replace(window.location.origin, '');
                
                window.localStorage.setItem('authRedirectUrl', `${relativePath}#scrollToUgc`);
                dataLayerPush('Rate & Review Submission Error', 'Error - Log Out');
                this.auth0SignOutLink.click();
            });
        }
    },
    methods: {
        closeBanner() {
            this.$store.dispatch('messageBannerStore/setBannerAlert', {
                message: '', 
                status: ''
            });
        }
    }
};
</script>
