/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    alert: {
        status: '',
        message: ''
    },
    genericFormError: ''
});

const mutations = {
    setBannerAlert(state, alert) {
        state.alert = alert;
    },
    setGenericFormError(state, genericFormError) {
        state.genericFormError = genericFormError;
    }
};

const actions = {
    setBannerAlert({ commit }, alert) {
        commit('setBannerAlert', alert);
    },
    setGenericFormError({ commit }, genericFormError) {
        commit('setGenericFormError', genericFormError);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
