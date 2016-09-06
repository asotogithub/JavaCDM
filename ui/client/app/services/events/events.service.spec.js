'use strict';

describe('Service: Event', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        ErrorRequestHandler,
        EventsService;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _ErrorRequestHandler_, _EventsService_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        EventsService = _EventsService_;
        installPromiseMatchers();
    }));

    describe('getEvents()', function () {
        describe('on success', function () {
            it('should get object for all Events for the site measurement with id 9046516', function () {
                var promise,
                    response = {
                        startIndex: '0',
                        pageSize: '1000',
                        totalNumberOfRecords: '4',
                        records: [
                            {
                                SmEventDTO: [
                                    {
                                        createdDate: '2015-06-09T09:55:15.402-04:00',
                                        createdTpwsKey: 'SmEvent 0-0',
                                        eventName: 'SmEvent 0-0',
                                        eventType: '1',
                                        id: '-69828409744182027',
                                        isTrafficked: '0',
                                        smEventType: '1',
                                        smGroupId: '-7516336341617334174'
                                    },
                                    {
                                        createdDate: '2015-06-09T09:55:15.402-04:00',
                                        createdTpwsKey: 'SmEvent 0-1',
                                        eventName: 'SmEvent 0-1',
                                        eventType: '1',
                                        id: '2823942526320719263',
                                        isTrafficked: '0',
                                        smEventType: '1',
                                        smGroupId: '-7516336341617334174'
                                    },
                                    {
                                        createdDate: '2015-06-09T09:55:15.402-04:00',
                                        createdTpwsKey: 'SmEvent 0-2',
                                        eventName: 'SmEvent 0-2',
                                        eventType: '1',
                                        id: '-9139611596872338034',
                                        isTrafficked: '0',
                                        smEventType: '1',
                                        smGroupId: '-7516336341617334174'
                                    },
                                    {
                                        createdDate: '2015-06-09T09:55:15.402-04:00',
                                        createdTpwsKey: 'SmEvent 0-3',
                                        eventName: 'SmEvent 0-3',
                                        eventType: '1',
                                        id: '6956649014634549747',
                                        isTrafficked: '0',
                                        smEventType: '1',
                                        smGroupId: '-7516336341617334174'
                                    }
                                ]
                            }
                        ]
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9046516/events').respond(response);
                promise = EventsService.getEvents(9046516);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(response);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9046516/events').respond(404);
                EventsService.getEvents(9046516);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9046516/events').respond(404, 'FAILED');
                promise = EventsService.getEvents(9046516);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('get Pings', function () {
        describe('on success', function () {
            it('should get details and pings for the event with id 9046516', function () {
                var promise,
                    response = {
                        createdDate: '2016-08-02T13:44:21-07:00',
                        createdTpwsKey: '40d3ef79-bf28-4fa2-a3b4-9370b27b310f',
                        eventName: 'Event1',
                        eventType: 1,
                        id: 15375,
                        isTrafficked: 1,
                        logicalDelete: 'N',
                        measurementState: 3,
                        modifiedDate: '2016-08-02T13:46:41-07:00',
                        modifiedTpwsKey: '40d3ef79-bf28-4fa2-a3b4-9370b27b310f',
                        pingEvents: [
                            {
                                eventName: 'Event1',
                                eventType: 1,
                                groupName: '01_PZ_Group',
                                id: 15375,
                                pingContent: 'http://ad.adlegend.com',
                                pingId: 18057,
                                pingTagType: 0,
                                pingType: 1,
                                siteId: 9428174,
                                siteName: 'siteName',
                                smEventType: 0,
                                smGroupId: 11665
                            },
                            {
                                eventName: 'Event1',
                                eventType: 1,
                                groupName: '01_PZ_Group',
                                id: 15375,
                                pingContent: 'http://ad.adlegend.com',
                                pingId: 18058,
                                pingTagType: 0,
                                pingType: 2,
                                siteId: 9428174,
                                siteName: 'sitename)',
                                smEventType: 0,
                                smGroupId: 11665
                            }
                        ],
                        smEventType: 0,
                        smGroupId: 11665
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurementEvents/9046516').respond(response);
                promise = EventsService.getPings(9046516);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(response);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurementEvents/9046516').respond(404);
                EventsService.getPings(9046516);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('update Event', function () {
        describe('on success', function () {
            it('should update event with id 9046516', function () {
                var promise,
                    payload = {
                        createdDate: '2016-07-26T12:53:09-07:00',
                        createdTpwsKey: 'd4a121db-c3e3-4f2e-9817-b9c450e52da2',
                        eventName: 'SEvent1',
                        eventType: 1,
                        id: 9046516,
                        isTrafficked: 0,
                        location: 'sdescription1',
                        logicalDelete: 'N',
                        modifiedDate: '2016-07-26T12:53:09-07:00',
                        modifiedTpwsKey: 'd4a121db-c3e3-4f2e-9817-b9c450e52da2',
                        smEventType: 1,
                        smGroupId: 5001165
                    };

                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurementEvents/9046516').respond(200);
                promise = EventsService.updateDetails(9046516, payload);
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurementEvents/9046516').respond(404);
                EventsService.updateDetails(9046516);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('Check if event name is unique', function () {
        describe('on success', function () {
            it('should check if event name is unique', function () {
                var eventName = 'eventName',
                    promise,
                    response = {
                        result: true
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurementEvents?query=' + eventName).respond(response);
                promise = EventsService.checkEventNameUnique(eventName);
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                var eventName = 'AnEventName';

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurementEvents?query=' + eventName).respond(404);
                EventsService.checkEventNameUnique(eventName);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('Save event', function () {
        describe('on success', function () {
            it('should save an event', function () {
                var promise;

                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurementEvents').respond({});
                promise = EventsService.saveEvent({});
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurementEvents').respond(404);
                EventsService.saveEvent({});
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });
});
