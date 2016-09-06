'use strict';

describe('Controller: SiteMeasurementCampaignAssocController', function () {
    var $q,
        $scope,
        $stateParams,
        $timeout,
        $translate,
        CAMPAIGN_LIST,
        CONSTANTS,
        DialogFactory,
        RESPONSE_CAMPAIGN_LIST,
        SiteMeasurementsService,
        Utils,
        controller,
        getCampaignsPromise,
        lodash,
        updateCampaignAssociationPromise;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                _$q_,
                                $rootScope,
                                $state,
                                _$timeout_,
                                _$translate_,
                                _CONSTANTS_,
                                _SiteMeasurementsService_,
                                _Utils_,
                                _lodash_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $stateParams = {
            siteMeasurementId: 9046516
        };
        $timeout = _$timeout_;
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        SiteMeasurementsService = _SiteMeasurementsService_;
        Utils = _Utils_;
        lodash = _lodash_;

        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showDismissableMessage']);
        DialogFactory.DISMISS_TYPE = {
            SUCCESS: 'success'
        };

        CAMPAIGN_LIST = [
            {
                advertiserId: 1,
                advertiserName: 'Advertiser Name 01',
                brandId: 2,
                brandName: 'Brand Name 01',
                campaignId: 3,
                campaignName: 'Campaign Name 01',
                campaignStatus: 'Active with changes',
                expirationDate: '2057-01-01',
                id: 3,
                status: 'Active'
            },
            {
                advertiserId: 4,
                advertiserName: 'Advertiser Name 02',
                brandId: 5,
                brandName: 'Brand Name 02',
                campaignId: 6,
                campaignName: 'Campaign Name 02',
                campaignStatus: 'New',
                expirationDate: '2016-01-01',
                id: 6,
                status: 'Inactive'
            }
        ];

        RESPONSE_CAMPAIGN_LIST = [
            {
                advertiserId: 1,
                advertiserName: 'Advertiser Name 01',
                brandId: 2,
                brandName: 'Brand Name 01',
                campaignId: 3,
                campaignName: 'Campaign Name 01',
                campaignStatus: 'Active with changes',
                expirationDate: '2057-01-01'
            },
            {
                advertiserId: 4,
                advertiserName: 'Advertiser Name 02',
                brandId: 5,
                brandName: 'Brand Name 02',
                campaignId: 6,
                campaignName: 'Campaign Name 02',
                campaignStatus: 'New',
                expirationDate: '2016-01-01'
            }
        ];

        getCampaignsPromise = $q.defer();
        updateCampaignAssociationPromise = $q.defer();
        spyOn(SiteMeasurementsService, 'getCampaigns').andReturn(getCampaignsPromise.promise);
        spyOn(SiteMeasurementsService, 'updateCampaignAssociation').andReturn(updateCampaignAssociationPromise.promise);
        spyOn($state, 'go');

        controller = $controller('SiteMeasurementCampaignAssocController', {
            $q: $q,
            $stateParams: $stateParams,
            $timeout: $timeout,
            $translate: $translate,
            CONSTANTS: CONSTANTS,
            DialogFactory: DialogFactory,
            SiteMeasurementsService: SiteMeasurementsService,
            Utils: Utils,
            lodash: lodash
        });
    }));

    describe('activate()', function () {
        it('should create and instance of the controller and initialize variables ', function () {
            expect(controller).not.toBeUndefined();
            expect(controller.pageSize).toEqual(10);
            expect(controller.savedChanges).toBe(true);
            expect(controller.templateLeft).toEqual(
                'app/cm/site-measurements/site-measurement/campaign-associations/templates/left-table.html');
            expect(controller.templateRight).toEqual(
                'app/cm/site-measurements/site-measurement/campaign-associations/templates/right-table.html');
        });

        it('should load unassociated/associated campaigns & filter data', function () {
            var activeInactiveList = ['Active', 'Inactive'],
                filterValues = [
                    {
                        fieldName: 'status',
                        values: activeInactiveList
                    }
                ],
                filterOption = [
                    {
                        text: 'Status',
                        value: activeInactiveList
                    }
                ];

            getCampaignsPromise.resolve(RESPONSE_CAMPAIGN_LIST);
            $scope.$apply();

            expect(controller.associatedCampaignList).toEqual(CAMPAIGN_LIST);
            expect(controller.associatedFilterValues).toEqual(filterValues);
            expect(controller.associatedFilterOption).toEqual(filterOption);
            expect(controller.unassociatedCampaignList).toEqual(CAMPAIGN_LIST);
            expect(controller.unassociatedFilterValues).toEqual(filterValues);
            expect(controller.unassociatedFilterOption).toEqual(filterOption);
        });
    });

    describe('save()', function () {
        it('should call a modal dialog', function () {
            controller.save();
            updateCampaignAssociationPromise.resolve({});
            $scope.$apply();

            expect(DialogFactory.showDismissableMessage).toHaveBeenCalledWith(
                DialogFactory.DISMISS_TYPE.SUCCESS,
                'info.operationCompleted');
        });
    });

    describe('setChanges()', function () {
        it('should set savedChanges flag', function () {
            expect(controller.savedChanges).toBe(true);
            controller.setChanges();
            expect(controller.savedChanges).toBe(false);
        });
    });
});
