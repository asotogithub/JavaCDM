'use strict';

describe('Controller: AddScheduleAssignmentsController', function () {
    var $uibModalInstance,
        $q,
        $scope,
        $translate,
        AddScheduleAssignmentsController,
        CONSTANTS,
        CampaignsService,
        CreativeInsertionService,
        DialogFactory,
        ErrorRequestHandler,
        campaignId;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            CreativeInsertionService = jasmine.createSpyObj('CreativeInsertionService', ['bulkSave']);
            $provide.value('CreativeInsertionService', CreativeInsertionService);
        });

        inject(function (_$q_) {
            var defer = _$q_.defer();

            defer.resolve();
            defer.$promise = defer.promise;

            CreativeInsertionService.bulkSave.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    beforeEach(inject(function (
        _$q_,
        _$translate_,
        _CONSTANTS_,
        _DialogFactory_,
        _ErrorRequestHandler_,
        $controller,
        $rootScope,
        $state,
        _CampaignsService_) {
        var data = {};

        $uibModalInstance = {
            close: jasmine.createSpy('close'),
            dismiss: jasmine.createSpy('dismiss')
        };
        $q = _$q_;
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        DialogFactory = _DialogFactory_;
        ErrorRequestHandler = _ErrorRequestHandler_;
        $scope = $rootScope.$new();
        CampaignsService = _CampaignsService_;
        campaignId = data.campaignId = 9084263;
        data.creativeInsertionList = [];
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
        data.placements = [
            {
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
            }
        ];

        spyOn($state, 'go');
        spyOn(CampaignsService, 'bulkSave');
        spyOn(DialogFactory, 'showCustomDialog');
        spyOn(DialogFactory, 'showDialog');
        spyOn(ErrorRequestHandler, 'handle').andCallThrough();

        AddScheduleAssignmentsController = $controller('AddScheduleAssignmentsController', {
            $uibModalInstance: $uibModalInstance,
            $scope: $scope,
            CampaignsService: CampaignsService,
            DialogFactory: DialogFactory,
            data: data
        });
    }));

    describe('$scope', function () {
        describe('vm', function () {
            it('should be the controller', function () {
                expect($scope.vm).toBe(AddScheduleAssignmentsController);
            });
        });
    });

    describe('activate()', function () {
        it('should set creatives and placements', function () {
            expect(AddScheduleAssignmentsController.creatives.length).toEqual(1);
            expect(AddScheduleAssignmentsController.placements.length).toEqual(1);
        });
    });

    describe('dismiss()', function () {
        it('should dismiss() $uibModalInstance', function () {
            AddScheduleAssignmentsController.dismiss();

            expect($uibModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe('removePending()', function () {
        it('should remove selection from pending', function () {
            var
                pending1 = {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    endDate: '2015-08-06T00:00:00-06:00',
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    startDate: '2015-07-07T00:00:00-06:00',
                    weight: 100
                },
                pending2 = {
                    campaignId: 9084263,
                    creativeAlias: 'Super Expert chiken',
                    creativeDefaultClickthrough: 'http://www.trueffect.com',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9128897,
                    endDate: '2015-08-08T00:00:00-06:00',
                    placementEndDate: '2015-08-08T00:00:00-06:00',
                    placementId: 9110480,
                    placementName: 'sport - Header - 100x100',
                    placementStartDate: '2015-07-09T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    sizeName: '100x100',
                    startDate: '2015-07-09T00:00:00-06:00',
                    weight: 100
                };

            AddScheduleAssignmentsController.pending = [pending1, pending2];
            AddScheduleAssignmentsController.removePending([pending1]);

            expect(AddScheduleAssignmentsController.pending).toEqual([pending2]);
        });
    });

    describe('selectCreatives()', function () {
        var creatives;

        beforeEach(function () {
            creatives = [
                {
                    campaignId: 9084263,
                    creativeAlias: '50x50',
                    creativeFileName: '50x50.jpg',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9110799,
                    height: 50,
                    sizeName: '50x50',
                    width: 50
                },
                {
                    campaignId: 9084263,
                    creativeAlias: '180x150',
                    creativeDefaultClickthrough: 'http://www.trueforce.com',
                    creativeFileName: '180x150.jpg',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108241,
                    height: 150,
                    sizeName: '180x150',
                    width: 180
                },
                {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeFileName: '200x100.jpg',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    height: 100,
                    sizeName: '200x100',
                    width: 200
                },
                {
                    campaignId: 9084263,
                    creativeAlias: 'Super Expert chiken',
                    creativeDefaultClickthrough: 'http://www.trueffect.com/1',
                    creativeClickthroughs: [
                        {
                            sequence: 2,
                            url: 'http://www.trueffect.com/2'
                        },
                        {
                            sequence: 3,
                            url: 'http://www.trueffect.com/3'
                        }
                    ],
                    creativeFileName: 'Chiken-2.zip',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9128897,
                    height: 100,
                    sizeName: '100x100',
                    width: 100
                }
            ];

            AddScheduleAssignmentsController.selectPlacements([
                {
                    height: 400,
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108257,
                    placementName: 'sport - Header - 100x400',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    sizeName: '100x400',
                    width: 100
                },
                {
                    height: 100,
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    width: 200
                },
                {
                    height: 90,
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108263,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107198,
                    siteName: 'ESPN',
                    siteSectionId: 9107199,
                    siteSectionName: 'Homepage',
                    sizeName: '728x90',
                    width: 728
                },
                {
                    height: 100,
                    placementEndDate: '2015-08-08T00:00:00-06:00',
                    placementId: 9110480,
                    placementName: 'sport - Header - 100x100',
                    placementStartDate: '2015-07-09T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    sizeName: '100x100',
                    width: 100
                }
            ]);
        });

        it('should updatePending() with selectedCreatives', function () {
            AddScheduleAssignmentsController.selectCreatives(creatives);
            expect(AddScheduleAssignmentsController.pending).toEqual([
                {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    endDate: '2015-08-06T00:00:00-06:00',
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    startDate: '2015-07-07T00:00:00-06:00',
                    weight: 100
                },
                {
                    campaignId: 9084263,
                    creativeAlias: 'Super Expert chiken',
                    creativeDefaultClickthrough: 'http://www.trueffect.com/1',
                    creativeClickthroughs: [
                        {
                            sequence: 2,
                            url: 'http://www.trueffect.com/2'
                        },
                        {
                            sequence: 3,
                            url: 'http://www.trueffect.com/3'
                        }
                    ],
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9128897,
                    endDate: '2015-08-08T00:00:00-06:00',
                    placementEndDate: '2015-08-08T00:00:00-06:00',
                    placementId: 9110480,
                    placementName: 'sport - Header - 100x100',
                    placementStartDate: '2015-07-09T00:00:00-06:00',
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    sizeName: '100x100',
                    startDate: '2015-07-09T00:00:00-06:00',
                    weight: 100
                }
            ]);

            AddScheduleAssignmentsController.selectCreatives(creatives.slice(0, 3));
            expect(AddScheduleAssignmentsController.pending).toEqual([
                {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    endDate: '2015-08-06T00:00:00-06:00',
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    startDate: '2015-07-07T00:00:00-06:00',
                    weight: 100
                }
            ]);

            AddScheduleAssignmentsController.selectCreatives(creatives.slice(0, 2));
            expect(AddScheduleAssignmentsController.pending).toEqual([]);
        });
    });

    describe('selectPlacements()', function () {
        var placements;

        beforeEach(function () {
            placements = [
                {
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108257,
                    placementName: 'sport - Header - 100x400',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    placementStatus: 'New',
                    height: 400,
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    sizeName: '100x400',
                    width: 100
                },
                {
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    placementStatus: 'New',
                    height: 100,
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    width: 200
                },
                {
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108263,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    placementStatus: 'New',
                    height: 90,
                    siteId: 9107198,
                    siteName: 'ESPN',
                    siteSectionId: 9107199,
                    siteSectionName: 'Homepage',
                    sizeName: '728x90',
                    width: 728
                },
                {
                    height: 100,
                    placementEndDate: '2015-08-08T00:00:00-06:00',
                    placementId: 9110480,
                    placementName: 'sport - Header - 100x100',
                    placementStartDate: '2015-07-09T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    sizeName: '100x100',
                    width: 100
                }
            ];

            AddScheduleAssignmentsController.selectCreatives([
                {
                    campaignId: 9084263,
                    creativeAlias: '50x50',
                    creativeFileName: '50x50.jpg',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9110799,
                    height: 50,
                    sizeName: '50x50',
                    width: 50
                },
                {
                    campaignId: 9084263,
                    creativeAlias: '180x150',
                    creativeDefaultClickthrough: 'http://www.trueforce.com',
                    creativeFileName: '180x150.jpg',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108241,
                    height: 150,
                    sizeName: '180x150',
                    width: 180
                },
                {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeFileName: '200x100.jpg',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    height: 100,
                    sizeName: '200x100',
                    width: 200
                },
                {
                    campaignId: 9084263,
                    creativeAlias: 'Super Expert chiken',
                    creativeDefaultClickthrough: 'http://www.trueffect.com/1',
                    creativeClickthroughs: [
                        {
                            sequence: 2,
                            url: 'http://www.trueffect.com/2'
                        },
                        {
                            sequence: 3,
                            url: 'http://www.trueffect.com/3'
                        }
                    ],
                    creativeFileName: 'Chiken-2.zip',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9128897,
                    height: 100,
                    sizeName: '100x100',
                    width: 100
                }
            ]);
        });

        it('should updatePending() with selectedPlacements', function () {
            AddScheduleAssignmentsController.selectPlacements(placements);
            expect(AddScheduleAssignmentsController.pending).toEqual([
                {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    endDate: '2015-08-06T00:00:00-06:00',
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    startDate: '2015-07-07T00:00:00-06:00',
                    weight: 100
                },
                {
                    campaignId: 9084263,
                    creativeAlias: 'Super Expert chiken',
                    creativeDefaultClickthrough: 'http://www.trueffect.com/1',
                    creativeClickthroughs: [
                        {
                            sequence: 2,
                            url: 'http://www.trueffect.com/2'
                        },
                        {
                            sequence: 3,
                            url: 'http://www.trueffect.com/3'
                        }
                    ],
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9128897,
                    endDate: '2015-08-08T00:00:00-06:00',
                    placementEndDate: '2015-08-08T00:00:00-06:00',
                    placementId: 9110480,
                    placementName: 'sport - Header - 100x100',
                    placementStartDate: '2015-07-09T00:00:00-06:00',
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    sizeName: '100x100',
                    startDate: '2015-07-09T00:00:00-06:00',
                    weight: 100
                }
            ]);

            AddScheduleAssignmentsController.selectPlacements(placements.slice(0, 3));
            expect(AddScheduleAssignmentsController.pending).toEqual([
                {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    endDate: '2015-08-06T00:00:00-06:00',
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    startDate: '2015-07-07T00:00:00-06:00',
                    weight: 100
                }
            ]);

            AddScheduleAssignmentsController.selectPlacements(placements.slice(0, 1));
            expect(AddScheduleAssignmentsController.pending).toEqual([]);
        });
    });

    describe('saveAndClose()', function () {
        var deferred,
            dialogDeferred,
            errorMessage = 'An error message';

        beforeEach(function () {
            deferred = $q.defer();
            CampaignsService.bulkSave.andReturn(deferred.promise);
            AddScheduleAssignmentsController.saveAndClose([
                {
                    campaignId: 9084263,
                    creativeAlias: '200x100',
                    creativeDefaultClickthrough: 'http://www.google.com',
                    creativeGroupId: 31878,
                    creativeGroupName: 'Default00',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9108242,
                    endDate: '2015-08-06T00:00:00-06:00',
                    placementEndDate: '2015-08-06T00:00:00-06:00',
                    placementId: 9108260,
                    placementName: 'IO',
                    placementStartDate: '2015-07-07T00:00:00-06:00',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '200x100',
                    startDate: '2015-07-07T00:00:00-06:00',
                    weight: 100
                }
            ]);

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
                $scope.$digest();

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
                $scope.$apply();

                expect(ErrorRequestHandler.handle).toHaveBeenCalled();
            });

            it('should invoke DialogFactory.showDialog() as default error catch', function () {
                deferred.reject({});
                $scope.$apply();

                expect(ErrorRequestHandler.handle).toHaveBeenCalled();
            });
        });
    });
});
