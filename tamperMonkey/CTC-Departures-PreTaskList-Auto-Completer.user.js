// ==UserScript==
// @name         CTC-Departures PreTaskList Auto Completer
// @namespace    http://tampermonkey.net/
// @version      5.2
// @description  Script to automatically fill out CTC sections
// @author       Reece-Carruthers
// @match        http*://*/manage-transit-movements/what-do-you-want-to-do
// @match        http*://*/manage-transit-movements/departures/local-reference-number
// @match        http*://*/manage-transit-movements/departures/*/pre-task-list/*
// @match        http*://*/manage-transit-movements/departures/*/declaration-summary
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_setValue
// @grant        GM_getValue
// @updateURL https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-PreTaskList-Auto-Completer.user.js
// ==/UserScript==

(function () {
    'use strict';
})();

window.addEventListener('load', function () {
    generateLRN()
    isAButtonToggled()
}, false);

/* Main Functions */

function isAButtonToggled() {
    if (GM_getValue('preTaskT1NoSecurityToggle', false)) {
        preTaskListT1NoSecurity()
    } else {
        if (!location.href.includes('declaration-summary')) {
            document.body.appendChild(setupGUI())
        }
    }
}

function toggleButtonsOff() {
    GM_setValue('preTaskT1NoSecurityToggle', false)
}

function setupGUI() {
    const panel = document.createElement('div')
    panel.appendChild(createPreTaskT1NoSecurityButton())
    return panel
}

/* Helper Functions */

function generateLRN() {
    if (location.href.includes('manage-transit-movements/departures/local-reference-number')) {
        GM_setValue('lrn', Math.floor((Math.random() + 1) * 10000))
    }
}

function getLRN() {
    return GM_getValue('lrn', null)
}

const currentPageIs = (path) => {
    if (path.includes("*")) {
        let matches = window.location.pathname.match(path)
        return matches && window.location.pathname.endsWith(path.slice(-5))
    } else {
        return path === window.location.pathname
    }
}

/* Buttons */

function createPreTaskT1NoSecurityButton() {
    let button = document.createElement('button')
    button.id = 'preTaskT1NoSecurityButton'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button', 'govuk-!-display-none-print')
    }

    button.style.position = 'absolute'
    button.style.top = '50px'
    button.innerHTML = 'Complete Pretask List (T1/No Security)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('preTaskT1NoSecurityToggle', true)
        preTaskListT1NoSecurity()
    })

    return button
}

/* #### PreTaskList Pages #### */

const departureDeclarationPage = () => {
    if (currentPageIs('/manage-transit-movements/what-do-you-want-to-do')) {
        document.getElementById('make-departure-declaration').click()
        document.getElementById('submit').click()
    }
}

const lrnPage = (lrn) => {
    if (currentPageIs('/manage-transit-movements/departures/local-reference-number')) {
        document.getElementById('value').value = lrn
        document.getElementById('submit').click()
    }
}

const standardPrelodgedDeclarationPage = (lrn, data) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/standard-prelodged-declaration`)) {
        document.getElementById(data).click()
        document.getElementById('submit').click()
    }
}

const additionalDeclarationTypePage = (lrn, data) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/standard-prelodged-declaration`)) {
        document.getElementById(data).click()
        document.getElementById('submit').click()
    }
}

const officeOfDeparturePage = (lrn, data) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/office-of-departure`)) {
        document.getElementById('value-select').value = data
        document.getElementById('submit').click()
    }
}

const procedureTypePage = (lrn, data) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/procedure-type`)) {
        document.getElementById(data).click()
        document.getElementById('submit').click()
    }
}

const declarationTypePage = (lrn, data) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/declaration-type`)) {
        document.getElementById(data).click()
        document.getElementById('submit').click()
    }
}

const securityDetails = (lrn, data) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/security-details`)) {
        document.getElementById(data).click()
        document.getElementById('submit').click()
    }
}

const preTaskListCYA = (lrn) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/check-answers`)) {
        document.getElementById('submit').click()
    }
}

const taskListPage = (lrn) => {
    if (currentPageIs(`/manage-transit-movements/departures/${lrn}/declaration-summary`)) {
        toggleButtonsOff()
    }
}
/* #### Journeys #### */

/* ## Pre-task list journey ## */

function preTaskListT1NoSecurity() {
    departureDeclarationPage()
    lrnPage(getLRN())
    standardPrelodgedDeclarationPage(getLRN(), 'value')
    additionalDeclarationTypePage(getLRN(), 'value')
    officeOfDeparturePage(getLRN(), 'GB000011')
    procedureTypePage(getLRN(), 'value')
    declarationTypePage(getLRN(), 'value')
    securityDetails(getLRN(), 'value')
    preTaskListCYA(getLRN())
    taskListPage(getLRN())
}


