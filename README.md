# Third Assignment PSS - RnW

## RnW - Read n' Write
Read n' Write is a web application that allows users to post various pieces of writing.
Users can:
- Create texts.
- Change visibility of their own texts (private or public).
- Comment on public texts.
- Delete their own texts.
- Report the texts they can view.
- Change their name and password.
- Delete their own account.

Users flagged as administrator can also:
- Delete other users' accounts.
- Delete other users' texts.
- Change visibility (public/private) of other people's texts.
- Change other users' names.
- Resolve reports.

Texts must:
- Be composed of three macro-sections: introduction, body and conclusion, of which none can be empty.
- Have a title.

To grant some freedom to the end-user, every macro-section can be split into multiple sub-sections.

## Installation
### Prerequisites
Download and install:
- [Spring Tool Suite 4](https://spring.io/tools).
- [MongoDB Community Edition](https://www.mongodb.com/try/download/community).
- [MongoDB Shell](https://www.mongodb.com/try/download/shell).

### Setup
1. Open MongoDB Shell and run the following command `load("[path to directory]/init.js")`.
2. Open Spring Tool Suite.
3. Import this project (via Git or Maven).
4. From Spring Tool Suite, click on the project, select `Project > Properties > Targeted Runtimes`.
5. Add a new runtime, select `Apache > Apache Tomcat v9.0 > Next`.
6. Press **Download and Install** or select the installation path of your existing Apache Tomcat v9.0.
7. Install **Eclipse Enterprise Java and Web Developer Tools** from **Eclipse Marketplace**.
8. Right click on the project, then press `Properties > Project Facets`, enable project facets and ensure that **Dynamic Web Module**, **Java** and **JavaScript** are ticked. The respective versions should be: 4.0, 1.8 and 1.0.
9. Now click on `Project > Properties > Java Compiler`. Tick `Enable project specific settings > Use compliance from execution environment 'JavaSE-1.8' on the 'Java Build Path'`.

### Setup for tests
A JRE should be present in the Spring Tool Suite installation folder. In this section we are going to set it up to run our tests.
1. Click on `Window > Preferences > Installed JREs` and add the JRE that you can find in the STS installation folder. (Or any JRE 21 if you have one).
2. Right click on the project then press `Run As > Run Configurations`.
3. Create a new Maven configuration and set its goals to `'clean verify'`.
4. Go to the JRE tab, set `Runtime JRE` to `Alternate JRE` and choose the JRE 21 installation that you imported before.


## Usage

To start the webapp simply right click on the project, select `Run As > Run on Server`.

To run the tests, select `Run As` and choose the newly added configuration.
