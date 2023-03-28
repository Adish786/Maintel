window.Mntl = window.Mntl || {};

Vue.directive('mntl-social-share', {
    inserted: function(shareContainer) {
        var shareTitle = shareContainer.dataset.title;
        var description = shareContainer.dataset.description;
        var shareUrl = shareContainer.dataset.shareUrl;
        var shareImageUrl = shareContainer.dataset.shareImage;
        var baseUtmParams = '?utm_medium=social&utm_campaign=' + shareContainer.dataset.utmCampaign;

        Mntl.fnUtilities.toArray(shareContainer.querySelectorAll('.share-item'))
            .forEach(function(shareLink) {
                var network = shareLink.dataset.network;
                var baseUrl = shareLink.dataset.href;
                var transformedShareURL = new URL(baseUrl);
                var qs = querystring.parse(transformedShareURL.search);
                var utmParams = baseUtmParams + '&utm_source=' + network;
                var shareUrlWithParams = shareUrl + utmParams;

                if (network === 'twitter') {
                    qs.url = shareUrlWithParams;
                    qs.text = description;
                    qs.via = shareContainer.dataset.twitterHandle;
                    transformedShareURL.search = querystring.stringify(qs);
                    shareLink.setAttribute('data-href', transformedShareURL.href);
                } else if (network === 'pinterest') {
                    qs.url = shareUrlWithParams;
                    qs.description = shareContainer.dataset.pinterestDescription || description;
                    qs.media = shareImageUrl;
                    transformedShareURL.search = querystring.stringify(qs);
                    shareLink.setAttribute('data-href', transformedShareURL.href);
                } else if (network === 'facebook') {
                    qs.href = shareUrlWithParams;
                    transformedShareURL.search = querystring.stringify(qs);
                    shareLink.setAttribute('data-href', transformedShareURL.href);
                } else if (network === 'emailshare') {
                    shareLink.setAttribute('data-href', shareUrlWithParams);
                    shareContainer.setAttribute('data-email-subject', shareContainer.dataset.emailSubject || shareTitle);
                    shareContainer.setAttribute('data-email-body', shareContainer.dataset.emailBody || description + ' ' + shareUrlWithParams);
                } else if (network === 'sms') {
                    shareLink.setAttribute('data-href', shareUrlWithParams);
                    shareContainer.setAttribute('data-sms-body', shareContainer.dataset.smsBody || description + ' ' + shareUrlWithParams);
                }
            });
    }
});
