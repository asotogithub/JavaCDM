'use strict';

var Bootstrap = function() {
    var request = require('request-promise'),
        apiUsername = 'api@trueforce.com',
        apiPassword = 'api',
        userAgent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36',
        page = require('../page-object/login.po'),
        logger = require('../utilities/log-to-file.spec.js').getLogger('bootstrap'),
        login = require('../utilities/login.spec'),
        advertiserId,
        agencyId,
        brandId,
        cookieDomainId,
        publisherId,
        siteId,
        siteSectionId,
        sizeId,
        stringCookieJson = {
            'cookieName' : 'te_new',
            'cookieContentType' : 1,
            'cookieDomainId' : cookieDomainId,
            'logicalDelete' : 'N'
        },
        numericalCookieJson = {
            'cookieName' : 'cp_type',
            'cookieContentType' : 3,
            'cookieDomainId' : cookieDomainId,
            'logicalDelete' : 'N'
        },
        listCookieJson = {
            'contentPossibleValues': 'value1`value2`value3',
            'cookieName' : 'newcookie',
            'cookieContentType' : 2,
            'cookieDomainId' : cookieDomainId,
            'logicalDelete' : 'N'
        },
        userCredentials = {
            userName : '',
            userPassword : '',
            token : '',
            refreshToken: '',
            apiToken: '',
            apiRefreshToken: ''
        };

    function getOauthUrl(){
        var url;
        if(browser.baseUrl == 'http://localhost:9000'){
            url = 'http://localhost:8080/oauth';
            return url
        }
        else{
            url = browser.baseUrl + '/oauth/' + browser.params.apiVersion;
            return url
        }
    };

    function getCmsUrl(){
        var url;
        if(browser.baseUrl == 'http://localhost:9000'){
            url = 'http://localhost:8080/cms';
            return url
        }
        else{
            url = browser.baseUrl + '/cms/' + browser.params.apiVersion;
            return url
        }
    };

    function getCookieDomain(){
        var cookieDomain;
        if(browser.baseUrl == 'https://my-stg.trueffect.com'){
            cookieDomain = 'ext.adlegend.net';
            return cookieDomain
        }
        else{
            cookieDomain = 'extdev.adlegend.net';
            return cookieDomain
        }
    };

    function setUserCredentials(user, password, token, refreshToken){
        userCredentials.userName = user;
        userCredentials.userPassword = password;
        userCredentials.token = token;
        userCredentials.refreshToken = refreshToken;
        logger.info('Token: ' + userCredentials.token);
        logger.info('Refresh Token: ' + userCredentials.refreshToken);
    };

    function teardown(token, agencyId){
        request({
            method: 'DELETE',
            url: getCmsUrl() + '/Agencies/' + agencyId + '/physical',
            headers: {
                'Content-type': 'application/json',
                'authorization': 'Bearer ' + token,
            },
            json: true
        })
        .then(function (body) {
            logout();
            logger.info('TEARDOWN BODY: ', body)
        })
        .catch(function (err) {
            logger.error(err);
        });
    };

    this.getUserCredentials = function(){
        return userCredentials;
    };

    this.getIds = function(){
        return {
            advertiserId: advertiserId,
            agencyId: agencyId,
            brandId: brandId,
            cookieDomainId: cookieDomainId,
            publisherId: publisherId,
            siteId: siteId,
            siteSectionId: siteSectionId,
            sizeId: sizeId
        };
    };

    this.refreshToken = function(callback, isUserToken) {
        var requestToken = isUserToken ? userCredentials.refreshToken : userCredentials.apiRefreshToken;

        request({
            method: 'POST',
            url: getOauthUrl() + '/refreshaccesstoken',
            headers: {
                'Authorization': 'Bearer ' + requestToken
            },
            json: true
        })
            .then(function (body) {
                if(isUserToken) {
                    userCredentials.token = body.accessToken;
                    userCredentials.refreshToken = body.refreshToken;
                }
                else {
                    userCredentials.apiToken = body.accessToken;
                    userCredentials.apiRefreshToken = body.refreshToken;
                }
                logger.info('Token: ' + userCredentials.apiToken);
                logger.info('Refresh Token: ', userCredentials.apiRefreshToken);
                logger.info('Token Expires in', body.expiresIn);
                if(callback !== undefined) {
                    callback();
                }
            })
            .catch(function (err) {
                logger.error(err);
            });
    };

    this.newAgencyLogin = function(){
        
        request({
            method: 'POST',
            url: getOauthUrl() + '/accesstoken',
            headers: {
                'Authorization': 'Basic ' + new Buffer(apiUsername + ':' + apiPassword).toString('base64'),
                'User-Agent': userAgent
            },
            json: true
        })
        .then(function (body) {
            userCredentials.apiToken = body.accessToken;
            userCredentials.apiRefreshToken = body.refreshToken;
            logger.info('API Token: ' + userCredentials.apiToken);
            logger.info('API Refresh Token: ', userCredentials.apiRefreshToken);
            logger.info('API Token Expires in', body.expiresIn);
            callBootstrap(userCredentials.apiToken);
        })
        .catch(function (err) {
            logger.error(err);
        });

        var callBootstrap = function(apiToken){
            request({
                method: 'POST',
                url: getCmsUrl() + '/Bootstrap',
                headers: {
                    'Authorization': 'Basic ' + new Buffer(apiUsername + ':' + apiPassword).toString('base64'),
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + apiToken
                },
                json: true
            })
            .then(function (body) {
                logger.info('UserId: ', body.userId);
                logger.info('Password: ', body.userPassword);
                logger.info('AgencyId: ', body.agencyId);
                logger.info('Url with API Version: ', getCmsUrl());
                agencyId = body.agencyId;
                newUserAuth(body.userId, body.userPassword, agencyId);
            })
            .catch(function (err) {
                logger.error(err);
            });
        };


        var newUserAuth = function(userId, password, agencyId){
            request({
                method: 'POST',
                url: getOauthUrl() + '/accesstoken',
                headers: {
                    'Authorization': 'Basic ' + new Buffer(userId + ':' + password).toString("base64"),
                    'User-Agent': userAgent
                },
                json: true
            })
            .then(function (body) {
                setUserCredentials(userId, password, body.accessToken, body.refreshToken);
                callBasicBootstrap(body.accessToken, userId, password, agencyId);
                callAddSize(body.accessToken, agencyId);
            })
            .catch(function (err) {
                logger.error(err);
            });
        };

        var callBasicBootstrap = function(token, userId, password, agencyId){
            request({
                method: 'POST',
                url: getCmsUrl() + '/Bootstrap/' + agencyId + '/basic',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token,
                },
                json: true
            })
            .then(function (body) {
                callAddCookieDomain(token, agencyId);
                callAddPublisher(token, agencyId);
            })
            .catch(function (err) {
                logger.error(err);
            });
        };

        var callAddCookieDomain = function(token, agencyId){
            request({
                method: 'POST',
                url: getCmsUrl() + '/CookieDomains',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token,
                },
                body: {
                  'createdTpwsKey' : '000000',
                  'modifiedDate' : '2015-06-17T12:46:57-07:00',
                  'domain' :  getCookieDomain(),
                  'createdDate' : '2015-06-17T12:46:57-07:00',
                  'logicalDelete' : 'N',
                  'agencyId' : agencyId,
                  'modifiedTpwsKey' : '0000000'
                },
                json: true
            })
            .then(function (body) {
                  logger.info('new cookie domain id: ', body.id);
                  cookieDomainId = body.id;
                  callAddCookieTarget(token, cookieDomainId, stringCookieJson);
                  callAddCookieTarget(token, cookieDomainId, numericalCookieJson);
                  callAddCookieTarget(token, cookieDomainId, listCookieJson);
            })
            .catch(function (err) {
                logger.error(err);
            });
        };

        var callAddCookieTarget = function(token, cookieDomainId, jsonBody){
            request({
                method: 'POST',
                url: getCmsUrl() + '/CookieDomains/' + cookieDomainId + '/cookie',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token,
                },
                body: jsonBody,
                json: true
            })
            .then(function (body) {
                  logger.info('cookie target template id: ', body.cookieTargetTemplateId);
            })
            .catch(function (err) {
                logger.error(err);
            });
        };

        var callAddPublisher = function(token, agencyId){
            request({
                method: 'POST',
                url: getCmsUrl() + '/Publishers',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token,
                },
                body: {
                    'agencyId': agencyId,
                    'name': 'Protractor Publisher',
                    'zipCode': '00000'
                },
                json: true
            })
            .then(function (body) {
                  logger.info('publisher Id : ', body.id);
                  publisherId = body.id;
                  callAddSite(token, publisherId)
            })
            .catch(function (err) {
                logger.error(err);
            });
        };

        var callAddSite = function(token, publisherId){
            request({
                method: 'POST',
                url: getCmsUrl() + '/Sites',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token,
                },
                body: {
                    'acceptsFlash': 'N',
                    'clickTrack': 'N',
                    'encode': 'N',
                    'name': 'Protractor Site',
                    'publisherId': publisherId,
                    'publisherName': 'Protractor Publisher',
                    'richMedia': 'N',
                    'preferredTag': 'IFRAME'
                },
                json: true
            })
            .then(function (body) {
                  logger.info('site Id : ', body.id);
                  siteId = body.id;
                  callAddSiteSection(token, siteId)
            })
            .catch(function (err) {
                logger.error(err);
            });
        };

        var callAddSiteSection = function(token, siteId){
            request({
                method: 'POST',
                url: getCmsUrl() + '/SiteSections',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token,
                },
                body: {
                    'createdTpwsKey': '0000',
                    'modifiedTpwsKey': '0000',
                    'name': 'Home',
                    'siteId': siteId
                },
                json: true
            })
            .then(function (body) {
                  logger.info('site section Id : ', body.id);
                  siteSectionId = body.id;
                  callAdvertisers(token, agencyId);
            })
            .catch(function (err) {
                logger.error(err);
            });
        };

        var callAddSize = function(token, agencyId){
            request({
                method: 'POST',
                url: getCmsUrl() + '/Sizes',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token,
                },
                body: {
                    'agencyId': agencyId,
                    'height': '250',
                    'width': '350',
                    'label': '350x250'
                },
                json: true
            })
            .then(function (body) {
                logger.info('size Id : ', body.id)
                sizeId = body.id;
            })
            .catch(function (err) {
                logger.error(err);
            });
        };



        var callAdvertisers = function(token, agencyId){
            request({
                method: 'GET',
                url: getCmsUrl() + '/Advertisers',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token
                },
                json: true
            })
                .then(function (body) {
                    logger.info('advertiser Id : ', body.records[0].Advertiser[0].id);
                    advertiserId = body.records[0].Advertiser[0].id;
                    callBrands(token, agencyId);
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var callBrands = function(token, agencyId){
            request({
                method: 'GET',
                url: getCmsUrl() + '/Brands',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + token
                },
                json: true
            })
                .then(function (body) {
                    logger.info('brand Id : ', body.records[0].Brand[0].id);
                    brandId = body.records[0].Brand[0].id;
                    page.usernameEl.clear();
                    page.usernameEl.sendKeys(userCredentials.userName);
                    page.passwordEl.clear();
                    page.passwordEl.sendKeys(userCredentials.userPassword);
                    page.submitButton.click();
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };
    };
};

Bootstrap.instance = null;

Bootstrap.getInstance = function(){
    if(this.instance === null){
        this.instance = new Bootstrap();
    }
    return this.instance;
}

module.exports = Bootstrap.getInstance();
