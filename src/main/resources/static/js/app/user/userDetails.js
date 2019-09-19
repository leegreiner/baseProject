var CONTEXT = (function() {
  var my = {}, userId;
  
  my.setUserId = function(id) {
    userId = id;
  }
  
  my.getUserId = function() {
    return userId;
  }
  
  return my;
}());

var targetPageSetUp = function() {
  'use strict';
  
  hotkeys(BP_GLOBALS.getHistoryHotKeys(), function(event, handler) {
    window.location = "/users/" + CONTEXT.getUserId() + "/history";
  });
}