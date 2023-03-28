window.Mntl = window.Mntl || {};

Mntl.Newsletter = (function (utils) {
    function init() {
        const forms = document.querySelectorAll(".mntl-newsletter-signup");
        const successClass = "is-submit-success";
        const errorClass = "is-submit-error";

        function onError(form) {
            form.classList.add(errorClass);
            form.classList.remove(successClass);

            form.dispatchEvent(new Event("submit-error"));
            form.dispatchEvent(new Event("submit-newsletter-error"));
        }

        function onSuccess(form) {
            form.classList.add(successClass);
            form.classList.remove(errorClass);

            form.dispatchEvent(new Event("submit-success"));
            form.dispatchEvent(new Event("submit-newsletter-success"));
        }

        function jsonToQueryString(json) {
            return Object.keys(json)
                .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(json[key])}`)
                .join("&");
        }

        function setHidCookie(hashId) {
            const now = new Date();
            const expirationDate = new Date(now);

            expirationDate.setDate(now.getDate() + 365);
            window.docCookies.setItem('hid', hashId, expirationDate, '/', utils.getDomain());
        }

        if (forms.length) {
            forms.forEach((form) => {
                form.addEventListener("submit", (e) => {
                    const submit = form.querySelector(".mntl-newsletter-submit");
                    const sharedServices = submit.dataset.sharedServices === "true";
                    const formData = new FormData(e.target);

                    e.preventDefault();

                    if (sharedServices) {
                        const objectorObjectIdsInput = form.querySelector('[name="objectorObjectIds"]');
                        let objectorObjectIds = null;

                        if (objectorObjectIdsInput) {
                            objectorObjectIds = objectorObjectIdsInput.value;
                        }

                        const { 
                            regSourceId,
                            newsletterObjectIds
                        } = submit.dataset;

                        if (!regSourceId || !newsletterObjectIds || !objectorObjectIds) {
                            onError(form);

                            return;
                        }

                        let query = {
                            modelId: 'accountRegistration', 
                            regSourceId,
                            newsletterObjectIds,
                            objectorObjectIds
                        };

                        formData.forEach((value, key) => (query[key] = value));
                        query = jsonToQueryString(query);

                        utils
                            .ajaxPromiseGetCall(`/servemodel/model.json?${query}`, "error", 100)
                            .then((response) => {
                                try {
                                    const result = JSON.parse(response === '' ? null : response);

                                    if (result && result.emailReputation === "DO_NOT_EMAIL") {
                                        onError(form);
                                    } else if (result && result.emailReputation === "SAFE_TO_EMAIL") {
                                        onSuccess(form);

                                        if (result.hashId) {
                                            setHidCookie(result.hashId);
                                        }
                                    } else {
                                        onError(form);
                                    }
                                } catch (error) {
                                    onError(form);
                                }
                            })
                            .catch(() => {
                                onError(form);
                            });
                    } else {
                        utils
                            .ajaxPromisePost('/newsletter/signup', formData, "error", 100)
                            .then(() => {
                                onSuccess(form);
                            })
                            .catch((error) => {
                                onError(form, error);
                            });
                    }
                });
            });
        }
    }

    return { init };
})(window.Mntl.utilities || {});

Mntl.utilities.readyAndDeferred(Mntl.Newsletter.init);
