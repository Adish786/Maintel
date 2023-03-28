window.Mntl = window.Mntl || {};

Mntl.Disqus = (function(win, utils) {
    return {
        init: function init(obj) {
            win.disqus_config = function() { // eslint-disable-line camelcase
                this.page.url = obj.url;
                this.page.identifier = obj.identifier;
                this.page.title = obj.title;
            };

            /* eslint-disable quote-props */
            utils.loadExternalJS({
                src: ['//', obj.forumName, '.disqus.com/embed.js'].join(''),
                'data-timestamp': Number(new Date())
            },
            function() {
                win.dispatchEvent(new CustomEvent('mntl.disqus-script-loaded', {once: true}));
            });
            /* eslint-enable*/
        }
    };
}(window, window.Mntl.utilities || {}));
