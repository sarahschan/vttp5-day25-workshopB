package sg.edu.nus.iss.paf_day25_wsB.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import sg.edu.nus.iss.paf_day25_wsB.service.SubscriberService;


@Configuration
public class RedisConfig {
    
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Autowired
    private SubscriberService subscriberService;


    public RedisConnectionFactory createConnectionFactory(){
        
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
            config.setDatabase(0);

        if (!redisUsername.equals("") && !redisPassword.equals("")){
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        final JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(config, jedisClient);
        jedisConnectionFactory.afterPropertiesSet();

        return jedisConnectionFactory;
    }


    @Bean("myRedis")
    public RedisTemplate<String, String> redisTemplate(){

        RedisConnectionFactory redisConnectionFactory = createConnectionFactory();
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


    @Bean
    public RedisMessageListenerContainer createMessageListenerContainer() {

        RedisConnectionFactory redisConnectionFactory = createConnectionFactory();
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(redisConnectionFactory);
            container.addMessageListener(subscriberService, ChannelTopic.of("myTopic"));
                                      // consumer/subscriber, topic

        return container;
    }
}
