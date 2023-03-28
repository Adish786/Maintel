<template>
    <div class="feedback-list__sort">
        <span class="feedback-list__sort-label">Sort By:</span>
        <span
            aria-label="Select a sort option"
            :aria-expanded="showOptions"
            :class="{
                'feedback-list__sort-trigger': true, 
                'shown': showOptions
            }"
            @click="toggleOptions()"
            v-click-outside="closeOptions"
        >
            {{ selectedOptionLabel }}
        </span>
        <ul
            :class="{
                'feedback-list__sort-options':true, 
                'shown': showOptions
            }"
        >
            <li
                v-for="(sortingOption, index) in sortingOptionsWithSelected"
                :key="index"
                class="feedback-list__sort-option-item"
            >
                <a
                    role="button"
                    tabindex="0"
                    data-click-tracked="false"
                    :class="{
                        'feedback-list__sort-option-label': true,
                        selected: sortingOption.selected,
                    }"
                    @click="sort(sortingOption.option, sortingOption.label)"
                >
                    {{ sortingOption.label }}
                </a>
            </li>
        </ul>
    </div>
</template>

<script>
import {
    dataLayerPush
} from '../utils';
import vClickOutside from '../../../widgets/v-click-outside/v-click-outside';

Vue.directive('click-outside', vClickOutside);

export default {
    name: 'Sort',
    props: {
        sortingOptions: {
            type: Array,
            default: () => []
        }
    },
    data() {
        return { 
            showOptions: false 
        };
    },
    computed: {
        selectedOptionLabel() {
            let selectedOptionLabel = this.sortingOptions[0].label;

            this.sortingOptions.forEach((sortingOption) => {
                if (sortingOption.option === this.state.sortStore?.sort) {
                    selectedOptionLabel = sortingOption.label;
                }
            });

            return selectedOptionLabel;
        },
        sortingOptionsWithSelected() {
            const options = [];

            this.sortingOptions.forEach((sortingOption) => {
                sortingOption.selected = false;
                if (sortingOption.option === this.state.sortStore?.sort) {
                    sortingOption.selected = true;
                }
                options.push(sortingOption);
            });

            return options;
        },
        state() {
            return this.$store.state;
        }
    },
    methods: {
        sort(option, label) {
            this.$store.dispatch(
                'sortStore/setSort', {
                    sort: option,
                    sortClicked: true
                }
            );

            dataLayerPush('No Target URL', label);

            this.showOptions = false;
        },
        toggleOptions() {
            this.showOptions = !this.showOptions;
        },
        closeOptions(event) {
            // We don't need to do anything if options are already hidden
            if(!this.showOptions) return;

            // Don't close the menu if the user clicked between options
            if(!event.target.classList.contains('feedback-list__sort-options')){
                this.showOptions = false;
            }
        }
    }
};
</script>
