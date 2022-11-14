package com.fazpass.header_enrichment

import android.util.ArrayMap

internal class DataCarrierUtility {
    companion object {
        fun check(phonePrefix: String, carrierName: String) : Boolean {
            val alias = getCarrierAlias(carrierName.uppercase().split(" ")[0])

            return collection[alias]?.contains(phonePrefix.replaceRange(0..1, "0")) ?: false
        }

        private const val telkomsel = "TELKOMSEL"
        private const val indosat = "INDOSAT"
        private const val xl = "XL"
        private const val three = "THREE"
        private const val axis = "AXIS"
        private const val smartfren = "SMARTFREN"

        private val collection: Map<String, List<String>> = mapOf(
            telkomsel to listOf(
                "0852", "0853", "0811",
                "0812", "0813", "0821",
                "0822", "0851"
            ),
            indosat to listOf(
                "0855", "0856", "0857",
                "0858", "0814", "0815",
                "0816"
            ),
            xl to listOf(
                "0817", "0818", "0819",
                "0859", "0877", "0878"
            ),
            three to listOf(
                "0895", "0896", "0897",
                "0898", "0899"
            ),
            axis to listOf(
                "0813", "0832", "0833",
                "0838"
            ),
            smartfren to listOf(
                "0881", "0882", "0883",
                "0884", "0885", "0886",
                "0887", "0888", "0889"
            )
        )

        private fun getCarrierAlias(carrierName: String) : String = when (carrierName) {
            "AS" -> telkomsel
            "HALO" -> telkomsel
            "SIMPATI" -> telkomsel
            "LOOP" -> telkomsel
            "BY.U" -> telkomsel
            "IM3" -> indosat
            "MENTARI" -> indosat
            "MATRIX" -> indosat
            "EXCELCOMINDO" -> xl
            "EXCL" -> xl
            "3" -> three
            else -> carrierName
        }
    }
}