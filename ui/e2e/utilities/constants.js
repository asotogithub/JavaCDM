'use strict';

var Constants = function() {
    this.defaultGroupName = 'Default';
    this.defaultWaitInterval = 5000;
    this.CAMPAIGN = {
        NAME: {
            MAX_LENGTH: 128
        },
        STATUS: {
            ACTIVE: 'Active',
            INACTIVE: 'Inactive'
        }
    };
    this.CREATIVE_GROUPS = {
        GEO: {
            NUMBER_OF_COUNTRIES: 257
        }
    };
    this.MEDIA = {
        IMPORT_TEMPLATE_NAME: 'Media_Import_Template.xlsx',
        INVALID_IMPORT_NAME: 'invalid-media-import.xlsx',
        INVALID_IMPORT_FORMAT_NAME: 'invalid-media-import-format.xlsx',
        VALID_IMPORT_NAME: 'valid-media-import.xlsx'
    };
    this.SCHEDULE_PIVOT = {
        SITE : 'Site',
        PLACEMENT : 'Placement',
        GROUP : 'Group',
        CREATIVE : 'Creative'
    };
    this.SCHEDULE_SEARCH_OPTION = {
        SITE : 'Site',
        SECTION : 'Section',
        PLACEMENT : 'Placement',
        GROUP : 'Creative Group',
        CREATIVE : 'Creative'
    };
    this.SEND_AD_TAGS_FORMAT = {
        HTML: 'HTML',
        PDF: 'PDF',
        SELECT: 'Select',
        TXT: 'TXT (Standard)',
        XLSX: 'Excel'
    };
    this.AD_TAGS_FILTERS = {
        CAMPAIGN: 0,
        SITE: 1,
        TRAFFICKED: 2
    };
    this.AD_TAGS_FILTERS_OPCION_INDEX = {
        TRAFFICKED: {
            PENDING: 0,
            TRAFFICKED: 1
        }
    };
    this.SM_EVENTS_PINGS_FILTERS = {
        EVENT_TYPE: 0,
        GROUP: 1,
        TAG_TYPE: 2
    };
    this.SM_EVENTS_PING_CARD = {
        SELECTIVE: 'Selective',
        BROADCAST: 'Broadcast'
    };
    this.SM_EVENTS_PING_SEARCH_OPTIONS = {
        TAG_TYPE: 'Tag Type',
        SITE: 'Site',
        PING: 'Ping'
    };
    this.SITE_MEASUREMENT_EVENT_TYPE = {
        SELECT: 'Select',
        STANDARD: 'Standard',
        TRU_TAG: 'TruTag'
    };
    this.SITE_MEASUREMENT_EVENT_TAG_TYPE = {
        SELECT: 'Select',
        CONVERSION: 'Conversion',
        CONVERSION_REVENUE: 'Conversion w/Revenue',
        MEASURED: 'Measured',
        OTHER: 'Other'
    };
    this.SM_CAMPAIGN_ASSOCIATIONS_FILTERS = {
        STATUS_UNASSOCIATED: 0,
        STATUS_ASSOCIATED: 1
    };
};

module.exports = new Constants();
