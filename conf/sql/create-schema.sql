CREATE SEQUENCE diary_id_seq INCREMENT BY 1;
CREATE SEQUENCE tasks_id_seq INCREMENT BY 1;

CREATE TABLE tasks
(
	id BIGINT DEFAULT nextval('tasks_id_seq'::regclass) NOT NULL,
	name VARCHAR NOT NULL,
	is_frozen BOOL DEFAULT 'false' NOT NULL,
	cost DOUBLE PRECISION NOT NULL
) WITHOUT OIDS;
ALTER TABLE tasks ADD CONSTRAINT pktasks PRIMARY KEY (id);


CREATE TABLE execs
(
	id BIGINT DEFAULT nextval('diary_id_seq'::regclass) NOT NULL,
	task_id BIGINT NOT NULL,
	score DOUBLE PRECISION NOT NULL,
	ended_at TIMESTAMP DEFAULT NOW() NOT NULL,
	count INTEGER NOT NULL
) WITHOUT OIDS;
ALTER TABLE execs ADD CONSTRAINT pkexecs PRIMARY KEY (id);


ALTER TABLE execs ADD CONSTRAINT fk_diary_tasks FOREIGN KEY (task_id) REFERENCES tasks (id) ON UPDATE CASCADE ON DELETE CASCADE;


CREATE OR REPLACE VIEW days_productivity
AS
SELECT date_trunc('day', ended_at)::date as day,
       sum(score) AS total_score,
       count(score) AS exec_count,
       count(DISTINCT task_id) AS types_tasks_count
FROM execs
GROUP BY day;

CREATE OR REPLACE VIEW tasks_stat
AS
SELECT id, name, a.*
FROM tasks LEFT JOIN
(SELECT task_id, SUM(score) AS sum_score, sum(count) AS sum_el, count(count) AS times, avg(count) AS avg_time
FROM execs
GROUP BY task_id) a ON tasks.id = a.task_id;


--  select SUM(*) from execs where ended_at between DATE 'today' and DATE 'tomorrow';
