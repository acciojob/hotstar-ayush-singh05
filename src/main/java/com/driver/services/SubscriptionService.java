package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setTotalAmountPaid(subscriptionAmount(subscriptionEntryDto));
        Date date = new Date();
        subscription.setStartSubscriptionDate(date);
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(user);
        user.setSubscription(subscription);
       User save = userRepository.save(user);
        return save.getId();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        if(user.getSubscription().equals(SubscriptionType.ELITE)) {
            throw new Exception("Already the best Subscription");
        }
        Subscription subscription = user.getSubscription();
        int noOfScreen = user.getSubscription().getTotalAmountPaid();
        int elite = 1000 + 350 * user.getSubscription().getNoOfScreensSubscribed();
        subscription.setSubscriptionType(SubscriptionType.ELITE);
        user.setSubscription(subscription);
        userRepository.save(user);
        return elite - noOfScreen;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        int amount = 0;
        List<Subscription> sub = subscriptionRepository.findAll();
        for(Subscription s : sub) {
            amount += s.getTotalAmountPaid();
        }
         return amount;
    }
    public int subscriptionAmount(SubscriptionEntryDto  subscriptionEntryDto) {
        int amount = 0;
        if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)) {
            amount += 500 + 200 * subscriptionEntryDto.getNoOfScreensRequired();
        }else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)) {
            amount += 800 + 250 * subscriptionEntryDto.getNoOfScreensRequired();
        }else {
            amount += 1000 + 350 * subscriptionEntryDto.getNoOfScreensRequired();
        }
       return amount;

    }
}
