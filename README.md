# SauceNetworkTester

1. Dependencies
    * Install a Java Runtime Environment

2. Sauce Labs Credentials
    * In the terminal, export your Sauce Labs credentials as environmental variables.  This step is optional but is
    required to precisely prove connectability:
    ```
    $ export SAUCE_USERNAME=<your Sauce Labs username>
    $ export SAUCE_ACCESS_KEY=<your Sauce Labs access key>
    $ export TESTOBJECT_API_KEY=<API Key for your mobile website as setup in Sauce Labs RDC>
    ```

### Running Connectability Test
```
cd out/artifacts/SauceNetworkTester_jar
java -jar SauceNetworkTester.jar -d <us|eu> -u SAUCE_USERNAME -a $SAUCE_ACCESS_KEY -t $TESTOBJECT_API_KEY
```

### Resources
##### [Sauce Labs Documentation](https://wiki.saucelabs.com/)
##### [Sauce Labs Firewall and Proxy Configuration Info](https://wiki.saucelabs.com/display/DOCS/Setting+Up+Sauce+Connect+Proxy)

