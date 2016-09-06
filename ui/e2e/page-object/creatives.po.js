'use strict';

var creatives = function() {
  this.clickThroughTab = element(by.id('clickThroughURLs'));
  this.creativeCounter = element(by.css('#creativeCounter #legend'));
  this.upload = element(by.css('input[type="file"]'));
  this.newCreativeTable = element(by.id('newCreativeTable'));
  this.versionedCreativeTable = element(by.id('versionedCreativeTable'));
  this.uploadCreativeRowsByTable = function (tableElement) {
    return tableElement.all(by.repeater('creative in $data'));
  };
  this.aliasInputByTable = function (tableElement, index) {
    return tableElement.all(by.css('td[data-title="\'global.alias\' | translate"] input.form-control')).get(index);
  };
  this.errorMessageByTable = function (tableElement) {
    return tableElement.element(by.css('.has-error p.help-block'));
  };
  this.informationDiv = element(by.css('div.information-div'));
  this.nextBtn = element(by.css('[data-ng-click="vmAdd.activateStep(vmAdd.STEP.ASSIGN); wizard.go(2)"]'));
  this.checkboxByGroupName =  function(creativeGroupName) {
    return element(by.xpath("//*[@data-title-text='Name'][contains(text(),'" + creativeGroupName + "')]/../td/div/label/span"));
  };
  this.saveBtn = element(by.css('[data-ng-click="vmAdd.save()"]'));
  this.editCreativeBtn = element(by.css('[data-te-table-btn-click="vm.editCreative($selection)"]'));
  this.backToCreativeList = element(by.css('[data-ng-click="vmDetails.backToList()"]'));
  this.creativeGridEditButton = element(by.css('te-table-btns .btn-default'));
  this.getCreativeGridRowData =  function(creative, column) {
    return element(by.xpath("//*[@data-title-text='Filename'][contains(text(),'" + creative + "')]/../td[@data-title-text='" + column + "']"));
  };

  this.creativesRow = element.all(by.repeater('creative in $data'));
  // details tab
  this.videoPreview = element(by.id('videoPlayerPreview'));

  this.dataRows = element.all(by.css('tr[data-ng-repeat$="$data"]'));
  this.downloadGifFile = element(by.id('downloadGifFile'));
  this.downloadHtml5File = element(by.id('downloadHtml5File'));
  this.downloadTxtFile = element(by.id('downloadTxtFile'));
  this.downloadEmptyTxtFile = element(by.id('downloadEmptyTxtFile'));
  this.downloadThirdPartyFile = element(by.id('downloadThirdPartyFile'));
  this.downloadVideoVastFile = element(by.id('downloadVideoVastFile'));
  this.downloadVideoVmapFile = element(by.id('downloadVideoVmapFile'));
  this.paginationControl = element(by.css('.pagination.ng-table-pagination'));
  this.searchInput = element(by.model('$table.searchTerm'));

    this.uploadFile = function (config, filePath, fileName) {
        if (config.seleniumAddress === config.env.browserstack.seleniumAddress) {
            this.upload.sendKeys(config.env.browserstack.downloadDirectory + fileName);
        }
        else {
            this.upload.sendKeys(filePath);
        }
    };

    this.hyperlinkDownloadById = function (name) {
        return element(by.id(name));
    };
};

module.exports = new creatives();
