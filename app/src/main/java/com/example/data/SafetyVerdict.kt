package com.example.data

enum class SafetyVerdict(
    val shortLabel: String,
    val fullTitle: String,
    val subtitle: String
) {
    SAFE(
        shortLabel = "Safe",
        fullTitle = "Safe to Feed",
        subtitle = "Wholesome snack or treat in sensible portions"
    ),
    PREP_REQUIRED(
        shortLabel = "Prep",
        fullTitle = "Prep Required",
        subtitle = "Safe only when prepared properly or strictly portioned"
    ),
    AVOID(
        shortLabel = "Avoid",
        fullTitle = "Not Recommended",
        subtitle = "Not acutely toxic, but causes egg taint or digestive issues"
    ),
    DANGEROUS(
        shortLabel = "Danger",
        fullTitle = "Toxic / Dangerous",
        subtitle = "Do not feed — can cause severe illness or fatality"
    )
}
