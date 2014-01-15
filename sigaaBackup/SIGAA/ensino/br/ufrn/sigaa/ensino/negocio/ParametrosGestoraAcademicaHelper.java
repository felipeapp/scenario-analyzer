/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 18/10/2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosInfantil;
import br.ufrn.sigaa.parametros.dominio.ParametrosMedio;

/**
 * Classe de Helper para m�todos de par�metros de gestora acad�mica.
 * 
 * @author Andre Dantas
 *
 */
public class ParametrosGestoraAcademicaHelper {

	/** Unidade referente a unidade Global da Institui��o. */
	private final static Unidade UNIDADE_DIREITO_GLOBAL = new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL);

	/**
	 * 
	 * Pega os par�metros da gestora de acordo com o discente.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametros(DiscenteAdapter discente) throws DAOException {
		return getParametros(discente.getGestoraAcademica(), discente.getNivel(), null, null, discente.getCurso());
	}

	
	/**
	 * 
	 * Pega os par�metros de um curso. Utilizado por exemplo em conv�nios.
	 * 
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametros(Curso c) throws DAOException  {
		GenericDAO dao = getDAO();
		try {
			dao.initialize(c);
			return getParametros(c.getUnidade().getGestoraAcademica(), null, null, null, c);
		} finally {
			dao.close();
		}
	}

	
	/**
	 * 
	 * Pega os par�metros da unidade gestora acad�mica da unidade do usu�rio de acordo com n�vel do usu�rio.
	 * 
	 * @param u
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametros(Usuario u) throws DAOException {
		UnidadeGeral unidade = u.getVinculoAtivo().getUnidade();
		if (unidade.getGestoraAcademica() != null)
			unidade = unidade.getGestoraAcademica();
		return getParametros((Unidade)unidade, u.getNivelEnsino(), null, null, null);
	}

	/**
	 * 
	 * Pega os par�metros da gestora global para um n�vel de ensino espec�fico.
	 * 
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosNivelEnsino(char nivelEnsino) throws DAOException {
		switch (nivelEnsino) {
		case NivelEnsino.GRADUACAO:
			return getParametrosUnidadeGlobalGraduacao();
		case NivelEnsino.STRICTO:
			return getParametrosUnidadeGlobalStricto();
		case NivelEnsino.MESTRADO:
			return getParametrosUnidadeGlobalStricto();
		case NivelEnsino.DOUTORADO:
			return getParametrosUnidadeGlobalStricto();
		case NivelEnsino.LATO:
			return getParametrosUnidadeGlobalLato();
		case NivelEnsino.INFANTIL:
			return getParametrosInfantil();
		case NivelEnsino.RESIDENCIA:
			return getParametrosResidencia();
		case NivelEnsino.MEDIO:
			return getParametrosMedio();			
		default:
			return null;
		}
	}
	
	
	/**
	 * 
	 * Pega os par�metros da unidade global e n�vel de ensino gradua��o.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosUnidadeGlobalGraduacao() throws DAOException {
		return getParametros(UNIDADE_DIREITO_GLOBAL, NivelEnsino.GRADUACAO, null, null, null);
	}

	/**
	 * 
	 * Pega os par�metros da unidade global e n�vel de ensino LATO.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosUnidadeGlobalLato() throws DAOException {
		return getParametros(UNIDADE_DIREITO_GLOBAL, NivelEnsino.LATO, null, null, null);
	}
	
	/**
	 * 
	 * Pega os par�metros da unidade global e n�vel de ensino RESIDENCIA
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosResidencia() throws DAOException {
		return getParametros(UNIDADE_DIREITO_GLOBAL, NivelEnsino.RESIDENCIA, null, null, null);
	}
	
	
	/**
	 * 
	 * Pega os par�metros do n�cleo de educa��o infantil.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosInfantil() throws DAOException {
	    return getParametros(
	            new Unidade(ParametroHelper.getInstance().getParametroInt(ParametrosInfantil.ID_NEI)), 
	            NivelEnsino.INFANTIL, null, null, null);
	}
	
	/**
	 * 
	 * Pega os par�metros do n�vel m�dio.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosMedio() throws DAOException {
		return getParametros(new Unidade(ParametroHelper.getInstance().getParametroInt(ParametrosMedio.ID_UNIDADE_GERAL_MEDIO)), 
				NivelEnsino.MEDIO, null, null, null);
	}	

	
	/**
	 * 
	 * Pega os par�metros da unidade global para o n�vel de ensino STRICTO
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosUnidadeGlobalStricto() throws DAOException {
		return getParametros(UNIDADE_DIREITO_GLOBAL, NivelEnsino.STRICTO, null, null, null);
	}

	/**
	 * 
	 * Pega os par�metros da unidade global para o n�vel de ensino GRADUA��O e modalidade EAD.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametrosEAD() throws DAOException {
		return getParametros(UNIDADE_DIREITO_GLOBAL, NivelEnsino.GRADUACAO, new ModalidadeEducacao(ModalidadeEducacao.A_DISTANCIA), null, null);
	}

	
	/**
	 * 
	 * Retorna os par�metros da gestora acad�mida de acordo com um componente curricular.
	 * 
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametros(ComponenteCurricular c) throws DAOException {
		GenericDAO dao = getDAO();
		try {
			c.setUnidade(dao.findByPrimaryKey(c.getUnidade().getId(), Unidade.class));
			return getParametros(c.getUnidade().getGestoraAcademica(), c.getNivel(), null, null, null);
		} finally {
			dao.close();
		}
	}

	
	/**
	 * 
	 * Retorna os par�metros da gestora acad�mida de acordo com uma turma
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametros(Turma turma) throws DAOException {
		return getParametros(turma, null);
	}

	
	/**
	 * 
	 * Retorna os par�metros da gestora acad�mida de acordo com uma turma e unidade gestora.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosGestoraAcademica getParametros(Turma turma, Unidade gestoraAcademica) throws DAOException {
		char nivel = turma.getDisciplina().getNivel();
		ModalidadeEducacao mod = null;
		if (turma.getPolo() != null)
			mod = new ModalidadeEducacao( ModalidadeEducacao.A_DISTANCIA);
		Unidade un = gestoraAcademica;
		if (un == null) {
			if ( (turma.isTecnico() || turma.isFormacaoComplementar()) 
					&& (turma.getDisciplina().getUnidade().isUnidadeAcademicaEspecializada())) {
				un = turma.getDisciplina().getUnidade();
			} else {
				un = turma.getDisciplina().getUnidade().getGestoraAcademica();
			}
		}
		
		ConvenioAcademico conv = null;
		if (turma.getCurso() != null && nivel != NivelEnsino.LATO && nivel != NivelEnsino.MEDIO) {
			conv = turma.getCurso().getConvenio();
			return getParametros(un, nivel, mod, conv, null);
		} 
		else if (turma.getCurso() != null && nivel == 'L') 
		   return getParametros(un, nivel, mod, conv, turma.getCurso());
		
		if(MetropoleDigitalHelper.isMetropoleDigital(turma)){
			un = turma.getDisciplina().getUnidade();
			return getParametros(un, nivel, mod, conv, MetropoleDigitalHelper.cursoMetropoleDigital(un));
		}
		
		return getParametros(un, nivel, mod, conv, null);
	}

	/**
	 * * Carregando par�metros e calend�rio acad�mico de acordo com o n�vel de ensino:
	 * GRADUA��O
	 * 		- regular : ufrn - grad - presencial
	 *  	- ead: tenta carregar do curso, se n�o encontrar carrega um geral do  EAD - grad
	 * 	 	- probasica: curso
	 * T�CNICO
	 * 		- da gestora acad�mica
	 * LATO
	 * 		- ufrn - lato
	 * DOUTORADO ou MESTRADO
	 *		- tenta carregar do programa, caso n�o encontre, carrega um geral do n�vel de ensino
	 */
	public static ParametrosGestoraAcademica getParametros(Unidade gestoraAcademica, Character nivel,
			ModalidadeEducacao modalidade, ConvenioAcademico convenio, Curso curso) throws DAOException {
		if (nivel == null && curso != null)
			nivel = curso.getNivel();
		if (NivelEnsino.isAlgumNivelStricto(nivel))
			nivel = NivelEnsino.STRICTO;
		if (modalidade == null && curso != null && curso.isADistancia())
			modalidade = curso.getModalidadeEducacao();
		if (convenio == null && curso != null)
			convenio = curso.getConvenio();

		ParametrosGestoraAcademicaDao pdao =  getDAO();
		try {
			ParametrosGestoraAcademica param = null;
			if (curso != null)
				param = pdao.findByCurso(curso);
			if (param == null) {
				if (nivel != null && nivel.charValue() == NivelEnsino.GRADUACAO) {
					if ( modalidade != null && modalidade.isADistancia()) {
						// tenta do curso, se n�o der carrega um geral do EAD
						param = pdao.findByModalidade(UNIDADE_DIREITO_GLOBAL, nivel, modalidade);
					} else if ( convenio != null && convenio.isProBasica()) {
						param = pdao.findByConvenio(UNIDADE_DIREITO_GLOBAL, nivel, convenio);
					}
					if (param == null)
						param = pdao.findByUnidade(UNIDADE_DIREITO_GLOBAL.getId(), NivelEnsino.GRADUACAO);

				} else if(nivel != null && nivel.charValue() == NivelEnsino.LATO) {
					// no Lato as informa��es de par�metros est�o na proposta do curso
					if(curso != null){
						CursoLato cursoLato = pdao.findByPrimaryKey(curso.getId(), CursoLato.class);
						param =  new ParametrosGestoraAcademica();
						param.setMetodoAvaliacao(cursoLato.getPropostaCurso().getMetodoAvaliacao());
						param.setMediaMinimaAprovacao(cursoLato.getPropostaCurso().getMediaMinimaAprovacao());
						param.setFrequenciaMinima(cursoLato.getPropostaCurso().getFreqObrigatoria());
						param.setPesoMediaRecuperacao("10,10");
						param.setUnidade( new Unidade(Unidade.UNIDADE_DIREITO_GLOBAL) );
						param.setNivel(NivelEnsino.LATO);
						param.setCurso(cursoLato);
						param.setHorasCreditosAula((short)15);
						param.setHorasCreditosEstagio((short) 45);
						param.setHorasCreditosLaboratorio((short) 15);
						param.setPesosAvaliacoes("1,1,1");
						param.setQtdAvaliacoes((short) 1);
						param.setMinutosAulaRegular(60);
					} else
						param = pdao.findByUnidade(UNIDADE_DIREITO_GLOBAL.getId(), NivelEnsino.LATO);
				} else if (nivel != null && nivel.charValue() == NivelEnsino.TECNICO){
					if (gestoraAcademica != null)
						param = pdao.findByUnidade(gestoraAcademica.getId(), nivel);
				} else  {
					// tenta da gestora, se n�o der carrega um geral do n�vel de ensino.
					if (gestoraAcademica != null) {
						param = pdao.findByUnidade(gestoraAcademica.getId(), nivel);
					}
					if (param == null)
						param = pdao.findByUnidade(UNIDADE_DIREITO_GLOBAL.getId(), nivel);
				}

				// se ainda assim estiver nulo, carrega os par�metros da gradua��o
				// (acontece enquanto existirem alunos sem gestora acad�mica)
				if (param == null)
					param = pdao.findByUnidade(UNIDADE_DIREITO_GLOBAL.getId(), NivelEnsino.GRADUACAO);
			}


			return param;
		} finally {
			pdao.close();
		}
	}

	/**  Retorna uma inst�ncia de um DAO com a sess�o setada para os par�metros de gestora acad�mica. */
	private static ParametrosGestoraAcademicaDao getDAO() throws DAOException {
		return DAOFactory.getInstance().getDAO(ParametrosGestoraAcademicaDao.class, null, null);
	}

}
