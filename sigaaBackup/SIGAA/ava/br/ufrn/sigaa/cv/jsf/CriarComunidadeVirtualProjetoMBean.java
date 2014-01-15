/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 25/05/2010
 */
package br.ufrn.sigaa.cv.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.cv.dominio.TipoComunidadeVirtual;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;

/**
 * MBean responsável por criar Comunidades Virtuais a partir de um {@link Projeto}, 
 * seja de pesquisa, monitoria ou extensão.
 * Importa automaticamente os membros do projeto como participantes da comunidade. 
 * 
 * @author agostinho campos
 *
 */
@Component
@Scope("request")
public class CriarComunidadeVirtualProjetoMBean extends SigaaAbstractController<ComunidadeVirtual> {

	/**
	 * Importa os {@link MembroProjeto} de um projeto criando uma Comunidade Virtual com os membros desse projeto
	 * automaticamente cadastrados como participantes da comunidade em questão.
	 * 
	 * @throws ArqException 
	 *   
	 */
	private void criarComunidadeVirtualPesquisaOuExtensao(Collection<MembroProjeto> listaMembros) throws ArqException {

		if (!listaMembros.isEmpty()) {
			obj = new ComunidadeVirtual();
			obj.setTipoComunidadeVirtual(new TipoComunidadeVirtual(TipoComunidadeVirtual.MODERADA));
			obj.setUsuario(getUsuarioLogado());

			Projeto projeto = listaMembros.iterator().next().getProjeto();
			obj.setNome(projeto.getAnoTitulo());
			
			if (isCoordenador(obj.getUsuario(), projeto)){
			
				if (projeto.getDescricao() != null && !projeto.getDescricao().isEmpty()) 
					obj.setDescricao(projeto.getDescricao());
				else
					obj.setDescricao("Comunidade Virtual do Projeto "+obj.getNome());
				
				AtividadeExtensao atividade = getDAO(AtividadeExtensaoDao.class).findAcaoByProjeto(projeto.getId());
				
				if(atividade != null){
					if(atividade.getCursoEventoExtensao() != null){
						getGenericDAO().initialize(atividade.getCursoEventoExtensao());
						obj.setDescricao(atividade.getCursoEventoExtensao().getResumo());
					}
				}
				else{
					obj.setDescricao(projeto.getResumo());
				}
	
				for (MembroProjeto membroProjeto : listaMembros) {
					MembroComunidade membroComunidade = new MembroComunidade();
					membroComunidade.setPermissao(MembroComunidade.MEMBRO);
					membroComunidade.setPessoa(membroProjeto.getPessoa());
					membroComunidade.setComunidade(obj);
	
					obj.getParticipantesComunidade().add(membroComunidade);
				}
					cadastrarComunidade();
			}
			else {
				addMensagemErro("Somente o coordenador do projeto pode criar a Comunidade Virtual.");
			}
		}
		else
			addMensagemErro("Não existem membros nesse projeto para que se possa criar a Comunidade Virtual.");
	}
	
	/**
	 * Seta os membros de um projeto de monitoria como participantes de uma comunidade e cria a respectiva comunidade.
	 * 
	 * @param listaMembros
	 * @throws ArqException
	 */
	private void criarComunidadeVirtualMonitoria(Collection<DiscenteMonitoria> discentesMonitoria, Collection<EquipeDocente> equipeDocente) throws ArqException {
		
		if (!discentesMonitoria.isEmpty()) {
			obj = new ComunidadeVirtual();
			obj.setTipoComunidadeVirtual(new TipoComunidadeVirtual(TipoComunidadeVirtual.MODERADA));
			obj.setUsuario(getUsuarioLogado());
			
			for (DiscenteMonitoria discenteMonitoria : discentesMonitoria) {
				MembroComunidade membroComunidade = new MembroComunidade();
				membroComunidade.setPermissao(MembroComunidade.MEMBRO);
				membroComunidade.setPessoa(discenteMonitoria.getDiscente().getPessoa());
				membroComunidade.setComunidade(obj);

				obj.setNome(discenteMonitoria.getProjetoEnsino().getProjeto().getAnoTitulo());
				obj.setDescricao(discenteMonitoria.getProjetoEnsino().getProjeto().getResumo());
				
				obj.getParticipantesComunidade().add(membroComunidade);
			}
			
			for (EquipeDocente equipeDoc : equipeDocente) {
				MembroComunidade membroComunidade = new MembroComunidade();
				membroComunidade.setPermissao(MembroComunidade.MEMBRO);
				membroComunidade.setPessoa(equipeDoc.getServidor().getPessoa());
				membroComunidade.setComunidade(obj);

				obj.setNome(equipeDoc.getProjetoEnsino().getProjeto().getAnoTitulo());
				obj.setDescricao(equipeDoc.getProjetoEnsino().getProjeto().getResumo());
				
				obj.getParticipantesComunidade().add(membroComunidade);
			}
			
			cadastrarComunidade();
		}
		else
			addMensagemErro("Não existem participantes nesse projeto para que se possa criar a Comunidade Virtual.");
	}

	/**
	 * Cria a nova comunidade com os participantes que foram setados anteriormente. 
	 * 
	 * @throws ArqException
	 */
	private void cadastrarComunidade() throws ArqException {
		// o usuário que cria a comunidade torna-se administrador da mesma,
		// porém caso ele também incluído como membro do projeto, remove pois
		// ele já vai ser adicionado como Administrador na comunidade no ProcessadorCadastroComunidadeVirtual
		MembroComunidade membroAdminRemover = new MembroComunidade();
		for (MembroComunidade membroComunidade : obj.getParticipantesComunidade() )
			if ( membroComunidade.getPessoa().equals(obj.getUsuario().getPessoa()) )
				membroAdminRemover = new MembroComunidade(); membroAdminRemover.setPessoa(obj.getUsuario().getPessoa());
				
		// remove o usuário que está criando a comunidade caso ele também seja membro do projeto
		obj.getParticipantesComunidade().remove(membroAdminRemover);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_COMUNIDADE_VIRTUAL);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			notifyError(e);
		}
		BuscarComunidadeVirtualMBean buscarBean = getMBean("buscarComunidadeVirtualMBean");
		buscarBean.setComunidadesPorPessoa(null);
		addMensagemInformation("Comunidade Virtual cadastrada com sucesso!");
	}
	
	/**
	 * Recupera todos os participantes de um Projeto de Extensão e criar uma 
	 * Comunidade Virtual incluindo automaticamente os membros desse projeto 
	 * na comunidade recém criada. 
	 * 
	 *  Os membros de um projeto de Monitoria podem ser {@link DiscenteMonitoria} e {@ EquipeDocente}
	 *  
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *		<li>sigaa.war/monitoria/ProjetoMonitoria/meus_projetos.jsp</li>
	 * </ul>
	 *  
	 * @throws ArqException
	 */
	public void criarComunidadeVirtualMonitoria() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_COMUNIDADE_VIRTUAL);
		Integer id = getParameterInt("id", 0);
		ProjetoEnsino ativEnsino = getDAO(MembroProjetoDao.class).findByPrimaryKey(id, ProjetoEnsino.class);
		
		// os membros que projetos de monitoria são representados por DiscenteMonitoria e EquipeDocente
		Collection<DiscenteMonitoria> listaDiscentesMonitoria = getDAO(MembroProjetoDao.class).findDiscentesMonitoriaByProjetoEnsino(ativEnsino.getId());
		Collection<EquipeDocente> listaEquipeDocente = getDAO(MembroProjetoDao.class).findEquipeDocenteMonitoriaByProjetoEnsino(ativEnsino.getId());
		
		criarComunidadeVirtualMonitoria(listaDiscentesMonitoria, listaEquipeDocente);
	}

	/**
	 * Recupera todos os participantes de um Projeto de Extensão e criar uma 
	 * Comunidade Virtual incluindo automaticamente os membros desse projeto 
	 * na comunidade recém criada. 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *		<li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp</li>
	 * </ul>
	 *  
	 * @throws ArqException
	 */
	public void criarComunidadeVirtualExtensao() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_COMUNIDADE_VIRTUAL);
		Integer id = getParameterInt("id", 0);
		AtividadeExtensao ativExtensao = getDAO(MembroProjetoDao.class).findByPrimaryKey(id, AtividadeExtensao.class);
		Collection<MembroProjeto> listaMembros = getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", ativExtensao.getProjeto().getId());
		
		criarComunidadeVirtualPesquisaOuExtensao(listaMembros);
	}
	
	/**
	 * Recupera todos os participantes de um Projeto de Pesquisa e criar uma 
	 * Comunidade Virtual incluindo automaticamente os membros desse projeto 
	 * na comunidade recém criada.
	 * 
	 * Método não chamado por JSPs.
	 *  
	 * @throws ArqException
	 */
	public void criarComunidadeVirtualPesquisa(Integer idProjetoPesquisa) throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_COMUNIDADE_VIRTUAL);
		ProjetoPesquisa projetoPesquisa = getDAO(MembroProjetoDao.class).findByPrimaryKey(idProjetoPesquisa, ProjetoPesquisa.class);
		Collection<MembroProjeto> listaMembros = getDAO(MembroProjetoDao.class).findMembroProjetoAtivoByProjetoPesquisa(projetoPesquisa.getProjeto().getId(), false);
		
		criarComunidadeVirtualPesquisaOuExtensao(listaMembros);
	}
	
	/**
	 * Verificar se o usuario passado passado como parametro e o mesmo e o
	 * coordenador do projeto.
	 * 
	 * @param idPessoa
	 * @param projeto
	 * @return
	 * @throws ArqException
	 */
	private boolean isCoordenador(Usuario usuario,Projeto projeto) throws ArqException {
		
		Integer idServidor = usuario.getServidor().getId();
		//Caso o coordenador em projeto não seja vazio.
		if ( ValidatorUtil.isNotEmpty(projeto.getCoordenador()) && projeto.getCoordenador().getId()!= 0) {
			return idServidor == projeto.getCoordenador().getServidor().getId();
			
		} else {
			
			MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
			try {
				//Caso o projeto seja de extensao.
				if (projeto.getTipoProjeto().getId() == TipoProjeto.EXTENSAO) {
					MembroProjeto  coordenador = dao.findCoordenadorAtualProjeto(projeto.getId());
					if ( ValidatorUtil.isEmpty(coordenador))
						return false;
					else
						return idServidor == coordenador.getServidor().getId();
					
				}//caso o projeto seja de pesquisa.
				else {
							
					int idServidorCoordenador =  dao.findCoordenadorAtualProjetoPesquisa(projeto.getId());
					if (idServidorCoordenador == 0 )
						return false;
					else
						return idServidorCoordenador == idServidor;
					
				}
			} finally {
				dao.close();
			}

		}

	}
	
}
