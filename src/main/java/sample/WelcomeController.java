/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2014 Wisdom Framework
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
package sample;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

import java.net.UnknownHostException;


/**
 * Your first Wisdom Controller.
 */
@Controller
public class WelcomeController extends DefaultController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeController.class);
    /**
     * Injects a template named 'welcome'.
     */
    @View("welcome")
    Template welcome;


    /**
     * The action method returning the welcome page. It handles
     * HTTP GET request on the "/" URL.
     *
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "/")
    public Result welcome() throws UnknownHostException {
        LOGGER.info("I am here");
       int b =init();
        return ok(render(welcome, "welcome", b));
    }

    private int init() throws UnknownHostException {
        LOGGER.info("I am init");

        DB db = new MongoClient().getDB("mydb");
         JongoCRUDService<Friend> jc = new JongoCRUDService(Friend.class, db);


        return 0;

    }

}
