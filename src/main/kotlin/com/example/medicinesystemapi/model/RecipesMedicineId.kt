package com.example.medicinesystemapi.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Embeddable
class RecipesMedicineId : Serializable {
    @NotNull
    @Column(name = "id_recipe", nullable = false)
    var idRecipe: Int? = null

    @NotNull
    @Column(name = "id_medicine", nullable = false)
    var idMedicine: Int? = null

    override fun hashCode(): Int = Objects.hash(idRecipe, idMedicine)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as RecipesMedicineId

        return idRecipe == other.idRecipe &&
            idMedicine == other.idMedicine
    }

    companion object {
        private const val serialVersionUID = -8712693179221889950L
    }
}
