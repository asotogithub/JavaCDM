(function () {
    'use strict';

    angular.module('uiApp')
        .service('CreativeUtilService', CreativeUtilService);

    CreativeUtilService.$inject = [
        '$q',
        '$translate',
        'CONSTANTS',
        'CreativeService',
        'DialogFactory',
        'Utils',
        'lodash'
    ];

    function CreativeUtilService(
        $q,
        $translate,
        CONSTANTS,
        CreativeService,
        DialogFactory,
        Utils,
        lodash) {
        this.isCreativeXML = function (creativeType) {
            return creativeType === CONSTANTS.CREATIVE.FILE_TYPE.XML ||
                creativeType === CONSTANTS.CREATIVE.FILE_TYPE.VAST ||
                creativeType === CONSTANTS.CREATIVE.FILE_TYPE.VMAP;
        };

        this.isCreativeVideoTemplate = function (creativeType) {
            return creativeType === CONSTANTS.CREATIVE.FILE_TYPE.VAST ||
                creativeType === CONSTANTS.CREATIVE.FILE_TYPE.VMAP;
        };

        this.isCreativeText = function (creativeType) {
            return creativeType === CONSTANTS.CREATIVE.FILE_TYPE.TXT;
        };

        this.hasClickThrough = function (creativeType) {
            return creativeType !== CONSTANTS.CREATIVE.FILE_TYPE.THIRD_PARTY;
        };

        this.duplicateAliasPopup = function (error, scope, creativeVersionedList, callback) {
            var wURL = 'app/services/creative/template/duplicate-alias-warning-dialog.html',
                errorListAPI = [],
                popupModel = [];

            if (error.data.error instanceof Array) {
                errorListAPI = error.data.error;
            }
            else {
                errorListAPI.push(error.data.error);
            }

            angular.forEach(errorListAPI, function (dupCreative) {
                angular.forEach(creativeVersionedList, function (creative) {
                    if (dupCreative.objectName === creative.filename) {
                        creative.versions.push({
                            alias: dupCreative.rejectedValue,
                            isDateSet: 1
                        });
                        popupModel.push(creative);
                    }
                });
            });

            scope.$broadcast('validateAllAlias');

            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                size: DialogFactory.DIALOG.SIZE.EXTRA_LARGE,
                windowClass: 'app-modal-window',
                title: $translate.instant('DIALOGS_OOPS'),
                partialHTML: wURL,
                partialHTMLParams: {
                    creatives: popupModel
                },
                buttons: {
                    yes: $translate.instant('global.back')
                }
            }).result.then(
                function () {
                    if (!Utils.isUndefinedOrNull(callback) && callback instanceof Function) {
                        callback();
                    }
                });
        };

        this.deleteCreativeWithConfirmation = function (params, finalFunction) {
            var creativeDeleted = $q.defer();

            CreativeService.getCreativeAssociationsCount(params.creativeId)
                .then(deleteCreativeWithConfirmationComplete);

            function deleteCreativeWithConfirmationComplete(data) {
                var message,
                    hasSchedules = false,
                    groupWithOutAssociation = [];

                if (data.length === 1) {
                    message = 'creative.confirm.deleteCreativeAssociatedOnlyOneGroup';
                    hasSchedules = data[0].schedules !== 0;
                }
                else {
                    if (params.tab === 'creative') {
                        message = 'creative.confirm.deleteCreativeAssociatedMultipleGroupsCreativeTab';

                        groupWithOutAssociation = lodash.filter(data,
                                function (item) {
                                    return item.schedules === 0;
                                }).map(function (a) {
                                return a.groupId;
                            });

                        hasSchedules = groupWithOutAssociation.length < data.length;
                    }
                    else {
                        message = 'creative.confirm.deleteCreativeAssociatedMultipleGroupsCreativeGroupTab';
                        hasSchedules = lodash.filter(data,
                            function (item) {
                                return item.groupId.toString() === params.creativeGroupId.toString();
                            })[0].schedules > 0;
                    }
                }

                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.WARNING,
                    title: $translate.instant('DIALOGS_WARNING'),
                    description: $translate.instant(message),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE
                }).result.then(
                    function () {
                        if (hasSchedules) {
                            CreativeService.getScheduleByCreative(params.creativeId).then(function (response) {
                                var wURL = 'app/services/creative/template/warning-dialog.html',
                                    deleteAssociation = groupWithOutAssociation.length < data.length &&
                                        groupWithOutAssociation.length !== 0;

                                DialogFactory.showCustomDialog({
                                    type: CONSTANTS.DIALOG.TYPE.WARNING,
                                    size: DialogFactory.DIALOG.SIZE.EXTRA_LARGE,
                                    windowClass: 'app-modal-window',
                                    title: $translate.instant('DIALOGS_OOPS'),
                                    partialHTML: wURL,
                                    partialHTMLParams: {
                                        placements: response,
                                        groupName: response.map(function (a) {
                                            return a.creativeGroupName;
                                        })
                                    },
                                    buttons: deleteAssociation ?
                                        CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE :
                                        CONSTANTS.DIALOG.BUTTON_SET.OK,
                                    footer: deleteAssociation ? {
                                        title: $translate.instant(
                                            'creative.confirm.deleteRemainingUnassociatedCreatives'
                                        )
                                    } : undefined
                                }).result.then(
                                    function () {
                                        if (deleteAssociation) {
                                            deleterCreative(
                                                params.creativeId,
                                                groupWithOutAssociation,
                                                creativeDeleted,
                                                finalFunction
                                            );
                                        }
                                        else {
                                            creativeDeleted.resolve();
                                        }
                                    });

                                creativeDeleted.resolve();
                            });
                        }
                        else {
                            var groupIdList;

                            if (params.tab === 'creative') {
                                groupIdList = lodash.filter(data,
                                    function (item) {
                                        return item.schedules === 0;
                                    }).map(function (result) {
                                        return result.groupId;
                                    });
                            }
                            else {
                                groupIdList = [params.creativeGroupId];
                            }

                            deleterCreative(params.creativeId, groupIdList, creativeDeleted, finalFunction);
                        }
                    },

                    dialogResultComplete
                );
            }

            function dialogResultComplete() {
                creativeDeleted.resolve();
            }

            return creativeDeleted.promise;
        };

        function deleterCreative(creativeId, groupWithOutAssociation, deferred, finalFunction) {
            CreativeService.deleteCreative(creativeId, groupWithOutAssociation).then(
                function (response) {
                    finalFunction();
                    deferred.resolve(response);
                });
        }
    }
})();
