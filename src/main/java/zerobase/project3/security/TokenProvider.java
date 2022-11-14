package zerobase.project3.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zerobase.project3.service.MemberService;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private static final String KEY_ROLES = "roles";
  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour

  private final MemberService memberService;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  public String generateToken(String username, List<String> roles) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put(KEY_ROLES, roles);

    var now = new Date();
    var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiredDate)
        .signWith(SignatureAlgorithm.ES512, this.secretKey)
        .compact();
  }


  private Claims parseClaims(String token) {
    try {
      Claims body = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJwt(token).getBody();
      return body;
    } catch (ExpiredJwtException e) {

      return e.getClaims();
    }
  }

  public String getUserName(String token) {

    return this.parseClaims(token).getSubject();

  }

  public boolean validateToken(String token) {
    if(!StringUtils.hasText(token)) {
      return false;
    }

    var claims = this.parseClaims(token);
    return !claims.getExpiration().before(new Date());
  }


  public Authentication getAuthentication(String jwt) {

    UserDetails userDetails = this.memberService.loadUserByUsername(this.getUserName(jwt));
    return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    //위의 리턴값은 사용자 정보와 사용자 권환정보를 가지고 있음
    // 스프링에서 만든 새로운 토큰임

  }
}
