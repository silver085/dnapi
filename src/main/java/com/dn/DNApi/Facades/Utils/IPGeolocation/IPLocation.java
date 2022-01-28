package com.dn.DNApi.Facades.Utils.IPGeolocation;

import java.util.List;

public class IPLocation {
    private String geoname_id;
    private String capital;
    private List<IPLangages> languages;

    public String getGeoname_id() {
        return geoname_id;
    }

    public void setGeoname_id(String geoname_id) {
        this.geoname_id = geoname_id;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public List<IPLangages> getLanguages() {
        return languages;
    }

    public void setLanguages(List<IPLangages> languages) {
        this.languages = languages;
    }
}
