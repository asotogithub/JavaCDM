(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SiteMeasurementsUtilService', SiteMeasurementsUtilService);

    SiteMeasurementsUtilService.$inject = ['$rootScope', '$state', '$translate', 'CONSTANTS', 'DateTimeService'];

    function SiteMeasurementsUtilService($rootScope, $state, $translate, CONSTANTS, DateTimeService) {
        var DAYS_SUBTRACT = 1,
            YEARS_ADD = 10,
            destiny,
            pendingChangesList = {},
            statusList = [
                {
                    key: true,
                    name: 'global.active'
                },
                {
                    key: false,
                    name: 'global.inactive'
                }
            ];

        function getIdentifier(key) {
            return angular.isDefined(key) ? key : 'default';
        }

        function getTranslatedTagList(tagList) {
            var result = [],
                i;

            for (i = 0; i < tagList.length; i++) {
                result.push({
                    key: tagList[i].KEY,
                    name: $translate.instant(tagList[i].NAME)
                });
            }

            return result;
        }

        function hasPendingChanges() {
            var result = false,
                key;

            for (key in pendingChangesList) {
                if (pendingChangesList[key]) {
                    result = true;
                    break;
                }
            }

            return result;
        }

        return {
            clearPendingChanges: function () {
                pendingChangesList = {};
            },

            getStatusList: function () {
                return statusList;
            },

            getExpirationDate: function (active) {
                var currentMomentDate = DateTimeService.getMoment(),
                    expirationDate = active ?
                        currentMomentDate.add(YEARS_ADD, 'year') : currentMomentDate.subtract(DAYS_SUBTRACT, 'day');

                return DateTimeService.inverseParse(expirationDate.toDate());
            },

            getHasPendingChanges: function () {
                return hasPendingChanges();
            },

            getPingCardTitleByType: function (pingType) {
                var pingCard = CONSTANTS.SITE_MEASUREMENT.PING_CARD.TYPE,
                    i,
                    result = '';

                for (i = 0; i < pingCard.length; i++) {
                    if (pingCard[i].KEY === pingType) {
                        result = pingCard[i].NAME;
                    }
                }

                return $translate.instant(result);
            },

            getPingCardTagTypeList: function (pingType) {
                return CONSTANTS.SITE_MEASUREMENT.PING_CARD.TYPE_BROADCAST === pingType ?
                    getTranslatedTagList(CONSTANTS.SITE_MEASUREMENT.PING_CARD.TAG_TYPE_BROADCAST_LIST) :
                    getTranslatedTagList(CONSTANTS.SITE_MEASUREMENT.PING_CARD.TAG_TYPE_SELECTIVE_LIST);
            },

            goRouteDestiny: function () {
                if (destiny) {
                    $state.go(destiny.toStateName, destiny.toParams);
                }
            },

            getPingCardTagTypeName: function (pingTagType) {
                var pingTypeList = CONSTANTS.SITE_MEASUREMENT.PING_CARD.TAG_TYPE_BROADCAST_LIST,
                    i,
                    result = '';

                for (i = 0; i < pingTypeList.length; i++) {
                    if (pingTypeList[i].KEY === pingTagType) {
                        result = pingTypeList[i].NAME;
                    }
                }

                return $translate.instant(result);
            },

            isAllowedToPerformAction: function (callback) {
                if (hasPendingChanges()) {
                    $rootScope.$broadcast('smContent.hasUnsavedChanges', {
                        action: function () {
                            callback();
                        }
                    });
                }

                return !hasPendingChanges();
            },

            isStateChangeAllowed: function (event, toState, toParams) {
                if (hasPendingChanges()) {
                    destiny = {
                        toStateName: toState.name,
                        toParams: toParams
                    };
                    event.preventDefault();
                    $rootScope.$broadcast('smContent.hasUnsavedChanges', {});
                }

                return !hasPendingChanges();
            },

            setHasPendingChanges: function (status, key) {
                var identifier = getIdentifier(key);

                pendingChangesList[identifier] = status;
            }
        };
    }
})();
