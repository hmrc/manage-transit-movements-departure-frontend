// ==UserScript==
// @name         CTC-Departures Auth Auto Completer
// @namespace    http://tampermonkey.net/
// @version      1.0
// @description  Script to automatically fill out auth details
// @author       Reece-Carruthers
// @match        http*://*/auth-login-stub/gg-sign-in?continue=*manage-transit-movements*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        none
// @updateURL https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-Auth-Auto-Completer.user.js
// ==/UserScript==

(function() {
    'use strict';

    document.getElementsByName("redirectionUrl")[0].value = getBaseUrl() + "/manage-transit-movements";

    document.getElementById("affinityGroupSelect").selectedIndex = 1;

    document.getElementsByName("enrolment[0].name")[0].value = "HMRC-CTC-ORG";
    document.getElementById("input-0-0-name").value = "EoriNumber";
    document.getElementById("input-0-0-value").value = Math.floor((Math.random() * 10000) + 1000);

    document.querySelector('header').appendChild(createQuickAuthButton())

})();

function createQuickAuthButton() {
    let button = document.createElement('button');
    button.id='quickAuth'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.position = 'absolute'
    button.style.top = '50px'
    button.innerHTML = 'Quick Auth'
    button.onclick = () => document.getElementById('submit').click();
    return button;
}

function getBaseUrl() {
    let host = window.location.host;
    if (window.location.hostname === 'localhost') {
        host = 'localhost:9485'
    }
    return window.location.protocol + "//" + host;
}