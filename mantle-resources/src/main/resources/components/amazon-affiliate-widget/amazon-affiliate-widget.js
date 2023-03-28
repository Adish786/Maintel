window.Mntl = window.Mntl || {};

Mntl.amazonAffiliateWidget = (function($) {
    // This component does not include the actual link to the amazon product, that's in the parent.  This method reaches
    // outside of the component to the parent to get / set the href of the link.  This is a no-no.  Revisited
    // in GLBE-5323
    function findWrappingAnchor($amazonComponent) {
        return $amazonComponent.parents('.js-heading-link');
    }

    function init($container) {
        var $amazonComponents = $container.find('.mntl-amazon-affiliate-widget');
        var itemIds = [];
        var componentMap = {};
        var amazonUrlRegex;
        var imageSize;

        if ($amazonComponents.length) {
            amazonUrlRegex = RegExp('amazon.com(?:%2F|/)(?:[\\w-]+(?:%2F|/))?(?:dp|gp(?:%2F|/)product)(?:%2F|/)(?:\\w+(?:%2F|/))?(\\w{10}).*');

            imageSize = $amazonComponents.data('image-size');

            $amazonComponents.each(function() {
                var $amazonComponent = $(this);
                var href = findWrappingAnchor($amazonComponent).attr('href');
                var match = href.match(amazonUrlRegex);
                var itemId;

                // check if url matches the default amazon URL pattern
                if (match) {
                    // save the item ID from the url
                    itemId = match[1];

                    // save item ID to array only if it doesn't already exist
                    if (itemIds.indexOf(itemId) === -1) {
                        itemIds.push(itemId);
                    }

                    // add button selectors to map to look up later with the item ID
                    if (componentMap[itemId]) {
                        // if we've already seen this item, add the component to the list
                        componentMap[itemId].push($amazonComponent);
                    } else {
                        // otherwise, create a new list with just this component
                        componentMap[itemId] = [$amazonComponent];
                    }
                }
            });

            $.ajax({
                url: [
                    '/servemodel/model.json?modelId=retrieveAmazonProductPriceAndImage&itemIds=',
                    itemIds.join(','),
                    '&imageSize=',
                    imageSize
                ].join(''),
                method: 'get'
            })
                .done(function(data) {
                    var itemId;
                    var amazonItem;
                    var $amazonComponent;
                    var componentList;
                    var component;

                    for (itemId in data) {
                        amazonItem = data[itemId];
                        componentList = componentMap[itemId];

                        // there may be multiple components for an itemId, so loop through those components
                        for (component in componentList) {
                            $amazonComponent = componentList[component];

                            // will display the regular price if not on sale, the sale price if it
                            // is on sale
                            if (amazonItem.price) {
                                $amazonComponent
                                    .find('.js-price')
                                    .text(amazonItem.price);
                            }

                            if (amazonItem.eligibleForPrime) {
                                $amazonComponent
                                    .find('.js-prime')
                                    .removeClass('is-hidden');
                            }

                            // reducedPrice is null if the item is not on sale
                            if (amazonItem.reducedPrice) {
                                $amazonComponent
                                    .find('.js-regular-price')
                                    .text(amazonItem.regularPrice)
                                    .end()
                                    .find('.js-amount-saved')
                                    .text(amazonItem.amountSaved)
                                    .end()
                                    .find('.js-amount-saved-pct')
                                    .text(amazonItem.percentSaved)
                                    .end()
                                    .find('.js-savings')
                                    .removeClass('is-hidden');
                            }

                            if (amazonItem.price || amazonItem.eligibleForPrime) {
                                // the product-info container is hidden by default, so if no price/prime come back then the margin doesn't affect other elements
                                $amazonComponent
                                    .find('.js-product-info').removeClass('is-hidden');
                            }

                            if (amazonItem.imageUrl) {
                                $amazonComponent
                                    .find('.js-photo')
                                    .find('img')
                                    .attr('src', amazonItem.imageUrl)
                                    .end()
                                    .removeClass('is-hidden');
                            }

                            if (amazonItem.url) {
                                // Achtung!  mutating the dom outside of our own component.  See GLBE-5323
                                findWrappingAnchor($amazonComponent).attr('href', amazonItem.url);
                            }
                        }
                    }

                    // find all remaining components that didn't have an image returned and show default image from selene
                    $amazonComponents.find('.js-photo.is-hidden').each(function() {
                        var $photoContainer = $(this);
                        var defaultImageUrl = $photoContainer.data('default-image');

                        if (defaultImageUrl) {
                            $photoContainer
                                .find('img')
                                .attr('src', defaultImageUrl)
                                .end()
                                .removeClass('is-hidden');
                        }
                    });
                });
        }
    }

    init($('.article'));

    return { init: init };
})(window.jQuery || {});
