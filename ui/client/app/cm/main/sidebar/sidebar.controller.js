(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SidebarController', SidebarController);

    SidebarController.$inject = [
        '$state'
    ];

    function SidebarController(
        $state
    ) {
        var vm = this;

        vm.menu = {
            MEDIA: {
                CAMPAIGNS: {
                    state: 'campaigns-list'
                },
                TAG_INJECTION: {
                    state: 'tag-injection-standard'
                },
                TAGS: {
                    state: 'tag-placement-list'
                },
                SITE_MEASUREMENT: {
                    state: 'site-measurements-list'
                }
            }
        };

        vm.getMenuItemPropClasses = getMenuItemPropClasses;
        vm.sidebarMenu = {
            isMediaCollapsed: true
        };

        vm.toggleMediaItem = toggleMediaItem;

        function getMenuItemPropClasses(menu) {
            return $state.current.name === menu.state ? 'active' : '';
        }

        function toggleMediaItem() {
            vm.sidebarMenu.isCollapsed = !vm.sidebarMenu.isCollapsed;
        }
    }
})();
