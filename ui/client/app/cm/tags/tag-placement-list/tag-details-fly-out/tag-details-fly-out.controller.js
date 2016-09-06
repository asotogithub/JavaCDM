(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TagDetailsFlyOutController', TagDetailsFlyOutController);

    TagDetailsFlyOutController.$inject = [
        '$scope',
        '$translate',
        'DateTimeService',
        'PlacementService',
        'CONSTANTS'
    ];

    function TagDetailsFlyOutController(
        $scope,
        $translate,
        DateTimeService,
        PlacementService,
        CONSTANTS
        ) {
        var vm = this,
            flyOutController = $scope.vmTeFlyOutController,
            placement = flyOutController.flyOutModel.data;

        vm.flyOutController = flyOutController;
        vm.FLYOUT_STATE = CONSTANTS.FLY_OUT.STATE;
        vm.closeAction = closeAction;
        vm.enableSendBtn = false;
        vm.flyoutState = flyOutController.flyoutState;
        vm.openSendTagDialog = false;
        vm.resource = {
            instructions: $translate.instant('tags.details.instructionsDescription', {
                mailto: '<a href="mailto:' + CONSTANTS.TAGS.SEND_AD_TAGS.EMAIL_CLIENT + '" class="txt-under-line">' +
                     CONSTANTS.TAGS.SEND_AD_TAGS.EMAIL_CLIENT + '</a>'
            })
        };

        activate();

        function activate() {
            vm.model = {};

            vm.promiseTDFlyOut = PlacementService.getAdTags(placement.id).then(function (response) {
                vm.model = response;
                vm.model.flightDates = formatFlightDates(response.startDate, response.endDate);
                vm.model.impressions = parseInt(response.impressions).toLocaleString();
                vm.model.enableSendBtn = placement.enableSendBtn;
            });
        }

        function closeAction(openSendTagDialog) {
            flyOutController.closeAction({
                openSendTagDialog: openSendTagDialog
            });
        }

        function formatFlightDates(startDate, endDate) {
            return DateTimeService.format(startDate, DateTimeService.FORMAT.DATE) + ' \u2014 ' +
                DateTimeService.format(endDate, DateTimeService.FORMAT.DATE);
        }

        //Fly-Out Actions
        $scope.$watch('vmTeFlyOutController.flyoutState', function (newValue) {
            vm.flyoutState = newValue;
        });

        $scope.$watch('vmTeFlyOutController.flyOutModel.data', function (newValue) {
            placement = newValue;
            activate();
        });

        $scope.$on('tagPlacementList:closeFlyout', function () {
            flyOutController.close(true);
        });
    }
})();
