window.Mntl = window.Mntl || {};

Mntl.vanillaScroll = (function() {
    let lastScrollTop = 0;
    const throttleScroll = Mntl.throttle(() => {
        const eventParams = { detail: { scrollTop: window.pageYOffset } };

        if (eventParams.detail.scrollTop > lastScrollTop) {
            eventParams.detail.direction = 'down';
        } else if (eventParams.detail.scrollTop < lastScrollTop) {
            eventParams.detail.direction = 'up';
        }

        lastScrollTop = eventParams.detail.scrollTop;
        const scrollEvent = new CustomEvent('mntl.scroll', eventParams);

        window.dispatchEvent(scrollEvent);
    }, 20);

    window.addEventListener('scroll', throttleScroll, { passive: true });
}());
