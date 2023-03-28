<template>
    <div class="feedback__stars">
        <span v-for="(star, index) in stars" :key="index" class="feedback__star">
            <ugc-svg :id="star" />
        </span>
    </div>
</template>

<script>
import UgcSvg from './ugc-svg.vue';
export default {
    name: 'Stars',
    components: {
        UgcSvg
    },
    props: {
        ratingValue: {
            type: Number,
            default: 5
        }
    },
    data() {
        return {
            iconIds: {
                star: 'ugc-icon-star',
                starHalf: 'ugc-icon-star-half',
                starOutline: 'ugc-icon-star-outline'
            }
        };
    },
    computed: {
        stars() {
            const starIconIds = [];
            // Round rating to half
            const rounded = Math.round(this.ratingValue * 2) / 2;
            const isDecimal = rounded % 1 !== 0;
            const fullStars = Math.floor(rounded);

            // Full stars
            for (let i = 0; i < fullStars; i += 1) {
                starIconIds.push(this.iconIds.star);
            }

            // Rest should be either half or empty stars
            for (let i = 0; i < (5 - fullStars); i += 1) {
                // Determine if the first one is half or empty
                if (i === 0) {
                    if (isDecimal) {
                        starIconIds.push(this.iconIds.starHalf);
                    } else {
                        starIconIds.push(this.iconIds.starOutline);
                    }
                } else {
                    starIconIds.push(this.iconIds.starOutline);
                }
            }

            return starIconIds;
        }
    }
};
</script>