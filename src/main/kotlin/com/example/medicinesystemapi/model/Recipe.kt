package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import com.example.medicinesystemapi.model.Doctor
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "recipes")
class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipes_id_recipe_seq")
    @SequenceGenerator(name = "recipes_id_recipe_seq", sequenceName = "recipes_id_recipe_seq", allocationSize = 1)
    @Column(name = "id_recipe", nullable = false)
    var id: Int? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_doctor", nullable = false)
    var idDoctor: Doctor? = null
}