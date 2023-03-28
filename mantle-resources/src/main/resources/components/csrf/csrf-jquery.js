$.ajaxPrefilter((options, originalOptions) => {
    const csrfToken = window.Mntl.csrf() || 'INVALID';

    if(options.type.toUpperCase() == 'POST') {
        if(options.contentType.indexOf("application/x-www-form-urlencoded") > -1) {
            if (typeof originalOptions.data === 'string') {
                if (originalOptions.data.length) {
                    originalOptions.data += "&";
                }

                originalOptions.data += `CSRFToken=${csrfToken}`;
                options.data = originalOptions.data;
            } else {
                options.data = $.param(($.extend(originalOptions.data, { "CSRFToken": csrfToken })), true);
            }
        }

        if (options.contentType.indexOf("application/json") > -1) {
            if (typeof originalOptions.data === 'string') {
                originalOptions.data = JSON.parse(originalOptions.data);
            }
            options.data = JSON.stringify($.extend(originalOptions.data, { "CSRFToken": csrfToken}));
        }
    }
});