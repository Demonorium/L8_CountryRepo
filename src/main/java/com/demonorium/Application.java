package com.demonorium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan
@EnableAutoConfiguration
public class Application {

    static class Builder {
        CountryRepo countries;
        RegionRepo regions;
        CityRepo cities;

        public Builder(CountryRepo countries, RegionRepo regions, CityRepo cities) {
            this.countries = countries;
            this.regions = regions;
            this.cities = cities;
        }

        public CountryBuilder addCountry(String name) {
            return new CountryBuilder(name);
        }

        class CountryBuilder {
            Country country;

            CountryBuilder(String name) {
                country = new Country(name);
                countries.save(country);
            }
            public RegionBuilder addRegion(String name) {
                return new RegionBuilder(this, name);
            }

            Builder build() {
                return Builder.this;
            }
        }

        class RegionBuilder {
            Region region;
            CountryBuilder builder;
            RegionBuilder(CountryBuilder country, String name) {
                this.builder = country;
                region = new Region(name, builder.country);
                regions.save(region);
            }
            public RegionBuilder city(String name) {
                City city = new City(name, region);
                cities.save(city);
                return this;
            }
            public CountryBuilder build() {
                return builder;
            }
        }
    }

    public static Builder builder(CountryRepo countries, RegionRepo regions, CityRepo cities) {
        return new Builder(countries, regions, cities);
    }
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class);
        CountryRepo countries = context.getBean(CountryRepo.class);
        RegionRepo regions = context.getBean(RegionRepo.class);
        CityRepo cities = context.getBean(CityRepo.class);


        builder(countries, regions, cities)
                .addCountry("Россия")
                    .addRegion("Вологодская область")
                        .city("Вологда")
                        .city("Череповец")
                        .build()
                    .addRegion("Московская область")
                        .city("Москва")
                        .city("Зеленоград")
                        .build()
                    .build()
                .addCountry("Украина")
                    .addRegion("Киевская область")
                        .city("Киев")
                        .build()
                    .build();
        for (Country country: countries.findAll()) {
            System.out.println(country.getName());
            for (Region region: country.getRegions()) {
                System.out.println("\t>"+region.getName());
                for (City city: region.getCities()) {
                    System.out.println("\t\t>"+city.getName());

                }
            }
        }

        context.close();
    }
}
