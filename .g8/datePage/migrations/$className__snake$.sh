#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.$package$.routes"

if [ ! -f ../conf/app.$package$.routes ]; then
  echo "Write into app.routes file"
  awk '
  /# microservice specific routes/ {
    print;
    print "";
    next;
  }
  /^\$/ {
    if (!printed) {
      printed = 1;
      print "->         /                                              app.$package$.Routes";
      next;
    }
    print;
    next;
  }
  {
    if (!printed) {
      printed = 1;
      print "->         /                                                 app.$package$.Routes";
    }
    print
  }' ../conf/app.routes > tmp && mv tmp ../conf/app.routes
fi

echo "" >> ../conf/app.$package$.routes
echo "GET        /:lrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes
echo "POST       /:lrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes

echo "GET        /:lrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes
echo "POST       /:lrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.title = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.heading = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.hint = For example, 14 1 2020" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.all = Enter the date for $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.two = The date for $title$" must include {0} and {1} >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = The date for $title$ must include {0}" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.invalid = Enter a real date for $title$" >> ../conf/messages.en

if grep -q "protected def localDate" ../app/forms/mappings/Mappings.scala; then
  echo "Function 'localDate' already exists in Mappings. No changes made."
else
  awk '/trait Mappings extends Formatters with Constraints \{/{
      print;
      print "";
      print "  import java.time.LocalDate";
      print "";
      print "  protected def localDate(";
      print "    invalidKey: String,";
      print "    requiredKey: String";
      print "  ): FieldMapping[LocalDate] =";
      print "    of(new LocalDateFormatter(invalidKey, requiredKey))";
      next;
  }
  { print }' ../app/forms/mappings/Mappings.scala > tmp && mv tmp ../app/forms/mappings/Mappings.scala
  echo "Function 'localDate' has been added to Mappings."
fi

echo "Migration $className;format="snake"$ completed"
