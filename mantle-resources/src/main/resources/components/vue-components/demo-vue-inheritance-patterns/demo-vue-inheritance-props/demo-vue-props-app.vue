<template>
    <div class="demo-vue-container">
        <demo-vue-header
            :header="header"
            :subHeader="subHeader"
        />
    </div>
</template>

<script>
import DemoVueHeader from '../demo-vue-header-component/demo-vue-header.vue';

export default {
    name: 'demoVueContainerComponent',
    data() {
        return {
            header: 'Vue Default Example',
            subHeader: 'This is an example of a VueJs app running within globe'
        };
    },
    components: {
        'demo-vue-header': DemoVueHeader
    }
};
</script>
