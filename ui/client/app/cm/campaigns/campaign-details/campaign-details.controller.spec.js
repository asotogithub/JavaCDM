'use strict';

describe('Controller: CampaignDetailsController', function () {
    var $scope,
        CampaignDetailsController,
        CampaignsService,
        campaignId,
        deferred,
        metricsDeferred,
        buildKpiDeferred;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, $q) {
        var $stateParams = {};

        $scope = $rootScope.$new();
        CampaignsService = jasmine.createSpyObj('CampaignsService', [
            'getCampaign',
            'getCampaignMetrics',
            'buildKpiDisplay'
        ]);
        campaignId = $stateParams.campaignId = 1337;
        deferred = $q.defer();
        metricsDeferred = $q.defer();
        buildKpiDeferred = $q.defer();

        CampaignsService.getCampaign.andReturn(deferred.promise);
        CampaignsService.getCampaignMetrics.andReturn(metricsDeferred.promise);
        CampaignsService.buildKpiDisplay.andReturn(buildKpiDeferred.promise);

        CampaignDetailsController = $controller('CampaignDetailsController', {
            $scope: $scope,
            $stateParams: $stateParams,
            CampaignsService: CampaignsService
        });
    }));

    it('should invoke CampaignsService.getCampaignMetrics()', function () {
        metricsDeferred.resolve([
            {
                clicks: 2451,
                conversions: 5245,
                cost: 0.0,
                ctr: 0.001704095,
                day: '2015-06-30T00:00:00-06:00',
                id: 1088050,
                impressions: 1438300,
                eCPA: 0.0
            }
        ]);
        $scope.$digest();

        expect(CampaignsService.getCampaignMetrics).toHaveBeenCalledWith(campaignId);
        expect(CampaignsService.buildKpiDisplay).toHaveBeenCalled();
    });
});
