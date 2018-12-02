# ReportPortal Katalon Integration
This is an attempt to integrate ReportPortal.io into Katalon Studio.    

## Getting Started
Follow these instructions to get this sample code run on your local machine.  
### Prerequisites
* Has set up a ReportPortal.io server on your own machine.
* Has downloaded and installed the latest version of Katalon Studio.

### Installing
Simply copy the *Keywords* and *Profile* folder into your Katalon project's folder.

### Updating the Katalon profile
Set following values of the ReportPortal profile to new ones using info from your own profile on ReportPortal server.
```
RP_HOST : your ReportPortal API host. Ex : http://127.0.0.1:8080/api/v1  
RP_TOKEN : 'Bearer' + UUID value. Ex : Bearer f974e146-5f90-4912-9332-5b77d7bbd3d8  
RP_NAME : your project's name on ReportPortal.  
```

### Registering the ReportPortal listener
Add the following code snippet into your suites. This will create a launch object named *Sample_Suite* each time the injected suite being executed.
```groovy
@SetUp(skipped = false) // Please change skipped to be false to activate this method.
def setUp() {
	// Put your code here.
	ExecutionEventManager.getInstance().addListenerEventHandle(new ReportPortalListener("Sample_Suite"))
}
```
## Authors
**Minh Ngoc Hoang** - *Core service implementation* - [minhhoang](https://github.com/minhhoangvn)
