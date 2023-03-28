
/* eslint no-undefined:"off", no-unused-expressions: "off" */
describe('Mntl.domUtilities', () => {
    before(() => {
        fixture.setBase('mantle-resources/test/spec/fixtures');
    });

    // closestPreviousSibling
    describe('closestPreviousSibling method', () => {
        beforeEach(() => {
            fixture.load('masonry.html');
        });

        afterEach(() => {
            fixture.cleanup();
        });

        it('should check itself', () => {
            const el = fixture.el.getElementsByClassName('card__content')[0];

            function testSelf(e) {
                return e.classList.contains('card__content');
            }

            expect(Mntl.domUtilities.closestPreviousSibling(testSelf, el)).to.deep.equal(el);
        });

        it('should find the first previous match', () => {
            const el = fixture.el.getElementsByTagName('li');
            const last = el[el.length - 1];
            const nextToLast = el[el.length - 2];

            function testLast(e) {
                return e.classList.contains('closest-previous-sibling');
            }

            expect(Mntl.domUtilities.closestPreviousSibling(testLast, last)).to.deep.equal(nextToLast);
        });

        it('should return null when there is no match', () => {
            const el = fixture.el.getElementsByTagName('li');
            const last = el[el.length - 1];

            function testLast() {
                return el.nodeName === 'DIV';
            }

            expect(Mntl.domUtilities.closestPreviousSibling(testLast, last)).to.equal(null);
        });
    });

    // createEl
    describe('createEl method', () => {
        it('should throw if given poorly formatted arguments', () => {
            expect(Mntl.domUtilities.createEl.bind(null, {type: 'DIV'})).to['throw'](TypeError);
            expect(Mntl.domUtilities.createEl.bind(null, {
                type: 'DIV', 
                props: {}, 
                children: [{type: 'SPAN'}]
            })).to['throw'](TypeError);
        });

        it('should create crazy tags because that\'s what document.createElement does', () => {
            const newEl = Mntl.domUtilities.createEl({
                type: 'Ralph', 
                props: {}, 
                children: []
            });

            expect(newEl.nodeName).to.equal('RALPH');
        });

        it('should create text nodes', () => {
            const newEl = Mntl.domUtilities.createEl('Megadeth');

            expect(newEl instanceof Node).to.equal(true);
        });

        it('should create nested DOM structures', () => {
            const ul = Mntl.domUtilities.createEl({
                type: 'UL',
                props: { className: 'this-is-ul'},
                children: [{
                    type: 'LI',
                    props: {},
                    children: ['List Item One']
                }, {
                    type: 'LI',
                    props: { className: 'itemTwo' },
                    children: []
                }, {
                    type: 'LI',
                    props: {},
                    children: []
                }]
            });

            expect(ul.nodeName).to.equal('UL');
            expect(ul.classList.contains('this-is-ul')).to.equal(true);
            expect(ul.classList.contains('anotherClass')).to.equal(false);
            expect(ul.childNodes.length).to.equal(3);
            expect(ul.childNodes[0].innerText).to.equal('List Item One');
            expect(ul.childNodes[1].classList.contains('itemTwo')).to.equal(true);

        });
    });

    // getData
    describe('getData method', () => {
        it('should return parsed data', () => {
            const mockEl = {
                dataset: {
                    boolEan: 'true',
                    boolF: 'false',
                    num: '1',
                    zero: '0',
                    float: '1.5',
                    str: '"string"',
                    nothing: 'null',
                    empty: '',
                    arr: '[1, 2, 3]',
                    obj: JSON.stringify({
                        a: 1,
                        b: 'a'
                    })
                }
            };

            expect(Mntl.domUtilities.getData('boolEan', mockEl)).to.equal(true);
            expect(Mntl.domUtilities.getData('boolF', mockEl)).to.equal(false);
            expect(Mntl.domUtilities.getData('num', mockEl)).to.equal(1);
            expect(Mntl.domUtilities.getData('zero', mockEl)).to.equal(0);
            expect(Mntl.domUtilities.getData('float', mockEl)).to.equal(1.5);
            expect(Mntl.domUtilities.getData('str', mockEl)).to.equal('string');
            expect(Mntl.domUtilities.getData('nothing', mockEl)).to.equal(null);
            expect(Mntl.domUtilities.getData('empty', mockEl)).to.equal('');
            expect(Mntl.domUtilities.getData('arr', mockEl)).to.deep.equal([1, 2, 3]);
            expect(Mntl.domUtilities.getData('obj', mockEl)).to.deep.equal({
                a: 1,
                b: 'a'
            });
            expect(Mntl.domUtilities.getData('doesNotExist', mockEl)).to.equal(undefined);
        });

        it('should return parsedData', () => {
            const mockEl = {
                'data-bool-ean': 'true',
                'data-bool-f': 'false',
                'data-num': '1',
                'data-zero': '0',
                'data-float': '1.5',
                'data-str': '"string"',
                'data-nothing': 'null',
                'data-empty': '',
                'data-arr': '[1, 2, 3]',
                'data-obj': "{\"a\":1,\"b\":\"a\"}",
                getAttribute(n) {
                    return this[n];
                }
            };

            expect(Mntl.domUtilities.getData('boolEan', mockEl)).to.equal(true);
            expect(Mntl.domUtilities.getData('boolF', mockEl)).to.equal(false);
            expect(Mntl.domUtilities.getData('num', mockEl)).to.equal(1);
            expect(Mntl.domUtilities.getData('zero', mockEl)).to.equal(0);
            expect(Mntl.domUtilities.getData('float', mockEl)).to.equal(1.5);
            expect(Mntl.domUtilities.getData('str', mockEl)).to.equal('string');
            expect(Mntl.domUtilities.getData('nothing', mockEl)).to.equal(null);
            expect(Mntl.domUtilities.getData('empty', mockEl)).to.equal('');
            expect(Mntl.domUtilities.getData('arr', mockEl)).to.deep.equal([1, 2, 3]);
            expect(Mntl.domUtilities.getData('obj', mockEl)).to.deep.equal({
                a: 1,
                b: 'a'
            });
            expect(Mntl.domUtilities.getData('doesNotExist', mockEl)).to.equal(undefined);
        });
    });

    // getResourceRootUrl
    describe('getResourceRootUrl method', () => {
        it('Should return ', (done) => {
            const expected = `${window.location.protocol}//${window.location.host}`;

            expect(Mntl.domUtilities.getResourceRootUrl()).to.be.equal(expected);
            done();
        })
    });

    // outerHeight
    describe('outerHeight method', () => {
        beforeEach((done) => {
            fixture.load('outerHeight.html');
            done();
        });

        afterEach(() => {
            fixture.cleanup();
        });

        it('should return the total height of an element with margin', () => {
            const d = document.getElementById('theDiv');

            expect(Mntl.domUtilities.outerHeight(d)).to.equal(142);
        });
    });

    // setDataAttrs
    describe('setDataAttrs method', () => {
        const el = document.createElement('DIV');
        const data = { // eslint-disable-line quote-props
            goodKey: 'good val',
            boolKey: true,
            numKey: 1,
            'bad-key': 'bad-val'
        };

        before(() => {
            sinon.spy(window.debug, 'error');
        });

        after(() => {
            window.debug.error.restore();
        });

        it('Should set data attributes', () => {
            Mntl.domUtilities.setDataAttrs(data, el);

            expect(el.dataset.goodKey).to.equal(data.goodKey);
            expect(el.dataset.boolKey).to.equal('true');
            expect(el.dataset.numKey).to.equal('1');
            expect(el.dataset.badKey).to.equal(undefined);
            expect(window.debug.error).to.have.been.called;
        });
    });

    // setProp
    describe('setProp method', () => {
        it('Should set a single prop on the provided element', () => {
            const el1 = document.createElement('DIV');
            const el2 = document.createElement('DIV');

            Mntl.domUtilities.setProp('className', 'my-class', el1);
            Mntl.domUtilities.setProp('title', 'The Div', el2);

            expect(el1.classList.contains('my-class')).to.equal(true);
            expect(el2.getAttribute('title')).to.equal('The Div');
        });
    });

    // setProps
    describe('setProps method', () => {
        it('Should set a single prop on the provided element', () => {
            const el = document.createElement('DIV');

            Mntl.domUtilities.setProps(el, {
                className: 'my-class',
                'title': 'The Div'
            });

            expect(el.classList.contains('my-class')).to.equal(true);
            expect(el.getAttribute('title')).to.equal('The Div');
        });
    });
});
