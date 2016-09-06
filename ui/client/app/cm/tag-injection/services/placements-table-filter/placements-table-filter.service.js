(function () {
    'use strict';

    angular
        .module('uiApp')
        .factory('PlacementsTableFilterService', PlacementsTableFilterService);

    PlacementsTableFilterService.$inject = ['Utils', 'lodash'];

    function PlacementsTableFilterService(Utils, lodash) {
        var mapPlacements = {},
            mapSites = {},
            service = {
                getPlacementsMap: getPlacementsMap,
                getSitesMap: getSitesMap,
                loadFilterOptions: loadFilterOptions,
                mappingFilters: mappingFilters,
                siteIdByName: siteIdByName,
                placementIdByName: placementIdByName,
                updatePlacement: updatePlacement,
                updateSite: updateSite
            };

        return service;

        function loadFilterOptions(model, options, values) {
            var campaignList = [],
                siteList = [],
                placementList = [];

            lodash.forEach(model, function (row) {
                mappingFilters(row);

                if (!lodash.find(campaignList,
                        {
                            label: row.campaignName
                        })) {
                    campaignList.push({
                        id: row.campaignId,
                        label: row.campaignName,
                        enabled: true
                    });
                }

                if (!lodash.find(siteList,
                        {
                            label: row.siteName
                        })) {
                    siteList.push({
                        id: row.siteId,
                        label: row.siteName,
                        enabled: true
                    });
                }

                if (!lodash.find(placementList,
                        {
                            label: row.name
                        })) {
                    placementList.push({
                        id: row.id,
                        label: row.name,
                        enabled: true
                    });
                }
            });

            options[0].value = lodash.clone(lodash.sortBy(campaignList, 'label'));
            options[1].value = lodash.clone(lodash.sortBy(siteList, 'label'));
            options[2].value = lodash.clone(lodash.sortBy(placementList, 'label'));

            lodash.forEach(values, function (filter, index) {
                filter.values = options[index].value.map(function (item) {
                    return item.label;
                });
            });
        }

        function mappingFilters(row) {
            var dataPlacement = mapPlacements[row.name],
                data,
                dataSite;

            if (Utils.isUndefinedOrNull(dataPlacement)) {
                data = {
                    site: [],
                    campaign: []
                };

                data.site.push(row.siteName);
                data.campaign.push(row.campaignName);
                mapPlacements[row.name] = data;
            }
            else {
                if (lodash.indexOf(dataPlacement.site, row.siteName) < 0) {
                    dataPlacement.site.push(row.siteName);
                }

                if (lodash.indexOf(dataPlacement.campaign, row.campaignName) < 0) {
                    dataPlacement.campaign.push(row.campaignName);
                }
            }

            dataSite = mapSites[row.siteName];
            if (Utils.isUndefinedOrNull(dataSite)) {
                data = {
                    placement: [],
                    campaign: []
                };

                data.placement.push(row.name);
                data.campaign.push(row.campaignName);
                mapSites[row.siteName] = data;
            }
            else {
                if (lodash.indexOf(dataSite.placement, row.name) < 0) {
                    dataSite.placement.push(row.name);
                }

                if (lodash.indexOf(dataSite.campaign, row.campaignName) < 0) {
                    dataSite.campaign.push(row.campaignName);
                }
            }
        }

        function siteIdByName(model, label) {
            var result = lodash.find(model,
                {
                    siteName: label
                });

            if (!Utils.isUndefinedOrNull(result)) {
                return result.siteId;
            }

            return result;
        }

        function placementIdByName(model, label) {
            var result = lodash.find(model,
                {
                    name: label
                });

            if (!Utils.isUndefinedOrNull(result)) {
                return result.id;
            }

            return result;
        }

        function updateSite(newValue, oldValue, model, options) {
            var sitesSelected = [],
                sitesOptions = [],
                newOptionsAux = [],
                oldPlacementValue = lodash.clone(newValue[1].values),
                newValueSelect = lodash.difference(newValue[0].values, oldValue),
                selectAux,
                siteId;

            lodash.forEach(mapSites, function (value, id) {
                if (lodash.intersection(value.campaign, newValueSelect).length > 0) {
                    sitesSelected.push(id);
                }
            });

            lodash.forEach(mapSites, function (value, id) {
                if (lodash.intersection(value.campaign, newValue[0].values).length > 0) {
                    sitesOptions.push(id);
                }
            });

            selectAux = lodash.intersection(sitesOptions, newValue[1].values);

            lodash.forEach(sitesSelected, function (value) {
                if (lodash.indexOf(selectAux, value) < 0) {
                    selectAux.push(value);
                }
            });

            newValue[1].values = selectAux;
            lodash.forEach(sitesOptions, function (siteLabel) {
                siteId = siteIdByName(model, siteLabel);

                if (!Utils.isUndefinedOrNull(siteId)) {
                    newOptionsAux.push(
                        {
                            id: siteId,
                            label: siteLabel,
                            enabled: true
                        }
                    );
                }
            });

            options[1].value = lodash.clone(lodash.sortBy(newOptionsAux, 'label'));

            updatePlacement(newValue, oldPlacementValue, model, options);
        }

        function updatePlacement(newValue, oldValue, model, options) {
            var placementsSelected = [],
                placementsOptions = [],
                newOptionsAux = [],
                newValueSelect = lodash.difference(newValue[1].values, oldValue),
                selectAux,
                placementId;

            lodash.forEach(mapPlacements, function (value, id) {
                if (lodash.intersection(value.site, newValueSelect).length > 0) {
                    placementsSelected.push(id);
                }
            });

            lodash.forEach(mapPlacements, function (value, id) {
                if (lodash.intersection(value.site, newValue[1].values).length > 0) {
                    placementsOptions.push(id);
                }
            });

            selectAux = lodash.intersection(placementsOptions, newValue[2].values);

            lodash.forEach(placementsSelected, function (value) {
                if (lodash.indexOf(selectAux, value) < 0) {
                    selectAux.push(value);
                }
            });

            newValue[2].values = selectAux;
            lodash.forEach(placementsOptions, function (placementLabel) {
                placementId = placementIdByName(model, placementLabel);

                if (!Utils.isUndefinedOrNull(placementId)) {
                    newOptionsAux.push(
                        {
                            id: placementId,
                            label: placementLabel,
                            enabled: true
                        }
                    );
                }
            });

            options[2].value = lodash.clone(lodash.sortBy(newOptionsAux, 'label'));
        }

        function getPlacementsMap() {
            return mapPlacements;
        }

        function getSitesMap() {
            return mapSites;
        }
    }
})();

