Getting Started With Grendel
============================

1. Install Java 6 and Maven 2
-----------------------------

Grendel requires Java 1.6.0, ideally 1.6.0_17 or newer, and Maven 2, ideally 2.2
or newer.


2. Install Bouncy Castle JCE Provider
-------------------------------------

First, download the
[latest JDK16 release of the Bouncy Castle JCE Provider](http://www.bouncycastle.org/latest_releases.html).
(You'll need version **145** or newer, no IDEA implementation required.)

Second, copy the Bouncy Castle JCE Provider JAR file to the `lib/ext` directory
of your `$JAVA_HOME` directory. On OS X, this looks something like this:

    $ cp bcprov-jdk16-145.jar /System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext

Third, add the Bouncy Castle JCE Provider to the list of allowed JCE providers
by editing the `security/java.security` file in your Java install and adding the
following line:

    security.provider.<n>=org.bouncycastle.jce.provider.BouncyCastleProvider

Where `<n>` is the number greater than the last number in that section.

For example:

     security.provider.1=sun.security.pkcs11.SunPKCS11 ${java.home}/lib/security/sunpkcs11-macosx.cfg
     security.provider.2=sun.security.provider.Sun
     ... etc ...
     security.provider.10=sun.security.smartcardio.SunPCSC
     security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider

For more information, please refer to the
[Bouncy Castle documentation](http://www.bouncycastle.org/specifications.html#install).


3. Build Grendel
----------------

Check out the Grendel source with `git clone git://github.com/wesabe/grendel.git`.

Run `mvn clean package`. This will download all of Grendel's dependencies,
compile it, run all of its units tests, and create a composite JAR file with all
dependencies included.

The JAR file will be in the `target` directory, named `grendel-${VERSION}.jar`.


4. Configure Grendel
--------------------

Grendel requires a single configuration file, usually named
`grendel.properties`. It should look something like this:

    hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
    hibernate.connection.username=${DBUSER}
    hibernate.connection.password=${DBPASSWORD}
    hibernate.connection.url=jdbc:mysql://${DBHOST}:3306/grendel?zeroDateTimeBehavior=convertToNull
    hibernate.c3p0.min_size=10
    hibernate.c3p0.max_size=50
    hibernate.generate_statistics=true

Replace ${DBUSER}, ${DBPASSWORD}, and ${DBHOST} with values appropriate for 
your system.

The `zeroDateTimeBehavior=convertToNull` option may be required to compensate
for MySQL's storage of null `DATETIME` values as all-zero strings.

It's recommended that your properties file be stored so that only the system
user running the Grendel server can access it.

Once you have the properties file in place, create a database and a database
user for Grendel. Create the tables Grendel needs by generating a full database
schema script (see _Run Grendel_, below) and running it. For instance:

    java -jar target/grendel-${VERSION}.jar schema -c grendel.properties > setup-grendel.sql
    mysql -u grendel -p grendel < setup-grendel.sql

Grendel should work with relational databases other than MySQL. Simply use an
appropriate JDBC connection URL and Hibernate dialect in the configuration file:

    hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    ...
    hibernation.connection.url=jdbc:postgres://${DBHOST}/grendel

You'll also need to place the JDBC drivers for your database on Grendel's
classpath when running Grendel:
    
    java -cp postgresql-8.4-701.jdbc4.jar -jar target/grendel-${VERSION}.jar etc.


5. Run Grendel
--------------

For help, simply run:
    
    java -jar target/grendel-${VERSION}.jar

(Replace `${VERSION}` with whatever version you're running.)

To generate a full database schema script, run this:
    
    java -jar target/grendel-${VERSION}.jar schema -c grendel.properties

To generate a migration database schema script, run this:

    java -jar target/grendel-${VERSION}.jar schema --migration -c grendel.properties

To run Grendel as a web service, run this:
    
    java -jar target/grendel-${VERSION}.jar server -c grendel.properties -p 8080

This will run Grendel on port 8080.


6. Read About Grendel's API
---------------------------

For detailed information on how to use Grendel, please read
[the API documentation](http://github.com/wesabe/grendel/blob/master/API.md)
(API.md).