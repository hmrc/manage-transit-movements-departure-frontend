// ==UserScript==
// @name         CTC-Departures Section Auto Completer
// @namespace    http://tampermonkey.net/
// @version      3.0
// @description  Script to automatically fill out CTC sections
// @author       Reece-Carruthers
// @match        http*://*/manage-transit-movements/departures/*/task-list
// @match        http*://*/manage-transit-movements/departures/*/route-details/*
// @match        http*://*/manage-transit-movements/departures/*/trader-details/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_setValue
// @grant        GM_getValue
// @grant        GM_addStyle
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
    if(GM_getValue('traderDetailsReducedDataSetToggle',false)){
        traderDetailsReducedDataSet()
    }
    else if(GM_getValue('routeDetailsAuthorisedToggle',false)){
        routeDetailsAuthorised()
    }else {
        document.body.appendChild(setupGUI())
    }
}

function toggleButtonsOff() {
    GM_setValue('traderDetailsReducedDataSetToggle',false)
    GM_setValue('routeDetailsAuthorisedToggle',false)
}

function setupGUI() {
    var panel = document.createElement('div')
    GM_addStyle(' .guiStyle { position: absolute; top: 50px; display: grid; grid-template-rows: repeat(2, 1fr);')
    panel.classList.add('guiStyle')
    panel.appendChild(createTraderDetailsButton())
    panel.appendChild(createRouteDetailsAuthorisedButton())
    return panel
}

/* Helper Functions */


function isSectionCompleted() {
    if (onLandingPage()) {
        if (document.getElementById('route-details-status').innerText === 'COMPLETED') {
            document.getElementById('routeDetailsAuthorised').remove()
        }
        if (document.getElementById('trader-details-status').innerText === 'COMPLETED') {
            document.getElementById('traderDetailsReducedDataSet').remove()
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

function createTraderDetailsButton() {
    let button = document.createElement('button')
    button.id='traderDetailsReducedDataSet'
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.innerHTML = 'Complete Trader Details (Reduced Data Set)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('traderDetailsReducedDataSetToggle',true)
        traderDetailsReducedDataSet()
    })

    return button
}

function createRouteDetailsAuthorisedButton() {
    let button = document.createElement('button')
    button.id='routeDetailsAuthorised'
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.innerHTML = 'Complete Route Details (Authorised Place)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('routeDetailsAuthorisedToggle',true)
        routeDetailsAuthorised()
    })

    return button
}

/* #### Trader Details Pages #### */

const startTraderDetails = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        location.href = `/manage-transit-movements/departures/${lrn}/trader-details/transit-holder/add-eori-tin`
    }
}

const addEoriTin = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/transit-holder/add-eori-tin`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitHolderName = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/holder-of-transit/name`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitHolderCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/transit-holder/country`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitHolderAddress = (lrn, data, data2, data3) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/transit-holder/address`)){
        document.getElementById('numberAndStreet').value = data
        document.getElementById('city').value = data2
        document.getElementById('postalCode').value = data3
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addContact = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/holder-of-transit/add-contact`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const actingRepresentative = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/acting-as-representative`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const reducedDataSet = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/consignment/reduced-data-set`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const moreThanOneConsignee = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/consignment/more-than-one-consignee`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeEoriTin = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/consignee/add-eori-tin`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeName = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/consignment/consignee/name`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/consignee/country`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeAddress = (lrn, data, data2, data3) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/consignee/address`)){
        document.getElementById('numberAndStreet').value = data
        document.getElementById('city').value = data2
        document.getElementById('postalCode').value = data3
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const traderDetailsCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/trader-details/check-answers`)){
        toggleButtonsOff()
        document.getElementsByClassName('govuk-button')[0].click()
    }
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

const bindingItinerary = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/binding-itinerary`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteAddCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/transit-route-add-country`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/1/transit-route-country`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteAddAnother = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/routing/transit-route-add-another-country`)){
        document.getElementById(data).click()
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

const officeOfTransitETA = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/1/office-of-transit-add-eta`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitLoopCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/1/office-of-transit-check-answers`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitAddAnother = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/transit/add-another-office-of-transit`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-type`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsIdentification = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-identification`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsEORI = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/eori-tin`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsAddAnotherIdentifier = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-add-identifier`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsAddContact = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-add-contact`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/location-of-goods/location-of-goods-check-answers`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingUNLOCODE = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/loading/place-of-loading-add-un-locode`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/loading/place-of-loading-country`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingLocation = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/route-details/loading/place-of-loading-location`)){
        document.getElementById('value').value = data
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

/* Trader Details Journey */

function traderDetailsReducedDataSet(){
    startTraderDetails(getLRN())
    addEoriTin(getLRN(), 'value-no')
    transitHolderName(getLRN(), 'Person')
    transitHolderCountry(getLRN(), 'IT')
    transitHolderAddress(getLRN(), '12 Italy Road', 'Rome', 'IT65')
    addContact(getLRN(), 'value-no')
    actingRepresentative(getLRN(), 'value-no')
    reducedDataSet(getLRN(), 'value')
    moreThanOneConsignee(getLRN(), 'value-no')
    consigneeEoriTin(getLRN(), 'value-no')
    consigneeName(getLRN(), 'consignee')
    consigneeCountry(getLRN(), 'IT')
    consigneeAddress(getLRN(), '14 Italy Road', 'Rome', 'IT87')
    traderDetailsCYA(getLRN())
}

/* ## Route Details Authorised Place journey ## */

function routeDetailsAuthorised() {
    startRouteDetails(getLRN())
    countryOfDestination(getLRN(), 'IT')
    officeOfDestination(getLRN(), 'IT034105')
    bindingItinerary(getLRN(), 'value-no')
    transitRouteAddCountry(getLRN(), 'value')
    transitRouteCountry(getLRN(), 'DE')
    transitRouteAddAnother(getLRN(), 'value-no')
    routingCYA(getLRN())
    officeOfTransitCountry(getLRN(),'DE')
    officeOfTransit(getLRN(),'DE004058')
    officeOfTransitETA(getLRN(), 'value-no')
    officeOfTransitLoopCYA(getLRN())
    officeOfTransitAddAnother(getLRN(), 'value-no')
    locationOfGoodsType(getLRN(), 'value')
    locationOfGoodsIdentification(getLRN(), 'value_1')
    locationOfGoodsEORI(getLRN(), 'eori1234')
    locationOfGoodsAddAnotherIdentifier(getLRN(), 'value-no')
    locationOfGoodsAddContact(getLRN(), 'value-no')
    locationOfGoodsCYA(getLRN())
    placeOfLoadingUNLOCODE(getLRN(), 'value-no')
    placeOfLoadingCountry(getLRN(), 'AR')
    placeOfLoadingLocation(getLRN(), 'locid1234')
    loadingCYA(getLRN())
    routeDetailsCYA(getLRN())
}


