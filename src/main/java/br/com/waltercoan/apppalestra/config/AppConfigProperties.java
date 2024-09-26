package br.com.waltercoan.apppalestra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apppalestra")
public class AppConfigProperties {
    private String storageTable;
    private String tableName;
    private String color;
    private String contentSafeAPI;
    private String featureContentSafe;
    

    public String getFeatureContentSafe() {
        return featureContentSafe;
    }

    public void setFeatureContentSafe(String featureContentSafe) {
        this.featureContentSafe = featureContentSafe;
    }

    public String getContentSafeAPI() {
        return contentSafeAPI;
    }

    public void setContentSafeAPI(String contentSafeAPI) {
        this.contentSafeAPI = contentSafeAPI;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStorageTable() {
        return storageTable;
    }

    public void setStorageTable(String storageTable) {
        this.storageTable = storageTable;
    }

    

    

}
