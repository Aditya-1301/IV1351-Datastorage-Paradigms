--TASK 3 Fourth Query
CREATE VIEW next_week_ensemble_lessons AS 
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
ORDER BY "Genre", "Day"