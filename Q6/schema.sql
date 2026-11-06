CREATE TABLE IF NOT EXISTS events (
 id INT PRIMARY KEY,
 name VARCHAR(80) NOT NULL UNIQUE
);
INSERT IGNORE INTO events(id,name) VALUES (1,'Music'),(2,'Dance'),(3,'Drama');
CREATE TABLE IF NOT EXISTS registrations (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 student_id VARCHAR(9) NOT NULL,
 student_name VARCHAR(80) NOT NULL,
 event_id INT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT unique_student_event UNIQUE(student_id,event_id),
 CONSTRAINT event_fk FOREIGN KEY(event_id) REFERENCES events(id),
 INDEX event_page(event_id,id)
);
CREATE TABLE IF NOT EXISTS fees (
 receipt VARCHAR(36) PRIMARY KEY,
 register_no VARCHAR(9) NOT NULL,
 amount DECIMAL(12,2) NOT NULL CHECK(amount>0)
);
