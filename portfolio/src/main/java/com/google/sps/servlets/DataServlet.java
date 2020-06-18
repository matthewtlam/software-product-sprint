// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.util.ArrayList;
import java.lang.String;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
  //ArrayList<String> comments = new ArrayList<String>();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Retrieve comments based on latest timestamp.
      Query query = new Query("Comments").addSort("timestamp", SortDirection.DESCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      
      // Create comment object from the entity data.
      ArrayList<Comment> comments = new ArrayList<>();
      for (Entity entity : results.asIterable()) {
        long id = entity.getKey().getId();
        String name = (String) entity.getProperty("name");
        long timestamp = (long) entity.getProperty("timestamp");
        String text = (String) entity.getProperty("text");
        double sentimentScore = (double) entity.getProperty("sentimentScore");

        Comment comment = new Comment(id, name, timestamp, text, sentimentScore);
        comments.add(comment);
      }

    // Convert ArrayList to Json.
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    
    response.setContentType("text/html;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String name = getParameter(request, "name-input", "");
    String text = getParameter(request, "text-input", "");
    boolean upperCase = Boolean.parseBoolean(getParameter(request, "upper-case", "false"));
    long timestamp = System.currentTimeMillis();

    // Convert the text to upper case.
    if (upperCase) {
      text = text.toUpperCase();
    }

    // Sentiment Analysis of the comment
    Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    double score = sentiment.getScore();
    languageService.close();

    // Store data into entity.
    Entity commentEntity = new Entity("Comments");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("text", text);
    commentEntity.setProperty("sentimentScore", score);

    // Create Datastore and store comment data.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Respond with the result.
    response.setContentType("text/html;");
    response.sendRedirect("/index.html");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
