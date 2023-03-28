(function(Mntl) {
    Mntl.utilities.onLoad(function() {
        Mntl.utilities.loadExternalJS({
            src: '//s.skimresources.com/js/${model.skimlinksId}.skimlinks.js',
            async: true
        });
    });
}(window.Mntl || {}));

window.skimlinks_settings = {};
window.skimlinks_settings.skimlinks_tracking = "${requestContext.urlData.docId?c}|${(requestContext.requestId)!''}";
