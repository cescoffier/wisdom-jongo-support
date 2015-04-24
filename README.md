# Wisdom Jongo Bridge

This project extends the Wisdom Framework Crud Services.

## Installation

Add the following dependency to your `pom.xml` file:

````
<dependency>
    <groupId>org.wisdom-framework.jongo</groupId>
    <artifactId>wisdom-jongo-support</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
````


## Configuration using the `application.conf` file

In the `src/main/configuration/application.conf` file add the following snippet:

````
###
# MongoDB Configuration
###
mongodb {
  test { # Name used to identify the data source. If `datasources` is not set, it will be used
    hostname: localhost
    port: 12345
    dbname: kitten
  }
}
````
see https://github.com/wisdom-framework/wisdom-mongodb for more information on the Wisdom MongoDB plugin.


````
#
# Jongo configuration
#
jongo {
  kitten {  #Database repository
    entities: [ #Names of entities that will be stored inside
      "org.wisdom.jongo.entities.PandaUsingManualLong1",
      "org.wisdom.jongo.entities.PandaUsingAutoObjectId6"
    ]
  }
}
````

## Using
