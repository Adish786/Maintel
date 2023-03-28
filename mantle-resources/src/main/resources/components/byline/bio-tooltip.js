window.Mntl = window.Mntl || {};

(function() {
    var tooltips;
    var i;

    function init(tooltip) {
        var tooltipTriggerLink;
        var tooltipTrigger;

        tooltipTrigger = tooltip.closest('[data-tooltip]');

        if (!tooltipTrigger) {
            return;
        }

        Mntl.Tooltip.createDynamicTooltip(tooltipTrigger, {
            contentFactory: function() {
                // .cloneNode(true) needed to handle IE11 which removes tooltip content DOM element when clearing tooltip container on subsequent hovers
                return tooltip.cloneNode(true);
            },
            tracking: {
                event: 'transmitInteractiveEvent',
                eventCategory: 'article-meta Click',
                eventAction: 'author-byline-tooltip-open',
                eventLabel: window.location.href
            }
        });

        tooltipTriggerLink = tooltipTrigger.querySelector('[data-trigger-link]');
        if (tooltipTriggerLink) {
            tooltipTriggerLink.addEventListener('click', function(e) {
                e.preventDefault();
            });
        }
    }

    if (Mntl.Tooltip) {
        tooltips = document.getElementsByClassName('mntl-bio-tooltip');

        if (!tooltips.length) {
            return;
        }

        for (i = 0; i < tooltips.length; i++) {
            init(tooltips[i]);
        }
    }
})();
