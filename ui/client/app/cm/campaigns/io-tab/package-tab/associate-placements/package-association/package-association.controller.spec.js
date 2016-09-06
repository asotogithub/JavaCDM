'use strict';

describe('Controller: PackageAssociationController', function () {
    var PackageAssociationController,
        $httpBackend,
        API_SERVICE,
        scope,
        packagesResponse,
        PackageService,
        stateParams = {
            campaignId: 6031386,
            packageId: 6101572
        };

    // load the controller's module
    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (
        $controller,
        _$httpBackend_,
        _API_SERVICE_,
        $rootScope,
        _$q_,
        _lodash_,
        _PackageService_) {
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        PackageService = _PackageService_;
        scope = $rootScope.$new();
        scope.$parent.vmAssoc = {
            STEP: {
                PACKAGE_ASSOCIATION: {
                    index: 2,
                    isValid: false,
                    key: 'associatePlacements.packageAssociation'
                }
            }
        };

        PackageAssociationController = $controller('PackageAssociationController', {
            $scope: scope,
            $stateParams: stateParams,
            $q: _$q_,
            lodash: _lodash_,
            PackageService: _PackageService_
        });

        packagesResponse = [
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
    }));

    it('should create an instance of PackageAssociationController', function () {
        expect(PackageAssociationController).not.toBeUndefined();

        $httpBackend.whenGET(API_SERVICE +
            'campaigns/6031386/io/6061636/placement/package/6101572/edit/associate-placements')
            .respond(packagesResponse);
    });
});
