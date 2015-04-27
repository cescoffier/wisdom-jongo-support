/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2015 Wisdom Framework
 * %%
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
 * #L%
 */
package org.wisdom.jongo.entities;


import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Entity using the case 5 from http://jongo.org
 * public class Friend {
 *  @Id @ObjectId // auto
 *  private String key;
 * }
 */
public class PandaUsingAutoObjectIdAndId5 {

    @ObjectId // auto
    @Id
    private String key ;
    public int age;
    String name;

    public PandaUsingAutoObjectIdAndId5(int age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * Constructor used by Jongo.
     */
    public PandaUsingAutoObjectIdAndId5() {

    }

    public String getKey() {
        return key;
    }
}