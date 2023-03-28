window.Mntl = window.Mntl || {};

Mntl.RevenueTracking = (function(docCookies) {
    // SEM params we care about and use elsewhere
    const SEM_PARAMS = ['an', 'qsrc', 'q', 'am', 'dqi', 'askid'];
    const SEM_FALLBACK_VALUE = 'A1';

    // Map of tracking values
    // First index is the upstream tracking value
    // Second index is the downstream value (if one exists)
    const trackingValues = {
        shareurlbuttons: {
            facebook: ['S0', 'S8'],
            stumble: ['S1', 'S9'],
            reddit: ['S2', 'S10'],
            pinterest: ['S3', 'S11'],
            google: ['S4', 'S12'],
            linkedin: ['S5', 'S13'],
            twitter: ['S6', 'S14'],
            permalink: ['S7', 'S15'],
            social1: ['S64', 'S65']
        },
        mobilesharebutton2: {
            facebook: ['S16', 'S24'],
            stumble: ['S17', 'S25'],
            reddit: ['S18', 'S26'],
            pinterest: ['S19', 'S27'],
            google: ['S20', 'S28'],
            linkedin: ['S21', 'S29'],
            twitter: ['S22', 'S30'],
            permalink: ['S23', 'S31']
        },
        cmssocialpub: {
            facebook: ['S32', 'S40'],
            stumble: ['S33', 'S41'],
            reddit: ['S34', 'S42'],
            pinterest: ['S35', 'S43'],
            google: ['S36', 'S44'],
            linkedin: ['S37', 'S45'],
            twitter: ['S38', 'S46'],
            permalink: ['S39', 'S47']
        },
        cmsocialposting: {
            facebook: ['S48', 'S56'],
            stumble: ['S49', 'S57'],
            reddit: ['S50', 'S58'],
            pinterest: ['S51', 'S59'],
            google: ['S52', 'S60'],
            linkedin: ['S53', 'S61'],
            twitter: ['S54', 'S62'],
            permalink: ['S55', 'S63']
        },
        socialads1: {
            facebook: ['S66', 'S67'],
            twitter: ['S68', 'S69']
        },
        con: {
            out: ['S76'],
            tbl: ['S77'],
            facebook: ['S78'],
            oth: ['S79']
        },
        email: {
            exp_nl: ['MO'], // eslint-disable-line camelcase
            cn_nl: ['M1'], // eslint-disable-line camelcase
            abt_td: ['M2'], // eslint-disable-line camelcase
            ecou: ['M3'],
            do: ['M4'],
            welc: ['M5']
        },
        ity: {
            indexsem: ['A1'],
            display: ['A2'],
            boostsem: ['A3'],
            boostorg: ['A4'],
            web: ['A5'],
            boostdd: ['A6']
        }
    };

    function lookupDownsteamValue(upstreamValue) {
        if (upstreamValue) {
            for (const campaign in trackingValues) {
                for (const source in trackingValues[campaign]) {
                    if (upstreamValue === trackingValues[campaign][source][0]) {
                        return trackingValues[campaign][source][1];
                    }
                }
            }
        }

        return false;
    }

    function _setCookie(trackingKey, upstreamValue) {
        const existingValue = docCookies.getItem(trackingKey);
        const newTrackingValue = existingValue ? lookupDownsteamValue(existingValue) : upstreamValue;

        if (newTrackingValue) {
            // if either initial or downstream tracking value was found, set the cookie to that value
            docCookies.setItem(trackingKey, newTrackingValue, null, '/', Mntl.utilities.getDomain());
        }
    }

    function _setSemCookies(queryParams, askoc) {
        const exp = 1800; // 30mins in secs

        docCookies.setItem('askoc', askoc, exp, '/', Mntl.utilities.getDomain());

        // store all the SEM params as cookies
        SEM_PARAMS.forEach((semParam) => {
            if (queryParams[semParam]) {
                docCookies.setItem(semParam, queryParams[semParam].toLowerCase(), exp, '/', Mntl.utilities.getDomain());
            }
        });
    }

    // save utm params for session duration
    function _getUtmObject() {
        const storedParams = JSON.parse(window.sessionStorage.getItem('abtUtm'));
        const queryParams = Mntl.utilities.getQueryParams();
        let utmParams = {
            utm_content: null, // eslint-disable-line camelcase
            utm_medium: null, // eslint-disable-line camelcase
            utm_source: null, // eslint-disable-line camelcase
            utm_campaign: null, // eslint-disable-line camelcase
            utm_term: null // eslint-disable-line camelcase
        };
        const queryKeys = Object
            .keys(queryParams)
            .map((key) => {
                if (utmParams.hasOwnProperty(key)) {
                    utmParams[key] = queryParams[key];

                    return key;
                }

                return false;
            })
            .filter((val) => val !== false);

        // url params should always trump storage
        if (queryKeys.length) {
            window.sessionStorage.setItem('abtUtm', JSON.stringify(utmParams));
        } else if (storedParams) {
            utmParams = storedParams;
        }

        return utmParams;
    }

    /* eslint-disable camelcase */
    function setTrackingCookies() {
        const params = Mntl.utilities.getQueryParams();
        const utm_campaign = params.utm_campaign ? params.utm_campaign.toLowerCase() : '';
        const utm_source = params.utm_source ? params.utm_source.toLowerCase() : '';
        const utm_medium = params.utm_medium ? params.utm_medium.toLowerCase() : '';
        const askoc = params.o ? params.o.toLowerCase() : '';
        const ity = params.ity ? params.ity.toLowerCase() : '';
        const { utm_content } = params;
        let upstreamValue;
        const abtUtm = _getUtmObject();
        let mptCookie;

        if (trackingValues[utm_campaign] && trackingValues[utm_campaign][utm_source]) {
            [upstreamValue] = trackingValues[utm_campaign][utm_source];
        } else if (trackingValues[utm_medium]
          && trackingValues[utm_medium][utm_source]
          && (utm_medium === 'email' || utm_medium === 'con')) {
            [upstreamValue] = trackingValues[utm_medium][utm_source];
        } else if (askoc) {
            upstreamValue = ity ? trackingValues.ity[ity][0] : SEM_FALLBACK_VALUE;
            _setSemCookies(params, askoc);
        }

        if (upstreamValue
          || utm_content
          || docCookies.getItem('mpt')
          || docCookies.getItem('GMCID')) {
            // mpt cookie is set to the same as GMCID except for content marketing
            mptCookie = utm_medium === 'con' ? utm_content : upstreamValue;

            _setCookie('GMCID', upstreamValue);
            _setCookie('mpt', mptCookie);
        }

        if (Mntl.GPT) {
            Mntl.GPT.updateBaseSlotTargeting({
                mpt: docCookies.getItem('mpt'),
                o: docCookies.getItem('askoc'),
                uco: abtUtm.utm_content,
                umd: abtUtm.utm_medium,
                usc: abtUtm.utm_source,
                uca: abtUtm.utm_campaign,
                utr: abtUtm.utm_term
            });
        }
    }
    /* eslint-enable */

    setTrackingCookies();

    // set a cookie temporarily so Ask SEM team can always send site search users to their new node platform
    // Can be removed once migration is 100% complete
    docCookies.setItem('lb_ld', 'search', null, '/', Mntl.utilities.getDomain());

    return { setTrackingCookies };
})(window.docCookies);
