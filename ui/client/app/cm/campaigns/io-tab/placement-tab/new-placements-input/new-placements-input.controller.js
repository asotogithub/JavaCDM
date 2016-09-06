(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('NewPlacementsInputController', NewPlacementsInputController);

    NewPlacementsInputController.$inject = [
        '$filter',
        '$translate',
        '$q',
        'CONSTANTS',
        'SectionService',
        'SiteService',
        'SizeService',
        'dialogs',
        'lodash'
    ];

    function NewPlacementsInputController(
        $filter,
        $translate,
        $q,
        CONSTANTS,
        SectionService,
        SiteService,
        SizeService,
        dialogs,
        lodash) {
        var vm = this,
            sitesPromise = null,
            sizesPromise = null;

        vm.REGEX_PLACEMENT_NAME = CONSTANTS.REGEX.PLACEMENT_SECTION_SITE_NAME;
        vm.addNewPlacementDetailRow = addNewPlacementDetailRow;
        vm.blurButton = blurButton;
        vm.detailsForm = {};
        vm.getSectionList = getSectionList;
        vm.mediaPackageMaxLength = CONSTANTS.INPUT.MAX_LENGTH.MEDIA_PACKAGE;
        vm.nameMaxLength = CONSTANTS.INPUT.MAX_LENGTH.PLACEMENT_NAME;
        vm.openAddNew = openAddNew;
        vm.placementCharacters = $translate.instant('placement.addPlacementNameTooltip', {
            characters: vm.nameMaxLength
        });
        vm.removePlacementRow = removePlacementRow;
        vm.selectSite = selectSite;
        vm.selectSectionBySite = selectSectionBySite;
        vm.selectSizes = selectSizes;
        vm.validateAllRows = validateAllRows;

        activate();

        function activate() {
            sitesPromise = SiteService.getList();
            sizesPromise = SizeService.getList();
            vm.promise = $q.all([sitesPromise, sizesPromise]).then(function (promises) {
                vm.siteList = $filter('orderBy')(promises[0], 'name');
                vm.sizeList = $filter('orderBy')(promises[1], 'label');
            });

            addNewPlacementDetailRow();
        }

        function blurButton(buttonName) {
            angular.element('#' + buttonName).trigger('blur');
        }

        function getSectionList(idSite, selectedPlacement) {
            selectedPlacement.promiseSection = SectionService.getList(idSite);
            selectedPlacement.promiseSection.then(function (result) {
                selectedPlacement.sectionList = $filter('orderBy')(result, 'name');
            });
        }

        function addNewPlacementDetailRow() {
            var newPlacement = {
                packageName: '',
                name: '',
                site: {},
                section: [],
                size: []
            };

            if (vm.model) {
                vm.model.push(newPlacement);
                validateAllRows();
            }
        }

        function openAddNew(selectedPlacement, index) {
            var template = 'app/cm/campaigns/io-tab/placement-tab/placement-add/properties-add/properties-add.html',
                dlg = dialogs.create(template,
                    'PropertiesAddController as vm',
                    {
                        site: selectedPlacement.site,
                        section: selectedPlacement.section,
                        size: selectedPlacement.size,
                        index: index
                    },
                    {
                        size: 'xl',
                        keyboard: true,
                        key: false,
                        backdrop: 'static'
                    });

            dlg.result.then(
                function (data) {
                    vm.isLoadSize = false;
                    if (angular.isDefined(data.site) && data.site !== null &&
                        angular.isDefined(data.site.id) && data.site.id !== null) {
                        loadSite(selectedPlacement, data.site.id, data, true, index);
                    }
                    else if (angular.isDefined(data.section) && data.section !== null &&
                        angular.isDefined(data.section.id) && data.section.id !== null) {
                        loadSite(selectedPlacement, data.section.siteId, data, false, index);
                    }
                    else if (angular.isDefined(data.size) && data.size !== null &&
                        angular.isDefined(data.size.id) && data.size.id !== null) {
                        loadSize(data, index, selectedPlacement);
                    }
                });

            return dlg;
        }

        function loadSite(selectedPlacement, siteId, data, isSiteNew, index) {
            var sitePromise = SiteService.getList(),
                idsSectionSelected = [];

            if (angular.isDefined(data.site) && data.site !== null &&
                angular.isDefined(data.site.id) && data.site.id !== null) {
                angular.forEach(selectedPlacement.section, function (value) {
                    idsSectionSelected.push(value.id);
                });
            }

            vm.promise = $q.all([sitePromise]).then(function (promises) {
                vm.siteList = $filter('orderBy')(promises[0], 'name');

                selectedPlacement.site = lodash.find(vm.siteList, function (site) {
                    return site.id === siteId;
                });

                if (isSiteNew) {
                    loadSiteSection(selectedPlacement, data, idsSectionSelected);
                }
                else {
                    loadSection(selectedPlacement, data, index);
                }

                if (angular.isDefined(data.size) && data.size !== null) {
                    if (angular.isDefined(data.size.id) && data.size.id !== null) {
                        vm.isLoadSize = true;
                        loadSize(data, index);
                    }
                }
                else if (angular.isDefined(data.model.size.id) && data.model.size.id !== null) {
                    loadSize(data.model, index, null);
                }

                angular.forEach(vm.model, function (placement) {
                    if (placement.site && placement.site.id) {
                        placement.site = lodash.find(vm.siteList, function (site) {
                            return site.id === placement.site.id;
                        });
                    }
                });
            });
        }

        function loadSection(selectedPlacement, data, index) {
            var idsSectionSelected = [],
                sections = [];

            angular.forEach(selectedPlacement.section, function (value) {
                idsSectionSelected.push(value.id);
            });

            selectedPlacement.promiseSection = SectionService.getList(data.section.siteId);
            selectedPlacement.promiseSection.then(function (result) {
                if (idsSectionSelected.indexOf(data.section.id) === -1) {
                    idsSectionSelected.push(data.section.id);
                }

                selectedPlacement.section = [];
                angular.forEach(idsSectionSelected, function (value) {
                    sections.push(lodash.find(result, function (section) {
                        return section.id === value;
                    }));
                });

                angular.forEach(sections, function (value) {
                    if (!angular.isUndefined(value) && value !== null) {
                        selectedPlacement.section.push(value);
                    }
                });

                if (selectedPlacement.section.length > 1) {
                    selectedPlacement.name = '';
                }

                angular.forEach(vm.model, function (placement, key) {
                    if (placement.site.id === selectedPlacement.site.id && key !== index) {
                        var ids = [];

                        angular.forEach(placement.section, function (value) {
                            ids.push(value.id);
                        });

                        angular.forEach(ids, function (value) {
                            placement.section.push(lodash.find(result, function (section) {
                                return section.id === value;
                            }));
                        });

                        if (placement.section.length > 1) {
                            placement.name = '';
                        }

                        placement.sectionList = $filter('orderBy')(result, 'name');
                    }
                });

                selectedPlacement.sectionList = $filter('orderBy')(result, 'name');
                validateAllRows();
            });
        }

        function loadSiteSection(selectedPlacement, data, idsSectionSelected) {
            var sections = [];

            if (angular.isDefined(data.section) && data.section !== null &&
                angular.isDefined(data.section.id) && data.section.id !== null) {
                if (idsSectionSelected.indexOf(data.section.id) === -1) {
                    idsSectionSelected.push(data.section.id);
                }

                selectedPlacement.promiseSection = SectionService.getList(data.site.id);
                selectedPlacement.promiseSection.then(function (result) {
                    selectedPlacement.section = [];

                    angular.forEach(idsSectionSelected, function (value) {
                        sections.push(lodash.find(result, function (section) {
                            return section.id === value;
                        }));
                    });

                    angular.forEach(sections, function (value) {
                        if (!angular.isUndefined(value) && value !== null) {
                            selectedPlacement.section.push(value);
                        }
                    });

                    if (selectedPlacement.section.length > 1) {
                        selectedPlacement.name = '';
                    }

                    selectedPlacement.sectionList = $filter('orderBy')(result, 'name');
                    validateAllRows();
                });
            }
            else if (idsSectionSelected.length > 0) {
                selectedPlacement.promiseSection = SectionService.getList(data.site.id);
                selectedPlacement.promiseSection.then(function (result) {
                    selectedPlacement.section = [];

                    angular.forEach(idsSectionSelected, function (value) {
                        sections.push(lodash.find(result, function (section) {
                            return section.id === value;
                        }));
                    });

                    angular.forEach(sections, function (value) {
                        if (!angular.isUndefined(value) && value !== null) {
                            selectedPlacement.section.push(value);
                        }
                    });

                    if (selectedPlacement.section.length > 1) {
                        selectedPlacement.name = '';
                    }

                    selectedPlacement.sectionList = $filter('orderBy')(result, 'name');
                    validateAllRows();
                });
            }
            else {
                selectedPlacement.promiseSection = SectionService.getList(data.site.id);
                selectedPlacement.promiseSection.then(function (result) {
                    selectedPlacement.sectionList = $filter('orderBy')(result, 'name');
                });

                selectedPlacement.section = [];
            }

            validateAllRows();
        }

        function loadSize(data, index, selectedPlacement) {
            var sizePromise = SizeService.getList();

            vm.promise = $q.all([sizePromise]).then(function (promises) {
                var listIdsSelected = [];

                angular.forEach(vm.model, function (value, i) {
                    var ids = [] ;

                    angular.forEach(value.size, function (size) {
                        ids.push(size.id);
                    });

                    listIdsSelected[i] = ids;
                });

                vm.sizeList = $filter('orderBy')(promises[0], 'label');

                angular.forEach(vm.model, function (placement, key) {
                    var ids = listIdsSelected[key];

                    if (index === key) {
                        if (selectedPlacement !== null && !vm.isLoadSize &&
                            angular.isDefined(data.model.site.id) && data.model.site.id !== null) {
                            loadSite(selectedPlacement, data.model.site.id, data.model, true, index);
                        }

                        if (angular.isDefined(data.size.id) && data.size.id !== null &&
                            ids.indexOf(data.size.id) === -1) {
                            ids.push(data.size.id);
                        }
                    }

                    placement.size = [];
                    angular.forEach(ids, function (id) {
                        placement.size.push($filter('filter')(vm.sizeList ,
                            {
                                id: id
                            },
                            true)[0]);
                    });

                    if (placement.size.length > 1) {
                        placement.name = '';
                    }
                });

                validateAllRows();
            });
        }

        function selectSectionBySite(selectedPlacement) {
            if (selectedPlacement.section.length > 1) {
                selectedPlacement.name = '';
            }

            validateAllRows();
        }

        function selectSite(selectedSite, selectedPlacement) {
            if (selectedSite.id) {
                getSectionList(selectedSite.id, selectedPlacement);
                selectedPlacement.section = [];
                validateAllRows();
            }
        }

        function selectSizes(selectedSizes, selectedPlacement) {
            selectedPlacement.size = selectedSizes;
            if (selectedPlacement.size.length > 1) {
                selectedPlacement.name = '';
            }

            validateAllRows();
        }

        /**
         * Is valid when at least one row is filled.
         * @returns {boolean}
         */
        function validateAllRows() {
            var isValid = false,
                i = 0;

            while (i < vm.model.length && !isValid) {
                isValid = isValid || validateSinglePlacement(vm.model[i++]);
            }

            vm.model.isValid = isValid;
        }

        function validateSinglePlacement(placement) {
            if (!angular.isDefined(placement.site) || !angular.isDefined(placement.site.id)) {
                return false;
            }

            if (!angular.isDefined(placement.section) || placement.section.length === 0) {
                return false;
            }

            if (!angular.isDefined(placement.size) || placement.size.length === 0) {
                return false;
            }

            if (!angular.isDefined(placement.name) || placement.name.length > vm.nameMaxLength) {//Name can be empty
                return false;
            }

            if (!angular.isDefined(placement.packageName) ||
                placement.packageName.length > vm.mediaPackageMaxLength) {//Media Package can be empty
                return false;
            }

            return true;
        }

        function removePlacementRow($index) {
            vm.model.splice($index, 1);
            validateAllRows();
        }
    }
})();
