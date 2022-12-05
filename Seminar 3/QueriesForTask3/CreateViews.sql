CREATE VIEW LESSONS_PER_MONTH AS
SELECT "Private Lessons",
	"Group Lessons",
	ENSEM."Number of Lessons" AS "Ensemble Lessons",
	("Private Lessons" + "Group Lessons" + ENSEM."Number of Lessons") AS "Total Lessons",
	PRIVATE_AND_GROUP."Month",
	PRIVATE_AND_GROUP."Year"
FROM
	(SELECT PRIVATE."Number of Lessons" AS "Private Lessons",
			"group"."Number of Lessons" AS "Group Lessons",
			PRIVATE."Month" AS "Month",
			PRIVATE."Year" AS "Year"
		FROM
			(SELECT COUNT(*) AS "Number of Lessons",
					DATE_PART('month', date) AS "Month",
					DATE_PART('year', date) AS "Year"
				FROM PRIVATE_LESSON
				INNER JOIN BOOKING_TABLE ON PRIVATE_LESSON_DB_ID = PRIVATE_LESSON.DATABASE_ID
				GROUP BY "Year",
					"Month") AS PRIVATE
		FULL OUTER JOIN
			(SELECT COUNT(*) AS "Number of Lessons",
					DATE_PART('month', date) AS "Month",
					DATE_PART('year', date) AS "Year"
				FROM GROUP_LESSON
				INNER JOIN BOOKING_TABLE ON GROUP_LESSON_DB_ID = GROUP_LESSON.DATABASE_ID
				GROUP BY "Year",
					"Month") AS "group" ON PRIVATE."Year" = "group"."Year"
		AND PRIVATE."Month" = "group"."Month") AS PRIVATE_AND_GROUP
FULL OUTER JOIN
	(SELECT COUNT(*) AS "Number of Lessons",
			DATE_PART('month', date) AS "Month",
			DATE_PART('year', date) AS "Year"
		FROM ENSEMBLE
		INNER JOIN BOOKING_TABLE ON ENSEMBLE_DB_ID = ENSEMBLE.DATABASE_ID
		GROUP BY "Year",
			"Month") AS ENSEM ON PRIVATE_AND_GROUP."Year" = ENSEM."Year"
AND PRIVATE_AND_GROUP."Month" = ENSEM."Month"
ORDER BY PRIVATE_AND_GROUP."Year",
	PRIVATE_AND_GROUP."Month";

-- Example on how to use it
-- SELECT *
-- FROM LESSONS_PER_MONTH
-- WHERE "Year" = 2021

CREATE VIEW NUMBER_OF_SIBLINGS AS
SELECT COUNT("Count") AS "Students",
	"Count" AS "Number of Siblings"
FROM
	(SELECT "ID",
			COUNT(*) AS "Count"
		FROM
			(SELECT SIBLING_ID_1 AS "ID"
				FROM BRIDGE_SIBLING
				UNION ALL SELECT SIBLING_ID_2 AS "ID"
				FROM BRIDGE_SIBLING) AS STUDENTS_WITH_SIBLINGS
		GROUP BY "ID") AS COUNTED_SIBLINGS
GROUP BY "Count"
UNION
SELECT COUNT(*) AS "Students",
	0 AS "Number of Siblings"
FROM STUDENT
WHERE NOT EXISTS
		(SELECT SIBLING_ID_1
			FROM BRIDGE_SIBLING
			WHERE SIBLING_ID_1 = STUDENT.DATABASE_ID
				OR SIBLING_ID_2 = STUDENT.DATABASE_ID )
ORDER BY "Number of Siblings";

-- Example on how to use it
-- SELECT *
-- FROM NUMBER_OF_SIBLINGS


CREATE MATERIALIZED VIEW overworked_employees AS 
SELECT DISTINCT INSTRUCTORS.DATABASE_ID AS "Employee_ID",
	INSTRUCTORS.FULL_NAME AS "Name",

	(SELECT COUNT(*)
		FROM PRIVATE_LESSON
		WHERE PRIVATE_LESSON.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID) AS "Private Lessons",

	(SELECT COUNT(*)
		FROM GROUP_LESSON
		WHERE GROUP_LESSON.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID) AS "Group Lessons",

	(SELECT COUNT(*)
		FROM ENSEMBLE
		WHERE ENSEMBLE.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID) AS "Ensemble Lessons",
	(
			(SELECT COUNT(*)
				FROM PRIVATE_LESSON
				WHERE PRIVATE_LESSON.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID) +
			(SELECT COUNT(*)
				FROM GROUP_LESSON
				WHERE GROUP_LESSON.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID) +
			(SELECT COUNT(*)
				FROM ENSEMBLE
				WHERE ENSEMBLE.INSTRUCTOR_DB_ID = INSTRUCTORS.DATABASE_ID)) AS "Total",
	EXTRACT(MONTH FROM date) AS "Month"
FROM INSTRUCTORS
JOIN PRIVATE_LESSON ON INSTRUCTORS.DATABASE_ID = PRIVATE_LESSON.INSTRUCTOR_DB_ID
JOIN GROUP_LESSON ON INSTRUCTORS.DATABASE_ID = GROUP_LESSON.INSTRUCTOR_DB_ID
JOIN ENSEMBLE ON INSTRUCTORS.DATABASE_ID = ENSEMBLE.INSTRUCTOR_DB_ID
JOIN BOOKING_TABLE ON PRIVATE_LESSON.DATABASE_ID = BOOKING_TABLE.PRIVATE_LESSON_DB_ID
OR GROUP_LESSON.DATABASE_ID = BOOKING_TABLE.GROUP_LESSON_DB_ID
OR ENSEMBLE.DATABASE_ID = BOOKING_TABLE.ENSEMBLE_DB_ID
WHERE EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE)
AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE)
ORDER BY "Total" DESC;

-- Example on how to use it
-- SELECT *
-- FROM OVERWORKED_EMPLOYEES
-- WHERE "Total" > 3


CREATE MATERIALIZED VIEW next_week_ensemble_lessons AS 
SELECT *,
	CASE
					WHEN "Max number of students" - "Students signed up" > 0 
					THEN ("Max number of students" - "Students signed up")::text
					ELSE 'Full Booked'
	END AS "Spots still available"
FROM
	(SELECT DISTINCT GENRE AS "Genre",
			CLASSROOM AS "Classroom",
			MIN_NUMBER_OF_STUDENTS AS "Min number of students",
			MAX_NUMBER_OF_STUDENTS AS "Max number of students",

			(SELECT COUNT(*)
				FROM BRIDGE_STUDENT_TO_BOOKING
				WHERE BOOKING_TABLE.DATABASE_ID = BRIDGE_STUDENT_TO_BOOKING.BOOKING_DB_ID) AS "Students signed up",
			CASE
							WHEN DATE_PART('isodow', date) = 1 THEN 'Monday'
							WHEN DATE_PART('isodow', date) = 2 THEN 'Tuesday'
							WHEN DATE_PART('isodow', date) = 3 THEN 'Wednesday'
							WHEN DATE_PART('isodow', date) = 4 THEN 'Thursday'
							WHEN DATE_PART('isodow', date) = 5 THEN 'Friday'
							WHEN DATE_PART('isodow', date) = 6 THEN 'Saturday'
							WHEN DATE_PART('isodow', date) = 7 THEN 'Sunday'
			END AS "Day"
		FROM ENSEMBLE
		INNER JOIN BOOKING_TABLE ON ENSEMBLE.DATABASE_ID = BOOKING_TABLE.ENSEMBLE_DB_ID
		WHERE DATE_PART('week', date) = (DATE_PART('week', CURRENT_DATE) + 1)) AS FOO
ORDER BY "Genre", "Day";

-- Example on how to use it
-- SELECT *
-- FROM NEXT_WEEK_ENSEMBLE_LESSONS