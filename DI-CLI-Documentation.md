# DataIngestion CLI User Guide

## Table of Contents

1. [Introduction](#introduction)

2. [Installation](#installation)

3. [Getting Started](#getting-started)

4. [Command Reference](#command-reference)

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

1. Download the `NBS-DataIngestion-CLI-*` application (based on the OS that you use) from source installation folder.
2. Run the below commands directly from where you downloaded the `NBS-DataIngestion-CLI-*` application (based on the OS that you use).
3. Set up system environment variable
    - Mac: run `nano ~/.zshrc`, add `export DI_URL=[URL_GOES_HERE]`, exit terminal or `source` it
    - Windows: press `windows` button and search for `env`, select `environment varibles`, add new system variable where variable name is `DI_URL` and value is `YOUR DI URL`.
    - Linux: run `nano ~/.bashrc`, add `export DI_URL=[URL_GOES_HERE]`, exit terminal or `source` it

*ONLY FOR MAC USERS:*
- If you're using the latest Mac with Apple Silicon Chip, please download and use the image named `NBS-DataIngestion-CLI-macos-aarch64`. For other mac users, use the image named `NBS-DataIngestion-CLI-macos`.
- If you're facing "Cannot open because of unidentified developer", please go to Settings -> Privacy & Security -> Allow `NBS-DataIngestion-CLI-macos` to run on this computer.

- To ensure executable files downloaded on Mac or Linux systems are runnable, use the command chmod +x <filename> to grant execution permissions, thereby preventing errors related to unrecognized software or access denial. For example,
```bash
chmod +x ./NBS-DataIngestion-CLI-macos
```

## Getting Started <a name="getting-started"></a>

Before using the DataIngestion CLI, ensure you have the necessary credentials and permissions to access the DataIngestion Service. The CLI requires an username and password to connect to the service.

To get started, open a terminal or command prompt and navigate to the directory where you extracted the `NBS-DataIngestion-CLI-*` application (based on the OS that you use).

## Command Reference <a name="command-reference"></a>

The DataIngestion CLI offers the following commands with their respective functionalities:

### token Command <a name="token-command"></a>

The token command generates a JWT token, which is used for authentication.

Usage:

Mac OS:

```bash
./NBS-DataIngestion-CLI-macos token --client-id --client-secret
```
or
```bash
./NBS-DataIngestion-CLI-macos-aarch64 token --client-id --client-secret
```

Linux:

```bash
./NBS-DataIngestion-CLI-linux token --client-id --client-secret
```

Windows:

```bash
NBS-DataIngestion-CLI-windows token --client-id --client-secret
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--client-id*: Client ID to connect to the DataIngestion Service (required).

* *--client-secret*: Client Secret to connect to the DataIngestion Service (required).

### injecthl7 Command <a name="injecthl7-command"></a>

The injecthl7 command allows developers to use the `/api/reports` endpoint of the DataIngestion Service.

Usage:

Mac OS:

```bash
./NBS-DataIngestion-CLI-macos injecthl7 --hl7-file
```
or
```bash
./NBS-DataIngestion-CLI-macos-aarch64 injecthl7 --hl7-file
```

Linux:

```bash
./NBS-DataIngestion-CLI-linux injecthl7 --hl7-file
```

Windows:

```bash
NBS-DataIngestion-CLI-windows injecthl7 --hl7-file
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--hl7-file*: The full path of the HL7 file (required).


### status Command <a name="status-command"></a>

The status command allows developers to use the `/report-status/{id}` endpoint of the DataIngestion Service.

Usage:

Mac OS:

```bash
./NBS-DataIngestion-CLI-macos status --report-id
```
or
```bash
./NBS-DataIngestion-CLI-macos-aarch64 status --report-id
```

Linux:

```bash
./NBS-DataIngestion-CLI-linux status --report-id
```

Windows:

```bash
NBS-DataIngestion-CLI-windows status --report-id
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--report-id*: The UUID provided when the report was injected into the DataIngestion service (required).


### hl7 validation Command <a name="hl7-validation"></a>

HL7 Validate command allows developers to use the `/api/reports/hl7-validator` endpoint of the DataIngestion Service to validate any HL7 messages.

Usage:

Mac OS:

```bash
./NBS-DataIngestion-CLI-macos validatehl7 --hl7-file
```
or
```bash
./NBS-DataIngestion-CLI-macos-aarch64 validatehl7 --hl7-file
```

Linux:

```bash
./NBS-DataIngestion-CLI-linux validatehl7 --hl7-file
```

Windows:

```bash
NBS-DataIngestion-CLI-windows validatehl7 --hl7-file
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--hl7-file*:  The full path of the HL7 file(required).


### Get dlt messages Command <a name="dlt-messages"></a>

Get DLT Messages command allows developers to use the `/api/reports-dlt/get-error-messages` endpoint of the DataIngestion Service to view the DLT messages.

Usage:

Mac OS:

```bash
./NBS-DataIngestion-CLI-macos dltmessages --msg-size
```
or
```bash
./NBS-DataIngestion-CLI-macos-aarch64 dltmessages --msg-size
```

Linux:

```bash
./NBS-DataIngestion-CLI-linux dltmessages --msg-size
```

Windows:

```bash
NBS-DataIngestion-CLI-windows dltmessages --msg-size
```

You will be prompted with interactive input where you'll be providing all the required details to the CLI.

Arguments:

* *--msg-size*: Number of messages to be displayed on the screen(optional). Default is 10.


### Troubleshooting <a name="troubleshooting"></a>

If you encounter any issues or errors while using the DataIngestion CLI, consider the following troubleshooting steps:

* Verify that you provided the correct credentials and required arguments for the command.

* Ensure that the DataIngestion Service is accessible and running.

* Check your internet connection to ensure successful communication with the Data Ingestion service.

* If you're facing ***Unauthorized: Your token may have expired.*** in the following scenarios:
   * During token generation, that means you provided wrong credentials.
   * After token generation, (usually token is valid for an hour), that means the generated token is expired, and you'll have to re-run the token command.

* For specific error messages, refer to the error output provided by the CLI.

If you have any questions or need further assistance, please refer to the documentation or contact the development team.