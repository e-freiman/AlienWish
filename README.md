# Alien Wish

### Goal ###

The goal of this project is to obtain / refresh Java and Android programming skills through creating simple and useful application. The point is to go through all stages from architecture and coding up to deployment on Google Play

### Idea ###

The idea of the application is to remind your friend / wife / child or even yourself about something that they promised to do. Imagine, you asking your friend to bring you a book and he tells "please, remind me in the evening". It may take years before you get this book. Or you could ask your friend to install AlienWish app on his phone and start it, nothing more. On your phone you open AlienWish, create a request with text "don't forget to take a book", set 8.00 pm and send it to your friend by choosing his name in the phone's address book. In the evening your friend gets notification about the book.

**Key features:**

* Notifications can be triggered not only by time, but also by location. Moreover, location may be set either as fixed coordinates or as keywords which are being searched on the map. Use case example for fixed location is you ask your friend to buy a screwdriver when he happens to be in a household goods shop near his house. Keywords might be used when you don't care in which shop he buy you a screwdriver, he will be getting notifications every time when he is near a suitable shop.
* Users should perform as little action as possible, especially ones who doing a favor. No registration, no other complications
* This app can also be used for setting notifications for yourself, you can imagine that this app is your own reminder where your friends can add events (at least as long as you don't block them)
* There are three main ways to show notifications which are waiting for triggering: on the calendar, on the map and as a list
* Notifications are short simple text that is easy to enter and read on a smartphone

**Distinctions**

* This is not a *ToDo list*. ToDo list have to somehow manage tasks, like changing status to *done* and so on. We don't have this. Notification fires and this is it.
* This is not a *Calendar*. Core functionality of calendar is to show events on a calendar. We also have events that are triggered by location and they can only be shown on a map.
* One interesting user case that is not covered by others so far, is to fire notifiction when you meet somebody. However both shoud have this app istalled.
* *Google Keep* has extremely close functionality to this project. However, it doesn't work on phones that don't have *Google Services* installed. Althogh majority of Android phones has them preinstalled by a manifacurer, some of them (especially Chenese) don't. Also many manifacturers like Blackberry, Samsung (Tizen), Jolla (Jellyfish), UnubtuTouch (not sure) can run Android apps on their systems but without Google Services. It's a big marker that isn't covered by Google. In order to distanciate ourselves from *Google Keep* we are not going to use *Google Services* and our app should work on all these systems
* The big idea behind this project is to make general open source infrastructure for sending / getting notifications. This will have three layers:
  - The first level is the most general - text description of rules of communication between server and clients (or between clients) in order exchange notifications. So far, it's not clear on which existing protocol it's going to be based. This will allow anybody, in any language to write a client and be integrated into our system.
  - The second layer is Java library that implements the protocol from level one and provides functionality for getting / sending notifications. This will allow to implement client easyily on any system that can run JVM.
  - The third layer is client for android which uses level 2 library and since it complitely open, anybody can fork it and customise for their needs

*ToDo list* and *Calendar* functionalities are close to AlienWish app and in the future some incorporation is possible, as well as integration with existing *Calendar* and *ToDo lists* applications.

To avoid confusions: Yes, I am going to have a server for my own money and let anybody to use it, at list while almost nowbody uses it and this is cheap.

### Rules of engagement: ###

* The first rule of this repository is you write elegant code
* The second rule of this repository is you DO WRITE ELEGANT CODE
* If there is a choice between do fast or do properly, you do properly
* Anyone can contribute or observe the project, even the universal evil is welcome
* If someone says "stop" or goes limp, they can postpone their engagement into the project for as long as they wish
* You can commit into the project whatever you want, however talking with colleagues is still a good idea
* Reading / writing *Wiki* and *Issues* pages of the project would be highly appreciated
* No Russian, no other languages, only English
* If this is your first commit into the project you MUST discuss it with other members
