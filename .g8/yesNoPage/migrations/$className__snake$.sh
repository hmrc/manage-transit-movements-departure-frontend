#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:lrn/$package;format="word-space,normalize"$/$className;format="decap"$                        controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /:lrn/$package;format="word-space,normalize"$/$className;format="decap"$                        controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /:lrn/$package;format="word-space,normalize"$/change-$className;format="decap"$                controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /:lrn/$package;format="word-space,normalize"$/change-$className;format="decap"$                controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = Select yes if $className;format="decap"$" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$package;format="word-space,upper-camel"$$className$UserAnswersEntry: Arbitrary[(pages.$package$.$className$Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[pages.$package$.$className$Page.type#Data].map(Json.toJson(_))";\
    print "      } yield (pages.$package$.$className$Page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary$package;format="word-space,upper-camel"$$className$UserAnswersEntry.arbitrary ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration $className;format="snake"$ completed"
