/* eslint no-empty-function:"off", no-unused-expressions: "off" */
describe('Mntl.Pageview', () => {
    const domain = `${window.location.href.substring(window.location.href.indexOf('.'), window.location.href.indexOf('.com'))}.com`;

    function load(done) {
        Mntl.utilities.loadExternalJS(
            { src: 'base/mantle-resources/src/main/resources/static/js/page-view.js' },
            done
        );
    }

    before((done) => {
        window.docCookies = {
            removeItem: sinon.spy(() => {}),
            setItem: sinon.spy(() => {})
        };

        done();
    });

    beforeEach((done) => {
        window.dataLayer = [];
        load(done);
    });

    after((done) => {
        window.dataLayer = [];
        window.docCookies = null;
        done();
    });

    // race condition
    it.skip('Should make a call to setItem on load', (done) => {
        setTimeout(() => {
            expect(window.docCookies.setItem).to.have.been.called;
            expect(window.docCookies.setItem).to.have.been.calledWith('pageEntryType', '/', domain);
            done();
        }, 100);
    });

    // init
    describe('Mntl.PageView.init', () => {
        it('Should push an object onto dataLayer', () => {
            Mntl.PageView.init({testVal: null});
            expect(window.dataLayer.length).to.equal(1);
            expect(window.dataLayer[0]).to.deep.equal({
                event: 'unifiedPageview',
                testVal: null
            });
        });

        it('Should return "undefined" when _initialized is truthy', () => {
            Mntl.PageView.init({testVal: null});
            expect(Mntl.PageView.init()).to.equal(false);
        });
    });

    // setEntryType
    describe('Mntl.PageView.setEntryType', () => {
        it('Should call docCookies.setItem with arguments', () => {
            Mntl.PageView.setEntryType('entryType');
            expect(window.docCookies.setItem).have.been.called;
            expect(window.docCookies.setItem).to.have.been.calledWith('pageEntryType', 'entryType', null, '/', domain);
        });
    });

    // pushToDataLayer
    describe('Mntl.PageView.pushToDataLayer', () => {
        it('Should return "undefined" when called before init', () => {
            expect(Mntl.PageView.pushToDataLayer()).to.equal(false);
        });

        it('Should return "undefined" when called before init', () => {
            Mntl.PageView.init({testInit: null});
            Mntl.PageView.pushToDataLayer({
                testInit: true,
                testPushToDl: null
            });

            expect(window.dataLayer.length).to.equal(2);
            expect(window.dataLayer[1]).to.deep.equal({
                event: 'unifiedPageview',
                testInit: true
            });
        });
    });
});
