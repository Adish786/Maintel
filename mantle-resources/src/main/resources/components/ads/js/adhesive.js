Mntl.utilities.readyAndDeferred(() => {
    const adhesiveAd = document.querySelector('.mntl-adhesive-ad');

    if (adhesiveAd !== null) {
        window.addEventListener('mntl.scroll', () => {
            adhesiveAd.classList.add('is-visible');
        }, { once: true });
    }
});
