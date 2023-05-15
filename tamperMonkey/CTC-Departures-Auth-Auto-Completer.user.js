// ==UserScript==
// @name         CTC-Departures Auth Auto Completer
// @namespace    http://tampermonkey.net/
// @version      2.1
// @description  Script to automatically fill out auth details
// @author       Reece-Carruthers
// @match        http*://*/auth-login-stub/gg-sign-in*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_addStyle
// @updateURL https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-Auth-Auto-Completer.user.js
// ==/UserScript==

(function() {
    'use strict';

    document.getElementsByName("redirectionUrl")[0].value = getBaseUrl() + "/manage-transit-movements";

    document.getElementById("affinityGroupSelect").selectedIndex = 1;

    document.getElementsByName("enrolment[0].name")[0].value = "HMRC-CTC-ORG";
    document.getElementById("input-0-0-name").value = "EoriNumber";
    document.getElementById("input-0-0-value").value = Math.floor((Math.random() * 10000) + 1000);

    const panel = document.createElement('div')
    GM_addStyle(` .guiStyle { position: absolute; top: 50px; display: grid; grid-template-rows: repeat(2, 1fr);`)
    panel.classList.add('guiStyle')
    panel.appendChild(createRandomAuthButton())
    panel.appendChild(create1234567AuthButton())

    document.querySelector('header').appendChild(panel)

})();

function createRandomAuthButton() {
    let button = document.createElement('button');
    button.id='randomAuth'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '4px'
    button.innerHTML = 'Quick Auth (Random EORI)'

    button.addEventListener("click", function handleClick() {
        document.getElementById('submit').click();
    })
    return button;
}

function create1234567AuthButton() {
    let button = document.createElement('button');
    button.id='quick1234567Auth'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '4px'
    button.innerHTML = 'Quick Auth (1234567 EORI)'

    button.addEventListener("click", function handleClick() {
        document.getElementById("input-0-0-value").value = "1234567";
        document.getElementById('submit').click();
    })
    return button;
}

function getBaseUrl() {
    let host = window.location.host;
    if (window.location.hostname === 'localhost') {
        host = 'localhost:9485'
    }
    return window.location.protocol + "//" + host;
}