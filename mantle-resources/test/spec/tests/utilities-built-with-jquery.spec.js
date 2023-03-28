/* eslint no-unused-expressions: "off" */
describe('Mntl.utilities (jquery dependent methods)', () => {
    // isElementVisibleY
    describe('isElementVisibleY method', () => {
        it('Should return boolean true when an element is in the viewport', (done) => {
            const el = $('<div />').css({
                width: 100,
                height: 100
            });

            $(document.body).append(el);
            window.innerHeight = 768; // sometimes reports incorrect inner height without this line

            expect(Mntl.utilities.isElementVisibleY(el)).to.equal(true);
            expect(Mntl.utilities.isElementVisibleY(el, null)).to.equal(true);
            expect(Mntl.utilities.isElementVisibleY(el, 0.5)).to.equal(true);
            expect(Mntl.utilities.isElementVisibleY(el, '')).to.equal(true);
            expect(Mntl.utilities.isElementVisibleY(el, 'a')).to.equal(true); // testing type coercion in visiblePctY comparator
            expect(Mntl.utilities.isElementVisibleY(el, null, 30)).to.equal(true); // ~78% in viewport
            expect(Mntl.utilities.isElementVisibleY(el, 0.5, 30)).to.equal(true);
            expect(Mntl.utilities.isElementVisibleY(el, 0.2, 30)).to.equal(true);
            expect(Mntl.utilities.isElementVisibleY(el, 0.8, 30)).to.equal(false);
            done();
        });

        it('Should return boolean false when an element is not in the viewport', (done) => {
            const el = $('<div />').css({
                width: 100,
                height: 100,
                position: 'absolute',
                top: 10000
            });

            $(document.body).append(el);
            window.innerHeight = 768; // reports incorrect inner height without this line

            expect(Mntl.utilities.isElementVisibleY(el)).to.equal(false);
            expect(Mntl.utilities.isElementVisibleY(el, null)).to.equal(false);
            expect(Mntl.utilities.isElementVisibleY(el, 0.5)).to.equal(false);
            expect(Mntl.utilities.isElementVisibleY(el, '')).to.equal(false);
            expect(Mntl.utilities.isElementVisibleY(el, null, 30)).to.equal(false);
            expect(Mntl.utilities.isElementVisibleY(el, 0.5, 5000)).to.equal(false);
            expect(Mntl.utilities.isElementVisibleY(el, 0.2, 8000)).to.equal(false);
            expect(Mntl.utilities.isElementVisibleY(el, 0.7, 9500)).to.equal(true);
            done();
        });
    });

    // loadStyleSheet
    describe('loadStyleSheet method', () => {
        it('Should append link when Modernizr returns true', (done) => {
            const callback = sinon.spy();
            const event = new Event('load');
            const modernizr = window.Modernizr;

            window.Modernizr = {
                hasEvent() {
                    return true;
                }
            };
            Mntl.utilities.loadStyleSheet('static/fakestyle.css', callback);

            const linkTags = document.getElementsByTagName('head')[0].getElementsByTagName('link');
            const lastLinkTag = linkTags[linkTags.length - 1];

            lastLinkTag.dispatchEvent(event);

            expect(lastLinkTag.href).to.equal('http://localhost:9876/static/fakestyle.css');
            expect(lastLinkTag.rel).to.equal('stylesheet');
            expect(lastLinkTag.type).to.equal('text/css');
            expect(callback).to.have.been.called;
            window.Modernizr = modernizr;
            done();
        });

        it('Should append link when Modernizr returns false', (done) => {
            const callback = sinon.spy();
            const modernizr = window.Modernizr;

            window.Modernizr = {
                hasEvent() {
                    return false;
                }
            };

            Mntl.utilities.loadStyleSheet('static/fakestylesheet.css', callback);

            const linkTags = document.getElementsByTagName('body')[0].getElementsByTagName('link');
            const lastLinkTag = linkTags[linkTags.length - 1];

            expect(lastLinkTag.href).to.equal('http://localhost:9876/static/fakestylesheet.css');
            expect(lastLinkTag.rel).to.equal('stylesheet');
            expect(lastLinkTag.media).to.equal('only x');
            setTimeout(() => {
                expect(callback).to.have.been.called;
                expect(lastLinkTag.media).to.equal('all');
            }, 100);
            window.Modernizr = modernizr;
            done();
        });
    });

    // readyAndDeferred
    describe('readyAndDeferred method', () => {
        it('should fire the callback on jQuery doucment.ready', (done) => {
            const cb = sinon.spy();

            Mntl.utilities.readyAndDeferred(cb);

            $(() => {
                expect(cb).to.have.been.called;
                done();
            });
        });

        it('Should fire the callback on the "defer-batch-complete" event', (done) => {
            const cb = sinon.spy();

            Mntl.utilities.readyAndDeferred(cb);
            $(document).trigger('defer-batch-complete', [['a', 1]]);

            setTimeout(() => {
                expect(cb).to.have.been.calledThrice; // doc.ready, 'a', 1
                expect(cb).to.have.been.calledWith('a');
                expect(cb).to.have.been.calledWith(1);
                done();
            }, 100);
        });
    });

    // getDocumentBaseUrl
    describe('getDocumentBaseUrl method', () => {
        it('Should return base url from window.location object', (done) => {
            const expectedResult = `${location.protocol}//${location.host}${location.pathname}`;

            expect(Mntl.utilities.getDocumentBaseUrl()).to.equal(expectedResult);
            done();
        });

        it('Should return base url from the "og:url" meta tag', (done) => {
            const expectedResult = 'https://www.verywell.com/';
            const meta = document.createElement('meta');

            meta.content = expectedResult;
            meta.setAttribute('property', 'og:url');
            document.getElementsByTagName('head')[0].appendChild(meta);

            expect(Mntl.utilities.getDocumentBaseUrl()).to.equal(expectedResult);
            done();
        });
    });

    // getDocumentSocialTitle
    describe('getDocumentSocialTitle method', () => {
        it('Should return an empty string if itemprop=name is not found', (done) => {
            expect(Mntl.utilities.getDocumentSocialTitle()).to.equal('');
            done();
        });

        it('Should return content from meta[itemprop=name]', (done) => {
            const expectedResult = 'Very Well';
            const meta = document.createElement('meta');

            meta.content = expectedResult;
            meta.setAttribute('itemprop', 'name');
            document.getElementsByTagName('head')[0].appendChild(meta);

            expect(Mntl.utilities.getDocumentSocialTitle()).to.equal(expectedResult);
            done();
        });
    });

    // getDocumentMetaDescription
    describe('getDocumentMetaDescription method', () => {
        it('Should return an empty string if meta[name=description] is not found', (done) => {
            expect(Mntl.utilities.getDocumentMetaDescription()).to.equal('');
            done();
        });

        it('Should return content from meta[name=description]', (done) => {
            const expectedResult = 'This is a description';
            const meta = document.createElement('meta');

            meta.content = expectedResult;
            meta.setAttribute('name', 'description');
            document.getElementsByTagName('head')[0].appendChild(meta);

            expect(Mntl.utilities.getDocumentMetaDescription()).to.equal(expectedResult);
            done();
        });
    });
}); // -- close
