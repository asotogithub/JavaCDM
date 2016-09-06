// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function(config) {
    var mkdirp = require('mkdirp');
    mkdirp("test-results", function(err) {
        if(err) console.error(err);
    });
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: '',

        // testing framework to use (jasmine/mocha/qunit/...)
        frameworks: ['jasmine'],

        // list of files / patterns to load in the browser
        files: [
            // bower:js
            'client/bower_components/es5-shim/es5-shim.js',
            'client/bower_components/jquery/dist/jquery.js',
            'client/bower_components/angular/angular.js',
            'client/bower_components/angular-base64/angular-base64.js',
            'client/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'client/bower_components/angular-ui-router/release/angular-ui-router.js',
            'client/bower_components/angular-breadcrumb/release/angular-breadcrumb.js',
            'client/bower_components/angular-animate/angular-animate.js',
            'client/bower_components/angular-busy/dist/angular-busy.js',
            'client/bower_components/chosen/chosen.jquery.js',
            'client/bower_components/angular-chosen-localytics/chosen.js',
            'client/bower_components/angular-cookies/angular-cookies.js',
            'client/bower_components/bootstrap/dist/js/bootstrap.js',
            'client/bower_components/angular-sanitize/angular-sanitize.js',
            'client/bower_components/angular-translate/angular-translate.js',
            'client/bower_components/angular-dialog-service/dist/dialogs.min.js',
            'client/bower_components/angular-dialog-service/dist/dialogs-default-translations.min.js',
            'client/bower_components/angular-growl-v2/build/angular-growl.js',
            'client/bower_components/angular-loggly-logger/angular-loggly-logger.js',
            'client/bower_components/moment/moment.js',
            'client/bower_components/angular-momentjs/angular-momentjs.js',
            'client/bower_components/angular-percentage-filter/percentage.js',
            'client/bower_components/angular-resource/angular-resource.js',
            'client/bower_components/angular-uuid/angular-uuid.js',
            'client/bower_components/bootstrap-growl-injectable/jquery.bootstrap-growl.js',
            'client/bower_components/dndLists/angular-drag-and-drop-lists.js',
            'client/bower_components/highcharts/highcharts.js',
            'client/bower_components/highcharts/highcharts-more.js',
            'client/bower_components/highcharts/modules/exporting.js',
            'client/bower_components/highcharts-ng/dist/highcharts-ng.js',
            'client/bower_components/jquery-extendext/jQuery.extendext.js',
            'client/bower_components/jQuery-QueryBuilder/dist/js/query-builder.js',
            'client/bower_components/jquery-ui/jquery-ui.js',
            'client/bower_components/jquery.inputmask/dist/inputmask/jquery.inputmask.js',
            'client/bower_components/jquery.inputmask/dist/inputmask/jquery.inputmask.extensions.js',
            'client/bower_components/jquery.inputmask/dist/inputmask/jquery.inputmask.date.extensions.js',
            'client/bower_components/jquery.inputmask/dist/inputmask/jquery.inputmask.numeric.extensions.js',
            'client/bower_components/jquery.inputmask/dist/inputmask/jquery.inputmask.phone.extensions.js',
            'client/bower_components/jquery.inputmask/dist/inputmask/jquery.inputmask.regex.extensions.js',
            'client/bower_components/jqwidgets/jqwidgets/jqxcore.js',
            'client/bower_components/jqwidgets/jqwidgets/jqx-all.js',
            'client/bower_components/json3/lib/json3.js',
            'client/bower_components/ng-file-upload/ng-file-upload.js',
            'client/bower_components/ng-lodash/build/ng-lodash.js',
            'client/bower_components/ng-table/dist/ng-table.min.js',
            'client/bower_components/ng-tags-input/ng-tags-input.min.js',
            'client/bower_components/ngStorage/src/angularLocalStorage.js',
            'client/bower_components/sql-parser/browser/sql-parser.js',
            'client/bower_components/angular-mocks/angular-mocks.js',
            'client/bower_components/jasmine-promise-matchers/dist/jasmine-promise-matchers.js',
            'client/bower_components/lodash/lodash.js',
            // endbower
            'client/vendor/angle/**/*.module.js',
            'client/vendor/angular-http-auth/**/*.js',
            'client/app/app.js',
            'client/app/app.coffee',
            'client/app/**/*.js',
            'client/app/**/*.coffee',
            'client/components/**/*.js',
            'client/components/**/*.coffee',
            'client/app/**/*.jade',
            'client/components/**/*.jade',
            'client/app/**/*.html',
            'client/components/**/*.html'
        ],

        preprocessors: {
            '**/*.jade': 'ng-jade2js',
            '**/*.html': 'html2js',
            '**/*.coffee': 'coffee'
        },

        ngHtml2JsPreprocessor: {
            stripPrefix: 'client/'
        },

        ngJade2JsPreprocessor: {
            stripPrefix: 'client/'
        },

        // list of files / patterns to exclude
        exclude: require('./excluded.karma.conf.js'),

        // web server port
        port: 8087,

        // level of logging
        // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,

        // Start these browsers, currently available:
        // - Chrome
        // - ChromeCanary
        // - Firefox
        // - Opera
        // - Safari (only Mac)
        // - PhantomJS
        // - IE (only Windows)
        browsers: ['PhantomJS'],

        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: false,

        // Reporter configuration
        reporters: ['progress', 'junit'],
        junitReporter: {
            outputFile: 'test-results/karma-results.xml',
            suite: ''
        },

        // How long will Karma wait for a message from a browser before disconnecting from it(in ms).
        browserNoActivityTimeout: 60000
    });
};
