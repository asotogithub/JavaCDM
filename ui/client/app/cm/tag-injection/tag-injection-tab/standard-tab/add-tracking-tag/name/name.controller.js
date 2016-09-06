(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddTrackingTagNameController', AddTrackingTagNameController);

    AddTrackingTagNameController.$inject = [
        '$filter',
        '$scope',
        'CONSTANTS',
        'UserService',
        'Utils'
    ];

    function AddTrackingTagNameController(
        $filter,
        $scope,
        CONSTANTS,
        UserService,
        Utils) {
        var vm = this,
            vmAddTrackingTab = $scope.$parent.vmAddTrackingTag;

        vm.HTML_TAG_TYPE = vmAddTrackingTab.HTML_TAG_TYPE;
        vm.contentMaxLength = CONSTANTS.INPUT.MAX_LENGTH.TRACKING_TAG_HTML_CONTENT;
        vm.maxLength = CONSTANTS.INPUT.MAX_LENGTH.TRACKING_TAG_NAME;
        vm.nameForm = {};
        vm.namePattern = new RegExp(CONSTANTS.REGEX.ALPHANUMERIC);
        vm.step = vmAddTrackingTab.STEP.NAME;
        vm.updateSelectedDomain = updateSelectedDomain;
        vm.urlMaxLength = CONSTANTS.INPUT.MAX_LENGTH.OPT_OUT_URL;
        vm.urlPattern = CONSTANTS.REGEX.OPT_OUT_URL;
        vm.validateHtmlContent = validateHtmlContent;

        function activate() {
            vm.actualTagType = vmAddTrackingTab.htmlTagType;
            resetForm();
            if (vmAddTrackingTab.htmlTagType === vm.HTML_TAG_TYPE.FACEBOOK_CUSTOM) {
                getDomains();
            }
        }

        function getDomains() {
            vmAddTrackingTab.promise = UserService.getDomains();
            vmAddTrackingTab.promise.then(function (domains) {
                vm.domains = $filter('orderBy')(domains, 'domain');
            });
        }

        function updateSelectedDomain() {
            vmAddTrackingTab.trackingTagDomain = vm.trackingTagDomain;
        }

        function resetForm() {
            vmAddTrackingTab.optOutURL = null;
            vmAddTrackingTab.trackingTagDomain = null;
            vmAddTrackingTab.trackingTagName = null;
            vmAddTrackingTab.htmlContent = null;
            vmAddTrackingTab.secureHtmlContent = null;
            vm.nameForm.$setPristine();
            if (vmAddTrackingTab.htmlTagType === vm.HTML_TAG_TYPE.CUSTOM) {
                vm.nameForm.tagContent.$setValidity('contentHtmlRequired', false);
                vm.nameForm.tagSecureContent.$setValidity('contentHtmlRequired', false);
            }
        }

        function validateHtmlContent(htmlContent, element) {
            var stringContent = htmlContent,
                regEx = new RegExp(CONSTANTS.REGEX.CONTENT_HTML, 'g'),
                matchTags = Utils.isUndefinedOrNull(stringContent) ? null : stringContent.match(regEx);

            if ((Utils.isUndefinedOrNull(vmAddTrackingTab.htmlContent) || vmAddTrackingTab.htmlContent === '') &&
                (Utils.isUndefinedOrNull(vmAddTrackingTab.secureHtmlContent) ||
                vmAddTrackingTab.secureHtmlContent === '')) {
                vm.nameForm[element].$setValidity('contentHtmlRequired', false);
                vm.nameForm[element].$setValidity('contentHtmlValid', true);
            }
            else {
                vm.nameForm.tagContent.$setValidity('contentHtmlRequired', true);
                vm.nameForm.tagSecureContent.$setValidity('contentHtmlRequired', true);
                if (htmlContent !== '' && Utils.isUndefinedOrNull(matchTags)) {
                    vm.nameForm[element].$setValidity('contentHtmlValid', false);
                }
                else {
                    vm.nameForm[element].$setValidity('contentHtmlValid', true);
                }
            }
        }

        $scope.$on(vm.step.key, function (event, data) {
            if (Utils.isUndefinedOrNull(vm.actualTagType) || vm.actualTagType !== vmAddTrackingTab.htmlTagType) {
                if (data.activate) {
                    activate();
                }
            }
        });

        $scope.$watch('vm.nameForm.$valid', function (newVal) {
            vm.step.isValid = newVal;
        });
    }
})();
