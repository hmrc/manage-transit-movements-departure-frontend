// ==UserScript==
// @name         CTC-Departures Section Auto Completer
// @namespace    http://tampermonkey.net/
// @version      15
// @description  Script to automatically fill out CTC sections
// @author       Reece-Carruthers
// @author       Tega-Okeremeta
// @match        http*://*/manage-transit-movements/departures/*/declaration-summary
// @match        http*://*/manage-transit-movements/departures/route-details/*
// @match        http*://*/manage-transit-movements/departures/trader-details/*
// @match        http*://*/manage-transit-movements/departures/transport-details/*
// @match        http*://*/manage-transit-movements/departures/guarantee-details/*
// @match        http*://*/manage-transit-movements/departures/documents/*
// @match        http*://*/manage-transit-movements/departures/items/*
// @match        http*://*/manage-transit-movements/departures/not-found
// @icon         https://www.google.com/s2/favicons?sz=64&domain=tampermonkey.net
// @grant        GM_setValue
// @grant        GM_getValue
// @grant        GM_addStyle
// @downloadURL  https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-Section-Auto-Completer.user.js
// @updateURL    https://github.com/hmrc/manage-transit-movements-departure-frontend/raw/main/tamperMonkey/CTC-Departures-Section-Auto-Completer.user.js
// ==/UserScript==

(function () {
    'use strict'
})()

function initialiseJourneys() {
    function traderDetailsReducedDataSetJourney() {
        let journey = new Journey("traderDetailsReducedDataSetJourney", "Trader Details (Reduced Data Set)", "/manage-transit-movements/departures/trader-details/", "10130", "trader-details-status")
        journey.addPages(
            [
                new ButtonPage("/manage-transit-movements/departures/trader-details/holder-of-transit/is-tir-id-known/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/transit-holder/add-eori-tin/", "value-no"),
                new InputPage("/manage-transit-movements/departures/trader-details/transit-holder/name/", "Person"),
                new SelectPage("/manage-transit-movements/departures/trader-details/transit-holder/country/", "IT"),
                new AddressPage("/manage-transit-movements/departures/trader-details/transit-holder/address/", "12 Italy Road", "Rome", "IT65"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/transit-holder/add-contact/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/representative/acting/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/reduced-data-set/", "value"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/consignee/multiple/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/consignee/add-eori-tin/", "value-no"),
                new InputPage("/manage-transit-movements/departures/trader-details/consignee/name/", "consignee"),
                new SelectPage("/manage-transit-movements/departures/trader-details/consignee/country/", "IT"),
                new AddressPage("/manage-transit-movements/departures/trader-details/consignee/address/", "14 Italy Road", "Rome", "IT87"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/trader-details/check-answers/")
            ]
        )
        return journey
    }

    function traderDetailsFullDataSetJourney() {
        let journey = new Journey("traderDetailsFullDataSetJourney", "Trader Details (Full Data Set)", "/manage-transit-movements/departures/trader-details/", "10130", "trader-details-status")
        journey.addPages(
            [
                new ButtonPage("/manage-transit-movements/departures/trader-details/holder-of-transit/is-tir-id-known/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/transit-holder/add-eori-tin/", "value-no"),
                new InputPage("/manage-transit-movements/departures/trader-details/transit-holder/name/", "Person"),
                new SelectPage("/manage-transit-movements/departures/trader-details/transit-holder/country/", "IT"),
                new AddressPage("/manage-transit-movements/departures/trader-details/transit-holder/address/", "12 Italy Road", "Rome", "IT65"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/transit-holder/add-contact/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/representative/acting/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/reduced-data-set/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/consignee/multiple/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/consignee/add-eori-tin/", "value-no"),
                new InputPage("/manage-transit-movements/departures/trader-details/consignee/name/", "consignee"),
                new SelectPage("/manage-transit-movements/departures/trader-details/consignee/country/", "IT"),
                new AddressPage("/manage-transit-movements/departures/trader-details/consignee/address/", "14 Italy Road", "Rome", "IT87"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/consignor/add-eori-tin/", "value-no"),
                new InputPage("/manage-transit-movements/departures/trader-details/consignor/name/", "consignor"),
                new SelectPage("/manage-transit-movements/departures/trader-details/consignor/country/", "IT"),
                new AddressPage("/manage-transit-movements/departures/trader-details/consignor/address/", "22 Italy Road", "Rome", "IT53"),
                new ButtonPage("/manage-transit-movements/departures/trader-details/consignor/add-contact/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/trader-details/check-answers/")
            ]
        )
        return journey
    }

    function routeDetailsJourney() {
        let journey = new Journey("routeDetailsJourney", "Route Details (Authorised Place)", "/manage-transit-movements/departures/route-details/", "10129", "route-details-status")
        journey.addPages(
            [
                new SelectPage("/manage-transit-movements/departures/route-details/transit-route/country-of-destination/", "IT"),
                new ButtonPage("/manage-transit-movements/departures/route-details/location-of-goods/add-location-of-goods/", "value"),
                new SelectPage("/manage-transit-movements/departures/route-details/transit-route/office-of-destination/", "IT034105"),
                new ButtonPage("/manage-transit-movements/departures/route-details/transit-route/binding-itinerary/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/route-details/transit-route/add-country/", "value"),
                new SelectPage("/manage-transit-movements/departures/route-details/transit-route/1/country/", "DE"),
                new ButtonPage("/manage-transit-movements/departures/route-details/transit-route/add-another-country/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/route-details/transit-route/check-answers/"),
                new SelectPage("/manage-transit-movements/departures/route-details/office-of-transit/1/country/", "DE"),
                new SelectPage("/manage-transit-movements/departures/route-details/office-of-transit/1/office/", "DE004058"),
                new ButtonPage("/manage-transit-movements/departures/route-details/office-of-transit/1/add-eta/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/route-details/office-of-transit/1/check-answers/"),
                new ButtonPage("/manage-transit-movements/departures/route-details/office-of-transit/add-another/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/route-details/location-of-goods/type/", "value"),
                new ButtonPage("/manage-transit-movements/departures/route-details/location-of-goods/identification/", "value_1"),
                new InputPage("/manage-transit-movements/departures/route-details/location-of-goods/eori-tin/", "eori1234"),
                new ButtonPage("/manage-transit-movements/departures/route-details/location-of-goods/add-identifier/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/route-details/location-of-goods/add-contact/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/route-details/location-of-goods/check-answers/"),
                new ButtonPage("/manage-transit-movements/departures/route-details/place-of-loading/add-un-locode/", "value-no"),
                new SelectPage("/manage-transit-movements/departures/route-details/place-of-loading/country/", "AR"),
                new InputPage("/manage-transit-movements/departures/route-details/location-of-goods/authorisation-number/", "auth123"),
                new InputPage("/manage-transit-movements/departures/route-details/place-of-loading/location/", "locid1234"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/route-details/place-of-loading-unloading/check-answers/"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/route-details/check-answers/")
            ]
        )
        return journey
    }

    function transportDetailsJourney() {
        let journey = new Journey("transportDetailsJourney", "Transport Details", "/manage-transit-movements/departures/transport-details/", "10131", "transport-details-status")
        journey.addPages(
            [
                new SelectPage("/manage-transit-movements/departures/transport-details/country-of-dispatch/", "IT"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/apply-ucr-to-all-items/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/items-same-destination-country/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/containers/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/inland-mode-of-transport/", "value_1"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/departure-means-of-transport/identification/", "value"),
                new InputPage("/manage-transit-movements/departures/transport-details/departure-means-of-transport/identification-number/", "wagon12"),
                new SelectPage("/manage-transit-movements/departures/transport-details/departure-means-of-transport/country/", "GB"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/border-mode-of-transport/add/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/border-mode-of-transport/", "value"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/border-means-of-transport/1/identification/", "value"),
                new InputPage("/manage-transit-movements/departures/transport-details/border-means-of-transport/1/identification-number/", "1234"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/border-means-of-transport/1/add-country/", "value-no"),
                new SelectPage("/manage-transit-movements/departures/transport-details/border-means-of-transport/1/office-of-transit/", "DE004058"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/transport-details/border-means-of-transport/1/check-your-answers/"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/border-means-of-transport/add-another/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/border-means-of-transport/1/add-conveyance-reference-number/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/transport-details/modes-means-of-transport/check-answers/"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/supply-chain-actor/add/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/authorisations/add/", "value-no"),
                new InputPage("/manage-transit-movements/departures/transport-details/authorisations/1/reference-number/", "TRD123"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/authorisations/add-another/", "value-no"),
                new InputPage("/manage-transit-movements/departures/transport-details/carrier/eori-tcuin/", "GB123456"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/carrier/add-contact/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/transport-equipment/add-transport-equipment/", "value"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/transport-equipment/1/seals/add/", "value"),
                new InputPage("/manage-transit-movements/departures/transport-details/transport-equipment/1/seals/1/identification-number/", "TransportSeal1"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/transport-equipment/1/seals/add-another/", "value-no"),
                new InputPage("/manage-transit-movements/departures/transport-details/transport-equipment/1/goods-item-numbers/1/goods-item-number/", "1234"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/transport-equipment/1/goods-item-numbers/add-another/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/transport-details/transport-equipment/1/check-answers/"),
                new ButtonPage("/manage-transit-movements/departures/transport-details/transport-equipment/add-another/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/transport-details/check-answers/")
            ]
        )
        return journey
    }

    function addDocumentsJourney() {
        let journey = new Journey("addDocumentsJourney", "Add Documents", "/manage-transit-movements/departures/documents/", "10132", "documents-status")
        journey.addPages(
            [
                new SelectPage("/manage-transit-movements/departures/documents/1/type/", "CodeOnly"),
                new SelectPage("/manage-transit-movements/departures/documents/1/type-previous/", "CodeOnly"),
                new InputPage("/manage-transit-movements/departures/documents/1/reference-number/", "9078GH87"),
                new ButtonPage("/manage-transit-movements/departures/documents/1/previous/goods-item-number/add/", "value"),
                new InputPage("/manage-transit-movements/departures/documents/1/previous/goods-item-number/", "12345"),
                new ButtonPage("/manage-transit-movements/departures/documents/1/previous/add-package/", "value"),
                new SelectPage("/manage-transit-movements/departures/documents/1/previous/package/", "BG"),
                new ButtonPage("/manage-transit-movements/departures/documents/1/previous/add-package-quantity/", "value"),
                new InputPage("/manage-transit-movements/departures/documents/1/previous/package-quantity/", "45"),
                new ButtonPage("/manage-transit-movements/departures/documents/1/previous/quantity/add/", "value"),
                new SelectPage("/manage-transit-movements/departures/documents/1/previous/quantity/metric/", "KLT"),
                new InputPage("/manage-transit-movements/departures/documents/1/previous/quantity/", "1000"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/documents/1/check-answers/"),
                new ButtonPage("/manage-transit-movements/departures/documents/add-another/", "value-no")
            ]
        )
        return journey
    }

    function itemsJourney() {
        let journey = new Journey("itemsJourney", "Items", "/manage-transit-movements/departures/items/", "10127", "items-status")
        journey.addPages(
            [
                new InputPage("/manage-transit-movements/departures/items/1/description/", "Item description"),
                new SelectPage("/manage-transit-movements/departures/items/1/country-of-destination/", "IT"),
                new InputPage("/manage-transit-movements/departures/items/1/ucr/", "UCR"),
                new ButtonPage("/manage-transit-movements/departures/items/1/cus-code/add/", "value"),
                new InputPage("/manage-transit-movements/departures/items/1/cus-code/", "123ABC789"),
                new ButtonPage("/manage-transit-movements/departures/items/1/commodity-code/add/", "value"),
                new InputPage("/manage-transit-movements/departures/items/1/commodity-code/", "123ABC"),
                new ButtonPage("/manage-transit-movements/departures/items/1/dangerous-goods/add/", "value"),
                new InputPage("/manage-transit-movements/departures/items/1/dangerous-goods/1/un-number/", "AB12"),
                new ButtonPage("/manage-transit-movements/departures/items/1/dangerous-goods/add-another/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/items/1/measurement/add-net-weight/", "value-no"),
                new InputPage("/manage-transit-movements/departures/items/1/measurement/gross-weight/", "12.345"),
                new ButtonPage("/manage-transit-movements/departures/items/1/measurement/add-supplementary-units/", "value"),
                new InputPage("/manage-transit-movements/departures/items/1/measurement/supplementary-units/", "12.345"),
                new SelectPage("/manage-transit-movements/departures/items/1/packages/1/type/", "NU"),
                new InputPage("/manage-transit-movements/departures/items/1/packages/1/type-quantity/", "65"),
                new SelectPage("/manage-transit-movements/departures/items/1/packages/1/add-shipping-mark/", "value"),
                new InputPage("/manage-transit-movements/departures/items/1/packages/1/shipping-mark/", "The shipping mark"),
                new ButtonPage("/manage-transit-movements/departures/items/1/packages/add-another/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/items/1/documents/attach/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/items/1/additional-reference/add/", "value-no"),
                new ButtonPage("/manage-transit-movements/departures/items/1/additional-information/add/", "value-no"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/items/1/check-answers/"),
                new ButtonPage("/manage-transit-movements/departures/items/add-another/", "value-no")
            ]
        )
        return journey
    }

    function guaranteeDetailsWaiverJourney() {
        let journey = new Journey("guaranteeDetailsWaiverJourney", "Guarantee Details", "/manage-transit-movements/departures/guarantee-details/", "10128", "guarantee-details-status")
        journey.addPages(
            [
                new ButtonPage("/manage-transit-movements/departures/guarantee-details/1/guarantee-type/", "value"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/guarantee-details/guarantee-added-tir/"),
                new InputPage("/manage-transit-movements/departures/guarantee-details/1/guarantee-reference-number/", "01GB1234567890120A123456"),
                new InputPage("/manage-transit-movements/departures/guarantee-details/1/access-code/", "1234"),
                new SelectPage("/manage-transit-movements/departures/guarantee-details/1/liability-currency/", "GBP"),
                new InputPage("/manage-transit-movements/departures/guarantee-details/1/liability-amount/", "1234"),
                new CheckYourAnswersPage("/manage-transit-movements/departures/guarantee-details/1/check-answers/"),
                new ButtonPage("/manage-transit-movements/departures/guarantee-details/add-another-guarantee/", "value-no"),
            ]
        )
        return journey
    }

    return [
        traderDetailsReducedDataSetJourney(),
        traderDetailsFullDataSetJourney(),
        routeDetailsJourney(),
        transportDetailsJourney(),
        addDocumentsJourney(),
        itemsJourney(),
        guaranteeDetailsWaiverJourney()
    ]
}

window.addEventListener('load', function () {
    saveLRN()
    let journeys = initialiseJourneys()
    stopOnFail(journeys)
    if (!onLandingPage()) {
        document.body.appendChild(displayPanicButton(journeys))
    }
    if (GM_getValue("completeAll", null)) {
        completeAllJourneys(journeys)
    } else {
        isAJourneyToggled(journeys)
        if (onLandingPage()) {
            document.body.appendChild(displayButtons(journeys))
        }
    }
}, false)

function stopOnFail(journeys) {
    if (currentPageIs(`/manage-transit-movements/departures/not-found`)) {
        resetStates(journeys)
    }
}

function completeAllJourneys(journeys) { // Manually do the journeys for complete all until all are fully complete
    if (!journeysComplete(journeys)) {
        if (!isJourneyComplete(journeys[0]) && GM_getValue(journeys[0]._button.id, null) !== "Complete") {
            completeJourney(journeys[0])
        } else if (!isJourneyComplete(journeys[2]) && GM_getValue(journeys[2]._button.id, null) !== "Complete") {
            completeJourney(journeys[2])
        } else if (!isJourneyComplete(journeys[3]) && GM_getValue(journeys[3]._button.id, null) !== "Complete") {
            completeJourney(journeys[3])
        } else if (!isJourneyComplete(journeys[4]) && GM_getValue(journeys[4]._button.id, null) !== "Complete") {
            completeJourney(journeys[4])
        } else if (!isJourneyComplete(journeys[5]) && GM_getValue(journeys[5]._button.id, null) !== "Complete") {
            completeJourney(journeys[5])
        } else {
            completeJourney(journeys[6])
        }
    } else {
        resetStates(journeys)
        location.reload()
    }
}

function resetStates(journeys) {
    journeys.forEach(journey => GM_setValue(journey._button.id, false))
    GM_setValue("completeAll", false)
}

function completeJourney(journey) {
    if (onLandingPage()) {
        GM_setValue(journey._button.id, true)
        if (currentPageIs(`/manage-transit-movements/departures/${getLRN()}/declaration-summary`)) {
            if (location.hostname === "localhost") {
                location.href = `http:\/\/localhost:${journey.localHostPort}${journey.journeyStartUrl}${getLRN()}`
            } else {
                location.href = `${journey.journeyStartUrl}/${getLRN()}`
            }
        }
    } else {
        journey.isToggled()
    }
}

function journeysComplete(journeys) {
    let count = 0
    let countToReach = journeys.length
    journeys.forEach(journey => {
        if (isJourneyComplete(journey)) {
            count += 1
        }
    })
    if (count === countToReach) {
        GM_setValue("completeAll", false)
        return true
    } else {
        return false;
    }
}

const currentPageIs = (path) => {
    if (path.includes("*")) {
        let matches = window.location.pathname.match(path)
        return matches && window.location.pathname.endsWith(path.slice(-5))
    } else {
        return path === window.location.pathname
    }
}

function isAJourneyToggled(journeys) {
    journeys.forEach(journey => journey.isToggled())
}

function getJourneyIDs(journeys) {
    let journeyIDs = []
    journeys.forEach(journey => journeyIDs.push(journey.buttonId))
    return journeyIDs
}

function isJourneyComplete(journey) {
    try {
        if (document.getElementById(journey.statusId).innerText === "COMPLETED") {
            GM_setValue(journey._button.id, "Complete")
            return true
        } else {
            return false
        }
    } catch (err) {
        return false
    }
}

function journeyCannotBeStarted(journey) {
    try {
        return document.getElementById(journey.statusId).innerText === "CANNOT START YET";
    } catch (err) {
        return false
    }
}

function displayButtons(journeys) {
    const panel = document.createElement('div')
    GM_addStyle(` .guiStyle { position: absolute; top: 50px; display: grid; grid-template-rows: repeat(${journeys.length + 1}, 1fr);`)
    panel.classList.add('guiStyle')
    journeys.forEach(journey => {
        if (!isJourneyComplete(journey)) {
            if (journeyCannotBeStarted(journey)) {
                journey._button._button.disabled = true
                journey._button._button.backgroundColor = "#757575"
            }
            panel.appendChild(journey._button._button)
        }
    })
    let journeyIds = getJourneyIDs(journeys)
    if (!journeysComplete(journeys)) {
        panel.appendChild(new CompleteAllButton("Complete All", journeys).button)
    }
    panel.appendChild(new StopScriptsButton(journeyIds).button)
    return panel
}

function displayPanicButton(journeys) {
    const panel = document.createElement('div')
    GM_addStyle(` .guiStyle { position: absolute; top: 50px; display: grid; grid-template-rows: repeat(1, 1fr);`)
    panel.classList.add('guiStyle')
    let journeyIds = getJourneyIDs(journeys)
    panel.appendChild(new StopScriptsButton(journeyIds).button)
    return panel
}

function saveLRN() {
    if (onLandingPage()) {
        GM_setValue('lrn', location.href.split('/')[5])
    }
}

function onLandingPage() {
    return location.href.includes('declaration-summary')
}

function getLRN() {
    return GM_getValue('lrn', null)
}

class Journey {
    get button() {
        return this._button.display
    }

    get buttonId() {
        return this._button.id
    }

    get pages() {
        return this._pages.allPages
    }

    set pages(pages) {
        this._pages = pages
    }


    get journeyStartUrl() {
        return this._journeyStartUrl;
    }

    set journeyStartUrl(value) {
        this._journeyStartUrl = value;
    }

    get localHostPort() {
        return this._localHostPort;
    }

    set localHostPort(value) {
        this._localHostPort = value;
    }

    constructor(buttonID, buttonText, journeyStartUrl, localHostPort, statusId) {
        this._pages = new Pages()
        this._button = new Button(buttonID, buttonText, journeyStartUrl, localHostPort)
        this.statusId = statusId
        this._journeyStartUrl = journeyStartUrl;
        this._localHostPort = localHostPort;
    }

    addPages(pages) {
        pages.forEach(page => this._pages.addPage(page))
        let lastPage = pages.slice(-1)[0]
        this._pages.addPage(new StopPage(this._button.id, lastPage))
    }

    isToggled() {
        if (GM_getValue(this._button.id, null) && GM_getValue(this._button.id, null) !== "Complete") {
            this._pages.runThroughJourney()
        }
    }

}

class Pages {
    get allPages() {
        return this._pages
    }

    set pages(pages) {
        this._pages = pages
    }

    constructor() {
        this._pages = []
    }

    addPage(page) {
        this._pages.push(page)
    }

    runThroughJourney() {
        this._pages.forEach(page => page.onPage())
    }

}

class Page {
    constructor(pageUrl, answer) {
        this.pageUrl = pageUrl
        this.answer = answer
    }
}

class StopPage extends Page {
    constructor(buttonId, lastPage) {
        super(lastPage.pageUrl, null)
        this.buttonId = buttonId
    }

    onPage() {
        if (currentPageIs(`${this.pageUrl}${getLRN()}`)) {
            GM_setValue(this.buttonId, false)
        }
    }
}

class InputPage extends Page {
    constructor(pageUrl, answer) {
        super(pageUrl, answer)
    }

    onPage() {
        if (currentPageIs(`${this.pageUrl}${getLRN()}`)) {
            document.getElementById('value').value = this.answer
            document.getElementById('submit').click()
        }
    }
}

class ButtonPage extends Page {
    constructor(pageUrl, answer) {
        super(pageUrl, answer)
    }

    onPage() {
        if (currentPageIs(`${this.pageUrl}${getLRN()}`)) {
            document.getElementById(this.answer).click()
            document.getElementById('submit').click()
        }
    }
}

class AddressPage extends Page {
    constructor(pageUrl, numberAndStreet, city, postalCode) {
        super(pageUrl, null)
        this.numberAndStreet = numberAndStreet
        this.city = city
        this.postalCode = postalCode
    }

    onPage() {
        if (currentPageIs(`${this.pageUrl}${getLRN()}`)) {
            document.getElementById('numberAndStreet').value = this.numberAndStreet
            document.getElementById('city').value = this.city
            document.getElementById('postalCode').value = this.postalCode
            document.getElementById('submit').click()
        }
    }
}

class CheckYourAnswersPage extends Page {
    constructor(pageUrl) {
        super(pageUrl, null)
    }

    onPage() {
        if (currentPageIs(`${this.pageUrl}${getLRN()}`)) {
            document.getElementById('submit').click()
        }
    }
}

class SelectPage extends Page {
    constructor(pageUrl, answer) {
        super(pageUrl, answer)
    }

    onPage() {
        if (currentPageIs(`${this.pageUrl}${getLRN()}`)) {
            document.getElementById('value-select').value = this.answer
            document.getElementById('submit').click()
        }
    }
}

class Button {
    display() {
        return this._button
    }

    buttonId() {
        return this.id
    }

    constructor(id, buttonText, journeyStartUrl, localHostPort) {
        this.id = id
        this.buttonText = buttonText
        this.journeyStartUrl = journeyStartUrl
        this.localHostPort = localHostPort


        this._button = this.createButton(this.id, this.buttonText, this.journeyStartUrl, this.localHostPort)
    }

    createButton(id, buttonText, journeyStartUrl, localHostPort) {

        let button = document.createElement('button')
        button.id = id

        if (!!document.getElementById('global-header')) {
            button.classList.add('button-start', 'govuk-!-display-none-print')
        } else {
            button.classList.add('govuk-button', 'govuk-!-display-none-print')
        }

        button.style.margin = '1px'
        button.innerHTML = buttonText

        button.addEventListener("click", function handleClick() {
            GM_setValue(id, true)
            if (currentPageIs(`/manage-transit-movements/departures/${getLRN()}/declaration-summary`)) {
                if (location.hostname === "localhost") {
                    location.href = `http:\/\/localhost:${localHostPort}${journeyStartUrl}${getLRN()}`
                } else {
                    location.href = `${journeyStartUrl}/${getLRN()}`
                }
            }
        })

        return button
    }
}

class CompleteAllButton {
    get button() {
        return this._button
    }

    set button(button) {
        this._button = button
    }

    get id() {
        return this._id
    }

    set id(id) {
        this._id = id
    }

    constructor(buttonText, journeys) {
        this._id = "completeAll"
        this.firstJourneyId = journeys[0]._button.id
        this.buttonText = buttonText
        this.firstJourneyUrl = journeys[0].journeyStartUrl
        this.firstJourneyPort = journeys[0].localHostPort
        this.journeys = journeys

        this._button = this.createButton(this.id, this.firstJourneyId, this.buttonText, this.firstJourneyUrl, this.firstJourneyPort, this.journeys)

    }

    createButton(id, firstJourneyId, buttonText, firstJourneyUrl, firstJourneyPort, journeys) {

        let button = document.createElement('button')
        button.id = id

        if (!!document.getElementById('global-header')) {
            button.classList.add('button-start', 'govuk-!-display-none-print')
        } else {
            button.classList.add('govuk-button', 'govuk-!-display-none-print')
        }
        button.style.margin = '1px'
        button.innerHTML = buttonText
        button.addEventListener("click", function handleClick() {
            journeys.forEach(journey => GM_setValue(journey._button.id, false))
            GM_setValue(id, true)
            GM_setValue(firstJourneyId, true)
            if (currentPageIs(`/manage-transit-movements/departures/${getLRN()}/declaration-summary`)) {
                if (location.hostname === "localhost") {
                    location.href = `http:\/\/localhost:${firstJourneyPort}${firstJourneyUrl}${getLRN()}`
                } else {
                    location.href = `${firstJourneyUrl}/${getLRN()}`
                }
            }
        })

        return button
    }
}

class StopScriptsButton {
    get button() {
        return this._button
    }

    set button(button) {
        this._button = button
    }

    get id() {
        return this._id
    }

    set id(id) {
        this._id = id
    }

    constructor(journeyIds) {
        this._id = "stopAll"
        this.buttonText = "Stop Scripts"
        this.journeyIds = journeyIds

        this._button = this.createButton(this.id, this.buttonText, this.journeyIds)

    }

    createButton(id, buttonText, journeyIds) {

        let button = document.createElement('button')
        button.id = id

        if (!!document.getElementById('global-header')) {
            button.classList.add('button-start', 'govuk-!-display-none-print')
        } else {
            button.classList.add('govuk-button', 'govuk-!-display-none-print')
        }
        button.style.backgroundColor = "#d13b3b"
        button.style.margin = '5px'
        button.innerHTML = buttonText
        button.addEventListener("click", function handleClick() {
            journeyIds.forEach(journeyId => GM_setValue(journeyId, false))
            GM_setValue("completeAll", false)
        })

        return button
    }
}
