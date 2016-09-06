(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddTrackingTagController', AddTrackingTagController);

    AddTrackingTagController.$inject = [
        '$scope',
        '$state',
        'AgencyService',
        'DialogFactory'
    ];

    function AddTrackingTagController(
        $scope,
        $state,
        AgencyService,
        DialogFactory) {
        var vm = this;

        vm.HTML_TAG_TYPE = {
            CUSTOM: {
                key: 'custom',
                name: 'global.custom'
            },
            AD_CHOICES: {
                key: 'adChoices',
                name: 'tagInjection.adChoices'
            },
            FACEBOOK_CUSTOM: {
                key: 'facebookCustom',
                name: 'tagInjection.facebookCustom'
            }
        };
        vm.STEP = {
            TAG_TYPE: {
                index: 1,
                isValid: false,
                key: 'addTrackingTag.tagType'
            },
            NAME: {
                index: 2,
                isValid: false,
                key: 'addTrackingTag.name'
            }
        };
        vm.activateStep = activateStep;
        vm.cancel = cancel;
        vm.save = save;

        function activateStep(step) {
            if (!step) {
                return;
            }

            $scope.$broadcast(step.key, {
                activate: true
            });
        }

        function cancel() {
            $state.go('tag-injection-standard');
        }

        function save() {
            vm.promise = AgencyService.saveTrackingTag(formatTrackingTag(), vm.htmlTagTypeKey).then(
                function () {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    $state.go('tag-injection-standard');
                });
        }

        function formatTrackingTag() {
            var trackingTag = {};

            trackingTag.name = vm.trackingTagName;
            if (vm.htmlTagType === vm.HTML_TAG_TYPE.AD_CHOICES) {
                vm.htmlTagTypeKey = 'htmlInjectionTypeAdChoices';
                trackingTag.optOutUrl = vm.optOutURL;
            }
            else if (vm.htmlTagType === vm.HTML_TAG_TYPE.FACEBOOK_CUSTOM) {
                vm.htmlTagTypeKey = 'htmlInjectionTypeFacebook';
                trackingTag.firstPartyDomainId = vm.trackingTagDomain.id;
            }
            else {
                vm.htmlTagTypeKey = 'htmlInjectionTypeCustom';
                trackingTag.tagContent = vm.htmlContent;
                trackingTag.secureTagContent = vm.secureHtmlContent;
            }

            return trackingTag;
        }
    }
})();
