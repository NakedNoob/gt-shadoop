# Introduction #

The project will consist of several objectives and are as follows:

_Resource procurement:_ The resources we needed to get ahold of was laptops; two of them to be exact. The servers I will discuss later were being hosted at our work (Northrop Grumman) and we needed two laptops so that we could connect to our internal network through our VPN. The next resource we needed to get was to set up Spatial Hadoop as well as GeoServer on the servers in on our internal network. We also need to request access to our network so we can connect to the network through VPN.

_Scale the learning curve:_ Some of the programs and concepts we will need to either better understand or learn are Maven project dependency management and how it works with NetBeans, Spatial Hadoop, GeoServer, and Data Stores.

_Create a Plugin Layer for GeoServer to connect with Spatial Hadoop:_ The Plugin Layer needs to be able to at the very least read from Spatial Hadoop complex geospatial information and if time permits, write to Spatial Hadoop complex geospatial information.
_Create a GUI to illustrate the connection between GeoServer and Spatial Hadoop:_ We will use OpenLayers, which is built into GeoServer, to plot geospatial information on a map.



# Details #

**Project organization**
Our team consists of Samantha Barsell, Gordon Roberts, and Andrew Shearer.  Samantha takes notes for the meetings and will also write code when we start on the coding portion of our project.  Gordon will write code as well, but he is also in charge of tracking problems in the project and tracking the versions of our project.  Andrew is the team leader and will write code along with the other members of the team.  As the team leader, he will break down tasks for himself and the rest of the team and direct the team in a fair, concise, and organized manner. When an issue comes up, we will first try to figure out the answer by oneself, and then we will converse with other members of the team to see if one of the other members has an answer, and lastly we will go to our client to see if he has some insight as to how to resolve our problem. Communication between the team members, and our client, will be a vital part of the success of this project.

**Risk analysis**
Some of the risks that we may encounter are:
●	Not fully understanding the system: Not a technical risk but a risk nonetheless. It is a risk that anyone of us on the team may not fully understand the system and it would hinder production of said system. The likelihood of this occurring is varying because as we learn more about the system, the less likely this will occur. The plan to handle this would be to have whoever does not understand the system be taught by the members who do.
●	Server hosting Spatial Hadoop and GeoServer goes down: The likelihood of this occurring is low and in the event that this does occur there is another server we can host them on. However the time and effort to migrate Spatial Hadoop and GeoServer over to the new server would be costly in terms of productivity.
●	Laptops used for development crash: The likelihood of this occurring are medium to low. The results of this occurring are serious and on the verge of catastrophic because we can only use Northrop Grumman laptops to access the network and if one of our machines went down, that person would be unable to work for the amount of time it took to either fix the machine or get a new machine ordered.In this event, we would oder a new laptop or fix the one we had as quickly as possible.
●	VPN servers go down: The likelihood of this occurring are very low. The result of this occurring would be catastrophic because of the nature of our work and that we are 100% dependent upon having access to the internal network through VPN. There is no backup plan in this situation because it is the only way to reach Spatial Hadoop and GeoServer away from work and we are not allowed to do work on the project while at work so we would have to do whatever we could until the VPN servers came back online.
●	System is too complex to finish on time: The likelihood of this occurring is varying much like the first risk analysed. It is too early to tell whether or not the system we intend to build is too complex to finish in the time allotted. In the event of this occurring, we will get as much of the core requirements completed and consult with our client on how to proceed.

**Hardware and software resource requirements**
Netbeans is the integrated development environment (IDE) that will be used throughout this project.  The project will be also using Maven in the development effort.  Maven integrates with Netbeans easier than with the Eclipse IDE.  Maven provides a standard way to build projects, helps make a clear definition of the contents of a project, makes the build process easier, and tracks dependencies and changes as they are made.

Windows XP Professional is the operating system in which we are developing our project.  The main reason for this is because Northrop Grumman provided the team members with developer laptops in order to connect to their VPN.  It is necessary to connect to the VPN in order to access the Hadoop server that has been set up on their system specifically for this project. It will also be necessary to set up Geoserver in order to implement our project as the middleware in-between the two.  The utilization of GeoTools, which is the open source Java library for geospatial data, will also be a necessary part of this project.


# Work breakdown structure (WBS) #


**Project schedule**
The software developed during this semester will be delivered to the client incrementally.  Our goal for the project deliveries are as follows:
1.	Secure laptops - Week of Monday, September 9
2.	Acquire VPN access for the server at Northrop Grumman - Week of Monday, September 16
3.	Setup Spatial Hadoop - Week of Monday, September 30
4.	Establish GeoServer - Week of Monday, September 30
5.	Setup laptop environments - Week of Monday, September 30
6.	Create Plugin layer - Week of Monday, November 4
7.	Create GUI - Week of Monday, November 25
8.	Deliver finished product - Week of Monday, December 16

# Monitoring and reporting mechanisms #

Document Sharing is accomplished primarily through two mechanisms.  The first is Google Docs, which allows the sharing and collaboration of documents online, as well as editing directly from a browser.  The second mechanism is through the Google Code hosting site.  Documents can be saved as part of the project repository, which also keeps track of the version history.

Google Code is being used to host this project.  Google Code provides tools to track revisions, issue reporting and issue resolution.  We are using Tortoise SVN to implement our subversion system.  It provides an extension of Windows Explorer and allows a GUI interface of all commands.
A mailing list through Google Groups is integrated with Google Code to provide a mechanism for team communication, as well as official alerts and reports from Google Code including issue tracker activity, code reviews, and email notifications of code commits.

