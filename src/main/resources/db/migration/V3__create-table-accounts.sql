CREATE TABLE account (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    balance NUMERIC(10, 2) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_account_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);