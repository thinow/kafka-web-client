.PHONY: setup lock install test run build

VERSION=$(shell git log -1 --pretty=format:"%H" | cut -c -16)

# source : https://stackoverflow.com/questions/10858261/abort-makefile-if-variable-not-set
check_defined = \
    $(strip $(foreach 1,$1, \
        $(call __check_defined,$1,$(strip $(value 2)))))
__check_defined = \
    $(if $(value $1),, \
      $(error Undefined $1$(if $2, ($2))))

setup:
	python3.7 -m venv venv

lock:
	$(call check_defined, VIRTUAL_ENV, please use a virtual environment)
	pip freeze > requirements.txt

install:
	$(call check_defined, VIRTUAL_ENV, please use a virtual environment)
	pip install -r requirements.txt

test: install
	python -m unittest

run: install
	python -m application.runner

build:
	rm -rf ./build/context
	mkdir -p ./build/context
	cp -Rv application ./build/context
	cp -v requirements.txt ./build/context
	docker build -t not-named-yet:$(VERSION) ./build
