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

/**
 * Adds a random quote to the page.
 */
function addRandomQuote() {
  const quotes =
      ["The only version of yourself that you should compare yourself to is past versions of yourself. \n-Allison Raskin",
       "I am going my way and doing my best!",
       "What I did each day would determine the kind of person I'd become. \n-Chris Hadfield"];

  // Pick a random quote.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Add it to the page.
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}

// Retrieves the comments from Datastore.
function getComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
      
    const commentsListElement = document.getElementById('comments-container');
    commentsListElement.innerHTML = '';

    // for each message, display it in a list
    comments.forEach(element => {
        commentsListElement.appendChild(createListElement(element));
    });
  });
}

/** Creates an <li> element containing name, date, text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.className = 'comment';

  const nameElement = document.createElement('span');
  nameElement.innerText = text.name;

  const dateElement = document.createElement('span');
  const date = new Date(text.timestamp);
  const dateTime = " - " + date.toDateString() + " " + date.getHours() + ":" + date.getMinutes();
  dateElement.innerText = dateTime;

  const textElement = document.createElement('p');
  textElement.innerText = text.text;

  /** 
   * The sentimentScore is a float that ranges from [-1.0, 1.0]
   * Note that the SENTIMENT_THRESHOLD of 0.2 is an artibtray value. 
   */
  const sentimentScoreElement = document.createElement('img');
  const SENTIMENT_THRESHOLD = 0.2;
  if (text.sentimentScore > SENTIMENT_THRESHOLD) {
      sentimentScoreElement.src = "/images/smile.png";
  } else if (text.sentimentScore < SENTIMENT_THRESHOLD){
      sentimentScoreElement.src = "/images/sad.png";
  }else{
      sentimentScoreElement.src = "/images/neutral.png";
  }
  sentimentScoreElement.width = 18;

  liElement.appendChild(nameElement);
  liElement.appendChild(sentimentScoreElement);
  liElement.appendChild(dateElement);
  liElement.appendChild(textElement);
  
  return liElement;
}

