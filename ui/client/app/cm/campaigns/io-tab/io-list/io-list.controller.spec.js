'use strict';

describe('Controller: InsertionOrderListTabController', function () {
    var $q,
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        DateTimeService,
        CampaignsService,
        InsertionOrderService,
        campaignId,
        controller,
        insertionOrderList,
        placementList;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _$q_,
                                _$state_,
                                _$stateParams_,
                                _$translate_,
                                _CONSTANTS_,
                                _InsertionOrderService_,
                                _CampaignsService_,
                                _DateTimeService_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        $stateParams = _$stateParams_;
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        InsertionOrderService = _InsertionOrderService_;
        CampaignsService = _CampaignsService_;
        campaignId = $stateParams.campaignId = '5998696';
        insertionOrderList = $q.defer();
        placementList = $q.defer();
        DateTimeService = _DateTimeService_;

        spyOn($state, 'go');
        spyOn(InsertionOrderService, 'getList').andReturn(insertionOrderList.promise);
        spyOn(CampaignsService, 'getPackagePlacements').andReturn(placementList.promise);

        controller = $controller('InsertionOrderListTabController', {
            $scope: $scope,
            $stateParams: $stateParams,
            CampaignsService: CampaignsService,
            InsertionOrderService: InsertionOrderService
        });

        installPromiseMatchers();
    }));

    describe('activate()', function () {
        it('should invoke InsertionOrderService.getList()', function () {
            expect(InsertionOrderService.getList).toHaveBeenCalledWith(campaignId);
        });

        it('should invoke CampaignsService.getPackagePlacements()', function () {
            expect(CampaignsService.getPackagePlacements).toHaveBeenCalledWith(campaignId);
        });

        it('should set promise from InsertionOrderService.getList() and CampaignsService.getPackagePlacements()',
            function () {
                expect(controller.promise).not.toBeNull();
                expect(controller.promise).not.toBeResolved();

                insertionOrderList.resolve();
                expect(controller.promise).not.toBeResolved();

                placementList.resolve();
                expect(controller.promise).toBeResolved();
            });

        it('should set insertionOrderList when promise is resolved', function () {
            var placementsCount1 = '10',
                placementsCount2 = '20';

            insertionOrderList.resolve([
                {
                    id: '6009383',
                    ioNumber: '1',
                    logicalDelete: 'N',
                    mediaBuyId: '5030908',
                    name: 'UI Test - Automated generated IO',
                    notes: 'Automated generated UI',
                    placementsCount: placementsCount1,
                    publisherId: '5010696',
                    status: 'Active',
                    totalAdSpend: '100'
                },
                {
                    id: '6031011',
                    ioNumber: '123456',
                    logicalDelete: 'N',
                    mediaBuyId: '5032564',
                    name: 'IO 2',
                    notes: 'somenotes',
                    placementsCount: placementsCount2,
                    publisherId: '5010696',
                    status: 'Active',
                    totalAdSpend: '100'
                }
            ]);
            placementList.resolve([
                {
                    adSpend: 0,
                    campaignId: 9024566,
                    countryCurrencyId: 1,
                    endDate: DateTimeService.applyOffset('2015-09-19T00:00:00-07:00'),
                    height: 150,
                    id: 6101580,
                    inventory: 1,
                    ioId: 6009383,
                    isScheduled: 'N',
                    isTrafficked: 0,
                    logicalDelete: 'N',
                    maxFileSize: 1,
                    placementName: 'Test',
                    packageId: 6101578,
                    packageName: 'Test',
                    rate: 0,
                    rateType: 'CPM',
                    resendTags: 0,
                    siteId: 6086072,
                    siteName: 'SITE - tests',
                    siteSectionId: 6101579,
                    sizeId: 5006631,
                    sizeName: '180x150',
                    startDate: DateTimeService.applyOffset('2015-08-20T00:00:00-07:00'),
                    status: 'New',
                    utcOffset: 0,
                    width: 180
                },
                {
                    adSpend: 0,
                    campaignId: 9024566,
                    countryCurrencyId: 1,
                    endDate: DateTimeService.applyOffset('2015-09-19T00:00:00-07:00'),
                    height: 150,
                    id: 6101581,
                    inventory: 1,
                    ioId: 6031011,
                    isScheduled: 'N',
                    isTrafficked: 0,
                    logicalDelete: 'N',
                    maxFileSize: 1,
                    placementName: 'Test2',
                    packageId: 6101579,
                    packageName: 'Test2',
                    rate: 0,
                    rateType: 'CPM',
                    resendTags: 0,
                    siteId: 6086072,
                    siteName: 'SITE - tests',
                    siteSectionId: 6101579,
                    sizeId: 5006631,
                    sizeName: '180x150',
                    startDate: DateTimeService.applyOffset('2015-08-20T00:00:00-07:00'),
                    status: 'New',
                    utcOffset: 0,
                    width: 180
                }
            ]);
            $scope.$apply();

            expect(controller.insertionOrderList).toEqual(
                [
                    {
                        id: '6009383',
                        ioNumber: '1',
                        logicalDelete: 'N',
                        mediaBuyId: '5030908',
                        notes: 'Automated generated UI',
                        publisherId: '5010696',
                        status: 'Active',
                        adSpend: '100',
                        endDate: DateTimeService.applyOffset('2015-09-19T00:00:00-07:00'),
                        ioName: 'UI Test - Automated generated IO',
                        placementName: placementsCount1,
                        startDate: DateTimeService.applyOffset('2015-08-20T00:00:00-07:00'),
                        children: [
                            {
                                adSpend: 0,
                                campaignId: 9024566,
                                countryCurrencyId: 1,
                                endDate: DateTimeService.applyOffset('2015-09-19T00:00:00-07:00'),
                                height: 150,
                                id: 6101580,
                                inventory: 1,
                                ioId: 6009383,
                                isScheduled: 'N',
                                isTrafficked: 0,
                                logicalDelete: 'N',
                                maxFileSize: 1,
                                packageId: 6101578,
                                packageName: 'Test',
                                rate: 0,
                                rateType: 'CPM',
                                resendTags: 0,
                                siteId: 6086072,
                                siteName: 'SITE - tests',
                                siteSectionId: 6101579,
                                sizeId: 5006631,
                                sizeName: '180x150',
                                startDate: DateTimeService.applyOffset('2015-08-20T00:00:00-07:00'),
                                status: 'Planning',
                                utcOffset: 0,
                                width: 180,
                                ioName: 'UI Test - Automated generated IO',
                                ioNumber: '1',
                                placementName: 'Test'
                            }
                        ]
                    },
                    {
                        id: '6031011',
                        ioNumber: '123456',
                        logicalDelete: 'N',
                        mediaBuyId: '5032564',
                        notes: 'somenotes',
                        publisherId: '5010696',
                        status: 'Active',
                        adSpend: '100',
                        endDate: DateTimeService.applyOffset('2015-09-19T00:00:00-07:00'),
                        ioName: 'IO 2',
                        placementName: placementsCount2,
                        startDate: DateTimeService.applyOffset('2015-08-20T00:00:00-07:00'),
                        children: [
                            {
                                adSpend: 0,
                                campaignId: 9024566,
                                countryCurrencyId: 1,
                                endDate: DateTimeService.applyOffset('2015-09-19T00:00:00-07:00'),
                                height: 150,
                                id: 6101581,
                                inventory: 1,
                                ioId: 6031011,
                                isScheduled: 'N',
                                isTrafficked: 0,
                                logicalDelete: 'N',
                                maxFileSize: 1,
                                packageId: 6101579,
                                packageName: 'Test2',
                                rate: 0,
                                rateType: 'CPM',
                                resendTags: 0,
                                siteId: 6086072,
                                siteName: 'SITE - tests',
                                siteSectionId: 6101579,
                                sizeId: 5006631,
                                sizeName: '180x150',
                                startDate: DateTimeService.applyOffset('2015-08-20T00:00:00-07:00'),
                                status: 'Planning',
                                utcOffset: 0,
                                width: 180,
                                ioName: 'IO 2',
                                ioNumber: '123456',
                                placementName: 'Test2'
                            }
                        ]
                    }
                ]
            );

            expect(controller.legendMedia).toEqual(
                $translate.instant('media.rowsIOsPlacements', {
                    rowsIO: controller.insertionOrderList.length,
                    rowsPlacement: parseInt(placementsCount1) + parseInt(placementsCount2)
                }));
        });
    });

    describe('addIo()', function () {
        it('should go() to `io-add` state', function () {
            controller.addIo();

            expect($state.go).toHaveBeenCalledWith('io-add');
        });
    });

    describe('cellClassName()', function () {
        it('should return undefined for insertion order rows', function () {
            expect(controller.cellClassName(0, {
                level: 0
            })).toBeUndefined();
            expect(controller.cellClassName(1, {
                level: 0
            })).toBeUndefined();
        });

        it('should return `placement-cell-*` for placement rows', function () {
            expect(controller.cellClassName(0, {
                level: 1
            })).toEqual('placement-cell-odd');
            expect(controller.cellClassName(1, {
                level: 1
            })).toEqual('placement-cell-even');
        });
    });

    describe('editIoPlacement()', function () {
        it('should go() to `campaign-io-details` state', function () {
            var io = {
                id: 1337
            };

            controller.selectionLevel = 0;
            controller.editIoPlacement(io);

            expect($state.go).toHaveBeenCalledWith('campaign-io-details', {
                campaignId: campaignId,
                ioId: io.id,
                io: io
            });
        });

        it('should go() to `edit-placement` state', function () {
            var placement = {
                campaignId: 1337,
                ioId: 1345,
                id: 1337
            };

            controller.selectionLevel = 1;
            controller.editIoPlacement(placement);

            expect($state.go).toHaveBeenCalledWith('edit-placement', {
                campaignId: campaignId,
                ioId: placement.ioId,
                placementId: placement.placementId,
                from: 'io-list'
            });
        });
    });

    describe('filterByLevel()', function () {
        it('should return cellText only if rowData.level is visibleLevel', function () {
            expect(controller.filterByLevel(0, {
                level: 0
            }, 'foobar')).toEqual('foobar');

            expect(controller.filterByLevel(1, {
                level: 0
            }, 'foobar')).toEqual('');
        });
    });

    describe('getStatusName()', function () {
        it('should $translate status name', function () {
            expect(controller.getStatusName('New')).toEqual('Planning');
            expect(controller.getStatusName('Accepted')).toEqual('Active');
            expect(controller.getStatusName('Rejected')).toEqual('Inactive');
            expect(controller.getStatusName('notValid')).toEqual('notValid');
        });
    });

    describe('onClearFiltering()', function () {
        it('should set legendMedia', function () {
            controller.legendMedia = '';
            expect(controller.legendMedia).toBe('');

            controller.onClearFiltering();
            expect(controller.legendMedia).toBe(
                $translate.instant('media.rowsIOsPlacements', {
                    rowsIO: 0,
                    rowsPlacement: 0
                }));
        });
    });

    describe('onFilterApplied()', function () {
        it('should set legendMedia', function () {
            var rows = [
                {
                    records: [
                        {
                            dummy: '1'
                        },
                        {
                            dummy: '2'
                        }
                    ]
                }
            ];

            controller.legendMedia = '';
            expect(controller.legendMedia).toBe('');

            controller.onFilterApplied(rows);
            expect(controller.legendMedia).toBe(
                $translate.instant('media.rowsIOsPlacements', {
                    rowsIO: rows.length,
                    rowsPlacement: rows[0].records.length
                }));
        });
    });

    describe('onSelect()', function () {
        it('should set selection and selectionLevel', function () {
            var selection = {
                id: 69
            };

            controller.onSelect(selection, 1);

            expect(controller.selection).toBe(selection);
            expect(controller.selectionLevel).toEqual(1);
        });
    });

    describe('Media template URL', function () {
        it('should have template url equal as defined in constants', function () {
            var templateUrl = CONSTANTS.INSERTION_ORDER.IMPORT.TEMPLATE_URL;

            expect(controller.TEMPLATE_PATH).toEqual(templateUrl);
        });

        it('should not need to encode template url', function () {
            var templateUrl = encodeURI(CONSTANTS.INSERTION_ORDER.IMPORT.TEMPLATE_URL);

            expect(controller.TEMPLATE_PATH).toEqual(templateUrl);
        });
    });

    describe('exportAll()', function () {
        it('should invoke CampaignsService.exportResource()', function () {
            spyOn(CampaignsService, 'exportResource').andReturn(controller.promise);
            controller.exportAll();
            expect(CampaignsService.exportResource).toHaveBeenCalledWith(
                campaignId,
                CONSTANTS.INSERTION_ORDER.FILE_TYPE.XLSX,
                CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION);
        });
    });
});
