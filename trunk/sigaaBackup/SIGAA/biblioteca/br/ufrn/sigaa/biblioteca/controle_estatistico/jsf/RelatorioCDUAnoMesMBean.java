/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 20/11/2008
 *
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;


/**
 * 
 * MBean para preparar os dados do relatório de Total e por mês de Itens e Títulos por CDU 
 *
 * @author Fred_Castro
 * @since 10/11/2008
 * @version 1.0
 *
 */
@Component(value="relatorioCDUAnoMes")
@Scope(value="request")
public class RelatorioCDUAnoMesMBean extends SigaaAbstractController<RelatorioCDUAnoMesMBean> {
	
	public static final String PAGINA_RELATORIO_CDU_ANO_MES = "/biblioteca/controle_estatistico/relatorioCDUAnoMes.jsp";
	public static final String PAGINA_FILTRO_RELATORIO_CDU_ANO_MES = "/biblioteca/controle_estatistico/formRelatorioCDUAnoMes.jsp";
	
	private int[][] matriz;
	private int ano;
	
	public String entrar(){
		matriz = null;
		return telaFiltroRelatorioCDUAnoMes();
	}

	public String gerar(){
		return telaRelatorioCDUAnoMes();
	}
		
	public int[][] getContagem() throws HibernateException, DAOException{
		if (matriz == null){
			
			String sql =
				"select " +
				"c.codigo_classe as classe, " +
				"sum(c.quantidade_material_consultado) as quantidade, " +
				"extract(month from m.data) as mes, " +
				"extract(year  from m.data) as ano " +
				"from biblioteca.classe_material_consultado c " +
				"join biblioteca.moviment_diario_consulta_materiais m " +
				"on m.id_moviment_diario_consulta_materiais = c.id_moviment_diario_consulta_materiais " +
				"and extract(year from m.data) >= " + ano + " " +
				"and extract(year from m.data) <= " + ano + " " +
				"group by ano, mes, c.codigo_classe " +
				"order by ano, mes, codigo_classe";
	
			Query q = getGenericDAO().getSession().createSQLQuery(sql);

			@SuppressWarnings("unchecked")
			List <Object>  rs = q.list();
			Iterator <Object> it = rs.iterator();
									
			/*
			 * Os dados do relatório serão guardados em uma matriz 13x11,
			 * onde as 12 primeiras linhas e colunas representam as quantidades
			 * de itens pesquisados para os meses e classes na ordem crescente
			 * Classes-> 0 1 2 3 4 5 6 7 8 9 Total por mes
			 *                               |
			 *   Janeiro 0 0 0 0 0 0 0 0 0 0 0
			 * Fevereiro 0 0 0 0 0 0 0 0 0 0 0
			 *     Marco 0 0 0 0 0 0 0 0 0 0 0
			 *     Abril 0 0 0 0 0 0 0 0 0 0 0
			 *      Maio 0 0 0 0 0 0 0 0 0 0 0
			 *     Junho 0 0 0 0 0 0 0 0 0 0 0
			 *     Julho 0 0 0 0 0 0 0 0 0 0 0
			 *    Agosto 0 0 0 0 0 0 0 0 0 0 0
			 *  Setembro 0 0 0 0 0 0 0 0 0 0 0
			 *   Outubro 0 0 0 0 0 0 0 0 0 0 0
			 *  Novembro 0 0 0 0 0 0 0 0 0 0 0
			 *  Dezembro 0 0 0 0 0 0 0 0 0 0 0
			 *     Total 0 0 0 0 0 0 0 0 0 0 0 <- Total Geral
			 * por classe
			 */
			
			matriz = new int[13][12];
			
			while (it.hasNext()){
				Object [] linha = (Object[]) it.next();
				
				int classe = Integer.parseInt(""+linha[0]) + 1;
				int quantidade = Integer.parseInt(""+linha[1]);
				int mes = Integer.parseInt((""+linha[2]).replace(".0", "")) - 1;
				
				// Seta o valor da quantidade para aquela classe e mês
				matriz[mes][classe] = quantidade;
				// Soma a quantidade ao total do mês
				matriz[mes][11] += quantidade;
				// Soma a quantidade ao total da classe
				matriz[12][classe] += quantidade;
				// Soma a quantidade ao total geral
				matriz[12][11] += quantidade;
			}
		}

		return matriz;
	}
	
	// telas de navegação
	
	public String telaRelatorioCDUAnoMes(){
		return forward(PAGINA_RELATORIO_CDU_ANO_MES);
	}
	
	public String telaFiltroRelatorioCDUAnoMes(){
		return forward(PAGINA_FILTRO_RELATORIO_CDU_ANO_MES);
	}
	
	//setters e getters
	
	public void setAno(int ano){
		this.ano = ano;
	}
	
	public int getAno(){
		return ano;
	}
}