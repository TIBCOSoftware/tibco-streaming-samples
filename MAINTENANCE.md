# Maintenance Notes #

These samples illustrate a lot of functionality, which makes use of a
lot of different Maven plugins and external tools and
services. Because of the way Maven project construction works, some
projects are not active by default, because validating or running them
would require prerequisites that many users would not normally
have.

## Setup ##

A comprehensive audit of these samples require the following
tools that are often third party downloads or available only from
add-on package repositories.

* [Docker](https://www.docker.com)
* [Ansible](https://www.ansible.com)
* [Helm](https://helm.sh)

You need to install these tools so that at least some basic validation
can be done projects, which in turn allows Maven to validate the
projects.

Ansible will need a version of
[the Docker SDK for Python](https://docker-py.readthedocs.io/en/stable/)
that will be installed in the "ansible python module location"
location reported by `ansible --version`. To be sure to run the right
version of `pip`, run the `pip` module directly from the explicitly
versioned Python command to do so. For example, for a version of
Ansible that is using Python 3.8:

    python3.8 -m pip install --user docker

You can ensure that you have installed the Maven
artifacts using the `epdev install maven` command of each product
installation involved in the transition in the Maven local repository
as configured by your defaults `.m2/settings.xml`, if it exists. And
if not, the `epdev` commands will just install into `.m2/repository`
as expected.

As of Streaming 11.2, the version change process is part of the Streaming
release process because the Maven versions of the samples align with
the Streaming version.

## Basics ##

Each Streaming release eventually gets a branch named for the
version. The latest release version becomes the default branch
for this repository. 

## Enabling Specialized Projects ##

As of now, some skipping of feature-specific functionality is
implemented by using profiles to conditionally include the worker
modules in the reactor build. **If these modules are not included
during version maintenance operations, they will retain stale
version references.** So, we are currently obliged to add the
following `mvn` command line options during such operations:

* `-PactiveAnsible` for [docker/ef-2node-ansible](docker/ef-2node-ansible).

Which then leaves a base command of:

    mvn -PactiveAnsible versions:update-parent ...

These vexing issues are handled in
[.EPDev/common.bash](.EPDev/common.bash).

# Performing The Updates

## Maven Settings

If the Maven settings file you want to use is not in the default
location, your `MVN_OPTIONS` environment variable will need to include
something like:

    MVN_OPTIONS="--settings ${HOME}/.m2/ep-dev-settings.xml"
    
which you could just use as a prefix to running the maintenance commands.

## Regenerating Sample Indices ##

This step should not be needed unless the sample index scheme changes.

After a branch's parent versions are updated, you should update the
indices, which will make sure that all the sample documentation is linked
from the top, and the correct Streaming versions are mentioned.

Run this command:

    [ MVN_OPTIONS=...more mvn parameters... ] ./EPDev/update-indices
   
You must be able to build all the samples locally because the updates
require that all project depdendencies are available, including
dependencies between the projects. So, Docker and Ansible need to
be installed and functional because of the container technology
samples.

Therefore, the first `update-indices` run will need the
`--check-consistency` option to make sure those intra-sample
dependencies. Because checking consistency involves running a lot of
cloud tools, it can take thirty minutes or more to get through that.

Then inspect the results, commit, and push.

# Changing The Processs #

This file and the scripts under [`.EPDev`](.EPDev) can be edited on
the main branch. The main branch scripts can still run in the clones of other
branches if needed.  The changes should be commited as one commit, if
possible, to the main branch, and they cherry-picked back to the
branch that motivated the changes, and then pushed as well.

# Futures #

It would be nice to get rid of the reliance on profiles because they
cannot be specified in the preferences for Eclipse m2e, although
Maven launch configurations can specify them.

One approach would be always to always include modules, and skip
the specialized executions by other means.

Another approach is define the property `updatingVersionPhase` during
maintenance operations, and skip specialized executions based on that
property. But combining this with other techniques for end-users who
want to activate the specialized executions could be a challenge.
