/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.negocio.DiscenteMonitoriaValidator;

/**
 * Managed-Bean utilizado para cadastro de monitores no projeto pela PROGRAD(Pró-Reitoria de Graduação).
 * 
 * @author Gleydson
 * @author Victor Hugo
 * @author ilueny santos
 * 
 * 
 */
@Component("cadMonitor")
@Scope("session")
public class CadastrarMonitorMBean extends SigaaAbstractController<DiscenteMonitoria> {

	private int idDiscente;

	private String nomeDiscente;

	private ProjetoEnsino projeto = new ProjetoEnsino();

	// Inicia cadastro de monitor com indicação de desabilitado verdadeira
	private boolean desabilitado = true;

	private Integer idCentro;

	private Collection<SelectItem> projetos = new ArrayList<SelectItem>();

	private Collection<EquipeDocente> docentes = new ArrayList<EquipeDocente>();

	// Indica o tipo de vínculo do aluno com o projeto de monitoria.
	private int tipoVinculo;

	public CadastrarMonitorMBean() {
		obj = new DiscenteMonitoria();
	}

	/**
	 * Método não invocado por JSP.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	/**
	 * Busca os Membros docentes de um projeto para seleção dos orientadores do
	 * DiscenteMonitoria
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/CadastrarMonitor/lista.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public String formCadastroMonitor() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		prepararNovoCadastro(); 
		
		int id = getParameterInt("id");
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		projeto = dao.findByPrimaryKey(id, ProjetoEnsino.class);
		if ((projeto != null) && (projeto.getId() > 0)) {
			docentes = dao.findByProjeto(projeto.getId(), true);
			projeto.getDiscentesMonitoria().iterator();
			for (DiscenteMonitoria d : projeto.getDiscentesMonitoria()) {
				d.getOrientacoes().iterator();
			}
			for (EquipeDocente ed : docentes) {
			    ed.setSelecionado(false);
			}
			prepareMovimento(ArqListaComando.CADASTRAR);
			return forward(ConstantesNavegacaoMonitoria.CADASTRARMONITOR_FORM);
		} else {
			addMensagemErro("Projeto não selecionado!");
			return null;
		}

	}

	/**
	 * Prepara para cadastro de novo discente;
	 * 
	 */
	private void prepararNovoCadastro() {
	    obj = new DiscenteMonitoria();
	    idDiscente = 0;
	    nomeDiscente = "";
	    projeto = new ProjetoEnsino();
	    docentes = new ArrayList<EquipeDocente>();
	}

	/**
	 * Verifica se o discente pode assumir monitoria e adiciona o discente ao projeto.
	 * JSP: Não invocado por JSP.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {

		if (idDiscente > 0) {
			obj.setDiscente(getGenericDAO().findByPrimaryKey(idDiscente, DiscenteGraduacao.class));
			obj.getTipoVinculo().setId(tipoVinculo);
			obj.setProjetoEnsino(projeto);
			obj.setBanco(null);
			obj.setProvaSelecao(null);
			obj.setDataCadastro(new Date());
			obj.setAtivo(true);
			obj.getOrientacoes().clear();
			ListaMensagens lista = new ListaMensagens();

			// setando os orientadores selecionados
			for (EquipeDocente equipe : docentes) {
				if (equipe.isSelecionado()) {				    

					Orientacao orientacao = new Orientacao();
					orientacao.setEquipeDocente(equipe);
					orientacao.setDiscenteMonitoria(obj);
					orientacao.setDataInicio(equipe.getDataInicioOrientacao());
					orientacao.setDataFim(equipe.getDataFimOrientacao());
					orientacao.setAtivo(true);
					
					// não trava aqui,...só mostrar mensagem de alerta...a pedido da prograd!
					// somente 2 orientandos por docente no projeto
					DiscenteMonitoriaValidator.validaMaximoOrientacoesDocentes(equipe, orientacao.getDataInicio(), orientacao.getDataFim(),  lista);
					
					lista.addAll(orientacao.validate());
					if (!lista.isEmpty()) {
					    break;
					}					
					obj.getOrientacoes().add(orientacao);
				}
			}
			addMensagens(lista);
			projeto.getDiscentesMonitoria().add(obj);
		}
	}
	
	protected void doValidate() throws ArqException {
	    // Verifica se discente pode assumir monitoria
	    DiscenteMonitoriaValidator.validaDiscenteAssumirMonitoria(obj, erros);
	}

	@Override
	protected void afterCadastrar() {
	    	prepararNovoCadastro();
		resetBean();
	}
	
	@Override
	public String forwardCadastrar() {	
	    return getContextPath() + ConstantesNavegacaoMonitoria.CADASTRARMONITOR_LISTA_PROJETOS;
	}

	public boolean isDesabilitado() {
		return desabilitado;
	}

	public void setDesabilitado(boolean desabilitado) {
		this.desabilitado = desabilitado;
	}

	public int getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}

	public ProjetoEnsino getProjeto() {
		return projeto;
	}

	public void setProjeto(ProjetoEnsino projeto) {
		this.projeto = projeto;
	}

	public Collection<SelectItem> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<SelectItem> projetos) {
		this.projetos = projetos;
	}

	public String getNomeDiscente() {
		return nomeDiscente;
	}

	public void setNomeDiscente(String nomeDiscente) {
		this.nomeDiscente = nomeDiscente;
	}

	public void setIdCentro(Integer idCentro) {
		this.idCentro = idCentro;
	}

	public Integer getIdCentro() {
		return idCentro;
	}

	public Collection<EquipeDocente> getDocentes() {
		return docentes;
	}

	public void setDocentes(Collection<EquipeDocente> docentes) {
		this.docentes = docentes;
	}

	public int getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(int tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

}