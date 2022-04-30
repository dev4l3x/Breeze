module domain {
    requires lombok;

    exports dev.asiglesias.domain.repository;
    exports dev.asiglesias.domain.exception.code;
    exports dev.asiglesias.domain.exception;
    exports dev.asiglesias.domain.service;
    exports dev.asiglesias.domain;
}