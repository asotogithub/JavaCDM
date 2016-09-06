'use strict';

describe('Controller: PackageEditController', function () {
    var $scope,
        $state,
        PackageService,
        controller,
        mockObject;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            PackageService = jasmine.createSpyObj('PackageService',
                ['getPackage', 'updatePackage', 'addAllowRemoveProperty']);
            $provide.value('PackageService', PackageService);
        });

        inject(function ($q) {
            mockObject = {
                campaignId: 6031386,
                countryCurrencyId: 1,
                createdDate: '2015-08-20T12:50:33-04:00',
                externalId: '50000',
                ioId: 6061636,
                modifiedDate: '2015-08-26T20:14:51-04:00',
                description: 'Panda Test',
                id: 6101578,
                name: 'Panda Test POST for PUT twice',
                placements: [
                    {
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
                        }
                    }
                ]
            };

            var defer = $q.defer();

            defer.resolve(mockObject);
            defer.$promise = defer.promise;

            PackageService.getPackage.andReturn(defer.promise);
            PackageService.updatePackage.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('PackageEditController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('should redirect to placement edit view', function () {
        controller.package = {
            name: 'Package 01'
        };
        controller.selectRows({
            campaignId: 458965,
            ioId: 659963,
            id: 259875
        });
        expect($state.go).toHaveBeenCalled();
        expect($state.go).toHaveBeenCalledWith('edit-placement', {
            campaignId: 458965,
            ioId: 659963,
            placementId: 259875,
            from: 'edit-package',
            packageName: 'Package 01'
        });
    });

    it('should set placementList when PackageService.getPackage() is resolved', function () {
        $scope.$apply();
        expect(controller.package).toEqual(mockObject);
    });

    it('Should resolve the promise to be updated', function () {
        controller.package = {};
        controller.package.placements = [
            {
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
                }
            }
        ];
        controller.package.costDetails = [
            {
                costKey: 0,
                margin: 0,
                plannedGrossAdSpend: 0,
                inventory: 1,
                plannedGrossRate: 0,
                rateType: 4,
                startDate: '2015-08-13T00:00:00-07:00',
                endDate: '2025-08-14T23:59:59-07:00',
                isLast: false
            }
        ];
        controller.promise = null;
        controller.save();

        expect(controller.promise).toBeResolved();
    });
});
