'use strict';

describe('Controller: CampaignIoDetailsController', function () {
    var API_SERVICE,
        $httpBackend,
        $scope,
        InsertionOrderService,
        PlacementService,
        controller,
        io,
        placementList,
        stateParams;

    // load the controller's module
    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (
        $controller,
        _$httpBackend_,
        $rootScope,
        _API_SERVICE_,
        _InsertionOrderService_,
        _PlacementService_) {
        $httpBackend = _$httpBackend_;
        $scope = $rootScope.$new();
        API_SERVICE = _API_SERVICE_;
        InsertionOrderService = _InsertionOrderService_;
        PlacementService = _PlacementService_;
        stateParams = {
            ioId: 6031583
        };

        controller = $controller('CampaignIoDetailsController', {
            $scope: $scope,
            $stateParams: stateParams,
            InsertionOrderService: _InsertionOrderService_,
            PlacementService: _PlacementService_
        });
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        io = {
            activePlacementCounter: 1,
            campaignId: 6031581,
            createdDate: '2015-07-15T12:51:35-04:00',
            createdTpwsKey: 'e2c8a0bb-71b0-4fb5-8cec-bf4d37806eba',
            id: 6031583,
            ioNumber: 123456,
            lastUpdated: '2015-07-29T18:18:33-04:00',
            lastUpdatedAuthor: 'UI Truforce USER UI Truforce USER',
            logicalDelete: 'N',
            mediaBuyId: 5034699,
            modifiedDate: '2015-07-29T18:18:33-04:00',
            modifiedTpwsKey: '920b249c-0eaa-4dfd-beea-158d22bbd7fd',
            name: 'john',
            notes: 'somenotes',
            placementsCount: 2,
            publisherId: 5011330,
            status: 'Rejected',
            totalAdSpend: 300
        };
        placementList = [
            {
                adSpend: 100,
                endDate: '2015-08-08T00:00:00-04:00',
                height: 100,
                id: 9110480,
                inventory: 1,
                name: 'sport - Header - 100x100',
                rate: 0,
                rateType: 'CPM',
                siteName: 'sport',
                startDate: '2015-07-09T00:00:00-04:00',
                status: 'Rejected',
                width: 100
            },
            {
                adSpend: 200,
                endDate: '2015-08-08T00:00:00-04:00',
                height: 150,
                id: 9110481,
                inventory: 1,
                name: 'AOL - Header - 150x150',
                rate: 0,
                rateType: 'CPM',
                siteName: 'sport',
                startDate: '2015-07-09T00:00:00-04:00',
                status: 'Accepted',
                width: 150
            }
        ];
    }));

    it('should load an InsertionOrder', function () {
        $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/' + io.id).respond(io);
        $httpBackend.whenGET(API_SERVICE + 'Placements?query=ioId%3D' + io.id).respond({
            records: [
                {
                    Placement: placementList
                }
            ]
        });

        $httpBackend.flush();

        expect(controller.io.status).toEqual('Rejected');
        expect(controller.io.name).toEqual('john');
        expect(controller.io.ioNumber).toEqual(123456);
        expect(controller.io.id).toEqual(6031583);

        var metricsExpected = [
            {
                key: 'Total Placements', value: 2, icon: 'fa-cubes', type: 'number'
            }, {
                key: 'Active Placements', value: 1, icon: 'fa-cube', type: 'number'
            }, {
                key: 'Total IO Ad Spend', value: 300, icon: 'fa-usd', type: 'currency'
            }
        ];

        expect(controller.metrics).toEqual(metricsExpected);
    });
});
