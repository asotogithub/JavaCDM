'use strict';

var LogToFile = function () {
    this.getLogger = function(name) {
        var log4js = require('log4js'),
            logger;

        log4js.configure({
            appenders: [
                { type: 'console' },
                { type: 'file', filename: global.logFiles.protractorLogFilename, category: name }
            ]
        });

        logger = log4js.getLogger(name);
        logger.setLevel('ALL');

        return logger;
    };
};

module.exports = new LogToFile();


