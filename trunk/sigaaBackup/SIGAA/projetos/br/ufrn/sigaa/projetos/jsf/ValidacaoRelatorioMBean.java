/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/04/2011'
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.projetos.RelatorioAcaoAssociadaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.projetos.dominio.RelatorioAcaoAssociada;
import br.ufrn.sigaa.projetos.dominio.TipoParecerAvaliacaoProjeto;


/*******************************************************************************
 * MBean usado na validação de Relatórios de Ações Acadêmicas Associadas pelos chefes
 * dos departamentos envolvidos na ação através da Unidade Proponente.<br/>
 * 
 * Nas autorizações de relatórios: O chefe lista todas as ações pendentes de validação 
 * e após a seleção de uma delas será exibido um formulário com dados da ação 
 * e campos para o seu parecer. Caso o relatório seja reprovado ele não segue 
 * para a Avaliação do Comitê e o coordenador da ação deverá alterá-lo. 
 * O relatório só segue para a avaliação do Comitê depois que o chefe do departamento 
 * o aprova.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("validacaoRelatorioBean")
@Scope("request")
public class ValidacaoRelatorioMBean extends SigaaAbstractController<RelatorioAcaoAssociada> {

	/** Atributo utilizado para representar o relatório da Ação Associada. */
	private RelatorioAcaoAssociada relatorio;
	
	/** Atributo utilizado para representar o departamento que valida o relatório. */
	private Unidade unidade = new Unidade(0);
	
	/** A lista dos relatórios da Unidade */
	private List<RelatorioAcaoAssociada> relatoriosUnidade;

	
	/** 
	 * Construtor padrão
	 */
	public ValidacaoRelatorioMBean() {
		obj = new RelatorioAcaoAssociada();
	}

	
	/**
	 * Inicia a chamada para autorização dos relatórios pelo chefe de
	 * departamento
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.ear/sigaa.war/projetos/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String relatoriosPendentesDepartamento() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE);
		relatoriosUnidade = getRelatoriosUnidadesUsuarioLogado();
		return forward(ConstantesNavegacaoProjetos.VALIDAR_RELATORIO_DEPARTAMENTO_LISTA);
	}

	
	/**
	 * Lista de relatórios de todas as  unidades onde o usuário logado tem algum tipo de responsabilidade. 
	 * Utilizado na autorização dos chefes de departamentos.
	 * 
	 * <br />
	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public List<RelatorioAcaoAssociada> getRelatoriosUnidadesUsuarioLogado() {
		
		UnidadeDAOImpl undDao = new UnidadeDAOImpl(Sistema.COMUM);
		try {
			List<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();			
			unidades.addAll( undDao.findUnidadesByResponsavel(getUsuarioLogado().getServidor().getId(), new Character[] {NivelResponsabilidade.CHEFE, NivelResponsabilidade.VICE, NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO}));			
			if ((unidades == null) || (unidades.isEmpty())) {
				addMensagemErro("O usuário atual não possui unidades configuradas sob sua responsabilidade.");
			}				
			return getDAO(RelatorioAcaoAssociadaDao.class).findByUnidades(unidades);
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<RelatorioAcaoAssociada>();
		}finally {
			undDao.close();
		}
		
	}
	

	/**
	 * Chefe de departamento ou diretor de centro escolhe um relatório de
	 * projeto que ainda não foi analisado.
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/ValidarRelatorio/Departamento/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String analisarPorDepartamento() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.CHEFE_UNIDADE);
		try {
			relatorio = getGenericDAO().findByPrimaryKey(getParameterInt("id"), RelatorioAcaoAssociada.class);
			relatorio.setTipoParecerDepartamento(getGenericDAO().findByPrimaryKey(TipoParecerAvaliacaoProjeto.APROVADO, TipoParecerAvaliacaoProjeto.class));
			prepareMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_DPTO);
			setConfirmButton("Confirmar Validação");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return forward(ConstantesNavegacaoProjetos.VALIDAR_RELATORIO_DEPARTAMENTO_FORM);
	}
	
	
	/**
	 * Chefe de departamento ou diretor de centro valida um relatório.
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.ear/sigaa.war/projetos/ValidarRelatorio/Departamento/form.jsp</li>
	 * </ul>
 
	 * @return
	 * @throws ArqException
	 */
	public String validarPorDepartamento() throws ArqException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, 
				SigaaPapeis.CHEFE_UNIDADE, SigaaPapeis.GESTOR_EXTENSAO);

		try {

			relatorio.setRegistroEntradaDepartamento(getUsuarioLogado().getRegistroEntrada().getId());
			relatorio.setDataValidacaoDepartamento(new Date());

			if (ValidatorUtil.isEmpty(relatorio.getTipoParecerDepartamento())) {
				addMensagemErro("Parecer: Campo obrigatório não informado");
				return null;
			}
			
			ListaMensagens lista = relatorio.validate();
			if (ValidatorUtil.isNotEmpty(lista)) {
				addMensagens(lista);
				return null;
			}

			//Tipo de parecer do departamento.
			getGenericDAO().initialize(relatorio.getTipoParecerDepartamento());

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(relatorio);
			mov.setCodMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_DPTO);

			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			if (relatorio.isReprovadoDepartamento() || relatorio.isAprovadoComRecomendacoesDepartamento()) {
				addMensagemInformation("Relatório devolvido à coordenação do projeto para correções.");
			}

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}

		return relatoriosPendentesDepartamento();
	}

	

	/**
	 * Inicia a chamada para autorização dos relatórios pelo Comitê como chefe de departamento.
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.ear/sigaa.war/projetos/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String relatoriosPendenteComite() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		RelatorioAcaoAssociadaDao dao = getDAO(RelatorioAcaoAssociadaDao.class);
		relatoriosUnidade = dao.findAllRelatoriosPendentesComite(null);
		//listarRelatoriosPorUnidade();
		return forward(ConstantesNavegacaoProjetos.VALIDAR_RELATORIO_COMITE_LISTA);
	}

	

	/**
	 * Lista todos os relatórios para a unidade informada.
	 * Utilizado quando um membro do comitê valida relatórios COMO chefe de departamento.
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.ear/sigaa.war/projetos/menu.jsp</li>
	 * </ul>
   
	 * @throws SegurancaException
	 *  
	 */
	public void listarRelatoriosPorUnidade() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);

		try {
			int idUnidade = getParameterInt("idUnidade", 0);
			if (idUnidade > 0) {
				unidade = new Unidade(idUnidade);
			}

			RelatorioAcaoAssociadaDao dao = getDAO(RelatorioAcaoAssociadaDao.class);
			ArrayList<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();
			unidades.add(unidade);
			
			relatoriosUnidade = dao.findByUnidades(unidades);

		} catch (DAOException e) {
			notifyError(e);
			relatoriosUnidade = new ArrayList<RelatorioAcaoAssociada>();
		}
	}

	
	/**
	 * Membro do comitê integrado escolhe um relatório de projeto que ainda não foi analisado.
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/ValidarRelatorio/Comite/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String analisarPorComite() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		try {
			relatorio = getGenericDAO().findByPrimaryKey(getParameterInt("id"), RelatorioAcaoAssociada.class);
			relatorio.setTipoParecerComite(getGenericDAO().findByPrimaryKey(TipoParecerAvaliacaoProjeto.APROVADO, TipoParecerAvaliacaoProjeto.class));
			prepareMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_COMITE);
			setConfirmButton("Confirmar Validação");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return forward(ConstantesNavegacaoProjetos.VALIDAR_RELATORIO_COMITE_FORM);
	}

	

	/**
	 * Membro do comitê integrado valida um relatório.
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.ear/sigaa.war/projetos/ValidarRelatorio/Departamento/form.jsp</li>
	 * </ul>
 
	 * @return
	 * @throws ArqException
	 */
	public String validarPorComite() throws ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		try {
			
			relatorio.setRegistroEntradaComite(getUsuarioLogado().getRegistroEntrada().getId());
			relatorio.setDataValidacaoComite(new Date());

			if (ValidatorUtil.isEmpty(relatorio.getTipoParecerComite())) {
				addMensagemErro("Parecer: Campo obrigatório não informado.");
				return null;
			}
			
			ListaMensagens lista = obj.validate();
			if (ValidatorUtil.isNotEmpty(lista)) {
				addMensagens(lista);
				return null;
			}

			//Tipo de parecer do departamento.
			getGenericDAO().initialize(relatorio.getTipoParecerComite());

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(relatorio);
			mov.setCodMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_COMITE);

			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}

		return relatoriosPendenteComite();
	}

	
	/**
	 * Membro do comitê integrado devolve um relatório para um coordenador de projeto.
	 * 
 	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.ear/sigaa.war/projetos/ValidarRelatorio/Departamento/lista.jsp</li>
	 * </ul>
 
	 * @return
	 * @throws ArqException
	 */
	public String devolverRelatorio() throws ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		try {
			relatorio = getGenericDAO().findByPrimaryKey(getParameterInt("id"), RelatorioAcaoAssociada.class);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(relatorio);
			mov.setCodMovimento(SigaaListaComando.DEVOLVER_RELATORIO_ACAO_ASSOCIADA_COORD);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}

		return relatoriosPendenteComite();
	}
	
	

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}


	public RelatorioAcaoAssociada getRelatorio() {
		return relatorio;
	}


	public void setRelatorio(RelatorioAcaoAssociada relatorio) {
		this.relatorio = relatorio;
	}


	public List<RelatorioAcaoAssociada> getRelatoriosUnidade() {
		return relatoriosUnidade;
	}


	public void setRelatoriosUnidade(List<RelatorioAcaoAssociada> relatoriosUnidade) {
		this.relatoriosUnidade = relatoriosUnidade;
	}

}

