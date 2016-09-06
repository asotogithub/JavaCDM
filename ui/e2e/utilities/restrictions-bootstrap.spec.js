'use strict';

var restrictionsBootstrap = function(){
    var advertiserIdentifiers = [],
        bootstrapData,
        campaignsBootstrap = require('../utilities/campaigns-bootstrap.spec'),
        fs = require('fs'),
        functionParam,
        isUserToken = false,
        logger = require('../utilities/log-to-file.spec.js').getLogger('restrictions-bootstrap'),
        numberOfAdvertisers,
        path = require('path'),
        queuedFunction,
        request = require('request-promise'),
        userCredentials;

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

    function addUserRestriction(limitUser) {
        request({
            method: 'PUT',
            url: getCmsUrl() + '/Users/' + userCredentials.userName + '/limit',
            headers: {
                'Content-type': 'application/json',
                'authorization': 'Bearer ' + userCredentials.apiToken
            },
            body: {
                'limitAdvertisers': limitUser ? 'Y' : 'N',
                'userName': userCredentials.userName
            },
            json: true
        }).then(function () {
            global.restrictions.limitUser = true && limitUser;
        }).catch(function (err) {
            if(err.statusCode === 303) {
                global.restrictions.limitUser = true && limitUser;
            }
            else {
                if(err.statusCode === 401) {
                    isUserToken = false;
                    functionParam = limitUser;
                    queuedFunction = addUserRestriction;
                    campaignsBootstrap.getParentBootstrap().refreshToken(refreshToken, isUserToken);
                }
                else {
                    logger.error(err);
                }
            }
        });
    };

    function setUserAdvertiser() {
        request({
            method: 'PUT',
            url: getCmsUrl() + '/Users/' + userCredentials.userName + '/userAdvertisers',
            headers: {
                'Content-type': 'application/json',
                'authorization': 'Bearer ' + userCredentials.token
            },
            body: {
                'records': [
                    {
                        'UserAdvertiser': [
                            {
                                'userId': userCredentials.userName,
                                'advertiserId': advertiserIdentifiers[0]
                            }
                        ]
                    }
                ]
            },
            json: true
        }).then(function () {
            global.restrictions.setAdvertisers = true;
        }).catch(function (err) {
            if(err.statusCode === 401) {
                isUserToken = true;
                functionParam = undefined;
                queuedFunction = setUserAdvertiser;
                campaignsBootstrap.getParentBootstrap().refreshToken(refreshToken, isUserToken);
            }
            else {
                logger.error(err);
            }
        });
    };

    function createCampaigns() {
        var arrayIndex = numberOfAdvertisers - 1;
        campaignsBootstrap.setupCampaignData(bootstrapData.advertiserNames[arrayIndex],
                                                bootstrapData.brandNames[arrayIndex],
                                                bootstrapData.campaignNames[arrayIndex], enableRestrictionsTest);
    }

    function enableRestrictionsTest() {
        numberOfAdvertisers--;

        if(numberOfAdvertisers === 0) {
            global.restrictions.runRestrictions = true;
        }
        else {
            advertiserIdentifiers.push(campaignsBootstrap.getAdvertiserId());
            createCampaigns();
        }
    };

    function refreshToken() {
        userCredentials = campaignsBootstrap.getUserCredentials();
        queuedFunction(functionParam);
    };

    this.getUserCredentials = function() {
        return campaignsBootstrap.getUserCredentials();
    };

    this.setupData = function (bootstrapDataParam) {
        userCredentials = campaignsBootstrap.getUserCredentials();
        bootstrapData = bootstrapDataParam;
        numberOfAdvertisers = bootstrapData.advertiserNames.length;
        createCampaigns();
    };

    this.limitUser = function(limitUser) {
        addUserRestriction(limitUser);
    };

    this.setAdvertiser = function() {
        setUserAdvertiser();
    };
};

module.exports = new restrictionsBootstrap();