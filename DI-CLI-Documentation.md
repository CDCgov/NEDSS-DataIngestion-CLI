# DataIngestion CLI User Guide

## Table of Contents

1. [Introduction](#introduction)

2. [Installation](#installation)

3. [Getting Started](#getting-started)

4. [Command Reference](#command-reference)

    - [register](#register-command)

    - [token](#token-command)

    - [injecthl7](#injecthl7-command)
   
    - [status](#status-command)
    - [hl7validation](#hl7-validation)
    - [dltmessages](#dlt-messages)

5. [Troubleshooting](#troubleshooting)

## Introduction <a name="introduction"></a>

The DataIngestion CLI is a command-line tool that allows users to interact with the DataIngestion Service. It provides essential functionalities for onboarding clients, generating JWT tokens, and using the /api/reports endpoint to inject HL7 data for developers. This user guide explains how to install and use the CLI effectively.

## Installation <a name="installation"></a>

The DataIngestion CLI is packaged using GraalVM, and no additional dependencies or prerequisites are required for installation. To install the CLI, follow these steps:

1. Download the `nbs-di-cli` application from the folder `/builtImages/linux-unix` or `/builtImages/windows` depending on the machine operating system that you use.

## Getting Started <a name="getting-started"></a>

Before using the DataIngestion CLI, ensure you have the necessary credentials and permissions to access the DataIngestion Service. The CLI requires an admin username and password to connect to the service successfully.

To get started, open a terminal or command prompt and navigate to the directory where you extracted the `nbs-di-cli` application.

## Command Reference <a name="command-reference"></a>

The DataIngestion CLI offers the following commands with their respective functionalities:

### register Command <a name="register-command"></a>

The `register` command allows you to onboard a client by providing their username and secret.

**Usage:**

Mac OS/Linux:

```bash
./nbs-di-cli register --client-username --client-secret --username --password
```

Windows:
```bash
nbs-di-cli register --client-username --client-secret --username --password
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--client-username*: The username provided by the client (required).

* *--client-secret*: The secret provided by the client (required).

* *--username*: The admin username to connect to the DataIngestion Service (required).

* *--password*: The admin password to connect to the DataIngestion Service (required).


### token Command <a name="token-command"></a>

The token command generates a JWT token, which is used for authentication.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli token --username --password
```

Windows:

```bash
nbs-di-cli token --username --password
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--username*: The admin username to connect to the DataIngestion Service (required).

* *--password*: The admin password to connect to the DataIngestion Service (required).

### injecthl7 Command <a name="injecthl7-command"></a>

The injecthl7 command allows developers to use the /api/reports endpoint of the DataIngestion Service.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli injecthl7 --hl7-file --username --password
```

Windows:

```bash
nbs-di-cli injecthl7 --hl7-file --username --password
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--hl7-file*: The full path of the HL7 file (required).

* *--username*: The admin username to connect to the DataIngestion Service (required).

* *--password*: The admin password to connect to the DataIngestion Service (required).


### status Command <a name="status-command"></a>

The status command allows developers to use the /report-status/{id} endpoint of the DataIngestion Service.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli status --report-id --username --password
```

Windows:

```bash
nbs-di-cli status --report-id --username --password
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--report-id*: The UUID provided when the report was injected into the DataIngestion service (required).

* *--username*: The admin username to connect to the DataIngestion Service (required).

* *--password*: The admin password to connect to the DataIngestion Service (required).

### hl7 validation Command <a name="hl7-validation"></a>

HL7 Validate command allows developers to use the /api/reports/hl7-validator endpoint of the DataIngestion Service to validate any HL7 messages.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli validatehl7 --hl7-file --username --password
```

Windows:

```bash
nbs-di-cli validatehl7 --hl7-file --username --password
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--validatehl7 --hl7-file*:  The full path of the HL7 file(required).

* *--username*: The admin username to connect to the DataIngestion Service (required).

* *--password*: The admin password to connect to the DataIngestion Service (required).

### Get dlt messages Command <a name="dlt-messages"></a>

Get DLT Messages command allows developers to use the api/reports-dlt/get-error-messages endpoint of the DataIngestion Service to view the DLT messages.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli dltmessages --msgsize --username --password
```

Windows:

```bash
nbs-di-cli dltmessages --msgsize --username --password
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--msgsize   Number of messages to be displayed on the screen(optional). Default is 10.

* *--username*: The admin username to connect to the DataIngestion Service (required).

* *--password*: The admin password to connect to the DataIngestion Service (required).

### Troubleshooting <a name="troubleshooting"></a>

If you encounter any issues or errors while using the DataIngestion CLI, consider the following troubleshooting steps:

* Verify that you provided the correct credentials and required arguments for the command.

* Ensure that the DataIngestion Service is accessible and running.

* Check your internet connection to ensure successful communication with the service.

* For specific error messages, refer to the error output provided by the CLI.

If you have any questions or need further assistance, please refer to the documentation or contact the development team.