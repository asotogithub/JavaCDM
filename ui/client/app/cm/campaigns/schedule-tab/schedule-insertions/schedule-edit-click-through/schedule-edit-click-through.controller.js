(function () {
    'use strict';

    angular.module('uiApp')
        .controller('ScheduleEditClickThroughController', ScheduleEditClickThroughController);

    ScheduleEditClickThroughController.$inject = [
        '$modalInstance',
        'CONSTANTS',
        'data',
        'lodash'
    ];

    function ScheduleEditClickThroughController(
        $modalInstance,
        CONSTANTS,
        data,
        lodash) {
        var vm = this,
            ANIMATION_DELAY = 1000,
            MIN_ROWS_TO_SCROLL = 5;

        vm.addCTUrl = addCTUrl;
        vm.cancel = cancel;
        vm.clickthroughs = [];
        vm.clickthroughsForm = {};
        vm.urlPattern = new RegExp(CONSTANTS.REGEX.URL, 'i');
        vm.removeCTUrl = removeCTUrl;
        vm.save = save;
        vm.validationClickThroughs = validationClickThroughs;

        initializeModel();

        function addCTUrl() {
            if (containEmptyClickThroughs()) {
                return;
            }

            var sequence = vm.clickthroughs.length + 1;

            vm.clickthroughs.push({
                sequence: sequence,
                url: ''
            });

            if (sequence > MIN_ROWS_TO_SCROLL) {
                angular.element('.clickthrough-scrollBar').animate({
                    scrollTop: angular.element('.clickthrough-scrollBar')[0].scrollHeight
                }, ANIMATION_DELAY);
            }
        }

        function cancel() {
            closeModal();
        }

        function closeModal(cturls) {
            if (cturls) {
                $modalInstance.close({
                    status: true,
                    cturls: cturls,
                    total: cturls.length
                });
            }
            else {
                $modalInstance.close({
                    status: false
                });
            }
        }

        function initializeModel() {
            vm.mainTitle = data.propertiesModel.mainTitle;
            vm.clickthroughs = getListCTUrls(data.propertiesModel.clickThrough);
        }

        function containEmptyClickThroughs() {
            var emptyClick = lodash.find(vm.clickthroughs,
                function (item) {
                    return item.url ? item.url.trim() === '' : true;
                });

            return emptyClick !== undefined;
        }

        function getListCTUrls(throught) {
            var cturls = [],
                sequence;

            angular.forEach(throught, function (value, index) {
                sequence = index++;
                cturls.push({
                    sequence: sequence,
                    url: value
                });
            });

            return cturls;
        }

        function save() {
            data.cturls = [];

            angular.forEach(vm.clickthroughs, function (value) {
                data.cturls.push(value.url);
            });

            closeModal(data.cturls);
        }

        function removeCTUrl(index) {
            if (!vm.clickthroughs || vm.clickthroughs.length < 2 || typeof vm.clickthroughs === undefined) {
                return;
            }

            vm.clickthroughs.splice(index, 1);
            vm.clickthroughsForm.$setDirty();
            refreshClickthroughsSequence();
        }

        function refreshClickthroughsSequence() {
            angular.forEach(vm.clickthroughs, function (clickthrough, index) {
                clickthrough.sequence = index + 1;
            });
        }

        function validationClickThroughs(item) {
            vm.clickthroughsForm.$setDirty();
            vm.clickthroughsForm[item.sequence].$dirty = true;
            refreshClickthroughsSequence();
        }
    }
})();
