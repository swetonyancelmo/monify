package com.swetonyancelmo.monify.domain;

import com.swetonyancelmo.monify.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    private TransactionType type;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Length(max = 100)
    private String description;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_transaction")
    )
    private Account account;

    @ManyToOne
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_category_transaction")
    )
    private Category category;

}
