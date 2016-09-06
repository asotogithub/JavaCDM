'use strict';

describe('Controller: AddGroupCreativeAssociationsController', function () {
    var $httpBackend,
        $q,
        $translate,
        $uibModalInstance,
        API_SERVICE,
        AddGroupCreativeAssociationsController,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        ErrorRequestHandler,
        data,
        scope;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (
        _$httpBackend_,
        _$q_,
        _$translate_,
        $controller,
        $rootScope,
        $state,
        _API_SERVICE_,
        _CONSTANTS_,
        _CampaignsService_,
        _DialogFactory_,
        _ErrorRequestHandler_) {
        data = {};
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        $translate = _$translate_;
        API_SERVICE = _API_SERVICE_;
        CONSTANTS = _CONSTANTS_;
        CampaignsService = _CampaignsService_;
        DialogFactory = _DialogFactory_;
        ErrorRequestHandler = _ErrorRequestHandler_;
        scope = $rootScope.$new();

        $uibModalInstance = {
            close: jasmine.createSpy('close'),
            dismiss: jasmine.createSpy('dismiss')
        };

        data.creatives = [
            {
                campaignId: 9084263,
                creativeAlias: '50x50',
                creativeFileName: '50x50.jpg',
                creativeGroupId: 31878,
                creativeGroupName: 'Default00',
                creativeGroupWeight: 100,
                creativeGroupWeightEnabled: false,
                creativeHeight: 50,
                creativeId: 9110799,
                creativeWidth: 50
            }
        ];
        data.placements = {
                adSpend: 0,
                campaignId: 9084263,
                countryCurrencyId: 1,
                createdDate: '2015-07-07T08:29:33-06:00',
                createdTpwsKey: 'e7e081ee-dd48-430e-aa79-fc1091031636',
                endDate: '2015-08-06T00:00:00-06:00',
                height: 400,
                id: 9108257,
                inventory: 1,
                ioId: 9108256,
                isScheduled: 'N',
                isSecure: 0,
                isTrafficked: 0,
                logicalDelete: 'N',
                maxFileSize: 1,
                modifiedDate: '2015-08-12T16:00:32-06:00',
                modifiedTpwsKey: '20c3535a-6718-4737-923d-f3748c247f1c',
                name: 'sport - Header - 100x400',
                rate: 0,
                rateType: 'CPM',
                resendTags: 0,
                siteId: 9107208,
                siteName: 'sport',
                siteSectionId: 9107209,
                siteSectionName: 'Header',
                sizeId: 22157,
                sizeName: '100x400',
                smEventId: -1,
                startDate: '2015-07-07T00:00:00-06:00',
                status: 'New',
                utcOffset: 0,
                width: 100
            };

        spyOn($state, 'go');
        spyOn(CampaignsService, 'bulkSave');
        spyOn(DialogFactory, 'showCustomDialog');
        spyOn(DialogFactory, 'showDialog');
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();

        AddGroupCreativeAssociationsController = $controller('AddGroupCreativeAssociationsController', {
            $scope: scope,
            $uibModalInstance: $uibModalInstance,
            CampaignsService: CampaignsService,
            DialogFactory: DialogFactory,
            data: data
        });
    }));

    describe('AddGroupCreativeAssociationsController Suite:', function () {
        it('should be the controller', function () {
            expect(scope.vm).toBe(AddGroupCreativeAssociationsController);
        });

        it('should load the dialog with available data to be associated', function () {
            expect(AddGroupCreativeAssociationsController.creativeInputList.length).toEqual(1);
            expect(AddGroupCreativeAssociationsController.creativeInputList).toEqual(data.creatives);
            expect(AddGroupCreativeAssociationsController.creativeInsertionTemplateObject).toEqual({
                campaignId: 9084263,
                endDate: '2015-08-06T00:00:00-06:00',
                placementEndDate: '2015-08-06T00:00:00-06:00',
                placementId: 9108257,
                placementName: 'sport - Header - 100x400',
                placementStartDate: '2015-07-07T00:00:00-06:00',
                placementStatus: 'New',
                startDate: '2015-07-07T00:00:00-06:00',
                siteId: 9107208,
                siteName: 'sport',
                siteSectionId: 9107209,
                siteSectionName: undefined,
                released: 0,
                sequence: 0,
                timeZone: 'MST'
            });
        });
    });

    describe('saveAndClose()', function () {
        var deferred,
            dialogDeferred,
            errorMessage = 'An error message';

        beforeEach(function () {
            deferred = $q.defer();
            CampaignsService.bulkSave.andReturn(deferred.promise);
            AddGroupCreativeAssociationsController.saveAndClose();

            dialogDeferred = $q.defer();
            DialogFactory.showCustomDialog.andReturn({
                result: dialogDeferred.promise
            });
        });

        describe('on failure', function () {
            it('should invoke DialogFactory.showCustomDialog()', function () {
                deferred.reject({
                    status: CONSTANTS.HTTP_STATUS.BAD_REQUEST,
                    data: {
                        error: {
                            code: {
                                $: '101'
                            },
                            message: errorMessage
                        }
                    }
                });
                scope.$digest();

                expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                    type: CONSTANTS.DIALOG.TYPE.ERROR,
                    title: $translate.instant('global.error'),
                    description: errorMessage
                });
            });

            it('should invoke DialogFactory.showDialog() if error code is not available', function () {
                deferred.reject({
                    status: CONSTANTS.HTTP_STATUS.BAD_REQUEST,
                    data: {
                        error: {
                            message: errorMessage
                        }
                    }
                });
                scope.$apply();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            });

            it('should invoke DialogFactory.showDialog() as default error catch', function () {
                deferred.reject({});
                scope.$apply();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            });
        });
    });
});
