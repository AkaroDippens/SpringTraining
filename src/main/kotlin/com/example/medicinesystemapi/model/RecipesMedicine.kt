package com.example.medicinesystemapi.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "recipes_medicines")
class RecipesMedicine {
    @EmbeddedId
    var id: RecipesMedicineId? = null

    @MapsId("idRecipe")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_recipe", nullable = false)
    var idRecipe: Recipe? = null

    @MapsId("idMedicine")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_medicine", nullable = false)
    var idMedicine: Medicine? = null

    @Column(name = "usage_method", length = Integer.MAX_VALUE)
    var usageMethod: String? = null
}
