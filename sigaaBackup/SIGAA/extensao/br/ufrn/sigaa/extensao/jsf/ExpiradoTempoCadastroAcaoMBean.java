/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/03/2010
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
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * MBean responsável por encerrar atividades de extensão com prazo estabelecido pela proex.
 * @author Geyson
 *
 */
@Component("expirarTempoCadastro")
@Scope("request")
public class ExpiradoTempoCadastroAcaoMBean extends SigaaAbstractController<AtividadeExtensao> {
	
	/** Lista de Ações localizadas com cadastro em andamento ocioso. */
	Collection<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();
	
	/** Lista de Ações das quais o usuário logado é o criador. */
	Collection<AtividadeExtensao> atividadesGravadas = new ArrayList<AtividadeExtensao>();
	
	/** Total de dias para com cadastro em andamento ocioso. */
	private Integer diasEncerrar = 0;
	
	/**
	 * Construtor padrão; 
	 */
	public ExpiradoTempoCadastroAcaoMBean(){
		obj = new AtividadeExtensao();
	}
	
	/**
	 * Redireciona para página para a realização do encerramento de ações de extensão isoladas com pendências.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/menu.jsp<li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciaEncerrar() throws ArqException{
		checkListRole();
		prepareMovimento(SigaaListaComando.ENCERRRAR_PROJETOS_COM_TEMPO_CADASTRO_EXPIRADO);
		setOperacaoAtiva(SigaaListaComando.ENCERRRAR_PROJETOS_COM_TEMPO_CADASTRO_EXPIRADO.getId());
		return forward("/extensao/ExpirarTempoCadastro/form.jsp");
	}
	
	/**
	 * Lista ações encerradas pela proex.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciaBuscaAcoesEncerradas() throws ArqException{
		checkListRole();
		prepareMovimento(SigaaListaComando.REATIVAR_PROJETO_COM_TEMPO_CADASTRO_EXPIRADO);
		setOperacaoAtiva(SigaaListaComando.REATIVAR_PROJETO_COM_TEMPO_CADASTRO_EXPIRADO.getId());
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		Integer[] idSituacao = new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_TEMPO_DE_CADASTRO_EXPIRADO};
		// Evita erro de null pointer quando um docente externo desejar listar as ações encerradas
		if (getUsuarioLogado().getServidor() == null){
			atividadesGravadas = dao.findByCoordenadorAtivo(getUsuarioLogado().getDocenteExterno(), idSituacao);
		} else {
			atividadesGravadas = dao.findByCoordenadorAtivo(getUsuarioLogado().getServidor(), idSituacao);
		}
		
		return forward("/extensao/ExpirarTempoCadastro/lista.jsp");
	}
	
	
	
	/**
	 * Reativa ação de extensão para que o docente possa continuar o cadastro da ação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/ExpirarTempoCadastro/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String reativarProposta() throws ArqException{
		if(checkOperacaoAtiva(SigaaListaComando.REATIVAR_PROJETO_COM_TEMPO_CADASTRO_EXPIRADO.getId())){
			checkListRole();
			Integer id = getParameterInt("id", 0);
			MovimentoCadastro mov = new MovimentoCadastro();	
			AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
			obj = dao.findByPrimaryKey(id, AtividadeExtensao.class);
			mov.setCodMovimento(SigaaListaComando.REATIVAR_PROJETO_COM_TEMPO_CADASTRO_EXPIRADO);
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
		}
		
		return redirectJSF(getSubSistema().getLink());
	}
	
	
	/**
	 * Encerra ações de extensão com cadastro em andamento após dias estabelecidos pela proex.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/ExpirarTempoCadastro/form.jsp<li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String encerrar() throws DAOException{

		if(!checkOperacaoAtiva(SigaaListaComando.ENCERRRAR_PROJETOS_COM_TEMPO_CADASTRO_EXPIRADO.getId())){
			return forward("/extensao/menu.jsp");
		}

		if(ValidatorUtil.isEmpty(diasEncerrar)){
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Dias");
			
		}else {		
			atividades = getDAO(AtividadeExtensaoDao.class).findByCadastroEmAndamentoOcioso(diasEncerrar);
			
			if(!ValidatorUtil.isEmpty(atividades)) {
				try {
					MovimentoCadastro mov = new MovimentoCadastro();	
					mov.setCodMovimento(SigaaListaComando.ENCERRRAR_PROJETOS_COM_TEMPO_CADASTRO_EXPIRADO);
					mov.setColObjMovimentado(atividades);
					execute(mov);
					removeOperacaoAtiva();
					addMensagem(OPERACAO_SUCESSO);
					return forward("/extensao/menu.jsp");
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
				} catch (ArqException e) {
					return tratamentoErroPadrao(e);
				}
			}			
		}
		return null;
	}
	
	/**
	 * Lista ações de extensão com cadastro em andamento após dias estabelecidos pela proex.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/ExpirarTempoCadastro/form.jsp<li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String preEncerrar() throws DAOException {
		atividades = new ArrayList<AtividadeExtensao>();
		if(ValidatorUtil.isEmpty(diasEncerrar)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Dias");
			return null;
		}else {
			atividades = getDAO(AtividadeExtensaoDao.class).findByCadastroEmAndamentoOcioso(diasEncerrar);
		}
		return null;
	}

	public Integer getDiasEncerrar() {
		return diasEncerrar;
	}

	public void setDiasEncerrar(Integer diasEncerrar) {
		this.diasEncerrar = diasEncerrar;
	}

	public Collection<AtividadeExtensao> getAtividadesGravadas() {
		return atividadesGravadas;
	}

	public void setAtividadesGravadas(
			Collection<AtividadeExtensao> atividadesGravadas) {
		this.atividadesGravadas = atividadesGravadas;
	}

	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
	}

}
