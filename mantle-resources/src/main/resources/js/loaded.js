// We want to set a data-attribute to mark scripts as 'loaded' so that `Mntl.utilities.scriptOnLoad`
// has some way of knowing that a script has already loaded and can therefore safely fire the callback.
// Otherwise there is the potential that the callback may never fire.
// See https://dotdash.atlassian.net/browse/GLBE-7230
(function() {
    // Need to set on document to ensure all scripts beyond this point are caught.
    // When limited to the current script tags the coverage is incomplete.
    document.addEventListener('load', (ev) => {
        if (ev.target.tagName === 'SCRIPT') {
            ev.target.dataset.loaded = 'loaded';
        }
    }, { capture: true });

    // In addition to script attributes we set this class in the HTML element to allow verticals to render
    // components when the window is loaded. 
    function setIsWindowLoaded() {
        // Avoid a blocking style recalculation for later functions that run on page load
        window.requestAnimationFrame(() => document.documentElement.classList.add('is-window-loaded'));
    }
  
    if (document.readyState === 'complete') {
        setIsWindowLoaded();
    } else {
        window.addEventListener('load', setIsWindowLoaded);
    }
}());
