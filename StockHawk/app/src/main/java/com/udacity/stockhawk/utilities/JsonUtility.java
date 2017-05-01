/*
 * Copyright (C) 2017 Julia Mattjus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.stockhawk.utilities;

import com.google.gson.Gson;

/**
 * Utility class for converting to and from JSON
 *
 * @author Julia Mattjus
 */
public class JsonUtility {

    private static final Gson gson = new Gson();

    /**
     * Method converting a JSON string to an object
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * Method converting an object to a JSON stirng
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> String toJson(T type) {
        return gson.toJson(type);
    }
}
