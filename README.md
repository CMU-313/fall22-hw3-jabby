![Open in Codespaces](https://classroom.github.com/assets/open-in-codespaces-abfff4d4e15f9e1bd8274d9a39a0befe03a0632bb0f153d0ec72ff541cedbe34.svg)
<h3 align="center">
  <img src="https://teedy.io/img/github-title.png" alt="Teedy" width=500 />
</h3>

[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
![Coverage](https://raw.githubusercontent.com/CMU-313/Teedy/badges/badges/jacoco.svg?token=GHSAT0AAAAAABYAA6NCYJCAXNYCQUUBMWB6YYKLW2Q)

Teedy is an open source, lightweight document management system for individuals and businesses.

![New!](https://teedy.io/img/laptop-demo.png?20180301)

# New Feature: Admission Document Ratings and Analytics Dashboard 

In order to get Teedy installed and running: 

Clone the repository to your local machine (`git clone https://github.com/CMU-313/Teedy`) and then use Maven to build Teedy from source by running the following command from the root directory:

```console
mvn clean -DskipTests install
```

After successfully building Teedy from source, you can launch a Teedy instance by running the following commands from the root directory:

```console
cd docs-web
mvn jetty:run
```

Navigate to ```http://localhost:8080/src``` or ```http://localhost:8080/``` and you should see Teedy running. 

To log in, use admin as both the username and password 

Our feature has 2 major changes, the ability to rate a document (candidate) on three criteria: technical skills, interpersonal skills, and overall fit. 

These can be applied to a document after it is created by clicking on the rate button. 

First, create a new document. Then, click on the rate button found next to the share button: 

![rate_button](https://user-images.githubusercontent.com/56098501/194445324-f7d3a10f-24f9-427d-83e2-591f039e8219.png)

This will open up a form where the 3 ratings can be given on a scale of 1-10:

![form](https://user-images.githubusercontent.com/56098501/194445334-1166ca2f-fe3e-40c0-81a8-5b522f4a613c.png)

After clicking rate, navigate to the analytics tab:

![analytics_tab](https://user-images.githubusercontent.com/56098501/194445368-fb8f630a-d058-4467-a07e-1c97adb0eb42.png)

This will open up a dashboard showing the average ratings given for this candidate across all users logged in to the Teedy system: 

![dashboard_one_review](https://user-images.githubusercontent.com/56098501/194445340-94dd095b-ac53-4f42-9cc5-c1d2bb745588.png)

In order to demonstrate averaging across multiple users. Navigate to the Settings page and click Users. Add a new user. Then, revisit the document first created and go to the Permissions tab. Give the user you created read and **write** permissions. Now, log in as that new user and rate the document. 

You should see that the ratings were averaged accross the two users:

![dashboard_two_reviews](https://user-images.githubusercontent.com/56098501/194445387-fd0d1a4a-29bc-4cd6-9a5c-d5efc063f780.png)

The number of reviewers is a work in progress. We determined that it was not a core functionality of our feature so it will be tackled and iterated upon in  a future sprint. 

Tests for our feature are written in the TestRatingResource.java file. These can be run using ```mvn test``` (to run the full test suite) or ```mvn -Dtest=TestRatingResource test``` (to run just the rating feature tests)

# Features

- Responsive user interface
- Optical character recognition
- LDAP authentication ![New!](https://www.sismics.com/public/img/new.png)
- Support image, PDF, ODT, DOCX, PPTX files
- Video file support
- Flexible search engine with suggestions and highlighting
- Full text search in all supported files
- All [Dublin Core](http://dublincore.org/) metadata
- Custom user-defined metadata ![New!](https://www.sismics.com/public/img/new.png)
- Workflow system ![New!](https://www.sismics.com/public/img/new.png)
- 256-bit AES encryption of stored files
- File versioning ![New!](https://www.sismics.com/public/img/new.png)
- Tag system with nesting
- Import document from email (EML format)
- Automatic inbox scanning and importing
- User/group permission system
- 2-factor authentication
- Hierarchical groups
- Audit log
- Comments
- Storage quota per user
- Document sharing by URL
- RESTful Web API
- Webhooks to trigger external service
- Fully featured Android client
- [Bulk files importer](https://github.com/sismics/docs/tree/master/docs-importer) (single or scan mode)
- Tested to one million documents


# Native Installation

## Requirements

Before building Teedy from source, you will need to install several prerequisites, including Java 11+, Maven 3+, NPM, Grunt, Tesseract 4, ffmpeg, and mediainfo.
We give instructions for installing these prerequisites on several platforms below.

### Linux (Ubuntu 22.04)

```console
sudo apt install \
  default-jdk \
  ffmpeg \
  grunt \
  maven \
  npm \
  tesseract-ocr \
  tesseract-ocr-ara \
  tesseract-ocr-ces \
  tesseract-ocr-chi-sim \
  tesseract-ocr-chi-tra \
  tesseract-ocr-dan \
  tesseract-ocr-deu \
  tesseract-ocr-fin \
  tesseract-ocr-fra \
  tesseract-ocr-heb \
  tesseract-ocr-hin \
  tesseract-ocr-hun \
  tesseract-ocr-ita \
  tesseract-ocr-jpn \
  tesseract-ocr-kor \
  tesseract-ocr-lav \
  tesseract-ocr-nld \
  tesseract-ocr-nor \
  tesseract-ocr-pol \
  tesseract-ocr-por \
  tesseract-ocr-rus \
  tesseract-ocr-spa \
  tesseract-ocr-swe \
  tesseract-ocr-tha \
  tesseract-ocr-tur \
  tesseract-ocr-ukr \
  tesseract-ocr-vie
```

### Mac

```console
brew install \
  ffmpeg \
  grunt-cli \
  maven \
  mediainfo \
  npm \
  openjdk \
  tesseract \
  tesseract-lang
```

### Windows

It is highly recommended that you proceed to install Windows Subsystem Linux (WSL), following the link: [Install Linux on Windows with WSL
](https://docs.microsoft.com/en-us/windows/wsl/install). This will allow you to run a Linux distro (Ubuntu's the default) within the Windows environment, and you can then proceed to follow the Linux (Ubuntu 22.04) instructions to install the dependencies.

**Note**: This would mean that you should proceed to execute the following instructions within the Linux environment as well.

## Installation Steps

Clone the repository to your local machine (`git clone https://github.com/CMU-313/Teedy`) and then use Maven to build Teedy from source by running the following command from the root directory:

```console
mvn clean -DskipTests install
```

After successfully building Teedy from source, you can launch a Teedy instance by running the following commands from the root directory:

```console
cd docs-web
mvn jetty:run
```

**The default admin password is "admin". Don't forget to change it before going to production.**

# License

Teedy is released under the terms of the GPL license. See `COPYING` for more
information or see <http://opensource.org/licenses/GPL-2.0>.
