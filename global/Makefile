all:
	cd sources/ && $(MAKE)
	tar czvf ima_sources.tgz sources bin 
	cp ima_sources.tgz ../../Enseignants/JDeca/docker/ima_sources.tgz

realclean:
	cd sources/ && $(MAKE) realclean
	rm -f ima_sources.tgz

# Make all files readable, and propagate the 'x' bit from user to
# group & other.
set-perms:
	find . -type f -exec chmod a+r {} \+
	find . -type d -exec chmod a+rx {} \+
	find . -perm -u+x -exec chmod a+x {} \+
