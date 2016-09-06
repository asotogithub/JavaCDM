(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('NewCreativeGroupController', NewCreativeGroupController);

    NewCreativeGroupController.$inject = [
        '$log',
        '$state',
        '$stateParams',
        '$translate',
        'CreativeGroupService',
        'DialogFactory',
        'ErrorHandlingService',
        'ErrorRequestHandler'
    ];

    function NewCreativeGroupController(
        $log,
        $state,
        $stateParams,
        $translate,
        CreativeGroupService,
        DialogFactory,
        ErrorHandlingService,
        ErrorRequestHandler) {
        var vm = this,
            campaignId = $stateParams.campaignId;

        vm.campaignId = campaignId;
        vm.close = close;
        vm.creativeGroup = null;
        vm.errorMessage = null;
        vm.goToCreativeGroupsTab = goToCreativeGroupsTab;
        vm.pristine = true;
        vm.promise = null;
        vm.save = save;
        vm.submitDisabled = false;

        activate();

        function activate() {
            vm.creativeGroup = {
                campaignId: campaignId,
                cookieTarget: null,
                daypartTarget: null,
                doCookieTargeting: 0,
                doDaypartTargeting: 0,
                enableFrequencyCap: 0,
                enableGroupWeight: 0,
                enablePriority: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_zip'
                    }
                },
                isDefault: 0,
                name: null,
                priority: 0,
                weight: 100
            };
        }

        function close(evt) {
            if (!vm.pristine) {
                evt.preventDefault();
                DialogFactory.showDialog(DialogFactory.DIALOG.SPECIFIC_TYPE.DISCARD_CHANGES)
                    .result
                    .then(function () {
                        $state.go('creative-groups-tab', {
                            campaignId: vm.campaignId
                        });
                    });
            }
        }

        function goToCreativeGroupsTab() {
            $state.go('creative-groups-tab', {
                campaignId: vm.campaignId
            });
        }

        function save() {
            vm.promise = CreativeGroupService.saveCreativeGroup(vm.creativeGroup);

            vm.promise.then(
                function (creativeGroup) {
                    DialogFactory.showDismissableMessage(
                        DialogFactory.DISMISS_TYPE.INFO,
                        $translate.instant('info.operationCompleted'));

                    $state.go('creative-group', {
                        campaignId: creativeGroup.campaignId,
                        creativeGroupId: creativeGroup.id
                    });
                }).catch(function (error) {
                    var errorMessage = error && error.data && error.data.errors &&
                        ErrorHandlingService.getErrorMessage(error.data.errors);

                    vm.errorMessage = errorMessage;

                    if (!errorMessage) {
                        ErrorRequestHandler.handleAndReject('Error while saving the creative group: ' +
                            angular.toJson(error))(error);
                    }
                });
        }
    }
})();
