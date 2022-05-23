#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:lrn/$package;format="packaged"$/$className;format="decap"$                        controllers.$package$.$className$Controller.onPageLoad(lrn: LocalReferenceNumber)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en

echo "Migration $className;format="snake"$ completed"
