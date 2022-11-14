package zerobase.project3.model;

import java.util.List;
import lombok.Data;
import zerobase.project3.persist.entity.MemberEntity;

public class Auth {

  @Data
  public static class SignIn { //로그인 할때 사용할 클래스
    private String username;
    private String password;

  }

  @Data
  public static class SignUp { //회원가입 할때 사용할 클래스
    private String username;
    private String password;
    private List<String> roles;

    public MemberEntity toEntity() { //signup class to memberEntity
      return MemberEntity.builder()
          .name(this.username)
          .password(this.password)
          .roles(this.roles)
          .build();
    }

  }

}
