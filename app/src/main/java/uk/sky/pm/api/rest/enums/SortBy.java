package uk.sky.pm.api.rest.enums;

public enum SortBy {

    DEFAULT("name"),
    RATING("movieRating");

    private String dbColumn;

    SortBy(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public String getDbColumn() {
        return dbColumn;
    }
}
