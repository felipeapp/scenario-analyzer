/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '30/09/2008 '
 *
 */
package br.ufrn.sigaa.arq.acesso;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe para guardar informa��es sobre acesso aos subsistemas do SIGAA
 * 
 * @author David Pereira
 * 
 */
public class DadosAcesso {

	/** Constante textual para indicar que o acesso est� setado. */
	private final String classOn = "on";

	/** Constante textual para indicar que o acesso est� negado. */
	private final String classOff = "off";

	/** Usu�rio referente � estes dados de acesso.*/
	private final Usuario usuario;

	/** Total de sistemas que o usu�rio tem acesso. */
	protected int totalSistemas;

	/** N�vel de Ensino do docente. */
	protected String nivelDocente;

	/** Armazena informa��oes sobre os M�dulos que tem acesso */
	protected Set<DadosSubsistemaPrincipal> dadosSubsistemaPrincipal = new HashSet<DadosSubsistemaPrincipal>();

	/** Atributo que identifica se o usu�rio tem a permiss�o para abrir chamados ou n�o */
	protected boolean abrirChamado;
	
	/** Indica se o usu�rio tem n�vel de acesso se administrador dos sistemas. */
	protected boolean administradorSistema;

	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Ensino M�dio. */
	protected boolean medio;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Ensino Infantil. */
	protected boolean infantil;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Ensino T�cnico. */
	protected boolean tecnico;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Escolas Acad�micas Especializadas. */
	protected boolean formacaoComplementar;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Monitoria. */
	protected boolean monitoria;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Pesquisa. */
	protected boolean pesquisa;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Extens�o. */
	protected boolean extensao;

	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de A��es Acad�micas Associadas. */
	protected boolean acoesAssociadas;

	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Lato Sensu. */
	protected boolean lato;
	
	/** Indica se o usu�rio tem n�vel de acesso ao portal do discente. */
	protected boolean discente;
	
	/** Indica se o usu�rio tem n�vel de acesso ao portal do discente, como discente no n�vel m�dio. */
	protected boolean discenteMedio;
	
	/** Indica se o usu�rio tem n�vel de acesso ao portal do docente. */
	protected boolean docente;
	
	/** Indica se o usu�rio tem n�vel de acesso para o prodocente. */
	protected boolean prodocente;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Gradua��o. */
	protected boolean graduacao;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Stricto Sensu. */
	protected boolean stricto;
	
	/** Indica se o usu�rio tem n�vel de acesso ao SIPAC. */
	protected boolean sipac;
	
	/** Indica se o usu�rio tem n�vel de acesso ao SIGRH. */
	protected boolean sigrh;
	
	/** Indica se o usu�rio tem n�vel de acesso ao SIGPP. */
	protected boolean sigpp;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de Ensino � Dist�ncia - EAD. */
	protected boolean ead;
	
	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo do Complexo Hospitalar. */
	protected boolean complexoHospitalar;
	
	/** Indica se o usu�rio tem n�vel de acesso de Gestor Complexo Hospitalar. */
	protected boolean gestorComplexoHospitalar;
	
	/** Indica se o usu�rio tem n�vel de acesso de Gestor Resid�ncia M�dica. */
	protected boolean gestorResidenciaMedica;

	/** Indica se o usu�rio tem n�vel de acesso �s opera��es de Coordena��o de um Programa de Resid�ncia M�dica. */
	protected boolean coordenadorResidenciaMedica;

	/** Indica se o usu�rio � docente da UFRN. */
	protected boolean docenteUFRN;
	
	/** Indica se o usu�rio tem n�vel de acesso se administrador dos Stricto Sensu. */
	protected boolean administradorStricto;
	
	
	/** Indica se o usu�rio tem n�vel de acesso de Gestor do PAP. */
	protected boolean programaAtualizacaoPedagogica;

	/** Indica se o usu�rio tem n�vel de acesso ao m�dulo de tradu��o de documentos. */
	protected boolean relacoesInternacionais;
	
	// gradua��o
	/** Indica se o usu�rio tem n�vel de acesso �s opera��es de Coordena��o Did�tico-Pedag�gica - CDP. */
	protected boolean cdp;

	/** Indica se o usu�rio tem n�vel de acesso �s opera��es de administrador Departamento de Administra��o Escolar - DAE. */
	protected boolean administradorDAE;

	/** Indica se o usu�rio tem n�vel de acesso �s opera��es do Departamento de Administra��o Escolar - DAE. */
	protected boolean dae;

	/** Indica se o usu�rio tem n�vel de acesso �s opera��es da Secretaria de Assuntos Estudantis - SAE. */
	protected boolean sae;
	
	/** Indica se o usu�rio tem n�vel de acesso �s opera��es da Ouvidoria. */
	protected boolean ouvidoria;

	/** Indica se o usu�rio tem n�vel de acesso �s opera��es da PR�-REITORIA DE P�S-GRADUA��O - PPG. */
	protected boolean ppg;
	
	/** Indica se o usu�rio � um membros da Coordena��o de Apoio T�cnico-Pedag�gico da Doc�ncia Assistida (CATP),
	 * permitindo realizar opera��es de Planos de Doc�ncia Assistida - PPG. */
	protected boolean membroApoioDocenciaAssistida;	

	/** Indica se o usu�rio tem n�vel de acesso �s opera��es do Vestibular. */
	protected boolean vestibular;
	
	/** Indica se o usu�rio tem n�vel de acesso �s opera��es do Nee. */
	protected boolean nee;

	// protected boolean coordenacaoUnica;
	/** Indica se o usu�rio tem n�vel de acesso ao menu administra��o. */
	protected boolean administracao;

	/** Indica se o usu�rio tem n�vel de acesso de Chefe de Departamento. */
	protected boolean chefeDepartamento;
	
	/** Indica se o usu�rio tem n�vel de acesso de Diretor de Centro. */
	protected boolean diretorCentro;
	
	/** Indica se o usu�rio tem n�vel de acesso de Chefe de Unidade. */
	protected boolean chefeUnidade;

	/** Indica se o usu�rio tem n�vel de acesso coordenador de P�lo de Ensino � Dist�ncia - EAD. */
	protected boolean coordenadorPolo;
	
	/** Indica se o usu�rio tem n�vel de acesso tutor de Ensino � Dist�ncia - EAD. */
	protected boolean tutorEad;

	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Curso de Gradua��o. */
	protected boolean coordenadorCursoGrad;
	
	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Est�gio. */
	protected boolean coordenadorEstagio;
	
	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Curso de Ensino � Dist�ncia - EAD. */
	protected boolean cursoEad;

	/** Coordena��o geral de todos os cursos de probasica, quando o usu�rio tem o papel GESTOR_PROBASICA. */
	protected boolean coordenacaoProbasica;

	/** Indica se o usu�rio tem n�vel de acesso de Secret�rio de Curso de Gradua��o. */
	protected boolean secretarioGraduacao;
	
	/** Indica se o usu�rio tem n�vel de acesso de Secret�rio de Departamento. */
	protected boolean secretarioDepartamento;
	
	/** Indica se o usu�rio tem n�vel de acesso de Secret�rio de Centro. */
	protected boolean secretarioCentro;
	
	/** Indica se o usu�rio tem n�vel de acesso de Secret�rio de Curso de Lato Sensu. */
	protected boolean secretarioLato;
	
	/** Indica se o usu�rio tem n�vel de acesso de Secret�rio de T�cnico. */
	protected boolean secretarioTecnico;
	
	/** Indica se o usu�rio tem n�vel de acesso de Secret�rio de M�dio. */
	protected boolean secretarioMedio;

	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Curso de Stricto Sensu. */
	protected boolean coordenadorCursoStricto;

	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Curso de Lato Sensu. */
	protected boolean coordenadorCursoLato;
	
	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Curso T�cnico. */
	protected boolean coordenadorCursoTecnico;

	/** Indica se o usu�rio tem n�vel de acesso de Secret�rio de P�s-Gradua��o. */
	protected boolean secretariaPosGraduacao;

	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Monitoria. */
	protected boolean coordenadorMonitoria;
	
	/** Indica se o usu�rio tem n�vel de acesso de Membro da Comiss�o de Monitoria. */
	protected boolean comissaoMonitoria;
	
	/** Indica se o usu�rio tem n�vel de acesso de Membro de Comiss�o Cient�fica de Monitoria. */
	protected boolean comissaoCientificaMonitoria;
	
	/** Indica se o usu�rio tem n�vel de acesso de Autoria de Projetos de Monitoria. */
	protected boolean autorProjetoMonitoria;
	
	/** Indica se o usu�rio tem n�vel de acesso de Solicitante de Projeto de Ensino. */
	protected boolean solicitanteProjetoEnsino;

	/** Indica se o usu�rio tem n�vel de acesso de Comiss�o de Pesquisa. */
	protected boolean comissaoPesquisa;
	
	/** Indica se o usu�rio tem n�vel de acesso de Comiss�o de Extens�o. */
	protected boolean comissaoExtensao;

	/** Indica se o usu�rio tem n�vel de acesso de Comiss�o Integrada de Ensino, Pesquisa e Extens�o. */
	protected boolean comissaoIntegrada;
	
	/** Indica se o usu�rio tem n�vel de acesso de Paceiro de Extens�o. */
	protected boolean pareceristaExtensao;
	
	/** Indica se o usu�rio tem n�vel de acesso de Avaliador de A��es Acad�micas Associadas. */
	protected boolean avaliadorAcoesAssociadas;	
	
	/** Indica se o usu�rio tem n�vel de acesso de Presidente de Comit�  de Extens�o. */
	protected boolean presidenteComiteExtensao;

	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Projeto de Extens�o. */
	protected boolean coordenadorExtensao;

	/** Indica se o usu�rio tem n�vel de acesso de Coordenador de Projeto de Pesquisa. */
	protected boolean coordPesquisa;

	/** Indica se o usu�rio tem n�vel de acesso de Orientador Acad�mico. */
	protected boolean orientadorAcademico;
	
	/** Indica se o usu�rio tem n�vel de acesso de Orientador de Stricto Sensu. */
	protected boolean orientadorStricto;
	
	/** Indica se o usu�rio tem n�vel de acesso de CoOrientador de Stricto Sensu. */
	protected boolean coOrientadorStricto;

	/** Indica se o usu�rio tem n�vel de acesso de Gestor de Unidade Especializada. */
	protected boolean unidadeEspecializada;

	/** Indica se o usu�rio tem n�vel de acesso de Consulta � alunos. */
	protected boolean consulta;
	
	/** Indica se o usu�rio tem n�vel de acesso ao Portal de Planejamento. */
	protected boolean planejamento;

	/** Unidade do secret�rio de Centro. */
	protected Unidade secretariaCentro;

	/** Cole��o de cursos que o coordenador coordena. */
	protected Collection<Curso> cursos;

	/** Quantidade de orienta��es acad�micas do orientador acad�mico. */
	protected int orientacoesAcademicas;
	
	/** Quantidade de orienta��es do orientador stricto. */
	protected int orientacoesStricto;
	
	/** Quantidade de orienta��es do orientador stricto. */
	protected int coOrientacoesStricto;

	/** Cole��o de programas, quando um usu�rio � secret�rio de mais de um programa. */
	protected Collection<Unidade> programas;

	/** Indica que o usu�rio possui m�ltiplos v�nculos. */
	protected boolean multiplosVinculos;

	/** Indica que o usu�rio tem o v�nculo principal escolhido. */
	protected boolean escolhidoVinculoPrincipal = true;

	/** Indica se o usu�rio tem n�vel de acesso ao Portal da CPDI. */
	protected boolean cpdi;

	/** Indica se o usu�rio tem n�vel de acesso ao NIT. */
	protected boolean nit;

	/** Indica que o docente est� apto para preencher a Avalia��o Institucional. */
	protected boolean docenteAptoAvaliacaoInstitucional;

	/** Indica que o discente est� apto para preencher a Avalia��o Institucional. */
	protected boolean discenteAptoAvaliacaoInstitucional;

	/** Indica que o usu�rio tem permiss�o para usar o m�dulo da biblioteca. Setado no AcessoBiblioteca e lido na p�gina itens_menu_principal.jsp */
	protected boolean moduloBiblioteca; 

	/** Indica que o usu�rio tem permiss�o para usar o m�dulo de registro de diplomas. */
	protected boolean moduloDiploma;
	
	/** Indica que o usu�rio tem permiss�o para usar o m�dulo de Conv�nios de Est�gio. */
	protected boolean moduloConvenioEstagio;
	
	/** Indica que o usu�rio � gestor de espa�o f�sico. */
	protected boolean espacoFisico;
	
	/** Biblioteca ao qual o usu�rio � vinculado.
	 * Ele so vai poder realizar algumas opera��es se pertencer a um biblioteca, e s� pode pertencer a uma biblioteca.
	 */
	protected Biblioteca biblioteca;
	
	/** Indica se o usu�rio tem n�vel de acesso ao Portal da Avalia��o. */
	protected boolean avaliacao;

	/** Indica se o usu�rio tem plano de reposi��o de aula Pendente. */
	protected boolean pendentePlanoResposicao;
	
	/** Indica se o usu�rio tem n�vel de acesso �s opera��es dos Ambientes Virtuais. */
	protected boolean ambientesVirtuais;
	
	/** Indica se o usu�rio tem n�vel de acesso ao Portal de Relat�rios. */
	protected boolean relatorios;
	
	/** Indica se o usu�rio tem n�vel de acesso ao . */
	protected boolean portalConcedenteEstagio;
	
	/** Indica se o usu�rio tem n�vel de acesso ao Portal do Familiar. */
	protected boolean portalFamiliar;		
	
	/** Cole��o de programas de resid�ncias m�dica que o coordenador coordena */
	protected Collection<Unidade> residencias;
	
	/** Verifica se o modo acessibilidade est� ativo */
	protected Boolean acessibilidade;
	
	/** Indica se o usu�rio possui notifica��o acad�mica Pendente. */
	protected boolean pendenteNotificacaoAcademica;
	
	/** Indica se o usu�rio est� pendente quanto ao envio de relat�rios parcial/final(EXTENS�O)*/
	protected boolean pendenteRelatoriosExtensao = false;
	
	/** Indica se o usu�rio est� pendente quanto ao envio de question�rio de Extens�o */
	protected boolean pendenteQuestionarioExtensao = false;

	/** Indica se o usu�rio tem somente permissao de acesso a Consultas no m�dulo de Ensino M�dio ou no m�dulo de Ensino T�cnico. */
	protected boolean pedagogico;
	
	/** Construtor parametrizado.
	 * @param usuario
	 */
	public DadosAcesso(Usuario usuario) {
		this.usuario = usuario;
	}

	/** Construtor parametrizado.
	 * @param usuario
	 * @param multiplosVinculos
	 */
	public DadosAcesso(Usuario usuario, boolean multiplosVinculos) {
		this.usuario = usuario;
		this.multiplosVinculos = multiplosVinculos;
	}

	/** Retorna uma descri��o textual indicando se a permiss�o est� dispon�vel (on) ou n�o (off).
	 * @param acesso
	 * @return
	 */
	public String getClass(boolean acesso) {
		return acesso ? classOn : classOff;
	}

	public String getTecnicoClass() {
		return getClass(tecnico || coordenadorCursoTecnico);
	}
	
	public String getFormacaoComplementarClass() {
		return getClass(formacaoComplementar);
	}

	public String getPlanejamentoClass() {
		return getClass(planejamento);
	}

	public String getEadClass() {
		return getClass(ead);
	}

	public String getSaeClass() {
		return getClass(sae);
	}
	
	public String getOuvidoriaClass() {
		return getClass(ouvidoria);
	}
	
	public String getAmbientesVirtuaisClass() {
		return getClass(ambientesVirtuais);
	}
	
	public String getRelatoriosClass() {
		return getClass(relatorios);
	}
	

	public String getMedioClass() {
		return getClass(medio);
	}

	public String getStrictoClass() {
		return getClass(stricto && (ppg || membroApoioDocenciaAssistida || consulta));
	}

	public String getProdocenteClass() {
		return getClass(prodocente);
	}

	public String getGraduacaoClass() {
		return getClass(graduacao || secretarioDepartamento);
	}

	public String getSipacClass() {
		return getClass(sipac);
	}

	public String getSigrhClass() {
		return getClass(sigrh);
	}

	public String getMonitoriaClass() {
		return getClass(monitoria);
	}

	public String getExtensaoClass() {
		return getClass(extensao);
	}
	
	public String getAcoesAssociadasClass() {
		return getClass(acoesAssociadas);
	}
	
	public String getEspacoFisicoClass() {
		return getClass(espacoFisico);
	}

	public String getInfantilClass() {
		return getClass(infantil);
	}

	public String getPesquisaClass() {
		return getClass(pesquisa || comissaoPesquisa);
	}

	public String getLatoClass() {
		return getClass(lato);
	}

	public String getCoordenadorCursoLatoClass() {
		return getClass(coordenadorCursoLato || secretarioLato);
	}

	public String getCoordenadorCursoGradClass() {
		return getClass(coordenadorCursoGrad || secretarioGraduacao || coordenacaoProbasica || coordenadorEstagio);
	}

	public String getCoordenadorCursoStrictoClass() {
		return getClass(coordenadorCursoStricto || secretariaPosGraduacao);
	}

	public String getDocenteClass() {
		return getClass(docente);
	}

	public String getCpdiClass() {
		return getClass(cpdi);
	}

	public String getTutorClass() {
		return getClass(tutorEad);
	}

	public String getCoordenadorPoloClass() {
		return getClass(coordenadorPolo);
	}

	public String getDiscenteClass() {
		return getClass(discente || discenteMedio);
	}

	public String getAdministracaoClass() {
		return getClass(administracao);
	}
	
	public String getPapClass() {
		return getClass(programaAtualizacaoPedagogica);
	}

	public int getTotalSistemas() {
		return totalSistemas;
	}

	public void setTotalSistemas(int totalSistemas) {
		this.totalSistemas = totalSistemas;
	}

	public String getNivelDocente() {
		return nivelDocente;
	}

	public void setNivelDocente(String nivelDocente) {
		this.nivelDocente = nivelDocente;
	}

	public Set<DadosSubsistemaPrincipal> getDadosSubsistemaPrincipal() {
		return dadosSubsistemaPrincipal;
	}

	public void setDadosSubsistemaPrincipal(
			Set<DadosSubsistemaPrincipal> dadosSubsistemaPrincipal) {
		this.dadosSubsistemaPrincipal = dadosSubsistemaPrincipal;
	}

	public boolean isAdministradorSistema() {
		return administradorSistema;
	}

	public void setAdministradorSistema(boolean administradorSistema) {
		this.administradorSistema = administradorSistema;
	}

	public boolean isMedio() {
		return medio;
	}

	public void setMedio(boolean medio) {
		this.medio = medio;
	}

	public boolean isInfantil() {
		return infantil;
	}

	public void setInfantil(boolean infantil) {
		this.infantil = infantil;
	}

	public boolean isTecnico() {
		return tecnico;
	}

	public void setTecnico(boolean tecnico) {
		this.tecnico = tecnico;
	}

	public boolean isFormacaoComplementar() {
		return formacaoComplementar;
	}

	public void setFormacaoComplementar(boolean formacaoComplementar) {
		this.formacaoComplementar = formacaoComplementar;
	}

	public boolean isMonitoria() {
		return monitoria;
	}

	public void setMonitoria(boolean monitoria) {
		this.monitoria = monitoria;
	}

	public boolean isPesquisa() {
		return pesquisa;
	}

	public void setPesquisa(boolean pesquisa) {
		this.pesquisa = pesquisa;
	}

	public boolean isExtensao() {
		return extensao;
	}

	public void setExtensao(boolean extensao) {
		this.extensao = extensao;
	}

	public boolean isLato() {
		return lato;
	}

	public void setLato(boolean lato) {
		this.lato = lato;
	}

	public boolean isDiscente() {
		return discente;
	}

	public void setDiscente(boolean discente) {
		this.discente = discente;
	}

	public boolean isDiscenteMedio() {
		return discenteMedio;
	}

	public void setDiscenteMedio(boolean discenteMedio) {
		this.discenteMedio = discenteMedio;
	}

	public boolean isDocente() {
		return docente;
	}

	/**
	 * Seta o Docente e modifica o valor do sipac pra true desde que seja docente
	 * 
	 * @param docente
	 */
	public void setDocente(boolean docente) {
		this.docente = docente;
		if (docente)
			sipac = true;
	}

	public boolean isProdocente() {
		return prodocente;
	}

	public void setProdocente(boolean prodocente) {
		this.prodocente = prodocente;
	}

	public boolean isGraduacao() {
		return graduacao;
	}

	public void setGraduacao(boolean graduacao) {
		this.graduacao = graduacao;
	}

	public boolean isStricto() {
		return stricto;
	}

	public void setStricto(boolean stricto) {
		this.stricto = stricto;
	}

	public boolean isSipac() {
		return sipac;
	}

	public void setSipac(boolean sipac) {
		this.sipac = sipac;
	}

	public boolean isSigrh() {
		return sigrh;
	}

	public void setSigrh(boolean sigrh) {
		this.sigrh = sigrh;
	}

	public boolean isSigpp() {
		return sigpp;
	}

	public void setSigpp(boolean sigpp) {
		this.sigpp = sigpp;
	}

	public boolean isEad() {
		return ead;
	}

	public void setEad(boolean ead) {
		this.ead = ead;
	}

	public boolean isDocenteUFRN() {
		return docenteUFRN;
	}

	public void setDocenteUFRN(boolean docenteUFRN) {
		this.docenteUFRN = docenteUFRN;
	}

	public boolean isCdp() {
		return cdp;
	}

	public void setCdp(boolean cdp) {
		this.cdp = cdp;
	}

	public boolean isAdministradorDAE() {
		return administradorDAE;
	}

	public void setAdministradorDAE(boolean administradorDAE) {
		this.administradorDAE = administradorDAE;
	}

	public boolean isDae() {
		return dae;
	}

	public void setDae(boolean dae) {
		this.dae = dae;
	}

	public boolean isSae() {
		return sae;
	}

	public void setSae(boolean sae) {
		this.sae = sae;
	}

	public boolean isOuvidoria() {
	    return ouvidoria;
	}

	public void setOuvidoria(boolean ouvidoria) {
	    this.ouvidoria = ouvidoria;
	}

	public boolean isPpg() {
		return ppg;
	}

	public void setPpg(boolean ppg) {
		this.ppg = ppg;
	}

	public boolean isVestibular() {
		return vestibular;
	}

	public void setVestibular(boolean vestibular) {
		this.vestibular = vestibular;
	}
	
	public boolean isNee() {
		return nee;
	}

	public void setNee(boolean nee) {
		this.nee = nee;
	}

	public boolean isAdministracao() {
		return administracao;
	}

	public void setAdministracao(boolean administracao) {
		this.administracao = administracao;
	}

	public boolean isChefeDepartamento() {
		return chefeDepartamento;
	}

	/**
	 * Seta do chefe de Departamento e ativa o boleano do chefe de unidade
	 * 
	 * @param chefeDepartamento
	 */
	public void setChefeDepartamento(boolean chefeDepartamento) {
		this.chefeDepartamento = chefeDepartamento;
		if ( chefeDepartamento ) {
			setChefeUnidade(true);
		}
	}
	
	public boolean isChefeUnidade() {
		return chefeUnidade;
	}

	public void setChefeUnidade(boolean chefeUnidade) {
		this.chefeUnidade = chefeUnidade;
	}

	public boolean isDiretorCentro() {
		return diretorCentro;
	}

	public void setDiretorCentro(boolean diretorCentro) {
		this.diretorCentro = diretorCentro;
	}

	public boolean isCoordenadorPolo() {
		return coordenadorPolo;
	}

	public void setCoordenadorPolo(boolean coordenadorPolo) {
		this.coordenadorPolo = coordenadorPolo;
	}

	public boolean isTutorEad() {
		return tutorEad;
	}

	public void setTutorEad(boolean tutorEad) {
		this.tutorEad = tutorEad;
	}

	public boolean isCoordenadorCursoGrad() {
		return coordenadorCursoGrad;
	}

	public void setCoordenadorCursoGrad(boolean coordenadorCursoGrad) {
		this.coordenadorCursoGrad = coordenadorCursoGrad;
	}

	public boolean isCursoEad() {
		return cursoEad;
	}

	public void setCursoEad(boolean cursoEad) {
		this.cursoEad = cursoEad;
	}

	public boolean isCoordenacaoProbasica() {
		return coordenacaoProbasica;
	}

	public void setCoordenacaoProbasica(boolean coordenacaoProbasica) {
		this.coordenacaoProbasica = coordenacaoProbasica;
	}

	public boolean isSecretarioGraduacao() {
		return secretarioGraduacao;
	}

	public void setSecretarioGraduacao(boolean secretarioGraduacao) {
		this.secretarioGraduacao = secretarioGraduacao;
	}

	public boolean isSecretarioDepartamento() {
		return secretarioDepartamento;
	}

	public void setSecretarioDepartamento(boolean secretarioDepartamento) {
		this.secretarioDepartamento = secretarioDepartamento;
	}

	public boolean isSecretarioCentro() {
		return secretarioCentro;
	}

	public void setSecretarioCentro(boolean secretarioCentro) {
		this.secretarioCentro = secretarioCentro;
	}

	public boolean isSecretarioLato() {
		return secretarioLato;
	}

	public void setSecretarioLato(boolean secretarioLato) {
		this.secretarioLato = secretarioLato;
	}

	public boolean isSecretarioTecnico() {
		return secretarioTecnico;
	}

	public void setSecretarioTecnico(boolean secretarioTecnico) {
		this.secretarioTecnico = secretarioTecnico;
	}
	
	public boolean isSecretarioMedio() {
		return secretarioMedio;
	}

	public void setSecretarioMedio(boolean secretarioMedio) {
		this.secretarioMedio = secretarioMedio;
	}

	public boolean isCoordenadorCursoStricto() {
		return coordenadorCursoStricto;
	}

	public void setCoordenadorCursoStricto(boolean coordenadorCursoStricto) {
		this.coordenadorCursoStricto = coordenadorCursoStricto;
	}

	public boolean isCoordenadorCursoLato() {
		return coordenadorCursoLato;
	}

	public void setCoordenadorCursoLato(boolean coordenadorCursoLato) {
		this.coordenadorCursoLato = coordenadorCursoLato;
	}

	public boolean isSecretariaPosGraduacao() {
		return secretariaPosGraduacao;
	}

	public void setSecretariaPosGraduacao(boolean secretariaPosGraduacao) {
		this.secretariaPosGraduacao = secretariaPosGraduacao;
	}

	public boolean isCoordenadorMonitoria() {
		return coordenadorMonitoria;
	}

	public void setCoordenadorMonitoria(boolean coordenadorMonitoria) {
		this.coordenadorMonitoria = coordenadorMonitoria;
	}

	public boolean isComissaoMonitoria() {
		return comissaoMonitoria;
	}

	public void setComissaoMonitoria(boolean comissaoMonitoria) {
		this.comissaoMonitoria = comissaoMonitoria;
	}

	public boolean isComissaoCientificaMonitoria() {
		return comissaoCientificaMonitoria;
	}

	public void setComissaoCientificaMonitoria(
			boolean comissaoCientificaMonitoria) {
		this.comissaoCientificaMonitoria = comissaoCientificaMonitoria;
	}

	public boolean isAutorProjetoMonitoria() {
		return autorProjetoMonitoria;
	}

	public void setAutorProjetoMonitoria(boolean autorProjetoMonitoria) {
		this.autorProjetoMonitoria = autorProjetoMonitoria;
	}

	public boolean isComissaoPesquisa() {
		return comissaoPesquisa;
	}

	public void setComissaoPesquisa(boolean comissaoPesquisa) {
		this.comissaoPesquisa = comissaoPesquisa;
	}

	public boolean isComissaoExtensao() {
		return comissaoExtensao;
	}

	public void setComissaoExtensao(boolean comissaoExtensao) {
		this.comissaoExtensao = comissaoExtensao;
	}

	public boolean isPareceristaExtensao() {
		return pareceristaExtensao;
	}

	public void setPareceristaExtensao(boolean pareceristaExtensao) {
		this.pareceristaExtensao = pareceristaExtensao;
	}

	public boolean isPresidenteComiteExtensao() {
		return presidenteComiteExtensao;
	}

	public void setPresidenteComiteExtensao(boolean presidenteComiteExtensao) {
		this.presidenteComiteExtensao = presidenteComiteExtensao;
	}

	public boolean isCoordenadorExtensao() {
		return coordenadorExtensao;
	}

	public void setCoordenadorExtensao(boolean coordenadorExtensao) {
		this.coordenadorExtensao = coordenadorExtensao;
	}

	public boolean isCoordPesquisa() {
		return coordPesquisa;
	}

	public void setCoordPesquisa(boolean coordPesquisa) {
		this.coordPesquisa = coordPesquisa;
	}

	public boolean isOrientadorAcademico() {
		return orientadorAcademico;
	}

	public boolean isCoOrientadorStricto() {
		return coOrientadorStricto;
	}

	public void setCoOrientadorStricto(boolean coOrientadorStricto) {
		this.coOrientadorStricto = coOrientadorStricto;
	}

	public int getCoOrientacoesStricto() {
		return coOrientacoesStricto;
	}

	public void setCoOrientacoesStricto(int coOrientacoesStricto) {
		this.coOrientacoesStricto = coOrientacoesStricto;
	}

	public void setOrientadorAcademico(boolean orientadorAcademico) {
		this.orientadorAcademico = orientadorAcademico;
	}

	public boolean isOrientadorStricto() {
		return orientadorStricto;
	}

	public void setOrientadorStricto(boolean orientadorStricto) {
		this.orientadorStricto = orientadorStricto;
	}

	public boolean isUnidadeEspecializada() {
		return unidadeEspecializada;
	}

	public void setUnidadeEspecializada(boolean unidadeEspecializada) {
		this.unidadeEspecializada = unidadeEspecializada;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setConsulta(boolean consulta) {
		this.consulta = consulta;
	}

	public boolean isPlanejamento() {
		return planejamento;
	}

	public void setPlanejamento(boolean planejamento) {
		this.planejamento = planejamento;
	}

	public Unidade getSecretariaCentro() {
		return secretariaCentro;
	}

	public void setSecretariaCentro(Unidade secretariaCentro) {
		this.secretariaCentro = secretariaCentro;
	}

	public Collection<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	public int getOrientacoesAcademicas() {
		return orientacoesAcademicas;
	}

	public void setOrientacoesAcademicas(int orientacoesAcademicas) {
		this.orientacoesAcademicas = orientacoesAcademicas;
	}

	public int getOrientacoesStricto() {
		return orientacoesStricto;
	}

	public void setOrientacoesStricto(int orientacoesStricto) {
		this.orientacoesStricto = orientacoesStricto;
	}

	public Collection<Unidade> getProgramas() {
		return programas;
	}

	public void setProgramas(Collection<Unidade> programas) {
		this.programas = programas;
	}

	public boolean isMultiplosVinculos() {
		return multiplosVinculos;
	}

	public void setMultiplosVinculos(boolean multiplosVinculos) {
		this.multiplosVinculos = multiplosVinculos;
	}

	public boolean isEscolhidoVinculoPrincipal() {
		return escolhidoVinculoPrincipal;
	}

	public void setEscolhidoVinculoPrincipal(boolean escolhidoVinculoPrincipal) {
		this.escolhidoVinculoPrincipal = escolhidoVinculoPrincipal;
	}

	public boolean isCpdi() {
		return cpdi;
	}

	public void setCpdi(boolean cpdi) {
		this.cpdi = cpdi;
	}

	public boolean isNit() {
		return nit;
	}

	public void setNit(boolean nit) {
		this.nit = nit;
	}

	public boolean isDocenteAptoAvaliacaoInstitucional() {
		return docenteAptoAvaliacaoInstitucional;
	}

	public void setDocenteAptoAvaliacaoInstitucional(
			boolean docenteAptoAvaliacaoInstitucional) {
		this.docenteAptoAvaliacaoInstitucional = docenteAptoAvaliacaoInstitucional;
	}

	public boolean isDiscenteAptoAvaliacaoInstitucional() {
		return discenteAptoAvaliacaoInstitucional;
	}

	public void setDiscenteAptoAvaliacaoInstitucional(
			boolean discenteAptoAvaliacaoInstitucional) {
		this.discenteAptoAvaliacaoInstitucional = discenteAptoAvaliacaoInstitucional;
	}

	public boolean isModuloBiblioteca() {
		return moduloBiblioteca;
	}

	public void setModuloBiblioteca(boolean moduloBiblioteca) {
		this.moduloBiblioteca = moduloBiblioteca;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Incrementa o total de sistemas
	 */
	public void incrementaTotalSistemas() {
		totalSistemas++;
	}

	/**
	 * diz se o usuario eh coordenador de algum curso de probasica
	 * 
	 * @return
	 */
	public boolean isCoordenadorCursoProbasica() {
		if (getCursos() != null) {
			return getCursos().iterator().next().isProbasica();
		}
		return false;
	}

	/**
	 * Verifica se � coordenador EAD em um dos cursos curso
	 * 
	 * @return
	 */
	public boolean isCoordenadorCursoEad() {
		Collection<Curso> cursos = getCursos();
		if (!isEmpty(cursos)) {
			Curso curso = cursos.iterator().next();
			if (curso.isADistancia())
				return true;
		}

		return false;
	}

	public boolean isAlgumUsuarioStricto() {
		return coordenadorCursoStricto || secretariaPosGraduacao || ppg;
	}
	
	public boolean isAlgumUsuarioLato() {
		return coordenadorCursoLato || secretarioLato || lato;
	}

	public boolean isProgramaStricto() {
		return coordenadorCursoStricto || secretariaPosGraduacao;
	}

	public String getVestibularClass() {
		return getClass(vestibular);
	}
	
	public String getNeeClass() {
		return getClass(nee);
	}

	public String getBibliotecaClass() {
		return getClass(moduloBiblioteca);
	}

	public String getDiplomaClass(){
		return getClass(moduloDiploma);
	}
	
	public String getConvenioEstagioClass(){
		return getClass(moduloConvenioEstagio);
	}
	
	public String getPortalConcedenteEstagioClass(){
		return getClass(portalConcedenteEstagio);
	}
	
	public String getPortalFamiliarClass(){
		return getClass(portalFamiliar);
	}	
	
	public String getRelacoesInternacionaisClass() {
		return getClass(relacoesInternacionais);
	}
	
	public boolean isEspacoFisico() {
		return espacoFisico;
	}

	public void setEspacoFisico(boolean espacoFisico) {
		this.espacoFisico = espacoFisico;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public boolean isAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(boolean avaliacao) {
		this.avaliacao = avaliacao;
	}
	
	public String getAvaliacaoClass() {
		return getClass(avaliacao);
	}

	public boolean isSolicitanteProjetoEnsino() {
		return solicitanteProjetoEnsino;
	}

	public void setSolicitanteProjetoEnsino(boolean solicitanteProjetoEnsino) {
		this.solicitanteProjetoEnsino = solicitanteProjetoEnsino;
	}

	public boolean isPendentePlanoResposicao() {
		return pendentePlanoResposicao;
	}

	public void setPendentePlanoResposicao(boolean pendentePlanoResposicao) {
		this.pendentePlanoResposicao = pendentePlanoResposicao;
	}

	public boolean isComplexoHospitalar() {
		return complexoHospitalar;
	}

	public void setComplexoHospitalar(boolean complexoHospitalar) {
		this.complexoHospitalar = complexoHospitalar;
	}
	
	public String getComplexoHospitalarClass() {
		return getClass(complexoHospitalar);
	}

	public boolean isAdministradorStricto() {
		return administradorStricto;
	}

	public void setAdministradorStricto(boolean administradorStricto) {
		this.administradorStricto = administradorStricto;
	}
	
	public boolean isAmbientesVirtuais () {
		return ambientesVirtuais;
	}

	public void setAmbientesVirtuais(boolean ambientesVirtuais) {
		this.ambientesVirtuais = ambientesVirtuais;
	}

	public boolean isComissaoIntegrada() {
	    return comissaoIntegrada;
	}

	public void setComissaoIntegrada(boolean comissaoIntegrada) {
	    this.comissaoIntegrada = comissaoIntegrada;
	}

	public boolean isRelatorios() {
		return relatorios;
	}

	public void setRelatorios(boolean relatorios) {
		this.relatorios = relatorios;
	}

	public boolean isGestorResidenciaMedica() {
		return gestorResidenciaMedica;
	}

	public void setGestorResidenciaMedica(boolean gestorResidenciaMedica) {
		this.gestorResidenciaMedica = gestorResidenciaMedica;
	}
	
	public boolean isGestorComplexoHospitalar() {
		return gestorComplexoHospitalar;
	}

	public void setGestorComplexoHospitalar(boolean gestorComplexoHospitalar) {
		this.gestorComplexoHospitalar = gestorComplexoHospitalar;
	}
	
	public boolean isCoordenadorResidenciaMedica() {
		return coordenadorResidenciaMedica;
	}

	public void setCoordenadorResidenciaMedica(boolean coordenadorResidenciaMedica) {
		this.coordenadorResidenciaMedica = coordenadorResidenciaMedica;
	}

	public boolean isCoordenadorCursoTecnico() {
		return coordenadorCursoTecnico;
	}
	
	public Collection<Unidade> getResidencias() {
		return residencias;
	}

	public void setResidencias(Collection<Unidade> residencias) {
		this.residencias = residencias;
	}

	public void setCoordenadorCursoTecnico(boolean coordenadorCursoTecnico) {
		this.coordenadorCursoTecnico = coordenadorCursoTecnico;
	}

	public boolean isModuloDiploma() {
		return moduloDiploma;
	}

	public void setModuloDiplomas(boolean moduloDiploma) {
		this.moduloDiploma = moduloDiploma;
	}

	public boolean isModuloConvenioEstagio() {
		return moduloConvenioEstagio;
	}

	public void setModuloConvenioEstagio(boolean moduloConvenioEstagio) {
		this.moduloConvenioEstagio = moduloConvenioEstagio;
	}

	public boolean isPortalConcedenteEstagio() {
		return portalConcedenteEstagio;
	}

	public void setPortalConcedenteEstagio(boolean portalConcedenteEstagio) {
		this.portalConcedenteEstagio = portalConcedenteEstagio;
	}

	public boolean isPortalFamiliar() {
		return portalFamiliar;
	}

	public void setPortalFamiliar(boolean portalFamiliar) {
		this.portalFamiliar = portalFamiliar;
	}

	public boolean isAcoesAssociadas() {
		return acoesAssociadas;
	}

	public void setAcoesAssociadas(boolean acoesAssociadas) {
		this.acoesAssociadas = acoesAssociadas;
	}

	public boolean isAvaliadorAcoesAssociadas() {
		return avaliadorAcoesAssociadas;
	}

	public void setAvaliadorAcoesAssociadas(boolean avaliadorAcoesAssociadas) {
		this.avaliadorAcoesAssociadas = avaliadorAcoesAssociadas;
	}

	public boolean isAbrirChamado() {
		return abrirChamado;
	}

	public void setAbrirChamado(boolean abrirChamado) {
		this.abrirChamado = abrirChamado;
	}

	public Boolean getAcessibilidade() {
		return acessibilidade;
	}

	public void setAcessibilidade(Boolean acessibilidade) {
		this.acessibilidade = acessibilidade;
	}

	public boolean isProgramaAtualizacaoPedagogica() {
		return programaAtualizacaoPedagogica;
	}

	public void setProgramaAtualizacaoPedagogica(
			boolean programaAtualizacaoPedagogica) {
		this.programaAtualizacaoPedagogica = programaAtualizacaoPedagogica;
	}

	public boolean isMembroApoioDocenciaAssistida() {
		return membroApoioDocenciaAssistida;
	}

	public void setMembroApoioDocenciaAssistida(boolean membroApoioDocenciaAssistida) {
		this.membroApoioDocenciaAssistida = membroApoioDocenciaAssistida;
	}	
	
	public boolean isPendenteNotificacaoAcademica() {
		return pendenteNotificacaoAcademica;
	}

	public void setPendenteNotificacaoAcademica(boolean pendenteNotificacaoAcademica) {
		this.pendenteNotificacaoAcademica = pendenteNotificacaoAcademica;
	}

	public boolean isPendenteRelatoriosExtensao() {
		return pendenteRelatoriosExtensao;
	}

	public void setPendenteRelatoriosExtensao(boolean pendenteRelatoriosExtensao) {
		this.pendenteRelatoriosExtensao = pendenteRelatoriosExtensao;
	}
	
	/** M�todo utilizado para identificar docentes apenas do ensino m�dio. */
	public boolean isNivelDocenteMedio(){
		return nivelDocente.equals(String.valueOf(NivelEnsino.MEDIO));
	}

	public boolean isRelacoesInternacionais() {
		return relacoesInternacionais;
	}

	public void setRelacoesInternacionais(boolean relacoesInternacionais) {
		this.relacoesInternacionais = relacoesInternacionais;
	}

	public boolean isPedagogico() {
		return pedagogico;
	}

	public void setPedagogico(boolean pedagogico) {
		this.pedagogico = pedagogico;
	}

	public boolean isPendenteQuestionarioExtensao() {
		return pendenteQuestionarioExtensao;
	}

	public void setPendenteQuestionarioExtensao(boolean pendenteQuestionarioExtensao) {
		this.pendenteQuestionarioExtensao = pendenteQuestionarioExtensao;
	}

	public boolean isCoordenadorEstagio() {
		return coordenadorEstagio;
	}

	public void setCoordenadorEstagio(boolean coordenadorEstagio) {
		this.coordenadorEstagio = coordenadorEstagio;
	}
	
}