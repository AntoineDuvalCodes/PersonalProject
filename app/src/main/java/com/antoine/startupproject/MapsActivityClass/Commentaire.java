package com.antoine.startupproject.MapsActivityClass;

/**
 * Created by Antoine on 07/01/2016.
 */
public class Commentaire {

    private String authorPictureUrl;
    private String authorName;
    private String commentaire;
    private int note;
    private String pictureUrl = null;

    public Commentaire(String authorPictureUrl, String authorName, String commentaire, int note){

        this.authorPictureUrl =authorPictureUrl;
        this.authorName = authorName;
        this.commentaire = commentaire;
        this.note = note;

    }

    public Commentaire (String authorPictureUrl, String authorName, String commentaire, int note, String pictureUrl){

        this.authorPictureUrl =authorPictureUrl;
        this.authorName = authorName;
        this.commentaire = commentaire;
        this.note = note;
        this.pictureUrl = pictureUrl;

    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPictureUrl() {
        return authorPictureUrl;
    }

    public void setAuthorPictureUrl(String authorPictureUrl) {
        this.authorPictureUrl = authorPictureUrl;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
