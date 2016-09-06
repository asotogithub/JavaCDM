'use strict';

describe('Service: SiteMeasurementGroups', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        ErrorRequestHandler,
        SiteMeasurementGroupsService;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (
        _$httpBackend_,
        _$q_,
        _API_SERVICE_,
        _ErrorRequestHandler_,
        _SiteMeasurementGroupsService_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        SiteMeasurementGroupsService = _SiteMeasurementGroupsService_;
        installPromiseMatchers();
    }));

    describe('Check if group name is unique', function () {
        describe('on success', function () {
            it('should check if group name is unique', function () {
                var groupName = 'groupName',
                    promise,
                    response = {
                        result: true
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurementGroups?query=' + groupName).respond(response);
                promise = SiteMeasurementGroupsService.checkGroupNameUnique(groupName);
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                var groupName = 'AGroupName';

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurementGroups?query=' + groupName).respond(404);
                SiteMeasurementGroupsService.checkGroupNameUnique(groupName);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('get Groups', function () {
        describe('on success', function () {
            it('should get groups for the Site Measurement ID 6786916', function () {
                var promise,
                    groupList = [
                        {
                            id: 1,
                            groupName: 'group01'
                        },
                        {
                            id: 2,
                            groupName: 'group02'
                        },
                        {
                            id: 3,
                            groupName: 'group03'
                        },
                        {
                            id: 4,
                            groupName: 'group04'
                        }
                    ],
                    response = {
                        startIndex: '0',
                        pageSize: '1000',
                        totalNumberOfRecords: '4',
                        records: [
                            {
                                SmGroup: groupList
                            }
                        ]
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9063954/groups').respond(response);
                promise = SiteMeasurementGroupsService.getGroupList(9063954);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(groupList);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9063954/groups').respond(404);
                SiteMeasurementGroupsService.getGroupList(9063954);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('Save event', function () {
        describe('on success', function () {
            it('should save an event', function () {
                var promise;

                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurementGroups').respond({});
                promise = SiteMeasurementGroupsService.saveEvent({});
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurementGroups').respond(404);
                SiteMeasurementGroupsService.saveEvent({});
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });
});
