CREATE TABLE transacions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    type VARCHAR(10) CHECK (type IN ('INCOME', 'EXPENSE')),
    amount NUMERIC(10, 2) NOT NULL,
    description VARCHAR(100) NOT NULL,
    date DATE NOT NULL,

    account_id UUID NOT NULL,
    category_id UUID,

    CONSTRAINT fk_transaction_account
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id)
        REFERENCES category(id)
        ON DELETE SET NULL
);