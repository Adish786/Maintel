(function(utils, domUtils) {
    const childNodeTabs = document.getElementsByClassName('mntl-taxonomysc-child-node');

    // Check whether there are in fact child nodes before proceeding.
    // Without this check lastChildNodeXPos throws an error and breaks additional JS on the page
    if (!childNodeTabs.length) {
        return;
    }

    const articleListsContainer = document.getElementsByClassName('mntl-taxonomysc-article-list-group')[0];
    const articleLists = document.getElementsByClassName('mntl-taxonomysc-article-list');
    const childNodeTabsContainer = document.getElementsByClassName('mntl-taxonomysc-child-nodes')[0];
    const childNodeTabsWrapper = document.getElementsByClassName('mntl-taxonomysc-child-nodes__wrapper')[0];
    const lastChildNodeXPos = childNodeTabs[childNodeTabs.length - 1].getBoundingClientRect().right;
    const moreButton = document.getElementsByClassName('mntl-taxonomysc-child-node__more-button')[0];
    const hasMoreButton = typeof moreButton !== 'undefined';

    // TO DO: investigate if we can remove is-scrollable logic and go with CSS-only solution for overflow
    function onHandleWindowResize() {
        if (lastChildNodeXPos > childNodeTabsContainer.offsetWidth) {
            childNodeTabsContainer.classList.add('is-scrollable');
        } else {
            childNodeTabsContainer.classList.remove('is-scrollable');
        }
    }

    const onHandleWindowResizeThrottled = Mntl.throttle(onHandleWindowResize, 200);

    function moveChildNodeTab(childNodeToRow) {
        const childTabsWrapper = document.getElementsByClassName('mntl-taxonomysc-child-nodes__wrapper')[0];
        const childTabToDropdownIndex = Array.prototype.indexOf.call(childTabsWrapper.children, moreButton.parentElement) - 1;
        const childTabToDropdown = document.getElementsByClassName('mntl-taxonomysc-child-node')[childTabToDropdownIndex];
        const childTabDropdown = document.getElementsByClassName('mntl-taxonomysc-child-node__dropdown')[0];
        const clonedTab = childNodeToRow.cloneNode(true);

        if (childTabDropdown.getElementsByClassName('is-hidden').length === 0) {
            childTabDropdown.prepend(childTabToDropdown);
        } else {
            const previousTab = childTabDropdown.getElementsByClassName('is-hidden')[0];

            previousTab.classList.remove('is-active');
            previousTab.classList.remove('is-hidden');
            childTabsWrapper.removeChild(childTabToDropdown);
        }

        clonedTab.classList.add('is-loaded');

        childTabsWrapper.insertBefore(clonedTab, moreButton.parentElement);
        childNodeToRow.classList.add('is-hidden');
    }

    function toggleShowAndHideClasses(articleListToShow) {
        const isHiddenClassName = 'is-hidden';

        for (let i = 0; i < articleLists.length; i++) {
            const articleListItem = articleLists[i];

            if (articleListItem === articleListToShow) {
                articleListItem.classList.remove(isHiddenClassName);
            } else {
                articleListItem.classList.add(isHiddenClassName);
            }
        }

        setTimeout(() => {
            articleListsContainer.classList.remove('is-loading');
        }, 500);
    }

    function loadNewChildList(childNodeTab) {
        const parentDocId = childNodeTab.getAttribute('data-docid');
        const context = new Mntl.Deferred.RequestContext();
        const urlParams = utils.getQueryParams(window.location.search);
        let finalUrl;

        context.setComponentIds('mntl-taxonomysc-child-list');
        context.htmlCallback = function(key, html) {
            const placeholder = document.createElement('div');
            let newArticleList;

            placeholder.innerHTML = html;
            childNodeTab.classList.add('is-loaded');
            placeholder.firstElementChild.setAttribute('data-docid', parentDocId);
            newArticleList = articleListsContainer.insertBefore(placeholder.firstElementChild, articleLists[articleLists.length - 1].nextSibling);
            toggleShowAndHideClasses(newArticleList);
        };

        urlParams.parentDocId = parentDocId;
        const urlParamString = `?${querystring.stringify(urlParams)}`;

        finalUrl = `${domUtils.getResourceRootUrl()}${window.location.pathname}${urlParamString}`;
        context.urlOverload = finalUrl;
        context.skipGenerify = true;

        Mntl.Deferred.requestComponents(context);
    }

    if (!hasMoreButton) {
        window.addEventListener('resize', onHandleWindowResizeThrottled);
    }

    function init() {
        let parentDocId;
        let articleListToShow;

        if (hasMoreButton) {
            moreButton.addEventListener('click', () => {
                moreButton.classList.toggle('is-active');
            });

            document.addEventListener('click', (e) => {
                if (moreButton.classList.contains('is-active') &&
                    !moreButton.contains(e.target)) {
                    moreButton.classList.remove('is-active');
                }
            });
        }

        // On click, check if clicked node list has already been loaded and show it/hide others
        // If not loaded, request child list component
        childNodeTabsWrapper.addEventListener('click', (event) => {
            event.preventDefault();
            const childNodeTab = event.target.closest('.mntl-taxonomysc-child-node');

            if (!childNodeTab || articleListsContainer.classList.contains('is-loading')) {
                return;
            }

            articleListsContainer.classList.add('is-loading');
            const previousActiveTab = childNodeTabsContainer.getElementsByClassName('is-active')[0];

            parentDocId = childNodeTab.getAttribute('data-docid');

            if (previousActiveTab) {
                previousActiveTab.classList.remove('is-active');
            }

            childNodeTab.classList.add('is-active');

            if (childNodeTab.classList.contains('is-loaded')) {
                articleListToShow = document.querySelector(`.mntl-taxonomysc-article-list[data-docid=\"${parentDocId}\"]`);
                toggleShowAndHideClasses(articleListToShow);
            } else {
                loadNewChildList(childNodeTab);
            }

            if (childNodeTab.parentElement.classList.contains('mntl-taxonomysc-child-node__dropdown')) {
                moreButton.classList.remove('is-active');
                moveChildNodeTab(childNodeTab);
            }
        });

        // If there are overflowing child node tabs, make container scrollable
        if (lastChildNodeXPos > childNodeTabsContainer.offsetWidth && !hasMoreButton) {
            childNodeTabsContainer.classList.add('is-scrollable');
        }
    }

    init();
})(window.Mntl.utilities || {}, window.Mntl.domUtilities || {});
