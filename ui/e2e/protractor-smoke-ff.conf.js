'use strict';
var q = require('q');
var FirefoxProfile = require('firefox-profile');

exports.config = {
  // The timeout for each script run on the browser. This should be longer
  // than the maximum time your application needs to stabilize between tasks.
  allScriptsTimeout: 90000,

  params: {
    'apiVersion': 'latest'
  },

  seleniumAddress: 'http://127.0.0.1:4444/wd/hub',

  baseUrl: 'http://localhost:' + (process.env.PORT || '9000'),

  // list of files / patterns to load in the browser
  suites: {
    smoke: './smoke/spec.js',
  },

  // Patterns to exclude.
  exclude: [],

  getMultiCapabilities: function() {
        var deferred = q.defer();

        var multiCapabilities = [];

        // Wait for a server to be ready or get capabilities asynchronously.
        setTimeout(function() {
            var firefoxProfile = new FirefoxProfile();
            firefoxProfile.setPreference("javascript.enabled", false);
            firefoxProfile.setPreference('browser.newtab.url', 'https://www.angularjs.org');
            firefoxProfile.setPreference('browser.download.folderList', 0);
            firefoxProfile.setPreference('browser.download.manager.showWhenStarting', false);
            firefoxProfile.setPreference('browser.download.manager.focusWhenStarting', false);
            firefoxProfile.setPreference('browser.download.useDownloadDir', true);
            firefoxProfile.setPreference('browser.helperApps.alwaysAsk.force', false);
            firefoxProfile.setPreference('browser.download.manager.alertOnEXEOpen', false);
            firefoxProfile.setPreference('browser.download.manager.closeWhenDone', true);
            firefoxProfile.setPreference('browser.download.manager.showAlertOnComplete', false);
            firefoxProfile.setPreference('browser.download.manager.useWindow', false);
            firefoxProfile.setPreference('browser.helperApps.neverAsk.saveToDisk', 'application/zip, image/gif');
            firefoxProfile.encoded(function (encodedProfile) {
                var capabilities = {
                    "browserName": "firefox",
                    "browserstack.user" : "calebpowell1",
                    "browserstack.key" : "Hkqnr8ubYK9SdMpMF7V5",
                    "browserstack.local" : "true",
                    "os" : "OS X",
                    "os_version" : "El Capitan",
                    "resolution" : "1280x1024",
                    "browserstack.debug" : "true",
                    "browser_version" : "46.0",
                    "firefox_profile": encodedProfile
                };
                multiCapabilities.push(capabilities);
                deferred.resolve(multiCapabilities);
            });
        }, 1000);

        return deferred.promise;
    },

  capabilities: {},

  env: {
    browserstack: {
      seleniumAddress: 'http://hub.browserstack.com/wd/hub',
      downloadDirectory: '/Users/test1/Downloads/',
      downloadDirectoryFirefox: '/Users/test1/Desktop/',
      downloadDirectoryWindowsFirefox: 'C:\\Users\\hello\\Desktop\\',
      downloadDirectoryWindowsChrome: 'C:\\Users\\hello\\Downloads\\'
    }
  },

  framework: 'jasmine',

  jasmineNodeOpts: {
    // If true, display spec names.
    isVerbose: true,
    // If true, print colors to the terminal.
    showColors: true,
    // If true, include stack traces in failures.
    includeStackTrace: true,
    // Default time to wait in ms before a test fails.
    defaultTimeoutInterval: 90000
  },

  onPrepare: function() {
    var HtmlReporter = require('protractor-html-screenshot-reporter'),
        bootstrap;

    global.logFiles = {
      protractorLogFilename: 'e2e/protractor.log',
      browserLogFilename: 'e2e/browser-console.log'
    };
    bootstrap = require('./utilities/bootstrap.spec');
    // Add a screenshot reporter and store screenshots to `/tmp/screnshots`:
    jasmine.getEnv().addReporter(new HtmlReporter({
      baseDirectory: 'e2e/tmp/screenshots'
      , takeScreenShotsOnlyForFailedSpecs: true
      , docName: 'report.html'
    }));
    require('jasmine-reporters');
    jasmine.getEnv().addReporter(
        new jasmine.JUnitXmlReporter('e2e/tmp/xmloutput', true, true)
    );
    browser.driver.manage().window().setSize(1280,1024);
  }

};
