Vue.component('iframe-embed', {
    props: {
        vertical: {
            type: String,
            required: true
        },
        toolId: {
            type: String,
            default: true
        }
    },
    data: function() {
        return {
            show: false,
            copy: false
        };
    },
    methods: {
        selectAllText: function() {
            var textarea = this.$el.getElementsByTagName('textarea')[0];

            textarea.focus();
            textarea.setSelectionRange(0, textarea.value.length);
            document.execCommand('copy');
            this.copy = true;
        }
    }
});
