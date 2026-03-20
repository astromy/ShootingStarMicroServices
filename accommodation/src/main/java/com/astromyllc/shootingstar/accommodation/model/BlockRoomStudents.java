package com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "block_room_students")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public class BlockRoomStudents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBlockRoomStudent;
    private String studentID;
    private String institutionID;

    @ManyToOne
    @JoinColumn(name = "idBlockRoom")
    private BlockRoom blockRoom;
}
