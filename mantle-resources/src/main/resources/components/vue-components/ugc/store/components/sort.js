/* eslint-disable func-style */

const namespaced = true;

const state = () => ({
    sort: 'DATE_DESC',
    sortEnabled: false,
    sortLoading: false
});

const mutations = {
    setSort(state, sort) {
        state.sort = sort;
    },
    setSortEnabled(state, sortEnabled) {
        state.sortEnabled = sortEnabled;
    },
    setSortLoading(state, loading) {
        state.sortLoading = loading;
    }
};

const actions = {
    setSort({
        commit,
        dispatch,
        state,
        rootState
    }, data) {
        commit('setSort', data.sort);

        if (data.sortClicked) {
            commit('setSortLoading', true);

            // Sorting will reset to start at page 1
            commit('setSort', data.sort);
            commit('setPage', 1, { root: true });
            const getUrl = `${rootState.apiBase}&sort=${state.sort}&offset=0&limit=${rootState.numberPerPage}`;

            // GLBE-9646 sets timeout at 30 seconds for extremely slow connections to complete the ajax registration call to the server
            Mntl.utilities.ajaxPromiseGetCall(getUrl, 'error', null)
                .then((stringResponse) => {
                    commit('setSortLoading', false);
                    try {
                        const response = JSON.parse(stringResponse);
                        const feedbackList = response.list;

                        dispatch('setLoadFeedbackList', feedbackList, { root: true });
                    } catch (err) {
                        console.log(`setSort: Unable to parse response from ${getUrl} to json`, err);
                    }
                })
                .catch((err) => {
                    commit('setSortLoading', false);
                    console.log(`setSort: Unexpected error from ${getUrl}`, err);
                });
        }
    },
    setSortEnabled({ commit }, sortEnabled) {
        commit('setSortEnabled', sortEnabled);
    }
};

export default {
    namespaced,
    state,
    mutations,
    actions
};
