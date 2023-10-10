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
./nbs-di-cli register --client-username --client-secret --admin-user --admin-password
```

Windows:
```bash
nbs-di-cli register --client-username --client-secret --admin-user --admin-password
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
./nbs-di-cli token --admin-user --admin-password
```

Windows:

```bash
nbs-di-cli token --admin-user --admin-password
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
./nbs-di-cli injecthl7 --hl7-file --admin-user --admin-password
```

Windows:

```bash
nbs-di-cli injecthl7 --hl7-file --admin-user --admin-password
```

You will be prompted wit interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--hl7-file*: The full path of the HL7 file (required).

* *--admin-user*: The admin username to connect to the DataIngestion Service (required).

* *--admin-password*: The admin password to connect to the DataIngestion Service (required).


### status Command <a name="status-command"></a>

The status command allows developers to use the /report-status/{id} endpoint of the DataIngestion Service.

Usage:

Mac OS/Linux:

```bash
./nbs-di-cli status --report-id --admin-user --admin-password
```

Windows:

```bash
nbs-di-cli status --report-id --admin-user --admin-password
```

You will be prompted wit interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--report-id*: The UUID provided when the report was injected into the DataIngestion service (required).

* *--admin-user*: The admin username to connect to the DataIngestion Service (required).

* *--admin-password*: The admin password to connect to the DataIngestion Service (required).

### Troubleshooting <a name="troubleshooting"></a>

If you encounter any issues or errors while using the DataIngestion CLI, consider the following troubleshooting steps:

* Verify that you provided the correct credentials and required arguments for the command.

* Ensure that the DataIngestion Service is accessible and running.

* Check your internet connection to ensure successful communication with the service.

* For specific error messages, refer to the error output provided by the CLI.

If you have any questions or need further assistance, please refer to the documentation or contact the development team.