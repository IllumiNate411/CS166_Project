--1--


--2--


--3--


--4--


--5--


--6--


--7--
SELECT D.doctor_ID, D.name, D.specialty, A.status, count(A.status) AS C
FROM Doctor D, Appointment A, has_appointment H
WHERE A.appointment_id = H.appointment_ID AND H.doctor_id = D.doctor_id
GROUP BY D.doctor_ID, D.name, D.specialty, A.status
ORDER BY Desc doctor_ID, C;

--8--
