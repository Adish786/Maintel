// Setting document.domain avoids CORS problems with PL iframes
document.domain = document.domain; // eslint-disable-line no-self-assign

$('.code').each(function() {
    window.hljs.highlightBlock($(this)[0]);
});
