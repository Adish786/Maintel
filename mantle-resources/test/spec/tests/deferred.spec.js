/* eslint no-unused-expressions: "off" */
describe('Mntl.Deferred', () => {
    beforeEach((done) => {
        window.Mntl = window.Mntl || {};
        window.Mntl.csrf = () => 'mockCSRFToken';

        Mntl.utilities.loadExternalJS(
            { src: 'base/mantle-resources/src/main/resources/components/internal/defer/js/deferred.js' },
            done
        );
    });

    describe('.init', () => {
        const $container = $('<div />')
            .append('<div data-defer="scroll" />')
            .append('<div data-defer="load" />')
            .append('<div data-defer="other" />');

        beforeEach(() => {
            sinon.spy($.fn, 'find');
            sinon.spy($.fn, 'on');
            sinon.spy(Mntl.Deferred, 'addToBatch');
        });

        afterEach(() => {
            Mntl.Deferred.addToBatch.restore();
            $.fn.find.restore();
            $.fn.on.restore();
        });

        // TODO: Once deferred js is refactored update this test per console.log note
        it('Should start defer', () => {
            Mntl.Deferred.init($container);

            const addToBatchArgs = Mntl.Deferred.addToBatch.getCall(0).args[0];

            expect(typeof (Mntl.Deferred.config.windowHeight)).to.equal('number');
            expect($.fn.find).to.have.been.calledOnce;
            expect($.fn.on).to.have.been.calledOnce;
            expect(Mntl.Deferred.addToBatch).to.have.been.called;
            console.log('Unexpected response log message expected here as this test is running addToBatch. Deferred JS should be refactored here due to a code smell that the add to batch functionality is doing too much');
            expect(addToBatchArgs[0][0].getAttribute('data-defer')).to.equal('scroll');
            expect(addToBatchArgs[1][0].getAttribute('data-defer')).to.equal('load');
        });
    });

    describe('.RequestContext', () => {
        it('Should be an object with default properties', () => {
            expect(new Mntl.Deferred.RequestContext()).to.contain.all.keys({
                componentIds: {},
                forceCisComponents: [],
                urlOverload: null,
                skipGenerify: false,
                htmlCallback: null,
                isPageview: false
            });
        });

        it('Should be an object with passed properties', () => {
            const rc = new Mntl.Deferred.RequestContext({}, 'doIt!', true, () => {}, true); // eslint-disable-line no-empty-function

            expect(typeof (rc.componentIds)).to.equal('object');
            expect(rc.urlOverload).to.equal('doIt!');
            expect(rc.skipGenerify).to.equal(true);
            expect(typeof (rc.htmlCallback)).to.equal('function');
            expect(rc.isPageview).to.equal(true);
        });

        it('Should set a componentId array to a map of null values', () => {
            expect(new Mntl.Deferred.RequestContext(['a', 'b', 'c']).componentIds).to.deep.equal({
                a: null,
                b: null,
                c: null
            });
        });

        it('Should set a componentId string to a map of null values', () => {
            expect(new Mntl.Deferred.RequestContext('a,b,c').componentIds).to.deep.equal({
                a: null,
                b: null,
                c: null
            });

            expect(new Mntl.Deferred.RequestContext('x, y, z').componentIds).to.deep.equal({
                x: null,
                y: null,
                z: null
            });
        });

        it('Should set a component object to an object of jQuery elements', () => {
            expect(new Mntl.Deferred.RequestContext({
                a: 'someId',
                b: 'someotherId'
            }).componentIds).to.deep.equal({
                a: $(),
                b: $()
            });
        });
    });

    describe('.requestComponents', () => {
        const context = {
            componentIds: {
                idOne: 'el',
                idTwo: 'el'
            },
            forceCisComponents: [],
            isPageView: true
        };
        const deferParams = {};

        beforeEach(() => {
            sinon.spy(Mntl.utilities, 'ajaxPromisePost')
            sinon.spy(Mntl.fnUtilities, 'iterate');
        });

        afterEach(() => {
            Mntl.utilities.ajaxPromisePost.restore();
            Mntl.fnUtilities.iterate.restore();
        });

         // TODO: Once deferred js is refactored update this test per console.log note
        it('Should make an ajax request', () => {
            Mntl.Deferred.requestComponents(context, deferParams);
            console.log('Unexpected response log message expected here as this test is executing a ajaxPromisePost request. Some refactor needed in deferred.js to make this more testable in chunks.');

            const ajaxArgs = Mntl.utilities.ajaxPromisePost.getCall(0).args[1];
            const ajaxTraditionalDataArgs = Mntl.utilities.ajaxPromisePost.getCall(0).args[3];

            expect(Mntl.fnUtilities.iterate).to.have.been.called;
            expect(Mntl.utilities.ajaxPromisePost).to.have.been.called;
            expect(ajaxArgs.globeDeferVersion).to.equal('2');
            expect(ajaxArgs.cr).to.equal('idOne,idTwo');
            expect(ajaxArgs.loaded_cr).to.equal('');
            expect(ajaxArgs.cis).to.equal(1);
            expect(ajaxArgs.force_cis).to.equal('');
            expect(ajaxArgs.pv).to.equal(true);
            expect(ajaxTraditionalDataArgs.loaded_css).to.be.an('array');
            expect(ajaxTraditionalDataArgs.loaded_js).to.be.an('array');
        });
    });
    
    describe('batching logic', () => {
        describe('getUnsuffixedId', () => {
            it('should getUnsuffixedId from a full suffixed id', () => {
                const fullId = 'component-1_0-1';
                const unsuffexedId = 'component-1_0';

                expect(Mntl.Deferred.getUnsuffixedId(fullId)).to.equal(unsuffexedId);
            });
        })

        // can't test positive case in this context because of internal switch
        describe('.addToBatch', () => {
            beforeEach(() => {
                sinon.spy(window.debug, 'log');
                sinon.spy(window, 'setTimeout');
            });

            afterEach(() => {
                window.debug.log.restore();
                window.setTimeout.restore();
            });

             // TODO: Once deferred js is refactored update this test per console.log note
            it('Should reveal small signs of doing anything', () => {
                Mntl.Deferred.addToBatch($('<div />'));
                console.log('Unexpected response log message expected here as this test is running addToBatch. Deferred JS should be refactored here due to a code smell that the add to batch functionality is doing too much');
                expect(window.debug.log).to.have.been.called;
                expect(window.setTimeout).to.have.been.called;
            });
        });
    });

    // Loops over and modifies hidden var
    describe.skip('.setBtfVals', () => {}); // eslint-disable-line no-empty-function

    describe('deferType scroll utility functions', () => {
        describe('calculateViewportBottom', () => {
            it('Should calculate the ViewportBottom with a default offset of 200', () => {
                const expectedResult = 800; // scrollTop = 0, windowHeight = 600, default offset = 200

                expect(Mntl.Deferred.calculateViewportBottom()).to.equal(expectedResult);
            });

            it('Should calculate the ViewportBottom with a passed value of 300', () => {
                const expectedResult = 900; // scrollTop = 0, windowHeight = 600, default offset = 300

                expect(Mntl.Deferred.calculateViewportBottom(300)).to.equal(expectedResult);
            })
        })
    })

    describe('.removeEventListener', () => { // Deprecated
        beforeEach(() => {
            sinon.spy($.fn, 'off');
        });

        afterEach(() => {
            $.fn.off.restore();
        });

        it('Should fire the jQuery off method', () => {
            Mntl.Deferred.removeEventListener('#an-id', 'click', '.a-links', () => {}); // eslint-disable-line no-empty-function

            expect($.fn.off).to.have.been.called;
        });
    });

    
});
