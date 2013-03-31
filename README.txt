README.txt – Glass Line Factory v0
Name: Tim Righettini

----------------
Hello Mr. Grader
----------------

This README doc will elaborate upon the work that I have completed for the Glass Line Factory, version 0.  Unlike the previous restaurant submissions, there really is not a lot to explain this time, so that will make both of our lives easier.  I will explain the information to know behind each requirement as they are stated on the website:

Requirement #1:  The team is to produce an interaction diagram of the glass line including all parts BUT using the v.0 conveyor family idea mentioned in class.

The team interaction diagram is located in both the design document & in the Design folder as a file called “TeamOverallInteractionDiagram _v0.png.”  The latter allows you to zoom into the image and see it in high resolution if you want.

Requirement #2:  EACH student on the team will design the conveyor family by themselves, diagrams plus pseudo-code.

The individual conveyor family interaction diagrams are located within the same places as the team interaction diagrams as previously specified.  The PseudoCode design is located within my v0 design document.  Please note that I made two designs v0 and v0.1, and that I used the latter in my unit tested code, although the former design also works.  I left the former design in just in case I need to fall back to robot agents in the future, else, I will just continue using the workstation paradigm where the popUp communicates directly with the machine agent.

Requirement #3:  v.0 code including full Unit Testing.

To find my unit test code, open up the factory_v0_Tim package, and you will find my agents, interfaces, misc, and test directories there.  To run the tests, do the following:
1.  Open the three test files for the conveyorFamily agents within the test directory: ConveyorTestsCases.java, PopUpTestCases.java, and SensorTestCases.java
2.  Assuming JUnit 4 is installed, all you will have to do is just run the file, and everything should take off from there.

*If for some reason JUnit 4 does not come with my submission, here is how you can add it in:
To fix this, go over one of the red highlights in my test code, and then scroll down to the entry where it says “Fix Project Setup.”  Afterward, you will be prompted to put JUnit 4 into your build path, and then everything should rectify itself.

Other Notes:

Although Professor Wilczynski mentioned that we only had to Unit Test the conveyorFamily agents only and just use a mock transducer, I tried to go above and beyond as much as I could with the time that I had.  

There are two things that I did that went above and beyond and requirements for Unit Testing, and they could possibly be attempts for Extra Credit, if any is being given out.

1.  I used the REAL transducer to test out my agents in Unit Testing, and this was no trivial matter, considering the transducer normally runs threaded.  I also used a MockAnimation to simulate the animation of glass to accompany the transducer.
2.  For some my tests, particularly with the Sensor and PopUp Agents, I attempted to simulate what would happen with the conveyorFamily as a whole when something changed or was modified in one agent instance.  I do not know I did enough in your eyes to merit anything, but I at least want to point that I attempted some v1 type work within this area.
3.  I included a Machine Agent within my factory_v0 package, but I have not tested it yet because it is not directly part of the conveyor Family.

