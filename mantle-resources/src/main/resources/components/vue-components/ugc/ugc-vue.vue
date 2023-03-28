<template>
    <!-- Show this section if there are review or the user can write feedback -->
    <div
        v-if="total > 0 || canWriteFeedback"
        class="feedback-list"
    >
        <!-- Reviews heading on LM is h2. need to verify -->
        <h2 class="feedback-list__heading">
            Reviews
            <span 
                v-if="total > 0" 
                class="feedback-list__heading-count"
            >
                ({{ total.toLocaleString() }})
            </span>
        </h2>

        <div
            v-if="canWriteFeedback"
        >
            <!-- eslint-disable vue/no-v-html -->
            <p
                class="feedback-list__guidelines"
                v-html="addFeedbackGuidelinesText"
            />
            <!--eslint-enable-->
            <add-feedback
                ref="addFeedback"
                :addFeedbackIcon="addFeedbackIcon"
            />
        </div>

        <!-- If total is greater than 0 -->
        <div 
            v-if="total > 0"
        >
            <!-- No sorting for read only mode -->
            <div
                v-if="state.sortStore.sortEnabled && !state.readOnly"
            >
                <sort 
                    :sortingOptions="sortingOptions"
                    :selectedOptionValue="defaultSort"
                />
            </div>
            <div 
                :class="{
                    'feedback-list__items': true,
                    'feedback-loading':state.sortStore.sortLoading
                }"
            >
                <div 
                    v-for="(feedback, index) in state.feedbackList" 
                    :key="index"
                    class="feedback-list__item"
                >
                    <feedback
                        :index="index"
                        :feedback="feedback"
                        :type="type"
                    />
                    <div 
                        v-if="showAd(index)" 
                        class="mntl-mobSquareFixed-sticky-dynamic mntl-sc-sticky-billboard"
                    >
                        <div class="mntl-billboard mntl-sc-sticky-billboard-ad mntl-dynamic-billboard mntl-gpt-dynamic-adunit mntl-gpt-adunit gpt billboard dynamic js-lazy-ad">
                            <div 
                                id="mob-square-fixed" 
                                class="wrapper" 
                                data-type="billboard" 
                                data-pos="atf" 
                                data-priority="2" 
                                data-sizes="[[300, 250],[299, 251],&quot;fluid&quot;]" 
                                data-rtb="true" 
                                data-wait-for-third-party="false" 
                                data-targeting="{}" 
                                :data-auction-floor-id="getAuctionId()" 
                                :data-auction-floor-value="getAuctionFloor()" 
                            />
                        </div>
                    </div>
                </div>
                <div 
                    v-if="hasLoadMore" 
                    :class="{
                        'feedback-list__load-more':true,
                        'feedback-loading':state.loadMoreLoading,
                        'hide':state.sortStore.sortLoading
                    }"
                >
                    <button 
                        class="feedback-list__load-more-button"
                        data-click-tracked="false"
                        @click="loadMore"
                    >
                        Load More {{ type }}s
                    </button>
                </div>
            </div>
        </div>
        <!-- Else -->
        <div 
            v-else
            class="feedback-list__no-feedback"
        >
            Be the first to {{ type }}!
        </div>
    </div>
</template>

<script>
import Feedback from './components/feedback.vue';
import AddFeedback from './components/add-feedback.vue';
import Sort from './components/sort.vue';
import {
    dataLayerPush
} from './utils';

export default {
    name: 'UgcVue',
    components: {
        'add-feedback': AddFeedback,
        'feedback': Feedback,
        'sort': Sort
    },
    props: {
        auth0Enabled: {
            type: Boolean,
            default: false
        },
        hashId: {
            type: String,
            default: ''
        },
        readOnly: {
            type: Boolean,
            default: true
        },
        apiBase: {
            type: String,
            default: null
        },
        docId: {
            type: Number,
            default: null
        },
        legacyMeredithId: {
            type: String,
            default: null
        },
        fallbackAvatars: {
            type: String,
            default() {
                return '["ugc-icon-avatar"]';
            }
        },
        numberOfFallbackAvatars: {
            type: Number,
            default: 5
        },
        type: {
            type: String,
            default: 'review'
        },
        page: {
            type: Number,
            default: 1
        },
        hasReviewAds: {
            type: Boolean,
            default: false
        },
        adIteration: {
            type: Number,
            default: null
        },
        numberPerPage: {
            type: Number,
            default: 9
        },
        sortEnabled: {
            type: Boolean,
            default: false
        },
        defaultSort: {
            type: String,
            default: 'DATE_DESC'
        },
        addFeedbackIcon: {
            type: String,
            default: 'ugc-icon-add-feedback'
        },
        availableSortOptions: {
            type: String,
            default: '[ {"label": "Newest", "option": "DATE_DESC"}, {"label": "Oldest", "option": "DATE_DESC"} ]'
        },
        addFeedbackGuidelinesText: {
            type: String,
            default: ''
        },
        articleTitle: {
            type: String,
            default: ''
        },
        addFeedbackCta: {
            type: String,
            default: ''
        },
        feedbackStatus: {
            type: String,
            default: 'awaitingReview'
        },
        feedbackStatusPendingText: {
            type: String,
            default: ''
        },
        feedbackStatusRejectedText: {
            type: String,
            default: ''
        },
        photoUploadFileLimitSize: {
            type: Number,
            default: 30
        },
        photoUploadRequirementsText: {
            type: String,
            default: '["PNG, GIF, JPEG formats only", "Minimum dimensions 960x960", "Max file size: 30MB"]'
        },
        errorMessageUploadImageSize: {
            type: String,
            default: ''
        },
        errorMessageUploadFileSize: {
            type: String,
            default: ''
        },
        errorMessageUploadCombined: {
            type: String,
            default: ''
        },
        errorMessageFormGeneric: {
            type: String,
            default: ''
        },
        helpfulEnabled: {
            type: Boolean,
            default: false
        },
        helpfulClickableEnabled: {
            type: Boolean,
            default: false
        },
        linkDisplayName: {
            type: Boolean,
            default: false
        },
        photoUploadEnabled: {
            type: Boolean,
            default: false
        },
        contentGraphEnabled: {
            type: Boolean,
            default: false
        },
        showMostHelpfulReviews: {
            type: Boolean,
            default: false
        },
        aggregatedFeedbacks: {
            type: String,
            default: ''
        },
        ratingsHistogramEnabled: {
            type: Boolean,
            default: false
        },
        showPhotoNextToReview: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            addFeedback: null
        };
    },
    computed: {
        state() {
            return this.$store.state;
        },
        total() {
            return parseInt(this.state.total, 10);
        },
        sortingOptions() {
            try {
                const options = JSON.parse(this.availableSortOptions);

                // Select the default
                options.forEach((option) => {
                    if (option.option === this.defaultSort) {
                        option.selected = true;
                    }
                });

                return options;
            } catch(err) {
                console.log(`Unable to json parse availableSortOptions ${this.availableSortOptions}`, err);
                
                return [];
            }
        },
        hasLoadMore() {
            return this.$store.getters.hasLoadMore;
        },
        canWriteFeedback() {
            // User can only write feedback if auth0 is enabled and ugc is not read-only
            return this.state.auth0Enabled && !this.state.readOnly;
        }
    },
    created() {
        this.$store.dispatch('setUgcData', this.$props);
    },
    mounted() {
        this.addFeedback = this.$refs.addFeedback;

        if (this.addFeedback) {
            this.loginEventListener();
        }

        // Load the lightbox library if it hasn't already been loaded
        if(this.state.feedbackStore.showPhotoNextToReview && !document.getElementById('mntl-lightbox')) {
            window.Mntl.utilities.loadExternalJS({
                src: `${window.Mntl.utilities.getStaticPath()}/static/mantle/components/structured-content/image/createLightboxCarousel.js`
            });
        }

        document.body.dispatchEvent(new CustomEvent('mntl.infiniterightrail.enable.expand'));

        document.body.addEventListener('mntl.infiniterightrail.loaded', () => {
            document.body.dispatchEvent(new CustomEvent('mntl.infiniterightrail.enable.expand'));
        });
    },
    methods: {
        loadMore() {
            dataLayerPush(window.location.href, 'Load More Reviews');

            this.$store.dispatch('setLoadMore');
        },
        loginEventListener() {
            document.addEventListener(Mntl.auth0.LOG_IN_EVENT_NAME, (event) => {
                this.$store.dispatch('setHashId', event.detail.userHashId);
                this.addFeedback.getReviewStatus();
            });
        },
        showAd(index) {
            const isIterationLocation = ((index + 1) % this.state.adIteration === 0) && (index + 1) % this.state.numberPerPage !== 0;

            return this.state.hasReviewAds && isIterationLocation;
        },
        getAuctionId() {
            return Mntl.GPT.getFloorIdByAdUnitType('mob-square-fixed');
        },
        getAuctionFloor() {
            return Mntl.GPT.getFloorValueByAdUnitType('mob-square-fixed');
        }
    }
};
</script>