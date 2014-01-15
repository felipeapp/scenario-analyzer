/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 17/02/2011
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.relatorios.dao.RelatorioTaxaSucessoStrictoSensuDao;

/**
 * MBean responsável por gerar os Relatórios vinculado a taxa de sucesso de Stricto Sensu.
 *
 * @author arlindo
 */
@Component("relatorioTaxaSucessoStricto") @Scope("request")
public class RelatorioTaxaSucessoStrictoSensuMBean extends SigaaAbstractController<DiscenteStricto> {
	
	/** Lista com o resultado do relatório */
	private List<Map<String,Object>> listagem = new ArrayList<Map<String,Object>>();
	
	/** Lista com o resultado dos detalhes relatório */
	private List<Map<String,Object>> detalhes = new ArrayList<Map<String,Object>>();
	
	/** Unidade selecionada */
	private Unidade unidade = new Unidade();
	
	/** Curso selecionado */
	private Curso curso = new Curso();
	
	/** Nível selecionado (Doutorado ou Mestrado)*/
	private Character nivel;
	
	/** Ano de referência da defesa para emissão do relatório*/
	private Integer anoDefesa;

	/** Ano de referência do ingresso para emissão do relatório*/
	private Integer anoIngresso;
	
	/**
	 * Inicia o fomulário para preenchimento dos dados para emissão do relatório
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *   <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarTaxaSucesso() throws SegurancaException{
		checkRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG );
		anoDefesa = CalendarUtils.getAnoAtual();
		anoIngresso = CalendarUtils.getAnoAtual() - 2; 
		return forward(getFormPage());
	}
	
	/**
	 * Gera o relatório
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>sigaa.war/stricto/relatorios/taxa_sucesso/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException{
		
		ListaMensagens erros = new ListaMensagens(); 
		
		if (nivel == null || nivel.equals('0'))
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nível");
		
		if (anoIngresso == null || anoIngresso <= 0)
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano de Referência do Ingresso");
		
		if (anoDefesa == null || anoDefesa <= 0)
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano de Referência da Defesa");
		
		if (anoDefesa != null && anoIngresso != null && anoIngresso > anoDefesa)
			erros.addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Ano de Referência do Ingresso", "Ano de Referência da Defesa");
		
		if (isPortalCoordenadorStricto())
			unidade = getProgramaStricto();
		else if (unidade.getId() > 0)
			unidade = getGenericDAO().findByPrimaryKey(unidade.getId(), Unidade.class, "id", "nome");
		
		if (isPortalPpg() && unidade.getId() < 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");

     	if (!erros.isEmpty()){
     		addMensagens(erros);
     		return null;
		}
     	
		RelatorioTaxaSucessoStrictoSensuDao dao = getDAO(RelatorioTaxaSucessoStrictoSensuDao.class);
		try {
			listagem = dao.findTaxaSucesso(unidade.getId(), nivel, anoIngresso, anoDefesa);

			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			for ( Map<String,Object> vlistaDiscente : listagem){
				vlistaDiscente.put("nivel", NivelEnsino.getDescricao(vlistaDiscente.get("nivel").toString().toCharArray()[0]));
			}				
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getViewPage());
	}
	
	/**
	 * Exibe o detalhamento do relatório
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>sigaa.war/stricto/relatorios/taxa_sucesso/taxa_sucesso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String exibirDetalhes() throws DAOException{

		int idCurso = getParameterInt("idCurso", 0);
		String tipo = getParameter("tipo");
		
		RelatorioTaxaSucessoStrictoSensuDao dao = getDAO(RelatorioTaxaSucessoStrictoSensuDao.class);
		try {
			if ("D".equals(tipo))			
				detalhes = dao.findDefesas(idCurso, nivel, anoIngresso, anoDefesa);
			else if ("I".equals(tipo))
				detalhes = dao.findIngressos(idCurso, nivel, anoIngresso);

			if (ValidatorUtil.isEmpty(detalhes)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			curso = new Curso();
			if (idCurso > 0)
				curso = getGenericDAO().findByPrimaryKey(idCurso, Curso.class, "id", "nome");
			
			for ( Map<String,Object> vlistaDiscente : detalhes){
				vlistaDiscente.put("nivel", NivelEnsino.getDescricao(vlistaDiscente.get("nivel").toString().toCharArray()[0]));
			}				
		} finally {
			if (dao != null)
				dao.close();
		}		
		
		return forward(getDetalhesPage());
	}
	
	@Override
	public String getFormPage() {
		return "/stricto/relatorios/taxa_sucesso/form.jsp";
	}
	
	@Override
	public String getViewPage() {
		return "/stricto/relatorios/taxa_sucesso/taxa_sucesso.jsp";
	}
	
	public String getDetalhesPage() {
		return "/stricto/relatorios/taxa_sucesso/detalhes_discente.jsp";
	}

	public List<Map<String, Object>> getListagem() {
		return listagem;
	}

	public void setListagem(List<Map<String, Object>> listagem) {
		this.listagem = listagem;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public String getDescricaoNivel() {
		return NivelEnsino.getDescricao(nivel);
	}

	public Integer getAnoDefesa() {
		return anoDefesa;
	}

	public void setAnoDefesa(Integer anoDefesa) {
		this.anoDefesa = anoDefesa;
	}

	public Integer getAnoIngresso() {
		return anoIngresso;
	}

	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public List<Map<String, Object>> getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(List<Map<String, Object>> detalhes) {
		this.detalhes = detalhes;
	}
}
