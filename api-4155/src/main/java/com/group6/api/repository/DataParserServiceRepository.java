package com.group6.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.group6.api.models.UsersPoint;

@Repository
public interface DataParserServiceRepository extends CrudRepository<UsersPoint, Integer> {

}
