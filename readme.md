Overall Architecture
The project is following MVP as only 1 activity was required. The detailed description of the classes follows:

CreditCardFormActivity - Main and only activity, it has a reference to the CreditCardValidation, another to the BackendCaller and it implements the CreditCardPresenter contract

CreditCardValidator - The component isolates all credit card validations in order to have a better visibility for testing (CreditCardValidatorTest is testing it). It has a reference to CreditCardPresenter in order to been able to communicate validation results to the user

CreditCardPresenter - The component is the contract between the core components (not related to UI). In order to allow CreditCardValidator and BackendCaller to interact with the user interface, core components have a reference to their presenter

BackendCaller - The component isolates the comunication with the server, it just simulates the call and callbacking to the presenter to display results

CreditCardModel - The model is just a model, created to hold the information is going to be sended to the server and to hold server responses to gets in future implementations
