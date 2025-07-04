package com.gymcrm.workloadservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Document(collection = "trainerWorkloads")
public class TrainerWorkloadDocument {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String firstName;
    private String lastName;
    private Boolean status;
    private HashMap<Integer, HashMap<Integer, Long>> years = new HashMap<>();
}
