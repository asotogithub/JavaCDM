//Returns array of test files, according to params
exports.divideTests = function (start, end) {

    var allFiles = getFiles(__dirname + '/client'),
        groupFiles = [];
    for (var i = start - 1; i < end; i++) {
        if (allFiles[i]) {
            groupFiles.push(allFiles[i]);
        }

    }

    return groupFiles;
};

//Returns total number of test files
exports.countTests = function () {

    var allFiles = getFiles(__dirname + '/client'),
        fs = require('fs');
    return allFiles.length;
};

//Recursive function to search all the test files
function getFiles(dir, files_) {

    var fs = require('fs'),
        files_ = files_ || [],
        files = fs.readdirSync(dir),
        excludedList = require('./excluded.karma.conf.js');

    for (var i in files) {
        var name = dir + '/' + files[i],
            excluded = false;
        if (fs.statSync(name).isDirectory()) {
            getFiles(name, files_);
        } else {
            if (files[i].indexOf('spec.') > -1) {
                for (var e in excludedList) {
                    if (name.indexOf(excludedList[e].split('*')[0]) > -1 ) {
                        excluded = true;
                    }
                }

                if(!excluded) {
                    files_.push(name);
                }
            }

        }

    }
    return files_;
}