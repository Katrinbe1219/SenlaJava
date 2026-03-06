CREATE TABLE IF NOT EXISTS user_account (
	id SERIAL PRIMARY KEY,
	balance INT
);

CREATE TABLE IF NOT EXISTS  user_transitions(
	id SERIAL PRIMARY KEY,
	sender_id INT NOT NULL,
	receiver_id INT NOT NULL,
	amount INT,
	status   CHAR CHECK (status IN ('R','N')),

	FOREIGN KEY (sender_id) REFERENCES user_account(id),
	FOREIGN KEY (receiver_id) REFERENCES user_account(id)
);
