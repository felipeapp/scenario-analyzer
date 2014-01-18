/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/08/2011
 */

package br.ufrn.sigaa.arq.vinculo.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.medio.dominio.UsuarioFamiliar;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenacaoGeralRede;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 *	DAO com consultas utilizdas durante o processamento dos vínculos
 * 
 * @author Henrique André
 */
public class VinculosDao extends GenericSigaaDAO {
	
	/**
	 * Busca todos os discente de uma pessoa
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<DiscenteAdapter> findDiscentes(Pessoa pessoa) throws DAOException {
		
		String projecao = " d.id, d.nivel, d.matricula, d.status, d.tipo, d.anoIngresso, d.periodoIngresso, " +
				" gestoraAcademica.id as d.gestoraAcademica.id, gestoraAcademica.nome as d.gestoraAcademica.nome, " +
				" curso.id as d.curso.id, curso.nome as d.curso.nome, " +
				" unidadeCurso.id as d.curso.unidade.id, unidadeCurso.nome as d.curso.unidade.nome, unidadeCurso.sigla as d.curso.unidade.sigla, " +
				" municipioCurso.id as d.curso.municipio.id, municipioCurso.nome as d.curso.municipio.nome, " +
				" tipoCursoStricto.descricao as d.curso.tipoCursoStricto.descricao, " +
				" modalidadeEducacao.id as d.curso.modalidadeEducacao.id, modalidadeEducacao.descricao as d.curso.modalidadeEducacao.descricao, " + 
				" convenio.id as d.curso.convenio.id, convenio.descricao as d.curso.convenio.descricao ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from Discente d " +
				"	inner join d.pessoa p" +
				" 	inner join d.gestoraAcademica gestoraAcademica" +
				"	left join d.curso curso " +
				"	left join curso.unidade unidadeCurso " +
				"	left join curso.municipio municipioCurso " +
				"	left join curso.tipoCursoStricto tipoCursoStricto " +
				"	left join curso.modalidadeEducacao modalidadeEducacao " +
				"	left join curso.convenio convenio " +
				" where p.id = :idPessoa and d.status not in (:discentesSemAcesso)");
		
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idPessoa", pessoa.getId());
		query.setParameterList("discentesSemAcesso", getStatusSemAcesso());
		
		return (List<DiscenteAdapter>) HibernateUtils.parseTo(query.list(), projecao, Discente.class, "d");
	}
	
	/**
	 * Discentes que não tem acesso ao sistema
	 * 
	 * @return
	 */
	private Collection<Integer> getStatusSemAcesso() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(StatusDiscente.EXCLUIDO);
		status.add(StatusDiscente.PENDENTE_CADASTRO);
		return status;
	}	
	
	/**
	 * Busca todos os servidores de uma pessoa
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Servidor> findServidores(Pessoa pessoa) throws DAOException {
		
		String projecao = " s.id, s.siape, " +
				" ativo.id as s.ativo.id, categoria.id as s.categoria.id, " +
				" pessoa.id as s.pessoa.id, pessoa.nome as s.pessoa.nome," +
				" unidadeLotacao.id as s.unidade.id, unidadeLotacao.nome as s.unidade.nome," +
				" (select case when cv.id is not null then true else false end from ColaboradorVoluntario cv where cv.servidor.id = s.id and cv.ativo = trueValue()) as s.colaboradorVoluntario ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from Servidor s " +
				"	inner join s.pessoa pessoa " +
				"	inner join s.unidade unidadeLotacao " +
				"	inner join s.ativo ativo " +
				"	inner join s.categoria categoria " +
				"	inner join s.situacaoServidor situacao " +
				" where pessoa.id = :idPessoa and situacao.id in " + UFRNUtils.gerarStringIn(ParametroHelper.getInstance().getParametroIntegerArray(ConstantesParametro.VINCULOS_SITUACAO_SERVIDOR_PERMITIDO)));

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idPessoa", pessoa.getId());
		
		return (List<Servidor>) HibernateUtils.parseTo(query.list(), projecao, Servidor.class, "s");
	}
	
	/**
	 * Busca todos os docentes externos de uma pessoa
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteExterno> findDocenteExternos(Pessoa pessoa) throws DAOException {
		
		String projecao = " de.id, de.ativo, de.prazoValidade, de.matricula, " +
				" pessoa.id as de.pessoa.id, pessoa.nome as de.pessoa.nome, " +
				" unidade.id as de.unidade.id, unidade.sigla as de.unidade.sigla, " +
				" instituicao.id as de.instituicao.id, instituicao.nome as de.instituicao.nome, instituicao.sigla as de.instituicao.sigla ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));		
		hql.append(" from DocenteExterno de " +
				"		inner join de.pessoa pessoa " +
				"		left join de.unidade unidade " +
				"		left join de.instituicao instituicao " +
				" where pessoa.id = :idPessoa " +
				"	and (de.prazoValidade is null or de.prazoValidade >= :prazoValidade) " +
				"	and de.ativo = trueValue() " +
				"	and de.matricula is not null ");

		
		// Verificar se o prazo adicional de acesso também foi excedido
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		cal.add(Calendar.DAY_OF_MONTH, -ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO)-1);
		Date dataAtual = cal.getTime();

		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idPessoa", pessoa.getId());
		query.setDate("prazoValidade", dataAtual);
		
		@SuppressWarnings("unchecked")
		List<DocenteExterno> lista = (List<DocenteExterno>) HibernateUtils.parseTo(query.list(), projecao, DocenteExterno.class, "de");
		return lista;
	}
	
	/**
	 * Busca todos os tutores
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<TutorOrientador> findTutores(Pessoa pessoa) throws DAOException {
		
		String projecao = " t.id_tutor_orientador, t.tipo as ttipo, t.ativo, p.id_pessoa , p.nome as pnome , p.cpf_cnpj, p.tipo as ptipo, "+
						  " pc.id_polo_curso , pl.id_polo , m.id_municipio , m.nome as mnome , uf.sigla , c.id_curso , c.nome as cnome ";
		
		String sql = " select " + projecao +
					" from  ead.tutor_orientador t " +
					" left join ead.tutor_polo_curso tpc on tpc.id_tutor_orientador = t.id_tutor_orientador " + 
					" left join ead.polo_curso pc on pc.id_polo_curso = tpc.id_polo_curso " +
					" left join ead.polo pl on pl.id_polo = pc.id_polo " +
					" left join comum.municipio m on m.id_municipio = pl.id_cidade " +  
					" left join comum.unidade_federativa uf on uf.id_unidade_federativa = m.id_unidade_federativa " +  
					" left join curso c on c.id_curso = pc.id_curso " +  
					" join comum.pessoa p on p.id_pessoa = t.id_pessoa " +  
					" where t.ativo = trueValue() and p.id_pessoa = " +pessoa.getId()+  
					" group by t.id_tutor_orientador, t.tipo , t.ativo, p.id_pessoa , p.nome , p.cpf_cnpj, p.tipo , " +  
					"  	pc.id_polo_curso , c.id_curso , c.nome , pl.id_polo , m.id_municipio , " +  
					" 	m.nome , uf.sigla " +
					" order by t.id_tutor_orientador ";
		
		Query query = getSession().createSQLQuery(sql.toString());
		List <Object []> ls = query.list();
		List<TutorOrientador> listaTutores = new ArrayList<TutorOrientador>();
		TutorOrientador t = null;
		
		Integer idTutor = 0;
		for (Object [] linha : ls){
		
			Integer i = 0;
			Integer newIdTutor = (Integer)linha[i++];
			if (idTutor.intValue() == newIdTutor.intValue())
				i = 7;
			else {	
				
				t = new TutorOrientador();
				t.setId(newIdTutor);
				t.setTipo((Character)linha[i++]);
				t.setAtivo((Boolean)linha[i++]);
			
				Pessoa p = new Pessoa();
				p.setId((Integer)linha[i++]);
				p.setNome((String)linha[i++]);
				p.setCpf_cnpj(((Number)linha[i++]).longValue());
				p.setTipo((Character)linha[i++]);
				t.setPessoa(p);

				t.setPoloCursos(new ArrayList<PoloCurso>());
				
				idTutor = newIdTutor;
				listaTutores.add(t);
			}
			
			if (linha[i]!=null){
				PoloCurso pc = new PoloCurso();
				pc.setId((Integer)linha[i++]);
				
				Polo pl = new Polo();
				pl.setId((Integer)linha[i++]);
				pl.setCidade(new Municipio());
				pl.getCidade().setId((Integer)linha[i++]);
				pl.getCidade().setNome((String)linha[i++]);
				pl.getCidade().setUnidadeFederativa(new UnidadeFederativa());
				pl.getCidade().getUnidadeFederativa().setSigla((String)linha[i++]);
				pc.setPolo(pl);
				
				Curso c = new Curso();
				c.setId((Integer)linha[i++]);
				c.setNome((String)linha[i++]);
				pc.setCurso(c);
				
				if (t!=null){
					t.setPoloCurso(pc);
					t.getPoloCursos().add(pc);
				}
			}
		}		
				
		return listaTutores;
	}
	
	/**
	 * Busca todas as coordenações de polo EAD de uma pessoa
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<CoordenacaoPolo> findCoordenadoresPolo(Pessoa pessoa) throws DAOException {
		
		String projecao = " cp.id, cp.fim, " +
				" pessoa.id as cp.pessoa.id, pessoa.nome as cp.pessoa.nome, pessoa.cpf_cnpj as cp.pessoa.cpf_cnpj, pessoa.tipo as cp.pessoa.tipo, " +
				" polo.id as cp.polo.id, " +
				" cidade.id as cp.polo.cidade.id, cidade.nome as cp.polo.cidade.nome, " +
				" uf.sigla as cp.polo.cidade.unidadeFederativa.sigla ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from CoordenacaoPolo cp " +
				"		inner join cp.pessoa pessoa " +
				"		inner join cp.polo polo " +
				"		inner join polo.cidade cidade " +
				"		inner join cidade.unidadeFederativa uf " +
				" where (cp.fim is null or cp.fim >= :data) and pessoa.id = :idPessoa");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idPessoa", pessoa.getId());
		query.setDate("data", new Date());
		
		return (List<CoordenacaoPolo>) HibernateUtils.parseTo(query.list(), projecao, CoordenacaoPolo.class, "cp");
	}
	
	/**
	 * Busca as secretárias de um usuário
	 * 
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public List<SecretariaUnidade> findSecretarias(Usuario usuario) throws DAOException {
		
		String projecao = " su.id, su.ativo, " +
				" curso.id as su.curso.id, curso.nome as su.curso.nome, " +
				" unidadeCurso.id as su.curso.unidade.id , unidadeCurso.nome as su.curso.unidade.nome, " +
				" unidadeSecretaria.id as su.unidade.id, unidadeSecretaria.nome as su.unidade.nome, " +
				" pessoa.cpf_cnpj as su.usuario.pessoa.cpf_cnpj, pessoa.tipo as su.usuario.pessoa.tipo ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from SecretariaUnidade su " +
				"		inner join su.usuario usuario " +
				"		left join su.curso curso " +
				"		left join curso.unidade unidadeCurso " +
				"		left join su.unidade unidadeSecretaria " +
				"		inner join usuario.pessoa pessoa " +
				" where su.ativo = trueValue() " +
				"	and usuario.id = :idUsuario " +
				"   and (su.fim is null or su.fim >= :data) "+
				"	and (curso IS NULL OR curso.id NOT IN (SELECT cl.id FROM CursoLato cl INNER JOIN cl.propostaCurso pc WHERE pc.situacaoProposta.id <> :idSituacaoProposta) )"); 
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idUsuario", usuario.getId());
		query.setInteger("idSituacaoProposta", SituacaoProposta.ACEITA);
		query.setDate("data", new Date());
		
		@SuppressWarnings("unchecked")
		List<SecretariaUnidade> lista = (List<SecretariaUnidade>) HibernateUtils.parseTo(query.list(), projecao, SecretariaUnidade.class, "su");
		return lista;		
	}
	
	/**
	 * Busca os concedentees
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<ConcedenteEstagioPessoa> findConcedente(Pessoa pessoa) throws DAOException {
		
		String projecao = " pep.id, " +
				" p.ativo as pep.concedente.ativo, " +
				" pessoa.cpf_cnpj as pep.pessoa.cpf_cnpj, pessoa.tipo as pep.pessoa.tipo, " +
				" pessoaConcedente.nome as pep.concedente.pessoa.nome ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from ConcedenteEstagioPessoa pep " +
				"		inner join pep.pessoa pessoa " + 
				"		inner join pep.concedente p " +
				"		inner join p.pessoa pessoaConcedente " + 
				"		inner join p.convenioEstagio convenio " + 
				" where pessoa.id = :idPessoa " +
				"	and convenio.status.id = :idSituacao " +
				"	and p.ativo = trueValue()");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idPessoa", pessoa.getId());
		query.setInteger("idSituacao", StatusConvenioEstagio.APROVADO);
		
		@SuppressWarnings("unchecked")
		List<ConcedenteEstagioPessoa> lista = (List<ConcedenteEstagioPessoa>) HibernateUtils.parseTo(query.list(), projecao, ConcedenteEstagioPessoa.class, "pep");
		return lista;		
	}
	
	/**
	 * Busca o familiar que possui discentes vinculados a ele
	 * @param usuario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<UsuarioFamiliar> findFamiliarDiscentes(Usuario usuario) throws HibernateException, DAOException{
		
		String projecao = " uf.id, uf.usuario.id, uf.usuario.pessoa.cpf_cnpj, " +
				" uf.usuario.pessoa.nome, uf.usuario.pessoa.tipo, uf.usuario.tipo, " +
				" uf.discenteMedio.discente.matricula, uf.discenteMedio.discente.pessoa.nome, uf.discenteMedio.id," +
				" uf.discenteMedio.discente.id, uf.discenteMedio.discente.status, uf.discenteMedio.discente.curso.id, " +
				" uf.discenteMedio.discente.curso.nome, uf.discenteMedio.discente.tipo, uf.discenteMedio.discente.nivel ";

		Collection<Integer> status = StatusDiscente.getStatusComVinculo();
		status.add(StatusDiscente.ATIVO_DEPENDENCIA);
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(projecao);
		hql.append(" from UsuarioFamiliar uf " +
				"		inner join uf.usuario usuario " + 
				"		inner join uf.usuario.pessoa pessoa " +
				"		inner join uf.discenteMedio discenteMedio " +
				"		inner join uf.discenteMedio.discente discente " +
				"		inner join uf.discenteMedio.discente.pessoa pessoaDisc " +
				"		inner join uf.discenteMedio.discente.curso curso " +
				" where usuario.id = :idUsuario " +
				"   and discente.status in "+(UFRNUtils.gerarStringIn(status)));
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idUsuario", usuario.getId());
		
		@SuppressWarnings("unchecked")
		List<UsuarioFamiliar> lista = (List<UsuarioFamiliar>) HibernateUtils.parseTo(query.list(), projecao, UsuarioFamiliar.class, "uf");
		return lista;			
		
	}
	
	/**
	 * Busca as coordenações em programas em rede
	 * 
	 * @param usuario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<CoordenacaoGeralRede> findCoordenacaoGeralRede(Pessoa pessoa) throws DAOException{
		
		String projecao = " coord ";

		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(projecao);
		hql.append(" from CoordenacaoGeralRede coord " +
				"		inner join coord.pessoa pessoa " +
				" where pessoa.id = :idPessoa ");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idPessoa", pessoa.getId());
		
		return query.list();
	}
	
	/**
	 * Busca as coordenações em programas em rede
	 * 
	 * @param usuario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<CoordenadorUnidade> findCoordenacaoUnidadeRede(Pessoa pessoa) throws DAOException {
		
		String projecao = " coord ";

		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(projecao);
		hql.append(" from CoordenadorUnidade coord " +
				"		inner join coord.pessoa pessoa " +
				" where pessoa.id = :idPessoa " +
				" and coord.ativo = trueValue() ");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idPessoa", pessoa.getId());
		
		return query.list();
	}	
}
