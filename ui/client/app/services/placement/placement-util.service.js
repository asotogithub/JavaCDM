(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('PlacementUtilService', PlacementUtilService);

    PlacementUtilService.$inject = [
        '$translate',
        'CONSTANTS',
        'lodash'
    ];

    function PlacementUtilService(
        $translate,
        CONSTANTS,
        lodash) {
        var
            statusList = {
                NEW: {
                    key: 'New',
                    name: 'global.planning'
                },
                ACCEPTED: {
                    key: 'Accepted',
                    name: 'global.active'
                },
                REJECTED: {
                    key: 'Rejected',
                    name: 'global.inactive'
                }
            },
            statusMap = lodash.chain(statusList).values().indexBy('key').value();

        return {
            generatePlacementName: generatePlacementName,

            getStatusName: function (key) {
                var name = lodash.result(statusMap[key], 'name');

                return name ? $translate.instant(name) : key;
            },

            parserPlacementList: function (placementList, campaignId, ioId) {
                var propertiesParsedList = [];

                lodash.forEach(placementList, function (property, pkey) {
                    var siteAuxArr = [],
                        name = '',
                        packageName = '',
                    // Preparing the arrays to be combined
                        sectionAuxArr = property.section,
                        sizeAuxArr = property.size,
                        sectionSizeCombined = sectionAndSizeCombinatorics(sectionAuxArr, sizeAuxArr);

                    if (property.packageName && property.packageName !== '') {
                        packageName = property.packageName;
                    }

                    if (property.name && property.name !== '') {
                        name = property.name;
                    }

                    siteAuxArr[pkey] = {
                        name: name,
                        packageName: packageName,
                        Site: {
                            id: property.site.id,
                            name: property.site.name
                        }
                    };

                    // Combine the combined section and size arrays with the placement name and site name
                    lodash.forEach(sectionSizeCombined, function (val) {
                        val.name = siteAuxArr[pkey].name;
                        val.packageName = siteAuxArr[pkey].packageName;
                        val.siteName = siteAuxArr[pkey].Site.name;
                        val.campaignId = campaignId;
                        val.ioId = ioId;
                        val.siteId = siteAuxArr[pkey].Site.id;
                        val.utcOffset = CONSTANTS.PLACEMENT.UTC_OFFSET;
                        val.countryCurrencyId = CONSTANTS.COST_DETAIL.CURRENCY_ID;
                        val.inventory = CONSTANTS.COST_DETAIL.INVENTORY.DEFAULT;
                        val.status = statusList.NEW.key;
                        propertiesParsedList.push(val);
                    });
                });

                lodash.forEach(propertiesParsedList, function (placement, i) {
                    if (placement.name === '') {
                        propertiesParsedList[i].name = generatePlacementName(placement);
                    }
                });

                return propertiesParsedList;
            }
        };

        function sectionAndSizeCombinatorics(sectionAuxArr, sizeAuxArr) {
            var arrayResult = [];

            lodash.forEach(sizeAuxArr, function (value1) {
                lodash.forEach(sectionAuxArr, function (value2) {
                    arrayResult.push({
                        siteSectionId: value2.id,
                        sectionName: value2.name,
                        sizeName: value1.label,
                        sizeId: value1.id,
                        height: value1.height,
                        width: value1.width
                    });
                });
            });

            return arrayResult;
        }

        function generatePlacementName(placement) {
            return (placement.siteName + ' - ' + placement.sectionName + ' - ' + placement.sizeName)
                .slice(0, CONSTANTS.INPUT.MAX_LENGTH.PLACEMENT_NAME);
        }
    }
})();
