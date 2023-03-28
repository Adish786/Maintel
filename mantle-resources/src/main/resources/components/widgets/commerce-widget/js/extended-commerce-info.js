window.Mntl = window.Mntl || {};

Mntl.Commerce = (function($, fnUtils, utils, Maybe) {
    const $commerceWidgets = $('.mntl-commerce-widget');
    let isLoaded = false;

    /**
     * Helper function to determine if the commerce info is loaded for other components like right-rail that listen
     * for the commerce component to load.
     * @returns {boolean}
     */
    function getIsCommerceInfoLoaded() {
        return isLoaded;
    }

    function updatePriceInformation(commerceItem, $commerceButton) {
        if (fnUtils.getDeepValue(commerceItem, 'commerceModel', 'bestPrice')) {
            $commerceButton.attr('data-commerce-price', commerceItem.commerceModel.bestPrice);
        }
    }

    function updateHref(commerceItem, $commerceButton) {
        if (fnUtils.getDeepValue(commerceItem, 'commerceModel', 'url')) {
            $commerceButton.attr('href', commerceItem.commerceModel.url);
        }
    }

    function updateCommerceItems(commerceData, index) {
        const $item = $commerceWidgets.eq(index);
        const $imageContainer = $item.find('.js-commerce-image');
        let $image = $imageContainer.find('img');
        const $commerceButtons = $item.find('.mntl-commerce-btn');
        let imageNeedsReplacing = true;

        // For each commerce list item (each item in data)
        commerceData.forEach((commerceItem, i) => {
            const $commerceButton = $commerceButtons.eq(i);

            // Update image field with first image from items
            if (imageNeedsReplacing) {
                if (fnUtils.getDeepValue(commerceItem, 'commerceModel', 'imageUrl')) {
                    if ($image.length === 0) {
                        $image = $('<img />').addClass('mntl-commerce-widget__image');
                        $imageContainer.append($image);
                    }
                    $image.attr('src', commerceItem.commerceModel.imageUrl);
                    imageNeedsReplacing = false;
                }
            }

            // Insert any price information
            updatePriceInformation(commerceItem, $commerceButton);
            // Insert updated url
            updateHref(commerceItem, $commerceButton);
            // ...and run post-processing
            Mntl.externalizeLinks.processLink($commerceButton);
        });
    }

    // Calls commerce task
    function getExtendedCommerceInfo() {
        const baseUrl = Maybe.of(document.querySelector('meta[property="og:url"]'))
            .map((el) => el.getAttribute('content'))
            .orElse([window.location.protocol, utils.hasWwwSubdomain() ? '//www.' : '//', utils.getDomain(), window.location.pathname].join('')); // canonical domain needed for selene call
        const params = utils.getQueryParams();
        const previewParams = ['et', 'state'].reduce(function(acc, key) {
            return params.hasOwnProperty(key) ? acc.concat(['&' + key + '=', params[key]]) : acc;
        }, []);

        $.ajax({
            url: ['/servemodel/model.json?modelId=gatherListOfListCommerceInfo&url=',
                baseUrl].concat(previewParams).join(''),
            method: 'GET'
        })
            .done((data) => {
                data.forEach(updateCommerceItems);
                $(document).trigger('commerce-info-loaded', [data]);
                isLoaded = true;
            })
            .fail((err) => {
                window.debug.error('there was an error', err);
            });

        return;
    }

    function init($container) {
        if ($container.find('.commerce-widget').length > 0) {
            getExtendedCommerceInfo();
        }
    }

    return {
        init,
        getIsCommerceInfoLoaded
    };
})(window.jQuery || {}, Mntl.fnUtilities || {}, Mntl.utilities || {}, Mntl.Maybe || {});

Mntl.utilities.readyAndDeferred(Mntl.Commerce.init);
