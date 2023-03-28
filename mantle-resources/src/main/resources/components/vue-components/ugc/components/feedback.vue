<template>
    <div :class="['feedback', type]">
        <slot name="feedback-title" />
        <div class="feedback__heading">
            <!-- Not showing the user avatar in read only mode -->
            <span
                v-if="feedback.user && hasAvatar(feedback.user) && !state.readOnly"
            >
                <img 
                    :src="feedback.user.udf.image.original.src" 
                    :alt="userName"
                    class="feedback__avatar" 
                >
            </span>
            <span
                v-else
                class="feedback__avatar-wrapper"
            >
                <ugc-svg
                    :id="state.feedbackStore.fallbackAvatars[avatarIndex-1]"
                    :avatarIndex="avatarOverride || avatarIndex"
                />
            </span>
            <span class="feedback__display-name">
                <a
                    v-if="state.feedbackStore.linkDisplayName && profileUrl" 
                    :href="profileUrl"
                    data-click-tracked="false"
                    class="feedback__display-name-link"
                    @click="trackProfileLink($event)"
                >
                    {{ userName }}
                </a>
                <template
                    v-else
                >
                    {{ userName }}
                </template>
            </span>
            <slot name="edit-button" />
        </div>
        <div class="feedback__meta">
            <stars 
                :ratingValue="feedback.starRating"
            />
            <span class="feedback__meta-date">
                {{ publishedDate }}
            </span>
        </div>
        <div class="feedback__body-container">
            <div :class="['feedback__body', readMoreClass]">
                <div
                    v-if="userPhoto && state.feedbackStore.showPhotoNextToReview"
                    class="feedback__photo"
                >
                    <ugc-svg
                        id="ugc-icon-zoom-photo"
                    />
                    <img
                        class="feedback__photo-media mntl-sc-block-image"
                        data-img-lightbox="true"
                        :data-hi-res-src="userPhotoHiRes"
                        :src="userPhoto"
                        alt="User Photo Upload"
                    >
                </div>
                <div class="feedback__text-wrapper">
                    <!-- eslint-disable vue/no-v-html -->
                    <div
                        v-if="userReview"
                        ref="feedbackText" 
                        class="feedback__text"
                        v-html="userReview"
                    />
                    <!--eslint-enable-->
                    <a
                        href="#"
                        role="button"
                        data-click-tracked="false"
                        class="feedback__read-more"
                        @click.prevent="handleReadMore()"
                    >
                        Read More
                    </a>
                </div>
            </div>
            <helpful-button
                v-if="state.feedbackStore.helpfulEnabled && !isMyReview"
                :count="helpfulCount"
                :feedbackId="feedbackId"
            />
        </div>
    </div>
</template>

<script>
import UgcSvg from './ugc-svg.vue';
import Stars from './stars.vue';
import HelpfulButton from './helpful-button.vue';
import {
    getProp,
    pad,
    dataLayerPush
} from '../utils';

export default {
    name: 'Feedback',
    components: {
        UgcSvg,
        stars: Stars,
        HelpfulButton
    },
    props: {
        type: {
            type: String,
            default: 'review'
        },
        feedback: {
            type: Object,
            default: () => ({})
        },
        index: {
            type: Number,
            default: 0
        },
        avatarOverride: {
            type: Number,
            default: 0
        },
        isMyReview: {
            type: Boolean,
            default: false
        },
        displayName: {
            type: String,
            default: undefined
        }
    },
    data() {
        return {
            readMoreClass: null,
            readMoreClicked: false
        };
    },
    computed: {
        publishedDate() {
            const publishedDate = new Date(this.feedback.created);

            return `${pad(publishedDate.getUTCMonth() + 1)}/${pad(publishedDate.getUTCDate())}/${publishedDate.getUTCFullYear()}`;
        },
        mostPositiveReview() {
            return this.state.aggregatedFeedbacks?.mostHelpful?.positive;
        },
        mostCriticalReview() {
            return this.state.aggregatedFeedbacks?.mostHelpful?.critical;
        },
        avatarIndex() {
            const hasOneHelpfulReview = (this.mostPositiveReview && !this.mostCriticalReview) || (!this.mostPositiveReview && this.mostCriticalReview);
            const hasBothHelpfulReviews = this.mostPositiveReview && this.mostCriticalReview;
            let avatarIndex;
            
            if (this.canAddFeedback) {
                // Read + write
                if (this.avatarOverride) {
                    // Use My Review and Most Helpful Review index overrides
                    avatarIndex = this.avatarOverride;
                } else if (hasOneHelpfulReview) {
                    // Reviews list avatars start at 3rd index
                    avatarIndex = ((this.index + 2) % this.state.feedbackStore.numberOfFallbackAvatars) + 1;
                } else if (hasBothHelpfulReviews) {
                    // Reviews list avatars start at 4th index
                    avatarIndex = ((this.index + 3) % this.state.feedbackStore.numberOfFallbackAvatars) + 1; 
                } else {
                    // If My Review only, reviews list avatars start at 2nd index
                    avatarIndex = ((this.index + 1) % this.state.feedbackStore.numberOfFallbackAvatars) + 1;
                }
            } else {
                // Read-only: reviews list avatars start at 1st index
                avatarIndex = (this.index % this.state.feedbackStore.numberOfFallbackAvatars) + 1;
            }

            return avatarIndex;
        },
        state() {
            return this.$store.state;
        },
        userName() {
            return (
                this.displayName || 
                this.feedback.user?.displayName || 
                this.feedback.displayName
            );
        },
        profileUrl() {
            return this.feedback.profileUrl;
        },
        userReview() {
            return this.feedback.review;
        },
        helpfulCount() {            
            return this.feedback.helpfulCount;
        },
        feedbackId() {            
            return this.feedback.legacyFeedbackId;
        },
        userPhoto() {
            if (this.feedback.photoUrl) {
                return `https://imagesvc.meredithcorp.io/v3/mm/image?url=${encodeURIComponent(this.feedback.photoUrl)}&w=150&c=sc&poi=face&q=60&orient=true`;
            }

            if (this.feedback.photos?.length) {
                const photo = this.feedback.photos[0].url;

                return `https://imagesvc.meredithcorp.io/v3/mm/image?url=${encodeURIComponent(photo)}&w=150&c=sc&poi=face&q=60&orient=true`;
            }
            
            return null;
        },
        userPhotoHiRes() {
            if (this.feedback.photoUrl) {
                return `https://imagesvc.meredithcorp.io/v3/mm/image?url=${encodeURIComponent(this.feedback.photoUrl)}&c=sc&poi=face&q=60&orient=true`;
            }

            if (this.feedback.photos?.length) {
                const photo = this.feedback.photos[0].url;
                
                return `https://imagesvc.meredithcorp.io/v3/mm/image?url=${encodeURIComponent(photo)}&c=sc&poi=face&q=60&orient=true`;
            }
            
            return null;
        },
        canAddFeedback() {
            return this.state.auth0Enabled && !this.state.readOnly;
        }
    }, 
    mounted() {
        this.addReadMore();
    }, 
    updated() {
        this.addReadMore();
    },
    methods: {
        hasAvatar(user) {
            return getProp(user, 'udf.image.original.src');
        },
        trackProfileLink(e) {
            dataLayerPush(e.target.href, 'Open User Profile');
        },
        addReadMore() {
            if (!this.readMoreClicked) {
                const element = this.$refs.feedbackText;
                const hasOverflow = element ? element.scrollHeight > element.clientHeight || element.scrollWidth > element.clientWidth : false;

                if (hasOverflow) {
                    this.readMoreClass = 'feedback__body--show-read-more';
                } else {
                    this.readMoreClass = null;
                }
            }
        },
        handleReadMore() {            
            dataLayerPush('Read More');

            this.readMoreClass = 'feedback__body--show-read-more-expanded';
            this.readMoreClicked = true;
        }
    }
};
</script>
