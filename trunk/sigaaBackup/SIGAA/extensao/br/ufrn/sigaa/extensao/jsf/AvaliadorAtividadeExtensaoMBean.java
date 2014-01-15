/*
* Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf;  

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.extensao.AvaliacaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.AvaliadorAtividadeExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.AvaliadorAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;

/*******************************************************************************
 * Controlador de cadastro de avaliadores externos de ações de extensão.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("avaliadorExtensao")
public class AvaliadorAtividadeExtensaoMBean extends SigaaAbstractController<AvaliadorAtividadeExtensao> {

	/** Atributo utilizado para infomrar o ID da Área Temática da Busca */
	private int areaTematicaBusca;

	/** Atributo utilizado para armazenar os avaliadores */
	private List<AvaliadorAtividadeExtensao> avaliadores = new ArrayList<AvaliadorAtividadeExtensao>();

	/** Atributo utilizado para representar os avaliadores do membro atual. */
	private Collection<AvaliacaoAtividade> avaliacoesDoMembroAtual;

	public AvaliadorAtividadeExtensaoMBean() {
		obj = new AvaliadorAtividadeExtensao();
	}

	/**
	 * Método utilizado para iniciar a gerencia dos avaliadores
	 */
	private void iniciarGerenciarAvaliadoresExtensao() {
	}

	
	/**
	 * Lista avaliadores de extensão.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s).	  
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarAvaliadorExtensao() throws ArqException {
		obj = new AvaliadorAtividadeExtensao();
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		iniciarGerenciarAvaliadoresExtensao();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getListPage());
	}

	/**
	 * Prepara o cadastro de um avaliador de extensão.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarAvaliadorExtensao() throws ArqException {
		obj = new AvaliadorAtividadeExtensao();
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		iniciarGerenciarAvaliadoresExtensao();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	@Override
	public void afterAtualizar() throws ArqException {

		// permite a inclusão da 2 area temática depois de ter criado o
		// avaliador
		if (obj.getAreaTematica2() == null)
			obj.setAreaTematica2(new AreaTematica());

	}

	@Override
	public String getDirBase() {
		return "/extensao/AvaliadorAtividadeExtensao";
	}

	/**
	 * Faz uma busca de avaliadores de extensão e preenche a coleção 'avaliadores'.
	 *
	 * sigaa.war/extensao/AvaliadorAtividadeExtensao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String busca() {

		AvaliadorAtividadeExtensaoDao dao = getDAO(AvaliadorAtividadeExtensaoDao.class);
		avaliadores.clear();
		try {
			
			//Todas as áreas
			if (areaTematicaBusca == -1)
				avaliadores.addAll(dao.findByAreaTematica(null));
			
			//Uma área específica
			else {
				avaliadores.addAll(dao.findByAreaTematica(areaTematicaBusca));
				
				/**  
				 * A partir de 22/08/2011 a segunda área temática não será setada e o cadastro 
				 * automaticamente setará para null se for um novo cadastro.
				 * Na view apenas aparece a opção de uma área temática  
				avaliadores.addAll(dao.findByExactField(
						AvaliadorAtividadeExtensao.class, "areaTematica2.id",
						areaTematicaBusca, "asc", "servidor.pessoa.nome"));
				*/
			}
			
			setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
			prepareMovimento(ArqListaComando.DESATIVAR);

		} catch (ArqException e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}

		// limpa todas as avaliações do avaliador atual só preenche quando o operador escolher outro
		if (avaliacoesDoMembroAtual != null)
			avaliacoesDoMembroAtual.clear();

		return null;
	}

	/**
	 * Lista de todos os avaliadores localizados em um combo
	 * 
	 * @return Coleção de SelectItem usado em selects HTML
	 */
	public Collection<SelectItem> getAvaliadoresCombo() {
		List<AvaliadorAtividadeExtensao> lista = new ArrayList<AvaliadorAtividadeExtensao>();

		if (avaliadores != null)
			lista = new ArrayList<AvaliadorAtividadeExtensao>(avaliadores);

		return toSelectItems(lista, "id", "servidor.pessoa.nome");
	}

	/**
	 * Muda o avaliador do Mbean
	 * 
	 * Não chamado diretamente por JSP(s).
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void changeAvaliadorAtividade(ValueChangeEvent evt)
			throws DAOException {
		Integer idAvaliador = 0;
		try {

			idAvaliador = new Integer(evt.getNewValue().toString());
			if (idAvaliador != null && idAvaliador != 0) {

				AvaliacaoExtensaoDao daoAVA = getDAO(AvaliacaoExtensaoDao.class);
				obj = daoAVA.findByPrimaryKey(idAvaliador,
						AvaliadorAtividadeExtensao.class);

				avaliacoesDoMembroAtual = daoAVA.findByAvaliadorAtividade(obj
						.getServidor().getId(), null,
						TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA);
			}

		} catch (Exception e) {
			notifyError(e);
		}

	}

	/**
	 * Remove avaliador.
	 * 
	 * sigaa.war/extensao/AvaliadorAtividadeExtensao/lista.jsp
	 * 
	 * @return
	 */
	@Override
	protected void afterInativar() {
		busca();
	}

	/**
	 * Ação a ser executada após o cadastro de um avaliador.
	 * 
	 * Não chamado por JSP(s).
	 * 
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		obj = new AvaliadorAtividadeExtensao();
		busca();
	}

	
	/**
	 * Método chamado antes de cadastrar e validar um avaliador.
	 * 
	 * Método não chamado por JSP(s).
	 * 
	 */
	@Override
	public void beforeCadastrarAndValidate() {
		if (obj.getAreaTematica2() != null && obj.getAreaTematica2().getId() == 0)
			obj.setAreaTematica2(null);

		obj.setAtivo(true);
		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
	}
	
	@Override
	protected void doValidate() throws ArqException {
		AvaliadorAtividadeExtensaoDao dao = getDAO(AvaliadorAtividadeExtensaoDao.class);
		if ((obj.getId() == 0)
				&& (dao.isAvaliadorCadastrado(obj.getServidor().getId()))) {
			addMensagemErro("Servidor(a) já cadastrado(a) como avaliador(a).");
		}
		super.doValidate();
	}

	/**
	 * Utilizado na visualização dos avaliadores que tem projetos e projetos que
	 * tem avaliadores...
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAvaliadoresAtivosCombo() {

		AvaliadorAtividadeExtensaoDao dao = getDAO(AvaliadorAtividadeExtensaoDao.class);
		try {
			return toSelectItems(dao.findAllAtivos(
					AvaliadorAtividadeExtensao.class, "pessoa.nome"), "id",
					"pessoa.nome");
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<SelectItem>();
		}

	}
	
	/**
	 * Método que realiza validações antes de alterar um avaliador.
	 */
	@Override
	protected void beforeAtualizar() throws ArqException {
		GenericDAO dao = getGenericDAO();
		setId();
		AvaliadorAtividadeExtensao av = dao.findByPrimaryKey(obj.getId(), AvaliadorAtividadeExtensao.class);
		if(av == null || !av.getAtivo()){
			addMensagemErro("O avaliador não está ativo no sistema. Por favor realize uma nova busca e altere um avaliador ativo.");
			cancelar();
		}
		super.beforeAtualizar();
	}
	
	/**
	 * Método que realiza validações antes de inativar um avaliador.
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		GenericDAO dao = getGenericDAO();
		setId();
		AvaliadorAtividadeExtensao av = dao.findByPrimaryKey(obj.getId(), AvaliadorAtividadeExtensao.class);
		if(av == null || !av.getAtivo()){
			addMensagemErro("Este usuário já foi removido do sistema, por favor realize uma nova busca para remover um avaliador ativo.");
			return cancelar();
		}else{
			return super.inativar();
		}
		
	}
	
	/**
	 * Método que retorna para a lista de avaliadores.
	 * @return
	 */
	public String listPage(){
		return redirect(getListPage());
	}

	public int getAreaTematicaBusca() {
		return areaTematicaBusca;
	}

	public void setAreaTematicaBusca(int areaTematicaBusca) {
		this.areaTematicaBusca = areaTematicaBusca;
	}

	public Collection<AvaliacaoAtividade> getAvaliacoesDoMembroAtual() {
		return avaliacoesDoMembroAtual;
	}

	public void setAvaliacoesDoMembroAtual(
			Collection<AvaliacaoAtividade> avaliacoesDoMembroAtual) {
		this.avaliacoesDoMembroAtual = avaliacoesDoMembroAtual;
	}

	public List<AvaliadorAtividadeExtensao> getAvaliadores() {
		return avaliadores;
	}

	public void setAvaliadores(List<AvaliadorAtividadeExtensao> avaliadores) {
		this.avaliadores = avaliadores;
	}

}
