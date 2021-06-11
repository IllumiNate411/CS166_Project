/* THESE ARE THE MOST USED IN QUERIES. NOT EVERY KEYWORD HAS AN INDEX */

/*PATIENT
patient_ID: We must query through existing ID's to find one/make one
*/
CREATE INDEX index_pid ON Patient USING btree(patient_ID);

/*HOSPITAL
hospital_ID: to quickly find which doctor works here
*/
CREATE INDEX index_hid ON Hospital USING btree(hospital_ID);

/*DEPARTMENT
dept_ID: to quickly find what department is in what hospital
dname: to find what doc worked in what dept
*/
CREATE INDEX index_deptID ON Department USING btree(dept_ID);

CREATE INDEX index_dname ON Department USING btree(name);


/*DOCTOR
did: needed to find doctor ids quickly
specialty: needed to find specialty quickly
name: same as above
*/
CREATE INDEX index_did ON Doctor USING btree(doctor_ID);

CREATE INDEX index_specialty ON Doctor USING btree(specialty);

CREATE INDEX index_docname ON Doctor USING btree(name);

/*APPOINTMENT
appnt_ID: quick search up for appt ids
adate: quick search up for available dates
time_slot: quick search up for available time slot
status: quick search up for status
*/
CREATE INDEX index_appID ON Appointment USING btree(appnt_ID);

CREATE INDEX index_adate ON Appointment USING btree(adate);

CREATE INDEX index_ts ON Appointment USING btree(time_slot);

CREATE INDEX index_status ON Appointment USING btree(status);