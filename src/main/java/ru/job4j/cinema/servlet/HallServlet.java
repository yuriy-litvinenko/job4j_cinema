package ru.job4j.cinema.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.DuplicateKeyValue;
import ru.job4j.cinema.store.PSQLStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class HallServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(PSQLStore.class);
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Collection<Ticket> tickets = PSQLStore.instOf().findAllTickets();
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        String json = GSON.toJson(tickets);
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = null;
        HttpSession session = req.getSession();
        if (session != null) {
            sessionId = session.getId();
        }
        req.setCharacterEncoding("UTF-8");
        try {
            Account account = PSQLStore.instOf().saveAccount(
                    new Account(
                            req.getParameter("username"),
                            req.getParameter("email"),
                            req.getParameter("phone")
                    )
            );
            PSQLStore.instOf().saveTicket(
                    new Ticket(sessionId,
                            Integer.parseInt(req.getParameter("row")),
                            Integer.parseInt(req.getParameter("cell")),
                            account)
            );
            req.getRequestDispatcher("complete.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (DuplicateKeyValue dkv) {
            resp.sendError(409, dkv.getMessage());
        }
    }
}
