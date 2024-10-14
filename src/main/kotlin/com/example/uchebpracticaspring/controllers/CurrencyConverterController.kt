package com.example.uchebpracticaspring.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class CurrencyConverterController {

    val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.9,
        "RUB" to 95.0
    )

    @PostMapping("/convert")
    fun convert(
        model: Model,
        @RequestParam("fromCurrency") fromCurrency: String,
        @RequestParam("toCurrency") toCurrency: String,
        @RequestParam("amount") amount: Double
    ): String {
        val fromCurrencyResult = exchangeRates[fromCurrency] ?: 0.0
        val toCurrencyResult = exchangeRates[toCurrency] ?: 0.0
        val result = amount * (toCurrencyResult / fromCurrencyResult)
        val roundedResult = String.format("%.2f", result)

        model.addAttribute("fromCurrency", fromCurrency)
        model.addAttribute("toCurrency", toCurrency)
        model.addAttribute("amount", amount)
        model.addAttribute("result", roundedResult)

        return "converter"
    }
}