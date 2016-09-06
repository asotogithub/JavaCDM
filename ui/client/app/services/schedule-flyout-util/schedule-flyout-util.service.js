(function () {
'use strict';

angular
    .module('uiApp')
    .service('ScheduleFlyoutUtilService', ScheduleFlyoutUtilService);
function ScheduleFlyoutUtilService() {
    var bulkEditOptions = {
        isCreativeVisible: false,
        isCreativeGroupVisible: false
    };

    return {
        bulkEditOptions: function () {
            return bulkEditOptions;
        },

        resetBulkEditOptions: function () {
            bulkEditOptions.isCreativeGroupVisible = false;
            bulkEditOptions.isCreativeVisible = false;
        },

        hasSiteVisibleCreatives: function (site) {
            this.resetBulkEditOptions();
            var result = false,
                i = 0;

            if (site.expanded) {
                while (!result && i < site.children.length) {
                    result = this.hasSectionVisibleCreatives(site.children[i]);
                    i++;
                }
            }

            return result;
        },

        hasSectionVisibleCreatives: function (section) {
            var result = false,
                i = 0;

            if (section.expanded) {
                while (!result && i < section.children.length) {
                    result = this.hasPlacementVisibleCreatives(section.children[i]);
                    i++;
                }
            }

            return result;
        },

        hasPlacementVisibleCreatives: function (placement) {
            var result = false,
                i = 0;

            if (placement.expanded) {
                while (!result && i < placement.children.length) {
                    result = this.hasCreativeGroupVisibleCreatives(placement.children[i]);
                    i++;
                }
            }

            return result;
        },

        hasCreativeGroupVisibleCreatives: function (creativeGroup) {
            if (creativeGroup.expanded) {
                bulkEditOptions.isCreativeGroupVisible = false;
                bulkEditOptions.isCreativeVisible = true;
                return true;
            }
            else {
                bulkEditOptions.isCreativeGroupVisible = true;
                bulkEditOptions.isCreativeVisible = false;
            }

            return false;
        }
    };
}
})();
