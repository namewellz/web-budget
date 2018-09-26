/*
 * Copyright (C) 2015 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.domain.entities.registration;

import br.com.webbudget.domain.entities.PersistentEntity;
import br.com.webbudget.domain.exceptions.BusinessLogicException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.REGISTRATION;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.REGISTRATION_AUDIT;

/**
 * The representation of a card in the application
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/04/2014
 */
@Entity
@Audited
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "cards", schema = REGISTRATION)
@AuditTable(value = "cards", schema = REGISTRATION_AUDIT)
public class Card extends PersistentEntity {

    @Getter
    @Setter
    @NotBlank(message = "{card.name}")
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Getter
    @Setter
    @NotBlank(message = "{card.number}")
    @Column(name = "number", nullable = false, length = 45)
    private String number;
    @Getter
    @Setter
    @NotBlank(message = "{card.flag}")
    @Column(name = "flag", nullable = false, length = 45)
    private String flag;
    @Getter
    @Setter
    @Column(name = "credit_limit")
    private BigDecimal creditLimit;
    @Getter
    @Setter
    @Column(name = "expiration_day")
    private Integer expirationDay;
    @Getter
    @Setter
    @NotBlank(message = "{card.owner}")
    @Column(name = "owner", nullable = false, length = 45)
    private String owner;
    @Getter
    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "id_wallet")
    private Wallet wallet;

    /**
     * Default constructor
     */
    public Card() {
        this.active = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() throws BusinessLogicException {
        if (this.cardType == CardType.DEBIT && this.wallet == null) {
            throw new BusinessLogicException("error.card.no-debit-wallet");
        }
    }

    /**
     * A way more elegant to see the name of a card
     *
     * @return the more aesthetic name of this card
     */
    public String getReadableName() {

        final StringBuilder builder = new StringBuilder();

        builder.append(this.name);
        builder.append(" - ");

        if (this.number.length() > 3) {
            builder.append(this.number.substring(this.number.length() - 4,
                    this.number.length()));
        } else {
            builder.append(this.number);
        }

        builder.append(" - ");
        builder.append(this.flag);

        return builder.toString();
    }

    /**
     * This method provide a secure way to display the number of the card
     *
     * @return the first and last four numbers of the card
     */
    public String getSecureNumber() { // FIXME check if this is used

        final StringBuilder secured = new StringBuilder();

        if (this.number != null && this.number.length() >= 8) {

            secured.append(this.number.substring(0, 2));

            for (int i = 0; i < (this.number.length() - 2); i++) {
                secured.append("*");
            }
            secured.append(this.number.substring(
                    this.number.length() - 4, this.number.length()));
        } else {
            return this.number;
        }

        return secured.toString();
    }

    /**
     * Use this method to check if the card is a credit card or not
     *
     * @return <code>true</code> for credit card, <code>false</code> otherwise
     */
    public boolean isCreditCard() {
        return this.cardType == CardType.CREDIT;
    } // FIXME check if this is used

    /**
     * Use this method to check if the card is a debit card or not
     *
     * @return <code>true</code> for debit card, <code>false</code> otherwise
     */
    public boolean isDebitCard() {
        return this.cardType == CardType.DEBIT;
    } // FIXME check if this is used
}
