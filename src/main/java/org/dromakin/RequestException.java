/*
 * File:     RequestException
 * Package:  org.dromakin
 * Project:  netology_api_calls_homework_1
 *
 * Created by dromakin as 22.01.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.01.22
 */

package org.dromakin;

public class RequestException extends Exception {

    public RequestException(String s) {
        super(s);
    }

    public RequestException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
