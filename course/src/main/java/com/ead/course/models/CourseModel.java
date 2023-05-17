package com.ead.course.models;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_COURSES")
public class CourseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID courseId;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 250)
    private String description;
    @Column
    private String imageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = )
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
    private CourseStatus courseStatus;
    private CourseLevel courseLevel;
    private UUID userInstructor;

}
