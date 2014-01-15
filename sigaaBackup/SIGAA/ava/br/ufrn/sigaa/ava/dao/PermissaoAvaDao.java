package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para consultas relacionadas a PermissãoAva.
 * 
 * @author Diego Jácome
 *
 */
public class PermissaoAvaDao extends GenericSigaaDAO {

	/**
	 * Retorna as turmas que estão habilitadas para determinada pessoa, utilizada no Portal do Docente e Discente   
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTurmasHabilitadasByPessoaOtimizado(Pessoa pessoa) throws DAOException {
		try {

			String projecao = " select t.id_turma , t.id_disciplina , d.codigo , cd.nome , cd.ch_total , cd.cr_aula , cd.cr_estagio , " +
							  " cd.cr_laboratorio , d.nivel , t.ano , t.periodo , t.distancia , t.descricao_horario , t.local , " +
							  "	t.codigo as tCod , t.id_situacao_turma , s.descricao , t.capacidade_aluno ," +
							  " t.agrupadora , t.id_polo , c.nome as cNome , uf.id_unidade_federativa , uf.sigla , " +
						      " st.id_turma as subId , st.id_turma_agrupadora , st.ano as subAno , st.periodo as subPeriodo , st.local as subLocal , " +
						      " st.codigo as subCod , st.id_situacao_turma as subSit, sst.descricao as subDesc , st.descricao_horario as subHor , " +
						      " st.capacidade_aluno as subCap ";
			
			String sql = projecao +
						 " from ava.permissao_ava p "+
						 " left join ensino.turma t on t.id_turma = p.id_turma " +
						 " left join ensino.componente_curricular d on d.id_disciplina = t.id_disciplina " +
						 " left join ensino.componente_curricular_detalhes cd on d.id_detalhe = cd.id_componente_detalhes " +
						 " left join ensino.situacao_turma s on s.id_situacao_turma = t.id_situacao_turma " +
						 " left join ead.polo pl on pl.id_polo = t.id_polo " +
						 " left join comum.municipio c on pl.id_cidade = c.id_municipio " +
						 " left join comum.unidade_federativa uf on uf.id_unidade_federativa = c.id_unidade_federativa " +
						 " left join ensino.turma st on st.id_turma_agrupadora = t.id_turma " +
						 " left join ensino.componente_curricular sd on sd.id_disciplina = st.id_disciplina " +
						 " left join ensino.situacao_turma sst on sst.id_situacao_turma = st.id_situacao_turma " +
						 " where p.id_pessoa = "+pessoa.getId()+" and t.id_situacao_turma in "+gerarStringIn( new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA}) +
						 " order by t.ano desc , t.periodo desc , d.nivel , cd.nome, t.codigo , st.codigo asc";

			
			
			Query  q = getSession().createSQLQuery(sql);
			
			List<Turma> turmas = new ArrayList<Turma>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			
			if ( result != null ){

				Integer oldId = null;
				Integer newId = null;
				Boolean isNovaTurma;
				Turma t = null;
				 
				for ( Object[] linha : result ) {
					 
					 Integer i = 0;				 
					 newId = (Integer) linha[i++];	
					 
					 if ( newId.equals(oldId) ) isNovaTurma = false; else isNovaTurma = true;
					 
					 if (isNovaTurma){
						 
						 t = new Turma();
						 ComponenteCurricular d = new ComponenteCurricular();
						 ComponenteDetalhes cd = new ComponenteDetalhes();
						 SituacaoTurma s = new SituacaoTurma();

						 			 
						 t.setId(newId);
						 d.setId((Integer)linha[i++]);
						 d.setCodigo((String)linha[i++]);
						 cd.setNome((String)linha[i++]);
						 cd.setChTotal((Short)linha[i++]);
						 cd.setCrAula((Integer)linha[i++]);
						 cd.setCrEstagio((Integer)linha[i++]);
						 cd.setCrLaboratorio((Integer)linha[i++]);
						 d.setNivel((Character)linha[i++]);
						 t.setAno((Integer)linha[i++]);
						 t.setPeriodo((Integer)linha[i++]);
						 t.setDistancia((Boolean)linha[i++]);
						 t.setDescricaoHorario((String)linha[i++]);
						 t.setLocal((String)linha[i++]);
						 t.setCodigo((String)linha[i++]);
						 s.setId((Integer)linha[i++]);
						 s.setDescricao((String)linha[i++]);
						 t.setCapacidadeAluno((Integer)linha[i++]);
						 t.setAgrupadora((Boolean)linha[i++]);
						 
						 d.setDetalhes(cd);
						 t.setDisciplina(d);
						 t.setSituacaoTurma(s);
						 
						 Integer idPolo = (Integer)linha[i++];
						 if (idPolo != null){
							 
							 Polo p = new Polo();
							 Municipio c = new Municipio();
							 UnidadeFederativa uf = new UnidadeFederativa();
							 
							 p.setId(idPolo);
							 c.setNome((String)linha[i++]);
							 uf.setId((Integer)linha[i++]);
							 uf.setSigla((String)linha[i++]);

							 c.setUnidadeFederativa(uf);
							 p.setCidade(c);
							 t.setPolo(p);	 
						 }
						 
						 turmas.add(t);
					 }
					 
					 if (t != null && t.isAgrupadora()){
						 
						 i = 23;
						 Turma sub = new Turma();
						 Turma agr = new Turma();
						 ComponenteCurricular sd = new ComponenteCurricular();
						 SituacaoTurma s = new SituacaoTurma();
						 
						 sub.setId((Integer)linha[i++]);
						 agr.setId((Integer)linha[i++]);
						 sub.setAno((Integer)linha[i++]);
						 sub.setPeriodo((Integer)linha[i++]);
						 sub.setLocal((String)linha[i++]);
						 sub.setCodigo((String)linha[i++]);
						 s.setId((Integer)linha[i++]);
						 s.setDescricao((String)linha[i++]);
						 sub.setDescricaoHorario((String)linha[i++]);
						 sub.setCapacidadeAluno((Integer)linha[i++]);

						 sub.setDisciplina(sd);
						 sub.setSituacaoTurma(s);
						 sub.setTurmaAgrupadora(agr);
						 if (t != null ){
							 if ( isEmpty(t.getSubturmas()) )
								 t.setSubturmas( new ArrayList<Turma>());
							 t.getSubturmas().add(sub);
						 }
					 }
					 
					 oldId = newId;
				}

			}
					
			List<Integer> idsTurmas = new ArrayList<Integer>();
			for ( Turma t : turmas ){
				if (!t.isAgrupadora())
					idsTurmas.add(t.getId());
				else 
					for (Turma sub : t.getSubturmas())
						idsTurmas.add(sub.getId());				
			}
			
			if (!isEmpty(idsTurmas)){
				Query qtd = getSession().createQuery(
						"select turma.id, situacaoMatricula.id, count(*)"
							+ " from MatriculaComponente"
							+ " where turma.id in " + UFRNUtils.gerarStringIn(idsTurmas)
							+ " group by turma.id, situacaoMatricula.id");
				@SuppressWarnings("unchecked")
				List<Object[]> resultSet = qtd.list();
				for (Object[] row : resultSet) {
					for (Turma t : turmas) {
												
						Turma turma = null;
						if (!t.isAgrupadora() )
							turma = t;
						else {
							for (Turma sub : t.getSubturmas())
								if (sub.getId() == ((Integer)row[0]))
										turma = sub;
						}
							
						if (turma != null && turma.getId() == ((Integer)row[0])) {
													
							int situacao = (Integer) row[1];
							int total = ((Long) row[2]).intValue();
							if (situacao == SituacaoMatricula.MATRICULADO.getId())
								turma.setQtdMatriculados(total);
							else if (situacao == SituacaoMatricula.APROVADO.getId())
								turma.setQtdAprovados(total);
							else if (situacao == SituacaoMatricula.REPROVADO.getId())
								turma.setQtdReprovados(total);
							else if (situacao == SituacaoMatricula.REPROVADO_FALTA.getId())
								turma.setQtdReprovadosFalta(total);
							else if (situacao == SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId())
								turma.setQtdReprovadosMediaFalta(total); 
							break;
						}
					}
				}
			}
			return turmas;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todas as turmas que pessoa possui permissão no ano e período determinados
	 * @param pessoa
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List <Turma> findTurmasPermitidasByPessoaAnoPeriodo(Pessoa pessoa, int tipoPermissao, int ano, int periodo) throws HibernateException, DAOException{
		
		String auxPermissao = "";
		
		switch (tipoPermissao) {
			case PermissaoAva.DOCENTE: auxPermissao += "a.docente = true"; break;
			case PermissaoAva.FORUM: auxPermissao += "a.forum = true"; break;
			case PermissaoAva.ENQUETE: auxPermissao += "a.enquete = true"; break;
			case PermissaoAva.TAREFA: auxPermissao += "a.tarefa = true"; break;
			case PermissaoAva.CORRIGIR_TAREFA: auxPermissao += "a.corrigir_tarefa = true"; break;
			case PermissaoAva.INSERIR_ARQUIVO: auxPermissao += "a.inserir_arquivo = true"; break;
		}
		
		@SuppressWarnings("unchecked")
		List <Turma> rs = getSession().createQuery("select t from PermissaoAva a join a.turma t" +
				" where a.pessoa.id = " + pessoa.getId() + " and " + auxPermissao + " and t.ano = " +ano+ " and t.periodo = "+periodo+ " order by t.ano, t.periodo").list();
		
		return rs;
	}
}
