/* eslint no-unused-expressions: "off" */
describe('the PubSub Object', () => {
    let pubsub;

    beforeEach((done) => {
        Mntl.utilities.loadExternalJS({ src: '/base/mantle-resources/src/main/resources/static/js/pubsub.js' },
            () => {
                pubsub = new Mntl.PubSub(['click', 'mouseenter', 'mouseout']);
                done();
            }
        );
    });

    describe('PubSub.constructor', () => {
        it('should store the events in an object', () => {
            expect(typeof (pubsub.events)).to.equal('object');
            expect(pubsub.events).to.have.keys(['click', 'mouseenter', 'mouseout']);
        });

        it('should be an object', () => {
            expect(pubsub).to.be.an.instanceof(Mntl.PubSub);
        });
    });

    describe('PubSub.what', () => {
        it('should return a list of keys', () => {
            expect(typeof(pubsub.what())).to.equal('string');
            expect(pubsub.what()).to.equal('click, mouseenter, mouseout');
        });
    });

    describe('PubSub.addEvent', () => {
        it('should return true when a new event has been added', () => {
            expect(pubsub.addEvent('clack')).to.equal(true);
            expect(pubsub.events).to.have.keys(['click', 'mouseenter', 'mouseout', 'clack']);
        });

        it('should return false when an existing event has been passed', () => {
            expect(pubsub.addEvent('click')).to.equal(false);
        });
    });

    describe('PubSub.deleteEvent', () => {
        it('should return true when an existing event has been removed', () => {
            expect(pubsub.deleteEvent('click')).to.equal(true);
            expect(pubsub.events).to.have.keys(['mouseenter', 'mouseout']);
        });

        it('should return false when there is no event to delete', () => {
            expect(pubsub.deleteEvent('roger')).to.equal(false);
        });
    });

    describe('PubSub.publish', () => {
        it('shoud throw if the event does not exist', () => {
            expect(() => {
                pubsub.publish('grapes');
            }).to['throw'];
        });

        it('should fire events marked "once" one time', () => {
            const cb = sinon.spy();

            pubsub.subscribe('click', false, cb, 'once');
            pubsub.publish('click');
            pubsub.publish('click');

            expect(cb).to.have.been.calledOnce;
        });

        it('should fire events marked "on" many times', () => {
            const cb = sinon.spy();

            pubsub.subscribe('click', false, cb, 'on');
            pubsub.publish('click');
            pubsub.publish('click');
            pubsub.publish('click');

            expect(cb).to.have.been.calledThrice;
        });
    });

    describe('PubSub.subscribe', () => {
        it('shoud throw if the event does not exist', () => {
            expect(() => {
                pubsub.subscribe('grapes');
            }).to['throw'];
        });

        it('should fire one time when subscribed with "once"', () => {
            const cb = sinon.spy();

            pubsub.subscribe('click', false, cb, 'once');
            pubsub.publish('click');
            pubsub.publish('click');

            expect(cb).to.have.been.calledOnce;
        });

        it('should fire many times when subscribed with "on"', () => {
            const cb = sinon.spy();

            pubsub.subscribe('click', false, cb, 'on');
            pubsub.publish('click');
            pubsub.publish('click');
            pubsub.publish('click');

            expect(cb).to.have.been.calledThrice;
        });

        it('should get data passed by the publish event', () => {
            let d;
            const cb = sinon.spy((data) => {
                d = data;
            });

            pubsub.subscribe('click', false, cb, 'once');
            pubsub.publish('click', {
                instigator: 'mouse',
                a: 1
            });

            expect(d).to.have.keys(['type', 'instigator', 'a']);
        });

        it('should return an unsubscribe method', () => {
            const cb = sinon.spy();
            const myEvent = pubsub.subscribe('click', false, cb, 'on');

            expect(myEvent).to.have.keys(['unSubscribe']);
        });

        it('should not fire listener when unsubscribe is called', () => {
            const cb1 = sinon.spy();
            const cb2 = sinon.spy();
            const cb3 = sinon.spy();
            const event1 = pubsub.subscribe('click', false, cb1, 'on');
            const event2 = pubsub.subscribe('click', false, cb2, 'on');
            const event3 = pubsub.subscribe('click', false, cb3, 'on'); // eslint-disable-line no-unused-vars

            pubsub.publish('click', {
                instigator: 'mouse',
                a: 1
            });
            event1.unSubscribe();
            pubsub.publish('click', {
                instigator: 'mouse',
                a: 1
            });
            event2.unSubscribe();
            pubsub.publish('click', {
                instigator: 'mouse',
                a: 1
            });

            expect(pubsub.events.click.length).to.equal(3);
            expect(pubsub.events.click[0].callback.name).to.equal('_noop');
            expect(pubsub.events.click[1].callback.name).to.equal('_noop');
            expect(cb1).to.have.been.calledOnce;
            expect(cb2).to.have.been.calledTwice;
            expect(cb3).to.have.been.calledThrice;
        });
    });

    describe('PubSub.on', () => {
        it('should fire events many times', () => {
            const cb = sinon.spy();

            pubsub.on('click', false, cb);
            pubsub.publish('click');
            pubsub.publish('click');
            pubsub.publish('click');

            expect(cb).to.have.been.calledThrice;
        });
    });

    describe('PubSub.once', () => {
        it('should fire event one time', () => {
            const cb = sinon.spy();

            pubsub.once('click', false, cb);
            pubsub.publish('click');
            pubsub.publish('click');

            expect(cb).to.have.been.calledOnce;
        });
    });
});
