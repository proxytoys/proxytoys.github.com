# ProxyToys Documentation

This repository contains the GitHub pages for [ProxyToys](https://github.com/proxytoys/proxytoys)
that are used to render automatically the static [website of ProxyToys](https://proxytoys.github.io/). 

# Site Generation

The content of this repository is generated. ProxyToys uses Maven and XSite to generate the pages.
To create an update of the site, you have to edit the sources in the ProxyToys repository.
 
## Requisites
+ Java 5 runtime
+ Maven 3.0
 
## Steps
0. Create a clone of the current repository
0. Create a clone of the [proxytoys Git repository](https://github.com/proxytoys/proxytoys) using
the same base directory.
0. Edit the sources in proxytoys.git/proxytoys/website/src/site/content
0. From the root of proxytoys.git call
		mvn clean package
0. Update the repository with the GitHub pages.
	rsync -cr --progress --delete --exclude=.nojekyll --exclude=.git* --exclude=*.md --exclude=jira --exclude=apidocs ./website/target/xsite/ ../proxytoys.io/
 	 
## After Release
 
Typically you want to update the site after a ProxyToys release. In that case you can call: 
 
	rsync -cr --progress --delete --exclude=.nojekyll --exclude=.git* --exclude=*.md --exclude=jira ./target/checkout/website/target/xsite/ ../proxytoys.io/

Note, that this variant will also replace the javadocs.
