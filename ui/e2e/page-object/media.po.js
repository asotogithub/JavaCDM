'use strict';

var Media = function() {
  this.mediaGrid = element(by.id('mediaGrid'));
  this.columnNames = ["Status","check", "IO Name","IO Number","Package","Placements","Site","Size","Ad Spend","Start Date","End Date"];
  this.columnNamesPackage = ["Package Name","Placements", "Size", "Site"];
  this.dataRows = element.all(by.css('[role="row"]'));
  this.noDataToDisplay = element(by.css('tr[role="row"] td.jqx-cell.jqx-center-align[role="gridcell"]'));
  this.ioExpand = element.all(by.css('.jqx-tree-grid-collapse-button'));
  this.domainFieldReadOnly = element(by.id('domain'));
  this.ioName = element(by.id('creativeDetailsHeader'));
  this.searchClear = element(by.css('span.form-control-feedback[data-ng-click="vm.clearSearch();"]'));
  this.searchInput = element(by.model('vm.searchTerm'));
  this.sortedText = function(index){
    return  this.textToSort(index).then(function(trimmedTexts) {
       return trimmedTexts.sort(function(a, b) {
         return isNaN(a) || isNaN(b) ?
           a.toLowerCase().localeCompare(b.toLowerCase()) :
           parseFloat(a) - parseFloat(b);
       });
    });
  };
  this.textToSort = function(index){
    return  this.valuesByIndex(index).map(function(element){
      return element.getText();
    })
  };
  this.valuesByIndex = function(index) {
    var child = index + 1;
    return element.all(by.css('[role="row"] td:nth-child(' + child + ')'));
  };
  this.ioRowByName = function(ioName) {
      return element(by.cssContainingText('[role="row"] td:nth-child(3)', ioName));
  };
  this.ioNameField = element(by.id('ioName'));
  this.ioNumberField = element(by.id('ioNumber'));
  this.ioNoteField = element(by.id('ioNotes'));
  this.ioStatusField = element(by.id('ioStatusSelect'));
  this.ioCancel = element(by.css('[data-ng-click="vmAddIO.cancel()"]'));
  this.ioBulkDropDownButton = element(by.id('bulk-dropdown'));
  this.ioDownloadTemplateLink = element(by.id('downloadTemplate'));
  this.ioSave = element(by.css('[data-ng-click="vmAddIO.save()"]'));
  this.ioNameMaxLength = element(by.id('ioNameMaxLength'));
  this.ioNumberOnlyNumber = element(by.id('ioNumberOnlyNumber'));
  this.ioNumberMax = element(by.id('ioNumberMax'));
  this.ioNameRequired = element(by.id('ioNameRequired'));
  this.editIcon = element(by.css('.fa-pencil'));
  this.ioEditSave = element(by.id('saveButton'));
  this.ioEditCancel = element(by.css('[data-ng-click="vmEdit.cancel()"]'));
  this.placementAddNextBtn = element(by.css('[data-ng-click="vmAdd.activateStep(vmAdd.STEP.PROPERTIES); wizard.go(2);"]'));
  this.mediaPackageField =  element.all(by.id('mediaPackageName'));
  this.placementNameField =  element.all(by.id('placementName'));
  this.invalidPlacementName = element(by.id('invalidPlacementName'));
  this.siteAssocDropDown =  element.all(by.css('[data-ng-model="currentPlacement.site"]:first-of-type + div'));
  this.sectionDropDown =  element.all(by.css('[data-ng-model="currentPlacement.section"]:first-of-type + div'));
  this.sizeDropDown =  element.all(by.css('[data-ng-model="currentPlacement.size"]:first-of-type + div'));
  this.addPlcmntRow = element(by.css('[data-te-table-btn-click="vm.addNewPlacementDetailRow()"] i'));
  this.addPlcmntCancel = element(by.css('[data-ng-click="vmAdd.cancel()"]'));
  this.addPlcmntPrevious =  element(by.css('[data-ng-click="wizard.go(1)"]'));
  this.addPlcmntRowByName = function(placementName) {
    return element(by.xpath("//*[contains(text(),'" + placementName + "')]/.."));
  };
  this.addSiteSectionSize = element.all(by.css('button[data-ng-click="vm.openAddNew(currentPlacement, $index)"]'));
  this.addSiteSectionSizeButton = element(by.css('div.modal-content button[data-ng-click="vm.add()"]'));
  this.addSiteSectionSizeCancel = element(by.css('div.modal-content button[data-ng-click="vm.cancel()"]'));
  this.addSectionDropdown = element(by.id('section_chosen'));
  this.addSiteDropdown = element(by.id('site_chosen'));
  this.addSizeDropdown = element(by.id('size_chosen'));
  this.addSectionInput = this.addSectionDropdown.element(by.css('div.chosen-search input'));
  this.addSiteInput = this.addSiteDropdown.element(by.css('div.chosen-search input'));
  this.addedSectionPlacementName = element(by.id('placement0Name'));
  this.addedSectionInvalidPlacementName = element(by.id('invalidPlacement0Name'));
  this.getAdSpendColumn = element.all(by.css('table.placement-properties-grid input[id^=adSpendInput]'));
  this.getRateColumn = element.all(by.css('table.placement-properties-grid input[id^=rateInput]'));
  this.getRateTypeColumn = element.all(by.css('table.placement-properties-grid select[id=rateTypeSelect]'));
  this.getStartDateColumn = element.all(by.css('table.placement-properties-grid input[id$=StartDateInput]'));
  this.getEndDateColumn = element.all(by.css('table.placement-properties-grid input[id$=EndDateInput]'));
  this.getInventoryColumn = element.all(by.css('table.placement-properties-grid td[id$=Inventory] div'));
  this.applyToBtn = element(by.css('[data-ng-click="vm.applyToAll()"]'));
  this.applyToAdSpend = element(by.css('[data-ng-model="vm.headerRow.adSpend"]'));
  this.applyToRate = element(by.css('[data-ng-model="vm.headerRow.rate"]'));
  this.applyToRateType = element(by.css('[data-ng-model="vm.headerRow.rateType"]'));
  this.applyToStartDate = element(by.name('placementStartDateInput'));
  this.applyToEndDate = element(by.name('placementEndDateInput'));
  this.saveBtn = element(by.css('[data-ng-click="vmAdd.save()"]'));
  this.addPlcmntTab2 = element(by.css('[data-ng-class="{\'active\':wizard.active(2)}"]'));
  this.addPlcmntTab1 = element(by.css('[data-ng-class="{\'active\':wizard.active(1)}"]'));
  this.applyToRateTypeByValue = function(value) {
    return this.applyToRateType.element(by.cssContainingText('select option', value));
  };
  this.getPlcmntGridRowData =  function(placementName, column) {
    return element(by.xpath("//*[@id='placementName'][contains(text(),'" + placementName + "')]/../td[@data-title-text='" + column + "']"));
  };
  this.mediaPackageNameMaxLength = element(by.id('mediaPackageNameMaxLength'));
  this.placementNameMaxLength = element(by.id('placementNameMaxLength'));
  this.addPlcmntStartDate = element(by.css('[id^="placement"][id$="StartDate"]'));
  this.addPlcmntEndDate = element(by.css('[id^="placement"][id$="EndDate"]'));
  this.addPlcmntStartDateCalendar = element(by.css('input[id^="placement"][id$="StartDate"]:first-of-type + ul em.fa-calendar'));
  this.addPlcmntEndDateCalendar = this.addPlcmntEndDate.element(by.css('em.fa-calendar'));
  this.addPlcmntDateControl = element(by.css('button[id^="datepicker"]'));
  this.placementRowByName = function(placementName) {
    return element(by.cssContainingText('#placementName', placementName));
  };
  this.summaryMargin = element.all(by.css('input[id^="marginInput"]'));
  this.summaryAdSpend = element.all(by.css('input[id^="adSpendInput"]'));
  this.summaryInventory = element.all(by.css('td[id^="cost"][id$="Inventory"]'));
  this.summaryRate = element.all(by.css('input[id^="rateInput"]'));
  this.summaryRateType = element.all(by.css('select[id="rateTypeSelect"]'));
  this.summaryStartDate = element.all(by.css('input[id^="cost"][id$="StartDateInput"]'));
  this.summaryEndDate = element.all(by.css('input[id^="cost"][id$="EndDateInput"]'));
  this.summarySite = element(by.id('siteSelect'));
  this.summarySection = element(by.id('sectionSelect'));

  this.packageRowByName = function(packageName) {
    return element(by.cssContainingText('#placementMediaPackage', packageName));
  };
  this.summaryPackageName = element(by.id('name'));

  this.assocPlcmntsAddBtn = element(by.css('associated-placements .fa-plus'));
  this.addPlacements = element(by.id('addPlacements'));
  this.fromExistingPlacements = element(by.id('fromExistingPlacements'));
  this.addAssocPlcmntNext = element(by.css('[data-ng-click="vmAssoc.activateStep(vmAssoc.STEP.PACKAGE_ASSOCIATION); wizard.go(2);"]'));
  this.addAssocSite =  element(by.css('[data-ng-model="currentPlacement.site"]:first-of-type + div'));
  this.addAssocSection =  element(by.css('[data-ng-model="currentPlacement.section"]:first-of-type + div'));
  this.addAssocSize =  element(by.css('[data-ng-model="currentPlacement.size"]:first-of-type + div'));
  this.addAssocPlacementName =  element.all(by.id('placementName'));
  this.addAssocToList = element(by.css('[data-ng-click="vmPkgCreatePlac.addToAssociatedPlacements()"]'));
  this.addAssocSave = element(by.css('[data-ng-click="vmAssoc.save()"]'));
  this.getAssocPlcmntGridRowData =  function(placementName, column) {
    return element(by.xpath("//*[@data-title-text='Placement Name'][contains(text(),'" + placementName + "')]/../td[@data-title-text='" + column + "']"));
  };
  this.addAssocAddAll = element(by.css('[data-ng-click="addAllAssociation()"]'));
  this.addAssocRemoveAll = element(by.css('[data-ng-click="removeAllAssociation()"]'));
  this.addAssocAdd = element(by.css('[data-ng-click="addAssociation()"] i'));
  this.addAssocRemove = element(by.css('[data-ng-click="removeAssociation()"] i'));
  this.addAssocPlacementRowByName = function(placementName) {
    return element(by.cssContainingText('[data-title-text="Name"]', placementName));
  };
  this.addAssocInputGridRows = element.all(by.css('[data-model="inputModel"] tr.actionable-row'));
  this.addAssocOuputGridRows = element.all(by.css('[data-model="outputModel"] tr.actionable-row'));
  this.addAssocCurrentGridRows = element.all(by.css('[data-model="vmPkgAssoc.currentPackage.placements"] tr[data-ng-repeat="placement in $data"]'));
  this.addToAssociatedList = element(by.css('[data-ng-click="vmPkgAssoc.addToAssociatedPlacements()"]'));
  this.editPackageCurrentPlacementRowByName = function(placementName) {
    return element(by.cssContainingText('[data-title-text="Placement Name"]', placementName));
  };
  this.removeAssocPlcmnt =  function(placementName) {
    return element(by.xpath("//*[@data-title-text='Placement Name'][contains(text(),'" + placementName + "')]/../td[@data-title-text='Remove']/button"));
  };

    this.removeAssocPlcmntFromPackage =  element(by.css('[data-ng-click="vmEdit.removeAssociation()"]'));
    this.labelCostDetailsEmpty =  element(by.id('labelCostDetailsEmpty'));
  this.editPackageSaveBtn = element(by.css('[data-ng-click="vmEdit.save()"]'));
  this.selectAll = element(by.css('.te-table-select-column .fa-check:first-of-type'));
  this.activateBtn = element(by.id('activateSelected'));
  this.deactivateBtn = element(by.id('deactivateSelected'));
  this.deletePlacementRow = element.all(by.css('[data-ng-click="vm.removePlacementRow($index)"] i'));
  this.warningOkBtn = element(by.css('.modal-content button[data-ng-click="yes()"]'));
  this.warningCloseBtn = element(by.css('.modal-content button[data-ng-click="no()"]'));
  this.placementSearchClear = element(by.css('span.form-control-feedback[data-ng-click="$table.clearSearch()"]'));
  this.placementSearchInput = element(by.model('$table.searchTerm'));
  this.placementStatusSelect = element(by.id('placementStatusSelect'));
  this.placementSizeSelect = element(by.id('sizeSelect'));
  this.extProp1 = element(by.id('extProp1'));
  this.extProp2 = element(by.id('extProp2'));
  this.extProp3 = element(by.id('extProp3'));
  this.extProp4 = element(by.id('extProp4'));
  this.extProp5 = element(by.id('extProp5'));
  this.plusBtn = element(by.css('[class="btn btn-primary"]'));
  this.listOfPlacementBtn = element(by.id('listOfPlacementsBtn'));
  this.placementNameInput = element(by.id('placementName'));
  this.errorPopup = element(by.id('error-pop-body'));
  this.errorPopupOkBtn = element(by.css('.modal-content button[data-ng-click="no()"]'));

    //Package Summary
    this.placementAssociationsList = element.all(by.css('#associated-placement-table tbody [data-title-text="Placement Name"]'));
  //Bulk Import/Export Dropdown
  this.bulkDropdown = element(by.id('bulk-dropdown'));
  this.bulkDropdownList = element(by.id('bulk-dropdown-list'));
  this.importAll = element(by.id('importAll'));
  this.exportAll = element(by.id('exportAll'));
  this.downloadSuccessful = element(by.id('downloadSuccessful'));

  //Import
  this.cancelImportBtn = element(by.id('cancel-import'));
  this.downloadInvalidFile = element(by.id('downloadInvalidFile'));
  this.downloadInvalidFormatFile = element(by.id('downloadInvalidFormatFile'));
  this.downloadValidFile = element(by.id('downloadValidFile'));
  this.globalValidationWarningPopup = element(by.css('div.modal div.modal-dialog div.modal-content'));
  this.processImportBtn = element(by.id('process-import'));
  this.selectFileBtn = element(by.id('select-file'));
  this.upload = element(by.css('input[type="file"]'));
  this.uploadFile = function (config, filePath, fileName) {
    if (config.seleniumAddress === config.env.browserstack.seleniumAddress) {
      this.upload.sendKeys(config.env.browserstack.downloadDirectory + fileName);
    }
    else {
      this.upload.sendKeys(filePath);
    }
  };

  //Import Errors
  this.cancelImportErrorsBtn = element(by.id('cancel-import-errors'));
  this.completeImportBtn = element(by.id('complete-import'));
  this.exportIssuesBtn = element(by.id('export-issues'));
  this.exportIssuesSuccessful = element(by.id('exportIssuesSuccessful'));
  this.errorRows = element.all(by.css('#errorsGrid tbody tr'));
  this.errorsTab = element(by.id('errorsTab'));
  this.warningRows = element.all(by.css('#warningsGrid tbody tr'));
  this.warningsTab = element(by.id('warningsTab'));
  this.rowsIssues = element(by.id('rowsIssues'));
  this.actionsTab = element(by.id('actionsTab'));
  this.actionsRows = element.all(by.css('#actionsGrid tbody tr'));
  this.actionSelect = element.all(by.id('actionOptions'));
  this.getOptionByIndex = function (selector, optionNumber) {
    return selector.element(by.css('option:nth-child(' + optionNumber + ')'));
  };
  this.getChecked = function (selector) {
    return selector.element(by.css('option:checked'));
  };

  //Selection by checkbox & legend
  this.legendMedia = element(by.id('legend'));
  this.mediaGrid = element(by.id('mediaGrid'));
  this.searchInputByGrid = function(parentEl){
      return parentEl.element(by.model('vm.searchTerm'));
  }
  this.searchInputMedia = this.searchInputByGrid(this.mediaGrid);

  // invalid Placement-Site-Section Name
    this.invalidNames = ['|helloo','©helloo','ühelloo','>helloo¾',')hellooÁ','`helloo',')hellooÐ'];
    this.invalidSiteName = element(by.id('invalidSiteName'));
    this.invalidSectionName = element(by.id('invalidSectionName'));

    // Confirmation Dialog
    this.confirmationDialog = element(by.css('.modal-content'));
    this.confirmationCancelButton = element(by.css('.modal-content button[data-ng-click="no()"]'));

  //Package List
    this.packageList = {
        grid: element(by.id('packageGrid')),
        ioExpand: element.all(by.css('.jqx-tree-grid-collapse-button'))
    }
};

module.exports = new Media();
