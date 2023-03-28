window.Mntl = window.Mntl || {};

Mntl.Product = (function() {
    function init($container) {
        const container = $container.jquery ? $container[0] : $container;
        const productBlocks = container.getElementsByClassName('mntl-sc-block-product');

        Array.prototype.forEach.call(productBlocks, (productBlock) => {
            let productButton = productBlock.getElementsByClassName('mntl-sc-block-product__button');

            if (productButton.length) {
                [productButton] = productButton;
                ['title', 'image', 'details'].forEach((section) => {
                    const element = productBlock.getElementsByClassName(`mntl-sc-block-product__${section}`);

                    if (element.length) {
                        element[0].addEventListener('click', () => {
                            // a click on any of these elements should be forwarded to the product button
                            // CMRC-349 - Adding timeout to account for skimlinks issue
                            setTimeout(() => {
                                productButton.dispatchEvent(new MouseEvent('click'));
                            }, 250);
                        });
                    }
                });
            }
        });
    }

    return { init };
})();

Mntl.utilities.readyAndDeferred(Mntl.Product.init);
