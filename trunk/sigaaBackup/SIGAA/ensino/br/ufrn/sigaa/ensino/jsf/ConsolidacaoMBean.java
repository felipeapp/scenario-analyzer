/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 03/07/2007
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dao.CadastroNotasUnidadesDao;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * MBean usado para consolida��o de todos os n�veis de turmas
 *
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
public class ConsolidacaoMBean extends SigaaAbstractController<Object> {

	/** Turma a ser consolidada */
	protected Turma turma = new Turma();
	/** M�todo de avalia��o utilizado */
	protected int metodoAvaliacao;
	/** M�todo de avalia��o utilizado */
	protected MetodologiaAvaliacao metodologiaAvaliacao;	
	/** Metodologia usada no EAD*/
	protected int metodologia;
	/** N�mero de unidades do componente curricular associado a turma a ser consolidada. */
	protected int numeroUnidades;
	/** Indica que o caso de uso de consolida��o foi acessado pela Turma Virtual. */
	protected boolean fromPortalTurma;
	/** Representa a rela��o entre os conceitos usados e a m�dia do discente */
	protected static Collection<ConceitoNota> conceitos;

	/**
	 * 
	 * Retorna os par�metros necess�rios para a consolida��o de turma.
	 * Nos par�metros est�o contidas informa��es como a metodologia de avalia��o utilizada,
	 * os pesos das notas para cada unidade e outras informa��es importantes para o prosseguimento do
	 * processo de consolida��o.
	 * 
	 */
	@Override
	public ParametrosGestoraAcademica getParametrosAcademicos() throws DAOException {
		try {
			int gestoraAcademica = 0;
			if (getUsuarioLogado().getVinculoAtivo().getUnidade().getGestoraAcademica() != null)
				gestoraAcademica = turma.getDisciplina().getUnidade().getId();
			ComponenteCurricular cc = turma.getDisciplina();
			char nivel = cc.getNivel();
	
			if (nivel == NivelEnsino.GRADUACAO || nivel == NivelEnsino.LATO ) {
				gestoraAcademica = UnidadeGeral.UNIDADE_DIREITO_GLOBAL;
			}			
			else if(nivel == NivelEnsino.STRICTO){
				gestoraAcademica =  turma.getDisciplina().getUnidade().getGestoraAcademica().getId();
			}
			else if(nivel == NivelEnsino.MEDIO){
				gestoraAcademica =  turma.getDisciplina().getUnidade().getGestoraAcademica().getId();
			}
			else {
				gestoraAcademica = cc.getUnidade().getId();
			}
			return ParametrosGestoraAcademicaHelper.getParametros(getTurma(), new br.ufrn.sigaa.dominio.Unidade(gestoraAcademica));
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Pega o n�mero de NotaUnidades para essa turma.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public int getNumeroUnidades() throws ArqException, NegocioException {
		return TurmaUtil.getNumUnidadesDisciplina(turma);
	}

	/**
	 * Verifica se a metodologia de avalia��o da
	 * unidade gestora acad�mica � por nota.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return true, se o m�todo for por nota, false caso contr�rio
	 */
	public boolean isNota() {
		return isMetodo(MetodoAvaliacao.NOTA);
	}

	/**
	 * Verifica se a metodologia de avalia��o da
	 * unidade gestora acad�mica � por conceito
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 *
	 * @return true, se o m�todo for por conceito, false caso contr�rio
	 */
	public boolean isConceito() {
		return isMetodo(MetodoAvaliacao.CONCEITO);
	}

	/**
	 * Verifica se a metodologia de avalia��o da
	 * unidade gestora acad�mica � por compet�ncia
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return true, se o m�todo for por compet�ncia, false caso contr�rio
	 */
	public boolean isCompetencia() {
		return isMetodo(MetodoAvaliacao.COMPETENCIA);
	}

	/**
	 * Verifica se a turma obriga o professor lan�ar o di�rio.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return true, se a turma for de gradua��o
	 */
	public boolean isObrigatoriedadeDiario() {
		String[] niveisEnsino = ParametroHelper.getInstance().getParametroStringArray(ParametrosGerais.NIVEIS_ENSINO_OBRIGATORIEDADE_DIARIO);
		
		for (String nivel : niveisEnsino) 
			if (nivel.charAt(0) == turma.getDisciplina().getNivel())
				return true;
		return false;	
	}
	
	/**
	 * Verifica se a metodologia de avalia��o da
	 * unidade gestora acad�mica � do tipo especificado
	 * como par�metro,
	 *
	 * @param metodo m�todo de avalia��o a ser verificado
	 * @return true, se o m�todo for do tipo especificado, false caso contr�rio
	 */
	private boolean isMetodo(int metodo) {
		if (metodoAvaliacao == 0) {
			metodoAvaliacao = getMetodoAvaliacao();
		}

		return metodoAvaliacao == metodo;
	}

	/**
	 * Pega o m�todo de avalia��o da unidade gestora acad�mica
	 * do usu�rio
	 *
	 * @return m�todo de avalia��o
	 */
	public int getMetodoAvaliacao() {
		try {
			ParametrosGestoraAcademica p = getParametrosAcademicos();
			if (p == null || p.getMetodoAvaliacao() == null)
				throw new NegocioException("N�o foi poss�vel determinar o m�todo de avalia��o para esta turma. Por favor, reinicie o processo ou contate o suporte.");
			return p.getMetodoAvaliacao();
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro("Erro inesperado: " + e.getMessage());
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * Retorna lista de conceitos poss�veis
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 */
	public Collection<ConceitoNota> getConceitos() throws DAOException {
		if (conceitos == null || conceitos.isEmpty()) {
			GenericDAO dao = getGenericDAO();
			conceitos = dao.findAll(ConceitoNota.class, "valor", "desc");
		}

		return conceitos;
	}

	/**
	 * Identifica se foram cadastradas avalia��es para a turma
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isAvaliacao() throws DAOException {

		if (turma.getMatriculasDisciplina() != null && !turma.getMatriculasDisciplina().isEmpty()) {
			MatriculaComponente matricula = turma.getMatriculasDisciplina().iterator().next();
			return matricula.hasAvaliacao();
		}
		return false;
	}

	/**
	 * 
	 * Retorna as notas de uma MatriculaComponente, caso a metodologia de avalia��o seja por NOTA. 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<NotaUnidade> getNotas() throws ArqException, NegocioException {
		if (metodoAvaliacao == 0)
			metodoAvaliacao = getMetodoAvaliacao();
			
		if (metodoAvaliacao == MetodoAvaliacao.NOTA) {
			if (turma.getMatriculasDisciplina() != null && !turma.getMatriculasDisciplina().isEmpty()) {
				
				Integer numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
				MatriculaComponente res = turma.getMatriculasDisciplina().iterator().next();

				// Verifica se o n�mero de unidades cadastrados na matr�cula bate com o n�mero de unidades do componente
				// Visto que uma matr�cula pode ter sido consolidada antes do fechamento da turma.
				for (MatriculaComponente m : turma.getMatriculasDisciplina()){
					if (m.getNotas() != null && !m.getNotas().isEmpty() && m.getNotas().size() == numUnidades){
						res = m;
						break;
					}			
				}		
				
				return res.getNotas();

			}
		}
		return null;
	}

	/**
	 * Retorna o n�mero de avalia��es realizadas com a turma.
	 * @return
	 */
	public int getNumeroAvaliacoes() {
		MatriculaComponente matricula = turma.getMatriculasDisciplina().iterator().next();
		int total = 0;

		for (NotaUnidade nota : matricula.getNotas()) {
			if (nota.getAvaliacoes() != null && !nota.getAvaliacoes().isEmpty()) {
				total += nota.getAvaliacoes().size();
			}
		}

		return total;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/**
	 * Retorna uma lista contendo todas as turmas do docente logado com a situa��o passada.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @param situacao
	 * @return
	 */
	public Collection <Turma> getTurmasSituacao (int situacao) {
		TurmaDao dao = getDAO(TurmaDao.class);

		try {
			Collection <Turma> turmas = null;

			if (getServidorUsuario() != null)
				// se o usu�rio for docente interno
				turmas = dao.findByDocente(getServidorUsuario(), situacao, null);
			else
				// se n�o tiver servidor associado ao usu�rio considera que ele � docente externo
				turmas = dao.findByDocenteExternoOtimizado(getUsuarioLogado().getDocenteExterno(), situacao, false);

			return turmas;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Turma>();
		}
	}
	
	/**
	 * Retorna uma lista de selectitems contendo todas as turmas do docente logado com a situa��o passada.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @param situacao
	 * @return
	 */
	public List<SelectItem> getTurmasSituacaoCombo (int situacao) {
		try {
			return toSelectItems(getTurmasSituacao(situacao), "id", "descricaoSemDocente");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}
	}
	
	/**
	 * Carrega as informa��es da turma (basicamente faltas) quando o m�todo de avalia��o � por CONCEITO.
	 * Quando o m�todo de avalia��o � por NOTA, al�m das frequ�ncias serem setadas na MatriculaComponente
     * s�o criadas as nota_unidade de cada MatriculaComponente, caso n�o existam e s�o carregadas as avalia��es
	 * de cada nota_unidade(Caso existam) e os respectivos pesos de cada Avaliacao(Configurados nas configura��es
 	 * de turma da Turma Virtual).
	 * 
	 * @param matriculas
	 * @param pesosAvaliacoes
	 * @param metodoAvaliacao
	 * @throws ArqException
	 * @throws NegocioException
	 */
	protected void carregaInfoTurmaPos(Turma turma, Collection<MatriculaComponente> matriculas, String[] pesosAvaliacoes, 
			Integer metodoAvaliacao, String[] pesoMediaPesoRec) throws ArqException, NegocioException{
		
		// dao para buscar avalia��es(Uma NotaUnidade pode ser constituida de Avaliac�es, quando por exemplo o docente resolve dividir a nota de uma unidade em um trabalho e uma prova )
		AvaliacaoDao dao = getDAO(AvaliacaoDao.class);		
		// para buscar as faltas
		FrequenciaAlunoDao fDao = getDAO(FrequenciaAlunoDao.class);

		if(metodoAvaliacao == MetodoAvaliacao.NOTA) {
			
	
			if (!isEmpty(matriculas)) {
				MatriculaComponente mat = matriculas.iterator().next();
			
				if (pesosAvaliacoes != null && pesosAvaliacoes.length > 0) {
					// Cadastro das notas, se ainda n�o houver notas cadastradas
					
					//Recupera as notas da MatriculaComponente, se houver.
					mat.setNotas(dao.findNotasByMatricula(mat));
					
					CadastroNotasUnidadesDao notaUnidadeDao = getDAO(CadastroNotasUnidadesDao.class);
					
					Integer numUnidades = TurmaUtil.getNumUnidadesDisciplina(mat.getTurma());
					List<Integer> lista = notaUnidadeDao.countNotaUnidadeByTurma(turma);
					
					//Se o m�todo de avalia��o for por NOTA e a MatriculaComponente esta sem NotaUnidde ou o n�mero de NotaUnidade for diferente
					//do n�mero de unidades do componente curricular ent�o devemos criar uma linha na tabela nota_unidade associando-a a MatriculaComponente.
					if (metodoAvaliacao == MetodoAvaliacao.NOTA && (numUnidades != null && (lista.size() > 1 || !lista.contains(numUnidades))) ) {
						if (getAcessoMenu().isDocente() || isPermissaoDocente() || getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO)) {
							prepareMovimento(SigaaListaComando.CADASTRAR_NOTAS_UNIDADES);
							MovimentoCadastro mov = new MovimentoCadastro();
							mov.setObjMovimentado(turma);
							mov.setColObjMovimentado(matriculas);
							mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOTAS_UNIDADES);
		
							matriculas = (Collection<MatriculaComponente>) execute(mov, getCurrentRequest());
						} else {
							throw new NegocioException("Nenhuma nota foi lan�ada pelo docente.");
						}
					}
		
				}
			}
			
			HashMap<Integer,Integer> faltasAlunos = fDao.findFaltasAlunos(turma,getDatasAtivas());
			//TODO: Ordenar por id_unidade e s� percorrer at� mudar a unidade
			List<Avaliacao> avaliacoes = dao.findAvaliacoes(turma);
	
			Collection<NotaUnidade> notas = dao.findNotasByTurma(turma);
	
			// Setar pesos e avalia��es das nota_unidade e as frequencias dos discentes.
			if (!isEmpty(matriculas)) {
				for (MatriculaComponente matricula : matriculas) {
					
					if(pesoMediaPesoRec != null && pesoMediaPesoRec.length > 0) {
						//Seta o peso da recupera��o no calculo da m�dia final
						matricula.setPesoMedia(new Integer(pesoMediaPesoRec[0].trim()));
						//Seta o peso da m�dia sem recupera��o no calculo da m�dia final
						matricula.setPesoRecuperacao(new Integer(pesoMediaPesoRec[1].trim()));
					}
					matricula.setMetodoAvaliacao(metodoAvaliacao);
					matricula.setComponente(turma.getDisciplina());
					matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
					matricula.setFaltasCalculadas( faltasAlunos.get(matricula.getDiscente().getId() ) );
					
					if (pesosAvaliacoes != null && pesosAvaliacoes.length > 0) {
						int unidade = 0;
						matricula.setComponente(turma.getDisciplina());
						matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
		
						List<NotaUnidade> notasMatricula = new ArrayList<NotaUnidade>();
						for ( NotaUnidade notaMatricula : notas) {
							if ( notaMatricula.getMatricula().getId() == matricula.getId() ) {
								notasMatricula.add(notaMatricula);
							}
						}
		
						matricula.setNotas(notasMatricula);				
		
						for (NotaUnidade nota : matricula.getNotas()) {
							nota.setPeso(pesosAvaliacoes[unidade].trim());
							unidade++;
		
							
							//O Docente pode dividir a nota_unidade em subnotas, cada uma sendo uma avalia��o.
							//Seta as avalia��es desta nota_unidade
							ArrayList<Avaliacao> avaliacoesDessaNota = new ArrayList<Avaliacao>();
		
							for ( Avaliacao av : avaliacoes ) {
								if ( av.getUnidade().getId() == nota.getId() ) {
									avaliacoesDessaNota.add(av);
								}
							}
							nota.setAvaliacoes(avaliacoesDessaNota);
						}
					}
				}
			} else {
				matriculas = new ArrayList<MatriculaComponente>();
			}
		}
		else {//Avalia��o por Conceito.		
			
			// mapa que traz as faltas dos alunos
			HashMap<Integer,Integer> faltasAlunos = fDao.findFaltasAlunos(turma,getDatasAtivas());
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
			
			// Seta as informa��es da matr�cula (obs: a nota j� vem em media_final )
			if (!isEmpty(matriculas)) {
				for (MatriculaComponente matricula : matriculas) {
					matricula.setMetodoAvaliacao(metodoAvaliacao);
					matricula.setComponente(turma.getDisciplina());
					matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
					matricula.setFaltasCalculadas( faltasAlunos.get(matricula.getDiscente().getId() ) );
					if(matricula.getEstrategia()== null && estrategia != null)					
						matricula.setEstrategia(estrategia);
				}
			}
			
		}
		
		turma.setMatriculasDisciplina(matriculas);
		
	}


	/**
	 *  Carrega as informa��es da turma para os n�veis t�cnico, lato e gradua��o.
	 *  Neste m�todo as notas de cada unidade e as suas avalia��es s�o carregadas.
	 * 
	 * @param matriculas
	 * @param pesosAvaliacoes
	 * @param metodoAvaliacao
	 * @throws ArqException
	 * @throws NegocioException
	 */
	protected void carregaInfoTurma(Turma turma, Collection<MatriculaComponente> matriculas,
			String[] pesosAvaliacoes, Integer metodoAvaliacao, String[] pesoMediaPesoRec) throws ArqException, NegocioException{ 

		// dao para buscar avalia��es
		AvaliacaoDao dao = getDAO(AvaliacaoDao.class);
		// dao para verificar as notas dos tutores
		EADDao eDao = getDAO(EADDao.class);
		// para buscar as faltas
		FrequenciaAlunoDao fDao = getDAO(FrequenciaAlunoDao.class);
		
		try {

			if (!isEmpty(matriculas)) {
			
				//TODO: Ordenar por id_unidade e s� percorrer at� mudar a unidade
				List<Avaliacao> avaliacoes = dao.findAvaliacoes(turma);				
				
				turma.setCurso(dao.refresh(turma.getCurso()));
				ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
				EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
				EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
				
				for (MatriculaComponente matricula : matriculas) {
					for (NotaUnidade nota : matricula.getNotas()) {
						ArrayList<Avaliacao> avaliacoesDessaNota = new ArrayList<Avaliacao>();
						
						for ( Avaliacao av : avaliacoes ) {
							if ( av.getUnidade().getId() == nota.getId() ) {
								avaliacoesDessaNota.add(av);
							}
						}
						nota.setAvaliacoes(avaliacoesDessaNota);
					}
					if (estrategia != null)
						matricula.setEstrategia(estrategia);
				}
				
				
				if (pesosAvaliacoes != null && pesosAvaliacoes.length > 0) {
					// Cadastro das notas, se ainda n�o houver notas cadastradas
					
					CadastroNotasUnidadesDao notaUnidadeDao = getDAO(CadastroNotasUnidadesDao.class);

					Integer numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
					List<Integer> lista = notaUnidadeDao.countNotaUnidadeByTurma(turma);
					
					//Se o m�todo de avalia��o for por NOTA e as MatriculaComponentes esta sem NotaUnidde ou o n�mero de NotaUnidade for diferente
					//do n�mero de unidades do componente curricular.
					if (metodoAvaliacao == MetodoAvaliacao.NOTA && (numUnidades != null && (lista.size() > 1 || !lista.contains(numUnidades))) ) {
						if (getAcessoMenu().isDocente() || isPermissaoDocente() || getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO)) {						
							prepareMovimento(SigaaListaComando.CADASTRAR_NOTAS_UNIDADES);
							MovimentoCadastro mov = new MovimentoCadastro();
							mov.setObjMovimentado(turma);
							mov.setColObjMovimentado(matriculas);
							mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOTAS_UNIDADES);
		
							matriculas = (Collection<MatriculaComponente>) execute(mov, getCurrentRequest());
						} else {
							throw new NegocioException("Usu�rio n�o possui acesso para modificar as notas dos discentes.");
						}
					}
		
				}
			}

			HashMap<Integer,Integer> faltasAlunos = fDao.findFaltasAlunos(turma,getDatasAtivas());

			//TODO: Ordenar por id_unidade e s� percorrer at� mudar a unidade
			List<Avaliacao> avaliacoes = dao.findAvaliacoes(turma);	
			
			Collection<NotaUnidade> notas = dao.findNotasByTurma(turma);

			// Setar pesos das notas e as faltas de cada discente
			if (!isEmpty(matriculas)) {
				
				if (isEad()) {
					metodologiaAvaliacao = MetodologiaAvaliacaoHelper.getMetodologia(matriculas.iterator().next().getDiscente().getCurso(), turma.getAno(), turma.getPeriodo());
				}				
				
				HashMap<Integer,Double> notasTutor1 = new HashMap<Integer, Double>();
				HashMap<Integer,Double> notasTutor2 = new HashMap<Integer, Double>();
				
				encontraNotasTutor(notasTutor1, notasTutor2, matriculas, eDao, metodologiaAvaliacao);
				
				for (MatriculaComponente matricula : matriculas) {
					
					if(pesoMediaPesoRec != null && pesoMediaPesoRec.length > 0) {
						//Seta o peso da recupera��o no calculo da m�dia final
						matricula.setPesoMedia(new Integer(pesoMediaPesoRec[0].trim()));
						//Seta o peso da m�dia sem recupera��o no calculo da m�dia final
						matricula.setPesoRecuperacao(new Integer(pesoMediaPesoRec[1].trim()));					
						//Seta a metodologia de Avalia��o na MatriculaComponente
					}
					matricula.setMetodoAvaliacao(metodoAvaliacao);
					//Seta as faltas do discente na MatriculaComponente
					
					Integer faltasMatricula = faltasAlunos.get(matricula.getDiscente().getId());
					if (faltasMatricula == null)
						faltasMatricula = 0;
					
					if (faltasMatricula > turma.getChTotalTurma())
						faltasMatricula = turma.getChTotalTurma();
					
					matricula.setFaltasCalculadas( faltasMatricula );
					
					if (pesosAvaliacoes != null && pesosAvaliacoes.length > 0) {
						int unidade = 0;
						matricula.setComponente(turma.getDisciplina());
						matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
		
						List<NotaUnidade> notasMatricula = new ArrayList<NotaUnidade>();
						for ( NotaUnidade notaMatricula : notas) {
							if ( notaMatricula.getMatricula().getId() == matricula.getId() ) {
								notasMatricula.add(notaMatricula);
							}
						}
		
						matricula.setNotas(notasMatricula);
						
						populaNotasTutor(notasTutor1, notasTutor2, matricula, metodologiaAvaliacao);
		
						for (NotaUnidade nota : matricula.getNotas()) {
							nota.setPeso(pesosAvaliacoes[unidade].trim());
							unidade++;
		
							ArrayList<Avaliacao> avaliacoesDessaNota = new ArrayList<Avaliacao>();
		
							for ( Avaliacao av : avaliacoes ) {
								if ( av.getUnidade().getId() == nota.getId() ) {
									avaliacoesDessaNota.add(av);
								}
							}
							nota.setAvaliacoes(avaliacoesDessaNota);
						}
					}else if ( isCompetencia() )
						matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
				}
			} else {
				matriculas = new ArrayList<MatriculaComponente>();
			}
			
			turma.setMatriculasDisciplina(matriculas);
		} finally {
			dao.close();
			eDao.close();
			fDao.close();
		}

	}

	/**
	 * Encontra todas as notas dos tutores da turma.
	 * 
	 * @param notasTutor1
	 * @param notasTutor2
	 * @param matriculas
	 * @param aDao
	 * @throws DAOException
	 * return datas
	 */
	private void encontraNotasTutor(HashMap<Integer, Double> notasTutor1,	HashMap<Integer, Double> notasTutor2, Collection<MatriculaComponente> matriculas , EADDao eDao, MetodologiaAvaliacao metodologiaEad) throws DAOException {
		if (isEad() && !isEstagioEad() && !isLato() && !isTurmaFeriasEad() && metodologiaEad.isPermiteTutor() ) {
			
			if (metodologiaEad.isUmaProva()) {
				notasTutor1.putAll(eDao.findNotaTutorTurma(turma,metodologiaEad));
			} else {
				int[] aulas = metodologiaEad.getNumeroAulasInt();
				int semanaInicial = 1;
				notasTutor1.putAll(eDao.findNotaTutorByIntervaloAulasTurma(turma,  metodologiaEad, semanaInicial, aulas[0]));
				notasTutor2.putAll(eDao.findNotaTutorByIntervaloAulasTurma(turma, metodologiaEad, aulas[0] + 1, aulas[0] + aulas[1]));
			}
		}
	}
	
	/**
	 * Povoa as notas dos tutores na matricula.
	 * 
	 * @param notasTutor1
	 * @param notasTutor2
	 * @param matricula
	 * return datas
	 */
	private void populaNotasTutor(HashMap<Integer, Double> notasTutor1, HashMap<Integer, Double> notasTutor2, MatriculaComponente matricula, MetodologiaAvaliacao metodologiaEad) {
		if (isEad() && !isEstagioEad() && !isLato() && !isTurmaFeriasEad()) {
			if (metodologiaEad.isUmaProva()) {
				matricula.setNotaTutor(notasTutor1.get(matricula.getId()));
			} else {
				matricula.setNotaTutor2(notasTutor2.get(matricula.getId()));
				matricula.setNotaTutor(notasTutor1.get(matricula.getId()));
			}
		}
	}

	/**
	 * Retorna as datas dos dias de aulas da turma.
	 * 
	 * @throws DAOException
	 * return datas
	 */
	List<Date> getDatasAtivas() throws DAOException{
		
		GenericDAO dao  = null;
		ArrayList<Date> datas = new ArrayList<Date>(); 
		
		try {
			dao = getGenericDAO();
			
			if (turma.getDisciplina() == null)
				return null;
			
			CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(turma.getDisciplina());
			if ( calendario != null ){
				datas.addAll(TurmaUtil.getDatasAulasTruncate(turma, calendario));
			}
			
			return datas;
			
		}finally{
			if (dao != null)
				dao.close();
		}	
	}
	
	public boolean isLato() {
		return turma.getDisciplina().getNivel() == NivelEnsino.LATO;
	}

	public boolean isEad() {
		return turma.isDistancia();
	}
	
	public boolean isMedio() {
		return turma.getDisciplina().getNivel() == NivelEnsino.MEDIO;
	}	

	/**
	 * Retorna a metodologia de avalia��o do curso associado a turma.
	 * 
	 * @return
	 */
	public MetodologiaAvaliacao getMetodologiaAvaliacao() {
		return metodologiaAvaliacao;
	}
	
	public boolean isEstagioEad() {
		return( turma.getDisciplina().isEstagio() && isEad());
	}
	
	public boolean isTurmaFeriasEad() {
		return( turma.isTurmaFerias() && isEad());
	}
	
	/**
	 * Identifica se a metodologia de avalia��o � por uma nota.
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isUmaNota() {
		if (getMetodologiaAvaliacao() != null)
			return getMetodologiaAvaliacao().getMetodoAvaliacao() == br.ufrn.sigaa.ead.dominio.MetodoAvaliacao.UMA_PROVA;
		return false;
	}
	
	/**
	 * Identifica se a metodologia de avalia��o � por duas notas.
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isDuasNotas() {
		if (getMetodologiaAvaliacao() != null)
			return getMetodologiaAvaliacao().getMetodoAvaliacao() == br.ufrn.sigaa.ead.dominio.MetodoAvaliacao.DUAS_PROVAS_RECUPERACAO;
		return false;
	}

	/**
	 * Retorna o peso da nota dada pelo tutor.
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getPesoTutor() {
		if (getMetodologiaAvaliacao() != null)
			return getMetodologiaAvaliacao().getPorcentagemTutor();
		return 1;
	}
	
	/**
	 * Identifica se a metodologia de avalia��o possui tutor.
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/relatorio.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermiteTutor() {
		if (getMetodologiaAvaliacao() != null)
			return getMetodologiaAvaliacao().isPermiteTutor();
		return false;
	}

	/**
	 * Retorna o peso da nota dada pelo professor.
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getPesoProfessor() {
		if (getMetodologiaAvaliacao() != null)
			return getMetodologiaAvaliacao().getPorcentagemProfessor();
		return 1;
	}

	/**
	 * Retorna se o usu�rio possui acesso de docente a turma.
	 * 
	 * @return
	 */
	private boolean isPermissaoDocente() {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		tBean.setTurma(turma);
		return tBean != null && tBean.isPermissaoDocente();
	}
	
	
}
