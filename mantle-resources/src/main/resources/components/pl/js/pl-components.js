// Setting document.domain avoids CORS problems with PL iframes
document.domain = document.domain; // eslint-disable-line no-self-assign

(function() {
    const previewPanel = document.querySelector('.preview-panel');

    /**
     * Helper function to determine the breakpoint size to be in px.
     * Currently handles values in em or px
     *
     * @returns {object} of breakpoints in px values
     */
    function determineBreakpointsInPx() {
        const globalBreakpoints = window.breakpoints;
        const localBreakpoints = {};
        const fontSize = parseFloat(getComputedStyle(document.body).fontSize); // Used to convert em -> px

        if (globalBreakpoints) {
            for (const breakpoint in globalBreakpoints) {
                if (globalBreakpoints[breakpoint].width && globalBreakpoints[breakpoint].width.indexOf('em') > -1) {
                    localBreakpoints[breakpoint] = parseFloat(globalBreakpoints[breakpoint].width) * fontSize;
                } else if (globalBreakpoints[breakpoint].width && globalBreakpoints[breakpoint].width.indexOf('px') > -1) {
                    localBreakpoints[breakpoint] = parseFloat(globalBreakpoints[breakpoint].width);
                }
            }
        }

        return localBreakpoints;
    }

    /**
     * Helper function used to setup the breakpoint links in the PL component page
     */
    function setupBreakpointsLinks() {
        const breakpoints = determineBreakpointsInPx();
        const container = document.querySelector('.snap-links.breakpoint-pointers');

        // Just display none the breakpoint field set in FTL
        if (!breakpoints) {
            container.style.display = 'none';

            return;
        }

        for (const breakpoint in breakpoints) {
            const link = document.createElement('a');

            link.href = '#';
            link.dataset.width = breakpoints[breakpoint];
            link.innerText = breakpoint;

            // Handles toggling logic when you click on a breakpoint link
            // to set active highlighted "state"
            link.addEventListener('click', (event) => {
                event.preventDefault(); // Don't go to anchor tag

                document.querySelectorAll('.snap-links.breakpoint-pointers a').forEach((aTags) => {
                    aTags.classList.remove('active');
                });

                event.target.classList.add('active');
                // Force the preview panel to be the width of the breakpoint
                previewPanel.style.width = `${breakpoints[breakpoint]}px`;
            });

            if (container) {
                container.appendChild(link);
            }
        }
    }

    /**
     * Helper function used to help setup the resizing logic with the window
     * on the preview panel
     */
    function setupPanelResizeFromWindowResize() {
        window.addEventListener('resize', () => {
            const parentWidth = previewPanel.parentElement.offsetWidth;

            previewPanel.style.width = `${parentWidth}px`;

            document.querySelectorAll('.snap-links.breakpoint-pointers a').forEach((aTags) => {
                aTags.classList.remove('active');
            });
        });
    }

    /**
     * Helper function that steps up the dev panel below when the user
     * expands on the code/model section in the pattern library
     */
    function setupDevPanel() {
        const devPanelLinks = document.querySelectorAll('.mntl-pl-component-tabs li a');

        devPanelLinks.forEach((devPanelLink) => {
            devPanelLink.addEventListener('click', (event) => {
                event.preventDefault(); // Don't go to anchor tag

                // Nested loop structure to toggle active and non-active cases
                devPanelLinks.forEach((link) => {
                    link.parentElement.classList.remove('is-active');
                });

                event.target.parentElement.classList.add('is-active');

                const contentElementId = event.target.hash;

                document.querySelectorAll('.tabs-panel').forEach((tabPanel) => {
                    tabPanel.classList.remove('is-active');
                });

                // Current toggle logic used to reveal the panel but tbh we should refactor this from the css
                // as follow up since we hide the panel on load. Causing active/inactive state to be tracked oddly.
                document.querySelector(contentElementId).classList.add('is-active');
                document.querySelector('.tabs-content').classList.add('active');
            });
        });
    }

    /**
     * Helper function that sets up iframe resizing logic when the content loads
     */
    function resizeIframeHeight() {
        const iframe = document.querySelector('.preview-panel iframe');

        if (iframe) {
            iframe.addEventListener('load', () => {
                const iframeBody = iframe.contentWindow.document.body;

                const observerConfig = {
                    attributes: true,
                    childList: true,
                    subtree: true
                };

                const observer = new MutationObserver(() => {
                    iframe.style.height = getComputedStyle(iframeBody).height;
                });

                observer.observe(iframeBody, observerConfig);
            });
        }
    }

    /**
     * Helper function to invoke libraries to pretty print the code portions of the PL
     */
    function prettyPrintCode() {
        document.querySelectorAll('code').forEach((code) => {
            if (code.dataset.xmlCode) {
                code.innerText = window.vkbeautify.xml(code.innerText);
            }

            if (code.classList.contains('language-css')) {
                code.innerText = window.vkbeautify.css(code.innerText);
            }

            window.hljs.highlightElement(code);
        });
    }

    /**
     * Helper function to help invoke libraries to format the markdown
     */
    function setupMarkdown() {
        const md = new window.markdownit({ // eslint-disable-line new-cap
            highlight: (str, lang) => {
                if (lang && window.hljs.getLanguage(lang)) {
                    try {
                        return window.hljs.highlight(str, { language: lang }).value;
                    } catch (e) {
                        console.log(`Error highlighting markdown: ${e}`);
                    }
                }

                return ''; // use external default escaping
            }
        });

        document.querySelectorAll('.markdown').forEach((markdownBlock) => {
            markdownBlock.innerHTML = md.render(markdownBlock.innerHTML);
        });
    }

    setupBreakpointsLinks();
    resizeIframeHeight();
    setupPanelResizeFromWindowResize();
    setupDevPanel();
    setupMarkdown();
    prettyPrintCode();
})();

