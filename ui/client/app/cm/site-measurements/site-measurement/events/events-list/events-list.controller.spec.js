'use strict';

describe('Controller: SiteMeasurementEventsController', function () {
    var controller,
        $scope,
        $state,
        $stateParams,
        EventService,
        SiteMeasurementModel,
        mockRetrievedObject;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            EventService = jasmine.createSpyObj('AdvertiserService', ['getEvents']);
            $provide.value('EventsService', EventService);
        });

        inject(function ($q) {
            var defer = $q.defer();

            mockRetrievedObject = {
                    startIndex: '0',
                    pageSize: '1000',
                    totalNumberOfRecords: '1',
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
                                }
                            ]
                        }
                    ]
                };

            defer.resolve(mockRetrievedObject);
            EventService.getEvents.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_, _SiteMeasurementModel_) {
        $rootScope.vm = {
            selectTab: function () {}
        };

        $scope = $rootScope.$new();
        $state = _$state_;
        $stateParams =
        {
            siteMeasurementId: 9046516
        };
        SiteMeasurementModel = _SiteMeasurementModel_;
        spyOn($state, 'go');
        spyOn(SiteMeasurementModel, 'getSiteMeasurement');
        controller = $controller('SiteMeasurementEventsController', {
            $scope: $scope,
            $stateParams: $stateParams,
            SiteMeasurementModel: SiteMeasurementModel
        });
    }));

    describe('activate()', function () {
        it('should set default values', function () {
            $scope.$apply();
            expect(controller.eventsList).toEqual(mockRetrievedObject.records[0].SmEventDTO);
            expect(controller.totalEvents).toBe(mockRetrievedObject.records[0].SmEventDTO.length);
        });
    });

    describe('onSearchCounter()', function () {
        it('should set row counter message', function () {
            $scope.$apply();
            controller.onSearchCounter(1);
            expect(controller.eventPingLegend).toEqual('1 Events');
            controller.onSearchCounter(0);
            expect(controller.eventPingLegend).toEqual('0 of 1 Events');
        });
    });

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should resolve the promise to get the events object for Site Measurement with id 9046516', function () {
        expect(controller.retrieveEventsList(9046516)).toBeResolved();
    });

    it('Should obtain all the events for Site Measurement with id 9046516', function () {
        expect(controller.retrieveEventsList().$$state.value.records[0].SmEventDTO.length).toBe(1);
    });
});
