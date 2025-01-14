package sg.edu.nus.iss.paf_day25_wsB.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class ThreadWorker implements Runnable {
    
    @Autowired
    @Qualifier("myRedis")
    RedisTemplate<String, String> template;

    private String name;



    public ThreadWorker(RedisTemplate<String, String> template, String name) {
        this.template = template;
        this.name = name;
    }


    @Override
    public void run() {

        // day 25 slide 10
        ListOperations<String, String> listOps = template.opsForList();
        
        while (true) {
            
            try {
                
                Optional<String> opt = Optional.ofNullable(listOps.rightPop("myQueue", Duration.ofSeconds(30)));
                if (opt.isEmpty()) {
                    continue;
                }
                String payload = opt.get();
                System.out.println(payload);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
