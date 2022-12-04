--TASK 3 Third query

CREATE VIEW OVERWORKED_EMPLOYEES AS
SELECT 
DISTINCT
instructors.database_id AS "Employee_ID",
instructors.full_name as "Name", 
(select count(*) from private_lesson where private_lesson.instructor_db_id = instructors.database_id) AS "Private Lessons",
(select count(*) from group_lesson where group_lesson.instructor_db_id = instructors.database_id) AS "Group Lessons",
(select count(*) from ensemble where ensemble.instructor_db_id = instructors.database_id) AS "Ensemble Lessons",
(
 (SELECT COUNT(*)
	FROM PRIVATE_LESSON
	WHERE PRIVATE_LESSON.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID) +
 (SELECT COUNT(*)
	FROM GROUP_LESSON
	WHERE GROUP_LESSON.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID) +
 (SELECT COUNT(*)
	FROM ENSEMBLE
	WHERE ENSEMBLE.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID)) AS "Total"
FROM instructors
join private_lesson on instructors.database_id = private_lesson.instructor_db_id
join group_lesson on instructors.database_id = group_lesson.instructor_db_id
join ensemble on instructors.database_id = ensemble.instructor_db_id
join booking_table on instructors.database_id = booking_table.database_id
WHERE EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE)
AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE)
AND OVERWORKED_EMPLOYEES.Total > 3
ORDER BY "Total" DESC;
