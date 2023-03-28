<template>
    <div class="feedback-form">
        <div class="feedback-form__heading">
            <!-- TODO: handle user avatar vs. read-only mode -->
            <ugc-svg
                :id="state.feedbackStore.fallbackAvatars[0]"
                :avatarIndex="1"
            />
            <div class="feedback-form__recipe-title">
                {{ articleTitle }}
            </div>
        </div>
        <div class="feedback-form__rating">
            <p
                class="feedback-form__field-label"
            >
                Your Rating
                <span class="feedback-form__field-requirement">(required)</span>
            </p>
            <star-rating
                :rating="userRating"
                @rating-selected="onRatingSelected"
            />
        </div>
        <div class="feedback-form__review">
            <label
                for="feedback-user-review"
                class="feedback-form__field-label"
            >
                Your Review
                <span class="feedback-form__field-requirement">(optional)</span>
            </label>
            <textarea
                id="feedback-user-review"
                v-model="userReview"
                class="feedback-form__input"
                cols="65"
                rows="4"
                placeholder="What did you think about this recipe? Did you make any changes or notes?"
            />
        </div>
        <photo-upload
            v-if="canUploadPhoto"
            ref="photoUpload"
            :reviewExists="reviewExists"
            @file-upload="handleImageUpload"
            @delete-upload="handleDeleteUpload"
        />
        <div class="feedback-form__actions">
            <button
                class="feedback-form__cancel"
                data-click-tracked="false"
                @click="cancelReview"
            >
                Cancel
            </button>
            <button
                class="feedback-form__submit"
                data-click-tracked="false"
                :disabled="!canSubmitFeedback"
                @click="submitFeedback"
            >
                Submit
            </button>
        </div>
    </div>
</template>

<script>
import UgcSvg from './ugc-svg.vue';
import StarRating from './star-rating.vue';
import PhotoUpload from './photo-upload.vue';
import {
    fetchAccessToken,
    postFeedback,
    stripHtml,
    handleLineBreaks,
    convertHtmlLineBreaks,
    dataLayerPush
} from '../utils';

export default {
    name: 'FeedbackForm',
    components: {
        UgcSvg,
        StarRating,
        PhotoUpload
    },
    props: {
        reviewExists: {
            type: Boolean,
            default: false
        }
    },
    emits: [
        'form-submitted',
        'form-cancelled',
        'form-submitted-error'
    ],
    data() {
        return {
            userRating: null,
            auth0Link: null,
            auth0SignOutLink: null,
            userReview: '',
            photoUploadFile: null,
            deleteUploadUrl: false,
            auth0Cookie: null
        };
    },
    computed: {
        state() {
            return this.$store.state;
        },
        userSubmission() {
            const submission = this.state.userSubmissionStore?.data;
            
            return {
                review: submission.review
                    ? handleLineBreaks(submission.review)
                    : '',
                ...submission
            };
        },
        articleTitle() {
            return this.state.feedbackFormStore?.articleTitle;
        },
        canSubmitFeedback() {
            // Only allow form submission when a new review receives a rating,
            // or the values are different than the previous ones on an existing review

            if (!this.reviewExists) {
                return Boolean(this.userRating);
            }

            return (
                this.userReview !== convertHtmlLineBreaks(this.userSubmission?.review || '')
                || this.userRating !== this.userSubmission?.starRating
                || (this.photoUploadFile || this.deleteUploadUrl)
            );
        },
        canUploadPhoto() {
            return this.state.feedbackFormStore?.photoUploadEnabled
                && this.state.feedbackFormStore?.contentGraphEnabled;
        },
        isEditedReview() {
            return this.state.feedbackSummaryStore?.isEditedReview;
        }
    },
    mounted() {
        this.resetForm();
        // Assuming this exists because of ugc-vue should be dependent on auth0 component
        this.auth0Link = document.querySelector('.mntl-ugc-vue-with-auth-auth0');
        this.auth0SignOutLink = document.querySelector('.mntl-ugc-vue-with-auth-auth0-sign-out');
        this.auth0Cookie = window.docCookies.getItem('ddmaccount');
    },
    methods: {
        onRatingSelected(ratingSelection) {
            if (ratingSelection > 0 || ratingSelection !== null) {
                this.userRating = ratingSelection;
            }
        },
        handleImageUpload(imageUpload) {
            this.deleteUploadUrl = false;
            this.photoUploadFile = imageUpload;
        },
        handleDeleteUpload(deleteUpload) {
            this.photoUploadFile = null;
            if (deleteUpload) {
                this.deleteUploadUrl = deleteUpload;
                this.$store.dispatch('photoUploadStore/setUrl', '');
            }
        },
        clearMessageBanner() {
            if (this.state.messageBannerStore?.alert) {
                this.$store.dispatch('messageBannerStore/setBannerAlert', {
                    message: '', 
                    status: ''
                });
            }
        },
        handleSubmission() {
            const currentDate = new Date().getTime();
            const userSubmission = {
                created: currentDate,
                starRating: this.userRating,
                review: handleLineBreaks(this.userReview),
                photoUrl: this.deleteUploadUrl
                    ? this.state.photoUploadStore?.url
                    : this.state.photoUploadStore?.url || this.state.userSubmissionStore?.data?.photoUrl
            };
            
            this.auth0Cookie = window.docCookies.getItem('ddmaccount');

            if (this.auth0Cookie) {
                // Always try to fetch a fresh access token for post call
                fetchAccessToken(this.auth0Cookie, this.state, this.auth0Link)
                    .then(() => {
                        const labelPrefix = this.isEditedReview ? 'Edited Rate' : 'Rate';
                        const hasPhotoUpload = !this.deleteUploadUrl && (this.photoUploadFile || this.state.userSubmissionStore?.data?.photoUrl);
                        const eventLabel = [
                            Boolean(this.userRating) && `${labelPrefix} ${this.userRating} Star`,
                            Boolean(stripHtml(this.userReview) && !hasPhotoUpload) && '& Review',
                            Boolean(!stripHtml(this.userReview) && hasPhotoUpload) && '& Photo',
                            Boolean(stripHtml(this.userReview) && hasPhotoUpload) && '& Review with Photo'
                        ]
                            .filter(Boolean)
                            .join(' ');
                        const eventAction = this.isEditedReview ? 'Edit Review' : '';

                        postFeedback(this.state, userSubmission)
                            .then(() => {
                                this.$store.dispatch('userSubmissionStore/setData', userSubmission);
                                this.$emit('form-submitted');
                            })
                            .catch((error) => {
                                const submitError = {
                                    status: 'error',
                                    message: this.state.messageBannerStore?.genericFormError
                                };
                                
                                this.$store.dispatch('messageBannerStore/setBannerAlert', submitError);
                                
                                if (this.state.userSubmissionStore?.data?.rating) {
                                    this.$emit('form-submitted');
                                } else {
                                    this.$emit('form-submitted-error');
                                }

                                console.error('Error with POST request to submit feedback', error);
                            });
                        
                        // Update status to pending
                        this.$store.dispatch('feedbackSummaryStore/setStatus', 'awaitingReview');

                        dataLayerPush(eventLabel, eventAction);
                    })
                    .catch(() => {
                        this.auth0Link.click();
                            
                        document.addEventListener(Mntl.auth0.LOG_IN_EVENT_NAME, () => this.handleSubmission());
                    });
            }
        },
        submitFeedback() {
            this.$emit('form-loading');            

            this.handleSubmission();

            // Auth0 login + post call fallback if user logged out in a separate window and tries to submit
            if (!this.auth0Cookie && this.auth0Link) {
                this.auth0Link.click();
                
                document.addEventListener(Mntl.auth0.LOG_IN_EVENT_NAME, () => this.handleSubmission());
            }

            this.clearMessageBanner();
        },
        resetForm() {
            // Restore past submission, if it exists
            this.userRating = this.userSubmission?.starRating;
            this.userReview = this.userSubmission?.review ? convertHtmlLineBreaks(this.userSubmission?.review) : '';
            
            this.clearMessageBanner();

            if(this.canUploadPhoto){
                this.photoUploadFile = null;
                this.$refs.photoUpload.resetInput();
            }
        },
        cancelReview() {
            this.resetForm();

            if (this.isEditedReview) {
                this.$store.dispatch('feedbackSummaryStore/setIsEditedReview', false);
            }

            if (this.auth0Cookie && !this.userSubmission?.created) {
                this.resetForm();
            } else {
                this.$emit('form-cancelled');
            }

            dataLayerPush('Cancel');
        }
    }
};
</script>
