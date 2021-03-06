package com.yunseong.second_project.member.query.dto.model;

import com.yunseong.second_project.member.query.dto.PurchaseResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class PurchaseResponseModel extends EntityModel<PurchaseResponse> {

    public PurchaseResponseModel(PurchaseResponse content, Link... links) {
        super(content, links);
    }
}
