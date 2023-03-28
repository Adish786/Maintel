/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    fileLimitSize: 30,
    requirementsText: '',
    url: '',
    error: {
        imageSize: '',
        fileSize: '',
        combined: ''
    }
});

const mutations = {
    setFileLimitSize(state, fileLimitSize) {
        state.fileLimitSize = fileLimitSize;
    },
    setRequirementsText(state, requirementsText) {
        state.requirementsText = requirementsText;
    },
    setUrl(state, url) {
        state.url = url;
    },
    setError(state, error) {
        state.error = error;
    }
};

const actions = {
    setFileLimitSize({ commit }, fileLimitSize) {
        commit('setFileLimitSize', fileLimitSize);
    },
    setRequirementsText({ commit }, requirementsText) {
        commit('setRequirementsText', requirementsText);
    },
    setUrl({ commit }, url) {
        commit('setUrl', url);
    },
    setError({ commit }, data) {
        commit('setError', data);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
