(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CookieTargetingController', CookieTargetingController);

    CookieTargetingController.$inject = [
        '$element',
        '$scope',
        'CookieDomainsService'
    ];

    function CookieTargetingController($element, $scope, CookieDomainsService) {
        var vm = this,
            events =
                'afterUpdateGroupCondition.queryBuilder ' +
                'afterDeleteRule.queryBuilder ' +
                'afterDeleteGroup.queryBuilder ' +
                'afterUpdateRuleValue.queryBuilder ' +
                'afterUpdateRuleFilter.queryBuilder ' +
                'afterUpdateRuleOperator.queryBuilder',
            cachedSql = '';

        vm.$cookieBuilder = null;
        vm.builderTabActive = true;
        vm.builderTabDisabled = false;
        vm.builderValidationError = null;
        vm.cookieTargets = null;
        vm.editorTabActive = false;
        vm.getCookieTargets = getCookieTargets;
        vm.promise = null;
        vm.setDirty = setDirty;
        vm.getSql = getSql;
        vm.switchToBuilderTab = switchToBuilderTab;
        vm.switchToEditorTab = switchToEditorTab;

        activate();

        function activate() {
            $scope.$watch('vm.model', function () {
                if (vm.builderTabActive && vm.model && vm.model.campaignId) {
                    // Since cookie targeting is default, we don't need to watch for when it becomes visible.
                    getCookieTargets(vm.model.campaignId);
                }
            });
        }

        function getCookieTargets(campaignId) {
            if (!vm.cookieTargets) {
                var promise = vm.promise = CookieDomainsService.getCookies(campaignId);

                promise.then(function (data) {
                    vm.cookieTargets = data;
                    reconcileTargets();
                    vm.builderTabActive = vm.$cookieBuilder !== null;
                    vm.builderTabDisabled = vm.editorTabActive = !vm.builderTabActive;
                });
            }
        }

        function setDirty() {
            setModelFromBuilder();
            vm.$form.$setDirty();
            $scope.$evalAsync();
        }

        function getSql() {
            var $cookieBuilder = vm.$cookieBuilder;

            if (vm.$cookieBuilder.queryBuilder('validateBuilder')) {
                cachedSql = $cookieBuilder.queryBuilder('rulesToSql', $cookieBuilder.queryBuilder('getRules'));
            }

            return cachedSql;
        }

        function switchToBuilderTab() {
            var $cookieBuilder = vm.$cookieBuilder,
                validationErrors;

            if ($cookieBuilder !== null) {
                validationErrors = $cookieBuilder.queryBuilder('validateSql', vm.model.cookieTarget);

                if (validationErrors.length === 0) {
                    vm.builderValidationError = null;
                    removeEvents();
                    activateTab('builder');
                    setBuilderFromModel();
                    $cookieBuilder.queryBuilder('validateBuilder');
                    addEvents();
                }
                else {
                    vm.builderValidationError = validationErrors;
                    activateTab('editor');
                }
            }
        }

        function switchToEditorTab() {
            if (vm.$cookieBuilder !== null) {
                if (!vm.$cookieBuilder.queryBuilder('validateBuilder')) {
                    vm.builderValidationError = ['creativeGroup.cookieTargeting.error.invalidBuilder'];
                }
            }

            activateTab('editor');
            setModelFromBuilder();
        }

        function reconcileTargets() {
            initCookieBuilder();
            if (vm.$cookieBuilder) {
                switchToBuilderTab();
                setModelFromBuilder();
            }
        }

        function setModelFromBuilder() {
            if (vm.builderTabActive && vm.$cookieBuilder.queryBuilder('validateBuilder')) {
                vm.model.cookieTarget = getSql();
            }
        }

        function removeEvents() {
            vm.$cookieBuilder.off(events);
            vm.$cookieBuilder.off('validationError.queryBuilder');
        }

        function activateTab(tabName) {
            switch (tabName) {
                case 'builder':
                    vm.builderTabActive = true;
                    vm.editorTabActive = false;
                    break;
                case 'editor':
                    vm.builderTabActive = false;
                    vm.editorTabActive = true;
                    break;
            }
        }

        function setBuilderFromModel() {
            if (vm.model &&
                vm.model.cookieTarget !== getSql()) {
                var $cookieBuilder = vm.$cookieBuilder,
                    rules = $cookieBuilder.queryBuilder('sqlToRules', vm.model.cookieTarget);

                $cookieBuilder.queryBuilder('setRules', rules);
            }
        }

        function addEvents() {
            vm.$cookieBuilder.on(events, function () {
                if (vm.builderTabActive) {
                    var sql = getSql();

                    if (sql !== vm.model.cookieTarget) {
                        vm.model.cookieTarget = sql;
                        setDirty();
                    }
                }
            });
        }

        function initCookieBuilder() {
            if (vm.cookieTargets && vm.cookieTargets.length && vm.model) {
                var $cookieBuilder = vm.$cookieBuilder =
                    $element.find('#cookieBuilder').queryBuilder({
                        filters: vm.cookieTargets,
                        //jscs:disable requireCamelCaseOrUpperCaseIdentifiers
                        plugins: {
                            cookie_builder: null
                        },
                        allow_empty: true
                        //jscs:enable
                    });

                $scope.$on('$destroy', function () {
                    removeEvents();
                    $cookieBuilder.queryBuilder('destroy');
                });
            }
        }
    }
})();
