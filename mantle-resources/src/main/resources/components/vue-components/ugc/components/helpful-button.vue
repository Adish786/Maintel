<template>
    <component
        :is="elementTag"
        :class="['feedback__helpful', {
            'feedback__helpful--marked': isIncrement
        }]"
        data-click-tracked="false"
        @[eventHandler]="toggleHelpful"
    >
        <span class="feedback__helpful-icon">
            <ugc-svg
                id="ugc-icon-thumb-up"
            />
        </span>
        <span class="feedback__helpful-counts">
            {{ helpfulCount }}
        </span>
    </component>
</template>

<script>
import Svg from './ugc-svg.vue';
import {
    fetchAccessToken,
    dataLayerPush
} from '../utils';

export default {
    name: 'HelpfulButton',
    components: {
        'ugc-svg': Svg
    },
    props: {
        count: {
            type: Number,
            default: 0
        },
        feedbackId: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            currentCount: 0,
            isIncrement: false,
            auth0Link: null,
            auth0Cookie: null
        };
    },
    computed: {
        state() {
            return this.$store.state;
        },
        docId() {
            return this.state.docId;
        },
        accessToken() {
            return this.state.accessToken;
        },
        helpfulClickableEnabled() {
            return this.state.feedbackStore.helpfulClickableEnabled;
        },
        elementTag() {
            return this.helpfulClickableEnabled ? 'button' : 'div';
        },
        eventHandler() {
            return this.helpfulClickableEnabled ? 'click' : null;
        },
        helpfulCount() {
            return `Helpful (${this.currentCount.toLocaleString()})`;
        }
    },
    watch: {
        count(newCount) {
            this.currentCount = newCount;
        }
    },
    mounted() {
        this.currentCount = this.count;
        this.auth0Link = document.querySelector('.mntl-ugc-vue-with-auth-auth0');
        this.auth0Cookie = window.docCookies.getItem('ddmaccount');
    },
    methods: {
        handleAuth0LoggedOut() {
            this.auth0Link.click();

            document.addEventListener(Mntl.auth0.LOG_IN_EVENT_NAME, () => this.handleMarkHelpful());
        },
        handleMarkHelpful() {
            this.auth0Cookie = window.docCookies.getItem('ddmaccount');
            
            if (this.auth0Cookie) {
                fetchAccessToken(this.auth0Cookie, this.state, this.auth0Link)
                    .then(() => {
                        const apiEndpoint = '/resound/react';
                        const formData = new FormData();
                        const currentUrl = window.location.href;

                        this.isIncrement = !this.isIncrement;
                        
                        formData.append('docId', this.docId);
                        if (this.feedbackId) {
                            formData.append('legacyFeedbackId', this.feedbackId);
                        }
                        formData.append('type', 'helpful');
                        formData.append('accessToken', this.accessToken);

                        if (this.isIncrement) {                
                            this.currentCount++;
                            
                            Mntl.utilities.ajaxPromisePost(`${apiEndpoint}/save`, formData, null)
                                .then(() => {
                                    dataLayerPush(currentUrl, 'Helpful');
                                })
                                .catch((error) => {
                                    console.error(`Error with POST request to ${apiEndpoint}/save:`, error);
                                });
                        } else {
                            this.currentCount--;
                            
                            Mntl.utilities.ajaxPromisePost(`${apiEndpoint}/delete`, formData, null)
                                .then(() => {
                                    dataLayerPush(currentUrl, 'Not Helpful');
                                })
                                .catch((error) => {
                                    console.error(`Error with POST request to ${apiEndpoint}/delete:`, error);
                                });
                        }
                    })
                    .catch(() => {
                        this.handleAuth0LoggedOut();
                    });
            }

            document.removeEventListener(Mntl.auth0.LOG_IN_EVENT_NAME, () => this.handleMarkHelpful());
        },
        toggleHelpful() {
            this.handleMarkHelpful();

            if (!this.auth0Cookie && this.auth0Link) {
                this.handleAuth0LoggedOut();
            }
        }
    }
};
</script>
