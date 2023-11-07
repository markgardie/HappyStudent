package com.example.happystudent.core.data.repository_impl

import com.example.happystudent.core.data.repository.SurveyItemRepository
import com.example.happystudent.core.model.SurveyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TestSurveyItemRepository @Inject constructor(): SurveyItemRepository {

    private val surveyItems = listOf(

        SurveyItem(
        id = 5,
        question = "Наскільки учню цікаво навчатись",
        answers = listOf("Дуже цікаво", "Переважно цікаво", "Цікаві тільки окремі теми", "Зовсім нецікаво"),
        weight = 0.25
         ),

        SurveyItem(
            id = 7,
            question = "Як довго навчається в школі?",
            answers = listOf("Декілька років", "Один рік", "Півроку", "Декілька місяців", "Перший місяць"),
            weight = 0.20
        ),

        SurveyItem(
            id = 8,
            question = "Як довго навчається у вас?",
            answers = listOf("Декілька років", "Один рік", "Півроку", "Декілька місяців", "Перший місяць"),
            weight = 0.18
        ),

        SurveyItem(
            id = 4,
            question = "Який настрій в учня на уроці?",
            answers = listOf("Піднесений, веселий", "Спокійний", "Нудно", "Засмучується, не хоче ходити на уроки"),
            weight = 0.14
        ),

        SurveyItem(
            id = 3,
            question = "Як учень поводить себе на уроці?",
            answers = listOf("Слухняний", "Іноді погана поведінка", "Переважно погана поведінка"),
            weight = 0.11
        ),

        SurveyItem(
            id = 2,
            question = "Як учень працює на уроці?",
            answers = listOf("Завжди активний", "Переважно активний", "Іноді активний", "Зовсім неактивний"),
            weight = 0.07
        ),

        SurveyItem(
            id = 1,
            question = "Як часто учень виконує домашнє завдання?",
            answers = listOf("Постійно", "Регулярно, але не завжди", "Іноді", "Ніколи"),
            weight = 0.04
        ),

        SurveyItem(
            id = 6,
            question = "Стосунки учня з іншими дітьми в групі",
            answers = listOf("Товариські з більшістю групи", "Є декілька друзів", "Нейтральні", "Є невеликі конфлікти", "Присутні серйозні конфлікти"),
            weight = 0.01
        )
    )

    val empty = emptyList<SurveyItem>()


    override fun getSurveyItemsStream(): Flow<List<SurveyItem>> =
        flow {
            emit(empty)
        }
}