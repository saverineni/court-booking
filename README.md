# court-booking
An example project of selenium using ChromeWebDriver, HtmlUnitDriver, FirefoxFriver

mvn clean package -DskipTests=true

ssh -i ec2-ubuntu.pem ubuntu@ec2-13-59-62-120.us-east-2.compute.amazonaws.com
scp -i ec2-ubuntu.pem /Users/suresh.averineni/misc-workspace/court-booking/target/court-booking-1.0-SNAPSHOT-fat-tests.jar   ubuntu@ec2-13-59-62-120.us-east-2.compute.amazonaws.com:~/webdriver/