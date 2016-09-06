(function () {
    'use strict';

    angular
        .module('uiApp')
        .factory('DialogFactory', DialogFactory);

    DialogFactory.$inject = ['$translate', 'dialogs', 'CONSTANTS', 'growl', 'lodash', '$cookies', 'storage', '$q'];

    function DialogFactory($translate, dialogs, CONSTANTS, growl, lodash, $cookies, storage, $q) {
        //var logger = Logger.getInstance('DialogFactory');
        var NOT_AVAILABLE,
            LABEL_OK,
            LABEL_YES,
            LABEL_NO,
            LABEL_CANCEL,
            LABEL_KEEP,
            LABEL_DISCARD,
            LABEL_DELETE,
            LABEL_REMOVE,
            TITLE_ERROR,
            TITLE_CONFIRMATION,
            TITLE_GENERIC,
            DESC_CONF_ACCEPT,
            ACTION_FAILURE,
            DESC_DISCARD,
            TITLE_DISCARD,
            ACTION_SUCCESS,
            DONT_SHOW_AGAIN,
            DONT_SHOW_AGAIN_KEY;

        $translate([
            'global.na',
            'global.ok',
            'global.yes',
            'global.no',
            'global.cancel',
            'global.keep',
            'global.discard',
            'global.delete',
            'global.remove',
            'global.error',
            'global.confirmAction',
            'global.bidOpt',
            'confirm.overrideValues',
            'error.performOperation',
            'confirm.leaveScreen',
            'confirm.discardChanges',
            'info.operationCompleted',
            'global.errors.failure',
            'global.dontShowAgain'
        ]).then(function (translations) {
            NOT_AVAILABLE = translations['global.na'];
            LABEL_OK = translations['global.ok'];
            LABEL_YES = translations['global.yes'];
            LABEL_NO = translations['global.no'];
            LABEL_CANCEL = translations['global.cancel'];
            LABEL_KEEP = translations['global.keep'];
            LABEL_DISCARD = translations['global.discard'];
            LABEL_DELETE = translations['global.delete'];
            LABEL_REMOVE = translations['global.remove'];
            TITLE_ERROR = translations['global.error'];
            TITLE_CONFIRMATION = translations['global.confirmAction'];
            TITLE_GENERIC = translations['global.bidOpt'];
            DESC_CONF_ACCEPT = translations['confirm.overrideValues'];
            DESC_DISCARD = translations['confirm.leaveScreen'];
            TITLE_DISCARD = translations['confirm.discardChanges'];
            ACTION_SUCCESS = translations['info.operationCompleted'];
            ACTION_FAILURE = translations['global.errors.failure'];
            DONT_SHOW_AGAIN = translations['global.dontShowAgain'];
        });

        // Public API here
        return {
            DIALOG: CONSTANTS.DIALOG,
            DISMISS_TYPE: {
                WARNING: 'warning',
                INFO: 'info',
                SUCCESS: 'success',
                ERROR: 'error'
            },

            showDismissableMessage: function (type, message, title) {
                if (type === null) {
                    type = this.DISMISS_TYPE.ERROR;
                }

                if (message === null) {
                    message = '';
                }

                if (title === null) {
                    title = '';
                }

                switch (type) {
                    case this.DISMISS_TYPE.WARNING:
                        growl.warning(message, {
                            title: title
                        });
                        break;
                    case this.DISMISS_TYPE.INFO:
                        growl.info(message, {
                            title: title
                        });
                        break;
                    case this.DISMISS_TYPE.SUCCESS:
                        growl.success(message, {
                            title: title
                        });
                        break;
                    case this.DISMISS_TYPE.ERROR:
                        growl.error(message, {
                            title: title
                        });
                        break;
                    default:
                        growl.error(message, {
                            title: title
                        });
                }
                return;
            },

            showDialog: function (type, options, callbacks) {
                if (type === null) {
                    type = CONSTANTS.DIALOG.SPECIFIC_TYPE.CONFIRM_CHANGES;
                }

                var config;

                switch (type) {
                case CONSTANTS.DIALOG.SPECIFIC_TYPE.CONFIRM_CHANGES:
                    config = {
                        type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                        title: TITLE_CONFIRMATION,
                        description: DESC_CONF_ACCEPT,
                        buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL
                    };
                    break;
                case CONSTANTS.DIALOG.SPECIFIC_TYPE.DISCARD_CHANGES:
                    config = {
                        type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                        title: TITLE_DISCARD,
                        description: DESC_DISCARD,
                        buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
                    };
                    break;
                case CONSTANTS.DIALOG.TYPE.ERROR:
                    config = {
                        type: CONSTANTS.DIALOG.TYPE.ERROR,
                        //              title: TITLE_ERROR,
                        //              description: DESC_ERROR_UPDATE,
                        // FIXME: hardcoded messags for now.
                        title: 'Error',
                        description: ACTION_FAILURE,
                        copy: true
                    };
                    break;
                case CONSTANTS.DIALOG.TYPE.CUSTOM:
                    config = {
                        type: CONSTANTS.DIALOG.TYPE.CUSTOM,
                        template: options.template,
                        controller: options.controller,
                        data: options.data,
                        callbacks: callbacks
                    };
                    break;
                }

                return this.showCustomDialog(config);
            },

            showCustomDialog: function (config) {
                if (config === null || typeof config.type === 'undefined' || config.type === null) {
                    throw new Error('Cannot create a dialog with no type');
                }

                var btype = CONSTANTS.DIALOG.BUTTON_SET.OK,
                    buttons = {},
                    controller = 'DialogController',
                    data,
                    description = NOT_AVAILABLE,
                    dlg,
                    dontShowAgain = '',
                    dontShowAgainCheck = false,
                    icon = 'fa-question-circle',
                    partialHTML = '',
                    partialHTMLParams = {},
                    template = '',
                    title = TITLE_GENERIC,
                    footer;

                if (typeof config.title !== 'undefined' && config.title !== null) {
                    title = config.title;
                }

                if (typeof config.description !== 'undefined' && config.description !== null) {
                    description = config.description;
                }

                if (typeof config.buttons !== 'undefined' && config.buttons !== null) {
                    btype = config.buttons;
                }

                if (title === null) {
                    title = 'N/A';
                }

                if (typeof config.footer !== 'undefined' && config.footer !== null) {
                    footer = config.footer;
                }

                if (angular.isDefined(config.dontShowAgainID) && config.dontShowAgainID !== '') {
                    dontShowAgain = DONT_SHOW_AGAIN;

                    DONT_SHOW_AGAIN_KEY = 'teDontShowDialog_' +
                        angular.lowercase(config.dontShowAgainID) +
                        '_' + angular.lowercase($cookies.username);

                    if (storage.get(DONT_SHOW_AGAIN_KEY) === true) {
                        dontShowAgainCheck = true;
                    }
                }

                if (angular.isDefined(config.partialHTML)) {
                    description = '';
                    partialHTML = config.partialHTML;
                    partialHTMLParams = config.partialHTMLParams;
                }

                switch (config.type) {
                    // Confirm type Dialogs
                case CONSTANTS.DIALOG.TYPE.CONFIRMATION:
                    template = 'components/dialog/confirm.html';

                    if (typeof btype === 'object') {
                        buttons = btype;
                    }
                    else if (btype === CONSTANTS.DIALOG.BUTTON_SET.YES_NO) {
                        buttons = {
                            yes: LABEL_YES,
                            no: LABEL_NO
                        };
                        icon = 'fa-exclamation-triangle';
                    }
                    else if (btype === CONSTANTS.DIALOG.BUTTON_SET.DISCARD_CANCEL) {
                        buttons = {
                            yes: LABEL_DISCARD,
                            no: LABEL_CANCEL
                        };
                        icon = 'fa-exclamation-triangle';
                    }
                    else if (btype === CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE) {
                        buttons = {
                            yes: LABEL_DELETE,
                            no: LABEL_CANCEL
                        };
                        icon = 'fa-exclamation-triangle';
                    }
                    else if (btype === CONSTANTS.DIALOG.BUTTON_SET.REMOVE_NO) {
                        buttons = {
                            yes: LABEL_REMOVE,
                            no: LABEL_NO
                        };
                        icon = 'fa-exclamation-triangle';
                    }
                    else if (btype === CONSTANTS.DIALOG.BUTTON_SET.KEEP_DISCARD) {
                        buttons = {
                            yes: LABEL_DISCARD,
                            no: LABEL_KEEP
                        };
                        icon = 'fa-exclamation-triangle';
                    }
                    else {
                        // OK_CANCEL button type
                        buttons = {
                            yes: LABEL_OK,
                            no: LABEL_CANCEL
                        };
                    }

                    data = {
                        title: title,
                        description: description,
                        buttons: buttons,
                        icon: icon,
                        dontShowAgain: dontShowAgain,
                        dontShowAgainCheck: dontShowAgainCheck,
                        dontShowAgainKey: DONT_SHOW_AGAIN_KEY,
                        partialHTML: partialHTML,
                        partialHTMLParams: partialHTMLParams
                    };
                    break;
                case CONSTANTS.DIALOG.TYPE.INFORMATIONAL:
                    template = 'components/dialog/informational.html';
                    buttons = {
                        yes: LABEL_OK
                    };

                    data = {
                        title: title,
                        description: description,
                        buttons: buttons,
                        icon: icon,
                        dontShowAgain: dontShowAgain,
                        dontShowAgainCheck: dontShowAgainCheck,
                        dontShowAgainKey: DONT_SHOW_AGAIN_KEY,
                        partialHTML: partialHTML,
                        partialHTMLParams: partialHTMLParams
                    };
                    break;
                case CONSTANTS.DIALOG.TYPE.ERROR:
                    template = 'components/dialog/error.html';
                    // No need to check for btype as the default button for this dialog type \
                    // should be OK only
                    buttons = {
                        yes: LABEL_OK
                    };

                    data = {
                        title: title,
                        description: description,
                        buttons: buttons,
                        icon: icon,
                        dontShowAgain: dontShowAgain,
                        dontShowAgainCheck: dontShowAgainCheck,
                        dontShowAgainKey: DONT_SHOW_AGAIN_KEY,
                        partialHTML: partialHTML,
                        partialHTMLParams: partialHTMLParams
                    };

                    break;
                case CONSTANTS.DIALOG.TYPE.WARNING:
                    if (typeof btype === 'object') {
                        buttons = btype;
                    }
                    else if (btype === CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE) {
                        buttons = {
                            yes: LABEL_DELETE,
                            no: LABEL_CANCEL
                        };
                    }
                    else if (btype === CONSTANTS.DIALOG.BUTTON_SET.OK) {
                        buttons = {
                            yes: LABEL_OK
                        };
                    }
                    else {
                        buttons = {
                            yes: LABEL_OK,
                            no: LABEL_CANCEL
                        };
                    }

                    template = 'components/dialog/warning.html';
                    icon = 'fa-exclamation-triangle';

                    data = {
                        title: title,
                        description: description,
                        buttons: buttons,
                        icon: icon,
                        dontShowAgain: dontShowAgain,
                        dontShowAgainCheck: dontShowAgainCheck,
                        dontShowAgainKey: DONT_SHOW_AGAIN_KEY,
                        partialHTML: partialHTML,
                        partialHTMLParams: partialHTMLParams,
                        footer: footer
                    };

                    break;

                case CONSTANTS.DIALOG.TYPE.CUSTOM:
                    template = config.template;
                    controller = config.controller;
                    data = config.data;

                    break;
                }

                if (dontShowAgainCheck) {
                    return {
                        result: $q.when([])
                    };
                }
                else {
                    dlg = dialogs.create(template,
                        controller,
                        data, {
                            size: config.size || CONSTANTS.DIALOG.SIZE.MEDIUM,
                            keyboard: true,
                            backdrop: config.backdrop ? config.backdrop : true,
                            windowClass: config.windowClass ? 'my-class ' + config.windowClass : 'my-class'
                        });

                    dlg.result.then(
                        function (_data) {
                            if (config.callbacks && lodash.isFunction(config.callbacks.callbackConfirm)) {
                                config.callbacks.callbackConfirm(_data);
                            }
                        },

                        function (cancelled) {
                            if (config.callbacks && lodash.isFunction(config.callbacks.callbackCancel)) {
                                config.callbacks.callbackCancel(cancelled);
                            }
                        },

                        function (error) {
                            DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
                            if (config.callbacks && lodash.isFunction(config.callbacks.callbackError)) {
                                config.callbacks.callbackError(error);
                            }
                        });
                }

                return dlg;
            }
        };
    }
})();
