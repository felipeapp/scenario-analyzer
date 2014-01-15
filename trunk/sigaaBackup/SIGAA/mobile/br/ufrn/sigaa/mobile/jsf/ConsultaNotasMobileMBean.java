package br.ufrn.sigaa.mobile.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

@Component("consultaNotasMobileMBean")
@Scope("session")
/**
 * MBean responsavel pela consulta de Notas pelo celular (notas do semestre
 * atual e dos passados)
 * 
 */
public class ConsultaNotasMobileMBean extends SigaaAbstractController {

	private List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
	private ConsultaNotasMobile consultaNotas = new ConsultaNotasMobile();
	private DiscenteAdapter discente = null;
	private ArrayList<VinculoUsuario> vinculosAtivos = new ArrayList<VinculoUsuario>();
	private Set<SemestresAnteriores> listaOutrosSemestres = new LinkedHashSet<SemestresAnteriores>();

	private int vinculoUsuarioInt;
	private boolean tecnicoMusica;

	public String redirectPaginaNotas() {
		return forward("/mobile/notas.jsp");
	}

	public ConsultaNotasMobileMBean() throws NumberFormatException,
			ArqException {

		DiscenteDao dao = getDAO(DiscenteDao.class);
		String str = getParameter("discente");

		if (str == null) {
			discente = getUsuarioLogado().getDiscenteAtivo();
		} else {
			discente = dao.findByPK(Integer.parseInt(str));
		}

		if (discente == null) {
			addMensagemErro("O seu usuário não está associado a nenhum discente.");
		}
		
		List<MatriculaComponente> tmpLista = null;
		
		if (discente != null) {
			consultaNotas.definirAnoPeriodoAtual(discente);
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
 			tmpLista = dao.findRelatorioNotasAluno(consultaNotas.getDiscente(), true, cal.getAno(), cal.getPeriodo());
 			if( consultaNotas.getMediaGeral() == null )
 				populaMediaDiscente();
		}
			
		if (tmpLista != null)
			removerNotasNulasDisciplinas(tmpLista);
		
		verificarTipoDiscenteTecnico(discente);
	}
	
	/**
	 * Popula a media geral do discente de acordo com nível de ensino.
	 * @throws DAOException
	 */
	private void populaMediaDiscente() throws DAOException{
		
		Double media = null;
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);
		
		if( discente.isGraduacao() )
			media = dao.buscaMcDiscente( discente.getId() ) ;
		else if ( discente.isMestrado() ) {
			DiscenteStricto discenteStricto = dao.findByPrimaryKey( 
					discente.getId(), DiscenteStricto.class, "mediaGeral" );
			media = Double.valueOf( discenteStricto.getMediaGeral() ) ; 
		}else if ( discente.isTecnico() && tecnicoMusica ) {
			DiscenteTecnico discenteTecnico = dao.findByPrimaryKey(
					discente.getId(), DiscenteTecnico.class, "mediaGeral" );
			media = Double.valueOf( discenteTecnico.getMediaGeral() ); 
		}
		
		consultaNotas.setMediaGeral( media );
		
	}

	/**
	 * Algumas notas mesmo sendo nulas estão ocupado a collection de notas, 
	 * isso faz com que na view NÃO apareça a msg "Notas não cadastradas"
	 * Se a nota for nula eu removo esses elementos
	 *  
	 */
	private void removerNotasNulasDisciplinas(List<MatriculaComponente> tmpLista) {
		
		List<NotaUnidade> notasNulasARemover = new ArrayList<NotaUnidade>();
		for (MatriculaComponente it : tmpLista) {
			if (it.getNotas().size() > 0) {
				for (NotaUnidade notaUnidade : it.getNotas()) {
					if (notaUnidade.getNota() == null) {
						notasNulasARemover.add(notaUnidade);
					}
				}
			}
		}
		
		
		for (MatriculaComponente it : tmpLista) {
			it.getNotas().removeAll(notasNulasARemover);
		}
		
		matriculas = tmpLista;
	}

	private void verificarTipoDiscenteTecnico(DiscenteAdapter discente)
			throws DAOException {

		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);

		if (discente instanceof DiscenteTecnico) {

			ParametrosGestoraAcademica gestoraAcademcia = null;
			if (discente != null) {
				gestoraAcademcia = ParametrosGestoraAcademicaHelper
						.getParametros(discente);
			}

			if (gestoraAcademcia != null) {
				if (gestoraAcademcia.getCertificado().equals(
						"diploma_musica.jasper"))
					tecnicoMusica = true;
				else
					tecnicoMusica = false;
			}

			// calcular media geral para tecnicos

			DiscenteTecnico tecnico = (DiscenteTecnico) this.discente;
			if (tecnico.getMediaGeral() == 0) {
				tecnico.setMediaGeral((float) dao.calculaIraDiscente(discente.getId()));
			}
		}

	}

	public String escolherVinculo() throws ArqException {

		ArrayList<VinculoUsuario> vinculosAtivos = (ArrayList<VinculoUsuario>) VinculoUsuario
				.processarVinculosUsuario(getUsuarioLogado(),
						getCurrentRequest());

		for (VinculoUsuario itVinculosAtivos : vinculosAtivos) {
			if (itVinculosAtivos.isVinculoDiscente() && itVinculosAtivos.isAtivo()) {
				if (itVinculosAtivos.getNumero() == vinculoUsuarioInt) {
					if (itVinculosAtivos.getDiscente() != null) {
						discente = itVinculosAtivos.getDiscente();
					} else {
						discente = getUsuarioLogado().getDiscenteAtivo();
					}
					getUsuarioLogado().setVinculoAtivo(vinculoUsuarioInt);
					DadosAcessoMobile acesso = new DadosAcessoMobile(getUsuarioLogado());
					getCurrentRequest().getSession().setAttribute("acesso", acesso);
				}
			}
		}

		DiscenteDao dao = getDAO(DiscenteDao.class);
		verificarTipoDiscenteTecnico(discente);

		consultaNotas.definirAnoPeriodoAtual(discente);
		matriculas = dao.findRelatorioNotasAluno(consultaNotas.getDiscente(),
				false, 0, 0);

		return forward("/mobile/menu.jsf");
	}

	/**
	 * abre o semestre especificado pelo link do celular
	 */
	public String acessarSemestrePassado() throws ArqException {

		consultaNotas.getDisciplinasAnteriores().clear();

		ArrayList<VinculoUsuario> vinculosAtivos = (ArrayList<VinculoUsuario>) VinculoUsuario
				.processarVinculosUsuario(getUsuarioLogado(),
						getCurrentRequest());

		for (VinculoUsuario itVinculosAtivos : vinculosAtivos) {
			if (itVinculosAtivos.isVinculoDiscente() && itVinculosAtivos.isAtivo()) {

				if (itVinculosAtivos.getNumero() == vinculoUsuarioInt) {
					if (itVinculosAtivos.getDiscente() != null) {
						discente = itVinculosAtivos.getDiscente();
					} else {
						discente = getUsuarioLogado().getDiscenteAtivo();
					}
				}
			}
		}

		int ano = getParameterInt("ano", 0);
		int periodo = getParameterInt("periodo", 0);
		
		DiscenteDao dao = getDAO(DiscenteDao.class);

		consultaNotas.definirAnoPeriodoAtual(discente);
		matriculas = dao.findRelatorioNotasAluno(consultaNotas.getDiscente(),
				true, ano, periodo);


		consultaNotas.setAnoSemestreAnterior(ano);
		consultaNotas.setPeriodoSemestreAnterior(periodo);

		for (MatriculaComponente itMat : matriculas) {
			if (itMat.getAno() == ano && itMat.getPeriodo() == periodo) {

				consultaNotas.adicionarDisciplinasAnteriores(itMat);
			}
		}

		return forward("/mobile/semestre_passado.jsf");
	}

	/**
	 * exibe os semestres anteriores disponiveis a partir do semestre atual
	 */
	public String listaSemestresAnterioresAtual() throws ArqException {

		consultaNotas.getDisciplinasAnteriores().clear();

		ArrayList<VinculoUsuario> vinculosAtivos = (ArrayList<VinculoUsuario>) VinculoUsuario
				.processarVinculosUsuario(getUsuarioLogado(),
						getCurrentRequest());

		for (VinculoUsuario itVinculosAtivos : vinculosAtivos) {
			if (itVinculosAtivos.isVinculoDiscente() && itVinculosAtivos.isAtivo()) {

				if (itVinculosAtivos.getNumero() == vinculoUsuarioInt) {
					if (itVinculosAtivos.getDiscente() != null) {
						discente = itVinculosAtivos.getDiscente();
					} else {
						discente = getUsuarioLogado().getDiscenteAtivo();
					}
				}
			}
		}

		DiscenteDao dao = getDAO(DiscenteDao.class);
		consultaNotas.definirAnoPeriodoAtual(discente);
		matriculas = dao.findRelatorioNotasAluno(consultaNotas.getDiscente(),
				false, 0, 0);

		SemestresAnteriores semestresAnteriores = null;
		for (MatriculaComponente itMat : matriculas) {
			if (itMat.getAno() != null) {
				if ( itMat.getAno() != consultaNotas.getAnoAtual() ) {

					semestresAnteriores = new SemestresAnteriores();
					semestresAnteriores.setAno(itMat.getAno());
					semestresAnteriores.setPeriodo(itMat.getPeriodo());

					listaOutrosSemestres.add(semestresAnteriores);
				}
				else if (itMat.getAno() == consultaNotas.getAnoAtual() && itMat.getPeriodo() == consultaNotas.getPeriodoAtual()-1) {
					semestresAnteriores = new SemestresAnteriores();
					semestresAnteriores.setAno(itMat.getAno());
					semestresAnteriores.setPeriodo(itMat.getPeriodo());

					listaOutrosSemestres.add(semestresAnteriores);
				}
			}
		}

		return forward("/mobile/lista_semestres_passados.jsf");
	}

	public String consultarDataProvas() {
		return forward("/mobile/datas_prova_discente.jsf");
	}
	
	public String atestadoMatricula() throws DAOException, IOException, SegurancaException {

		DiscenteAdapter discente = getDiscenteUsuario();
		if (discente.getNivel() == NivelEnsino.GRADUACAO || NivelEnsino.isAlgumNivelStricto(discente.getNivel())) {
			getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
			AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
			atestado.setDiscente(discente);
			return atestado.selecionaDiscente();
		}
		if (discente.getNivel() == NivelEnsino.TECNICO) {
			getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
			AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
			atestado.setDiscente(discente);
			return atestado.selecionaDiscente();
		}
		addMensagemErro("Ainda não é possível visualizar o atestado de matrícula para o seu nível de ensino.");
		return null;
	}
	
	public String voltarMenuPrincipal() throws ArqException {

		matriculas.clear();
		
		consultaNotas.getDisciplinasAnteriores().clear();

		ArrayList<VinculoUsuario> vinculosAtivos = (ArrayList<VinculoUsuario>) VinculoUsuario
				.processarVinculosUsuario(getUsuarioLogado(),
						getCurrentRequest());

		for (VinculoUsuario itVinculosAtivos : vinculosAtivos) {
			if (itVinculosAtivos.isVinculoDiscente() && itVinculosAtivos.isAtivo()) {

				if (itVinculosAtivos.getNumero() == vinculoUsuarioInt) {
					if (itVinculosAtivos.getDiscente() != null) {
						discente = itVinculosAtivos.getDiscente();
					} else {
						discente = getUsuarioLogado().getDiscenteAtivo();
					}
				}
			}
		}

		DiscenteDao dao = getDAO(DiscenteDao.class);

		consultaNotas.definirAnoPeriodoAtual(discente);
		matriculas = dao.findRelatorioNotasAluno(consultaNotas.getDiscente(),true, getAnoAtual(), getPeriodoAtual());

		return forward("/mobile/menu.jsf");
	}

	public ConsultaNotasMobile getConsultaNotas() {
		return consultaNotas;
	}

	public void setConsultaNotas(ConsultaNotasMobile consultaNotas) {
		this.consultaNotas = consultaNotas;
	}

	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public ArrayList<VinculoUsuario> getVinculoUsuario() throws ArqException {

		ArrayList<VinculoUsuario> listaVinculos = (ArrayList<VinculoUsuario>) VinculoUsuario
				.processarVinculosUsuario(getUsuarioLogado(),
						getCurrentRequest());
		for (VinculoUsuario itVinculos : listaVinculos) {
			if (itVinculos.isVinculoDiscente() && itVinculos.isAtivo()) {
				vinculosAtivos.add(itVinculos);
			}
		}

		return vinculosAtivos;
	}

	public void setVinculoUsuario(ArrayList<VinculoUsuario> vinculoUsuario) {
		this.vinculosAtivos = vinculoUsuario;
	}

	public Set<SemestresAnteriores> getListaOutrosSemestres() {
		return listaOutrosSemestres;
	}

	public void setListaOutrosSemestres(
			Set<SemestresAnteriores> listaOutrosSemestres) {
		this.listaOutrosSemestres = listaOutrosSemestres;
	}

	public boolean isTecnicoMusica() {
		return tecnicoMusica;
	}

	public void setTecnicoMusica(boolean tecnicoMusica) {
		this.tecnicoMusica = tecnicoMusica;
	}

	public int getVinculoUsuarioInt() {
		return vinculoUsuarioInt;
	}

	public void setVinculoUsuarioInt(int vinculoUsuarioInt) {
		this.vinculoUsuarioInt = vinculoUsuarioInt;
	}

}
