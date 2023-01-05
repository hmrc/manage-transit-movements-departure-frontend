// ==UserScript==
// @name         CTC-Departures Section Auto Completer
// @namespace    http://tampermonkey.net/
// @version      2.0
// @description  Script to automatically fill out CTC sections
// @author       Reece-Carruthers
// @match        http*://*/manage-transit-movements/departures/*/task-list
// @match        http*://*/manage-transit-movements/departures/*/route-details/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_setValue
// @grant        GM_getValue
// @updateURL    https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-Section-Auto-Completer.user.js
// ==/UserScript==

(function() {
    'use strict';
})();

window.addEventListener('load', function() {
    saveLRN()
    isAButtonToggled()
    isSectionCompleted()
}, false);

/* Main Functions */

function isAButtonToggled() {
    if(GM_getValue('routeDetailsAuthorisedToggle',false)){
        routeDetailsAuthorised()
    }else {
        document.body.appendChild(setupGUI())
    }
}

function toggleButtonsOff() {
    GM_setValue('routeDetailsAuthorisedToggle',false)
}

function setupGUI() {
    const panel = document.createElement('div');
    panel.appendChild(createRouteDetailsAuthorisedButton())
    return panel
}

/* Helper Functions */


function isSectionCompleted() {
    if (onLandingPage()) {
        if (document.getElementById('route-details-status').innerText === 'COMPLETED') {
            document.getElementById('routeDetailsAuthorised').remove()
        }
    }
}

function saveLRN() {
    if(onLandingPage()){
        GM_setValue('lrn',location.href.split('/')[5])
    }
}

function onLandingPage() {
    return location.href.includes('task-list')
}

function getLRN() {
    return GM_getValue('lrn', null)
}

const currentPageIs = (path) => {
    if(path.includes("*")) {
        let matches = window.location.pathname.match(path)
        return matches && window.location.pathname.endsWith(path.slice(-5))
    } else {
        return path === window.location.pathname
    }
}

/* Buttons */

function createRouteDetailsAuthorisedButton() {
    let button = document.createElement('button')
    button.id='routeDetailsAuthorised'
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.position = 'absolute'
    button.style.top = '50px'
    button.innerHTML = 'Complete Route Details (Authorised Place)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('routeDetailsAuthorisedToggle',true)
        routeDetailsAuthorised()
    })

    return button
}

/* #### Route Details Pages #### */

const startRouteDetails = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        location.href = `/manage-transit-movements/departures/${lrn}/route-details/routing/country-of-destination`
    }
}

const countryOfDestination = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/country-of-destination`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfDestination = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/office-of-destination`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const bindingItinerary = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/binding-itinerary`)){
        document.getElementById('value-no').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteAddCountry = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/transit-route-add-country`)){
        document.getElementById('value').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/1/transit-route-country`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteAddAnother = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/transit-route-add-another-country`)){
        document.getElementById('value-no').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const routingCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/check-answers`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/1/office-of-transit-country`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransit = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/1/office-of-transit`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitETA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/1/office-of-transit-add-eta`)){
        document.getElementById('value-no').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitLoopCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/1/office-of-transit-check-answers`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitAddAnother = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/add-another-office-of-transit`)){
        document.getElementById('value-no').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsType = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-type`)){
        document.getElementById('value').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsIdentification = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-identification`)){
        document.getElementById('value_1').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsEORI = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/eori-tin`)){
        document.getElementById('value').value = '1234'
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsAddAnotherIdentifier = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-add-identifier`)){
        document.getElementById('value-no').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsAddContact = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-add-contact`)){
        document.getElementById('value-no').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-check-answers`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingUNLOCODE = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/loading/place-of-loading-add-un-locode`)){
        document.getElementById('value-no').click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/loading/place-of-loading-country`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingLocation = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/loading/place-of-loading-location`)){
        document.getElementById('value').value = '1234'
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const loadingCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/loading-and-unloading/check-answers`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const routeDetailsCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/check-answers`)){
        toggleButtonsOff()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* #### Journeys #### */

/* ## Route Details Authorised Place journey ## */

function routeDetailsAuthorised() {
    startRouteDetails(getLRN())
    countryOfDestination(getLRN(), 'IT')
    officeOfDestination(getLRN(), 'IT034105')
    bindingItinerary(getLRN())
    transitRouteAddCountry(getLRN())
    transitRouteCountry(getLRN(), 'DE')
    transitRouteAddAnother(getLRN())
    routingCYA(getLRN())
    officeOfTransitCountry(getLRN(),'DE')
    officeOfTransit(getLRN(),'DE004058')
    officeOfTransitETA(getLRN())
    officeOfTransitLoopCYA(getLRN())
    officeOfTransitAddAnother(getLRN())
    locationOfGoodsType(getLRN())
    locationOfGoodsIdentification(getLRN())
    locationOfGoodsEORI(getLRN())
    locationOfGoodsAddAnotherIdentifier(getLRN())
    locationOfGoodsAddContact(getLRN())
    locationOfGoodsCYA(getLRN())
    placeOfLoadingUNLOCODE(getLRN())
    placeOfLoadingCountry(getLRN(), 'AR')
    placeOfLoadingLocation(getLRN())
    loadingCYA(getLRN())
    routeDetailsCYA(getLRN())
}


