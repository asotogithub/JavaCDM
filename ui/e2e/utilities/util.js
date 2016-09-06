'use strict';

var Util = function () {

  this.click = function(element) {
      browser.actions().mouseMove(element).perform();
      element.click();
  }

};

module.exports = new Util;


