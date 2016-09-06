(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('CampaignsUtilService', CampaignsUtilService);

    CampaignsUtilService.$inject = ['$translate', 'lodash'];

    function CampaignsUtilService($translate, lodash) {
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

            statusListADM = {
                ACTIVE: {
                    key: 'true',
                    name: 'global.active'
                },
                INACTIVE: {
                    key: 'false',
                    name: 'global.inactive'
                }
            },

            statusMap = lodash.chain(statusList).values().indexBy('key').value();

        return {
            getIOStatusList: function () {
                return lodash.cloneDeep(statusList);
            },

            getADMStatusList: function () {
                return lodash.cloneDeep(statusListADM);
            },

            getStatusName: function (key) {
                var name = lodash.result(statusMap[key], 'name');

                return name ? $translate.instant(name) : key;
            }
        };
    }
})();
