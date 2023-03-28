window.Mntl = window.Mntl || {};

Mntl.jsErrors = [];

(function() {
    // if error domain is from the current domain, add to array
    window.onerror = function(error, url, lineNo, colNo) {
        var errorData = {};

        // check if domain is from the current domain
        if (Mntl.utilities.isCurrentDomain(url)) {
            errorData.errName = error;
            errorData.urlName = url;
            errorData.lineNum = lineNo;
            errorData.colNum = colNo;
            Mntl.jsErrors.push(errorData);
        }
    };
})();
