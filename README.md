
# manage-transit-movements-departure-frontend

This service allows a user to create a transit movement departure.

Service manager port: 10120

### Testing

Run unit tests:
<pre>sbt test</pre>  
Run integration tests:
<pre>sbt it/test</pre>
Run accessibility linter tests:
<pre>sbt A11y/test</pre>

### Running manually or for journey tests

To toggle between the Phase 5 transition and post-transition modes we have defined two separate modules, each with their own set of class bindings to handle the rules associated with these two periods.

#### Transition
<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE_TRANSITION
sm2 --stop MANAGE_TRANSIT_MOVEMENTS_DEPARTURE_FRONTEND_TRANSITION
sbt -Dplay.additional.module=config.TransitionModule run
</pre>

#### Final
<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE
sm2 --stop MANAGE_TRANSIT_MOVEMENTS_DEPARTURE_FRONTEND
sbt -Dplay.additional.module=config.PostTransitionModule run
</pre>

### Feature toggles

The following features can be toggled in [application.conf](conf/application.conf):

| Key                          | Argument type | sbt                                                             | Description                                                                                                                                                                                    |
|------------------------------|---------------|-----------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `trader-test.enabled`        | `Boolean`     | `sbt -Dtrader-test.enabled=true run`                            | If enabled, this will override the behaviour of the "Is this page not working properly?" and "feedback" links. This is so we can receive feedback in the absence of Deskpro in `externaltest`. |
| `countriesOfDeparture`       | `Seq[String]` | `sbt -DcountriesOfDeparture.0=GB countriesOfDeparture.1=XI run` | Controls which countries we fetch offices of departure for. This is so we can run XI only from June 28th before going live with both GB and XI on July 1st.                                    |
| `banners.showUserResearch`   | `Boolean`     | `sbt -Dbanners.showUserResearch=true run`                       | Controls whether or not we show the user research banner.                                                                                                                                      |
| `features.isPreLodgeEnabled` | `Boolean`     | `sbt -Dfeatures.isPreLodgeEnabled=true run`                     | Controls whether or not we ask the user if it is a standard (A) or pre-lodged (D) declaration. If false we default to standard (A).                                                            |
| `play.additional.module`     | `String`      | `sbt -Dplay.additional.module=config.PostTransitionModule run`  | Controls which module (TransitionModule or PostTransitionModule) we bind to the application at start-up.                                                                                       |

To run the application in test-only mode use `sbt -play.http.router=testOnlyDoNotUseInAppConf.Routes` run

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

