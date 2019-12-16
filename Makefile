.PHONY: setup lock install test run prepare-build build

APP_DIRECTORY=./application
BUILD_CONTEXT_DIRECTORY=./build/context
DOCKER_IMAGE_NAME=kafka-web-client
VERSION=$(shell date +%Y%m%d)

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

prepare-build:
	@echo Creating build context directory...
	rm -rf $(BUILD_CONTEXT_DIRECTORY)
	mkdir -p $(BUILD_CONTEXT_DIRECTORY)
	@echo Copying application files into the build context directory...
	cp -Rv application requirements.txt $(BUILD_CONTEXT_DIRECTORY)
	@echo Removing files from the build context directory...
	find $(BUILD_CONTEXT_DIRECTORY) \
	    -name __pycache__ \
	    -or -name *.cpython* \
	    -or -name test_*.py \
	    | xargs rm -rfv

build: prepare-build
	$(call check_defined, DOCKER_IMAGE_PREFIX)
	docker build -t $(DOCKER_IMAGE_PREFIX)/$(DOCKER_IMAGE_NAME):$(VERSION) ./build
