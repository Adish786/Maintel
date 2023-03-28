/* eslint no-unused-expressions: "off" */
describe('Mntl.fnUtilities', () => {
    describe('all method', () => {
        it('should exist on Mntl.fnUtilities', () => {
            expect(typeof (Mntl.fnUtilities.all)).to.equal('function');
        });

        it('should call the callback once all functions have called their callback', (done) => {
            const fn1 = sinon.spy((fn) => {
                window.setTimeout(fn, 30);
            });
            const fn2 = sinon.spy((fn) => {
                window.setTimeout(fn, 20);
            });
            const fn3 = sinon.spy((fn) => {
                window.setTimeout(fn, 50);
            });
            const cb = sinon.spy();

            Mntl.fnUtilities.all([fn1, fn2, fn3], cb);

            window.setTimeout(() => {
                expect(fn1).to.have.been.calledBefore(fn2);
                expect(fn2).to.have.been.calledBefore(fn3);
                expect(fn3).to.have.been.calledBefore(cb);
                expect(cb).to.have.been.calledOnce;
                done();
            }, 200);
        });
    });

    // curry
    describe('curry method', () => {
        const simpleSum = Mntl.fnUtilities.curry((a, b, c) => a + b + c);

        function sumAll(...args) {
            return Array.prototype.slice.call(args).reduce((acc, n) => acc + n, 0);
        }

        it('should return functions until all args are gathered', () => {
            expect(typeof (simpleSum)).to.equal('function');
            expect(typeof (simpleSum(1))).to.equal('function');
            expect(typeof (simpleSum(1)(2))).to.equal('function');
        });

        it('should not require butted braces ()()', () => {
            expect(typeof (simpleSum(1, 2))).to.equal('function');
        });

        it('should return a value when all arguments are gathered', () => {
            expect(simpleSum(1, 2, 3)).to.equal(6);
            expect(simpleSum(1, 2)(3)).to.equal(6);
            expect(simpleSum(1)(2)(3)).to.equal(6);
        });

        it('should throw if arity is < 1', () => {
            expect(() => { Mntl.fnUtilities.curry(sumAll) }).to.throw(TypeError);
        });

        it('should complete the function when the provided number of argument is met', () => {
            const sa = Mntl.fnUtilities.curry(sumAll, 5);

            expect(sa(1)(2)(3)(4)(5)).to.equal(15);
        });
    });

    // deepExtend
    describe('deepExtend method', () => {
        it('Should deep merge the source object into the destination object', (done) => {
            const destination = {
                a: 1,
                b: { c: 2 }
            };
            const source = {
                a: 2,
                b: { c: 3 },
                d: 4
            };

            Mntl.fnUtilities.deepExtend(destination, source);

            expect(destination).to.deep.equal({
                a: 2,
                b: { c: 3 },
                d: 4
            });
            done();
        });
    });

    // getDeepValue
    describe('getDeepValue method', () => {
        const obj = {
            items: [
                { id: '98rwq8w' },
                {
                    id: '809ashd',
                    name: 'widget'
                }
            ]
        };
        const arr = [
            { responses: [] },
            { responses: [0, 1, 2] }
        ];
        const falsy = {
            a: null,
            b: 0,
            c: '',
            d: false
        };

        it('should return undefined if the key/value does not exit', (done) => {
            expect(Mntl.fnUtilities.getDeepValue(obj, 'items', '3', 'id')).to.be.undefined;
            expect(Mntl.fnUtilities.getDeepValue(arr, '0', 'responses', '0')).to.be.undefined;
            expect(Mntl.fnUtilities.getDeepValue(falsy, 'e')).to.be.undefined;
            done();
        });

        it('should return undefined on keys nested in a null value', () => {
            expect(Mntl.fnUtilities.getDeepValue({data: { list: null }}, 'data', 'list', 0)).to.be.undefined;
            expect(Mntl.fnUtilities.getDeepValue({data: { list: null }}, 'data', 'list', 'one')).to.be.undefined;
        });

        it('should return null value', () => {
            expect(Mntl.fnUtilities.getDeepValue({data: { list: null }}, 'data', 'list')).to.equal(null);
        });

        it('should return the value when the key is present', (done) => {
            expect(Mntl.fnUtilities.getDeepValue(obj, 'items', '0', 'id')).to.equal('98rwq8w');
            expect(Mntl.fnUtilities.getDeepValue(arr, '1', 'responses', '2')).to.equal(2);
            expect(Mntl.fnUtilities.getDeepValue(falsy, 'a')).to.equal(null);
            expect(Mntl.fnUtilities.getDeepValue(falsy, 'b')).to.equal(0);
            expect(typeof (Mntl.fnUtilities.getDeepValue(falsy, 'c'))).to.equal('string')
            expect(Mntl.fnUtilities.getDeepValue(falsy, 'd')).to.equal(false);
            done();
        });

    });

    // iterate
    describe('iterate method', () => {
        it('Should call a callback on each item in an object', (done) => {
            const callback = sinon.spy((obj, key) => {
                obj[key] += 1;
            });
            const obj = {
                a: 1,
                b: 2,
                c: 3
            };

            Mntl.fnUtilities.iterate(obj, callback);

            expect(callback).to.have.been.calledThrice;
            expect(callback).to.always.have.been.calledWith(obj);
            expect(obj).to.deep.equal({ 
                a: 2,
                b: 3,
                c: 4
            });
            done();
        });
    });

    // mapObject
    describe('mapObject method', () => {
        const originalObject = {
            a: 1,
            b: 'string',
            c: ['one', 'two', 3],
            d: { id: 1234 }
        };

        it('Should return a new object with the same keys and values', () => {
            const result = Mntl.fnUtilities.mapObject(originalObject, null, null);

            expect(result).to.be.not.equal(originalObject);
            expect(result).to.contain.all.keys(['a', 'b', 'c', 'd']);
            expect(result.a).to.equal(1);
            expect(result.b).to.equal('string');
            expect(result.c).to.deep.equal(['one', 'two', 3]);
            expect(result.d).to.deep.equal({id: 1234});
        });

        it('Should return a new object with the same keys and changed values', () => {
            const result = Mntl.fnUtilities.mapObject(originalObject, null, (val) => {
                let v;

                switch (typeof val) {
                    case 'number':
                        v = `${val}-string`;
                        break;
                    case 'string':
                        v = val.length;
                        break;
                    default:
                        if (Array.isArray(val)) {
                            v = val.map((vl) => vl + 1);
                        } else {
                            v = val.id + 1;
                        }
                        break;
                }

                return v;
            });

            expect(result).to.be.not.equal(originalObject);
            expect(result).to.contain.all.keys(['a', 'b', 'c', 'd']);
            expect(result.a).to.equal('1-string');
            expect(result.b).to.equal(6);
            expect(result.c).to.deep.equal(['one1', 'two1', 4]);
            expect(result.d).to.equal(1235);
        });

        it('Should return a new object with changed keys and the same values', () => {
            const result = Mntl.fnUtilities.mapObject(originalObject, (key) => `${key}New`, null);

            expect(result).to.be.not.equal(originalObject);
            expect(result).to.contain.all.keys(['aNew', 'bNew', 'cNew', 'dNew']);
            expect(result.aNew).to.equal(1);
            expect(result.bNew).to.equal('string');
            expect(result.cNew).to.deep.equal(['one', 'two', 3]);
            expect(result.dNew).to.deep.equal({id: 1234});
        });

        it('Should return a new object with falsy keys discarded', () => {
            const result = Mntl.fnUtilities.mapObject(originalObject, (key) => (key === 'a' ? 'a' : null), null);

            expect(result).to.be.not.equal(originalObject);
            expect(result).to.contain.all.keys(['a']);
            expect(Object.keys(result).length).to.equal(1);
            expect(result.a).to.equal(1);
        });
    });

    // pickObject
    describe('pickObject method', () => {
        const originalObject = {
            a: 1,
            b: 'string',
            c: ['one', 'two', 3],
            d: { id: 1234 }
        };

        it('Should return an empty object if keys are not specified', () => {
            const result = Mntl.fnUtilities.pickObject(originalObject);

            expect(result).to.be.not.equal(originalObject);
            expect(Object.keys(result).length).to.equal(0);
        });

        it('Should return a new object with the picked keys', () => {
            const result = Mntl.fnUtilities.pickObject(originalObject, ['a', 'b']);

            expect(result).to.be.not.equal(originalObject);
            expect(result).to.contain.all.keys(['a', 'b']);
            expect(Object.keys(result).length).to.equal(2);
            expect(result.a).to.equal(1);
            expect(result.b).to.equal('string');
        });
    });

    // once
    describe('once method', () => {
        it('Should call the supplied function one time', () => {
            const cb = sinon.spy();
            const oneTime = Mntl.fnUtilities.once(cb);

            oneTime();
            oneTime();

            expect(cb).to.have.been.calledOnce;
        });

        it('should execute the supplied function one time each time it is wrapped in once', () => {
            const cb = sinon.spy();
            const oneTime = Mntl.fnUtilities.once(cb);
            const anotherTime = Mntl.fnUtilities.once(cb);

            oneTime();
            anotherTime();
            oneTime();
            anotherTime();

            expect(cb).to.have.been.calledTwice;
        });

        it('Should call the supplied function with the context and arguments supplied to once', () => {
            const context = {
                someInt: 5,
                someStr: 'Hello World'
            };

            function test() {
                return this;
            }

            const cb = sinon.spy(test);
            const oneTime = Mntl.fnUtilities.once(cb);
            const result = oneTime.call(context, 1, 'a');

            expect(cb).to.have.been.calledWith(1, 'a');
            expect(result).to.deep.equal(context);
        });
    });

    // promiseOnce
    describe('promiseOnce method', () => {
        function promiseFunction(callback) {
            return new Promise((resolve) => {
                setTimeout(() => resolve(callback()), 1000);
            });
        }

        it('Should resolve once indicated by the supplied function being called once', () => {
            const cb = sinon.spy();

            const oneTime = Mntl.fnUtilities.promiseOnce(() => promiseFunction(cb));

            oneTime();
            oneTime();

            return Promise.all([oneTime(), oneTime()]).then(() => expect(cb).to.have.been.calledOnce);
        });

        it('Should resolve twice if we have two instances of promiseOnce that pass in the supplied function', () => {
            const cb = sinon.spy();
            const oneTime = Mntl.fnUtilities.promiseOnce(() => promiseFunction(cb));
            const anotherTime = Mntl.fnUtilities.promiseOnce(() => promiseFunction(cb));

            oneTime();
            anotherTime();
            oneTime();
            anotherTime();

            return Promise.all([oneTime(), anotherTime(), oneTime(), anotherTime()]).then(() => expect(cb).to.have.been.calledTwice);
        });

        it('Should resolve to the values from the context passed into supplied function', () => {
            const context = {
                someInt: 5,
                someStr: 'Hello World'
            };

            function test() {
                return this;
            }

            const cb = sinon.spy(test);
            const oneTime = Mntl.fnUtilities.promiseOnce(() => promiseFunction(cb.bind(context, 1, 'a')));

            return oneTime().then((result) => {
                expect(result).to.deep.equal(context);
                expect(cb).to.have.been.calledWith(1, 'a');
            });
        });
    });

    // pipe
    describe('pipe method', () => {
        const result = Mntl.fnUtilities.pipe(
            Mntl.utilities.getQueryParams,
            (obj) => obj.dot,
            Mntl.fnUtilities.trimAllWhitespace
        );

        it('should return a unary function', () => {
            expect(typeof (result)).to.equal('function');
            expect(result.length).to.equal(1);
        });

        it('should output the expected value', () => {
            expect(result('dot=%20%20dash%20')).to.equal('dash');
        });

        it('should throw if all arguments are not functions', () => {
            expect(() => {
                Mntl.fnUtilities.pipe(() => {}, 1); // eslint-disable-line no-empty-function
            }).to['throw']('All arguments provided to Mntl.fnUtilities.pipe must be functions');
        });
    });

    // safeJsonParse
    describe('safeJsonParse method', () => {
        it('should return the parsed object', (done) => {
            const obj = {
                a: 1,
                b: null,
                c: false,
                d: true,
                e: 'string'
            };
            const objResult = Mntl.fnUtilities.safeJsonParse(JSON.stringify(obj));
            const arr = [1, 2, 'a', 'b', null, false, true];
            const arrResult = Mntl.fnUtilities.safeJsonParse(JSON.stringify(arr));

            expect(objResult).to.deep.equal(obj);
            expect(arrResult).to.deep.equal(arr);
            expect(Mntl.fnUtilities.safeJsonParse(2)).to.equal(2);
            expect(Mntl.fnUtilities.safeJsonParse(JSON.stringify('a'))).to.equal('a');
            expect(Mntl.fnUtilities.safeJsonParse(true)).to.equal(true);
            expect(Mntl.fnUtilities.safeJsonParse(false)).to.equal(false);
            done();
        });

        it('should return null', (done) => {
            expect(Mntl.fnUtilities.safeJsonParse('')).to.equal(null);
            expect(Mntl.fnUtilities.safeJsonParse('a')).to.equal(null);
            expect(Mntl.fnUtilities.safeJsonParse(null)).to.equal(null);
            done();
        });
    });

    // toArray
    describe('toArray method', () => {
        it('should turn an array like object into an array', () => {
            (function(...args) {
                const arr = Mntl.fnUtilities.toArray(args);

                expect(arr).to.be.an('array');
                expect(arr.length).to.equal(3);
                expect(arr).to.deep.equal([1, 2, 3]);
            }(1, 2, 3));
        });

        it('should optionally return an array of a portion of an array like object', () => {
            (function(...args) {
                const arr = Mntl.fnUtilities.toArray(args, 1);

                expect(arr).to.be.an('array');
                expect(arr.length).to.equal(2);
                expect(arr).to.deep.equal([2, 3]);
            }(1, 2, 3));
        });
    });

    // trimAllWhitespace
    describe('trimAllWhitespace method', () => {
        it('Should remove left hand white space, right hand white space and replace new lines with spaces', (done) => {
            const result = Mntl.fnUtilities.trimAllWhitespace(' The quick brown fox\n\njumped over the lazy dog   ');
            const expectedResult = 'The quick brown fox  jumped over the lazy dog';

            expect(result).to.equal(expectedResult);
            done();
        });

        it('Should return an empty string if called with a falsy value', (done) => {
            expect(Mntl.fnUtilities.trimAllWhitespace('')).to.equal('');
            expect(Mntl.fnUtilities.trimAllWhitespace(0)).to.equal('');
            expect(Mntl.fnUtilities.trimAllWhitespace(false)).to.equal('');
            done();
        });
    });
});