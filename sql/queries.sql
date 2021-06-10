--1--
INSERT INTO Doctor
SELECT 9999, 'Some Doctor', 'Some Specialty', 42
WHERE NOT EXISTS
(
  SELECT *
  FROM Doctor
  WHERE doctor_ID = 9999
);

--2--
INSERT INTO Patient
SELECT 9999, 'Some Patient', 'M', 99, 'Some Address', 9
WHERE NOT EXISTS
(
  SELECT *
  FROM Patient
  WHERE Patient_ID = 9999
);

--3--
INSERT INTO Appointment
SELECT 9999, '2021-01-01', '1:00-2:00', 'PA'
WHERE NOT EXISTS
(
  SELECT *
  FROM Appointment
  WHERE appnt_ID = 9999
);

--4--


--5--
SELECT A.appnt_ID, A.adate, A.time_slot, A.status
FROM Appointment A
WHERE A.adate <= '2021-01-01' AND A.adate >= '2021-01-01' AND A.appnt_ID IN
(
  SELECT A.appnt_ID
  FROM Appointment A, has_appointment H
  WHERE H.doctor_ID = 9999 AND H.appt_ID = A.appnt_ID
);

--6--
SELECT A.appnt_ID, A.time_slot, A.status
FROM Appointment A, searches S
WHERE A.adate = '2021-01-01' AND A.appnt_ID = S.aid AND S.hid IN
(
  SELECT S.hid
  FROM Hospital H, searches S
  WHERE H.name = 'Some Hospital' AND H.hospital_ID = S.hid
);

--7--
SELECT D.doctor_ID, D.name, D.specialty, A.status, count(A.status) AS C
FROM Doctor D, Appointment A, has_appointment H
WHERE A.appnt_ID = H.appt_ID AND H.doctor_ID = D.doctor_ID
GROUP BY D.doctor_ID, D.name, D.specialty, A.status
ORDER BY C Desc;

--8--
SELECT D.doctor_ID, D.name, D.specialty, count(S.pid)
FROM Doctor D, Searches S, has_appointment H, Appointment A
WHERE A.status = 'PA' AND A.appnt_ID = S.aid AND S.aid = H.appt_id AND H.doctor_id = D.doctor_id
GROUP BY D.doctor_ID, D.name, D.specialty;
