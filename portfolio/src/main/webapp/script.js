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
      ['The only version of yourself that you should compare yourself to is past versions of yourself. \n-Allison Raskin',
       'I am going my way and doing my best!',
       "What I did each day would determine the kind of person I'd become. \n-Chris Hadfield"];

  // Pick a random quote.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Add it to the page.
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}

function getGreeting() {
//   fetch('/data').then(response => response.text()).then((greeting) => {
//     document.getElementById('greeting-container').innerHTML = greeting;
//   });

  fetch('/data').then(response => response.json()).then((stats) => {
 
    const statsListElement = document.getElementById('greeting-container');
    statsListElement.innerHTML = '';

    // for each message, display it in a list
    stats.messages.forEach(element => {
        statsListElement.appendChild(
            createListElement(element));
    });

  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

