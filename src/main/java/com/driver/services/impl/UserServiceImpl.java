package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user=new User();
        if(countryName.equalsIgnoreCase("IND") || countryName.equalsIgnoreCase("USA")|| countryName.equalsIgnoreCase("JPN")|| countryName.equalsIgnoreCase("AUS")|| countryName.equalsIgnoreCase("CHI")) {
            user.setPassword(password);
            user.setUsername(username);
            user.setConnected(false);

            user = userRepository3.save(user);

            int id = user.getId();

            String ccode = "";

            Country country = new Country();

            if (countryName.equalsIgnoreCase("IND")) {
                ccode = CountryName.IND.toCode();
                country.setCode(ccode);
                country.setCountryName(CountryName.IND);

            }
            if (countryName.equalsIgnoreCase("USA")) {
                ccode = CountryName.USA.toCode();
                country.setCode(ccode);
                country.setCountryName(CountryName.USA);
            }
            if (countryName.equalsIgnoreCase("JPN")) {
                ccode = CountryName.JPN.toCode();
                country.setCode(ccode);
                country.setCountryName(CountryName.JPN);
            }
            if (countryName.equalsIgnoreCase("AUS")) {
                ccode = CountryName.AUS.toCode();
                country.setCode(ccode);
                country.setCountryName(CountryName.AUS);
            }
            if (countryName.equalsIgnoreCase("CHI")) {
                ccode = CountryName.CHI.toCode();
                country.setCode(ccode);
                country.setCountryName(CountryName.CHI);
            }

            String ip = ccode + "." + id;
            country.setUser(user);

            user.setOriginalIp(ip);
            user.setOrigionalCountry(country);

            return userRepository3.save(user);
        }
        else {
            throw new Exception("Country not found");
        }

    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {

        User user=userRepository3.findById(userId).get();
        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        serviceProviderList.add(serviceProviderRepository3.findById(serviceProviderId).get());
        user.setServiceProviderList(serviceProviderList);

        ServiceProvider serviceProvider=serviceProviderRepository3.findById(serviceProviderId).get();
        List<User> userList=serviceProvider.getUsers();
        userList.add(user);
        serviceProvider.setUsers(userList);

        return userRepository3.save(user);

    }
}
