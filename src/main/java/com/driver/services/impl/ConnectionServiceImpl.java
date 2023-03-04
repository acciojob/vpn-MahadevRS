package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        User user=userRepository2.findById(userId).get();

        if(user.getConnected()) throw new Exception("Already connected");

        String ccode="";
        if(countryName.equalsIgnoreCase("IND")) ccode=CountryName.IND.toCode();
        if(countryName.equalsIgnoreCase("USA")) ccode=CountryName.USA.toCode();
        if(countryName.equalsIgnoreCase("JPN")) ccode=CountryName.JPN.toCode();
        if(countryName.equalsIgnoreCase("AUS")) ccode=CountryName.AUS.toCode();
        if(countryName.equalsIgnoreCase("CHI")) ccode=CountryName.CHI.toCode();

        Country country=user.getOrigionalCountry();
        String code=country.getCode();

        if(code.equals(ccode)) {
            return user;
        }

        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        for(ServiceProvider serviceProvider:serviceProviderList){
            List<Country> countryList=serviceProvider.getCountryList();

            for(Country country1:countryList){
                if(ccode.equals(country1.getCode())){
                    String mastedIp=country1.getCode()+"."+serviceProvider.getId()+"."+userId;
                    user.setMaskedIp(mastedIp);
                    user.setConnected(true);

                    Connection connection=new Connection();
                    connection.setUser(user);
                    connection.setServiceProvider(serviceProvider);
                    connection = connectionRepository2.save(connection);

                    List<Connection> connectionList=user.getConnectionList();
                    connectionList.add(connection);
                    user.setConnectionList(connectionList);

                    return userRepository2.save(user);
                }
            }
        }

        throw new Exception("Unable to connect");


    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user=userRepository2.findById(userId).get();
        if(!user.getConnected()) throw new Exception("Already disconnected");

        user.setConnected(false);
        user.setMaskedIp(null);

        return userRepository2.save(user);
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        User user = userRepository2.findById(senderId).get();
        User user1 = userRepository2.findById(receiverId).get();

        if(user1.getMaskedIp()!=null){
            String str = user1.getMaskedIp();
            String cc = str.substring(0,3); //chopping country code = cc

            if(cc.equals(user.getOrigionalCountry().getCode()))
                return user;
            else {
                String countryName = "";

                if (cc.equalsIgnoreCase(CountryName.IND.toCode()))
                    countryName = CountryName.IND.toString();
                if (cc.equalsIgnoreCase(CountryName.USA.toCode()))
                    countryName = CountryName.USA.toString();
                if (cc.equalsIgnoreCase(CountryName.JPN.toCode()))
                    countryName = CountryName.JPN.toString();
                if (cc.equalsIgnoreCase(CountryName.CHI.toCode()))
                    countryName = CountryName.CHI.toString();
                if (cc.equalsIgnoreCase(CountryName.AUS.toCode()))
                    countryName = CountryName.AUS.toString();

                User user2 = connect(senderId,countryName);
                if (!user2.getConnected()){
                    throw new Exception("Cannot establish communication");

                }
                else return user2;
            }

        }
        else {
            if (user1.getOrigionalCountry().equals(user.getOrigionalCountry())) {
                return user;
            }
            String countryName = user1.getOrigionalCountry().getCountryName().toString();
            User user2 = connect(senderId, countryName);
            if (!user2.getConnected()) {
                throw new Exception("Cannot establish communication");
            } else return user2;
        }
    }

}
