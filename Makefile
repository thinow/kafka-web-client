.PHONY: setup lock install run

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

run: install
	python -m application.runner
