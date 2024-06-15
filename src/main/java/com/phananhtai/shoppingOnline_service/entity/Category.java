package com.phananhtai.shoppingOnline_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.phananhtai.shoppingOnline_service.utils.OrderUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Categories")
public class Category  implements Serializable {
    @Id
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Active status is required")
    private Boolean active;

    @JsonBackReference
    @OneToMany(mappedBy = "category")
    List<Product> products;

}
