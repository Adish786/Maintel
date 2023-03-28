/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    data: {
        created: '',
        starRating: null,
        review: '',
        photoUrl: ''
    }
});

const mutations = {
    setData(state, data) {
        state.data = data;
    }
};

const actions = {
    setData({ commit }, data) {
        commit('setData', data);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
