package com.example.user_management_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Date-11/25/2024
 * By Sardor Tokhirov
 * Time-4:30 AM (GMT+5)
 */
@Entity
@Table(name = "verification_numbers")
public class VerificationNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "random_number")
    private Integer randomNumber;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "attempts")
    private Integer attempts = 0;

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public VerificationNumber() {
    }

    public VerificationNumber(String number, Integer randomNumber, LocalDateTime expirationDate) {
        this.number = number;
        this.randomNumber = randomNumber;
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String email) {
        this.number = number;
    }

    public Integer getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(Integer randomNumber) {
        this.randomNumber = randomNumber;
    }
}
