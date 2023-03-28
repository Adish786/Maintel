/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    photoUploadEnabled: false,
    contentGraphEnabled: false,
    articleTitle: ''
});

const mutations = {
    setPhotoUploadEnabled(state, photoUploadEnabled) {
        state.photoUploadEnabled = photoUploadEnabled;
    },
    setContentGraphEnabled(state, contentGraphEnabled) {
        state.contentGraphEnabled = contentGraphEnabled;
    },
    setArticleTitle(state, articleTitle) {
        state.articleTitle = articleTitle;
    }
};

const actions = {
    setPhotoUploadEnabled({ commit }, photoUploadEnabled) {
        commit('setPhotoUploadEnabled', photoUploadEnabled);
    },
    setContentGraphEnabled({ commit }, contentGraphEnabled) {
        commit('setContentGraphEnabled', contentGraphEnabled);
    },
    setArticleTitle({ commit }, articleTitle) {
        commit('setArticleTitle', articleTitle);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
