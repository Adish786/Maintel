window.PatternLibrary = window.PatternLibrary || {};

window.PatternLibrary.EditForm = (function(utils) {
    // TODO: With ES6 Replace with class notation
    function EditableComponent(config) {
        this.id = config.id;
        this.iframe = config.iframe;
        this.form = config.form;
        this.xml = config.xml;

        if (this.form) {
            this.form.addEventListener('submit', (event) => {
                event.preventDefault();
                this.updateXML();
            });
        }
    }

    EditableComponent.prototype.writeToIframe = function(message) {
        const plIframe = this.iframe.contentWindow.document;

        plIframe.open();
        plIframe.write(message);
        plIframe.close();
    };

    EditableComponent.prototype.updateXML = function() {
        const errorMsg = 'Error submitting Ajax request for Pattern Library Model update';
        const formData = new FormData(this.form);
        let url = `//${window.location.hostname}${(window.location.port ? `:${window.location.port}` : '')}`
            + `/edit-pattern-library-component${(window.location.search ? `${window.location.search}&` : '?')}`
            + `globeNoTest&id=${encodeURIComponent(this.id)}&xml=`;

        formData.forEach((value, key) => {
            if (value) {
                this.xml.getElementById(key).setAttribute('value', value);
            }
        });

        url += encodeURIComponent((new XMLSerializer()).serializeToString(this.xml));

        // GLBE-9646 sets timeout at 5 seconds for extremely slow connection for internal tool 
        utils.ajaxPromiseGetCall(url, errorMsg, 5000)
            .then((data) => {
                this.writeToIframe(data);
            })
            .catch((error) => {
                this.writeToIframe(error);
            });
    };

    function init(config) {
        const XMLParser = new DOMParser();
        const editableComponentConfig = {
            id: config.id,
            iframe: document.getElementById(`iframe-${config.id}`),
            form: document.getElementById(`pl-edit-${config.id}`),
            xml: XMLParser.parseFromString(config.xml, 'text/xml')
        };

        new EditableComponent(editableComponentConfig); // eslint-disable-line no-new
    }

    return { init };
}(window.Mntl.utilities || {}));
