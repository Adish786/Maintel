// Disabling object newline for import like syntax
/* eslint object-curly-newline: ["off"] */
const { copyAssetTask } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-assets.js');
const { cleanTask: clean } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-clean.js');
const { cssTask, createProjectVersionTask, minifyCSSTask, mobileCSSTask } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-css.js')
const { copyJSFlatlyTask, copyJSAsMinifiedTask, copyPrebidJS, webpackJSTask } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-js.js');
const { svgTask } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-svg.js');
const { lintJSTask } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-lint.js');
const { lintCSSTask } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-stylelint.js');
const { lintXMLTask } = require('@dotdash/mantle-frontend-build/gulp-tasks/gulp-xmllint.js');

const config = require('./gulp.config.js');

const gulp = require('gulp'); // eslint-disable-line no-unused-vars
const { parallel, series, watch } = gulp;

// Clean
const cleanAll = clean([config.staticResources]);
const cleanCSS = clean([`${config.staticResources}/**/*.css`]);
const cleanJS = clean([`${config.staticResources}/**/*.js`]);
const cleanSVG = clean([`${config.staticResources}/**/*.svg`]);

exports.clean = cleanAll;
exports.cleanCSS = cleanCSS;
exports.cleanJS = cleanJS;
exports.cleanSVG = cleanSVG;

// CSS
const css = cssTask(config.scssPaths, config.staticResources, config.cssIncludePaths);
const projectVersionCSS = createProjectVersionTask(config.cssIncludePaths[0]);
const mobileCSS = series(
    mobileCSSTask([`${config.staticResources}/css/**/!(*mobile|*.min).css`, `${config.staticResources}/components/**/!(*mobile|*.min).css`]),
    // Combining these doesn't output properly so not sure why???
    parallel(
        minifyCSSTask([`${config.staticResources}/css/**/*-mobile.css`, `!${config.staticResources}/css/**/*-mobile.min.css`], `${config.staticResources}/css`),
        minifyCSSTask([`${config.staticResources}/components/**/*-mobile.css`, `!${config.staticResources}/components/**/*-mobile.min.css`], `${config.staticResources}/components`)
    )
);

exports.css = series(
    cleanCSS,
    parallel(
        projectVersionCSS,
        css,
        mobileCSS
    )
);

// JS
const evalJS = copyJSAsMinifiedTask(config.evalJSPaths, config.staticResources);

const copyJS = parallel(
    copyAssetTask(config.jsPaths, config.staticResources),
    evalJS,
    copyJSAsMinifiedTask(config.libsJSAsMinfied, config.libsDestPath),
    copyPrebidJS
);
const copyVendorJS = copyJSFlatlyTask(config.copyLibJSPaths, config.libsDestPath);

const webpackJS = webpackJSTask({});

exports.js = parallel(copyJS, copyVendorJS, webpackJS);

// Lint
exports.lint = lintJSTask(config.lintJSPaths);
exports.xmlLint = lintXMLTask(`${config.resources}/**/*.xml`);

// Stylelint
exports.stylelint = lintCSSTask(config.lintCSSPaths, config.resources);

// SVG
const svg = svgTask(config.SVGPaths, config.staticResources);

exports.svg = series(cleanSVG, svg);

// Build
exports.build = series(
    cleanAll,
    parallel(
        copyJS,
        copyVendorJS,
        webpackJS,
        projectVersionCSS,
        css,
        mobileCSS,
        svg
    )
);

// Watch
exports.watch = function() {
    watch(config.scssPaths, css); // CSS
    watch(config.SVGPaths, svg); // SVG
    watch(config.evalJSPaths, evalJS); // Eval JS

    // JS
    const regularJS = watch(config.jsPaths); 

    // TODO: Once GLBE-9910 is complete setup the watch task for regular js to only pass in changed files
    regularJS.on('change', () => { 
        console.log('Triggering Webpack Task through the watch');
        webpackJS(); // When tasks are run on callbacks instead of gulp.src then we lose some of the console log messaging
        // Webpack is ran through the original webpack instead instead of gulp since we are on Webpack V5 and gulp-webpack is only on V3/V4
    }); 
}
