<template>
    <div class="feedback-summary">
        <div
            v-if="showNotification && !state.resoundSubmitError"
            :class="[
                'feedback-summary__notification',
                feedbackStatus && `feedback-summary__notification--${feedbackStatus}`
            ]"
        >
            <ugc-svg :id="`ugc-icon-review-${feedbackStatus}`" />
            <!-- eslint-disable vue/no-v-html -->
            <span
                class="feedback-summary__notification-text"
                v-html="feedbackStatusText"
            />
            <!--eslint-enable-->
        </div>
        <feedback
            :feedback="userSubmission"
            :avatarOverride="1"
            :isMyReview="true"
            displayName="My Review"
        >
            <template
                v-slot:edit-button
            >
                <button
                    class="feedback-summary__edit-button"
                    data-click-tracked="false"
                    @click="editFeedback"
                >
                    <ugc-svg id="ugc-icon-review-edit" />
                    <span class="feedback-summary__edit-button-text">
                        Edit
                    </span>
                </button>
            </template>
        </feedback>
    </div>
</template>

<script>
import Feedback from './feedback.vue';
import UgcSvg from './ugc-svg.vue';
import {
    dataLayerPush,
    stripHtml
} from '../utils';

export default {
    name: 'FeedbackSummary',
    components: {
        UgcSvg,
        Feedback
    },
    computed: {
        state() {
            return this.$store.state;
        },
        feedbackStatus() {
            return this.state.feedbackSummaryStore?.status;
        },
        feedbackStatusText() {
            return this.state.feedbackSummaryStore?.statusText[this.feedbackStatus];
        },
        userSubmission() {
            return this.state.userSubmissionStore?.data;
        },
        isRatingOnly() {
            return Boolean(this.state.userSubmissionStore?.data?.starRating)
                && Boolean(!this.state.userSubmissionStore?.data?.photoUrl?.length)
                && !stripHtml(this.state.userSubmissionStore?.data?.review);
        },
        showNotification() {
            return !this.isRatingOnly && (this.feedbackStatus === 'awaitingReview' || this.feedbackStatus === 'rejected');
        }
    },
    methods: {
        editFeedback() {
            this.$emit('edit-feedback');
            this.$store.dispatch('feedbackSummaryStore/setIsEditedReview', true);

            dataLayerPush('Edit My Review');
        }
    }
};
</script>
