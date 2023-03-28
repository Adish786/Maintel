<template>
    <div class="ratings-histogram">
        <div class="ratings-histogram__average">
            <ugc-svg
                v-for="star in maxStars"
                :id="getIcon(star)"
                :key="star"
                class="ratings-histogram__average-star"
            />
            <span class="ratings-histogram__average-text">{{ average }} out of {{ maxStars }}</span>
        </div>
        <span class="ratings-histogram__total">{{ totalRatings }} Ratings</span>
        <div class="ratings-histogram__rows">
            <div
                v-for="(row, index) in rows"
                :key="index"
                class="ratings-histogram__row"
            >
                <span class="ratings-histogram__score">{{ row.score }}</span>
                <ugc-svg
                    id="ugc-icon-star"
                    class="ratings-histogram__score-star"
                />
                <div class="ratings-histogram__progress">
                    <span
                        class="ratings-histogram__progress-bar"
                        :style="`width: ${row.percent}%`"
                    />
                </div>
                <span class="ratings-histogram__count">{{ row.count }}</span>
            </div>
        </div>
    </div>
</template>

<script>
import UgcSvg from './ugc-svg.vue';

export default {
    name: 'RatingsHistogram',
    components: {
        UgcSvg
    },
    data() {
        return {
            maxStars: 5
        };
    },
    computed: {
        state() {
            return this.$store.state;
        },
        aggregatedFeedbacks() {
            return this.state.aggregatedFeedbacks;
        },
        average() {
            const avg = this.aggregatedFeedbacks?.averageRating.average;

            return parseFloat((Math.round(avg * 10) / 10).toFixed(1));
        },
        scoreHistogram() {
            return this.state.aggregatedFeedbacks?.scoreHistogram;
        },
        totalRatings() {
            return this.aggregatedFeedbacks?.averageRating.count;
        },
        rows() {
            const rowData = [];

            for (let i = 0; i < this.scoreHistogram?.totalSize; i++) {
                const { 
                    score, count 
                } = this.scoreHistogram.list[i];
                const total = this.totalRatings;
                const percent = total > 0 ? (count / total) * 100 : 0;
                const scoreData = {
                    score,
                    count,
                    percent
                };

                rowData.push(scoreData);
            }

            return rowData;
        }
    },
    methods: {
        getIcon(star) {
            if(star <= this.average){
                return 'ugc-icon-star';
            }

            if(star - this.average < 0.6){
                return 'ugc-icon-star-half';
            }

            return 'ugc-icon-star-outline';
        }
    }
};
</script>