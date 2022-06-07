#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.$package$.routes"

echo "" >> ../conf/app.$package$.routes
echo "GET        /:lrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes
echo "POST       /:lrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes

echo "GET        /:lrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes
echo "POST       /:lrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.title = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.heading = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.addressLine1 = Address line 1" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.addressLine2 = Address line 2" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.postalCode = Postal code" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.country = Country" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en

echo "$package$.$className;format="decap"$.error.postcode.invalid = The postcode of {0}’s address must only include letters a to z, numbers 0 to 9 and spaces" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.postcode.invalidFormat = Enter the postcode of {0}’s address in the right format, like AB1 1AB" >> ../conf/messages.en

echo "$package$.$className;format="decap"$.error.invalid = The {0} of {1}’s address must only include letters a to z without accents, numbers 0 to 9, ampersands (&), apostrophes, at signs (@), forward slashes, full stops, hyphens, question marks and spaces" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = Enter the {0} of {1}’s address" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.length = The {0} of {1}’s address must be 35 characters or less" >> ../conf/messages.en


echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$package;format="space,upper-camel"$$className$UserAnswersEntry: Arbitrary[(pages.$package$.$className$Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[pages.$package$.$className$Page.type#Data].map(Json.toJson(_))";\
    print "      } yield (pages.$package$.$className$Page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary$package;format="space,upper-camel"$$className$UserAnswersEntry.arbitrary ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration $className;format="snake"$ completed"
