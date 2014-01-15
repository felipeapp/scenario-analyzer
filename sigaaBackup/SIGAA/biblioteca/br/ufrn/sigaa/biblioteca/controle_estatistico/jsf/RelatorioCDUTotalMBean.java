package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.RelatorioCDUTotal;


/**
 * 
 * MBean para preparar os dados do relatório de Total de Itens e Títulos por CDU
 *
 * @author Fred_Castro
 * @since 31/10/2008
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
@Component(value="relatorioCDUTotal")
@Scope(value="request")
public class RelatorioCDUTotalMBean extends SigaaAbstractController {

	public static final String PAGINA_RELATORIO_CDU_TOTAL = "/biblioteca/controle_estatistico/relatorioCDUTotal.jsp";
	
	
	ArrayList <RelatorioCDUTotal> relatorioCDUTotal;

	public String gerar(){
		return telaRelatorioCDUTotal();
	}

	public ArrayList <RelatorioCDUTotal> getContagemPorClasse() throws HibernateException, DAOException{
		if (relatorioCDUTotal == null){
			relatorioCDUTotal = new ArrayList <RelatorioCDUTotal> ();
	
				String sql =
					"select t.classe_principal_cdu," +
					"(select count(t2.id) from biblioteca.titulo_catalografico t2 " +
					"where t2.classe_principal_cdu = t.classe_principal_cdu) as titulos, " +
					"(select count(i.id) " +
					"from biblioteca.item_catalografico i join " +
					"biblioteca.titulo_catalografico t2 on " +
					"i.id_titulo_catalografico = t2.id and " +
					"t2.classe_principal_cdu = t.classe_principal_cdu) as itens " +
					"from biblioteca.titulo_catalografico t group by t.classe_principal_cdu " +
					"order by t.classe_principal_cdu";
	
				Query q = getGenericDAO().getSession().createSQLQuery(sql);
	
				List rs = q.list();
				Iterator it = rs.iterator();
				while (it.hasNext()){
					Object [] linha = (Object[]) it.next();
					relatorioCDUTotal.add(new RelatorioCDUTotal(
							""+linha[0],
							""+linha[1],
							""+linha[2]
					));
				}
		}

		return relatorioCDUTotal;
	}
	
	public String telaRelatorioCDUTotal(){
		return forward(PAGINA_RELATORIO_CDU_TOTAL);
	}
	
}