package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.AgeRange
import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.model.TipCategory
import com.family.childtracker.domain.repository.ParentingTipRepository
import java.time.Instant
import java.util.UUID

class InitializeParentingTipsUseCase(
    private val repository: ParentingTipRepository
) {
    suspend operator fun invoke() {
        val tips = getSampleTips()
        repository.insertTips(tips)
    }

    private fun getSampleTips(): List<ParentingTip> {
        return listOf(
            // Sleep Tips
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Establish a Bedtime Routine",
                content = "Create a consistent bedtime routine with calming activities like bath time, reading, and gentle music. This helps signal to your child that it's time to sleep.",
                category = TipCategory.SLEEP,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Create a Sleep-Friendly Environment",
                content = "Keep the room dark, quiet, and at a comfortable temperature (65-70°F). Use blackout curtains and white noise machines if needed.",
                category = TipCategory.SLEEP,
                ageRange = AgeRange.INFANT,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Safe Sleep Practices",
                content = "Always place your baby on their back to sleep. Keep the crib free of blankets, pillows, and toys to reduce SIDS risk.",
                category = TipCategory.SLEEP,
                ageRange = AgeRange.NEWBORN,
                createdAt = Instant.now()
            ),

            // Nutrition Tips
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Introducing Solid Foods",
                content = "Start with single-ingredient purees around 6 months. Watch for allergic reactions and introduce new foods one at a time, waiting 3-5 days between each.",
                category = TipCategory.NUTRITION,
                ageRange = AgeRange.INFANT,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Healthy Snack Ideas",
                content = "Offer nutritious snacks like cut fruit, cheese cubes, whole grain crackers, and yogurt. Avoid sugary treats and keep portions appropriate for their age.",
                category = TipCategory.NUTRITION,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Encouraging Vegetable Consumption",
                content = "Make vegetables fun by creating colorful plates, involving kids in cooking, and being a role model. Offer vegetables in different forms - raw, cooked, or blended into smoothies.",
                category = TipCategory.NUTRITION,
                ageRange = AgeRange.PRESCHOOL,
                createdAt = Instant.now()
            ),

            // Development Tips
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Tummy Time for Newborns",
                content = "Start with 3-5 minutes of tummy time several times a day. This strengthens neck, shoulder, and arm muscles essential for crawling and sitting.",
                category = TipCategory.DEVELOPMENT,
                ageRange = AgeRange.NEWBORN,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Encouraging Language Development",
                content = "Talk to your child throughout the day, narrating activities. Read books together daily, sing songs, and respond to their babbling to encourage communication.",
                category = TipCategory.DEVELOPMENT,
                ageRange = AgeRange.INFANT,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Promoting Fine Motor Skills",
                content = "Provide activities like stacking blocks, playing with playdough, coloring, and using child-safe scissors. These activities develop hand-eye coordination and dexterity.",
                category = TipCategory.DEVELOPMENT,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            ),

            // Behavior Tips
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Positive Reinforcement",
                content = "Praise specific behaviors you want to encourage. Instead of 'good job,' say 'I love how you shared your toys with your friend.' This reinforces positive actions.",
                category = TipCategory.BEHAVIOR,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Managing Tantrums",
                content = "Stay calm during tantrums. Acknowledge their feelings, ensure safety, and wait it out. Once calm, discuss what happened and teach better ways to express emotions.",
                category = TipCategory.BEHAVIOR,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Setting Consistent Boundaries",
                content = "Establish clear, age-appropriate rules and stick to them. Consistency helps children feel secure and understand expectations.",
                category = TipCategory.BEHAVIOR,
                ageRange = AgeRange.PRESCHOOL,
                createdAt = Instant.now()
            ),

            // Safety Tips
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Childproofing Your Home",
                content = "Install safety gates, outlet covers, and cabinet locks. Keep small objects, cleaning supplies, and medications out of reach. Get down to your child's level to spot hazards.",
                category = TipCategory.SAFETY,
                ageRange = AgeRange.INFANT,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Car Seat Safety",
                content = "Keep children rear-facing as long as possible (until at least age 2). Ensure the car seat is properly installed and harness straps are snug.",
                category = TipCategory.SAFETY,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Water Safety",
                content = "Never leave children unattended near water, even for a moment. Use life jackets for young children around pools and teach swimming skills early.",
                category = TipCategory.SAFETY,
                ageRange = AgeRange.PRESCHOOL,
                createdAt = Instant.now()
            ),

            // Health Tips
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Vaccination Schedule",
                content = "Follow your pediatrician's recommended vaccination schedule. Vaccines protect against serious diseases and are safe and effective.",
                category = TipCategory.HEALTH,
                ageRange = AgeRange.INFANT,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Recognizing Illness Signs",
                content = "Watch for fever over 100.4°F, unusual lethargy, difficulty breathing, or persistent crying. Trust your instincts and contact your pediatrician when concerned.",
                category = TipCategory.HEALTH,
                ageRange = AgeRange.NEWBORN,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Dental Care for Toddlers",
                content = "Brush teeth twice daily with fluoride toothpaste (rice-grain size for under 3). Schedule first dental visit by age 1 or when first tooth appears.",
                category = TipCategory.HEALTH,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            ),

            // Additional tips for variety
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Screen Time Guidelines",
                content = "Avoid screens for children under 18 months (except video chatting). For ages 2-5, limit to 1 hour per day of high-quality programming. Co-view when possible.",
                category = TipCategory.DEVELOPMENT,
                ageRange = AgeRange.PRESCHOOL,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Building Social Skills",
                content = "Arrange playdates and encourage sharing and turn-taking. Model good social behavior and help children navigate conflicts with peers.",
                category = TipCategory.DEVELOPMENT,
                ageRange = AgeRange.PRESCHOOL,
                createdAt = Instant.now()
            ),
            ParentingTip(
                id = UUID.randomUUID().toString(),
                title = "Potty Training Readiness",
                content = "Look for signs of readiness: staying dry for longer periods, showing interest in the toilet, and communicating needs. Don't rush - every child is different.",
                category = TipCategory.DEVELOPMENT,
                ageRange = AgeRange.TODDLER,
                createdAt = Instant.now()
            )
        )
    }
}
