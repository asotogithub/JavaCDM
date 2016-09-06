'use strict';

describe('Controller: TagInjectionFlyOutController', function () {
    var $controller,
        $compile,
        $httpBackend,
        $q,
        $scope,
        $window,
        API_SERVICE,
        DialogFactory,
        PlacementsTableFilterService,
        TagInjectionService,
        TagInjectionFlyOutController,
        trackingTags,
        trackingTagsUpdate,
        dialogDeferred,
        trackingTagsPlacements,
        placementList = [
            {
                campaignId: 5011922,
                campaignName: '24c_test',
                endDate: '2013-01-13T23:59:59-07:00',
                id: 5011925,
                name: 'Placement1',
                siteId: 5011518,
                siteName: 'Site2',
                startDate: '2012-12-14T00:00:00-07:00'
            }, {
                campaignId: 5011061,
                campaignName: 'Piggy_back_QA',
                endDate: '2013-01-10T23:59:59-07:00',
                id: 5011520,
                name: 'Placement2',
                siteId: 5011518,
                siteName: 'Site2',
                startDate: '2012-12-11T00:00:00-07:00'
            }, {
                campaignId: 5011061,
                campaignName: 'Piggy_back_QA',
                endDate: '2013-01-11T23:59:59-07:00',
                id: 5011707,
                name: 'Placement3',
                siteId: 5011005,
                siteName: 'PubSite',
                startDate: '2012-12-12T00:00:00-07:00'
            }
        ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$controller_,
                                _$httpBackend_,
                                _$q_,
                                _$rootScope_,
                                _API_SERVICE_,
                                _PlacementsTableFilterService_,
                                _TagInjectionService_,
                                _$compile_,
                                _$window_) {
        $compile = _$compile_;
        $window = _$window_;
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        $scope = _$rootScope_.$new();
        API_SERVICE = _API_SERVICE_;
        PlacementsTableFilterService = _PlacementsTableFilterService_;
        TagInjectionService = _TagInjectionService_;
        trackingTags = $q.defer();
        trackingTagsPlacements = $q.defer();
        trackingTagsUpdate = $q.defer();
        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showDialog', 'showCustomDialog']);
        $scope.vmTeFlyOutController =
        {
            flyOutModel: {
                data: {
                    id: 123456
                }
            }

        };
        dialogDeferred = $q.defer();
        DialogFactory.showCustomDialog.andReturn({
            result: dialogDeferred.promise
        });
        TagInjectionFlyOutController = $controller('TagInjectionFlyOutController', {
            $scope: $scope,
            PlacementsTableFilterService: PlacementsTableFilterService,
            TagInjectionService: TagInjectionService,
            DialogFactory: DialogFactory
        });
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $httpBackend.whenGET(API_SERVICE +
            'HtmlInjectionTags/' + $scope.vmTeFlyOutController.flyOutModel.data.id).respond(
            {
                agencyId: 6031295,
                createdDate: '2012-12-11T13:22:20-07:00',
                htmlContent: '<SCRIPT SRC=\"http://qa.adlegend.net/jscript?NIF=N&target=_blank&@CPSC@=\"></SCRIPT>',
                id: 246,
                isEnabled: 1,
                isVisible: 0,
                modifiedDate: '2016-06-23T13:23:33-07:00',
                name: 'Adserver JSCRIPt1'
            });
        $httpBackend.whenGET(API_SERVICE +
            'HtmlInjectionTags/' + $scope.vmTeFlyOutController.flyOutModel.data.id + '/placementAssociated').respond(
            {
                totalNumberOfRecords: 3,
                records: [
                    {
                        PlacementView: [
                            {
                                campaignId: 5011922,
                                campaignName: '24c_test',
                                endDate: '2013-01-13T23:59:59-07:00',
                                id: 5011925,
                                name: 'Placement1',
                                siteId: 5011518,
                                siteName: 'Site2',
                                startDate: '2012-12-14T00:00:00-07:00'
                            },
                            {
                                campaignId: 5011061,
                                campaignName: 'Piggy_back_QA',
                                endDate: '2013-01-10T23:59:59-07:00',
                                id: 5011520,
                                name: 'Placement2',
                                siteId: 5011518,
                                siteName: 'Site2',
                                startDate: '2012-12-11T00:00:00-07:00'
                            },
                            {
                                campaignId: 5011061,
                                campaignName: 'Piggy_back_QA',
                                endDate: '2013-01-11T23:59:59-07:00',
                                id: 5011707,
                                name: 'Placement3',
                                siteId: 5011005,
                                siteName: 'PubSite',
                                startDate: '2012-12-12T00:00:00-07:00'
                            }
                        ]
                    }
                ]
            });

        installPromiseMatchers();
    }));

    describe('activate()', function () {
        it('should create an instance of the controller.', function () {
            expect(TagInjectionFlyOutController).not.toBeUndefined();
        });

        it('should load the filters and their list of options.', function () {
            $httpBackend.flush();
            expect(TagInjectionFlyOutController.filterValues[0].values.length).toEqual(2);
            expect(TagInjectionFlyOutController.filterValues[1].values.length).toEqual(2);
            expect(TagInjectionFlyOutController.filterValues[2].values.length).toEqual(3);
            expect(TagInjectionFlyOutController.filterOptions[0].value.length).toEqual(2);
            expect(TagInjectionFlyOutController.filterOptions[1].value.length).toEqual(2);
            expect(TagInjectionFlyOutController.filterOptions[2].value.length).toEqual(3);
        });
    });

    describe('Update()', function () {
        var deferred,
            modelUpdate = {
                name: 'updated tag name',
                id: 123456
            };

        beforeEach(inject(function () {
            deferred = $q.defer();
            spyOn(TagInjectionService, 'updateHtmlInjectionTag').andReturn(deferred.promise);
            spyOn(TagInjectionService, 'getHtmlInjectionTagsById').andReturn(deferred.promise);
            spyOn(TagInjectionService, 'getTagPlacementsAssociated').andReturn(deferred.promise);
        }));

        it('should invoke TagInjectionService', function () {
            $httpBackend.flush();
            expect(TagInjectionService.getHtmlInjectionTagsById).toHaveBeenCalledWith(modelUpdate.id);
            expect(TagInjectionService.getTagPlacementsAssociated).toHaveBeenCalledWith(modelUpdate.id);
        });

        it('should invoke TagInjectionFlyOutController.updateTagInjection()', function () {
            TagInjectionFlyOutController.model = modelUpdate;
            TagInjectionFlyOutController.updateTagInjection();
            expect(TagInjectionService.updateHtmlInjectionTag).toHaveBeenCalled();
        });
    });

    describe('Close()', function () {
        it('should show a popup before close fly-out', inject(function (CONSTANTS, $translate) {
            TagInjectionFlyOutController.flyOutForm.$dirty = true;

            TagInjectionFlyOutController.close();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('global.confirm.closeUnsavedChanges'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.DISCARD_CANCEL
            });
        }));
    });

    describe('searchPlacements()', function () {
        var searchResult;

        beforeEach(inject(function () {
            searchResult = $q.defer();
            spyOn(TagInjectionService, 'searchPlacementView').andReturn(searchResult.promise);
        }));

        it('should load data from search', function () {
            searchResult.resolve(placementList);
            TagInjectionFlyOutController.searchPlacements('Site2');
            $httpBackend.flush();
            $scope.$apply();
            expect(TagInjectionFlyOutController.associations).toEqual(
                [
                    {
                        campaignId: 5011922,
                        campaignName: '24c_test',
                        endDate: '2013-01-13T23:59:59-07:00',
                        id: 5011925,
                        name: 'Placement1',
                        siteId: 5011518,
                        siteName: 'Site2',
                        startDate: '2012-12-14T00:00:00-07:00'
                    },
                    {
                        campaignId: 5011061,
                        campaignName: 'Piggy_back_QA',
                        endDate: '2013-01-10T23:59:59-07:00',
                        id: 5011520,
                        name: 'Placement2',
                        siteId: 5011518,
                        siteName: 'Site2',
                        startDate: '2012-12-11T00:00:00-07:00'
                    },
                    {
                        campaignId: 5011061,
                        campaignName: 'Piggy_back_QA',
                        endDate: '2013-01-11T23:59:59-07:00',
                        id: 5011707,
                        name: 'Placement3',
                        siteId: 5011005,
                        siteName: 'PubSite',
                        startDate: '2012-12-12T00:00:00-07:00'
                    }
                ]
            );
        });
    });
});
