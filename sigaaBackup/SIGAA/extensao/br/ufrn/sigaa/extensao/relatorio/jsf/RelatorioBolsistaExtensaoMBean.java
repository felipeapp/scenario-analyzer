/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/12/2006
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioBolsistaExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.extensao.negocio.RelatorioExtensaoValidator;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioBolsistaExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;

/**
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 */
@Scope("session")
@Component("relatorioBolsistaExtensao")
public class RelatorioBolsistaExtensaoMBean extends
		SigaaAbstractController<RelatorioBolsistaExtensao> {

	private Collection<DiscenteExtensao> discentesExtensaoUsuarioLogado = new ArrayList<DiscenteExtensao>();
	private Collection<RelatorioBolsistaExtensao> relatoriosCoordenadorAvaliar = new ArrayList<RelatorioBolsistaExtensao>();

	public RelatorioBolsistaExtensaoMBean() {
		obj = new RelatorioBolsistaExtensao();
	}

	/**
	 * Verifica permiss�es e redireciona para tela de lista de relat�rios
	 * cadastrados pelo usu�rio atual.
	 * 
	 * sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarCadastroRelatorio() throws SegurancaException, DAOException {
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		if (discente == null) {
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
		}
		obj = new RelatorioBolsistaExtensao();
		atualizarDiscentesUsuarioLogado();		
		return forward(ConstantesNavegacao.RELATORIOBOLSISTAEXTENSAO_LISTA);
	}
	

	/**
	 * Atualiza a lista de discentes do usu�rio logado
	 * 
	 */
	private void atualizarDiscentesUsuarioLogado() {
		// atualizando lista de discentes de extens�o do usu�rio atual
		DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class);
		try {
			discentesExtensaoUsuarioLogado = dao
					.findByDiscenteComPlanoTrabalho(getUsuarioLogado()
							.getDiscenteAtivo().getId(),
							null);

			// evitando erro de lazy
			for (DiscenteExtensao dis : discentesExtensaoUsuarioLogado)
				dis.setRelatorios(getGenericDAO().findByExactField(
						RelatorioBolsistaExtensao.class, "discenteExtensao.id",
						dis.getId()));

		} catch (DAOException e) {
			notifyError(e);
			discentesExtensaoUsuarioLogado = new ArrayList<DiscenteExtensao>();
		}
	}

	/**
	 * Inicia o caso de uso de submeter relat�rio, popula os dados necessarios
	 * em sess�o
	 * 
	 * sigaa.war/extensao/RelatorioBolsistaExtensao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAdicionarRelatorio() throws ArqException {
		if (getUsuarioLogado().getDiscenteAtivo() == null)
			throw new SegurancaException(
					"Usu�rio n�o autorizado a realizar esta opera��o");

		Integer idDiscenteExtensao = getParameterInt("idDiscente", 0);
		Boolean relatorioFinal = getParameterBoolean("relatorioFinal");

		TipoRelatorioExtensao tipoRelatorio;
		if (relatorioFinal) {
			tipoRelatorio = getGenericDAO().findByPrimaryKey(
					TipoRelatorioExtensao.RELATORIO_FINAL,
					TipoRelatorioExtensao.class);
		} else {
			tipoRelatorio = getGenericDAO().findByPrimaryKey(
					TipoRelatorioExtensao.RELATORIO_PARCIAL,
					TipoRelatorioExtensao.class);
		}

		ListaMensagens lista = new ListaMensagens();

		if ((idDiscenteExtensao != null) && (idDiscenteExtensao > 0)) {
			RelatorioExtensaoValidator.validaNovoRelatorioBolsista(idDiscenteExtensao, tipoRelatorio.getId(), lista);
		}
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}

		prepareMovimento(ArqListaComando.CADASTRAR);
		DiscenteExtensao discenteExtensao = getGenericDAO().findByPrimaryKey(idDiscenteExtensao, DiscenteExtensao.class);
		obj = new RelatorioBolsistaExtensao();
		obj.setDiscenteExtensao(discenteExtensao);
		obj.setDataCadastro(new Date());
		obj.setRegistroEntradaCadastro(getUsuarioLogado().getRegistroEntrada());
		obj.setTipoRelatorio(tipoRelatorio);
		obj.setAtivo(true);
		setReadOnly(false);
		setConfirmButton("Enviar Relat�rio");
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(ConstantesNavegacao.RELATORIOBOLSISTAEXTENSAO_FORM);

	}

	@Override
	public void beforeCadastrarAndValidate() {
		if (obj.getDataCadastro() == null)
			obj.setDataCadastro(new Date());

		obj.setDataEnvio(new Date());

	}

	@Override
	protected void afterCadastrar() {
		obj = new RelatorioBolsistaExtensao();
	}

	@Override
	public String getDirBase() {
		return "/extensao/RelatorioBolsistaExtensao";
	}

	@Override
	public String forwardCadastrar() {
		try {

			atualizarDiscentesUsuarioLogado();

			return ConstantesNavegacao.RELATORIOBOLSISTAEXTENSAO_LISTA;

		} catch (Exception e) {
			notifyError(e);
			return null;
		}
	}

	/**
	 * Usado na valida��o do relat�rio pelo Coordenador
	 * 
	 * Chamado por:
	 * <ul>
	 * 	<li> sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String listarRelatoriosDiscentes() throws DAOException {
		relatoriosCoordenadorAvaliar = getDAO(RelatorioBolsistaExtensaoDao.class).findByCoordenador(getUsuarioLogado().getPessoa().getId());
		return forward(ConstantesNavegacao.RELATORIOBOLSISTAEXTENSAO_AVALIAR_LISTA);
	}

	/**
	 * Inicia a avalia��o de relat�rio do bolsista.
	 * 
	 * sigaa.war/extensao/RelatorioBolsistaExtensao/avaliar_relatorio_lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAvaliacao() throws ArqException {
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
		}

		Integer id = getParameterInt("id");
		if (id == null) {
			addMensagemErro("Selecione um Relat�rio para avaliar!");
			return null;
		}

		obj = getGenericDAO().findByPrimaryKey(id,	RelatorioBolsistaExtensao.class);
		
		//evitar erro de lazy
		obj.getDiscenteExtensao().getPlanoTrabalhoExtensao().getId();

		prepareMovimento(SigaaListaComando.VALIDAR_RELATORIO_DISCENTE_EXTENSAO);
		return forward(ConstantesNavegacao.RELATORIOBOLSISTAEXTENSAO_AVALIAR_FORM);

	}

	/**
	 * Valida��o do relat�rio realizada pelo coordenador da a��o de extens�o
	 * 
	 * sigaa.war/extensao/RelatorioBolsistaExtensao/avaliar_relatorio_form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String validarRelatorio() throws SegurancaException {
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
		}

		ListaMensagens mensagens = new ListaMensagens();
		ValidatorUtil.validateRequired(obj.getParecerOrientador(), "Parecer",	mensagens);
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		obj.setRegistroParecer(getUsuarioLogado().getRegistroEntrada());
		obj.setDataParecer(new Date());

		CadastroExtensaoMov mov = new CadastroExtensaoMov();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.VALIDAR_RELATORIO_DISCENTE_EXTENSAO);

		try {
			execute(mov, getCurrentRequest());
			addMessage("Valida��o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
			return listarRelatoriosDiscentes();

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}
	}

	/**
	 * Carrega relat�rio e prepara para visualiza��o.
	 *
	 * sigaa.war/extensao/RelatorioBolsistaExtensao/avaliar_relatorio_lista.jsp
	 * sigaa.war/extensao/RelatorioBolsistaExtensao/lista.jsp
	 * 
	 * @return
	 */
	public String view() {

		Integer id = getParameterInt("id");

		try {

			RelatorioBolsistaExtensaoDao dao = getDAO(RelatorioBolsistaExtensaoDao.class);
			obj = dao.viewRelatorioBolsistaExtensao(id);
			dao.close();
			return forward(ConstantesNavegacao.RELATORIOBOLSISTAEXTENSAO_VIEW);
			

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao Buscar relat�rio!");			
			return null;
		}
	}

	public Collection<DiscenteExtensao> getDiscentesExtensaoUsuarioLogado() {
		return discentesExtensaoUsuarioLogado;
	}

	@Override
	protected void beforeInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			notifyError(e);
		}
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		super.beforeInativar();
	}
	
	@Override
	protected void afterInativar() {
		atualizarDiscentesUsuarioLogado();		
		super.afterInativar();
	}

	public void setDiscentesExtensaoUsuarioLogado(
			Collection<DiscenteExtensao> discentesExtensaoUsuarioLogado) {
		this.discentesExtensaoUsuarioLogado = discentesExtensaoUsuarioLogado;
	}

	public Collection<RelatorioBolsistaExtensao> getRelatoriosCoordenadorAvaliar() {
		return relatoriosCoordenadorAvaliar;
	}

	public void setRelatoriosCoordenadorAvaliar(
			Collection<RelatorioBolsistaExtensao> relatoriosCoordenadorAvaliar) {
		this.relatoriosCoordenadorAvaliar = relatoriosCoordenadorAvaliar;
	}

}
