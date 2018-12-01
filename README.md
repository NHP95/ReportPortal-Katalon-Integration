# ReportPortal-Katalon-Integration
Credit : https://github.com/minhhoangvn

## Usage
1. Set following values to a new one using info from your own ReportPortal server  
**RP_HOST** : ReportPortal host. Ex : http://127.0.0.1:8080/api/v1  
**RP_TOKEN** : UUID value. Ex : Bearer f974e146-5f90-4912-9332-5b77d7bbd3d8  
**RP_NAME** : your project's name on ReportPortal.  

2. Add this code snippet into your suite :  
```groovy
@SetUp(skipped = false) // Please change skipped to be false to activate this method.
def setUp() {
	// Put your code here.
	ExecutionEventManager.getInstance().addListenerEventHandle(new ReportPortalListener("API_Sample_Suite"))
}
```
