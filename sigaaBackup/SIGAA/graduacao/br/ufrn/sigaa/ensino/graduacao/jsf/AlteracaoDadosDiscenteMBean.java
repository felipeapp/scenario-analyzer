/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.SituacaoSocioEconomicaDiscente;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * Managed Bean responsável pelo controle da alteração dos dados de um discente
 *
 * @author Ricardo Wendell
 *
 */
@Component("alteracaoDadosDiscente") @Scope("session")
public class AlteracaoDadosDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente, OperadorDadosPessoais {

	/** Indica se o discente é de curso a distância. */
	private boolean ead;

	/** Indica se o uso do bean é feito por discente. */
	private boolean acessoDiscente = false;
	
	/** Indica se o uso do bean é feito a partir da matrícula on-line. */
	private boolean acessoMatricula = false;

	/** Situação Sócio-Econômica do discente. */
	private SituacaoSocioEconomicaDiscente situacaoSocioEconomica;
	
	/** Lista de municípios para o cadastro do endereço de contato. */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	
	/** Discentes do curso do coordenador. */
	private Collection<Discente> discentesCurso;

	/** Retorna a lista de municípios para o cadastro do endereço de contato. 
	 * 
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/discente/dados_discente.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 */
	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	/** Seta a lista de municípios para o cadastro do endereço de contato. 
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/discente/dados_discente.jsp</li>
	 * </ul> 
	 * @param municipiosEndereco
	 */
	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	/** Construtor padrão. */
	public AlteracaoDadosDiscenteMBean() {
		obj = new Discente();
		obj.getPessoa().getEnderecoContato().setTipoLogradouro(
				new TipoLogradouro());
		setConfirmButton("Confirmar");
	}

	/**
	 * Inicia fluxo da alteração dos dados do discente
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciarIngressantes() throws SegurancaException, DAOException {
		checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO });
		if (isEmpty(getDiscentesCurso())) {
			addMensagemErro("Não há discentes ingressantes cadastrados para o curso.");
			return null;
		}
		return forward("/geral/pessoa/busca_coordenador.jsp");
	}
	
	/**
	 * Inicia fluxo da alteração dos dados do discente
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/complexo_hospitalar</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/ead/menu.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/aluno.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/cdp.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/diplomas.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/portais/discente/discente.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/diplomas.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/discente.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/stricto/coordenador.jsp</li>
	 *  <li> /SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *  <li> /WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciar() throws SegurancaException, DAOException {
		if (getSubSistema().equals(SigaaSubsistemas.REGISTRO_DIPLOMAS))
			checkRole(new int[] { SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO,
					SigaaPapeis.GESTOR_DIPLOMAS_STRICTO,
					SigaaPapeis.GESTOR_DIPLOMAS_LATO});
		if (getSubSistema().equals(SigaaSubsistemas.GRADUACAO)) {
			checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.CDP,
					SigaaPapeis.COORDENADOR_CURSO,
					SigaaPapeis.SECRETARIA_COORDENACAO,
					SigaaPapeis.SECRETARIA_CENTRO });
		} else if (getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU))
			checkRole(new int[] { SigaaPapeis.PPG,
					SigaaPapeis.COORDENADOR_CURSO_STRICTO,
					SigaaPapeis.SECRETARIA_POS });
		else if (getSubSistema().equals(SigaaSubsistemas.TECNICO))
			checkRole(new int[] { SigaaPapeis.GESTOR_TECNICO,
					SigaaPapeis.COORDENADOR_TECNICO,
					SigaaPapeis.SECRETARIA_TECNICO});
		else if (getSubSistema().equals(SigaaSubsistemas.COMPLEXO_HOSPITALAR))
			checkRole(new int[] { SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR,
					SigaaPapeis.SECRETARIA_RESIDENCIA,
					SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA });
		else if (getSubSistema().equals(SigaaSubsistemas.NEE))
			checkRole(new int[] { SigaaPapeis.GESTOR_NEE });
		else if (getSubSistema().equals(SigaaSubsistemas.SEDIS)) {
			checkRole(SigaaPapeis.COORDENADOR_GERAL_EAD);
			ead = true;
		} else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
			checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO });
		} else if (getSubSistema().equals(SigaaSubsistemas.MEDIO)) {
			checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		}
		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		acessoDiscente = false;
		return buscarDiscente();
	}

	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 *
	 * @return
	 */
	private String buscarDiscente() throws SegurancaException {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean
				.setCodigoOperacao(OperacaoDiscente.ALTERACAO_DADOS_DISCENTE);
		buscaDiscenteMBean.setEad(ead);
		return buscaDiscenteMBean.popular();
	}
	
	/** Seleciona um discente do curso do coordenador para poder alterar os dados pessoaiso.
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/geral/pessoa/busca_coordenador.jsp</li>
	 *  </ul>
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscenteCoordenador() {
		try {
			populateObj(true);
			// Popular situação sócio-econômica do discente
			GenericDAO dao = getGenericDAO();
			situacaoSocioEconomica = dao.findByExactField(SituacaoSocioEconomicaDiscente.class, "discente.id", obj.getId(), true);
			if (situacaoSocioEconomica == null) {
				situacaoSocioEconomica = new SituacaoSocioEconomicaDiscente(obj.getDiscente());
			}
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Não foi possível carregar o discente escolhido");
			return null;
		}
		return selecionaDiscente();
	}
	

	/** Processa o discente selecionado.
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/busca_discente.jsp</li>
	 *  </ul>
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() {
		getCurrentSession().setAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION, SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE.getId()+"_"+(obj.getPessoa().getId()));
		try {
			prepareMovimento(ArqListaComando.ALTERAR);
			prepareMovimento(SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE);

			// Se o usuário tiver permissão para alterar todos os dados,
			// re-encaminhar para o formulário completo
			if (hasPermissaoAlteracaoCompleta()) {
				DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
				dadosPessoaisMBean
						.setCodigoOperacao(OperacaoDadosPessoais.ALTERACAO_DADOS_DISCENTE);
				dadosPessoaisMBean.setObj(obj.getPessoa());
				dadosPessoaisMBean.carregarMunicipios();
				dadosPessoaisMBean.setSubmitButton("Atualizar dados");
				// ao coordenador do curso só será possível alterar o CPF caso esteja em branco
				if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO)) {
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
			} else {
				if ( !acessoDiscente ) carregarMunicipiosEndereco();
				obj.getPessoa().prepararDados();
				
				return forward("/graduacao/discente/dados_discente.jsp");
			}

		} catch (Exception e) {
			getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
			return tratamentoErroPadrao(e);
		}
	}

	/**
	 * Verifica se a operação de dados pessoais (cadastro ou atualização) continua ativa
	 * @param operacoes
	 * @return
	 */
	private boolean checkOperacaoDadosPessoaisAtiva(String ...operacoes) {
		String operacaoAtiva = (String) getCurrentSession().getAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		if (operacaoAtiva != null) {
			// No caso de estar alterando dados pessoais de uma pessoa que não tem CPF registrado
			if(operacaoAtiva.startsWith(String.valueOf(SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE.getId())) && operacaoAtiva.endsWith("0"))
				return false;
			for (String operacao : operacoes) {
				if (operacaoAtiva.equals(operacao)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Verificar se o usuário tem permissão para alterar todos os dados pessoais
	 * do discente
	 *
	 * @return
	 */
	private boolean hasPermissaoAlteracaoCompleta() {
		SubSistema subSistema = getSubSistema();
		if(subSistema == null)
			return false;
		return ( ( subSistema.equals(SigaaSubsistemas.GRADUACAO) && isUserInRole(SigaaPapeis.DAE))
				|| ( subSistema.equals(SigaaSubsistemas.GRADUACAO ) && isUserInRole(SigaaPapeis.SECRETARIA_CENTRO) )
				|| ( subSistema.equals(SigaaSubsistemas.TECNICO) && isUserInRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO))
				|| ( subSistema.equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR) && isUserInRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR) )
				|| ( subSistema.equals(SigaaSubsistemas.STRICTO_SENSU) && isUserInRole(SigaaPapeis.PPG ))
				|| ( subSistema.equals(SigaaSubsistemas.COMPLEXO_HOSPITALAR) && isUserInRole(SigaaPapeis.SECRETARIA_RESIDENCIA ))
				|| ( subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO) && (isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO) || isUserInRole(SigaaPapeis.SECRETARIA_POS)))
				|| ( subSistema.equals(SigaaSubsistemas.REGISTRO_DIPLOMAS) && isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO, SigaaPapeis.GESTOR_DIPLOMAS_LATO))
				|| ( subSistema.equals(SigaaSubsistemas.MEDIO) && (isUserInRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO)))
				|| ( subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR) && (isUserInRole(SigaaPapeis.COORDENADOR_CURSO) 
						&& ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.PERMITE_ALTERACAO_COMPLETA_DADOS_PESSOAIS))));
	}

	
	/** Atualiza os dados do discente.
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/dados_discente.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String atualizarDados() {

		try {
			ListaMensagens lista = new ListaMensagens();
			
			DiscenteAdapter discente = getObj();
			if(checkOperacaoDadosPessoaisAtiva(SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE.getId()+"_"+(discente.getPessoa().getId()))){
				addMensagem(MensagensGerais.JA_EXISTE_OUTRA_OPERACAO_DADOS_PESSOAIS_ATIVA);
				redirectJSF(getSubSistema().getLink());
				return null;
			}
			
			if (!confirmaSenha())
				return null;
			
			if (!hasPermissaoAlteracaoCompleta()) {
				GenericDAO dao = getGenericDAO();
				PessoaDao pessoaDao = getDAO(PessoaDao.class);
				
				//#35976 - Os campos obrigatórios são exigidos apenas quando a tela for utilizada pelo portal do discente.
				if (!getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)){
					// Validar dados pessoais. 
					lista = obj.getPessoa().validarDadosContato();
				}
				if (isAcessoDiscente()){
					// Validar dados da situação sócio-econômica
					lista.addAll( situacaoSocioEconomica.validate() );
				}
				if (!lista.isEmpty()) {
					addMensagens(lista);
					return null;
				}
				
				discente = dao.findByPrimaryKey(obj.getId(), Discente.class);
				discente.setPessoa(pessoaDao.findCompleto(discente.getPessoa().getId()));
				discente.getPessoa().setEnderecoContato(obj.getPessoa().getEnderecoContato());
				discente.getPessoa().setCpf_cnpj(obj.getPessoa().getCpf_cnpj());
				discente.getPessoa().setEmail(obj.getPessoa().getEmail());
				discente.getPessoa().setCodigoAreaNacionalTelefoneFixo(obj.getPessoa().getCodigoAreaNacionalTelefoneFixo());
				discente.getPessoa().setTelefone(obj.getPessoa().getTelefone());
				discente.getPessoa().setCodigoAreaNacionalTelefoneCelular(obj.getPessoa().getCodigoAreaNacionalTelefoneCelular());
				discente.getPessoa().setCelular(obj.getPessoa().getCelular());
				discente.getPessoa().setContaBancaria(obj.getPessoa().getContaBancaria());
			} 

			// Validar CPF
			validarCpf(lista, discente);

			if (!lista.isEmpty()) {
				addMensagens(lista);
				return null;
			}

			DiscenteMov mov = new DiscenteMov();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE);
			mov.setObjMovimentado(discente);
			
			if (!hasPermissaoAlteracaoCompleta() && isAcessoDiscente()) {
				mov.setSituacaoSocioEconomica(situacaoSocioEconomica);
			}
			
			Discente discenteAtualizado = (Discente) execute(mov, getCurrentRequest());

			// Atualizar dados em sessão
			getUsuarioLogado().setEmail( discenteAtualizado.getPessoa().getEmail() );

			DiscenteAdapter discenteUsuario = getDiscenteUsuario();
			if (discenteUsuario != null) {
				discenteUsuario.setPessoa( discenteAtualizado.getPessoa() );
			}

			// Redirecionar para a tela de comprovante
			addMessage(
					"Atualização dos dados do discente realizada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
			if (getNivelEnsino() == NivelEnsino.GRADUACAO
					&& isUserInRole(SigaaPapeis.DAE)) {

				HistoricoDiscenteMBean historicoDiscenteMBean = (HistoricoDiscenteMBean) getMBean("historicoDiscente");
				historicoDiscenteMBean.setDiscente(discente);
				getCurrentRequest().setAttribute(
						OperacaoDiscente.getOperacao(
								OperacaoDiscente.HISTORICO_COMPLETO_DISCENTE)
								.getMBean(), historicoDiscenteMBean);
				return historicoDiscenteMBean.selecionaDiscente();
			} else if (getNivelEnsino() == NivelEnsino.TECNICO || getNivelEnsino() == NivelEnsino.STRICTO) {
				getCurrentRequest().setAttribute("discente", obj);
				return forward("/geral/discente/view.jsp");
			} else if (getNivelEnsino() == NivelEnsino.GRADUACAO && !hasPermissaoAlteracaoCompleta() && isAcessoDiscente() && isAcessoMatricula()) {
				MatriculaGraduacaoMBean matriculaBean = getMBean("matriculaGraduacao");
				setAcessoMatricula(false);
				setConfirmButton("Confirmar");
				return matriculaBean.telaInstrucoes();
			} else {
				redirectJSF(getSubSistema().getLink());
				return null;
			}

		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		} catch (ArqException e) {
			if (e.getCodErro() == ConstantesErro.SOLICITACAO_JA_PROCESSADA)
				addMensagemErro("Solicitação já processada");
			return null;
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
	}

	/** Valida o CPF do discente.
	 * @param lista
	 * @param discente
	 */
	private void validarCpf(ListaMensagens lista, DiscenteAdapter discente) {
		if (!obj.getPessoa().isInternacional()) {
			
			boolean cpfValido = false;
			if (obj.getPessoa().getCpf_cnpj() == null) {
				addMensagemErro("Os dados pessoais não podem ser atualizados pois o CPF não foi informado");
			} else {
				cpfValido = ValidatorUtil.validateCPF_CNPJ(obj.getPessoa().getCpf_cnpj(), "CPF", lista);
			}

			if (!cpfValido && acessoDiscente) {
				String responsavel = " a coordenação do seu curso ou o DAE";
				if ( discente.isStricto() ) {
					responsavel = " a coordenação do seu programa de pós-graduação";
				}
				addMensagemWarning("Para que seus dados pessoais possam ser alterados é necessário " +
						"que seu número de CPF seja corrigido em nossa base de dados. <br>" +
						" Para tal é necessário contactar " + responsavel + ", responsáveis por esta operação.");
			}
		}
	}

	/** Seta o discente a operar.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) {
		try {
			GenericDAO dao = getGenericDAO();
			obj = dao.findByPrimaryKey(discente.getId(), Discente.class);
			
			// Popular situação sócio-econômica do discente
			situacaoSocioEconomica = dao.findByExactField(SituacaoSocioEconomicaDiscente.class, "discente.id", obj.getId(), true);
			if (situacaoSocioEconomica == null) {
				situacaoSocioEconomica = new SituacaoSocioEconomicaDiscente(obj.getDiscente());
			}
			
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar o discente escolhido");
		}
	}

	/** Cancela a operação corrente.
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li> /sigaa.war/graduacao/discente/dados_discente.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		return super.cancelar();
	}

	/** Seta os dados pessoais do discente
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/** Trata os dados pessoais submetidos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#submeterDadosPessoais()
	 */
	public String submeterDadosPessoais() {
		return atualizarDados();
	}

	/** Inicia a alteração de dados pessoais por discente.
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/monitoria/DiscenteMonitoria/inscricao_discente.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/discente/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarAcessoDiscente() throws DAOException {
		DiscenteAdapter discente = getDiscenteUsuario();

		if (discente == null) {
			addMensagemErro("Somente usuários utilizando seu vínculo de discente podem acessar esta operação.");
			return null;
		}

		if (discente.isTrancado()) {
			addMensagemErro("Apenas discentes ativos podem visualizar ou alterar seus dados pessoais.");
			return null;
		}
		
		if(isAcessoMatricula())
			addMensagemWarning("Atualize seus dados pessoais antes de efetuar a matrícula.");

		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		discente.setPessoa(pessoaDao.findCompleto(discente.getPessoa().getId()));

		acessoDiscente = true;
		setDiscente(getDiscenteUsuario());
		
		carregarMunicipiosEndereco();
		return selecionaDiscente();
	}

	/** Carrega a lista de municípios para o cadastro de endereço de contato.
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/dados_discente.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void carregarMunicipiosEndereco() throws DAOException {
		int idUf;

		if ( obj.getPessoa().getEnderecoContato() == null ) {
			obj.getPessoa().setEnderecoContato(new Endereco());
		}

		if (obj.getPessoa().getEnderecoContato() != null
				&& obj.getPessoa().getEnderecoContato().getUnidadeFederativa() != null
				&& obj.getPessoa().getEnderecoContato().getUnidadeFederativa()
						.getId() > 0) {
			idUf = obj.getPessoa().getEnderecoContato().getUnidadeFederativa()
			.getId();
		} else {
			idUf = UnidadeFederativa.ID_UF_PADRAO;
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
	}

	/** Indica se o uso do bean é feito por discente. 
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/dados_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAcessoDiscente() {
		return acessoDiscente;
	}

	/** Retorna a situação Sócio-Econômica do discente. 
	 * <br / >
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/dados_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public SituacaoSocioEconomicaDiscente getSituacaoSocioEconomica() {
		return situacaoSocioEconomica;
	}

	public boolean isAcessoMatricula() {
		return acessoMatricula;
	}

	public void setAcessoMatricula(boolean acessoMatricula) {
		this.acessoMatricula = acessoMatricula;
	}

	/** Retorna uma coleção de discentes ingressantes do curso coordenado.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> getDiscentesCurso() throws DAOException {
		if (discentesCurso == null) {
			Collection<Curso> cursos = new ArrayList<Curso>(0);
			cursos.add(getCursoAtualCoordenacao());
			cursos.add(getCursoAtualCoordenacao());
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao() );
			DiscenteDao dao = getDAO(DiscenteDao.class);
			discentesCurso = dao.findByCursoAnoPeriodo(cal.getAno(), cal.getPeriodo(), cursos, true);
		}
		return discentesCurso;
	}

}
