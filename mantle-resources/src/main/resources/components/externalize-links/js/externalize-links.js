window.Mntl = window.Mntl || {};

Mntl.externalizeLinks = (function($) {
    var hostnameParts = window.location.hostname.split('.');
    var hostname = hostnameParts[hostnameParts.length - 2] + '.' + hostnameParts[hostnameParts.length - 1];
    var selector = 'a[href*="http"]:not([href*="' + hostname + '"]), a[href^="//"]:not([href*="' + hostname + '"])';
    var plugins = [];

    function _externalizeLink($link) {
        var rel = $link.attr('rel') || '';

        if (rel.indexOf('noopener') === -1) {
            rel += ' noopener';
        }

        $link.attr({
            target: '_blank',
            rel: rel.trim()
        });

        plugins.forEach(function(processLink) {
            processLink($link, $);
        });
    }

    function _externalizeLinks($container) {
        $container.find(selector).each(function() {
            _externalizeLink($(this));
        });
    }

    function addPlugin(plugin) {
        if (plugin) {
            plugins.push(plugin.processLink);
        }
    }

    function init() {
        Mntl.utilities.readyAndDeferred(_externalizeLinks);
    }

    return {
        init: init,
        addPlugin: addPlugin,
        processLink: function($link) {
            if ($link.is(selector)) {
                _externalizeLink($link);
            }
        }
    };
}(window.jQuery || {}));
