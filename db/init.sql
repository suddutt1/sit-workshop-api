-- Role: admin
-- DROP ROLE IF EXISTS admin;

 CREATE ROLE admin WITH
   LOGIN
   SUPERUSER
   INHERIT
   CREATEDB
   CREATEROLE
   REPLICATION
   ENCRYPTED PASSWORD 'admin4test';

-- Role: appuser
-- DROP ROLE IF EXISTS appuser;

CREATE ROLE appuser WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  ENCRYPTED PASSWORD 'cnp4test';

COMMENT ON ROLE appuser IS 'User ID to be used by applications';

-- Database: mydatabase

-- DROP DATABASE IF EXISTS mydatabase;

CREATE DATABASE quizdb
    WITH
    OWNER = admin
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

\connect quizdb admin;    

CREATE SCHEMA ibm_qus_ans AUTHORIZATION admin;
GRANT ALL ON SCHEMA ibm_qus_ans TO admin;


ALTER DEFAULT PRIVILEGES IN SCHEMA ibm_qus_ans GRANT ALL ON TABLES TO admin WITH GRANT OPTION;

GRANT USAGE ON SCHEMA ibm_qus_ans TO appuser;

-- ibm_qus_ans.user_profile definition

-- Drop table

-- DROP TABLE ibm_qus_ans.user_profile;

CREATE TABLE ibm_qus_ans.user_profile (
  email text NOT NULL,
  "name" text NOT NULL,
  "password" text NOT NULL,
  CONSTRAINT user_profile_pkey PRIMARY KEY (email)
);

-- Permissions

ALTER TABLE ibm_qus_ans.user_profile OWNER TO admin;
GRANT ALL ON TABLE ibm_qus_ans.user_profile TO appuser;



CREATE TABLE ibm_qus_ans.quiz_details (
 
  quiz_id text NOT NULL,
  status text NOT NULL,
  title text NOT NULL,
  create_ts timestamp(6) NOT NULL,
  update_ts timestamp(6) NOT NULL,
  owner_email text NULL,
  CONSTRAINT quiz_details_pkey PRIMARY KEY (quiz_id)
  
);

-- Permissions

ALTER TABLE ibm_qus_ans.quiz_details OWNER TO admin;
GRANT ALL ON TABLE ibm_qus_ans.quiz_details TO appuser;


-- ibm_qus_ans.quiz_details foreign keys

ALTER TABLE ibm_qus_ans.quiz_details ADD CONSTRAINT fk_owner_email FOREIGN KEY (owner_email) REFERENCES ibm_qus_ans.user_profile(email);

-- ibm_qus_ans.quiz_question definition

-- Drop table

-- DROP TABLE ibm_qus_ans.quiz_question;

CREATE TABLE ibm_qus_ans.quiz_question (
  quiz_id text NOT NULL,
  question_id text NOT NULL,
  "options" json NOT NULL,
  answer json NOT NULL,
  answer_type text NOT NULL,
  create_ts timestamp(6) NOT NULL,
  update_ts timestamp(6) NOT NULL,
  score int4 NOT NULL,
  CONSTRAINT quiz_question_pkey PRIMARY KEY (quiz_id, question_id)
);

-- Permissions

ALTER TABLE ibm_qus_ans.quiz_question OWNER TO admin;
GRANT ALL ON TABLE ibm_qus_ans.quiz_question TO appuser;


-- ibm_qus_ans.quiz_question foreign keys

ALTER TABLE ibm_qus_ans.quiz_question ADD CONSTRAINT fk_quiz_id FOREIGN KEY (quiz_id) REFERENCES ibm_qus_ans.quiz_details(quiz_id);



-- ibm_qus_ans.score definition

-- Drop table

-- DROP TABLE ibm_qus_ans.score;

CREATE TABLE ibm_qus_ans.score (
  quiz_id text NOT NULL,
  score_id text NOT NULL,
  user_id text NOT NULL,
  answers json NOT NULL,
  score int4 NOT NULL,
  create_ts timestamp NOT NULL,
  update_ts timestamp NOT NULL,
  CONSTRAINT score_pkey PRIMARY KEY (quiz_id, score_id, user_id),
  CONSTRAINT score_score_id_key UNIQUE (score_id)
);

-- Permissions

ALTER TABLE ibm_qus_ans.score OWNER TO admin;
GRANT ALL ON TABLE ibm_qus_ans.score TO appuser;


-- ibm_qus_ans.score foreign keys

ALTER TABLE ibm_qus_ans.score ADD CONSTRAINT fk_quiz_id FOREIGN KEY (quiz_id) REFERENCES ibm_qus_ans.quiz_details(quiz_id);
ALTER TABLE ibm_qus_ans.score ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES ibm_qus_ans.user_profile(email);