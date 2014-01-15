/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * MBean utilizado na busca de relat�rios A��es Acad�micas Associadas em diversas �reas
 * do sub-sistema de projetos.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("buscaRelatoriosProjetosBean")
@Scope("request")
public class BuscaRelatoriosProjetosMBean extends SigaaAbstractController<RelatorioAcaoAssociada> {
	
	
	/** Lista de relat�rios de a��es associadas localizados. */
	private List<RelatorioAcaoAssociada> relatorios = new ArrayList<RelatorioAcaoAssociada>();
	
	/** Atributos utilizados para a sele��o de op��es de busca(filtros) em telas de busca. **/
	/** Filtro busca t�tulo */
	private boolean checkBuscaTitulo;
	/** Filtro busca tipo relat�rio */
	private boolean checkBuscaTipoRelatorio;
	/** Filtro busca ano */
	private boolean checkBuscaAno;
	/** Filtro busca per�odo */
	private boolean checkBuscaPeriodo;
	/** Filtro busca servidor */
	private boolean checkBuscaServidor;
	/** Filtro busca per�odo de conclus�o */
	private boolean checkBuscaPeriodoConclusao;
	/** Filtro busca edital */
	private Boolean checkBuscaEdital = false;
	
	/** Atributos utilizados para a inser��o de informa��es em telas de buscas. **/
	/** T�tulo a��o extens�o */
	private String buscaTitulo;
	/** Tipo de relat�rio */
	private Integer buscaTipoRelatorio;
	/** Ano */
	private Integer buscaAno = CalendarUtils.getAnoAtual();
	/** Data in�cio */
	private Date buscaInicio;
	/** Data fim */
	private Date buscaFim;
	/** Data in�cio conclus�o */
	private Date buscaInicioConclusao;
	/** Data fim conclus�o */
	private Date buscaFimConclusao;
	/** Edital  */
	private Integer buscaEdital;
	/** Membro de projeto indicado na busca dos relat�rios. */
	private MembroProjeto membroProjeto = new MembroProjeto();
	

	
	public BuscaRelatoriosProjetosMBean() {
		obj = new RelatorioAcaoAssociada();
		buscaAno = CalendarUtils.getAnoAtual();
	}
	
	/** 
	 * Inicia a busca de relat�rios de projetos.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Lista todos os tipos de relat�rios poss�veis Utilizado na busca por relat�rios.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Busca todos os tipos de relat�rios poss�veis de acordo com 
	 * os par�metros especificados. 
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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

		// Defini��o dos filtros e valida��es

		if (checkBuscaServidor) {
			idServidor = membroProjeto .getServidor().getId();
			ValidatorUtil.validateRequired(idServidor, "Servidor", mensagens);
		}

		if (checkBuscaTitulo) {
			titulo = buscaTitulo;
			ValidatorUtil.validateRequired(titulo, "T�tulo da A��o", mensagens);
		}

		if (checkBuscaTipoRelatorio) {
			idTipoRelatorio = buscaTipoRelatorio;
			ValidatorUtil.validateRequiredId(idTipoRelatorio, "Tipo de Relat�rio",	mensagens);
		}
		
		if (checkBuscaAno) {
			ano = buscaAno;
			ValidatorUtil.validaInt(ano, "Ano", mensagens);
		}
		
		if (checkBuscaPeriodo) {
			inicio = buscaInicio;
			fim = buscaFim;

			ValidatorUtil.validateRequired(inicio,"In�cio do Per�odo de Envio", mensagens);			
			ValidatorUtil.validateRequired(fim,"Fim do Per�odo de Envio", mensagens);			

			if ((inicio != null) && (fim != null)) {
				ValidatorUtil.validaInicioFim(inicio, fim, "Data In�cio", mensagens);
			}
		}
		
		if(checkBuscaPeriodoConclusao){
			inicioConclusao = buscaInicioConclusao;
			fimConclusao = buscaFimConclusao;

			ValidatorUtil.validateRequired(inicioConclusao,"In�cio do Per�odo de Conclus�o", mensagens);			
			ValidatorUtil.validateRequired(fimConclusao,"Fim do Per�odo de Conclus�o", mensagens);			

			if ((inicioConclusao != null) && (fimConclusao != null)) {
				ValidatorUtil.validaInicioFim(inicioConclusao, fimConclusao, "Data In�cio Conclus�o", mensagens);
			}
		}
		
		if(checkBuscaEdital) {
			idEdital = getBuscaEdital();
			ValidatorUtil.validaInt(idEdital, "Edital", mensagens);
		}		

		if (!checkBuscaTitulo && !checkBuscaTipoRelatorio && !checkBuscaAno
				&& !checkBuscaPeriodo && !checkBuscaEdital && !checkBuscaServidor && !checkBuscaPeriodoConclusao ) {
			addMensagemErro("Selecione uma op��o para efetuar a busca por relat�rios.");

		} else {

			try {
				if (mensagens.isEmpty()) {					
					RelatorioAcaoAssociadaDao dao = getDAO(RelatorioAcaoAssociadaDao.class);
					relatorios = dao.buscaRelatorios(titulo, idTipoRelatorio, ano, inicio, fim, inicioConclusao, fimConclusao , idEdital, idServidor);
					if(relatorios.isEmpty()) {
						addMensagemWarning("Nenhum Relat�rio encontrado com os par�metros de busca informados.");
					}
				} else {
					addMensagens(mensagens);
				}

			} catch (DAOException e) {
				notifyError(e);
				addMensagemErro("Erro ao Buscar Relat�rios.");
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
