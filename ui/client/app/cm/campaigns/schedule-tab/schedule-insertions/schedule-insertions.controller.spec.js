'use strict';

describe('Controller: ScheduleInsertionsController', function () {
    var $modalInstance,
        $scope,
        $state,
        $stateParams,
        $timezones,
        $httpBackend,
        API_SERVICE,
        CONSTANTS,
        CampaignsService,
        CreativeInsertionService,
        DialogFactory,
        ScheduleInsertionsController,
        ScheduleTabController,
        creativeInsertionList,
        rootScope,
        dialogDeferred,
        checkedList,
        scheduleInsertions,
        DateTimeService,
        lodash;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            CreativeInsertionService = jasmine.createSpyObj('CreativeInsertionService', ['bulkUpdate']);
            DialogFactory = jasmine.createSpyObj('DialogFactory', ['showCustomDialog']);
            $provide.value('CreativeInsertionService', CreativeInsertionService);
        });

        inject(function ($q) {
            var defer = $q.defer();

            defer.resolve();
            defer.$promise = defer.promise;
            CreativeInsertionService.bulkUpdate.andReturn(defer.promise);
            creativeInsertionList = $q.defer();

            dialogDeferred = $q.defer();
            DialogFactory.showCustomDialog.andReturn({
                result: dialogDeferred.promise
            });
        });

        installPromiseMatchers();
    });

    beforeEach(inject(function ($controller,
                                $q,
                                $rootScope,
                                _$state_,
                                _API_SERVICE_,
                                _CONSTANTS_,
                                _CreativeInsertionService_,
                                _CampaignsService_,
                                _DateTimeService_,
                                _lodash_,
                                _$httpBackend_) {
        var data = {},
            defer = $q.defer();

        $httpBackend = _$httpBackend_;
        $stateParams = {};
        $timezones = {};

        rootScope = $rootScope;
        API_SERVICE = _API_SERVICE_;
        CONSTANTS = _CONSTANTS_;
        CreativeInsertionService = _CreativeInsertionService_;
        CampaignsService = _CampaignsService_;
        DateTimeService = _DateTimeService_;
        lodash = _lodash_;
        $modalInstance = {
            dismiss: jasmine.createSpy('dismiss')
        };
        $scope = $rootScope.$new();
        scheduleInsertions = data.scheduleInsertions = checkedList = [
            {
                expanded: true,
                loadData: true,
                siteId: 6086072,
                siteLabel: 'SITE - Mare tests (6086072)',
                children: [
                    {
                        expanded: true,
                        loadData: true,
                        level: 1,
                        siteSectionId: 6086073,
                        siteSectionLabel: 'SiteSection - Mare tests (6086073)',
                        children: [
                            {
                                expanded: true,
                                loadData: true,
                                creativeGroupAssociations: 1,
                                flightDates: '09/11/2015 — 10/11/2015',
                                level: 2,
                                placementId: 6158783,
                                placementLabel: 'PL180x50 (6158783)',
                                placementStatus: 'New',
                                placementStatusLabel: 'Planning',
                                children: [
                                    {
                                        expanded: true,
                                        loadData: true,
                                        creativeAssociations: 1,
                                        creativeGroupId: 34158,
                                        creativeGroupLabel: 'Default (34158)',
                                        level: 3,
                                        placementAssociations: 1,
                                        weight: 100,
                                        flightDateEnd: '10/30/2015',
                                        flightDateStart: '09/30/2015',
                                        children: [
                                            {
                                                expanded: false,
                                                loadData: false,
                                                clickThroughUrl: ['http://www.google.com'],
                                                creativeGroupAssociations: 1,
                                                creativeId: 6158650,
                                                creativeLabel: '168x20image2 (6158650)',
                                                flightDates: '09/03/2015 — 10/03/2015',
                                                id: 10329241,
                                                level: 4,
                                                placementAssociations: 1,
                                                weight: 51,
                                                flightDateEnd: '10/30/2015',
                                                flightDateStart: '09/30/2015'
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];
        $state = _$state_;
        spyOn($state, 'go');
        spyOn($scope, '$on');
        spyOn(CampaignsService, 'bulkDeleteCreativeInsertions').andReturn(defer.promise);
        spyOn(CampaignsService, 'searchCreativeInsertions').andReturn(creativeInsertionList.promise);
        $scope.mainVm = {};
        $scope.mainVm.status = {
            cm: {
                schedule: {
                    isFlyoutVisible: false
                }
            }
        };

        ScheduleTabController = $controller('ScheduleTabController', {
            $scope: $scope,
            $stateParams: $stateParams,
            $timezones: $timezones,
            CampaignsService: CampaignsService,
            DialogFactory: DialogFactory,
            DateTimeService: DateTimeService,
            lodash: lodash
        });

        $scope.$parent.vmList = ScheduleTabController;

        ScheduleInsertionsController = $controller('ScheduleInsertionsController', {
            $modalInstance: $modalInstance,
            $scope: $scope,
            CONSTANTS: CONSTANTS,
            CreativeInsertionService: CreativeInsertionService,
            data: data,
            DialogFactory: DialogFactory,
            DateTimeService: DateTimeService
        });
    }));

    describe('$scope', function () {
        describe('vm', function () {
            it('should be the controller', function () {
                var vmMock = {
                    DATE_FORMAT: 'MM/dd/yyyy',
                    submodel: null,
                    selectionMode: 'MULTI'
                };

                expect(vmMock.DATE_FORMAT).toBe(ScheduleInsertionsController.DATE_FORMAT);
                expect(vmMock.submodel).toBe(ScheduleInsertionsController.submodel);
                expect(vmMock.selectionMode).toBe(ScheduleInsertionsController.selectionMode);
            });

            it('Should create an instance of the controller.', function () {
                expect(ScheduleInsertionsController).not.toBeUndefined();
            });
        });
    });

    describe('delete()', function () {
        it('should invoke CampaignsService.bulkDeleteCreativeInsertions()', function () {
            ScheduleInsertionsController.allMarkedAsChecked[0] = {};
            ScheduleInsertionsController.hierarchicalRemove();

            expect(CampaignsService.bulkDeleteCreativeInsertions).toHaveBeenCalled();
        });
    });

    describe('getDateRenderer()', function () {
        it('should invoke getDateRenderer()', function () {
            var mockDate = '09/11/2015',
                resultDate = ScheduleInsertionsController.getDateRenderer(mockDate);

            expect(resultDate).toEqual('09/11/2015');
        });
    });

    describe('confirmDelete()', function () {
        it('should verify if a parent was checked then show a dialog confirmation', function () {
            ScheduleInsertionsController.getAllChecked([
                {
                    $$uuid: '46656',
                    children: [
                        {
                            id: 55656
                        }
                    ]
                }
            ]);

            ScheduleInsertionsController.hierarchicalRemove();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalled();
        });
    });

    describe('confirmClose()', function () {
        it('should not show a dialog confirmation if not changes were made', function () {
            ScheduleInsertionsController.close();

            expect(DialogFactory.showCustomDialog).not.toHaveBeenCalled();
        });

        it('should show a dialog confirmation if any changes were made', function () {
            ScheduleInsertionsController.isGridValid = true;
            ScheduleInsertionsController.field = 'placement'; //Placement level
            ScheduleInsertionsController.close();

            expect(DialogFactory.showCustomDialog).toHaveBeenCalled();
        });
    });

    describe('bulkUpdate()', function () {
        it('should save the model', function () {
            ScheduleInsertionsController.submodel = scheduleInsertions;

            ScheduleInsertionsController.promise = null;
            ScheduleInsertionsController.bulkUpdate();
            expect(ScheduleInsertionsController.promise).not.toBeNull();
        });
    });

    describe('applyTo()', function () {
        it('should apply bulk edit for creative to model', function () {
            var creativeEntity,
                weight = 99,
                flightDateStart = '01/01/2015',
                flightDateEnd = '01/31/2015',
                clickThroughUrl = 'http://www.trueffect.com',
                updateAttributes = {
                    field: CONSTANTS.SCHEDULE.LEVEL.SCHEDULE.KEY,
                    weight: weight,
                    flightDateStart: flightDateStart,
                    flightDateEnd: flightDateEnd,
                    clickThroughUrl: clickThroughUrl
                };

            ScheduleInsertionsController.submodel = scheduleInsertions;
            creativeEntity = ScheduleInsertionsController.submodel[0].children[0].children[0].children[0].children[0];
            creativeEntity.field = CONSTANTS.SCHEDULE.LEVEL.SCHEDULE.KEY;
            creativeEntity._visible = true;

            creativeEntity.checked = false;
            ScheduleInsertionsController.applyTo(updateAttributes);
            expect(creativeEntity.weight).toEqual(51);
            expect(creativeEntity.flightDateStart).toEqual('09/30/2015');
            expect(creativeEntity.flightDateEnd).toEqual('10/30/2015');
            expect(creativeEntity.clickThroughUrl[0]).toEqual('http://www.google.com');

            creativeEntity.checked = true;
            ScheduleInsertionsController.applyTo(updateAttributes);
            expect(creativeEntity.weight).toEqual(weight);
            expect(creativeEntity.flightDateStart).toEqual(flightDateStart);
            expect(creativeEntity.flightDateEnd).toEqual(flightDateEnd);
            expect(creativeEntity.clickThroughUrl[0]).toEqual(clickThroughUrl);
        });

        it('should apply bulk edit for creative group to model', function () {
            var creativeGroupEntity,
                weight = 99,
                updateAttributes = {
                    field: CONSTANTS.SCHEDULE.LEVEL.CREATIVE_GROUP.KEY,
                    weight: weight
                };

            ScheduleInsertionsController.submodel = scheduleInsertions;
            creativeGroupEntity = ScheduleInsertionsController.submodel[0].children[0].children[0].children[0];
            creativeGroupEntity.field = CONSTANTS.SCHEDULE.LEVEL.CREATIVE_GROUP.KEY;
            creativeGroupEntity._visible = true;

            creativeGroupEntity.checked = false;
            ScheduleInsertionsController.applyTo(updateAttributes);
            expect(creativeGroupEntity.weight).toEqual(100);

            creativeGroupEntity.checked = true;
            ScheduleInsertionsController.applyTo(updateAttributes);
            expect(creativeGroupEntity.weight).toEqual(weight);
        });
    });

    describe('getAllChecked()', function () {
        it('should get all nodes as checked', function () {
            ScheduleInsertionsController.submodel = scheduleInsertions;
            ScheduleInsertionsController.getAllChecked(checkedList);
            expect(ScheduleInsertionsController.allMarkedAsChecked).toEqual(checkedList);
        });

        it('should get leaf node as checked', function () {
            var leafNode = [
                {
                    expanded: false,
                    loadData: false,
                    clickThroughUrl: ['http://www.google.com'],
                    creativeGroupAssociations: 1,
                    creativeId: 6158650,
                    creativeLabel: '168x20image2 (6158650)',
                    flightDates: '09/03/2015 — 10/03/2015',
                    id: 10329241,
                    level: 4,
                    placementAssociations: 1,
                    weight: 51,
                    flightDateEnd: '10/30/2015',
                    flightDateStart: '09/30/2015'
                }
            ];

            ScheduleInsertionsController.submodel = scheduleInsertions;
            ScheduleInsertionsController.searchServerText = 'text';
            ScheduleInsertionsController.getAllChecked(checkedList);
            expect(ScheduleInsertionsController.allMarkedAsChecked).toEqual(leafNode);
        });
    });

    describe('Search Server Flyout with levels differents ', function () {
        var response;

        beforeEach(function () {
            $httpBackend.whenGET(API_SERVICE +
                'Campaigns/creativeInsertions?pivotType=site&type=site').respond(200, '');
            ScheduleInsertionsController.selectedPivot = {};
            ScheduleInsertionsController.selectedPivot.KEY = CONSTANTS.SCHEDULE.LEVEL.CREATIVE.KEY;
        });

        it('should set Creative List in flyout when  level Site is selected', function () {
            scheduleInsertions = [
                {
                    checked: false,
                    creativeGroupId: undefined,
                    creativeGroupLabel: undefined,
                    creativeId: undefined,
                    creativeLabel: undefined,
                    expanded: false,
                    field: 'site',
                    id: undefined,
                    loadData: false,
                    nextLevelName: 'section',
                    oldKey: 'c0e92301-7a13-4654-a518-4291232f1fb3',
                    siteDetailLabel: '1-Site-Mare (6291689)',
                    siteId: 6291689,
                    siteLabel: '1-Site-Mare (6291689)',
                    siteName: '1-Site-Mare',
                    children: [
                        {
                            creativeGroupLabel: undefined,
                            creativeLabel: undefined,
                            placementLabel: null,
                            siteLabel: '1-Site-Mare (6291689)',
                            siteSectionLabel: null
                        }
                    ]
                }
            ];

            response = [
                {
                    siteId: 6086072,
                    siteName: 'SITE - Mare tests',
                    siteSectionId: 6086073,
                    siteSectionName: 'SiteSection - Mare tests'
                },
                {
                    siteId: 6086072,
                    siteName: 'SITE - Mare tests'
                },
                {
                    placementAssociationsWithCreativeGroups: 3,
                    placementEndDate: '2016-02-19T23:59:59-07:00',
                    placementId: 6512359,
                    placementName: '1-Site-Mare - 1-Section-Mare - 100x100',
                    placementStartDate: '2016-01-20T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    siteId: 6291689,
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionName: '1-Section-Mare'
                },
                {
                    siteId: 6291689,
                    siteName: '1-Site-Mare'
                }
            ];

            ScheduleInsertionsController.submodel = scheduleInsertions;
            ScheduleInsertionsController.serverSideSearch('site');
            creativeInsertionList.resolve(response);
            $scope.$apply();
            $httpBackend.flush();

            expect(ScheduleInsertionsController.submodel).toEqual(
                [
                    {
                        checked: false,
                        creativeGroupId: undefined,
                        creativeGroupLabel: undefined,
                        creativeId: undefined,
                        creativeLabel: undefined,
                        expanded: true,
                        field: 'site',
                        id: undefined,
                        loadData: true,
                        nextLevelName: 'section',
                        oldKey: 'c0e92301-7a13-4654-a518-4291232f1fb3',
                        siteDetailLabel: '1-Site-Mare (6291689)',
                        siteId: 6291689,
                        siteLabel: '1-Site-Mare (6291689)',
                        siteName: '1-Site-Mare',
                        children: [
                            {
                                siteId: 6086072,
                                loadData: false,
                                field: 'section',
                                nextLevelName: 'placement',
                                siteSectionId: 6086073,
                                siteLabel: 'SITE - Mare tests (6086072)',
                                siteName: 'SITE - Mare tests',
                                siteSectionLabel: 'SiteSection - Mare tests (6086073)',
                                siteSectionName: 'SiteSection - Mare tests',
                                siteDetailLabel: 'SiteSection - Mare tests (6086073)',
                                expanded: false,
                                children: [
                                    {
                                        siteLabel: 'SITE - Mare tests (6086072)',
                                        siteSectionLabel: 'SiteSection - Mare tests (6086073)',
                                        placementLabel: null,
                                        creativeGroupLabel: null,
                                        creativeLabel: null,
                                        checked: false
                                    }
                                ],
                                treeRowId: '077bd281b883faf5549b543a173b2e6243910352',
                                backUpChildren: [],
                                checked: false
                            }
                        ]
                    }
                ]);
        });

        it('should set Creative List in flyout when  level Section is selected', function () {
            scheduleInsertions = [
                {
                    expanded: false,
                    field: 'section',
                    loadData: false,
                    nextLevelName: 'placement',
                    siteDetailLabel: '1-Section-Mare',
                    siteId: 6291689,
                    siteLabel: '1-Site-Mare (6291689)',
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionLabel: '1-Section-Mare (6291690)',
                    siteSectionName: '1-Section-Mare (6291690)',
                    children: [
                        {
                            creativeGroupLabel: null,
                            creativeLabel: null,
                            placementLabel: null,
                            siteLabel: '1-Site-Mare (6291689)',
                            siteSectionLabel: '1-Section-Mare (6291690)'
                        }
                    ]
                }
            ];

            response = [
                {
                    placementAssociationsWithCreativeGroups: 2,
                    placementEndDate: '2015-12-13T23:59:59-07:00',
                    placementId: 6315747,
                    placementName: 'chicken-rechicken',
                    placementStartDate: '2015-11-13T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    siteId: 6291689,
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionName: '1-Section-Mare'
                }
            ];

            ScheduleInsertionsController.submodel = scheduleInsertions;
            ScheduleInsertionsController.serverSideSearch('chicken-rechicken');
            creativeInsertionList.resolve(response);
            $scope.$apply();
            $httpBackend.flush();

            expect(ScheduleInsertionsController.submodel).toEqual(
                [
                    {
                        expanded: true,
                        field: 'section',
                        loadData: true,
                        nextLevelName: 'placement',
                        siteDetailLabel: '1-Section-Mare',
                        siteId: 6291689,
                        siteLabel: '1-Site-Mare (6291689)',
                        siteName: '1-Site-Mare',
                        siteSectionId: 6291690,
                        siteSectionLabel: '1-Section-Mare (6291690)',
                        siteSectionName: '1-Section-Mare (6291690)',
                        children: [
                            {
                                id: undefined,
                                siteId: 6291689,
                                loadData: false,
                                field: 'placement',
                                nextLevelName: 'group',
                                siteSectionId: 6291690,
                                creativeId: undefined,
                                creativeGroupId: undefined,
                                creativeGroupLabel: undefined,
                                creativeLabel: undefined,
                                placementId: 6315747,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: undefined,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare',
                                placementLabel: 'chicken-rechicken (6315747)',
                                siteDetailLabel: 'chicken-rechicken (6315747)',
                                placementStatus: 'Accepted',
                                placementStatusLabel: 'Active',
                                creativeGroupAssociations: 2,
                                flightDates: undefined,
                                flightDateStart: undefined,
                                flightDateEnd: undefined,
                                sizeName: undefined,
                                expanded: false,
                                children: [
                                    {
                                        siteLabel: '1-Site-Mare (6291689)',
                                        siteSectionLabel: '1-Section-Mare (6291690)',
                                        placementLabel: 'chicken-rechicken (6315747)',
                                        creativeGroupLabel: undefined,
                                        creativeLabel: undefined,
                                        checked: false
                                    }
                                ],
                                treeRowId: '990317d7337b8017c0ca93a2e2724a24ea6dd9ba',
                                backUpChildren: [],
                                placementTagAssociationIds: {
                                    siteId: 6291689,
                                    sectionId: 6291690
                                },
                                checked: false
                            }
                        ],
                        checked: false
                    }
                ]);
        });

        it('should set Creative List in flyout when  level Placement is selected', function () {
            scheduleInsertions = [
                {
                    creativeGroupAssociations: 2,
                    creativeGroupId: undefined,
                    creativeGroupLabel: undefined,
                    creativeId: undefined,
                    creativeLabel: undefined,
                    expanded: false,
                    field: 'placement',
                    flightDateEnd: undefined,
                    flightDateStart: undefined,
                    flightDates: undefined,
                    id: undefined,
                    loadData: false,
                    nextLevelName: 'group',
                    placementId: 6315747,
                    placementLabel: 'chicken-rechicken (6315747)',
                    placementStatus: 'Accepted',
                    placementStatusLabel: 'Active',
                    siteDetailLabel: 'chicken-rechicken (6315747)',
                    siteId: 6291689,
                    siteLabel: '1-Site-Mare (6291689)',
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionLabel: '1-Section-Mare (6291690)',
                    siteSectionName: '1-Section-Mare (6291690)',
                    sizeName: undefined,
                    children: [
                        {
                            creativeGroupLabel: undefined,
                            creativeLabel: undefined,
                            placementLabel: 'chicken-rechicken (6315747)',
                            siteLabel: '1-Site-Mare (6291689)',
                            siteSectionLabel: '1-Section-Mare (6291690)'
                        }
                    ]
                }
            ];

            response = [
                {
                    campaignId: 6158572,
                    createdDate: '2016-03-10T11:11:33-07:00',
                    createdTpwsKey: 'a21cffb5-546e-42b6-b0fa-4beeee83cbf2',
                    creativeAlias: 'Pro',
                    creativeAssociationsWithCreativeGroups: 1,
                    creativeAssociationsWithPlacements: 2,
                    creativeGroupAssociationsWithCreatives: 1,
                    creativeGroupAssociationsWithPlacements: 4,
                    creativeGroupDoCookieTargeting: 0,
                    creativeGroupDoDaypartTarget: 0,
                    creativeGroupDoGeoTargeting: 0,
                    creativeGroupFrequencyCap: 1,
                    creativeGroupFrequencyCapWindow: 24,
                    creativeGroupId: 6345544,
                    creativeGroupName: 'My GC',
                    creativeGroupPriority: 0,
                    creativeGroupRotationType: 'Weighted',
                    creativeGroupWeight: 55,
                    creativeGroupWeightEnabled: 0,
                    creativeId: 6405348,
                    creativeInsertionRootId: 6467249,
                    creativeType: 'jpg',
                    endDate: '2015-12-13T23:59:59-07:00',
                    filename: 'Pro.jpg',
                    id: 10424909,
                    logicalDelete: 'N',
                    modifiedDate: '2016-03-10T11:11:33-07:00',
                    modifiedTpwsKey: 'a21cffb5-546e-42b6-b0fa-4beeee83cbf2',
                    placementAssociationsWithCreativeGroups: 1,
                    placementEndDate: '2015-12-13T23:59:59-07:00',
                    placementId: 6315747,
                    placementName: 'chicken-rechicken',
                    placementStartDate: '2015-11-13T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    primaryClickthrough: 'http://www.trueffect.com',
                    released: 0,
                    sequence: 0,
                    siteId: 6291689,
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionName: '1-Section-Mare',
                    sizeName: '300x250',
                    startDate: '2015-11-13T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                }
            ];

            ScheduleInsertionsController.submodel = scheduleInsertions;
            ScheduleInsertionsController.serverSideSearch('pro');
            creativeInsertionList.resolve(response);
            $scope.$apply();
            $httpBackend.flush();

            expect(ScheduleInsertionsController.submodel).toEqual(
                [
                    {
                        creativeGroupAssociations: 2,
                        creativeGroupId: undefined,
                        creativeGroupLabel: undefined,
                        creativeId: undefined,
                        creativeLabel: undefined,
                        expanded: true,
                        field: 'placement',
                        flightDateEnd: undefined,
                        flightDateStart: undefined,
                        flightDates: undefined,
                        id: undefined,
                        loadData: true,
                        nextLevelName: 'group',
                        placementId: 6315747,
                        placementLabel: 'chicken-rechicken (6315747)',
                        placementStatus: 'Accepted',
                        placementStatusLabel: 'Active',
                        siteDetailLabel: 'chicken-rechicken (6315747)',
                        siteId: 6291689,
                        siteLabel: '1-Site-Mare (6291689)',
                        siteName: '1-Site-Mare',
                        siteSectionId: 6291690,
                        siteSectionLabel: '1-Section-Mare (6291690)',
                        siteSectionName: '1-Section-Mare (6291690)',
                        sizeName: undefined,
                        children: [
                            {
                                siteId: 6291689,
                                loadData: true,
                                field: 'group',
                                nextLevelName: 'schedule',
                                creativeId: 6405348,
                                siteSectionId: 6291690,
                                placementId: 6315747,
                                creativeGroupId: 6345544,
                                siteLabel: '1-Site-Mare (6291689)',
                                creativeLabel: 'Pro',
                                siteName: undefined,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare',
                                placementLabel: 'chicken-rechicken (6315747)',
                                creativeGroupLabel: 'My GC (6345544)',
                                siteDetailLabel: 'My GC (6345544)',
                                placementAssociations: 4,
                                creativeAssociations: 1,
                                weight: 55,
                                frequencyCap: 1,
                                frequencyCapWindow: 24,
                                creativeGroupPriority: 0,
                                creativeGroupDoCookieTarget: 0,
                                creativeGroupDoGeoTarget: 0,
                                creativeGroupDoDayPartTarget: 0,
                                flightDates: undefined,
                                flightDateStart: undefined,
                                flightDateEnd: undefined,
                                expanded: true,
                                editSupport: {
                                    weight: true
                                },
                                children: [
                                    {
                                        id: 10424909,
                                        field: 'schedule',
                                        loadData: undefined,
                                        nextLevelName: undefined,
                                        siteId: 6291689,
                                        siteSectionId: 6291690,
                                        placementId: 6315747,
                                        creativeGroupId: 6345544,
                                        creativeId: 6405348,
                                        siteLabel: '1-Site-Mare (6291689)',
                                        siteName: undefined,
                                        siteSectionLabel: '1-Section-Mare (6291690)',
                                        siteSectionName: '1-Section-Mare',
                                        placementLabel: 'chicken-rechicken (6315747)',
                                        creativeGroupLabel: 'My GC (6345544)',
                                        creativeLabel: 'Pro (6405348)',
                                        creativeName: 'Pro',
                                        creativeType: 'jpg',
                                        siteDetailLabel: 'Pro (6405348)',
                                        sizeName: '300x250',
                                        filename: 'Pro.jpg',
                                        placementAssociations: 2,
                                        creativeGroupAssociations: 1,
                                        weight: 100,
                                        expanded: false,
                                        flightDates: '11/13/2015 12:00:00 AM — 12/13/2015 11:59:59 PM',
                                        flightDateStart: '11/13/2015 12:00:00 AM',
                                        flightDateEnd: '12/13/2015 11:59:59 PM',
                                        clickThroughUrl: [
                                            'http://www.trueffect.com'
                                        ],
                                        editSupport: {
                                            clickThroughUrl: true,
                                            flightDateStart: true,
                                            flightDateEnd: true,
                                            weight: true
                                        },
                                        children: null,
                                        treeRowId: '2f168ba7409f9ccddbcf53963c49ef6be0bdc865',
                                        backUpChildren: [],
                                        checked: false
                                    }
                                ],
                                treeRowId: '32457e1d12c7dcdcf60ffbf9a31b2b8ea046b7aa',
                                backUpChildren: [],
                                checked: false
                            }
                        ],
                        checked: false
                    }
                ]);
        });

        it('should set Creative List in flyout when level Creative Group is selected', function () {
            scheduleInsertions = [
                {
                    creativeAssociations: 1,
                    creativeGroupDoCookieTarget: 0,
                    creativeGroupDoDayPartTarget: 0,
                    creativeGroupDoGeoTarget: 0,
                    creativeGroupId: 34158,
                    creativeGroupLabel: 'Default (34158)',
                    creativeGroupPriority: 0,
                    creativeLabel: undefined,
                    expanded: false,
                    field: 'group',
                    flightDateEnd: undefined,
                    flightDateStart: undefined,
                    flightDates: undefined,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    loadData: false,
                    nextLevelName: 'schedule',
                    placementAssociations: 2,
                    placementId: 6512359,
                    placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                    siteDetailLabel: 'Default (34158)',
                    siteId: 6291689,
                    siteLabel: '1-Site-Mare (6291689)',
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionLabel: '1-Section-Mare (6291690)',
                    siteSectionName: '1-Section-Mare (6291690)',
                    weight: 21,
                    children: [
                        {
                            reativeGroupLabel: 'Default (34158)',
                            creativeLabel: undefined,
                            placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                            siteLabel: '1-Site-Mare (6291689)',
                            siteSectionLabel: '1-Section-Mare (6291690)'
                        }
                    ]
                }
            ];

            response = [
                {
                    campaignId: 6158572,
                    createdDate: '2016-03-10T11:11:33-07:00',
                    createdTpwsKey: 'a21cffb5-546e-42b6-b0fa-4beeee83cbf2',
                    creativeAlias: 'Chiken-2',
                    creativeAssociationsWithCreativeGroups: 3,
                    creativeAssociationsWithPlacements: 1,
                    creativeGroupAssociationsWithCreatives: 1,
                    creativeGroupAssociationsWithPlacements: 4,
                    creativeGroupDoCookieTargeting: 0,
                    creativeGroupDoDaypartTarget: 0,
                    creativeGroupDoGeoTargeting: 0,
                    creativeGroupFrequencyCap: 1,
                    creativeGroupFrequencyCapWindow: 24,
                    creativeGroupId: 34158,
                    creativeGroupName: 'Default',
                    creativeGroupPriority: 0,
                    creativeGroupRotationType: 'Weighted',
                    creativeGroupWeight: 66,
                    creativeGroupWeightEnabled: 0,
                    creativeId: 6315745,
                    creativeInsertionRootId: 6532864,
                    creativeType: 'zip',
                    endDate: '2016-02-19T23:59:59-07:00',
                    filename: 'Chiken-2.zip',
                    id: 10424901,
                    logicalDelete: 'N',
                    modifiedDate: '2016-03-10T11:11:33-07:00',
                    modifiedTpwsKey: 'a21cffb5-546e-42b6-b0fa-4beeee83cbf2',
                    placementAssociationsWithCreativeGroups: 3,
                    placementEndDate: '2016-02-19T23:59:59-07:00',
                    placementId: 6512359,
                    placementName: '1-Site-Mare - 1-Section-Mare - 100x100',
                    placementStartDate: '2016-01-20T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    primaryClickthrough: 'http://www.trueffect.com',
                    released: 0,
                    sequence: 0,
                    siteId: 6291689,
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionName: '1-Section-Mare',
                    sizeName: '100x100',
                    startDate: '2016-01-20T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100,
                    additionalClickthroughs: [
                        {
                            sequence: 3,
                            url: 'http://www.trueffect3.com'
                        },
                        {
                            sequence: 4,
                            url: 'http://www.trueffect4.com'
                        },
                        {
                            sequence: 2,
                            url: 'http://www.trueffect2.com'
                        }
                    ]
                }
            ];

            ScheduleInsertionsController.submodel = scheduleInsertions;
            ScheduleInsertionsController.serverSideSearch('chiken');
            creativeInsertionList.resolve(response);
            $scope.$apply();
            $httpBackend.flush();

            expect(ScheduleInsertionsController.submodel).toEqual(
                [
                    {
                        creativeAssociations: 1,
                        creativeGroupDoCookieTarget: 0,
                        creativeGroupDoDayPartTarget: 0,
                        creativeGroupDoGeoTarget: 0,
                        creativeGroupId: 34158,
                        creativeGroupLabel: 'Default (34158)',
                        creativeGroupPriority: 0,
                        creativeLabel: undefined,
                        expanded: true,
                        field: 'group',
                        flightDateEnd: undefined,
                        flightDateStart: undefined,
                        flightDates: undefined,
                        frequencyCap: 1,
                        frequencyCapWindow: 24,
                        loadData: true,
                        nextLevelName: 'schedule',
                        placementAssociations: 2,
                        placementId: 6512359,
                        placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                        siteDetailLabel: 'Default (34158)',
                        siteId: 6291689,
                        siteLabel: '1-Site-Mare (6291689)',
                        siteName: '1-Site-Mare',
                        siteSectionId: 6291690,
                        siteSectionLabel: '1-Section-Mare (6291690)',
                        siteSectionName: '1-Section-Mare (6291690)',
                        weight: 21,
                        children: [
                            {
                                id: 10424901,
                                field: 'schedule',
                                loadData: undefined,
                                nextLevelName: undefined,
                                siteId: 6291689,
                                siteSectionId: 6291690,
                                placementId: 6512359,
                                creativeGroupId: 34158,
                                creativeId: 6315745,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: undefined,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare',
                                placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                creativeGroupLabel: 'Default (34158)',
                                creativeLabel: 'Chiken-2 (6315745)',
                                creativeName: 'Chiken-2',
                                creativeType: 'zip',
                                siteDetailLabel: 'Chiken-2 (6315745)',
                                sizeName: '100x100',
                                filename: 'Chiken-2.zip',
                                placementAssociations: 1,
                                creativeGroupAssociations: 3,
                                weight: 100,
                                expanded: false,
                                flightDates: '01/20/2016 12:00:00 AM — 02/19/2016 11:59:59 PM',
                                flightDateStart: '01/20/2016 12:00:00 AM',
                                flightDateEnd: '02/19/2016 11:59:59 PM',
                                clickThroughUrl: [
                                    'http://www.trueffect.com',
                                    'http://www.trueffect2.com',
                                    'http://www.trueffect3.com',
                                    'http://www.trueffect4.com'
                                ],
                                editSupport: {
                                    clickThroughUrl: true,
                                    flightDateStart: true,
                                    flightDateEnd: true,
                                    weight: true
                                },
                                children: null,
                                treeRowId: 'c0345184e35b8578c3d57192a155141208b8a8e9',
                                backUpChildren: [],
                                checked: false
                            }
                        ],
                        checked: false
                    }
                ]);
        });
    });
});
