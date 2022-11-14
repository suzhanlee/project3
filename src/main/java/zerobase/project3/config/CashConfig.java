package zerobase.project3.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class CashConfig {

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() { //레디스를 커넥션함
//    RedisClusterConfiguration 클러스트일 경유
    RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
    conf.setHostName(this.host);
    conf.setPort(this.port);

    return new LettuceConnectionFactory(conf); //lettuce~ 에 설정정보를 넣어서 생성해줌
  }

  @Bean
  public CacheManager redisCashManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
    //serializaion : 테이터나 오브젝트와 같은 값들을 바이트 형태로 변환 (직렬화)
    // 바이트 -> 객체형태 (역 직력화)
    //자바 -> redis(직렬화), redis -> 자바(역 직렬화)
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory).cacheDefaults(conf).build();

  }



}
