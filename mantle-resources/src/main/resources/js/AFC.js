window.Mntl = window.Mntl || {};

Mntl = $.extend(Mntl, function() {
    var AFC = (function($) {
        var adsenseAfcConfigs = [];
        var dynamicUrl = false;
        var dynamicHints = false;
        var dynamicChannel = false;

        /* eslint-disable camelcase */
        function extendGoogleAdChannel(base) {
            var mptCookie = docCookies.getItem('mpt');
            var askoc = docCookies.getItem('askoc');
            var gmcidCookie = docCookies.getItem('GMCID');
            var duplicateChannelIds = {};
            var google_ad_channel = [];
            var uniqueChannelIds = [];
            var i;
            var channelId;

            if (typeof base === 'string') {
                base = base.split(' ');
            }

            if (askoc) {
                google_ad_channel.push(askoc);
                google_ad_channel.push('semd');
            } else {
                if (mptCookie) {
                    google_ad_channel.push(mptCookie);
                }
                if (gmcidCookie) {
                    google_ad_channel.push(gmcidCookie);
                }
            }

            if (base) {
                google_ad_channel = google_ad_channel.concat(base);
            }

            // remove duplicates
            for (i = 0; i < google_ad_channel.length; i++) {
                channelId = google_ad_channel[i]; // eslint-disable-line
                if (!duplicateChannelIds[channelId]) {
                    duplicateChannelIds[channelId] = true;
                    uniqueChannelIds.push(channelId);
                }
            }

            // turn back to a string
            return uniqueChannelIds.join(' ');
        }
        /* eslint-enable */

        function addAdsenseAfcConfig(unitId, numLinks, displayLabel) {
            var config = {
                unitId: unitId,
                numLinks: numLinks,
                displayLabel: displayLabel
            };
            var arr = [];
            var i;
            var len = adsenseAfcConfigs.length;

            for (i = 0; i < len; i++) {
                arr.push(adsenseAfcConfigs[i].unitId);
            }

            if (arr.indexOf(unitId) < 0) {
                adsenseAfcConfigs.push(config);
            }
        }

        function getTotalAdsenseAfcs() {
            var total = 0;

            adsenseAfcConfigs.forEach(function(elem) {
                total += elem.numLinks;
            });

            return total;
        }

        /* eslint-disable camelcase */
        function buildAdsenseAfc(label, containerSelector) {
            var count = 0;
            var $container = $('.' + containerSelector);

            function build(elem) {
                var html = '';
                var i;

                if (!google_ads[count]) {
                    return false;
                }

                if (elem.displayLabel !== false) {
                    html += '<div class="label">' + label + '</div>';
                }

                for (i = count; i < count + elem.numLinks; i++) {
                    if (!google_ads[i]) {
                        break;
                    }

                    html += [
                        '<div class="adsense-group"><div class="adsense-title"><a href="',
                        google_ads[i].url,
                        '" lnp="',
                        i,
                        '" target="_blank" title="',
                        google_ads[i].line2,
                        google_ads[i].line3,
                        '">',
                        google_ads[i].line1,
                        '</a></div><div class="adsense-url"><a href="',
                        google_ads[i].url,
                        '" lnp="',
                        i,
                        '" target="_blank" title="',
                        google_ads[i].line2,
                        google_ads[i].line3,
                        '">',
                        google_ads[i].visible_url.substr(0, 35),
                        '</a></div><div class="adsense-desc">',
                        google_ads[i].line2,
                        ' ',
                        google_ads[i].line3,
                        '</div></div>'
                    ].join('');
                }

                count += elem.numLinks;

                return html;
            }

            adsenseAfcConfigs.forEach(function(elem) {
                var $afcContainer;

                if ($container.length) {
                    $afcContainer = $container.find('#' + elem.unitId);
                } else {
                    $afcContainer = $('#' + elem.unitId);
                }

                if ($afcContainer.length) {
                    $afcContainer.html(build(elem));
                }
                $afcContainer.addClass('adsense-loaded');
                $afcContainer.trigger('afcLoaded');
            });
        }
        /* eslint-enable */

        function requestCpc(url, hints, channel, $container, config) {
            var $adRefresh = $('#adRefresh');
            var $this;

            debug.log('requestCpc()');

            if (url) {
                dynamicUrl = url;
            }

            if (hints) {
                dynamicHints = hints;
            }

            if (channel) {
                dynamicChannel = extendGoogleAdChannel(channel);
            }

            // if there is a container, the ad configs for this new container may be diff than the previous so re init
            // the configs
            if ($container.length) {
                adsenseAfcConfigs = [];

                $container.find('.adsense-slot').each(function() {
                    $this = $(this);
                    if ($this.parent().hasClass('expert-content-text')) {
                        Mntl.AFC.addAdsenseAfcConfig($this.attr('id'), (config && config.adsense) ? config.adsense : Number($this.attr('adsense-numlinks')), $this.attr('adsense-displaylabel') === 'true');
                    } else {
                        Mntl.AFC.addAdsenseAfcConfig($this.attr('id'), Number($this.attr('adsense-numlinks')), $this.attr('adsense-displaylabel') === 'true');
                    }
                });
            }

            if ($adRefresh.length) {
                $adRefresh.remove();
            }

            $('<iframe />', {
                name: 'adRefresh',
                id: 'adRefresh',
                src: '/googlecpcrefresh.html'
            }).appendTo('body');
        }

        function getDynamicHref() {
            return dynamicUrl;
        }

        function getDynamicHints() {
            return dynamicHints;
        }

        function getDynamicChannel() {
            return dynamicChannel;
        }

        return {
            addAdsenseAfcConfig: addAdsenseAfcConfig,
            getTotalAdsenseAfcs: getTotalAdsenseAfcs,
            buildAdsenseAfc: buildAdsenseAfc,
            extendGoogleAdChannel: extendGoogleAdChannel,
            requestCpc: requestCpc,
            getDynamicHref: getDynamicHref,
            getDynamicHints: getDynamicHints,
            getDynamicChannel: getDynamicChannel
        };
    }(window.jQuery || {}));

    return { AFC: AFC };
});
