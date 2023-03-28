<template>
    <div class="demo-vue-headers">
        <h1>{{ header }}</h1>
        <h2>{{ subHeader }}</h2>
    </div>
</template>

<script>
export default {
    name: 'demo-vue-header',
    props: {
        header: {
            type: String,
            default: 'header'
        },
        subHeader: {
            type: String,
            default: 'sub-header'
        }
    }
};
</script>
