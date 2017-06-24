package net.insomniakitten.smarthud.asm;

/*
 *  Copyright 2017 InsomniaKitten
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import java.util.HashMap;

public class ClassNameHashMap extends HashMap<String, String> {

    public ClassNameHashMap(String... s) {
        for (int i = 0; i < s.length / 2; i++)
            put(s[i * 2], s[i * 2 + 1]);
    }

    @Override
    public String put(String key, String value) {
        return super.put("L" + key + ";", "L" + value + ";");

    }

}

