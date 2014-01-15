/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/07/2010
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.RecuperarAcoesAssociadasDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * MBean responsável por recuperar ações associadas removidas.
 * @author Amanda Priscilla
 *
 */
@Component("recuperarAcoesAssociadas")
@Scope("request")
public class RecuperarAcoesAssociadasMBean extends SigaaAbstractController<Projeto> {
	/** Armazena os projetos de ações associadas retornados pela busca.  **/
	Collection<Projeto> projetos;
	
	/** Verifica se a busca por título foi selecionada.  **/
	private boolean checkBuscaTitulo;
	/** Verifica se a busca por servidor foi selecionada.  **/
	private boolean checkBuscaServidor;
	/** Verifica se a busca por ano foi selecionada.  **/
    private boolean checkBuscaAno;
   
    /** Valor booleano que informa se uma busca já foi ou não realizada  **/
    private boolean realizouBusca = false;
    /** Armazena a lista de membros do projeto.  **/
    private MembroProjeto membroEquipe = new MembroProjeto();
    
    /** Campo de busca em que o usuário digita o nome do projeto.  **/
    private String buscaNomeProjeto;
    /** Campo de busca em que o usuário digita o ano do projeto.  **/
    private Integer buscaAno = CalendarUtils.getAnoAtual();
    /** Campo de busca em que o usuário digita o título do projeto.  **/
    private String buscaTitulo;
    
    /** Número de projetos retornados pela busca.  **/
    private Integer totalAt;

	/** Construtor padrão do RecuperarAcoesAssociadasMBean */
	public RecuperarAcoesAssociadasMBean(){
		obj = new Projeto();
		projetos = new ArrayList<Projeto>();
		buscaNomeProjeto = null;
		buscaTitulo = null;
	}
	
	/**
	 * Checa se usuário tem o papel de Membro do Comitê Integrado e redireciona para página de busca.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws ArqException 
	 */
	public String iniciaRecuperacao() throws ArqException{
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		realizouBusca = false;
		prepareMovimento(SigaaListaComando.RECUPERAR_ACAO_ASSOCIADA_REMOVIDA);
		
		return forward("/projetos/RecuperarAcoesAssociadas/form.jsp");
	}
	
	/**
	 * Busca ações associadas removidas e redireciona para a página de visualização das ações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RecuperarAcoesAssociadas/form.jsp</li>
	 * </ul>
	 * @return 
	 * @throws SegurancaException
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public String buscarAcoes() throws SegurancaException, LimiteResultadosException, DAOException{
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		
		setOperacaoAtiva(SigaaListaComando.RECUPERAR_ACAO_ASSOCIADA_REMOVIDA.getId());
		
		Integer idServidor = null;
		Integer ano = null;
		String titulo = null;
		realizouBusca = true;
		
		// Verifica e valida os parâmetros de busca escolhidos
		if (checkBuscaServidor) {
			if(membroEquipe.getServidor().getId() > 0){
				idServidor = membroEquipe.getServidor().getId();
			}
			else
				addMensagemErro("Servidor: preencher campo selecionado.");
		}
		if (checkBuscaAno) {
			if(buscaAno !=  null){
				ano = buscaAno;
			}
			else
				addMensagemErro("Ano: preencher campo selecionado.");
		}
		if(checkBuscaTitulo){
			if(!ValidatorUtil.isEmpty(buscaTitulo)){
				titulo = buscaTitulo;
			}else{
				addMensagemErro("Titulo: preencher campo selecionado.");
			}
		}
		
		if (!hasErrors()) {
			RecuperarAcoesAssociadasDao dao = getDAO(RecuperarAcoesAssociadasDao.class);
			projetos = dao.findByAcoes(ano, idServidor, titulo);
			totalAt = projetos.size();
			// Retorna nulo pois já tinha sido redirecionado a página de busca anteriormente (iniciaRecuperacao())
			return null;
		}
		
		return null;
	}
	
	/**
	 * Recupera ação selecionada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RecuperarAcoesAssociadas/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String recuperarAcao() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		if(checkOperacaoAtiva(SigaaListaComando.RECUPERAR_ACAO_ASSOCIADA_REMOVIDA.getId())){
			Integer id = getParameterInt("id", 0);
			
			ProjetoDao dao = getDAO(ProjetoDao.class);
			MovimentoCadastro mov = new MovimentoCadastro();
			try {
				obj = dao.findByPrimaryKey(id, Projeto.class);
				mov.setCodMovimento(SigaaListaComando.RECUPERAR_ACAO_ASSOCIADA_REMOVIDA);
				mov.setObjMovimentado(obj);
					
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
				
			} catch (ArqException e) {
				addMensagemErro(e.getMessage());
				return null;
			}finally{
				removeOperacaoAtiva();
			}
		
		}else{
			
			return forward("/projetos/menu.jsp");
		}
			
		
		return forward("/projetos/menu.jsp");
		
	}

	
	public Collection<Projeto> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<Projeto> projetos) {
		this.projetos = projetos;
	}

	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public MembroProjeto getMembroEquipe() {
		return membroEquipe;
	}

	public void setMembroEquipe(MembroProjeto membroEquipe) {
		this.membroEquipe = membroEquipe;
	}

	public String getBuscaNomeProjeto() {
		return buscaNomeProjeto;
	}

	public void setBuscaNomeProjeto(String buscaNomeProjeto) {
		this.buscaNomeProjeto = buscaNomeProjeto;
	}

	public Integer getBuscaAno() {
		return buscaAno;
	}

	public void setBuscaAno(Integer buscaAno) {
		this.buscaAno = buscaAno;
	}

	public Integer getTotalAt() {
		return totalAt;
	}

	public void setTotalAt(Integer totalAt) {
		this.totalAt = totalAt;
	}

	public String getBuscaTitulo() {
		return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}

	public boolean isRealizouBusca() {
		return realizouBusca;
	}

	public void setRealizouBusca(boolean realizouBusca) {
		this.realizouBusca = realizouBusca;
	}
	
	

}
