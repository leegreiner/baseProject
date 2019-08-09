'use strict';

var classHolder = document.getElementsByTagName("BODY")[0],
  themeSettings = (localStorage.getItem('themeSettings')) ? JSON.parse(localStorage.getItem('themeSettings')) : {},
    themeURL = themeSettings.themeURL || '',
    themeOptions = themeSettings.themeOptions || '';

if (themeSettings.themeOptions) {
  classHolder.className = themeSettings.themeOptions;
}

if (themeSettings.themeURL && !document.getElementById('mytheme')) {
  var cssfile = document.createElement('link');
  cssfile.id = 'mytheme';
  cssfile.rel = 'stylesheet';
  cssfile.href = themeURL;
  document.getElementsByTagName('head')[0].appendChild(cssfile);
}

var saveSettings = function() {
  themeSettings.themeOptions = String(classHolder.className).split(/[^\w-]+/).filter(function(item) {
    return /^(nav|header|mod|display)-/i.test(item);
  }).join(' ');
  
  if (document.getElementById('mytheme')) {
    themeSettings.themeURL = document.getElementById('mytheme').getAttribute("href");
  };
  
  localStorage.setItem('themeSettings', JSON.stringify(themeSettings));
}

var resetSettings = function() {
    localStorage.setItem("themeSettings", "");
}