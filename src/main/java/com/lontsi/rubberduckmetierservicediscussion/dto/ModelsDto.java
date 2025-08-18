package com.lontsi.rubberduckmetierservicediscussion.dto;

import com.lontsi.rubberduckmetierservicediscussion.models.type.Model;
import lombok.Getter;

import java.util.List;

@Getter
public class ModelsDto {

    public static final List<Model> freeModel = List.of(Model.QUACK_1o);
    public static final List<Model> advancedModel = List.of(Model.QUACK_2);


}
