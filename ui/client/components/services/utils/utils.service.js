(function () {
    'use strict';

    angular
        .module('te.components')
        .service('Utils', Utils);

    Utils.$inject = ['lodash'];

    function Utils(lodash) {
        var PROMISE_STATUS = {
            PENDING: 0,
            RESOLVED: 1,
            REJECTED: 2
        };

        this.isUndefinedOrNull = function (obj) {
            return angular.isUndefined(obj) || obj === null;
        };

        this.getElementFromArray = function (model, element, criteria) {
            var result = lodash.find(model, function (value) {
                return value[criteria] === element;
            });

            return result;
        };

        this.isPromiseInProgress = function (promise) {
            return promise.$$state.status === PROMISE_STATUS.PENDING;
        };
    }
})();
