(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('IoAddController', IoAddController);

    IoAddController.$inject = [
        '$moment',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'InsertionOrderService',
        'MediaBuyService',
        'UserService'
    ];

    function IoAddController(
        $moment,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        DialogFactory,
        InsertionOrderService,
        MediaBuyService,
        UserService) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            dateNowUTC = $moment().utc().format();

        vm.cancel = cancel;
        vm.insertionOrder = {};
        vm.insertionOrder.notes = $translate.instant('insertionOrder.ioNotesMessage',
            {
                userName: UserService.getUsername(),
                date: $moment(dateNowUTC).utcOffset(CONSTANTS.TIMEZONE.MST).format(CONSTANTS.DATE.MOMENT.DATE_TIME)
            }
        );
        vm.alphanumeric = CONSTANTS.REGEX.ASCII_PRINTABLE;
        vm.maxIONumber = CONSTANTS.DB_DATA_TYPE_LIMITS.INT.MAX;
        vm.maxLength = CONSTANTS.INSERTION_ORDER.NAME.MAX_LENGTH;
        vm.maxLengthNotes = CONSTANTS.INSERTION_ORDER.NOTES.MAX_LENGTH;
        vm.mediaBuy = [];
        vm.numeric = CONSTANTS.REGEX.NUMERIC;
        vm.save = save;
        vm.user = [];

        MediaBuyService.getMediaBuyByCampaign(campaignId).then(function (mediaBuy) {
            vm.mediaBuy = mediaBuy;
        });

        function cancel() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('insertionOrder.confirm.discard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            }).result.then(
                function () {
                    $state.go('campaign-io-list');
                });
        }

        function save() {
            vm.promiseRequest = InsertionOrderService.saveInsertionOrder(formatIO(vm.insertionOrder));
            vm.promiseRequest.then(
                function () {
                    DialogFactory.showDismissableMessage(
                        DialogFactory.DISMISS_TYPE.SUCCESS,
                        'insertionOrder.successful'
                    );
                    $state.go('campaign-io-list');
                });
        }

        function formatIO(InsertionOrder) {
            var newIO = {};

            newIO.status = 'New';
            newIO.logicalDelete = 'N';
            newIO.name = InsertionOrder.name;
            newIO.notes = InsertionOrder.notes;
            newIO.mediaBuyId = vm.mediaBuy.id;
            newIO.ioNumber = InsertionOrder.ioNumber;

            return newIO;
        }
    }
})();
