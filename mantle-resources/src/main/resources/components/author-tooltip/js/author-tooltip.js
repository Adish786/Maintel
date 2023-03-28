// This file is a duplicate of bio-tooltip.js, but is needed to create the new mntl-author-tooltip.
// Once the rollout of bylines v2 is complete bio-tooltip.js will be removed and there will no longer be any duplication.
var Mntl = window.Mntl || {};

(function() {
    var tooltips;
    var i;

    function init(tooltip) {
        const isTouchDevice = window.Modernizr && window.Modernizr.touchevents;
        var tooltipTriggerLink;
        var tooltipTrigger;

        tooltipTrigger = tooltip.closest('[data-tooltip]');

        if (!tooltipTrigger) {
            return;
        }

        Mntl.Tooltip.createDynamicTooltip(tooltipTrigger, {
            contentFactory: function() {
                return tooltip;
            },
            tracking: {
                event: 'transmitInteractiveEvent',
                eventCategory: 'article-meta Click',
                eventAction: 'author-byline-tooltip-open',
                eventLabel: window.location.href
            }
        });

        tooltipTriggerLink = tooltipTrigger.querySelector('[data-trigger-link]');

        if (tooltipTriggerLink && isTouchDevice) {
            tooltipTriggerLink.addEventListener('click', function(e) {
                e.preventDefault();
            });
        }
    }

    if (Mntl.Tooltip) {
        tooltips = document.getElementsByClassName('mntl-author-tooltip');

        if (!tooltips.length) {
            return;
        }

        for (i = 0; i < tooltips.length; i++) {
            init(tooltips[i]);
        }
    }
})();
