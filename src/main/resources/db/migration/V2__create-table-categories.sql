CREATE TABLE category (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) CHECK (type IN ('INCOME', 'EXPENSE')),
    user_id UUID NOT NULL,
    CONSTRAINT fk_category_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);