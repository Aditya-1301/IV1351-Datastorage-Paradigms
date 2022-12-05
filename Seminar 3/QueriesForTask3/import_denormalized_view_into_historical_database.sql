INSERT INTO STUDENTS_AND_LESSONS
SELECT *
FROM DBLINK('dbname=postgres password=password user=postgres dbname=SoundGood',
'select * from student_lesson_price') 
AS T1(STUDENT VARCHAR(500), STUDENT_ID VARCHAR(100), DISCOUNT text, 
	  LESSON_ID VARCHAR(100), "Type" text, "Date" date, 
	  CLASSROOM VARCHAR(500), INSTRUCTOR VARCHAR(100), 
	  EMPLOYEE_ID VARCHAR(100), BASE_PRICE integer, 
	  SIBLING_DISCOUNT_MODIFIER real);