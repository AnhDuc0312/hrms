package com.hrms.sys.services.course;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.hrms.sys.models.Course;
import com.hrms.sys.repositories.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class CourseService {
    private final Firestore firestore;

    private final String COLLECTION_NAME = "courses";
    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();
        List<Course> courses = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            courses.add(document.toObject(Course.class));
        }
        return courses;
    }

    public Course createCourse(Course course) throws InterruptedException, ExecutionException {
        DocumentReference documentReference = firestore.collection(COLLECTION_NAME).document();
        course.setIdCourse(documentReference.getId());
        ApiFuture<WriteResult> result = documentReference.set(course);
        result.get(); // Wait for the operation to complete
        courseRepository.save(course);
        return course;
    }
}
