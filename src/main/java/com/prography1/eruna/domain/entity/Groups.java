package com.prography1.eruna.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "groupsTable")
public class Groups extends BaseTimeEntity{

    @Column(name="group_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="host_id")
    private User host;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "groups", cascade = CascadeType.ALL)
    private Alarm alarm;

    @OneToMany(fetch = FetchType.LAZY, mappedBy ="groups", cascade = CascadeType.ALL)
    private List<GroupUser> groupUserList = new ArrayList<>();

    @Builder
    public Groups(String code, User host) {
        this.code = code;
        this.host = host;
    }

    public static Groups create(User host){
        String newCode = generateCode();
        return Groups.builder().code(newCode).host(host).build();
    }

    private static String generateCode(){

        int codeLength = 6;

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

        StringBuilder sb = new StringBuilder(codeLength);

        for (int i = 0; i < codeLength; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public void changeCode(){
        code = generateCode();
    }
}

