(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider'
    ];

    function State(
        $stateProvider) {
        $stateProvider
            .state('add-tracking-tag', {
                parent: 'tag-injection',
                url: '/add-tracking-tag',
                templateUrl:
                    'app/cm/tag-injection/tag-injection-tab/standard-tab/add-tracking-tag/add-tracking-tag.html',
                controller: 'AddTrackingTagController',
                controllerAs: 'vmAddTrackingTag',
                ncyBreadcrumb: {
                    label: '{{"tagInjection.newTagInjection" | translate}}',
                    parent: 'tag-injection'
                }
            });
    }
})();
