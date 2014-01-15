package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;

/*******************************************************************************
 * MBean responsável por gerar unificar alguns métodos utilizados em mbeans de
 * geração de documentos autenticados <br>
 * A autenticação dos documentos eh realizada pelo SIGAA através da geração de
 * um código de documento e código de verificação que pode ser validado através
 * do portal publico do SIGAA.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("documentosAutenticadosMonitoria")
@Scope("request")
public class DocumentosAutenticadosMonitoriaMBean extends SigaaAbstractController<EquipeDocente> {

	private Collection<ProjetoEnsino> projetos = new HashSet<ProjetoEnsino>();

	private Collection<EquipeDocente> docentesMonitoria = new HashSet<EquipeDocente>();
	
	private Collection<DiscenteMonitoria> discentesMonitoria = new HashSet<DiscenteMonitoria>();
	
	private EquipeDocente docenteMonitoria = new EquipeDocente();

	private EmissaoDocumentoAutenticado comprovante;

	private boolean verificando = false;

	/**
	 * Exibe os docentes do projeto selecionado para emissão dos certificados
	 * 
	 * @throws DAOException
	 */
	public String selecionarDocente() {

		try {
			int idProjeto = Integer.parseInt(getParameter("idProjeto"));
			docentesMonitoria = getGenericDAO().findByExactField(EquipeDocente.class,
					"projetoEnsino.id", idProjeto);
		} catch (DAOException e) {
			notifyError(e);
		}

		return forward(getListPage());
	}

	/**
	 * Exibe os discentes do projeto selecionado para emissão dos certificados
	 * 
	 * @throws DAOException
	 */
	public String selecionarDiscente() {

		try {
			int idProjeto = Integer.parseInt(getParameter("idProjeto"));
			discentesMonitoria = getGenericDAO().findByExactField(DiscenteMonitoria.class, "projetoEnsino.id", idProjeto);
		} catch (DAOException e) {
			notifyError(e);
		}
		return forward(getListPage());
	}

	
	/**
	 * Todas as participações do servidor do usuário logado em projetos de monitoria válidos
	 * (aprovadas pela comissão de monitoria) para emissão de certificados e declarações
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String participacoesServidorUsuarioLogado() throws DAOException, SegurancaException {

		if ((getServidorUsuario() != null) && (getServidorUsuario().getPessoa() != null)){
			EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
			docentesMonitoria = dao.findByPessoa(getServidorUsuario().getPessoa().getId());
			
		}else{
			docentesMonitoria = new ArrayList<EquipeDocente>();
		}
		
		return forward(ConstantesNavegacaoMonitoria.DOCUMENTOS_AUTENTICADOS_LISTA_DOCENTES);
	}

	/**
	 * Todas as participações do discente do usuário logado em projetos de monitoria válidos
	 * (aprovadas pela comissão de monitoria) para emissão de certificados e declarações
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String participacoesDiscenteUsuarioLogado()
			throws DAOException, SegurancaException {

		if ((getDiscenteUsuario() != null) && (getDiscenteUsuario().getPessoa() != null)){
			DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
			discentesMonitoria = dao.findByPessoa(getDiscenteUsuario().getPessoa().getId());
		}else{
			discentesMonitoria = new ArrayList<DiscenteMonitoria>();
		}
		
		return forward(ConstantesNavegacaoMonitoria.DOCUMENTOS_AUTENTICADOS_LISTA_DISCENTES);
	}

	
	public Collection<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}

	public Collection<EquipeDocente> getDocentesMonitoria() {
		return docentesMonitoria;
	}

	public void setDocentesMonitoria(Collection<EquipeDocente> docentesMonitoria) {
		this.docentesMonitoria = docentesMonitoria;
	}

	public Collection<DiscenteMonitoria> getDiscentesMonitoria() {
		return discentesMonitoria;
	}

	public void setDiscentesMonitoria(
			Collection<DiscenteMonitoria> discentesMonitoria) {
		this.discentesMonitoria = discentesMonitoria;
	}

	public EquipeDocente getDocenteMonitoria() {
		return docenteMonitoria;
	}

	public void setDocenteMonitoria(EquipeDocente docenteMonitoria) {
		this.docenteMonitoria = docenteMonitoria;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isVerificando() {
		return verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}

}
