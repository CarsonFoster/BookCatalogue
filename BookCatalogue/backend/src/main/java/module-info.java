module backend {
    requires java.persistence;
    requires java.naming;
    requires java.sql;
    requires java.transaction;
    requires org.hibernate.orm.core;
    opens com.fostecar000.backend to org.hibernate.orm.core;
    exports com.fostecar000.backend;
}