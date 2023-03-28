/* eslint no-unused-expressions: "off" */
describe('Mntl.utilities', () => {
    // getDomain
    describe('getDomain method', () => {
        it('Should return domain and extension, no subdomain', (done) => {
            expect(Mntl.utilities.getDomain()).to.equal('localhost');
            done();
        });
    });

    // getQueryParams
    describe('getQueryParams method', () => {
        it('Should return an object', (done) => {
            const expectedResult = {
                name: 'Fred',
                animal: 'Fox'
            };
            const result2 = Mntl.utilities.getQueryParams('?name=Fred&animal=Fox');
            const result3 = Mntl.utilities.getQueryParams('?name=Fred&animal=Fox&animal=Lion');

            expect(typeof(result2)).to.equal('object');
            expect(result2).to.be.deep.equal(expectedResult);
            expect(result3).to.be.deep.equal(expectedResult);
            done();
        });
    });

    // getW
    describe('getW method', () => {
        beforeEach(() => {
            window.breakpoints = {
                small: {
                    arg: 'max-width',
                    width: '29em',
                    adventureNavItems: 0
                },
                med: {
                    arg: 'min-width',
                    width: '45em',
                    adventureNavItems: 3
                },
                large: {
                    arg: 'min-width',
                    width: '65em',
                    adventureNavItems: 4
                },
                xl: {
                    arg: 'min-width',
                    width: '78em',
                    adventureNavItems: 5
                }
            };
        });

        it('Should return the key of the matching breakpoint', (done) => {
            window.matchMedia = sinon.spy((...args) => {
                if (args[0] === '(min-width: 65em)') {
                    return { matches: true };
                }

                return false;
            });

            expect(Mntl.utilities.getW()).to.equal('large');
            done();
        });

        it('Should return false if no matches are found', (done) => {
            window.matchMedia = sinon.spy(() => false);
            expect(Mntl.utilities.getW()).to.equal(false);
            done();
        });
    });

    // getStaticPath
    describe('getStaticPath method', () => {
        it('Should return the value of "data-resource-version" attribute prepended with "/static/"', () => {
            window.document.documentElement.setAttribute('data-resource-version', '1.0');

            const result = Mntl.utilities.getStaticPath();

            expect(result).to.be.equal('/static/1.0');
        });
    });

    // isCurrentDomain
    describe('isCurrentDomain method', () => {
        it('Should return boolean false if the supplied domain does not match the current domain', (done) => {
            expect(Mntl.utilities.isCurrentDomain('https://www.verywell.com')).to.equal(false);
            done();
        });

        it('Should return boolean true if the supplied domain does match the current domain', (done) => {
            expect(Mntl.utilities.isCurrentDomain(window.location.hostname)).to.equal(true);
            done();
        });
    });

    // loadExternalJS
    describe('loadExternalJS method', () => {
        it('Should add a script tag to the head of the document', (done) => {
            const args = { // eslint-disable-line quote-props
                src: '/static/fakescript.js',
                class: 'test-class',
                id: 'test-id',
                async: 'true',
                'data-arbitrary': 'someval'
            };
            const callback = sinon.spy();
            const event = new Event('done');

            Mntl.utilities.loadExternalJS(args, callback);

            const scriptTags = document.getElementsByTagName('head')[0].getElementsByTagName('script');
            const lastScriptTag = scriptTags[scriptTags.length - 1];

            lastScriptTag.addEventListener('done', (e) => {
                e.target.onload();
            }, false);

            expect(lastScriptTag.src).to.equal('http://localhost:9876/static/fakescript.js');
            expect(lastScriptTag['class']).to.equal(args.className);
            expect(lastScriptTag.id).to.equal(args.id);
            expect(lastScriptTag.async).to.equal(true);
            expect(lastScriptTag.attributes['data-arbitrary'].value).to.equal(args['data-arbitrary']);
            lastScriptTag.dispatchEvent(event); // 1
            expect(callback).to.have.been.called;
            done();
        });
    });

    // onFontLoad
    describe('onFontLoad method', () => {
        beforeEach(() => {
            sinon.spy(window.debug, 'log');
        });

        afterEach(() => {
            window.debug.log.restore();
        });

        describe.skip('Should call the supplied callback if document.fonts does not exist', () => {
            // Cannot run this test for legacy browsers headless chrome always defines document.fonts.
            // document.fonts appears to be protected when deleting it and cannot be undefined.
            // Need to manually check on IE11 or Edge as of Nov 2019
        });

        it('Should call the supplied callback after a delay if document.fonts exists with a valid status code', (done) => {
            let check = false;

            function cb() {
                check = true;
            }

            document.fonts = { status: 'loaded' };
            Mntl.utilities.onFontLoad(cb);

            setTimeout(() => {
                expect(check).to.equal(true);
                done();
            }, 300);
        });

        it('Should call the supplied callback after a delay if document.fonts exists with a valid status code', (done) => {
            const cb = sinon.spy();
            const current = document.fonts;

            document.fonts = { status: 'loaded' };
            Mntl.utilities.onFontLoad(cb);

            setTimeout(() => {
                expect(cb).to.have.been.called; // after 200 ms
                document.fonts = current;
                done();
            }, 200);
        });
    });

    // onLoad
    describe('onLoad method', () => {
        it('Should fire the callback when document.readyState === complete', (done) => {
            const callBack = sinon.stub();

            Mntl.utilities.onLoad(callBack);

            expect(callBack).to.have.been.called;
            done();
        });

        it.skip('Should register a callBack to the load event when document.readyState !== complete', (done) => {
            // need to find a way to set the document.readyState !== complete
            done();
        });
    });

    // openWindow
    describe('openWindow method', () => {
        it('Should return a new window with expected properties', (done) => {
            const popup = Mntl.utilities.openWindow('https://www.verywell.com/');

            expect(popup.closed).to.equal(false);
            expect(popup.length).to.be.equal(0);
            expect(popup.location.href).to.be.equal('about:blank');
            done();
        });
    });

    // pushToDataLayer
    describe('pushToDataLayer', () => {
        const result = Mntl.utilities.pushToDataLayer({
            constant1: 'const1',
            constant2: 'const2',
            constant3: 'const3'
        });

        beforeEach(() => {
            window.dataLayer = [];
        });

        afterEach(() => {
            delete window.dataLayer;
        });

        it('Should return a function', () => {
            expect(typeof(result)).to.equal('function');
        });

        it('Last item in dataLayer should contain constants and variables', () => {
            result({
                variable1: 'var1',
                variable2: 'var2'
            });

            expect(window.dataLayer[window.dataLayer.length - 1]).to.deep.equal({
                constant1: 'const1',
                constant2: 'const2',
                constant3: 'const3',
                variable1: 'var1',
                variable2: 'var2'
            });
        });
    });

    // resourceVersion
    describe('resourceVersion method', () => {
        it('Should return the value of "data-resource-version" attribute on the html tag', (done) => {
            window.document.documentElement.setAttribute('data-resource-version', '1.0');
            window.document.documentElement.setAttribute('data-other-resource-version', '2.0');

            const result1 = Mntl.utilities.resourceVersion();
            const result2 = Mntl.utilities.resourceVersion('other');

            expect(result1).to.be.equal('1.0');
            expect(result2).to.be.equal('2.0');
            done();
        });

        it('Should return null if no attribute is found', (done) => {
            const result = Mntl.utilities.resourceVersion('not');

            expect(result).to.equal(null);
            done();
        });
    });

    // scriptOnLoad
    describe('scriptOnLoad method', () => {
        before(() => {
            sinon.spy(window.debug, 'error');
        });

        after(() => {
            window.debug.error.restore();
        });

        it('Should return false if script and callback are not both supplied', (done) => {
            expect(Mntl.utilities.scriptOnLoad()).to.equal(false);
            expect(window.debug.error).to.have.been.called;
            done();
        });

        it('Should add "onload" to script if script.readyState is falsy', (done) => {
            const callBack = sinon.spy();
            const event = new Event('done');
            const script = document.createElement('script');

            script.src = 'https://code.jquery.com/jquery-3.2.1.js';
            script.addEventListener('done', (e) => {
                e.target.onload();
            }, false);

            Mntl.utilities.scriptOnLoad(script, callBack);
            expect(typeof(script.onload)).to.equal('function');

            script.dispatchEvent(event); // 1
            expect(callBack).to.have.been.called;
            done();
        });

        describe('script.readyState', () => {
            let callBack;
            let event;
            let script;

            beforeEach(() => {
                callBack = sinon.spy();
                event = new Event('done');
                script = document.createElement('script');
                script.addEventListener('done', (e) => {
                    e.target.onreadystatechange();
                }, false);
            });

            it('Should add "onreadystatechange" to script if script.readyState is truthy', (done) => {
                script.readyState = 'scriptUnavailable';
                Mntl.utilities.scriptOnLoad(script, callBack);
                expect(typeof(script.onreadystatechange)).to.equal('function');
                script.dispatchEvent(event); // 1
                expect(callBack).not.to.have.been.called;
                done();
            });

            it('Should run the call back when script.readyState = "scriptAvailable"', (done) => {
                script.readyState = 'scriptAvailable';
                Mntl.utilities.scriptOnLoad(script, callBack);
                script.dispatchEvent(event); // 1
                expect(script.onreadystatechange).to.equal(null);
                expect(callBack).to.have.been.called;
                done();
            });

            it('Should run the call back when script.readyState = "complete"', (done) => {
                script.readyState = 'complete';
                Mntl.utilities.scriptOnLoad(script, callBack);
                script.dispatchEvent(event); // 1
                expect(script.onreadystatechange).to.equal(null);
                expect(callBack).to.have.been.called;
                done();
            });

            it('Should run the call back when script.readyState = "loaded"', (done) => {
                script.readyState = 'loaded';
                Mntl.utilities.scriptOnLoad(script, callBack);
                script.dispatchEvent(event); // 1
                expect(script.onreadystatechange).to.equal(null);
                expect(callBack).to.have.been.called;
                done();
            });
        });
    });

    describe('scriptsOnLoad method', () => {
        it('Should call the callback when all the scripts have loaded', (done) => {
            const callBack = sinon.spy();
            const event = new Event('done');
            const scripts = [...Array(3)].map(() => {
                const script = document.createElement('script');

                script.src = 'https://code.jquery.com/jquery-3.2.1.js';
                script.addEventListener('done', (e) => {
                    e.target.onload();
                }, false);

                return script;
            });

            Mntl.utilities.scriptsOnLoad(scripts, callBack, [42, 1337]);

            scripts.forEach((script) => {
                expect(typeof(script.onload)).to.equal('function');
                expect(callBack).not.to.have.been.called;
                script.dispatchEvent(event); // 1
            });
            expect(callBack).to.have.been.calledOnce;
            expect(callBack).to.have.been.calledWith(42, 1337);
            done();
        });
    });

    // getClosestMatchingParent
    describe('getClosestMatchingParent method', () => {
        const fixtureFile = 'getMatchingParent.html';

        before(() => {
            fixture.setBase('mantle-resources/test/spec/fixtures');
        });

        beforeEach(() => {
            fixture.load(fixtureFile);
        });

        it('Should return null if the parent element does not exist with given selector', () => {
            const [startingElement] = document.getElementsByClassName('delicious');
            const nonMatchingSelector = '.terrible';
            const result = Mntl.utilities.getClosestMatchingParent(startingElement, nonMatchingSelector);

            expect(result).equal(null);
        });

        it('Should return a parent element if the element exists', () => {
            const [startingElement] = document.getElementsByClassName('delicious');
            const matchingSelector1 = '.is';
            const matchingSelector2 = '.apple';
            const result1 = Mntl.utilities.getClosestMatchingParent(startingElement, matchingSelector1);
            const result2 = Mntl.utilities.getClosestMatchingParent(startingElement, matchingSelector2);
            const result3 = Mntl.utilities.getClosestMatchingParent(startingElement, 'div'); // should return .is not .delicious

            expect([...result1.classList][0]).to.be.equal('is');
            expect([...result2.classList][0]).to.be.equal('apple');
            expect([...result3.classList][0]).to.be.equal('is');
        });
    });
});
