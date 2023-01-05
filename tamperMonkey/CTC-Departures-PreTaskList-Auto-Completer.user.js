// ==UserScript==
// @name         CTC-Departures PreTaskList Auto Completer
// @namespace    http://tampermonkey.net/
// @version      2.0
// @description  Script to automatically fill out CTC sections
// @author       Reece-Carruthers
// @match        http*://*/manage-transit-movements/what-do-you-want-to-do
// @match        http*://*/manage-transit-movements/departures/local-reference-number
// @match        http*://*/manage-transit-movements/departures/*/pre-task-list/*
// @match        http*://*/manage-transit-movements/departures/*/task-list
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_setValue
// @grant        GM_getValue
// @updateURL https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-PreTaskList-Auto-Completer.user.js
// ==/UserScript==

(function() {
    'use strict';
})();

window.addEventListener('load', function() {
    var preTaskT1NoSecurityButtonPressed = GM_getValue('preTaskT1NoSecurityButtonPressed',false)

    // Generate random numbers for LRN if LRN page is hit
    if(location.href.includes('manage-transit-movements/departures/local-reference-number')){
        GM_setValue('lrn', Math.floor((Math.random() + 1) * 10000))
    }
    if(preTaskT1NoSecurityButtonPressed){
        preTaskListT1NoSecurity()
    }else {
        if(!location.href.includes('task-list')) {
            document.body.appendChild(setup())
        }
    }
}, false);

function setup() {
    var panel = document.createElement('div')
    panel.appendChild(createPreTaskT1NoSecurityButton())
    return panel
}

function createPreTaskT1NoSecurityButton() {
    let button = document.createElement('button')
            button.id='preTaskT1NoSecurityButton'

            if (!!document.getElementById('global-header')) {
                button.classList.add('button-start', 'govuk-!-display-none-print')
            } else {
                button.classList.add('govuk-button','govuk-!-display-none-print')
            }

            button.style.position = 'absolute'
            button.style.top = '50px'
            button.innerHTML = 'Complete Pretask List (T1/No Security)'
            button.addEventListener("click", function handleClick() {
                GM_setValue('preTaskT1NoSecurityButtonPressed',true)
                preTaskListT1NoSecurity()
            })

            return button
}

const currentPageIs = (path) => {
    if(path.includes("*")) {
        let matches = window.location.pathname.match(path)
        return matches && window.location.pathname.endsWith(path.slice(-5))
    } else {
        return path === window.location.pathname
    }
}

/* #### PreTaskList Pages #### */

const departureDeclarationPage = () => {
    if(currentPageIs('/manage-transit-movements/what-do-you-want-to-do')){
        document.getElementById('make-departure-declaration').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const lrnPage = (lrn) => {
    if(currentPageIs('/manage-transit-movements/departures/local-reference-number')){
        document.getElementById('value').value = lrn
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfDeparturePage = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/office-of-departure`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const procedureTypePage = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/procedure-type`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const declarationTypePage = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/declaration-type`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const securityDetails = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/security-details`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const preTaskListCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/pre-task-list/check-answers`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* Sets button press to false on the landing page to prevent script running once you've went through the journey */
const taskListPage = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        GM_setValue('preTaskT1NoSecurityButtonPressed', false)
    }
}
/* #### Journeys #### */

/* ## Pre task list journey ## */

function preTaskListT1NoSecurity() {
    departureDeclarationPage()
    var lrn = GM_getValue('lrn', null)
    lrnPage(lrn)
    officeOfDeparturePage(lrn, 'GB000068')
    procedureTypePage(lrn, 'value')
    declarationTypePage(lrn, 'value')
    securityDetails(lrn, 'value')
    preTaskListCYA(lrn)
    taskListPage(lrn)
}


