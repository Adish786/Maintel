/* eslint no-undefined:"off", no-unused-vars:"off", no-empty-function:"off", no-unused-expressions: "off" */
describe('Mntl.RefreshableAds', () => {
    let Mntl;

    function load(done) {
        Mntl.utilities.loadExternalJS(
            { src: '/base/mantle-resources/src/main/resources/static/js/refreshableAds.js' },
            done
        );
    }

    beforeEach(() => {
        Mntl = window.Mntl || {};
        Mntl.GPT = {
            _definedSlots: {'mob-adhesive-banner-fixed': { updateTargeting: sinon.spy(() => {}) }},
            registerCallback: sinon.spy(() => {}),
            displaySlots: sinon.spy(() => {}),
            getSlotById: (id) => Mntl.GPT._definedSlots[id]
        };
    });

    // TODO: Write unit tests for the RefreshableAdManager class: https://dotdash.atlassian.net/browse/REVDEV-598

    // RefreshableAd class
    describe('RefreshableAd Class', () => {
        let adhesiveSlotEl;
        let leaderboardFixedSlotEl;

        const adhesiveTimedRefreshConfig = {
            timedRefresh: {
                ms: "25000",
                once: false
            }
        };

        const leaderboardTimedRefreshConfig = {
            timedRefresh: {
                ms: "30000",
                once: false
            }
        }

        const afterAdSlotRendersConfig = {afterAdSlotRenders: { elementId: "another-ad-id" }};

        beforeEach((done) => {
            adhesiveSlotEl = {
                addEventListener: sinon.spy(() => {}),
                id: 'mob-adhesive-banner-fixed'
            };

            leaderboardFixedSlotEl = {
                addEventListener: sinon.spy(() => {}),
                id: 'leaderboard-fixed-0'
            }
            load(done);
        });

        describe('constructor', () => {
            describe('timed refresh ads', () => {
                it('should set the correct refresh settings for timedRefresh', () => {
                    const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);

                    expect(refreshableAd._refreshType).to.equal('timed');
                    expect(refreshableAd._timedRefresh).to.deep.equal({
                        ms: '25000',
                        once: false,
                        refreshTimeoutRunning: false,
                        refreshTimeoutEnabled: true,
                        timer: 25
                    });
                    expect(refreshableAd._afterAdSlotRenders).to.equal(undefined);
                });
    
                it('should add an adRendered event listener on the ad element with correct once settings ', () => {
                    new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                    expect(adhesiveSlotEl.addEventListener).to.have.been.calledWith('adRendered', sinon.match.func, { once: false });
                });

                it('should not call registerCallback for timed refresh ads', () => {
                    new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                    expect(Mntl.GPT.registerCallback).to.not.have.been.called;
                });
            });

            describe('after ad slot renders ads', () => {
                it('should set the correct refresh settings for afterAdSlotRenders', () => {
                    const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                    expect(refreshableAd._refreshType).to.equal('afterAdSlotRenders');
                    expect(refreshableAd._afterAdSlotRenders).to.deep.equal({ elementId: "another-ad-id" });
                    expect(refreshableAd._timedRefresh).to.equal(undefined);
                });
    
                it('should not add an adRendered event listener on the ad element for non timed refresh ad ', () => {
                    new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                    expect(adhesiveSlotEl.addEventListener).to.not.have.been.called;
                });
    
                it('should call registerCallback for afterAdSlotRenders', () => {
                    new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
                    expect(Mntl.GPT.registerCallback).to.have.been.calledWith(['another-ad-id'], sinon.match.func);
                });
            });
        });

        describe('public class functions', () => {
            describe('timed refresh ads', () => {
                describe('isInView', () => {
                    it('should always return true for adhesive if visibilityState is true', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        expect(refreshableAd.isInView()).to.be.true;
                    });
                });

                describe('isRefreshTimeoutEnabled', () => {
                    it('should return true as default for timedRefresh ads', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        expect(refreshableAd.isRefreshTimeoutEnabled()).to.be.true;
                    });
                });

                describe('pauseTimeout', () => {
                    it('should set the interval and refreshTimeoutRunning to false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd.pauseTimeout();
                        expect(refreshableAd._timedRefresh.interval).to.be.false;
                        expect(refreshableAd._timedRefresh.refreshTimeoutRunning).to.be.false;
                    });

                    it('should have its refreshTimeout running if a different ad is paused', () => {
                        const refreshableAdhesive = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        const refreshableLeaderboard = new Mntl.RefreshableAds.RefreshableAd(leaderboardFixedSlotEl, leaderboardTimedRefreshConfig);

                        refreshableAdhesive.pauseTimeout();

                        expect(refreshableLeaderboard._timedRefresh.interval).to.not.be.true;
                        expect(refreshableLeaderboard._timedRefresh.refreshTimeoutRunning).to.not.be.true;
                    });
                });

                describe('startTimeout', () => {
                    it('should set refreshTimeoutRunning to true and restart the timer with full amount', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        refreshableAd._timedRefresh.timer = -1;
                        sinon.stub(refreshableAd, '_isTimerReady').callsFake(() => true);
                        refreshableAd.startTimeout();
                        expect(refreshableAd._timedRefresh.refreshTimeoutRunning).to.be.true;
                        expect(refreshableAd._timedRefresh.timer).to.be.equal(25);
                    });
                    
                    it('should set refreshTimeoutRunning to true and start the timeout from where it left off if timer still has time remaining', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._timedRefresh.timer = 10;
                        sinon.stub(refreshableAd, '_isTimerReady').callsFake(() => true);
                        refreshableAd.startTimeout();
                        expect(refreshableAd._timedRefresh.refreshTimeoutRunning).to.be.true;
                        expect(refreshableAd._timedRefresh.timer).to.be.equal(10);
                    });
                });

                describe('disableTimedRefresh', () => {
                    it('should call _pauseTimeout and set refreshTimeoutEnabled to false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        refreshableAd.pauseTimeout = sinon.spy(() => {});
                        
                        refreshableAd.disableTimedRefresh();
                        expect(refreshableAd.pauseTimeout).to.have.been.called;
                        expect(refreshableAd._timedRefresh.refreshTimeoutRunning).to.be.false;
                    });


                    it('should not be paused if a different ad calls _pauseTimeout', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        const refreshableLeaderboard = new Mntl.RefreshableAds.RefreshableAd(leaderboardFixedSlotEl, leaderboardTimedRefreshConfig);

                        refreshableAd.pauseTimeout = sinon.spy(() => {});
                        
                        refreshableAd.disableTimedRefresh();

                        expect(refreshableAd.pauseTimeout).to.have.been.called;
                        expect(refreshableLeaderboard._timedRefresh.refreshTimeoutRunning).to.not.be.true;
                    });
                });

                describe('disableAfterAdSlotRendersRefresh', () => {
                    it('should not set _refreshType to null', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        refreshableAd.disableAfterAdSlotRendersRefresh();
                        expect(refreshableAd._refreshType).to.be.equal('timed');
                    });
                });

                describe('reEnableTimedRefresh', () => {
                    it('should call _pauseTimeout and set refreshTimeoutEnabled to false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        refreshableAd.startTimeout = sinon.spy(() => {});
                        
                        refreshableAd.reEnableTimedRefresh();
                        expect(refreshableAd.startTimeout).to.have.been.called;
                        expect(refreshableAd._timedRefresh.refreshTimeoutEnabled).to.be.true;
                    });
                });
            });

            describe('after ad slot renders ads', () => {
                describe('isInView', () => {
                    it('should always return true for adhesive if visibilityState is true', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
                        
                        expect(refreshableAd.isInView()).to.be.true;
                    });
                });

                describe('isRefreshTimeoutEnabled', () => {
                    it('should return false as default for afterAdSlotRenders refreshable ads', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);

                        expect(refreshableAd.isRefreshTimeoutEnabled()).to.be.false;
                    });
                });

                describe('startTimeout', () => {
                    it('should not do anything if it is not a timed refresh ad', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
                        
                        refreshableAd._isTimerReady = sinon.spy(() => {});
                        refreshableAd.startTimeout();
                        expect(refreshableAd._isTimerReady).to.not.have.been.called;
                    });
                });

                describe('disableTimedRefresh', () => {
                    it('should not do anything if it is not a timed refresh ad', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
                        
                        refreshableAd.pauseTimeout = sinon.spy(() => {});
                        
                        refreshableAd.disableTimedRefresh();
                        expect(refreshableAd.pauseTimeout).to.not.have.been.called;
                    });
                });

                describe('reEnableTimedRefresh', () => {
                    it('should not do anything if it is not a timed refresh ad', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
                        
                        refreshableAd.startTimeout = sinon.spy(() => {});
                        
                        refreshableAd.reEnableTimedRefresh();
                        expect(refreshableAd.startTimeout).to.not.have.been.called;
                    });
                });

                describe('disableAfterAdSlotRendersRefresh', () => {
                    it('should call set _refreshType to null', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
                        
                        refreshableAd.disableAfterAdSlotRendersRefresh();
                        expect(refreshableAd._refreshType).to.be.equal('afterAdSlotRenders-disabled');
                    });
                });
            });
        });

        describe('private class functions', () => {
            describe('timed refresh ads', () => {
                describe('_afterAdRenderedHandler', () => {
                    it('should set _inFlight to false and call startTimeout', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        refreshableAd.startTimeout = sinon.spy(() => {});
                        
                        refreshableAd._afterAdRenderedHandler();
                        expect(refreshableAd.startTimeout).to.have.been.called;
                        expect(refreshableAd._inFlight).to.be.false;
                    });
                });

                describe('_isRefreshReady', () => {
                    it('should return false if inFlight is true', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);

                        refreshableAd._inFlight = true;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = true;
                        sinon.stub(refreshableAd, 'isInView').callsFake(() => true);
                        expect(refreshableAd._isRefreshReady()).to.be.false;
                    });

                    it('should return false if refreshTimeoutEnabled is false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);

                        refreshableAd._inFlight = false;
                        sinon.stub(refreshableAd, 'isInView').callsFake(() => false);
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = false;
                        expect(refreshableAd._isRefreshReady()).to.be.false;
                    });

                    it('should return false if isInView() is false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);

                        refreshableAd._inFlight = false;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = true;
                        sinon.stub(refreshableAd, 'isInView').callsFake(() => false);
                        expect(refreshableAd._isRefreshReady()).to.be.false;
                    });

                    it('should return true if all requirements are met', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);

                        refreshableAd._inFlight = false;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = true;
                        sinon.stub(refreshableAd, 'isInView').callsFake(() => true);
                        expect(refreshableAd._isRefreshReady()).to.be.true;
                    });
                });

                describe('_isTimerReady', () => {
                    it('should return false if initial request has not yet been made', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        const slotObj = Mntl.GPT.getSlotById(refreshableAd._el.id);

                        slotObj.requested = false;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = true;
                        refreshableAd._timedRefresh.refreshTimeoutRunning = false;
                        refreshableAd._inFlight = false;
                        expect(refreshableAd._isTimerReady()).to.be.false;
                    });
    
                    it('should return false if timeout is not enabled', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        const slotObj = Mntl.GPT.getSlotById(refreshableAd._el.id);

                        slotObj.requested = true;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = false;
                        refreshableAd._timedRefresh.refreshTimeoutRunning = false;
                        refreshableAd._inFlight = false;
                        expect(refreshableAd._isTimerReady()).to.be.false;
                    });
    
                    it('should return false if timeout is already running', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        const slotObj = Mntl.GPT.getSlotById(refreshableAd._el.id);

                        slotObj.requested = true;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = true;
                        refreshableAd._timedRefresh.refreshTimeoutRunning = true;
                        refreshableAd._inFlight = false;
                        expect(refreshableAd._isTimerReady()).to.be.false;
                    });
    
                    it('should return false if an ad request is already inFlight', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        const slotObj = Mntl.GPT.getSlotById(refreshableAd._el.id);
                        
                        slotObj.requested = true;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = true;
                        refreshableAd._timedRefresh.refreshTimeoutRunning = false;
                        refreshableAd._inFlight = true;
                        expect(refreshableAd._isTimerReady()).to.be.false;
                    });
    
                    it('should return true if all requirements are met', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
                        
                        const slotObj = Mntl.GPT.getSlotById(refreshableAd._el.id);

                        slotObj.requested = true;
                        refreshableAd._timedRefresh.refreshTimeoutEnabled = true;
                        refreshableAd._timedRefresh.refreshTimeoutRunning = false;
                        refreshableAd._inFlight = false;
                        expect(refreshableAd._isTimerReady()).to.be.true;
                    });
                });

                describe('_timerCountdown', () => {
                    it('should call _refreshMe when timer hits 0', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._refreshMe = sinon.spy(() => {});
                        refreshableAd._timedRefresh.timer = 0;
                        refreshableAd._timerCountdown();
                        expect(refreshableAd._refreshMe).to.have.been.called;
                    });
    
                    it('should decrement the counter if we still have time', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._refreshMe = sinon.spy(() => {});
                        refreshableAd._timedRefresh.timer = 10;
                        refreshableAd._timerCountdown();
                        expect(refreshableAd._refreshMe).to.not.have.been.called;
                        expect(refreshableAd._timedRefresh.timer).to.equal(9);
                    });
                });

                describe('_stopTimeout', () => {
                    it('should clear out the timer and set the timer to not running', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._timedRefresh.timer = 10;
                        refreshableAd._timedRefresh.refreshTimeoutRunning = true;
                        refreshableAd._stopTimeout();
                        expect(refreshableAd._timedRefresh.refreshTimeoutRunning).to.be.false;
                        expect(refreshableAd._timedRefresh.timer).to.equal(-1);
                    });
                });

                describe('_removeTimedRefresh', () => {
                    it('should clear out the timer and set the timer to not running', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._removeTimedRefresh();
                        expect(refreshableAd._timedRefresh).to.be.null;
                        expect(refreshableAd._refreshType).to.be.null;
                    });
                });

                describe('_startInFlight', () => {
                    it('should set _inFlight to true', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._startInFlight();
                        expect(refreshableAd._inFlight).to.be.true;
                    });
                });

                describe('_completeInFlight', () => {
                    it('should set _inFlight to false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._completeInFlight();
                        expect(refreshableAd._inFlight).to.be.false;
                    });
                });

                describe('_refreshMe', () => {
                    it('should stop the timeout if it is a timed refresh ad', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._stopTimeout = sinon.spy(() => {});
                        refreshableAd._refreshMe();
                        expect(refreshableAd._stopTimeout).to.have.been.called;
                    });
    
                    it('should increment and add the rord targeting value and the refresh timed targeting value for timed refresh ads', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        sinon.stub(refreshableAd, '_isRefreshReady').callsFake(() => true);
                        refreshableAd._refreshMe();
                        const slotObj = Mntl.GPT.getSlotById(refreshableAd._el.id);

                        expect(slotObj.updateTargeting).to.have.been.calledWith({ rord: 1 });
                        expect(slotObj.updateTargeting).to.have.been.calledWith({ refresh: 'timed' });
                    });
    
                    it('should call _startInFlight and call Mntl.GPT.displaySlots', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._startInFlight = sinon.spy(() => {});
                        sinon.stub(refreshableAd, '_isRefreshReady').callsFake(() => true);
                        refreshableAd._refreshMe();
                        expect(refreshableAd._startInFlight).to.have.been.called;
                        expect(Mntl.GPT.displaySlots).to.have.been.called;
                    });
    
                    it('should call _removeTimedRefresh if timedRefresh once is set to true', () => {
                        adhesiveTimedRefreshConfig.timedRefresh.once = true;

                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, adhesiveTimedRefreshConfig);
    
                        refreshableAd._removeTimedRefresh = sinon.spy(() => {});
                        sinon.stub(refreshableAd, '_isRefreshReady').callsFake(() => true);
                        refreshableAd._refreshMe();
                        expect(refreshableAd._removeTimedRefresh).to.have.been.called;
                    });
                });
            });
            
            describe('after ad slot renders ads', () => {
                describe('_isRefreshReady', () => {
                    it('should return false if inFlight is true', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                        refreshableAd._inFlight = true;
                        sinon.stub(refreshableAd, 'isInView').callsFake(() => true);
                        expect(refreshableAd._isRefreshReady()).to.be.false;
                    });
    
                    it('should return false if isInView() is false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                        refreshableAd._inFlight = false;
                        sinon.stub(refreshableAd, 'isInView').callsFake(() => false);
                        expect(refreshableAd._isRefreshReady()).to.be.false;
                    });
    
                    it('should return true if all requirements are met', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                        refreshableAd._inFlight = false;
                        sinon.stub(refreshableAd, 'isInView').callsFake(() => true);
                        expect(refreshableAd._isRefreshReady()).to.be.true;
                    });
                });

                describe('_startInFlight', () => {
                    it('should set _inFlight to true', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                        refreshableAd._startInFlight();
                        expect(refreshableAd._inFlight).to.be.true;
                    });
                });

                describe('_completeInFlight', () => {
                    it('should set _inFlight to false', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                        refreshableAd._completeInFlight();
                        expect(refreshableAd._inFlight).to.be.false;
                    });
                });

                describe('_refreshMe', () => {
                    it('should increment and add the rord targeting value and not add the refresh timed targeting', () => {
                        const refreshableAd = new Mntl.RefreshableAds.RefreshableAd(adhesiveSlotEl, afterAdSlotRendersConfig);
    
                        const slotObj = Mntl.GPT.getSlotById(refreshableAd._el.id);

                        sinon.stub(refreshableAd, '_isRefreshReady').callsFake(() => true);
                        refreshableAd._refreshMe();
                        
                        expect(slotObj.updateTargeting).to.have.been.calledWith({ rord: 1 });
                        expect(slotObj.updateTargeting).to.not.have.been.calledWith({ refresh: 'timed' });
                    });
                });
            });
        });
    });
});