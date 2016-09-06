'use strict';

var path = require('path');

exports.config = {
  // The timeout for each script run on the browser. This should be longer
  // than the maximum time your application needs to stabilize between tasks.
  allScriptsTimeout: 90000,

  params: {
    'apiVersion': 'latest'
  },

  seleniumAddress: 'http://127.0.0.1:4444/wd/hub',

  baseUrl: 'http://localhost:' + (process.env.PORT || '9000'),
  // baseUrl: 'http://coredrill.trueffect.com',

  // list of files / patterns to load in the browser
  suites: {
    campaigns: './campaigns/spec.js',
    creativeGroups: './creative-groups/spec.js',
    media: './media/spec.js',
    creative: './creatives/spec.js',
    navigation: './navigation/spec.js',
    schedule: './schedule/spec.js',
    siteMeasurements: './site-measurements/spec.js',
    adm: './adm/spec.js',
    tagInjection: './tag-injection/spec.js',
    tags: './tags/spec.js',
    login: './login/spec.js',
    metrics: './metrics/spec.js',
    status: './status/spec.js',
    restrictions: './restrictions/spec.js'
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
          'prompt_for_download': false,
          'directory_upgrade': true,
          'default_directory': path.resolve(__dirname, '../e2e/tmp/')
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
      downloadDirectory: '/Users/test1/Downloads/'
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
        loginPage = require('./page-object/login.po'),
        bootstrap;

    global.countCampaigns = 0;
    global.logFiles = {
      protractorLogFilename: 'e2e/protractor.log',
      browserLogFilename: 'e2e/browser-console.log'
    };
    global.runSchedule = false;
    global.runTagInjection = false;
    global.restrictions = {
      runRestrictions: false,
      limitUser: false,
      setAdvertisers: false
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

    describe('Protractor Regression Suite', function() {
      it('Create new Agency and log in', function() {
        browser.get('/');
        browser.wait(function() {
            return loginPage.submitButton.isPresent();
        }).then(function(){
            bootstrap.newAgencyLogin();
        });
      });
    });
  },

  onComplete: function () {
    var log4js = require('log4js'),
        logger;

    log4js.configure({
      appenders: [
        {type: 'file', filename: global.logFiles.browserLogFilename, category: 'browser'}
      ]
    });

    logger = log4js.getLogger('browser');
    logger.setLevel('INFO');

    browser.manage().logs().get('browser').then(function (browserLog) {
      if (browserLog.length) {
        logger.info(browserLog);
      }
    });
  }
};
