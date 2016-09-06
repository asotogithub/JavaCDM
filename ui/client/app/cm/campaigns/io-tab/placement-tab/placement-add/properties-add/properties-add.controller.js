(function () {
    'use strict';

    angular.module('uiApp')
        .controller('PropertiesAddController', PropertiesAddController);

    PropertiesAddController.$inject = [
        '$filter',
        '$modalInstance',
        '$q',
        '$scope',
        '$timeout',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'DialogFactory',
        'ErrorRequestHandler',
        'PublisherService',
        'SectionService',
        'SiteService',
        'SizeService',
        'data',
        'lodash'
    ];

    function PropertiesAddController(
        $filter,
        $modalInstance,
        $q,
        $scope,
        $timeout,
        $translate,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        ErrorRequestHandler,
        PublisherService,
        SectionService,
        SiteService,
        SizeService,
        data,
        lodash) {
        var vm = this,
            onShowingDropdownPublisher = null,
            onShowingDropdownSite = null,
            onShowingDropdownSection = null,
            onShowingDropdownSize = null,
            OPTION = {
                PUBLISHER: {
                    KEY: 'publisher'
                },
                SITE: {
                    KEY: 'site'
                },
                SECTION: {
                    KEY: 'section'
                },
                SIZE: {
                    KEY: 'size'
                }
            },
            REGEX_NAME_PUBLISHER = new RegExp('^[\\w\\-\\\'\\,\\\/\\&\\!\\.\\(\\) ]+$'),
            REGEX_NAME =
                new RegExp('^[\\w\\.~\\/?%&=\\\'\\!\\+\\-\\<\\>\\(\\)\\*\\$\\#\\@\\:\\;\\^\\{\\}\\,\\[\\] ]+$'),
            REGEX_SIZE_NAME = new RegExp(/^[1-9][0-9]{0,3}x[1-9][0-9]{0,3}$/), // Max: 9999x9999
            TIMEOUT_RESOLVE_PROMISE = 250;

        vm.PUBLISHER_MAX_LENGTH = CONSTANTS.PLACEMENT.PROPERTIES.PUBLISHER_INPUT_MAX_LENGTH;
        vm.SECTION_MAX_LENGTH = CONSTANTS.PLACEMENT.PROPERTIES.SECTION_INPUT_MAX_LENGTH;
        vm.SITE_MAX_LENGTH = CONSTANTS.PLACEMENT.PROPERTIES.SITE_INPUT_MAX_LENGTH;
        vm.SELECT_OPTION = $translate.instant('global.selectAnOption');
        vm.MAX_ALL_SIZE = '9999x9999';
        vm.add = add;
        vm.cancel = cancel;
        vm.init = init;
        vm.model = {
            publisher: {
                $dirty: false
            },
            site: {
                $dirty: false
            },
            section: {
                $dirty: false
            },
            size: {
                $dirty: false
            }
        };
        vm.propertiesAddForm = {};
        vm.promise = null;
        vm.promises = {
            publisher: null,
            site: null,
            size: null
        };
        vm.publisherList = [];
        vm.sectionList = [];
        vm.selectedPublisher = {};
        vm.selectedSection = {};
        vm.selectedSite = {};
        vm.selectedSize = {};
        vm.selectPublisher = selectPublisher;
        vm.selectSection = selectSection;
        vm.selectSite = selectSite;
        vm.selectSize = selectSize;
        vm.siteList = [];
        vm.siteListAll = [];

        activate();

        function activate() {
            initializeModel();
            onShowingDropdownPublisher = onShowingDropdown(OPTION.PUBLISHER);
            onShowingDropdownSite = onShowingDropdown(OPTION.SITE);
            onShowingDropdownSection = onShowingDropdown(OPTION.SECTION);
            onShowingDropdownSize = onShowingDropdown(OPTION.SIZE);
        }

        function add() {
            var isValidSite = vm.model.publisher.id && vm.model.site.id && vm.model.section.id,
                isValidSection = vm.model.publisher.id && vm.model.site.id && !vm.model.section.name,
                payload;

            $timeout(function () {
                if ((!vm.model.publisher.name || !vm.model.site.name) && !vm.model.size.label && !vm.model.size.id) {
                    return;
                }
                // Close the modal with existing model if there are no new names.
                if ((isValidSite || isValidSection) && !vm.model.size.label) {
                    closeModal(vm.model);
                    return;
                }

                payload = createPayLoad();
                if (Object.keys(payload).length === 0) {
                    closeModal(vm.model);
                    return;
                }

                var errorPublisherExist = 'Publisher name already exists',
                    errorSiteDuplicate = 'The site name you are adding exists under different publisher',
                    errorSectionExists = 'Site Section name already exists',
                    errorSizeExists = ' already exists',
                    errorInvalidCharacters = 'Invalid characters',
                    errorServer;

                vm.promise = CampaignsService.bulkSaveSiteSectionSize(payload).then(function (response) {
                    closeModal(response);
                }).catch(function (error) {
                    errorServer = lodash.get(error, 'data.errors[0].message');
                    if (errorServer.indexOf(errorPublisherExist) > -1) {
                        showMessageErrorExist(errorServer);
                    }
                    else if (errorServer.indexOf(errorSiteDuplicate) > -1) {
                        showMessageErrorDuplicate(errorServer, payload);
                    }
                    else if (errorServer.indexOf(errorSectionExists) > -1) {
                        showMessageErrorExist(errorServer);
                    }
                    else if (errorServer.indexOf(errorSizeExists) > -1) {
                        showMessageErrorExist(errorServer);
                    }
                    else if (errorServer.indexOf(errorInvalidCharacters) > -1) {
                        showMessageErrorExist(errorServer);
                    }
                    else {
                        ErrorRequestHandler.handle('Cannot bulk save site, section, size', error, function () {
                            $modalInstance.dismiss();
                        });
                    }
                });
            }, TIMEOUT_RESOLVE_PROMISE);
        }

        function addListenersAndBinding() {
            var chosenPublisher = angular.element('#' + OPTION.PUBLISHER.KEY),
                chosenSite = angular.element('#' + OPTION.SITE.KEY),
                chosenSection = angular.element('#' + OPTION.SECTION.KEY),
                chosenSize = angular.element('#' + OPTION.SIZE.KEY);

            // Publisher
            chosenPublisher.off('chosen:showing_dropdown').on('chosen:showing_dropdown', onShowingDropdownPublisher);

            // Site
            chosenSite.off('chosen:showing_dropdown').on('chosen:showing_dropdown', onShowingDropdownSite);

            // Section
            chosenSection.off('chosen:showing_dropdown').on('chosen:showing_dropdown', onShowingDropdownSection);

            // Size
            chosenSize.off('chosen:showing_dropdown').on('chosen:showing_dropdown', onShowingDropdownSize);
        }

        function cancel() {
            closeModal();
        }

        function clearSelection(option, placeholder) {
            if (option === OPTION.PUBLISHER) {
                vm.selectedPublisher = null;
            }
            else if (option === OPTION.SITE) {
                vm.selectedSite = null;
            }
            else if (option === OPTION.SECTION) {
                vm.selectedSection = null;
            }
            else if (option === OPTION.SIZE) {
                vm.selectedSize = null;
            }

            if (option.KEY === 'size') {
                vm.model[option.KEY].label = null;
            }
            else {
                vm.model[option.KEY].name = null;
            }

            vm.model[option.KEY].id = null;
            placeholder.text(vm.SELECT_OPTION);
        }

        function closeModal(placementData) {
            if (placementData) {
                placementData.index = data.index;
                placementData.model = vm.model;
                $modalInstance.close(placementData);
            }
            else {
                $modalInstance.dismiss();
            }
        }

        function createPayLoad() {
            var payload = {},
                publisher = {},
                section = {},
                site = {},
                size = {},
                sizeArray,
                sizeLowercaseLabel;

            //New publisher
            if (vm.model.publisher.id) {
                site.publisherId = vm.model.publisher.id;
            }
            else if (vm.model.publisher.name) {
                publisher.name = vm.model.publisher.name;
            }

            //New Site
            if (vm.model.site.id) {
                section.siteId = vm.model.site.id;
            }
            else if (vm.model.site.name) {
                site.name = vm.model.site.name;
            }

            //New Section
            if (vm.model.section.name && !vm.model.section.id) {
                section.name = vm.model.section.name;
            }

            //New Size
            if (vm.model.size.label && !vm.model.size.id) {
                sizeLowercaseLabel = size.label = vm.model.size.label;
                angular.lowercase(sizeLowercaseLabel);
                sizeArray = sizeLowercaseLabel.split('x');

                if (sizeArray.length === 2) {
                    size.width = sizeArray[0];
                    size.height = sizeArray[1];
                }
            }

            //Add model to send Post
            if (publisher.name) {
                payload.publisher = publisher;
            }

            if (site.name) {
                payload.site = site;
            }

            if (section.name) {
                payload.section = section;
            }

            if (size.label) {
                payload.size = size;
            }

            return payload;
        }

        function find(collection, field, value) {
            return lodash.find(collection, function (o) {
                return angular.lowercase(o[field]) === angular.lowercase(value);
            });
        }

        function formatMessageDuplicate(error) {
            return error.substring(error.indexOf('[') + 1, error.length - 1);
        }

        function getPlaceholderElement(option) {
            return angular.element('#' + option.KEY).siblings('.chosen-container').first()
                .find('.chosen-single').find('span');
        }

        function init() {
            addListenersAndBinding();
        }

        function initializeModel() {
            var publisherPromise = PublisherService.getList(),
                sitePromise = SiteService.getList(),
                sizePromise = SizeService.getList();

            vm.promise = $q.all([publisherPromise, sitePromise, sizePromise]).then(function (promises) {
                vm.publisherList = $filter('orderBy')(promises[0], 'name');
                vm.siteListAll = $filter('orderBy')(promises[1], 'name');
                vm.sizeList = $filter('orderBy')(promises[2], 'label');

                if (data.site && data.site.id && data.site.publisherId) {
                    vm.selectedPublisher = lodash.find(vm.publisherList, function (publisher) {
                        return publisher.id === data.site.publisherId;
                    });

                    selectPublisher(vm.selectedPublisher);

                    vm.selectedSite = lodash.find(vm.siteList, function (site) {
                        return site.id === data.site.id;
                    });

                    selectSite(vm.selectedSite, true);
                }

                if (data.size && data.size.length === 1 && data.size[0] && data.size[0].id) {
                    vm.selectedSize = $filter('filter')(vm.sizeList, {
                        id: data.size[0].id
                    })[0];

                    vm.model.size.id = data.size[0].id;
                    vm.model.size.label = data.size[0].label;
                }

                // Need to watch changes into the model.
                $scope.$watch('vm.model.publisher.name', watchPublisher, true);
                $scope.$watch('vm.model.site.name', watchSite, true);
                $scope.$watch('vm.model.section', watchSection, true);
                $scope.$watch('vm.model.size', watchSize, true);
            });
        }

        function onShowingDropdown(option) {
            return function () {
                var input = angular.element(this).siblings('.chosen-container').first()
                        .find('.chosen-search').find('input'),
                    placeholder = getPlaceholderElement(option);

                input.off('keypress').on('keypress', function () {
                    $scope.$apply(function () {
                        clearSelection(option, placeholder);
                    });
                });

                input.off('change').on('change', function () {
                    $scope.$apply(function () {
                        setNewValueByOption(input.val().trim(), placeholder, option);
                    });
                });
            };
        }

        function selectPublisher(publisher) {
            if (!publisher) {
                return;
            }

            setPublisher(publisher);
        }

        function selectSection(section) {
            vm.model.section.name = section.name;
            vm.model.section.id = section.id;
        }

        function selectSite(site, isInit) {
            if (!site) {
                return;
            }

            if (isInit) {
                setSite(site, isInit);
            }
            else {
                setSite(site);
            }
        }

        function selectSize(size) {
            vm.model.size.label = size.label;
            vm.model.size.id = size.id;
        }

        function setPublisher(publisher) {
            vm.model.publisher.id = publisher.id;
            vm.model.publisher.name = publisher.name;

            // Set the siteList according current publisher
            var siteList = $filter('filter')(vm.siteListAll, {
                publisherId: publisher.id
            });

            vm.siteList = siteList && siteList.length > 0 ? siteList : [{}];

            vm.selectedSite = null;
            vm.model.site.id = null;
            vm.model.site.name = null;
            vm.model.site.$dirty = false;
            vm.model.site.$invalid = false;
            vm.model.site.$maxlength = false;
            vm.model.section.id = null;
            vm.model.section.name = null;
            vm.model.section.$dirty = false;
            vm.model.section.$invalid = false;
            vm.model.section.$maxlength = false;
            vm.sectionList = [];
        }

        function setNewValueByOption(text, placeholder, option) {
            var existing = null;

            placeholder.text(text);
            vm.model[option.KEY].id = null;
            vm.model[option.KEY].name = text;

            if (option.KEY === 'size') {
                vm.model[option.KEY].label = text;
            }

            if (option === OPTION.PUBLISHER) {
                existing = find(vm.publisherList, 'name', text);
                if (existing) {
                    setPublisher(existing);
                }
            }
            else if (option === OPTION.SITE) {
                existing = find(vm.siteList, 'name', text);
                if (existing) {
                    setSite(existing);
                }
            }
            else if (option === OPTION.SECTION) {
                existing = find(vm.sectionList, 'name', text);
                if (existing) {
                    vm.selectSection(existing);
                }
            }
            else if (option === OPTION.SIZE) {
                existing = find(vm.sizeList, 'label', text);
                if (existing) {
                    vm.selectSize(existing);
                }
            }
        }

        function setSite(site, isInit) {
            vm.model.site.id = site.id;
            vm.model.site.name = site.name;

            if (isInit) {
                populateSection(site, isInit);
            }
            else {
                populateSection(site);
            }

            vm.model.section.$dirty = false;
            vm.model.section.$invalid = false;
            vm.model.section.$maxlength = false;
        }

        function populateSection(site, isInit) {
            vm.sectionPromise = SectionService.getList(site.id).then(function (result) {
                vm.sectionList = $filter('orderBy')(result, 'name');
                if (vm.sectionList.length === 0) {
                    vm.sectionList = [
                        {}
                    ];
                    vm.model.section.id = null;
                    vm.model.section.name = null;
                }

                if (isInit && data.section.length === 1 && data.section[0] && data.section[0].id) {
                    vm.selectedSection = lodash.find(vm.sectionList, function (section) {
                        return section.id === data.section[0].id;
                    });

                    vm.model.section.id = data.section[0].id;
                    vm.model.section.name = data.section[0].name;
                }
            });
        }

        function showMessageErrorExist(message) {
            DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.ERROR, message);
        }

        function showMessageErrorDuplicate(error, postData) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('placement.createSite.duplicate', {
                        lisPublisher: formatMessageDuplicate(error) + '.<br>'
                    }
                ),
                buttons: {
                    yes: $translate.instant('global.createDuplicate'),
                    no: $translate.instant('global.cancel')
                }
            }).result.then(
                function () {
                    //Force Save Duplicate
                    vm.promise = CampaignsService.bulkSaveSiteSectionSize(postData , {
                        ignoreDupSite: true
                    }).then(function (response) {
                        closeModal(response);
                    }).catch(function (catchedError) {
                        showMessageErrorExist(catchedError.data.errors[0].message);
                    });
                });
        }

        function watchPublisher(newValue, oldValue) {
            if (angular.equals(newValue, oldValue)) {
                return;
            }

            vm.model.publisher.$dirty = true;
            if (newValue !== null) {
                vm.model.publisher.$invalid = !REGEX_NAME_PUBLISHER.test(newValue);
                vm.model.publisher.$maxlength = newValue && newValue.length > vm.PUBLISHER_MAX_LENGTH;
            }

            if (!vm.model.publisher.id) {
                vm.siteList = [
                    {}
                ];

                vm.model.site.id = null;
                vm.model.site.name = null;
                vm.model.site.$dirty = false;
                vm.model.site.$invalid = false;
                vm.model.site.$maxlength = false;
                vm.model.section.id = null;
                vm.model.section.name = null;
                vm.model.section.$dirty = false;
                vm.model.section.$invalid = false;
                vm.model.section.$maxlength = false;
            }
        }

        function watchSection(newValue, oldValue) {
            if (angular.equals(newValue, oldValue)) {
                return;
            }

            vm.model.section.$dirty = true;
            if (newValue.name !== null) {
                vm.model.section.$invalid = !REGEX_NAME.test(newValue.name);
                vm.model.section.$maxlength = newValue.name && newValue.name.length > vm.SECTION_MAX_LENGTH;
            }
        }

        function watchSite(newValue, oldValue) {
            if (angular.equals(newValue, oldValue)) {
                return;
            }

            vm.model.site.$dirty = true;
            if (vm.model.site.name) {
                vm.sectionList = [
                    {}
                ];
            }

            vm.model.section.id = null;
            vm.model.section.name = null;
            vm.model.section.$dirty = false;
            vm.model.section.$invalid = false;
            vm.model.section.$maxlength = false;

            vm.model.site.$dirty = true;
            vm.model.site.$invalid = !REGEX_NAME.test(newValue);
            vm.model.site.$maxlength = newValue && newValue.length > vm.SITE_MAX_LENGTH;
        }

        function watchSize(newValue, oldValue) {
            if (angular.equals(newValue, oldValue)) {
                return;
            }

            vm.model.size.$dirty = true;
            vm.model.size.$invalid = !REGEX_SIZE_NAME.test(newValue.label);
        }
    }
})();
