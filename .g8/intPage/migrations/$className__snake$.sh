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
echo "$package$.$className;format="decap"$.error.nonNumeric = Enter your $title$ using numbers" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = Enter your $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.wholeNumber = Enter your $title$ using whole numbers" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.maximum = $title$ must be {0} or less" >> ../conf/messages.en

if grep -q "private[mappings] def intFormatter" ../app/forms/mappings/Formatters.scala; then
  echo "private[mappings] def 'intFormatter' already exists in Formatters. No changes made."
else
  awk '/trait Formatters \{/{
      print;
      print "";
      print "  private[mappings] def intFormatter(requiredKey: String, wholeNumberKey: String, nonNumericKey: String, args: Seq[String] = Seq.empty): Formatter[Int] =";
      print "    import scala.util.control.Exception.nonFatalCatch";
      print "";
      print "    new Formatter[Int] {";
      print "";
      print "      val decimalRegexp = \"\"\"^-?(\\\\d*\\\\.\\\\d*)\$\"\"\"";
      print "";
      print "      private val baseFormatter = stringFormatter(requiredKey, args)(identity)";
      print "";
      print "      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] =";
      print "        baseFormatter";
      print "          .bind(key, data)";
      print "          .map(_.replace(\",\", \"\"))";
      print "          .flatMap {";
      print "            case s if s.matches(decimalRegexp) =>";
      print "              Left(Seq(FormError(key, wholeNumberKey, args)))";
      print "            case s =>";
      print "              nonFatalCatch";
      print "                .either(s.toInt)";
      print "                .left";
      print "                .map(";
      print "                  _ => Seq(FormError(key, nonNumericKey, args))";
      print "                )";
      print "          }";
      print "";
      print "      override def unbind(key: String, value: Int): Map[String, String] =";
      print "        baseFormatter.unbind(key, value.toString)";
      print "    }";
      next;
  }
  { print }' ../app/forms/mappings/Formatters.scala > tmp && mv tmp ../app/forms/mappings/Formatters.scala
  echo "private[mappings] def 'intFormatter' has been added to Formatters."
fi

if grep -q "protected def int" ../app/forms/mappings/Mappings.scala; then
  echo "Function 'int' already exists in Mappings. No changes made."
else
  awk '/trait Mappings extends Formatters with Constraints \{/{
      print;
      print "";
      print "  protected def int(";
      print "    requiredKey: String = \"error.required\",";
      print "    wholeNumberKey: String = \"error.wholeNumber\",";
      print "    nonNumericKey: String = \"error.nonNumeric\",";
      print "    args: Seq[String] = Seq.empty[String]";
      print "  ): FieldMapping[Int] =";
      print "    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))";
      next;
  }
  { print }' ../app/forms/mappings/Mappings.scala > tmp && mv tmp ../app/forms/mappings/Mappings.scala
  echo "Function 'int' has been added to Mappings."
fi

echo "Migration $className;format="snake"$ completed"
