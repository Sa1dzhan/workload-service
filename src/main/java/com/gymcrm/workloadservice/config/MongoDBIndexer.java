package com.gymcrm.workloadservice.config;

import com.gymcrm.workloadservice.entity.TrainerWorkload;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoDBIndexer {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void createIndexes() {
        // compound index = {firstName, lastName}
        mongoTemplate.indexOps(TrainerWorkload.class)
                .ensureIndex(new Index()
                        .on("firstName", Sort.Direction.ASC)
                        .on("lastName", Sort.Direction.ASC)
                        .named("firstName_lastName_idx"));

        // index on username
        mongoTemplate.indexOps(TrainerWorkload.class)
                .ensureIndex(new Index()
                        .on("username", Sort.Direction.ASC)
                        .unique()
                        .named("username_idx"));
    }
}
