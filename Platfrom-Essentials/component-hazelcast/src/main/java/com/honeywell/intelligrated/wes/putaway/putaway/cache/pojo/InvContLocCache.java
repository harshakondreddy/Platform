package com.honeywell.intelligrated.wes.putaway.putaway.cache.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Composite Cache POJO for Big lots */
public class InvContLocCache implements Serializable {

  private static final long serialVersionUID = 1L;

  private String key;
  private String alias;
  private String warehouse;
  private String area;
  private String locationType;
  private String zone;
  private String aisle;
  private String level;
  private String bay;
  private String positionInt;
  private String depthInt;
  private Boolean isOnline;
  private Boolean isEmpty;
  private LocalDateTime lockDateTime;
  private String lpn;
  private Double measuredDimensionWidth;
  private Double measuredDimensionLength;
  private String itemId;
  private LocalDateTime expireDateTime;
  private String productClassName;
  private String inventoryId;
  private LocalDateTime locUpdatedTimeStamp;
  private LocalDateTime conUpdatedTimeStamp;
  private LocalDateTime invUpdatedTimeStamp;
  private String locationId;
  private String containerId;
  private String baseContainerLocationId;
  private String productClassname;

  public String getLocationId() {
    return locationId;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  public String getContainerId() {
    return containerId;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public String getBaseContainerLocationId() {
    return baseContainerLocationId;
  }

  public void setBaseContainerLocationId(String baseContainerLocationId) {
    this.baseContainerLocationId = baseContainerLocationId;
  }

  public String getProductClassname() {
    return productClassname;
  }

  public void setProductClassname(String productClassname) {
    this.productClassname = productClassname;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(String warehouse) {
    this.warehouse = warehouse;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getLocationType() {
    return locationType;
  }

  public void setLocationType(String locationType) {
    this.locationType = locationType;
  }

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
  }

  public String getAisle() {
    return aisle;
  }

  public void setAisle(String aisle) {
    this.aisle = aisle;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getBay() {
    return bay;
  }

  public void setBay(String bay) {
    this.bay = bay;
  }

  public String getPositionInt() {
    return positionInt;
  }

  public void setPositionInt(String positionInt) {
    this.positionInt = positionInt;
  }

  public String getDepthInt() {
    return depthInt;
  }

  public void setDepthInt(String depthInt) {
    this.depthInt = depthInt;
  }

  public Boolean getOnline() {
    return isOnline;
  }

  public void setOnline(Boolean online) {
    isOnline = online;
  }

  public Boolean getEmpty() {
    return isEmpty;
  }

  public void setEmpty(Boolean empty) {
    isEmpty = empty;
  }

  public LocalDateTime getLockDateTime() {
    return lockDateTime;
  }

  public void setLockDateTime(LocalDateTime lockDateTime) {
    this.lockDateTime = lockDateTime;
  }

  public String getLpn() {
    return lpn;
  }

  public void setLpn(String lpn) {
    this.lpn = lpn;
  }

  public Double getMeasuredDimensionWidth() {
    return measuredDimensionWidth;
  }

  public void setMeasuredDimensionWidth(Double measuredDimensionWidth) {
    this.measuredDimensionWidth = measuredDimensionWidth;
  }

  public Double getMeasuredDimensionLength() {
    return measuredDimensionLength;
  }

  public void setMeasuredDimensionLength(Double measuredDimensionLength) {
    this.measuredDimensionLength = measuredDimensionLength;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public LocalDateTime getExpireDateTime() {
    return expireDateTime;
  }

  public void setExpireDateTime(LocalDateTime expireDateTime) {
    this.expireDateTime = expireDateTime;
  }

  public String getProductClassName() {
    return productClassName;
  }

  public void setProductClassName(String productClassName) {
    this.productClassName = productClassName;
  }

  public String getInventoryId() {
    return inventoryId;
  }

  public void setInventoryId(String inventoryId) {
    this.inventoryId = inventoryId;
  }

  public LocalDateTime getLocUpdatedTimeStamp() {
    return locUpdatedTimeStamp;
  }

  public void setLocUpdatedTimeStamp(LocalDateTime locUpdatedTimeStamp) {
    this.locUpdatedTimeStamp = locUpdatedTimeStamp;
  }

  public LocalDateTime getConUpdatedTimeStamp() {
    return conUpdatedTimeStamp;
  }

  public void setConUpdatedTimeStamp(LocalDateTime conUpdatedTimeStamp) {
    this.conUpdatedTimeStamp = conUpdatedTimeStamp;
  }

  public LocalDateTime getInvUpdatedTimeStamp() {
    return invUpdatedTimeStamp;
  }

  public void setInvUpdatedTimeStamp(LocalDateTime invUpdatedTimeStamp) {
    this.invUpdatedTimeStamp = invUpdatedTimeStamp;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
