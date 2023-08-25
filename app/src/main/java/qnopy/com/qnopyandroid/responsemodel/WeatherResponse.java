package qnopy.com.qnopyandroid.responsemodel;

import java.util.ArrayList;

public class WeatherResponse {
    private Current current;

    private Location location;

    private Forecast forecast;

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public static class Current {
        private String feelslike_c;

        private String uv;

        private String last_updated;

        private String feelslike_f;

        private String wind_degree;

        private String last_updated_epoch;

        private String is_day;

        private String precip_in;

        private String wind_dir;

        private String gust_mph;

        private String temp_c;

        private String pressure_in;

        private String gust_kph;

        private String temp_f;

        private String precip_mm;

        private String cloud;

        private String wind_kph;

        private Condition condition;

        private String wind_mph;

        private String vis_km;

        private String humidity;

        private String pressure_mb;

        private String vis_miles;

        public String getFeelslike_c() {
            return feelslike_c;
        }

        public void setFeelslike_c(String feelslike_c) {
            this.feelslike_c = feelslike_c;
        }

        public String getUv() {
            return uv;
        }

        public void setUv(String uv) {
            this.uv = uv;
        }

        public String getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(String last_updated) {
            this.last_updated = last_updated;
        }

        public String getFeelslike_f() {
            return feelslike_f;
        }

        public void setFeelslike_f(String feelslike_f) {
            this.feelslike_f = feelslike_f;
        }

        public String getWind_degree() {
            return wind_degree;
        }

        public void setWind_degree(String wind_degree) {
            this.wind_degree = wind_degree;
        }

        public String getLast_updated_epoch() {
            return last_updated_epoch;
        }

        public void setLast_updated_epoch(String last_updated_epoch) {
            this.last_updated_epoch = last_updated_epoch;
        }

        public String getIs_day() {
            return is_day;
        }

        public void setIs_day(String is_day) {
            this.is_day = is_day;
        }

        public String getPrecip_in() {
            return precip_in;
        }

        public void setPrecip_in(String precip_in) {
            this.precip_in = precip_in;
        }

        public String getWind_dir() {
            return wind_dir;
        }

        public void setWind_dir(String wind_dir) {
            this.wind_dir = wind_dir;
        }

        public String getGust_mph() {
            return gust_mph;
        }

        public void setGust_mph(String gust_mph) {
            this.gust_mph = gust_mph;
        }

        public String getTemp_c() {
            return temp_c;
        }

        public void setTemp_c(String temp_c) {
            this.temp_c = temp_c;
        }

        public String getPressure_in() {
            return pressure_in;
        }

        public void setPressure_in(String pressure_in) {
            this.pressure_in = pressure_in;
        }

        public String getGust_kph() {
            return gust_kph;
        }

        public void setGust_kph(String gust_kph) {
            this.gust_kph = gust_kph;
        }

        public String getTemp_f() {
            return temp_f;
        }

        public void setTemp_f(String temp_f) {
            this.temp_f = temp_f;
        }

        public String getPrecip_mm() {
            return precip_mm;
        }

        public void setPrecip_mm(String precip_mm) {
            this.precip_mm = precip_mm;
        }

        public String getCloud() {
            return cloud;
        }

        public void setCloud(String cloud) {
            this.cloud = cloud;
        }

        public String getWind_kph() {
            return wind_kph;
        }

        public void setWind_kph(String wind_kph) {
            this.wind_kph = wind_kph;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public String getWind_mph() {
            return wind_mph;
        }

        public void setWind_mph(String wind_mph) {
            this.wind_mph = wind_mph;
        }

        public String getVis_km() {
            return vis_km;
        }

        public void setVis_km(String vis_km) {
            this.vis_km = vis_km;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getPressure_mb() {
            return pressure_mb;
        }

        public void setPressure_mb(String pressure_mb) {
            this.pressure_mb = pressure_mb;
        }

        public String getVis_miles() {
            return vis_miles;
        }

        public void setVis_miles(String vis_miles) {
            this.vis_miles = vis_miles;
        }

        public static class Condition {
            private String code;

            private String icon;

            private String text;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }

    public static class Location {
        private String localtime;

        private String country;

        private String localtime_epoch;

        private String name;

        private String lon;

        private String region;

        private String lat;

        private String tz_id;

        public String getLocaltime() {
            return localtime;
        }

        public void setLocaltime(String localtime) {
            this.localtime = localtime;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getLocaltime_epoch() {
            return localtime_epoch;
        }

        public void setLocaltime_epoch(String localtime_epoch) {
            this.localtime_epoch = localtime_epoch;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getTz_id() {
            return tz_id;
        }

        public void setTz_id(String tz_id) {
            this.tz_id = tz_id;
        }
    }

    public static class Forecast {
        private ArrayList<Forecastday> forecastday;

        public ArrayList<Forecastday> getForecastday() {
            return forecastday;
        }

        public void setForecastday(ArrayList<Forecastday> forecastday) {
            this.forecastday = forecastday;
        }

        public static class Forecastday {
            private String date;

            private Astro astro;

            private String date_epoch;

            private Day day;

            private ArrayList<Hour> hour;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public Astro getAstro() {
                return astro;
            }

            public void setAstro(Astro astro) {
                this.astro = astro;
            }

            public String getDate_epoch() {
                return date_epoch;
            }

            public void setDate_epoch(String date_epoch) {
                this.date_epoch = date_epoch;
            }

            public Day getDay() {
                return day;
            }

            public void setDay(Day day) {
                this.day = day;
            }

            public ArrayList<Hour> getHour() {
                return hour;
            }

            public void setHour(ArrayList<Hour> hour) {
                this.hour = hour;
            }

            public static class Astro {
                private String moonset;

                private String sunrise;

                private String sunset;

                private String moonrise;

                public String getMoonset() {
                    return moonset;
                }

                public void setMoonset(String moonset) {
                    this.moonset = moonset;
                }

                public String getSunrise() {
                    return sunrise;
                }

                public void setSunrise(String sunrise) {
                    this.sunrise = sunrise;
                }

                public String getSunset() {
                    return sunset;
                }

                public void setSunset(String sunset) {
                    this.sunset = sunset;
                }

                public String getMoonrise() {
                    return moonrise;
                }

                public void setMoonrise(String moonrise) {
                    this.moonrise = moonrise;
                }
            }

            public static class Day {
                private String avgvis_km;

                private String uv;

                private String avgtemp_f;

                private String avgtemp_c;

                private String daily_chance_of_snow;

                private String maxtemp_c;

                private String maxtemp_f;

                private String mintemp_c;

                private String avgvis_miles;

                private String daily_will_it_rain;

                private String mintemp_f;

                private String totalprecip_in;

                private String avghumidity;

                private Current.Condition condition;

                private String maxwind_kph;

                private String maxwind_mph;

                private String daily_chance_of_rain;

                private String totalprecip_mm;

                private String daily_will_it_snow;

                public String getAvgvis_km() {
                    return avgvis_km;
                }

                public void setAvgvis_km(String avgvis_km) {
                    this.avgvis_km = avgvis_km;
                }

                public String getUv() {
                    return uv;
                }

                public void setUv(String uv) {
                    this.uv = uv;
                }

                public String getAvgtemp_f() {
                    return avgtemp_f;
                }

                public void setAvgtemp_f(String avgtemp_f) {
                    this.avgtemp_f = avgtemp_f;
                }

                public String getAvgtemp_c() {
                    return avgtemp_c;
                }

                public void setAvgtemp_c(String avgtemp_c) {
                    this.avgtemp_c = avgtemp_c;
                }

                public String getDaily_chance_of_snow() {
                    return daily_chance_of_snow;
                }

                public void setDaily_chance_of_snow(String daily_chance_of_snow) {
                    this.daily_chance_of_snow = daily_chance_of_snow;
                }

                public String getMaxtemp_c() {
                    return maxtemp_c;
                }

                public void setMaxtemp_c(String maxtemp_c) {
                    this.maxtemp_c = maxtemp_c;
                }

                public String getMaxtemp_f() {
                    return maxtemp_f;
                }

                public void setMaxtemp_f(String maxtemp_f) {
                    this.maxtemp_f = maxtemp_f;
                }

                public String getMintemp_c() {
                    return mintemp_c;
                }

                public void setMintemp_c(String mintemp_c) {
                    this.mintemp_c = mintemp_c;
                }

                public String getAvgvis_miles() {
                    return avgvis_miles;
                }

                public void setAvgvis_miles(String avgvis_miles) {
                    this.avgvis_miles = avgvis_miles;
                }

                public String getDaily_will_it_rain() {
                    return daily_will_it_rain;
                }

                public void setDaily_will_it_rain(String daily_will_it_rain) {
                    this.daily_will_it_rain = daily_will_it_rain;
                }

                public String getMintemp_f() {
                    return mintemp_f;
                }

                public void setMintemp_f(String mintemp_f) {
                    this.mintemp_f = mintemp_f;
                }

                public String getTotalprecip_in() {
                    return totalprecip_in;
                }

                public void setTotalprecip_in(String totalprecip_in) {
                    this.totalprecip_in = totalprecip_in;
                }

                public String getAvghumidity() {
                    return avghumidity;
                }

                public void setAvghumidity(String avghumidity) {
                    this.avghumidity = avghumidity;
                }

                public Current.Condition getCondition() {
                    return condition;
                }

                public void setCondition(Current.Condition condition) {
                    this.condition = condition;
                }

                public String getMaxwind_kph() {
                    return maxwind_kph;
                }

                public void setMaxwind_kph(String maxwind_kph) {
                    this.maxwind_kph = maxwind_kph;
                }

                public String getMaxwind_mph() {
                    return maxwind_mph;
                }

                public void setMaxwind_mph(String maxwind_mph) {
                    this.maxwind_mph = maxwind_mph;
                }

                public String getDaily_chance_of_rain() {
                    return daily_chance_of_rain;
                }

                public void setDaily_chance_of_rain(String daily_chance_of_rain) {
                    this.daily_chance_of_rain = daily_chance_of_rain;
                }

                public String getTotalprecip_mm() {
                    return totalprecip_mm;
                }

                public void setTotalprecip_mm(String totalprecip_mm) {
                    this.totalprecip_mm = totalprecip_mm;
                }

                public String getDaily_will_it_snow() {
                    return daily_will_it_snow;
                }

                public void setDaily_will_it_snow(String daily_will_it_snow) {
                    this.daily_will_it_snow = daily_will_it_snow;
                }
            }

            public static class Hour {
                private String feelslike_c;

                private String feelslike_f;

                private String wind_degree;

                private String windchill_f;

                private String windchill_c;

                private String temp_c;

                private String temp_f;

                private String cloud;

                private String wind_kph;

                private String wind_mph;

                private String humidity;

                private String dewpoint_f;

                private String will_it_rain;

                private String heatindex_f;

                private String dewpoint_c;

                private String is_day;

                private String precip_in;

                private String heatindex_c;

                private String wind_dir;

                private String gust_mph;

                private String pressure_in;

                private String chance_of_rain;

                private String gust_kph;

                private String precip_mm;

                private Current.Condition condition;

                private String will_it_snow;

                private String vis_km;

                private String time_epoch;

                private String time;

                private String chance_of_snow;

                private String pressure_mb;

                private String vis_miles;

                public String getFeelslike_c() {
                    return feelslike_c;
                }

                public void setFeelslike_c(String feelslike_c) {
                    this.feelslike_c = feelslike_c;
                }

                public String getFeelslike_f() {
                    return feelslike_f;
                }

                public void setFeelslike_f(String feelslike_f) {
                    this.feelslike_f = feelslike_f;
                }

                public String getWind_degree() {
                    return wind_degree;
                }

                public void setWind_degree(String wind_degree) {
                    this.wind_degree = wind_degree;
                }

                public String getWindchill_f() {
                    return windchill_f;
                }

                public void setWindchill_f(String windchill_f) {
                    this.windchill_f = windchill_f;
                }

                public String getWindchill_c() {
                    return windchill_c;
                }

                public void setWindchill_c(String windchill_c) {
                    this.windchill_c = windchill_c;
                }

                public String getTemp_c() {
                    return temp_c;
                }

                public void setTemp_c(String temp_c) {
                    this.temp_c = temp_c;
                }

                public String getTemp_f() {
                    return temp_f;
                }

                public void setTemp_f(String temp_f) {
                    this.temp_f = temp_f;
                }

                public String getCloud() {
                    return cloud;
                }

                public void setCloud(String cloud) {
                    this.cloud = cloud;
                }

                public String getWind_kph() {
                    return wind_kph;
                }

                public void setWind_kph(String wind_kph) {
                    this.wind_kph = wind_kph;
                }

                public String getWind_mph() {
                    return wind_mph;
                }

                public void setWind_mph(String wind_mph) {
                    this.wind_mph = wind_mph;
                }

                public String getHumidity() {
                    return humidity;
                }

                public void setHumidity(String humidity) {
                    this.humidity = humidity;
                }

                public String getDewpoint_f() {
                    return dewpoint_f;
                }

                public void setDewpoint_f(String dewpoint_f) {
                    this.dewpoint_f = dewpoint_f;
                }

                public String getWill_it_rain() {
                    return will_it_rain;
                }

                public void setWill_it_rain(String will_it_rain) {
                    this.will_it_rain = will_it_rain;
                }

                public String getHeatindex_f() {
                    return heatindex_f;
                }

                public void setHeatindex_f(String heatindex_f) {
                    this.heatindex_f = heatindex_f;
                }

                public String getDewpoint_c() {
                    return dewpoint_c;
                }

                public void setDewpoint_c(String dewpoint_c) {
                    this.dewpoint_c = dewpoint_c;
                }

                public String getIs_day() {
                    return is_day;
                }

                public void setIs_day(String is_day) {
                    this.is_day = is_day;
                }

                public String getPrecip_in() {
                    return precip_in;
                }

                public void setPrecip_in(String precip_in) {
                    this.precip_in = precip_in;
                }

                public String getHeatindex_c() {
                    return heatindex_c;
                }

                public void setHeatindex_c(String heatindex_c) {
                    this.heatindex_c = heatindex_c;
                }

                public String getWind_dir() {
                    return wind_dir;
                }

                public void setWind_dir(String wind_dir) {
                    this.wind_dir = wind_dir;
                }

                public String getGust_mph() {
                    return gust_mph;
                }

                public void setGust_mph(String gust_mph) {
                    this.gust_mph = gust_mph;
                }

                public String getPressure_in() {
                    return pressure_in;
                }

                public void setPressure_in(String pressure_in) {
                    this.pressure_in = pressure_in;
                }

                public String getChance_of_rain() {
                    return chance_of_rain;
                }

                public void setChance_of_rain(String chance_of_rain) {
                    this.chance_of_rain = chance_of_rain;
                }

                public String getGust_kph() {
                    return gust_kph;
                }

                public void setGust_kph(String gust_kph) {
                    this.gust_kph = gust_kph;
                }

                public String getPrecip_mm() {
                    return precip_mm;
                }

                public void setPrecip_mm(String precip_mm) {
                    this.precip_mm = precip_mm;
                }

                public Current.Condition getCondition() {
                    return condition;
                }

                public void setCondition(Current.Condition condition) {
                    this.condition = condition;
                }

                public String getWill_it_snow() {
                    return will_it_snow;
                }

                public void setWill_it_snow(String will_it_snow) {
                    this.will_it_snow = will_it_snow;
                }

                public String getVis_km() {
                    return vis_km;
                }

                public void setVis_km(String vis_km) {
                    this.vis_km = vis_km;
                }

                public String getTime_epoch() {
                    return time_epoch;
                }

                public void setTime_epoch(String time_epoch) {
                    this.time_epoch = time_epoch;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public String getChance_of_snow() {
                    return chance_of_snow;
                }

                public void setChance_of_snow(String chance_of_snow) {
                    this.chance_of_snow = chance_of_snow;
                }

                public String getPressure_mb() {
                    return pressure_mb;
                }

                public void setPressure_mb(String pressure_mb) {
                    this.pressure_mb = pressure_mb;
                }

                public String getVis_miles() {
                    return vis_miles;
                }

                public void setVis_miles(String vis_miles) {
                    this.vis_miles = vis_miles;
                }
            }
        }
    }
}
