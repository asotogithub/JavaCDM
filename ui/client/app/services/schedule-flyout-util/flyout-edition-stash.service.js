(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('FlyoutEditionStashService', FlyoutEditionStashService);

    FlyoutEditionStashService.$inject = ['CONSTANTS', 'Utils', 'lodash', 'Sha1'];

    function FlyoutEditionStashService(CONSTANTS, Utils, lodash, Sha1) {
        var siteCollection = {},
            sectionCollection = {},
            placementCollection = {},
            groupCollection = {},
            groupWeightCollection = {},
            creativeCollection = {},

            buildKey = function (row) {
                switch (row.field) {
                    case CONSTANTS.SCHEDULE.LEVEL.SITE.KEY:
                        return Sha1.hash('' + row.siteId);
                    case CONSTANTS.SCHEDULE.LEVEL.SECTION.KEY:
                        return Sha1.hash('' + row.siteId + row.siteSectionId);
                    case CONSTANTS.SCHEDULE.LEVEL.PLACEMENT.KEY:
                        return Sha1.hash('' + row.siteId + row.siteSectionId + row.placementId);
                    case CONSTANTS.SCHEDULE.LEVEL.CREATIVE_GROUP.KEY:
                        return Sha1.hash('' + row.siteId + row.siteSectionId + row.placementId +
                            row.creativeGroupId);
                    case CONSTANTS.SCHEDULE.LEVEL.SCHEDULE.KEY:
                        return Sha1.hash('' + row.siteId + row.siteSectionId + row.placementId +
                            row.creativeGroupId + row.creativeId);
                }
            },

            save = function (row) {
                var aux = {};

                if (Utils.isUndefinedOrNull(row.field)) {
                    return;
                }

                aux.checked = row.checked;
                aux.field = row.field;

                if (!Utils.isUndefinedOrNull(row.siteId)) {
                    aux.siteId = row.siteId;
                }

                if (!Utils.isUndefinedOrNull(row.siteSectionId)) {
                    aux.siteSectionId = row.siteSectionId;
                }

                if (!Utils.isUndefinedOrNull(row.placementId)) {
                    aux.placementId = row.placementId;
                }

                if (!Utils.isUndefinedOrNull(row.creativeGroupId)) {
                    aux.creativeGroupId = row.creativeGroupId;
                }

                if (!Utils.isUndefinedOrNull(row.creativeId)) {
                    aux.creativeId = row.creativeId;
                }

                switch (row.field) {
                    case CONSTANTS.SCHEDULE.LEVEL.SITE.KEY:
                        siteCollection[buildKey(row)] = aux;
                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.SECTION.KEY:
                        sectionCollection[buildKey(row)] = aux;
                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.PLACEMENT.KEY:
                        placementCollection[buildKey(row)] = aux;
                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.CREATIVE_GROUP.KEY:
                        aux.weight = row.weight;
                        groupWeightCollection[row.creativeGroupId] = row.weight;
                        angular.forEach(groupCollection, function (group) {
                            if (group.creativeGroupId === row.creativeGroupId) {
                                group.weight = row.weight;
                            }
                        });

                        groupCollection[buildKey(row)] = aux;
                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.SCHEDULE.KEY:
                        aux.weight = row.weight;
                        aux.flightDateStart = row.flightDateStart;
                        aux.flightDateEnd = row.flightDateEnd;
                        aux.clickThroughUrl = row.clickThroughUrl;
                        aux.clickthroughs = row.clickthroughs;
                        creativeCollection[buildKey(row)] = aux;
                        break;
                }
            },

            load = function (row) {
                var searchResult,
                    groupWeightSearchResult;

                if (Utils.isUndefinedOrNull(row.field)) {
                    return;
                }

                switch (row.field) {
                    case CONSTANTS.SCHEDULE.LEVEL.SITE.KEY:
                        searchResult = siteCollection[buildKey(row)];
                        if (!Utils.isUndefinedOrNull(searchResult)) {
                            row.checked = searchResult.checked;
                        }

                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.SECTION.KEY:
                        searchResult = sectionCollection[buildKey(row)];
                        if (!Utils.isUndefinedOrNull(searchResult)) {
                            row.checked = searchResult.checked;
                        }

                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.PLACEMENT.KEY:
                        searchResult = placementCollection[buildKey(row)];
                        if (!Utils.isUndefinedOrNull(searchResult)) {
                            row.checked = searchResult.checked;
                        }

                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.CREATIVE_GROUP.KEY:
                        searchResult = groupCollection[buildKey(row)];
                        if (Utils.isUndefinedOrNull(searchResult)) {
                            groupWeightSearchResult =
                                groupWeightCollection[row.creativeGroupId];
                            if (!Utils.isUndefinedOrNull(groupWeightSearchResult)) {
                                row.weight = groupWeightSearchResult.weight;
                            }
                        }
                        else {
                            row.checked = searchResult.checked;
                            row.weight = searchResult.weight;
                        }

                        break;
                    case CONSTANTS.SCHEDULE.LEVEL.SCHEDULE.KEY:
                        searchResult = creativeCollection[buildKey(row)];
                        if (!Utils.isUndefinedOrNull(searchResult)) {
                            row.checked = searchResult.checked;
                            row.weight = searchResult.weight;
                            row.flightDateStart = searchResult.flightDateStart;
                            row.flightDateEnd = searchResult.flightDateEnd;
                            row.clickThroughUrl = searchResult.clickThroughUrl;
                            row.clickthroughs = searchResult.clickthroughs;
                        }

                        break;
                }
            },

            bulkLoadGroups = function (groupList) {
                var that = this;

                angular.forEach(groupList, function (group) {
                    that.load(group);
                });
            },

            bulkLoadCreatives = function (creativeList) {
                var that = this;

                angular.forEach(creativeList, function (creative) {
                    that.load(creative);
                });
            },

            getCheckedRows = function () {
                return lodash.filter(siteCollection, 'checked').concat(lodash.filter(sectionCollection, 'checked'),
                    lodash.filter(placementCollection, 'checked'), lodash.filter(groupCollection, 'checked'),
                    lodash.filter(creativeCollection, 'checked'));
            },

            getGroups = function () {
                return groupCollection;
            },

            getCreatives = function () {
                return creativeCollection;
            },

            uncheckAll = function () {
                angular.forEach(siteCollection, function (site) {
                    site.checked = false;
                });

                angular.forEach(sectionCollection, function (section) {
                    section.checked = false;
                });

                angular.forEach(placementCollection, function (placement) {
                    placement.checked = false;
                });

                angular.forEach(groupCollection, function (group) {
                    group.checked = false;
                });

                angular.forEach(creativeCollection, function (creative) {
                    creative.checked = false;
                });
            },

            clear = function () {
                siteCollection = {};
                sectionCollection = {};
                placementCollection = {};
                groupCollection = {};
                groupWeightCollection = {};
                creativeCollection = {};
            };

        return {
            save: save,
            load: load,
            bulkLoadGroups: bulkLoadGroups,
            bulkLoadCreatives: bulkLoadCreatives,
            buildKey: buildKey,
            getCheckedRows: getCheckedRows,
            getGroups: getGroups,
            getCreatives: getCreatives,
            uncheckAll: uncheckAll,
            clear: clear
        };
    }
})();
