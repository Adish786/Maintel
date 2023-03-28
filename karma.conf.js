const karmaConfig = require('./node_modules/@dotdash/mantle-dev-deps/test-configs/default-karma.config.js');

module.exports = function(config) {
    karmaConfig.coverageReporter.dir = '/mantle-resources/test';
    karmaConfig.files = [
        {
            pattern: 'mantle-resources/src/main/resources/static/**/*.js',
            included: false
        },
        'node_modules/jquery/dist/jquery.js',
        'mantle-resources/src/main/resources/js/vendor/throttle.js',
        'node_modules/querystring/dist/querystring.js',
        'mantle-resources/src/main/resources/js/fn-utilities.js',
        'mantle-resources/src/main/resources/js/dom-utilities.js',
        'mantle-resources/src/main/resources/js/utilities.js',
        'mantle-resources/src/main/resources/js/utilities-built-with-jquery.js',
        'mantle-resources/src/main/resources/components/internal/defer/js/deferred.js',
        'mantle-resources/src/main/resources/js/cookies.js',
        'mantle-resources/src/main/resources/js/GPT.js',
        'mantle-resources/src/main/resources/components/ads/js/unique-slot-size.js',
        'mantle-resources/src/main/resources/components/ads/js/targeting-safelist.js',
        'mantle-resources/test/spec/fixtures/**/*.html',
        'mantle-resources/test/**/*.spec.js'
    ]
    karmaConfig.logLevel = config.LOG_INFO;

    config.set(karmaConfig);
}