/*
 * Checks to see if table body will overflow the table parent.
 * If it doesn't, add no-scroll class to the table wrapper.
 * Alleviates any scrollbar issues, particularly with inline citations in tables
 * However, tables with overflow will still have x and y scrollbars
 */

window.addEventListener('readyForThirdPartyTracking', () => {
    const tables = [...document.querySelectorAll(".mntl-sc-block-table")];

    tables.forEach((table) => {
        const tableBody = table.querySelector('tbody');

        if (tableBody.offsetWidth <= table.offsetWidth) {
            const tableWrapper = table.querySelector('.mntl-sc-block-table__table-wrapper');

            tableWrapper.classList.add('mntl-sc-block-table__table-wrapper--no-scroll');
        }
    });
});