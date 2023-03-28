(function(utils) {
    utils.readyAndDeferred(function() {
        var masonryInstance = document.getElementById('${manifest.instanceId}');
        if (masonryInstance.dataset.noJs) return;

        Mntl.MasonryList.init(masonryInstance, ${model.justifyConfig!'{}'});
    });
})(window.Mntl.utilities || {});