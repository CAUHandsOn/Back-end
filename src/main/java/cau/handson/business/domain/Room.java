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
public class Room extends BaseTimeEntity {

    @Id
    @Column(nullable = false)
    @Setter
    private String id;

    @Column(nullable = true)
    @Setter
    private String name;

    @Builder
    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
