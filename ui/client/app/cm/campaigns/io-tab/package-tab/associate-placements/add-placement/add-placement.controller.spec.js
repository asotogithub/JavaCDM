'use strict';

describe('Controller: PackageAddPlacementController', function () {
    // load the controller's module
    beforeEach(module('uiApp'));

    var PackageAddPlacementController, scope, campaignPackages;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, $q) {
        scope = $rootScope.$new();
        scope.$parent.vmAssoc = {
            STEP: {
                ADD_PLACEMENTS: {
                    index: 1,
                    isValid: false,
                    key: 'associatePlacements.addPlacements'
                }
            }
        };

        campaignPackages = [
            {
                campaignId: 6031386,
                packageId: 6101572,
                placements: [
                    {
                        id: 6101574,
                        name: 'test placement 1'
                    }
                ]
            },
            {
                campaignId: 6031386,
                packageId: 6101578,
                placements: [
                    {
                        id: 6101580,
                        name: 'test placement 1'
                    }
                ]
            },
            {
                placements: [
                    {
                        id: 6104859,
                        name: 'test standalone placement 1'
                    },
                    {
                        id: 6104967,
                        name: 'test standalone placement 2'
                    },
                    {
                        id: 6104982,
                        name: 'test standalone placement 3'
                    },
                    {
                        id: 6105051,
                        name: 'test standalone placement 4'
                    },
                    {
                        id: 6104920,
                        name: 'test standalone placement 5'
                    },
                    {
                        id: 6104926,
                        name: 'test standalone placement 6'
                    }
                ]
            }
        ];

        scope.$parent.vmAssoc.getData = function () {
            var request = $q.defer();

            request.resolve(campaignPackages);
            return request.promise;
        };

        PackageAddPlacementController = $controller('PackageAddPlacementController', {
            $scope: scope
        });
    }));

    it('should create an instance of PackageAddPlacementController', function () {
        expect(PackageAddPlacementController).not.toBeUndefined();
    });
});
