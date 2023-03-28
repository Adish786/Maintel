window.Mntl = window.Mntl || {};

window.Mntl.UniqueSlotSize = (function() {
    class UniqueSlotSize {
        constructor(adType, sizeConfig) {
            this.adType = adType;
            this.uniqueSize = sizeConfig.base;
            this.increment = sizeConfig.increment;
            this.counter = 0;
        }

        getUniqueSize() {
            return [this.uniqueSize[0], this.uniqueSize[1] + this.getCounter()];
        }

        getCounter() {
            if (this.increment) this.counter++;

            return this.counter;
        }
    }

    return UniqueSlotSize;
})();