window.Mntl = window.Mntl || {};
window.dataLayer = window.dataLayer || [];

Mntl.PageView = (function(docCookies) {
    var _data = { event: 'unifiedPageview' };
    var _domain = window.location.href.substring(window.location.href.indexOf('.'), window.location.href.indexOf('.com')) + '.com';
    var _initialized = false;

    function _convertToOriginalTypes(data) {
        // Convert data from jquery data() to original types from initialization
        var convertedData = data;

        Mntl.fnUtilities.iterate(data, function(dat, key) {
            // Don't convert data type if types match or is blank string (used as default value in GTM)
            if (typeof _data[key] === typeof dat[key]
                || dat[key] === ''
                || (_data[key] === '' && typeof dat[key] === 'number')) {
                return;
            }
            // Convert to string
            if (typeof _data[key] === 'string') {
                convertedData[key] += '';
            }
            // Convert to number
            if (typeof _data[key] === 'number') {
                convertedData[key] = parseInt(dat[key], 10);
            }
        });

        return convertedData;
    }

    /**
     * Update _data with new values but ignore new keys
     * @param       {Object} data
     * @constructor
     */
    function _setData(data) {
        Object.keys(_data).forEach(function(key) {
            if (data.hasOwnProperty(key)) {
                _data[key] = data[key];
            }
        });
    }

    function pushToDataLayer(data) {
        // Enforce prior initialization so we can convert values from jquery data() to original types
        if (!_initialized) {
            debug.warn('Mntl.PageView not initialized with default values. Cannot push data.');

            return false;
        }
        // If we have new data, extend cached data
        if (data) {
            _setData(_convertToOriginalTypes(data));
        }

        // Push cached data to data layer
        return dataLayer.push(Mntl.fnUtilities.deepExtend({}, _data));
    }

    // Not used yet
    function setEntryType(entryType) {
        docCookies.setItem('pageEntryType', entryType, null, '/', _domain);
    }

    function init(data) {
        if (_initialized) {
            return false;
        }
        // Set initial data with original data types
        _data = Mntl.fnUtilities.deepExtend(_data, data);
        _initialized = true;

        return dataLayer.push(Mntl.fnUtilities.deepExtend({}, _data));
    }

    Mntl.utilities.onLoad(function() {
        docCookies.removeItem('pageEntryType', '/', _domain);
    });

    return {
        pushToDataLayer: pushToDataLayer,
        setEntryType: setEntryType, // Not used yet
        init: init
    };
}(window.docCookies));
