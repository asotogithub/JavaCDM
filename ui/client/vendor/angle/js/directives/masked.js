/**=========================================================
 * Module: masked,js
 * Initializes the masked inputs
 =========================================================*/

angular.module('angle').directive('masked', [function(){
    'use strict';

    return {
        restrict: 'A',
        controller: ["$scope", "$element", function($scope, $element) {
            var $elem = $($element);
            if($.fn.inputmask)
                $elem.inputmask();
        }]
    };

}]);
