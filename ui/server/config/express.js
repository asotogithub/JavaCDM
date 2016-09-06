/**
 * Express configuration
 */

'use strict';

var express = require('express');
var favicon = require('serve-favicon');
var morgan = require('morgan');
var compression = require('compression');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');
var cookieParser = require('cookie-parser');
var errorHandler = require('errorhandler');
var path = require('path');
var config = require('./environment');
var _ = require('lodash');

module.exports = function (app) {
    var env = app.get('env');

    app.set('views', config.root + '/server/views');
    app.engine('html', require('ejs').renderFile);
    app.set('view engine', 'html');
    app.use(compression());
    app.use(bodyParser.urlencoded({extended: false}));
    app.use(bodyParser.json());
    app.use(methodOverride());
    app.use(cookieParser());
    app.use('/templates', express.static(path.join(config.root, 'server/templates')));

    if ('production' === env) {
        app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
        app.use(express.static(path.join(config.root, 'public')));
        app.set('appPath', config.root + '/public');
        app.use(morgan('dev'));
    }

    if ('development' === env || 'test' === env) {
        app.use(require('connect-livereload')());
        app.use(express.static(path.join(config.root, '.tmp')));
        app.use(express.static(path.join(config.root, 'client')));
        app.set('appPath', 'client');
        app.use(morgan('dev'));
        app.use(errorHandler()); // Error handler - has to be last
    }
    // Provides Environment specific settings to UI
    app.get('/app/environment.js', function (req, res) {
        var env = process.env;
        var host = env.API_HOST || 'http://localhost:8080';

        var publicContext = _.isUndefined(env.PUBLIC_CONTEXT) ? 'cms' : env.PUBLIC_CONTEXT;
        var oauthContext = _.isUndefined(env.OAUTH_CONTEXT) ? 'oauth' : env.OAUTH_CONTEXT;
        var apiVersion = _.isUndefined(env.API_VERSION) ? '' : env.API_VERSION;
        var measurementUrl = _.isUndefined(env.MEASUREMENT_URL) ? 'https://measurement.trueffect.com' : env.MEASUREMENT_URL;
        var deployEnv = _.isUndefined(env.DEPLOY_ENV) ? 'local' : env.DEPLOY_ENV;
        var logglyLevel = _.isUndefined(env.LOGGLY_LEVEL) ? 'OFF' : env.LOGGLY_LEVEL;
        if (!validURI(host)) {
            res.status(500).send('Internal Server Error: Wrong API_HOST provided');
        } else {
            res.type('.js');
            res.set('Cache-Control', 'no-cache');
            res.render('../views/environment.ejs', {
                API_HOST: host,
                PUBLIC_CONTEXT: publicContext,
                OAUTH_CONTEXT: oauthContext,
                API_VERSION: apiVersion,
                MEASUREMENT_URL: measurementUrl,
                DEPLOY_ENV: deployEnv,
                LOGGLY_LEVEL: logglyLevel
            });
        }
    });

    /**
     * Checks in the URI has valid format
     * @param s The provided URI
     * @returns {boolean} True when the URI is valid.
     */
    function validURI(s) {
        var regexp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
        return regexp.test(s);
    }
};
