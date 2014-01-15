/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe para guardar informações sobre acesso aos subsistemas do SIGAA
 * 
 * @author David Pereira
 * 
 */
public class DadosAcesso {

	/** Constante textual para indicar que o acesso está setado. */
	private final String classOn = "on";

	/** Constante textual para indicar que o acesso está negado. */
	private final String classOff = "off";

	/** Usuário referente à estes dados de acesso.*/
	private final Usuario usuario;

	/** Total de sistemas que o usuário tem acesso. */
	protected int totalSistemas;

	/** Nível de Ensino do docente. */
	protected String nivelDocente;

	/** Armazena informaçãoes sobre os Módulos que tem acesso */
	protected Set<DadosSubsistemaPrincipal> dadosSubsistemaPrincipal = new HashSet<DadosSubsistemaPrincipal>();

	/** Atributo que identifica se o usuário tem a permissão para abrir chamados ou não */
	protected boolean abrirChamado;
	
	/** Indica se o usuário tem nível de acesso se administrador dos sistemas. */
	protected boolean administradorSistema;

	/** Indica se o usuário tem nível de acesso ao módulo de Ensino Médio. */
	protected boolean medio;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Ensino Infantil. */
	protected boolean infantil;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Ensino Técnico. */
	protected boolean tecnico;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Escolas Acadêmicas Especializadas. */
	protected boolean formacaoComplementar;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Monitoria. */
	protected boolean monitoria;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Pesquisa. */
	protected boolean pesquisa;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Extensão. */
	protected boolean extensao;

	/** Indica se o usuário tem nível de acesso ao módulo de Ações Acadêmicas Associadas. */
	protected boolean acoesAssociadas;

	/** Indica se o usuário tem nível de acesso ao módulo de Lato Sensu. */
	protected boolean lato;
	
	/** Indica se o usuário tem nível de acesso ao portal do discente. */
	protected boolean discente;
	
	/** Indica se o usuário tem nível de acesso ao portal do discente, como discente no nível médio. */
	protected boolean discenteMedio;
	
	/** Indica se o usuário tem nível de acesso ao portal do docente. */
	protected boolean docente;
	
	/** Indica se o usuário tem nível de acesso para o prodocente. */
	protected boolean prodocente;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Graduação. */
	protected boolean graduacao;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Stricto Sensu. */
	protected boolean stricto;
	
	/** Indica se o usuário tem nível de acesso ao SIPAC. */
	protected boolean sipac;
	
	/** Indica se o usuário tem nível de acesso ao SIGRH. */
	protected boolean sigrh;
	
	/** Indica se o usuário tem nível de acesso ao SIGPP. */
	protected boolean sigpp;
	
	/** Indica se o usuário tem nível de acesso ao módulo de Ensino à Distância - EAD. */
	protected boolean ead;
	
	/** Indica se o usuário tem nível de acesso ao módulo do Complexo Hospitalar. */
	protected boolean complexoHospitalar;
	
	/** Indica se o usuário tem nível de acesso de Gestor Complexo Hospitalar. */
	protected boolean gestorComplexoHospitalar;
	
	/** Indica se o usuário tem nível de acesso de Gestor Residência Médica. */
	protected boolean gestorResidenciaMedica;

	/** Indica se o usuário tem nível de acesso às operações de Coordenação de um Programa de Residência Médica. */
	protected boolean coordenadorResidenciaMedica;

	/** Indica se o usuário é docente da UFRN. */
	protected boolean docenteUFRN;
	
	/** Indica se o usuário tem nível de acesso se administrador dos Stricto Sensu. */
	protected boolean administradorStricto;
	
	
	/** Indica se o usuário tem nível de acesso de Gestor do PAP. */
	protected boolean programaAtualizacaoPedagogica;

	/** Indica se o usuário tem nível de acesso ao módulo de tradução de documentos. */
	protected boolean relacoesInternacionais;
	
	// graduação
	/** Indica se o usuário tem nível de acesso às operações de Coordenação Didático-Pedagógica - CDP. */
	protected boolean cdp;

	/** Indica se o usuário tem nível de acesso às operações de administrador Departamento de Administração Escolar - DAE. */
	protected boolean administradorDAE;

	/** Indica se o usuário tem nível de acesso às operações do Departamento de Administração Escolar - DAE. */
	protected boolean dae;

	/** Indica se o usuário tem nível de acesso às operações da Secretaria de Assuntos Estudantis - SAE. */
	protected boolean sae;
	
	/** Indica se o usuário tem nível de acesso às operações da Ouvidoria. */
	protected boolean ouvidoria;

	/** Indica se o usuário tem nível de acesso às operações da PRÓ-REITORIA DE PÓS-GRADUAÇÃO - PPG. */
	protected boolean ppg;
	
	/** Indica se o usuário é um membros da Coordenação de Apoio Técnico-Pedagógico da Docência Assistida (CATP),
	 * permitindo realizar operações de Planos de Docência Assistida - PPG. */
	protected boolean membroApoioDocenciaAssistida;	

	/** Indica se o usuário tem nível de acesso às operações do Vestibular. */
	protected boolean vestibular;
	
	/** Indica se o usuário tem nível de acesso às operações do Nee. */
	protected boolean nee;

	// protected boolean coordenacaoUnica;
	/** Indica se o usuário tem nível de acesso ao menu administração. */
	protected boolean administracao;

	/** Indica se o usuário tem nível de acesso de Chefe de Departamento. */
	protected boolean chefeDepartamento;
	
	/** Indica se o usuário tem nível de acesso de Diretor de Centro. */
	protected boolean diretorCentro;
	
	/** Indica se o usuário tem nível de acesso de Chefe de Unidade. */
	protected boolean chefeUnidade;

	/** Indica se o usuário tem nível de acesso coordenador de Pólo de Ensino à Distância - EAD. */
	protected boolean coordenadorPolo;
	
	/** Indica se o usuário tem nível de acesso tutor de Ensino à Distância - EAD. */
	protected boolean tutorEad;

	/** Indica se o usuário tem nível de acesso de Coordenador de Curso de Graduação. */
	protected boolean coordenadorCursoGrad;
	
	/** Indica se o usuário tem nível de acesso de Coordenador de Estágio. */
	protected boolean coordenadorEstagio;
	
	/** Indica se o usuário tem nível de acesso de Coordenador de Curso de Ensino à Distância - EAD. */
	protected boolean cursoEad;

	/** Coordenação geral de todos os cursos de probasica, quando o usuário tem o papel GESTOR_PROBASICA. */
	protected boolean coordenacaoProbasica;

	/** Indica se o usuário tem nível de acesso de Secretário de Curso de Graduação. */
	protected boolean secretarioGraduacao;
	
	/** Indica se o usuário tem nível de acesso de Secretário de Departamento. */
	protected boolean secretarioDepartamento;
	
	/** Indica se o usuário tem nível de acesso de Secretário de Centro. */
	protected boolean secretarioCentro;
	
	/** Indica se o usuário tem nível de acesso de Secretário de Curso de Lato Sensu. */
	protected boolean secretarioLato;
	
	/** Indica se o usuário tem nível de acesso de Secretário de Técnico. */
	protected boolean secretarioTecnico;
	
	/** Indica se o usuário tem nível de acesso de Secretário de Médio. */
	protected boolean secretarioMedio;

	/** Indica se o usuário tem nível de acesso de Coordenador de Curso de Stricto Sensu. */
	protected boolean coordenadorCursoStricto;

	/** Indica se o usuário tem nível de acesso de Coordenador de Curso de Lato Sensu. */
	protected boolean coordenadorCursoLato;
	
	/** Indica se o usuário tem nível de acesso de Coordenador de Curso Técnico. */
	protected boolean coordenadorCursoTecnico;

	/** Indica se o usuário tem nível de acesso de Secretário de Pós-Graduação. */
	protected boolean secretariaPosGraduacao;

	/** Indica se o usuário tem nível de acesso de Coordenador de Monitoria. */
	protected boolean coordenadorMonitoria;
	
	/** Indica se o usuário tem nível de acesso de Membro da Comissão de Monitoria. */
	protected boolean comissaoMonitoria;
	
	/** Indica se o usuário tem nível de acesso de Membro de Comissão Científica de Monitoria. */
	protected boolean comissaoCientificaMonitoria;
	
	/** Indica se o usuário tem nível de acesso de Autoria de Projetos de Monitoria. */
	protected boolean autorProjetoMonitoria;
	
	/** Indica se o usuário tem nível de acesso de Solicitante de Projeto de Ensino. */
	protected boolean solicitanteProjetoEnsino;

	/** Indica se o usuário tem nível de acesso de Comissão de Pesquisa. */
	protected boolean comissaoPesquisa;
	
	/** Indica se o usuário tem nível de acesso de Comissão de Extensão. */
	protected boolean comissaoExtensao;

	/** Indica se o usuário tem nível de acesso de Comissão Integrada de Ensino, Pesquisa e Extensão. */
	protected boolean comissaoIntegrada;
	
	/** Indica se o usuário tem nível de acesso de Paceiro de Extensão. */
	protected boolean pareceristaExtensao;
	
	/** Indica se o usuário tem nível de acesso de Avaliador de Ações Acadêmicas Associadas. */
	protected boolean avaliadorAcoesAssociadas;	
	
	/** Indica se o usuário tem nível de acesso de Presidente de Comitê  de Extensão. */
	protected boolean presidenteComiteExtensao;

	/** Indica se o usuário tem nível de acesso de Coordenador de Projeto de Extensão. */
	protected boolean coordenadorExtensao;

	/** Indica se o usuário tem nível de acesso de Coordenador de Projeto de Pesquisa. */
	protected boolean coordPesquisa;

	/** Indica se o usuário tem nível de acesso de Orientador Acadêmico. */
	protected boolean orientadorAcademico;
	
	/** Indica se o usuário tem nível de acesso de Orientador de Stricto Sensu. */
	protected boolean orientadorStricto;
	
	/** Indica se o usuário tem nível de acesso de CoOrientador de Stricto Sensu. */
	protected boolean coOrientadorStricto;

	/** Indica se o usuário tem nível de acesso de Gestor de Unidade Especializada. */
	protected boolean unidadeEspecializada;

	/** Indica se o usuário tem nível de acesso de Consulta à alunos. */
	protected boolean consulta;
	
	/** Indica se o usuário tem nível de acesso ao Portal de Planejamento. */
	protected boolean planejamento;

	/** Unidade do secretário de Centro. */
	protected Unidade secretariaCentro;

	/** Coleção de cursos que o coordenador coordena. */
	protected Collection<Curso> cursos;

	/** Quantidade de orientações acadêmicas do orientador acadêmico. */
	protected int orientacoesAcademicas;
	
	/** Quantidade de orientações do orientador stricto. */
	protected int orientacoesStricto;
	
	/** Quantidade de orientações do orientador stricto. */
	protected int coOrientacoesStricto;

	/** Coleção de programas, quando um usuário é secretário de mais de um programa. */
	protected Collection<Unidade> programas;

	/** Indica que o usuário possui múltiplos vínculos. */
	protected boolean multiplosVinculos;

	/** Indica que o usuário tem o vínculo principal escolhido. */
	protected boolean escolhidoVinculoPrincipal = true;

	/** Indica se o usuário tem nível de acesso ao Portal da CPDI. */
	protected boolean cpdi;

	/** Indica se o usuário tem nível de acesso ao NIT. */
	protected boolean nit;

	/** Indica que o docente está apto para preencher a Avaliação Institucional. */
	protected boolean docenteAptoAvaliacaoInstitucional;

	/** Indica que o discente está apto para preencher a Avaliação Institucional. */
	protected boolean discenteAptoAvaliacaoInstitucional;

	/** Indica que o usuário tem permissão para usar o módulo da biblioteca. Setado no AcessoBiblioteca e lido na página itens_menu_principal.jsp */
	protected boolean moduloBiblioteca; 

	/** Indica que o usuário tem permissão para usar o módulo de registro de diplomas. */
	protected boolean moduloDiploma;
	
	/** Indica que o usuário tem permissão para usar o módulo de Convênios de Estágio. */
	protected boolean moduloConvenioEstagio;
	
	/** Indica que o usuário é gestor de espaço físico. */
	protected boolean espacoFisico;
	
	/** Biblioteca ao qual o usuário é vinculado.
	 * Ele so vai poder realizar algumas operações se pertencer a um biblioteca, e só pode pertencer a uma biblioteca.
	 */
	protected Biblioteca biblioteca;
	
	/** Indica se o usuário tem nível de acesso ao Portal da Avaliação. */
	protected boolean avaliacao;

	/** Indica se o usuário tem plano de reposição de aula Pendente. */
	protected boolean pendentePlanoResposicao;
	
	/** Indica se o usuário tem nível de acesso às operações dos Ambientes Virtuais. */
	protected boolean ambientesVirtuais;
	
	/** Indica se o usuário tem nível de acesso ao Portal de Relatórios. */
	protected boolean relatorios;
	
	/** Indica se o usuário tem nível de acesso ao . */
	protected boolean portalConcedenteEstagio;
	
	/** Indica se o usuário tem nível de acesso ao Portal do Familiar. */
	protected boolean portalFamiliar;		
	
	/** Coleção de programas de residências médica que o coordenador coordena */
	protected Collection<Unidade> residencias;
	
	/** Verifica se o modo acessibilidade está ativo */
	protected Boolean acessibilidade;
	
	/** Indica se o usuário possui notificação acadêmica Pendente. */
	protected boolean pendenteNotificacaoAcademica;
	
	/** Indica se o usuário está pendente quanto ao envio de relatórios parcial/final(EXTENSÃO)*/
	protected boolean pendenteRelatoriosExtensao = false;
	
	/** Indica se o usuário está pendente quanto ao envio de questionário de Extensão */
	protected boolean pendenteQuestionarioExtensao = false;

	/** Indica se o usuário tem somente permissao de acesso a Consultas no módulo de Ensino Médio ou no módulo de Ensino Técnico. */
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

	/** Retorna uma descrição textual indicando se a permissão está disponível (on) ou não (off).
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
	 * Verifica se é coordenador EAD em um dos cursos curso
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
	
	/** Método utilizado para identificar docentes apenas do ensino médio. */
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