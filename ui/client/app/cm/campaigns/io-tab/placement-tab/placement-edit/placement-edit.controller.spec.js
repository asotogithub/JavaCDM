'use strict';

describe('Controller: PlacementEditController', function () {
    var $httpBackend,
        $scope,
        $state,
        API_SERVICE,
        DialogFactory,
        InsertionOrderService,
        PlacementService,
        SectionService,
        SizeService,
        controller,
        mockObject,
        request,
        successMessage,
        defer,
        dialogDeferred;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            SectionService = jasmine.createSpyObj('SectionService', ['getList']);
            SizeService = jasmine.createSpyObj('SizeService', ['getList']);
            PlacementService = jasmine.createSpyObj('PlacementService', ['getPlacement', 'updatePlacement']);
            $provide.value('SectionService', SectionService);
            $provide.value('SizeService', SizeService);
            $provide.value('PlacementService', PlacementService);
        });

        inject(function ($q) {
            mockObject = {
                adSpend: 123,
                campaignId: 6031386,
                id: 6091529,
                ioId: 6061636,
                name: 'Jim-Placement-01010109d',
                sectionName: 'Jim-Placement-01010109-Section',
                siteId: 6064873,
                siteSectionId: 6064880,
                sizeId: 5006631,
                status: 'Accepted',
                sizeSelected: {
                    id: 5006631
                },
                statusSelected: {
                    key: 'Accepted'
                },
                costDetails: [
                    {
                        costKey: 0,
                        margin: 0,
                        plannedGrossAdSpend: 0,
                        inventory: 1,
                        plannedGrossRate: 0,
                        rateType: 4,
                        startDate: '2015-08-13T00:00:00-07:00',
                        endDate: '2025-08-14T23:59:59-07:00',
                        isLast: false,
                        startDateOpened: false,
                        endDateOpened: false
                    }
                ]
            };

            defer = $q.defer();

            dialogDeferred = $q.defer();
            defer.resolve(mockObject);
            defer.$promise = defer.promise;

            SectionService.getList.andReturn(defer.promise);
            SizeService.getList.andReturn(defer.promise);
            PlacementService.getPlacement.andReturn(defer.promise);
            PlacementService.updatePlacement.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function (
                                $q,
                                $controller,
                                $rootScope,
                                _$httpBackend_,
                                _$state_,
                                _API_SERVICE_,
                                _DialogFactory_,
                                _InsertionOrderService_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        DialogFactory = _DialogFactory_;
        InsertionOrderService = _InsertionOrderService_;
        defer = $q.defer();
        spyOn($state, 'go');
        controller = $controller('PlacementEditController', {
            $scope: $scope,
            DialogFactory: DialogFactory,
            $stateParams: {
                ioId: '6061636'
            }
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should resolve the promise to be saved', function () {
        controller.placement = {
            adSpend: 123,
            campaignId: 6031386,
            id: 6091529,
            ioId: 6061636,
            name: 'Jim-Placement-01010109d',
            sectionName: 'Jim-Placement-01010109-Section',
            siteId: 6064873,
            siteSectionId: 6064880,
            sizeId: 5006631,
            status: 'Accepted',
            sizeSelected: {
                id: 5006631
            },
            statusSelected: {
                key: 'Accepted'
            },
            costDetails: [
                {
                    costKey: 0,
                    margin: 0,
                    plannedGrossAdSpend: 0,
                    inventory: 1,
                    plannedGrossRate: 0,
                    rateType: 4,
                    startDate: '2015-08-13T00:00:00-07:00',
                    endDate: '2025-08-14T23:59:59-07:00',
                    isLast: false,
                    startDateOpened: false,
                    endDateOpened: false
                }
            ],
            extProp1: 'property 1',
            extProp2: 'property 2',
            extProp5: 'property 5'
        };
        controller.promise = null;
        controller.save();

        expect(controller.promise).toBeResolved();
    });

    it('Should resolve the promise to be saved without cost detail', function () {
        controller.placement = {
            adSpend: 123,
            campaignId: 6031386,
            id: 6091529,
            ioId: 6061636,
            packageId: 659625,
            name: 'Jim-Placement-01010109d',
            sectionName: 'Jim-Placement-01010109-Section',
            siteId: 6064873,
            siteSectionId: 6064880,
            sizeId: 5006631,
            status: 'Accepted',
            sizeSelected: {
                id: 5006631
            },
            statusSelected: {
                key: 'Accepted'
            },
            extProp1: 'property 1',
            extProp2: 'property 2',
            extProp5: 'property 5'
        };
        controller.promise = null;
        controller.save();

        expect(controller.promise).toBeResolved();
    });

    it('Should show notification when setting the last active placement in a IO to inactive', function () {
        controller.placement = {
            adSpend: 123,
            campaignId: 6031386,
            id: 6091529,
            ioId: 6061636,
            name: 'Jim-Placement-01010109d',
            sectionName: 'Jim-Placement-01010109-Section',
            siteId: 6064873,
            siteSectionId: 6064880,
            sizeId: 5006631,
            status: 'Rejected',
            sizeSelected: {
                id: 5006631
            },
            statusSelected: 'Inactive',
            costDetails: [
                {
                    costKey: 0,
                    margin: 0,
                    plannedGrossAdSpend: 0,
                    inventory: 1,
                    plannedGrossRate: 0,
                    rateType: 4,
                    startDate: '2015-08-13T00:00:00-07:00',
                    endDate: '2025-08-14T23:59:59-07:00',
                    isLast: false,
                    startDateOpened: false,
                    endDateOpened: false
                }
            ]
        };
        controller.statusList = [
            'Active',
            'Inactive'
        ];

        spyOn(DialogFactory, 'showCustomDialog');
        $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/6061636/packagePlacementView')
            .respond(controller.placement);
        controller.checkLastActive();
        $httpBackend.flush();

        expect(DialogFactory.showCustomDialog).toHaveBeenCalled();
    });

    it('should save extended properties', function () {
        controller.placement = {
            adSpend: 123,
            campaignId: 6031386,
            id: 6091529,
            ioId: 6061636,
            name: 'Jim-Placement-01010109d',
            sectionName: 'Jim-Placement-01010109-Section',
            siteId: 6064873,
            siteSectionId: 6064880,
            sizeId: 5006631,
            status: 'Rejected',
            sizeSelected: {
                id: 5006631
            },
            statusSelected: 'Active',
            costDetails: [
                {
                    costKey: 0,
                    margin: 0,
                    plannedGrossAdSpend: 0,
                    inventory: 1,
                    plannedGrossRate: 0,
                    rateType: 4,
                    startDate: '2015-08-13T00:00:00-07:00',
                    endDate: '2025-08-14T23:59:59-07:00',
                    isLast: false,
                    startDateOpened: false,
                    endDateOpened: false
                }
            ],
            extProp1: 'property 1',
            extProp2: 'property 2',
            extProp3: 'property 3',
            extProp4: 'property 4',
            extProp5: 'property 5'
        };
        controller.promise = null;
        controller.save();

        expect(controller.promise).toBeResolved();
        successMessage = {
            status: '200',
            message: '200 scheduling insertions were successfully updated!'
        };
        controller.save = jasmine.createSpy('save').andCallFake(function () {
            return successMessage;
        });

        request = controller.save();
        expect(request.status).toEqual(successMessage.status);
        expect(request.message).toEqual(successMessage.message);
    });

    it('should order size list', function () {
        var i = 0,
            unorderedSizeList = [
            {
                agencyId: 6031295,
                createdDate: '2015-10-29T11: 08: 09-04: 00',
                height: 31,
                id: 5014124,
                label: '1x31',
                modifiedDate: '2015-10-29T11: 08: 09-04: 00',
                width: 1
            },
            {
                agencyId: 6031295,
                createdDate: '2015-10-28T16: 42: 36-04: 00',
                height: 600,
                id: 5014062,
                label: '801x600',
                modifiedDate: '2015-10-28T16: 42: 36-04: 00',
                width: 801
            },
            {
                agencyId: 6031295,
                createdDate: '2015-10-28T18: 52: 03-04: 00',
                height: 14,
                id: 5014106,
                label: '1x14',
                modifiedDate: '2015-10-28T18: 52: 03-04: 00',
                width: 1
            },
            {
                agencyId: 6031295,
                createdDate: '2015-11-03T15: 11: 47-04: 00',
                height: 6,
                id: 5014164,
                label: '6x6',
                modifiedDate: '2015-11-03T15: 11: 47-04: 00',
                width: 6
            }

            ],
            orderedSiseList = [
                {
                    agencyId: 6031295,
                    createdDate: '2015-10-28T18: 52: 03-04: 00',
                    height: 14,
                    id: 5014106,
                    label: '1x14',
                    modifiedDate: '2015-10-28T18: 52: 03-04: 00',
                    width: 1
                },
                {
                    agencyId: 6031295,
                    createdDate: '2015-10-29T11: 08: 09-04: 00',
                    height: 31,
                    id: 5014124,
                    label: '1x31',
                    modifiedDate: '2015-10-29T11: 08: 09-04: 00',
                    width: 1
                },
                {
                    agencyId: 6031295,
                    createdDate: '2015-11-03T15: 11: 47-04: 00',
                    height: 6,
                    id: 5014164,
                    label: '6x6',
                    modifiedDate: '2015-11-03T15: 11: 47-04: 00',
                    width: 6
                },
                {
                    agencyId: 6031295,
                    createdDate: '2015-10-28T16: 42: 36-04: 00',
                    height: 600,
                    id: 5014062,
                    label: '801x600',
                    modifiedDate: '2015-10-28T16: 42: 36-04: 00',
                    width: 801
                }
            ];

        unorderedSizeList.sort(controller.compareSize);

        for (i = 0; i < unorderedSizeList.length; i++) {
            expect(unorderedSizeList[i].label).toEqual(orderedSiseList[i].label);
        }
    });

    describe('removeAssociation', function () {
        it('should remove with package', inject(function (CONSTANTS, $translate) {
            spyOn(DialogFactory, 'showCustomDialog');
            DialogFactory.showCustomDialog.andReturn({
                result: dialogDeferred.promise
            });

            controller.placement = {
                isLast: false
            };

            controller.removeAssociation();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('package.placementAssociatedRemoveWarning'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL,
                dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATED_REMOVE_WARNING_DIALOG_ID
            });
        }));

        it('should remove the last placement from package', inject(function (CONSTANTS, $translate) {
            spyOn(DialogFactory, 'showCustomDialog');
            DialogFactory.showCustomDialog.andReturn({
                result: dialogDeferred.promise
            });

            controller.placement = {
                isLast: true
            };

            controller.removeAssociation();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('placement.disassociateFromPackage'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK,
                dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATED_REMOVE_WARNING_DIALOG_ID
            });
        }));
    });
});
