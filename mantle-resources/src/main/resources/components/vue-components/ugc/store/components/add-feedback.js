/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    cta: '',
    ratingsHistogramEnabled: false,
    showMostHelpfulReviews: false
});

const mutations = {
    setCta(state, cta) {
        state.cta = cta;
    },
    setRatingsHistogramEnabled(state, ratingsHistogramEnabled) {
        state.ratingsHistogramEnabled = ratingsHistogramEnabled;
    },
    setShowMostHelpfulReviews(state, showMostHelpfulReviews) {
        state.showMostHelpfulReviews = showMostHelpfulReviews;
    }
};

const actions = {
    setCta({ commit }, cta) {
        commit('setCta', cta);
    },
    setRatingsHistogramEnabled({ commit }, ratingsHistogramEnabled) {
        commit('setRatingsHistogramEnabled', ratingsHistogramEnabled);
    },
    setShowMostHelpfulReviews({ commit }, showMostHelpfulReviews) {
        commit('setShowMostHelpfulReviews', showMostHelpfulReviews);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
