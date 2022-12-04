--TASK 3 First Query
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