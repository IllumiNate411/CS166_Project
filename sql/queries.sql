--1--
INSERT INTO Doctor
SELECT 9999, 'Some Doctor', 'Some Specialty'
WHERE NOT EXISTS (
  SELECT *
  FROM Doctor
  WHERE doctor_id = 9999
);

--2--
INSERT INTO Patient
SELECT 9999, 'Some Patient', 'M', 99, 'Some Address', 9
WHERE NOT EXISTS (
  SELECT *
  FROM Patient
  WHERE Patient_id = 9999
);

--3--
INSERT INTO Appointment
SELECT 9999, '2021-01-01', '1:00-2:00', 'PA'
WHERE NOT EXISTS (
  SELECT *
  FROM Appointment
  WHERE appointment_id = 9999
);

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
