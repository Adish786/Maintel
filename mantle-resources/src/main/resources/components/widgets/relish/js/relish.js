window.Mntl = window.Mntl || {};

Mntl.Relish = (function(window) {
    function init(relishScriptArgs, shopButtonOffset, context) {
        let metaRecipeEl = document.querySelector('meta[name="fexy-relish"]');
        let metaRecipeId = null;
        let relishSaveButtonSelector = document.querySelector('.mntl-relish-save-btn');
        let relishSavedRecipeButtonSelector = document.querySelectorAll('.mntl-relish-saved-recipes-btn');
        let relishButton = document.getElementById('shop-button');
        let fexyRelish;
        let isRelishRequested = false;
        let relishButtonTop = 0;
        let windowHeight = window.innerHeight;

        if (metaRecipeEl !== null) {
            metaRecipeId = metaRecipeEl.getAttribute('data-recipe-id');
        }

        if (context[0] !== document.body) {
            return;
        }

        window.addEventListener('resize', Mntl.throttle(function() {
            windowHeight = window.innerHeight;
        }));

        function loadRelish(cb) {
            isRelishRequested = true;
            Mntl.utilities.loadExternalJS({
                src: `https://${relishScriptArgs.domain}/scripts/relish.js?key=${relishScriptArgs.key}`,
                async: false
            }, () => {
                fexyRelish = window.fexyRelish;
                fexyRelish.cmds.push(() => {
                    cb();
                });
            });
        }

        function relishScrollLoadCheck() {
            relishButtonTop = relishButton.offsetTop;
            if (!isRelishRequested && (window.pageYOffset + windowHeight) >= (relishButtonTop - shopButtonOffset)) {
                loadRelish(() => {
                    window.removeEventListener('mntl.scroll', relishScrollLoadCheck);
                });
            }
        }

        function saveRecipeHandler(type) {
            if (!isRelishRequested) {
                loadRelish(() => {
                    if (type === 'save') {
                        fexyRelish.openRecipe(metaRecipeId);
                        window.removeEventListener('mntl.scroll', relishScrollLoadCheck);
                    } else {
                        fexyRelish.openRecipeList();
                    }
                });
            } else {
                type === 'save' ? fexyRelish.openRecipe(metaRecipeId) : fexyRelish.openRecipeList();
            }
        }

        relishButton && window.addEventListener('mntl.scroll', relishScrollLoadCheck);
        relishSaveButtonSelector && relishSaveButtonSelector.addEventListener('click', saveRecipeHandler.bind(null, 'save'), false);
        relishSavedRecipeButtonSelector.length > 0 && relishSavedRecipeButtonSelector.forEach((btn) => btn.addEventListener('click', saveRecipeHandler.bind(null, 'saveRecipes'), false));
    }

    return { init: init };
}(window || {}));
