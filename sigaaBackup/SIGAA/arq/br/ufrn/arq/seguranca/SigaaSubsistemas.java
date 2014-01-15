/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '22/08/2005'
 *
 */
package br.ufrn.arq.seguranca;

import java.util.Map;
import java.util.TreeMap;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Classe que define os subsistemas do SIGAA.
 * 
 * @author Gleydson
 */
public class SigaaSubsistemas {

	/* ***************************************************************** */
	/* ***************************************************************** */
	/* ******* ATEN��O!!! ******* */
	/* ******* ******* */
	/* ******* AO DECLARAR UM NOVO SUBSISTEMA NESTA CLASSE, ******* */
	/* ******* N�O ESQUECER DE ADICION�-LO, TAMB�M, AO MAPA ******* */
	/* ******* LOGO ABAIXO. ******* */
	/* ***************************************************************** */
	/* ***************************************************************** */

	/** Constante que define o Subsistema de Administra��o do SIGAA. */
	public static final SubSistema ADMINISTRACAO = new SubSistema(10100,
			"Administra��o", "/sigaa/administracao/index.jsf",
			"menuAdministracao", "/administracao/cadastro/");

	/** Constante que define o Subsistema do menu principal do SIGAA . */
	public static final SubSistema SIGAA = new SubSistema(10150, "SIGAA",
			"/sigaa/menuPrincipal.do", "menuPrincipal");

	/** Constante que define o Subsistema de Ensino Infantil e Fundamental. */
	public static final SubSistema INFANTIL = new SubSistema(10200,
			"Ensino Infantil e Fundamental", "/sigaa/verMenuInfantil.do",
			"menuInfantil", "/infantil/");

	/** Constante que define o Subsistema de Ensino M�dio. */
	public static final SubSistema MEDIO = new SubSistema(10300,
			"Ensino M�dio", "/sigaa/verMenuMedio.do", "menuMedio");

	/** Constante que define o Subsistema de Ensino T�cnico. */
	public static final SubSistema TECNICO = new SubSistema(10400,
			"Ensino T�cnico", "/sigaa/verMenuTecnico.do", "menuTecnico");

	/** Constante que define o Subsistema de Escolas Especializadas. */
	public static final SubSistema FORMACAO_COMPLEMENTAR = new SubSistema(
			10450, "Forma��o Complementar",
			"/sigaa/verMenuFormacaoComplementar.do", "menuFormacaoComplementar");

	/** Constante que define o Subsistema de Gradua��o. */
	public static final SubSistema GRADUACAO = new SubSistema(10500,
			"Gradua��o", "/sigaa/verMenuGraduacao.do", "menuGraduacao");

	/** Constante que define o Subsistema de Lato Sensu. */
	public static final SubSistema LATO_SENSU = new SubSistema(10600,
			"Lato Sensu", "/sigaa/verMenuLato.do", "menuLato");

	/** Constante que define o Subsistema de Stricto Sensu. */
	public static final SubSistema STRICTO_SENSU = new SubSistema(10700,
			"Stricto Sensu", "/sigaa/verMenuStricto.do", "menuStricto",
			"/stricto/");

	/** Constante que define o Subsistema de Pesquisa. */
	public static final SubSistema PESQUISA = new SubSistema(10800, "Pesquisa",
			"/sigaa/verMenuPesquisa.do", "menuPesquisa");

	/** Constante que define o Subsistema de Extens�o. */
	public static final SubSistema EXTENSAO = new SubSistema(10900, "Extens�o",
			"/sigaa/verMenuExtensao.do", "menuExtensao", "/extensao/");

	/** Constante que define o Subsistema de Monitoria. */
	public static final SubSistema MONITORIA = new SubSistema(11000,
			"Monitoria", "/sigaa/verMenuMonitoria.do", "menuMonitoria",
			"/monitoria/");

	/** Constante que define o Subsistema de Produ��o Intelectual. */
	public static final SubSistema PROD_INTELECTUAL = new SubSistema(11100,
			"Produ��o Intelectual", "/sigaa/verMenuProdocente.do",
			"menuProdocente", "/prodocente/");

	/** Constante que define o Subsistema de Ensino a Dist�ncia. */
	public static final SubSistema SEDIS = new SubSistema(11200,
			"Ensino a Dist�ncia", "/sigaa/verMenuSedis.do", "menuSedis",
			"/ead/");

	/** Constante que define o Subsistema Assist�ncia ao Estudante. */
	public static final SubSistema SAE = new SubSistema(11300,
			"Assist�ncia ao Estudante", "/sigaa/verMenuSae.do", "menuSae",
			"/sae/");

	/** Constante que define o Subsistema Ensino Sa�de. */
	public static final SubSistema SAUDE = new SubSistema(11300,
			"Ensino Sa�de", "/sigaa/verMenuSaude.do", "menuSaude", "/saude/");

	/** Constante que define o Subsistema de Infra-Estrutura F�sica. */
	public static final SubSistema INFRA_ESTRUTURA_FISICA = new SubSistema(
			11400, "Infraestrutura F�sica", "/sigaa/verMenuInfra.do",
			"menuInfra", "/infraestrutura/");

	/** Constante que define o Portal da Avalia��o Institucional. */
	public static final SubSistema PORTAL_AVALIACAO_INSTITUCIONAL = new SubSistema(
			11500, "Avalia��o Institucional", "/sigaa/verPortalAvaliacao.do",
			"portalAvaliacao");

	/** Constante que define o subsistema Metr�pole Digital. */
	public static final SubSistema METROPOLE_DIGITAL = new SubSistema(11600,
			"Metr�pole Digital", "/sigaa/verMenuMetropoleDigital.do",
			"menuMetropoleDigital");

	// Portais

	/** Constante que define o Portal do Docente. */
	public static final SubSistema PORTAL_DOCENTE = new SubSistema(12000,
			"Portal do Docente", "/sigaa/verPortalDocente.do", "portalDocente");

	/** Constante que define o Portal do Discente. */
	public static final SubSistema PORTAL_DISCENTE = new SubSistema(12100,
			"Portal do Discente", "/sigaa/verPortalDiscente.do",
			"portalDiscente");

	/** Constante que define o Portal do Consultor. */
	public static final SubSistema PORTAL_CONSULTOR = new SubSistema(12200,
			"Portal do Consultor", "/sigaa/verPortalConsultor.do",
			"portalConsultor");

	/** Constante que define o Subsistema Turma Virtual . */
	public static final SubSistema PORTAL_TURMA = new SubSistema(12300,
			"Turma Virtual", "/sigaa/ava/index.jsf", "turmaVirtual");

	/** Constante que define o Portal do Tutor. */
	public static final SubSistema PORTAL_TUTOR = new SubSistema(12400,
			"Portal do Tutor", "/sigaa/verPortalTutor.do", "portalTutor");
	
	/** Constante que define o Portal do Tutor do IMD. */
	public static final SubSistema PORTAL_TUTOR_IMD = new SubSistema(12402,
			"Portal do Tutor", "/sigaa/verPortalTutoriaIMD.do", "portalTutoriaIMD");

	/** Constante que define o Portal do Coordenador de P�lo. */
	public static final SubSistema PORTAL_COORDENADOR_POLO = new SubSistema(
			12401, "Portal do Coordenador de P�lo",
			"/sigaa/verPortalCoordPolo.do", "portalCoordPolo");

	/** Constante que define o Portal da Reitoria. */
	public static final SubSistema PORTAL_PLANEJAMENTO = new SubSistema(12500,
			"Portal da Reitoria", "/sigaa/verPortalPlanejamento.do",
			"portalPlanejamento");

	/** Constante que define o Portal do Coordenador. */
	public static final SubSistema PORTAL_COORDENADOR = new SubSistema(12600,
			"Portal do Coordenador", "/sigaa/verPortalCoordenadorGraduacao.do",
			"coordenador");

	/** Constante que define o Portal do Coordenador Stricto. */
	public static final SubSistema PORTAL_COORDENADOR_STRICTO = new SubSistema(
			12700, "Portal do Coordenador Stricto",
			"/sigaa/verMenuStricto.do?portal=programa", "coordenacaoStricto");

	/** Constante que define o Portal da CPDI. */
	public static final SubSistema PORTAL_CPDI = new SubSistema(12800,
			"Portal da CPDI", "/sigaa/portais/cpdi/menu.jsf", "portalCpdi");

	/** Constante que define o Portal do Coordenador Lato Sensu. */
	public static final SubSistema PORTAL_COORDENADOR_LATO = new SubSistema(
			12900, "Portal do Coordenador Lato Sensu",
			"/sigaa/verMenuLato.do?destino=coordenacao", "menuLato");

	/** Constante que define o Subsistema Vestibular. */
	public static final SubSistema VESTIBULAR = new SubSistema(13000,
			"Vestibular", "/sigaa/verMenuVestibular.do", "menuVestibular",
			"/vestibular/");

	/** Constante que define o Subsistema Biblioteca. */
	public static final SubSistema BIBLIOTECA = new SubSistema(14000,
			"Biblioteca", "/sigaa/verMenuBiblioteca.do", "menuBiblioteca",
			"/biblioteca/");

	/** Constante que define o Subsistema Comunidade Virtual. */
	public static final SubSistema COMUNIDADE_VIRTUAL = new SubSistema(14500,
			"Comunidade Virtual", "/sigaa/cv/index.jsp", "comunidadeVirtual");

	/** Constante que define o Subsistema Resid�ncias em Sa�de. */
	public static final SubSistema COMPLEXO_HOSPITALAR = new SubSistema(14600,
			"Resid�ncias em Sa�de", "/sigaa/verMenuComplexoHospitalar.do",
			"menuComplexoHospitalar");

	/** Constante que define o Subsistema Sigaa Mobile. */
	public static final SubSistema SIGAA_MOBILE = new SubSistema(15000,
			"Sigaa Mobile", "/sigaa/mobile/touch/menu.jsf",
			"/mobile/touch/menu.jsf");

	/** Constante que define o Subsistema de Ambientes Virtuais. */
	public static final SubSistema AMBIENTES_VIRTUAIS = new SubSistema(16600,
			"Ambientes Virtuais", "/sigaa/verMenuAmbientesVirtuais.do",
			"menuAmbientesVirtuais");

	/** Constante que define o Subsistema de Projetos Acad�micos. */
	public static final SubSistema PROJETOS = new SubSistema(16700,
			"Projetos Acad�micos", "/sigaa/verMenuProjetos.do",
			"menuProjetosAcademicos");

	/**
	 * Constante que define o Subsistema para simples consulta de alunos
	 */
	public static final SubSistema CONSULTA = new SubSistema(20000,
			"Consultas Acad�micas", "/sigaa/verMenuConsulta.do", "menuConsulta");

	/**
	 * Constante que define o Subsistema referente aos relat�rios de outros
	 * SubSistemas
	 */
	public static final SubSistema PORTAL_RELATORIOS = new SubSistema(21000,
			"Portal de Relat�rios de Gest�o",
			"/sigaa/portais/relatorios/menu.jsf", "portalRelatorios");

	/**
	 * Constante que define o Subsistema de registro e impress�o de diplomas.
	 */
	public static final SubSistema REGISTRO_DIPLOMAS = new SubSistema(22000,
			"Registro de Diplomas", "/sigaa/verMenuRegistroDiplomas.do",
			"menuRegistroDiplomas");

	/**
	 * Constante que define o Subsistema de Conv�nios de Est�gio.
	 */
	public static final SubSistema CONVENIOS_ESTAGIO = new SubSistema(23000,
			"Conv�nios de Est�gio", "/sigaa/estagio/menu.jsf",
			"menuConveniosEstagio");

	/**
	 * Constante que define o Portal de Est�gio da Empresa.
	 */
	public static final SubSistema PORTAL_CONCEDENTE_ESTAGIO = new SubSistema(
			23100, "Portal do Concedente de Est�gio",
			"/sigaa/verPortalConcedenteEstagio.do",
			"menuPortalConcedenteEstagio");

	/**
	 * Constante que define o Subsistema de Necessidades Educacionais Especiais
	 * (NEE).
	 */
	public static final SubSistema NEE = new SubSistema(24000,
			"Necessidades Educacionais Especiais", "/sigaa/verMenuNee.do",
			"menuNee");

	/**
	 * Constante que define o Subsistema de A��es Acad�micas Associadas.
	 */
	public static final SubSistema ACOES_ASSOCIADAS = new SubSistema(25000,
			"A��es Acad�micas Integradas", "/sigaa/projetos/menu.jsf",
			"menuAcoesAssociadas", "/projetos/");

	/**
	 * Constante que define o Subsistema do Programa de Atualiza��o Pedag�gica.
	 */
	public static final SubSistema PROGRAMA_ATUALIZACAO_PEDAGOGICA = new SubSistema(
			26000, "Programa de Atualiza��o Pedag�gica",
			"/sigaa/apedagogica/menu.jsf", "menuProgramaAtualizacaoPedagogica",
			"/apedagogica/");

	/**
	 * Constante que define o Subsistema Ouvidoria.
	 */
	public static final SubSistema OUVIDORIA = new SubSistema(27000,
			"Ouvidoria", "/sigaa/verMenuOuvidoria.do", "menuOuvidoria",
			"/ouvidoria/");

	/**
	 * Constante que define o Portal do Familiar.
	 */
	public static final SubSistema PORTAL_FAMILIAR = new SubSistema(28000,
			"Portal do Familiar", "/sigaa/portais/familiar/index.jsf",
			"menuPortalFamiliar");

	/**
	 * Constante que define o Subsistema de Tradu��o de Documentos pela
	 * Secretaria de Rela��es Internacionais.
	 */
	public static final SubSistema RELACOES_INTERNACIONAIS = new SubSistema(
			29000, "Rela��es Internacionais",
			"/sigaa/verMenuRelacoesInternacionais.do",
			"menuRelacoesInternacionais");

	/** Constante que define o Subsistema de Stricto Sensu. */
	public static final SubSistema ENSINO_REDE = new SubSistema(30000,
			"Programa em Rede", "/sigaa/verMenuEnsinoRede.do", "menuEnsinoRede",
			"/ensino_rede/");
	
	public static final SubSistema PORTAL_ENSINO_REDE = new SubSistema(31000,
			"Portal Programa em Rede", "/sigaa/verMenuEnsinoRede.do", "menuEnsinoRede",
			"/ensino_rede/portal/");
	
	/* ***************************************************************** */
	/* ***************************************************************** */
	/* ******* ATEN��O!!! ******* */
	/* ******* ******* */
	/* ******* AO DECLARAR UM NOVO SUBSISTEMA NESTA CLASSE, ******* */
	/* ******* N�O ESQUECER DE ADICION�-LO, TAMB�M, AO MAPA ******* */
	/* ******* LOGO ABAIXO. ******* */
	/* ***************************************************************** */
	/* ***************************************************************** */

	/** Cole��o de subsistemas definidos para o SIGAA. */
	public static Map<Integer, SubSistema> subsistemas = new TreeMap<Integer, SubSistema>();

	static {
		subsistemas.put(ADMINISTRACAO.getId(), ADMINISTRACAO);
		subsistemas.put(SIGAA.getId(), SIGAA);
		subsistemas.put(INFANTIL.getId(), INFANTIL);
		subsistemas.put(MEDIO.getId(), MEDIO);
		subsistemas.put(TECNICO.getId(), TECNICO);
		subsistemas.put(FORMACAO_COMPLEMENTAR.getId(), FORMACAO_COMPLEMENTAR);
		subsistemas.put(GRADUACAO.getId(), GRADUACAO);
		subsistemas.put(LATO_SENSU.getId(), LATO_SENSU);
		subsistemas.put(STRICTO_SENSU.getId(), STRICTO_SENSU);
		subsistemas.put(PESQUISA.getId(), PESQUISA);
		subsistemas.put(EXTENSAO.getId(), EXTENSAO);
		subsistemas.put(MONITORIA.getId(), MONITORIA);
		subsistemas.put(PROD_INTELECTUAL.getId(), PROD_INTELECTUAL);
		subsistemas.put(SEDIS.getId(), SEDIS);
		subsistemas.put(SAE.getId(), SAE);
		subsistemas.put(SAUDE.getId(), SAUDE);
		subsistemas.put(INFRA_ESTRUTURA_FISICA.getId(), INFRA_ESTRUTURA_FISICA);
		subsistemas.put(PORTAL_AVALIACAO_INSTITUCIONAL.getId(),
				PORTAL_AVALIACAO_INSTITUCIONAL);
		subsistemas.put(PORTAL_DOCENTE.getId(), PORTAL_DOCENTE);
		subsistemas.put(PORTAL_DISCENTE.getId(), PORTAL_DISCENTE);
		subsistemas.put(PORTAL_CONSULTOR.getId(), PORTAL_CONSULTOR);
		subsistemas.put(PORTAL_TURMA.getId(), PORTAL_TURMA);
		subsistemas.put(PORTAL_TUTOR.getId(), PORTAL_TUTOR);
		subsistemas.put(PORTAL_COORDENADOR_POLO.getId(),
				PORTAL_COORDENADOR_POLO);
		subsistemas.put(PORTAL_PLANEJAMENTO.getId(), PORTAL_PLANEJAMENTO);
		subsistemas.put(PORTAL_COORDENADOR.getId(), PORTAL_COORDENADOR);
		subsistemas.put(PORTAL_COORDENADOR_STRICTO.getId(),
				PORTAL_COORDENADOR_STRICTO);
		subsistemas.put(PORTAL_CPDI.getId(), PORTAL_CPDI);
		subsistemas.put(PORTAL_COORDENADOR_LATO.getId(),
				PORTAL_COORDENADOR_LATO);
		subsistemas.put(VESTIBULAR.getId(), VESTIBULAR);
		subsistemas.put(BIBLIOTECA.getId(), BIBLIOTECA);
		subsistemas.put(COMUNIDADE_VIRTUAL.getId(), COMUNIDADE_VIRTUAL);
		subsistemas.put(COMPLEXO_HOSPITALAR.getId(), COMPLEXO_HOSPITALAR);
		subsistemas.put(AMBIENTES_VIRTUAIS.getId(), AMBIENTES_VIRTUAIS);
		subsistemas.put(PROJETOS.getId(), PROJETOS);
		subsistemas.put(CONSULTA.getId(), CONSULTA);
		subsistemas.put(PORTAL_RELATORIOS.getId(), PORTAL_RELATORIOS);
		subsistemas.put(REGISTRO_DIPLOMAS.getId(), REGISTRO_DIPLOMAS);
		subsistemas.put(CONVENIOS_ESTAGIO.getId(), CONVENIOS_ESTAGIO);
		subsistemas.put(PORTAL_CONCEDENTE_ESTAGIO.getId(), PORTAL_CONCEDENTE_ESTAGIO);
		subsistemas.put(NEE.getId(), NEE);
		subsistemas.put(ACOES_ASSOCIADAS.getId(), ACOES_ASSOCIADAS);
		subsistemas.put(PROGRAMA_ATUALIZACAO_PEDAGOGICA.getId(), PROGRAMA_ATUALIZACAO_PEDAGOGICA);
		subsistemas.put(OUVIDORIA.getId(), OUVIDORIA);
		subsistemas.put(PORTAL_FAMILIAR.getId(), PORTAL_FAMILIAR);
		subsistemas.put(SIGAA_MOBILE.getId(), SIGAA_MOBILE);
		subsistemas.put(RELACOES_INTERNACIONAIS.getId(), RELACOES_INTERNACIONAIS);
		subsistemas.put(ENSINO_REDE.getId(), ENSINO_REDE);
	}

	/**
	 * Retorna o n�vel de ensino associado ao subsistema passado como par�metro.
	 * 
	 * @param subSistema
	 * @return
	 */
	public static char getNivelEnsino(SubSistema subSistema) {
		if (subSistema != null) {
			if (subSistema.equals(SigaaSubsistemas.GRADUACAO)
					|| subSistema.equals(SigaaSubsistemas.PORTAL_TURMA)) {
				return NivelEnsino.GRADUACAO;
			} else if (subSistema.equals(SigaaSubsistemas.STRICTO_SENSU)) {
				return NivelEnsino.STRICTO;
			} else if (subSistema.equals(SigaaSubsistemas.TECNICO)) {
				return NivelEnsino.TECNICO;
			} else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU)
					|| subSistema
							.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
				return NivelEnsino.LATO;
			} else if (subSistema.equals(SigaaSubsistemas.INFANTIL)) {
				return NivelEnsino.INFANTIL;
			} else if (subSistema.equals(SigaaSubsistemas.MEDIO)) {
				return NivelEnsino.MEDIO;
			} else if (subSistema.equals(SigaaSubsistemas.SEDIS)
					|| subSistema.equals(SigaaSubsistemas.PORTAL_TUTOR)) {
				return NivelEnsino.GRADUACAO;
			} else if (subSistema.equals(SigaaSubsistemas.COMPLEXO_HOSPITALAR)) {
				return NivelEnsino.RESIDENCIA;
			} else if (subSistema
					.equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)) {
				return NivelEnsino.FORMACAO_COMPLEMENTAR;
			} else if (subSistema
					.equals(SigaaSubsistemas.RELACOES_INTERNACIONAIS)) {
				return NivelEnsino.GRADUACAO;
			}
		}
		return ' ';
	}

	/**
	 * Retorna o tipo de edital de acordo com o subsistema passado como
	 * par�metro.
	 * 
	 * @param subSistema
	 * @return
	 */
	public static char getTipoEdital(SubSistema subSistema) {
		if (subSistema != null) {
			if (subSistema.equals(SigaaSubsistemas.ACOES_ASSOCIADAS)) {
				return Edital.ASSOCIADO;
			} else if (subSistema.equals(SigaaSubsistemas.PESQUISA)) {
				return Edital.PESQUISA;
			} else if (subSistema.equals(SigaaSubsistemas.EXTENSAO)) {
				return Edital.EXTENSAO;
			} else if (subSistema.equals(SigaaSubsistemas.MONITORIA)) {
				return Edital.MONITORIA;
			}
		}
		return ' ';
	}

	/**
	 * Retorna uma descri��o textual para o subsistema indicado.
	 * 
	 * @param subsistema
	 * @return
	 */
	public static String getDescricao(int subsistema) {
		if (subsistemas.containsKey(subsistema)) {
			return subsistemas.get(subsistema).getNome();
		}

		return "Indefinido";
	}

	/**
	 * Retorna a cole��o de subsistemas definidos para o SIGAA
	 * 
	 * @return
	 */
	public Map<Integer, SubSistema> getSubsistemas() {
		return subsistemas;
	}

	/**
	 * Retorna um subsistema definido para o SIGAA de acordo com seu id
	 * 
	 * @return
	 */
	public static SubSistema getSubsistema(int id) {
		if (subsistemas.containsKey(id)) {
			return subsistemas.get(id);
		}

		return null;
	}

}
