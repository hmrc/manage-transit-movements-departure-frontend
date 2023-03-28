// ==UserScript==
// @name         CTC-Departures Section Auto Completer
// @namespace    http://tampermonkey.net/
// @version      11.0
// @description  Script to automatically fill out CTC sections
// @author       Reece-Carruthers
// @author       Tega-Okeremeta
// @match        http*://*/manage-transit-movements/departures/*/task-list
// @match        http*://*/manage-transit-movements/departures/route-details/*
// @match        http*://*/manage-transit-movements/departures/trader-details/*
// @match        http*://*/manage-transit-movements/departures/transport-details/*
// @match        http*://*/manage-transit-movements/departures/guarantee-details/*
// @match        http*://*/manage-transit-movements/departures/documents/*
// @match        http*://*/manage-transit-movements/departures/items/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_setValue
// @grant        GM_getValue
// @grant        GM_addStyle
// @downloadURL  https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-Section-Auto-Completer.user.js
// @updateURL    https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-Section-Auto-Completer.user.js
// ==/UserScript==

(function() {
    'use strict';
})();

window.addEventListener('load', function() {
    saveLRN()
    displayPanicButton()
    isAButtonToggled()
    isSectionCompleted()
}, false);

/* Main Functions */

function isAButtonToggled() {
    if(GM_getValue('traderDetailsReducedDataSetToggle',false)){
        traderDetails()
    }
    else if(GM_getValue('routeDetailsAuthorisedToggle',false)){
        routeDetailsAuthorised()
    }
    else if(GM_getValue('transportDetailsToggle',false)){
        // Work around for when transport details is accessed before trader details, as nav sends you back to trader details and stops the script running
        if(location.href.includes('trader-details')){
            GM_setValue('traderDetailsReducedDataSetToggle',true)
            traderDetails()
        } else{
            transportDetails()
        }
    }
    else if(GM_getValue('addDocumentsToggle',false)){
        addDocuments()
    }
    else if(GM_getValue('itemsToggle',false)){
        items()
    }
    else if(GM_getValue('guaranteeDetailsWaiverToggle',false)){
        guaranteeDetailsWaiver()
    }
    else {
        if(onLandingPage()){
            setReducedDataSet()
            document.body.appendChild(setupGUI())
        }
    }
}

function displayPanicButton() {
    const panicPanel = document.createElement('div')
    if(!onLandingPage()){
        GM_addStyle(' .panicStyle { position: absolute; top: 50px; display: grid; grid-template-rows: repeat(1, 1fr);')
        panicPanel.classList.add('panicStyle')
    }else{
        GM_addStyle(' .panicStyle { position: absolute; top: 415px; display: grid; grid-template-rows: repeat(1, 1fr);')
        panicPanel.classList.add('panicStyle')
    }
    panicPanel.appendChild(createPanicButton())
    document.body.appendChild(panicPanel)
}

function toggleTraderDetailsButtonsOff() {
    GM_setValue('traderDetailsReducedDataSetToggle',false)
}
function toggleRouteDetailsButtonsOff() {
    GM_setValue('routeDetailsAuthorisedToggle',false)
}
function toggleTransportDetailsButtonsOff() {
    GM_setValue('transportDetailsToggle',false)
}
function toggleGuaranteeDetailsButtonsOff() {
    GM_setValue('guaranteeDetailsWaiverToggle',false)
}
function toggleAddDocumentButtonOff() {
    GM_setValue('addDocumentsToggle',false)
}
function toggleItemsButtonOff() {
    GM_setValue('itemsToggle',false)
}


function setupGUI() {
    const panel = document.createElement('div');
    GM_addStyle(' .guiStyle { position: absolute; top: 50px; display: grid; grid-template-rows: repeat(7, 1fr);') // Bump the repeat number up by 1 when adding a new button so there is space for it
    panel.classList.add('guiStyle')
    panel.appendChild(createTraderDetailsButton())
    panel.appendChild(createRouteDetailsAuthorisedButton())
    panel.appendChild(createTransportDetailsButton())
    panel.appendChild(createAddDocumentsButton())
    panel.appendChild(createItemsButton())
    panel.appendChild(createGuaranteeDetailsWaiverButton())
    panel.appendChild(createReducedDataSetSwitch())
    panel.appendChild(createCompleteAllButton())
    return panel
}

/* Helper Functions */


function isSectionCompleted() {
    if (onLandingPage()) {
        if (document.getElementById('route-details-status').innerText === 'COMPLETED') {
            document.getElementById('routeDetailsAuthorised').remove()
        }
        if (document.getElementById('trader-details-status').innerText === 'COMPLETED') {
            document.getElementById('traderDetails').remove()
            document.getElementById('reducedDataSetSwitch').remove()
        }
        if (document.getElementById('transport-details-status').innerText === 'COMPLETED') {
            document.getElementById('transportDetails').remove()
        }
        if (document.getElementById('documents-status').innerText === 'COMPLETED') {
            document.getElementById('addDocumentsButton').remove()
        }
        if (document.getElementById('items-status').innerText === 'COMPLETED') {
            document.getElementById('itemsButton').remove()
        }
        if (document.getElementById('guarantee-details-status').innerText === 'COMPLETED') {
            document.getElementById('guaranteeDetailsWaiver').remove()
        }
    }
}

function saveLRN() {
    if(onLandingPage()){
        GM_setValue('lrn',location.href.split('/')[5])
    }
}

function setReducedDataSet() {
    if(GM_getValue('reducedDataSetSwitch', 'notSet') === 'notSet') {
        GM_setValue('reducedDataSetSwitch', true)
    }
}

function getReducedDataSet() {
    return GM_getValue('reducedDataSetSwitch', null)
}

function getReducedDataSetAnswer() {
    if(getReducedDataSet()) {
        return 'value'
    }else {
        return 'value-no'
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
    button.id='traderDetails'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    if(getReducedDataSet()) {
        button.innerHTML = 'Complete Trader Details (Reduced Data Set)'
    }else {
        button.innerHTML = 'Trader Details (Full Data Set)'
    }

    button.addEventListener("click", function handleClick() {
        GM_setValue('traderDetailsReducedDataSetToggle',true)
        traderDetails()
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

function createAddDocumentsButton() {
    let button = document.createElement('button')
    button.id='addDocumentsButton'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.innerHTML = 'Complete Add Documents'
    button.addEventListener("click", function handleClick() {
        GM_setValue('addDocumentsToggle',true)
        addDocuments()
    })

    return button
}

function createTransportDetailsButton() {
    let button = document.createElement('button')
    button.id='transportDetails'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.innerHTML = 'Complete Transport Details'
    button.addEventListener("click", function handleClick() {
        GM_setValue('transportDetailsToggle',true)
        transportDetails()
    })

    return button
}

function createItemsButton() {
    let button = document.createElement('button')
    button.id='itemsButton'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.innerHTML = 'Complete Items (Up to UCR)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('itemsToggle',true)
        items()
    })

    return button
}

function createGuaranteeDetailsWaiverButton() {
    let button = document.createElement('button')
    button.id='guaranteeDetailsWaiver'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.innerHTML = 'Complete Guarantee Details (Guarantee Waiver)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('guaranteeDetailsWaiverToggle',true)
        guaranteeDetailsWaiver()
    })

    return button
}

function createReducedDataSetSwitch() {
    let button = document.createElement('button')
    button.id='reducedDataSetSwitch'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.style.marginTop = '5px'

    if(getReducedDataSet()){
        button.innerHTML = 'Using a Reduced Data Set'
    }else{
        button.innerHTML = 'Using the Full Data Set'
    }

    button.addEventListener("click", function handleClick() {
        let button = document.getElementById('reducedDataSetSwitch');
        if(getReducedDataSet()) {
            GM_setValue('reducedDataSetSwitch',false)
            button.innerHTML = 'Using a Full Data Set'
            button.style.backgroundColor = '#00752d'

            document.getElementById('traderDetails').innerHTML = 'Trader Details (Full Data Set)'
        }else {
            GM_setValue('reducedDataSetSwitch',true)
            button.innerHTML = 'Using a Reduced Data Set'
            button.style.backgroundColor = '#007025'

            document.getElementById('traderDetails').innerHTML = 'Trader Details (Reduced Data Set)'
        }
    })

    return button
}

function createCompleteAllButton() {
    let button = document.createElement('button')
    button.id='completeAll'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.margin = '1px'
    button.style.marginTop = '5px'
    button.innerHTML = 'Complete All Sections (Excluding Items)'
    button.addEventListener("click", function handleClick() {
        GM_setValue('traderDetailsReducedDataSetToggle',true)
        GM_setValue('routeDetailsAuthorisedToggle',true)
        GM_setValue('addDocumentsToggle',true)
        GM_setValue('transportDetailsToggle',true)
        // GM_setValue('itemsToggle',true)
        GM_setValue('guaranteeDetailsWaiverToggle',true)
        traderDetails()
    })

    return button
}

function createPanicButton() {
    let button = document.createElement('button')
    button.id='panicButton'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.style.position = 'absolute'
    button.style.margin = '1px'
    button.innerHTML = 'STOP SCRIPTS'
    button.style.backgroundColor = '#d13b3b'
    button.addEventListener("click", function handleClick() {
        GM_setValue('traderDetailsReducedDataSetToggle',false)
        GM_setValue('routeDetailsAuthorisedToggle',false)
        GM_setValue('transportDetailsToggle',false)
        GM_setValue('guaranteeDetailsWaiverToggle',false)
        GM_setValue('addDocumentsToggle',false)
        GM_setValue('itemsToggle',false)
    })

    return button
}

/* #### Trader Details Pages #### */

const startTraderDetails = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        if (location.hostname === "localhost") {
            location.href = `http:\/\/localhost:10130/manage-transit-movements/departures/trader-details/${lrn}`
        } else {
            location.href = `/manage-transit-movements/departures/trader-details/${lrn}`
        }

    }
}

const tirIdentificaitonKnown = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/holder-of-transit/is-tir-id-known/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addEoriTin = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/transit-holder/add-eori-tin/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitHolderName = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/transit-holder/name/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitHolderCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/transit-holder/country/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitHolderAddress = (lrn, data, data2, data3) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/transit-holder/address/${lrn}`)){
        document.getElementById('numberAndStreet').value = data
        document.getElementById('city').value = data2
        document.getElementById('postalCode').value = data3
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addContact = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/transit-holder/add-contact/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const actingRepresentative = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/representative/acting/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const reducedDataSet = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/reduced-data-set/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consignorEoriTin = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignor/add-eori-tin/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consignorName = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignor/name/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consignorCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignor/country/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consignorAddress = (lrn, data, data2, data3) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignor/address/${lrn}`)){
        document.getElementById('numberAndStreet').value = data
        document.getElementById('city').value = data2
        document.getElementById('postalCode').value = data3
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consignorContact = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignor/add-contact/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const moreThanOneConsignee = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignee/multiple/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeEoriTin = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignee/add-eori-tin/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeName = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignee/name/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignee/country/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const consigneeAddress = (lrn, data, data2, data3) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/consignee/address/${lrn}`)){
        document.getElementById('numberAndStreet').value = data
        document.getElementById('city').value = data2
        document.getElementById('postalCode').value = data3
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const traderDetailsCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/trader-details/check-answers/${lrn}`)){
        toggleTraderDetailsButtonsOff()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* #### Route Details Pages #### */

const startRouteDetails = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        if (location.hostname === "localhost") {
            location.href = `http:\/\/localhost:10129/manage-transit-movements/departures/route-details/${lrn}`
        } else {
            location.href = `/manage-transit-movements/departures/route-details/${lrn}`
        }

    }
}

const countryOfDestination = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/transit-route/country-of-destination/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addLocationOfGoods = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/add-location-of-goods/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfDestination = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/transit-route/office-of-destination/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const bindingItinerary = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/transit-route/binding-itinerary/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteAddCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/transit-route/add-country/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/transit-route/1/country/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transitRouteAddAnother = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/transit-route/add-another-country/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const routingCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/transit-route/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/office-of-transit/1/country/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransit = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/office-of-transit/1/office/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitETA = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/office-of-transit/1/add-eta/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitLoopCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/office-of-transit/1/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const officeOfTransitAddAnother = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/office-of-transit/add-another/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/type/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsIdentification = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/identification/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsEORI = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/eori-tin/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsAddAnotherIdentifier = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/add-identifier/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsAddContact = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/add-contact/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const locationOfGoodsCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingUNLOCODE = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/place-of-loading/add-un-locode/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/place-of-loading/country/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const authorisationNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/location-of-goods/authorisation-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const placeOfLoadingLocation = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/place-of-loading/location/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const loadingCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/place-of-loading-unloading/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const routeDetailsCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/route-details/check-answers/${lrn}`)){
        toggleRouteDetailsButtonsOff()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* #### Add Documents Pages #### */

const startAddDocuments = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        if (location.hostname === "localhost") {
            location.href = `http:\/\/localhost:10132/manage-transit-movements/departures/documents/${lrn}`
        } else {
            location.href = `/manage-transit-movements/departures/documents/${lrn}`
        }
    }
}

const documentType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/type/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const previousDocumentType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/type-previous/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const referenceNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/reference-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addGoodsItemNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/goods-item-number/add/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const goodsItemNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/goods-item-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addPackageType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/add-package/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const packageType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/package/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addPackageQuantity = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/add-package-quantity/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const packageQuantity = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/package-quantity/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addQuantity = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/quantity/add/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const quantityMetric = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/quantity/metric/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const quantity = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/previous/quantity/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const documentLoopCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/1/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const documentAddAnother = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/documents/add-another/${lrn}`)){
        toggleAddDocumentButtonOff()
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* #### Transport Details - UPDATE as journey develops #### */

const startTransportDetails = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        if (location.hostname === "localhost") {
            location.href = `http:\/\/localhost:10131/manage-transit-movements/departures/transport-details/apply-ucr-to-all-items/${lrn}`
        } else {
            location.href = `/manage-transit-movements/departures/transport-details/apply-ucr-to-all-items/${lrn}`
        }

    }
}

const countryOfDispatchTIR = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/country-of-dispatch/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const sameUCR = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/apply-ucr-to-all-items/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const sameCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/items-same-destination-country/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const anyContainers = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/containers/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const inlandMode = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/inland-mode-of-transport/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const meansIdentification = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/departure-means-of-transport/identification/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const meansIdentificationNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/departure-means-of-transport/identification-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const meansCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/departure-means-of-transport/country/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const borderCrossing = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-mode-of-transport/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const borderIdentificationType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-means-of-transport/1/identification/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const borderIdentificationNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-means-of-transport/1/identification-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const borderRegisteredCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-means-of-transport/1/add-country/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const borderOffice = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-means-of-transport/1/office-of-transit/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const anotherVehicleCrossing = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-mode-of-transport/add/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addConveyanceReferenceNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-means-of-transport/1/add-conveyance-reference-number/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const borderCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-means-of-transport/1/check-your-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addAnotherBorderMeans = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/border-means-of-transport/add-another/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const modesMeansCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/modes-means-of-transport/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addSupplyChainActor = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/supply-chain-actor/add/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addAuth = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/authorisations/add/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const authRefNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/authorisations/1/reference-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addAnotherAuthType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/authorisations/add-another/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const carrierEORI = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/carrier/eori-tin/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addCarrierContact = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/carrier/add-contact/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addTransportEquipment = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/add-transport-equipment/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addTransportSeal = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/1/seals/add/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transportSealNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/1/seals/1/identification-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addAnotherTransportSeal = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/1/seals/add-another/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transportGoodsItem = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/1/goods-item-numbers/1/goods-item-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addAnotherGoodsItem = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/1/goods-item-numbers/add-another/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transportSealCYA= (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/1/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const addAnotherTransportEquipment = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/transport-equipment/add-another/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const transportEquipmentCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/transport-details/check-answers/${lrn}`)){
        toggleTransportDetailsButtonsOff()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* #### Guarantee Details #### */

const startGuaranteeDetails = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        if (location.hostname === "localhost") {
            location.href = `http:\/\/localhost:10128/manage-transit-movements/departures/guarantee-details/${lrn}`
        } else {
            location.href = `/manage-transit-movements/departures/guarantee-details/${lrn}`
        }

    }
}

const tirDeclaration = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/guarantee-added-tir/${lrn}`)){
        toggleGuaranteeDetailsButtonsOff()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const guaranteeType = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/1/guarantee-type/${lrn}`)){
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const guaranteeNumber = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/1/guarantee-reference-number/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const accessCode = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/1/access-code/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const liabilityCurrency = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/1/liability-currency/${lrn}`)){
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const liabilityAmount = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/1/liability-amount/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const guaranteeLoopCYA = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/1/check-answers/${lrn}`)){
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const guaranteeAddAnother = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/guarantee-details/add-another-guarantee/${lrn}`)){
        toggleGuaranteeDetailsButtonsOff()
        document.getElementById(data).click()
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* Item Pages */

const startItems = (lrn) => {
    if(currentPageIs(`/manage-transit-movements/departures/${lrn}/task-list`)){
        if (location.hostname === "localhost") {
            location.href = `http:\/\/localhost:10127/manage-transit-movements/departures/items/${lrn}`
        } else {
            location.href = `/manage-transit-movements/departures/items/${lrn}`
        }

    }
}

const itemDescription = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/items/1/description/${lrn}`)){
        document.getElementById('value').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

const itemCountry = (lrn, data) => {
    if(currentPageIs(`/manage-transit-movements/departures/items/1/country-of-destination/${lrn}`)){
        toggleItemsButtonOff()
        document.getElementById('value-select').value = data
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

// const itemUCR = (lrn, data) => {
//     if(currentPageIs(`/manage-transit-movements/departures/items/1/ucr/${lrn}`)){
//         document.getElementById('value').value = data
//         document.getElementsByClassName('govuk-button')[0].click()
//     }
// }


/* #### Journeys #### */

/* Trader Details Journey */

function traderDetails(){
    if(!getReducedDataSet()) {
        consignorEoriTin(getLRN(), 'value-no')
        consignorName(getLRN(), 'consignor')
        consignorCountry(getLRN(), 'IT')
        consignorAddress(getLRN(), '22 Italy Road', 'Rome', 'IT53')
        consignorContact(getLRN(), 'value-no')
    }
    startTraderDetails(getLRN())
    tirIdentificaitonKnown(getLRN(),'value-no')
    addEoriTin(getLRN(), 'value-no')
    transitHolderName(getLRN(), 'Person')
    transitHolderCountry(getLRN(), 'IT')
    transitHolderAddress(getLRN(), '12 Italy Road', 'Rome', 'IT65')
    addContact(getLRN(), 'value-no')
    actingRepresentative(getLRN(), 'value-no')
    reducedDataSet(getLRN(), getReducedDataSetAnswer())
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
    addLocationOfGoods(getLRN(),'value')
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
    authorisationNumber(getLRN(), 'auth123')
    loadingCYA(getLRN())
    routeDetailsCYA(getLRN())
}

/* ## Add documents journey ## */

function addDocuments() {
    startAddDocuments(getLRN())
    documentType(getLRN(), "CodeOnly")
    previousDocumentType(getLRN(), "CodeOnly")
    referenceNumber(getLRN(), "9078GH87")
    addGoodsItemNumber(getLRN(), "value")
    goodsItemNumber(getLRN(), "12345")
    addPackageType(getLRN(), "value")
    packageType(getLRN(), "BG")
    addPackageQuantity(getLRN(), "value")
    packageQuantity(getLRN(), '45')
    addQuantity(getLRN(), 'value')
    quantityMetric(getLRN(), 'KLT')
    quantity(getLRN(), '1000')
    documentLoopCYA(getLRN())
    documentAddAnother(getLRN(), 'value-no')
}

/* ## Transport Details journey ## */

function transportDetails() {
    startTransportDetails(getLRN())
    countryOfDispatchTIR(getLRN(), 'IT')
    sameUCR(getLRN(),'value-no')
    sameCountry(getLRN(),'value-no')
    anyContainers(getLRN(),'value-no')
    inlandMode(getLRN(), 'value_1')
    meansIdentification(getLRN(),'value')
    meansIdentificationNumber(getLRN(),'wagon12')
    meansCountry(getLRN(),'GB')
    anotherVehicleCrossing(getLRN(),'value-no')
    borderCrossing(getLRN(),'value')
    borderIdentificationType(getLRN(),'value')
    borderIdentificationNumber(getLRN(),'1234')
    borderRegisteredCountry(getLRN(),'value-no')
    borderOffice(getLRN(),'DE004058')
    borderCYA(getLRN())
    addAnotherBorderMeans(getLRN(), 'value-no')
    addConveyanceReferenceNumber(getLRN(),'value-no')
    modesMeansCYA(getLRN())
    addSupplyChainActor(getLRN(), 'value-no')
    addAuth(getLRN(), 'value-no')
    authRefNumber(getLRN(), 'TRD123')
    addAnotherAuthType(getLRN(), 'value-no')
    carrierEORI(getLRN(), 'carrierEORI123')
    addCarrierContact(getLRN(), 'value-no')
    addTransportEquipment(getLRN(), 'value')
    addTransportSeal(getLRN(), 'value')
    transportSealNumber(getLRN(), 'TransportSeal1')
    addAnotherTransportSeal(getLRN(), 'value-no')
    transportGoodsItem(getLRN(), '1234')
    addAnotherGoodsItem(getLRN(), 'value-no')
    transportSealCYA(getLRN())
    addAnotherTransportEquipment(getLRN(), 'value-no')
    transportEquipmentCYA(getLRN())
}

/* ## Items ## */

function items() {
    startItems(getLRN())
    itemDescription(getLRN(), 'Item description')
    itemCountry(getLRN(), 'IT')
    // itemUCR(getLRN(), 'UCR')
}

/* ## Guarantee Details ## */

function guaranteeDetailsWaiver() {
    startGuaranteeDetails(getLRN())
    guaranteeType(getLRN(), 'value')
    tirDeclaration(getLRN())
    guaranteeNumber(getLRN(), '01GB1234567890120A123456')
    accessCode(getLRN(), '1234')
    liabilityCurrency(getLRN(), 'GBP')
    liabilityAmount(getLRN(), '1234')
    guaranteeLoopCYA(getLRN())
    guaranteeAddAnother(getLRN(), 'value-no')
}

