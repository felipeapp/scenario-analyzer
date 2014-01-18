/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/11/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.AvaliacaoOrganizacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.CargoAdministrativo;
import br.ufrn.sigaa.prodocente.atividades.dominio.Chefia;
import br.ufrn.sigaa.prodocente.atividades.dominio.Coordenacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.DesignacaoCargo;
import br.ufrn.sigaa.prodocente.atividades.dominio.DisciplinaQualificacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoAcademica;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoTese;
import br.ufrn.sigaa.prodocente.atividades.dominio.MiniCurso;
import br.ufrn.sigaa.prodocente.atividades.dominio.Monografia;
import br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoICExterno;
import br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoProex;
import br.ufrn.sigaa.prodocente.atividades.dominio.QualificacaoDocente;
import br.ufrn.sigaa.prodocente.atividades.dominio.RelatorioPesquisa;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoAvaliacaoOrganizacaoEvento;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoChefia;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoParecer;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoQualificacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoRelatorio;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;
import br.ufrn.sigaa.prodocente.atividades.dominio.TutoriaPet;
import br.ufrn.sigaa.prodocente.negocio.RelatorioHelper;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.CategoriaFuncional;
import br.ufrn.sigaa.prodocente.producao.dominio.NaturezaExame;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoColegiadoComissao;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.Patente;
import br.ufrn.sigaa.prodocente.producao.dominio.PremioRecebido;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoComissaoColegiado;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacaoOrganizacaoEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.prodocente.producao.dominio.VisitaCientifica;
import br.ufrn.sigaa.prodocente.relatorios.dominio.CargaHorariaEnsino;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ComponentesEnsinoPosStricto;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ProducaoIntelectual;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * DAO com consultas para os relatórios de avaliação dos docentes
 * 
 * @author Eric Moura
 * @author Ricardo Wendell
 *
 */
//@SuppressWarnings("unchecked")
public class AvaliacaoDocenteDao extends GenericSigaaDAO {

	public static final String DATA_INICIO = "dataInicio";

	public static final String DATA_FIM = "dataFim";

	private int anoInicio;

    private Query createQuery(Class<? extends ViewAtividadeBuilder> atividade, String where) throws HibernateException, DAOException {
    	return createQuery(atividade,where,null,null,false);
    }

    private Query createQuery(Class<? extends ViewAtividadeBuilder> atividade, String where, Integer anoVigencia, Integer validade) throws HibernateException, DAOException {
    	return createQuery(atividade,where,anoVigencia,validade,true);
    }

    /**
     * Método para gerar a query a partir das condições desejadas (where) e da classe da atividade
     *
     * @param atividade
     * @param where
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    private Query createQuery(Class<? extends ViewAtividadeBuilder> atividade, String where, Integer anoVigencia, Integer validade, boolean todasDatas) throws HibernateException, DAOException {

    	// Buscar os itens a serem utilizados na projeção
    	HashMap<String, String> itens = new HashMap<String, String>() ;
		try {
			itens = atividade.newInstance().getItens();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// Preparar inicio da consulta
		StringBuilder hql = new StringBuilder();
		hql.append("select ");

		// Adicionar os campos da projeção e seus aliases
        int i = 0;
        Set<String> campos = itens.keySet();
        int totalCampos = campos.size();
        for ( String campo : campos ) {
        	String alias = ( itens.get(campo) == null ? campo : itens.get(campo) );
        	hql.append( "o." + campo + " as " + alias );
        	if (i++ < totalCampos - 1) {
        		hql.append(", ");
        	}
        }
        hql.append(" from " + atividade.getName() + " o" );

        // Adicionar restrições da consulta
        hql.append( where );

        // Criar a consulta e preparar o ResultTransformer
        Query q = getSession().createQuery(hql.toString());
        q.setResultTransformer( Transformers.aliasToBean(atividade) );

        if(anoVigencia != null){
        	HashMap<String, Date> datas = getDatas(anoVigencia, validade);
        	if ( where.contains(":dataInicio")) {
				q.setDate("dataInicio", datas.get(DATA_INICIO));
			}
	        if(todasDatas) {
				if ( where.contains(":dataFim")) {
					q.setDate("dataFim", datas.get(DATA_FIM));
				}
			}
        }

        return q;

    }

    /** Método para obter a partir de um ano vigência as data de inicio e fim para consulta de atividades
     * @param anoVigencia
     * @param validade, quantidade de anos a ser buscada para trás, a partir do anoVigencia
     * @return
     */
    public HashMap<String, Date> getDatas(int anoVigencia, Integer validade){

    	HashMap<String, Date> datas = new HashMap<String, Date>();
        //Se não for informada a validade, irá consultar o ano de vigência apenas
    	if (validade == null)
        {
	    	Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.YEAR, anoVigencia);
	        cal.set(Calendar.MONTH, Calendar.JANUARY);
	        cal.set(Calendar.DAY_OF_MONTH, 1);

	        Date dataInicio = cal.getTime();
	        cal.set(Calendar.YEAR, anoVigencia + 1);

	        Date dataFim = cal.getTime();

	        datas.put("dataInicio", dataInicio);
	        datas.put("dataFim", dataFim);

        }
        else //Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        {
        	validade--; // o correto se o ano for 2007, por exemplo, é contar 2007 e 2006 por isso o decremento
        	Calendar cal = Calendar.getInstance();
        	cal.set(Calendar.YEAR, anoVigencia + 1);
        	cal.set(Calendar.MONTH, Calendar.JANUARY);
        	cal.set(Calendar.DAY_OF_MONTH, 1);
        	Date dataFim = cal.getTime();

        	cal.set(Calendar.YEAR, anoVigencia - validade);
        	cal.set(Calendar.MONTH, Calendar.JANUARY);
        	cal.set(Calendar.DAY_OF_MONTH, 1);
        	Date dataInicio = cal.getTime();

	        datas.put("dataFim", dataFim);
        	datas.put("dataInicio", dataInicio);
        }
        return datas;
    }
    //------//


    // Item 1
    /**
     * O parâmetro Validade indica quantos anos para traz, a partir de anoVigencia, a busca deve englobar.
     *  
     */
    @SuppressWarnings("unchecked")
	public Collection<Designacao> findCargosDirecaoFuncaoGratificada(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
      
    	try {
        	StringBuilder where = new StringBuilder();
//        	where.append(" where getidpessoa("+docente.getId()+") = o.servidorSigaa.pessoa.id ");
        	where.append(" where o.servidorSigaa.id = " + docente.getId() );
            // where.append(" and upper(o.remuneração) = 'T' ");
            where.append(" order by o.fim desc");

            // comentário gleydson: retirei a restrição and upper(o.gerencia) = 'T'" em conversa com Prof. Emanuel e seixas.
            // Assim os vices também contarão da mesma maneira.

            //where.append(" (o.inicio <= :dataInicio and o.fim >= dataInicio) or ( o.inicio >= dataInicio and o.fim <= dataFim ")

            Query q = createQuery(Designacao.class, where.toString(), anoVigencia, validade);
            Collection<Designacao> result = q.list();
            ArrayList<Designacao> resultProcessado = new ArrayList<Designacao>();
            for ( Designacao d : result ) {
            	Date inicio = d.getInicio();
            	Date fim = d.getFim();

            	Calendar c = Calendar.getInstance();
            	c.setTime(inicio);
            	int anoInicio = c.get(Calendar.YEAR);
            	int anoInicioQuery = (anoVigencia-validade) + 1;

            	int anoFim = 9999;
            	if ( fim != null ) {
            		c.setTime(fim);
            		anoFim = c.get(Calendar.YEAR);

            		if ( anoInicio < anoInicioQuery ) {
            			d.setInicio(Formatador.getInstance().parseDate("01/01/" + anoInicioQuery) );
            		}

            		if ( anoFim > anoVigencia){
            			d.setFim(Formatador.getInstance().parseDate("31/12/" + anoVigencia) );
            		}
            	}
            	if ( (anoInicioQuery <= anoInicio &&  anoVigencia >= anoInicio) 
            			|| (anoInicioQuery <= anoFim &&  anoVigencia >= anoFim) 
            			|| (anoInicio < anoInicioQuery && anoFim >= anoVigencia)) {
            		resultProcessado.add(d);
            	}
            }
            return resultProcessado;

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // ITEM 2

    // Item 2.1 - Ensino de graduação do docente
    @SuppressWarnings("unchecked")
	public Collection<DocenteTurma> findAtividadeEnsinoGraduacaoDocente(
            Servidor docente, Integer anoVigencia, Integer validade) throws DAOException {

        try {

        	StringBuilder where = new StringBuilder();
//        	where.append(" where getidpessoa("+docente.getId()+") = o.docente.pessoa.id");
        	where.append(" where o.docente.id = " + docente.getId() );
    		where.append(" and o.turma.disciplina.nivel = '"+NivelEnsino.GRADUACAO+"'");
    		where.append(" and o.turma.situacaoTurma.id in " + 
    		UFRNUtils.gerarStringIn(new int[]{SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA}));

    		//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0) {
        		int anoInicio = anoVigencia - validade;
        		int anoFim = anoVigencia;
        		where.append(" and  (o.turma.ano >= "+anoInicio+" and o.turma.ano <= "+anoFim+") ");
        	} else {
				where.append(" and  o.turma.ano = "+anoVigencia);
			}
        	where.append(" order by o.turma.ano, o.turma.periodo");
    		Query q = createQuery(DocenteTurma.class, where.toString());
            return q.list();
            
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // Item 2.2 - Ensino de Pós-Graduação do docente
    @SuppressWarnings("unchecked")
	public Collection<DocenteTurma> findAtividadeEnsinoPosDocente(
            Servidor docente, Integer anoVigencia, Integer validade) throws DAOException {

    	Collection<Character> niveisEnsinoPos = Arrays.asList( new Character[] {NivelEnsino.DOUTORADO, NivelEnsino.LATO, NivelEnsino.MESTRADO, NivelEnsino.STRICTO} );

        try {
        	StringBuilder where = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.docente.pessoa.id ");
//    		where.append(" where o.docente.id = " + docente.getId() );

    		//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade. IprojectLogTarefa: 5321 Edson Anibal (ambar@info.ufrn.br)
        	if (validade != null && validade > 1)
        	{
        		int anoInicio = anoVigencia - validade + 1;
        		int anoFim = anoVigencia;
        		where.append(" and  (o.turma.ano >= "+anoInicio+" and o.turma.ano <= "+anoFim+") ");
        	} else {
				where.append(" and o.turma.ano = "+anoVigencia);
			}
        	where.append(" and o.turma.situacaoTurma.id in "+ UFRNUtils.gerarStringIn(SituacaoTurma.getSituacoesValidas()));
    		where.append(" and o.turma.disciplina.nivel in "+ UFRNUtils.gerarStringIn(niveisEnsinoPos));
            Query q = createQuery(DocenteTurma.class, where.toString());

            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }

    }

    // Item 2.3 - Orientação - Estagio Supervisionado e correlatos
	@SuppressWarnings("unchecked")
	public Collection<Estagio> findOrientacoesGraduacaoEstagioDocente(
            Servidor docente, Integer anoVigencia, Integer validade) throws DAOException {
    	try {
    		StringBuilder where = new StringBuilder();
    		where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//    		where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
            where.append(" and (ativo is null or ativo = trueValue())");

            Query q = createQuery(Estagio.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // Item 2.4 - Trabalho ou Projeto Final de Curso concluído
    // 2.4.1
    @SuppressWarnings("unchecked")
	public Collection<TrabalhoFimCurso> findOrientacoesGraduacaoTrabalhoFimCursoConcluidas(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {

        	StringBuilder where = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//    		where.append(" where o.servidor.id = " + docente.getId() );
    		where.append(" and o.dataDefesa >= :dataInicio");
            where.append(" and o.dataDefesa < :dataFim");
            where.append(" and (ativo is null or ativo = trueValue())");
            where.append(" and o.orientando.nivel = '" + NivelEnsino.GRADUACAO + "'");

            Query q = createQuery(TrabalhoFimCurso.class, where.toString(), anoVigencia, validade);
            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
	public Collection<TrabalhoFimCurso> findOrientacoesEspecializacaoTrabalhoFimCursoConcluidas(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {

        	StringBuilder where = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//    		where.append(" where o.servidor.id = " + docente.getId() );
    		where.append(" and o.dataDefesa >= :dataInicio");
            where.append(" and o.dataDefesa < :dataFim");
            where.append(" and (ativo is null or ativo = trueValue())");
            where.append(" and o.orientando.nivel = '" + NivelEnsino.LATO + "'");

            Query q = createQuery(TrabalhoFimCurso.class, where.toString(), anoVigencia, validade);
            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
    
    // 2.4.2
    @SuppressWarnings("unchecked")
	public Collection<TrabalhoFimCurso> findOrientacoesGraduacaoTrabalhoFimCursoDocente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {

        	StringBuffer hql = new StringBuffer();
        	hql.append(" from TrabalhoFimCurso o where o.servidor.id = " + docente.getId() );

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade. IprojectLogTarefa: 5321 Edson Anibal (ambar@info.ufrn.br)
        	if (validade != null && validade >0)
        	{
        		int anoInicio = anoVigencia - validade;
        		int anoFim = anoVigencia;
        		hql.append(" and  ( (extract(year from data_inicio) >= "+anoInicio+" and extract(year from data_inicio) <="+anoFim+") " );
        	    hql.append(" or (extract(year from data_defesa) >= " + anoVigencia + " and extract(year from data_defesa) <="+anoFim+") ) ");
        	}
        	else
        	{
        		hql.append(" and  ( extract(year from data_inicio) = " + anoVigencia );
        	    hql.append(" or extract(year from data_defesa) = " + anoVigencia + ") ");
        	}
        	hql.append(" and (ativo = trueValue() or ativo is null) ");

            Query q = getSession().createQuery(hql.toString());

            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // Item 2.4 - Trabalho ou Projeto Final de Curso concluído
    // 2.4.3
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<Monografia> findOrientacoesMonografiaGraduacaoConcluidas(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {

        	StringBuilder where = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//    		where.append(" where o.servidor.id = " + docente.getId() );
    		where.append(" and o.tipoOrientacao.id = " + TipoOrientacao.GRADUACAO);
    		where.append(" and o.periodoFim >= :dataInicio");
            where.append(" and o.periodoFim < :dataFim");
            where.append(" and (o.ativo is null or o.ativo = trueValue())");

            Query q = createQuery(Monografia.class, where.toString(), anoVigencia, validade);
            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // Item 2.5 - Orientação - Especialização
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosEspecializacaoDocente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {

    		String sqlQuery = "select tese.periodo_inicio, tese.data_publicacao, p.nome, tese.orientando, " +
    				" ( select count(*) from prodocente.tese_orientada join discente using ( id_discente )" +
    				" where id_discente is not null and id_discente = d.id_discente and id_curso = d.id_curso" +
    				" and (tese.ativo is null or tese.ativo=trueValue())) as total" +
    				" from prodocente.tese_orientada tese" +
    				" left join discente d on ( tese.id_discente = d.id_discente )" +
    				" left join rh.servidor s on ( s.id_servidor = tese.id_servidor )" +
    				" left join comum.pessoa p on ( d.id_pessoa = p.id_pessoa )" +
    				" where tese.id_servidor = s.id_servidor" +
    				" and getidpessoa(:idDocente)=s.id_pessoa" +
    				" and tese.data_publicacao>= :dataInicio" +
    				" and tese.data_publicacao< :dataFim" +
    				" and tese.id_tipo_orientacao = :tipoOrientacao" +
    				" and (tese.ativo is null or tese.ativo=trueValue())";

    		Query query = getSession().createSQLQuery(sqlQuery.toString());
	      	query.setInteger("idDocente", docente.getId());
	      	query.setInteger("tipoOrientacao", TipoOrientacao.ESPECIALIZACAO);
	      	HashMap<String, Date> datas = getDatas(anoVigencia, validade);
	      	query.setDate("dataInicio", datas.get(DATA_INICIO));
	      	query.setDate("dataFim", datas.get(DATA_FIM));
	
	      	List <Object []> ds = query.list();
	      	Collection<TeseOrientada> result = new ArrayList<TeseOrientada>();
	      	if ( !isEmpty(ds) ) {
				for (Object [] d : ds){
					int col = 0;
					TeseOrientada linha = new TeseOrientada();
					
					linha.setPeriodoInicio((Date) d[col++]);
					linha.setDataPublicacao((Date) d[col++]);
					
					if ( !isEmpty( d[col] ) ) {
						linha.setOrientandoDiscente(new Discente());
						linha.getOrientandoDiscente().setPessoa(new Pessoa());
						linha.getOrientandoDiscente().getPessoa().setNome((String) d[col++]);
					} else {
						linha.setOrientando((String) d[++col]);
						linha.setOrientandoDiscente(null);
					}
	
					linha.setTotalOrientadores(((BigInteger) d[++col]).intValue());
	
					result.add(linha);
				}
	      	}
	      	
    		return result;
    		
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     *  ATENÇÃO: Ricardo vai analisar esse SQL pois não está claro como será essa mudança.
     */
    // item 2.6 - Orientação Mestrado na UFRN e outra IFES
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosMestradoDocente(Servidor docente,
            int anoVigencia) throws DAOException {
        try {
		      	String sqlQuery = "select tese.periodo_inicio, tese.data_publicacao, p.nome, tese.orientando," +
	  			" ( select count(*) from prodocente.tese_orientada join discente using ( id_discente )" +
	  			" where id_discente is not null and id_discente = d.id_discente and id_curso = d.id_curso " +
	  			" and (tese.ativo is null or tese.ativo=trueValue())) as total " +
	  			" from prodocente.tese_orientada tese" +
	  			" left outer join discente d on tese.id_discente=d.id_discente" +
	  			" left join rh.servidor s on ( tese.id_servidor = s.id_servidor )" +
	  			" left join comum.pessoa p on ( d.id_pessoa  = p.id_pessoa )" +
	  			" where tese.id_servidor=s.id_servidor and getidpessoa(:idDocente)=s.id_pessoa" +
	  			" and extract(year from tese.periodo_inicio)<=:ano" +
	  			" and (tese.periodo_fim is null or extract(year from tese.periodo_fim)>=:ano)" +
	  			" and (tese.data_publicacao is null or extract(year from tese.data_publicacao)>:ano)" +
	  			" and tese.id_tipo_orientacao=:tipoOrientacao and (tese.ativo is null or tese.ativo=trueValue())" +
	  			" and (d.status not in " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.CANCELADO, 
	        			StatusDiscente.EXCLUIDO, StatusDiscente.AFASTADO, StatusDiscente.JUBILADO}) + " )";
	
	      	Query query = getSession().createSQLQuery(sqlQuery.toString());
	      	query.setInteger("idDocente", docente.getId());
	      	query.setInteger("ano", anoVigencia);
	      	query.setInteger("tipoOrientacao", TipoOrientacao.MESTRADO);
	
	      	List <Object []> ds = query.list();
	      	Collection<TeseOrientada> result = new ArrayList<TeseOrientada>();
	      	if ( !isEmpty(ds) ) {
				for (Object [] d : ds){
					int col = 0;
					TeseOrientada linha = new TeseOrientada();
					
					linha.setPeriodoInicio((Date) d[col++]);
					linha.setDataPublicacao((Date) d[col++]);
					
					if ( !isEmpty( d[col] ) ) {
						linha.setOrientandoDiscente(new Discente());
						linha.getOrientandoDiscente().setPessoa(new Pessoa());
						linha.getOrientandoDiscente().getPessoa().setNome((String) d[col++]);
					} else {
						linha.setOrientando((String) d[++col]);
						linha.setOrientandoDiscente(null);
					}
	
					linha.setTotalOrientadores(((BigInteger) d[++col]).intValue());
	
					result.add(linha);
				}		      	
	      	}
	      	
	      	return result;

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     *  ATENÇÃO: Ricardo vai analisar esse SQL pois não está claro como será essa mudança.
     */
    // Item 2.7 - Orientação Doutorado na UFRN e outras IFES
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosDoutoradoDocente(
            Servidor docente, int anoVigencia) throws DAOException {

    	 try {
		      	String sqlQuery = "select tese.periodo_inicio, tese.data_publicacao, p.nome, tese.orientando," +
				   		" ( select count(*) from prodocente.tese_orientada join discente using ( id_discente )" +
				   		" where id_discente is not null and id_discente = d.id_discente and id_curso = d.id_curso " +
				   		" and (tese.ativo is null or tese.ativo=trueValue())) as total" +
		      			" from prodocente.tese_orientada tese" +
		      			" left join discente d on ( tese.id_discente=d.id_discente )" +
		      			" left join rh.servidor s on ( tese.id_servidor = tese.id_servidor )" +
		      			" left join comum.pessoa p on ( p.id_pessoa = d.id_pessoa )" +
		      			" where tese.id_servidor=s.id_servidor" +
		      			" and getidpessoa(:idDocente)=s.id_pessoa" +
		      			" and extract(year from tese.periodo_inicio)<= :ano" +
		      			" and (tese.periodo_fim is null or extract(year from tese.periodo_fim)>= :ano)" +
		      			" and (tese.data_publicacao is null or extract(year from tese.data_publicacao)> :ano)" +
		      			" and tese.id_tipo_orientacao=:tipoOrientacao" +
		      			" and ( tese.ativo is null or tese.ativo=trueValue() )";
	
	      	Query query = getSession().createSQLQuery(sqlQuery.toString());
	      	query.setInteger("idDocente", docente.getId());
	      	query.setInteger("ano", anoVigencia);
	      	query.setInteger("tipoOrientacao", TipoOrientacao.DOUTORADO);
	
	      	List <Object []> ds = query.list();
	      	Collection<TeseOrientada> result = new ArrayList<TeseOrientada>();
	      	if ( !isEmpty(ds) ) {
				for (Object [] d : ds){
					int col = 0;
					TeseOrientada linha = new TeseOrientada();
					
					linha.setPeriodoInicio((Date) d[col++]);
					linha.setDataPublicacao((Date) d[col++]);
					
					if ( !isEmpty( d[col] ) ) {
						linha.setOrientandoDiscente(new Discente());
						linha.getOrientandoDiscente().setPessoa(new Pessoa());
						linha.getOrientandoDiscente().getPessoa().setNome((String) d[col++]);
					} else {
						linha.setOrientando((String) d[++col]);
						linha.setOrientandoDiscente(null);
					}
	
					linha.setTotalOrientadores(((BigInteger) d[++col]).intValue());
	
					result.add(linha);
				}
	      	}

	      	return result;
	      	
         } catch (Exception e) {
             throw new DAOException(e.getMessage(), e);
         }
    }
    
    // Item 2.10 - Orientação de Pós-Doutorado na UFRN e outras IFES
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosDocDocente(
    		Servidor docente, int anoVigencia) throws DAOException {
    	
    	try {
    		StringBuilder where = new StringBuilder();
    		where.append(" select o.orientando as orientando," +
    				" o.periodoInicio as periodoInicio, o.periodoFim as periodoFim," +
    		" o.dataPublicacao as dataPublicacao, o.pago as pago, d as orientandoDiscente" );
    		where.append(" from TeseOrientada o left join o.orientandoDiscente as d ");
    		where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//    		where.append(" where o.servidor.id = " + docente.getId());
    		
    		where.append(" and year(o.periodoInicio) <= " + anoVigencia + "");
    		where.append(" and (o.periodoFim is null or year(o.periodoFim) >= " + anoVigencia + ")");
    		where.append(" and ( o.dataPublicacao is null or year(o.dataPublicacao) > " + anoVigencia + " )");
    		
    		where.append(" and o.tipoOrientacao.id = " + TipoOrientacao.POS_DOUTORADO);
    		where.append(" and (o.ativo is null or o.ativo = trueValue())");
    		
    		Query q = getSession().createQuery( where.toString());
    		return q.setResultTransformer( Transformers.aliasToBean(TeseOrientada.class) ).list();
    		
    	} catch (Exception e) {
    		throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 2.8
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosMestradoConcluidoDocente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	   try {
    		   String sqlQuery = "select tese.periodo_inicio, tese.data_publicacao, coalesce(p.nome, tese.orientando) as nome," +
    		   		" ( select count(*) from prodocente.tese_orientada join discente using ( id_discente )" +
    		   		" where id_discente is not null and id_discente = d.id_discente and id_curso = d.id_curso " +
    		   		" and (ativo is null or ativo=trueValue())) as total" +
    		   		" from prodocente.tese_orientada tese" +
    		   		" left join discente d on ( tese.id_discente = d.id_discente )" +
    		   		" left join comum.pessoa p on ( p.id_pessoa = d.id_pessoa )" +
    		   		" left join rh.servidor s on ( tese.id_servidor = s.id_servidor )" +
    		   		" where getidpessoa(:idDocente) = s.id_pessoa";
    		   
    		   		if ( validade != null && validade > 0 )
    	        		sqlQuery += " and extract(year from tese.data_publicacao) >= :anoInicial and extract(year from tese.data_publicacao) <= :anoFinal";
					else
						sqlQuery += " and extract(year from tese.data_publicacao) >= :anoInicial and extract(year from tese.data_publicacao) <= :anoFinal";
    		   
    		   		sqlQuery += " and tese.id_tipo_orientacao = :tipoOrientacao and ( tese.ativo is null or tese.ativo=trueValue() )";
    		   
   	      	Query query = getSession().createSQLQuery(sqlQuery.toString());
	      	query.setInteger("idDocente", docente.getId());
	      	if ( validade != null && validade > 0 ) {
	      		query.setInteger("anoInicial", anoVigencia - validade);
	      		query.setInteger("anoFinal", anoVigencia);
			} else {
				query.setInteger("anoInicial", anoVigencia);
				query.setInteger("anoFinal", anoVigencia - 1);
			}
	      	query.setInteger("tipoOrientacao", TipoOrientacao.MESTRADO);

	      	List <Object []> ds = query.list();
	      	Collection<TeseOrientada> result = new ArrayList<TeseOrientada>();
	      	if ( !isEmpty(ds) ) {
				for (Object [] d : ds){
					int col = 0;
					TeseOrientada linha = new TeseOrientada();
					linha.setPeriodoInicio((Date) d[col++]);
					linha.setDataPublicacao((Date) d[col++]);
					linha.setOrientandoDiscente(new Discente());
					linha.getOrientandoDiscente().setPessoa(new Pessoa());
					linha.getOrientandoDiscente().getPessoa().setNome((String) d[col++]);
					linha.setTotalOrientadores(((BigInteger) d[col++]).intValue());
					result.add(linha);
				}
	      	}
	      	
            return result;
            
           } catch (Exception e) {
               throw new DAOException(e.getMessage(), e);
           }
    }

    // Item 2.9
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosDoutoradoConcluidoDocente(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
   	 try {
   		 
      	String sqlQuery = "select tese.periodo_inicio, tese.data_publicacao, p.nome, tese.orientando," +
		   		" ( select count(*) from prodocente.tese_orientada join discente using ( id_discente )" +
		   		" where id_discente is not null and id_discente = d.id_discente and id_curso = d.id_curso " +
		   		" and (ativo is null or ativo=trueValue())) as total" +
		      	" from prodocente.tese_orientada tese" +
      			" left join discente d on ( tese.id_discente=d.id_discente)" +
      			" left join rh.servidor s on ( tese.id_servidor = s.id_servidor )" +
      			" left join comum.pessoa p on ( p.id_pessoa = d.id_pessoa )" +
      			" where tese.id_servidor=s.id_servidor" +
      			" and getidpessoa(:idDocente)=s.id_pessoa";
      	
      	if ( validade != null && validade > 0 )
      		sqlQuery += " and extract(year from tese.data_publicacao) >= :anoInicial and extract(year from tese.data_publicacao) <= :anoFinal";
      	else
      		sqlQuery += " and extract(year from tese.data_publicacao) = :anoInicial and extract(year from tese.data_publicacao) = :anoFinal";
      	
		 sqlQuery += " and tese.id_tipo_orientacao=:tipoOrientacao" +
      				 " and (tese.ativo is null or tese.ativo=trueValue())";
      	
  		Query query = getSession().createSQLQuery(sqlQuery.toString());
		query.setInteger("idDocente", docente.getId());
      	if ( validade != null && validade > 0 ) {
      		query.setInteger("anoInicial", anoVigencia - validade);
      		query.setInteger("anoFinal", anoVigencia);
		} else {
			query.setInteger("anoInicial", anoVigencia);
			query.setInteger("anoFinal", anoVigencia - 1);
		}
		query.setInteger("tipoOrientacao", TipoOrientacao.DOUTORADO);

		List <Object []> ds = query.list();
		Collection<TeseOrientada> result = new ArrayList<TeseOrientada>();
		if ( !isEmpty(ds) ) {
			for (Object [] d : ds){
				int col = 0;
				
				TeseOrientada linha = new TeseOrientada();
		
				linha.setPeriodoInicio((Date) d[col++]);
				linha.setDataPublicacao((Date) d[col++]);
		
				if ( !isEmpty( d[col] ) ) {
					linha.setOrientandoDiscente(new Discente());
					linha.getOrientandoDiscente().setPessoa(new Pessoa());
					linha.getOrientandoDiscente().getPessoa().setNome((String) d[col++]);
				} else {
					linha.setOrientando((String) d[++col]);
					linha.setOrientandoDiscente(null);
				}

				linha.setTotalOrientadores(((BigInteger) d[++col]).intValue());

				
			result.add(linha);
		   }
	}

	return result;


      } catch (Exception e) {
          throw new DAOException(e.getMessage(), e);
      }
    }


    // Item 3.1 - Tese de Doutorado defendida pelo docente com aprovação
    //Deprecated devido ao Refactoring em Formação Acadêmica, que será consultado utilizando o serviço diponível no SIGRH
    @SuppressWarnings("unchecked")
    @Deprecated
	public Collection<FormacaoAcademica> findTeseDoutoradoDefendida(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    		" or (o.dataInicio < :dataFim  and o.dataFim > :dataFim)  )");
	        where.append(" and o.formacao.id = "+FormacaoTese.DOUTORADO);
	        where.append(" and (o.ativo is null or o.ativo = trueValue())");

	        Query q = createQuery(FormacaoAcademica.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 3.2 - Tese de Mestrado defendida pelo docente com aprovação
  //Deprecated devido ao Refactoring em Formação Acadêmica, que será consultado utilizando o serviço diponível no SIGRH
    @SuppressWarnings("unchecked")
    @Deprecated
	public Collection<FormacaoAcademica> findTeseMestradoDefendida(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    		" or (o.dataInicio < :dataFim  and o.dataFim > :dataFim )  )");
	        where.append(" and o.formacao.id = "+FormacaoTese.MESTRADO);
	        where.append(" and (o.ativo is null or o.ativo = trueValue())");

	        Query q = createQuery(FormacaoAcademica.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

//  Item 3.3 - Monografia de Especialização defendida pelo docente com aprovação
  //Deprecated devido ao Refactoring em Formação Acadêmica, que será consultado utilizando o serviço diponível no SIGRH
    @SuppressWarnings("unchecked")
    @Deprecated
	public Collection<FormacaoAcademica> findMonografiaEspecializacaoDefendida(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    		" or (o.dataInicio < :dataFim  and o.dataFim > :dataFim )  )");
	        where.append(" and o.formacao.id = "+FormacaoTese.ESPECIALIZACAO);
	        where.append(" and (o.ativo is null or o.ativo = trueValue())");

	        Query q = createQuery(FormacaoAcademica.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    

    /**
     * Buscar produções intelectuais
     *
     * @param docente
     * @param ano
     * @param itensAvaliacao
     * @param validades
     * @return
     * @throws ClassNotFoundException 
     * @throws ArqException 
     */
    @SuppressWarnings("unchecked")
    @Deprecated
	public Hashtable<Integer, Collection<ProducaoIntelectual>> findProducaoIntelectual(
            Servidor docente, int ano, List<Integer> itensAvaliacao, Hashtable<Integer, Integer> validades)
            throws ArqException {

    	// Buscar as produções de acordo com os critérios especificados
        Criteria cProducao = getSession().createCriteria(Producao.class);
        cProducao.createCriteria("servidor").add(Restrictions.eq("pessoa", docente.getPessoa()));

    	// Caso sejam informadas a validades, verificar qual a maior validade especificada
        int anoInicioMinimo = 0;// = calcularAnoInicioMinimo(ano, validades);
    	cProducao.add(Expression.and(Expression.le("anoReferencia", ano), Expression.ge("anoReferencia", anoInicioMinimo)));

    	cProducao.add( Expression.or( Expression.isNull("ativo"), Expression.eq("ativo", true) ) );
    	cProducao.addOrder( Order.desc("anoReferencia") );
        cProducao.addOrder( Order.asc("titulo") );
        Collection<Producao> producoes = cProducao.list();

        RelatorioHelper helper = new RelatorioHelper();
        Hashtable<Integer, Collection<ProducaoIntelectual>> tabelaProducoes = null;//helper.processarProducoes(ano, itensAvaliacao, validades, producoes);
        return tabelaProducoes;
    }

	
    /**
     * 
     * @param docente
     * @param ano
     * @param anoInicioMinimo
     * @return
     * @throws DAOException
     */
    public Collection<Producao> findProducaoIntelectualByDocenteAnoAnoMinimo(Servidor docente, int ano, int anoInicioMinimo) throws DAOException{
    	
        String hql = "SELECT prod FROM Producao prod" +
        		" WHERE prod.servidor.pessoa.id = :idPessoa" +
        		" AND prod.anoReferencia >= :anoMinimo AND prod.anoReferencia <= :ano" +
        		" AND ( prod.ativo is null or prod.ativo is trueValue() )"+
        		" ORDER BY prod.anoReferencia desc, prod.titulo";
        Query q = getSession().createQuery(hql);
        q.setInteger("idPessoa", docente.getPessoa().getId());
        q.setInteger("anoMinimo", anoInicioMinimo);
        q.setInteger("ano", ano);
        return q.list();

    }

    //	 Item 3.20 (não há produção intelectual) - Relatório Final de pesquisa aprovado pela instância competente da UFRN
    //@SuppressWarnings("deprecation")
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Collection<RelatorioPesquisa> findRelatorioFinalPesquisa(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.projetoPesquisa.coordenador.pessoa.id ");
//	      	where.append(" where o.projetoPesquisa.coordenador.id = " + docente.getId());
	      	where.append(" and o.dataEnvio >= :dataInicio");
	      	where.append(" and o.dataEnvio < :dataFim");

	        Query q = createQuery(RelatorioProjeto.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // 4 - Atividade de Pesquisa e Extensão
    // Item 4.1 - Relatório parcial, ou final de atividades Internacionais de
    // extensão aprovados em instâncias
    // 4.1.1
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioAtividadeInternacional1(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id " );
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" and o.dataAprovacao >= :dataInicio");
	      	where.append(" and o.dataAprovacao < :dataFim");
	      	where.append(" and o.tipoRegiao.id = " + TipoRegiao.INTERNACIONAL);
	      	where.append(" and o.tipoRelatorio.id = " + TipoRelatorio.RELATORIO_EXTENSAO);

	        Query q = createQuery(RelatorioPesquisa.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // 4.1.2
    @SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findRelatorioAtividadeInternacional2(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
    		int[] status = new int[] {TipoSituacaoProjeto.EXTENSAO_CONCLUIDO};

	      	StringBuilder where = new StringBuilder();
	      	where.append(" , AtividadeExtensao at ");
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	      	where.append(" and " + HibernateUtils.generateDateIntersection("o.dataInicio", "o.dataFim", ":dataInicio",   ":dataFim"));
	      	where.append(" and o.projeto.id = at.projeto.id ");
	      	where.append(" and at.projeto.abrangencia.id = " + TipoRegiao.INTERNACIONAL);
	      	where.append(" and at.projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(status) );
	      	where.append(" order by at.projeto.titulo" );

	        Query q = createQuery(MembroProjeto.class, where.toString(), anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.2 - Relatório parcial, ou final de atividades Nacionais de
    // extensão aprovados em instâncias
    // 4.2.1
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioAtividadeNacional1(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	      	where.append(" and o.dataAprovacao >= :dataInicio");
	      	where.append(" and o.dataAprovacao < :dataFim");
	      	where.append(" and o.tipoRegiao.id = " + TipoRegiao.NACIONAL);
	      	where.append(" and o.tipoRelatorio.id = " + TipoRelatorio.RELATORIO_EXTENSAO);

	        Query q = createQuery(RelatorioPesquisa.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // 4.2.2
    @SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findRelatorioAtividadeNacional2(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	try {
    		int[] status = new int[] {TipoSituacaoProjeto.EXTENSAO_CONCLUIDO};

	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	      	where.append(" and " + HibernateUtils.generateDateIntersection("o.dataInicio", "o.dataFim", ":dataInicio",   ":dataFim"));
	      	where.append(" and o.projeto.abrangencia.id = " + TipoRegiao.NACIONAL);
	      	where.append(" and o.projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(status) );
	      	where.append(" order by o.projeto.titulo" );

	        Query q = createQuery(MembroProjeto.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.3 - Relatório parcial, ou final de atividades Regionais ou Locais
    // de extensão aprovados em instâncias
    // 4.3.1
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioAtividadeRegionalLocal1(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	int[] tiposRegiao ={TipoRegiao.REGIONAL, TipoRegiao.LOCAL};

    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	      	where.append(" and o.dataAprovacao >= :dataInicio");
	      	where.append(" and o.dataAprovacao < :dataFim");
	      	where.append(" and o.tipoRegiao.id in " + UFRNUtils.gerarStringIn(tiposRegiao));
	      	where.append(" and o.tipoRelatorio.id = " + TipoRelatorio.RELATORIO_EXTENSAO);

	        Query q = createQuery(RelatorioPesquisa.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // 4.3.2
    @SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findRelatorioAtividadeRegionalLocal2(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	int[] tiposRegiao = {TipoRegiao.REGIONAL, TipoRegiao.LOCAL};

    	try {
    		int[] status = new int[] {TipoSituacaoProjeto.EXTENSAO_CONCLUIDO};

	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	      	where.append(" and " + HibernateUtils.generateDateIntersection("o.dataInicio", "o.dataFim", ":dataInicio",   ":dataFim"));
	      	where.append(" and o.projeto.abrangencia.id in " + UFRNUtils.gerarStringIn(tiposRegiao));
	      	where.append(" and o.projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(status) );
	      	where.append(" and o.ativo = trueValue()" );
	      	where.append(" order by o.projeto.titulo" );

	        Query q = createQuery(MembroProjeto.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.4 - Atividades em cursos de extensão, devidamente comprovadas por
    // instâncias responsáveis
    // 4.4.1
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioAtividadeCurso1(Servidor docente,
            int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" and o.dataAprovacao >= :dataInicio");
	      	where.append(" and o.dataAprovacao < :dataFim");
	      	where.append(" and o.tipoRelatorio.id =" + TipoRelatorio.CURSO);

	        Query q = createQuery(RelatorioPesquisa.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // 4.4.2
    @SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findRelatorioAtividadeCurso2(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" , AtividadeExtensao at ");
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" and o.dataInicio >= :dataInicio");
	      	where.append(" and (o.dataFim is null  or o.dataFim < :dataFim)");
	      	where.append(" and o.projeto.id = at.projeto.id ");
	      	where.append(" and o.funcaoMembro.id = " + FuncaoMembro.COORDENADOR );
	      	where.append(" and o.projeto.situacaoProjeto.id = " + TipoSituacaoProjeto.EXTENSAO_CONCLUIDO);
	      	where.append(" and at.tipoAtividadeExtensao.id = " + TipoAtividadeExtensao.CURSO);

	        Query q = createQuery(MembroProjeto.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    //Grupo 1 - Item 5 - 5. Ensino de técnico
    @SuppressWarnings("unchecked")
	public Collection<DocenteTurma> findAtividadeEnsinoTecnicoDocente(Servidor docente, Integer anoVigencia, 
			Integer validade) throws DAOException {

        try {

        	StringBuilder where = new StringBuilder();
//        	where.append(" where getidpessoa("+docente.getId()+") = o.docente.pessoa.id");
        	where.append(" where o.docente.id = " + docente.getId() );
    		where.append(" and o.turma.disciplina.nivel = '"+NivelEnsino.TECNICO+"'");
    		where.append(" and o.turma.situacaoTurma.id in " + 
    		UFRNUtils.gerarStringIn(new int[]{SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA}));

    		//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0) {
        		int anoInicio = anoVigencia - validade;
        		int anoFim = anoVigencia;
        		where.append(" and  (o.turma.ano >= "+anoInicio+" and o.turma.ano <= "+anoFim+") ");
        	} else {
				where.append(" and  o.turma.ano = "+anoVigencia);
			}
    		Query q = createQuery(DocenteTurma.class, where.toString());
            return q.list();
            
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // Item 4.5 - Atividades de Assessoria, mini-curso em congresso,
    // consultoria, pericia ou sindicância devidamente comprovadas por
    // Instâncias
    // 4.5.1
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioAtividadeAssessoria1(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	int[] tiposRelatorio = {TipoRelatorio.ASSESSORIA,TipoRelatorio.ASSESSORIA_CONSULTORIA};
    	try {
	      	StringBuilder where = new StringBuilder();
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	      	where.append(" and o.dataAprovacao >= :dataInicio");
	      	where.append(" and o.dataAprovacao < :dataFim");
	      	where.append(" and o.tipoRelatorio.id in " + UFRNUtils.gerarStringIn(tiposRelatorio));

	        Query q = createQuery(RelatorioPesquisa.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // 4.5.2
    /*
    public Collection<MembroEquipeExtensao> findRelatorioAtividadeAssessoria2(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	
    	// TODO: Buscas nas classes do schema extensão
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where o.atividadeExtensaoProdocente.servidor.id = " + docente.getId());
	      	where.append(" and o.dataEntrada >= :dataInicio");
	      	where.append(" and o.dataSaida <= :dataInicio");
	      	where.append(" and o.atividadeExtensaoProdocente.tipoAtividadeExtensao.id = " + TipoAtividadeExtensao.PRESTACAO_SERVICO);

	        Query q = createQuery(MembroEquipeExtensao.class, where.toString(),anoVigencia,validade,false);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }*/

    // Item 4.6 - Atividades de Atividade de Atendimento de pacientes em
    // hospitais ou ambulatórios universitários, preferencialmente com a
    // presença de alunos
    // 4.6.1
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioAtividadeAtendimento1(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" and o.dataAprovacao >= :dataInicio");
	      	where.append(" and o.dataAprovacao < :dataFim");
	      	where.append(" and o.tipoRelatorio.id = " + TipoRelatorio.ATIVIDADE_TECNICA);

	        Query q = createQuery(RelatorioPesquisa.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // 4.6.2
    public Collection<MembroProjeto> findRelatorioAtividadeAtendimento2(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	return new ArrayList<MembroProjeto>();
    	/*try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where o.atividadeExtensaoProdocente.servidor.id = " + docente.getId());
	      	where.append(" and o.dataEntrada >= :dataInicio");
	      	where.append(" and o.dataSaida <= :dataInicio");
	      	where.append(" and o.atividadeExtensaoProdocente.tipoAtividadeExtensao.id = " + TipoAtividadeExtensao.PRESTACAO_SERVICO);
	      	where.append(" and (o.atividadeExtensaoProdocente.areaTematica1.id =" +AreaTematica.SAUDE+ " or o.atividadeExtensaoProdocente.areaTematica2.id= "+AreaTematica.SAUDE+")");

	        Query q = createQuery(MembroEquipeExtensao.class, where.toString(),anoVigencia, validade, false);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}*/
    }

    // Item 4.7 - Mini-cursos em eventos científicos, culturais e desportivos,
    // comprovados por certificados e aprovados em instâncias competentes
    @SuppressWarnings("unchecked")
	public Collection<MiniCurso> findRelatorioMiniCursoEvento(Servidor docente,
            int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( o.dataFim is null or o.dataFim > :dataFim  ) )  )");


	        Query q = createQuery(MiniCurso.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.8 - Patente ou produto registrado
    @SuppressWarnings("unchecked")
	public Collection<Patente> findRelatorioPatenteProduto(Servidor docente,
            int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id " );
	      	where.append(" and " + HibernateUtils.generateDateIntersection(":dataInicio", 
	      			":dataFim", "o.registroData", "o.registroData") );

	        Query q = createQuery(Patente.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.9 - Obra artística, cultural ou técnico-científica premiada ou
    @SuppressWarnings("unchecked")
	public Collection<PremioRecebido> findRelatorioObraArtisticaInternacional(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" and o.dataProducao >= :dataInicio");
	      	where.append(" and o.dataProducao < :dataFim");
	      	where.append(" and o.tipoRegiao.id = "+ TipoRegiao.INTERNACIONAL);

	        Query q = createQuery(PremioRecebido.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.10 - Obra artística, cultural ou tÃ©técnico-científica premiada
    // nacional
    @SuppressWarnings("unchecked")
	public Collection<PremioRecebido> findRelatorioObraArtisticaNacional(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id " );
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" and o.dataProducao >= :dataInicio");
	      	where.append(" and o.dataProducao < :dataFim");
	      	where.append(" and o.tipoRegiao.id = "+ TipoRegiao.NACIONAL);

	        Query q = createQuery(PremioRecebido.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }
    // Item 4.11 - Obra artística, cultural ou técnico-científica premiada
    // regional
    @SuppressWarnings("unchecked")
	public Collection<PremioRecebido> findRelatorioObraArtisticaRegionalLocal(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	int[] tiposRegiao = {TipoRegiao.REGIONAL, TipoRegiao.LOCAL};

    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" and o.dataProducao >= :dataInicio");
	      	where.append(" and o.dataProducao < :dataFim");
	      	where.append(" and o.tipoRegiao.id in "+ UFRNUtils.gerarStringIn(tiposRegiao));
	      	where.append(" and o.ativo = trueValue()");

	        Query q = createQuery(PremioRecebido.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // GRUPO IV: Item 01 do RID 
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findParticipacaoEventoRelatorioRID(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        	try {
    	      	StringBuilder where = new StringBuilder();
    	      	where.append(" where o.servidor.id = " + docente.getId());
    	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
        		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");

    	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
    	        return q.list();

        	} catch (Exception e) {
    	          throw new DAOException(e.getMessage(), e);
        	}
    }
    
    // Item 4.12 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais internacionais como coordenador geral
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoInternacionalCoordenador(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        	try {
    	      	StringBuilder where = new StringBuilder();
    	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//    	      	where.append(" where o.servidor.id = " + docente.getId());
    	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
        		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
    	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.RESPONSAVEL_ORGANIZACAO_EVENTO);
    	        where.append(" and o.ambito.id = "+TipoRegiao.INTERNACIONAL);
    	        where.append(" and (ativo is null or ativo = trueValue())");

    	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
    	        return q.list();

        	} catch (Exception e) {
    	          throw new DAOException(e.getMessage(), e);
        	}
    }

    // Item 4.13 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais nacionais como coordenador geral
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoNacionalCoordenador(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.RESPONSAVEL_ORGANIZACAO_EVENTO);
	        where.append(" and o.ambito.id = "+TipoRegiao.NACIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.14 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais Regionais como coordenador geral
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoRegionalCoordenador(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.RESPONSAVEL_ORGANIZACAO_EVENTO);
	        where.append(" and o.ambito.id = "+TipoRegiao.REGIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.15 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais locais como coordenador geral
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoLocalCoordenador(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.RESPONSAVEL_ORGANIZACAO_EVENTO);
	        where.append(" and o.ambito.id = "+TipoRegiao.LOCAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.16 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais internacional como membro
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoInternacionalMembro(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.MEMBRO_COMISSAO_ORGANIZADORA);
	        where.append(" and o.ambito.id = "+TipoRegiao.INTERNACIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}

    }

    // Item 4.17 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais nacional como membro
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoNacionalMembro(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.MEMBRO_COMISSAO_ORGANIZADORA);
	        where.append(" and o.ambito.id = "+TipoRegiao.NACIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.18 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais regional ou local como membro
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoRegionalMembro(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	int[] tiposRegiao = {TipoRegiao.REGIONAL,TipoRegiao.LOCAL};
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( periodoFim is null or periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.MEMBRO_COMISSAO_ORGANIZADORA);
	        where.append(" and o.ambito.id in "+UFRNUtils.gerarStringIn(tiposRegiao));
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.19 - Participação em visita ou missão Internacional para o
    // desenvolvimento de atividades acadêmicas
    @SuppressWarnings("unchecked")
	public Collection<VisitaCientifica> findRelatorioParticipacaoVisitaMissaoInternacional(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	      	where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( o.periodoFim is null or o.periodoFim > :dataFim  ) )  )");
	        where.append(" and o.ambito.id = "+TipoRegiao.INTERNACIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(VisitaCientifica.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.20 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais internacional como convidado
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoInternacionalConvidado(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( o.periodoFim is null or o.periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.CONFERENCISTA_CONVIDADO);
	        where.append(" and o.ambito.id = "+TipoRegiao.INTERNACIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.21 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais nacional como convidado
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoNacionalConvidado(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( o.periodoFim is null or o.periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.CONFERENCISTA_CONVIDADO);
	        where.append(" and o.ambito.id = "+TipoRegiao.NACIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 4.22 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais regional como convidado
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoRegionalConvidado(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( o.periodoFim is null or o.periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.CONFERENCISTA_CONVIDADO);
	        where.append(" and o.ambito.id = "+TipoRegiao.REGIONAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}

    }

    // Item 4.23 - Participação em eventos científicos, desportivos ou
    // artísticos-culturais local como convidado
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoComissaoOrgEventos> findRelatorioParticipacaoEventoLocalConvidado(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
//	      	where.append(" where o.servidor.id = " + docente.getId());
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
	        where.append(" and ((o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
    		" or (o.periodoInicio < :dataFim  and ( o.periodoFim is null or o.periodoFim > :dataFim  ) )  )");
	        where.append(" and o.tipoParticipacaoOrganizacao.id = "+TipoParticipacaoOrganizacaoEventos.CONFERENCISTA_CONVIDADO);
	        where.append(" and o.ambito.id = "+TipoRegiao.LOCAL);
	        where.append(" and (ativo is null or ativo = trueValue())");

	        Query q = createQuery(ParticipacaoComissaoOrgEventos.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // ITEM 5

    // Item 5.1 - Curso de Mestrado, Doutorado ou estágio de Pós-Doutorado
  //Deprecated devido ao Refactoring em Formação Acadêmica, que será consultado utilizando o serviço diponível no SIGRH
    @SuppressWarnings("unchecked")
    @Deprecated
	public Collection<FormacaoAcademica> findRelatorioCursoMestradoDoutoradoEstagioDocente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	int[] tiposFormacao = {FormacaoTese.ESPECIALIZACAO,FormacaoTese.DOUTORADO,FormacaoTese.POS_DOUTORADO};
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    		" or (o.dataInicio < :dataFim  and ( o.dataFim is null or o.dataFim > :dataFim  ) )  )");
	        where.append(" and o.formacao.id in "+UFRNUtils.gerarStringIn(tiposFormacao));
	        where.append(" and (o.ativo is null or o.ativo = trueValue())");

	        Query q = createQuery(FormacaoAcademica.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 5.2 - Avaliação do desempenho pelo Orientador
    @SuppressWarnings("unchecked")
	public Collection<QualificacaoDocente> findRelatorioAvaliacaoDesempenhoOrientador(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {

    	int[] tiposQualificacao = {TipoQualificacao.ESPECIALIZACAO,TipoQualificacao.POS_DOUTORADO};
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFinal >= :dataInicio and o.dataFinal < :dataFim) " +
    		" or (o.dataInicial < :dataFim  and ( o.dataFinal is null or o.dataFinal > :dataFim  ) )  )");
	        where.append(" and o.tipoQualificacao.id in "+UFRNUtils.gerarStringIn(tiposQualificacao));
	        where.append(" and o.tipoParecer.id = "+TipoParecer.FAVORAVEL);
	        where.append(" and (o.ativo is null or o.ativo = trueValue())");

	        Query q = createQuery(QualificacaoDocente.class, where.toString(),anoVigencia, validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}

    }
    
    // Grupo VI do relatório RID.
    @SuppressWarnings("unchecked")
	public Collection<QualificacaoDocente> findRelatorioQualificacaoDocente(
    		Servidor docente, int anoVigencia,Integer validade) throws DAOException {
    	
    	try {
    		StringBuilder where = new StringBuilder();
    		where.append(" where o.servidor.id = " + docente.getId());
    		where.append(" and ((o.dataFinal >= :dataInicio and o.dataFinal < :dataFim) " +
    		" or (o.dataInicial < :dataFim  and ( o.dataFinal is null or o.dataFinal > :dataFim  ) )  )");
    		where.append(" and o.tipoParecer.id = "+TipoParecer.FAVORAVEL);
    		where.append(" and (o.ativo is null or o.ativo = trueValue())");
    		
    		Query q = createQuery(QualificacaoDocente.class, where.toString(),anoVigencia, validade);
    		return q.list();
    		
    	} catch (Exception e) {
    		throw new DAOException(e.getMessage(), e);
    	}
    	
    }

    // Item 5.3 - Título de Especialista obtido no período avaliado
  //Deprecated devido ao Refactoring em Formação Acadêmica, que será consultado utilizando o serviço diponível no SIGRH
    @SuppressWarnings("unchecked")
    @Deprecated
	public Collection<FormacaoAcademica> findRelatorioEspecialistaDocente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    		" or (o.dataInicio < :dataFim  and ( o.dataFim is null or o.dataFim > :dataFim  ) )  )");
	        where.append(" and o.formacao.id = "+FormacaoTese.ESPECIALIZACAO);

	        Query q = createQuery(FormacaoAcademica.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }

    // Item 6 - Atividades administrativas e de Representação

    // Item 6.1 - Vice-Chefe de Departamento Acadêmico
    @SuppressWarnings("unchecked")
	public Collection<Designacao> findRelatorioViceChefeDepto(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {

        	StringBuilder where = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidorSigaa.pessoa.id " );
//        	where.append(" where o.servidorSigaa.id = " + docente.getId() );
            where.append(" and ((o.fim >= :dataInicio and o.fim < :dataFim) " +
            		" or (o.inicio < :dataFim  and ( o.fim is null or o.fim > :dataFim  ) )  )");
            where.append(" and o.gerencia = 'S'");
            where.append(" and o.atividade.codigoRH in " + UFRNUtils.gerarStringIn(AtividadeServidor.CHEFE_DEPARTAMENTO));
            where.append(" order by o.fim desc");

            Query q = createQuery(Designacao.class, where.toString(), anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}


    }

    // Item 6.2 - Vice-Coordenador de curso de Graduação ou pós
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<CargoAdministrativo> findRelatorioViceCoordenadorCurso(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    		int[] designacaoCargos ={DesignacaoCargo.VICE_COORDENADOR_CURSO_GRADUACAO,DesignacaoCargo.VICE_COORDENADOR_CURSO_POS_GRADUACAO};
    	try {
	      	StringBuilder where = new StringBuilder();
	      	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//	      	where.append(" where o.servidor.id = " + docente.getId());
	        where.append(" and ((o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    		" or (o.dataInicio < :dataFim  and ( o.dataFim is null or o.dataFim > :dataFim  ) )  )");
	        where.append(" and o.designacaoCargo.id in "+UFRNUtils.gerarStringIn(designacaoCargos));

	        Query q = createQuery(CargoAdministrativo.class, where.toString(),anoVigencia,validade);
	        return q.list();

    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}

    }

    /**
     * Item 6.3 - Participação em colegiados
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findRelatorioParticipacaoColegiado(
            Servidor docente, int anoVigencia,Integer validade) throws DAOException {
        try {
            int[] tipoComissaoColegado = { TipoComissaoColegiado.COLEGIADO_CURSO_CONSEC,
            		TipoComissaoColegiado.COLEGIADO_SUPERIOR };

            String where = " where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id and " +
            		HibernateUtils.generateDateIntersection(":dataInicio", ":dataFim", "o.periodoInicio", "o.periodoFim") +
            " and o.tipoComissaoColegiado.id in " + UFRNUtils.gerarStringIn(tipoComissaoColegado) +
            " and o.nato = falseValue()"+
            " and (ativo is null or ativo = trueValue())";

            Query q = createQuery(ParticipacaoColegiadoComissao.class, where, anoVigencia, validade);
            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

	/**
	 * Item 6.3 - Busca todas as Participações em colegiados e comissões
	 *
	 * @param docente
	 * @param anoVigencia
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findAllParticipacoesColegiadoComissao(
			Servidor docente, int anoVigencia,Integer validade) throws DAOException {
		try {
			String where = " where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id and " +
			HibernateUtils.generateDateIntersection(":dataInicio", ":dataFim", "o.periodoInicio", "o.periodoFim") +
			" and o.nato = falseValue()"+
			" and (ativo is null or ativo = trueValue())";
			
			Query q = createQuery(ParticipacaoColegiadoComissao.class, where, anoVigencia, validade);
			return q.list();
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
    /**
     * Item 6.4 - Participação em comissão de criação de novos cursos e
     * reformulação de projeto pedagógico
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findRelatorioParticipacaoComissaoNovosCursos(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            String where = " where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id and" +
        			HibernateUtils.generateDateIntersection(":dataInicio", ":dataFim", "o.periodoInicio", "o.periodoFim") +
					" and o.tipoComissaoColegiado.id = " + TipoComissaoColegiado.CRIACAO_CURSO_REFORM_PROJETO +
					" and (ativo is null or ativo = trueValue())";

            Query q = createQuery(ParticipacaoColegiadoComissao.class, where, anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.5 -  Participação em comissão de criação de novos cursos e
     * reformulação de projeto pedagógico
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findRelatorioParticipacaoComissaoPermanente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            String where = " where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id and " +
            				HibernateUtils.generateDateIntersection(":dataInicio", ":dataFim", "o.periodoInicio", "o.periodoFim") +
            			   " and o.tipoComissaoColegiado.id = " + TipoComissaoColegiado.COMISSAO_PERMANENTE +
            			   " and (ativo is null or ativo = trueValue())";

            Query q = createQuery(ParticipacaoColegiadoComissao.class, where, anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.6 -  Participação em comissão de criação de novos cursos e
     * reformulação de projeto pedagógico
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findRelatorioParticipacaoComissaoTemporarias(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            String where = " where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id and" +
            			   HibernateUtils.generateDateIntersection(":dataInicio", ":dataFim", "o.periodoInicio", "o.periodoFim") +
            			   " and o.tipoComissaoColegiado.id = " + TipoComissaoColegiado.COMISSAO_TEMPORARIA +
            			   " and (ativo is null or ativo = trueValue())";

            Query q = createQuery(ParticipacaoColegiadoComissao.class, where, anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // Item 6.7 - Participação em comissões de sindicância ou de processos de
    // natureza disciplinar

	@SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findRelatorioParticipacaoComissaoSindicancia(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            String where = " where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id and" +
            				HibernateUtils.generateDateIntersection(":dataInicio", ":dataFim", "o.periodoInicio", "o.periodoFim") +
			               " and o.tipoComissaoColegiado.id = " + TipoComissaoColegiado.COMISSAO_SINDICANCIA+
			               " and (ativo is null or ativo = trueValue())";

            Query q = createQuery(ParticipacaoColegiadoComissao.class, where, anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.8 - Chefia ou coordenação de setores acadêmico de apoio
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Chefia> findRelatorioChefiaCoordenacaoSetoresAcademicoApoio(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.dataFinal >= :dataInicio and o.dataPublicacao < :dataFim) " +
            		" or ( (o.dataPublicacao < :dataFim ) and ( o.dataFinal is null or o.dataFinal > :dataFim ) ) )");
            where.append(" and o.remunerado = falseValue() ");
            where.append(" and o.tipoChefia.id != " + TipoChefia.BASE_PESQUISA);

            Query q = createQuery(Chefia.class, where.toString(), anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.9 - Coordenação de Base de Pesquisa
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	@Deprecated
    public Collection<Chefia> findRelatorioChefiaCoordenacaoBasePesquisa(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.dataFinal >= :dataInicio and o.dataPublicacao < :dataFim) " +
            		" or ( (o.dataPublicacao < :dataFim ) and ( o.dataFinal is null or o.dataFinal > :dataFim ) ) )");
            where.append(" and o.remunerado = falseValue() ");
            where.append(" and o.tipoChefia.id = " + TipoChefia.BASE_PESQUISA);

            Query q = createQuery(Chefia.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.10 - Coordenador de Curso lato sensu
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<Coordenacao> findRelatorioChefiaCoordenacaoCursoLato(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.pago = falseValue()");

            Query q = createQuery(Coordenacao.class, where.toString(), anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
    
    /**
     * Item 6.10 - Coordenador de Curso lato sensu (Busca as coordenações de propostas de curso cadastradas)
     * O item de coordenador de curso lato sensu deve buscar os dados tanto na entidade prodocente.Coordenacao,
     * para as coordenações anteriores à 2007, como em ensino.CoordenacaoCurso, para os cursos cadastrados no SIGAA.
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<CoordenacaoCurso> findRelatorioCoordenacaoCursoLato(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.dataFimMandato >= :dataInicio and o.dataFimMandato < :dataFim) " +
            		" or ( (o.dataInicioMandato < :dataFim ) and ( o.dataFimMandato is null or o.dataFimMandato > :dataFim ) ) )");
            where.append(" and o.curso.id in (select id from CursoLato where cursoPago = falseValue())");

            Query q = createQuery(CoordenacaoCurso.class, where.toString(), anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
    
    /**
     * Grupo V: Itens 16 e 17 do Relatório RID. Retorna as coordenações de cursos de graduação e programas
     * de pós-graduação do docente no período informado.
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<CoordenacaoCurso> findRelatorioCoordenacaoCursoGraduacaoEPos(
    		Servidor docente, int anoVigencia, Integer validade) throws DAOException {
    	try {
    		StringBuilder hql  = new StringBuilder();
    		hql.append(" select o.dataInicioMandato, o.dataFimMandato, ca.id, c.id, c.nivel, u.id ");
    		hql.append(" from CoordenacaoCurso o left join o.curso c left join o.unidade u inner join o.cargoAcademico ca ");
    		hql.append(" where o.servidor.id = " + docente.getId() );
    		hql.append(" and "+ HibernateUtils.generateDateIntersection("o.dataInicioMandato", "o.dataFimMandato", ":dataInicio", ":dataFim"));
    		
    		String hqlStr = hql.toString();
    		Query q = getSession().createQuery(hqlStr);
        	HashMap<String, Date> datas = getDatas(anoVigencia, validade);
			q.setDate("dataInicio", datas.get(DATA_INICIO));
			q.setDate("dataFim", datas.get(DATA_FIM));
    		
			List<Object[]> lista = q.list();
	        Collection<CoordenacaoCurso> result = new ArrayList<CoordenacaoCurso>(lista.size());
	        for (int i = 0; i < lista.size(); i++) {
	        	int col = 0;
	        	Object[] colunas = lista.get(i);
	        	
	        	CoordenacaoCurso c = new CoordenacaoCurso();
	        	c.setDataInicioMandato((Date) colunas[col++]);
	        	c.setDataFimMandato((Date) colunas[col++]);
	        	
	        	CargoAcademico ca = new CargoAcademico();
	        	ca.setId((Integer) colunas[col++]);
	        	c.setCargoAcademico(ca);
	        	
	        	Integer idCurso = (Integer) colunas[col++];
	        	Character nivel = (Character) colunas[col++];
	        	Integer idUnidade = (Integer) colunas[col++];
	        	
	        	if(idCurso != null){
	        		c.setCurso( new Curso(idCurso) );
	        		c.getCurso().setNivel(nivel);
	        	}else
	        		c.setCurso(null);
	        	if(idUnidade != null)
	        		c.setUnidade( new Unidade(idUnidade) );
	        	else
	        		c.setUnidade(null);
	        	
	        	result.add(c);
	        }
			
    		return result;
    	} catch (Exception e) {
    		throw new DAOException(e.getMessage(), e);
    	}
    }

    /**
     * Item 6.11 - Consultor "ad hoc" revista internacional
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioConsultorAdHocRevistaInternacional(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.CONSULTOR_AD_HOC_REVISTA_CIENTIFICA);
            where.append(" and o.tipoRegiao.id = " + TipoRegiao.INTERNACIONAL);

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.12 - Consultor "ad hoc" revista nacional
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioConsultorAdHocRevistaNacional(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.CONSULTOR_AD_HOC_REVISTA_CIENTIFICA);
            where.append(" and o.tipoRegiao.id = " + TipoRegiao.NACIONAL);

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.13 - Consultor "ad hoc" revista regional ou local
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioConsultorAdHocRevistaRegionalLocal(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	int[] tiposRegioes = { TipoRegiao.REGIONAL, TipoRegiao.LOCAL };

        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.CONSULTOR_AD_HOC_REVISTA_CIENTIFICA);
            where.append(" and o.tipoRegiao.id in " + UFRNUtils.gerarStringIn(tiposRegioes));

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.14 - Consultor "ad hoc" anais internacional
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioConsultorAdHocAnaisInternacional(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.CONSULTOR_AD_HOC_ANAIS_EVENTO);
            where.append(" and o.tipoRegiao.id = " + TipoRegiao.INTERNACIONAL);

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.15 - Consultor "ad hoc" anais nacional ou regional
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioConsultorAdHocAnaisNacionalRegional(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	int[] tiposRegioes = { TipoRegiao.NACIONAL, TipoRegiao.REGIONAL };

        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.CONSULTOR_AD_HOC_ANAIS_EVENTO);
            where.append(" and o.tipoRegiao.id in " +  UFRNUtils.gerarStringIn(tiposRegioes));

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.16 - Consultor "ad hoc" de orgão formento
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioConsultorAdHocOrgaoFormento(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.CONSULTOR_AD_HOC_ORGAO_FOMENTO);

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.17 - Representação acadêmica e participação em orgão de
     * formulação e execução de políticas públicas de ensino, ciência e
     * tecnologia
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioRepresentacaoAcademica(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.REPRESENTACAO_ORGAO_FOMENTO);

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia,validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 6.18 - Representação acadêmica e participação em órgão de
     * formulação e execução de políticas públicas de ensino, ciência e
     * tecnologia
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<AvaliacaoOrganizacao> findRelatorioConsultorAdHocReformaAvaliacaoCurso(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoAvaliacaoOrganizacao.id = " + TipoAvaliacaoOrganizacaoEvento.CONSULTOR_AD_HOC_COMISSAO_NACIONAL_REFORMA);

            Query q = createQuery(AvaliacaoOrganizacao.class, where.toString(), anoVigencia,validade);
            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // ITEM 7 - Outras Atividades de ensino, pesquisa e extensão

    /**
     * Item 7.1 - Participação em banca examinadora de concurso público para
     * professor titular ou livre docência
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaConcursoProfessorTitular(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	int[] categoriasFuncionais = {CategoriaFuncional.PROF_TITULAR.getId(),
        			CategoriaFuncional.LIVRE_DOCENCIA.getId()};

        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	}
        	else
        	{
        		where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
        	}
            where.append(" and o.categoriaFuncional.id in " +  UFRNUtils.gerarStringIn(categoriasFuncionais));
            where.append(" and o.tipoBanca.id = " + TipoBanca.CONCURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.2 - Participação em banca examinadora de concurso público para
     * professor Adjunto, Assistente ou Auxiliar
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaConcursoProfessorAdjAssAux(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

        try {
        	int[] categoriasFuncionais = {CategoriaFuncional.PROF_ADJUNTO.getId(),
        			CategoriaFuncional.PROF_ASSISTENTE.getId(), CategoriaFuncional.PROF_AUXILIAR.getId()};

        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.categoriaFuncional.id in " +  UFRNUtils.gerarStringIn(categoriasFuncionais));
            where.append(" and o.tipoBanca.id = " + TipoBanca.CONCURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.3 - Participação em banca examinadora de concurso público para
     * professor Substituto
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaConcursoProfessorSubstituto(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.categoriaFuncional.id = " + CategoriaFuncional.PROF_SUBSTITUTO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CONCURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.4 - Participação em banca examinadora de concurso público para
     * professor Nível Médio
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaConcursoProfessorMedio(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.categoriaFuncional.id = " + CategoriaFuncional.PROF_NIVEL_MEDIO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CONCURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.5 - Participação em banca examinadora de concurso público para
     * Técnico
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaConcursoTecnico(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
        	where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.categoriaFuncional.id = " + CategoriaFuncional.TECNICO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CONCURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.6 - Participação em banca examinadora de Tese de Doutorado
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaTeseDoutorado(Servidor docente,
            int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
            
        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.TESE_DOUTORADO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.7 - Participação em banca examinadora de Tese de Mestrado
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaTeseMestrado(Servidor docente,
            int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
            
        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >=" + anoInicio + " AND o.anoReferencia <=" + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.DISSERTACAO_MESTRADO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.8 - Participação em banca examinadora de Qualificação de Doutorado
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaQualificacaoDoutorado(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.QUALIFICACAO_DOUTORADO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.9 - Participação em banca examinadora de Qualificação de Mestrado
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaQualificacaoMestrado(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.QUALIFICACAO_MESTRADO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.10 - Participação em banca examinadora de monografia de
     * especialização
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaMonografiaEspecializacao(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.MONOGRAFIA_ESPECIALIZACAO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.11 - Participação em banca examinadora de Seleção de Doutorado
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaSelecaoDoutorado(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoVigencia + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.SELECAO_DOUTORADO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.12 - Participação em banca examinadora de Seleção de Mestrado
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaSelecaoMestrado(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		//int anoInicio = anoVigencia - validade;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoVigencia + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.SELECAO_MESTRADO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.13 - Participação em banca examinadora de Monografia de Graduação
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaMonografiaGraduacao(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.MONOGRAFIA_GRADUACAO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.14 - Participação em banca examinadora de seleção de
     * Especialização
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Banca> findRelatorioBancaSelecaoEspecializacao(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	StringBuilder where  = new StringBuilder();
            where.append(" left join o.categoriaFuncional as categoriaFuncional ");
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");

        	//Caso seja informada a validade, a consulta será feita do ano Vigência para traz, até o valor de validade.
        	if (validade != null && validade >0)
        	{
        		int anoInicio = (anoVigencia - validade)+1;
        		int anoFim = anoVigencia;
        		where.append(" and (o.anoReferencia >= " + anoInicio + " AND o.anoReferencia <= " + anoFim + ")");
        	} else {
				where.append(" and (o.anoReferencia = " + anoVigencia + " or o.anoReferencia = " + (anoVigencia - 1) + ")");
			}

            where.append(" and o.naturezaExame.id = " + NaturezaExame.SELECAO_ESPECIALIZACAO.getId());
            where.append(" and o.tipoBanca.id = " + TipoBanca.CURSO);

            Query q = createQuery(Banca.class, where.toString());
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.15 - Disciplina Cursada como parte de programa de pós-graduação
     * Lato-Sensu sem afastamento
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<DisciplinaQualificacao> findRelatorioDisciplinaCursadaLato(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            int[] tiposQualificacao = { TipoQualificacao.CURSO_ATUALIZACAO_PEDAGOGICA,
                    TipoQualificacao.ESPECIALIZACAO };

            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.qualificacaoDocente.servidor.pessoa.id ");
//            where.append(" where o.qualificacaoDocente.servidor.id = " + docente.getId() );
            where.append(" and ( (o.qualificacaoDocente.dataFinal >= :dataInicio and o.qualificacaoDocente.dataFinal < :dataFim) " +
            		" or ( (o.qualificacaoDocente.dataInicial < :dataFim ) and ( o.qualificacaoDocente.dataFinal is null or o.qualificacaoDocente.dataFinal > :dataFim ) ) )");
            where.append(" and o.qualificacaoDocente.tipoQualificacao.id in " + UFRNUtils.gerarStringIn(tiposQualificacao));
            where.append(" and o.qualificacaoDocente.afastado = falseValue()");

            Query q = createQuery(DisciplinaQualificacao.class, where.toString(), anoVigencia, validade);
            return q.list();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     *  Item 7.16 - Orientador Acadêmico
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findRelatorioOrientadorAcademico(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoComissaoColegiado.id = " + TipoComissaoColegiado.ORIENTADOR_ACADEMICO );

            Query q = createQuery(ParticipacaoColegiadoComissao.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findOrientacaoIniciacaoCientifica(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	//int[] tipoBolsaPesquisa = {TipoBolsaPesquisa.BALCAO, TipoBolsaPesquisa.PIBIC,
    	//		TipoBolsaPesquisa.PROPESQ, TipoBolsaPesquisa.VOLUNTARIO};

        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.planoTrabalho.orientador.pessoa.id ");
//            where.append(" where o.planoTrabalho.orientador.id = " + docente.getId() );
            where.append( " and ( (o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    			" or ( o.dataInicio < :dataFim and ( o.dataFim is null or o.dataFim > :dataFim ) ) )");
            //where.append(" and o.planoTrabalho.tipoBolsa in " + UFRNUtils.gerarStringIn(tipoBolsaPesquisa));
            where.append(" group by o.discente.pessoa.nome");
            where.append(" order by o.discente.pessoa.nome");

            Query q = createQuery(MembroProjetoDiscente.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
	public Collection<OrientacaoICExterno> findOrientacaoIniciacaoCientificaExterna(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

    	//int[] tipoBolsaPesquisa = {TipoBolsaPesquisa.BALCAO, TipoBolsaPesquisa.PIBIC,
    	//		TipoBolsaPesquisa.PROPESQ, TipoBolsaPesquisa.VOLUNTARIO};

        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.orientador.pessoa.id ");
//            where.append(" where o.orientador.id = " + docente.getId() );
            where.append( " and ( (o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
    			" or ( o.dataInicio < :dataFim and ( o.dataFim is null or o.dataFim > :dataFim ) ) )");
           where.append(" group by nomeOrientando, dataInicio, dataFim");
           where.append(" order by nomeOrientando");

            Query q = createQuery(OrientacaoICExterno.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findOrientacaoIniciacaoCientificaRID(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        
        try {
            StringBuilder hql  = new StringBuilder();
            hql.append(" from MembroProjetoDiscente o " );
            hql.append(" where o.planoTrabalho.orientador.id = " + docente.getId() );
            hql.append( " and ( (o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
            " or ( o.dataInicio < :dataFim and ( o.dataFim is null or o.dataFim > :dataFim ) ) )");
            hql.append(" order by o.discente.pessoa.nome");
            
            Query q = getSession().createQuery(hql.toString());
            
            HashMap<String, Date> datas = getDatas(anoVigencia, validade);
            q.setDate("dataInicio", datas.get(DATA_INICIO));
            q.setDate("dataFim", datas.get(DATA_FIM));
            
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
    
    /**
     * Item 7.17 - Orientação de Alunos de Graduação: Iniciação Científica ,
     * Pet, iniciação tecnológica, Extensão, Monitoria e Apoio Técnico em
     * atividades acadêmicas
     * 7.17.1
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findRelatorioOrientacaoAlunosGraduacao1(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {

//    	int[] tipoBolsaPesquisa = {TipoBolsaPesquisa.BALCAO,TipoBolsaPesquisa.PIBIC,TipoBolsaPesquisa.PROPESQ};

        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.planoTrabalho.orientador.pessoa.id ");
//            where.append(" where o.planoTrabalho.orientador.id = " + docente.getId() );
            where.append(" and ( (o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
            		" or ( (o.dataInicio < :dataFim ) and ( o.dataFim is null or o.dataFim > :dataFim ) ) )");
//            where.append(" and o.planoTrabalho.tipoBolsa in "+UFRNUtils.gerarStringIn(tipoBolsaPesquisa));
            where.append(" group by o.discente.pessoa.nome");
            where.append(" order by o.discente.pessoa.nome");

            Query q = createQuery(MembroProjetoDiscente.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.17 - Orientação de Alunos de Graduação: Iniciação Científica ,
     * Pet, iniciação tecnológica, Extensão, Monitoria e Apoio Técnico em
     * atividades acadêmicas
     * 7.17.2
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<OrientacaoProex> findRelatorioOrientacaoAlunosGraduacao2(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.dataFinal >= :dataInicio and o.dataFinal < :dataFim) " +
            		" or ( (o.dataInicio < :dataFim ) and ( o.dataFinal is null or o.dataFinal > :dataFim ) ) )");

            Query q = createQuery(OrientacaoProex.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }


    /**
     * Item 7.17 -  Orientação de Alunos de Graduação: Iniciação Científica ,
     * Pet, iniciação tecnológica, Extensão, Monitoria e Apoio Técnico em
     * atividades acadêmicas
     * 7.17.3
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<Orientacao> findRelatorioOrientacaoAlunosGraduacao3(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") =  o.equipeDocente.servidor.pessoa.id" );
//            where.append(" where o.equipeDocente.servidor.id = " + docente.getId() );
            where.append(" and ( (o.dataFim >= :dataInicio and o.dataFim < :dataFim) " +
            		" or ( (o.dataInicio < :dataFim ) and ( o.dataFim is null or o.dataFim > :dataFim ) ) )");
            where.append(" and o.discenteMonitoria.tipoVinculo.id in " + 
            		UFRNUtils.gerarStringIn( new int[]{ TipoVinculoDiscenteMonitoria.BOLSISTA, TipoVinculoDiscenteMonitoria.NAO_REMUNERADO} ));
            where.append(" and o.ativo = trueValue()");
            
            Query q = createQuery(Orientacao.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 7.17 -  Orientação de Alunos de Graduação: Iniciação Científica ,
     * Pet, iniciação tecnológica, Extensão, Monitoria e Apoio Técnico em
     * atividades acadêmicas
     * 7.17.4
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<TutoriaPet> findRelatorioOrientacaoAlunosGraduacao4(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");

            Query q = createQuery(TutoriaPet.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

//
    /**
     * Item 8.39 - Orientação de Tese de Pós-Doutorado concluída na UFRN e outra IFES
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosPosDoutoradoConcluidoDocente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
//            where.append(" where o.servidor.id = " + docente.getId() );
            where.append(" and o.dataPublicacao >= :dataInicio");
            where.append(" and o.dataPublicacao < :dataFim");
            where.append(" and o.tipoOrientacao.id = " + TipoOrientacao.POS_DOUTORADO );

            Query q = createQuery(TeseOrientada.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * Item 8.40 - Orientação - Especialização concluída
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesPosEspecializacaoConcluidaDocente(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder hql  = new StringBuilder();
            hql.append(" select o.pago as pago, o.orientando as orientando, oD as orientandoDiscente, o.periodoFim as periodoFim, o.dataPublicacao as dataPublicacao, o.periodoInicio as periodoInicio ");
            hql.append(" from TeseOrientada o left join o.orientandoDiscente oD");
            hql.append(" where getidpessoa("+docente.getId()+") = o.servidor.pessoa.id ");
            hql.append(" and o.dataPublicacao >= :dataInicio");
            hql.append(" and o.dataPublicacao < :dataFim");
            hql.append(" and o.tipoOrientacao.id = " + TipoOrientacao.ESPECIALIZACAO);
            hql.append(" and o.periodoFim is not null");
            hql.append(" and (o.pago is null or o.pago = falseValue())");

            Query q = getSession().createQuery(hql.toString());
            q.setResultTransformer( Transformers.aliasToBean(TeseOrientada.class) );

        	HashMap<String, Date> datas = getDatas(anoVigencia, validade);
			q.setDate("dataInicio", datas.get(DATA_INICIO));
			q.setDate("dataFim", datas.get(DATA_FIM));
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    //item 8.41 = 7.17.1
    //item 8.42 = 7.17.3

    /**
     * Item 8.49 (especificação de uma consulta já existente subtipo  existente subtipo artístico específico, subconjunto de 3.20)
     *
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioFinalPesquisaCoordenador(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where getidpessoa("+docente.getId()+") = o.projetoPesquisa.coordenador.pessoa.id ");
//            where.append(" where o.projetoPesquisa.coordenador.id = " + docente.getId() );
            where.append(" and o.dataEnvio >= :dataInicio");
            where.append(" and o.dataEnvio < :dataFim");

            Query q = createQuery(RelatorioProjeto.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    /**
     * @param docente
     * @param anoVigencia
     * @return
     * @throws DAOException
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
	public Collection<RelatorioPesquisa> findRelatorioFinalPesquisaColaborador(
            Servidor docente, int anoVigencia, Integer validade) throws DAOException {
        try {
        	int[] funcoes = new int[] {FuncaoMembro.COLABORADOR, FuncaoMembro.VICE_COORDENADOR};

            StringBuilder where  = new StringBuilder();
            where.append(" join o.projetoPesquisa.projeto.equipe mp ");
            where.append(" where getidpessoa("+docente.getId()+") = mp.servidor.pessoa.id ");
//            where.append(" where mp.servidor.id = " + docente.getId() );
            where.append(" and  mp.funcaoMembro.id in  " + UFRNUtils.gerarStringIn(funcoes));
            where.append(" and o.dataEnvio >= :dataInicio");
            where.append(" and o.dataEnvio < :dataFim");

            Query q = createQuery(RelatorioProjeto.class, where.toString(), anoVigencia, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoes(Servidor servidor, int ano) throws DAOException {

    	Criteria c = getSession().createCriteria(TeseOrientada.class);
    	c.add(Expression.eq("servidor", servidor));
    	//c.add(Expression.sql("extract(year from data_publicacao) = " + ano));
    	c.add(Expression.or(Expression.isNull("ativo"), Expression.eq("ativo", Boolean.TRUE)));
    	c.addOrder(Order.desc("tipoOrientacao.id"));
    	return c.list();

    }

    @SuppressWarnings("unchecked")
	public Collection<TeseOrientada> findOrientacoesConcluidas(Servidor servidor, int anoInicial, int anoFinal, Integer validade) throws DAOException {

    	Criteria c = getSession().createCriteria(TeseOrientada.class);
    	c.add(Expression.eq("servidor", servidor));
    	c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo")));
    	c.add(Expression.or(Expression.eq("desligado", false), Expression.isNull("desligado")));
    	c.add(Expression.sql("extract(year from periodo_inicio) >= " + anoInicial));
    	c.add(Expression.sql("extract(year from periodo_fim) <= " + anoFinal));
     	
    	c.addOrder(Order.desc("tipoOrientacao.id"));
    	return c.list();

    }

    /**
     * Consulta que traz a carga horária total do docente na graduação
     * em um determinado ano
     *
     * @param servidor
     * @param ano
     * @param validade
     * @return
     * @throws DAOException
     */
    public Collection<CargaHorariaEnsino> findCargaHorariaGraduacaoSintetico(Servidor servidor, int ano, Integer validade) throws DAOException {
    	Collection<CargaHorariaEnsino> cargas = new ArrayList<CargaHorariaEnsino>();

    	if (validade == null) {
			validade = 1;
		}
    	StringBuilder hql = new StringBuilder();
    	hql.append(" select sum(chDedicadaPeriodo) from DocenteTurma dt ");
    	hql.append(" where getidpessoa("+servidor.getId()+") = dt.docente.pessoa.id ");
//    	hql.append(" where dt.docente.id = " + servidor.getId() );
    	hql.append(" and dt.turma.disciplina.nivel = '"+NivelEnsino.GRADUACAO+"'");
    	hql.append(" and  (dt.turma.ano >= "+ (ano - validade + 1)+" and dt.turma.ano <= "+ano+") ");

    	Long chTotal = (Long) getSession().createQuery(hql.toString()).uniqueResult();
    	if (chTotal != null) {
    		CargaHorariaEnsino cargaEnsino = new CargaHorariaEnsino();
    		cargaEnsino.setCargaHoraria(chTotal);
    		cargas.add(cargaEnsino);
    	}
    	return cargas;
    }
    
    /**
     * Consulta que traz a quantidade de componentes curriculares ministrados por um docente
     * na pós-graduação stricto sensu, num determinado ano.
     * 
     * @param servidor
     * @param ano
     * @param validade
     * @return
     * @throws DAOException
     */
    public Collection<ComponentesEnsinoPosStricto> findComponentesEnsinoPosStricto(Servidor servidor, int ano, Integer validade) throws DAOException {
    	Collection<ComponentesEnsinoPosStricto> comps = new ArrayList<ComponentesEnsinoPosStricto>();
    	
    	if(validade == null)
    		validade = 1;
    	
    	StringBuilder hql = new StringBuilder();
    	hql.append(" select count(*) from DocenteTurma dt ");
    	hql.append(" where getidpessoa("+servidor.getId()+")  = dt.docente.pessoa.id ");
//    	hql.append(" where dt.docente.id = " + servidor.getId() );
    	hql.append(" and dt.turma.disciplina.nivel = '"+NivelEnsino.STRICTO+"'");
    	hql.append(" and  (dt.turma.ano >= "+ (ano - validade + 1)+" and dt.turma.ano <= "+ano+") ");
    	
    	Long quant = (Long) getSession().createQuery(hql.toString()).uniqueResult();
    	if(quant != null && quant > 0){
    		ComponentesEnsinoPosStricto quantComp = new ComponentesEnsinoPosStricto();
    		quantComp.setNumeroComponentes(quant);
    		comps.add(quantComp);
    	}
    	return comps;
    }

    /**
     * Busca as participações em comitês científicos.
     *
     * @param servidor
     * @param ano
     * @param validade
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public Collection<ParticipacaoColegiadoComissao> findParticipacaoComiteCientifico(Servidor servidor, int ano, Integer validade) throws DAOException {
        try {
            StringBuilder where  = new StringBuilder();
            where.append(" where o.servidor.id = " + servidor.getId() );
            where.append(" and ( (o.periodoFim >= :dataInicio and o.periodoFim < :dataFim) " +
            		" or ( (o.periodoInicio < :dataFim ) and ( o.periodoFim is null or o.periodoFim > :dataFim ) ) )");
            where.append(" and o.tipoComissaoColegiado.id = " + TipoComissaoColegiado.COMITE_CIENTIFICO);

            Query q = createQuery(ParticipacaoColegiadoComissao.class, where.toString(), ano, validade);
            return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    public Date findUltimaProgressao(Servidor servidor) throws DAOException {
    	
    	try {
			return (Date) getJdbcTemplate(new Sistema(Sistema.SIGRH))
					.queryForObject("select data_de_vigencia "
									+ "from funcional.progressao "
									+ "where id_servidor = ? "
									+ "order by data_de_vigencia desc "
									+ BDUtils.limit(1),
							new Object[] { servidor.getId() }, Date.class);
		} catch (DataAccessException e) {
			return null;
		}
    }

	public int getAnoInicio() {
		return anoInicio;
	}

	public void setAnoInicio(int anoInicio) {
		this.anoInicio = anoInicio;
	}
	
	/**
	 * Busca relatórios de projetos dos quais os docente seja coordenador ou colaborador.
	 * 
	 * @param docente
	 * @param anoVigencia
	 * @param validade
	 * @param isColaborador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjeto> findProjetosAntigosRelatorioDocente(
            Servidor docente, Integer anoVigencia, Integer validade, boolean isColaborador) throws DAOException {
    	
		try {
	      	StringBuilder sqlQuery = new StringBuilder();
	      	sqlQuery.append("SELECT pp.cod_prefixo, pp.cod_numero, pp.cod_ano, p.titulo, ");
	      	sqlQuery.append(" p.data_fim FROM pesquisa.projeto_pesquisa pp ");
	      	sqlQuery.append(" INNER JOIN projetos.projeto p ON(p.id_projeto=pp.id_projeto)");
	      	if (isColaborador)
	      		sqlQuery.append(" INNER JOIN projetos.membro_projeto mp ON(mp.id_projeto=p.id_projeto)");
	      	sqlQuery.append(" WHERE pp.codmerg IS NOT null AND ");
	      	if (isColaborador) {
	      		sqlQuery.append(" mp.id_funcao_membro != ");
	      		sqlQuery.append(FuncaoMembro.COORDENADOR);
	      		sqlQuery.append(" AND mp.id_servidor"); 
	      	} else
	      		sqlQuery.append(" pp.id_coordenador");
	      	
	      	sqlQuery.append(" = :idDocente");
	      	sqlQuery.append(" AND (pp.cod_ano >= :anoInicio AND pp.cod_ano <= :anoFim)");

	      	Query query = getSession().createSQLQuery(sqlQuery.toString());
	      	query.setInteger("idDocente", docente.getId());
	      	query.setInteger("anoInicio", anoVigencia - validade);
	      	query.setInteger("anoFim", anoVigencia);
	        
	      	List<Object[]> resul = query.list();
	        Collection<RelatorioProjeto> relatorios = new ArrayList<RelatorioProjeto>(resul.size());
	        for (int i = 0; i < resul.size(); i++) {
	        	RelatorioProjeto relatorio = new RelatorioProjeto();
	        	int cont = 0;
	        	Object[] colunas = resul.get(i);
	        	ProjetoPesquisa projeto = new ProjetoPesquisa();
	        	projeto.setCodigo(new CodigoProjetoPesquisa((String) colunas[cont++], 
	        			(Integer) colunas[cont++], (Short) colunas[cont++]));
	        	projeto.setTitulo((String) colunas[cont++]);
	        	relatorio.setDataEnvio((Date) colunas[cont]);
	        	relatorio.setProjetoPesquisa(projeto);
	        	relatorios.add(relatorio);
	        }
	        return relatorios;
    	} catch (Exception e) {
	          throw new DAOException(e.getMessage(), e);
    	}
    }
      
}
