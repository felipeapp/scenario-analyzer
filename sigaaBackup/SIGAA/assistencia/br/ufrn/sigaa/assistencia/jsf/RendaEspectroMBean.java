/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.sae.RendaEspectroDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.SituacaoSocioEconomicaDiscente;

/**
 * MBean respons�vel por consultar espectros de renda
 *
 * @author Jean Guerethes
 *
 */
@SuppressWarnings("unchecked")
@Component("rendaEspectro")
@Scope("request")
public class RendaEspectroMBean extends SigaaAbstractController {

	public final String JSP_RENDA_ESPECTRO = "/sae/Renda/renda.jsp";

	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();

	private List<ClasseEconomica> classeEconomica;
	
	public RendaEspectroMBean() {
	
	}
	/**
	 * Esse m�todo e chamado no menu DAE, Portal da Reitoria e SAE.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sae/menu.jsp  	
	 * 	<li>portais/rh_plan/abas/graduacao.jsp
	 * 	<li>graduacao/menus/relatorios_dae.jsp
	 * </ul>
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRendaEspectro() throws DAOException, ArqException {
		RendaEspectroDao dao = getDAO(RendaEspectroDao.class);
		lista = dao.findRendaEspectro();
		return forward(JSP_RENDA_ESPECTRO);
	}

	/**
	 * Inicia o relat�rio de espetro de renda com base nas informa��es fornecidas 
	 * pelos alunos na matr�cula
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sae/menu.jsp  </ul>
	 * @return
	 */
	public String iniciarMatricula() {
		
		if (classeEconomica == null)
			classeEconomica = new ArrayList<ClasseEconomica>();
			
		RendaEspectroDao dao = getDAO(RendaEspectroDao.class);
		Map resultado = dao.contabilizarEspectroRendaMatricula(SituacaoSocioEconomicaDiscente.SALARIO_MINIMO);

		Long total = (Long) resultado.get("total");
		
		for (Iterator it = resultado.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();

			if (!((String) e.getKey()).equals("total")) {
				ClasseEconomica classe = new ClasseEconomica();
				classe.setNivel((String) e.getKey());
				classe.setValorBruto((Long) e.getValue());
				classe.calcularPercentual(total);
				classeEconomica.add(classe);
			}
			
		}
		
		
		return forward("/sae/Renda/espectro_universidade.jsp");
	}	
	
	public int getNumeroRegistrosEncontrados() {
		if(lista!=null)
			return lista.size();
		else
			return 0;
	}

	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}
	
	public List<ClasseEconomica> getClasseEconomica() {
		return classeEconomica;
	}
	public void setClasseEconomica(List<ClasseEconomica> classeEconomica) {
		this.classeEconomica = classeEconomica;
	}


	/**
	 * Classe para encapsular a classe econ�mica
	 * 
	 * @author Henrique Andr�
	 */
	public class ClasseEconomica {
		private String nivel;
		private Long valorBruto;
		private Double valorPercentual;

		/**
		 * Porcentagem formatada
		 * @return
		 */
		public String getPorcentagemFormatada() {
			return Formatador.getInstance().formatarDecimal1(valorPercentual);
		}
		
		public String getNivel() {
			return nivel;
		}
		/**
		 * Obt�m o valor em % sobre total
		 * @param totalPessoas
		 */
		public void calcularPercentual(double totalPessoas) {
			valorPercentual = ((100*valorBruto) / totalPessoas);
		}		
		
		public void setNivel(String nivel) {
			this.nivel = nivel;
		}
		public Long getValorBruto() {
			return valorBruto;
		}
		public void setValorBruto(Long valorBruto) {
			this.valorBruto = valorBruto;
		}
		public Double getValorPercentual() {
			return valorPercentual;
		}

		/**
		 * Descri��o do n�vel
		 * @return
		 */
		public String getDescricao() {
			if (nivel.equals("a"))
				return "Classe Alta";
			else if (nivel.equals("b"))
				return "Classe M�dia Alta";
			else if (nivel.equals("c"))
				return "Classe M�dia Baixa";
			else if (nivel.equals("d"))
				return "Classe Baixa";
			
			return null;
		}
		
	}
	
}