package com.google.sps.data;

public final class Comment {

  private final long id;
  private final String name;
  private final long timestamp;
  private final String text;
  private final double sentimentScore;

  public Comment(long id, String name, long timestamp, String text, double sentimentScore) {
    this.id = id;
    this.name = name;
    this.timestamp = timestamp;
    this.text = text;
    this.sentimentScore = sentimentScore;
  }
}