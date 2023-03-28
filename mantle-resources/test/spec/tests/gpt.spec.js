/* eslint no-undefined:"off", no-unused-vars:"off", no-empty-function:"off", no-unused-expressions: "off" */
(function() {
    const domain = window.location.hostname;

    class Googletag {
        constructor () {
            this.cmd = [];
            this.display = sinon.spy();
            this.destroySlots = sinon.spy();
            this.enableServices = sinon.spy();
            this.pubadsStub = sinon.stub({
                setTargeting(key, value) {},
                collapseEmptyDivs(collapse) {},
                addEventListener(event, callback) {},
                enableAsyncRendering() {},
                disableInitialLoad() {},
                enableSingleRequest() {},
                updateCorrelator() {},
                setPrivacySettings() {},
                refresh() {},
                clear() {}
            });
            this.gptSlotStub = sinon.stub({
                setTargeting(key, value) {},
                clearTargeting() {},
                addService(service) {},
                getSizes() {},
                defineSizeMapping() {},
                getTargetingKeys() {}
            });
            this.defineSlot = sinon.spy((url, sizes, id) => this.gptSlotStub);
            this.addSize = sinon.spy((viewport, sizes) => {
                this._addSizes = [viewport].concat(sizes);

                return this;
            });
            this.sizeMapping = sinon.spy(() => this);
            this.build = sinon.spy(() => this._addSizes);
        }
        pubads() {
            return this.pubadsStub;
        }
        companionAds() {
            sinon.spy();
        }
    }

    function load(done) {
        Mntl.utilities.loadExternalJS(
            { src: '/base/mantle-resources/src/main/resources/js/GPT.js' },
            done
        );
    }

    before((done) => {
        fixture.setBase('mantle-resources/test/spec/fixtures');
        done();
    });

    // TODO: Enable and fix in GLBE-8678
    describe('Mntl.GPT', () => {
        const slots = [{ 
            name: 'slot1', 
            gptSlot: 'slot1', 
            config: {id: '1'}, 
            targeting: {priority: 2}
        },{ 
            name: 'slot2', 
            gptSlot: 'slot2', 
            config: {id: '2'}, 
            targeting: {priority: 1}
        },{ 
            name: 'slot3', 
            gptSlot: 'slot3', 
            config: {id: '3'}, 
            targeting: {priority: 12}
        },{ 
            name: 'slot4', 
            gptSlot: 'slot4', 
            config: {id: '4'}, 
            targeting: {priority: 11}
        }];
        
        before((done) => {
            delete window.Mntl.RTB; // Mntl.RTB is defined in bc-player-rtb.spec.js

            window.docCookies = { getItem: sinon.spy(() => 'pcCookie') };
            done();
        });

        beforeEach((done) => {
            window.googletag = new Googletag();
            load(done);
        });

        afterEach((done) => {
            window.googletag = null;
            done();
        });

        after((done) => {
            window.docCookies = null;
            done();
        });

        // init()
        describe('init()', () => {
            it('Extends default configs', () => {
                const extendedConfigs = { baseSlotTargeting: {type: 'testType'} };
                const deepExtendSpy = sinon.spy(Mntl.fnUtilities, 'deepExtend');

                Mntl.GPT.init(extendedConfigs);
                expect(deepExtendSpy).to.have.been.calledWith(sinon.match.object, extendedConfigs);
            });

            it('Pushes commands to googletag.cmd and executes all of the googletag methods as expected', () => {
                Mntl.GPT.init({ 
                    singleRequest: false, 
                    baseSlotTargeting: {type: 'testType'} 
                });
                googletag.cmd.forEach((fn) => {
                    fn();
                });

                expect(googletag.cmd.length).to.equal(2);
                expect(googletag.pubadsStub.setTargeting).to.have.been.called;
                expect(googletag.pubadsStub.collapseEmptyDivs).to.have.been.calledWith(false);
                expect(googletag.pubadsStub.addEventListener).to.have.been.calledWith('slotRenderEnded', sinon.match.func);
                expect(googletag.pubadsStub.enableAsyncRendering).to.have.been.called;
                expect(googletag.pubadsStub.disableInitialLoad).to.have.been.called;
                expect(googletag.pubadsStub.enableSingleRequest).to.not.have.been.called;
                expect(googletag.enableServices).to.have.been.called;
            });

            it('Should set category on page targeting without spaces', () => {
                const configWithTax1 = {
                    baseSlotTargeting: { 
                        type: 'testType', 
                        foo: 'bar',
                        dc_ref: 'baz', // eslint-disable-line camelcase
                        wdth: '300',
                        tax1: 'test tax1'
                    }
                };

                Mntl.GPT.init(configWithTax1);
                expect(Mntl.GPT.getPageTargeting().category).to.equal('testtax1');
            });

            it('Should set category on page targeting with the last value when split by an underscore', () => {
                const configWithTax1 = {
                    baseSlotTargeting: { 
                        type: 'testType', 
                        foo: 'bar',
                        dc_ref: 'baz', // eslint-disable-line camelcase
                        wdth: '300',
                        tax1: 'test_one_two'
                    }
                };

                Mntl.GPT.init(configWithTax1);
                expect(Mntl.GPT.getPageTargeting().category).to.equal('two');
            });

            it('Should set category on page targeting with default when there is no tax1 and not on homepage', () => {
                const configWithNoTax1 = {
                    baseSlotTargeting: { 
                        type: 'testType', 
                        foo: 'bar',
                        dc_ref: 'baz', // eslint-disable-line camelcase
                        wdth: '300'
                    }
                };

                Mntl.GPT.init({
                    baseSlotTargeting: { type: 'testType' },
                    configWithNoTax1
                });
                expect(Mntl.GPT.getPageTargeting().category).to.equal('');
            });
        });

        // serializeAllTargeting
        describe('serializeAllTargeting', () => {
            beforeEach(() => {
                Mntl.GPT.init({
                    pageTargeting: {
                        ugc: '1',
                        dc_ref: 'foo', // eslint-disable-line camelcase
                        hgt: '1',
                        wdth: '1',
                        kw: 'kw'
                    },
                    baseSlotTargeting: {
                        type: 'testType', 
                        foo: 'bar',
                        dc_ref: 'baz', // eslint-disable-line camelcase
                        wdth: '300',
                        tax1: 'test_tax1'
                    }
                });
            });

            it('Should serialize page and slot level targeting', () => {
                expect(Mntl.GPT.serializeAllTargeting()).to.equal(encodeURIComponent('pc=pcCookie&type=testType&foo=bar&dc_ref=foo&wdth=1&tax1=test_tax1&ugc=1&hgt=1&kw=kw&category=tax1'));
            });
        });

        // serializePageTargeting()
        describe('serializePageTargeting()', () => {
            it('Should serialize', () => {
                Mntl.GPT.init({
                    pageTargeting: {
                        ugc: '1',
                        dc_ref: 'foo', // eslint-disable-line camelcase
                        hgt: '1',
                        wdth: '1',
                        kw: 'kw'
                    },
                    baseSlotTargeting: { 
                        type: 'testType', 
                        tax1: 'test_tax1' 
                    }
                });
                expect(Mntl.GPT.serializePageTargeting()).to.equal(encodeURIComponent('ugc=1&dc_ref=foo&hgt=1&wdth=1&kw=kw&category=tax1'));
            });
        });

        // serializeBaseSlotTargeting()
        describe('serializeBaseSlotTargeting()', () => {
            it('Should serialize', () => {
                Mntl.GPT.init({
                    baseSlotTargeting: { 
                        type: 'testType',
                        foo: 'bar'
                    } 
                });
                expect(Mntl.GPT.serializeBaseSlotTargeting()).to.equal(encodeURIComponent('pc=pcCookie&type=testType&foo=bar'));
            });
        });

        // isSingleRequest()
        describe('isSingleRequest()', () => {
            it('Should should be false', () => {
                Mntl.GPT.init({
                    baseSlotTargeting: { type: 'testType' },
                    singleRequest: false
                });
                expect(Mntl.GPT.isSingleRequest()).to.equal(false);
            });

            it('Should should be true', () => {
                Mntl.GPT.init({
                    baseSlotTargeting: { type: 'testType' },
                    singleRequest: true 
                });
                expect(Mntl.GPT.isSingleRequest()).to.equal(true);
            });

            it('Should have a default value', () => {
                Mntl.GPT.init({baseSlotTargeting: { type: 'testType' }});
                expect(Mntl.GPT.isSingleRequest()).to.be.oneOf([true, false]);
            });

            afterEach(() => {
                Mntl.GPT = null;
            });
        });

        // isMobile()
        describe('isMobile()', () => {
            it('Should should be false', () => {
                Mntl.GPT.init({ 
                    baseSlotTargeting: { type: 'testType' },
                    isMobile: false
                });
                expect(Mntl.GPT.isMobile()).to.equal(false);
            });

            it('Should should be true', () => {
                Mntl.GPT.init({ 
                    baseSlotTargeting: { type: 'testType' },
                    isMobile: true
                });
                expect(Mntl.GPT.isMobile()).to.equal(true);
            });

            it('Should have a default value', () => {
                Mntl.GPT.init({baseSlotTargeting: { type: 'testType' }});
                expect(Mntl.GPT.isMobile()).to.be.oneOf([true, false]);
            });
        });

        // updateBaseSlotTargeting
        describe('updateBaseSlotTargeting', () => {
            it('Should set and delete keys', () => {
                expect(Mntl.GPT.serializeBaseSlotTargeting()).equal(encodeURIComponent('pc=pcCookie'));
                Mntl.GPT.updateBaseSlotTargeting({
                    nullCheck: 'foo',
                    blankCheck: 'bar',
                    undefinedCheck: 'baz'
                });
                expect(Mntl.GPT.serializeBaseSlotTargeting()).to.equal(encodeURIComponent('pc=pcCookie&nullCheck=foo&blankCheck=bar&undefinedCheck=baz'));
                Mntl.GPT.updateBaseSlotTargeting({
                    nullCheck: null,
                    blankCheck: '',
                    undefinedCheck: undefined,
                    pc: null
                });
                expect(Mntl.GPT.serializeBaseSlotTargeting()).to.be.empty;
            });
        });

        // registerCallback
        describe('registerCallback', () => {
            it('should be tested in Mntl.utilities.', () => {});
        });

        // Slot()
        describe('Slot()', () => {
            const CONFIG = {
                id: "id",
                type: "leaderboard",
                sizes: [[728, 90], [970, 250], [970, 90], [1, 1]]
            };
            const TARGETING = {
                pos: "atf",
                priority: 1
            };

            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                done();
            });

            afterEach(() => {
                fixture.cleanup();
            });

            it('Should instantiate via Slot()', () => {
                const spy = sinon.spy(Mntl.GPT, 'Slot');
                const slot = new Mntl.GPT.Slot();

                expect(slot.config).to.be.empty;
                expect(slot.targeting).to.be.empty;
                expect(slot.gptSlot).to.equal(null);
                expect(slot.el).to.equal(null);
                expect(slot.displayState).to.equal(null);
                expect(spy).returned(slot);
            });

            it('Should define via byConfig()', () => {
                const spy = sinon.spy(Mntl.GPT.Slot.prototype, 'byConfig');
                const slot = new Mntl.GPT.Slot().byConfig(CONFIG, TARGETING);

                expect(slot.config).to.equal(CONFIG);
                expect(slot.targeting).to.equal(TARGETING);
                expect(slot.displayState).to.equal(null);
                expect(spy).returned(slot);
            });

            it('Should define via byElement()', () => {
                const spy = sinon.spy(Mntl.GPT.Slot.prototype, 'byElement');
                const billboard = document.getElementById('billboard');
                const slot = new Mntl.GPT.Slot().byElement(billboard);

                expect(slot.config.id).to.equal('billboard');
                expect(slot.config.type).to.equal('billboard');
                expect(slot.config.sizes).to.eql([[300, 250], [300, 600], [300, 1050], [1, 2]]);
                expect(slot.targeting.pos).to.equal('atf');
                expect(slot.targeting.priority).to.equal(2);
                expect(slot.el).to.equal(billboard);
                expect(slot.displayState).to.equal(null);
                expect(spy).returned(slot);
            });

            it('Should define via byElement() with an override id', () => {
                const billboard2 = document.getElementById('billboard2');
                const slot = new Mntl.GPT.Slot().byElement(billboard2, 'override');

                expect(slot.config.id).to.equal('override');
            });

            it('Should update previously defined slot when updateBaseSlotTargeting() is called with config set', () => {
                Mntl.GPT.init({ 
                    baseSlotTargeting: { type: 'testType' },
                    utils: { buildGptUrl(config, targeting) {} }
                });                
                const slot = new Mntl.GPT.Slot().byConfig(CONFIG, TARGETING);
                
                googletag.cmd.forEach((fn) => {
                    fn();
                });           
                Mntl.GPT.updateBaseSlotTargeting({ adconsent: 'y' }, true, true);
                expect(slot.targeting.adconsent).equal('y');
            });

            it('Executes googletag push', () => {
                Mntl.GPT.init({
                    baseSlotTargeting: { 
                        type: 'testType',
                        foo: 'bar'
                    },
                    utils: { buildGptUrl(config, targeting) {} }
                });
                const slot = new Mntl.GPT.Slot().byConfig(CONFIG, TARGETING);

                googletag.cmd.forEach((fn) => {
                    fn();
                });
                expect(googletag.gptSlotStub.addService).to.have.been.called;
                expect(googletag.gptSlotStub.addService.args[0][0]).to.equal(googletag.pubadsStub);
                expect(googletag.gptSlotStub.setTargeting).to.have.been.calledWith('foo', 'bar');
                expect(googletag.gptSlotStub.setTargeting).to.have.been.calledWith('tile', '0');
                expect(googletag.gptSlotStub.setTargeting).to.have.been.calledWith('pos', 'atf');
                expect(googletag.gptSlotStub.setTargeting).to.have.been.calledWith('priority', '1');
                expect(slot.gptSlot).to.equal(googletag.gptSlotStub);
            });

            it('Supports companion ads', () => {
                Mntl.GPT.init({
                    baseSlotTargeting: { type: 'testType' },
                    pageTargeting: { companion: true },
                    utils: { buildGptUrl(config, targeting) {} }
                });
                const slot = new Mntl.GPT.Slot().byConfig(CONFIG, TARGETING);

                googletag.cmd.forEach((fn) => {
                    fn();
                });
                expect(googletag.pubadsStub.collapseEmptyDivs).to.have.been.calledTwice;
                expect(googletag.pubadsStub.collapseEmptyDivs.args[1][0]).to.equal(true);
                expect(googletag.gptSlotStub.addService).to.have.been.calledTwice;
                expect(googletag.gptSlotStub.addService.args[0][0]).to.equal(googletag.companionAds());
            });
        });

        // displaySlots()
        describe('displaySlots()', () => {
            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                Mntl.GPT.init({ 
                    baseSlotTargeting: { type: 'testType' },
                    utils: { buildGptUrl(config, targeting) {}}
                });
                done();
            });

            afterEach(() => {
                fixture.cleanup();
            });

            function expectDisplayNewSlot(slot, callback) {
                Mntl.GPT.displaySlots([slot]);
                googletag.cmd.forEach((fn) => {
                    fn();
                });
                // Allow time to find slot wrapper on page
                setTimeout(() => {
                    expect(googletag.display).to.have.been.calledOnce;
                    expect(googletag.display).to.have.been.calledWith('leaderboard');
                    // Execute refresh cmd push
                    googletag.cmd[googletag.cmd.length - 1]();
                    expect(googletag.pubadsStub.refresh).to.have.been.calledWith([slot.gptSlot]);
                    callback();
                }, 100);
            }

            it('Should escape if slots are undefined or null and no previously defined slots exist', (done) => {
                const slots = null;

                expect(Mntl.GPT.displaySlots(slots)).to.equal(undefined);
                expect(Mntl.GPT.displaySlots(undefined)).to.equal(undefined);
                done();
            });

            it('Should return undefined/escape if slots are empty', (done) => {
                const slots = [];

                expect(Mntl.GPT.displaySlots(slots)).to.equal(undefined);
                done();
            });

            it('Should display new slots', (done) => {
                const slot = new Mntl.GPT.Slot().byElement(document.querySelector('#leaderboard'));

                expectDisplayNewSlot(slot, done);
            });

            it('Should refresh previously displayed slots', (done) => {
                const slot = new Mntl.GPT.Slot().byElement(document.querySelector('#leaderboard'));

                expectDisplayNewSlot(slot, () => {
                    Mntl.GPT.displaySlots();
                    // Does not call display again
                    expect(googletag.display).to.have.been.calledOnce;
                    googletag.cmd[googletag.cmd.length - 1]();
                    // Refreshes instead
                    expect(googletag.pubadsStub.refresh).to.have.been.calledTwice;
                    done();
                });
            });

        });

        // getGptUrl(slotConfig, slotTargeting, pageTargeting, pageConfig)
        describe('getGptUrl(slotConfig, slotTargeting, pageTargeting, pageConfig)', () => {
            describe('when useOxygen is false and useLmdFormat is true', () => {
                it('Should generate the legacy Meredith GPT desktop ad unit path', () => {
                    expect(Mntl.GPT.getGptUrl({ 
                        type: 'leaderboard',
                        tier: 1
                    },{ type: 'article' }, 
                    { category: 'news' }, {
                        useLmdFormat: true,
                        useOxygen: false,
                        dfpId: '3865',
                        lmdSiteCode: 'hlt',
                        isMobile: false
                    })).equal('3865/hlt.mdp.com/tier1/article/news');
                });
                it('Should generate the legacy Meredith GPT mobile ad unit path', () => {
                    expect(Mntl.GPT.getGptUrl({ 
                        type: 'billboard',
                        tier: 1
                    }, { type: 'category' },
                    { category: 'smart-and-connected-life' }, {
                        useLmdFormat: true,
                        useOxygen: false,
                        dfpId: '3865',
                        lmdSiteCode: 'hlt',
                        isMobile: true
                    })).equal('3865/hlt.mdp.mob/tier1/category/smart-and-connected-life');
                });
                it('Should generate the legacy Meredith GPT native ad unit path', () => {
                    expect(Mntl.GPT.getGptUrl({ 
                        type: 'native',
                        tier: 1
                    }, { type: 'slideshow' },
                    { category: 'banana' }, {
                        useLmdFormat: true,
                        useOxygen: false,
                        dfpId: '3865',
                        lmdSiteCode: 'parents',
                        isMobile: false
                    })).equal('3865/parents.mdp.com/ntv1');
                });
            });

            describe('when useOxygen is true', () => {
                beforeEach((done) => {
                    sinon.stub(Mntl.utilities, 'getDomain').callsFake(() => ('testdomain.com'));
                    done();
                });

                afterEach((done) => {
                    Mntl.utilities.getDomain.restore();
                    done();
                });

                it('Should generate the oxgenized GPT desktop ad unit path', () => {
                    expect(Mntl.GPT.getGptUrl({ 
                        type: 'leaderboard',
                        tier: 1
                    },{ type: 'article' }, 
                    { category: 'news' }, {
                        useLmdFormat: false,
                        useOxygen: true,
                        dfpId: '3865',
                        isMobile: false
                    })).equal('3865/ddm.testdomain.com/tier1/article/news');
                });
                it('Should generate the oxgenized GPT mobile ad unit path', () => {
                    expect(Mntl.GPT.getGptUrl({ 
                        type: 'billboard',
                        tier: 1
                    }, { type: 'category' },
                    { category: 'smart-and-connected-life' }, {
                        useLmdFormat: false,
                        useOxygen: true,
                        dfpId: '3865',
                        isMobile: true
                    })).equal('3865/ddm.testdomain.mob/tier1/category/smart-and-connected-life');
                });
                it('Should generate the oxgenized GPT native ad unit path', () => {
                    expect(Mntl.GPT.getGptUrl({ 
                        type: 'native',
                        tier: 1
                    }, { type: 'slideshow' },
                    { category: 'banana' }, {
                        useLmdFormat: false,
                        useOxygen: true,
                        dfpId: '3865',
                        isMobile: false
                    })).equal('3865/ddm.testdomain.com/ntv1');
                });
            });

            describe('useOxygen is false and when useLmdFormat is false', () => {
                it('Should call the vertical buildGptUrl util function to get the GPT ad unit path', () => {
                    const verticalBuildGptUrl = sinon.spy(() => 'verticalGptUrl');

                    Mntl.GPT.getGptUrl({}, {}, {}, {
                        useLmdFormat: false,
                        useOxygen: false,
                        utils: { buildGptUrl: verticalBuildGptUrl }
                    });

                    expect(verticalBuildGptUrl).to.have.been.called;
                });
            });
        });

        // getSitePath()
        describe('getSitePath()', () => {
            const config = {
                baseSlotTargeting: { type: 'testType' },
                domain,
                useOxygen: false,
                lmdSiteCode: 'hlt',
                isMobile: false,
                dfpId: 3865
            }

            it('Should show desktop L-MD path value' , () => {
                Mntl.GPT.init(config);

                expect(Mntl.GPT.getSitePath()).equal('hlt.mdp.com');
            })


            it('Should show mobile L-MD path value' , () => {
                Mntl.GPT.init({
                    ...config,
                    isMobile: true
                });

                expect(Mntl.GPT.getSitePath()).equal('hlt.mdp.mob');
            });
        });

        // getPageType()
        describe('getPageType()', () => {
            const config = { 
                baseSlotTargeting: { 
                    type: 'testType',
                    type: 'article'
                }
            };

            it('Should return a page type of article', () => {
                Mntl.GPT.init({
                    ...config, 
                    isMobile: true
                });

                expect(Mntl.GPT.getPageType()).equal('article');
            });
        });

        // clearSlots()
        describe('clearSlots()', () => {
            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                Mntl.GPT.init({ 
                    baseSlotTargeting: { type: 'testType' },
                    utils: { buildGptUrl(config, targeting) {} }
                });
                done();
            });

            it('Should escape if an empty array of slots is passed', () => {
                const slots = [];

                expect(Mntl.GPT.clearSlots(slots)).to.equal(undefined);
            });

            it('Should clear previously defined gpt slots', () => {
                const slot = new Mntl.GPT.Slot().byElement(document.querySelector('#leaderboard'));

                googletag.cmd.forEach((fn) => {
                    fn();
                });
                Mntl.GPT.clearSlots([slot]);
                googletag.cmd[googletag.cmd.length - 1]();
                expect(googletag.pubadsStub.clear).to.have.been.calledWith([slot.gptSlot]);
            });
        });

        // destroySlots(slots)
        describe('destroySlots(slots)', () => {
            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                load(() => {
                    Mntl.GPT.init({ 
                        baseSlotTargeting: { type: 'testType' },
                        utils: { buildGptUrl(config, targeting) {} }
                    });
                    done();
                });
            });

            it('Should escape if empty slots are passed', () => {
                const slots = [];

                expect(Mntl.GPT.destroySlots(slots)).to.equal(undefined);
            })

            it('Should clear previously defined gpt slots', () => {
                const slot = new Mntl.GPT.Slot().byElement(document.querySelector('#leaderboard'));

                googletag.cmd.forEach((fn) => {
                    fn();
                });
                Mntl.GPT.destroySlots([slot]);
                googletag.cmd[googletag.cmd.length - 1]();
                expect(googletag.destroySlots).to.have.been.calledWith([slot.gptSlot]);
            });
        });

        // sortSlotsByPriority()
        describe('sortSlotsByPriority()', () => {
            const slots = [{ 
                name: 'slot1', 
                gptSlot: 'slot1', 
                config: {id: 1}, 
                targeting: { priority: 2 } 
            },{ 
                name: 'slot2', 
                gptSlot: 'slot2',
                config: {id: 2}, 
                targeting: { priority: 1 } 
            },{ 
                name: 'slot3', 
                gptSlot: 'slot3', 
                config: {id: 3}, 
                targeting: { priority: 12 } 
            },{ 
                name: 'slot4',
                gptSlot: 'slot4', 
                config: {id: 4}, 
                targeting: {} 
            },{ 
                name: 'slot5', 
                gptSlot: 'slot5', 
                config: {id: 5}, 
                targeting: { priority: 2 } 
            }];

            it('Should sort slots by priority', () => {
                const sortedSlots = Mntl.GPT.sortSlotsByPriority(slots);

                expect(sortedSlots[0].name).to.equal('slot2');
                expect(sortedSlots[1].name).to.equal('slot1');
                expect(sortedSlots[2].name).to.equal('slot5');
                expect(sortedSlots[3].name).to.equal('slot3');
                expect(sortedSlots[4].name).to.equal('slot4');
            });
        });

        // addRequestSizeToSlot(slotId, size)
        describe('addRequestSizeToSlot()', () => {
            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                Mntl.GPT.init({ 
                    baseSlotTargeting: { type: 'testType' },
                    utils: { buildGptUrl(config, targeting) {} }
                });
                done();
            });

            it('Should add size to gpt slot', () => {
                const slot = new Mntl.GPT.Slot().byElement(document.querySelector('#billboard2'));

                googletag.cmd.forEach((fn) => {
                    fn();
                });
                googletag.gptSlotStub.getSizes.returns([{
                    getWidth() { return 300; },
                    getHeight() { return 250; }
                }]);

                Mntl.GPT.addRequestSizeToSlot('billboard2', [300, 600]);

                expect(googletag.sizeMapping).to.have.been.called;
                expect(googletag.addSize).to.have.been.calledWith([0, 0], [[300, 250], [300, 600]]);
                expect(googletag.build).to.have.been.called;
                expect(googletag.gptSlotStub.defineSizeMapping).to.have.been.calledWith([[0, 0], [300, 250], [300, 600]]);
            });

            it('Should return undefined if we can\'t find GPT slot from given ID', () => {
                const result = Mntl.GPT.addRequestSizeToSlot();

                expect(result).to.equal(undefined);
            });
        });

        // removeRequestSizeFromSlot(slotId size)
        describe('removeRequestSizeFromSlot()', () => {
            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                Mntl.GPT.init({ 
                    baseSlotTargeting: { type: 'testType' },
                    utils: { buildGptUrl(config, targeting) {} }
                });
                done();
            });

            it('Should escape if gptSlot is undefined or empty', () => {
                expect(Mntl.GPT.removeRequestSizeFromSlot('billboard', [300, 600])).to.equal(undefined);
            });

            it('Should remove size from gpt slot', () => {
                const slot = new Mntl.GPT.Slot().byElement(document.querySelector('#billboard'));

                googletag.cmd.forEach((fn) => {
                    fn();
                });
                googletag.gptSlotStub.getSizes.returns([{
                    getWidth() { return 300; },
                    getHeight() { return 250; }
                }, {
                    getWidth() { return 300; },
                    getHeight() { return 600; }
                }]);
                Mntl.GPT.removeRequestSizeFromSlot('billboard', [300, 600]);
                expect(googletag.gptSlotStub.getSizes).to.have.been.called;
                expect(googletag.gptSlotStub.defineSizeMapping.args[0][0].length).to.equal(1);
                expect(googletag.gptSlotStub.defineSizeMapping.args[0][0][0].getHeight()).to.equal(250);
                googletag.cmd[googletag.cmd.length - 1]();
                expect(googletag.pubadsStub.refresh).to.have.been.calledWith([slot.gptSlot]);
            });
        });

        // updatePageview()
        describe('updatePageview()', () => {
            it('Should call updateCorrelator', () => {
                Mntl.GPT.updatePageview();
                googletag.cmd.forEach((fn) => {
                    fn();
                });
                expect(googletag.pubadsStub.updateCorrelator).to.have.been.called;
            });
        });

        // getGptSlotFromSlots()
        describe('getGptSlotFromSlot()', () => {
            it('Should return a gptSlot item', () => {
                Mntl.GPT.init({
                    baseSlotTargeting: { type: 'testType' },
                    initialSlots: slots
                })
                expect(Mntl.GPT.getGptSlotFromSlot(slots[0]))
                    .to.contain('slot1');
            })
        })

        // getGptSlotsFromSlots()
        describe('getGptSlotsFromSlots()', () => {
            it('Should return a slots array', () => {
                Mntl.GPT.init({
                    baseSlotTargeting: { type: 'testType' },
                    initialSlots: slots
                });
                expect(Mntl.GPT.getGptSlotsFromSlots(slots))
                    .to.be.an('array')
                    .and.to.have.lengthOf(4)
                    .and.to.contain('slot1', 'slot2', 'slot3', 'slot4');
            });
        });

        // getUtils()
        describe('getUtils()', () => {
            const config = {
                baseSlotTargeting: { type: 'testType' },
                utils: {
                    buildGptUrl() {},
                    someVal: true,
                    aString: 'test'
                }
            };

            it('Should return a utils object', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getUtils()).to.deep.equal(config.utils);
            });
        });

        // getDfpId()
        describe('getDfpId()', () => {
            const config = { 
                baseSlotTargeting: { type: 'testType' },
                dfpId: 'thisIsATestDFPID'
            };

            it('Should return a DFP ID', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getDfpId()).to.equal(config.dfpId);
            });
        });

        // getDebugPageTargeting
        describe('getDebugPageTargeting()', () => {
            afterEach((done) => {
                Mntl.utilities.getQueryParams.restore();
                done();
            });

            it('should return value assigned to kw when no key provided', () => {
                sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ kw: "fluiddesktop" }));
                expect(Mntl.GPT.getDebugPageTargeting()).to.deep.equal({ kw: 'fluiddesktop' });
            });

            it('should return value assigned to specified key when key provided', () => {
                sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ kw: "mdpAdDebug,true" }));
                expect(Mntl.GPT.getDebugPageTargeting()).to.deep.equal({ mdpAdDebug: 'true' });
            });

            it('should handle multiple key value pairs', () => {
                sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ kw: "mdpAdDebug,true-foo,bar-apple,orange" }));
                expect(Mntl.GPT.getDebugPageTargeting()).to.deep.equal({
                    mdpAdDebug: 'true',
                    foo: 'bar',
                    apple: 'orange'
                });
            });

            it('should handle key value pairs and kw value', () => {
                sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ kw: "fluiddesktop-mdpAdDebug,true-foo,bar-apple,orange" }));
                expect(Mntl.GPT.getDebugPageTargeting()).to.deep.equal({
                    mdpAdDebug: 'true',
                    foo: 'bar',
                    apple: 'orange',
                    kw: 'fluiddesktop'
                });
            });
            it ('should handle key value pairs and kw value when the query param is adTestKeyValues', () => {
                sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adTestKeyValues: "fluiddesktop-mdpAdDebug,true-foo,bar-apple,orange" }));
                expect(Mntl.GPT.getDebugPageTargeting()).to.deep.equal({
                    mdpAdDebug: 'true',
                    foo: 'bar',
                    apple: 'orange',
                    kw: 'fluiddesktop'
                });
            });
        });

        describe('getFloorIdByAdUnitType', () => {
            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                done();
            });

            it ('Should give back the floor id for the last server rendered ad unit based on its id prefix', () => {
                expect(Mntl.GPT.getFloorIdByAdUnitType('mob-square-fixed')).to.equal('0c68a7cda7634f7d86139aa061bf0d36');
            });
        });

        describe('getFloorValueByAdUnitType', () => {
            beforeEach((done) => {
                fixture.load('gpt-slot.html');
                done();
            });

            it ('Should give back the floor value for the last server rendered ad unit based on its id prefix', () => {
                expect(Mntl.GPT.getFloorValueByAdUnitType('mob-square-fixed')).to.equal('5');
            });
        });

        // getVideoTargeting()
        describe('getVideoTargeting()', () => {
            const config = {
                baseSlotTargeting: { 
                    type: 'testType', 
                    id: 'testId',
                    tax1: 'testCategory',
                    foo: 'bar'
                },
                pageTargeting: {
                    mtax: ['testMtax'],
                    ab: ['67', '72', '99', '63', '99', '99']
                },
                publisherProvidedId: 'testMuid',
                useLmdFormat: true
            };

            it('Should return baseSlotTargeting.id as id', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getVideoTargeting().id).to.equal('testId');
            });
            it('Should return pageTargeting.category as category', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getVideoTargeting().category).to.equal('testCategory');
            });
            it('Should return pageTargeting.mtax as mtax', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getVideoTargeting().mtax.toString()).to.equal('testMtax');
            });
            it('Should return pageTargeting.ab as ab', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getVideoTargeting().ab.toString()).to.equal('67,72,99,63,99,99');
            });
            it('Should return the publisherProvidedId as muid', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getVideoTargeting().muid).to.equal('testMuid');
            });
            it('Should return baseSlotTargeting.type as type', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getVideoTargeting().type).to.equal('testType');
            });
            it('Should return all baseSlotTargetingValues', () => {
                Mntl.GPT.init(config);
                expect(Mntl.GPT.getVideoTargeting().foo).to.equal('bar');
            });
        });

        // removeUnallowedCharacters()
        describe('removeUnallowedCharacters()', () => {
            it('Should return empty string if value is null', () => {
                expect(Mntl.GPT.removeUnallowedCharacters(null)).to.equal('');
            })
            it('Should return string if value is number', () => {
                expect(Mntl.GPT.removeUnallowedCharacters(1)).to.equal('1');
            })
            it('Should return string if value is boolean', () => {
                expect(Mntl.GPT.removeUnallowedCharacters(true)).to.equal('true');
            })
            it('Should return clean string if string contains unallowed characters', () => {
                expect(Mntl.GPT.removeUnallowedCharacters('?\'a"=!# b*~;^()<>[]c')).to.equal('abc');
            })
            it('Should replace "+", "&", and "&amp;" with "and"', () => {
                expect(Mntl.GPT.removeUnallowedCharacters('1+2&3&amp;4')).to.equal('1and2and3and4');
            })
            it('Should return array of clean values if input is array', () => {
                expect(Mntl.GPT.removeUnallowedCharacters(['foo&bar!', 1, true])).to.deep.equal(['fooandbar', '1', 'true']);
            })
        })

    });
})();