package ru.job4j.cinema.model;

import java.util.Objects;

public class Ticket {
    private int id;
    private String sessionId;
    private int row;
    private int cell;
    private Account account;

    public Ticket(int id, String sessionId, int row, int cell, Account account) {
        this.id = id;
        this.sessionId = sessionId;
        this.row = row;
        this.cell = cell;
        this.account = account;
    }

    public Ticket(String sessionId, int row, int cell, Account account) {
        this.sessionId = sessionId;
        this.row = row;
        this.cell = cell;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ticket{"
                + "id=" + id
                + ", session_id=" + sessionId
                + ", row=" + row
                + ", cell=" + cell
                + ", account=" + account
                + '}';
    }
}
