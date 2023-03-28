/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    status: 'awaitingReview',
    statusText: {
        awaitingReview: '',
        rejected: ''
    },
    resoundSubmitError: false,
    isEditedReview: false
});

const mutations = {
    setStatus(state, status) {
        state.status = status;
    },
    setStatusText(state, statusText) {
        state.statusText = statusText;
    },
    setResoundSubmitError(state, resoundSubmitError) {
        state.resoundSubmitError = resoundSubmitError;
    },
    setIsEditedReview(state, isEditedReview) {
        state.isEditedReview = isEditedReview;
    }
};

const actions = {
    setStatus({ commit }, status) {
        commit('setStatus', status);
    },
    setStatusText({ commit }, data) {
        commit('setStatusText', {
            awaitingReview: data.awaitingReview,
            rejected: data.rejected
        });
    },
    setResoundSubmitError({ commit }, resoundSubmitError) {
        commit('setResoundSubmitError', resoundSubmitError);
    },
    setIsEditedReview({ commit }, isEditedReview) {
        commit('setIsEditedReview', isEditedReview);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
