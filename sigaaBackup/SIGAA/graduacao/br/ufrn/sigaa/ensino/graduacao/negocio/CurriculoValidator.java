/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/02/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/** Classw utilizada para validar todas regras de neg�cio referente � Curr�culos.
 * @author �dipo Elder F. de Melo
 *
 */
public class CurriculoValidator {

	/** Valida o curr�culo (dados gerais e lista de componentes)
	 * @param curriculo
	 * @param estruturaCurriculardao
	 * @param componenteCurriculardao
	 * @return
	 * @throws DAOException
	 */
	public static ListaMensagens validacaoGeral(Curriculo curriculo, EstruturaCurricularDao estruturaCurriculardao, ComponenteCurricularDao componenteCurriculardao) throws DAOException {
		ListaMensagens lista = new ListaMensagens();
		// valida��o dos dados gerais do curr�culo
		lista.addAll(validaDadosGerais(curriculo, estruturaCurriculardao));
		
		// valida��o dos componentes do curr�culo de gradua��o
		if (curriculo.isGraduacao())
			lista.addAll(validaComponentesGraduacao(componenteCurriculardao, curriculo));
		else {
			if (curriculo.isResidencia()){
				if(curriculo.getChOptativasMinima() == null)
					curriculo.setChOptativasMinima(new Integer(0));
				if(curriculo.getChMinimaSemestre() == null)
					curriculo.setChMinimaSemestre(new Integer(0));
			}
			if (curriculo.getCurriculoComponentes() == null || curriculo.getCurriculoComponentes().isEmpty()) {
				lista.addErro("Adicione pelo menos um componente curricular");
			}
		}
		return lista;
	}
	
	/** Valida apenas os dados gerais do curr�culo, n�o inclui a valida��o da lista de componentes curriculares.
	 * @param curriculo
	 * @param estruturaCurricularDao
	 * @return
	 * @throws DAOException
	 */
	public static ListaMensagens validaDadosGerais(Curriculo curriculo, EstruturaCurricularDao estruturaCurricularDao) throws DAOException {
		ListaMensagens lista = new ListaMensagens();
		lista.addAll(curriculo.validate());
		ParametrosGestoraAcademica paramCurso = ParametrosGestoraAcademicaHelper.getParametros(curriculo.getCurso());
		//Se o curso tiver parametros definidos para a unidade gestora academica da unidade do curso e 
		//esses valores estiverem definidos, usamos estes valores.
		//Caso contr�rio utilizamos os valores definidos globalmente para o n�vel de ensino.
		if(!( ValidatorUtil.isEmpty(paramCurso.getMinCreditosExtra())
				|| ValidatorUtil.isEmpty(paramCurso.getMaxCreditosExtra())
				|| ValidatorUtil.isEmpty(paramCurso.getHorasCreditosAula()) ) ) {
			paramCurso = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(curriculo.getCurso().getNivel());
		}
		// recupera o m�nimo e m�ximo de componentes eletivos
		int minComponenteEletivo = 0;
		int maxComponenteEletivo = 0;
		if(!ValidatorUtil.isEmpty(paramCurso.getMinCreditosExtra()) && !ValidatorUtil.isEmpty(paramCurso.getMaxCreditosExtra())
				&& !ValidatorUtil.isEmpty(paramCurso.getHorasCreditosAula()) ) {
			minComponenteEletivo = paramCurso.getMinCreditosExtra() * paramCurso.getHorasCreditosAula();
			maxComponenteEletivo = paramCurso.getMaxCreditosExtra() * paramCurso.getHorasCreditosAula();
		}
		if (curriculo.isGraduacao()){
			if (curriculo.getMaxEletivos() < minComponenteEletivo || curriculo.getMaxEletivos() > maxComponenteEletivo){
				lista.addErro("O valor m�ximo de componentes eletivos deve estar entre "+minComponenteEletivo +" e "+ maxComponenteEletivo);
			}			
		}
		
		if(!curriculo.isResidencia()){
			if(curriculo.getCrMinimoSemestre() <= 0){
				lista.addErro("Os Cr�ditos M�nimos Por Per�odo Letivo n�o podem ser menores ou iguais a zero");
			}
			
			if(curriculo.getCrIdealSemestre() <= 0){
				lista.addErro("Os Cr�ditos M�dios Por Per�odo Letivo n�o podem ser menores ou iguais a zero");
			}
			
			if(curriculo.getCrMaximoSemestre() <= 0){
				lista.addErro("Os Cr�ditos M�ximos Por Per�odo Letivo n�o podem ser menores ou iguais a zero");
			}
		}
		
		if(curriculo.getSemestreConclusaoMinimo() != null){
			if(curriculo.getSemestreConclusaoMinimo() <= 0)
				lista.addErro("O Prazo M�nimo Para Conclus�o n�o pode ser menor ou igual a zero");
		}
		if(curriculo.getSemestreConclusaoIdeal() != null){
			if(curriculo.getSemestreConclusaoIdeal() <= 0)
				lista.addErro("O Prazo M�dio Para Conclus�o n�o pode ser menor ou igual a zero");
		}
		if(curriculo.getSemestreConclusaoMaximo() != null){
			if(curriculo.getSemestreConclusaoMaximo() <= 0)
				lista.addErro("O Prazo M�ximo Para Conclus�o n�o pode ser menor ou igual a zero");
		}
		
		Curso curso = curriculo.getCurso();
		if (curriculo.getCurso() != null && curriculo.getCurso().getId() > 0) {
			curso = estruturaCurricularDao.findByPrimaryKey(curriculo.getCurso().getId(), Curso.class);
		}

		if (estruturaCurricularDao.existeCodigo(curriculo, curso.getNivel())) {
			lista.addErro("J� existe um curr�culo com esse c�digo");
		}
		
		return lista;
	}

	/** Valida a lista de componentes curriculares de gradua��o.
	 * @param dao
	 * @param curriculo
	 * @return
	 */
	public static ListaMensagens validaComponentesGraduacao(ComponenteCurricularDao dao, Curriculo curriculo) {
		ListaMensagens lista = new ListaMensagens();
		if (curriculo.isGraduacao()) {
			for (CurriculoComponente cc : curriculo.getCurriculoComponentes()) {
				lista.addAll(cc.validate(curriculo.getCurso().getNivel()));
				
			}
		}
		// cria uma c�pia do curr�culo e simula a inser��o dos componentes um a um
		try {
			Curriculo curriculoTeste = (Curriculo) BeanUtils.cloneBean(curriculo);
			curriculoTeste.setCurriculoComponentes(new ArrayList<CurriculoComponente>());
			for (CurriculoComponente cc : curriculo.getCurriculoComponentes()) {
				ListaMensagens erros = validaAdicaoComponenteGraduacao(curriculoTeste, cc.getComponente(), cc.getSemestreOferta(), cc.getObrigatoria(), dao);
				if (erros.isErrorPresent()) {
					lista.addAll(erros);
					break;
				} else {
					curriculoTeste.getCurriculoComponentes().add(cc);
					Collections.sort(curriculoTeste.getCurriculoComponentes());
				}
			}
		} catch (Exception e) {
			lista.addErro("Um erro ocorreu durante a valida��o do curr�culo. "
					+ "Por favor, contacte o suporte atrav�s do \"Abrir Chamado\".");
		}
		return lista;
	}
	
	/** Indica se na altera��o da estrutura curricular pode alterar a carga
	 * hor�ria optativa e os componentes obrigat�rias. Somente ser� a lterado se
	 * n�o houver nenhum aluno vinculado a estrutura.
	 * @param curriculo
	 * @param usuario
	 * @param estruturaCurricularDao
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPodeAlterarCHEObrigatoria(Curriculo curriculo, Usuario usuario, EstruturaCurricularDao estruturaCurricularDao) throws DAOException {
		boolean podeAlterarChEObrigatorias = true;
		if (usuario.isUserInRole(SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO)) {
			podeAlterarChEObrigatorias = true;
		} else {
			if (curriculo.getId() > 0
					&& !usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP)) {
				int totalDiscentes = estruturaCurricularDao.countDiscentesByCurriculo(curriculo.getId());
				/**
				 * se a opera��o � alterar curr�culo e existe alguma
				 * discente vinculado a este curr�culo n�o pode alterar ch
				 * optativa nem as componentes obrigat�rias
				 */
				if (totalDiscentes > 0)
					podeAlterarChEObrigatorias = false;
			}
			if (!podeAlterarChEObrigatorias) {
				podeAlterarChEObrigatorias = curriculo.isAberto();
			}
		}
		return podeAlterarChEObrigatorias;
	}

	/** Valida se o componente curricular indicado pode ser adicionado � um curr�culo de resid�ncia m�dica.
	 * @param curriculo
	 * @param curriculoComponente
	 * @return
	 */
	public static ListaMensagens validaAdicaoComponenteResidenciaMedica(Curriculo curriculo, CurriculoComponente curriculoComponente) {
		ListaMensagens lista = new ListaMensagens();
		// j� existe o componente no curr�culo?
		for (CurriculoComponente cc : curriculo.getCurriculoComponentes()){
			if (curriculoComponente.equals(cc) && (cc.getAreaConcentracao() == null || cc.getAreaConcentracao().equals(curriculoComponente.getAreaConcentracao()))){
				lista.addErro("O componente " + curriculoComponente.getComponente().getCodigoNome() + " j� foi adicionado ao curr�culo");
				break;
			}
		}		
		return lista;
	}

	/** Valida se o componente curricular indicado pode ser adicionado � um curr�culo de strictu sensu.
	 * @param curriculo
	 * @param componente
	 * @param semestre
	 * @param obrigatorio
	 * @param componenteCurricularDao
	 * @return
	 */
	public static ListaMensagens validaAdicaoComponenteStrictoSensu(Curriculo curriculo, ComponenteCurricular componente, int semestre, boolean obrigatorio, ComponenteCurricularDao componenteCurricularDao) {
		ListaMensagens lista = new ListaMensagens();
		// j� existe o componente no curr�culo?
		if (isExisteComponenteNoCurriculo(curriculo, componente))
			lista.addErro("O componente " + componente.getCodigoNome() + " j� foi adicionado ao curr�culo");
		return lista;
	}

	/** Valida se o componente curricular indicado pode ser adicionado � um curr�culo de gradua��o.
	 * @param curriculo
	 * @param componente
	 * @param semestre
	 * @param obrigatorio
	 * @param componenteCurricularDao
	 * @return
	 * @throws ArqException
	 */
	public static ListaMensagens validaAdicaoComponenteGraduacao(Curriculo curriculo, ComponenteCurricular componente, int semestre, boolean obrigatorio, ComponenteCurricularDao componenteCurricularDao) throws ArqException {
		ListaMensagens lista = new ListaMensagens();
		// j� existe o componente no curr�culo?
		if (isExisteComponenteNoCurriculo(curriculo, componente))
			lista.addErro("O componente " + componente.getCodigoNome() + " j� foi adicionado ao curr�culo");
		
		CurriculoComponente ccc = new CurriculoComponente();
		ccc.setCurriculo(curriculo);
		ccc.setSemestreOferta(semestre);
		ccc.setObrigatoria(obrigatorio);
		ccc.setComponente(componente);
		
		// Analisar equival�ncias
		String equivalencia = componente.getEquivalencia();
		Map<String, Object> componenteInvalido = new HashMap<String, Object>();
		if (!isEmpty(equivalencia)) {
			if (!verificaExpressao(componenteInvalido, equivalencia, componenteCurricularDao, "equivalencia", null))
				lista.addErro("O componente curricular " + componente.getDescricao() + " possui express�o de equival�ncia mal formada.");
		}
		
		// Analisar pr�-requisitos
		String preRequisito = componente.getPreRequisito();
		if (!isEmpty(preRequisito)) {
			if (!verificaExpressao(componenteInvalido, preRequisito, componenteCurricularDao, "preRequisito", null))
				lista.addErro("O componente curricular " + componente.getDescricao() + " possui express�o de pr�-requisito mal formada.");
		}
		
		String coRequisito = componente.getCoRequisito();
		// Analisar co-requisitos
		if (!isEmpty(coRequisito)) {
			if (!verificaExpressao(componenteInvalido, coRequisito, componenteCurricularDao, "coRequisito", null))
				lista.addErro("O componente curricular " + componente.getDescricao() + " possui express�o de co-requisito mal formada.");
		}
		
		// n�o pode existir nenhum componente equivalente j� no curr�culo
		if (componente.getEquivalencia() != null
				&& !componente.getEquivalencia().trim().equals("")) {
			boolean possuiEquivalencia = ExpressaoUtil.eval(componente.getEquivalencia(), curriculo.getComponentesCurriculares());
			if (possuiEquivalencia) {
				Collection<ComponenteCurricular> componentes = ArvoreExpressao.getMatchesComponentes(componente.getEquivalencia(), curriculo.getComponentesCurriculares());
				List<ComponenteCurricular> equivalentes = new ArrayList<ComponenteCurricular>();
				for (ComponenteCurricular equivalente : componentes) {
					equivalente = componenteCurricularDao.findCodigoNomeById(equivalente.getId(), true);
					equivalentes.add(equivalente);
				}
				lista.addErro("O componente " + componente.getCodigoNome() + " possui equival�ncia com o(s) componente(s) " +
					StringUtils.transformaEmLista(equivalentes) +
					", que j� est�(�o) presente(s) no curr�culo.");
			}
		}

		// Componentes dos semestres anteriores
		ArrayList<ComponenteCurricular> compSemAnteriores = new ArrayList<ComponenteCurricular>(0);
		for (CurriculoComponente cc : curriculo.getCurriculoComponentes()) {			
			if(cc.getSemestreOferta() < semestre) {
				compSemAnteriores.add(cc.getComponente());
			}
		}

		/*
		 * O componente a ser adicionado deve ter seus pr�-requisitos nos
		 * semestres anteriores, mas nenhum pr�-requisito nos semestres
		 * posteriores e no mesmo semestre
		 */
		if (componente.getPreRequisito() != null && !ExpressaoUtil.eval(componente.getPreRequisito(), compSemAnteriores) && ccc.getSemestreOferta() <= curriculo.getSemestreConclusaoIdeal())
			lista.addErro("O componente " + componente.getCodigoNome() + " deve ter todos os pr�-requisitos nos n�veis anteriores ao n�vel " + semestre);
		
		return lista;
	}

	/** Verifica se a express�o de um componente curricular � v�lida.
	 * @param componenteInvalido
	 * @param preRequisito
	 * @param componenteDao
	 * @param tipoExpressao
	 * @param cacheComponente 
	 * @return
	 */
	private static boolean verificaExpressao(Map<String, Object> componenteInvalido, String preRequisito, ComponenteCurricularDao componenteDao, String tipoExpressao, Map<Integer, String> cacheComponente) {
		String expressao = null;
		boolean valido = true;
		try {
			expressao = ExpressaoUtil.buildExpressaoFromDB(preRequisito, componenteDao, false, cacheComponente);
			componenteInvalido.put(tipoExpressao, expressao);
			ArvoreExpressao.fromExpressao(preRequisito);
		} catch (Exception e) {
			valido = false;
			if (expressao == null)
				componenteInvalido.put(tipoExpressao, "Express�o com componente curricular inativo/inv�lido.");
			componenteInvalido.put(tipoExpressao + "Invalido", true);
		}		
		return valido;
	}
	
	/** Indica se o componente j� foi inclu�do ao curr�culo ou n�o.
	 * @param curriculo
	 * @param componente
	 * @return
	 */
	private static boolean isExisteComponenteNoCurriculo(Curriculo curriculo, ComponenteCurricular componente) {
		for (CurriculoComponente cc : curriculo.getCurriculoComponentes()) {
			if (cc.getComponente().getId() == componente.getId()) {
				return true;
			}
		}
		return false;
	}
}
