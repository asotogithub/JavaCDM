'use strict';

describe('Controller: CreatePlacementsController', function () {
    var $scope,
        $state,
        controller;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $rootScope.vmAssoc = {};
        $rootScope.vmAssoc.STEP = {
            PACKAGE_ASSOCIATION: {
                index: 2,
                isValid: false,
                key: 'associatePlacements.packageAssociation'
            }
        };
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('CreatePlacementsController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    describe('addToAssociatedPlacements()', function () {
        it('should add new placements to list of associated placements', function () {
            controller.placementList = [
                {
                    name: '',
                    packageName: 'Media Package 1',
                    site: {
                        id: 6064873,
                        name: 'Site 1'
                    },
                    section: [
                        {
                            id: 6064880,
                            name: 'Section1/S1'
                        },
                        {
                            id: 6064880,
                            name: 'Section2/S2'
                        }
                    ],
                    size: [
                        {
                            id: 5006631,
                            width: 180,
                            height: 150,
                            label: 'Size 1'
                        },
                        {
                            id: 5006631,
                            width: 180,
                            height: 150,
                            label: 'Size 2'
                        }
                    ]
                }
            ];
            controller.currentPackage = {
                placements: []
            };
            controller.addToAssociatedPlacements();
            expect(controller.currentPackage.placements.length).toBe(4);
        });
    });
});
