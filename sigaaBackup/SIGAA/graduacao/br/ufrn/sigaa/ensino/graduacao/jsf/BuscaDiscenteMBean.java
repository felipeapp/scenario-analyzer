/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/01/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed bean para a realização de buscas por discentes de graduação
 *
 * @author David Pereira
 * @author Ricardo Wendell
 * @author André Dantas
 *
 */
@Component("buscaDiscenteGraduacao") @Scope("session")
public class BuscaDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> {

	/** Código da operação a ser realizada (Deve ser passado como parâmetro ou definido no Bean) */
	private int codigoOperacao;

	/** Operação ativa */
	private OperacaoDiscente operacao;

	/** Resultado da busca pelos discentes */
	private Collection<? extends DiscenteAdapter> discentes;

	/** Indica se a busca por Matrícula especial está ou não marcada. */
	private boolean buscaMatricula;

	/** Indica se a busca por Nome especial está ou não marcada. */
	private boolean buscaNome;
	
	/** Indica se a busca por Curso está ou não marcada. */
	private boolean buscaCurso;
	
	/** Indica se a busca por CPF está ou não marcada. */
	private boolean buscaCpf;
	
	/** Indica se a busca por Tipo de Necessidade Especial está ou não marcada. */
	private boolean buscaTipoNecessidadeEspecial;

	/** flag que indica se é pra trazer discentes de EAD*/
	private boolean ead;
	
	/** flag que indica que discentes de qual convênio devem ser incluídos */
	private int convenio;
	
	/** Nível de Ensino que o usuário deseja buscar discentes. É utilizado para busca por discentes no módulo de Registro de Diplomas, onde o usuário pode operar discentes de níveis de ensino diferentes. */
	private char nivelEnsinoEspecifico;

	/**
	 * Limpa os atributos do controlador, inicializando-os com seus valores-padrão.
	 */
	private void clear() {
		
		obj = new Discente();
		obj.setCurso(new Curso());
		obj.setPessoa(new Pessoa());
		obj.getPessoa().setTipoNecessidadeEspecial(new TipoNecessidadeEspecial());
		
		if (discentes != null) {
			discentes.clear();
		}
		
		buscaMatricula = false;
		buscaNome = false;
		buscaCurso = false;
		buscaCpf = false;
		buscaTipoNecessidadeEspecial = false;
	}

	/**
	 * Inicia busca por discentes e popula os objetos necessários para consulta.<br/>
	 * 
	 * Método não invocado por JSP's.
	 * @return
	 */
	public String popular() {
		clear();
		int op = getParameterInt("operacao",0);
		
		if (op != 0)
			codigoOperacao = op;
		
		operacao = OperacaoDiscente.getOperacao(codigoOperacao);
		setNivelEnsinoEspecifico(super.getNivelEnsino());
		
		return forward("/graduacao/busca_discente.jsp");
	}

	/**
	 * Busca os discentes de acordo com os critérios de busca informados.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws ArqException{

		Long matricula = null;
		String nome = null;
		String nomeCurso = null;
		Long cpf = null;
		Integer tipoNecessidadeEspecial = null;
		
		if (!buscaMatricula && !buscaNome && !buscaCurso && !buscaCpf && !buscaTipoNecessidadeEspecial) {
			if ( isModuloNee() || isModuloInternacionalizacao() ) 
				addMensagemErro("É necessário selecionar pelo menos um dos outros critérios de busca");
			else
				addMensagemErro("É necessário selecionar um dos critérios de busca");
			return null;
		}
		operacao = OperacaoDiscente.getOperacao(codigoOperacao);

		// Verificar os critérios de busca utilizados
		if (buscaMatricula) {
			ValidatorUtil.validateRequired(obj.getMatricula(), "Matrícula", erros);
			matricula = obj.getMatricula();
		}
		if (buscaNome) {
			ValidatorUtil.validateRequired(obj.getPessoa().getNome(), "Nome do Discente", erros);
			nome = obj.getPessoa().getNome().trim().toUpperCase();
		}
		if (buscaCurso) {
			ValidatorUtil.validateRequired(obj.getCurso().getNome(), "Curso", erros);
			nomeCurso = obj.getCurso().getNome().trim().toUpperCase();
		}
		if (buscaCpf) {
			cpf = obj.getPessoa().getCpf_cnpj();
			ValidatorUtil.validateRequired(cpf, "CPF", erros);
			if (cpf != null)
				ValidatorUtil.validateCPF_CNPJ(obj.getPessoa().getCpf_cnpj(), "CPF", erros);
		}
		if (buscaTipoNecessidadeEspecial) {
			tipoNecessidadeEspecial = new Integer(obj.getPessoa().getTipoNecessidadeEspecial().getId());
		}

		if (hasErrors())
			return null;

		// Realizar a consulta
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		CursoDao cursoDao = getDAO(CursoDao.class);
		try {
			int unidade = 0;
			Collection<Curso> cursos = new ArrayList<Curso>();
			
			//Somente realizar restrições na consulta caso o usuário não tenha permissão do DAE.
			if(!getAcessoMenu().isDae()) {
				//  Secretários de centro consultam todos os cursos do centro
				if(  SigaaSubsistemas.GRADUACAO.equals(getSubSistema()) && getAcessoMenu().isSecretarioCentro() ){
					cursos.addAll( cursoDao.findByCentro( getAcessoMenu().getSecretariaCentro().getId() ) );
				}
				
				// Coordenadores e secretários de cursos de graduação, coordenadores de cursos técnicos 
				// e gestores do ensino infantil consultam apenas o curso atual selecionado
				if ( isCoordenacaoCurso() || isInfantil() || isCoordenacaoTecnico()) {
					cursos = new ArrayList<Curso>(1);
					cursos.add(getCursoAtualCoordenacao());
				}
				
				// Coordenadores e secretários de cursos lato sensu consultam todos os cursos que coordenam
				if( isCoordenacaoCursoLato() ){
					cursos = getAllCursosCoordenacaoNivel();
				}
				
				if ( getAcessoMenu().isTecnico() && SigaaSubsistemas.TECNICO.equals(getSubSistema())) {
					unidade = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();
				}
				
				// Coordenador e secretários de programas consultam os discentes do programa atual selecionado
				if(  isCoordenacaoPrograma() ){
					unidade = getProgramaStricto().getId();
				}
				
				if (getAcessoMenu().isCoordenacaoProbasica() || convenio == ConvenioAcademico.PROBASICA) {
					cursos.addAll( cursoDao.findByConvenioAcademico(ConvenioAcademico.PROBASICA, NivelEnsino.GRADUACAO) );
				}
				
				if ( convenio == ConvenioAcademico.PRONERA) {
					cursos.addAll( cursoDao.findByConvenioAcademico(ConvenioAcademico.PRONERA, NivelEnsino.GRADUACAO) );
				}
				
				if (getNivelEnsino() == NivelEnsino.RESIDENCIA) {
					if(isUserInRole(SigaaPapeis.SECRETARIA_RESIDENCIA) && isPortalComplexoHospitalar())
						cursos.addAll( cursoDao.findAllCursosResidenciaByUnidadeResponsavel(
								getUsuarioLogado().getPermissao(SigaaPapeis.SECRETARIA_RESIDENCIA).iterator().next().getUnidadePapel().getId()));
					else
						cursos.addAll( cursoDao.findAllCursosResidenciaMedica() );
				}
				
			}

			// restringe a consulta aos discentes de EAD?
			if (ead) {
				cursos.addAll( cursoDao.findAllCursosADistancia() );
			}
			
			if (operacao == null) {
				operacao = new OperacaoDiscente();
			}

			// Definir os status de discentes que podem ser consultados
			int[] status = operacao.getStatusValidos();
			if (status == null) {
				// Caso a operação não defina as restrições de status,
				// utilizar somente aqueles válidos
				status = StatusDiscente.getValidos();
			}

			discentes = discenteDao.findOtimizado(cpf, matricula, nome, nomeCurso, tipoNecessidadeEspecial, cursos, status, operacao.getTiposValidos(),
													unidade, getNivelEnsino(), !getAcessoMenu().isAdministradorDAE() );

			if (discentes == null || discentes.isEmpty()) {
				addMessage("Não foram encontrados discentes com status " + operacao.getDescricaoStatusValidos() +
						" de acordo com os critérios de busca informados.", TipoMensagemUFRN.ERROR);
				return null;
			}

		} catch (LimiteResultadosException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		return redirect("/graduacao/busca_discente.jsf");
	}
	
	/**
	 * Retorna a(s) descrição(ões) do(s) curso(s) coordenado(s) pelo usuário logado
	 * (coordenador ou secretário do curso).
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDescricaoCursos(){
		StringBuilder sb = new StringBuilder();
		for(Curso c: getAllCursosCoordenacaoNivel()){
			sb.append(c.getDescricao());
			sb.append("<br/>");
		}
		String descricao = sb.toString();
		return descricao.substring(0, descricao.lastIndexOf("<br/>"));
	}

	/**
	 * Verifica se o usuário é coordenador ou secretário de algum programa
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCoordenacaoPrograma() {
		return SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema())
			&& (getAcessoMenu().isCoordenadorCursoStricto() || getAcessoMenu().isSecretariaPosGraduacao());
	}

	/**
	 * Verifica se o usuário é coordenador ou secretário de algum curso de graduação e
	 * se o contexto acessado permite a consulta.
	 *
	 * A verificação do contexto permite que usuário com mais de uma permissão
	 * possa acessar somente os discentes do subsistema atual.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCoordenacaoCurso() {
		return SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema())
		&& (getAcessoMenu().isCoordenadorCursoGrad() || getAcessoMenu().isSecretarioGraduacao());
	}
	
	/**
	 * Verifica se o usuário é coordenador ou secretário de algum curso lato sensu e
	 * se o contexto acessado permite a consulta.
	 *
	 * A verificação do contexto permite que usuário com mais de uma permissão
	 * possa acessar somente os discentes do subsistema atual.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCoordenacaoCursoLato() {
		return  SigaaSubsistemas.PORTAL_COORDENADOR_LATO.equals(getSubSistema())
			&& (getAcessoMenu().isCoordenadorCursoLato() || getAcessoMenu().isSecretarioLato());
	}
	
	/**
	 * Verifica se o usuário é gestor do ensino infantil e
     * se o contexto acessado permite a consulta.
     * 
     * A verificação do contexto permite que usuário com mais de uma permissão
     * possa acessar somente os discentes do subsistema atual
     * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isInfantil() {
	    return SigaaSubsistemas.INFANTIL.equals(getSubSistema()) && getAcessoMenu().isInfantil();
	}
	
	/**
	 * Verifica se o usuário é gestor do ensino técnico e
	 * se o contexto acessado permite a consulta.
	 * 
	 * A verificação do contexto permite que usuário com mais de uma permissão
	 * possa acessar somente os discentes do subsistema atual
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isTecnico() {
		return SigaaSubsistemas.TECNICO.equals(getSubSistema()) && getAcessoMenu().isTecnico();
	}
	
	/**
	 * Verifica se o usuário é gestor de formação complementar e
	 * se o contexto acessado permite a consulta.
	 * 
	 * A verificação do contexto permite que usuário com mais de uma permissão
	 * possa acessar somente os discentes do subsistema atual
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isFormacaoComplementar() {
		return SigaaSubsistemas.FORMACAO_COMPLEMENTAR.equals(getSubSistema()) && getAcessoMenu().isFormacaoComplementar();
	}
	
	/**
	 * Verifica se o usuário é coordenador de um curso técnico e
	 * se o contexto acessado permite a consulta.
	 * 
	 * A verificação do contexto permite que usuário com mais de uma permissão
	 * possa acessar somente os discentes do subsistema atual
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCoordenacaoTecnico() {
		return SigaaSubsistemas.TECNICO.equals(getSubSistema()) && getAcessoMenu().isCoordenadorCursoTecnico();
	}

	/**
	 * Verifica se o usuário é coordenador de um curso técnico e
	 * se o contexto acessado permite a consulta.
	 * 
	 * A verificação do contexto permite que usuário com mais de uma permissão
	 * possa acessar somente os discentes do subsistema atual
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isLato() {
		return SigaaSubsistemas.LATO_SENSU.equals(getSubSistema()) && getAcessoMenu().isLato();
	}	
	
	/**
	 * Verifica se o usuário tem acesso a Residência em Saúde e
     * se o contexto acessado permite a consulta.
     * 
     * A verificação do contexto permite que usuário com mais de uma permissão
     * possa acessar somente os discentes do subsistema atual
     * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isResidencia(){
		return SigaaSubsistemas.COMPLEXO_HOSPITALAR.equals(getSubSistema()) && getAcessoMenu().isComplexoHospitalar();
	}
	
	/**
	 * Seleciona o discente a partir do resultado da busca.
	 * <br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
 	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void escolheDiscente(ActionEvent evt) throws ArqException {

		Integer id = (Integer) evt.getComponent().getAttributes().get("idDiscente");
		escolheDiscente(id);
		
	}
	
	/**
	 * Realiza a busca dos dados do discente selecionado
	 * e redireciona para o MBean de acordo com o nível de ensino do discente.
	 * <br/>
 	 * Este método não é chamado em JSPs.
	 * 
	 * @throws ArqException
	 */
	public void escolheDiscente(Integer id) throws ArqException {

		DiscenteAdapter discente = null;
		
		try {
			
			discente = getDAO(DiscenteDao.class).findByPK(id);
			if (discente != null && discente.isDiscenteEad()) {
				DiscenteGraduacao dg = (DiscenteGraduacao) discente;
				if (dg.getPolo() != null) {
					dg.getPolo().getDescricao();
				}
			}
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações do discente");
			e.printStackTrace();
		}

		if (discente == null) {
			addMensagemErro("Discente não encontrado!");
		}
		
		redirecionarDiscente(discente);
	}
	
	/**
	 * Redireciona para a operação especificada
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	private String redirecionarDiscente(DiscenteAdapter discente) throws ArqException {
		// Redirecionar para a operação especificada
		operacao = OperacaoDiscente.getOperacao(codigoOperacao);
		OperadorDiscente mBean = (OperadorDiscente) getMBean(operacao.getMBean());
		mBean.setDiscente(discente);
		return mBean.selecionaDiscente();
	}

	/**
	 * Cancela a operação e redireciona o usuário para a tela principal
	 * do módulo/portal no qual se encontra atualmente.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/graduacao/busca_discente.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		if (operacao != null) {
			AbstractController bean = (AbstractController) getMBean(operacao.getMBean());
			bean.resetBean();
		}
		return super.cancelar();
	}

	/**
	 * Retorna o nível de ensino selecionado na busca.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 */
	@Override
	public char getNivelEnsino() {
		// se o acesso for no módulo de registro de diplomas, usar o nível de ensino escolhido pelo usuário
		if (isModuloDiploma() || isModuloNee() || isModuloInternacionalizacao() ) {
			if (getNiveisHabilitados().length == 1)
				return getNiveisHabilitados()[0];
			else 
				return this.nivelEnsinoEspecifico;
		} else if (getAcessoMenu().isChefeDepartamento() && isPortalDocente())
			return NivelEnsino.GRADUACAO;
		else return super.getNivelEnsino();
	}
	
	public Collection<? extends DiscenteAdapter> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<? extends DiscenteAdapter> discentes) {
		this.discentes = discentes;
	}

	public int getCodigoOperacao() {
		return codigoOperacao;
	}

	public OperacaoDiscente getOperacao() {
		return operacao;
	}

	public void setCodigoOperacao(int codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	public void setOperacao(OperacaoDiscente operacao) {
		this.operacao = operacao;
	}

	public boolean isApenasBusca() {
		return operacao == null || (operacao.getNome() == null || operacao.getNome().equals(""));
	}

	public boolean isBuscaCurso() {
		return this.buscaCurso;
	}

	public void setBuscaCurso(boolean buscaCurso) {
		this.buscaCurso = buscaCurso;
	}

	public boolean isBuscaMatricula() {
		return this.buscaMatricula;
	}

	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	public boolean isBuscaNome() {
		return this.buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}


	public boolean isBuscaCpf() {
		return this.buscaCpf;
	}

	public void setBuscaCpf(boolean buscaCpf) {
		this.buscaCpf = buscaCpf;
	}
	
	public boolean isBuscaTipoNecessidadeEspecial() {
		return buscaTipoNecessidadeEspecial;
	}
	
	public void setBuscaTipoNecessidadeEspecial(boolean buscaTipoNecessidadeEspecial) {
		this.buscaTipoNecessidadeEspecial = buscaTipoNecessidadeEspecial;
	}

	public boolean isEad() {
		return ead;
	}

	public void setEad(boolean ead) {
		this.ead = ead;
	}

	public int getConvenio() {
		return convenio;
	}

	public void setConvenio(int convenio) {
		this.convenio = convenio;
	}

	public char getNivelEnsinoEspecifico() {
		return nivelEnsinoEspecifico;
	}

	public void setNivelEnsinoEspecifico(char nivelEnsinoEspecifico) {
		this.nivelEnsinoEspecifico = nivelEnsinoEspecifico;
	}

	/**
	 * Verifica se o usuário está acessando o módulo de registro de diplomas.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isModuloDiploma() {
		return SigaaSubsistemas.REGISTRO_DIPLOMAS.equals(getSubSistema());
	}
	
	/**
	 * Verifica se o usuário está acessando o módulo de NEE.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isModuloNee() {
		return SigaaSubsistemas.NEE.equals(getSubSistema());
	}
	
	/**
	 * Verifica se o usuário está acessando o módulo de Médio.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isMedio() {
		return SigaaSubsistemas.MEDIO.equals(getSubSistema());
	}
	
	/**
	 * Verifica se o usuário está acessando o módulo de Internacionalização de documentos.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isModuloInternacionalizacao() {
		return SigaaSubsistemas.RELACOES_INTERNACIONAIS.equals(getSubSistema());
	}
	
	/**
	 * Verifica se existe a necessidade de seleção do nível de ensino no formulário de busca de discente.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isSelecionaNivelEnsino(){
		return isModuloNee() || isModuloInternacionalizacao();
	}

}


