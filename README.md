# XDropWizard

A jump-start DropWizard Web Application integrating and demonstrating several useful open source projects such as Yank, Sundial (a Quartz fork), Flot, Bootstrap, AngularJS, HSQLDB, XChart, JUnit, etc. Demonstrates how to serve static content, dynamic content loaded into Freemarker templates, using AJAX and more...

![Screenshot of Dashboard](https://raw.githubusercontent.com/timmolter/XDropWizard/master/etc/xdropwizard.png)

## Features

* [x] Uses Dropwizard for web application
* [x] Uses Bootstrap for looks
* [x] Uses AngularJS for MVC
* [x] Uses Sundial for Job scheduling
* [x] Uses Yank for databases
* [x] Uses XChart for Bitmap charts
* [x] Uses Flot for Javascript charts
* [x] Uses Freemarker for dymanic templating
* [x] Demonstrates working Dropwizard setup
* [x] Demonstrates Dropwizard tasks
* [x] Demonstrates Dropwizard halthchecks
* [x] Demonstrates Dropwizard building and deploying
* [x] Java 7 and up
* [x] Apache 2.0 license

## Banner Generator

If you want DropWizard to print out a banner in the console during app startup, you can add a file called `banner.txt` in `src/main/resources`. Use the following link to generate a banner:

http://www.webestools.com/ascii-text-generator-ascii-art-code-online-txt2ascii-text2ascii-maker-free-text-to-ascii-converter.html

## Running in Eclipse

Run `XDropWizardApplication` in Eclipse. Add a program arg: `server xdropwizard.yml`.

## Terminal

    cd ~/path/to/project/XDropWizard

## Build

    mvn clean package

## Run

    $ java -jar target/xdropwizard.jar server xdropwizard.yml

## Test Basics

    http://localhost:9090/service/hello-world
    http://localhost:9090/admin/
    http://localhost:9090/admin/healthcheck

## Run Tasks

    curl -X POST http://localhost:9090/admin/tasks/gc

## Static Content

Serving static content such as images, html, css, javascript and binary files from you XDropWizard Webservice is possible in addition to the normal JSON resources
typical for a webservice. DropWizard names static content as "Assets" and all you need to do is place them on the classpath in a folder called `assets`. In our case
we simply add the `assets` folder to `src/main/resources` and Maven takes care of adding the folder and its contents to the classpath during the build.

Either your service or your static assets can be served from the root path, but not both. The latter is useful when using Dropwizard to back a Javascript application
as is the case with XDropWizard. To enable it, move your service to a sub-URL. Note that all webservice calls will now need `service` at the root of the URL. This only applies to the
non-admin port however.

    http:
      rootPath: /service/*  # Default is /*

Then use an extended AssetsBundle constructor to serve resources in the assets folder from the root path. `index.htm` is served as the default page. You need to include the following
line of code in the `Service` class in the `initialize` method:

    bootstrap.addBundle(new AssetsBundle("/assets/", "/"));

In order to keep the `assets` folder a bit organized, we can add subfolders to it. Our `assets` folder contains `img`, `js`, and `css` subfolders. Our `assets` folder also contains
a special-case file called `index.htm`. By default, DropWizard serves this as the default HTML page.

### Static Content Access

Finally, once DropWizard is running, you can access the static content via the following URLS:

    http://localhost:9090
    http://localhost:9090/img/favicon.png
    http://localhost:9090/img/logo_60.png
    http://localhost:9090/css/main.css
    http://localhost:9090/sample.html

BTW, the HTML file `index.html` contains all the links referenced in this README file providing a nice overview of all the demonstrated functionality. If you run this
DropWizard application as described above, you should be able to click on all the links displayed on `index.html` at the following URL:

    http://localhost:9090

### Another Option

Another approach is to serve all static content from a webserver such as Apache HTTP or nginx, placed in front of the DropWizard instance. This however has the disadvantage
of spreading your app's content over several places, and the configuration and maintenance is more complex. In certain cases it may make sense though. Gary Rowe blogged
about how it can be done with nginx [here](http://gary-rowe.com/agilestack/2013/02/13/an-nginx-config-file-for-dropwizard-with-static-content/).

## Sundial

Sundial is a lightweight Java job scheduling framework.

Integrating [Sundial](https://github.com/timmolter/Sundial) into a DropWizard instance requires minimal setup, and once it's all configured and running,
the scheduling and automatic running of jobs is straight forward and stable. For those not familiar with Sundial, it is a simplified fork of [Quartz](http://quartz-scheduler.org/)
developed by Xeiam. A lot of the bloat and confusion of configuring Quartz was removed in creating Sundial and a convenient wrapper around jobs was added to enable
more modular job building and organization. Sundial creates a threadpool on application startup and uses it for background jobs.


## Adding the dropwizard-sundial dependency

Add the **dropwizard-sundial** library as a dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.xeiam</groupId>
    <artifactId>dropwizard-sundial</artifactId>
    <version>0.8.1.0</version>
</dependency>
```


## Create a Job Class

```java
public class SampleJob extends com.xeiam.sundial.Job {

  @Override
  public void doRun() throws JobInterruptException {
    // Do something interesting...
  }
}
```
##  ...with CronTrigger or SimpleTrigger Annotation
```java
@CronTrigger(cron = "0/5 * * * * ?")
```
```java
@SimpleTrigger(repeatInterval = 30, timeUnit = TimeUnit.SECONDS)
```

## Start Sundial Job Scheduler

```java
public static void main(String[] args) {

  SundialJobScheduler.startScheduler("com.xeiam.sundial.jobs"); // package with annotated Jobs
}
```

## Alternatively, Put an XML File Called jobs.xml on Classpath

```xml
<?xml version='1.0' encoding='utf-8'?>
<job-scheduling-data>

    <schedule>

        <!-- job with cron trigger -->
        <job>
            <name>SampleJob3</name>
            <job-class>com.foo.bar.jobs.SampleJob3</job-class>
            <concurrency-allowed>true</concurrency-allowed>
        </job>
        <trigger>
            <cron>
                <name>SampleJob3-Trigger</name>
                <job-name>SampleJob3</job-name>
                <cron-expression>*/15 * * * * ?</cron-expression>
            </cron>
        </trigger>

        <!-- job with simple trigger -->
        <job>
            <name>SampleJob2</name>
            <job-class>com.foo.bar.jobs.SampleJob2</job-class>
            <job-data-map>
                <entry>
                    <key>MyParam</key>
                    <value>42</value>
                </entry>
            </job-data-map>
        </job>
        <trigger>
            <simple>
                <name>SampleJob2-Trigger</name>
                <job-name>SampleJob2</job-name>
                <repeat-count>5</repeat-count>
                <repeat-interval>5000</repeat-interval>
            </simple>
        </trigger>

    </schedule>

</job-scheduling-data>
```


### SampleJob3.java

This job is slightly more complicated and it demonstrates two nice features of Sundial. First it logs the value for myParam which it gets from jobs.xml.
Second it uses a `JobAction` and passes it a parameter via the `JobContext`. Using `JobAction`s is a good way to reuse common job actions across many different
jobs, mixing and matching if desired. This keeps your jobs organized.

```java
    public class SampleJob3 extends Job {

      private final Logger logger = LoggerFactory.getLogger(SampleJob3.class);

      @Override
      public void doRun() throws JobInterruptException {

        JobContext context = getJobContext();

        String valueAsString = context.get("MyParam");
        logger.info("valueAsString = " + valueAsString);

        Integer valueAsInt = Integer.valueOf(valueAsString);
        logger.info("valueAsInt = " + valueAsInt);

        context.put("MyValue", new Integer(123));

        new SampleJobAction().run();

      }
    }
```

In your `*.yml` DropWizard configuration file, you can easily set some helpful parameters to customize Sundial as DropWizard starts up, right from the config file:

```yml
sundial:

    thread-pool-size: 5
    shutdown-on-unload: true
    wait-on-shutdown: false
    start-delay-seconds: 0
    start-scheduler-on-load: true
    global-lock-on-load: false
    annotated-jobs-package-name: com.foo.bar.jobs
```

### Sundial Asynchronous Control via HTTP

By defining some tasks and hooking them into DropWizard you can asynchronously trigger your jobs and/or put a global lock and unlock on the Sundial scheduler.

    curl -X POST http://localhost:9090/admin/tasks/locksundialscheduler
    curl -X POST http://localhost:9090/admin/tasks/unlocksundialscheduler
    curl -X POST http://localhost:9090/admin/tasks/samplejob3?MyParam=56789
    curl -X POST "http://localhost:9090/admin/tasks/startjob?JOB_NAME=MyJob"
    curl -X POST "http://localhost:9090/admin/tasks/startjob?JOB_NAME=SampleJob3&MyParam=9999"
    curl -X POST "http://localhost:9090/admin/tasks/stopjob?JOB_NAME=SampleJob3"
    curl -X POST "http://localhost:9090/admin/tasks/removejob?JOB_NAME=SampleJob3"
    curl -X POST "http://localhost:9090/admin/tasks/addjob?JOB_NAME=SampleJob3&JOB_CLASS=com.xeiam.xdropwizard.jobs.SampleJob3&MyParam=888"
    curl -X POST http://localhost:9090/admin/tasks/removejobtrigger?TRIGGER_NAME=SampleJob3-Trigger
    curl -X POST "http://localhost:9090/admin/tasks/addcronjobtrigger?TRIGGER_NAME=SampleJob3-Trigger&JOB_NAME=SampleJob3&CRON_EXPRESSION=0/45%20*%20*%20*%20*%20?"
    curl -X POST "http://localhost:9090/admin/tasks/addcronjobtrigger?TRIGGER_NAME=SampleJob3-Trigger&JOB_NAME=SampleJob3" --data-urlencode "CRON_EXPRESSION=0/45 * * * * ?"

## Yank

Yank is a very easy-to-use yet flexible Java persistence layer for JDBC-compatible databases build on top of
[org.apache.DBUtils](http://commons.apache.org/dbutils/). Usage is very simple: define DB connectivity properties, create a DAO and POJO class,
and execute queries.

Integrating Yank into DropWizard requires just a minimum of setup.

### DB.properties

The `DB.properties` file should be a familiar sight for people used to working with JDBC-compatible databases such as MySQL, HSQLDB, Oracle, and Postgres.
Put a file called `DB.properties` on your classpath. See `DB.properties` in `src/main/resources`. In this file, you define the properties needed to connect to your
database such as the JDBC driver class name, the user and password. Yank will load this file at startup and handle connecting to the database.

### SQL.properties

Put a file called `SQL.properties` on your classpath. See `SQL.properties` in `src/main/resources`. The `SQL.properties` file is a place to centrally store your
SQL statements. There are a few advantages to this. First, all your statements are found at a single place so you can see tham all at once. Secondly, if you want
to switch your underlying database you'll need to rewrite all your SQL statements. If you have a `SQL.properties` file, you can just create a second one for the new
database and easily make the transition. Of course, you can write all your SQL statements in the Java DAO classes directly as well.

### Book.java

Yank requires that you have a single POJO for each table in your database. The POJO's fields should match the column names and data types of the matching table.
Add the getter and setters as well.

```java
    public class Book {

      private String title;
      private String author;
      private double price;

      /** Pro-tip: In Eclipse, generate all getters and setters after defining class fields: Right-click --> Source --> Generate Getters and Setters... */

      public String getTitle() {
        return title;
      }

      public void setTitle(String title) {
        this.title = title;
      }

      public String getAuthor() {
        return author;
      }

      public void setAuthor(String author) {
        this.author = author;
      }

      public double getPrice() {
        return price;
      }

      public void setPrice(double price) {
        this.price = price;
      }

    }
```

### BooksDAO.java

It is not required by Yank, but it really helps to organize your persistence layer code to have one DAO class for each table. The DAO class is just a collection
of public static methods that each interact with Yank's `DBProxy` class. Note that in some of the following methods, the SQL statements are written directly as a
String, while others come from the `SQL.properties` file on the classpath. The presence of the word `key` in the `DBProxy` method indicates that the SQL
statement is being fetched from the `SQL.properties`.

```java
    public class BooksDAO {

      public static int createBooksTable() {

        String sqlKey = "BOOKS_CREATE_TABLE";
        return DBProxy.executeSQLKey("myconnectionpoolname", sqlKey, null);
      }

      public static int insertBook(Book book) {

        Object[] params = new Object[] { book.getTitle(), book.getAuthor(), book.getPrice() };
        String SQL = "INSERT INTO BOOKS  (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";
        return DBProxy.executeSQL("myconnectionpoolname", SQL, params);
      }

      public static List<Book> selectAllBooks() {

        String SQL = "SELECT * FROM BOOKS";
        return DBProxy.queryObjectListSQL("myconnectionpoolname", SQL, Book.class, null);
      }

      public static Book selectRandomBook() {

        String sqlKey = "BOOKS_SELECT_RANDOM_BOOK";
        return DBProxy.querySingleObjectSQLKey("myconnectionpoolname", sqlKey, Book.class, null);
      }

    }
```

### YankBookResource.java

In order to access objects from the database and return them as JSON, you need a resource class for it. It makes most sense to create a resource class for
each table in your database. Don't forget to add this resource in the `Service` class!

```java
    @Path("book")
    @Produces(MediaType.APPLICATION_JSON)
    public class YankBookResource {

      @GET
      @Path("random")
      public Book getRandomBook() {

        return BooksDAO.selectRandomBook();
      }

      @GET
      @Path("all")
      public List<Book> getAllBooks() {

        return BooksDAO.selectAllBooks();
      }
    }
```

### YankManager.java

`YankManager.java` is the class responsible for setting up `Yank` and it is hooked into DropWizard in the `Service` class by
including the following line of code:

```java
    YankManager ym = new YankManager(configuration.getYankConfiguration()); // A DropWizard Managed Object
    environment.manage(ym); // Assign the management of the object to the Service
    environment.addResource(new YankBookResource());
```

In your `.yml` DropWizard configuration file, you can easily define the database and SQL statement files that Yank uses:

    yank:

        dbPropsFileName: DB.properties
        sqlPropsFileName: SQL.properties

### Yank Database Access

Finally, once DropWizard is running, you can access the JSON objects via the following URLS:

    http://localhost:9090/service/book/random
    http://localhost:9090/service/book/all

## XChart

[XChart](https://github.com/timmolter/XChart) is a light-weight and convenient library for plotting data. We use it in Dropwizard to dynamically create line,
scatter, and bar charts and to provide the resulting bitmaps (PNGs, JPGs, etc.) as URL endpoint resources.

There is no required setup or initialization as in the case with Sundial and Yank. You only need to create a resource for each chart you are providing.

### XChartResource.java

This example XChartResource class creates an XChart `QuickChart` and sends the image as a byte[] using `XChart`'s `BitmapEncoder` class. Don't forget to add this resource in the `Service` class!

```java
    @Path("xchart")
    public class XChartResource {

      @GET
      @Path("random.png")
      @Produces("image/png")
      public Response getRandomLineChart() throws IOException {

        Chart chart = QuickChart.getChart("XChart Sample - Random Walk", "X", "Y", null, null, getRandomWalk(105));

        return Response.ok().type("image/png").entity(BitmapEncoder.getPNGBytes(chart)).build();
      }

      private double[] getRandomWalk(int numPoints) {

        double[] y = new double[numPoints];
        for (int i = 1; i < y.length; i++) {
          y[i] = y[i - 1] + Math.random() - .5;
        }
        return y;
      }

    }
```

### XChart Image Access

Finally, once DropWizard is running, you can access the XChart plots as PNGs via the following URL:

    http://localhost:9090/service/xchart/random.png
    http://localhost:9090/xchart.html

## Dynamic HTML Pages

Dynamic HTML pages in DropWizard are referred to as "Views". These are like dynamic web pages produced by php or jsp/Servlets. Before adding Views to DropWizard
, you need to include the following line of code in the `Service` class in the `initialize` method:

    bootstrap.addBundle(new ViewBundle());

You'll also need to add the `dropwizard-views` dependency to the pom.xml file:

```xml
    <dependency>
        <groupId>com.yammer.dropwizard</groupId>
        <artifactId>dropwizard-views</artifactId>
        <version>whatever</version>
    </dependency>
```

### View Resource

Just as we need a `Resource` class for JSON endpoints, a `Resource` class is needed for "Views" too.

```java
    @Path("view/book")
    @Produces(MediaType.TEXT_HTML)
    public class ViewBookResource {

      @GET
      @Timed
      @CacheControl(noCache = true)
      public BookView bookView() {

        return new BookView();
      }

    }
```

Don't forget to add this resource in the `Service` class!

### View Class

The view class provides both the freemaker template and the dynamic data for the page. Here a `Book` object is hardcoded,
but it could easily come from a database. Any URL parameters can be passed from the `Resource` to the `View` via its constructor.

```java
    public class BookView extends View {

      public BookView() {

        super("ftl/book.ftl");
      }

      public Book getBook() {

        Book book = new Book();
        book.setTitle("Cryptonomicon");
        book.setAuthor("Neal Stephenson");
        book.setPrice(23.99);
        return book;
      }

    }
```

### FTL - FreeMarker Template

`book.ftl` is the path of the template relative to the class name. If this class was `com.xeiam.xdropwizard.views.PersonView`, Dropwizard
would then look for the file src/main/resources/com/xeiam/xdropwizard/views/person.ftl.

```html
    <!DOCTYPE html>
    <html lang="en">
    <head>
    <#include "../includes/head.ftl">
    </head>

    <body>

    <div>

        <#include "../includes/header.ftl">

        <div id="markdown">
        <table>
        <tr>
        <td>Book Title:</td><td>${book.title}</td></tr>
        <tr><td>Book Author:</td><td>${book.author}</td></tr>
        <tr><td>Book Price:</td><td>${book.price}</td></tr>
        </table>
        </div>

        <#include "../includes/footer.ftl">

    </div>

    <#include "../includes/cdn-scripts.ftl">

    </body>
    </html>
```

Notice the `#include` sections. This allows you to set up common page elements, thus avaoiding copy and pasting header, footer and script sections in all your FTLs.

### Book View Access

Since the view is a `Resource`, we need to include `service` in the URL:

    http://localhost:9090/service/view/book

## AJAX

For pseudo-real-time updates to an HTML page, AJAX comes in handy. Adding AJAX to a web application requires 2 compnents:

1. an HTML page with a JQuery AJAX query
1. a backend JSON endpoint

### AJAX HTML page (ajax.html)

This page uses JQuery to fetch JSON, update contents of the `numberplaceholder` span, and repeat every one second.
```html
    <html>
      <head>
        <title>Sample AJAX HTML Page</title>
      </head>
      <body>

        <h1>Sample AJAX HTML Page</h1>
        <p>This is a sample html page demonstrating AJAX.</p>

        <div class="github">
            Random Number from Server: <span id="numberplaceholder">&nbsp;</span>
        </div>

        <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
        <script>
            $(function() {

                dataUpdate();
            });

            function dataUpdate() {

                $.getJSON("/service/random", function(json) {

                    $('#numberplaceholder').html(json.number);
                    setTimeout('dataUpdate()', 1000);

                }).error(function() {
                    console.log("errorfetching JSON asynchronously!");
                });
            }
        </script>
      </body>
    </html>
```
### Backend JSON endpoint (RandomNumberResource.java)
```java
@Path("random")
@Produces(MediaType.APPLICATION_JSON)
public class RandomNumberResource {

  @GET
  public RandomNumber getRandom() {

    return new RandomNumber();
  }
}
```

### AJAX Page Access

    http://localhost:9090/ajax.html

## Flot

[Flot](http://www.flotcharts.org/) is a pure JavaScript plotting library for jQuery, with a focus on simple usage, attractive looks and interactive features. It's
wonderful for web-based plots.

### jquery.flot.js

Integrating `flot` into a webapp requires adding the latest `javascript` file(s) from `flot`, which can be grabbed from their [GitHub page](https://github.com/flot/flot/releases).
We place the `jquery.flot.js` file into a `js` folder in `src/main.resources`. There are many extra `flot` js files used to add extra functionaity to flot.

### flot.html

There are just two main things needed to make a flot chart:

1. a link to `jquery.flot.js`
1. the `placeholder` div

```html
    <html>
      <head>
        <title>Sample Flot Page</title>
        <link rel="stylesheet" type="text/css" href="/css/main.css" />
      </head>
      <body>

        <h1>Sample Flot HTML Page</h1>
        <p>This is a sample html page demonstrating Flot.</p>

        <div class="github notFullWidth">
            <div id="placeholder" style="width: 600px; height: 400px; font-size: 14px; line-height: 1.2em;"></div>
        </div>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/jquery.flot.js"></script>
        <script>
            $(function() {

                var d1 = [];
                for (var i = 0; i < 14; i += 0.5) {
                    d1.push([i, Math.sin(i)]);
                }

                $.plot("#placeholder", [ d1 ]);

            });
        </script>
      </body>
    </html>
```

## AngularJS

[AngularJS](https://angularjs.org/) is a Java-script-based thick client providing a model-view-controller framework for web applications.

Integrating `AngularJS` into a webapp requires adding some Javascript files to the generated HTML and the integration of Javascript acting as the controller (in MVC) between the view (HTML) and the model (the JSON webservice).

### books.html
```html
    <html>

    <head>
      <title>Sample AngularJS Page</title>
      <!-- Bootstrap core CSS -->
      <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" rel="stylesheet">

      <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular.js"></script>
      <script src="/js/books_angular.js"></script>
    </head>

    <body ng-app="sampleApp">

      <h1>Sample AngularJS Page</h1>
      <p>This is a sample html page demonstrating AngularJS.</p>


      <div ng-controller="sampleAppController">
        <div class="container-fluid">
          <div class="row">
            <div class="col-sm-3 col-md-2 sidebar">
              <table class="table table-condensed">
                <tr ng-repeat="book in books">
                  <td>{{book.author}}</td>
                  <td>{{book.title}}</td>
                  <td>{{book.price}}</td>
                </tr>
              </table>
            </div>
          </div>
        </div>
      </div>

    </body>

    </html>
```
### books_angular.js
```javascript
angular.module('sampleApp', [])
  .controller('sampleAppController', ['$scope', '$http', function($scope, $http) {

    $scope.books = [];

    $http.get('/service/book/all').success(function(data) {
      console.log(data);
      $scope.books = data;
    }).
    error(function(data, status, headers, config) {
      console.log(data);
    });

  }]);
```

The controller defines a dependency to the `$scope` and the `$http` module. An HTTP GET request to the `/service/book/all` endpoint is carried out with the get method. It returns a $promise object with a success and an error method. Once successful, the JSON data is assigned to $scope.books to make it available in the template in books.html. The 'ng-repeat' creates a multi-rowed table to match the data fetched from the backend.
