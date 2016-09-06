FROM node:0.12.7
MAINTAINER TE Galaxy Team<galaxy@trueffect.com>

# AWS is blocking git:// for some reason?
RUN git config --global url."https://".insteadOf git://

RUN npm install -g bower && npm install -g grunt-cli

# Copy the project
ADD . /truadvertiser

# CD to the app dir
WORKDIR /truadvertiser
RUN npm install && bower install --allow-root && grunt build

EXPOSE 8080

WORKDIR /truadvertiser/dist/server

# Set the configuration.  This must be done LAST because its also used by npm to decide what to install
# in 'production' mode npm wont install the devDependencies like grunt
ENV NODE_ENV production

CMD node app.js

