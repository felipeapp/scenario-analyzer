/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * created on 23/11/2011
 * 
 */
package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO com consultas utilizadas no caso de uso de alterar/remover turma 
 * 
 * @author Henrique André
 */
public class GerenciarTurmaDao extends GenericSigaaDAO {
	
	/**
	 * Busca todas as subturmas da turma passada como parâmetro
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findSubturmasByTurma( Turma turma ) throws DAOException{
		if( turma == null || turma.getId() == 0 )
			return new ArrayList<Turma>();

		String hql = 	" select distinct t from Turma t " +
						"	inner join t.turmaAgrupadora agrupadora " +
						"	inner join fetch t.situacaoTurma sit " +
						"	left  join fetch t.docentesTurmas dts " +
						"	left  join fetch dts.docente docente " +
						"	left  join fetch t.horarios horariosTurma " +
						"	left  join dts.horarios horariosDocente " +
						" where agrupadora.id = :idTurma " +
						"	and sit.id in " + gerarStringIn( SituacaoTurma.getSituacoesValidas() ) + 
						" order by t.codigo ";
		
		@SuppressWarnings("unchecked")
		List<Turma> lista = getSession().createQuery(hql).setInteger("idTurma", turma.getId()).list();
		return lista;
	}
	
	/**
	 * Retorna as turmas do docente considerando o ano/período
	 *
	 * @param pessoa
	 * @param docenteExterno
	 * @param periodo
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocenteExternoServidor(final Pessoa pessoa, boolean docenteExterno, final Integer ano, final Integer periodo) throws DAOException {
		
		Integer[] situacoes = new Integer[] {SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.CONSOLIDADA};
		
		String projecao =
				" t.id, t.dataInicio, t.dataFim, t.ano, t.periodo, t.agrupadora, t.codigo, " +
				" agrupadora.id, " + 
				" disciplina.id, disciplina.codigo, disciplina.nivel, " +
				" uniDisciplina.id, uniDisciplina.tipoAcademica, " +
				" ht.id, ht.dia, ht.horaInicio, ht.horaFim, ht.dataInicio, ht.dataFim, ht.tipo, " + 
				" horario.id, horario.inicio, horario.fim, horario.ordem, horario.tipo, horario.ativo, horario.nivel, " + 
				" todosDocentesTurma.id, todosDocentesTurma.chDedicadaPeriodo, docente.id, pessoaInstituicao.id, pessoaInstituicao.nome, pessoaInstituicao.cpf_cnpj, " +
				" docenteExterno.id, pessoaExterno.id, pessoaExterno.nome, pessoaExterno.cpf_cnpj, " +
				" horarioDocente.id, horarioDocente.dia, horarioDocente.dataInicio, horarioDocente.dataFim, horarioDocente.tipo, " + 
				" hDoc.id, hDoc.inicio, hDoc.fim, hDoc.ordem, hDoc.tipo, hDoc.ativo, hDoc.nivel, " + 
				" poloTurma.id, " + 
				" curso.id, mod.id, conv.id ";
		
		StringBuilder consulta = new StringBuilder();
		consulta.append("select ");
		consulta.append(projecao);
		consulta.append(" from DocenteTurma dts " +
						"	left  join dts.docenteExterno docEx " +
						"	left  join dts.docente doc " + 
						"	inner  join dts.turma t " +
						"	inner  join t.docentesTurmas todosDocentesTurma " +
						"	inner  join t.disciplina disciplina " +
						"	inner  join disciplina.unidade uniDisciplina " +
						"	left   join t.turmaAgrupadora agrupadora " + 
						"	inner  join t.situacaoTurma sit " +
						"	left   join t.horarios ht " +
						"	left   join ht.horario horario " +
						"	left   join todosDocentesTurma.docente docente " +
						"	left   join docente.pessoa pessoaInstituicao " +
						"	left   join todosDocentesTurma.docenteExterno docenteExterno " +
						"	left   join docenteExterno.pessoa pessoaExterno " +
						"	left   join todosDocentesTurma.horarios horarioDocente " +
						"	left   join horarioDocente.horario hDoc " +
						"	left   join t.polo poloTurma " +
						"	left   join t.curso curso " +
						"	left   join curso.modalidadeEducacao mod " +
						"	left   join curso.convenio conv " + 
						" where t.ano = " + ano + 
						"	and t.periodo = " + periodo + 
						"	and (doc.pessoa.id = " + pessoa.getId() + 
						"   or docEx.pessoa.id = " + pessoa.getId() + ") "+
						"	and sit.id in " + gerarStringIn(situacoes) +
						" order by t.id, ht.id, horario.id, todosDocentesTurma.id, docente.id, docenteExterno.id, horarioDocente.id, hDoc.id ");

		Query q = getSession().createQuery(consulta.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
	
		ProjectionResultSet rs = ProjectionUtil.execute(linhas, projecao);
		Map<Integer, Turma> resultado = new HashMap<Integer, Turma>();
		
		while(rs.next()) {
			Integer idTurmaAtual = rs.getInt("t.id");
			
			Turma turma = new Turma();
			
			if (resultado.containsKey(idTurmaAtual)) {
				turma = resultado.get(idTurmaAtual);
			} else {
				resultado.put(idTurmaAtual, turma);
				turma.setId(idTurmaAtual);
				turma.setDataInicio(rs.getDate("t.dataInicio"));
				turma.setDataFim(rs.getDate("t.dataFim"));
				turma.setAno(rs.getInt("t.ano"));
				turma.setPeriodo(rs.getInt("t.periodo"));
				turma.setAgrupadora(rs.getBoolean("t.agrupadora"));
				turma.setCodigo(rs.getString("t.codigo"));
				
				Integer idTurmaAgrupadora = rs.getInt("agrupadora.id");
				if (isNotEmpty(idTurmaAgrupadora)) {
					turma.setTurmaAgrupadora(new Turma());
					turma.getTurmaAgrupadora().setId(idTurmaAgrupadora);
				}
				turma.setDisciplina(new ComponenteCurricular());
				turma.getDisciplina().setId(rs.getInt("disciplina.id"));
				turma.getDisciplina().setCodigo(rs.getString("disciplina.codigo"));
				turma.getDisciplina().setNivel(rs.getString("disciplina.nivel").charAt(0));
				turma.getDisciplina().setUnidade(new Unidade());
				turma.getDisciplina().getUnidade().setId(rs.getInt("uniDisciplina.id"));
				turma.getDisciplina().getUnidade().setTipoAcademica(rs.getInt("uniDisciplina.tipoAcademica"));
				
				
				if ( isNotEmpty(rs.getInt("poloTurma.id")) ) {
					turma.setPolo(new Polo());
					turma.getPolo().setId(rs.getInt("poloTurma.id"));
				}

				if ( isNotEmpty(rs.getInt("curso.id")) ) {
					
					Curso curso = new Curso();
					
					curso.setId(rs.getInt("curso.id"));
					
					if (isNotEmpty(rs.getInt("mod.id")))
						curso.setModalidadeEducacao(new ModalidadeEducacao(rs.getInt("mod.id")));
					
					if ( isNotEmpty(rs.getInt("conv.id")) ) {
						curso.setConvenio(new ConvenioAcademico());
						curso.getConvenio().setId(rs.getInt("conv.id"));
					}
					
					turma.setCurso(curso);
				}
			}
			
			
			Integer idHorarioTurma = rs.getInt("ht.id");
			if (isNotEmpty(idHorarioTurma)) {
				
				if (turma.getHorarios() == null)
					turma.setHorarios(new ArrayList<HorarioTurma>());

				
				HorarioTurma ht = new HorarioTurma();
				ht.setId(idHorarioTurma);
				ht.setDia(rs.getString("ht.dia").charAt(0));
				ht.setHoraInicio(rs.getDate("ht.horaInicio"));
				ht.setHoraFim(rs.getDate("ht.horaFim"));
				ht.setDataInicio(rs.getDate("ht.dataInicio"));
				ht.setDataFim(rs.getDate("ht.dataFim"));
				ht.setTipo(rs.getInt("ht.tipo"));
				ht.setTurma(turma);
				
				Horario horario = new Horario();
				horario.setId(rs.getInt("horario.id"));
				horario.setInicio(rs.getDate("horario.inicio"));
				horario.setFim(rs.getDate("horario.fim"));
				horario.setOrdem(rs.getShort("horario.ordem"));
				horario.setTipo(rs.getShort("horario.tipo"));
				horario.setAtivo(rs.getBoolean("horario.ativo"));
				horario.setNivel(rs.getString("horario.ativo").charAt(0));
				
				ht.setHorario(horario);
				
				if (!turma.getHorarios().contains(ht))
					turma.getHorarios().add(ht);
			}
			
			Integer idDocenteTurmaAtual = rs.getInt("todosDocentesTurma.id");
			if (isNotEmpty(idDocenteTurmaAtual)) {
				
				DocenteTurma dt = null;
				
				for (DocenteTurma dtTemp : turma.getDocentesTurmas()) {
					if (dtTemp.getId() == idDocenteTurmaAtual)
						dt = dtTemp;
				}
				
				if (dt == null) {
					dt = new DocenteTurma();
					dt.setId(idDocenteTurmaAtual);
					dt.setChDedicadaPeriodo(rs.getInt("todosDocentesTurma.chDedicadaPeriodo"));
					
					// Popula docente da instituição
					Integer idDocente = rs.getInt("docente.id");
					if (isNotEmpty(idDocente)) {
						Servidor servidor = new Servidor();
						servidor.setId(idDocente);
						servidor.setPessoa(new Pessoa());
						servidor.getPessoa().setId(rs.getInt("pessoaInstituicao.id"));
						servidor.getPessoa().setNome(rs.getString("pessoaInstituicao.nome"));
						servidor.getPessoa().setCpf_cnpj(rs.getLong("pessoaInstituicao.cpf_cnpj"));
						
						dt.setDocente(servidor);
					} else {
						DocenteExterno de = new DocenteExterno();
						de.setId(rs.getInt("docenteExterno.id"));
						de.setPessoa(new Pessoa());
						de.getPessoa().setId(rs.getInt("pessoaExterno.id"));
						de.getPessoa().setNome(rs.getString("pessoaExterno.nome"));
						de.getPessoa().setCpf_cnpj(rs.getLong("pessoaExterno.cpf_cnpj"));
						
						dt.setDocenteExterno(de);
						dt.setDocente(null);
					}
					
					if (turma.getDocentesTurmas() == null)
						turma.setDocentesTurmas(new HashSet<DocenteTurma>());
					
					turma.getDocentesTurmas().add(dt);						
				}
				
				if (dt != null) {
					Integer idHorarioDocente = rs.getInt("horarioDocente.id");
					if (isNotEmpty(idHorarioDocente)) {
						HorarioDocente hDocente = new HorarioDocente();
						hDocente.setId(idHorarioDocente);
						hDocente.setDia(rs.getString("horarioDocente.dia").charAt(0));
						hDocente.setDataInicio(rs.getDate("horarioDocente.dataInicio"));
						hDocente.setDataFim(rs.getDate("horarioDocente.dataFim"));
						hDocente.setTipo(rs.getInt("horarioDocente.tipo"));
						
						Horario horario = new Horario();
						horario.setId(rs.getInt("hDoc.id"));
						horario.setInicio(rs.getDate("hDoc.inicio"));
						horario.setFim(rs.getDate("hDoc.fim"));
						horario.setOrdem(rs.getShort("hDoc.ordem"));
						horario.setTipo(rs.getShort("hDoc.tipo"));
						horario.setAtivo(rs.getBoolean("hDoc.ativo"));
						horario.setNivel(rs.getString("hDoc.nivel").charAt(0));						
						
						hDocente.setHorario(horario);
						
						if (dt.getHorarios() == null)
							dt.setHorarios(new ArrayList<HorarioDocente>());

						if (!dt.getHorarios().contains(hDocente))
							dt.getHorarios().add(hDocente);
					}
				}
			}
			
		}
		
		return resultado.values();
	}

}