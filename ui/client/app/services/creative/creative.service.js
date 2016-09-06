(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('CreativeService', CreativeService);

    CreativeService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function CreativeService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        /**
         * You can apply the following endpoints with this resource:
         *
         * <code>
         /Creatives
         /Creatives/{id}
         /Creatives/{id}/file
         /Creatives/{id}/image
         </code>
         */
        var creativeResource = $resource(API_SERVICE + 'Creatives/:id/:criteria', {
                id: '@id',
                criteria: '@criteria'
            }, {
                update: {
                    method: 'PUT'
                }
            }),

            creativePreviewResource = $resource(API_SERVICE + 'Creatives/:id/preview', {
                id: '@id'
            }, {
                getImagePreview: {
                    responseType: 'arraybuffer',
                    transformResponse: function (data) {
                        return {
                            data: data
                        };
                    }
                },
                getTextPreview: {
                    responseType: 'text/plain',
                    transformResponse: function (data) {
                        return {
                            data: data
                        };
                    }
                }
            }),

            creativeTemplateResource = $resource(API_SERVICE + 'Creatives/:id/file', {
                id: '@id'
            }, {
                getTemplateFile: {
                    responseType: 'arraybuffer',
                    transformResponse: function (data) {
                        return {
                            data: data
                        };
                    }
                }
            }),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            },

        result = {
            deleteCreative: function (creativeId, groupIdList) {
                var creativeDeleted = $q.defer();

                creativeResource.update({
                    id: creativeId,
                    criteria: 'remove'
                }, {
                    records: {
                        Long: groupIdList
                    }
                }).$promise
                    .then(deleteCreativeComplete)
                    .catch(deleteCreativeFailed);

                function deleteCreativeComplete(response) {
                    creativeDeleted.resolve(response);
                }

                function deleteCreativeFailed(error) {
                    creativeDeleted.reject(error);
                }

                return creativeDeleted.promise;
            },

            deleteCreativeList: function (creativeList) {
                var creativeDeleted = $q.defer();

                creativeResource.delete({
                    recordSet: creativeList
                }).$promise
                    .then(deleteCreativeListComplete)
                    .catch(errorHandler('Cannot delete the creative', creativeDeleted));

                function deleteCreativeListComplete(response) {
                    creativeDeleted.resolve(response);
                }

                return creativeDeleted.promise;
            },

            hasSchedules: function (creativeId) {
                var scheduleHas = $q.defer();

                creativeResource.get({
                    id: creativeId,
                    criteria: 'hasSchedules'
                }).$promise
                    .then(hasSchedulesComplete)
                    .catch(errorHandler('Error while performing haSchedules request', scheduleHas));

                function hasSchedulesComplete(response) {
                    scheduleHas.resolve(response);
                }

                return scheduleHas.promise;
            },

            getCreative: function (creativeId) {
                var creative = $q.defer();

                creativeResource.get({
                    id: creativeId
                }).$promise
                    .then(getCreativeComplete)
                    .catch(errorHandler('Error while performing get creative request', creative));

                function getCreativeComplete(response) {
                    creative.resolve(response);
                }

                return creative.promise;
            },

            getCreativeFile: function (creativeId, responseType) {
                var imageFile = $q.defer(),
                    getPreview;

                if (responseType === 'arraybuffer') {
                    getPreview = creativePreviewResource.getImagePreview({
                        id: creativeId
                    });
                }
                else {
                    getPreview = creativePreviewResource.getTextPreview({
                        id: creativeId
                    });
                }

                getPreview.$promise
                    .then(getCreativeFileComplete)
                    .catch(errorHandler('Error while performing creative file request', imageFile));

                function getCreativeFileComplete() {
                    imageFile.resolve(getPreview.data);
                }

                return imageFile.promise;
            },

            getCreativeTemplateFile: function (creativeId) {
                var templateFile = $q.defer(),
                    getFile = creativeTemplateResource.getTemplateFile({
                        id: creativeId
                    });

                getFile.$promise
                    .then(getCreativeTemplateFileComplete)
                    .catch(errorHandler('Error while performing creative file request', templateFile));

                function getCreativeTemplateFileComplete() {
                    templateFile.resolve(getFile.data);
                }

                return templateFile.promise;
            },

            updateCreative: function (creative) {
                var creativeUpdated = $q.defer();

                creativeResource.update({
                    id: creative.id
                }, creative).$promise
                    .then(updateCreativeComplete)
                    .catch(errorHandler('Error while performing update creative request', creative));

                function updateCreativeComplete(response) {
                    creativeUpdated.resolve(response);
                }

                return creativeUpdated.promise;
            },

            getCreativeNotAssociatedByCampaignAndGroup: function (campaignId, groupId) {
                var creativeList = $q.defer();

                creativeResource.get({
                    id: 'unassociated',
                    campaignId: campaignId,
                    groupId: groupId
                }).$promise
                    .then(getCreativeNotAssociatedByCampaignAndGroupComplete)
                    .catch(errorHandler('Cannot get creative list', creativeList));

                function getCreativeNotAssociatedByCampaignAndGroupComplete(response) {
                    creativeList.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].Creative)));
                }

                return creativeList.promise;
            },

            getCreativeAssociationsCount: function (creativeId) {
                var creativeAssociationsCount = $q.defer();

                creativeResource.get({
                    id: creativeId,
                    criteria: 'creativeAssociationsCount'
                }).$promise
                    .then(creativeAssociationsCountComplete)
                    .catch(errorHandler('Error while performing creativeAssociationsCount request',
                        creativeAssociationsCount));

                function creativeAssociationsCountComplete(response) {
                    creativeAssociationsCount.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].CreativeAssociationsDTO)));
                }

                return creativeAssociationsCount.promise;
            },

            getScheduleByCreative: function (creativeId) {
                var schedule = $q.defer();

                creativeResource.get({
                    id: creativeId,
                    criteria: 'schedules'
                }).$promise
                    .then(scheduleComplete)
                    .catch(errorHandler('Error while performing schedules request', schedule));

                function scheduleComplete(response) {
                    schedule.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].CreativeInsertionView)));
                }

                return schedule.promise;
            }
        };

        return result;
    }
})();
