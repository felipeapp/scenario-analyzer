/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 19/10/2011
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.relatorios.dao.RelatorioCreditosIntegralizadosDao;

/**
 * MBean responsável por gerar os Relatórios de crétidos integralizados dos alunos de pós-graduação.
 * 
 * @author Arlindo Rodrigues
 */
@Component("relatorioCreditosIntegralizadosMBean") @Scope("request")
public class RelatorioCreditosIntegralizadosMBean extends SigaaAbstractController<DiscenteStricto> {
	
	/** Programa atual selecionado */
	private Unidade unidade;
	/** Nível de ensino selecionado (mestrado ou doutorado)*/
	private char nivel;
	/** Lista de discente */
	private List<Map<String,Object>> listaDiscente = new ArrayList<Map<String,Object>>();
	
	/** Construtor padrão */
	public RelatorioCreditosIntegralizadosMBean() {
		unidade = new Unidade();
		nivel = '0';
	}
	
	/**
	 * Inicia o relatório para informar os parâmetros para sua exibição
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar(){		
		return forward(getFormPage());
	}
	
	/**
	 * Gera o relatório de crédidos integralizados
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/relatorios/creditos_integralizados/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */	
	public String gerarRelatorio() throws DAOException{
		
		if (isPortalPpg() && ValidatorUtil.isEmpty(unidade))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");
		
		if (nivel == '0')
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nível");	
		
		if (hasErrors())
			return null;
		
		RelatorioCreditosIntegralizadosDao dao = getDAO(RelatorioCreditosIntegralizadosDao.class);
		try {
			if (isPortalCoordenadorStricto())
				unidade = getProgramaStricto();
			else
				unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class, "id", "nome");
			
			listaDiscente = dao.findCreditosIntegralizados(unidade.getId(), nivel);
			
			
			if (ValidatorUtil.isEmpty(listaDiscente)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getViewPage());
	}
	
	/**
	 * Retorna a descrição do nível selecionado
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/relatorios/creditos_integralizados/relatorio.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDescricaoNivel(){
		return NivelEnsino.getDescricao(nivel);
	}
	
	/**
	 * Form para geração do relatório
	 */
	@Override
	public String getFormPage() {
		return "/stricto/relatorios/creditos_integralizados/form.jsf";
	}
	
	/**
	 * JSP do caminho do relatório
	 */
	@Override
	public String getViewPage() {
		return "/stricto/relatorios/creditos_integralizados/relatorio.jsf";
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public List<Map<String, Object>> getListaDiscente() {
		return listaDiscente;
	}

	public void setListaDiscente(List<Map<String, Object>> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}
}
