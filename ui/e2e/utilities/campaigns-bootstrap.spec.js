'use strict';

var campaignsBootstrap = function(){
    var advertiserFilterId,
        brandFilterId,
        campaignFilterId,
        commonBootstrap = require('../utilities/bootstrap.spec'),
        fs = require('fs'),
        generatedIds,
        logger = require('../utilities/log-to-file.spec.js').getLogger('campaigns-bootstrap'),
        path = require('path'),
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

    this.getUserCredentials = function() {
        return commonBootstrap.getUserCredentials();
    };

    this.getParentBootstrap = function() {
        return commonBootstrap;
    };

    this.getAdvertiserId = function() {
        return advertiserFilterId;
    };

    this.setupCampaignData = function (advertiserName, brandName, campaignName, callback) {
        userCredentials = commonBootstrap.getUserCredentials();
        generatedIds = commonBootstrap.getIds();

        request({
            method: 'POST',
            url: getCmsUrl() + '/Advertisers',
            headers: {
                'Content-type': 'application/json',
                'authorization': 'Bearer ' + userCredentials.token
            },
            body: {
                'address1': '01 Protractor',
                'address2': '02 Protractor',
                'agencyId': generatedIds.agencyId,
                'city': 'ProCity',
                'contactDefault': 'protractor@hotmail.com',
                'country': 'Japan',
                'enableHtmlTag': 0,
                'faxNumber': '983213213213',
                'isHidden': 'N',
                'name': advertiserName,
                'notes': 'Protractor Notes',
                'phoneNumber': '32143242341',
                'state': 'NY',
                'url': 'www.hello.info',
                'zipCode': '123421-1234'
            },
            json: true
        }).then(function (body) {
            logger.info('advertiser 2 : ', body.id);
            advertiserFilterId = body.id;
            createBrand();
        }).catch(function (err) {
            logger.error(err);
        });

        var createBrand = function () {
            request({
                method: 'POST',
                url: getCmsUrl() + '/Brands',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'advertiserId': advertiserFilterId,
                    'description': 'Description for protractor',
                    'isHidden': 'N',
                    'name': brandName
                },
                json: true
            })
                .then(function (body) {
                    logger.info('brand 2 : ', body.id);
                    brandFilterId = body.id;
                    createCampaign();
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var createCampaign = function () {
            request({
                method: 'POST',
                url: getCmsUrl() + '/Campaigns',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'advertiserId': advertiserFilterId,
                    'agencyId': generatedIds.agencyId,
                    'brandId': brandFilterId,
                    'cookieDomainId': generatedIds.cookieDomainId,
                    'description': '',
                    'endDate': '2050-03-02T23:59:59-07:00',
                    'isActive': 'Y',
                    'isHidden': 'N',
                    'logicalDelete': 'N',
                    'name': campaignName,
                    'overallBudget': '50000.00',
                    'startDate': '2015-02-03T00:00:00-07:00'
                },
                json: true
            }).then(function (body) {
                logger.info('campaign \'', campaignName, '\' Id : ', body.id);
                campaignFilterId = body.id;
                setCampaignInactive(body);
            }).catch(function (err) {
                logger.error(err);
            });
        };

        var setCampaignInactive = function (campaign) {
            campaign.isActive = 'N';
            request({
                method: 'PUT',
                url: getCmsUrl() + '/Campaigns/' + campaign.id,
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: campaign,
                json: true
            }).then(function (body) {
                logger.info('set campaign \'', campaignName, '\' to inactive');
                if(callback != undefined) {
                    callback();
                }
            }).catch(function (err) {
                logger.error(err);
            });
        };
    };
};

module.exports = new campaignsBootstrap();