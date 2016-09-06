(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('EventsService', EventsService);

    EventsService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler'
    ];

    function EventsService($q,
                           $resource,
                           API_SERVICE,
                           ErrorRequestHandler) {
        var siteMeasurementEventsResource = $resource(API_SERVICE + 'SiteMeasurementEvents/:id/:criteria',
            {
                id: '@id',
                criteria: '@criteria'
            },
            {
                get: {
                    method: 'GET'
                },
                update: {
                    method: 'PUT'
                }
            }),
            siteMeasurementsResource = $resource(API_SERVICE + 'SiteMeasurements/:id/:criteria',
                {
                    id: '@id',
                    criteria: '@criteria'
                }, {
                    get: {
                        method: 'GET'
                    },
                    update: {
                        method: 'PUT'
                    }
                }
            ),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            checkEventNameUnique: function (eventName) {
                var measurementsDefer = $q.defer();

                siteMeasurementEventsResource.get({
                    query: eventName
                }).$promise
                    .then(checkEventNameUniqueComplete)
                    .catch(errorHandler('Cannot check if event name is unique', measurementsDefer));

                function checkEventNameUniqueComplete(response) {
                    measurementsDefer.resolve(response);
                }

                return measurementsDefer.promise;
            },

            getEvents: function (idSiteMeasurement) {
                var events = $q.defer();

                siteMeasurementsResource.get({
                    id: idSiteMeasurement,
                    criteria: 'events'
                }).$promise
                    .then(getEventsComplete)
                    .catch(errorHandler('Cannot get events', events));

                function getEventsComplete(response) {
                    events.resolve(response);
                }

                return events.promise;
            },

            getPings: function (idEvent) {
                var pings = $q.defer();

                siteMeasurementEventsResource.get({
                    id: idEvent
                }).$promise
                    .then(getPingsEvents)
                    .catch(errorHandler('Cannot get pings', pings));

                function getPingsEvents(response) {
                    pings.resolve(response);
                }

                return pings.promise;
            },

            updateDetails: function (idEvent, payload) {
                var eventUpdate = $q.defer();

                siteMeasurementEventsResource.update({
                    id: idEvent
                }, payload).$promise
                    .then(updateEventDetailsSuccess)
                    .catch(errorHandler('Cannot update event details', eventUpdate));

                function updateEventDetailsSuccess(response) {
                    eventUpdate.resolve(response);
                }

                return eventUpdate.promise;
            },

            saveEvent: function (event) {
                var measurementsDefer = $q.defer();

                siteMeasurementEventsResource.save(event).$promise
                    .then(saveEventComplete)
                    .catch(errorHandler('Error while performing save event request', measurementsDefer));

                function saveEventComplete(eventData) {
                    measurementsDefer.resolve(eventData.toJSON());
                }

                return measurementsDefer.promise;
            }
        };
    }
})();
