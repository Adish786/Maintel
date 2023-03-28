/* eslint no-unused-expressions: "off" */
describe('Mntl.BCPlayerRtb', () => {
    before(() => {
        fixture.setBase('mantle-resources/test/spec/fixtures');
    });

    let Mntl;

    function load(done) {
        Mntl = window.Mntl || {};
        Mntl.utilities.loadExternalJS(
            { src: 'base/mantle-resources/src/main/resources/static/components/widgets/video-player/js/bc-player-rtb.js' },
            done
        );
    }

    let initVideoSlotStub = sinon.stub();

    beforeEach(() => {
        Mntl = window.Mntl || {};
        Mntl.RTB = {
            initVideoSlot: function() {
                initVideoSlotStub();
                return Promise.resolve([
                    { key1: 'value1', key2: 'value2' },
                    { key3: 'value3', key4: 'value4' },
                    { key5: 'value5', key6: 'value6' }
                ]);
            }
        };
    });

    afterEach((done) => {
        initVideoSlotStub.resetHistory();
        done();
    });

    describe('resetBidTargeting', () => {
        describe('when there is no jumpstart player on the page', () => {
            before((done) => {
                load(done); 
            });
            it('should not inititate a call to Mntl.RTB.initVideoSlot', () => {
                expect(initVideoSlotStub).to.have.not.been.called;
            });            
        });
        describe('when there is a jumpstart player on the page', () => {
            before((done) => {
                fixture.load('bc-player.html');
                load(done); 
            });
            it('should inititate a call to Mntl.RTB.initVideoSlot', () => {
                expect(initVideoSlotStub).to.have.been.calledOnce;
            });
        });
    });

    describe('getBidTargeting', () => {
        before((done) => {
            fixture.load('bc-player.html');
            load(done); 
        });
        it('should return the localally scoped bidTargeting, reduced to a single object', () => {
            const combinedTargeting = {
                key1: 'value1', 
                key2: 'value2',
                key3: 'value3', 
                key4: 'value4',
                key5: 'value5',
                key6: 'value6'
            }
            expect(Mntl.BCPlayerRtb.getBidTargeting()).to.deep.equal(combinedTargeting);
        });
        it('should return an empty object if there is no jumpstart player', () => {
            document.querySelector('.jumpstart-js-wrapper').className = '';
            Mntl.BCPlayerRtb.resetBidTargeting();
            expect(Mntl.BCPlayerRtb.getBidTargeting()).to.deep.equal({});
        });
    });
});