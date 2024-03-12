package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TermRepository termRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    GradeRepository gradeRepository;

    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(
            @PathVariable("secNo") int secNo) {

        //TODO: Check for instructor status

        List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);

        if(assignments == null){
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "section number is invalid");
        }

        List<AssignmentDTO> dto_list = new ArrayList<>();

        for (Assignment a : assignments) {
            dto_list.add(new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDate(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo()));
        }

        return dto_list;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(@RequestBody AssignmentDTO dto) {

        //TODO: Check for instructor status

        int sectionId = dto.secId();

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));

        Assignment a = new Assignment();

        a.setTitle(dto.title());
        a.setSection(section);
        a.setDueDate(dto.dueDate());

        assignmentRepository.save(a);

        return new AssignmentDTO(a.getAssignmentId(), a.getTitle(), a.getDueDate(),
                a.getSection().getCourse().getCourseId(), a.getSection().getSecId(), a.getSection().getSectionNo());
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {

        //TODO: Check for instructor status

        int assignmentId = dto.id();

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        if (assignment.getSection().getSecId() != dto.secId()) {
            Section section = sectionRepository.findById(dto.secId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
            assignment.setSection(section);
        }

        assignment.setTitle(dto.title());
        assignment.setDueDate(dto.dueDate());

        Assignment updatedAssignment = assignmentRepository.save(assignment);

        return new AssignmentDTO(updatedAssignment.getAssignmentId(), updatedAssignment.getTitle(), updatedAssignment.getDueDate(),
                updatedAssignment.getSection().getCourse().getCourseId(), updatedAssignment.getSection().getSecId(), updatedAssignment.getSection().getSectionNo());
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {


        // TODO
    }

    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {

        //TODO: Check for instructor status

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        Section section = assignment.getSection();

        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(section.getSectionNo());
        List<GradeDTO> gradesDtoList = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollment.getEnrollmentId(), assignmentId);

            gradesDtoList.add(new GradeDTO(grade.getGradeId(), grade.getEnrollment().getUser().getName(),
                    grade.getEnrollment().getUser().getEmail(), assignment.getTitle(), enrollment.getSection().getCourse().getCourseId(),
                    enrollment.getSection().getSecId(), grade.getScore()));
        }

        return gradesDtoList;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {

        // TODO

        // for each grade in the GradeDTO list, retrieve the grade entity
        // update the score and save the entity

    }



    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
            @RequestParam("studentId") int studentId,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {

        // TODO remove the following line when done

        // return a list of assignments and (if they exist) the assignment grade
        //  for all sections that the student is enrolled for the given year and semester
		//  hint: use the assignment repository method findByStudentIdAndYearAndSemesterOrderByDueDate

        return null;
    }
}
