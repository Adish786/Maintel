<template>
    <div class="star-rating">
        <div class="star-rating__stars">
            <label
                v-for="star in maxStars"
                :key="star"
                :class="['star-rating__star', {
                    'star-rating__star--active': star <= ratingSelection
                }]"
                :for="`star-${star}`"
                @click="setRating(star)"
                @keyup.enter="setRating(star)"
                @mouseenter="activeHoverIndex = star"
                @mouseleave="activeHoverIndex = 0"
                tabindex="0"
            >
                <span class="is-screenreader-only">Rate this a {{ star }}</span>
                <ugc-svg :id="handleIcons(star)" />
                <input
                    :id="`star-${star}`"
                    class="star-rating__input is-screenreader-only"
                    type="radio"
                    :value="star"
                    tabindex="-1"
                >
            </label>
        </div>
        <span
            v-if="activeHoverIndex > 0 || ratingSelection"
            class="star-rating__text"
        >
            {{ ratingTextLabel }}
        </span>
    </div>
</template>

<script>
import UgcSvg from './ugc-svg.vue';

export default {
    name: 'StarRating',
    components: {
        UgcSvg
    },
    props: {
        rating: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {
            maxStars: 5,
            activeHoverIndex: 0,
            ratingSelection: null,
            ratingText: [
                'Couldn\'t eat it',
                'Didn\'t like it',
                'It was OK',
                'Liked it',
                'Loved it'
            ]
        };
    },
    computed: {
        ratingTextLabel() {
            return this.ratingText[
                this.activeHoverIndex > 0
                    ? this.activeHoverIndex - 1
                    : this.ratingSelection - 1
            ];
        }
    },
    watch: {
        rating(newRating) {
            this.ratingSelection = newRating;
        }
    },
    mounted() {
        this.ratingSelection = this.rating;
    },
    methods: {
        setRating(star) {
            if (star <= this.maxStars && star >= 0) {
                this.$emit('rating-selected', star);
            }
        },
        handleIcons(star) {
            const starIcon = star <= (this.activeHoverIndex > 0 ? this.activeHoverIndex : this.ratingSelection)
                ? 'ugc-icon-star'
                : 'ugc-icon-star-outline';
            
            return starIcon;
        }
    }
};
</script>
