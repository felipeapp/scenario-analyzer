/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.IndicacaoReferenciaDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaInternaBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Managed bean para cadastro de referências para a turma
 * virtual.
 * @author David Pereira
 *
 */
@Component("indicacaoReferencia") @Scope("session")
public class IndicacaoReferenciaMBean extends CadastroTurmaVirtual<IndicacaoReferencia> implements PesquisarAcervoBiblioteca {

	/** Id da referência selecionada para remoção sem passar um parâmetro pela requisição. */
	private int idReferenciaSelecionada;
	
	/** Se a referência será associada num tópico de aula. */
	private boolean inserirReferenciaEmTopico = false;
	
	/** Guarda as informações MARC de uma catalogação no acervo da biblioteca, para gerar o formato de referência com base nessa catalogação. */
	private CacheEntidadesMarc titulo;

	/**
	 * Retorna a listagem contendo todas as referências de uma turma.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public List<IndicacaoReferencia> lista() {
	
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		IndicacaoReferenciaDao dao = null;
		if (listagem == null){
			try {
				dao = new IndicacaoReferenciaDao();
				listagem = dao.findReferenciasTurma(turma(),tBean.isPermissaoDocente());
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return listagem;
	}
	
	/**
	 * Prepara para a remoção do objeto.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	protected void antesRemocao(){
		getGenericDAO().clearSession();
	}
	
	/**
	 * Exibe o formulário para alterar uma indicação de referência.<br/><br/>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP: /ava/IndicacaoReferencia/listar.jsp
	 */
	@Override
	public String editar() {
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), IndicacaoReferencia.class);
	
			if (object == null || !object.isAtivo()){
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
				return tBean.retornarParaTurma();
			}
			
			if (object.getAula() == null)
				object.setAula(new TopicoAula());
			else
				object.setAula(getGenericDAO().refresh(object.getAula()));
			
			if ( object.getMaterial() != null )
				object.setMaterial(getGenericDAO().refresh(object.getMaterial()));
			
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_AVA);
			
			paginaOrigem = getParameter("paginaOrigem");

		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
		return forward("/ava/" + getClasse().getSimpleName() + "/editar.jsp");
	}
	
	/**
	 * Atualiza a referência selecionada.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/IndicacaoReferencia/editar.jsp
	 */
	@Override
	public String atualizar() {
		
		try {
		
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.INICIAR_ALTERACAO, object.getId());
			
			object.setTurma(turma());
			if (object.getAula().getId() == 0)
				object.setAula(null);
			else
				object.getMaterial().setTopicoAula(getGenericDAO().findByPrimaryKey(object.getId(), IndicacaoReferencia.class, new String [] { "aula.id"}).getAula());

			antesPersistir();
			
			Specification specification = getEspecificacaoAtualizacao();
			if (specification == null || specification instanceof NullSpecification ) 
				specification = getEspecificacaoCadastro();
			
			prepare(SigaaListaComando.ATUALIZAR_AVA);
			Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, object, specification);
			
			if (notification.hasMessages()) {
				return notifyView(notification);
			}
	
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.ALTERAR, object.getId());
			listagem = null;
			flash("Indicação de referência atualizada com sucesso!");
			
			if ( object.isNotificarAlunos() && !notification.hasMessages()) {
							
				String assunto = "Material alterado: " +  object.getNome() + " - " + object.getTurma().getDescricaoSemDocente();
				String texto = "Um material foi alterado na turma virtual: " + object.getTurma().getDescricaoSemDocente() + "<p>Para visualiza-lo acesse a turma virtual no SIGAA</p>";
				notificarTurma(assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE , ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);	
			}
			
			aposPersistir();
			
			if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
				TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
				return tBean.retornarParaTurma();
			} else 
				return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
		} catch	(DAOException e){
			tratamentoErroPadrao(e);
			return null;		
		}
	}
	
	/**
	 * Altera a referência selecionada.<br/><br/>
	 * 
	 * Não invocado por JSPs
	 */
	public void atualizarSimples(IndicacaoReferencia indicacaoReferencia) throws ArqException{
		
		object = indicacaoReferencia;
		prepare(SigaaListaComando.ATUALIZAR_AVA);
		registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.INICIAR_ALTERACAO, object.getId());
		
		instanciarAposEditar();
		
		antesPersistir();
		
		Specification specification = getEspecificacaoAtualizacao();
		if (specification == null || specification instanceof NullSpecification ) 
			specification = getEspecificacaoCadastro();
		
		execute(SigaaListaComando.ATUALIZAR_AVA, object, specification);
		
		registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.ALTERAR, object.getId());
		
	}
		
	/**
	 * Remove a referência selecionada.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/IndicacaoReferencia/listar.jsp
	 */
	@Override
	public String remover() {
		
		try {
			prepare(SigaaListaComando.REMOVER_AVA);
			
			if (idReferenciaSelecionada <= 0)
				object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), IndicacaoReferencia.class);
			else {
				object = new IndicacaoReferencia ();
				object.setId(idReferenciaSelecionada);
			}
			
			if (object == null || !object.isAtivo()){
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
				return tBean.retornarParaTurma();
			}
			
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.INICIAR_REMOCAO, object.getId());
			
			antesRemocao();
			Notification notification = execute(SigaaListaComando.REMOVER_AVA, object, getEspecificacaoRemocao());
			
			if (notification.hasMessages())
				return notifyView(notification);
			
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.REMOVER, object.getId());
			listagem = null;		
			flash("Referência removida com sucesso.");

		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// força recarregar os tópicos de aulas
		clearCacheTopicosAula();

		return null;	
	}
	
	/**
	 * Remove a referência selecionada.<br/><br/>
	 * 
	 * Não invocado por JSPs
	 */
	public void removerSimples(IndicacaoReferencia indicacaoReferencia) {
		object = indicacaoReferencia;
		prepare(SigaaListaComando.REMOVER_AVA);
		registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.INICIAR_ALTERACAO, object.getId());
		antesRemocao();
		execute(SigaaListaComando.REMOVER_AVA, object, getEspecificacaoRemocao());
		registrarAcao(object.getTitulo(), EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.REMOVER, object.getId());
	}
	
	/**
	 * Retorna a especificação para a validação do cadastro de indicações de referência.<br/><br/>
	 * 
	 * Método não invocado por JSPs.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				IndicacaoReferencia ref = (IndicacaoReferencia) objeto;
//				if (ref.getAula() == null || ref.getAula().getId() == 0)
//					notification.addError("É obrigatório informar o tópico de aula");
				if (isEmpty(ref.getNome()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome/Título");
				if (ref.getTipo() == '-')
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo");
				/*if (isEmpty(ref.getAula()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tópico de Aula");*/
				//caso o tipo seja Site e a URL esteja vazia.
				if (ref.getTipo() == 'S' && ref.getUrl().equals("http://"))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Endereço (URL)");
				
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Retorna a listagem das referências de uma turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 */
	@Override
	public String listar() {
		listagem = null;
		return forward("/ava/IndicacaoReferencia/listar.jsp");
	}

	/**
	 * Exibe o formulário para se cadastrar uma indicação de referência.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/IndicacaoReferencia/listar.jsp
	 */
	public void inserirReferencia(Integer idTopicoSelecionado) {
		instanciar();
		object.setAula(new TopicoAula(idTopicoSelecionado));		
		inserirReferenciaEmTopico = true;
		novo();
	}

	/**
	 * Prepara para cadastar uma nova indicação de referência.
	 * Método chamado pela seguinte JSP: sigaa.war/ava/IndicacaoReferencia/listar.jsp
	 * @return
	 */
	public String novo() {
		try {
			if (!inserirReferenciaEmTopico)
				instanciar();
			
			inserirReferenciaEmTopico = false;
			if (cadastrarEmVariasTurmas()) {
				cadastrarEm = new ArrayList<String>();
				cadastrarEm.add(String.valueOf(turma().getId()));
			}
			prepare(SigaaListaComando.CADASTRAR_AVA);
			return forward("/ava/" + getClasse().getSimpleName() + "/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Cadastra uma nova indicação de referência.
	 * Método chamado pela seguinte JSP: sigaa.war/ava/IndicacaoReferencia/novo.jsp
	 * @return
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		object.setTurma(turma());
		if (object.getAula().getId() == 0)
			object.setAula(null);
		
		object.setTipoIndicacao(IndicacaoReferencia.TIPO_INDICACAO_BASICA);
		
		if ( !object.isLivro() ){
			object.setTituloCatalografico(null);
			object.setTitulo(null);
			object.setAutor(null);
			object.setEditora(null);
			object.setEdicao(null);
			object.setAno(null);
		}
		
		super.cadastrar(); 
		
		if (!hasErrors()) {
			listagem = null;
			return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
		}
		else {
			if (object.getAula() == null)
			object.setAula(new TopicoAula());
			
			return redirectMesmaPagina();
		}
	}
	
	/**
	 * Efetua o cadastro de uma indicação de referência.
	 * 
	 * Método não é chamado por JSP.
	 * 
	 */
	public void cadastrarSimples(IndicacaoReferencia indicacaoReferencia, Turma turma) throws ArqException {
		
		if (!inserirReferenciaEmTopico)
			instanciar();
		
		inserirReferenciaEmTopico = false;
		if (cadastrarEmVariasTurmas()) {
			cadastrarEm = new ArrayList<String>();
			cadastrarEm.add(String.valueOf(turma.getId()));
		}
		
		object = indicacaoReferencia;
		if (object.getTipoIndicacao() == null)
			object.setTipoIndicacao(IndicacaoReferencia.TIPO_INDICACAO_BASICA);
		
		// Registra a tentativa de cadastrar algum objeto.
		registrarAcao(AcaoAva.INICIAR_INSERCAO);
		
		antesPersistir();
		prepare(SigaaListaComando.CADASTRAR_AVA);
		execute(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());
		registrarAcao(AcaoAva.INSERIR);
		
	}
	
	@Override
	public String mostrar() throws DAOException {

		String paginaDestino = super.mostrar();
		paginaOrigem = getParameter("paginaOrigem");
		return paginaDestino;
	}
	
	/**
	 * Volta para a listagem das indicações de referência.
	 * 
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/ava/IndicacaoReferencia/editar.jsp</li>
	 * 	<li>sigaa.war/ava/IndicacaoReferencia/referencia.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarAnterior () {
		
		if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
			try {
			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			return tBean.retornarParaTurma();
			} catch (DAOException e){
				tratamentoErroPadrao(e);
				return null;
			}
		} else 
			return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
		
		
	}
	
	/**
	 * Redefinição da página após o cadastrar
	 * Método não chamado por JSPs.
	 * @return
	 */
	@Override
	public String forwardCadastrar() {
		return "/ava/"+ getClasse().getSimpleName() +"/listar.jsf";
	}
	
	/**
	 * Indica se a indicação de referência será cadastrada em várias turmas 
	 * Método não chamado por JSPs.
	 * @return
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	public int getIdReferenciaSelecionada() {
		return idReferenciaSelecionada;
	}

	public void setIdReferenciaSelecionada(int idReferenciaSelecionada) {
		this.idReferenciaSelecionada = idReferenciaSelecionada;
	}
	
	/**
	 * Recupera os dados do título catalográfico associado a uma referência. 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/IndicacaoReferencia/referencia.jsp
	 * @return
	 */
	public CacheEntidadesMarc getTituloCatalografico() throws DAOException
	{
		TituloCatalograficoDao tDao = null;
		
		try{
			
			tDao = getDAO(TituloCatalograficoDao.class);
			object.setTituloCatalografico(tDao.refresh(object.getTituloCatalografico()));
			CacheEntidadesMarc cache = BibliotecaUtil.obtemDadosTituloCache(object.getTituloCatalografico().getId());
		
			return cache;
			
		}finally{
			if( tDao != null )
				tDao.close();
		}
	}
	
	/**
	 * Recupera os exemplares do título catalográfico associado a referência. 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/IndicacaoReferencia/referencia.jsp
	 * @return
	 */
	public List<Exemplar> getExemplares() throws DAOException
	{
			ExemplarDao eDao = null;
			EmprestimoDao emDao = null;
			
			try {
				if ( object.getExemplares() == null ){
					eDao = getDAO(ExemplarDao.class);
					emDao = getDAO(EmprestimoDao.class);
					
					object.setTituloCatalografico(eDao.refresh(object.getTituloCatalografico()));
					object.setExemplares(eDao.findAllExemplarAtivosByTitulo(object.getTituloCatalografico().getId()));
				
					// Recupera o prazo do empréstimo 
					if ( object.getExemplares() != null ) {
						for (Exemplar e : object.getExemplares() ){
							if ( e.isEmprestado() )
								e.setPrazoEmprestimo(emDao.findPrazoDevolucaoMaterial(e.getId()));
						}	
					}
						
				}
				return object.getExemplares();
			}finally{
				if (eDao != null)
					eDao.close();
				if (emDao != null)
					emDao.close();
			}
	}
	
	/**
	 * Após recuperar o fluxo do título selecionado, prepara a indicação de referência com os dados do título. 
	 * Método não chamado por JSPs
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		
		if (titulo != null){
				
			titulo = BibliotecaUtil.obtemDadosTituloCache(titulo.getIdTituloCatalografico());
			TituloCatalografico tituloCatalografico = getGenericDAO().findByPrimaryKey(titulo.getIdTituloCatalografico(), TituloCatalografico.class);
				
			object.setDetalhes(new FormatosBibliograficosUtil().gerarFormatoReferencia(tituloCatalografico, true));
			object.setTituloCatalografico(new TituloCatalografico (titulo.getIdTituloCatalografico()));
			object.setDescricao(titulo.getTitulo());
			object.setTitulo(titulo.getTitulo());
			object.setAutor(titulo.getAutor());
			
			StringBuilder editoraTemp =  new StringBuilder();
			
			for (String editora : titulo.getEditorasFormatadas()) {
				editoraTemp.append(editora+" ");
			} 
			object.setEditora(editoraTemp.toString());
					
			StringBuilder anoTemp =  new StringBuilder();
			
			for (String ano : titulo.getAnosFormatados()) {
				anoTemp.append(ano+" ");
			} 
			object.setAno(anoTemp.toString());			
			object.setEdicao(titulo.getEdicao());
			
			// Se não passou os dados do acervo da biblitoeca, vai abrir os campos para o usuário digitar
			} else {
				
				object.setTituloCatalografico(null);
				object.setTitulo(null);
				object.setAutor(null);
				object.setEditora(null);
				object.setAno(null);
				object.setEdicao(null);
				
		}

		if (object.getId() == 0)
			return forward("/ava/IndicacaoReferencia/novo.jsp");
		else
			return forward("/ava/IndicacaoReferencia/editar.jsp");
	}

	/**
	 * Realiza uma busca no acervo.<br/><br/>
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/IndicacaoReferencia/_form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String pesquisarNoAcervo (){
		PesquisaInternaBibliotecaMBean pBean = getMBean ("pesquisaInternaBibliotecaMBean");
		return pBean.iniciarBusca(this, null);
	}
	
	/**
	 *  Método para indicar se o botão voltar deve ser mostrado ou não na tela da pesquisa no acervo.
	 *  Método não chamado por JSPs
	 *  @return
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.titulo = titulo;
	}

	/**
	 *  Ação ao clicar no botão voltar na tela da pesquisa no acervo.
	 *  Método não chamado por JSPs
	 *  @return
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		// Volta para a página de formulário do cadastro de plano de curso, caso o usuário utiliza o botão voltar na busca no acervo da biblioteca
		return forward("/ava/IndicacaoReferencia/novo.jsp");
	}
	
	public CacheEntidadesMarc getTitulo() {
		return titulo;
	}

}