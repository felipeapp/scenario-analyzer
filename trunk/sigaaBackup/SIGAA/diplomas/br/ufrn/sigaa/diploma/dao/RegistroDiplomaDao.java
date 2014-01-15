/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/05/2009
 *
 */
package br.ufrn.sigaa.diploma.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.diploma.dominio.AlteracaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ControleNumeroRegistro;
import br.ufrn.sigaa.diploma.dominio.DadosImpressaoDiploma;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.LogGeracaoDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiplomaColetivo;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Reconhecimento;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/** DAO responsável por consultas especializadas aos Registros de Diplomas
 * @author Édipo Elder F. Melo
 *
 */
public class RegistroDiplomaDao extends GenericSigaaDAO {
	
	/** Retorna o registro de diploma coletivo da turma de colação especificada pelo curso e pela data de colação de grau.
	 * 
	 * @param idCurso
	 * @param dataColacao
	 * @return
	 * @throws DAOException
	 */
	public RegistroDiplomaColetivo findByCursoDataColacao(int idCurso, Date dataColacao, Polo polo) throws DAOException {
		Criteria c = getSession().createCriteria(RegistroDiplomaColetivo.class);
		Criteria cCurso = c.createCriteria("registrosDiplomas").createCriteria("discente").createCriteria("curso");
		if (idCurso > 0)
			cCurso.add(Restrictions.eq("id", idCurso));
		c.add(Restrictions.eq("dataColacao", dataColacao));
		if (!isEmpty(polo))
			c.add(Restrictions.eq("polo.id", polo.getId()));
		return (RegistroDiplomaColetivo) c.uniqueResult();
	}
	
	
	/** Retorna o registro de diploma do discente especificado.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public RegistroDiploma findByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(RegistroDiploma.class);
		c.add(Restrictions.eq("discente.id", idDiscente)).add(Restrictions.eq("ativo", true));
		return (RegistroDiploma) c.uniqueResult();
	}
	

	/** Retorna uma coleção de registros de diploma dado o nome do discente ou o número do registro 
	 * @param nome Caso null, não é utilizado na consulta
	 * @param numeroRegistro Caso null, não é utilizado na consulta
	 * @param nivelEnsino pode ser 'G' para graduação, ou 'S' para stricto sensu
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroDiploma> findByDiscenteNumeroRegistro(Discente discente, Integer numeroRegistro, Character nivelEnsino) throws DAOException {
		StringBuilder sql = new StringBuilder("select id_registro_diploma" +
				" from diploma.registro_diploma" +
				" inner join diploma.folha_registro_diploma using (id_folha_registro_diploma)" +
				" inner join diploma.livro_registro_diploma using (id_livro_registro_diploma)" +
				" inner join discente using (id_discente)" +
				" inner join comum.pessoa using (id_pessoa)" +
				" where registro_diploma.ativo = trueValue()");
		if (discente !=null && discente.getNome() != null)
				sql.append(" and pessoa.nome_ascii like :nome");
		if (discente !=null && discente.getMatricula() != null && discente.getMatricula() > 0)
			sql.append(" and discente.matricula = :matricula");
		if (nivelEnsino != null)
			sql.append(" and livro_registro_diploma.nivel = :nivel");
		if (numeroRegistro != null)
			sql.append(" and registro_diploma.numero_registro = :numeroRegistro");
		Query q = getSession().createSQLQuery(sql.toString());
		if(discente !=null && discente.getNome() != null)
			q.setString("nome", StringUtils.toAsciiAndUpperCase(discente.getNome())+"%");
		if (discente !=null && discente.getMatricula() != null && discente.getMatricula() > 0)
			q.setLong("matricula", discente.getMatricula());
		if (numeroRegistro != null)
			q.setInteger("numeroRegistro", numeroRegistro);
		if (nivelEnsino !=null)
			q.setCharacter("nivel", nivelEnsino);

		@SuppressWarnings("unchecked")
		Collection<Integer> ids = q.list();
		
		if (ids == null || ids.size() == 0)
			return new ArrayList<RegistroDiploma>();
		Criteria c = getSession().createCriteria(RegistroDiploma.class)
		.add(Restrictions.in("id", ids))
		.setFetchMode("discente", FetchMode.JOIN)
		.setFetchMode("folha", FetchMode.JOIN)
		.createCriteria("discente")
		.setFetchMode("pessoa", FetchMode.JOIN)
		.createCriteria("pessoa")
		.addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		Collection<RegistroDiploma> lista = c.list();
		return lista;
	}

	
	/**
	 * Retorna uma colação de registro de diplomas coletivos de um curso no
	 * ano/período informado. Caso o idCurso seja igual a 0, retorna os
	 * registros coletivos de todos cursos.
	 * 
	 * @param idCurso caso 0, é ignorado na busca.
	 * @param ano Ano da colação coletiva.
	 * @param semestre 1, caso a colação tenha sido entre janeiro e junho (inclusive). 2, caso tenha sido entre julho e dezembro (inclusive).
	 * @return
	 * @throws DAOException 
	 */
	public Collection<RegistroDiplomaColetivo> findRegistroColetivoByCursoAnoPeriodo(int idCurso, int ano, int semestre, char nivelEnsino) throws DAOException{
		Criteria c = getSession().createCriteria(RegistroDiplomaColetivo.class);
		c.add(Restrictions.eq("ano", ano)).add(Restrictions.eq("periodo", semestre));
		Criteria cCurso = c.createCriteria("curso");
		cCurso.add(Restrictions.eq("nivel", nivelEnsino));
		if (idCurso > 0)
			cCurso.add(Restrictions.eq("id", idCurso));
		
		@SuppressWarnings("unchecked")
		Collection<RegistroDiplomaColetivo> lista = c. setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return lista;
	}
	

	/**
	 * Retorna uma lista de números de registro de diplomas correspondente aos
	 * IDs dos discentes passados como parâmetro.
	 * <b>Esta consulta é utilizada no log da emissão de diplomas.</b> 
	 * @param listaIdDiscente
	 *            lista de idDiscente.
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<Integer> findRegistrosByIdDiscente(Collection<Integer> listaIdDiscente) throws DAOException {
		if (listaIdDiscente == null || listaIdDiscente.isEmpty() )
			return null;
		String sql = "select numero_registro" +
				" from diploma.registro_diploma" +
				" where id_discente in "+UFRNUtils.gerarStringIn(listaIdDiscente)+
				" and ativo = trueValue()" +
				" order by numero_registro";
		
		@SuppressWarnings("unchecked")
		Collection<Integer> lista = getSession().createSQLQuery(sql).list();
		return lista;
	}

	/** Retorna uma coleção de logs de geração de diplomas baseado nos parâmetros especificados.
	 * @param matricula matricula do discente. Caso seja igual a zero, não será utilizado na busca.
	 * @param nomeDiscente nome do discente. Caso seja igual nulo, não será utilizado na busca.
	 * @param numeroRegistro número do registro do diploma. Caso seja igual a zero, não será utilizado na busca.
	 * @param nomeUsuario nome do usuário. Caso seja nulo, não será utilizado na busca.
	 * @param data data da geração do diploma. Caso seja nulo, não será utilizado na busca.
	 * @param nivel Nível de Ensino (G - graduação; S - Stricto Sensu)
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<LogGeracaoDiploma> findLogGeracaoDiploma(long matricula, String nomeDiscente,
			int numeroRegistro, String nomeUsuario, Date data, char nivel) throws DAOException {
		StringBuilder sql = new StringBuilder("select id_log_geracao_diploma"
				+ " from diploma.log_geracao_diploma log"
				+ " inner join comum.registro_entrada re on (log.id_registro_entrada = re.id_entrada)"
				+ " inner join comum.usuario u on (re.id_usuario = u.id_usuario)"
				+ " inner join diploma.registro_diploma rd on ( array[cast(rd.numero_registro as text)] <@ string_to_array(log.registros,', ') )"
				+ " inner join discente d using (id_discente)"
				+ " inner join comum.pessoa p1 on (d.id_pessoa = p1.id_pessoa)"
				+ " inner join comum.pessoa p2 on (u.id_pessoa = p2.id_pessoa)"
				+ " where 1 = 1");
		switch (nivel) {
		case NivelEnsino.STRICTO: sql.append(" and d.nivel in " + NivelEnsino.getNiveisStrictoString()); break;
		default: sql.append(" and d.nivel = '" + nivel+"'"); break;
		}
		if (matricula > 0) {
			sql.append(" and d.matricula = :matricula");
		}
		if (nomeDiscente != null) {
			sql.append(" and p1.nome_ascii like :nomeDiscente");
		}
		if (numeroRegistro > 0) {
			sql.append(" and rd.numero_registro = :numeroRegistro");
		}
		if (nomeUsuario != null) {
			sql.append(" and p2.nome_ascii like :nomeUsuario");
		}
		if (data != null){
			sql.append(" and cast(log.data as date) = cast( :data as date)");
		}
		Query q = getSession().createSQLQuery(sql.toString());
		if (matricula > 0) {
			q.setLong("matricula", matricula);
		}
		if (nomeDiscente != null) {
			q.setString("nomeDiscente", StringUtils.toAsciiAndUpperCase(nomeDiscente)+"%");
		}
		if (numeroRegistro > 0) {
			q.setInteger("numeroRegistro", numeroRegistro);
		}
		if (nomeUsuario != null) {
			q.setString("nomeUsuario", StringUtils.toAsciiAndUpperCase(nomeUsuario)+"%");
		}
		if (data != null){
			q.setDate("data", data);
		}
		
		@SuppressWarnings("unchecked")
		Collection<Integer> idS = q.list();
		
		if (idS == null || idS.isEmpty())
			return null;
		
		Criteria c = getSession().createCriteria(LogGeracaoDiploma.class).add(Restrictions.in("id", idS)).addOrder(Order.desc("data"));
		
		@SuppressWarnings("unchecked")
		Collection<LogGeracaoDiploma> lista = c.list();
		return lista;
	}
	
	/** Retorna uma coleção de logs de geração de diplomas baseado nos parâmetros especificados.
	 * 
	 * @param numeroRegistro número do registro do diploma. Caso seja igual a zero, não será utilizado na busca.
	 * @param nomeUsuario nome do usuário. Caso seja igual nulo, não será utilizado na busca.
	 * @param registroAntigo busca por requisições de números para registros de diplomas antigos. Caso seja nulo, não será utilizado na busca.
	 * @param registroExterno busca por requisições de números para registros de diplomas externos. Caso seja nulo, não será utilizado na busca.
	 * @param solicitadoEm data da requisição do número de registro do diploma. Caso seja nulo, não será utilizado na busca.
	 * @return
	 * @throws DAOException
	 */
	public Collection<ControleNumeroRegistro> findControleNumeroDiploma(
			Integer numeroRegistro, String nomeUsuario, Boolean registroAntigo, 
			Boolean registroExterno, Date solicitadoEm, Character nivelEnsino) 
			throws DAOException {
		Criteria c = getSession().createCriteria(ControleNumeroRegistro.class);
		if (numeroRegistro != null && numeroRegistro > 0)
			c.add(Restrictions.eq("numeroRegistro", numeroRegistro));
		if (nomeUsuario != null && !nomeUsuario.trim().isEmpty())
			c.createCriteria("registroEntrada").createCriteria("usuario")
			.createCriteria("pessoa").add(Restrictions.ilike("nomeAscii", nomeUsuario+"%"));
		if (registroAntigo != null)
			c.add(Restrictions.eq("registroAntigo", registroAntigo));
		if (registroExterno != null)
			c.add(Restrictions.eq("registroExterno", registroExterno));
		if (solicitadoEm != null)
			c.add(Restrictions.and(
					Restrictions.ge("solicitadoEm", CalendarUtils.descartarHoras(solicitadoEm)),
					Restrictions.lt("solicitadoEm", CalendarUtils.descartarHoras(CalendarUtils.adicionaUmDia(solicitadoEm)))));
		if (nivelEnsino != null)
			c.add(Restrictions.eq("nivel", nivelEnsino));
		c.addOrder(Order.desc("solicitadoEm")).addOrder(Order.desc("id"));
		
		@SuppressWarnings("unchecked")
		Collection<ControleNumeroRegistro> lista = c.list();
		return lista;
	}

	/**
	 * Retorna o próximo número de registro de diploma.<br>
	 * O número de registro de diplomas é definido como um sequencial único
	 * para a graduação, continuado entre livros de registro de diplomas. Para
	 * os diplomas de stricto sensu, o número de registro de diploma é um
	 * sequencial para cada livro, iniciando sempre em "1". <br>
	 * Este método deve ser chamado dentro de uma transação. Ao requisitar um
	 * número, o controle de números é bloqueado e liberado somente ao final da
	 * transação (commit ou rollback).<br>
	 * 
	 * <b>NÃO INVOCAR ESTE MÉTODO SE NÃO ESTIVER EM UMA TRANSAÇÃO (DENTRO DE UM
	 * PROCESSADOR)</b>
	 * 
	 * @param registroExterno
	 *            true, caso o número seja utilizado para o registro de um
	 *            diploma externo à instituição.
	 * @param nivelEnsino
	 *            nível de ensino de registro do diploma.
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public Integer requisitaNumeroRegistro(boolean registroExterno, char nivelEnsino) throws DAOException, NegocioException {
		Integer numeroRegistro = null;
		Integer idLivro = null;
		// requisita acesso exclusivo (LOCK) à tabela de controle de números de registros
		update("lock table diploma.controle_numero_registro in access exclusive mode");
		// tanto graduação quanto lato sensu utilizam um sequencial único
		if (NivelEnsino.GRADUACAO == nivelEnsino || NivelEnsino.LATO == nivelEnsino) {
			// busca pelo próximo número a ser utilizado para a graduação
			numeroRegistro = getJdbcTemplate()
					.queryForInt(
							"select max(numero_registro)" +
							" from diploma.controle_numero_registro" +
							" where nivel = ?", new Object[] {String.valueOf(nivelEnsino)});
		} else if (NivelEnsino.STRICTO == nivelEnsino) {
			// já stricto sensu utiliza um sequencial por livro...
			// busca pelo próximo número a ser utilizado para o livro de stricto
			idLivro = getJdbcTemplate()
				.queryForInt(
						"select id_livro_registro_diploma" +
						" from diploma.livro_registro_diploma" +
						" where nivel = ?" +
						" and livro_antigo = falseValue()" + 
						" and ativo = trueValue();", new Object[] {String.valueOf(nivelEnsino)});
			if (idLivro == null) {
				throw new NegocioException("Não há livro aberto para registro de "+(nivelEnsino == NivelEnsino.LATO ? "certificado" : "diploma")+" stricto sensu.");
			}
			numeroRegistro = getJdbcTemplate()
					.queryForInt(
							"select max(numero_registro)" +
							" from diploma.controle_numero_registro" +
							" where id_livro_registro_diploma = ?", new Object[]{idLivro});
		}
		// caso não exista número registrado, inicia-se o registro com o valor inicial parametrizado
		if (numeroRegistro == null || numeroRegistro == 0) {
			switch (nivelEnsino) {
			case NivelEnsino.GRADUACAO: numeroRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.NUMERO_INICIAL_REGISTRO_DIPLOMA);				
				break;
			case NivelEnsino.LATO: numeroRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosLatoSensu.NUMERO_INICIAL_REGISTRO_DIPLOMA);				
				break;
			case NivelEnsino.STRICTO: numeroRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.NUMERO_INICIAL_REGISTRO_DIPLOMA);				
				break;
			default:
				numeroRegistro = 1;
				break;
			}
		} else { 
			numeroRegistro++;
		}
		// insere no controle, o número que será utilizado
		ControleNumeroRegistro controle = new ControleNumeroRegistro();
		controle.setNumeroRegistro(numeroRegistro);
		controle.setRegistroAntigo(false);
		controle.setRegistroExterno(registroExterno);
		controle.setRegistroEntrada(getUsuario().getRegistroEntrada());
		controle.setSolicitadoEm(new Date());
		controle.setNivel(nivelEnsino);
		// no caso de stricto sensu, define em qual livro o número foi utilizado.
		if (NivelEnsino.STRICTO == nivelEnsino && idLivro != null) {
			LivroRegistroDiploma livro = findByPrimaryKey(idLivro, LivroRegistroDiploma.class);
			controle.setLivro(livro);
		} else {
			controle.setLivro(null);
		}
		create(controle);
		return numeroRegistro;
	}

	/**
	 * Anota o uso de um número de registro de diploma utilizado em
	 * um registro de diploma antigo, ou seja, anterior ao registro digital no
	 * SIGAA. Este método deve ser chamado dentro de uma transação. Ao
	 * requisitar um número, o controle de números é bloqueado e liberado
	 * somente ao final da transação (commit ou rollback).<br>
	 * 
	 * <b>NÃO INVOCAR ESTE MÉTODO SE NÃO ESTIVER EM UMA TRANSAÇÃO 
	 * (DENTRO DE UM PROCESSADOR)</b>
	 * 
	 * @param numeroRegistro
	 *            Número do registro utilizado.
	 * @param registroExterno
	 *            true, caso o número seja utilizado para o registro de um
	 *            diploma externo à instituição.          
	 *            
	 * @return
	 * @throws DAOException
	 */
	public void anotaNumeroRegistroAntigo(int numeroRegistro, boolean registroExterno, char nivel) throws DAOException {
		// requisita acesso exclusivo (LOCK) à tabela de controle de números de registros
		update("lock table diploma.controle_numero_registro in access exclusive mode");
		// insere no controle, o número que será utilizado
		ControleNumeroRegistro controle = new ControleNumeroRegistro();
		controle.setNumeroRegistro(numeroRegistro);
		controle.setRegistroAntigo(true);
		controle.setRegistroExterno(registroExterno);
		controle.setRegistroEntrada(getUsuario().getRegistroEntrada());
		controle.setSolicitadoEm(new Date());
		controle.setNivel(nivel);
		create(controle);
	}
	
	/** Dada uma lista de discentes, verifica quais já possuem diploma/certificado registrado. 
	 * @param discentes Lista de discentes que possui diploma/certificado registrado.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Discente> verificaDiscentesRegistrados(Collection<Discente> discentes) throws DAOException{
		if (discentes == null || discentes.isEmpty()) return null;
		Criteria c = getSession().createCriteria(RegistroDiploma.class);
		c.add(Restrictions.in("discente", discentes));
		c.add(Restrictions.eq("ativo", true));
		c.setProjection(Property.forName("discente"));
		@SuppressWarnings("unchecked")
		List<Discente> registrados = c.list();
		return registrados;
	}


	/** Retorna uma coleção de alterações de registro de diplomas.
	 * @param registro
	 * @return
	 * @throws DAOException
	 */
	public Collection<AlteracaoRegistroDiploma> findAlteracoesRegistroDiploma(RegistroDiploma registro) throws DAOException {
		Criteria c = getSession().createCriteria(AlteracaoRegistroDiploma.class);
		c.createCriteria("registroDiploma").add(Restrictions.eq("id", registro.getId()));
		c.addOrder(Order.desc("criadoEm"));
		@SuppressWarnings("unchecked")
		Collection<AlteracaoRegistroDiploma> lista = c.list();
		return lista;
	}
	
	/** Retorna uma coleção de dados necessários para a impressão dos diplomas da lista de IDs de discentes especificada.
	 * @param idsDiscente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<DadosImpressaoDiploma> findDadosImpressaoDiplomaByIdDiscentes(Collection<Integer> idsDiscente) throws HibernateException, DAOException {
		Collection<DadosImpressaoDiploma> dados = new ArrayList<DadosImpressaoDiploma>();
		// registros de diplomas
		String projecaoRegistro = "registro.assinaturaDiploma.id, registro.ativo, registro.coordenadorCurso.id, registro.criadoEm, registro.dataColacao," +
				" registro.dataExpedicao, registro.dataRegistro, registro.discente.id, registro.discente.curso," +
				" registro.discente.pessoa, registro.folha.id, registro.id, registro.impresso, " +
				" registro.livre, registro.numeroRegistro, registro.processo, registro.registroDiplomaColetivo.id," +
				" registro.folha.id, registro.folha.numeroFolha, registro.folha.livro.id, registro.folha.livro.titulo, registro.folha.livro.nivel";
		String hql = "select " + projecaoRegistro +
				" from RegistroDiploma registro" +
				" where registro.discente.id in "+
				UFRNUtils.gerarStringIn(idsDiscente) +
				" and registro.ativo = trueValue()";
		String projecaoTCC = " tcc.ano, tcc.area, tcc.ativo, tcc.dataAlteracao, tcc.dataDefesa, tcc.dataInicio," +
				" tcc.departamento, tcc.discenteExterno, tcc.docenteExterno," +
				" tcc.entidadeFinanciadora, tcc.id, tcc.idArquivo, tcc.ies, tcc.informacao," +
				" tcc.instituicao, tcc.matricula, tcc.orientacao, tcc.orientando," +
				" tcc.orientandoString, tcc.paginas, tcc.registroCadastro, tcc.servidor," +
				" tcc.subArea, tcc.tipoTrabalhoConclusao, tcc.titulo, tcc.validacao";
		String hqlTCC = "select "+projecaoTCC+" from TrabalhoFimCurso tcc" +
				" where tcc.orientando.id = :idDiscente" +
				" and (tcc.ativo = trueValue() or tcc.ativo is null)" +
				" order by tcc.dataDefesa desc";
		Query q = getSession().createQuery(hql);
		Query qTCC = getSession().createQuery(hqlTCC);
		@SuppressWarnings("unchecked")
		List<RegistroDiploma> lista = (List<RegistroDiploma>) HibernateUtils.parseTo(q.list(), projecaoRegistro, RegistroDiploma.class, "registro");
		// Registros e Reconhecimentos de cursos dos discentes de graduação
		Collection<Integer> idsDiscentes= new ArrayList<Integer>();
		char nivel = ' ';
		if (!isEmpty(lista)) {
			nivel = lista.get(0).getLivroRegistroDiploma().getNivel();
			for (RegistroDiploma registro : lista) {
				DadosImpressaoDiploma dado = new DadosImpressaoDiploma();
				dado.setRegistro(registro);
				dados.add(dado);
				idsDiscentes.add(registro.getDiscente().getId());
				TrabalhoFimCurso tcc = (TrabalhoFimCurso) qTCC.setInteger("idDiscente", registro.getDiscente().getId())
					.setMaxResults(1)
					.uniqueResult();
				dado.setTrabalhoFimCurso(tcc);
			}
			switch (nivel) {
				case NivelEnsino.GRADUACAO:
					// reconhecimentos de cursos dos discentes de graduação
					carregaReconhecimentoCursos(dados);
					break;
				case NivelEnsino.STRICTO:
					// área de concentração dos discentes de stricto sensu
					carregaAreaConcentracao(dados);
					break;
				case NivelEnsino.LATO:
					// média do curso e lista de componentes curriculares pagos dos discentes de lato sensu
					carregaComponentesPagos(dados);
					break;
				default:
					break;
			}
		}
		return dados;
	}


	/** Carrega a lista de componentes pagos pelo discente de lato sensu e média geral do curso, para impressão de diplomas
	 * @param dados
	 * @throws DAOException
	 */
	private void carregaComponentesPagos(Collection<DadosImpressaoDiploma> dados) throws DAOException {
		if (isEmpty(dados)) return;
		Collection<Integer> idsDiscentes = new ArrayList<Integer>();
		for (DadosImpressaoDiploma dado : dados) {
			idsDiscentes.add(dado.getRegistro().getDiscente().getId());
		}
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.add(Restrictions.in("situacaoMatricula", SituacaoMatricula.getSituacoesConcluidas()));
		c.add(Restrictions.in("discente.id", idsDiscentes));
		c.addOrder(Order.asc("ano")).addOrder(Order.asc("periodo")).addOrder(Order.asc("dataCadastro"));
		@SuppressWarnings("unchecked")
		List<MatriculaComponente> componentes = c.list();
		if(!isEmpty(componentes)) {
			Integer metodoAvaliacao = null;
			if (componentes.iterator().next().getDiscente().isLato())
				metodoAvaliacao = ((CursoLato) dados.iterator().next().getRegistro().getDiscente().getCurso()).getPropostaCurso().getMetodoAvaliacao();
			for (DadosImpressaoDiploma dado : dados) {
				for (MatriculaComponente mc : componentes) {
					if (mc.getDiscente().getId() == dado.getRegistro().getDiscente().getId()) {
						if (mc.getDiscente().isLato()) {
							mc.setMetodoAvaliacao(metodoAvaliacao);
						}
						dado.addMatriculaComponente(mc);
					}
				}
			}
			// reordena a lista de componentes, colocando por último o TCC
			for (DadosImpressaoDiploma dado : dados) {
				Collections.sort(dado.getComponentesPagos(), new Comparator<MatriculaComponente>() {
					@Override
					public int compare(MatriculaComponente mc1, MatriculaComponente mc2) {
						if (mc1.getComponente().isTrabalhoConclusaoCurso())
							return 1;
						if (mc2.getComponente().isTrabalhoConclusaoCurso())
							return -1;
						int cmp = mc1.getAno() * 10 + mc1.getPeriodo() - mc2.getAno() * 10 - mc2.getPeriodo();
						if (cmp == 0 && mc1.getDataCadastro() != null && mc2.getDataCadastro() != null) 
							return (int) (mc1.getDataCadastro().getTime() - mc2.getDataCadastro().getTime());
						else
							return cmp;
					}
				});
			}
		}
	}


	/** Carrega os dados da área de concentração dos discentes para impressão do diploma.
	 * @param dados
	 * @throws DAOException
	 */
	private void carregaAreaConcentracao(Collection<DadosImpressaoDiploma> dados) throws DAOException {
		if (isEmpty(dados)) return;
		Collection<Integer> idsDiscentes = new ArrayList<Integer>();
		for (DadosImpressaoDiploma dado : dados) {
			idsDiscentes.add(dado.getRegistro().getDiscente().getId());
		}
		String hqlArea = "select ds.id as idDiscente, " +
				" areaConcentracao" +
				" from DiscenteStricto ds" +
				" inner join ds.area areaConcentracao" +
				" where ds.id in "
				+ UFRNUtils.gerarStringIn(idsDiscentes);
		Query qArea= getSession().createQuery(hqlArea);
		@SuppressWarnings("unchecked")
		List<Object[]> areas = qArea.list();
		if(!isEmpty(areas)) {
			for (DadosImpressaoDiploma dado : dados) {
				for (Object[] obj : areas) {
					if (((Integer) obj[0]) == dado.getRegistro().getDiscente().getId()) {
						dado.setArea((AreaConcentracao) obj[1]);
					}
				}
			}
		}
	}


	/** Carrega os reconhecimentos de cursos de graduação para impressão de diplomas.
	 * @param dados
	 * @throws DAOException
	 */
	private void carregaReconhecimentoCursos(Collection<DadosImpressaoDiploma> dados) throws DAOException {
		if (isEmpty(dados)) return;
		Collection<Integer> idsDiscentes = new ArrayList<Integer>();
		for (DadosImpressaoDiploma dado : dados) {
			idsDiscentes.add(dado.getRegistro().getDiscente().getId());
		}
		String hqlMatrizes = "select dg.id as idDiscente, " +
				" matriz" +
				" from DiscenteGraduacao dg" +
				" inner join dg.matrizCurricular matriz " +
				" where dg.id in "
				+ UFRNUtils.gerarStringIn(idsDiscentes);
		Query qMatrizes = getSession().createQuery(hqlMatrizes);
		@SuppressWarnings("unchecked")
		List<Object[]> matrizes = qMatrizes.list();
		// cache de reconhecimento de matrizes curriculares equivalentes que não possuem ênfase
		Map<Integer, Reconhecimento> cache = new HashMap<Integer, Reconhecimento>();
		if(!isEmpty(matrizes)) {
			for (DadosImpressaoDiploma dado : dados) {
				for (Object[] obj : matrizes) {
					MatrizCurricular matriz = (MatrizCurricular) obj[1];
					Reconhecimento reconhecimento = null;
					if (((Integer) obj[0]) == dado.getRegistro().getDiscente().getId()) {
						// se a matriz possui ênfase, buscar a equivalente sem ênfase
						if (!isEmpty(matriz.getEnfase())) {
							reconhecimento = cache.get(matriz.getId());
							if (isEmpty(reconhecimento)) {
								StringBuilder hqlEquivalente = new StringBuilder("select reconhecimento" +
										" from Reconhecimento reconhecimento" +
										" inner join reconhecimento.matriz matriz" +
										" where matriz.curso.id = :idCurso" +
										" and matriz.grauAcademico.id = :idGrauAcademico" +
										" and matriz.turno.id = :idTurno" +
										" and matriz.enfase is null");
								if (!isEmpty(matriz.getHabilitacao()))
									hqlEquivalente.append(" and matriz.habilitacao.id = :idHabilitacao");
								hqlEquivalente.append(" order by reconhecimento.validade desc, reconhecimento.dataPublicacao desc, reconhecimento.dataDecreto desc");
								Query qEquivalente = getSession().createQuery(hqlEquivalente.toString());
								qEquivalente.setInteger("idCurso", matriz.getCurso().getId());
								qEquivalente.setInteger("idGrauAcademico", matriz.getGrauAcademico().getId());
								qEquivalente.setInteger("idTurno", matriz.getTurno().getId());
								if (!isEmpty(matriz.getHabilitacao()))
									qEquivalente.setInteger("idHabilitacao", matriz.getHabilitacao().getId());
								Reconhecimento equivalente = (Reconhecimento) qEquivalente.setMaxResults(1).uniqueResult();
								cache.put(matriz.getId(), equivalente);
								reconhecimento = equivalente;
							}
						} else {
							// caso contrário, busca o reconhecimento da própria matriz
							reconhecimento = cache.get(matriz.getId());
							if (isEmpty(reconhecimento)) {
								StringBuilder hqlReconhecimento = new StringBuilder("select reconhecimento" +
										" from Reconhecimento reconhecimento" +
										" where reconhecimento.matriz.id = :idMatriz" +
										" order by reconhecimento.validade desc, reconhecimento.dataPublicacao desc, reconhecimento.dataDecreto desc");
								Query qReconhecimento = getSession().createQuery(hqlReconhecimento.toString());
								qReconhecimento.setInteger("idMatriz", matriz.getId());
								Reconhecimento equivalente = (Reconhecimento) qReconhecimento.setMaxResults(1).uniqueResult();
								cache.put(matriz.getId(), equivalente);
								reconhecimento = equivalente;
							}
						}
						dado.setReconhecimento(reconhecimento);
						dado.setMatrizCurricular(matriz);
					}
				}
			}
		}
	}
}
