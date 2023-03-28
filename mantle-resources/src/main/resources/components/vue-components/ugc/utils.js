export function getProp(object, keys, defaultVal) {
    keys = Array.isArray(keys) ? keys : keys.split('.');
    object = object[keys[0]];
    if (object && keys.length > 1) {
        return getProp(object, keys.slice(1), defaultVal);
    }

    return object ? object : defaultVal;
}

export function pad(n) {
    return (n < 10 ? '0' : '') + n;
}

export function dataLayerPush(eventLabel, eventAction, eventCategory) {
    const trackClick = Mntl.utilities.pushToDataLayer({
        event: 'transmitInteractiveEvent',
        eventCategory: eventCategory || 'UGC',
        eventAction: eventAction || 'Content Action Taken',
        eventLabel: eventLabel || '',
        nonInteraction: 0
    });

    trackClick();
}

export function fetchAccessToken(isLoggedIn, state, auth0Link) {
    return new Promise((resolve, reject) => {
        if (isLoggedIn) {
            // Always fetch a new token instead of using existing in case of token expiration
            if (Mntl.auth0.auth0Client) {
                Mntl.auth0.getAccessToken().then(() => {
                    if (Mntl.auth0.accessToken) {
                        Mntl.VueInGlobe.apps.ugcVue.datastore.dispatch('setAccessToken', Mntl.auth0.accessToken);

                        resolve(Mntl.auth0.accessToken);
                    } else {
                        reject('unable to get token');
                    }
                })
                    .catch(() => {
                        reject('unable to get token');
                    });
            } else {
                document.addEventListener(Mntl.auth0.CLIENT_INITED_EVENT_NAME, () => {
                    Mntl.auth0.getAccessToken().then(() => {
                        if (Mntl.auth0.accessToken) {
                            Mntl.VueInGlobe.apps.ugcVue.datastore.dispatch('setAccessToken', Mntl.auth0.accessToken);

                            resolve(Mntl.auth0.accessToken);
                        } else {
                            reject('unable to get token');
                        }
                    })
                        .catch(() => {
                            reject('unable to get token');
                        });
                });
                document.dispatchEvent(new CustomEvent(Mntl.auth0.CLIENT_INIT_EVENT_NAME, { detail: { auth0Link } }));
            }
        } else {
            reject('user not logged in');
        }
    });
}

export function stripHtml(html) {
    const tmp = document.createElement('div');

    tmp.innerHTML = html;

    return tmp.textContent || tmp.innerText || '';
}

/**
 * For blockhtml components in UDF, we should have proper HTML for line breaks.
 * @param text
 */
export function handleLineBreaks(text) {
    // eslint-disable-next-line no-param-reassign
    text = stripHtml(text);
    let lineBreak = '\n';

    if (text.indexOf('\r\n') !== -1) {
        lineBreak = '\r\n';
    }

    let paragraphs = text.split(`${lineBreak}${lineBreak}`);

    paragraphs = paragraphs.map((paragraph) => {
        // eslint-disable-next-line no-param-reassign
        paragraph = paragraph.trim();

        if (paragraph.indexOf(lineBreak) > -1) {
            const lines = paragraph.split(lineBreak).map((line) => line.trim());

            paragraph = lines.join('<br />');
        }

        return paragraph;
    });

    return `<p>${paragraphs.join('</p><p>')}</p>`;
}

/**
 * Convert html line breaks to line break characters for display of the data
 * inside text inputs.
 * @param userText
 * @returns {*}
 */
export function convertHtmlLineBreaks(userText) {
    let result = '';
    const paragraphs = userText.split('<p>');

    paragraphs.forEach((paragraph) => {
        if (paragraph.trim() !== '') {
            let resultingParagraph = paragraph.replace('</p>', '\r\n\r\n');

            if (resultingParagraph.indexOf('<br />') > -1) {
                resultingParagraph = resultingParagraph.split('<br />').join('\r\n');
            }
            result += resultingParagraph;
        }
    });
    result = result.trim();

    return result;
}

export function formatDate(dateStringIsoFormat) {
    const date = new Date(dateStringIsoFormat);

    return `${pad(date.getUTCMonth() + 1)}/${pad(date.getUTCDate())}/${date.getUTCFullYear()}`;;
}

export function reviewStatusCheck(state) {
    return new Promise((resolve, reject) => {
        const formData = new FormData();

        formData.append('docId', state.docId);
        if (state.legacyMeredithId) {
            formData.append('cmsId', state.legacyMeredithId);
        }
        formData.append('accessToken', state.accessToken);

        Mntl.utilities.ajaxPromisePost('/resound/review/status', formData, null)
            .then((response) => {
                try {
                    const result = JSON.parse(response);

                    if (result) {
                        if (result.status === 200) {
                            const userReview = getProp(result, 'review.entity.udf.user_text');
                            const rating = getProp(result, 'review.entity.udf.rating_five_star');
                            const publishedDate = getProp(result, 'review.entity.udf.publish_date');
                            const photoUpload = result.review.entity.udf.media
                                ? getProp(result.review.entity.udf.media[0], 'entity.udf.original.src')
                                : '';
                            const status = getProp(result, 'review.moderationStatus');
                            const dateString = formatDate(publishedDate);

                            Mntl.VueInGlobe.apps.ugcVue.datastore.dispatch('userSubmissionStore/setData', {
                                review: userReview,
                                starRating: rating,
                                created: dateString,
                                ...(photoUpload && { photoUrl: photoUpload })
                            });

                            Mntl.VueInGlobe.apps.ugcVue.datastore.dispatch('feedbackSummaryStore/setStatus', status);

                            resolve(result);
                        } else {
                            console.error('/resound/review/status not getting 200 http status', result);
                            reject(result.status);
                        }
                    }
                } catch(parseError) {
                    console.error('/resound/review/status error (200 status) Unable to parse response', parseError);
                    reject(parseError);
                }
            })
            .catch((error) => {
                console.error('/resound/review/status Unable to post', error);
                reject(error);
            });
    });
}

export function postFeedback(state, userSubmission) {
    return new Promise((resolve, reject) => {
        // Submit data to BE
        const formData = new FormData();

        formData.append('docId', state.docId);
        if (state.legacyMeredithId) {
            formData.append('cmsId', state.legacyMeredithId);
        }
        formData.append('accessToken', state.accessToken);
        formData.append('rating', userSubmission.starRating);
        formData.append('reviewText', userSubmission.review);
        if (userSubmission.photoUrl) {
            formData.append('photoUrl', userSubmission.photoUrl);
        }

        Mntl.utilities.ajaxPromisePost('/resound/review/submit', formData, null)
            .then((response) => {
                try {
                    const result = JSON.parse(response);
                    const { status } = result;

                    if (result) {
                        if (status === 202) {
                            resolve(response);
                        } else {
                            Mntl.VueInGlobe.apps.ugcVue.datastore.dispatch('feedbackSummaryStore/setResoundSubmitError', true);

                            console.error('/resound/review/submit not getting 202 http status', result);
                            reject(status);
                        }
                    }
                } catch (error) {
                    console.error('/resound/review/submit error (202 status) Unable to parse response', error);
                    reject(error);
                }
            })
            .catch((error) => {
                console.error('/resound/review/submit Unable to post', error);
                reject(error);
            });
    });
}

/**
 * Parse fallbackAvatars JSON and set array to length of numberOfFallbackAvatars
 * Fallback if parse fails
 * @param   {String}   fallbackAvatars
 * @param   {Integer} numberOfFallbackAvatars
 * @returns {Array}
 */
export function getFallbackAvatarsArray(fallbackAvatars, numberOfFallbackAvatars) {
    const newFallbackAvatarsArr = [];

    try {
        const fallbackAvatarsArr = JSON.parse(fallbackAvatars);
        const avatarsLength = fallbackAvatarsArr.length;

        if (avatarsLength < numberOfFallbackAvatars) { // If length of fallbackAvatars is less than numberOfFallbackAvatars
            for (let i = 0; i < numberOfFallbackAvatars; i++) {
                for (let ii = 0; ii < avatarsLength; ii++) {
                    newFallbackAvatarsArr.push(fallbackAvatarsArr[ii]);
                }
            }

            return newFallbackAvatarsArr;
        } else if (avatarsLength > numberOfFallbackAvatars) { // If length of numberOfFallbackAvatars is less than fallbackAvatars
            for (let i = 0; i < numberOfFallbackAvatars; i++) {
                newFallbackAvatarsArr.push(fallbackAvatarsArr[i]);
            }

            return newFallbackAvatarsArr;
        }

        return fallbackAvatarsArr;

    } catch(err) { // If parse fails, reset to default
        console.log(`Unable to json parse fallbackAvatars ${fallbackAvatars}`, err);

        if (numberOfFallbackAvatars > 1) { // If length of numberOfFallbackAvatars is greater than 1
            for (let i = 0; i < numberOfFallbackAvatars; i++) {
                newFallbackAvatarsArr.push('ugc-icon-avatar');
            }

            return newFallbackAvatarsArr;
        }

        return ['ugc-icon-avatar'];

    }
}