/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/02/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dao.RegiaoMatriculaDao;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.RegiaoMatricula;
import br.ufrn.sigaa.dominio.RegiaoMatriculaCampus;

/**
 * Managed bean para cadastro de regiões de matrículas.
 * @author Rafael Gomes
 *
 */
@Component("regiaoMatriculaBean") @Scope("request")
public class RegiaoMatriculaMBean extends SigaaAbstractController<RegiaoMatricula>{

	/** DataModel responsável pelas operações dos campus */
	private DataModel campus;
	
	
	/** Construtor padrão. */
	public RegiaoMatriculaMBean() {
		obj = new RegiaoMatricula();
	}
	
	@Override
	public String getDirBase() {
		return "/geral/regiaoMatricula";
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.DAE);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_REGIAO_CAMPUS.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_REGIAO_CAMPUS);
		if ( isNotEmpty(getNivelEnsino()))
			obj.setNivel(getNivelEnsino());
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		
		prepareMovimento(SigaaListaComando.CADASTRAR_REGIAO_CAMPUS);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_REGIAO_CAMPUS.getId());
		setReadOnly(false);

		setConfirmButton("Alterar");
		
		return forward(getFormPage());
	}
	
	/**
	 * Cadastrar uma Região de Matrícula e suas associações com Campus.
	 * 
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String cadastrar() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		RegiaoMatriculaDao dao = getDAO(RegiaoMatriculaDao.class);
		
		if ( isNotEmpty(getNivelEnsino()))
			obj.setNivel(getNivelEnsino());
		
		checkOperacaoAtiva(SigaaListaComando.CADASTRAR_REGIAO_CAMPUS.getId());
		if (hasErrors()) return null;
		
		boolean nenhumCampusSelecionado = true;
		@SuppressWarnings("unchecked")
		List<CampusIes> campusSelecionados = (List<CampusIes>) campus.getWrappedData();
		for (Iterator<CampusIes> it = campusSelecionados.iterator(); it.hasNext(); ) {
			CampusIes c = it.next();
			if (c.isSelecionado())
				nenhumCampusSelecionado = false;
		}
		
		obj.setRegioesMatriculaCampus(
				dao.findByExactField(RegiaoMatriculaCampus.class, "regiaoMatricula.id", obj.getId()));
		
		erros = obj.validate();
		
		if (nenhumCampusSelecionado)
			erros.addErro("Pelo menos um Campus é obrigatório.");
			
		if (!dao.findByNome(obj.getNome(), obj.getNivel(), obj.getId()).isEmpty()){
			erros.addErro("Já existe uma região cadastrada com o nome informado para o nível de ensino "+ NivelEnsino.getDescricao(obj.getNivel()));
		}

		if (hasErrors()){
			return null;
		}
			
		for (Iterator<CampusIes> it = campusSelecionados.iterator(); it.hasNext(); ) {
			CampusIes c = it.next();
			if (!c.isSelecionado())
				it.remove();
		}
							
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_REGIAO_CAMPUS);
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(campusSelecionados);

		boolean cadastro = obj.getId() == 0;
		try {
			execute(mov, getCurrentRequest());
			if (cadastro)
				addMensagem( MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Região de Matrícula" );	
			else 
				addMensagem( MensagensArquitetura.ALTERADO_COM_SUCESSO, "Região de Matrícula" );	
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		if (cadastro){
			super.afterCadastrar();
			return cancelar();
		} else {
			return redirect(getListPage()); 
		}
	}
	
	/**
	 * Remove uma região de matrícula.
	 * 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.ear/sigaa.war/geral/regiaoMatricula/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		setId();
	
		if (obj.getId() == 0) {
			addMensagemErro("Não há objeto informado para remoção");
			return null;
		} else {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			prepareMovimento(SigaaListaComando.REMOVER_REGIAO_CAMPUS);
			mov.setCodMovimento(SigaaListaComando.REMOVER_REGIAO_CAMPUS);

			try {
				execute(mov, getCurrentRequest());
				redirect(getListPage());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return forward(getListPage());
			}
			addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);

			return null;
		}
	}
	
	/**
	 * Retorna todos as regiões de matrícula por nível de ensino, quando este for identificado.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<RegiaoMatricula> getAllByNivelEnsino() throws ArqException {
		if (all == null) {
			GenericDAO dao = null;
			dao = new GenericDAOImpl(getSistema(), getSessionRequest());
			if ( isNotEmpty(getNivelEnsino())){
				all = dao.findByExactField(RegiaoMatricula.class, "nivel", getNivelEnsino(), "ASC", "nome");
			} else {
				all = dao.findAll(RegiaoMatricula.class, "ASC", "nome");
			}
		}
		return all;
	}
	
	/**
	 * Retorna um DataModel com todos os campus
	 * 
  	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.ear/sigaa.war/geral/regiaoMatricula/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public DataModel getCampus() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_REGIAO_CAMPUS);

		if (campus == null) {
			List<CampusIes> listCampus = (List<CampusIes>) getGenericDAO().findAll(CampusIes.class);

			if (obj.getId() != 0) {
				Collection<RegiaoMatriculaCampus> regioes = obj.getRegioesMatriculaCampus();
				for (RegiaoMatriculaCampus regiaoCampus : regioes) {
					Integer index = listCampus.indexOf(regiaoCampus.getCampusIes());
					if(index != -1) {
						listCampus.get(index).setSelecionado(true);
					}
				}
			}

			campus = new ListDataModel(listCampus);
		}
		return campus;
	}
	
	/**
	 * Exibe a página de detalhes (view) da região de matrícula
	 *
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.ear/sigaa.war/geral/regiaoMatricula/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String detalhes() throws DAOException {
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		return forward(getDirBase() +"/view.jsp");
	}
	
	/**
	 * Retorna uma descrição textual do nível de ensino da turma.
	 *
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.ear/sigaa.war/geral/regiaoMatricula/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getNivelDescricao(){
		return NivelEnsino.getDescricao(obj.getNivel());
	}
}
