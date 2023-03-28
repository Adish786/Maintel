// Constants
const MANTLE_COMMITED_JS_LIBS_PATH = 'mantle-resources/src/libs/js';
const MANTLE_RESOURCE_PATH = './mantle-resources/src/main/resources';

const config = {
    // CSS
    scssPaths: [`${MANTLE_RESOURCE_PATH}/**/*.scss`],
    cssIncludePaths: [`${MANTLE_RESOURCE_PATH}/css/`],
    // JS
    copyLibJSPaths: [
        'node_modules/@highlightjs/cdn-assets/highlight.js',
        'node_modules/@highlightjs/cdn-assets/highlight.min.js',
        'node_modules/a11y-dialog/dist/a11y-dialog.js',
        'node_modules/a11y-dialog/dist/a11y-dialog.min.js',
        'node_modules/autocompleter/autocomplete.js',
        'node_modules/autocompleter/autocomplete.min.js',
        'node_modules/jquery/dist/jquery.js',
        'node_modules/jquery/dist/jquery.min.js',
        'node_modules/jquery-lazyload/jquery.lazyload.js',
        'node_modules/jquery-lazyload/jquery.lazyload.min.js',
        'node_modules/lazysizes/lazysizes.js',
        'node_modules/lazysizes/lazysizes.min.js',
        'node_modules/lazysizes/plugins/bgset/ls.bgset.js',
        'node_modules/lazysizes/plugins/bgset/ls.bgset.min.js',
        'node_modules/markdown-it/dist/markdown-it.js',
        'node_modules/markdown-it/dist/markdown-it.min.js',
        'node_modules/picturefill/dist/picturefill.js',
        'node_modules/picturefill/dist/picturefill.min.js',
        'node_modules/querystring/dist/querystring.js',
        'node_modules/querystring/dist/querystring.min.js',
        'node_modules/sticky-kit/dist/sticky-kit.js',
        'node_modules/sticky-kit/dist/sticky-kit.min.js',
        'node_modules/stickybits/dist/stickybits.js',
        'node_modules/stickybits/dist/stickybits.min.js',
        'node_modules/vue/dist/vue.js',
        'node_modules/vue/dist/vue.min.js',
        'node_modules/vuex/dist/vuex.js',
        'node_modules/vuex/dist/vuex.min.js',
        `${MANTLE_COMMITED_JS_LIBS_PATH}/justified-columns.js`,
        `${MANTLE_COMMITED_JS_LIBS_PATH}/justified-columns.min.js`,
        `${MANTLE_COMMITED_JS_LIBS_PATH}/lodash-deepclone.js`,
        `${MANTLE_COMMITED_JS_LIBS_PATH}/lodash-deepclone.min.js`,
        `${MANTLE_COMMITED_JS_LIBS_PATH}/ResizeSensor.js`,
        `${MANTLE_COMMITED_JS_LIBS_PATH}/ResizeSensor.min.js`
    ],
    libsJSAsMinfied: ['node_modules/@auth0/auth0-spa-js/dist/auth0-spa-js.production.js'],
    evalJSPaths: [`${MANTLE_RESOURCE_PATH}/**/*.evaluated.js`],
    // General Resource Paths
    resources: MANTLE_RESOURCE_PATH,
    staticResources: `${MANTLE_RESOURCE_PATH}/static`
}

// CSS
config.cssPaths = [
    `${config.staticResources}/**/*.css`,
    `!${config.staticResources}/**/*.min.css`
]

// JS
config.jsPaths = [
    `${config.resources}/**/*.js`,
    `${config.resources}/components/vue-components/ugc/demo/ugc-vue-demo.json`,
    `!${config.resources}/components/vue-components/**/*.js`, // Exclude Vue
    `!${config.resources}/**/*.evaluated.js`, // Exclude Evaluated
    `!${config.staticResources}/**/*.js` // Exclude Static
]
config.libsDestPath = `${config.staticResources}/libs/js/`;

// Linting Configs
config.lintJSPaths = [
    `${config.resources}/**/*.js`,
    `!${config.resources}/**/vendor/**/*.js`, // Excluding vendor code
    `!${config.staticResources}/**/*.js`,
    `!${config.resources}/**/vue-components/**/*.js`,
    `!${config.resources}/**/*.evaluated.js` // Excluding evaluated files since we can't really lint freemarker js files
];

// Stylelint Configs
config.lintCSSPaths = [
    `${MANTLE_RESOURCE_PATH}/**/*.scss`
];

// SVG Paths
config.SVGPaths = [
    `${config.resources}/**/*.svg`,
    `!${config.staticResources}/**/*.svg`,
    `!${config.target}/**/*.svg`
];

module.exports = config;
