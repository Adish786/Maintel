/* eslint dot-notation:off, no-unused-expressions: "off" */
(function() {
    // Load maybe.js
    beforeEach((done) => {
        Mntl.utilities.loadExternalJS(
            { src: '/base/mantle-resources/src/main/resources/static/js/maybe.js' },
            done
        );
    });

    describe('the Maybe Monad', () => {
        it('should store the supplied value', () => {
            const m = new Mntl.Maybe(1);

            expect(m._v).to.equal(1);
        });

        it('should have three instance methods', () => {
            const m = Mntl.Maybe.of(1);

            expect(typeof (m.isNothing)).to.equal('function');
            expect(typeof (m.map)).to.equal('function');
            expect(typeof (m.flatMap)).to.equal('function');
        });

        it('.of should instantiate a maybe', () => {
            const m = Mntl.Maybe.of(1);

            expect(m instanceof Mntl.Maybe).to.equal(true);
        });

        it('.isNothing should return boolean true for null and undefined', () => {
            let u;
            const m = Mntl.Maybe.of(null);
            const n = Mntl.Maybe.of(u);

            expect(m.isNothing()).to.equal(true);
            expect(n.isNothing()).to.equal(true);
        });

        it('.map should run the supplied function and return a new maybe', () => {
            const m = Mntl.Maybe.of(1);
            const n = m.map((a) => a * 9);

            expect(n._v).to.equal(9);
            expect(n instanceof Mntl.Maybe).to.equal(true);
        });

        it('.map should be chainable', () => {
            const m = Mntl.Maybe.of(1).map((a) => a * 5)
                .map((a) => a * 5);

            expect(m._v).to.equal(25);
            expect(m instanceof Mntl.Maybe).to.equal(true);
        });

        it('.map once with composed functions should yield the same result as multiple maps', () => {
            function times(a) {
                return a * 5; 
            }
            function times2(a) {
                return [times, times].reduce((acc, fn) => fn(acc), a);
            }

            const m = Mntl.Maybe.of(1).map(times)
                .flatMap(times);
            const n = Mntl.Maybe.of(1).flatMap(times2);

            expect(m).to.equal(n);
        });

        it('.flatMap should run the supplied function and return the result', () => {
            const m = Mntl.Maybe.of(1);
            const n = m.flatMap((a) => a * 9);

            expect(n).to.equal(9);
            expect(n.constructor.name).to.equal('Number');
        });

        it('.orElse should return the supplied default or stored value ', () => {
            const m = Mntl.Maybe.of(1);
            const n = Mntl.Maybe.of(null);

            expect(m.orElse('default')).to.equal(1);
            expect(n.orElse('default')).to.equal('default');
        });

        it('should NOT throw type errors', () => {
            const m = Mntl.Maybe.of(null);

            expect(m.flatMap((a) => a.split(''))).to.not.throw;
            expect(m.flatMap((a) => a.join())).to.not.throw;
            expect(m.flatMap(parseInt)).to.not.throw;
        });
    });
}());
