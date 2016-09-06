(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignCreativeDetailsTabController', CampaignCreativeDetailsTabController);

    CampaignCreativeDetailsTabController.$inject = [
        '$filter',
        '$q',
        '$sce',
        '$scope',
        '$state',
        '$stateParams',
        '$timeout',
        '$window',
        'CONSTANTS',
        'CreativeGroupService',
        'CreativeService',
        'CreativeUtilService',
        'DialogFactory',
        'Utils',
        'lodash'
    ];

    function CampaignCreativeDetailsTabController(
        $filter,
        $q,
        $sce,
        $scope,
        $state,
        $stateParams,
        $timeout,
        $window,
        CONSTANTS,
        CreativeGroupService,
        CreativeService,
        CreativeUtilService,
        DialogFactory,
        Utils,
        lodash) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            creativeId = $stateParams.creativeId,
            creativeGroupId = $stateParams.creativeGroupId,
            fromState = $stateParams.from,
            COUNT_PROPERTIES = 5;

        vm.aliasChanged = aliasChanged;
        vm.close = close;
        vm.clickThroughArray = [];
        vm.creativeVersionsForm = {};
        vm.creativeDetailsForm = {};
        vm.dateFormat = CONSTANTS.DATE.DATE_FORMAT;
        vm.extendedPropertiesArray = [];
        vm.externalIdChange = externalIdChange;
        vm.editCreativeForm = {};
        vm.hasClickThrough = CreativeUtilService.hasClickThrough;
        vm.isCreativeText = CreativeUtilService.isCreativeText;
        vm.isCreativeVideoTemplate = false;
        vm.isCreativeXML = CreativeUtilService.isCreativeXML;
        vm.urlCreative = null;
        vm.loadCreativeGroups = loadCreativeGroups;
        vm.deleteCreative = deleteCreative;
        vm.maxLength = CONSTANTS.INPUT_MAX_LENGTH;
        vm.maxSearchElements = CONSTANTS.CREATIVE_GROUP.MAX_SEARCH_ELEMENTS;
        vm.save = save;
        vm.validateDuplicatedAlias = validateDuplicatedAlias;
        vm.video = {
            defaultHeight: 'auto',
            defaultWidth: '100%',
            isLoaded: true,
            onError: setVideoError,
            type: null,
            url: null
        };

        activate();

        function activate() {
            getCreative(creativeId);
        }

        function aliasChanged() {
            if (!Utils.isUndefinedOrNull(vm.creative.versions)) {
                var length = vm.creative.versions.length,
                    currentVersion = vm.creative.versions[length - 1];

                currentVersion.alias = vm.creative.alias;
            }
        }

        function externalIdChange() {
            var inputBox = angular.element('#creativeExtId')[0],
                cursorPosition = inputBox.selectionStart;

            vm.creative.externalId = vm.creative.externalId.replace(/ /g, '_');

            $timeout(function () {
                inputBox.setSelectionRange(cursorPosition, cursorPosition);
            }, 1);
        }

        function getCreative(id) {
            vm.promise = CreativeService.getCreative(id);
            vm.promise.then(
                function (response) {
                    if (!vm.hasClickThrough(response.creativeType) &&
                        $state.current.name !== 'campaign-creative-details-extended-properties') {
                        $state.go('campaign-creative-details-extended-properties', null, {
                            location: 'replace'
                        });
                    }

                    vm.creative = response;
                    $scope.$parent.vmDetails.creativeName = vm.creative.alias;
                    populateClickThroughs(vm.creative);
                    populateExtendedProperties(vm.creative);
                    getCreativePreview(creativeId, vm.creative.creativeType);
                });
        }

        function loadCreativeGroups(query) {
            var promiseCreatives = $q.defer();

            CreativeGroupService.searchByCampaignAndName($stateParams.campaignId, query).then(function (response) {
                promiseCreatives.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].CreativeGroup)));
            });

            return promiseCreatives.promise;
        }

        function populateClickThroughs(creative) {
            if (creative.clickthrough) {
                vm.clickThroughArray.push({
                    sequence: null,
                    url: vm.creative.clickthrough,
                    name: 'clickthrough1'
                });
            }

            if (creative.clickthroughs) {
                var i = 0,
                    ct;

                for (i = 0; i < creative.clickthroughs.length; i++) {
                    ct = creative.clickthroughs[i];

                    vm.clickThroughArray.push({
                        sequence: ct.sequence,
                        url: ct.url,
                        name: 'clickthrough' + ct.sequence
                    });
                }
            }
        }

        function populateExtendedProperties(creative) {
            var i, name, value;

            for (i = 1; i <= COUNT_PROPERTIES; i++) {
                name = 'extProp' + i;
                value = creative['extProp' + i];
                vm.extendedPropertiesArray.push({
                    value: value,
                    name: name
                });
            }
        }

        function deleteCreative() {
            var params = {
                tab: fromState === 'creative-group-creative-list' ? 'creative-group' : 'creative',
                creativeId: creativeId,
                creativeGroupId: creativeGroupId
            };

            vm.promise = CreativeUtilService.deleteCreativeWithConfirmation(params, close);
        }

        function close() {
            if (fromState === 'creative-group-creative-list') {
                $state.go('creative-group-creative-list', {
                    campaignId: campaignId,
                    creativeGroupId: creativeGroupId
                });
            }
            else {
                $state.go('creative-list', {
                    campaignId: campaignId
                });
            }
        }

        function formatCreative(creative) {
            var sequence = 1,
                i,
                name;

            creative.clickthroughs = [];
            angular.forEach(vm.clickThroughArray, function (ct) {
                ct.sequence = sequence;
                creative.clickthroughs.push(ct);
                sequence++;
            });

            for (i = 1; i <= COUNT_PROPERTIES; i++) {
                name = 'extProp' + i;
                creative[name] = vm.extendedPropertiesArray[i - 1].value;
            }

            creative.clickthrough = vm.clickThroughArray[0].url;
            creative.clickthroughs.splice(0, 1);

            return creative;
        }

        function save() {
            var formattedCreative = formatCreative(vm.creative);

            vm.promise = CreativeService.updateCreative(formattedCreative);
            processSavePromise(vm.promise);
        }

        function processSavePromise(savePromise) {
            savePromise.then(
                function () {
                    $scope.$parent.vmDetails.creativeName = vm.creative.alias;
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    vm.editCreativeForm.$setPristine();
                });
        }

        function getCreativePreview(id, creativetype) {
            if (vm.isCreativeXML(creativetype)) {
                vm.video.url = null;
                vm.video.type = null;
                vm.promise = CreativeService.getCreativeTemplateFile(id);
                vm.promise.then(function (data) {
                    var xmlContentFile = String.fromCharCode.apply(null, new Uint8Array(data));

                    parseCreativeVideoTemplate(xmlContentFile);
                });
            }
            else {
                vm.promise = CreativeService.getCreativeFile(id,
                    creativetype === CONSTANTS.CREATIVE.FILE_TYPE.TXT ? 'text/plain' : 'arraybuffer');
                vm.promise.then(function (data) {
                        if (vm.isCreativeText(creativetype)) {
                            vm.urlCreative = data;
                        }
                        else {
                            var arrayBufferView = new Uint8Array(data),
                                file = new Blob([arrayBufferView], {
                                    type: 'image/*'
                                });

                            vm.urlCreative = $window.URL.createObjectURL(file);
                        }
                    },

                    function () {
                        vm.urlCreative = null;
                    });
            }
        }

        function parseCreativeVideoTemplate(xml) {
            var xmlParser = new $window.DOMParser(),
                xmlDoc = xmlParser.parseFromString(xml, 'text/xml'),
                xmlElement = xmlDoc.getElementsByTagName('MediaFile')[0],
                urlValue, typeValue;

            if (!Utils.isUndefinedOrNull(xmlElement)) {
                typeValue = xmlElement.getAttribute('type');
                urlValue = xmlElement.childNodes.length > 0 ?
                    xmlElement.childNodes[0].wholeText.toString().trim() :
                    null;

                if (!Utils.isUndefinedOrNull(typeValue) && !Utils.isUndefinedOrNull(urlValue)) {
                    vm.video.type = typeValue;
                    vm.video.url = $sce.trustAsResourceUrl(urlValue);
                    vm.isCreativeVideoTemplate = true;
                }
            }
        }

        function setVideoError() {
            vm.video.isLoaded = false;
        }

        function validateDuplicatedAlias(model) {
            var uniqueList = lodash.uniq(model, function (item) {
                    return item.versionNumber;
                }),

                result = uniqueList.map(function (res) {
                    return res.alias;
                }),

                fieldName = 'creativeAlias';

            if (lodash.uniq(result).length === result.length) {
                vm.creativeDetailsForm[fieldName].$setValidity('duplicatedField', true);
            }

            return result;
        }

        $scope.$watch('vmEdit.creative.creativeGroups[0].creativeGroups.length', function (newLength) {
            if (newLength > 1) {
                vm.creative.creativeGroups[0].creativeGroups =
                    $filter('orderBy')(vm.creative.creativeGroups[0].creativeGroups, 'name');
            }
        });
    }
})();

