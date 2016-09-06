'use strict';

describe('Controller: NewPlacementsInputController', function () {
    var $scope,
        $state,
        controller,
        SiteService,
        SectionService,
        SizeService,
        PLACEMENT_LIST = [
            {
                packageName: 'Media Package 1',
                name: '',
                site: {
                    id: 256587,
                    name: 'siteName'
                },
                section: [
                    {
                        id: 548564,
                        siteId: 256587,
                        name: 'section1'
                    }
                ],
                size: [
                    {
                        id: 586486,
                        label: '105x180'
                    },
                    {
                        id: 586487,
                        label: '150x108'
                    }
                ]
            }
        ];

    beforeEach(function () {
        module('uiApp', function ($provide) {
            SiteService = jasmine.createSpyObj('SiteService', ['getList']);
            SectionService = jasmine.createSpyObj('SectionService', ['getList']);
            SizeService = jasmine.createSpyObj('SizeService', ['getList']);
            $provide.value('SiteService', SiteService);
            $provide.value('SectionService', SectionService);
            $provide.value('SizeService', SizeService);
        });

        inject(function ($q) {
            var defer = $q.defer();

            defer.resolve();
            defer.$promise = defer.promise;

            SiteService.getList.andReturn(defer.promise);
            SectionService.getList.andReturn(defer.promise);
            SizeService.getList.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $rootScope.vmAdd = {};
        $rootScope.vmAdd.STEP = {
            DETAILS: {
                index: 1,
                isValid: false,
                key: 'addPlacement.details'
            }
        };
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('NewPlacementsInputController', {
            $scope: $scope
        });
        controller.model = PLACEMENT_LIST;
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should verify placement is valid', function () {
        controller.validateAllRows();
        expect(controller.model.isValid).toBe(true);
    });
});
