/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/07/2010
 *
 */
package br.ufrn.sigaa.extensao.jsf;

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
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RecuperarAcoesDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * MBean responsável por recuperar atividades de extensão removidas.
 * @author Geyson Karlos
 *
 */
@Component("recuperarAcoes")
@Scope("request")
public class RecuperarAcoesMBean extends SigaaAbstractController<AtividadeExtensao> {
	
	Collection<AtividadeExtensao> atividades;
	
	private boolean checkBuscaTitulo;
	private boolean checkBuscaServidor;
    private boolean checkBuscaAno;
    private boolean realizouBusca = false;
    private MembroProjeto membroEquipe = new MembroProjeto();
    
    private String buscaNomeAtividade;
    private Integer buscaAno = CalendarUtils.getAnoAtual();
    private String buscaTitulo;
    
    private Integer totalAt;

	
	public RecuperarAcoesMBean(){
		obj = new AtividadeExtensao();
		atividades = new ArrayList<AtividadeExtensao>();
		buscaNomeAtividade = null;
		buscaTitulo = null;
	}
	
	/**
	 * Checa se usuário tem o papel de gestor de extensão e redireciona para página de busca.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciaRecuperacao() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		realizouBusca = false;
		prepareMovimento(SigaaListaComando.RECUPERAR_ACAO_EXTENSAO_REMOVIDA);
		
		return forward("/extensao/RecuperarAcoes/form.jsp");
	}
	
	/**
	 * Busca ações de extensão removidas e redireciona para página para a visualização das ações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RecuperarAcoes/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public String buscarAcoes() throws SegurancaException, LimiteResultadosException, DAOException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		
		setOperacaoAtiva(SigaaListaComando.RECUPERAR_ACAO_EXTENSAO_REMOVIDA.getId());
		
		Integer idServidor = null;
		Integer ano = null;
		String titulo = null;
		realizouBusca = true;
		
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
			RecuperarAcoesDao dao = getDAO(RecuperarAcoesDao.class);
			atividades = dao.findByAcoes(ano, idServidor, titulo);
			totalAt = atividades.size();
		
			return null;//forward("/extensao/RecuperarAcoes/lista.jsp");
		}
		
		return null;
	}
	
	/**
	 * Recupera ação selecionada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RecuperarAcoes/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String recuperarAcao() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		if(checkOperacaoAtiva(SigaaListaComando.RECUPERAR_ACAO_EXTENSAO_REMOVIDA.getId())){
			Integer id = getParameterInt("id", 0);
			
			AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
			MovimentoCadastro mov = new MovimentoCadastro();
			
			obj = dao.findByPrimaryKey(id, AtividadeExtensao.class);
			mov.setCodMovimento(SigaaListaComando.RECUPERAR_ACAO_EXTENSAO_REMOVIDA);
			mov.setObjMovimentado(obj);
			
			try {
				execute(mov);
			} catch (NegocioException e) {
				return tratamentoErroPadrao(e);
			} catch (ArqException e) {
				return tratamentoErroPadrao(e);
			}
			removeOperacaoAtiva();
			addMensagem(OPERACAO_SUCESSO);
		}else{
			
			return forward("/extensao/menu.jsp");
		}
			
		
		return forward("/extensao/menu.jsp");
		
	}

	
	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
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

	public String getBuscaNomeAtividade() {
		return buscaNomeAtividade;
	}

	public void setBuscaNomeAtividade(String buscaNomeAtividade) {
		this.buscaNomeAtividade = buscaNomeAtividade;
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
