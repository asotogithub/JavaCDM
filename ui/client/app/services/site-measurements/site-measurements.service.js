(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SiteMeasurementsService', SiteMeasurementsService);

    SiteMeasurementsService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function SiteMeasurementsService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var siteMeasurementsResource = $resource(API_SERVICE + 'SiteMeasurements/:id/:criteria/:criteriaId',
                {
                    id: '@id',
                    criteria: '@criteria'
                }, {
                    get: {
                        method: 'GET'
                    },
                    update: {
                        method: 'PUT'
                    },
                    delete: {
                        method: 'DELETE'
                    }
                }
            ),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getCampaigns: function (siteMeasurementId, associated) {
                var measurementsDefer = $q.defer();

                siteMeasurementsResource.get({
                    id: siteMeasurementId,
                    criteria: 'campaigns',
                    type: associated ? 'ASSOCIATED' : 'UNASSOCIATED'
                }).$promise
                    .then(getCampaignsComplete)
                    .catch(errorHandler('Cannot get Campaigns', measurementsDefer));

                function getCampaignsComplete(response) {
                    measurementsDefer.resolve(
                        [].concat(lodash.result(response, 'records[0].SiteMeasurementCampaignDTO', []))
                    );
                }

                return measurementsDefer.promise;
            },

            getSiteMeasurements: function (siteMeasurementId, criteria) {
                var measurementsDefer = $q.defer();

                siteMeasurementsResource.get({
                    id: siteMeasurementId,
                    criteria: criteria
                }).$promise
                    .then(getSiteMeasurementsComplete)
                    .catch(errorHandler('Cannot get Site Measurements', measurementsDefer));

                function getSiteMeasurementsComplete(response) {
                    measurementsDefer.resolve(response);
                }

                return measurementsDefer.promise;
            },

            updateSiteMeasurement: function (siteMeasurement, id) {
                var measurementsDefer = $q.defer();

                siteMeasurementsResource.update(
                    {
                        id: id
                    },
                    siteMeasurement).$promise
                    .then(updateSiteMeasurementComplete)
                    .catch(errorHandler('Cannot update Site Measurement', measurementsDefer));

                function updateSiteMeasurementComplete(response) {
                    measurementsDefer.resolve(response);
                }

                return measurementsDefer.promise;
            },

            saveCampaign: function (campaign) {
                var measurementsDefer = $q.defer();

                siteMeasurementsResource.save(campaign).$promise
                    .then(saveCampaignComplete)
                    .catch(errorHandler('Error while performing save campaign request', measurementsDefer));

                function saveCampaignComplete(campaignData) {
                    measurementsDefer.resolve(campaignData.toJSON());
                }

                return measurementsDefer.promise;
            },

            updateCampaignAssociation: function (siteMeasurementId, campaignAssociation) {
                var measurementsDefer = $q.defer();

                siteMeasurementsResource.update(
                    {
                        id: siteMeasurementId,
                        criteria: 'campaigns'
                    },
                    {
                        records: [
                            {
                                SiteMeasurementCampaignDTO: campaignAssociation
                            }
                        ]
                    }).$promise
                    .then(updateCampaignAssociationComplete)
                    .catch(errorHandler('Cannot update campaign association', measurementsDefer));

                function updateCampaignAssociationComplete(response) {
                    measurementsDefer.resolve(response);
                }

                return measurementsDefer.promise;
            }
        };
    }
})();
