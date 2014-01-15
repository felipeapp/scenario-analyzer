package br.ufrn.sigaa.assistencia.dao;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;
import br.ufrn.sigaa.assistencia.relatorio.dominio.LinhaMovimentacaoDiscente;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável pelas consultas relativas 
 * as movimentações dos bolsistas.
 * 
 * @author Jean Guerethes
 */
public class MovimentacaoDiscenteDao extends GenericSigaaDAO {

	/**
	 * Busca as movimentações dos discente que possuem bolsa alimentação no ano e período selecionados.
	 * 
	 * @param matricula
	 * @param ano
	 * @param periodo
	 * @param verificarSituacao
	 * @param trancadosReprovados
	 * @param situacoes
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LinhaMovimentacaoDiscente> findByMovimentacaoDiscente( int ano, int periodo, List<String> tipoRelatorio ) throws HibernateException, DAOException {

		StringBuilder sql = new StringBuilder();
		String projecao = "select distinct d.matricula, mc.id_componente_curricular, mc.id_discente, d.status, p.nome, mcd.nome as disciplina, t.descricao_horario, da.segunda, da.terca, da.quarta, da.quinta, da.sexta, da.sabado, da.domingo, da.id_tipo_refeicao_ru, da.id_bolsa_auxilio, mcd.codigo";
		
		if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.TRANCAMENTO_COMPONENTE) ) {
			
			sql.append(projecao + 
					" from sae.bolsa_auxilio ba" +
					" join ensino.matricula_componente mc using ( id_discente )" +
					" join discente d using ( id_discente )" +
					" join ensino.turma t using ( id_turma )" +
					" join ensino.horario_turma ht using ( id_turma )" +
					" join sae.dias_alimentacao da on ( ba.id_bolsa_auxilio = da.id_bolsa_auxilio )" +
					" join comum.pessoa p on ( d.id_pessoa = p.id_pessoa )" +
					" join ensino.componente_curricular_detalhes mcd on ( mc.id_componente_detalhes = mcd.id_componente_detalhes )" +
					" join sae.bolsa_auxilio_periodo bap on ( bap.id_bolsa_auxilio = da.id_bolsa_auxilio )" +
					" where ba.id_tipo_bolsa_auxilio = " + TipoBolsaAuxilio.ALIMENTACAO + " and ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA +
					" and mc.ano = :ano and mc.periodo = :periodo and mc.id_situacao_matricula = " + SituacaoMatricula.TRANCADO.getId()  + 
					" and bap.ano = :ano and bap.periodo = :periodo and d.status = " + StatusDiscente.ATIVO);
		}
		
		if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.TRANCAMENTO_CURSO, LinhaMovimentacaoDiscente.CANCELAMENTO_CURSO) ) {
			
			if ( sql.length() > 0 )
				sql.append(" union ");
			
			List<Integer> statusMatricula = new ArrayList<Integer>();
			List<Integer> statusDiscente = new ArrayList<Integer>();
			
			if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.TRANCAMENTO_CURSO) ) {
				statusMatricula.add( SituacaoMatricula.TRANCADO.getId() );
				statusDiscente.add( StatusDiscente.TRANCADO );
			}
			if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.CANCELAMENTO_CURSO) ) {
				statusMatricula.add( SituacaoMatricula.CANCELADO.getId() );
				statusMatricula.add( SituacaoMatricula.TRANCADO.getId() );
				statusDiscente.add( StatusDiscente.CANCELADO );
				statusDiscente.add( StatusDiscente.TRANCADO );
			}
			
		sql.append(projecao +
				" from sae.bolsa_auxilio ba" +
				" join ensino.matricula_componente mc using ( id_discente )" +
				" join discente d using ( id_discente )" +
				" join ensino.turma t using ( id_turma )" +
				" join ensino.horario_turma ht using ( id_turma )" +
				" join sae.dias_alimentacao da on ( ba.id_bolsa_auxilio = da.id_bolsa_auxilio )" +
				" join comum.pessoa p on ( d.id_pessoa = p.id_pessoa )" +
				" join ensino.componente_curricular_detalhes mcd on ( mc.id_componente_detalhes = mcd.id_componente_detalhes )" +
				" join sae.bolsa_auxilio_periodo bap on ( bap.id_bolsa_auxilio = da.id_bolsa_auxilio )" +
				" where ba.id_tipo_bolsa_auxilio = " + TipoBolsaAuxilio.ALIMENTACAO + " and ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA +
				" and mc.ano = :ano and mc.periodo = :periodo and mc.id_situacao_matricula in " + UFRNUtils.gerarStringIn( statusMatricula ) +
				" and mc.id_discente in (" +
						" select id_discente" +
						" from ensino.movimentacao_aluno" +
						" where id_tipo_movimentacao_aluno in (" +
						" select id_tipo_movimentacao_aluno from ensino.tipo_movimentacao_aluno where ( ");
								if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.CANCELAMENTO_CURSO) )			
									sql.append( " statusdiscente in  " +  UFRNUtils.gerarStringIn( new int[] {StatusDiscente.CANCELADO, StatusDiscente.TRANCADO }) + " " );
								if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.TRANCAMENTO_CURSO) )
									sql.append( statusDiscente.contains(StatusDiscente.CANCELADO) ? " or id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO + " ) " : 
										" id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO + " ) ");
								sql.append(" and ativo " +  
						" and ano_referencia = :ano and periodo_referencia = :periodo )"); 
					if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.TRANCAMENTO_CURSO) ) 
						sql.append(")");
					else 
						sql.append(") )");
				sql.append(" and bap.ano = :ano and bap.periodo = :periodo and d.status in " + UFRNUtils.gerarStringIn( statusDiscente ) );
		}

		if ( LinhaMovimentacaoDiscente.contains(tipoRelatorio, LinhaMovimentacaoDiscente.CONCLUSAO_CURSO) ) {

			if ( sql.length() > 0 )
				sql.append(" union ");
		
		sql.append(projecao + 
				" from sae.bolsa_auxilio ba" +
				" join ensino.matricula_componente mc using ( id_discente )" +
				" join discente d using ( id_discente )" +
				" join ensino.turma t using ( id_turma )" +
				" join ensino.horario_turma ht using ( id_turma ) " +
				" join sae.dias_alimentacao da on ( ba.id_bolsa_auxilio = da.id_bolsa_auxilio )" +
				" join comum.pessoa p on ( d.id_pessoa = p.id_pessoa ) " +
				" join ensino.componente_curricular_detalhes mcd on ( mc.id_componente_detalhes = mcd.id_componente_detalhes )" +
				" join sae.bolsa_auxilio_periodo bap on ( bap.id_bolsa_auxilio = da.id_bolsa_auxilio )" +
				" where  ba.id_tipo_bolsa_auxilio = " + TipoBolsaAuxilio.ALIMENTACAO + " and ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA +
				" and mc.ano = :ano and mc.periodo = :periodo and mc.id_situacao_matricula = " + SituacaoMatricula.APROVADO.getId() + 
				" and bap.ano = :ano and bap.periodo = :periodo and d.status in " + UFRNUtils.gerarStringIn(new int[]{ StatusDiscente.GRADUANDO, StatusDiscente.CONCLUIDO }));
		}
		
		sql.append(" order by status, matricula, disciplina, nome, id_tipo_refeicao_ru");
		
		Query query = getSession().createSQLQuery(sql.toString());
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
	
		List<Object[]> lista = query.list();
		Collection<LinhaMovimentacaoDiscente> result = new ArrayList<LinhaMovimentacaoDiscente>();
		List<LinhaMovimentacaoDiscente> parcial = new ArrayList<LinhaMovimentacaoDiscente>();
		long matriculaAtual = 0;
		int componenteAtual = 0;
		
		if ( !lista.isEmpty() ) {
			for (Object[] item : lista) {
				int k = 0;
				
				long matricula = ((BigInteger) item[k++]).longValue();
				int componenteCurricular = (Integer) item[k++];
				
				if ( ( componenteAtual != 0 && componenteAtual == componenteCurricular ) && ( matriculaAtual != 0 && matricula == matriculaAtual ) ) {
					DiasAlimentacao dias = new DiasAlimentacao();
					dias = montarDiasAlimentacao(dias, item, 7);
					parcial.get(parcial.size() - 1).getDiasAlimentacao().add(dias);
				} 
				
				else if ( componenteAtual == 0 && matriculaAtual == 0 ) {
					addBolsista(parcial, item, k, matricula);
				}
				
				else if ( componenteAtual != 0 && ( ( matriculaAtual != 0 && matricula != matriculaAtual ) || ( componenteAtual != 0 && componenteCurricular != componenteAtual ) ) ) {
					result.addAll( parcial );
					parcial.clear();
					addBolsista(parcial, item, k, matricula);
				}
				
				matriculaAtual = matricula;
				componenteAtual = componenteCurricular;
			}	

			if ( !parcial.get(parcial.size() - 1).getDiasAlimentacao().isEmpty() )
				result.addAll( parcial );
		}		
		
		return result;
	}

	/**
	 * Adiciona o bolsista a resultado a consulta
	 * 
	 * @param parcial
	 * @param item
	 * @param k
	 * @param matricula
	 */
	private void addBolsista(List<LinhaMovimentacaoDiscente> parcial, Object[] item, int k, long matricula) {
		LinhaMovimentacaoDiscente linha = new LinhaMovimentacaoDiscente();
		
		linha.setDiscente(new Discente());
		linha.getDiscente().setPessoa( new Pessoa() );
		
		linha.getDiscente().setMatricula( matricula );
		linha.getDiscente().setId( (Integer) item[k++] );
		linha.getDiscente().setStatus( ((Short) item[k++]).intValue() );
		linha.getDiscente().getPessoa().setNome( (String) item[k++] );
		linha.setMatriculaComponente( (String) item[k++] );
		linha.setDescricaoHorario( (String) item[k++] );
		
		DiasAlimentacao dias = new DiasAlimentacao();
		dias = montarDiasAlimentacao(dias, item, k);
		
		String codigo = (String) item[16];

		linha.setMatriculaComponente( codigo + " - " + linha.getMatriculaComponente() );
		
		linha.setDiasAlimentacao(new ArrayList<DiasAlimentacao>());
		linha.getDiasAlimentacao().add(dias);

		parcial.add(linha);
	}

	/**
	 * Reponsável pela montagem dos dias de alimentação do bolsista alimentação.
	 * 
	 * @param dias
	 * @param item
	 * @param k
	 * @return
	 */
	private DiasAlimentacao montarDiasAlimentacao(DiasAlimentacao dias, Object[] item, int k) {
		
		dias.setSegunda( (Boolean) item[k++] );
		dias.setTerca(   (Boolean) item[k++] );
		dias.setQuarta(  (Boolean) item[k++] );
		dias.setQuinta(  (Boolean) item[k++] );
		dias.setSexta(   (Boolean) item[k++] );
		dias.setSabado(  (Boolean) item[k++] );
		dias.setDomingo( (Boolean) item[k++] );

		dias.setTipoRefeicao(new TipoRefeicaoRU());
		dias.getTipoRefeicao().setId( (Integer) item[k++] );

		dias.setBolsaAuxilio(new BolsaAuxilio());
		dias.getBolsaAuxilio().setId( (Integer) item[k++] );
		
		return dias;
	}

}