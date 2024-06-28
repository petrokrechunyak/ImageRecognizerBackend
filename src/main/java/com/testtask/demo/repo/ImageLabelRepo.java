package com.testtask.demo.repo;

import com.testtask.demo.model.ImageLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ImageLabelRepo extends JpaRepository<ImageLabel, Long> {

    List<ImageLabel> findAllByLabel(String label);
}
