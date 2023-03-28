(function() {
    const [filter] = document.querySelectorAll('.filter input');
    const [filterError] = document.getElementsByClassName('categories-error');
    const filterEvent = new CustomEvent('filter');
    const [categories] = document.getElementsByClassName('pl-categories-wrapper');
    const [categoryList] = categories.getElementsByTagName('div');
    const categoryListCopy = categoryList.cloneNode(true);
    const categoryCopyLinks = categoryListCopy.getElementsByTagName('a');
    const [labelSelect] = document.querySelectorAll('.labels select');
    const searchKey = 'plNavQuery';
    const labelKey = 'plNavLabel';
    let storageTimeout; // Tracked here to allow for throttling when saving to Local Storage in the event
    let swap = false;

    categoryListCopy.style.zIndex = '1';
    categoryListCopy.querySelectorAll('.accordion-trigger, .accordion-content').forEach((element) => {
        element.classList.remove('accordion-trigger', 'is-open', 'accordion-content', 'is-closed');
    });
    // Use querySelectorAll for forEach
    categoryListCopy.querySelectorAll('.accordion-arrow').forEach((element) => {
        element.remove();
    });
    categories.appendChild(categoryListCopy);

    function swapLists() {
        if (swap) {
            categoryListCopy.style.zIndex = '3';
            categoryList.style.zIndex = '1';
        } else {
            categoryListCopy.style.zIndex = '1';
            categoryList.style.zIndex = '3';
        }
    }

    categoryListCopy.addEventListener('filter', () => {
        const label = labelSelect.value;
        const query = filter.value.trim();
        let labelsData;
        let matches;

        if (storageTimeout) {
            clearTimeout(storageTimeout);
        }

        storageTimeout = setTimeout(() => {
            localStorage.setItem(searchKey, query);
            localStorage.setItem(labelKey, label);
        }, 500);

        filterError.classList.add('u-hidden');
        [...categoryCopyLinks].forEach((link) => {
            link.classList.remove('matched');
        });
        categoryListCopy.classList.add('filtering');

        if (label === 'all' && query === '') {
            if (swap) {
                swap = false;
                swapLists();
            }
            categoryListCopy.classList.remove('filtering');
            [...categoryCopyLinks].forEach((link) => {
                link.classList.remove('matched');
            });
        } else {
            if (!swap) {
                swap = true;
                swapLists();
            }
        }

        matches = [...categoryCopyLinks].filter((link) => {
            let labelMatch = true;
            let searchMatch = true;

            if (label !== 'all') {
                labelsData = link.getAttribute('data-labels');
                labelMatch = labelsData && labelsData.indexOf(label) !== -1;
            }

            if (query !== '') {
                searchMatch = link.innerText.match(new RegExp(query, 'i')) !== null;
            }

            return labelMatch && searchMatch;
        });

        matches.forEach((link) => {
            link.classList.add('matched');
        });

        if (!matches.length) {
            filterError.classList.remove('u-hidden');
        }
    });

    if (localStorage.getItem(searchKey) !== null && localStorage.getItem(labelKey) !== null) {
        filter.value = localStorage.getItem(searchKey);
        labelSelect.value = localStorage.getItem(labelKey);
        categoryListCopy.dispatchEvent(filterEvent);
    }

    filter.addEventListener('keyup', () => {
        categoryListCopy.dispatchEvent(filterEvent);
    });

    labelSelect.addEventListener('change', () => {
        categoryListCopy.dispatchEvent(filterEvent);
    });
}());
