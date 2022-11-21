package cau.handson.business.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @Column(nullable = false)
    @Setter
    private String id;

    @Column(nullable = true, unique = true)
    @Setter
    private String email;

    @Column(nullable = true)
    @Setter
    private String password;

    @Column(nullable = true)
    @Setter
    private String name;

    @Column(nullable = true)
    @Setter
    private String role;

    @Builder
    public User(String id, String email, String name, String role, String password) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
    }

}
