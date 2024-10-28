INSERT INTO users (username, password, enabled)
values ('user',
        '{noop}pass',
        1);

INSERT INTO users (username, password, enabled)
values ('admin',
        '{noop}pass',
        1);

INSERT INTO authorities (username, authority)
values ('user', 'ROLE_USER');

INSERT INTO authorities (username, authority)
values ('admin', 'ROLE_ADMIN');