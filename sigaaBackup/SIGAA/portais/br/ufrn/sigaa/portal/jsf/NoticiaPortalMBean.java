/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean responsável por carregar as notícias cadastradas para os diferentes portais
 * 
 * @author Leonardo
 */
public class NoticiaPortalMBean extends	SigaaAbstractController<NoticiaPortal> {

	/** Define a quantidade de notícias exibidas em destaque no portal dos discente de pós-graduação.  */
	public static final int QTD_NOTICIAS_CURSO_PROGRAMA = 10;
	/** Define as notícias que serão exibidas no RSS.  */
	private Map<Integer, NoticiaPortal> mapaNoticiasRSS;

	public NoticiaPortalMBean() {
		obj = new NoticiaPortal();
	}

	/** 
	 * Retorna coleção de notícias referente ao portal dos docentes 
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\portais\docente\docente.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> getNoticiasDocente() throws DAOException {
		return carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_DOCENTE);
	}
	
	/** 
	 * Retorna coleção de notícias referente ao portal de Avaliação
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\avaliacao\portal.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> getNoticiasAvaliacao() throws DAOException {
		return carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_AVALIACAO_INSTITUCIONAL);
	}

	/** 
	 * Retorna coleção de notícias referente ao portal dos tutores 
	 * <br>JSP: <ul><li>\SIGAA\app\sigaa.ear\sigaa.war\portais\cpolo\cpolo.jsp</li>
	 * 		<li>\SIGAA\app\sigaa.ear\sigaa.war\portais\tutor\tutor.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<NoticiaPortal> getNoticiasTutor() throws DAOException {
		return carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_TUTOR);
	}

	/**
	 * Retorna uma coleção de notícias referente aos discentes
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
		
		// notícias do curso
		if (getDiscenteUsuario() != null && getDiscenteUsuario().getCurso() != null)
			noticiasDoCurso = carregarNoticiasDoCurso(getDiscenteUsuario().getCurso().getId(), QTD_NOTICIAS_CURSO_PROGRAMA);
		
		// Notícias do programa
		if (getDiscenteUsuario() != null && getDiscenteUsuario().isStricto())
			noticiasDoprograma = carregarNoticiasDoPrograma(getDiscenteUsuario().getGestoraAcademica().getId(), QTD_NOTICIAS_CURSO_PROGRAMA);			
		
		if (noticiasDoCurso != null)
			noticias.addAll(noticiasDoCurso);
		
		if (noticiasDoprograma != null)
			noticias.addAll(noticiasDoprograma);

		// Notícias geral para todos os tipos de alunos
		noticias.addAll( carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_DISCENTE) );
		
		return ordenaNoticias( noticias );
	}

	/**
	 * Retorna uma coleção de notícias referente a coordenação de graduação
	 * <br>JSP: \SIGAA\app\sigaa.ear\sigaa.war\graduacao\coordenador.jsp
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Collection<NoticiaPortal> getNoticiasCoordenadorGraduacao() throws DAOException, IllegalArgumentException,  IOException {

		Collection<NoticiaPortal> noticias = new ArrayList<NoticiaPortal>();
		noticias = carregarNoticias(LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_GRADUACAO);
		
		//Carrega somente as 4(quatro) primeiras notícia associadas ao curso da coordenação.
		Collection<NoticiaPortal> noticiasDoCurso = null;
		if (getCursoAtualCoordenacao() != null)
			noticiasDoCurso = carregarNoticiasDoCurso(getCursoAtualCoordenacao().getId(), 4);
		if (noticiasDoCurso != null)
			noticias.addAll(noticiasDoCurso);
		
		return ordenaNoticias( noticias );
	}
	
	/**
	 * Retorna uma coleção de notícias referente a coordenação de um programa
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
	 * Retorna uma coleção de notícias referente a coordenação de um programa
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
	 * Retorna uma coleção de notícias referente a coordenação de Latos
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
	 * Retorna uma coleção de notícias
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
	 * Método invocado pela componente da view que contém as notícias.
	 * Ao se clicar na notícia, usa-se o ID da notícia para busca-lá no 
	 * banco e exibi-la em um pop-up maior.
	 * 
	 * Caso a notícia venha do RSS, o ID não vai existir no banco,
	 * por isso a busca vai ser realizada dentro de uma Mapa que contém
	 * as notícias RSS.
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
			if (noticiaPortal == null) { //caso a notícia clicada seja RSS, noticiaPortal vai ser null
				noticiaPortal = mapaNoticiasRSS.get(id);
			}
			
		} finally {
			dao.close();
		}
		return noticiaPortal;
	}

	/**
	 * Esse método é responsável por carregar as notícias do portal passado como parâmetro e a quantidade. 
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
	 * Esse método é responsável por carregar as notícias do portal passado como parâmetro e a quantidade. 
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
	 * Esse método é responsável por carregar as notícias do curso passado como parâmetro e a quantidade.
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
	 * Esse método é responsável por carregar as notícias do programa passado como 
	 * parâmetro e a quantidade.
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
	 * Esse método é responsável por carregar as notícias do DadosCursoRede passado como 
	 * parâmetro e a quantidade.
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
	 * Reordena as notícias pela ordem descrescente de cadastro.
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