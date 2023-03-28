export default {
    bind(el, binding, vnode) {
        el.clickOutsideEvent = function (event) {
            // if the click was outside the element
            if (!(el == event.target || el.contains(event.target))) {
                // call the method provided to this directive
                vnode.context[binding.expression](event);
            }
        };
        document.body.addEventListener("click", el.clickOutsideEvent);
    },
    unbind(el) {
        document.body.removeEventListener("click", el.clickOutsideEvent);
    }
};
