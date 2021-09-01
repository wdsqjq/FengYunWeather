package me.wsj.fengyun.bean

/*
* private String cityName;
    private String cityId;
    private String cnty;
    private String location;
    private String parentCity;
    private String adminArea;
    private boolean isFavor;
*
* */
data class CityBean(
    var cityName: String = "",
    var cityId: String = "",
    var cnty: String = "",
    val location: String = "",
    val parentCity: String = "",
    var adminArea: String = "",
    val isFavor: Boolean = false
)