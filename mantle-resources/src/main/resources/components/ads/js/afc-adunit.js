// ads/afc/afcAdUnit.ftl
(function($, Mntl) {
    function adsenseFn() {
        $('.adsense:not(.adsense-loaded)').each(function() {
            var $this = $(this);

            Mntl.AFC.addAdsenseAfcConfig($this.attr('id'), Number($this.attr('adsense-numlinks')), $this.attr('adsense-displaylabel') === 'true');
        });
    }

    adsenseFn();

    $(document).on('defer-batch-complete', adsenseFn);
})(window.jQuery || {}, window.Mntl || {});
