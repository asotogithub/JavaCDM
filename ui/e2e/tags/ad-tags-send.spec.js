'use strict';

var adTagsSend = (function () {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/tags.po'),
        nav = require('../page-object/navigation.po'),
        users = require('../utilities/users.spec'),
        emails = {
            newMail: 'ken.masters@capcom.com.jp',
            invalidEmails: ['Dh@lsim@capcom.com', '@don@capcom.com', '@kuma.com',
                            'ryu..@capcom.com', '.Evil.Ryu@capcom.com']
        },
        filters = {
            advertiser: 'Trafficked Advertiser',
            brand: 'trafficked Brand'
        },
        user = users.auto01;

    describe('Tags - Placement Grid', function(){
        it('should filter specific advertiser and brand', function() {
            nav.tagsItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tags');
            page.selectDropdown(page.advertiserDropdown, filters.advertiser).click();
            page.selectDropdown(page.brandDropdown, filters.brand).click();
            expect(page.dataRows.count()).toBeGreaterThan(0);
        });

        it('should enable send button',function() {
            page.filterDropdown(CONSTANTS.AD_TAGS_FILTERS.TRAFFICKED).click();
            page.filterDropdownOption(CONSTANTS.AD_TAGS_FILTERS.TRAFFICKED,
                CONSTANTS.AD_TAGS_FILTERS_OPCION_INDEX.TRAFFICKED.PENDING).click();
            expect(page.dataRows.count()).toBeGreaterThan(0);
            page.dataRows.get(0).click();
            expect(page.sendBtn.isEnabled()).toBe(true);
        });
    });

    describe('Tags - Send Ad Tags', function(){
        it('should display Send Ad Tags modal',function() {
            page.sendBtn.click();
            expect(page.adTagsModal.isDisplayed()).toBe(true);
        });

        it('should add an email tag by selecting a row from recipients grid',function() {
            expect(page.adTagsModalRecipientsDataRows.count()).toBeGreaterThan(0);
            page.adTagsModalRecipientsDataRows.get(0).click();
            expect(page.adTagsModalEmailList.count()).toEqual(1);
            expect(page.adTagsModalRecipientsCheckedDataRows.count()).toEqual(1);
        });

        it('should remove an email tag when unchecks a row from recipients grid',function() {
            expect(page.adTagsModalRecipientsDataRows.count()).toBeGreaterThan(0);
            page.adTagsModalRecipientsDataRows.get(0).click();
            expect(page.adTagsModalEmailList.count()).toEqual(0);
            expect(page.adTagsModalRecipientsCheckedDataRows.count()).toEqual(0);
        });

        it('should manually add and email tag',function() {
            page.adTagsModalEmailListInput.sendKeys(emails.newMail);
            page.adTagsModalEmailListInput.sendKeys(protractor.Key.ENTER);
            expect(page.adTagsModalEmailList.count()).toEqual(1);
            expect(page.adTagsModalRecipientsCheckedDataRows.count()).toEqual(0);
        });

        it('should remove email tag by pressing close button',function() {
            page.adTagsModalEmailListClose.get(0).click();
            expect(page.adTagsModalEmailList.count()).toEqual(0);
        });

        it('should manually add and email tag that user is available in recipients grid',function() {
            page.adTagsModalEmailListInput.sendKeys(user.email);
            page.adTagsModalEmailListInput.sendKeys(protractor.Key.ENTER);
            expect(page.adTagsModalEmailList.count()).toEqual(1);
            expect(page.adTagsModalRecipientsCheckedDataRows.count()).toEqual(1);
        });

        it('should remove manually added email from recipients grid',function() {
            page.getAddTagsModalDataRowByEmail(user.email).click();
            expect(page.adTagsModalEmailList.count()).toEqual(0);
            expect(page.adTagsModalRecipientsCheckedDataRows.count()).toEqual(0);
        });

        it('should not allow to manually add invalid emails',function() {
            for (var i =0; i < emails.invalidEmails.length; i++) {
                page.adTagsModalEmailListInput.sendKeys(emails.invalidEmails[i]);
                page.adTagsModalEmailListInput.sendKeys(protractor.Key.ENTER);
                expect(page.adTagsModalEmailList.count()).toEqual(0);
                page.adTagsModalEmailListInput.clear();
            }
        });

        it('should clear all added email',function() {
            expect(page.adTagsModalRecipientsDataRows.count()).toBeGreaterThan(0);
            page.adTagsModalRecipientsDataRows.get(0).click();
            expect(page.adTagsModalEmailList.count()).toEqual(1);
            expect(page.adTagsModalRecipientsCheckedDataRows.count()).toEqual(1);
            page.adTagsModalEmailListInput.sendKeys(user.email);
            page.adTagsModalEmailListInput.sendKeys(protractor.Key.ENTER);
            expect(page.adTagsModalEmailList.count()).toEqual(2);
            page.adTagsModalClearAllLink.click();
            expect(page.adTagsModalEmailList.count()).toEqual(0);
            expect(page.adTagsModalRecipientsCheckedDataRows.count()).toEqual(0);
        });

        it('should load formats into dropdown',function() {
            page.selectDropdown(page.adTagsModalFormatInput, CONSTANTS.SEND_AD_TAGS_FORMAT.TXT).click();
            page.selectDropdown(page.adTagsModalFormatInput, CONSTANTS.SEND_AD_TAGS_FORMAT.XLSX).click();
            page.selectDropdown(page.adTagsModalFormatInput, CONSTANTS.SEND_AD_TAGS_FORMAT.HTML).click();
            page.selectDropdown(page.adTagsModalFormatInput, CONSTANTS.SEND_AD_TAGS_FORMAT.PDF).click();
            page.adTagsModalFormatInput.getText().then(function (text) {
                var options = text.split(/\r\n|\r|\n/g).join('');

                expect(options).toEqual(CONSTANTS.SEND_AD_TAGS_FORMAT.SELECT +
                    CONSTANTS.SEND_AD_TAGS_FORMAT.TXT +
                    CONSTANTS.SEND_AD_TAGS_FORMAT.XLSX +
                    CONSTANTS.SEND_AD_TAGS_FORMAT.HTML +
                    CONSTANTS.SEND_AD_TAGS_FORMAT.PDF);
            });
        });

        it('should enable send button',function() {
            expect(page.adTagsModalRecipientsDataRows.count()).toBeGreaterThan(0);
            page.adTagsModalRecipientsDataRows.get(0).click();
            expect(page.adTagsModalEmailList.count()).toEqual(1);
            page.selectDropdown(page.adTagsModalFormatInput, CONSTANTS.SEND_AD_TAGS_FORMAT.HTML).click();
            expect(page.adTagsModalSendButton.isEnabled()).toBe(true);
            page.adTagsModalRecipientsDataRows.get(0).click();
            expect(page.adTagsModalEmailList.count()).toEqual(0);
            expect(page.adTagsModalSendButton.isEnabled()).toBe(false);
            page.adTagsModalIndividualRecipientsInput.sendKeys(emails.newMail);
            page.adTagsModalIndividualRecipientsInput.sendKeys(protractor.Key.ENTER);
            expect(page.adTagsModalIndividualRecipientsList.count()).toEqual(1);
            expect(page.adTagsModalSendButton.isEnabled()).toBe(true);
            page.adTagsModalRecipientsDataRows.get(0).click();
            expect(page.adTagsModalEmailList.count()).toEqual(1);
            expect(page.adTagsModalSendButton.isEnabled()).toBe(true);
        });

        it('should close modal when press close button',function() {
            var EC = protractor.ExpectedConditions;

            page.adTagsModalCloseButton.click();
            browser.wait(EC.not(EC.presenceOf(page.adTagsModal)), CONSTANTS.defaultWaitInterval);
            expect(page.adTagsModal.isPresent()).toBe(false);
        });
    });

    describe('Tags - Details', function(){

        it('should enable edit button', function() {
            expect(page.editBtn.isEnabled()).toBe(true);
            page.dataRows.get(0).click();
            expect(page.sendBtn.isEnabled()).toBe(false);
            page.dataRows.get(0).click();
            expect(page.sendBtn.isEnabled()).toBe(true);
        });

        it('should open Ad Tag details', function() {
            expect(page.editBtn.isEnabled()).toBe(true);
            page.editBtn.click();
            expect(page.adTagsDetails.isPresent()).toBe(true);
            expect(page.adTagsDetailsSendBtn.isEnabled()).toBe(true);
        });

        it('should close Ad Tag details', function() {
            expect(page.adTagsDetails.isPresent()).toBe(true);
            page.adTagsDetailsCloseBtn.click();
            expect(page.adTagsDetails.isPresent()).toBe(false);
        });
    });

});

module.exports = adTagsSend;
