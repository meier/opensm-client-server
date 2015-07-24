%define java_package_name OsmClientServer
%define osm_config_dir etc/opensm-plugin

Name:           	opensm-client-server-java
Version:        	2.0.0
Release:        	65%{?dist}
Summary:        	Java Client and Server library for OpenSM

Group:          	Development/Libraries
License:        	GPL/BSD
BuildRoot: 			%{_tmppath}/%{name}-%{version}
Source0:        	%{name}-%{version}.tar.gz
BuildArch:      	noarch

BuildRequires:  	java-devel >= 1:1.6.0
Requires:       	java >= 1:1.6.0
Requires:       	llnl-ldapotp-clt-jni-auth-libs
Requires:       	opensm-jni-plugin-libs
Requires:       	llnl-ldapotp-clt-java >= 1.0.0-13
BuildRequires:     	llnl-ldapotp-clt-java >= 1.0.0-13
BuildRequires:  	jpackage-utils
BuildRequires:  	ant >= 1.6

Prefix:				/usr/share/java

%description
This package includes a multi-threaded daemon service that interfaces directly
to its OpenSM plugin for the purpose of providing subnet information to
clients.  This package also includes the necessary client interfaces and test
applications for creating robust client applications that use the service.
See /usr/share/java/OsmClientServer/bin/ for tools and sample clients.

%package javadoc
Summary:        Javadocs for %{java_package_name}
Group:          Documentation
Requires:       jpackage-utils

%description javadoc
This package contains the OpenSM Monitoring Service API specification, and the
typical installation location is /usr/share/javadoc/OsmClientServer.  This
documentation is in Javadoc style html, so you can simply point your browser
at "file:///usr/share/javadoc/OsmClientServer/index.html".


%prep
%setup -q

%build


%install
[ "%{buildroot}" != "/" ] && rm -rf %{buildroot}
mkdir -p $RPM_BUILD_ROOT%{_javadir}
cd $RPM_BUILD_ROOT%{_javadir}
tar -xzf %{SOURCE0}
mv $RPM_BUILD_ROOT%{_javadir}/%{name}-%{version} $RPM_BUILD_ROOT%{_javadir}/%{java_package_name}
cd $RPM_BUILD_ROOT%{_javadir}/%{java_package_name}
rm -f *.spec
ln -s %{java_package_name}-*.jar %{java_package_name}.jar

mkdir -p $RPM_BUILD_ROOT/%{osm_config_dir}
mv $RPM_BUILD_ROOT%{_javadir}/%{java_package_name}/%{osm_config_dir} $RPM_BUILD_ROOT/etc

mkdir -p $RPM_BUILD_ROOT%{_javadocdir}
mv $RPM_BUILD_ROOT%{_javadir}/%{java_package_name}/docs $RPM_BUILD_ROOT%{_javadocdir}/%{java_package_name}


%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(644,root,root,755)
%{_javadir}/*
%config(noreplace) /%{osm_config_dir}/*.properties
%config(noreplace) /%{osm_config_dir}/OsmServerKeystore
%config(noreplace) /%{osm_config_dir}/OsmClientKeystore

%defattr(600,root,root,755)
%config(noreplace) /%{osm_config_dir}/OsmServerPriv*


%defattr(755,root,root,755)
%{_javadir}/%{java_package_name}/bin/*

%files javadoc
%defattr(644,root,root,755)
%{_javadocdir}/%{java_package_name}


%changelog
* Wed May 20 2015 Tim Meier <meier3@llnl.gov> 2.0.0-65
- a variety of small improvements to support node position awareness
- within the fabric (neighborhood)
* Fri Apr 17 2015 Tim Meier <meier3@llnl.gov> 2.0.0-63
- small Port change, to support levels in new heat maps
* Mon Mar  2 2015 Tim Meier <meier3@llnl.gov> 2.0.0-61
- increased memory, to accomodate larger fabrics, enhanced version support
* Wed Feb 25 2015 Tim Meier <meier3@llnl.gov> 2.0.0-59
- increased capacity for multicast groups and calmed some logging
* Fri Feb 20 2015 Tim Meier <meier3@llnl.gov> 2.0.0-57
- improved support for multicast groups and partitions
* Tue Feb 17 2015 Tim Meier <meier3@llnl.gov> 2.0.0-55
- handle HCA's that give ports their own guid (as opposed to using the node guid)
* Wed Feb  4 2015 Tim Meier <meier3@llnl.gov> 2.0.0-53
- updated to v2.0 of the API, support for routining
* Thu Oct 23 2014 Tim Meier <meier3@llnl.gov> 1.0.0-47
- fixed oms-abstract to handle java better, also cleaned up Jar dependencies
* Fri Oct 17 2014 Tim Meier <meier3@llnl.gov> 1.0.0-45
- refactored to eliminate the need for a client package
* Tue Oct 14 2014 Tim Meier <meier3@llnl.gov> 1.0.0-41
- use new build process, fixed GUID bugs to handle guids starting 0x8.. to 0xf
* Thu Mar 13 2014 Tim Meier <meier3@llnl.gov> 1.0.0-39
- minor build.xml fix (now includes release number)
* Thu Mar  6 2014 Tim Meier <meier3@llnl.gov> 1.0.0-37
- new certificates in keystores, minor fix to OMS_Port diagnostic
* Tue Dec 17 2013 Tim Meier <meier3@llnl.gov> 1.0.0-35
- Xmas snapshot (LinkRate correction, factor of 4)
* Tue Oct 15 2013 Tim Meier <meier3@llnl.gov> 1.0.0-33
- Furlough snapshot
* Fri Oct  4 2013 Tim Meier <meier3@llnl.gov> 1.0.0-31
- Fixed convenience scripts to start with magic kernel shell directive
* Thu Sep 26 2013 Tim Meier <meier3@llnl.gov> 1.0.0-29
- fixed xfer & rcv rate calculation
* Fri Sep 20 2013 Tim Meier <meier3@llnl.gov> 1.0.0-27
- added support for links and edges for the smt-link command
* Fri Aug  9 2013 Tim Meier <meier3@llnl.gov> 1.0.0-25
- added support for vertex and edges, as well as levels
* Mon Jun 24 2013 Tim Meier <meier3@llnl.gov> 1.0.0-24
- changed to linked hash maps, included 2nd tier "collection" objects
* Fri May  3 2013 Tim Meier <meier3@llnl.gov> 1.0.0-20
- extended helper utilities, and improved collection performance via hash maps
* Wed Jul 18 2012 Tim Meier <meier3@llnl.gov>
Modified the packages (common, client-tools, lc-client-server) and bumped the release to 14.
* Wed Jul 11 2012 Tim Meier <meier3@llnl.gov>
Modified the package descriptions and bumped the release to 11.