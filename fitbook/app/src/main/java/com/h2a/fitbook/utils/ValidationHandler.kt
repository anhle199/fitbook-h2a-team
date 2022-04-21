package com.h2a.fitbook.utils

import java.text.SimpleDateFormat
import java.util.*

object ValidationHandler {

    fun validateFullName(fullName: String): Boolean {
        return fullName.matches(Regex("[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỂưạảấầẩẫậắằẳẵặẹẻẽềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]{1,50}"))
    }

    fun validateDateOfBirth(dateInString: String, dateFormatPattern: String): Boolean {
        val vnLocale = Locale("vi", "VN")
        val formatter = SimpleDateFormat(dateFormatPattern, vnLocale)

        return try {
            formatter.parse(dateInString) != null
        } catch (e: Exception) {
            false
        }
    }

    /*
    Username:
        - Length of 6 to 20 characters.
        - Must contain at least one letter.
        - Optionally contain digits.
    */
    fun validateUsername(username: String): Boolean {
        return username.matches(Regex("[a-zA-Z\\d]{6,20}"))
    }

    /*
    Password:
       - Length of 6 to 20 characters.
       - Must contain both letters and digits.
    */
    fun validatePassword(password: String): Boolean {
        val containEntireLettersOrDigits = password.matches(Regex("([a-zA-Z]{6,20})|(\\d{6,20})"))
        val allCases = password.matches(Regex("[a-zA-Z\\d]{6,20}"))

        return allCases && !containEntireLettersOrDigits
    }

}
