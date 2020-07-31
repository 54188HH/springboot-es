package com.lzq.springbootes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: springboot-es
 * @description:
 * @author: liuzhenqi
 * @create: 2020-07-31 13:54
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private Long id;
    private Integer age;
}
