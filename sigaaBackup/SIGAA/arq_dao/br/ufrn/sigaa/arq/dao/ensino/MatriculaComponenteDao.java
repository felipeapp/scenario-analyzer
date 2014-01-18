/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 02/02/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO com consultas relativas à entidade MatriculaComponente.
 * 
 * @author André Dantas
 *
 */
public class MatriculaComponenteDao extends GenericSigaaDAO  {

	/**
	 * Busca todas as turmas nas quais um discente possui matrícula
	 * com status MATRICULADO.
	 * 
	 * TODO refazer
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasMatriculadasByDiscente(Discente discente) throws DAOException {
        Collection<MatriculaComponente> col = findMatriculadasByDiscente(discente);
        
		Collection<Turma> colTurma = new HashSet<Turma>();
        for( MatriculaComponente tmd: col ){
        	colTurma.add(tmd.getTurma());
        }
        return colTurma;
	}

	/**
	 * Busca todas as turmas,exclusivamente para outros niveis, nas quais um discente possui matrícula
	 * com status MATRICULADO.
	 * 
	 * TODO refazer
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasMatriculadasByDiscenteOutrosCursos(Discente discente, Integer ano, Integer periodo) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select mc.turma ");
		
		hql.append(" FROM MatriculaComponente mc " +
			" WHERE mc.discente.pessoa.id=" + discente.getPessoa().getId() +
			" and mc.situacaoMatricula in " + gerarStringIn(SituacaoMatricula.getSituacoesMatriculadas()) +
			" and mc.ano = " + ano + 
			" and mc.periodo = " + periodo +
			" and mc.discente.nivel <> '" + discente.getNivel() + "'" +
			" and mc.componente.tipoComponente.id in " + gerarStringIn(TipoComponenteCurricular.getNaoAtividades()) +
			" ORDER BY mc.turma.disciplina.codigo");
		
		Collection<Turma> turmas = null;
		@SuppressWarnings("unchecked")
		Collection<Turma> lista  = getSession().createQuery(hql.toString()).list();		
		turmas = lista;
		
		return turmas;
		
	}
	
	/**
	 * Método com mesma funcionalidade que o {@link #findByDiscente(Discente discente, SituacaoMatricula... situacoes)},
	 * mas que recebe como parâmetro uma coleção de situações, e não um array.
	 * .
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscente(DiscenteAdapter discente, Collection<SituacaoMatricula> situacoes) throws DAOException {
		return findByDiscente(discente,  situacoes.toArray(new SituacaoMatricula[situacoes.size()]));

	}
	
	/**
	 * Retorna todas as matrículas ativas de um discente, dados o ano e o período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List <MatriculaComponente> findAtivasByDiscenteAnoPeriodo (DiscenteAdapter discente, int ano, int periodo) throws DAOException{
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.add(Restrictions.eq("discente.id", discente.getId()));
		c.add(Restrictions.eq("ano", (short) ano));
		c.add(Restrictions.eq("periodo", (byte) periodo));
		
		List <Integer> situacoes = new ArrayList <Integer> ();
	
		situacoes.add(SituacaoMatricula.EM_ESPERA.getId());
		situacoes.add(SituacaoMatricula.MATRICULADO.getId());
		
		c.add(Restrictions.in("situacaoMatricula.id", situacoes));
		
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = c.list();
		return rs;
	}

	/**
	 * Busca a lista de matrículas de um determinado aluno que estão com as situações passadas
	 * como parâmetro. Esse método utiliza projeção para limitar o número de informações trazidas
	 * e ser mais rápido que outros métodos semelhantes e que trazem o grafo de objetos completo.
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscenteOtimizado(DiscenteAdapter discente, Collection<TipoComponenteCurricular> tipos, Collection<SituacaoMatricula> situacoes) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			// o null era o conceito que não posssui mais
            hql.append("SELECT mc.id, mc.ano, mc.periodo, mc.mediaFinal,  mc.situacaoMatricula.descricao, t.codigo, " +
            		" mc.componente.codigo, mc.componente.detalhes.nome, mc.componente.detalhes.chTotal, t.observacao, mc.componente.id, mc.tipoIntegralizacao " +
            		" , mc.situacaoMatricula.id, mc.discente.id, t.id " +
            		" FROM MatriculaComponente mc left join mc.turma t" +
            		" WHERE mc.discente.id = :discente");
            
            if (!isEmpty(tipos))
            	hql.append(" and mc.componente.tipoComponente.id in " + gerarStringIn(tipos));
            
            if (!isEmpty(situacoes))
            	hql.append(" and mc.situacaoMatricula.id in " + gerarStringIn(situacoes));

        	hql.append(" ORDER BY mc.ano, mc.periodo, mc.situacaoMatricula.id, mc.componente.codigo");

            Query q = getSession().createQuery(hql.toString());
            q.setInteger("discente", discente.getId());
            @SuppressWarnings("unchecked")
            Collection<Object[]> res = q.list();
            Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
            if (res != null ) {
            	for (Object[] reg : res) {
            		MatriculaComponente mat = new MatriculaComponente((Integer) reg[0]);
            		mat.setAno((Short)reg[1]);
            		mat.setPeriodo((Byte)reg[2]);
            		mat.setMediaFinal((Double) reg[3]);
            		//mat.setConceito((Double) reg[4]);
            		mat.setSituacaoMatricula(new SituacaoMatricula((String) reg[4]));
            		mat.setTurma(new Turma());
            		mat.getTurma().setCodigo((String) reg[5]);
            		mat.getTurma().setObservacao((String) reg[9]);
            		mat.setComponente(new ComponenteCurricular());
            		mat.getComponente().setCodigo((String) reg[6]);
            		mat.getComponente().getDetalhes().setCodigo((String) reg[6]);
            		mat.getComponente().setNome((String) reg[7]);
            		mat.getComponente().setChTotal((Integer) reg[8]);
            		mat.getComponente().setId((Integer) reg[10]);
            		mat.setTipoIntegralizacao((String) reg[11]); 
            		mat.getSituacaoMatricula().setId((Integer) reg[12]);
            		mat.setDiscente(new Discente((Integer) reg[13]));
            		Integer idTurma = (Integer) reg[14];
            		if(idTurma != null)
            			mat.getTurma().setId(idTurma);
            		matriculas.add(mat);
				}
            }
            return matriculas;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Retorna as matrículas de um discente que estão com as situações passadas
	 * como parâmetro.
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findBySituacoes(DiscenteAdapter discente, SituacaoMatricula... situacoes) throws DAOException {
		StringBuilder hql = new StringBuilder();
        hql.append("SELECT mc.id, mc.componente.id, mc.componente.detalhes.equivalencia, " +
        		"mc.componente.detalhes.coRequisito	, mc.componente.detalhes.preRequisito, " +
        		"mc.componente.tipoComponente.id, mc.situacaoMatricula.id, mc.serie.id,mc.serie.numero, " +
        		"mc.componente.codigo, mc.componente.detalhes.nome, mc.ano, mc.periodo " +
        		" FROM MatriculaComponente mc " +
        		" WHERE mc.discente.id = :discente");
        hql.append(" and mc.situacaoMatricula.id in " + gerarStringIn(situacoes));

        Query q = getSession().createQuery(hql.toString());
        q.setInteger("discente", discente.getId());
        @SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
        if (res != null ) {
        	for (Object[] reg : res) {
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[0]);
        		mat.setComponente(new ComponenteCurricular((Integer) reg[1]));
        		mat.getComponente().setEquivalencia((String)reg[2]);
        		mat.getComponente().setCoRequisito((String)reg[3]);
        		mat.getComponente().setPreRequisito((String)reg[4]);
        		mat.getComponente().getTipoComponente().setId((Integer)reg[5]);
        		mat.setSituacaoMatricula(new SituacaoMatricula());
        		mat.getSituacaoMatricula().setId((Integer)reg[6]);
        		if (reg[7] != null) {
        			mat.setSerie(new Serie());
        			mat.getSerie().setId((Integer)reg[7]);
        			mat.getSerie().setNumero((Integer)reg[8]);
        			}
        		matriculas.add(mat);
        		mat.getComponente().setCodigo((String)reg[9]);
        		mat.getComponente().setNome((String)reg[10]);
        		mat.setAno((Short)reg[11]);
        		mat.setPeriodo((Byte)reg[12]);
			}
        }
        return matriculas;
	}

	/**
	 * Retorna uma matrícula com o id e a situação populados de acordo
	 * com o id informado como parâmetro.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findOtimizado(int id) throws DAOException {
		StringBuilder hql = new StringBuilder();
        hql.append("SELECT mc.id, mc.situacaoMatricula.id" +
        		" FROM MatriculaComponente mc " +
        		" WHERE id=:mat");

        Query q = getSession().createQuery(hql.toString());
        q.setInteger("mat", id);
        Object[] res = (Object[]) q.uniqueResult();
        if (res != null) {
        	MatriculaComponente mat = new MatriculaComponente();
        	mat.setId((Integer)res[0]);
        	mat.setSituacaoMatricula(new SituacaoMatricula((Integer)res[1]));
        	return mat;
        }
        return null;
	}

	/**
	 * Busca a lista de matrículas de um determinado aluno que estão com as situações passadas
	 * como parâmetro. Esse método retorna a matrícula completa, com todas as suas dependências,
	 * sendo, portanto, mais custoso que o findByDiscenteOtimizado.  
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscente(DiscenteAdapter discente, SituacaoMatricula... situacoes) throws DAOException {
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.setFetchMode("situacaoMatricula", FetchMode.JOIN);
		c.add( Restrictions.eq("discente.id", discente.getId()) );
		if (situacoes != null && situacoes.length > 0)
			c.add(Restrictions.in("situacaoMatricula", situacoes));
		c.addOrder(Order.asc("ano"));
		c.addOrder(Order.asc("periodo"));
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = c.list();
		return rs;
	}

	/**
	 * Busca matrículas do discente que foram aproveitadas.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findAproveitamentosByDiscente(DiscenteAdapter discente) throws DAOException {
		try {
			Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAproveitadas();
			situacoes.add(SituacaoMatricula.APROVADO);
			Criteria c = getSession().createCriteria(MatriculaComponente.class);
            c.add(Restrictions.eq("discente", discente) );
        	c.add(Restrictions.in("situacaoMatricula", situacoes));
           	c.add(Restrictions.isNull("turma"));
           	c.add(Restrictions.isNull("registroAtividade"));
            c.addOrder(Order.asc("ano"));
            c.addOrder(Order.asc("periodo"));
            @SuppressWarnings("unchecked")
            List <MatriculaComponente> rs = c.list();
        	return rs;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Retorna todas as matrículas de um discente que possuem TURMA e
	 * que estão nas situações passadas como parâmetro.
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscenteComTurma(DiscenteAdapter discente, Integer ano, Integer periodo, SituacaoMatricula... situacoes) throws DAOException {
        Criteria c = getSession().createCriteria(MatriculaComponente.class);
        c.add( Restrictions.eq("discente.id", discente.getId()) );
        c.add( Restrictions.isNotNull("turma") );
        
        if( ano != null )
        	c.add( Restrictions.eq("ano", ano.shortValue()) );
        if( periodo != null )
        	c.add( Restrictions.eq("periodo", periodo.byteValue()) );
        
        if (situacoes != null && situacoes.length > 0)
        	c.add(Restrictions.in("situacaoMatricula", situacoes));
        
        c.addOrder(Order.asc("ano"));
        c.addOrder(Order.asc("periodo"));
        Criteria turma = c.createCriteria("turma");
        Criteria disciplina = turma.createCriteria("disciplina");
        Criteria detalhes = disciplina.createCriteria("detalhes");
        detalhes.addOrder(Order.asc("nome"));
        @SuppressWarnings("unchecked")
        List <MatriculaComponente> rs = c.list();
    	return rs;
	}
	
	/**
	 * Retorna todas as matrículas de um discente que possuem TURMA e
	 * que estão nas situações passadas como parâmetro para trancamento.
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscenteComTurmaParaTrancamento(DiscenteAdapter discente, Integer ano, Integer periodo, SituacaoMatricula... situacoes) throws DAOException {
		String projecao = " mc.id, mc.ano, mc.periodo, cc.id, cc.codigo, cc.detalhes.nome, cc.nivel, " +
						" cc.tipoComponente.id, situacao.descricao, situacao.id, " +
						" turma.id, turma.ano, turma.periodo, turma.dataInicio, turma.dataFim, turma.codigo, polo.id, gestora.id, ta.id, ta.descricao ";
		
		String hql = "select " + projecao +
					"FROM MatriculaComponente mc " +
						" INNER JOIN mc.situacaoMatricula situacao " +
						" LEFT JOIN mc.turma turma " +
						" LEFT JOIN turma.polo polo " +
						" LEFT JOIN mc.componente cc " +
						" LEFT JOIN cc.unidade u " +
						" LEFT JOIN cc.unidade u " +
						" LEFT JOIN u.gestoraAcademica gestora " +
						" LEFT JOIN cc.tipoAtividade ta " +
					" WHERE" +
					" ( " +
					"	cc.tipoComponente.id in " + gerarStringIn(TipoComponenteCurricular.getNaoAtividades()) +
						" or " +
					"	(cc.tipoComponente.id = :idAtividade and cc.formaParticipacao.id = :idFormaParticipacao )" +
					" ) " +
					" AND mc.discente.id = :discente " +
					" AND mc.ano = :ano " +
					" AND mc.periodo = :periodo ";
		if (situacoes != null && situacoes.length > 0)
					hql += " AND situacao.id in " + gerarStringIn(situacoes);
		
		hql += "ORDER BY mc.ano, mc.periodo";
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("discente", discente.getId());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idAtividade", TipoComponenteCurricular.ATIVIDADE);
		q.setInteger("idFormaParticipacao", FormaParticipacaoAtividade.ESPECIAL_COLETIVA);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);

		if (linhas != null ) {
        	for (Object[] reg : linhas) {
        		int cont = 0;
        		
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[cont++]);
        		mat.setAno((Short)reg[cont++]);
        		mat.setPeriodo((Byte)reg[cont++]);
        		
        		mat.setComponente(new ComponenteCurricular());
        		mat.getComponente().setId((Integer) reg[cont++]);
        		mat.getComponente().setCodigo((String) reg[cont++]);
        		mat.getComponente().setNome((String) reg[cont++]);
        		mat.getComponente().setNivel((Character) reg[cont++]);
        		mat.getComponente().getTipoComponente().setId((Integer) reg[cont++]);
        		mat.setSituacaoMatricula(new SituacaoMatricula((String) reg[cont++]));
        		mat.getSituacaoMatricula().setId( (Integer) reg[cont++] );
        		
        		Integer idTurma = (Integer) reg[cont++];
        		
        		if(!isEmpty(idTurma)) {
	        		mat.setTurma(new Turma(idTurma));
	        		mat.getTurma().setAno((Integer)reg[cont++]);
	        		mat.getTurma().setPeriodo((Integer)reg[cont++]);
	        		mat.getTurma().setDataInicio((Date)reg[cont++]);
	        		mat.getTurma().setDataFim((Date)reg[cont++]);
	        		mat.getTurma().setCodigo((String)reg[cont++]);
	        		mat.getTurma().setDisciplina(mat.getComponente());
        		}
        		else {
        			cont += 5;
        		}

        		Integer polo = (Integer) reg[cont++];
        		if(!isEmpty(polo)) {
        			mat.getTurma().setPolo(new Polo(polo));
        		}
        		
        		Integer gestora = (Integer) reg[cont++];
        		if(!isEmpty(gestora)) {
        			mat.getComponente().setUnidade(new Unidade());
        			mat.getComponente().getUnidade().setGestora(new Unidade(gestora));
        		}
        		
        		Integer ra = (Integer) reg[cont++];
        		if(!isEmpty(ra)) {
        			mat.getComponente().getTipoAtividade().setId(ra);
        			mat.getComponente().getTipoAtividade().setDescricao((String) reg[cont]);
        		}
        		
        		matriculas.add(mat);
			}
        }
		
		return matriculas;
	}

	/**
	 * Retorna as matrículas de um discente que pertencem a um determinado
	 * ano-período e que estão com as situações passadas como parâmetro.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscente(DiscenteAdapter discente, int ano, int periodo, SituacaoMatricula... situacoes) throws DAOException {
        Criteria c = getSession().createCriteria(MatriculaComponente.class);
        c.add( Restrictions.eq("discente.id", discente.getId()) );
        c.add( Restrictions.eq("ano", (short)ano) );
        c.add( Restrictions.eq("periodo", (byte)periodo) );
        if (situacoes != null && situacoes.length > 0)
        	c.add(Restrictions.in("situacaoMatricula", situacoes));

        @SuppressWarnings("unchecked")
        List <MatriculaComponente> rs = c.list();
    	return rs;
	}


	/**
	 * Busca todas as matrículas em atividades de um discente que estão
	 * com as situações passadas como parâmetro.
	 * 
	 * @param discente
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findAtividades(DiscenteAdapter discente, SituacaoMatricula... situacao) throws DAOException {
		return findAtividades(discente, null, null, situacao);
	}
	
	/**
	 * Busca todas as matrículas em atividades de um discente que pertencem a
	 * um determinado ano-período e estão com as situações passadas como parâmetro.
	 * 
	 * @param discente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	private Collection<MatriculaComponente> findAtividades(DiscenteAdapter discente, Integer ano, Integer periodo, 
			SituacaoMatricula... situacao) throws DAOException {
        
		 boolean disciplinaTipoDissertacao = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.TESE_DEFINIDA_COMO_DISCIPLINA);
	        
        int tipos[] = new int[] {TipoComponenteCurricular.ATIVIDADE};
        if( disciplinaTipoDissertacao )
        	tipos = new int[] {TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.DISCIPLINA};
		
		StringBuilder hql = new StringBuilder();
        hql.append("select mc.id, mc.ano, mc.periodo, cc.codigo, cc.detalhes.nome, " +
        		" cc.tipoComponente.id, mc.situacaoMatricula.descricao, mc.situacaoMatricula.id, ra, " +
        		" cc.detalhes.chTotal, ta.id, ta.descricao FROM MatriculaComponente mc left join mc.registroAtividade ra " +
        		" left join mc.componente cc left join cc.tipoAtividade ta " +
        		" WHERE cc.tipoComponente.id in " + gerarStringIn(tipos) +
        		" AND mc.discente.id = :discente ");
        if( disciplinaTipoDissertacao )
        	hql.append(" AND ta.id > 0 ");
        hql.append(" AND mc.situacaoMatricula.id in(");
        for (SituacaoMatricula s : situacao)
        	 hql.append(s.getId() + ",");
        
        hql.replace(hql.length() - 1, hql.length(), ")");
    	if (ano != null && periodo != null) {
    		hql.append(" AND mc.ano = " + ano + " AND mc.periodo = " + periodo);
    	}
        hql.append(" ORDER BY mc.ano, mc.periodo, mc.dataCadastro");
        Query q = getSession().createQuery(hql.toString());
        q.setInteger("discente", discente.getId());
        //q.setInteger("tipo", TipoComponenteCurricular.ATIVIDADE);
        
        @SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
        if (res != null ) {
        	for (Object[] reg : res) {
        		
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[0]);
        		mat.setAno((Short)reg[1]);
        		mat.setPeriodo((Byte)reg[2]);
        		
        		mat.setComponente(new ComponenteCurricular());
        		mat.getComponente().setCodigo((String) reg[3]);
        		mat.getComponente().getDetalhes().setCodigo((String) reg[3]);
        		mat.getComponente().setNome((String) reg[4]);
        		mat.getComponente().getTipoComponente().setId((Integer) reg[5]);
        		mat.setSituacaoMatricula(new SituacaoMatricula((String) reg[6]));
        		mat.getSituacaoMatricula().setId( (Integer) reg[7] );
        		
        		mat.setRegistroAtividade((RegistroAtividade) reg[8]);
        		mat.getComponente().setChTotal((Integer) reg[9]);
        		
        		mat.getComponente().getTipoAtividade().setId((Integer) reg[10]);
        		mat.getComponente().getTipoAtividade().setDescricao((String) reg[11]);
        		
        		matriculas.add(mat);
			}
        }
        return matriculas;            
	}

	/**
	 * Busca todas as matrículas em atividades de um discente que estão
	 * com as situações passadas como parâmetro e que são do tipo
	 * passado como parâmetro.
	 * 
	 * @param discente
	 * @param tipo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findAtividades(DiscenteAdapter discente, TipoAtividade tipo, SituacaoMatricula... situacoes ) throws DAOException {
		return findAtividadesByDiscente(discente, tipo, Arrays.asList(situacoes) );
	}

	/**
	 * Busca todas as matrículas em atividades de um discente que estão
	 * com as situações passadas como parâmetro e que são do tipo
	 * passado como parâmetro.
	 * 
	 * @param discente
	 * @param tipo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findAtividadesByDiscente(DiscenteAdapter discente, TipoAtividade tipoAtividade, Collection<SituacaoMatricula> situacoes) throws DAOException {
        StringBuilder hql = new StringBuilder();
       
        boolean disciplinaTipoDissertacao = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.TESE_DEFINIDA_COMO_DISCIPLINA);
        
        int tipos[] = new int[] {TipoComponenteCurricular.ATIVIDADE};
        if( disciplinaTipoDissertacao )
        	tipos = new int[] {TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.DISCIPLINA};
        String projecao = "mc.id," +
        		" mc.componente.codigo," +
        		" mc.componente.detalhes.nome," +
        		" mc.componente.tipoComponente.id," +
        		" mc.ano, mc.periodo," +
        		" mc.situacaoMatricula.id," +
				" mc.situacaoMatricula.descricao, " +
        		" mc.registroAtividade ";
        hql.append("select "+projecao
        		+ " FROM MatriculaComponente mc left join mc.registroAtividade ra WHERE " +
        		" mc.componente.tipoComponente.id in " + gerarStringIn(tipos) +
        		" AND mc.componente.tipoAtividade.id = :tipoAtividade " +
        		" AND mc.discente.id = :discente");
    	hql.append(" AND mc.situacaoMatricula.id in " + gerarStringIn(situacoes));
        hql.append(" ORDER BY mc.ano, mc.periodo, mc.dataCadastro ");
        Query q = getSession().createQuery(hql.toString());
        q.setInteger("discente", discente.getId());
       // q.setInteger("tipo", TipoComponenteCurricular.ATIVIDADE);
        q.setInteger("tipoAtividade", tipoAtividade.getId());
        
        @SuppressWarnings("unchecked")
        List <Object[]> rs = q.list();
        Collection<MatriculaComponente> lista = HibernateUtils.parseTo(rs, projecao, MatriculaComponente.class, "mc");
        return lista;
	}
	
	/**
	 * Busca uma matrícula do discente em uma atividade que esteja com o status
	 * MATRICULADO.
	 * 
	 * @param discente
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findAtividadeMatriculadoByDiscenteComponente(Discente discente, ComponenteCurricular componente) throws DAOException {
		try {
            StringBuilder hql = new StringBuilder();
            hql.append("select new MatriculaComponente(mc.id, mc.componente.codigo, mc.componente.detalhes.nome, mc.componente.tipoComponente.id, "
            		+ "mc.situacaoMatricula.id, mc.situacaoMatricula.descricao, ra, mc.ano, mc.periodo) "
            		+ "FROM MatriculaComponente mc left join mc.registroAtividade ra WHERE " +
            		" mc.componente.tipoComponente.id = :tipo " +
            		" AND mc.componente.id = :idAtividade " +
            		" AND mc.discente.id = :discente");
        	hql.append(" AND mc.situacaoMatricula.id = " + SituacaoMatricula.MATRICULADO.getId() );
        	hql.append(" ORDER BY mc.ano desc, mc.periodo desc " );

            Query q = getSession().createQuery(hql.toString());
            q.setInteger("discente", discente.getId());
            q.setInteger("tipo", TipoComponenteCurricular.ATIVIDADE);
            q.setInteger("idAtividade", componente.getId());

            @SuppressWarnings("unchecked")
            Collection<MatriculaComponente> matriculas = q.list();
            if( !isEmpty( matriculas ) )
            	return matriculas.iterator().next();
            return null;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Retorna o número total de matrículas que um discente tem 
	 * em uma turma que estão com uma determinada situação.
	 * 
	 * @param discente
	 * @param t
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public int countByDiscenteTurmas(DiscenteAdapter discente, Turma t,  SituacaoMatricula situacao) throws DAOException {
        Criteria c = getSession().createCriteria(MatriculaComponente.class);
        c.setProjection(Projections.count("id"));
        c.add( Restrictions.eq("discente", discente.getDiscente()) );
        c.add( Restrictions.eq("situacaoMatricula", situacao) );
        c.add( Restrictions.eq("turma.id", t.getId()) );
        return (Integer)c.uniqueResult();
	}


	/**
	 * Retorna todas as matrículas de um discente com o status MATRICULADO ou EM_ESPERA.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculadasByDiscente(Discente discente) throws DAOException{
		return findMatriculadasByDiscenteAnoPeriodo(discente, null, null, TipoComponenteCurricular.getNaoAtividades());
	}

	/**
	 * Retorna as matriculas de um discente em um ano/período com o status MATRICULADO ou EM_ESPERA
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculadasByDiscenteAnoPeriodo(DiscenteAdapter discente, Integer ano, Integer periodo, Collection<TipoComponenteCurricular> tipos) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {

			String sql = "select mc.id_matricula_componente,mc.id_turma, sit.id_situacao_matricula as ID_SITUACAO, sit.descricao as SITUACAO, ccd.nome as COMPONENTE_NOME , " +
			"cc.codigo as COMPONENTE_CODIGO, t.codigo as COD_TURMA, t.descricao_horario as DESCRICAO_HORARIO, t.local as LOCAL, " +
			"p1.nome as DOCENTE_NOME, dt.id_docente as ID_DOCENTE, ccd.ch_total as COMPONENTE_CH_TOTAL, " +
			"mc.ano as ANO, mc.periodo as PERIODO, t.id_turma_bloco, cc.id_tipo_componente, " +
			"dt.id_docente_externo as ID_DOCENTE_EXTERNO, p2.nome as DOCENTE_NOME_EXTERNO, t.data_inicio, t.data_fim, tcc.descricao as tipoComponenteCurricular, " +
			"serie.id_serie as ID_SERIE, serie.descricao as SERIE_DESCRICAO, serie.numero as SERIE_NUMERO " +
			"FROM discente d " +
			" join ensino.matricula_componente mc on (mc.id_discente=d.id_discente)" +
			" join ensino.componente_curricular cc on (mc.id_componente_curricular=cc.id_disciplina)" +
			" join ensino.tipo_componente_curricular tcc on ( cc.id_tipo_componente = tcc.id_tipo_disciplina )" + 
			" join ensino.situacao_matricula sit on (mc.id_situacao_matricula = sit.id_situacao_matricula)" +
			" join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe=ccd.id_componente_detalhes)" +
			" left join ensino.turma t on (mc.id_turma = t.id_turma)" +
			" left join ensino.docente_turma dt on (t.id_turma = dt.id_turma)" +
			" left join rh.servidor s on (dt.id_docente=s.id_servidor)" +
			" left join comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa) " +
			" left join ensino.docente_externo de on (dt.id_docente_externo = de.id_docente_externo) " +
			" left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) " +
			" left join medio.serie serie on (serie.id_serie = mc.id_serie) " +
			"WHERE d.id_discente=? and (mc.id_situacao_matricula=? or mc.id_situacao_matricula=?) ";
			if( ano != null )
				sql += "AND mc.ano =? ";
			if( periodo != null )
				sql += "AND mc.periodo =? ";
			if (!isEmpty(tipos))
				sql += " AND cc.id_tipo_componente in " + gerarStringIn(tipos);
			
			
			sql += "ORDER BY ccd.nome asc, mc.data_cadastro asc";

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);

			st.setInt(1, discente.getId());
			st.setInt(2, SituacaoMatricula.EM_ESPERA.getId());
			st.setInt(3, SituacaoMatricula.MATRICULADO.getId());
			if( ano != null )
				st.setInt(4, ano);
			if( periodo != null )
				st.setInt(5, periodo);

			rs = st.executeQuery();

			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
			int idAtual = 0;
			while(rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				if (idAtual == mc.getId() && idAtual > 0)
					mc = matriculas.get(matriculas.size()-1);
				else
					mc.setTurma(new Turma());
				DocenteTurma dt = new DocenteTurma();
				if (rs.getInt("ID_DOCENTE") > 0) {
					dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
					dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				} else {
					dt.setDocenteExterno(new DocenteExterno(rs.getInt("ID_DOCENTE_EXTERNO")));
					dt.getDocenteExterno().getPessoa().setNome(rs.getString("DOCENTE_NOME_EXTERNO"));
				}
				mc.getTurma().getDocentesTurmas().add(dt);
				if (idAtual == mc.getId() && idAtual > 0)
					continue;

				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("ID_SITUACAO")));
				mc.getSituacaoMatricula().setDescricao(rs.getString("SITUACAO"));
				mc.setAno((short)rs.getInt("ANO"));
				mc.setPeriodo((byte)rs.getInt("PERIODO"));

				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setNome(rs.getString("COMPONENTE_NOME"));
				cc.getTipoComponente().setId(rs.getInt("id_tipo_componente"));
				cc.getTipoComponente().setDescricao(rs.getString("tipoComponenteCurricular"));
				cc.setCodigo(rs.getString("COMPONENTE_CODIGO"));
				cc.setChTotal(rs.getInt("COMPONENTE_CH_TOTAL"));
				cc.getDetalhes().setCodigo(rs.getString("COMPONENTE_CODIGO"));
				mc.getTurma().setDisciplina(cc);
				mc.setComponente(cc);
				mc.setDetalhesComponente(cc.getDetalhes());

				mc.getTurma().setId(rs.getInt("ID_TURMA"));
				mc.getTurma().setCodigo(rs.getString("COD_TURMA"));
				mc.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				mc.getTurma().setLocal(rs.getString("LOCAL"));
				mc.getTurma().setDataInicio(rs.getDate("data_inicio"));
				mc.getTurma().setDataFim(rs.getDate("data_fim"));
				mc.getTurma().setDisciplina(cc);
				
				if ( rs.getInt("ID_SERIE") > 0 ){
					mc.setSerie(new Serie());
					mc.getSerie().setId(rs.getInt("ID_SERIE"));
					mc.getSerie().setDescricao(rs.getString("SERIE_DESCRICAO"));
					mc.getSerie().setNumero(rs.getInt("SERIE_NUMERO"));
				}

				idAtual = mc.getId();
				matriculas.add(mc);
			}

			return matriculas;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}
	
	/**
	 * Retorna as matriculas de um discente renovadas em um determinado ano/período com o status MATRICULADO ou EM_ESPERA.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findRenovadasByDiscenteAnoPeriodo(DiscenteAdapter discente, Integer ano, Integer periodo) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT mc.id_matricula_componente,mc.id_turma, sit.id_situacao_matricula as ID_SITUACAO, sit.descricao as SITUACAO, " +
							"ccd.nome as COMPONENTE_NOME , cc.codigo as COMPONENTE_CODIGO, t.codigo as COD_TURMA, t.descricao_horario as DESCRICAO_HORARIO, " +
							"t.local as LOCAL, p1.nome as DOCENTE_NOME, dt.id_docente as ID_DOCENTE, ccd.ch_total as COMPONENTE_CH_TOTAL, mc.ano as ANO, " +
							"mc.periodo as PERIODO, t.id_turma_bloco, cc.id_tipo_componente, dt.id_docente_externo as ID_DOCENTE_EXTERNO, " +
							"p2.nome as DOCENTE_NOME_EXTERNO, t.data_inicio, t.data_fim, tcc.descricao as tipoComponenteCurricular " +
							"FROM stricto_sensu.renovacao_atividade_pos ren " +
								"LEFT JOIN graduacao.solicitacao_matricula sol using (id_solicitacao_matricula) " +
								"JOIN ensino.matricula_componente mc on (ren.id_matricula_componente = mc.id_matricula_componente or sol.id_matricula_gerada = mc.id_matricula_componente) " +
								"JOIN ensino.componente_curricular cc on (mc.id_componente_curricular=cc.id_disciplina) " +
								"JOIN ensino.tipo_componente_curricular tcc on ( cc.id_tipo_componente = tcc.id_tipo_disciplina ) " +
								"JOIN ensino.componente_curricular_detalhes ccd on (cc.id_detalhe=ccd.id_componente_detalhes) " +
								"JOIN ensino.situacao_matricula sit on (mc.id_situacao_matricula = sit.id_situacao_matricula) " +
								"LEFT JOIN ensino.turma t on (mc.id_turma = t.id_turma) " +
								"LEFT JOIN ensino.docente_turma dt on (t.id_turma = dt.id_turma) " +
								"LEFT JOIN rh.servidor s on (dt.id_docente=s.id_servidor) " +
								"LEFT JOIN comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa) " +
								"LEFT JOIN ensino.docente_externo de on (dt.id_docente_externo = de.id_docente_externo) " +
								"LEFT JOIN comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) " +
							"WHERE 1 = 1 " +
							"AND (mc.id_discente = ? OR sol.id_discente = ? )" +
							"AND mc.id_situacao_matricula in (?,?)";
								
			if(ano != null) 
				sql += "AND ren.ano = ? ";
								
			if(periodo != null)
				sql += "AND ren.periodo = ? ";
			
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			
			int cont = 1;
			
			st.setInt(cont++, discente.getId());
			st.setInt(cont++, discente.getId());
			st.setInt(cont++, SituacaoMatricula.EM_ESPERA.getId());
			st.setInt(cont++, SituacaoMatricula.MATRICULADO.getId());
			if(ano != null) 
				st.setInt(cont++, ano);
			if(periodo != null)
				st.setInt(cont++, periodo);
			
			rs = st.executeQuery();
			
			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
			int idAtual = 0;
			
			while(rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				if (idAtual == mc.getId() && idAtual > 0)
					mc = matriculas.get(matriculas.size()-1);
				else
					mc.setTurma(new Turma());
				DocenteTurma dt = new DocenteTurma();
				if (rs.getInt("ID_DOCENTE") > 0) {
					dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
					dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				} else {
					dt.setDocenteExterno(new DocenteExterno(rs.getInt("ID_DOCENTE_EXTERNO")));
					dt.getDocenteExterno().getPessoa().setNome(rs.getString("DOCENTE_NOME_EXTERNO"));
				}
				mc.getTurma().getDocentesTurmas().add(dt);
				if (idAtual == mc.getId() && idAtual > 0)
					continue;

				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("ID_SITUACAO")));
				mc.getSituacaoMatricula().setDescricao(rs.getString("SITUACAO"));
				mc.setAno((short)rs.getInt("ANO"));
				mc.setPeriodo((byte)rs.getInt("PERIODO"));

				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setNome(rs.getString("COMPONENTE_NOME"));
				cc.getTipoComponente().setId(rs.getInt("id_tipo_componente"));
				cc.getTipoComponente().setDescricao(rs.getString("tipoComponenteCurricular"));
				cc.setCodigo(rs.getString("COMPONENTE_CODIGO"));
				cc.setChTotal(rs.getInt("COMPONENTE_CH_TOTAL"));
				cc.getDetalhes().setCodigo(rs.getString("COMPONENTE_CODIGO"));
				mc.getTurma().setDisciplina(cc);
				mc.setComponente(cc);
				mc.setDetalhesComponente(cc.getDetalhes());

				mc.getTurma().setId(rs.getInt("ID_TURMA"));
				mc.getTurma().setCodigo(rs.getString("COD_TURMA"));
				mc.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				mc.getTurma().setLocal(rs.getString("LOCAL"));
				mc.getTurma().setDataInicio(rs.getDate("data_inicio"));
				mc.getTurma().setDataFim(rs.getDate("data_fim"));
				mc.getTurma().setDisciplina(cc);


				idAtual = mc.getId();
				matriculas.add(mc);
			}
			
			return matriculas;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Busca as matrículas de um discente em um conjunto de componentes curriculares
	 * e que possuem situação dentre as situações passadas como parâmetro. 
	 * 
	 * @param discente
	 * @param componentes
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	private Collection<MatriculaComponente> findByDiscenteEDisciplina(DiscenteAdapter discente, Collection<ComponenteCurricular> componentes, Collection<SituacaoMatricula> situacoes) throws DAOException {
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.add(Restrictions.eq("discente.id", discente.getId()));
		c.add(Restrictions.in("situacaoMatricula", situacoes));
		c.add(Restrictions.in("componente", componentes));
		c.setProjection(Projections.property("id"));
		
		
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = c.list();
		return rs;
	}

	/**
	 * Busca as matrículas de um discente em um conjunto de componentes curriculares
	 * e que possuem situação dentre as situações passadas como parâmetro. 
	 * 
	 * @param discente
	 * @param componentes
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	private Collection<MatriculaComponente> findByDiscenteEDisciplina(DiscenteAdapter discente, Collection<ComponenteCurricular> componentes, SituacaoMatricula... situacoes) throws DAOException {
		return findByDiscenteEDisciplina(discente, componentes, Arrays.asList(situacoes));
	}

	/**
	 * Busca, de forma otimizada, as matrículas de um discente em um conjunto de componentes 
	 * curriculares e que possuem situação dentre as situações passadas como parâmetro.
	 * 
	 * @param discente
	 * @param componentes
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscenteOtimizado(DiscenteAdapter discente, Collection<ComponenteCurricular> componentes, SituacaoMatricula... situacoes) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT mc.id_matricula_componente, sit.descricao as SITUACAO, ccd.nome as COMPONENTE_NOME , " +
			"ccd.codigo as COMPONENTE_CODIGO, ccd.ch_total as COMPONENTE_CH_TOTAL, mc.media_final, " +
			"mc.ano as ANO, mc.periodo as PERIODO, cc.id_tipo_componente, ccd.id_componente_detalhes, cc.id_disciplina, mc.tipo_integralizacao " +
			" FROM discente d " +
			" join ensino.matricula_componente mc on (mc.id_discente=d.id_discente)" +
			" join ensino.componente_curricular cc on (mc.id_componente_curricular=cc.id_disciplina)" +
			" join ensino.situacao_matricula sit on (mc.id_situacao_matricula = sit.id_situacao_matricula)" +
			" join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe=ccd.id_componente_detalhes)" +
			" WHERE d.id_discente=? and mc.id_situacao_matricula in " + gerarStringIn(situacoes) +
			" and mc.id_componente_curricular in " + gerarStringIn(componentes) +
			" ORDER BY mc.ano, mc.periodo, mc.data_cadastro asc, ccd.nome asc";

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);

			st.setInt(1, discente.getId());
			rs = st.executeQuery();

			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
			while(rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));

				mc.setSituacaoMatricula(new SituacaoMatricula());
				mc.getSituacaoMatricula().setDescricao(rs.getString("SITUACAO"));
				mc.setAno((short)rs.getInt("ANO"));
				mc.setMediaFinal(rs.getDouble("media_final"));
				mc.setPeriodo((byte)rs.getInt("PERIODO"));

				ComponenteCurricular cc = new ComponenteCurricular(rs.getInt("id_disciplina"));
				cc.setNome(rs.getString("COMPONENTE_NOME"));
				cc.getTipoComponente().setId(rs.getInt("id_tipo_componente"));
				cc.setCodigo(rs.getString("COMPONENTE_CODIGO"));
				cc.setChTotal(rs.getInt("COMPONENTE_CH_TOTAL"));
				cc.getDetalhes().setCodigo(rs.getString("COMPONENTE_CODIGO"));
				cc.getDetalhes().setId(rs.getInt("id_componente_detalhes"));
				mc.setComponente(cc);
				mc.setDetalhesComponente(cc.getDetalhes());
				mc.setTipoIntegralizacao(rs.getString("tipo_integralizacao"));
				
				matriculas.add(mc);
			}

			return matriculas;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Busca as matrículas de um discente em um componente curricular
	 * e que possui situação dentre as situações passadas como parâmetro. 
	 * 
	 * @param discente
	 * @param componente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscenteEDisciplina(DiscenteAdapter discente, ComponenteCurricular componente, SituacaoMatricula... situacoes ) throws DAOException {
		ArrayList<ComponenteCurricular> l = new ArrayList<ComponenteCurricular>(0);
		l.add(componente);
		return findByDiscenteEDisciplina(discente, l, situacoes);
	}


	/**
	 * Retorna true se existir uma matrícula para o discente no conjunto de componentes especificado
	 * e com situação dentre as situações passadas como parâmetro. Retorna false se não existir. 
	 * 
	 * @param discente
	 * @param componentes
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	private boolean containsByDiscenteEComponentes(Discente discente, Collection<ComponenteCurricular> componentes, SituacaoMatricula... situacoes ) throws DAOException {
		Collection<MatriculaComponente> l = findByDiscenteEDisciplina(discente, componentes, situacoes);
		return l != null && !l.isEmpty();
	}

	
	/**
	 * Busca uma lista de componentes curriculares nos quais um discente possui
	 * uma matrícula com status MATRICULADO. 
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesMatriculadosByDiscente(DiscenteAdapter discente) throws DAOException {
		return findComponentesSituacaoByDiscente(discente, SituacaoMatricula.MATRICULADO);
	}
	
	/**
	 * Busca uma lista de componentes curriculares nos quais um discente possui
	 * uma matrícula com os status passados como parâmetro. 
	 * 
	 * @param discente
	 * @param collection
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesSituacaoByDiscente(DiscenteAdapter discente, Collection<SituacaoMatricula> collection) throws DAOException {
		return findComponentesSituacaoByDiscente(discente, collection.toArray(new SituacaoMatricula[collection.size()]));
	}
	
	/**
	 * Busca uma lista de componentes curriculares nos quais um discente possui
	 * uma matrícula com os status passados como parâmetro. 
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	private List<ComponenteCurricular> findComponentesSituacaoByDiscente(DiscenteAdapter discente, SituacaoMatricula... situacoes) throws DAOException {
		Query q = getSession().createQuery("select distinct mc.componente from "
				+ "MatriculaComponente mc where mc.discente.id = ? and mc.situacaoMatricula.id in " 
				+ gerarStringIn(situacoes)
				+ " order by mc.componente.codigo");

		q.setInteger(0, discente.getId());

		@SuppressWarnings("unchecked")
		List <ComponenteCurricular> rs = q.list();
        return rs;
	}

	/**
	 * Busca uma lista de componentes curriculares nos quais um discente possui
	 * uma matrícula para o ano-período informado 
	 * e com status {@link SituacaoMatricula#MATRICULADO}
	 * e turma com situação {@link SituacaoTurma#ABERTA}. 
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesMatriculadosTurmaAbertaByDiscente(DiscenteAdapter discente, Integer ano, Integer periodo) throws DAOException {

		StringBuilder hql = new StringBuilder(" SELECT DISTINCT c FROM ");
		hql.append(" MatriculaComponente mc INNER JOIN mc.componente c ");
		hql.append(" INNER JOIN mc.turma t ");
		hql.append(" WHERE mc.discente.id = :discente ");
		hql.append(" AND mc.situacaoMatricula.id = :situacaoMatricula ");
		hql.append(" AND t.situacaoTurma.id = :situacaoTurma ");
		
		if( !isEmpty(ano) &&   !isEmpty(periodo) )
			hql.append(" AND mc.ano = :ano AND mc.periodo = :periodo ");
		
		hql.append(" AND c.tipoComponente.id <> :tipoComponente ");
		hql.append(" ORDER BY c.codigo ");

		Query q = getSession().createQuery( hql.toString() );
		
		q.setInteger("discente", discente.getId());
		q.setInteger("situacaoMatricula", SituacaoMatricula.MATRICULADO.getId());
		q.setInteger("situacaoTurma", SituacaoTurma.ABERTA);
		q.setInteger("tipoComponente", TipoComponenteCurricular.ATIVIDADE);
		
		if( !isEmpty(ano) &&   !isEmpty(periodo) ){
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}	
		
		@SuppressWarnings("unchecked")
		List <ComponenteCurricular> rs = q.list();
        return rs;
	}

	/**
	 * Busca uma matrícula do discente em uma turma cujo status seja MATRICULADO.
	 * 
	 * @param discente
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findMatriculadoByDiscenteTurma(DiscenteAdapter discente, Turma turma) throws DAOException {
		Query q = getSession().createQuery("from MatriculaComponente mc where mc.turma.id = :turma " +
				"and mc.discente.id = :discente and mc.situacaoMatricula.id = :situacao");
		q.setInteger("turma", turma.getId());
		q.setInteger("discente", discente.getId());
		q.setInteger("situacao", SituacaoMatricula.MATRICULADO.getId());
		return (MatriculaComponente) q.uniqueResult();
	}
	
	/**
	 * Busca matrículas do discente nas turmas passadas cujo status seja MATRICULADO.
	 * 
	 * @param discente
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MatriculaComponente> findMatriculadosByDiscenteTurmas(DiscenteAdapter discente, Object ...ids) throws DAOException {
		Query q = getSession().createQuery("from MatriculaComponente mc where mc.turma.id in "+UFRNUtils.gerarStringIn(ids)+" " +
				"and mc.discente.id = :discente and mc.situacaoMatricula.id = :situacao");
		q.setInteger("discente", discente.getId());
		q.setInteger("situacao", SituacaoMatricula.MATRICULADO.getId());
		return q.list();
	}

	/**
	 * Persiste uma coleção de objetos AlteracaoMatricula.
	 * 
	 * @param alteracoes
	 * @throws DAOException
	 */
	public void persistirAlteracoes(Collection<AlteracaoMatricula> alteracoes) throws DAOException {
		Connection con = null;
		Statement st = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.createStatement();
			for (AlteracaoMatricula alteracao : alteracoes) {
				createNoFlush(alteracao);
				String sql = "UPDATE ensino.matricula_componente SET tipo_integralizacao =  '" + alteracao.getTipoIntegralizacaoNovo() + "'"  +
						" WHERE id_matricula_componente = " + alteracao.getMatricula().getId();
				System.out.println(sql);
				st.addBatch(sql);
			}
			st.executeBatch();
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Retorna lista com projeção das matrículas do discente informado
	 * que estão com situação APROVADO, REPROVADO, REPROVADO POR FALTA, MATRICULADO
	 * ou EM ESPERA, além dos aproveitamentos. 
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findPagaseMatriculadasByDiscente(DiscenteAdapter discente) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT mc.id_matricula_componente, comp.id_disciplina, comp.nivel, det.id_componente_detalhes, det.cr_aula, det.cr_estagio, det.cr_laboratorio, det.cr_ead, " +
					" det.ch_aula, det.ch_laboratorio, det.ch_estagio, det.ch_ead, mc.tipo_integralizacao, mc.id_situacao_matricula, comp.codigo, det.nome, mc.data_cadastro, comp.id_unidade " +
					" FROM ensino.matricula_componente mc join ensino.componente_curricular comp " +
							" on (mc.id_componente_curricular = comp.id_disciplina) " +
							" join ensino.componente_curricular_detalhes det on ( comp.id_detalhe = det.id_componente_detalhes ) " +
					" WHERE mc.id_discente=? " +
					" and mc.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesPagasEMatriculadas()) +
					" order by mc.data_cadastro";

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, discente.getId());
			rs = st.executeQuery();

			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
			while(rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setTipoIntegralizacao(rs.getString("tipo_integralizacao"));
				mc.setSituacaoMatricula(new SituacaoMatricula( rs.getInt("id_situacao_matricula") ));
				ComponenteCurricular cc = new ComponenteCurricular( rs.getInt("id_disciplina") );
				cc.setCodigo(rs.getString("codigo"));
				cc.setNome(rs.getString("nome"));
				cc.setNivel(((rs.getString("nivel").toCharArray())[0]));
				cc.setUnidade(new Unidade(rs.getInt("id_unidade")));
				mc.setComponente(cc);
				mc.setDataCadastro(rs.getDate("data_cadastro"));
				mc.setDetalhesComponente(new ComponenteDetalhes(rs.getInt("id_componente_detalhes")));
				mc.getDetalhesComponente().setCrTotal(rs.getInt("cr_aula") + rs.getInt("cr_estagio") + rs.getInt("cr_laboratorio") + rs.getInt("cr_ead"));
				mc.getDetalhesComponente().setChAula(rs.getInt("ch_aula"));
				mc.getDetalhesComponente().setChLaboratorio(rs.getInt("ch_laboratorio"));
				mc.getDetalhesComponente().setChEstagio(rs.getInt("ch_estagio"));				
				mc.getDetalhesComponente().setChEad(rs.getInt("ch_ead"));	
				matriculas.add(mc);
			}

			return matriculas;
		} catch(Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Retorna o número total de alunos matriculados em uma turma, podendo consider ou não
	 * se as matrículas estão válidas ainda, ou seja, se o aluno não trancou, desistiu, cancelou, etc.
	 * 
	 * @param turma
	 * @param ativos
	 * @return
	 * @throws DAOException
	 */
	public long findTotalMatriculasByTurma(Turma turma, Boolean ativos) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(mc.id) ");
		hql.append(" from MatriculaComponente mc where mc.turma.id = :turma ");

		if (ativos != null && ativos) {
			int[] status = { SituacaoMatricula.MATRICULADO.getId(),
					SituacaoMatricula.APROVADO.getId(),
					SituacaoMatricula.TRANCADO.getId(),
					SituacaoMatricula.REPROVADO.getId(),
					SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(),
					SituacaoMatricula.REPROVADO_FALTA.getId()};
			hql.append("and mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(status));
		} else if (ativos != null && !ativos) {
			int[] status = { SituacaoMatricula.DESISTENCIA.getId(),
					SituacaoMatricula.CANCELADO.getId(),
					SituacaoMatricula.EM_ESPERA.getId(),
					SituacaoMatricula.EXCLUIDA.getId(),
					SituacaoMatricula.INDEFERIDA.getId(),
					SituacaoMatricula.NAO_CONCLUIDO.getId()};
			hql.append("and mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(status));
		}

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("turma", turma.getId());
		return (Long) q.uniqueResult();
	}

	
	/**
	 * Retorna o total de matrículas da turma informada com as situações informadas
	 * @param turma
	 * @param situacoesMatricula
	 * @return
	 * @throws DAOException
	 */
	public long findTotalMatriculasByTurmaSituacao(Turma turma, Integer... situacoesMatricula) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT count(mc.id) ");
		hql.append(" FROM MatriculaComponente mc WHERE mc.turma.id = :turma ");

		hql.append("and mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(situacoesMatricula));

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("turma", turma.getId());
		return (Long) q.uniqueResult();
	}
	
	/**
	 * Retorna o número total de alunos matriculados em uma turma e que
	 * estão com matrícula válida. 
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public long findTotalMatriculasByTurmaAtivos(Turma turma) throws DAOException {
		return findTotalMatriculasByTurma(turma, true);
	}

	/**
	 * Retorna o número de matrículas efetuadas em um determinado componente curricular.
	 * Consulta usada pra saber se pode alterar CH de um componente.
	 * 
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	public long findTotalJaMatriculadosByComponente(int componente) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(mc.id) ");
		hql.append(" from MatriculaComponente mc where mc.componente.id = :comp ");
		int[] situacao = { SituacaoMatricula.CANCELADO.getId(), SituacaoMatricula.EXCLUIDA.getId(), SituacaoMatricula.INDEFERIDA.getId()};
		hql.append(" and mc.situacaoMatricula.id not in " + UFRNUtils.gerarStringIn(situacao));
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("comp", componente);
		return (Long) q.uniqueResult();
	}

	/**
	 * Retorna a quantidade de matrículas componente de um discente com as situações informadas
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public int countMatriculasByDiscente(DiscenteAdapter discente, SituacaoMatricula... situacoes) throws DAOException {
		return countMatriculasByDiscente(discente, 0, 0, situacoes);
	}

	/**
	 * Retorna a quantidade de matrículas componente de um discente com as situações informadas em um determinado ano-período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public int countMatriculasByDiscente(DiscenteAdapter discente, int ano, int periodo, SituacaoMatricula... situacoes) throws DAOException {
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT count(id) FROM MatriculaComponente WHERE discente.id="+discente.getId());
        if (ano > 0)
        	hql.append(" and ano="+ano);
        if (periodo > 0)
        	hql.append(" and periodo="+periodo);
        if( !isEmpty(situacoes) )
        	hql.append(" and situacaoMatricula.id in "+gerarStringIn(situacoes));

        return ((Long)getSession().createQuery(hql.toString()).uniqueResult()).intValue();
	}
	
	/**
	 * Retorna a quantidade de matrículas componente de um discente com as situações informadas em um determinado ano-período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public int countMatriculasByTurma(Turma turma, SituacaoMatricula... situacoes) throws DAOException {
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT count(id) FROM MatriculaComponente WHERE turma.id="+turma.getId());
        if( !isEmpty(situacoes) )
        	hql.append(" and situacaoMatricula.id in "+gerarStringIn(situacoes));

        return ((Long)getSession().createQuery(hql.toString()).uniqueResult()).intValue();
	}
	
	/**
	 * Retorna a quantidade de matrículas em turmas de um discente com as situações informadas em um determinado ano/período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public int countMatriculasTurmasByDiscente(Discente discente, int ano, int periodo, SituacaoMatricula... situacoes) throws DAOException {
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT count(id) FROM MatriculaComponente WHERE discente.id="+discente.getId());
        if (ano > 0)
        	hql.append(" and ano="+ano);
        if (periodo > 0)
        	hql.append(" and periodo="+periodo);
        if( !isEmpty(situacoes) )
        	hql.append(" and situacaoMatricula.id in "+gerarStringIn(situacoes));
        	hql.append(" and turma is not null");

        return ((Long)getSession().createQuery(hql.toString()).uniqueResult()).intValue();
	}

	/**
	 * Retorna o número de matrículas com situação EM ESPERA ou MATRICULADO
	 * que um discente possui em determinado componente curricular.
	 * 
	 * @param discente
	 * @param atividade
	 * @return
	 */
	public long countMatriculadasByDiscenteComponente(DiscenteAdapter discente, ComponenteCurricular atividade, SituacaoMatricula... situacoes) {

		if ( isEmpty(situacoes) ) {
			// Se não foram informadas as situações, utilizar apenas as matriculadas
			Collection<SituacaoMatricula> situacoesMatriculadas = SituacaoMatricula.getSituacoesMatriculadas();
			situacoes = situacoesMatriculadas.toArray(new SituacaoMatricula[situacoesMatriculadas.size()]);
		}
		
		StringBuilder hql = new StringBuilder(); 
		hql.append(	"select count(*) from MatriculaComponente " +
		" where situacaoMatricula.id in " + gerarStringIn(situacoes) +
		" and discente.id = ? and componente.id = ?" );
		
		return (Long) getHibernateTemplate().uniqueResult(hql.toString() ,
				new Object[] {discente.getId(), atividade.getId()});
	}
	
	/**
	 * Buscar os componentes curriculares em que um discente possui a partir
	 * de um determinado número de reprovações
	 * 
	 * @param discente
	 * @param numeroReprovacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesReprovadosByDiscente(Discente discente, int numeroReprovacoes) throws DAOException {
		
		// Preparar consulta
		String projecao = "componente.id, componente.codigo, componente.detalhes.nome";
		String hql = "select " + projecao +
			" from MatriculaComponente  " +
			" where discente.id = :idDiscente " +
			" and situacaoMatricula.id in " + gerarStringIn(SituacaoMatricula.getSituacoesReprovadas()) +
			" group by componente.id, componente.codigo, componente.detalhes.nome " +
			" having count(componente.id) >= :numeroReprovacoes";

		// Definir parâmetros
		Query query = getSession().createQuery(hql);
		query.setInteger("idDiscente", discente.getId());
		query.setInteger("numeroReprovacoes", numeroReprovacoes);

		// Realizar consulta
		Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
		try {
			@SuppressWarnings("unchecked")
			List <Object []> rs = query.list();
			matriculas = HibernateUtils.parseTo(rs, projecao, MatriculaComponente.class);
		} catch (Exception e) {
			throw new DAOException(e);
		} 
		
		// Transformar resultado de Matrículas para Componentes
		@SuppressWarnings("unchecked")
		List <ComponenteCurricular> rs = (List<ComponenteCurricular>) CollectionUtils.collect(matriculas, new Transformer() {
			public Object transform(Object obj) {
				return ((MatriculaComponente) obj).getComponente();
			}
		});
		
		return rs;
		
	}

	/**
	 * Seta os tipos de integralização de todas as matrículas de um aluno
	 * para null;
	 * 
	 * @param discente
	 * @throws DAOException
	 */
	public void zerarTiposIntegralizacao(DiscenteGraduacao discente) throws DAOException {
		update("UPDATE ensino.matricula_componente SET tipo_integralizacao = null where id_discente=?", discente.getId() );
	}

	/**
	 * Zera os tipos de integralizações dos discentes ativos associados ao currículo.
	 * 
	 * @param curriculo
	 * @throws DAOException
	 */
	public void zerarTipoIntegralizacoes( Curriculo curriculo ) throws DAOException{
		
		if( curriculo.getCurso().getNivel() != NivelEnsino.GRADUACAO )
			return;
		
			String sql = "UPDATE ensino.matricula_componente SET tipo_integralizacao = null "
					+ " WHERE id_matricula_componente in( "
					+ " SELECT DISTINCT mc.id_matricula_componente FROM ensino.matricula_componente  mc " 
					+ " JOIN discente d USING(id_discente) "
					+ " WHERE d.nivel = '" + NivelEnsino.GRADUACAO + "' "
					+ " AND d.status in " + gerarStringIn( new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO } )
					+ " AND d.id_curriculo =  " + curriculo.getId()
					+ " ) ";
			
			update(sql);
	}
	
	/**
	 * Retorna a coleção de matrículas com os ids informados na string passada como parâmetro.
	 * Essa string já deve estar em um formato reconhecido pelo operador in do HQL.
	 * 
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByIds(String ids) throws DAOException {

		String projecao = " id, ano, periodo, mediaFinal,  situacaoMatricula.descricao, situacaoMatricula.id, componente.id, "
			+ " componente.codigo, componente.detalhes.id, componente.detalhes.nome, componente.detalhes.chTotal, "
			+ " detalhesComponente.id, detalhesComponente.codigo, detalhesComponente.nome ";

		String hql = "SELECT "
				+ projecao
				+ " FROM MatriculaComponente mc "
				+ " WHERE mc.id in "
				+ ids;

		Query q = getSession().createQuery(hql);
		Collection<MatriculaComponente> retorno = null;

		try {
			@SuppressWarnings("unchecked")
			List <Object []> rs = q.list();
			retorno = HibernateUtils.parseTo(rs, projecao, MatriculaComponente.class);
		}catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		return retorno;
	}

	/**
	 * Busca matrículas em uma turma de forma aleatória e com a quantidade especificada como parâmetro.
	 * As matrículas devem estar com situação MATRICULADO ou EM ESPERA.
	 * 
	 * @param turma
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculadosAleatoriosByTurma(Turma turma, Integer quantidade) throws DAOException {
		Query q = getSession().createQuery("from MatriculaComponente mc " +
				" where mc.turma.id = ? " +
				" and mc.situacaoMatricula.id in " + gerarStringIn(SituacaoMatricula.getSituacoesMatriculadas()) +
				" order by random()");
		q.setInteger(0, turma.getId());
		if (quantidade != null) {
			q.setMaxResults(quantidade);
		}
		
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = q.list();
		return rs;
	}


	/**
	 * Retorna todas as matrículas da turma informada.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByTurma(int idTurma) throws DAOException  {
		try {
            Criteria c = getSession().createCriteria(MatriculaComponente.class);
            c.add( Restrictions.eq("turma.id", idTurma) );
            
            @SuppressWarnings("unchecked")
            List <MatriculaComponente> rs = c.list();
        	return rs;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	/**
	 * Retorna todas as matrículas da turma informada ou de suas subturmas.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByTurma(Turma t) throws DAOException  {
		try {
			
			List <Integer> idsTurmas = new ArrayList <Integer> ();
			idsTurmas.add(t.getId());
			if (t.isAgrupadora()){
				@SuppressWarnings("unchecked")
				List <Integer> auxIdsTurmas = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora = " + t.getId()).list();
				idsTurmas = auxIdsTurmas;
			}
			
            Criteria c = getSession().createCriteria(MatriculaComponente.class);
            c.add( Restrictions.in("turma.id", idsTurmas) );
            
            @SuppressWarnings("unchecked")
            List <MatriculaComponente> rs = c.list();
        	return rs;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}


	/**
	 * Busca as matrículas que um discente possui em sub-unidades de um bloco em um determinado ano-período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param bloco
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findMatriculasSubUnidadesByBloco(DiscenteAdapter discente, int ano, int periodo, ComponenteCurricular bloco) throws DAOException {		
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = getSession().createQuery(				
				" select mc " +
				" from MatriculaComponente mc " +
				" inner join mc.componente cc " +				
				" where mc.discente.id = ? and mc.ano = ? and mc.periodo = ? and " +
				" mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido()) + 
				" and cc.id in " + UFRNUtils.gerarStringIn(bloco.getSubUnidades()))
					.setInteger(0, discente.getId())
					.setInteger(1, ano)
					.setInteger(2, periodo)					
					.list();		
		return rs;
	}
	
	/**
	 * Busca as matrículas que um discente possui em sub-unidades de um bloco que estão com o status MATRICULADO ou EM ESPERA.
	 * 
	 * @param discente
	 * @param bloco
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findMatriculadasSubUnidadesByBloco(DiscenteAdapter discente, ComponenteCurricular bloco) throws DAOException {
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = getSession().createQuery("select mc from MatriculaComponente mc left join mc.turma.disciplina d left join d.blocoSubUnidade b "
				+ "where mc.discente.id = ? and b.id = ? and mc.situacaoMatricula.id in " + gerarStringIn(SituacaoMatricula.getSituacoesMatriculadas()))
				.setInteger(0, discente.getId())
				.setInteger(1, bloco.getId())
				.list();
		
		return rs;
	}


	/**
	 * Busca matrículas que foram realizadas sem terem uma solicitação de matrícula.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasSemSolicitacao(Discente discente, int ano, int periodo) throws DAOException {
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = getSession().createQuery("select m from MatriculaComponente m" +
				" where m.discente.id = :discente " +
				" and m.ano = :ano and m.periodo = :periodo" +
				" and m.situacaoMatricula.id in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) +
				" and not exists (select id from SolicitacaoMatricula sm where m.id = sm.matriculaGerada.id)" +
				" order by componente.detalhes.nome")
				.setInteger("discente", discente.getId())
				.setInteger("ano", ano)
				.setInteger("periodo", periodo)
				.list();
		
		return rs;
	}
	
	/**
	 * Retorna as matrículas de um aluno em um determinado componente curricular
	 * e que estão em um ano-período informados.
	 * 
	 * @param cc
	 * @param d
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findMatriculaByComponenteDiscenteAnoPeriodo(ComponenteCurricular cc, Discente d, int ano, int periodo) throws DAOException {
		return (MatriculaComponente) getSession().createCriteria(MatriculaComponente.class)
			.add(eq("componente.id", cc.getId()))
			.add(eq("discente.id", d.getId()))
			.add(eq("ano", (short) ano))
			.add(eq("periodo", (byte) periodo)).uniqueResult();
	}

	/**
	 * Busca todas as matrículas de uma turma que estão com as situações aprovado, 
	 * reprovado ou reprovado por falta.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findMatriculasConsolidadas(Turma turma) throws DAOException {
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.add(eq("turma", turma)).add(in("situacaoMatricula", SituacaoMatricula.getSituacoesConcluidas()));
		
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = c.list();
		return rs;
	}
	
	/**
	 * Busca matrículas em uma disciplina e em um ano-período informados. A matrícula 
	 * deverá estar entre as situações APROVADO, REPROVADO, REPROVADO POR FALTA, EM ESPERA,
	 * MATRICULADO ou TRANCADO.
	 * 
	 * @param idDisciplina
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<MatriculaComponente> findByMatriculadoEmDisciplina(Integer idDisciplina, Integer ano, Integer periodo) {
		
		String sql = "select p.nome, d.matricula, cc.codigo, t.codigo " +
				"from ensino.matricula_componente mc, ensino.turma t, ensino.componente_curricular cc, discente d, comum.pessoa p " +
				"where mc.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " and " +
				"mc.id_discente = d.id_discente and " +
				"d.id_pessoa = p.id_pessoa and " +
				"mc.id_turma = t.id_turma and " +
				"t.id_situacao_turma not in " + gerarStringIn(SituacaoTurma.getSituacoesInvalidas()) + " and " +
				"t.id_disciplina = cc.id_disciplina " +
				"and t.ano = ? and t.periodo = ? " +
				"and cc.id_disciplina = ? " +
				"order by t.id_turma, p.nome";
		
		return getSimpleJdbcTemplate().query(sql, new ParameterizedRowMapper<MatriculaComponente>() {

			public MatriculaComponente mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				int col = 1;
				
				MatriculaComponente mc = new MatriculaComponente();
				
				Discente discente = new Discente();
				discente.setPessoa(new Pessoa());
				discente.getPessoa().setNome(rs.getString(col++));
				discente.setMatricula(rs.getLong(col++));
				
				ComponenteCurricular disciplina = new ComponenteCurricular();
				disciplina.setCodigo(rs.getString(col++));
				
				Turma turma = new Turma();
				turma.setCodigo(rs.getString(col++));
				turma.setDisciplina(disciplina);
				
				mc.setDiscente(discente);
				mc.setComponente(disciplina);
				mc.setTurma(turma);
				
				return mc;
			}
			
		}, ano, periodo, idDisciplina);
	}
	
	/**
	 * Retora todas as matrículas ativas da turma passada, incluindo todas as subturmas.
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	public List <MatriculaComponente> findAtivasByTurma (Turma t) throws DAOException {
		
		List <Integer> idsTurma = new ArrayList <Integer> ();
		int idAgrupadora = 0;
		if (t.isAgrupadora() || t.getTurmaAgrupadora() != null)
			idAgrupadora = t.isAgrupadora() ? t.getId() : t.getTurmaAgrupadora().getId();
			
		if (idAgrupadora > 0){
			@SuppressWarnings("unchecked")
			List <Integer> ids = getSession().createSQLQuery ("select id_turma from ensino.turma where id_turma_agrupadora = " + idAgrupadora).list();
			idsTurma.addAll(ids);
		} else
			idsTurma.add(t.getId());
		
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.add(Restrictions.in("turma.id", idsTurma));
		
		List <Integer> idsSituacoes = new ArrayList <Integer>();
		for (SituacaoMatricula s : SituacaoMatricula.getSituacoesAtivas())
			idsSituacoes.add(s.getId());
		
		c.add(Restrictions.in("situacaoMatricula.id", idsSituacoes));
		
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = c.list();
		return rs;
	}


	/**
	 * Retorna um map contendo todas as matrículas dos alunos matriculados na turma
	 * 
	 * @param id
	 * @return um map contendo pares <idPessoa, matricula>
	 * @throws DAOException 
	 */
	public Map<Integer, Long> findMatriculasByTurma(int idTurma) throws DAOException {
		@SuppressWarnings("unchecked")
		List <Object []> ds = getSession().createSQLQuery (
											"select d.id_pessoa, d.matricula from discente d " +
											"join ensino.matricula_componente mc using (id_discente) " +
											"join ensino.turma using (id_turma) where id_turma = " + idTurma).list();
		
		Map <Integer, Long> rs = new HashMap <Integer, Long> ();
		
		for (Object [] d : ds)
			rs.put(((Number) d[0]).intValue(), d[1] == null ? null : ((Number) d[1]).longValue());
		
		return rs;
	}
	
	/**
	 * Verifica se existe alguma {@link MatriculaComponente} cadastrada na unidade passada.
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Boolean existemMatriculasNaUnidade(Integer idUnidade) throws DAOException {
		
		Query q = getSession().createQuery (
				
				" select distinct und.id " +
				" from MatriculaComponente as mc " +
				" inner join mc.componente as cc " +
				" inner join cc.unidade as und " +
				" where und.id = :idUnidade or und.unidadeResponsavel = :idUnidade "
				
		);
		
		q.setInteger("idUnidade", idUnidade);
		q.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<Integer> lista = q.list();
		
		if(lista!= null && !lista.isEmpty())
			return true;				
		
		return false;
	}
	
	/**
	 * Retorna uma listagem contendo todas informações para exportação 
	 * as matrículas dos componentes dos discentes de graduação juntamente com suas
	 * notas em um determinado ano e período
	 * @return
	 * @throws DAOException, SQLException 
	 * @throws  
	 */
	public Collection<Map<String, Object>> exportarNotasDiscente(int idCurso, int ano, Integer periodo) throws DAOException, SQLException{
		
		StringBuilder sql = new StringBuilder(" SELECT mc.periodo, d.matricula,  ");
		sql.append(" cc.codigo as codigo_componente, t.codigo as codigo_turma, ");
		sql.append(" mc.media_final, sm.descricao, t.descricao_horario, ccd.nome,  (s.siape||'-'||s.digito_siape) as siape, mc.id_matricula_componente ");
		sql.append(" FROM ensino.matricula_componente mc ");
		sql.append(" JOIN discente d ON mc.id_discente = d.id_discente " );
		sql.append(" JOIN ensino.componente_curricular cc ON cc.id_disciplina = mc.id_componente_curricular " );
		sql.append(" JOIN ensino.componente_curricular_detalhes ccd on ccd.id_componente_detalhes = cc.id_detalhe " );
		sql.append(" LEFT JOIN ensino.turma t ON t.id_turma = mc.id_turma " );
		sql.append(" LEFT JOIN ensino.docente_turma dt ON dt.id_turma = t.id_turma ");
		sql.append(" LEFT JOIN rh.servidor s ON s.id_servidor = dt.id_docente ");
		sql.append(" JOIN ensino.situacao_matricula sm ON sm.id_situacao_matricula = mc.id_situacao_matricula " );
		sql.append(" WHERE sm.id_situacao_matricula not in "+gerarStringIn(SituacaoMatricula.getSituacoesAproveitadas()));
		sql.append(" AND d.id_curso = :idCurso " );
		
		sql.append(" AND mc.ano = :ano " );
		if( !isEmpty(periodo) )
			sql.append(" AND mc.periodo = :periodo" );

		sql.append(" ORDER BY mc.id_matricula_componente ");

		Query q = getSession().createSQLQuery(sql.toString());

		q.setInteger("idCurso", idCurso);
		q.setInteger("ano", ano);
		if( !isEmpty(periodo) )
			q.setInteger("periodo", periodo);
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		int cont = 1;
		int total = resultado.size();
		Integer idMatriculaComponente = 0;
		StringBuilder siape = new StringBuilder();
		
		if (!resultado.isEmpty()) {
			Object[] primeiraLinha = resultado.iterator().next();
			int i = 0;
			mapa.put("Período", primeiraLinha[i++]);
			mapa.put("Matrícula", primeiraLinha[i++]);
			mapa.put("Código", primeiraLinha[i++]);
			mapa.put("Turma", (primeiraLinha[i++] != null? primeiraLinha[i-1]:'-'));
			mapa.put("Nota", (primeiraLinha[i++] != null? primeiraLinha[i-1]:'-'));
			mapa.put("Situação", primeiraLinha[i++]);
			mapa.put("Horário", (primeiraLinha[i++] != null? primeiraLinha[i-1]:'-'));
			mapa.put("Nome", primeiraLinha[i++]);
			
			idMatriculaComponente = (Integer) primeiraLinha[9];
			
		}
		
		for (Object[] linha : resultado) {
			
			if( idMatriculaComponente.equals(linha[9]) ){
				if( siape.length() > 0 )
					siape.append(",");
				siape.append((linha[8] != null?linha[8]:'-'));
			}			
			if( !idMatriculaComponente.equals(linha[9]) || (idMatriculaComponente.equals(linha[9]) && cont == total) ){
				
				mapa.put("Siape", siape.toString());
				lista.add(mapa);
				int i = 0;
				mapa = new HashMap<String, Object>();
				siape = new StringBuilder();
				
				mapa.put("Período", linha[i++]);
				mapa.put("Matrícula", linha[i++]);
				mapa.put("Código", linha[i++]);
				mapa.put("Turma", (linha[i++] != null? linha[i-1]:'-'));
				mapa.put("Nota", (linha[i++] != null? linha[i-1]:'-'));
				mapa.put("Situação", linha[i++]);
				mapa.put("Horário", (linha[i++] != null? linha[i-1]:'-'));
				mapa.put("Nome", linha[i++]);
				
				siape.append((linha[8] != null?linha[8]:'-'));	
			}
			idMatriculaComponente = (Integer) linha[9];
			cont++;
		}
		
		return lista;
		
	}
	
	/**
	 * 
	 * Retorna s situação anteior mais recente das matriculas.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public Map<Integer,Integer> getMapaSituacaoAnteriorMaisRecenteMacricula(Collection<MatriculaComponente> matriculas) throws DAOException {
		StringBuilder sql = new StringBuilder();
		sql.append(" select id_matricula_disciplina,id_situacao_antiga " +
				   " from ensino.alteracao_matricula "  +
				   " where id_matricula_disciplina in " + UFRNUtils.gerarStringIn(matriculas) +
				   " order by id_matricula_disciplina,data_alteracao desc ");		

		Query q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		HashMap<Integer,Integer> mapaMatriculaSituacaoAnteriorMaisRecente = new HashMap<Integer, Integer>();
		
		for (Object[] linha : resultado) {
			Integer idMatricula = (Integer) linha[0];
			if(!mapaMatriculaSituacaoAnteriorMaisRecente.containsKey(idMatricula)) {
				mapaMatriculaSituacaoAnteriorMaisRecente.put(idMatricula, (Integer) linha[1]);
			}
		}
		
		return mapaMatriculaSituacaoAnteriorMaisRecente;
	}

	/**
	 * Retorna o ano e período da última matrícula componente do discente
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findAnoPeriodoUltimaMatriculaComponente(int idDiscente) throws DAOException {
		String hql = "select ano as ano, periodo as periodo from MatriculaComponente where discente.id = :idDiscente and ano is not null and periodo is not null order by cast(ano ||''|| periodo as integer) desc";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idDiscente", idDiscente);
		query.setMaxResults(1);
		
		return (MatriculaComponente) query.setResultTransformer(new AliasToBeanResultTransformer(MatriculaComponente.class)).uniqueResult();
	}
	
	/**
	 * Busca as matrículas de um discente em um conjunto de componentes curriculares
	 * e que possuem situação dentre as situações passadas como parâmetro. 
	 * 
	 * @param discente
	 * @param componentes
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findByDiscenteEDisciplina(DiscenteAdapter discente, ComponenteCurricular componentes, Collection<SituacaoMatricula> situacoes) throws DAOException {
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.add(Restrictions.eq("discente.id", discente.getId()));
		c.add(Restrictions.in("situacaoMatricula", situacoes));
		c.add(Restrictions.eq("componente", componentes));		
		@SuppressWarnings("unchecked")
		List <MatriculaComponente> rs = c.list();
		return rs;
	}
	
}