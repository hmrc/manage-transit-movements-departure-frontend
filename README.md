
# manage-transit-movements-departure-frontend

This service allows a user to create a transit movement departure.

Service manager port: 10120

### Testing

Run unit tests:
<pre>sbt test</pre>  
Run integration tests:  
<pre>sbt it:test</pre>  
or
<pre>sbt IntegrationTest/test</pre>  

### Running manually or for journey tests

<pre>sm --start CTC_TRADERS_PRELODGE -r
sm --stop MANAGE_TRANSIT_MOVEMENTS_DEPARTURE_FRONTEND
sbt run
</pre>

If you hit an entry point before running the journey tests, it gets the compile out of the way and can help keep the first tests from failing.  

e.g.: http://localhost:10120/manage-transit-movements/departures

### Running Scaffold

Create New Page in sbt:
<pre>sbt --supershell=false
g8Scaffold radioButtonPage
</pre>
At this point you need to answer the questions to fill in the className, package, etc
This will create the pages and tests for a RadioButtonPage. It won't overwrite any classes that already exist

Return to command line:
<pre>./migrate.sh
</pre>

This updates the test classes app.routes and messages files

### Creating an Address Page in Scaffold
Then you create the address page referencing the Name Page
<pre>g8Scaffold addressPage
-> package: foo.bar                       # use same package as created for Address name page above
-> title[My New Address]: Consignee Address                    
-> className[MyNewAddress]: ConsigneeAddress
-> formProvider [DynamicAddressFormProvider]
-> navRoute [PreTaskListDetails]:         # use same package as created for Address name page above
-> pageSection [PreTaskListSection]:      # use same package as created for Address name page above
</pre>

### Creating an InputSelect Page in Scaffold
This requires that you already have a Service class with a method that will return a list of the reference object that you require
That object must also extend Selectable.

For the test an arbitrary constructor of the Selectable object will be required, or you can manually create these objects
<pre>g8Scaffold inputSelectPage
-> package: foo.bar
-> className: OfficeOfDeparture
-> referenceClass = CustomsOffice
...
</pre>

### Component view models
In certain cases, we want to limit the number of ways in which the govuk design components are being used so that we can ensure the pages being built are accessible.
This has been done through the use of our own component view models, to define how a heading, caption, and label will be rendered relative to an input.

In the example of a text input, we have 4 distinct use cases:

![Ordinary text input where the heading is a label](images/OrdinaryTextInput.png) | ![Text input with heading and hidden label](images/TextInputWithHiddenLabel.png)
:-------------------------:|:-------------------------:
![Text input with statement heading and visible label that asks the question](images/TextInputWithStatementHeading.png) | ![Text input used for address fields](images/AddressTextInput.png)

There is similar logic behind the InputYesNo and InputSelect component view models.

### Accessibility testing
The accessibility of our templates and components can be checked by running `sbt a11y:test`

### User answers reader
This microservice has been quite experimental in the approach taken towards navigation. This is primarily driven by the UserAnswersReader.
For any given JourneyDomainModel, a user answers reader is defined as the steps taken in order to have a valid set of user answers for that particular journey, often dependent on a series of other answers from previous sections.
Depending on the state of the user answers, the reader will either return a `Right` when the answers are in a completed state, or a `Left` when they are not.
The `Right` contains an instance of the journey domain model, which has a corresponding `routeIfCompleted` (generally a check your answers page) to navigate to.
The `Left` contains an instance of the page that caused the reader to fail, which has a corresponding `route` to navigate to.

This logic is utilised by the navigators, add-to-list change links, and task list links.

NormalMode and CheckMode are still used. Generally speaking:
* sub-section check your answers pages use NormalMode
* section check your answers pages use CheckMode

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

