FROM poolingpeople/wildfly
ADD libs/mysql-connector-java-5.1.34-bin.jar /opt/jboss/wildfly/standalone/deployments/
ADD libs/itext-2.1.7.jar /opt/jboss/wildfly/modules/system/layers/base/com/rha/main/
ADD libs/primefaces-5.0.jar /opt/jboss/wildfly/modules/system/layers/base/com/rha/main/
ADD libs/poi-3.7.jar /opt/jboss/wildfly/modules/system/layers/base/com/rha/main/
ADD module.xml /opt/jboss/wildfly/modules/system/layers/base/com/rha/main/
RUN /opt/jboss/wildfly/bin/add-user.sh alacambra alacambra1982. --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]