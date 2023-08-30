# DataIngestion CLI User Guide

## Table of Contents

1. [Introduction](#introduction)

2. [Installation](#installation)

3. [Getting Started](#getting-started)

4. [Command Reference](#command-reference)

    - [register](#register-command)

    - [token](#token-command)

    - [injecthl7](#injecthl7-command)

5. [Troubleshooting](#troubleshooting)

## Introduction <a name="introduction"></a>

The DataIngestion CLI is a command-line tool that allows users to interact with the DataIngestion Service. It provides essential functionalities for onboarding clients, generating JWT tokens, and using the /api/reports endpoint to inject HL7 data for developers. This user guide explains how to install and use the CLI effectively.

## Installation <a name="installation"></a>

The DataIngestion CLI is packaged using GraalVM, and no additional dependencies or prerequisites are required for installation. To install the CLI, follow these steps:

1. Go to the Actions page of this Github Repo - https://github.com/CDCgov/NEDSS-DataIngestion-CLI/actions and click on the latest workflow.
2. In the artifacts section, you'll see three zip files, each one for one operating system *(Windows, Linux and MacOS)*
3. Download the zip package depending on the operating system of your computer. For example, if you're using **Windows Operating System**, then click on ``NEDSS-DataIngestion-CLI-windows.exe.zip``.
4. The download will start automatically. Once the download is finished, unzip it to the location you want.

#### Notes: If you're using MacOS, for the first time, you need to control + click on the application to allowlist it. The apoplication will open in a different terminal window and exit automatically. After this, you can use the cli normally from next time. 

## Getting Started <a name="getting-started"></a>

Before using the DataIngestion CLI, ensure you have the necessary credentials and permissions to access the DataIngestion Service. The CLI requires an admin username and password to connect to the service successfully.

To get started, open a terminal or command prompt and navigate to the directory where you extracted the `NEDSS-DataIngestion-CLI-{operating_system}` application.

## Command Reference <a name="command-reference"></a>

The DataIngestion CLI offers the following commands with their respective functionalities:

### register Command <a name="register-command"></a>

The `register` command allows you to onboard a client by providing their username and secret.

**Usage:**

Mac OS/Linux:

```bash
./NEDSS-DataIngestion-CLI-{{ macos || linux || windows }} register --client-username --client-secret --admin-user --admin-password
```

Windows:
```bash
NEDSS-DataIngestion-CLI-{{ macos || linux || windows }} register --client-username --client-secret --admin-user --admin-password
```

You will be prompted wit interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--client-username*: The username provided by the client (required).

* *--client-secret*: The secret provided by the client (required).

* *--admin-user*: The admin username to connect to the DataIngestion Service (required).

* *--admin-password*: The admin password to connect to the DataIngestion Service (required).


### token Command <a name="token-command"></a>

The token command generates a JWT token, which is used for authentication.

Usage:

Mac OS/Linux:

```bash
./NEDSS-DataIngestion-CLI-{{ macos || linux || windows }} token --admin-user --admin-password
```

Windows:

```bash
NEDSS-DataIngestion-CLI-{{ macos || linux || windows }} token --admin-user --admin-password
```

You will be prompted wit interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--admin-user*: The admin username to connect to the DataIngestion Service (required).

* *--admin-password*: The admin password to connect to the DataIngestion Service (required).

### injecthl7 Command <a name="injecthl7-command"></a>

The injecthl7 command allows developers to use the /api/reports endpoint of the DataIngestion Service.

Usage:

Mac OS/Linux:

```bash
./NEDSS-DataIngestion-CLI-{{ macos || linux || windows }} injecthl7 --hl7-file --admin-user --admin-password
```

Windows:

```bash
NEDSS-DataIngestion-CLI-{{ macos || linux || windows }} injecthl7 --hl7-file --admin-user --admin-password
```

You will be prompted wit interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--hl7-file*: The full path of the HL7 file (required).

* *--admin-user*: The admin username to connect to the DataIngestion Service (required).

* *--admin-password*: The admin password to connect to the DataIngestion Service (required).


### Troubleshooting <a name="troubleshooting"></a>

If you encounter any issues or errors while using the DataIngestion CLI, consider the following troubleshooting steps:

* Verify that you provided the correct credentials and required arguments for the command.

* Ensure that the DataIngestion Service is accessible and running.

* Check your internet connection to ensure successful communication with the service.

* For specific error messages, refer to the error output provided by the CLI.

If you have any questions or need further assistance, please refer to the documentation or contact the development team.