# TruAdvertiser Project

## Running the API

The API depends on an etcd server and an Oracle database to be running.  Scripts have been written to help with the work of getting these servers up and running.

There is some prep work that needs to be done before the scripts will work however.

### Prep work

#### Boot2Docker

Make sure that you have `boot2docker` installed and correctly configured.  You will need the **1.6.2** version of `boot2docker`.
    
You can download the package directly from GitHub and install it, <https://github.com/boot2docker/osx-installer/releases/tag/v1.6.2>.

When running the initialization for `boot2docker` you will need to point to the 1.6.2 version of the ISO image as well.

    boot2docker init --iso-url="https://github.com/boot2docker/boot2docker/releases/download/v1.6.2/boot2docker.iso"

#### Port Forwarding

1. Open VirtualBox application
2. Select the `boot2docker-vm` machine
3. Click the **Settings** button
4. Click the **Network** button
5. Click the **Port Forwarding** button
6. Click the **+** button on the right to create a new rule
	* You will need to create new rules for **etcd** and **Oracle**.  Use the following table to create your rules.
	
| Name   | Protocol | Host IP   | Host Port | Guest IP | Guest Port |
|--------|----------|-----------|-----------|----------|------------|
| etcd   | TCP      | 127.0cd ...0.1 | 4001      |          | 4001       |
| oracle | TCP      | 127.0.0.1 | 1521      |          | 1521       |

#### Oracle sqlplus

(Note: `$` denotes a prompt in the terminal)

1. Download the sqlplus installation files from [here](http://www.oracle.com/technetwork/topics/intel-macsoft-096467.html).
	* You will need the **Basic Lite** and **SQL\*Plus** zip files.
2. Unzip those files to a common directory.
	* I will be using `/opt/instantclient_11_2` for all examples.
	* Both files will need to be unzipped to the same directory.
	* 
	
			$ cd /opt
			$ sudo unzip ~/Downloads/instantclient-basiclite-*
			$ sudo unzip ~/Downloads/instantclient-sqlplus-*
			$ chown -R <username> instantclient_11_2
	
3. Update your PATH variable to include the common directory sqlplus was unzipped into.
	* You should also add this to your `.bash_profile` file so that the PATH is always updated
	* 
	
			$ export PATH=$PATH:/opt/instantclient_11_2
			$ echo "PATH=\$PATH:/opt/instantclient_11_2" >> ~/.bash_profile
	
4. Update all of the files in the sqlplus directory (`/opt/instantclient_11_2`) with write permissions.
	* This doesn't need to be done on all computers for some reason, but it won't hurt anything.
	* 
		
			$ cd /opt/instantclient_11_2
			$ chmod +w *
			
5. Run the `changeOracleLibs.sh` script in the `deploy/environments/local` directory of this project from the newly created sqlplus directory.
	* 
	
			$ <path_to_api_project>/deploy/environments/local/changeOracleLibs.sh
	
6. You should now be able to run sqlplus from the command line.
	* 
	
			$ sqlplus
			
			SQL*Plus: Release 11.2.0.3.0 Production on Sun Mar 3 22:38:28 2013
	
			Copyright (c) 1982, 2012, Oracle.  All rights reserved.
		
			Enter user-name: ^C
			$ cd ..
			$ sqlplus
		
			SQL*Plus: Release 11.2.0.3.0 Production on Sun Mar 3 22:38:40 2013
			
			Copyright (c) 1982, 2012, Oracle.  All rights reserved.
			
			Enter user-name: ^C


Reference: <http://javalinpilipinas.blogspot.com/2013/05/oracle-sqlplus-and-instant-client-on.html>

#### Dependent services

The dependent services can be run using the `launch-dependencies.sh` script in the `deploy/environments/local` directory of the project.  This script assumes that `boot2docker` is running and configured correctly for the current shell.

This script will launch docker containers for both etcd and Oracle as well as seed them appropriately.

There is minimal seed data in the Oracle instance.  We can always add more.

The user you can use to connect to the local instance is **local-admin@trueffect.com**/**Trueffect123**.

### Running

You can run the API locally using the `local-run.sh` script.  There are several options to this script.

| Option | Description |
|---|---|
| `-l` | Run the launch dependencies script. |
| `-e` | Set the environment to run against.  The available options are **te** (Dev database at Trueffect), **tf** (Database at Trueforce), and **local** (Locally running Oracle database) |

Run `local-run.sh -h` to see all options.

When working remotely you may run into the following error when starting the Oracle container:
ORA-21561 - OID generation failed

To solve this issue you just have to add your hostname to your localhost entry in your /etc/hosts

1. Get your workstation hostname with the command hostname<br/>`hostname`
1. Edit your /etc/hosts as root<br/>`sudo vi /etc/hosts`
1. Look for the following line<br/>`127.0.0.1   localhost`
1. Add the hostname you've got in step one with a whitespace to this line at the end<br/>`127.0.0.1   localhost hostname`
1. Save the file

Below is the link to the article describing the fix:
<http://chaos667.tumblr.com/post/20006357466/ora-21561-and-oracle-instant-client-11-2>


### User Audit

To determine what user has modified an object from Loggly
1. Get the Oauth key that modified the object from Loggly
1. Enter the Oauth key in the search bar followed by AuthorizeToken to find the access token of the user
1. Take the access token and enter it in the search bar followed by getToken to get the username of the person who modified the object