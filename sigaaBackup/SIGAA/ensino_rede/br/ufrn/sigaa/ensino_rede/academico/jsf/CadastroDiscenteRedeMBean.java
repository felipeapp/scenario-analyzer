package br.ufrn.sigaa.ensino_rede.academico.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.ensino_rede.dao.DiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.dominio.CursoAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscente;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscenteMBean;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@Component @Scope("session")
public class CadastroDiscenteRedeMBean extends EnsinoRedeAbstractController<DiscenteAssociado> implements SelecionaDiscente, OperadorDadosPessoais {

	public enum Acao {
	    CADASTRAR_DISCENTE, ALTERAR_DISCENTE, ATUALIZAR_DADOS_PESSOAIS
	}
	
	private Acao acao;
	
	private Collection<SelectItem> allProgramaRedeCombo;
	private Collection<SelectItem> cursosFromProgramaRedeCombo;
	private Collection<SelectItem> campusFromProgramaRedeCombo;
	
	/** Dados do Curso ao qual o discente está associado. */
	private DadosCursoRede dadosCursoRede;

	private Collection<SelectItem> statusCombo;
	
	public CadastroDiscenteRedeMBean() {
		allProgramaRedeCombo = null;
		cursosFromProgramaRedeCombo = null;
		campusFromProgramaRedeCombo = null;
	}
	
	public String iniciarAlterar() throws NegocioException, ArqException {
		acao = Acao.ALTERAR_DISCENTE;
		SelecionaDiscenteMBean mBean = getMBean("selecionaDiscenteMBean");
		mBean.setRequisitor(this);
		prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE_REDE);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_DISCENTE_REDE.getId());
		return mBean.executar();
	}
	
	public String iniciarAtualizarDadosPessoais() throws DAOException, NegocioException {
		acao = Acao.ATUALIZAR_DADOS_PESSOAIS;
		SelecionaDiscenteMBean mBean = getMBean("selecionaDiscenteMBean");
		mBean.setRequisitor(this);
		if (isCoordenadorUnidadeRede())
			return mBean.executar(getCampusIes());
		else
			return mBean.executar();
	}
	
	public String iniciarCadastrar() throws ArqException {
		acao = Acao.CADASTRAR_DISCENTE;
		obj = new DiscenteAssociado();
		obj.setAnoIngresso(CalendarUtils.getAnoAtual());
		obj.setPeriodoIngresso(getPeriodoAtual());
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE_REDE);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE_REDE.getId());
		return popularDadosPessoais();
	}
	
	/**
	 * Chama o operador de dados pessoais para realizar a verificação do CPF e redireciona para o cadastro do discente de graduação. <br /><br />
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/cdp.jsp
	 * @return
	 * @throws ArqException
	 */
	public String popularDadosPessoais() throws ArqException{
		//checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.CDP});
		setConfirmButton("Cadastrar");
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.CADASTRO_DISCENTE_REDE );
		return dadosPessoaisMBean.popular();
	}
	
	@Override
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	@Override
	public String submeterDadosPessoais() {
		DiscenteAssociadoDao dao = getDAO( DiscenteAssociadoDao.class );
		try {
			DiscenteAssociado discenteExistente = dao.findVinculoAtivoByCPF(obj.getPessoa().getCpf_cnpj());
			if( discenteExistente != null && discenteExistente.getId() != obj.getId() ){
				addMensagemErro("Atenção! O discente possui um vínculo ativo associado ao mesmo CPF (campus: " + 
						discenteExistente.getDadosCurso().getCampus().getNome() + ").");
				return null;
			}
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		} finally {
			dao.close();
		}
		dadosCursoRede = obj.getDadosCurso();
		obj.setStatus(new StatusDiscenteAssociado());
		// caso novo cadastro, os dados do curso de rede estão nulos.
		if (dadosCursoRede == null) dadosCursoRede = new DadosCursoRede();
		if (isPortalCoordenadorEnsinoRede()) {
			TipoVinculoCoordenadorUnidadeRede tipoVinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			dadosCursoRede = tipoVinculo.getCoordenacao().getDadosCurso();
			obj.setNivel(dadosCursoRede.getCurso().getNivel());
			allProgramaRedeCombo = null;
			cursosFromProgramaRedeCombo = null;
			campusFromProgramaRedeCombo = null;
		}
		setConfirmButton("Cadastrar");
		return forward("/ensino_rede/discente/form.jsp");
	}
	
	public void programaListener() {
		cursosFromProgramaRedeCombo = null;
		campusFromProgramaRedeCombo = null;
	}
	
	public void cursoListener() throws DAOException {
		// seta o nível de ensino do discente baseado no curso
		CursoAssociado curso = getGenericDAO().findByPrimaryKey(dadosCursoRede.getCurso().getId(), CursoAssociado.class);
		if (curso != null)
			dadosCursoRede.setCurso(curso);
		obj.setNivel(dadosCursoRede.getCurso().getNivel());
		campusFromProgramaRedeCombo = null;
	}
	
	public void campusListener() throws DAOException {
		GenericDAO dao = getGenericDAO();
		String fields[] = {"programaRede.id", "curso.id", "campus.id"};
		Object values[] = {dadosCursoRede.getProgramaRede().getId(), dadosCursoRede.getCurso().getId(), dadosCursoRede.getCampus().getId()};
		Collection<DadosCursoRede> listaDados = dao.findByExactField(DadosCursoRede.class, fields, values);
		DadosCursoRede dados = null;
		if (!isEmpty(listaDados)) dados = listaDados.iterator().next();
		obj.setDadosCurso(dados);
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		campusListener();
		validacaoDados(erros);
		if (hasErrors()) return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		if (acao == Acao.CADASTRAR_DISCENTE)
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_DISCENTE_REDE);
		else
			mov.setCodMovimento(SigaaListaComando.ALTERAR_DISCENTE_REDE);
		mov.setObjMovimentado(obj);
		mov.setRegistroEntrada(getRegistroEntrada());
		execute(mov);
		addMensagem(OPERACAO_SUCESSO);
		return cancelar();
	}
	
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		validateRequired(obj.getStatus(), "Status", lista);	
		validateRequired(obj.getAnoIngresso(), "Ano de Ingresso", lista);
		validateRequired(obj.getPeriodoIngresso(), "Período de Ingresso", lista);
		validateRequired(obj.getDadosCurso(), "Dados do Curso", lista);
		if (!isEmpty(obj.getDadosCurso()))
			validateRequired(obj.getNivel(), "Nível de Ensino", lista);
		validateMaxLength(obj.getObservacao(), 1000, "Observações", lista);
		return lista.isErrorPresent(); 
	}

	@Override
	public void setDiscente(DiscenteAssociado discente) {
		obj = discente;
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		if (acao == Acao.ATUALIZAR_DADOS_PESSOAIS)
			return atualizarDadosPessoais();
		if (acao == Acao.ALTERAR_DISCENTE)
			return alterarDiscente();
		return null;
	}

	private String alterarDiscente() {
		dadosCursoRede = obj.getDadosCurso();
		setConfirmButton("Alterar");
		return forward("/ensino_rede/discente/form.jsp");
	}

	private String atualizarDadosPessoais() throws ArqException {
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.setCodigoOperacao(OperacaoDadosPessoais.ALTERAR_DADOS_DISCENTE_REDE);
		dadosPessoaisMBean.setObj(obj.getPessoa());
		dadosPessoaisMBean.carregarMunicipios();
		dadosPessoaisMBean.setSubmitButton("Atualizar dados");
		dadosPessoaisMBean.setOrdemBotoes(true);
		// ao coordenador do curso só será possível alterar o CPF caso esteja em branco
		if (isUserInRole(SigaaPapeis.COORDENADOR_UNIDADE_REDE)) {
			dadosPessoaisMBean.setPassivelAlterarCpf(isEmpty(obj.getPessoa().getCpf_cnpj()));
			boolean permiteAlterarIdentidade = isEmpty(obj.getPessoa().getIdentidade())
					|| isEmpty(obj.getPessoa().getIdentidade().getNumero())
					|| isEmpty(obj.getPessoa().getIdentidade().getOrgaoExpedicao())
					|| isEmpty(obj.getPessoa().getIdentidade().getUnidadeFederativa())
					|| isEmpty(obj.getPessoa().getIdentidade().getDataExpedicao());
			boolean permiteAlterarNome = isEmpty(obj.getPessoa().getNome());
			dadosPessoaisMBean.setPermiteAlterarIdentidade(permiteAlterarIdentidade);
			dadosPessoaisMBean.setPermiteAlterarNome(permiteAlterarNome);
		}
		return dadosPessoaisMBean.popular();
	}

	public DadosCursoRede getDadosCursoRede() {
		return dadosCursoRede;
	}

	public void setDadosCursoRede(DadosCursoRede dadosCursoRede) {
		this.dadosCursoRede = dadosCursoRede;
	}
	
	public Collection<SelectItem> getAllProgramaRedeCombo() throws DAOException {
		if (allProgramaRedeCombo == null) {
			GenericDAO dao = getGenericDAO();
			Collection<ProgramaRede> lista = dao.findAll(ProgramaRede.class);
			allProgramaRedeCombo  = toSelectItems(lista, "id", "descricao");
		}
		return allProgramaRedeCombo;
	}
	
	public Collection<SelectItem> getCursosFromProgramaRedeCombo() throws DAOException {
		if (cursosFromProgramaRedeCombo == null) {
			cursosFromProgramaRedeCombo = new LinkedList<SelectItem>();
			if (dadosCursoRede != null) {
				GenericDAO dao = getGenericDAO();
				String fields[] = {"programa.id"};
				Object values[] = {dadosCursoRede.getProgramaRede().getId()};
				Collection<CursoAssociado> lista = dao.findByExactField(CursoAssociado.class, fields, values);
				cursosFromProgramaRedeCombo  = toSelectItems(lista, "id", "nome");
			}
		}
		return cursosFromProgramaRedeCombo;
	}
	
	public Collection<SelectItem> getCampusFromCursoProgramaRedeCombo() throws DAOException {
		if (campusFromProgramaRedeCombo == null) {
			campusFromProgramaRedeCombo = new LinkedList<SelectItem>();
			if (dadosCursoRede != null) {
				GenericDAO dao = getGenericDAO();
				String fields[] = {"programaRede.id", "curso.id"};
				Object values[] = {dadosCursoRede.getProgramaRede().getId(), dadosCursoRede.getCurso().getId()};
				List<DadosCursoRede> lista = (List<DadosCursoRede>) dao.findByExactField(DadosCursoRede.class, fields, values);
				Collections.sort(lista, new Comparator<DadosCursoRede>() {
					@Override
					public int compare(DadosCursoRede o1, DadosCursoRede o2) {
						int cmp = o1.getCampus().getInstituicao().getNome().compareTo(o2.getCampus().getInstituicao().getNome());
						if (cmp == 0) cmp = o1.getCampus().getNome().compareTo(o2.getCampus().getNome());
						return cmp;
					}
				});
				for (DadosCursoRede dados : lista) {
					campusFromProgramaRedeCombo.add(new SelectItem(dados.getCampus().getId(), dados.getCampus().getInstituicao().getNome()+" - " + dados.getCampus().getNome()));
				}
			}
		}
		return campusFromProgramaRedeCombo;
	}
	
	public Collection<SelectItem> getStatusCombo() {
		if (statusCombo == null) {
			statusCombo = new LinkedList<SelectItem>();
			for (StatusDiscenteAssociado status : StatusDiscenteAssociado.getStatusAtivos())
				statusCombo.add(new SelectItem(status.getId(), status.toString()));
		}
		return statusCombo;
	}
	
}
