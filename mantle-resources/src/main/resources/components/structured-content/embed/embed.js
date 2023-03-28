/**
 * Automatically adjusts the height of the embed iframe based on its content.
 * We use the assumption that the largest height is OK to take based on observations of several embeds always expanding.
 * May introduce white space if an embed collapses in size from a very large embed but haven't seen issues.
 *
 * Counterpart of mantle-resources/src/main/resources/components/structured-content/embed/endpoint/js/embed.js
 */

(function() {
    window.addEventListener('message', (message) => {
        if (message && message.data && typeof message.data.embedId === 'string') {
            const embedId = document.getElementById(message.data.embedId);
            const previousHeight = embedId.dataset.previousHeight ? embedId.dataset.previousHeight : 0;
            const embedHeight = message.data.embedHeight;

            if (embedId && (previousHeight < embedHeight)) {
                embedId.style.height = `${embedHeight}px`;
                embedId.dataset.previousHeight = embedHeight;

                const embedReflowEvent = new CustomEvent('embedReflow', { bubbles: true });

                document.dispatchEvent(embedReflowEvent);
            }
        }
    });
})();
