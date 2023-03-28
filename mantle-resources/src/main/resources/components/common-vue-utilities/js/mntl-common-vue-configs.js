window.Mntl = window.Mntl || {};

window.isLocal = window.location.host.indexOf('www.local') === 0;

Vue.config.devtools = window.isLocal || Mntl.DEBUG || false;
Vue.config.productionTip = window.isLocal|| Mntl.DEBUG || false;
