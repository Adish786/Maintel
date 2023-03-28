(function(utils) {
    let resourcesLoaded = false;

    utils.readyAndDeferred(($context) => {
        // TODO: Deprecate this in 3.15 for final axe of jquery cleanup.
        const context = ($context && $context.jquery) ? $context[0] : $context;
        const katexElements = context.querySelectorAll('.katex:not(.katex--loaded)');

        function getKatexPath(ext) {
            return [
                utils.getStaticPath(),
                '/',
                ext,
                '/vendor/katex.',
                ext
            ].join('');
        }

        function renderFormulas() {
            Array.prototype.forEach.call(katexElements, (node) => {
                const parent = node.parentElement;

                katex.render(parent.getAttribute('data-value'), parent);
                parent.firstChild.classList.add('katex--loaded');
            });
        }

        if (katexElements.length) {
            if (!resourcesLoaded) {
                resourcesLoaded = true;
                utils.loadExternalJS({src: getKatexPath('js')}, renderFormulas);
                utils.loadStyleSheet(getKatexPath('css'));
            } else {
                renderFormulas();
            }
        }
    });
})(window.Mntl.utilities || {});
