/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 12/11/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;


import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dao.NoticiaPortalDAO;
import br.ufrn.comum.dominio.LocalizacaoNoticiaPortal;
import br.ufrn.comum.dominio.NoticiaPortal;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.portal.dao.NoticiaPortalAcademicoDAO;


/**
 * MBean respons�vel por carregar as not�cias cadastradas para os diferentes portais
 * 
 * @author Leonardo
 */
public class NoticiaPortalMBean extends	SigaaAbstractController<NoticiaPortal> {

	/** Define a quantidade de not�cias exibidas em destaque no portal dos discente de p�s-gradua��o.  */
	public static final int QTD_NOTICIAS_CURSO_PROGRAMA = 10;
	/** Define as not�cias que ser�o exibidas no RSS.  */
	private Map<Integer, NoticiaPortal> mapaNoticiasRSS;

	public NoticiaPortalMBean() {
		obj = new NoticiaPortal();
	}

	/** 
	 * Retorna cole��o de not�cias referente ao portal dos docentes 
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\portais\docente\docente.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> getNoticiasDocente() throws DAOException {
		return carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_DOCENTE);
	}
	
	/** 
	 * Retorna cole��o de not�cias referente ao portal de Avalia��o
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\avaliacao\portal.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> getNoticiasAvaliacao() throws DAOException {
		return carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_AVALIACAO_INSTITUCIONAL);
	}

	/** 
	 * Retorna cole��o de not�cias referente ao portal dos tutores 
	 * <br>JSP: <ul><li>\SIGAA\app\sigaa.ear\sigaa.war\portais\cpolo\cpolo.jsp</li>
	 * 		<li>\SIGAA\app\sigaa.ear\sigaa.war\portais\tutor\tutor.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> getNoticiasTutor() throws DAOException {
		return carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_TUTOR);
	}

	/**
	 * Retorna uma cole��o de not�cias referente aos discentes
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\portais\discente\discente.jsp
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Collection<NoticiaPortal> getNoticiasDiscente() throws DAOException, IllegalArgumentException, IOException {
		Collection<NoticiaPortal> noticias = new ArrayList<NoticiaPortal>();
		Collection<NoticiaPortal> noticiasDoCurso = null;
		Collection<NoticiaPortal> noticiasDoprograma = null;
		
		// not�cias do curso
		if (getDiscenteUsuario() != null && getDiscenteUsuario().getCurso() != null)
			noticiasDoCurso = carregarNoticiasDoCurso(getDiscenteUsuario().getCurso().getId(), QTD_NOTICIAS_CURSO_PROGRAMA);
		
		// Not�cias do programa
		if (getDiscenteUsuario() != null && getDiscenteUsuario().isStricto())
			noticiasDoprograma = carregarNoticiasDoPrograma(getDiscenteUsuario().getGestoraAcademica().getId(), QTD_NOTICIAS_CURSO_PROGRAMA);			
		
		if (noticiasDoCurso != null)
			noticias.addAll(noticiasDoCurso);
		
		if (noticiasDoprograma != null)
			noticias.addAll(noticiasDoprograma);

		// Not�cias geral para todos os tipos de alunos
		noticias.addAll( carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_DISCENTE) );
		
		return ordenaNoticias( noticias );
	}

	/**
	 * Retorna uma cole��o de not�cias referente a coordena��o de gradua��o
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\graduacao\coordenador.jsp
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Collection<NoticiaPortal> getNoticiasCoordenadorGraduacao() throws DAOException, IllegalArgumentException,  IOException {

		Collection<NoticiaPortal> noticias = new ArrayList<NoticiaPortal>();
		noticias = carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_GRADUACAO);
		
		//Carrega somente as 4(quatro) primeiras not�cia associadas ao curso da coordena��o.
		Collection<NoticiaPortal> noticiasDoCurso = null;
		if (getCursoAtualCoordenacao() != null)
			noticiasDoCurso = carregarNoticiasDoCurso(getCursoAtualCoordenacao().getId(), 4);
		if (noticiasDoCurso != null)
			noticias.addAll(noticiasDoCurso);
		
		return ordenaNoticias( noticias );
	}
	
	/**
	 * Retorna uma cole��o de not�cias referente a coordena��o de um programa
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\stricto\coordenacao.jsp
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Collection<NoticiaPortal> getNoticiasCoordenadorStricto() throws DAOException, IllegalArgumentException,  IOException {
		Collection<NoticiaPortal> noticias = new ArrayList<NoticiaPortal>();
		Collection<NoticiaPortal> noticiasDoPrograma = null;
		
		noticias = carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_STRICTO);
		
		if (getProgramaStricto() != null)
			noticiasDoPrograma = carregarNoticiasDoPrograma(getProgramaStricto().getId(), 4);
		
		if (noticiasDoPrograma != null)
			noticias.addAll(noticiasDoPrograma);
		
		return ordenaNoticias( noticias );
	}
	
	/**
	 * Retorna uma cole��o de not�cias referente a coordena��o de um programa
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\ensino_rede\portal\portal.jsp
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Collection<NoticiaPortal> getNoticiasProgramaRede() throws DAOException, IllegalArgumentException,  IOException {
		Collection<NoticiaPortal> noticias = new ArrayList<NoticiaPortal>();
		Collection<NoticiaPortal> noticiasDoProgramaRede = null;
		
		noticias = carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_PROGRAMA_REDE);
		
		TipoVinculoCoordenadorUnidadeRede tipoVinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		DadosCursoRede dadosCurso = tipoVinculo.getCoordenacao().getDadosCurso();
		
		if (dadosCurso != null)
			noticiasDoProgramaRede = carregarNoticiasDoProgramaRede(dadosCurso.getId(), 4);
		
		if (noticiasDoProgramaRede != null)
			noticias.addAll(noticiasDoProgramaRede);
		
		return ordenaNoticias( noticias );
	}

	/**
	 * Retorna uma cole��o de not�cias referente a coordena��o de Latos
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\lato\coordenador.jsp
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Collection<NoticiaPortal> getNoticiasCoordenadorLato() throws DAOException, IllegalArgumentException,  IOException {
		Collection<NoticiaPortal> noticias = new ArrayList<NoticiaPortal>();
		noticias = carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_LATO);
		
		return noticias;
	}

	/**
	 * Retorna uma cole��o de not�cias
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\public\home.jsp
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Collection<NoticiaPortal> getNoticiasPortalPublico() throws DAOException, IllegalArgumentException,  IOException {
		return carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_PUBLICO_SIGAA, 15);
	}

	/**
	 * M�todo invocado pela componente da view que cont�m as not�cias.
	 * Ao se clicar na not�cia, usa-se o ID da not�cia para busca-l� no 
	 * banco e exibi-la em um pop-up maior.
	 * 
	 * Caso a not�cia venha do RSS, o ID n�o vai existir no banco,
	 * por isso a busca vai ser realizada dentro de uma Mapa que cont�m
	 * as not�cias RSS.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException

	 * @throws IOException
	 */
	public NoticiaPortal getNoticiaRequest() throws DAOException, IllegalArgumentException, IOException {
		int id = getParameterInt("id");
		NoticiaPortalAcademicoDAO dao = getDAO(NoticiaPortalAcademicoDAO.class, Sistema.COMUM);
		NoticiaPortal noticiaPortal = dao.findByPrimaryKey(id, NoticiaPortal.class);
		
		try {
			if (noticiaPortal == null) { //caso a not�cia clicada seja RSS, noticiaPortal vai ser null
				noticiaPortal = mapaNoticiasRSS.get(id);
			}
			
		} finally {
			dao.close();
		}
		return noticiaPortal;
	}

	/**
	 * Esse m�todo � respons�vel por carregar as not�cias do portal passado como par�metro e a quantidade. 
	 * 
	 * @param portal
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 * @Deprecated
	 * 
	 */
	private Collection<NoticiaPortal> carregarNoticias(String portal, int quantidade) throws DAOException {
		NoticiaPortalDAO dao = getDAO(NoticiaPortalDAO.class, Sistema.COMUM);
		try {
			return dao.findPublicadasByLocalizacao(portal, quantidade);
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Esse m�todo � respons�vel por carregar as not�cias do portal passado como par�metro e a quantidade. 
	 * 
	 * @param portal
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 * 
	 * 
	 */
	private Collection<NoticiaPortal> carregarNoticias(String portal) throws DAOException {
		NoticiaPortalAcademicoDAO dao = getDAO(NoticiaPortalAcademicoDAO.class);
		try {
			return dao.findPublicadasByLocalizacao(portal);
		} finally {
			dao.close();
		}
	}
	
	/** 
	 * Esse m�todo � respons�vel por carregar as not�cias do curso passado como par�metro e a quantidade.
	 * 
	 * @param idCurso
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 */
	private Collection<NoticiaPortal> carregarNoticiasDoCurso(int idCurso, int quantidade) throws DAOException {
		NoticiaPortalAcademicoDAO dao = getDAO(NoticiaPortalAcademicoDAO.class);
		try {
			return dao.findPublicadasByCurso(idCurso, quantidade);
		} finally {
			dao.close();
		}
	}

	/***
	 * Esse m�todo � respons�vel por carregar as not�cias do programa passado como 
	 * par�metro e a quantidade.
	 * 
	 * @param idPrograma
	 * @param quantidade
	 * @return
	 */
	private Collection<NoticiaPortal> carregarNoticiasDoPrograma(int idPrograma, int quantidade) {
		NoticiaPortalAcademicoDAO dao = getDAO(NoticiaPortalAcademicoDAO.class);
		try {
			return dao.findPublicadasByPrograma(idPrograma, quantidade);
		} finally {
			dao.close();
		}
	}
	
	/***
	 * Esse m�todo � respons�vel por carregar as not�cias do DadosCursoRede passado como 
	 * par�metro e a quantidade.
	 * 
	 * @param idDadosCursoRede
	 * @param quantidade
	 * @return
	 */
	private Collection<NoticiaPortal> carregarNoticiasDoProgramaRede(int idDadosCursoRede, int quantidade) {
		NoticiaPortalAcademicoDAO dao = getDAO(NoticiaPortalAcademicoDAO.class);
		try {
			return dao.findPublicadasByProgramaRede(idDadosCursoRede, quantidade);
		} finally {
			dao.close();
		}
	}	
	
	/**
	 * Reordena as not�cias pela ordem descrescente de cadastro.
	 * @param noticias
	 * @return
	 */
	private Collection<NoticiaPortal> ordenaNoticias( Collection<NoticiaPortal> noticias ){
		
		if( isAllEmpty(noticias) )
			return Collections.EMPTY_LIST;
			
		Collections.sort((List<NoticiaPortal>) noticias, new Comparator<NoticiaPortal>(){
			public int compare(NoticiaPortal n1, NoticiaPortal n2) {
				return n2.getCriadoEm().compareTo( n1.getCriadoEm() );
			}
		});
		
		return noticias;
		
	}
	
}