#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:lrn/$package$/$className;format="decap"$                        controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /:lrn/$package$/$className;format="decap"$                        controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /:lrn/$package$/change$className$                  controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /:lrn/$package$/change$className$                  controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.en

echo "$className;format="decap"$.error.postcode.required = Enter the postcode of {0}’s address" >> ../conf/messages.en
echo "$className;format="decap"$.error.postcode.length = Postcode must be 9 characters or less" >> ../conf/messages.en
echo "$className;format="decap"$.error.postcode.invalid = Enter a real postcode" >> ../conf/messages.en
echo "$className;format="decap"$.error.postcode.invalidFormat = Enter the postcode of {0}’s address in the right format, like AB1 1AB" >> ../conf/messages.en

echo "$className;format="decap"$.error.invalid = The {0} of {1}’s address must only include letters a to z without accents, numbers 0 to 9, ampersands (&), apostrophes, asterisks(*), forward slashes, full stops, hyphens, percent signs, question marks and spaces" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Enter the {0} of {1}’s address" >> ../conf/messages.en
echo "$className;format="decap"$.error.length = The {0} of {1}’s address must be 35 characters or less" >> ../conf/messages.en


echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$UserAnswersEntry: Arbitrary[(pages.$package$.$className$Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[pages.$package$.$className$Page.type#Data].map(Json.toJson(_))";\
    print "      } yield (pages.$package$.$className$Page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary$className$UserAnswersEntry.arbitrary ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration $className;format="snake"$ completed"
