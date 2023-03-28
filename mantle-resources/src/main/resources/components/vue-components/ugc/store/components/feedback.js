/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    helpfulEnabled: false,
    helpfulClickableEnabled: false,
    linkDisplayName: false,
    fallbackAvatars: ['ugc-icon-avatar'],
    numberOfFallbackAvatars: 5,
    showPhotoNextToReview: false
});

const mutations = {
    setHelpfulEnabled(state, helpfulEnabled) {
        state.helpfulEnabled = helpfulEnabled;
    },
    setHelpfulClickableEnabled(state, helpfulClickableEnabled) {
        state.helpfulClickableEnabled = helpfulClickableEnabled;
    },
    setLinkDisplayName(state, linkDisplayName) {
        state.linkDisplayName = linkDisplayName;
    },
    setFallbackAvatars(state, fallbackAvatars) {
        state.fallbackAvatars = fallbackAvatars;
    },
    setNumberOfFallbackAvatars(state, numberOfFallbackAvatars) {
        state.numberOfFallbackAvatars = numberOfFallbackAvatars;
    },
    setShowPhotoNextToReview(state, showPhotoNextToReview) {
        state.showPhotoNextToReview = showPhotoNextToReview;
    }
};

const actions = {
    setHelpfulEnabled({ commit }, helpfulEnabled) {
        commit('setHelpfulEnabled', helpfulEnabled);
    },
    setHelpfulClickableEnabled({ commit }, helpfulClickableEnabled) {
        commit('setHelpfulClickableEnabled', helpfulClickableEnabled);
    },
    setLinkDisplayName({ commit }, linkDisplayName) {
        commit('setLinkDisplayName', linkDisplayName);
    },
    setFallbackAvatars({ commit }, fallbackAvatars) {
        commit('setFallbackAvatars', fallbackAvatars);
    },
    setNumberOfFallbackAvatars({ commit }, numberOfFallbackAvatars) {
        commit('setNumberOfFallbackAvatars', numberOfFallbackAvatars);
    },
    setShowPhotoNextToReview({ commit }, showPhotoNextToReview) {
        commit('setShowPhotoNextToReview', showPhotoNextToReview);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
