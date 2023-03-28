<template>
    <svg
        :class="[
            'icon',
            id,
            avatarClass
        ]"
    >
        <use
            xmlns:xlink="http://www.w3.org/1999/xlink"
            :xlink:href="svgId"
        />
    </svg>
</template>

<script>
export default {
    name: 'UgcSvg',
    props: {
        id: {
            type: String,
            default: null
        },
        avatarIndex: {
            type: Number,
            default: null
        }
    },
    computed: {
        avatarClass() {
            return `ugc-icon-avatar-${this.avatarIndex}`;
        },
        svgId() {
            return `#${this.id}`;
        }
    }
};
</script>