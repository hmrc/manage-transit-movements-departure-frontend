// ==UserScript==
// @name         CTC-Departures Section Auto Completer
// @namespace    http://tampermonkey.net/
// @version      1.0
// @description  Script to automatically fill out CTC sections
// @author       Reece-Carruthers
// @match        http*://*/manage-transit-movements/departures/*/task-list
// @match        http*://*/manage-transit-movements/departures/*/route-details/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_setValue
// @grant        GM_getValue
// @updateURL https://raw.githubusercontent.com/hmrc/???
// ==/UserScript==

(function() {
    'use strict';
})();

window.addEventListener('load', function() {
    var routeDetailsButtonPressed = GM_getValue('routeDetailsButtonPressed', false)

    if(location.href.includes('task-list')){
        const lrn = location.href.split('/')[5]
        GM_setValue('lrn',lrn)
    }
    if(routeDetailsButtonPressed){
        routeDetails()
    }else {
        document.body.appendChild(setup())
    }

}, false);


function setup() {
    var panel = document.createElement('div')
    panel.appendChild(createRouteDetailsButton())
    return panel
}

function createRouteDetailsButton() {
    let button = document.createElement('button')
    button.id='routeDetails'
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.position = 'absolute'
    button.style.top = '50px'
    button.innerHTML = 'Complete Route Details (Authorised Place)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('routeDetailsButtonPressed',true)
        routeDetails()
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

/* #### RouteDetails Pages #### */

const startRouteDetails = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        location.href = `/manage-transit-movements/departures/${lrn}/route-details/routing/country-of-destination`
    }
}

const countryOfRoutingPage = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/country-of-destination`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const countryOfDestinationPage = (lrn, data) => {
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
        GM_setValue('routeDetailsButtonPressed',false)
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* #### Journeys #### */

/* ## Route Details journey ## */

function routeDetails() {
    let lrn = GM_getValue('lrn',null)
    startRouteDetails(lrn)
    countryOfRoutingPage(lrn, 'IT')
    countryOfDestinationPage(lrn, 'IT034105')
    bindingItinerary(lrn)
    transitRouteAddCountry(lrn)
    transitRouteCountry(lrn, 'DE')
    transitRouteAddAnother(lrn)
    routingCYA(lrn)
    officeOfTransitCountry(lrn,'DE')
    officeOfTransit(lrn,'DE004058')
    officeOfTransitETA(lrn)
    officeOfTransitLoopCYA(lrn)
    officeOfTransitAddAnother(lrn)
    locationOfGoodsType(lrn)
    locationOfGoodsIdentification(lrn)
    locationOfGoodsEORI(lrn)
    locationOfGoodsAddAnotherIdentifier(lrn)
    locationOfGoodsAddContact(lrn)
    locationOfGoodsCYA(lrn)
    placeOfLoadingUNLOCODE(lrn)
    placeOfLoadingCountry(lrn, 'AR')
    placeOfLoadingLocation(lrn)
    loadingCYA(lrn)
    routeDetailsCYA(lrn)
}


