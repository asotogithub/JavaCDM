(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SiteMeasurementModel', SiteMeasurementModel);

    SiteMeasurementModel.$inject = [
        '$q',
        '$state',
        'CONSTANTS',
        'DialogFactory',
        'SiteMeasurementsService',
        'lodash'
    ];

    function SiteMeasurementModel(
        $q,
        $state,
        CONSTANTS,
        DialogFactory,
        SiteMeasurementsService,
        lodash) {
        var START_END_CHARS_LENGTH = 9,
            that = this;

        //--- Functions
        this.clear = function () {
            that.siteMeasurement = undefined;
            that.siteMeasurementId = undefined;
        };

        this.getSiteMeasurement = function (siteMeasurementId) {
            return this.getData(siteMeasurementId).then(function (result) {
                that.siteMeasurement = result[0];
                that.siteMeasurement.shortName = getShortName(that.siteMeasurement.name);
            }).catch(that.showServiceError);
        };

        this.selectTab = function (state, tabs) {
            var tab = getElementByValue(tabs, state, 'state', tabs[0]);

            if (!tab.active) {
                tab.active = true;
                $state.go(tab.state);
            }
        };

        this.setSiteMeasurement = function (siteMeasurement) {
            that.siteMeasurement = siteMeasurement;
            that.siteMeasurement.shortName = getShortName(that.siteMeasurement.name);
        };

        this.setSiteMeasurementId = function (siteMeasurementId) {
            that.siteMeasurementId = siteMeasurementId;
        };

        this.getPromiseDetails = retrieveSiteMeasurement;

        //Utilities
        function getElementByValue(elements, value, field, defaultValue) {
            var found = lodash.find(elements, function (element) {
                return value === element[field];
            });

            if (!found) {
                return defaultValue;
            }

            return found;
        }

        function getShortName(name) {
            if (name.length > CONSTANTS.SITE_MEASUREMENT.NAME_TRUNCATE_LENGTH) {
                return name.substr(0, START_END_CHARS_LENGTH) + '...' +
                       name.substr(name.length - START_END_CHARS_LENGTH, name.length);
            }

            return name;
        }

        this.getData = function (siteMeasurementId) {
            var siteMeasurementPromise = retrieveSiteMeasurement(siteMeasurementId);

            that.promiseRequest = $q.all([siteMeasurementPromise]);
            return that.promiseRequest;
        };

        function retrieveSiteMeasurement(siteMeasurementId) {
            return SiteMeasurementsService.getSiteMeasurements(siteMeasurementId);
        }

        this.showServiceError = function () {
            DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
        };
    }
})();
