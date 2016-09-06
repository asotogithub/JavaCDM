'use strict';

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

  capabilities: {
    'browserstack.user' : 'calebpowell1',
    'browserstack.key' : 'Hkqnr8ubYK9SdMpMF7V5',

    // Needed for testing localhost
    'browserstack.local' : 'true',

    // Settings for the browser you want to test
    // (check docs for difference between `browser` and `browserName`
    'browserName' : 'Chrome',
    'browser_version' : '36.0',
    'loggingPrefs': {
      "driver": "ALL",
      "browser": "ALL"
    },
    'os' : 'OS X',
    'os_version' : 'Mavericks',
    'resolution' : '1280x1024',
    'chromeOptions': {
      args: ['--no-sandbox', '--test-type=browser'],
      prefs: {
        'download': {
          'prompt_for_download': false
        },
        'profile': {
          'default_content_settings': {'multiple-automatic-downloads': 1}
        },
        'safebrowsing': {
          'enabled': true
        }
      }
    }
  },

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
