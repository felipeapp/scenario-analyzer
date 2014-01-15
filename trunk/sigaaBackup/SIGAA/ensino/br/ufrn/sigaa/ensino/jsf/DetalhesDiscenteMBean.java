/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 06/11/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.AlunoDTO;
import br.ufrn.integracao.interfaces.DadosBolsistasRemoteService;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe auxiliar para exibir os dados de um discente em um tooltip
 *
 * @author Ricardo Wendell
 *
 */
@Component("detalhesDiscenteBean") @Scope("request")
public class DetalhesDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> {

	/** Id do discente */ 
	private int id;

	/** Usuário do discente */
	private Usuario usuario;

	/** Serviço responsável pela comunicação com SIPAC para retornar os discente bolsistas */
	@Autowired
	private DadosBolsistasRemoteService service;

	/** Template do SIPAC, utilizado para pegar o id da pessoa do discente no SIPAC */
	private JdbcTemplate sipacTemplate = null;
	
	/** Informações sobre o bolsista que será buscado na base do SIPAC */
	List<AlunoDTO> alunoDto;
	
	/** Ano das matrículas do discente */ 
	int ano;
	/** Período das matrículas do discente */ 
	int periodo;
	/** Frequência total do discente em suas turmas*/ 
	float frenquenciaTurma;
	/** Total de créditos aprovados do discente */ 
	float creditosAprovados;
	
	public DetalhesDiscenteMBean() {
		obj = new Discente();
		setUsuario(new Usuario());
	}

	/**
	 * Popular dados do discente selecionado para exibição
	 *
	 * @param event
	 * @throws DAOException
	 */
	public String getDadosDiscente() {
		obj.setId(getParameterInt("idDiscente",0));
		if (obj.getId() > 0) {
			try {
				obj = getDAO(DiscenteDao.class).findByPK(obj.getId());

				if (obj == null){
					return null;
				}
				// Buscar usuário do discente
				setUsuario(getDAO(UsuarioDao.class).findByDiscente(obj.getDiscente()));

				// Popular dados específicos de discentes stricto
				if (obj.isStricto()) {
					OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
					DiscenteStricto stricto = (DiscenteStricto) obj;
					stricto.setOrientacao( orientacaoDao.findOrientadorAtivoByDiscente(obj.getId()) );
				}

				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(obj);
				getBolsasDiscente(cal);
				getMatriculaComponenteDiscente(cal);
				
			} catch (DAOException e) {
				notifyError(e);
			}
		}
		return "";
	}

	/**
	 * Retorna todas as bolsas do discente na instituição
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/detalhes_discente.jsp </li>
	 * 	</ul>
	 * 
	 */
	public void getBolsasDiscente(CalendarioAcademico cal) throws DAOException{
		if(Sistema.isSipacAtivo()) {
			Date dataInicial = createDate(cal, true); 
			Date dataFinal = createDate(cal, false);
			
			Integer idPessoa = null;	
			try {
				idPessoa = getSipacTemplate().queryForInt("select id_pessoa from comum.pessoa where cpf_cnpj=?", new Object[] { obj.getPessoa().getCpf_cnpj() });
				if (idPessoa != null ) 
					alunoDto = service.findAllDiscenteBolsistasPorPeriodoUnidadeMatricula( dataInicial, dataFinal, 0, idPessoa, null );
			}catch (EmptyResultDataAccessException e) { 
				//Query vazia. 
			}
			 
		}
	} 
	
	/**
	 * 
	 * 
	 * @param cal
	 * @throws DAOException
	 */
	private void getMatriculaComponenteDiscente(CalendarioAcademico cal) throws DAOException {
		
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		ano = cal.getAno();
		periodo = 0;

		try {
			
			if (cal.getPeriodo() == 1) {
				ano = ( cal.getAno() - 1 );
				periodo = ( cal.getPeriodo() + 1 );
			} else {
				periodo = (cal.getPeriodo() - 1 ); 
			}
			
			Collection<MatriculaComponente> matriculas = dao.findAtivasByDiscenteAnoPeriodo( obj.getDiscente(), ano, periodo );
			
			int numeroMaximoFaltas = 0;
			int numeroFaltasDiscente = 0;
			int totalCredito = 0;
			int totalCreditoAprovados = 0;
			
			ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(obj.getDiscente());

			for (MatriculaComponente matriculaComponente : matriculas) {
				numeroMaximoFaltas += matriculaComponente.getMaximoFaltas(0.0,parametros.getMinutosAulaRegular());
				if (matriculaComponente.getNumeroFaltas() != null)
					numeroFaltasDiscente += matriculaComponente.getNumeroFaltas();
				totalCredito += matriculaComponente.getComponenteCrTotal();
				if ( matriculaComponente.getSituacaoMatricula().getId() == SituacaoMatricula.APROVADO.getId() )
					totalCreditoAprovados += matriculaComponente.getComponenteCrTotal();
			}

			frenquenciaTurma = numeroMaximoFaltas > 0 ? ( (numeroMaximoFaltas - numeroFaltasDiscente) * 100 ) / numeroMaximoFaltas : 0; 
			creditosAprovados = totalCredito > 0 ? ( totalCreditoAprovados * 100 ) / totalCredito : 0;
			
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Retornar o Template do SIPAC para que seja possível pegar o id_pessoa do discente no SIPAC, para efetuar a consulta correta no sistema em questão.
	 * 
	 * @return
	 */
	private JdbcTemplate getSipacTemplate() {
		if (sipacTemplate == null)
			sipacTemplate = new JdbcTemplate(Database.getInstance().getSipacDs());
		return sipacTemplate;
	}
	
	/**
	 * Realizar a criação da data baseada nas informações do calendário acadêmico do discente
	 * 
	 * @param cal
	 * @param inicio
	 * @return
	 */
	private Date createDate(CalendarioAcademico cal, boolean inicio) {
		if (inicio) 
			return CalendarUtils.createDate(1, cal.getPeriodo() == 1 ? 0 : 6, cal.getAno());
		else
			return CalendarUtils.createDate(cal.getPeriodo() == 1 ? 30 : 31, cal.getPeriodo() == 1 ? 5 : 11, cal.getAno());
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	public List<AlunoDTO> getAlunoDto() {
		return alunoDto;
	}

	public void setAlunoDto(List<AlunoDTO> alunoDto) {
		this.alunoDto = alunoDto;
	}

	public void setSipacTemplate(JdbcTemplate sipacTemplate) {
		this.sipacTemplate = sipacTemplate;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public float getFrenquenciaTurma() {
		return frenquenciaTurma;
	}

	public void setFrenquenciaTurma(float frenquenciaTurma) {
		this.frenquenciaTurma = frenquenciaTurma;
	}

	public float getCreditosAprovados() {
		return creditosAprovados;
	}

	public void setCreditosAprovados(float creditosAprovados) {
		this.creditosAprovados = creditosAprovados;
	}
	
}