(function(Mntl) {
    Mntl.utilities.readyAndDeferred(function(context) {
        var relishScriptArgs = {
            key: "${model.key}",
            domain: "${model.domain}"
        };

        Mntl.Relish.init(relishScriptArgs, "${model.shopButtonOffset}", context);
    });
})(window.Mntl || {});