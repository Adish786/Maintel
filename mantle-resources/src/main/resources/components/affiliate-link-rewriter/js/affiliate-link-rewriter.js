window.Mntl = window.Mntl || {};

Mntl.affiliateLinkRewriter = (function() {
    let _tokenMappings;

    function updateHref(href, existingQueryParams, dataAttributeValue) {
        let updatedHref = href;

        if (existingQueryParams !== '') {
            updatedHref += `&${dataAttributeValue}`;
        } else {
            updatedHref += `?${dataAttributeValue}`;
        }

        return updatedHref;
    }

    function findReplaceTokens(dataAttributeValueToBeReplaced) {
        let dataAttributeValue = dataAttributeValueToBeReplaced;
        let token;
        let tokenToFind;

        for (token in _tokenMappings) {
            if (_tokenMappings.hasOwnProperty(token) && dataAttributeValue.indexOf(token) !== -1) {
                tokenToFind = '${' + token + '}';
                dataAttributeValue = dataAttributeValue.replaceAll(tokenToFind, _tokenMappings[token]);
            }
        }

        return dataAttributeValue;
    }

    function processLink($link) {
        const dataAttributeValue = $link[0].getAttribute('data-affiliate-link-rewriter');
        let finalDataAttributeValue;

        if (dataAttributeValue) {
            finalDataAttributeValue = findReplaceTokens(dataAttributeValue);
            $link[0].href = updateHref($link[0].href, $link[0].search, finalDataAttributeValue);
        }
    }

    function setMappings(tokenMappings) {
        _tokenMappings = tokenMappings;
    }

    return {
        processLink,
        setMappings
    };
}());
