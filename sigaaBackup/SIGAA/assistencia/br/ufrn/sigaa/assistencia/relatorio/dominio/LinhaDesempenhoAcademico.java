package br.ufrn.sigaa.assistencia.relatorio.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.AlunoDTO;
import br.ufrn.sigaa.assistencia.dao.DesempenhoAcademicoBolsistaDao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Usada para agregar informações obtidas através do relatório de desempenho Acadêmico 
 * 
 * @author Jean Guerethes
 */
public class LinhaDesempenhoAcademico {
	
	/** Linha apresentada no relatório */
	private int linha;
	/** Tipo de Bolsas do Discente bolsista */
	private int idTipoBolsa;
	/** Descrição do tipo de bolsa do Discente Bolsista */
	private String bolsa;
	/** Alunos bolsistas encontrados no SIPAC */
	private List<AlunoDTO> alunos = new ArrayList<AlunoDTO>();
	/** Total de bolsistas encontrados */
	private Float totalBolsistas;
	/** Total de bolsistas prioritários encontrados */
	private Float totalPrioritarios;
	/** Total de bolsistas não prioritários encontrados */
	private Float totalNPrioritarios;
	/** Total de bolsistas prioritários e com reprovação encontrados */
	private Float totalReprovadosP;
	/** Total de bolsistas não prioritários e com reprovação encontrados */
	private Float totalReprovadosNP;
	/** Total de bolsistas prioritários e com trancamento encontrados */
	private Float totalTrancadosP;
	/** Total de bolsistas não prioritários e com trancamento encontrados */
	private Float totalTrancadosNP;
	/** Total de bolsistas prioritários, com trancamento e/ou com reprovação encontrados */
	private Float totalTrancadosReprovadosP;
	/** Total de bolsistas não prioritários, com trancamento e/ou com reprovação encontrados */
	private Float totalTrancadosReprovadosNP;

	public int getIdTipoBolsa() {
		return idTipoBolsa;
	}

	public void setIdTipoBolsa(int idTipoBolsa) {
		this.idTipoBolsa = idTipoBolsa;
	}

	public String getBolsa() {
		return bolsa;
	}

	public void setBolsa(String bolsa) {
		this.bolsa = bolsa;
	}

	public List<AlunoDTO> getAlunos() {
		return alunos;
	}

	public void setAlunos(List<AlunoDTO> alunos) {
		this.alunos = alunos;
	}

	public Float getTotalPrioritarios() {
		return totalPrioritarios;
	}

	public void setTotalPrioritarios(Float totalPrioritarios) {
		this.totalPrioritarios = totalPrioritarios;
	}

	public Float getTotalReprovadosP() {
		return totalReprovadosP;
	}

	public void setTotalReprovadosP(Float totalReprovadosP) {
		this.totalReprovadosP = totalReprovadosP;
	}

	public Float getTotalReprovadosNP() {
		return totalReprovadosNP;
	}

	public void setTotalReprovadosNP(Float totalReprovadosNP) {
		this.totalReprovadosNP = totalReprovadosNP;
	}

	public Float getTotalTrancadosP() {
		return totalTrancadosP;
	}

	public void setTotalTrancadosP(Float totalTrancadosP) {
		this.totalTrancadosP = totalTrancadosP;
	}

	public Float getTotalTrancadosNP() {
		return totalTrancadosNP;
	}

	public void setTotalTrancadosNP(Float totalTrancadosNP) {
		this.totalTrancadosNP = totalTrancadosNP;
	}

	public Float getTotalTrancadosReprovadosP() {
		return totalTrancadosReprovadosP;
	}

	public void setTotalTrancadosReprovadosP(Float totalTrancadosReprovadosP) {
		this.totalTrancadosReprovadosP = totalTrancadosReprovadosP;
	}

	public Float getTotalTrancadosReprovadosNP() {
		return totalTrancadosReprovadosNP;
	}

	public void setTotalTrancadosReprovadosNP(Float totalTrancadosReprovadosNP) {
		this.totalTrancadosReprovadosNP = totalTrancadosReprovadosNP;
	}

	public Float getTotalBolsistas() {
		return totalBolsistas;
	}

	public void setTotalBolsistas(Float totalBolsistas) {
		this.totalBolsistas = totalBolsistas;
	}

	public Float getTotalNPrioritarios() {
		return totalNPrioritarios;
	}

	public void setTotalNPrioritarios(Float totalNPrioritarios) {
		this.totalNPrioritarios = totalNPrioritarios;
	}
	
	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

	/**
	 * Compara se o id passado já se encontra dentro do coleção informada
	 * 
	 * @param id
	 * @param desempenhoAcademico
	 * @return
	 */
	public static boolean compareTo(int id, Collection<LinhaDesempenhoAcademico> desempenhoAcademico) {
		for (LinhaDesempenhoAcademico desemp : desempenhoAcademico) {
			if ( desemp.getIdTipoBolsa() == id )
				return false;
		}
		return true;
	}

	/**
	 * Compara se o id passado já se encontra dentro do coleção informada
	 * 
	 * @param id
	 * @param alunos
	 * @return
	 */
	public static boolean compareToBolsas(int id, Collection<AlunoDTO> alunos) {
		for (AlunoDTO alu : alunos) {
			if ( alu.getIdBolsa() == id )
				return true;
		}
		return false;
	}
	
	/** Métodos 
	 * @throws DAOException 
	 * @throws HibernateException */
	
	public Collection<Discente> getNumeroTotalBolsistas(int ano, int periodo) throws HibernateException, DAOException{
		List<String> matriculaDiscentes = new ArrayList<String>();
		
		for (AlunoDTO aluno : alunos) {
			matriculaDiscentes.add(aluno.getMatricula());
		}
		
		DesempenhoAcademicoBolsistaDao dao = DAOFactory.getInstance().getDAO(DesempenhoAcademicoBolsistaDao.class);
		Collection<Discente> discentes;
		try {
			discentes = dao.findByDiscentes(matriculaDiscentes, ano, periodo, false, false, null);
		} finally {
			dao.close();
		}
		return discentes;
	}
	
	/**
	 * Retorna o total de Discente Bolsistas prioritários de acordo com o ano e período informandos.
	 * 
	 * @param ano
	 * @param periodo
	 * @param prioritarios
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Discente> getTotalBolsistasPrioritario(int ano, int periodo, boolean prioritarios) throws HibernateException, 
		DAOException{
		
		List<String> matriculaDiscentes = carregarMatricula(prioritarios);

		DesempenhoAcademicoBolsistaDao dao = DAOFactory.getInstance().getDAO(DesempenhoAcademicoBolsistaDao.class);
		Collection<Discente> discentes;
		try {
			discentes = dao.findByDiscentes(matriculaDiscentes, ano, periodo, false, false, null);
		} finally {
			dao.close();
		}
		return discentes;
	}

	/**
	 * Serve para auxiliar criando uma lista com todas as matrículas dos discentes bolsistas ou não bolsistas encontrados.
	 * 
	 * @param prioritarios
	 * @return
	 */
	public List<String> carregarMatricula(boolean prioritarios) {
		List<String> matriculaDiscentes = new ArrayList<String>();
		
		for (AlunoDTO aluno : alunos) {
			if (prioritarios) {
				if (aluno.getCarente())
					matriculaDiscentes.add(aluno.getMatricula());
			}else{
				if (!aluno.getCarente())
					matriculaDiscentes.add(aluno.getMatricula());
			}
		}
		return matriculaDiscentes;
	}
	
	/**
	 * Carrega os Bolsistas com Reprovação de acordo com a bolsa, ano, período e priotiários ou não.
	 * 
	 * @param idTipoBolsa
	 * @param ano
	 * @param periodo
	 * @param prioritarios
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Discente> carregarBolsistasReprovados(int idTipoBolsa, int ano, int periodo, boolean prioritarios) 
		throws HibernateException, DAOException{
		
		List<String> matriculaDiscentes = carregarMatricula(prioritarios);
		Collection<Discente> discentes = new ArrayList<Discente>();

		if (!matriculaDiscentes.isEmpty()) {
			DesempenhoAcademicoBolsistaDao dao = DAOFactory.getInstance().getDAO(DesempenhoAcademicoBolsistaDao.class);
			try {
				discentes = dao.findByDiscentes(matriculaDiscentes, ano, periodo, true, false, SituacaoMatricula.REPROVADO, 
						SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA);
			} finally {
				dao.close();
			}
		}
		return discentes;
	}

	/**
	 * Carrega os Bolsistas com Trancamento de acordo com a bolsa, ano, período e priotiários ou não.
	 * 
	 * @param idTipoBolsa
	 * @param ano
	 * @param periodo
	 * @param prioritarios
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Discente> carregarBolsistasTrancados(int idTipoBolsa, int ano, int periodo, boolean prioritarios) 
		throws HibernateException, DAOException{
	
		List<String> matriculaDiscentes = carregarMatricula(prioritarios);
		Collection<Discente> discentes = new ArrayList<Discente>();
	
		if (!matriculaDiscentes.isEmpty()) {
			DesempenhoAcademicoBolsistaDao dao = DAOFactory.getInstance().getDAO(DesempenhoAcademicoBolsistaDao.class);
			try {
				discentes = dao.findByDiscentes(matriculaDiscentes, ano, periodo, true, false, SituacaoMatricula.TRANCADO);
			} finally {
				dao.close();
			}
		}
		return discentes;
	}

	/**
	 * Carrega os Bolsistas sem reprovação e sem trancamento de acordo com a bolsa, ano, período e priotiários ou não.
	 * 
	 * @param idTipoBolsa
	 * @param ano
	 * @param periodo
	 * @param prioritarios
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Discente> carregarBolsistasSemTrancamentoESemReprovacao(int idTipoBolsa, int ano, int periodo, boolean prioritarios) 
		throws HibernateException, DAOException{

		List<String> matriculaDiscentes = carregarMatricula(prioritarios);
	
		Collection<Discente> discentes = new ArrayList<Discente>();
		
		if (!matriculaDiscentes.isEmpty()) {
			DesempenhoAcademicoBolsistaDao dao = DAOFactory.getInstance().getDAO(DesempenhoAcademicoBolsistaDao.class);
			try {
				discentes = dao.findByDiscentes(matriculaDiscentes, ano, periodo, true, true, SituacaoMatricula.REPROVADO, 
						SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA, SituacaoMatricula.TRANCADO);
			} finally {
				dao.close();
			}
		}		
		return discentes;
	}

}