CREATE OR REPLACE VIEW my_records
AS
  SELECT date_trunc('day', ended_at)::date as day,
    task_id,
         SUM(count) AS sum_count,
         SUM(score) AS sum_score
  FROM execs
  GROUP BY day,task_id;

-------------------------------------------------------------------------

CREATE OR REPLACE VIEW tasks_stat
AS
  SELECT id, name, sum_score, sum_el, times, avg_time, ago, day_max_score, day_max_count, max_at_time,today_count
  FROM tasks LEFT JOIN
    (SELECT task_id,
       SUM(score) AS sum_score,
       SUM(count) AS sum_el,
       MAX(count) AS max_at_time,
       COUNT(count) AS times,
       ROUND(AVG(count), 2) AS avg_time,
       CAST(CAST(now() AS date) - CAST(MAX(ended_at) AS date) AS INT) AS ago
     FROM execs GROUP BY task_id) a ON tasks.id = a.task_id
    JOIN
    (SELECT task_id,
       MAX(sum_score) AS day_max_score,
       MAX(sum_count) AS day_max_count
     FROM my_records GROUP BY task_id) maxes ON tasks.id = maxes.task_id
    LEFT JOIN
    (SELECT task_id, SUM(count) AS today_count
     FROM execs WHERE CAST(ended_at AS date) = CAST(now() AS date)
     GROUP BY task_id
    ) today ON tasks.id = today.task_id
  WHERE tasks.is_frozen = FALSE;

