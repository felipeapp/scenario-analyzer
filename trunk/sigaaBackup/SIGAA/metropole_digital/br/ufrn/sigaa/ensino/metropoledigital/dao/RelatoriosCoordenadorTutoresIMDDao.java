package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RelatorioAcompanhamentoExecucaoFreqNotas;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
/**
 * 
 * Entidade responsável pelas consultas referentes aos relatórios da coordenação de tutores do IMD
 * 
 * @author Rafael Barros
 *
 */
public class RelatoriosCoordenadorTutoresIMDDao extends GenericSigaaDAO {

	
	/**
	 * Relatório de listagem do acompanhamento da execução da frequência e notas semanais das turmas
	 *  
	 * @param 
	 * @return
	 * @throws HibernateException, DAOException
	 */
	public Collection<RelatorioAcompanhamentoExecucaoFreqNotas> findAcompanhamentoExecFreqNotas() throws HibernateException, DAOException{									
		
		GenericSigaaDAO dao = new GenericSigaaDAO();
		
		try {
			String projecao =
					//INFORMAÇÕES DAS TURMAS E PERIODOS
					" DISTINCT turma.id, esp.descricao, per.id, per.dataInicio, per.datafim, " +
							
					//VERIFICAÇÃO DO ACOMPANHAMENTO DA FREQUÊNCIA
					" (SELECT  COUNT(*) " +
					" FROM AcompanhamentoSemanalDiscente a " +
					" WHERE a.periodoAvaliacao.id = per.id AND a.discente.turmaEntradaTecnico.id = turma.id AND a.discente.discente.status = "+StatusDiscente.ATIVO +" AND a.frequencia IS NULL),"+
	
					//VERIFICAÇÃO DO ACOMPANHAMENTO DA PARTICIPAÇÃO VIRTUAL
					" (SELECT  COUNT(*) " +
					" FROM AcompanhamentoSemanalDiscente a " +
					" WHERE a.periodoAvaliacao.id = per.id AND a.discente.turmaEntradaTecnico.id = turma.id AND a.discente.discente.status = "+StatusDiscente.ATIVO +" AND a.participacaoVirtual IS NULL),"+
	
					//VERIFICAÇÃO DO ACOMPANHAMENTO DA PARTICIPAÇÃO PRESENCIAL
					" (SELECT  COUNT(*) " +
					" FROM AcompanhamentoSemanalDiscente a " +
					" WHERE a.periodoAvaliacao.id = per.id AND a.discente.turmaEntradaTecnico.id = turma.id AND a.discente.discente.status = "+StatusDiscente.ATIVO +" AND a.participacaoPresencial IS NULL)";
					
			//CONSULTA GERAL
			String hql = "SELECT " + projecao +
					" FROM TurmaEntradaTecnico turma, EspecializacaoTurmaEntrada esp, DiscenteTecnico disc, AcompanhamentoSemanalDiscente acomp, PeriodoAvaliacao per, Discente d " +
					" WHERE turma.especializacao.id = esp.id AND turma.id = disc.turmaEntradaTecnico.id AND disc.id = acomp.discente.id AND acomp.periodoAvaliacao.id = per.id AND d.id = disc.id" +
					" AND d.status = " + StatusDiscente.ATIVO+ 
					" GROUP BY turma.id, esp.descricao, per.id, per.dataInicio, per.datafim" +
					" ORDER BY esp.descricao, per.dataInicio ";
			Query q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			Collection<Object[]> res = q.list();
			
			Collection<RelatorioAcompanhamentoExecucaoFreqNotas> listaRetorno = new ArrayList<RelatorioAcompanhamentoExecucaoFreqNotas>();
			
			Date dataAtual = new Date();
				
			for (Object[] reg : res) {
				int col = 0;
				
				Integer turmaId = (Integer) reg[col++];
				String especializacao = (String) reg[col++];
				Integer periodoId = (Integer) reg[col++];
				Date dataInicio = (Date) reg[col++];
				Date dataFim = (Date) reg[col++];
				Long frequencia = (Long) reg[col++];
				Long pv = (Long) reg[col++];
				Long pp = (Long) reg[col++];
				
				
				
				RelatorioAcompanhamentoExecucaoFreqNotas registro = new RelatorioAcompanhamentoExecucaoFreqNotas();
				
				if(turmaId != null) {
					TurmaEntradaTecnico turma = dao.findByPrimaryKey(turmaId, TurmaEntradaTecnico.class);
					registro.setTurmaEntrada(turma);
					turma.getEspecializacao().setDescricao(especializacao);
				}
				
				
				if(periodoId != null) {
					PeriodoAvaliacao periodo = dao.findByPrimaryKey(periodoId, PeriodoAvaliacao.class);
					registro.setPeriodo(periodo);
				}
				
				if(dataInicio.after(dataAtual) || (! dataInicio.before(dataAtual) && ! dataInicio.after(dataAtual)) ){
					registro.setFrequenciaExecutada(-1);
					registro.setPpExecutada(-1);
					registro.setPvExecutada(-1);
				} else {
					
					if(frequencia != null) {
						if(frequencia > 0){
							registro.setFrequenciaExecutada(0);
						} else {
							registro.setFrequenciaExecutada(1);
						}
					} else {
						registro.setFrequenciaExecutada(1);
					}
					
					if(pv != null) {
						if(pv > 0){
							registro.setPvExecutada(0);
						} else {
							registro.setPvExecutada(1);
						}
					} else {
						registro.setPvExecutada(1);
					}
					
					
					if(pp != null) {
						if(pp > 0){
							registro.setPpExecutada(0);
						} else {
							registro.setPpExecutada(1);
						}
					} else {
						registro.setPpExecutada(1);
					}
					
					
				}
				
				
				
				listaRetorno.add(registro);
			
			}		
			
			
			return listaRetorno;
		} finally {
			dao.close();
		}
	}
	
}
