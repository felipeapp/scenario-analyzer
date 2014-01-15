/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 25/10/2006 
*/

package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocenteComponente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaValidator;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed Bean para o caso de uso de alterar docente de um
 * projeto de pesquisa.
 *
 * @author ilueny santos
 *
 */
@Component("alterarEquipeDocente")
@Scope("request")
public class AlterarEquipeDocenteMBean extends SigaaAbstractController<EquipeDocente> {

	
	private ProjetoEnsino projeto = new ProjetoEnsino();
	
	private ArrayList<ComponenteCurricularMonitoria> componentesSelecionados = new ArrayList<ComponenteCurricularMonitoria>();
	

	public AlterarEquipeDocenteMBean() {
		this.obj = new EquipeDocente();
	}

	
	/**
	 * Confirma a alteração do docente
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>JSP: sigaa.war\monitoria\AlterarEquipeDocente\form.jsp</li>
	 * </ul>
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void alterar() throws SegurancaException, ArqException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );
		
		if ( getConfirmButton().equalsIgnoreCase("Finalizar Docente no Projeto") )
			finalizarEquipeDocente();
		
		if ( getConfirmButton().equalsIgnoreCase("Salvar") )
			alterarEquipeDocente();
		
		if ( getConfirmButton().equalsIgnoreCase("Excluir Docente do Projeto") )
			excluirEquipeDocente();
			
	}
	
	
	
	/**
	 * Preparando movimento para excluir docente
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\monitoria\AlterarEquipeDocente\listar.jsp</li>
	 *   </ul>
	 * @throws SegurancaException 
	 */
	public String preExcluirEquipeDocente() throws SegurancaException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );

		try {
			EquipeDocenteDao dao = getDAO( EquipeDocenteDao.class );
			int idDocente = getParameterInt("id");			
			obj = dao.findByPrimaryKey(idDocente, EquipeDocente.class);
			obj.getOrientacoes().iterator();			
			prepareMovimento( SigaaListaComando.EXCLUIR_EQUIPEDOCENTE );			
			setConfirmButton("Excluir Docente do Projeto");			
			return forward( ConstantesNavegacaoMonitoria.ALTERAREQUIPEDOCENTE_FORM );
			
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);			
		}		
		return forward(getFormPage());
	}	
	
	
	/**
	 * Método para excluir docente.
	  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\monitoria\AlterarEquipeDocente\listar.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String excluirEquipeDocente() throws DAOException, SegurancaException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );
		
		try {
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(SigaaListaComando.EXCLUIR_EQUIPEDOCENTE);				
				execute(mov, getCurrentRequest());	
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);			

		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			return forward(getFormPage());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}		
		
		obj = new EquipeDocente();
		
		return forward( ConstantesNavegacaoMonitoria.ALTERAREQUIPEDOCENTE_LISTA );

	}

	
	
	/**
	 * Preparando movimento para finalizar o docente do projeto
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\monitoria\AlterarEquipeDocente\listar.jsp</li>
	 *   </ul>
	 * @throws SegurancaException 
	 */
	public String preFinalizarEquipeDocente() throws SegurancaException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );

		try {
			EquipeDocenteDao dao = getDAO( EquipeDocenteDao.class );
			int idDocente = getParameterInt("id");			
			obj = dao.findByPrimaryKey(idDocente, EquipeDocente.class);
			obj.getOrientacoes().iterator();
			prepareMovimento( SigaaListaComando.FINALIZAR_EQUIPEDOCENTE );			
			setConfirmButton("Finalizar Docente no Projeto");			
			return forward( ConstantesNavegacaoMonitoria.ALTERAREQUIPEDOCENTE_FORM );
			
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}
		return forward(getFormPage());
	}


	
	/**
	 * Remove (DESATIVA) o discente selecionado
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\monitoria\AlterarEquipeDocente\listar.jsp</li>
	 *   </ul>
	 * @throws DAOException 
	 */
	public String finalizarEquipeDocente() throws ArqException {
		checkRole( SigaaPapeis.GESTOR_MONITORIA );
		
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
		Collection<Orientacao> ors = dao.findByOrientacoesAtivas(obj.getId());
		
		if (obj.getId() == 0) {
			addMensagemErro("Não há Docente selecionado!");
			return null;
		}
		else if (obj.getDataSaidaProjeto() == null) {
			addMensagemErro("Data de Saída do projeto deve ser informada.");
			return null;
		}else if (obj.isCoordenador()){
		
			addMensagemErro("Docente selecionado é Coordenador(a) de projeto.");			
			addMensagemErro("Só podem ser finalizados os docentes que NÃO são Coordenadores de projeto.");
			addMensagemErro("Realize a operação de troca de coordenador do projeto e só depois finalize este docente.");			
			return null;
		
		}else if (!ValidatorUtil.isEmpty(ors)){
			
			addMensagemErro("Docente selecionado possui orientações ativas.");			
			addMensagemErro("Só podem ser finalizados os docentes que NÃO possuam orientações ativas.");
			addMensagemErro("Realize a operação de finalizar orientação, no menu de monitores, e só depois finalize este docente.");			
			return null;
			
		}else {
		    MovimentoCadastro mov = new MovimentoCadastro ();
		    mov.setObjMovimentado( obj );
		    mov.setCodMovimento( SigaaListaComando.FINALIZAR_EQUIPEDOCENTE );
		    try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);				
		    } catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return forward( getFormPage() );
		    }
		    return forward(getListPage());
		}

	}

	
	@Override
	public String getDirBase() {
		return "/monitoria/AlterarEquipeDocente";
	}
	
	
	
	
	
	/**
	 * Este método carrega as orientações do docente e leva para a tela de alteração de docentes
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\monitoria\AlterarEquipeDocente\listar.jsp</li>
	 *   </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String preAlterarEquipeDocente() throws ArqException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );		
		
		EquipeDocenteDao dao = getDAO( EquipeDocenteDao.class );
		int idDocente = getParameterInt("id");		
		obj = dao.findByPrimaryKey(idDocente, EquipeDocente.class);
		obj.getOrientacoes().iterator();		
		setConfirmButton("Salvar");		
		prepareMovimento( SigaaListaComando.ALTERAR_EQUIPEDOCENTE );
		return forward( ConstantesNavegacaoMonitoria.ALTERAREQUIPEDOCENTE_FORM );		
	}
	
	/**
	 * Altera equipe docentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war\monitoria\AlterarEquipeDocente\form.jspM</li>
	 *  	<li>sigaa.war\monitoria\AlterarEquipeDocente\lista.jsp</li>
	 *  	<li>sigaa.war\monitoria\CadastrarEquipeDocente\form.jsp</li>
	 *  	<li>sigaa.war\monitoria\CadastrarEquipeDocente\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String alterarEquipeDocente() throws DAOException, SegurancaException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_EQUIPEDOCENTE);
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(getFormPage());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}		
		
		obj = new EquipeDocente();
		
		return forward( ConstantesNavegacaoMonitoria.ALTERAREQUIPEDOCENTE_LISTA );

	}
	
	
	/**
	 * Permite selecionar de novo docente, a qualquer tempo, no projeto de
	 * monitoria pelos membros da prograd, utilizado pra manutenção
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: sigaa.war\monitoria\CadastrarEquipeDocente\lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String preNovoEquipeDocente() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		//buscando o projeto onde o docente será inserido...
		int id = getParameterInt("id");
		projeto =  getGenericDAO().findByPrimaryKey(id, ProjetoEnsino.class);		
		
		for (ComponenteCurricularMonitoria c : projeto.getComponentesCurriculares()) {
			c.getDocentesComponentes().iterator();
		} 
		
		//setando o projeto no novo docente
		obj.setProjetoEnsino(projeto);
		
		//segue para cadastrar outros dados
		prepareMovimento(SigaaListaComando.CADASTRAR_EQUIPEDOCENTE);
		return forward(ConstantesNavegacaoMonitoria.CADASTRARDOCENTE_FORM);

	}
	
	
	
	/**
	 * Permite cadastramento de novo docente, a qualquer tempo, no projeto de
	 * monitoria pelos membros da prograd(Pró-Reitoria de Graduação), utilizado pra manutenção.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 *  <li>sigaa.war\monitoria\CadastrarEquipeDocente\form.jsp</li>
	 *  <li>sigaa.war\monitoria\CadastrarEquipeDocente\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String novoEquipeDocente() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try {

			// Adicionando o docente no projeto e validando			
			ListaMensagens mensagens = new ListaMensagens();
			EquipeDocenteDao equipeDao = getDAO(EquipeDocenteDao.class);

				//carrega dados do servidor do banco para evitar null pointer na validação logo em seguida...
				if ((obj.getServidor() != null) && (obj.getServidor().getId() > 0)) {
					obj.setServidor(equipeDao.findByPrimaryKey(obj.getServidor().getId(), Servidor.class));
				}
				
				//pegando os componentes selecionados
				componentesSelecionados = new ArrayList<ComponenteCurricularMonitoria>();				
				for (ComponenteCurricularMonitoria ccm : projeto.getComponentesCurriculares()){ 
					if (ccm.isSelecionado()) {
						componentesSelecionados.add(ccm);
					}
				}
				
				
				//validando
				ProjetoMonitoriaValidator.validaAdicionaDocente(projeto, componentesSelecionados, obj, mensagens);			
				//VALIDAÇÃO DE DOCENTES COM MAIS DE 2 ORIENTAÇÕES FOI RETIRADA A PEDIDO DA PROGRAD
								
				//criando orientações...
				for (DiscenteMonitoria discente : projeto.getDiscentesMonitoria()) {

						// discente selecionado, adicionar orientação
						// o discente selecionado deve ser ativo (validado pela prograd) par poder ter orientação válida
						if ((discente.isSelecionado()) && (discente.isAtivo())) {

							//adicionando novo orientador
							Orientacao orientacao = new Orientacao();
								orientacao.setDiscenteMonitoria(discente);
								orientacao.setDataInicio(discente.getDataInicioOrientacao());
								orientacao.setDataFim(discente.getDataFimOrientacao());
								orientacao.setAtivo(true);
								orientacao.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
								
								mensagens.addAll(orientacao.validate());
								if (!mensagens.isEmpty()) {
								    break;
								}
								
								obj.addOrientacao(orientacao);

						}
				}

				
				mensagens.addAll(obj.validate().getMensagens());

				if (!mensagens.isEmpty()) {
					addMensagens(mensagens);
					return null;
				}
				

				//verifica que já tem esse servidor na equipeDocente do projeto
				EquipeDocente ed = equipeDao.findByServidorProjeto(obj.getServidor().getId(), obj.getId());

				
				// se ainda não tem, monta um novo equipeDocente
				if ((ed == null) || (ed.getId() == 0)) {

					boolean servidorJaIncluido = false;

					// aproveita o orientador já incluído no projeto para não
					// cadastra-lo 2 vezes
					// os docentes que serão testados aqui, ainda não foram para o
					// banco de dados
					// por isso procuro dentro dos componentes curriculares.
					for (ComponenteCurricularMonitoria cc : projeto.getComponentesCurriculares()) {
						for (EquipeDocenteComponente edc : cc.getDocentesComponentes()) {
							if ((edc.getEquipeDocente().getServidor().getId() == obj.getServidor().getId())) {
								// faz o novo ser o que já estava no projeto impedindo duplicação
								obj = edc.getEquipeDocente();
								servidorJaIncluido = true;
							}
						}
					}
					

					// verifica se o servidor já está no projeto
					if (!servidorJaIncluido) {
						// carrega todos os dados do servidor direto do banco
						obj.setServidor(equipeDao.findByPrimaryKey(obj.getServidor().getId(), Servidor.class));
					}

					
					// criando e associando os componentes curriculares ao equipeDocente através do equipeDocenteComponente
					for (ComponenteCurricularMonitoria compSelecionado : componentesSelecionados) {
						EquipeDocenteComponente docenteComponente = new EquipeDocenteComponente();
							docenteComponente.setEquipeDocente(obj);
							docenteComponente.setDataVinculacao(new Date());					
							
						compSelecionado.addDocenteComponente(docenteComponente);
					}
					

					// se já tem na equipe do projeto, so atualiza....
				} else {
					obj = ed;
				}

				
				//Processando...				
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);	
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_EQUIPEDOCENTE);
				execute(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				
				// limpa os dados
				componentesSelecionados = new ArrayList<ComponenteCurricularMonitoria>();
				obj = new EquipeDocente();

				return forward(ConstantesNavegacaoMonitoria.CADASTRARDOCENTE_LISTA);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;				
		} catch (Exception e) {
			notifyError(e);
			return null;
		}
	}
	

	public ProjetoEnsino getProjeto() {
		return projeto;
	}


	public void setProjeto(ProjetoEnsino projeto) {
		this.projeto = projeto;
	}

	
	
	public ArrayList<ComponenteCurricularMonitoria> getComponentesSelecionados() {
		return componentesSelecionados;
	}


	public void setComponentesSelecionados(
			ArrayList<ComponenteCurricularMonitoria> componentesSelecionados) {
		this.componentesSelecionados = componentesSelecionados;
	}
	
}