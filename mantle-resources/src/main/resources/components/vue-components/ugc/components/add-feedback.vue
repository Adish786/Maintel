<template>
    <div ref="addFeedbackContainer" class="feedback-list__add-feedback">
        <message-banner
            v-if="showBanner()"
        />
        <div class="feedback-list__add-feedback-inner">
            <div
                v-if="isCtaVisible"
                :class="['feedback-list__add-feedback-cta', { 'feedback-loading': isLoading }]"
            >
                <div class="feedback-list__add-feedback-icon">
                    <ugc-svg :id="addFeedbackIcon" />
                </div>
                <p
                    v-if="addFeedbackCta"
                    class="feedback-list__add-feedback-text"
                >
                    {{ addFeedbackCta }}
                </p>
                <button
                    class="feedback-list__add-feedback-button"
                    data-click-tracked="false"
                    @click="showForm(true)"
                >
                    Add Rating &amp; Review
                </button>
            </div>
  
            <div
                v-else
                :class="['feedback-list__add-feedback-wrapper', { 'feedback-loading': isLoading }]">
                <feedback-form
                    v-if="isFormVisible"
                    :reviewExists="userReviewExists"
                    @form-submitted="onSubmit"
                    @form-cancelled="onCancel"
                    @form-loading="onLoading"
                    @form-submitted-error="onSubmitError"
                />
                <feedback-summary
                    v-else
                    @edit-feedback="showForm(false)"
                />
            </div>

            <ratings-histogram v-if="showRatingsHistogram" />
            <div
                v-if="state.addFeedbackStore.showMostHelpfulReviews && mostHelpfulReviews"
                class="feedback__most-helpful"
            >
                <feedback
                    v-if="mostPositiveReview"
                    :feedback="mostPositiveReview"
                    :avatarOverride="2"
                >
                    <template
                        v-slot:feedback-title
                    >
                        <div class="feedback__title">
                            Most helpful positive review
                        </div>    
                    </template>
                </feedback>
                <feedback
                    v-if="mostCriticalReview"
                    :feedback="mostCriticalReview"
                    :avatarOverride="mostCriticalAvatar"
                >
                    <template
                        v-slot:feedback-title
                    >
                        <div class="feedback__title">
                            Most helpful critical review
                        </div>    
                    </template>
                </feedback>
            </div>
        </div>
    </div>
</template>

<script>
import UgcSvg from './ugc-svg.vue';
import FeedbackForm from './feedback-form.vue';
import FeedbackSummary from './feedback-summary.vue';
import Feedback from './feedback.vue';
import MessageBanner from './message-banner.vue';
import {
    fetchAccessToken,
    reviewStatusCheck,
    dataLayerPush
} from '../utils';
import RatingsHistogram from './ratings-histogram.vue';

export default {
    name: 'AddFeedback',
    components: {
        UgcSvg,
        FeedbackForm,
        FeedbackSummary,
        Feedback,
        MessageBanner,
        RatingsHistogram
    },
    props: {
        addFeedbackIcon: {
            type: String,
            default: 'add-feedback'
        }
    },
    data() {
        return {
            isCtaVisible: true,
            isFormVisible: false,
            auth0Link: null,
            isLoading: false,
            userReviewExists: false,
            isCtaClick: false
        };
    },
    computed: {
        state() {
            return this.$store.state;
        },
        addFeedbackCta() {
            return this.state.addFeedbackStore?.cta;
        },
        isLoggedIn() {
            return this.state.hashId?.length > 0 && window.docCookies.getItem('ddmaccount');
        },
        accessToken() {
            return this.state.accessToken;
        },
        showRatingsHistogram() {
            // Only show the histogram if it's enabled and there are ratings
            return this.state.addFeedbackStore?.ratingsHistogramEnabled && this.state.total;
        },
        mostHelpfulReviews() {
            return this.state.aggregatedFeedbacks?.mostHelpful;
        },
        mostPositiveReview() {
            return this.state.aggregatedFeedbacks?.mostHelpful?.positive;
        },
        mostCriticalReview() {
            return this.state.aggregatedFeedbacks?.mostHelpful?.critical;
        },
        mostCriticalAvatar() {
            let index;

            if (this.mostCriticalReview && !this.mostPositiveReview) {
                index = 2;
            } else if (this.mostCriticalReview && this.mostPositiveReview) {
                index = 3;
            }

            return index;
        }
    },
    mounted() {
        this.isFormVisible = this.isLoggedIn;
        this.isCtaVisible = !this.isLoggedIn;
        this.getReviewStatus();

        // Assuming this exists because of ugc-vue should be dependent on auth0 component
        this.auth0Link = document.querySelector('.mntl-ugc-vue-with-auth-auth0');
    },
    methods: {
        showBanner() {
            const hasMessage = this.state.messageBannerStore?.alert?.message?.length;
            const scrollTo = window.location.hash.includes('scrollToUgc');

            if (hasMessage || scrollTo) {
                this.$nextTick(() => {
                    // If there exists a fixed leaderboard ad, make sure the ad doesn't cover up the error message.
                    const leaderboardAd = document.querySelector('.mntl-leaderboard-fixed');

                    if (leaderboardAd) {
                        window.scroll({
                            top: this.$refs.addFeedbackContainer.offsetTop,
                            left: 0
                        });
                    } else {
                        this.$refs.addFeedbackContainer.scrollIntoView(true);
                    }
                });
            }
            
            return hasMessage;
        },
        getReviewStatus() {
            this.isLoading = true;
            fetchAccessToken(this.isLoggedIn, this.state, this.auth0Link)
                .then(() => {
                    reviewStatusCheck(this.state)
                        .then(() => {
                            this.isLoading = false;
                            this.isCtaVisible = false;
                            this.isFormVisible = false;
                            this.userReviewExists = true;
                        })
                        .catch((status) => {
                            // If access token issue, try getting a new access token
                            if (status === 403) {
                                Mntl.auth0.getAccessToken().then(() => {
                                    this.$store.dispatch('setAccessToken', Mntl.auth0.accessToken);

                                    reviewStatusCheck(this.state)
                                        .then(() => {
                                            this.isLoading = false;
                                            this.isCtaVisible = false;
                                            this.isFormVisible = false;
                                            this.userReviewExists = true;
                                        })
                                        .catch(() => {
                                            this.isLoading = false;
                                            this.isCtaVisible = false;
                                            this.isFormVisible = true;
                                            this.userReviewExists = false;
                                        });
                                });
                            } else {
                                this.isLoading = false;
                                this.isCtaVisible = false;
                                this.isFormVisible = true;
                                this.userReviewExists = false;
                            }
                        });
                })
                .catch((error) => { 
                    this.isLoading = false;
                    console.error(error);
                });
        },
        showForm(isCtaClick) {
            if (this.isLoggedIn) {
                this.isCtaVisible = false;
                this.isFormVisible = true;
                if (
                    this.state.userSubmissionStore?.data?.starRating
                    && this.state.userSubmissionStore?.data?.photoUrl
                ) {
                    this.userReviewExists = true;
                }
            } else if (this.auth0Link) {
                this.auth0Link.click();
            }

            if (isCtaClick) {
                dataLayerPush('Add Rating & Review Button');
            }
        },
        onSubmit() {
            this.isLoading = false;
            this.isFormVisible = false;
            this.isCtaVisible = false;
        },
        onCancel() {
            this.isLoading = false;
            this.isFormVisible = false;
            this.isCtaVisible = !this.isLoggedIn;
        },
        onSubmitError() {
            this.isLoading = false;
            this.showForm();
        },
        onLoading() {
            this.isLoading = true;
        }
    }
};
</script>