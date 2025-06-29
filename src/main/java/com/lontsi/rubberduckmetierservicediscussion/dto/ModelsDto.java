package com.lontsi.rubberduckmetierservicediscussion.dto;

import com.lontsi.rubberduckmetierservicediscussion.models.type.Model;
import lombok.Getter;

import java.util.List;

@Getter
public class ModelsDto {

    public static final List<Model> freeModel = List.of(Model.MISTRAL, Model.CHATGPT3_5);
    public static final List<Model> premiumModel = List.of(Model.CHATGPT4_1, Model.CLAUDE4);


}
