#
#  Copyright (c) 2018-2025 Cloud Software Group, Inc.
#  All Rights Reserved. Confidential and Proprietary.
#  
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are met:
#  
#  1. Redistributions of source code must retain the above copyright notice,
#     this list of conditions and the following disclaimer.
#  
#  2. Redistributions in binary form must reproduce the above copyright notice,
#     this list of conditions and the following disclaimer in the documentation
#     and/or other materials provided with the distribution.
#  
#  3. Neither the name of the copyright holder nor the names of its contributors
#     may be used to endorse or promote products derived from this software
#     without specific prior written permission.
#  
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
#  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
#  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
#  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
#  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
#  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
#  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
#  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
#  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
#  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
#  POSSIBILITY OF SUCH DAMAGE.
#
# Dockerfile for Streaming generic base image
#
FROM eclipse-temurin:22-jdk AS java-image
FROM almalinux:8

LABEL description="TIBCO Streaming base image"
LABEL PRODUCT_VERSION=${docker_base_version}

#
# Set environment
#
ENV STREAMING_PRODUCT_HOME     /opt/tibco/streambase
ENV STREAMING_RUNTIME_HOME     /var/opt/tibco/streambase
ENV JAVA_HOME        /etc/alternatives/jre
ENV PATH             /bin:/usr/sbin:${STREAMING_PRODUCT_HOME}/distrib/tibco/bin
ENV USER_NAME        tibco

COPY --from=java-image /opt/java/openjdk $JAVA_HOME

RUN ln -s $JAVA_HOME/bin/java /usr/bin/java \
    && ln -s $JAVA_HOME/bin/javac /usr/bin/javac \
    && ln -s $JAVA_HOME/bin/jar /usr/bin/jar

#
# Add required additional packages
#
# systat    required for runtime statistcs
# gdb       required for crash dumps
# java      required for applications
# zip       required for snapshots
# unzip     required to unpack runtime on windows
# libnsl    required by epadmin
# less      to aid log file reading
#
RUN echo 'fastestmirror=1' >> /etc/dnf/dnf.conf && dnf --assumeyes update && dnf --setopt=install_weak_deps=False --best --assumeyes --exclude=gcc --exclude=binutils install \
    sysstat \
    gdb \
    zip \
    unzip \
    libnsl \
    less \
    iproute \
    && dnf clean all

#
# Create a user to run nodes
#
RUN /usr/sbin/useradd \
    --comment "TIBCO Streaming User" \
    --create-home \
    --user-group \
    --password ${USER_NAME} \
    --shell /bin/bash \
    ${USER_NAME}

#
# Copy platform archive
#
RUN mkdir -p ${STREAMING_PRODUCT_HOME}
COPY maven ${STREAMING_PRODUCT_HOME}

#
# Unpack the platform archive and set permissions
#
RUN cd ${STREAMING_PRODUCT_HOME} \
    && unzip platform_linuxx86_64.zip \
    && rm platform_linuxx86_64.zip \
    && sed -i 's/\r//' ${STREAMING_PRODUCT_HOME}/start-node \
    && chmod a+x \
        ${STREAMING_PRODUCT_HOME}/start-node \
        ${STREAMING_PRODUCT_HOME}/distrib/tibco/bin/ep* \
        ${STREAMING_PRODUCT_HOME}/distrib/kabira/bin/sw* \
        ${STREAMING_PRODUCT_HOME}/distrib/kabira/component/swcoordadmin/prodbin/swcoordadmin \
        ${STREAMING_PRODUCT_HOME}/distrib/kabira/component/DtmEngine/prodbin/DtmEngine \
    && chmod -R a-w ${STREAMING_PRODUCT_HOME}

#
# Allow user access to nodes and applications
#
RUN mkdir -p ${STREAMING_RUNTIME_HOME}
RUN chown ${USER_NAME}:${USER_NAME} ${STREAMING_RUNTIME_HOME}

#
# Change to guest user
#
USER ${USER_NAME}
