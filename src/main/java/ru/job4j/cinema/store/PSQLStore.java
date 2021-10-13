package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PSQLStore implements Store {
    private static final Logger LOGGER = Logger.getLogger(PSQLStore.class);
    private final BasicDataSource pool = new BasicDataSource();

    public PSQLStore() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties cfg = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream("db.properties")) {
            cfg.load(resourceStream);
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PSQLStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Ticket> findAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(new Ticket(it.getInt("id"), it.getString("session_id"),
                            it.getInt("row"), it.getInt("cell"), null));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return tickets;
    }

    @Override
    public Account findAccountByEmail(String email) {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE email = (?)")) {
            ps.setString(1, email);
            ResultSet it = ps.executeQuery();
            if (it.next()) {
                account = new Account(it.getInt("id"),
                        it.getString("username"),
                        it.getString("email"),
                        it.getString("phone"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return account;
    }

    @Override
    public Account saveAccount(Account account) {
        Account findAccount;
        Account result;
        findAccount = findAccountByEmail(account.getEmail());
        if (findAccount == null) {
            result = create(account);
        } else {
            account.setId(findAccount.getId());
            update(account);
            result = account;
        }
        return result;
    }

    private Account create(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            ResultSet id = ps.getGeneratedKeys();
            if (id.next()) {
                account.setId(id.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateKeyValue("Пользователь с указанным номером телефона уже существует");
            }
        }
        return account;
    }

    private void update(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE account SET username = (?), phone = (?) WHERE email = (?)")) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPhone());
            ps.setString(3, account.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateKeyValue("Пользователь с указанным номером телефона уже существует");
            }
        }
    }

    @Override
    public void saveTicket(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(session_id, row, cell, account_id) VALUES (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getAccount().getId());
            ps.execute();
            ResultSet id = ps.getGeneratedKeys();
            if (id.next()) {
                ticket.setId(id.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateKeyValue("Данное место уже занято");
            }
        }
    }
}
