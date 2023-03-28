window.Mntl = window.Mntl || {};

Mntl.amazonAffiliateTagger = (function() {
    var _amazonId;
    var _docId;
    var _requestId;

    function _generateValidSubTag(docId, asin) {
        // Subtag should be in the following format:
        // docId|requestId
        var parameters = [
            docId ? (String(docId)).replace(/,/g, '') : '',
            _requestId,
            asin
        ];

        return parameters.join('|');
    }

    function _parseHref($link) {
        var hrefParts = $link.attr('href').split(/\?|%3F/, 2); // encoded '?' is '%3F' which amazon recognizes as the beginning of query params
        var qp;

        try {
            qp = hrefParts.length > 1 && Mntl.utilities && Mntl.utilities.getQueryParams(decodeURIComponent(hrefParts[1])) || {}; // eslint-disable-line no-mixed-operators
        } catch (e) {
            qp = {};
        }

        return {
            url: hrefParts[0],
            qp: qp
        };
    }

    function _parseASIN($link) {
        var asinMatch = $link.data('amazon-asin') || '';

        return asinMatch;
    }

    function _addSubTag($link) {
        var parsedHref = _parseHref($link);
        var parsedASIN = _parseASIN($link);
        var composedHref;

        parsedHref.qp.tag = _amazonId;
        parsedHref.qp.ascsubtag = _generateValidSubTag(_docId, parsedASIN);

        composedHref = parsedHref.url + '?' + querystring.stringify(parsedHref.qp);

        $link.attr('href', composedHref);
    }

    function processLink($link) {
        if ($link.is('a[href*="amazon.com"]')) {
            // NOTE: Changed from on-click to on-load to prevent an on-click race condition
            // that was preventing the tags from being applied.
            Mntl.utilities.ready(function() {
                _addSubTag($link);
            });
        }
    }

    function init(amazonId, docId, requestId) {
        _amazonId = amazonId;
        _docId = docId;
        _requestId = requestId;
    }

    return {
        init: init,
        processLink: processLink
    };
}());
