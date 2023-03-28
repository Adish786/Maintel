(function($, Mntl) {
    function seeMoreClickHandler(e) {
        var $seeMoreBtn = $(this);
        var $seeMoreWrapper = $seeMoreBtn.parents('.see-more-wrapper');
        var moreText = $seeMoreBtn.attr('data-more') || 'More';
        var lessText = $seeMoreBtn.attr('data-less') || 'Less';
        var $btnText = $seeMoreBtn.find('.text');

        e.preventDefault();

        if ($seeMoreWrapper.hasClass('collapsed')) {
            $seeMoreWrapper.find('.see-more-content').css('max-height', '');
            $btnText.html(lessText);
        } else {
            $seeMoreWrapper.find('.see-more-content').css('max-height', $seeMoreWrapper.data('height'));
            $btnText.html(moreText);
        }

        $seeMoreWrapper.toggleClass('collapsed');
    }

    function init($seeMoreWrappers) {
        var $seeMoreWrapper;
        var $seeMoreBtn;
        var $seeMoreContent;

        if ($seeMoreWrappers.length) {
            $seeMoreWrappers.each(function() {
                $seeMoreWrapper = $(this);
                $seeMoreBtn = $seeMoreWrapper.find('.see-more-wrapper-btn');
                $seeMoreContent = $seeMoreWrapper.find('.see-more-content');
                if ($seeMoreContent.children().height() > $seeMoreWrapper.data('height')) {
                    $seeMoreBtn.on('click', seeMoreClickHandler);
                    $seeMoreBtn.css('visibility', 'visible');
                } else {
                    $seeMoreWrapper.removeClass('collapsed');
                    $seeMoreContent.css('max-height', '');
                    $seeMoreWrapper.find('.see-more-btn-container').hide();
                }
            });
        }
    }

    if (Mntl.Deferred) {
        $('[data-defer]').on('deferred-loaded', function() {
            init($(this).find('.see-more-wrapper'));
        });
    }

    init($('.see-more-wrapper'));
})(window.jQuery || {}, window.Mntl || {});
