
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

e.g.: http://localhost:10120/manage-transit-movements/departure/local-reference-number

### Running Scaffold
The first time you create a scaffold page for a new package you will need to add that app.$package$.routes into prod.routes

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
First you need to ensure you have a related AddressNamePage
<pre>g8Scaffold stringPage
-> package: foo.bar
-> title: Consignee Name
-> className: ConsigneeName
</pre>

Then you create the address page referencing the Name Page
<pre>g8Scaffold addressPage
-> package: foo.bar
-> title: Condignee Address
-> className: ConsigneeAddress
-> addressHolderNamePage: ConsigneeNamePage
</pre>

### Creating an InputSelect Page in Scaffold
This requires that you already have a Service class with a method that will return a list of the reference object that you reqire
That object must also extend Selectable.

For the test an arbitrary constructor of the Selectable object will be required, or you can manually create these objects
<pre>g8Scaffold inputSelectPage
-> package: foo.bar
-> title: Office Of Departure
-> className: OfficeOfDeparture
-> referenceClass = CustomsOffice
...
</pre>

### Creating an Objetc Page in Scaffold
When creating a new View to store a new Object you'll have to go into ModelGenerations and create a method to create a new arbitrary copy of this object
<pre>g8Scaffold objectPage
-> package: foo.bar
-> title: My New Widget
-> className: MyNewWidget
-> objectClassName: Widget
-> formProvider: WidgetFormProvider
...
</pre>
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

