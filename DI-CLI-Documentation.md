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

The DataIngestion CLI is a command-line tool that allows users to interact with the DataIngestion Service. It provides essential functionalities for onboarding clients, generating JWT tokens, and to inject HL7 data for users. This user guide explains how to install and use the CLI effectively.

## Installation <a name="installation"></a>

The DataIngestion CLI is packaged using GraalVM, and no additional dependencies or prerequisites are required for installation. To install the CLI, follow these steps:

1. Download the `nbs-di-cli` application from source installation folder.
2. Run the below commands directly from where you downloaded the `nbs-di-cli` application.

## Getting Started <a name="getting-started"></a>

Before using the DataIngestion CLI, ensure you have the necessary credentials and permissions to access the DataIngestion Service. The CLI requires an username and password to connect to the service.

To get started, open a terminal or command prompt and navigate to the directory where you extracted the `nbs-di-cli` application.

## Command Reference <a name="command-reference"></a>

The DataIngestion CLI offers the following commands with their respective functionalities:

### register Command <a name="register-command"></a>

The `register` command allows you to onboard a client by providing their username and secret.

**Usage:**

Mac OS/Linux:

```bash
./nbs-di-cli register --client-username --client-secret --admin-user --admin-password
```

Windows:
```bash
nbs-di-cli register --client-username --client-secret --admin-user --admin-password
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--client-username*: Username provided by the client (required).

* *--client-secret*: Secret provided by the client (required).

* *--admin-username*: Admin username to connect to the DataIngestion Service (required).

* *--admin-password*: Admin password to connect to the DataIngestion Service (required).


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

* *--username*: Username to connect to the DataIngestion Service (required).

* *--password*: Password to connect to the DataIngestion Service (required).

### injecthl7 Command <a name="injecthl7-command"></a>

The injecthl7 command allows developers to use the /api/reports endpoint of the DataIngestion Service.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli injecthl7 --hl7-file
```

Windows:

```bash
nbs-di-cli injecthl7 --hl7-file
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--hl7-file*: The full path of the HL7 file (required).


### status Command <a name="status-command"></a>

The status command allows developers to use the /report-status/{id} endpoint of the DataIngestion Service.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli status --report-id
```

Windows:

```bash
nbs-di-cli status --report-id
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--report-id*: The UUID provided when the report was injected into the DataIngestion service (required).


### hl7 validation Command <a name="hl7-validation"></a>

HL7 Validate command allows developers to use the /api/reports/hl7-validator endpoint of the DataIngestion Service to validate any HL7 messages.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli validatehl7 --hl7-file
```

Windows:

```bash
nbs-di-cli validatehl7 --hl7-file
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--hl7-file*:  The full path of the HL7 file(required).


### Get dlt messages Command <a name="dlt-messages"></a>

Get DLT Messages command allows developers to use the api/reports-dlt/get-error-messages endpoint of the DataIngestion Service to view the DLT messages.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli dltmessages --msgsize
```

Windows:

```bash
nbs-di-cli dltmessages --msgsize
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--msgsize*: Number of messages to be displayed on the screen(optional). Default is 10.


### Troubleshooting <a name="troubleshooting"></a>

If you encounter any issues or errors while using the DataIngestion CLI, consider the following troubleshooting steps:

* Verify that you provided the correct credentials and required arguments for the command.

* Ensure that the DataIngestion Service is accessible and running.

* Check your internet connection to ensure successful communication with the Data Ingestion service.

* If you're facing ***Unauthorized error*** in the following scenarios:
   * During token generation, that means you provided wrong credentials.
   * After token generation, (usually token is valid for an hour), that means the geenrated token is expired and you'll have to re-run the token command.

* For specific error messages, refer to the error output provided by the CLI.

If you have any questions or need further assistance, please refer to the documentation or contact the development team.