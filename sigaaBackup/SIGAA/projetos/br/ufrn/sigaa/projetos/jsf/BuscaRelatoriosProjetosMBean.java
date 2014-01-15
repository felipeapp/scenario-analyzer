/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/05/2011'
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.RelatorioAcaoAssociadaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.RelatorioAcaoAssociada;
import br.ufrn.sigaa.projetos.dominio.TipoRelatorioProjeto;


/*******************************************************************************
 * MBean utilizado na busca de relatórios Ações Acadêmicas Associadas em diversas áreas
 * do sub-sistema de projetos.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("buscaRelatoriosProjetosBean")
@Scope("request")
public class BuscaRelatoriosProjetosMBean extends SigaaAbstractController<RelatorioAcaoAssociada> {
	
	
	/** Lista de relatórios de ações associadas localizados. */
	private List<RelatorioAcaoAssociada> relatorios = new ArrayList<RelatorioAcaoAssociada>();
	
	/** Atributos utilizados para a seleção de opções de busca(filtros) em telas de busca. **/
	/** Filtro busca título */
	private boolean checkBuscaTitulo;
	/** Filtro busca tipo relatório */
	private boolean checkBuscaTipoRelatorio;
	/** Filtro busca ano */
	private boolean checkBuscaAno;
	/** Filtro busca período */
	private boolean checkBuscaPeriodo;
	/** Filtro busca servidor */
	private boolean checkBuscaServidor;
	/** Filtro busca período de conclusão */
	private boolean checkBuscaPeriodoConclusao;
	/** Filtro busca edital */
	private Boolean checkBuscaEdital = false;
	
	/** Atributos utilizados para a inserção de informações em telas de buscas. **/
	/** Título ação extensão */
	private String buscaTitulo;
	/** Tipo de relatório */
	private Integer buscaTipoRelatorio;
	/** Ano */
	private Integer buscaAno = CalendarUtils.getAnoAtual();
	/** Data início */
	private Date buscaInicio;
	/** Data fim */
	private Date buscaFim;
	/** Data início conclusão */
	private Date buscaInicioConclusao;
	/** Data fim conclusão */
	private Date buscaFimConclusao;
	/** Edital  */
	private Integer buscaEdital;
	/** Membro de projeto indicado na busca dos relatórios. */
	private MembroProjeto membroProjeto = new MembroProjeto();
	

	
	public BuscaRelatoriosProjetosMBean() {
		obj = new RelatorioAcaoAssociada();
		buscaAno = CalendarUtils.getAnoAtual();
	}
	
	/** 
	 * Inicia a busca de relatórios de projetos.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/menu.jsp</li>
	 * </ul>
	 * 
	 */
	public String iniciarBusca() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		buscaAno = CalendarUtils.getAnoAtual();
		if (relatorios != null) {
		    relatorios.clear();
		}
		return forward(ConstantesNavegacaoProjetos.RELATORIO_PROJETO_BUSCA);		
	}
	
	/**
	 * Lista todos os tipos de relatórios possíveis Utilizado na busca por relatórios.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposRelatorioCombo() {
		return getAll(TipoRelatorioProjeto.class, "id", "descricao");
	}

	
	/**
	 * Busca todos os tipos de relatórios possíveis de acordo com 
	 * os parâmetros especificados. 
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String buscar() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		
		erros.getMensagens().clear();

		if (relatorios != null) {
		    relatorios.clear();
		}

		/* Analisando filtros selecionados */
		String titulo = null;
		Integer idTipoRelatorio = null;
		Integer ano = null;
		Date inicio = null;
		Date fim = null;
		Date inicioConclusao = null;
		Date fimConclusao = null;
		Integer idEdital = null;
		Integer idServidor = null;

		ListaMensagens mensagens = new ListaMensagens();

		// Definição dos filtros e validações

		if (checkBuscaServidor) {
			idServidor = membroProjeto .getServidor().getId();
			ValidatorUtil.validateRequired(idServidor, "Servidor", mensagens);
		}

		if (checkBuscaTitulo) {
			titulo = buscaTitulo;
			ValidatorUtil.validateRequired(titulo, "Título da Ação", mensagens);
		}

		if (checkBuscaTipoRelatorio) {
			idTipoRelatorio = buscaTipoRelatorio;
			ValidatorUtil.validateRequiredId(idTipoRelatorio, "Tipo de Relatório",	mensagens);
		}
		
		if (checkBuscaAno) {
			ano = buscaAno;
			ValidatorUtil.validaInt(ano, "Ano", mensagens);
		}
		
		if (checkBuscaPeriodo) {
			inicio = buscaInicio;
			fim = buscaFim;

			ValidatorUtil.validateRequired(inicio,"Início do Período de Envio", mensagens);			
			ValidatorUtil.validateRequired(fim,"Fim do Período de Envio", mensagens);			

			if ((inicio != null) && (fim != null)) {
				ValidatorUtil.validaInicioFim(inicio, fim, "Data Início", mensagens);
			}
		}
		
		if(checkBuscaPeriodoConclusao){
			inicioConclusao = buscaInicioConclusao;
			fimConclusao = buscaFimConclusao;

			ValidatorUtil.validateRequired(inicioConclusao,"Início do Período de Conclusão", mensagens);			
			ValidatorUtil.validateRequired(fimConclusao,"Fim do Período de Conclusão", mensagens);			

			if ((inicioConclusao != null) && (fimConclusao != null)) {
				ValidatorUtil.validaInicioFim(inicioConclusao, fimConclusao, "Data Início Conclusão", mensagens);
			}
		}
		
		if(checkBuscaEdital) {
			idEdital = getBuscaEdital();
			ValidatorUtil.validaInt(idEdital, "Edital", mensagens);
		}		

		if (!checkBuscaTitulo && !checkBuscaTipoRelatorio && !checkBuscaAno
				&& !checkBuscaPeriodo && !checkBuscaEdital && !checkBuscaServidor && !checkBuscaPeriodoConclusao ) {
			addMensagemErro("Selecione uma opção para efetuar a busca por relatórios.");

		} else {

			try {
				if (mensagens.isEmpty()) {					
					RelatorioAcaoAssociadaDao dao = getDAO(RelatorioAcaoAssociadaDao.class);
					relatorios = dao.buscaRelatorios(titulo, idTipoRelatorio, ano, inicio, fim, inicioConclusao, fimConclusao , idEdital, idServidor);
					if(relatorios.isEmpty()) {
						addMensagemWarning("Nenhum Relatório encontrado com os parâmetros de busca informados.");
					}
				} else {
					addMensagens(mensagens);
				}

			} catch (DAOException e) {
				notifyError(e);
				addMensagemErro("Erro ao Buscar Relatórios.");
			}
		}
		return null;
	}

	public List<RelatorioAcaoAssociada> getRelatorios() {
		return relatorios;
	}

	public void setRelatorios(List<RelatorioAcaoAssociada> relatorios) {
		this.relatorios = relatorios;
	}

	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	public boolean isCheckBuscaTipoRelatorio() {
		return checkBuscaTipoRelatorio;
	}

	public void setCheckBuscaTipoRelatorio(boolean checkBuscaTipoRelatorio) {
		this.checkBuscaTipoRelatorio = checkBuscaTipoRelatorio;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaPeriodo() {
		return checkBuscaPeriodo;
	}

	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
		this.checkBuscaPeriodo = checkBuscaPeriodo;
	}

	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	public boolean isCheckBuscaPeriodoConclusao() {
		return checkBuscaPeriodoConclusao;
	}

	public void setCheckBuscaPeriodoConclusao(boolean checkBuscaPeriodoConclusao) {
		this.checkBuscaPeriodoConclusao = checkBuscaPeriodoConclusao;
	}

	public Boolean getCheckBuscaEdital() {
		return checkBuscaEdital;
	}

	public void setCheckBuscaEdital(Boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}

	public String getBuscaTitulo() {
		return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}

	public Integer getBuscaTipoRelatorio() {
		return buscaTipoRelatorio;
	}

	public void setBuscaTipoRelatorio(Integer buscaTipoRelatorio) {
		this.buscaTipoRelatorio = buscaTipoRelatorio;
	}

	public Integer getBuscaAno() {
		return buscaAno;
	}

	public void setBuscaAno(Integer buscaAno) {
		this.buscaAno = buscaAno;
	}

	public Date getBuscaInicio() {
		return buscaInicio;
	}

	public void setBuscaInicio(Date buscaInicio) {
		this.buscaInicio = buscaInicio;
	}

	public Date getBuscaFim() {
		return buscaFim;
	}

	public void setBuscaFim(Date buscaFim) {
		this.buscaFim = buscaFim;
	}

	public Date getBuscaInicioConclusao() {
		return buscaInicioConclusao;
	}

	public void setBuscaInicioConclusao(Date buscaInicioConclusao) {
		this.buscaInicioConclusao = buscaInicioConclusao;
	}

	public Date getBuscaFimConclusao() {
		return buscaFimConclusao;
	}

	public void setBuscaFimConclusao(Date buscaFimConclusao) {
		this.buscaFimConclusao = buscaFimConclusao;
	}

	public Integer getBuscaEdital() {
		return buscaEdital;
	}

	public void setBuscaEdital(Integer buscaEdital) {
		this.buscaEdital = buscaEdital;
	}

	public MembroProjeto getMembroProjeto() {
		return membroProjeto;
	}

	public void setMembroProjeto(MembroProjeto membroProjeto) {
		this.membroProjeto = membroProjeto;
	}

}
