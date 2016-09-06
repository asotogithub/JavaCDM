(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('scheduleInsertions', ScheduleInsertionsDirective);

    ScheduleInsertionsDirective.$inject = [
        '$animate',
        '$compile',
        '$timeout',
        '$window'
    ];

    function ScheduleInsertionsDirective(
        $animate,
        $compile,
        $timeout,
        $window) {
        var animateDuration = 0.7 * 10,
            animationDelay = 200;

        function resizeGrid() {
            escapeTrigger();
            $timeout(function () {
                angular.element($window).triggerHandler('resize');
            }, animateDuration + animationDelay);
        }

        function escapeTrigger() {
            //TODO: this is a workaround. Remove this when a better solution is found.
            var esc = $.Event('keydown', {
                keyCode: 27
            });

            angular.element('.jqx-input').trigger(esc);
        }

        return {
            bindToController: true,
            controller: 'ScheduleInsertionsController',
            controllerAs: 'vm',
            restrict: 'E',
            templateUrl: 'app/cm/campaigns/schedule-tab/schedule-insertions/schedule-insertions.html',
            scope: {
                onSelectItem: '=',
                rowCollapsed: '=',
                rowExpanded: '='
            },

            link: function (scope, element, attrs, controller) {
                element.on('rowVisible', function () {
                    $timeout(function () {
                        angular.forEach(element.find('.compileMe'), function (val) {
                            $compile(val)(scope);
                        });
                    }, 100);
                });

                element.on('flyout', function (e, data) {
                    var el = angular.element(e.target),
                    ghost = angular.element('#scheduleInsertionsContainerGhost'),
                    from = {},
                    to = {};

                    escapeTrigger();
                    if (el.hasClass('schedule-insertions-container-done')) {
                        el.removeClass('schedule-insertions-animations');
                        el.addClass('schedule-insertions-container-initial');
                        el.removeClass('schedule-insertions-container-done');
                        el.removeAttr('style');
                    }

                    from = {
                        top: data.offsetTop - ghost.offset().top,
                        left: data.left,
                        width: data.width,
                        height: data.height,
                        opacity: 0
                    };
                    el.css(from);
                    to.width = ghost.width();
                    to.height = 'auto';
                    to.top = 0;
                    to.left = ghost.offset().left - data.offsetLeft;
                    to.opacity = 1;

                    el.addClass('schedule-insertions-animations');
                    $animate.addClass(el, 'schedule-insertions-container-moving');
                    $animate.removeClass(el, 'schedule-insertions-container-initial');
                    $animate.animate(el, from, to).then(function () {
                        $timeout(function () {
                            el.addClass('schedule-insertions-container-done');
                            el.removeClass('schedule-insertions-container-moving');
                            el.css({
                                width: '',
                                left: '',
                                right: 0,
                                top: 0,
                                height: 'auto',
                                opacity: 1
                            });
                            el.on('webkitTransitionEnd MozTransitionEnd oTransitionEnd transitionend',
                                function () {
                                    $timeout(function () {
                                        resizeGrid();
                                    });
                                });
                        });
                    });
                });

                element.on('flyoutClose', function (e) {
                    var el = angular.element(e.target),
                        from,
                        to;

                    escapeTrigger();
                    el.css({
                        width: el.width(),
                        height: el.height(),
                        left: el.position().left,
                        right: ''
                    });
                    from = {
                        left: el.offset().left,
                        opacity: 1
                    };
                    to = {
                        left: el.width() * 2 + 300,
                        opacity: 0
                    };
                    el.addClass('schedule-insertions-container-moving');
                    $timeout(function () {
                        $animate.animate(el, from, to).then(function () {
                            $timeout(function () {
                                el.removeClass('schedule-insertions-animations');
                                el.addClass('schedule-insertions-container-initial');
                                el.removeClass('schedule-insertions-container-done');
                                el.removeAttr('style');
                                resizeGrid();
                                controller.onCloseFlyout();
                            }, animateDuration + animationDelay);
                        });
                    });
                });

                element.on('flyoutFull', function (e) {
                    var el = angular.element(e.target);

                    el.css({
                        width: '100%'
                    });
                });

                element.on('flyoutHalf', function (e) {
                    var el = angular.element(e.target);

                    el.css({
                        width: '75%'
                    });
                });
            }
        };
    }
})();
