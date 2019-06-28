# Effects of context and motivation on willingness to perform mobile crowdsourcing tasks
This project aims to find out crucial contextual factors, users' motivation that affect users' willingness to perform mobile crowdsourcing tasks.
For more information, please visit [this website](https://www.armuro.info/research).
<br/>
## Implementation
mobile_crowdsourcing_study_android is extended from [Minuku](https://github.com/minuku/minuku-android), a context-aware tool, which collect contextual factors, such as location, time, current using app,etc.
The app extendeds in terms of detection, collection and questionnaires ( which got inspiration from [this repo](https://github.com/ShashiPrasadKushwaha/Questionnaire)). 
<br/>
### Detection 

When mobile crowdsourcing tasks are performed by users, the app would detect the condition and start to collect contextual data. 
For this project, we only aim to two condition : 
1. using (google Crowdsource app)[https://crowdsource.google.com/]
2. using google map to contribute mobile crowdsourcing tasks. 

### Collection 

There are two type of the contextual data when user is performing mobile crowdsourcing tasks. 
1. phone log : current activity, current location, application usage, battery, accessiblity,etc. 
2. video recording : To collect in-situ data while users are performing the tasks.  

### Questionnaires 

The questionnaires try to collect the ground of the situation, motivation, current activity from users that phone log and video recorder couldn't provide. 

## CI/CD System 
please refer to this [repository](https://github.com/zi3120courses/project-p4-minuku-team.git). The implementation is the final project of "Programming and Development Courses" taught by Professor Ansgar Fehnker in University of Twente.
