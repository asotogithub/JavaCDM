(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SendAdTagsController', SendAdTagsController);

    SendAdTagsController.$inject = [
        '$scope',
        '$translate',
        '$uibModalInstance',
        'AgencyService',
        'CONSTANTS',
        'DialogFactory',
        'PlacementService',
        'Utils',
        'data',
        'lodash',
        'uuid'
    ];

    function SendAdTagsController(
        $scope,
        $translate,
        $uibModalInstance,
        AgencyService,
        CONSTANTS,
        DialogFactory,
        PlacementService,
        Utils,
        data,
        lodash,
        uuid) {
        var vm = this;

        vm.EMAIL_PATTERN = CONSTANTS.REGEX.EMAIL;
        vm.SEND_AD_TAGS_PAGE_SIZE = CONSTANTS.TAGS.SEND_AD_TAGS.PAGE_SIZE;
        vm.clearAll = clearAll;
        vm.dismiss = dismiss;
        vm.emailList = [];
        vm.formatList = lodash.values(CONSTANTS.TAGS.SEND_AD_TAGS.FORMAT_TYPE.LIST);
        vm.isIndividualRecipientEmpty = isIndividualRecipientEmpty;
        vm.legendInfo = {
            recipientsCount: 0,
            recipientsSelected: 0
        };
        vm.onEmailTagAdded = onEmailTagAdded;
        vm.onEmailTagRemoved = onEmailTagRemoved;
        vm.onSelectFormat = onSelectFormat;
        vm.currentSelectedRecipients = [];
        vm.promise = null;
        vm.recipientsLegendMessage = '';
        vm.recipientsList = [];
        vm.recipientsTableUUID = uuid.v4();
        vm.selectedRecipientsCallback = selectedRecipientsCallback;
        vm.sendAndClose = sendAndClose;
        vm.siteList = [];
        vm.siteWithFormatList = [];
        vm.tagsList = [];
        vm.unselectedSiteCount = 0;
        vm.validForm = false;

        activate();

        function activate() {
            getDataList();
        }

        function buildSendAdTagsPayload() {
            var key, tagEmailObject,
                payload = {
                    tagEmailSites: [],
                    toEmails: getEmailList(vm.emailList),
                    userEmail: CONSTANTS.TAGS.SEND_AD_TAGS.EMAIL_CLIENT
                };

            for (key in vm.tagsList) {
                if (vm.tagsList.hasOwnProperty(key)) {
                    tagEmailObject = {
                        fileType: getEmailFiletype(key),
                        placementIds: getPlacementList(key),
                        recipients: getEmailList(vm.tagsList[key].emailList),
                        siteId: parseInt(key)
                    };

                    payload.tagEmailSites.push(tagEmailObject);
                }
            }

            return payload;
        }

        function clearAll() {
            //FIXME: Use angular-way once ngTagInput team add the feature to remove invalid tags
            var emailContainerScope = angular.element('.email-container-adTags').scope();

            vm.emailList = [];
            updateTableSelectionList(vm.emailList);

            if (!Utils.isUndefinedOrNull(emailContainerScope)) {
                emailContainerScope.$$childTail.newTag.text(null);
                emailContainerScope.$$childTail.events.trigger('input-focus');
            }
        }

        function dismiss() {
            $uibModalInstance.dismiss();
        }

        function getDataList() {
            vm.promise = AgencyService.getUsersTrafficking();
            vm.promise.then(function (response) {
                vm.recipientsList = response;
                vm.legendInfo.recipientsCount = vm.recipientsList.length;
                updateRecipientsLegendMessage();
            });

            vm.siteList = data.siteList;
            vm.unselectedSiteCount = vm.siteList.length;
        }

        function getEmailFiletype(siteId) {
            var siteObject = lodash.find(vm.siteList, function (site) {
                return site.siteId.toString() === siteId;
            });

            return Utils.isUndefinedOrNull(siteObject) ? vm.tagsList[siteId].format.KEY :
                siteObject.isOnly1x1 &&
                vm.tagsList[siteId].format.KEY === CONSTANTS.TAGS.SEND_AD_TAGS.FORMAT_TYPE.LIST.XLSX.KEY ?
                    CONSTANTS.TAGS.SEND_AD_TAGS.FORMAT_TYPE.XLSX_1X1 : vm.tagsList[siteId].format.KEY;
        }

        function getEmailList(emailList) {
            return lodash.map(emailList, function (emailObject) {
                return emailObject.text;
            });
        }

        function getPlacementList(siteId) {
            var siteObject = lodash.find(vm.siteList, function (site) {
                return site.siteId.toString() === siteId;
            });

            return Utils.isUndefinedOrNull(siteObject) ? [] : siteObject.placements;
        }

        function isIndividualRecipientEmpty() {
            var key;

            for (key in vm.tagsList) {
                if (vm.tagsList.hasOwnProperty(key) &&
                    !Utils.isUndefinedOrNull(vm.tagsList[key].emailList) && vm.tagsList[key].emailList.length > 0) {
                    return false;
                }
            }

            return true;
        }

        function onEmailTagAdded(addedTag) {
            var foundElement = lodash.find(vm.recipientsList, function (recipient) {
                return recipient.userName === addedTag.text;
            });

            if (foundElement) {
                vm.currentSelectedRecipients.push(foundElement);
                updateTableSelectionList(vm.currentSelectedRecipients);
            }
        }

        function onEmailTagRemoved(removedTag) {
            if (vm.currentSelectedRecipients.length > 0) {
                var removedElements = lodash.remove(vm.currentSelectedRecipients, function (recipient) {
                    return recipient.userName === removedTag.text;
                });

                if (removedElements.length > 0) {
                    updateTableSelectionList(vm.currentSelectedRecipients);
                }
            }
        }

        function onSelectFormat(siteId) {
            if (vm.unselectedSiteCount > 0 && lodash.indexOf(vm.siteWithFormatList, siteId) < 0) {
                vm.siteWithFormatList.push(siteId);
                vm.unselectedSiteCount--;
            }
        }

        function selectedRecipientsCallback(selectedList) {
            if (selectedList) {
                vm.legendInfo.recipientsSelected = selectedList.length;
            }

            updateRecipientsLegendMessage();
            updateEmailList(selectedList);
        }

        function sendAndClose() {
            vm.promise = PlacementService.sendTagEmail(buildSendAdTagsPayload())
                .then(function () {
                    $uibModalInstance.close();

                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                        $translate.instant('tags.sendAdTags.successfullySent'));
                });
        }

        function updateRecipientsLegendMessage() {
            vm.recipientsLegendMessage = $translate.instant('tags.sendAdTags.rowsRecipientsSelected', {
                rows: vm.legendInfo.recipientsCount,
                rowsSelected: vm.legendInfo.recipientsSelected
            });
        }

        function updateEmailList(selectedList) {
            for (var i = 0; i < selectedList.length; i++) {
                if (lodash.where(vm.emailList, {
                    text: selectedList[i].userName
                }).length === 0) {
                    vm.emailList.push({
                        text: selectedList[i].userName
                    });
                }
            }

            if (selectedList.length < vm.currentSelectedRecipients.length) {
                lodash.difference(vm.currentSelectedRecipients, selectedList).forEach(function (unselected) {
                    lodash.remove(vm.emailList, function (value) {
                        return value.text === unselected.userName;
                    });
                });
            }

            vm.currentSelectedRecipients = selectedList;
        }

        function updateTableSelectionList(selection) {
            var args = {};

            args.uuid = vm.recipientsTableUUID ;
            args.selection = selection;
            $scope.$broadcast('te-table:updateSelectionList', args);
        }
    }
})();
