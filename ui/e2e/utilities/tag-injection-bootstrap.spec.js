'use strict';

var tagInjection = function() {
    var campaignId,
        commonBootstrap = require('../utilities/bootstrap.spec'),
        fs = require('fs'),
        generatedIds,
        ioId,
        logger = require('../utilities/log-to-file.spec.js').getLogger('bootstrap'),
        mediaBuyId,
        path = require('path'),
        request = require('request-promise'),
        runAddNewTag = false,
        userCredentials;

    function getCmsUrl() {
        var url;
        if (browser.baseUrl == 'http://localhost:9000') {
            url = 'http://localhost:8080/cms';
            return url
        }
        else {
            url = browser.baseUrl + '/cms/' + browser.params.apiVersion;
            return url
        }
    }

    this.getUserCredentials = function () {
        return commonBootstrap.getUserCredentials();
    };

    this.setupCreativeGroupData = function (campaignName, io, placement, newTagInjection) {
        userCredentials = commonBootstrap.getUserCredentials();
        generatedIds = commonBootstrap.getIds();
        //Create Campaign
        request({
            method: 'POST',
            url: getCmsUrl() + '/Campaigns',
            headers: {
                'Content-type': 'application/json',
                'authorization': 'Bearer ' + userCredentials.token
            },
            body: {
                'advertiserId': generatedIds.advertiserId,
                'agencyId': generatedIds.agencyId,
                'brandId': generatedIds.brandId,
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
            campaignId = body.id;
            getMediaBuy();
        }).catch(function (err) {
            logger.error(err);
        });


        //Create IO
        var getMediaBuy = function () {
            request({
                method: 'GET',
                url: getCmsUrl() + '/MediaBuys/byCampaign/' + campaignId,
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                json: true
            })
                .then(function (body) {
                    logger.info('mediaBuy Id : ', body.id);
                    mediaBuyId = body.id;
                    callCreateIO();
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var callCreateIO = function () {
            request({
                method: 'POST',
                url: getCmsUrl() + '/InsertionOrders',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'ioNumber': io.number,
                    'logicalDelete': 'N',
                    'mediaBuyId': mediaBuyId,
                    'name': io.name,
                    'notes': 'Created for Protractor test',
                    'status': 'Accepted'
                },
                json: true
            })
                .then(function (body) {
                    logger.info('IO Id : ', body.id);
                    ioId = body.id;
                    createPlacement();
                    //getDefaultCreativeGroup();
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        //Create Placement
        var createPlacement = function () {
            request({
                method: 'GET',
                url: getCmsUrl() + '/Sizes/' + generatedIds.sizeId,
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                json: true
            })
                .then(function (body) {
                    logger.info('size Id : ', body.id);
                    callCreatePlacement(body);
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var callCreatePlacement = function (size) {
            request({
                method: 'POST',
                url: getCmsUrl() + '/InsertionOrders/' + ioId + '/bulkPackagePlacement',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'packages': [],
                    'placements': [
                        {
                            'adSpend': 50,
                            'campaignId': campaignId,
                            'costDetails': [
                                {
                                    'countryCurrencyId': 1,
                                    'endDate': '2041-01-01T23:59:59-07:00',
                                    'plannedGrossAdSpend': 50,
                                    'plannedGrossRate': 1,
                                    'rateType': 4,
                                    'startDate': '2040-01-01T00:00:00-07:00'
                                }
                            ],
                            'countryCurrencyId': 1,
                            'customKey': 0,
                            'endDate': '2041-01-02T03:59:59.000Z',
                            'endDateOpened': false,
                            'getDate': '2041-01-02T03:59:59.000Z',
                            'height': size.height,
                            'inventory': '50000',
                            'ioId': ioId,
                            'isChild': false,
                            'name': placement[0].name,
                            'packageName': '',
                            'rate': 1,
                            'rateType': {
                                'KEY': 'CPM',
                                'NAME': 'placement.rateTypeList.cpm',
                                'VALUE': 4
                            },
                            'sectionName': 'Home',
                            'siteId': generatedIds.siteId,
                            'siteName': 'Protractor Site',
                            'siteSectionId': generatedIds.siteSectionId,
                            'sizeId': size.id,
                            'sizeName': size.label,
                            'startDate': '2040-01-01T04:00:00.000Z',
                            'startDateOpened': false,
                            'status': 'New',
                            'utcOffset': 0,
                            'width': size.width
                        },
                        {
                            'adSpend': 50,
                            'campaignId': campaignId,
                            'costDetails': [
                                {
                                    'countryCurrencyId': 1,
                                    'endDate': '2041-01-01T23:59:59-07:00',
                                    'plannedGrossAdSpend': 50,
                                    'plannedGrossRate': 1,
                                    'rateType': 4,
                                    'startDate': '2040-01-01T00:00:00-07:00'
                                }
                            ],
                            'countryCurrencyId': 1,
                            'customKey': 0,
                            'endDate': '2041-01-02T03:59:59.000Z',
                            'endDateOpened': false,
                            'getDate': '2041-01-02T03:59:59.000Z',
                            'height': size.height,
                            'inventory': '50000',
                            'ioId': ioId,
                            'isChild': false,
                            'name': placement[1].name,
                            'packageName': '',
                            'rate': 1,
                            'rateType': {
                                'KEY': 'CPM',
                                'NAME': 'placement.rateTypeList.cpm',
                                'VALUE': 4
                            },
                            'sectionName': 'Home',
                            'siteId': generatedIds.siteId,
                            'siteName': 'Protractor Site',
                            'siteSectionId': generatedIds.siteSectionId,
                            'sizeId': size.id,
                            'sizeName': size.label,
                            'startDate': '2040-01-01T04:00:00.000Z',
                            'startDateOpened': false,
                            'status': 'New',
                            'utcOffset': 0,
                            'width': size.width
                        }
                    ]
                },
                json: true
            })
                .then(function (body) {

                    logger.info('Placement Id : ', body.placements[0].id);
                    logger.info('Placement Id : ', body.placements[1].id);

                    callCreativeTagAdChoices(newTagInjection[0]);
                    browser.wait(function () {
                        return runAddNewTag;
                    }).then(function() {
                        runAddNewTag = false;
                        callCreativeTagAdChoices(newTagInjection[1]);
                        browser.wait(function () {
                            return runAddNewTag;
                        }).then(function() {
                            global.runTagInjection = true;

                        });
                    });
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var callCreativeTagAdChoices = function (newTag) {
            request({
                method: 'POST',
                url: getCmsUrl() + '/Agencies/' + generatedIds.agencyId + '/htmlInjectionTypeAdChoices',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'name': newTag.name,
                    'optOutUrl': newTag.optOutUrl
                },
                json: true
            })
                .then(function (body) {
                    logger.info('Tag Id : ', body.id);
                    runAddNewTag = true;
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };
    };
};

module.exports = new tagInjection();
