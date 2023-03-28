(function() {
    function load(done) {
        Mntl.utilities.loadExternalJS(
            { src: 'base/mantle-resources/src/main/resources/static/components/masonry-list/masonry-list.js' },
            done
        );
    }

    before(() => {
        fixture.setBase('mantle-resources/test/spec/fixtures');
    });

    describe('Mntl.MasonryList.init', () => {
        let el;
        let masonryList;

        beforeEach((done) => {
            el = fixture.load('masonry.html');
            masonryList = document.getElementById('masonry-list1_1-0');
            sinon.spy(Mntl.utilities, 'onFontLoad');
            window.JustifiedColumns = sinon.spy();
            window.Modernizr = {};
            load(done);
        });

        afterEach(() => {
            fixture.cleanup();
            Mntl.utilities.onFontLoad.restore();
        });

        function runSetStyle() {
            const imageElms = document.getElementsByTagName('img');
            const fixedWidth = imageElms[0].width; // For Masonry-List we fixed the width to the first element of the masonry list

            [...imageElms].forEach((img) => {
                const calculatedHeight = parseFloat(`${fixedWidth / (img.dataset.dimRatio || 1)}px`).toFixed(2);
                const actualHeight = parseFloat(img.style.height).toFixed(2);

                expect(actualHeight).to.equal(calculatedHeight);
            });
        }

        describe('init + justifyConfig + smil', () => {
            it('should run set styles', (done) => {
                sinon.stub(window, 'getComputedStyle', () => ({ columnCount: 1 }));
                window.Modernizr.smil = true;
                Mntl.MasonryList.init(masonryList, {});
                runSetStyle(el);
                expect(Mntl.utilities.onFontLoad).to.have.been.called;
                setTimeout(() => {
                    // Font loading induces a set timeout due to headless chrome
                    expect(window.JustifiedColumns).to.have.been.called;
                    done();
                }, 1200);

                window.getComputedStyle.restore();
            });
        });

        describe('init - justifyConfig', () => {
            it('should run set styles', () => {
                Mntl.MasonryList.init(masonryList, null);
                runSetStyle();
                expect(window.JustifiedColumns).to.not.have.been.called;
                expect(Mntl.utilities.onFontLoad).to.not.have.been.called;
            });
        });

        describe('init with config but no column-count', () => {
            it('should not justify columns', () => {
                sinon.stub(window, 'getComputedStyle', () => ({}));
                Mntl.MasonryList.init(masonryList, {});
                runSetStyle();
                expect(window.JustifiedColumns).to.not.have.been.called;
                expect(Mntl.utilities.onFontLoad).to.not.have.been.called;
                window.getComputedStyle.restore();
            });
        });
    });
}());
