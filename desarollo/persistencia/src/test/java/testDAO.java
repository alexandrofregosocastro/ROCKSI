import mx.avanti.desarollo.dao.ClienteDAO;
import mx.avanti.desarollo.persistence.HibernateUtil;
import mx.desarollo.entity.Cliente;

import java.util.List;

public class testDAO {

    public static void main(String[] args) {
        ClienteDAO ClienteDAO = new ClienteDAO(HibernateUtil.getEntityManager());



        /*for (Cliente Cliente : ClienteDAO.listarTodos()) {
            System.out.println(Cliente + "|| id [" + Cliente.getIdCliente()+ "]");
        }*/
    }
}
