'use strict';

var creativeGroupBootstrap = function() {
    var campaignId,
        commonBootstrap = require('../utilities/bootstrap.spec'),
        creativeList = [],
        customCGroupId,
        defaultGroupId,
        fs = require('fs'),
        generatedIds,
        ioId,
        logger = require('../utilities/log-to-file.spec.js').getLogger('creative-group-bootstrap'),
        mediaBuyId,
        path = require('path'),
        placementId,
        request = require('request-promise'),
        runUploadCreative = false,
        runAssociateCG = false,
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

    this.setupCreativeGroupData = function (campaignName, io, placement, creatives, customCreativeGroup) {
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
                        }
                    ]
                },
                json: true
            })
                .then(function (body) {
                    logger.info('Placement Id : ', body.placements[0].id);
                    placementId = body.placements[0].id;
                    callActivatePlacement(body.placements);
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var callActivatePlacement = function (placements) {
            request({
                method: 'PUT',
                url: getCmsUrl() + '/Placements/status?ioId=' + ioId,
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'records': {
                        'Placement': [
                            {
                                'adSpend': 50,
                                'campaignId': campaignId,
                                'endDate': placements[0].endDate,
                                'formattedEndDate': '01/01/2041',
                                'formattedStartDate': '01/01/2040',
                                'id': placements[0].id,
                                'inventory': placements[0].inventory,
                                'ioId': placements[0].ioId,
                                'isScheduled': placements[0].isScheduled,
                                'placementId': placements[0].id,
                                'placementName': placements[0].name,
                                'rate': placements[0].rate,
                                'rateType': placements[0].rateType,
                                'siteId': placements[0].siteId,
                                'siteName': placements[0].siteName,
                                'sizeId': placements[0].sizeId,
                                'sizeName': placements[0].sizeName,
                                'startDate': placements[0].startDate,
                                'status': 'Accepted'
                            }
                        ]
                    }
                },
                json: true
            })
                .then(function (body) {
                    logger.info('Activated Placement Id : ', body.records[0].Placement[0].id,
                        body.records[0].Placement[0].status);

                    getDefaultCreativeGroup();

                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var getDefaultCreativeGroup = function () {
            request({
                method: 'GET',
                url: getCmsUrl() + '/Campaigns/' + campaignId + '/creativeGroups',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                json: true
            })
                .then(function (body) {
                    logger.info('Default Creative Group : ', body.records[0].CreativeGroupDtoForCampaigns[0].id);
                    defaultGroupId = body.records[0].CreativeGroupDtoForCampaigns[0].id;

                    callUploadCreative(creatives[0], true);

                    browser.wait(function () {
                        return runUploadCreative;
                    }).then(function () {
                        runUploadCreative = false;

                        callUploadCreative(creatives[1], true);
                        browser.wait(function () {
                            return runUploadCreative;
                        }).then(function () {
                            runUploadCreative = false;

                            callUploadCreative(creatives[2], true);
                            browser.wait(function () {
                                return runUploadCreative;
                            }).then(function () {
                                runUploadCreative = false;

                                callUploadCreative(creatives[3], true);
                                browser.wait(function () {
                                    return runUploadCreative;
                                }).then(function () {
                                    associateCreativesToCreativeGroup(defaultGroupId, creativeList);

                                    browser.wait(function () {
                                        return runAssociateCG;
                                    }).then(function () {
                                        createNewCreativeGroup(customCreativeGroup, campaignId);


                                    });

                                });
                            });
                        });
                    });
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        //Create Creative
        var callUploadCreative = function (fileName, temporalDir) {
            request({
                method: 'POST',
                url: getCmsUrl() + '/Campaigns/' + campaignId + '/Creative?filename=' + fileName,
                headers: {
                    'content-type': 'application/octet-stream',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: fs.readFileSync(path.resolve(__dirname, '../../server/templates/' +
                    (temporalDir ? '.tmp/' : '') + fileName)),
                json: false
            })
                .then(function (body) {
                    var obj = JSON.parse(body);
                    logger.info('Uploaded creative Id : ', obj.id);

                    creativeList.push(
                        {
                            id: obj.id,
                            clickthrough: obj.clickthrough,
                            associate: false
                        }
                    );

                    runUploadCreative = true;
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var createNewCreativeGroup = function (nameCreativeGroup, campaignId) {
            request({
                method: 'POST',
                url: getCmsUrl() + '/CreativeGroups',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'campaignId': campaignId,
                    'cookieTarget': '',
                    'daypartTarget': null,
                    'doCookieTargeting': 0,
                    'doDaypartTargeting': 0,
                    'enableFrequencyCap': 0,
                    'enableGroupWeight': 0,
                    'frequencyCap': 1,
                    'frequencyCapWindow': 24,
                    'geoTargets': null,
                    'isDefault': 0,
                    'name': nameCreativeGroup,
                    'priority': 0,
                    'weight': 100,
                    'doGeoTargeting': 0,
                    'clickthroughCap': -1,
                    'doOptimization': 0,
                    'impressionCap': 0,
                    'isReleased': 0,
                    'minOptimizationWeight': -1
                },
                json: true
            }).then(function (body) {
                logger.info('new Creative Group \'', nameCreativeGroup, '\' Id : ', body.id);
                customCGroupId = body.id;

                runAssociateCG = false;
                associateCreativesToCreativeGroup(
                    customCGroupId,
                    [
                        creativeList[0],
                        creativeList[1]
                    ]
                );

                browser.wait(function () {
                    return runAssociateCG;
                }).then(function () {
                    createCreativeInsertion([
                        {
                            'campaignId': campaignId,
                            'clickthrough': creativeList[1].clickthrough,
                            'creativeGroupId': defaultGroupId,
                            'creativeId': creativeList[1].id,
                            'endDate': '2041-01-01T23:59:59-07:00',
                            'placementId': placementId,
                            'released': 0,
                            'sequence': 0,
                            'startDate': '2040-01-01T00:00:00-07:00',
                            'timeZone': 'MST',
                            'weight': 100
                        },
                        {
                            'campaignId': campaignId,
                            'clickthrough': creativeList[2].clickthrough,
                            'creativeGroupId': defaultGroupId,
                            'creativeId': creativeList[2].id,
                            'endDate': '2041-01-01T23:59:59-07:00',
                            'placementId': placementId,
                            'released': 0,
                            'sequence': 0,
                            'startDate': '2040-01-01T00:00:00-07:00',
                            'timeZone': 'MST',
                            'weight': 100
                        }
                    ]);
                });
            }).catch(function (err) {
                logger.error(err);
            });
        };

        var associateCreativesToCreativeGroup = function (cGroupId, creativeListId) {
            request({
                method: 'PUT',
                url: getCmsUrl() + '/CreativeGroups/' + cGroupId + '/creatives',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'creativeGroupId': cGroupId,
                    'creatives': creativeListId
                },
                json: true
            })
                .then(function (body) {
                    logger.info('Associated Group with Creative');
                    runAssociateCG = true;
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        //Create Creative Insertion
        var createCreativeInsertion = function (creativeInsertions) {
            request({
                method: 'POST',
                url: getCmsUrl() + '/Campaigns/'+ campaignId +'/bulkCreativeInsertion',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'creativeInsertions': creativeInsertions
                },
                json: true
            }).then(function (body) {
                logger.info('creativeInsertion : ', body.creativeInsertions[0].id);
            }).catch(function (err) {
                logger.error(err);
            });
        };
    };
};

module.exports = new creativeGroupBootstrap();
