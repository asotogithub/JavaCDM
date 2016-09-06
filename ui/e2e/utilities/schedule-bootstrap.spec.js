'use strict';

var scheduleBootstrap = function() {
    var clickthrough,
        commonBootstrap = require('../utilities/bootstrap.spec'),
        creativeId,
        creativeInsertionId,
        campaignId,
        defaultGroupId,
        fs = require('fs'),
        generatedIds,
        ioId,
        logger = require('../utilities/log-to-file.spec.js').getLogger('schedule-bootstrap'),
        mediaBuyId,
        path = require('path'),
        placementId,
        placement3rdId,
        request = require('request-promise'),
        userCredentials,
        creativeList = [],
        creativeInsertionList = [],
        runUploadCreative = false,
        runAddNewTag = false;

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
    };

    this.getUserCredentials = function () {
        return commonBootstrap.getUserCredentials();
    };

    this.setupScheduleData = function (campaignName, creatives, ioName, ioNumber, placementName, newTagInjection) {
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
            getDefaultCreativeGroup();
        }).catch(function (err) {
            logger.error(err);
        });

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
                    getMediaBuy()
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

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
                    'ioNumber': ioNumber,
                    'logicalDelete': 'N',
                    'mediaBuyId': mediaBuyId,
                    'name': ioName,
                    'notes': 'Created for Protractor test',
                    'status': 'Accepted'
                },
                json: true
            })
                .then(function (body) {
                    logger.info('IO Id : ', body.id);
                    ioId = body.id;
                    create3rdSize();
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        //Create 3rd Size
        var create3rdSize = function () {
            request({
                method: 'POST',
                url: getCmsUrl() + '/Agencies/bulkPublisherSiteSectionSize',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'size': {
                        'height': '3',
                        'label': '3x3',
                        'width': '3'
                    }
                },
                json: true
            })
                .then(function (body) {
                    logger.info('new size Id : ', body.size.id);
                    createPlacement(body.size);
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        //Create Placement
        var createPlacement = function (size3rd) {
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
                    callCreatePlacement(body, size3rd);
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var callCreatePlacement = function (size, size3rd) {
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
                            'name': placementName[0],
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
                            'name': placementName[1],
                            'packageName': '',
                            'rate': 1,
                            'rateType': {
                                'KEY': 'CPM',
                                'NAME': 'placement.rateTypeList.cpm',
                                'VALUE': 4
                            },
                            'sectionName': 'Home2',
                            'siteId': generatedIds.siteId,
                            'siteName': 'Protractor Site2',
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
                            'name': placementName[2],
                            'packageName': '',
                            'rate': 1,
                            'rateType': {
                                'KEY': 'CPM',
                                'NAME': 'placement.rateTypeList.cpm',
                                'VALUE': 4
                            },
                            'sectionName': 'Home',
                            'siteId': generatedIds.siteId,
                            'siteName': 'Protractor Site2',
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
                            'height': size3rd.height,
                            'inventory': '50000',
                            'ioId': ioId,
                            'isChild': false,
                            'name': placementName[3],
                            'packageName': '',
                            'rate': 1,
                            'rateType': {
                                'KEY': 'CPM',
                                'NAME': 'placement.rateTypeList.cpm',
                                'VALUE': 4
                            },
                            'sectionName': 'Home',
                            'siteId': generatedIds.siteId,
                            'siteName': 'Protractor Site2',
                            'siteSectionId': generatedIds.siteSectionId,
                            'sizeId': size3rd.id,
                            'sizeName': size3rd.label,
                            'startDate': '2040-01-01T04:00:00.000Z',
                            'startDateOpened': false,
                            'status': 'New',
                            'utcOffset': 0,
                            'width': size3rd.width
                        }
                    ]
                },
                json: true
            })
                .then(function (body) {
                    logger.info('Placement Id : ', body.placements[0].id);
                    logger.info('Placement Id : ', body.placements[1].id);
                    logger.info('Placement Id : ', body.placements[2].id);
                    logger.info('Placement Id : ', body.placements[3].id);
                    placementId = body.placements[0].id;
                    placement3rdId = body.placements[3].id;
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
                            },
                            {
                                'adSpend': 50,
                                'campaignId': campaignId,
                                'endDate': placements[1].endDate,
                                'formattedEndDate': '01/01/2041',
                                'formattedStartDate': '01/01/2040',
                                'id': placements[1].id,
                                'inventory': placements[1].inventory,
                                'ioId': placements[1].ioId,
                                'isScheduled': placements[1].isScheduled,
                                'placementId': placements[1].id,
                                'placementName': placements[1].name,
                                'rate': placements[1].rate,
                                'rateType': placements[1].rateType,
                                'siteId': placements[1].siteId,
                                'siteName': placements[1].siteName,
                                'sizeId': placements[1].sizeId,
                                'sizeName': placements[1].sizeName,
                                'startDate': placements[1].startDate,
                                'status': 'Accepted'
                            },
                            {
                                'adSpend': 50,
                                'campaignId': campaignId,
                                'endDate': placements[2].endDate,
                                'formattedEndDate': '01/01/2041',
                                'formattedStartDate': '01/01/2040',
                                'id': placements[2].id,
                                'inventory': placements[2].inventory,
                                'ioId': placements[2].ioId,
                                'isScheduled': placements[2].isScheduled,
                                'placementId': placements[2].id,
                                'placementName': placements[2].name,
                                'rate': placements[2].rate,
                                'rateType': placements[2].rateType,
                                'siteId': placements[2].siteId,
                                'siteName': placements[2].siteName,
                                'sizeId': placements[2].sizeId,
                                'sizeName': placements[2].sizeName,
                                'startDate': placements[2].startDate,
                                'status': 'Accepted'
                            },
                            {
                                'adSpend': 50,
                                'campaignId': campaignId,
                                'endDate': placements[3].endDate,
                                'formattedEndDate': '01/01/2041',
                                'formattedStartDate': '01/01/2040',
                                'id': placements[3].id,
                                'inventory': placements[3].inventory,
                                'ioId': placements[3].ioId,
                                'isScheduled': placements[3].isScheduled,
                                'placementId': placements[3].id,
                                'placementName': placements[3].name,
                                'rate': placements[3].rate,
                                'rateType': placements[3].rateType,
                                'siteId': placements[3].siteId,
                                'siteName': placements[3].siteName,
                                'sizeId': placements[3].sizeId,
                                'sizeName': placements[3].sizeName,
                                'startDate': placements[3].startDate,
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
                    logger.info('Activated Placement Id : ', body.records[0].Placement[1].id,
                        body.records[0].Placement[1].status);
                    logger.info('Activated Placement Id : ', body.records[0].Placement[2].id,
                        body.records[0].Placement[2].status);
                    logger.info('Activated Placement Id : ', body.records[0].Placement[3].id,
                        body.records[0].Placement[3].status);

                    callCreativeTagAdChoices(newTagInjection[0]);

                    browser.wait(function () {
                        return runAddNewTag;
                    }).then(function() {
                        runAddNewTag = false;
                        callCreativeTagAdChoices(newTagInjection[1]);
                        browser.wait(function () {
                            return runAddNewTag;
                        }).then(function() {
                            callAssociateTagToPlacement(newTagInjection, body.records[0].Placement);
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
                    newTag.id = body.id;
                    runAddNewTag = true;

                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var callAssociateTagToPlacement = function (tagIds, placements) {
            associateTagToPlacement(tagIds[0].id, placements[0].id)
                .then(associateTagToPlacement(tagIds[0].id, placements[1].id))
                .then(associateTagToPlacement(tagIds[0].id, placements[2].id))
                .then(associateTagToPlacement(tagIds[0].id, placements[3].id))
                .then(associateTagToPlacement(tagIds[1].id, placements[0].id))
                .then(associateTagToPlacement(tagIds[1].id, placements[1].id))
                .then(associateTagToPlacement(tagIds[1].id, placements[2].id))
                .then(associateTagToPlacement(tagIds[1].id, placements[3].id))
                .then(function (body) {
                    logger.info('Associate Tag to placements');

                    callUploadCreative(creatives[0], true, true);

                    browser.wait(function () {
                        return runUploadCreative;
                    }).then(function () {
                        runUploadCreative = false;

                        callUploadCreative(creatives[1], false, true);
                        browser.wait(function () {
                            return runUploadCreative;
                        }).then(function () {
                            runUploadCreative = false;

                            callUploadCreative(creatives[2], false, true);
                            browser.wait(function () {
                                return runUploadCreative;
                            }).then(function () {
                                runUploadCreative = false;

                                callUploadCreative(creatives[3], true, true);
                                browser.wait(function () {
                                    return runUploadCreative;
                                }).then(function () {
                                    runUploadCreative = false;

                                    callUploadCreative(creatives[4], true, false);
                                    browser.wait(function () {
                                        return runUploadCreative;
                                    }).then(function () {
                                        runUploadCreative = false;

                                        callUploadCreative(creatives[5], true, false);
                                        browser.wait(function () {
                                            return runUploadCreative;
                                        }).then(function () {
                                            associateCreativesToCreativeGroup(creativeList);
                                        });
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
        var callUploadCreative = function (fileName, associate, temporalDir) {
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
                            associate: associate
                        }
                    );

                    runUploadCreative = true;
                })
                .catch(function (err) {
                    logger.error(err);
                });
        };

        var associateCreativesToCreativeGroup = function (creativeListId) {
            request({
                method: 'PUT',
                url: getCmsUrl() + '/CreativeGroups/' + defaultGroupId + '/creatives',
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'creativeGroupId': defaultGroupId,
                    'creatives': creativeListId
                },
                json: true
            })
                .then(function (body) {
                    logger.info('Associated Group with Creative');
                    for (var i = 0; i < creativeListId.length; i++) {
                        if (creativeListId[i].associate) {
                            creativeInsertionList.push(
                                {
                                    'campaignId': campaignId,
                                    'clickthrough': creativeListId[i].clickthrough,
                                    'creativeGroupId': defaultGroupId,
                                    'creativeId': creativeListId[i].id,
                                    'endDate': '2041-01-01T23:59:59-07:00',
                                    'placementId': creativeListId[i].clickthrough === ' ' ? placement3rdId : placementId,
                                    'released': 0,
                                    'sequence': 0,
                                    'startDate': '2040-01-01T00:00:00-07:00',
                                    'timeZone': 'MST',
                                    'weight': 100
                                }
                            );
                        }
                    }
                    createCreativeInsertion(creativeInsertionList);
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
                creativeInsertionId = body.creativeInsertions[0].id;
                global.runSchedule = true;
            }).catch(function (err) {
                logger.error(err);
            });
        };

        var associateTagToPlacement = function (tagId, placementId) {
            return request({
                method: 'PUT',
                url: getCmsUrl() + '/Agencies/' + generatedIds.agencyId +
                '/htmlInjectionTagAssociationsBulk?advertiserId=' +
                generatedIds.advertiserId + '&brandId=' +
                generatedIds.brandId,
                headers: {
                    'Content-type': 'application/json',
                    'authorization': 'Bearer ' + userCredentials.token
                },
                body: {
                    'action': 'c',
                    'campaignId': campaignId,
                    'htmlInjectionId': tagId,
                    'levelType': 'placement',
                    'placementId': placementId,
                    'sectionId': generatedIds.siteSectionId,
                    'siteId': generatedIds.siteId
                },
                json: true
            })
        };
    };


};

module.exports = new scheduleBootstrap();
