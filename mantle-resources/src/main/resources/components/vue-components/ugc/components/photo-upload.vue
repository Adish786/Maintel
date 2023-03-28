<template>
    <div class="photo-upload">
        <div
            :class="['photo-upload__uploader', {
                'feedback-loading': isUploading
            }]"
            :style="backgroundImage"
        >
            <div
                v-show="!imageSrc"
                class="photo-upload__add-photo"
            >
                <label
                    for="feedback-user-photo"
                    class="photo-upload__field-label"
                >
                    <div class="photo-upload__icon">
                        <ugc-svg
                            id="ugc-icon-add-photo"
                        />
                    </div>
                    Add Photo
                    <span class="photo-upload__field-requirement">(optional)</span>
                </label>
                <input
                    id="feedback-user-photo"
                    ref="fileUpload"
                    type="file"
                    accept="image/png, image/gif, image/jpeg"
                    class="photo-upload__input"
                    name="photo-upload"
                    @change="handleInputChange"
                >
            </div>
            <button
                v-if="imageSrc && !isUploading"
                aria-label="Remove photo"
                class="photo-upload__remove-photo"
                @click="resetInput(false)"
            >
                <span class="is-screenreader-only">Remove Photo</span>
                <div class="photo-upload__icon">
                    <ugc-svg
                        id="ugc-icon-remove-photo"
                    />
                </div>
            </button>
        </div>
        <div 
            v-if="requirementTexts"
            class="photo-upload__text"
        >
            Images must be:
            <ul class="photo-upload__requirements">
                <li
                    v-for="(requirementText, index) in requirementTexts"
                    :key="index"
                    class="photo-upload__requirements-item"
                >
                    {{ requirementText }}
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import UgcSvg from './ugc-svg.vue';
import {
    dataLayerPush
} from '../utils';

export default {
    name: 'PhotoUpload',
    components: {
        UgcSvg
    },
    props: {
        reviewExists: {
            type: Boolean,
            default: false
        }
    },
    emits: [
        'file-upload',
        'delete-upload'
    ],
    data() {
        return {
            imageUpload: null,
            imageSrc: null,
            isUploading: false,
            isValidUpload: false,
            errorMessage: ''
        };
    },
    computed: {
        state() {
            return this.$store.state;
        },
        backgroundImage() {
            let url = this.imageSrc;
            
            if (url && url.startsWith('http')) {
                url = `https://imagesvc.meredithcorp.io/v3/mm/image?url=${encodeURIComponent(this.imageSrc)}&w=150&c=sc&poi=face&q=60&orient=true`;
            }
            
            return url
                ? `background-image:url(${url});`
                : '';
        },
        requirementTexts() {
            try {
                const requirementTexts = JSON.parse(this.state.photoUploadStore?.requirementsText);
                
                return requirementTexts;
            } catch(err) {
                console.log(`Unable to json parse requirementsText ${this.state.photoUploadStore?.requirementsText}`, err);
                
                return [];
            }
        }
    },
    mounted() {
        if (this.reviewExists) {
            this.$nextTick(() => {
                this.imageSrc = this.state.userSubmissionStore?.data?.photoUrl;
            });
        }
    },
    methods: {
        convertMbToBytes(mb) {
            return mb * Math.pow(1024, 2);
        },
        setPreviewImage() {
            // Convert image to Base64 to provide preview image
            // Add loading state to preview image once uploading to BE is available
            const reader = new FileReader();

            reader.onload = (event) => {
                this.imageSrc = event.target.result;
            };

            reader.readAsDataURL(this.imageUpload);
        },
        handleFailedUpload() {
            const uploadError = {
                status: 'error',
                message: this.state.messageBannerStore?.genericFormError
            };
            
            this.isUploading = false;
            
            this.resetInput(true);

            this.$store.dispatch('messageBannerStore/setBannerAlert', uploadError);
        },
        handlePhotoUpload() {
            const formData = new FormData();
            const sanitizedFilename = this.imageUpload.name.replace(/(?:\.(?![^.]+$)|[^\w.])+/g, '-');
                
            formData.append('provider', 'cms');
            formData.append('providerId', this.state.legacyMeredithId);
            formData.append('contentType', this.imageUpload.type);
            formData.append('filename', sanitizedFilename);
                
            Mntl.utilities.ajaxPromisePost('/content-graph/generatepresignedurl/public', formData, 30000)
                .then((response) => {
                    const result = JSON.parse(response);
                    const imageBinary = result.uri;
                    const publicUrl = result.publicUri;

                    Mntl.utilities.ajaxPromisePut(imageBinary, this.imageUpload, this.imageUpload.type, null)
                        .then(() => {
                            this.isUploading = false;

                            this.$store.dispatch('photoUploadStore/setUrl', publicUrl);
                        })
                        .catch((error) => {
                            this.handleFailedUpload();

                            console.error('Error with PUT request to image binary:', error);
                        });
                })
                .catch((error) => {
                    this.handleFailedUpload();

                    console.error('Error with POST request to Content Graph:', error);
                });
        },
        validateImage() {
            const img = new Image();
            const imageSizeLimit = 960;
            const fileSizeLimit = this.convertMbToBytes(this.state.photoUploadStore?.fileLimitSize);
            
            // Create an img from the input's File in order to access image size
            img.src = window.URL.createObjectURL(this.imageUpload);
            
            img.onload = () => {
                const imgWidth = img.naturalWidth;
                const imgHeight = img.naturalHeight;
                const isRejectedImageSize = imgWidth < imageSizeLimit && imgHeight < imageSizeLimit;
                const isRejectedFileSize = this.imageUpload.size > fileSizeLimit;

                if (isRejectedImageSize && isRejectedFileSize) {
                    this.errorMessage = this.state.photoUploadStore?.error?.combined;
                } else if (isRejectedImageSize) {
                    this.errorMessage = this.state.photoUploadStore?.error?.imageSize;
                } else if (isRejectedFileSize) {
                    this.errorMessage = this.state.photoUploadStore?.error?.fileSize;
                }
                
                // Validate upload's file/image size against limits
                if (isRejectedImageSize || isRejectedFileSize) {
                    const uploadError = {
                        status: 'error',
                        message: this.errorMessage
                    };

                    this.$refs.fileUpload.value = null;

                    this.$store.dispatch('messageBannerStore/setBannerAlert', uploadError);
                } else {
                    this.isUploading = true;
                    this.isValidUpload = true;

                    this.setPreviewImage();
                    this.handlePhotoUpload();
                    this.$emit('file-upload', this.imageUpload);
                }

                window.URL.revokeObjectURL(img.src);
            };
        },
        handleInputChange() {
            // eslint-disable-next-line prefer-destructuring
            this.imageUpload = this.$refs.fileUpload.files[0];

            dataLayerPush('Photo Selected');
            
            if (this.state.messageBannerStore?.alert?.message?.length) {
                this.$store.dispatch('messageBannerStore/setBannerAlert', {
                    message: '', 
                    status: ''
                });
            }
            
            if (this.imageUpload) {
                this.validateImage();
            }
            
            this.$refs.fileUpload.value = null;
        },
        resetInput(hasAjaxError) {
            this.$emit('delete-upload', this.imageSrc === this.state.userSubmissionStore?.data?.photoUrl);

            this.imageUpload = null;
            this.imageSrc = null;
            this.isValidUpload = false;
            
            this.$store.dispatch('photoUploadStore/setUrl', '');

            if (hasAjaxError) {
                this.$store.dispatch('messageBannerStore/setBannerAlert', {
                    message: '', 
                    status: ''
                });
            }
        }
    }
};
</script>
