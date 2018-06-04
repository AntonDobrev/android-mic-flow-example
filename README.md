# MICFlow-Android

This application showcases using the Mobile Identity Connect (MIC) authorization-grant login flow from Kinvey's client libraries.  

Mobile Identity Connect is a service that bridges mobile applications with existing enterprise identity and single sign-on solutions. MIC enables mobile applications to integrate with a variety of identity solutions using a single OAuth2-based interface. This allows enterprise application developers to avoid the complexity of integrating these protocols into mobile, while providing enterprise IT the means to ensure that access to resources is secured only to authenticated users, as well as maintaining full control over a mobile user's identity.

##Set up

1. Clone the Repo, or download the zip file and extract it.
2. Download the latest Kinvey library (zip) and extract the downloaded zip file, from: http://devcenter.kinvey.com/android/downloads

###Console
1.  Visit the __User__ Section of the [console](https://console.kinvey.com).
2.  Click the __Auth Source__ tab at the top of the page
3.  Set the `Type of Provider` to be `Custom`
4.  In the `Provider URL`, enter: `https://authlinkdemo.herokuapp.com/authenticate`
5.  In the `Redirect URI's: ` enter: `kinveyAuthDemo://`

The above Auth Provider sample can be used with the username: `test` and the password: `test`.


###Android Studio

1. In Android Studio, go to **File &rarr; New &rarr; Import Project**
2. **Browse** to the extracted zip from step 1, and click **OK**
3. Click **Next** and **Finish**.
4. Copy all jars in the **libs/** folder of the Kinvey Android library zip to the **lib/** folder at the root of the project
5.  Click the **play** button to start a build, if you still see compilation errors ensure the versions are correctly defined in the dependencies list


###Finally, for all IDEs

Specify your app key and secret in `MainActivity` constant variables **APP_KEY** and **APP_SECRET**.  Specify your redirect URI in **redirectURI**, and the username and password you want to use for the automated flow.  


    public class MainActivity extends ActionBarActivity {
	
	    private Client kinveyClient;
    	private static final String APP_KEY = "APP_KEY";
		private static final String APP_SECRET = "APP_SECRET";
		private static final String redirectURI = "REDIRECT_URI";
	
		private static final String USERNAME = "USERNAME";
		private static final String PASSWORD = "PASSWORD";
		
		
In your `AndroidManifest.xml`, specify your redirect URI in all lowercase letters without the trailing characters-- for example, "MyRedirect://" would be entered as "myredirect"

       	<data android:scheme="RedirectURI" />  


Take a look at our [MIC Guide](http://devcenter.kinvey.com/android/guides/mobile-identity-connect#authenticating) for more information.
