window.Mntl = window.Mntl || {};

Mntl.Targeting = (function () {
    const blocklist = [
        "aid",
        "bts",
        "category",
        "child",
        "chpg",
        "ctype",
        "dload",
        "gtemplate",
        "inf",
        "microtags",
        "parent",
        "path",
        "pos",
        "priority",
        "rtd_ias_id",
        "sbj",
        "stax",
        "tile"
    ];

    function getBlocklist() {
        return blocklist;
    }

    function logBlocklistedKeys(key) {
        window.debug.log(`Mntl.Targeting: Targeting key '${key}' was blocked from the ad call`);
    }

    function removeBlocklistedSlotTargeting(gptSlots) {
        gptSlots.forEach((slot) => {
            const slotLevelKeys = slot.getTargetingKeys();

            if (!slotLevelKeys) return;

            slotLevelKeys.forEach((key) => {
                if (blocklist.includes(key)) {
                    slot.clearTargeting(key);
    
                    logBlocklistedKeys(key);
                }
            });
        })
    }

    return {
        removeBlocklistedSlotTargeting,
        getBlocklist,
        logBlocklistedKeys
    };
})();
