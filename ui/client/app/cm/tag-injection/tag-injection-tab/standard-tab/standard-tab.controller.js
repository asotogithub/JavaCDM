(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('StandardTabController', StandardTabController);

    StandardTabController.$inject = [
        '$q',
        '$scope',
        '$stateParams',
        '$translate',
        'AgencyService',
        'CONSTANTS',
        'DialogFactory',
        'TagInjectionAssociation',
        'TagInjectionUtilService',
        'Utils'
    ];

    function StandardTabController($q,
                                   $scope,
                                   $stateParams,
                                   $translate,
                                   AgencyService,
                                   CONSTANTS,
                                   DialogFactory,
                                   TagInjectionAssociation,
                                   TagInjectionUtilService,
                                   Utils) {
        var vm = this;

        vm.campaignSelected = $stateParams.campaign;
        vm.clearAssociations = clearAssociations;
        vm.closeFlyOut = closeFlyOut;
        vm.directAssociations = [];
        vm.flyOutDefaultState = CONSTANTS.FLY_OUT.STATE.FULL_VIEW;
        vm.flyOutModel = {};
        vm.inheritedAssociations = [];
        vm.isOpenFlyOut = false;
        vm.limitStringView = CONSTANTS.TAG_INJECTION.ASSOCIATION.LIMIT_STRING_VIEW;
        vm.loadTrackingTag = loadTrackingTag;
        vm.onCloseFlyout = onCloseFlyout;
        vm.onStart = onStart;
        vm.openFlyOut = openFlyOut;
        vm.performAction = performAction;
        vm.removeAssociation = removeAssociation;
        vm.removeTagsFromAssociations = removeTagsFromAssociations;
        vm.setAssociations = setAssociations;
        vm.vmTagInjectionTab = $scope.vmTagInjectionTab;
        vm.vmTagInjectionTab.campaignSelected = vm.campaignSelected;
        activate();

        function activate() {
            loadTrackingTag();
        }

        function loadTrackingTag(reloadAssociations) {
            if (Utils.isPromiseInProgress(vm.vmTagInjectionTab.promiseTab)) {
                vm.vmTagInjectionTab.promiseTab.then(function () {
                    resolveLoadTrackingTagPromise(reloadAssociations);
                });
            }
            else {
                resolveLoadTrackingTagPromise(reloadAssociations);
            }
        }

        function resolveLoadTrackingTagPromise(reloadAssociations) {
            vm.vmTagInjectionTab.promiseTab = AgencyService.getHTMLTagInjections().then(
                function (htmlTagInjectionList) {
                    vm.trackingTags = htmlTagInjectionList;
                    if (!Utils.isUndefinedOrNull(vm.campaignSelected.id)) {
                        $scope.$broadcast('tagInjection.loadCampaigns', {
                            campaignId: vm.campaignSelected.id,
                            advertiser: {
                                id: vm.campaignSelected.advertiserId
                            },
                            brand: {
                                id: vm.campaignSelected.brandId
                            }
                        });
                    }

                    if (!Utils.isUndefinedOrNull(reloadAssociations) && reloadAssociations) {
                        $scope.$broadcast('trackingTag.deleteTag');
                    }
                });
        }

        function openFlyOut(title, model) {
            vm.isOpenFlyOut = true;
            vm.flyOutModel = {
                title: title,
                data: model
            };
        }

        function closeFlyOut() {
            vm.isOpenFlyOut = false;
        }

        function onCloseFlyout() {
            $scope.$broadcast('te-flyout:closeFlyout');
        }

        function clearAssociations() {
            vm.directAssociations = [];
            vm.inheritedAssociations = [];
        }

        function onStart(event, ui, index, data) {
            vm.dragDropTag = data[index];
        }

        function removeAssociation(association) {
            var actionRemove = TagInjectionAssociation.buildPlacementActionRemove(association),
                promise = vm.vmTagInjectionTab.promiseTab = performAction(actionRemove);

            promise.then(function () {
                $scope.$broadcast('tagInjection.afterRemoveAssociation', {
                    placementRow: association
                });
            });
        }

        function setAssociations(placementRow, associations) {
            var direct = associations.directAssociations,
                inherited = associations.inheritedAssociations;

            direct = TagInjectionAssociation.mapPlacementProperties(direct, placementRow,
                TagInjectionAssociation.ASSOCIATION_TYPE.DIRECT);
            inherited = TagInjectionAssociation.mapPlacementProperties(inherited, placementRow,
                TagInjectionAssociation.ASSOCIATION_TYPE.INHERITED);

            vm.directAssociations = direct;
            vm.inheritedAssociations = inherited;
        }

        function removeTagsFromAssociations(tags) {
            $scope.$broadcast('tagInjection.removeTagsFromAssociations', {
                tags: tags
            });
        }

        function performAction(action) {
            var deferred = $q.defer();

            AgencyService.bulkSaveHtmlInjectionTagAssociations(
                action, $scope.vmTagInjectionTab.advertiser.id, $scope.vmTagInjectionTab.brand.id).then(
                function () {
                    deferred.resolve(action);
                });

            return deferred.promise;
        }

        function navigateDestination(data) {
            if (data.action) {
                data.action();
            }
            else {
                TagInjectionUtilService.goRouteDestiny(data);
            }
        }

        $scope.$on('status.newTagAssociation', function (event, data) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('global.confirm.closeUnsavedChanges'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.DISCARD_CANCEL
            }).result.then(
                function () {
                    navigateDestination(data);
                },

                function () {
                    if (data.revert) {
                        data.revert();
                    }
                });
        });
    }
})();
