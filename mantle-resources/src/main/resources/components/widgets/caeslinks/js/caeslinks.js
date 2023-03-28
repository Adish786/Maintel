window.Mntl = window.Mntl || {};

/**
 * Adds click handlers to links that DO NOT have
 * 'noceas' rel attribute. These click handlers will
 * send the user to CAES /product endopoint by adding
 * the href attribute into a CAES link
 * @return {Object} Object containing init and callback function processLink()
 */
Mntl.caesLinksHandler = (function() {
    let _docId;
    let _requestId;

    /**
     * Takes the href value from the parameter link
     * and adds it to a newly built CAES /product endpont
     * link
     * https://{VERTICAL_DOMAIN}/product/?url={RETAILER_URL}&=docId{DOC_ID}&=requestId{REQUEST_ID}&=clickId{CLICK_ID}
     * @param {Object} link HTMLAnchorElement
     * @return {String} CAES /product endpoint URL
    */
    function _buildCaesURL(link) {
        if (link.hasAttribute("data-caes-built")) {
            return link.getAttribute('href');
        }

        const retailerURL = encodeURIComponent(decodeURIComponent(link.getAttribute('href')));
        let caesURL = '/product/';

        caesURL += `?url=${retailerURL}`;
        caesURL += `&docId=${_docId}`;
        caesURL += `&requestId=${_requestId}`;
        caesURL += `&clickId=${Date.now()}`;

        if (link.dataset.amazonAsin) {
            caesURL += `&asin=${link.dataset.amazonAsin}`;
        }
        
        link.href = caesURL;
        link.setAttribute('data-caes-built', true);

        return caesURL;
    }

    function init(docId, requestId) {
        _docId = docId;
        _requestId = requestId;
    }

    /**
     * Callback function that processes all
     * <a> tags on page load
     * @param {Object} $link jQuery object
    */
    function processLink($link) {
        const link = ($link && $link.jquery) ? $link[0] : $link; // Retrieve the DOM element
        let events;
        let eventKey;

        // change href on click if click href is present
        link.replaceCAESHref = function () {
            _buildCaesURL(this);
        };

        link.openCaesUrl = function (e) {
            e.preventDefault();
            const caesURL = _buildCaesURL(link);

            window.open(caesURL, link.getAttribute('target')
                ? link.getAttribute('target')
                : '_blank');
        };

        // list of events to attach to each button
        events = {
            click: link.openCaesUrl,
            contextmenu: link.replaceCAESHref, // replaces href on right click
            touchstart: link.replaceCAESHref // replaces href on touch start
        };

        // If rel attribute does NOT contain 'nocaes' also checking to make sure 'data-caes-processed'
        // has not been added yet to avoid adding multiple event listeners
        if (!link.relList.contains('nocaes') && !link.hasAttribute("data-caes-processed")) {
            link.setAttribute('data-caes-processed', true);

            // Attach event handlers
            for (eventKey in events) {
                link.addEventListener(eventKey, events[eventKey]);
            }
        }
    }

    return {
        init,
        processLink
    };
}());
