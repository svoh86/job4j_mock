package ru.checkdev.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Svistunov Mikhail
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private int id;
    private String name;
    private int total;
    private int topicsSize;
    private int position;
}
