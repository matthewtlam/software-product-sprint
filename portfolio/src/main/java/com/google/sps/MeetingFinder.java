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

package com.google.sps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

public final class MeetingFinder {
  public Collection<TimeRange> query(Collection<Event> events, Meeting request) {
      long duration = request.getDuration();
      Collection<TimeRange> available = Arrays.asList(TimeRange.WHOLE_DAY);

      //Create list of relevant events
      for (Iterator<Event> eventIterator = events.iterator(); eventIterator.hasNext();) {
          Event event = eventIterator.next();

          //Needs to check if at least one person in attendees is in the event
          boolean overlappingAttendees = false;
          Iterator reqAttendees = request.getAttendees().iterator();
         
          while (reqAttendees.hasNext() && !overlappingAttendees){
            if (event.getAttendees().contains(reqAttendees.next())){
              overlappingAttendees = true;
            }
          }
            
          // Removes that time slot from the availability 
          if (overlappingAttendees) {
              Iterator<TimeRange> availableIterator = available.iterator();
              TimeRange eventTime = event.getWhen();
              Collection<TimeRange> newAvailability = new ArrayList<TimeRange>();
              
               while (availableIterator.hasNext()){
                   TimeRange availableTime = availableIterator.next();
                   // Contains case
                   if(availableTime.end() > eventTime.end() && availableTime.start() <= eventTime.start()){
                       newAvailability.add(TimeRange.fromStartDuration(availableTime.start(), eventTime.start() - availableTime.start()));
                       newAvailability.add(TimeRange.fromStartDuration(eventTime.end() , availableTime.end() - eventTime.end()));
                   }
                   else if(availableTime.end() > eventTime.end() && availableTime.start() < eventTime.end()){
                       newAvailability.add(TimeRange.fromStartDuration(eventTime.end(), availableTime.end() - eventTime.end()));
                   }
                   else if(availableTime.end() > eventTime.start() && availableTime.start() <= eventTime.start()) {
                       newAvailability.add(TimeRange.fromStartDuration(availableTime.start(), eventTime.start() - availableTime.start()));
                   }
                   else{
                       newAvailability.add(availableTime);
                   }
               }
               available = newAvailability;
          }
      }

      //check if there is an available time available
      Iterator<TimeRange> availableIterator = available.iterator();
      Collection<TimeRange> newAvailability = new ArrayList<TimeRange>();
      while (availableIterator.hasNext()){
          TimeRange curTimeRange = availableIterator.next();
          if(curTimeRange.duration() >= duration){
              newAvailability.add(curTimeRange);
          }
      }
    return newAvailability;
  }
}
