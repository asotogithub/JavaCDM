'use strict';

describe('Service: InsertionOrderService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        InsertionOrderService,
        ErrorRequestHandler,
        DateTimeService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_,
                                _$q_,
                                _API_SERVICE_,
                                _InsertionOrderService_,
                                _DateTimeService_,
                                _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        InsertionOrderService = _InsertionOrderService_;
        DateTimeService = _DateTimeService_;

        installPromiseMatchers();
    }));

    describe('getList()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].InsertionOrder', function () {
                var promise;

                $httpBackend.expectGET(
                        API_SERVICE + 'InsertionOrders?query=' + encodeURIComponent('campaignId=5998696'))
                    .respond({
                        records: [
                            {
                                InsertionOrder: [
                                    {
                                        createdDate: '2015-05-21T13:15:40-04:00',
                                        id: '6009383',
                                        ioNumber: '1',
                                        logicalDelete: 'N',
                                        mediaBuyId: '5030908',
                                        modifiedDate: '2015-07-03T19:41:51-04:00',
                                        name: 'UI Test - Automated generated IO',
                                        notes: 'Automated generated UI',
                                        placementsCount: '10',
                                        publisherId: '5010696',
                                        status: 'Accepted',
                                        totalAdSpend: '100'
                                    },
                                    {
                                        createdDate: '2015-07-14T21:15:18-04:00',
                                        id: '6031011',
                                        ioNumber: '123456',
                                        logicalDelete: 'N',
                                        mediaBuyId: '5032564',
                                        modifiedDate: '2015-07-14T21:15:18-04:00',
                                        name: 'asdasd',
                                        notes: 'somenotes',
                                        placementsCount: '10',
                                        publisherId: '5010696',
                                        status: 'Accepted',
                                        totalAdSpend: '100'
                                    }
                                ]
                            }
                        ]
                    });
                promise = InsertionOrderService.getList('5998696');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        createdDate: '2015-05-21T13:15:40-04:00',
                        id: '6009383',
                        ioNumber: '1',
                        logicalDelete: 'N',
                        mediaBuyId: '5030908',
                        modifiedDate: '2015-07-03T19:41:51-04:00',
                        name: 'UI Test - Automated generated IO',
                        notes: 'Automated generated UI',
                        placementsCount: '10',
                        publisherId: '5010696',
                        status: 'Accepted',
                        totalAdSpend: '100'
                    },
                    {
                        createdDate: '2015-07-14T21:15:18-04:00',
                        id: '6031011',
                        ioNumber: '123456',
                        logicalDelete: 'N',
                        mediaBuyId: '5032564',
                        modifiedDate: '2015-07-14T21:15:18-04:00',
                        name: 'asdasd',
                        notes: 'somenotes',
                        placementsCount: '10',
                        publisherId: '5010696',
                        status: 'Accepted',
                        totalAdSpend: '100'
                    }
                ]);
            });

            it('should wrap single record response in an array', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'InsertionOrders?query=' + encodeURIComponent('campaignId=5998696'))
                    .respond({
                        records: [
                            {
                                InsertionOrder: {
                                    createdDate: '2015-05-21T13:15:40-04:00',
                                    id: '6009383',
                                    ioNumber: '1',
                                    logicalDelete: 'N',
                                    mediaBuyId: '5030908',
                                    modifiedDate: '2015-07-03T19:41:51-04:00',
                                    name: 'UI Test - Automated generated IO',
                                    notes: 'Automated generated UI',
                                    placementsCount: '10',
                                    publisherId: '5010696',
                                    status: 'Accepted',
                                    totalAdSpend: '100'
                                }
                            }
                        ]
                    });
                promise = InsertionOrderService.getList('5998696');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        createdDate: '2015-05-21T13:15:40-04:00',
                        id: '6009383',
                        ioNumber: '1',
                        logicalDelete: 'N',
                        mediaBuyId: '5030908',
                        modifiedDate: '2015-07-03T19:41:51-04:00',
                        name: 'UI Test - Automated generated IO',
                        notes: 'Automated generated UI',
                        placementsCount: '10',
                        publisherId: '5010696',
                        status: 'Accepted',
                        totalAdSpend: '100'
                    }
                ]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'InsertionOrders?query=' + encodeURIComponent('campaignId=5998696'))
                    .respond(404);
                InsertionOrderService.getList('5998696');
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'InsertionOrders?query=' + encodeURIComponent('campaignId=5998696'))
                    .respond(404, 'FAILED');
                promise = InsertionOrderService.getList('5998696');
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getInsertionOrder()', function () {
        it('should get a Insertion Order based on the given id', function () {
            var ioId = 1234567,
                promise,
                ioResponse = {
                    campaignId: 6031581,
                    createdDate: '2015-07-28T12:51:35-04:00',
                    id: 1234567,
                    ioNumber: '1234567',
                    lastUpdated: '2015-07-28T14:26:02-04:00',
                    lastUpdatedAuthor: 'UI Truforce USER UI Truforce USER',
                    logicalDelete: 'N',
                    mediaBuyId: 5034699,
                    modifiedDate: '2015-07-28T14:26:02-04:00',
                    name: 'someIOName',
                    notes: 'somenotes',
                    placementsCount: 0,
                    publisherId: 5011330,
                    status: 'Rejected',
                    totalAdSpend: 0
                };

            $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/' + ioId).respond(ioResponse);
            promise = InsertionOrderService.getInsertionOrder(ioId);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(ioResponse);
        });

        it('should get an error due a request of a Insertion Order with a wrong id', inject(function () {
            var ioId = 0,
                ioResponse = 404;

            $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/' + ioId).respond(ioResponse);
            InsertionOrderService.getInsertionOrder(ioId);
            $httpBackend.flush();

            expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
        }));
    });

    describe('getPackagePlacements()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].PackagePlacementView', function () {
                var promise,
                    packagePlacementResponse = [
                        {
                            adSpend: 0,
                            endDate: '2015-08-08T00:00:00-04:00',
                            height: 100,
                            id: 9110480,
                            inventory: 1,
                            name: 'sport - Header - 100x100',
                            rate: 0,
                            rateType: 'CPM',
                            siteName: 'sport',
                            startDate: '2015-07-09T00:00:00-04:00',
                            status: 'Accepted',
                            width: 100
                        },
                        {
                            adSpend: 0,
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

                $httpBackend.expectGET(
                        API_SERVICE + 'InsertionOrders/9110478/packagePlacementView')
                    .respond({
                        records: [
                            {
                                PackagePlacementView: packagePlacementResponse
                            }
                        ]
                    });
                promise = InsertionOrderService.getPackagePlacements('9110478');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(packagePlacementResponse);
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    packagePlacementResponse = {
                        adSpend: 0,
                        endDate: '2015-08-08T00:00:00-04:00',
                        height: 100,
                        id: 9110480,
                        inventory: 1,
                        name: 'sport - Header - 100x100',
                        rate: 0,
                        rateType: 'CPM',
                        siteName: 'sport',
                        startDate: '2015-07-09T00:00:00-04:00',
                        status: 'Accepted',
                        width: 100
                    };

                $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/9110478/packagePlacementView')
                    .respond({
                        records: [
                            {
                                PackagePlacementView: packagePlacementResponse
                            }
                        ]
                    });
                promise = InsertionOrderService.getPackagePlacements('9110478');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    packagePlacementResponse
                ]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/9110478/packagePlacementView')
                    .respond(404);
                InsertionOrderService.getPackagePlacements('9110478');
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/9110478/packagePlacementView')
                    .respond(404, 'FAILED');
                promise = InsertionOrderService.getPackagePlacements('9110478');
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('updateInsertionOrder()', function () {
        var insertionOrder;

        beforeEach(function () {
            insertionOrder = {
                campaignId: 6031581,
                createdDate: '2015-07-28T12:51:35-04:00',
                id: 1234567,
                ioNumber: '1234567',
                lastUpdated: '2015-07-28T14:26:02-04:00',
                lastUpdatedAuthor: 'UI Truforce USER UI Truforce USER',
                logicalDelete: 'N',
                mediaBuyId: 5034699,
                modifiedDate: '2015-07-28T14:26:02-04:00',
                name: 'someIOName',
                notes: 'somenotes',
                placementsCount: 0,
                publisherId: 5011330,
                status: 'Planning',
                totalAdSpend: 0
            };
        });

        it('should PUT all data', function () {
            var promise;

            insertionOrder.name = 'updatedIOName';
            insertionOrder.ioNumber = '987654321';
            insertionOrder.status = 'Accepted';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'InsertionOrders/1234567',
                {
                    campaignId: 6031581,
                    createdDate: '2015-07-28T12:51:35-04:00',
                    id: 1234567,
                    ioNumber: '987654321',
                    lastUpdated: '2015-07-28T14:26:02-04:00',
                    lastUpdatedAuthor: 'UI Truforce USER UI Truforce USER',
                    logicalDelete: 'N',
                    mediaBuyId: 5034699,
                    modifiedDate: '2015-07-28T14:26:02-04:00',
                    name: 'updatedIOName',
                    notes: 'somenotes',
                    placementsCount: 0,
                    publisherId: 5011330,
                    status: 'Accepted',
                    totalAdSpend: 0
                }
            ).respond({});
            promise = InsertionOrderService.updateInsertionOrder(insertionOrder);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('saveInsertionOrder()', function () {
        it('should create a new Insertion Order', function () {
            var promise,
                iODetails = {
                    campaignId: 6031581,
                    createdDate: '2015-07-15T12:51:35-04:00',
                    createdTpwsKey: 'e2c8a0bb-71b0-4fb5-8cec-bf4d37806eba',
                    ioNumber: '123456',
                    lastUpdated: '2015-07-15T14:26:02-04:00',
                    lastUpdatedAuthor: 'UI Truforce USER UI Truforce USER',
                    logicalDelete: 'N',
                    mediaBuyId: 5034699,
                    modifiedDate: '2015-07-15T14:26:02-04:00',
                    modifiedTpwsKey: '8ffb4b7e-cdb1-497b-a71c-2894efead3a5',
                    name: 'richard',
                    notes: 'somenotes',
                    placementsCount: 0,
                    publisherId: 5011330,
                    status: 'Rejected',
                    totalAdSpend: 0
                };

            $httpBackend.expectPOST(API_SERVICE + 'InsertionOrders').respond(200);
            promise = InsertionOrderService.saveInsertionOrder(iODetails);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(iODetails);
        });
    });

    describe('saveBulkPackagePlacement()', function () {
        var placements;

        beforeEach(function () {
            placements = [
                {
                    adSpend: 0,
                    endDate: new Date('2015-08-08T00:00:00-07:00'),
                    height: 100,
                    inventory: 1,
                    name: 'sport - Header - 100x100',
                    rate: 0,
                    rateType: 'CPM',
                    siteName: 'sport',
                    startDate: new Date('2015-07-09T00:00:00-07:00'),
                    status: 'Accepted',
                    width: 100
                },
                {
                    adSpend: 0,
                    endDate: new Date('2015-08-08T00:00:00-07:00'),
                    height: 150,
                    inventory: 1,
                    name: 'AOL - Header - 150x150',
                    rate: 0,
                    rateType: 'CPM',
                    siteName: 'sport',
                    startDate: new Date('2015-07-09T00:00:00-07:00'),
                    status: 'Accepted',
                    width: 150
                }
            ];
        });

        it('should save the placements under Insertion Order', function () {
            var promise;

            $httpBackend.expectPOST(API_SERVICE + 'InsertionOrders/9110478/bulkPackagePlacement').respond(200);

            promise = InsertionOrderService.saveBulkPackagePlacement(9110478, placements);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });
});
