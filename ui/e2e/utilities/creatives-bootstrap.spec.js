'use strict';

var creativesBootstrap = function(){
    var advertiserFilterId,
        brandFilterId,
        campaignFilterId,
        commonBootstrap = require('../utilities/bootstrap.spec'),
        fs = require('fs'),
        generatedIds,
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

    this.setupCreativesData = function (campaignName, callback) {
        userCredentials = commonBootstrap.getUserCredentials();
        generatedIds = commonBootstrap.getIds();

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
            console.log('campaign \'', campaignName, '\' Id : ', body.id);
            campaignFilterId = body.id;
            setCampaignInactive(body);
        }).catch(function (err) {
            console.log('ERROR: ', err);
        });


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
                console.log('set campaign \'', campaignName, '\' to inactive');
                if(callback != undefined) {
                    callback();
                }
            }).catch(function (err) {
                console.log('ERROR: ', err);
            });
        };
    };
};

module.exports = new creativesBootstrap();