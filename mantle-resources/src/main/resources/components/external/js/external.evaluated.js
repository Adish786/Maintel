var Mntl = window.Mntl || {};

Mntl.scroll = Mntl.scroll || (function($) {
    var $window = $(window);
    var lastScrollTop = 0;
    var throttleScroll = Mntl.throttle(function(e) {
        var scrollEvent = $.extend({}, e);

        scrollEvent.type = 'mntl.scroll';
        scrollEvent.scrollTop = $window.scrollTop();

        if (scrollEvent.scrollTop > lastScrollTop) {
            scrollEvent.direction = 'down';
        } else if (scrollEvent.scrollTop < lastScrollTop) {
            scrollEvent.direction = 'up';
        }

        $window.trigger(scrollEvent);
        lastScrollTop = scrollEvent.scrollTop;
    }, 20);

    $window.on('scroll', throttleScroll);
}(window.jQuery || {}));

<#-- Workaround for some race conditions with external components on 3.12 with mantle 3.13 -->
<#-- We need to remove the above javascript when all the jquery is removed from the code base -->

${model.externalComponent.get().javascript}