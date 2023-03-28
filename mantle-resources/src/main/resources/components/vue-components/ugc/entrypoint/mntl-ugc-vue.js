import ugcVue from '../ugc-vue.vue';
import { getFallbackAvatarsArray } from '../utils';
import addFeedbackStore from '../store/components/add-feedback';
import feedbackFormStore from '../store/components/feedback-form';
import feedbackSummaryStore from '../store/components/feedback-summary';
import feedbackStore from '../store/components/feedback';
import messageBannerStore from '../store/components/message-banner';
import photoUploadStore from '../store/components/photo-upload';
import sortStore from '../store/components/sort';
import userSubmissionStore from '../store/user-submission';

(function(utils) {
    const datastore = new Vuex.Store({
        modules: {
            addFeedbackStore,
            feedbackFormStore,
            feedbackSummaryStore,
            feedbackStore,
            messageBannerStore,
            photoUploadStore,
            sortStore,
            userSubmissionStore
        },
        state: {
            feedbackList: [],
            page: 1,
            numberPerPage: 9,
            apiBase: null,
            total: 0,
            loadMoreLoading: false,
            auth0Enabled: false,
            readOnly: true,
            hasReviewAds: false,
            adIteration: null,
            legacyMeredithId: '',
            hashId: '',
            accessToken: '',
            docId: '',
            aggregatedFeedbacks: null
        },
        getters: {
            totalPages(state) {
                return Math.ceil(state.total / state.numberPerPage);
            },
            hasLoadMore(state) {
                return (parseInt(state.page) + 1) <= Math.ceil(state.total / state.numberPerPage);
            }
        },
        mutations: {
            setFeedbackList(state, feedbackList) {
                state.feedbackList = state.feedbackList.concat(feedbackList);
            },
            setLoadFeedbackList(state, feedbackList) {
                state.feedbackList = feedbackList;
            },
            setPage(state, page) {
                state.page = page;
            },
            setNumberPerPage(state, numberPerPage) {
                state.numberPerPage = numberPerPage;
            },
            setApiBase(state, apiBase) {
                state.apiBase = apiBase;
            },
            setTotal(state, total) {
                state.total = total;
            },
            setLoadMoreLoading(state, loading) {
                state.loadMoreLoading = loading;
            },
            setAuth0Enabled(state, auth0Enabled) {
                state.auth0Enabled = auth0Enabled;
            },
            setReadOnly(state, readOnly) {
                state.readOnly = readOnly;
            },
            setHasReviewAds(state, hasReviewAds) {
                state.hasReviewAds = hasReviewAds;
            },
            setAdIteration(state, adIteration) {
                state.adIteration = adIteration;
            },
            setLegacyMeredithId(state, legacyMeredithId) {
                state.legacyMeredithId = legacyMeredithId;
            },
            setHashId(state, hashId) {
                state.hashId = hashId;
            },
            setAccessToken(state, accessToken) {
                state.accessToken = accessToken;
            },
            setDocId(state, docId) {
                state.docId = docId;
            },
            setAggregatedFeedbacks(state, aggregatedFeedbacks) {
                state.aggregatedFeedbacks = aggregatedFeedbacks;
            }
        },
        actions: {
            setUgcData({
                commit,
                dispatch,
                state
            }, data) {
                // Store data passed from xml
                commit('setApiBase', data.apiBase);
                commit('setPage', data.page);
                commit('setNumberPerPage', data.numberPerPage);
                commit('setAuth0Enabled', data.auth0Enabled);
                commit('setReadOnly', data.readOnly);
                commit('setHashId', data.hashId);
                commit('setDocId', data.docId);
                commit('setHasReviewAds', data.hasReviewAds);
                commit('setAdIteration', data.adIteration);
                commit('setLegacyMeredithId', data.legacyMeredithId);
                commit('setAggregatedFeedbacks', data.aggregatedFeedbacks && JSON.parse(data.aggregatedFeedbacks));

                // addFeedbackStore
                dispatch('addFeedbackStore/setCta', data.addFeedbackCta);
                dispatch('addFeedbackStore/setRatingsHistogramEnabled', data.ratingsHistogramEnabled);
                dispatch('addFeedbackStore/setShowMostHelpfulReviews', data.showMostHelpfulReviews);
                // feedbackFormStore
                dispatch('feedbackFormStore/setPhotoUploadEnabled', data.photoUploadEnabled);
                dispatch('feedbackFormStore/setContentGraphEnabled', data.contentGraphEnabled);
                dispatch('feedbackFormStore/setArticleTitle', data.articleTitle);
                // feedbackSummaryStore
                dispatch('feedbackSummaryStore/setStatusText', {
                    awaitingReview: data.feedbackStatusPendingText,
                    rejected: data.feedbackStatusRejectedText
                });
                // feedbackStore
                dispatch('feedbackStore/setHelpfulEnabled', data.helpfulEnabled);
                dispatch('feedbackStore/setHelpfulClickableEnabled', data.helpfulClickableEnabled);
                dispatch('feedbackStore/setLinkDisplayName', data.linkDisplayName);
                dispatch('feedbackStore/setNumberOfFallbackAvatars', data.numberOfFallbackAvatars);
                dispatch('feedbackStore/setFallbackAvatars', getFallbackAvatarsArray(data.fallbackAvatars, state.feedbackStore.numberOfFallbackAvatars));
                dispatch('feedbackStore/setShowPhotoNextToReview', data.showPhotoNextToReview);
                // messageBannerStore
                dispatch('messageBannerStore/setGenericFormError', data.errorMessageFormGeneric);
                // photoUploadStore
                dispatch('photoUploadStore/setFileLimitSize', data.photoUploadFileLimitSize);
                dispatch('photoUploadStore/setRequirementsText', data.photoUploadRequirementsText);
                dispatch('photoUploadStore/setError', {
                    imageSize: data.errorMessageUploadImageSize,
                    fileSize: data.errorMessageUploadFileSize,
                    combined: data.errorMessageUploadCombined
                });
                // sortStore
                dispatch('sortStore/setSort', { sort: data.defaultSort });
                dispatch('sortStore/setSortEnabled', data.sortEnabled);
                // Finally, load feedback list
                dispatch('setLoadFeedbackList');
            },
            setLoadFeedbackList({
                commit,
                state
            }) {
                // @TODO will need to update once we use resound
                const getUrl = `${state.apiBase}&sort=${state.sortStore.sort}&offset=0&limit=${state.numberPerPage}`;

                // GLBE-9646 sets timeout at 30 seconds for extremely slow connections to complete the ajax registration call to the server
                utils.ajaxPromiseGetCall(getUrl, 'error', 30000)
                    .then((stringResponse) => {
                        try {
                            const response = JSON.parse(stringResponse);
                            const feedbackList = response.list;
                            const feedbackTotal = response.totalSize;

                            commit('setLoadFeedbackList', feedbackList);
                            commit('setTotal', feedbackTotal);
                        } catch (err) {
                            console.log(`setLoadFeedbackList: Unable to parse response from ${getUrl} to json`, err);
                        }
                    })
                    .catch((err) => {
                        console.log(`setLoadFeedbackList: Unexpected error from ${getUrl}`, err);
                    });
            },
            setLoadMore({
                commit,
                state
            }) {
                const nextPage = parseInt(state.page) + 1;
                const getUrl = `${state.apiBase}&sort=${state.sortStore.sort}&offset=${parseInt(state.page) * state.numberPerPage}&limit=${state.numberPerPage}`;

                commit('setLoadMoreLoading', true);

                // GLBE-9646 sets timeout at 30 seconds for extremely slow connections to complete the ajax registration call to the server
                utils.ajaxPromiseGetCall(getUrl, 'error', 30000)
                    .then((stringResponse) => {
                        commit('setLoadMoreLoading', false);
                        try {
                            const response = JSON.parse(stringResponse);
                            const feedbackList = response.list;

                            commit('setFeedbackList', feedbackList);
                            commit('setPage', nextPage);
                        } catch (err) {
                            console.log(`setLoadMore: Unable to parse response from ${getUrl} to json`, err);
                        }
                    })
                    .catch((err) => {
                        commit('setLoadMoreLoading', false);
                        console.log(`setLoadMore: Unexpected error from ${getUrl}`, err);
                    });
            },
            setHashId({ commit }, hashId) {
                commit('setHashId', hashId);
            },
            setAccessToken({ commit }, accessToken) {
                commit('setAccessToken', accessToken);
            },
            setDocId({ commit }, docId) {
                commit('setDocId', docId);
            }
        }
    });

    utils.initVueApp('ugcVue', ugcVue, datastore);
})(window.Mntl.utilities || {});