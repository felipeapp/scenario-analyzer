/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/03/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.aquisicao.dominio.CampoOrdenacaoConsultaAssinatura;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroMovimentacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 *    Dao para consulta de assinaturas de peri�dicos das bibliotecas..
 *
 * @author Jadson
 * @since 24/03/2009
 * @version 1.0 Cria��o da classe
 *
 */
public class AssinaturaDao extends GenericSigaaDAO{

	/** Limite de resultados das buscas. */
	public static final Integer LIMITE_RESULTADOS = 300;
	
	// Tipo de par�metros para consulta de assinaturas, se precisar passar um novo tipo de par�metro criar aqui.
	
	/** Par�metro do tipo data. */
	public static final int PARAMETRO_DATA = 1;
	/** Par�metro do tipo inteiro. */
	public static final int PARAMETRO_INTEIRO = 2;
	/** Par�metro do tipo lista de inteiros. */
	public static final int PARAMETRO_LISTA_INTEIROS = 3;
	
	
	/**
	 *  Encontra a assinatura ativas do fasc�culo
	 */
	public Assinatura findAssinaturaDoFasciculo(int idFasciculo) throws  DAOException {
		
		String hqlAssinatura = " SELECT f.assinatura.id, f.assinatura.titulo, f.assinatura.codigo FROM Fasciculo f where f.id = :idFasciculo ";

		Query q = getSession().createQuery(hqlAssinatura);
		q.setInteger("idFasciculo", idFasciculo);
		
		Object[] dadosAsssinatura = (Object[]) q.uniqueResult();
		Assinatura retorno = new Assinatura((Integer)dadosAsssinatura[0], (String)dadosAsssinatura[1],  (String)dadosAsssinatura[2]);
		return retorno;
	}
	
	
	
	/**
	 *  Encontra todas as assinaturas ativas de um determinado t�tulo.
	 */
	public Long countAssinaturasAtivasByTitulo(int idTitulo) throws  DAOException {
		
		StringBuilder hql = new StringBuilder(" SELECT COUNT(DISTINCT a.id) ");
		hql.append(" FROM Assinatura a ");
		hql.append(" WHERE a.tituloCatalografico.id = :idTitulo AND a.ativa = trueValue() ");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idTitulo", idTitulo);
		
		return (Long) q.uniqueResult();
	}
	
	
	/**
	 *  Encontra todas as assinaturas ativas de um determinado t�tulo das bibliotecas passadas
	 */
	public List<Assinatura> findAssinaturasAtivasByTituloByBibliotecas(int idTitulo,  List<Integer> idsBibliotecas) throws  DAOException {
		
		StringBuilder hql = new StringBuilder(" SELECT a ");
		hql.append(" FROM Assinatura a ");
		hql.append(" WHERE a.tituloCatalografico.id = :idTitulo AND a.ativa = trueValue() ");
		if(idsBibliotecas != null && idsBibliotecas.size() > 0)
			hql.append(" AND a.unidadeDestino.id in ( :idBibliotecas )");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idTitulo", idTitulo);
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0)
			q.setParameterList("idBibliotecas", idsBibliotecas);
		
		@SuppressWarnings("unchecked")
		List<Assinatura> lista = q.list();
		return lista;
	}
	
	/**
	 *    Retorna as assinaturas que possuem fasc�culos pendentes de transfer�ncia de acordo com a
	 *    biblioteca destino da transfer�ncia escolhida.
	 * 
	 * @return as assinaturas com fasc�culos pendentes de transfer�ncia.
	 */
	public List<Assinatura> encontraAssinaturasCujosFasciculosEstaoPendentesTransferencia(int idBiblioteca) throws  DAOException {
	
		StringBuilder hql = new StringBuilder(" SELECT distinct reg.assinturaDestino ");
		hql.append(" FROM RegistroMovimentacaoMaterialInformacional reg ");
		hql.append(" WHERE reg.pendente =  trueValue() AND reg.bibliotecaDestino.id = :idBiblioteca ");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idBiblioteca", idBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Assinatura> list = q.list();
		return list;
	}
	
	
	/**
	 *    Retorna os registros de movimenta��o de fasc�culos feitos para um biblioteca espec�fica que n�o possu�a
	 * uma assinatura para os fasc�culos.
	 * 
	 * @return as assinaturas com fasc�culos pendentes de transfer�ncia.
	 */
	public List<Object> encontraDadosMovimentacaoSemAssinaturaFasciculos(int idBiblioteca) throws  DAOException {
	
		StringBuilder hql = new StringBuilder(" SELECT reg.id, m.id, m.codigoBarras, m.anoCronologico, m.ano, m.volume, m.numero, m.edicao, reg.assinaturaOrigem.tituloCatalografico.id, reg.usuarioMovimentouMaterial.pessoa.nome ");
		hql.append(" FROM RegistroMovimentacaoMaterialInformacional reg ");
		hql.append(" INNER JOIN reg.material m ");
		hql.append(" WHERE reg.pendente =  trueValue() AND reg.assinturaDestino is null "
				+" AND reg.assinaturaOrigem is not null AND reg.bibliotecaDestino.id = :idBiblioteca ");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idBiblioteca", idBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Object> list = q.list();
		return list;
	}
	
	/**
	 *    Retorna os registros de movimenta��o de fasc�culos feitos para um biblioteca espec�fica que n�o possu�a
	 * uma assinatura para os fasc�culos do mesmo t�tulo.
	 * 
	 * @return as assinaturas com fasc�culos pendentes de transfer�ncia.
	 */
	public List<RegistroMovimentacaoMaterialInformacional> encontraRegistrosMovimentacaoFasciculosSemAssinatura(int idBibliotecaDestino, int idTituloFasciculos) throws  DAOException {
	
		StringBuilder hql = new StringBuilder(" SELECT reg ");
		hql.append(" FROM RegistroMovimentacaoMaterialInformacional reg ");
		hql.append(" INNER JOIN reg.material m ");
		hql.append(" WHERE reg.pendente =  trueValue() AND reg.assinturaDestino is null "
				+" AND reg.assinaturaOrigem is not null AND reg.bibliotecaDestino.id = :idBiblioteca "
				+" AND m.assinatura.tituloCatalografico.id = :idTituloFasciculo");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idBiblioteca", idBibliotecaDestino);
		q.setInteger("idTituloFasciculo", idTituloFasciculos);
		
		@SuppressWarnings("unchecked")
		List<RegistroMovimentacaoMaterialInformacional> list = q.list();
		return list;
	}
	
	/**
	 *    Retorna o nome do usu�rio que criou a assinatura.
	 */
	public String findNomeCriadorAssinatura(int idAssinatura) throws  DAOException {
		
		StringBuilder hql = new StringBuilder(" SELECT a.registroCriacao.usuario.pessoa.nome ");
		hql.append(" FROM Assinatura a ");
		hql.append(" WHERE a.id = :idAssinatura ");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idAssinatura", idAssinatura);
		return (String) q.uniqueResult();
	}
	
	/**
	 *    Retorna todas as assinaturas poss�veis de inclus�o de fasc�culo para o t�tulo passado,
	 *  ou seja, as assinaturas que contenham esse t�tulo ou as que n�o possuam t�tulo ainda.
	 */
	
	public List<Assinatura> findAssinaturasAtivasPossiveisInclusaoFasciculos(int idTitulo, String restricaoBusca,
			String nomeParametroRestricao, Object valorPatrametroRestricao, final Integer  tipoPatrametroRestricao) throws  DAOException {
		
		String projecao = " assinatura.id_assinatura as idAssinatura, assinatura.codigo as codigo, assinatura.titulo as tituloAssinatura, assinatura.internacional, assinatura.modalidade_aquisicao, assinatura.id_titulo_catalografico as idTituloCatalografico, " +
				" biblioteca.id_biblioteca as idBiblioteca, biblioteca.descricao as biblioteca, " +
				" registro.id_usuario as id_usuario, pessoa.id_pessoa as idPessoa, pessoa.nome as nomePessoa, "+ 
		"  ( SELECT count(distinct fasciculos.id_fasciculo) "+ 
		"    FROM biblioteca.fasciculo fasciculos "+
		"    INNER JOIN biblioteca.material_informacional m ON m.id_material_informacional = fasciculos.id_fasciculo "+
		"    WHERE assinatura.id_assinatura=fasciculos.id_assinatura and fasciculos.incluido_acervo = :false and m.ativo = :true) as quantidadeFasciculos ";
		
		String sql = " SELECT "+projecao+
			" FROM        biblioteca.assinatura assinatura " +
			" INNER JOIN  biblioteca.biblioteca biblioteca     ON assinatura.id_biblioteca = biblioteca.id_biblioteca " +
			" LEFT JOIN  comum.registro_entrada registro      ON assinatura.id_registro_criacao = registro.ID_ENTRADA " +
			" LEFT JOIN  comum.usuario usuario                ON registro.ID_USUARIO=usuario.id_usuario " +
			" LEFT JOIN  comum.pessoa pessoa                  ON usuario.id_pessoa=pessoa.id_pessoa "+ 
			" WHERE assinatura.ativa = :true AND ( assinatura.id_titulo_catalografico = :idTitulo OR assinatura.id_titulo_catalografico is null) ";
		
		if(StringUtils.notEmpty(restricaoBusca)) 
			sql += restricaoBusca;
		
		sql += "ORDER BY quantidadeFasciculos DESC, biblioteca, tituloAssinatura";
		
		Query q = getSession().createSQLQuery( sql );
		q.setInteger("idTitulo", idTitulo);
		q.setBoolean("true", true);
		q.setBoolean("false", false);
		
		// A RESTRI��O PASSADA DE OUTROS M�TODO PARA SER ADICIONADA A CONSULTA POSSUI PAR�METROS
		if(nomeParametroRestricao != null && valorPatrametroRestricao != null && tipoPatrametroRestricao != null){
			
			switch (tipoPatrametroRestricao) {
			case PARAMETRO_INTEIRO :
				q.setInteger(nomeParametroRestricao, (Integer) valorPatrametroRestricao );
				break;
			case PARAMETRO_LISTA_INTEIROS :
				
				@SuppressWarnings("unchecked")
				List<Integer> parametrosInteiros = (List<Integer>)  valorPatrametroRestricao;
				q.setParameterList(nomeParametroRestricao, parametrosInteiros);
				break;
			}
			
		}
		
		List<Assinatura> lista = new ArrayList<Assinatura>() ;
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados =  q.list();
		
		for (Object[] objects : dados) {
			Assinatura  a = new Assinatura();
			a.setId( (Integer) objects[0] );
			a.setCodigo( (String) objects[1] );
			a.setTitulo( (String) objects[2] );
			a.setInternacional( (Boolean) objects[3] );
			a.setModalidadeAquisicao( (Short) objects[4] );
			
			// se est� associada a um t�tulo catalografico
			if(objects[5] != null)
				a.setTituloCatalografico( new TituloCatalografico( (Integer) objects[5] ) );
			
			a.setUnidadeDestino( new Biblioteca((Integer) objects[6], (String) objects[7] ) );
			
			if(objects[8] != null){
				a.setRegistroCriacao(new RegistroEntrada());
				a.getRegistroCriacao().setUsuario(new UsuarioGeral((Integer) objects[8]));
				a.getRegistroCriacao().getUsuario().setPessoa( new Pessoa((Integer) objects[9], (String) objects[10]) );
			}
			
			a.setQuantidadeFasciulos( objects[11] != null ? ( (BigInteger) objects[11]).intValue() : 0);
			
			lista.add(a);
		}
		
		
		return lista;
	}
	
	/**
	 * 
	 *  Retorna todas as assinaturas poss�veis de inclus�o de fasc�culo para o t�tulo passado,
	 *  ou seja, as assinaturas que contenham esse t�tulo ou as que n�o possuam t�tulo ainda, e seja
	 *  assinatura da unidade de destino passada, porque o usu�rio s� vai ter permiss�o de incluir
	 *  fasc�culo nas assinaturas da unidade dele.
	 */
	public List<Assinatura> findAssinaturasPossiveisInclusaoFasciculosByUnidadeDestino(int idTitulo, List<Integer> idsBiblioteca) throws  DAOException {
		
		return findAssinaturasAtivasPossiveisInclusaoFasciculos(idTitulo,  " AND biblioteca.id_biblioteca in (:idsBiblioteca) ",
				"idsBiblioteca", idsBiblioteca, PARAMETRO_LISTA_INTEIROS);
	}
	
	/**
	 *  <p>Retorna todas as assinaturas poss�veis para a transfer�ncia dos fasc�culos para uma
	 *  biblioteca espec�fica.</p>
	 *  <p>Traz todas as assinaturas que possuam o mesmo T�tulo da assinatura original dos fasc�culos,
	 *  porque n�o d� para transferir fasc�culos de T�tulos diferentes, por exemplo, fasc�culos da
	 *  "Veja" para "Isto�". </p>
	 */
	
	public List<Assinatura> findAssinaturasPossiveisTransferenciaFasciculosByUnidadeDestino(int idTitulo, int idBiblioteca) throws  DAOException {
		
		StringBuilder hql = new StringBuilder(" SELECT a ");
		hql.append(" FROM Assinatura a ");
		hql.append(" WHERE a.tituloCatalografico.id = :idTitulo  AND a.unidadeDestino.id = :idBiblioteca AND a.ativa = trueValue() ");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idTitulo", idTitulo);
		q.setInteger("idBiblioteca", idBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Assinatura> lista = q.list();
		return lista;
	}
	
	
	/**
	 * Recupera os dados Principais da assunatura passada
	 *
	 * @return uma lista de assinaturas encontradas de acordo com os par�metro passados
	 * @throws DAOException
	 */
	public Assinatura findInformacoesPrincipaisAssinaturaById(int idAssinatura) throws DAOException{
	
		String projecao = " a.id, a.titulo, a.codigo, a.dataInicioAssinatura, a.dataTerminoAssinatura, " 
		+" a.unidadeDestino.id, a.unidadeDestino.descricao, frequenciaPeriodicos.id, frequenciaPeriodicos.descricao, " 
		+" a.internacional, a.modalidadeAquisicao ";
		
		StringBuilder hqlSelect = new StringBuilder(" SELECT   "+projecao+" FROM Assinatura a ");
		
		hqlSelect.append(" LEFT JOIN a.frequenciaPeriodicos frequenciaPeriodicos ");
		
		StringBuilder hqlComum = new StringBuilder();
		
		hqlComum.append(" WHERE a.ativa = trueValue() AND a.id = :idAssinatura ");
		
		
		Query q = getSession().createQuery( hqlSelect.append(hqlComum).toString());
		q.setInteger("idAssinatura", idAssinatura);
		q.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return  new ArrayList<Assinatura>(HibernateUtils.parseTo(lista, projecao, Assinatura.class, "a")).get(0);
		
	}
	
	
	
	/**
	 * 
	 * Busca as assinaturas de acordo com o exemplo de assinatura passado.
	 *
	 * @param assinatura os dados da assinatura para a busca
	 * @param entidadesMarc campos que n�o est�o localizados na classe Assinatura, mas que est�o em cache
	 * @param restricaoConsulta se deve ser adicionada alguma restri��o da pesquisa
	 * @param nomeParametroRestricao caso a pesquisa tenha um par�metro na restri��o, indicar aqui o nome do par�metro
	 * @param valorParametroRestricao caso a pesquisa tenha um par�metro na restri��o, indicar aqui o valor do par�metro
	 * @param tipoPatrametroRestricao caso a pesquisa tenha um par�metro na restri��o, indicar aqui o tipo do par�metro
	 * 
	 * @return uma lista de assinaturas encontradas de acordo com os par�metro passados
	 * @throws DAOException
	 */
	public List<Assinatura> findAssinaturasAtivasByExemplo(
			Assinatura assinatura, String restricaoConsulta, String nomeParametroRestricao,
			Object valorParametroRestricao, final Integer tipoPatrametroRestricao, final CampoOrdenacaoConsultaAssinatura campoOrdenacao) throws DAOException{
	
		String projecao = " a.id, a.titulo, a.codigo, a.dataInicioAssinatura, a.dataTerminoAssinatura, " 
		+" a.unidadeDestino.id, a.unidadeDestino.descricao, frequenciaPeriodicos.id, frequenciaPeriodicos.descricao, " 
		+" a.internacional, a.modalidadeAquisicao ";
		
		StringBuilder hqlSelect = new StringBuilder(" SELECT   "+projecao+" FROM Assinatura a ");
		
		hqlSelect.append(" LEFT JOIN a.frequenciaPeriodicos frequenciaPeriodicos ");
		
		StringBuilder hqlComum = new StringBuilder();
		
		hqlComum.append(" WHERE 1 = 1 AND a.ativa = trueValue() ");
		
		if (StringUtils.notEmpty(restricaoConsulta))  hqlComum.append(restricaoConsulta);
		
		if(StringUtils.notEmpty(assinatura.getTitulo())){
			
			String[] titulosBusca = BibliotecaUtil.retornaPalavrasBusca(assinatura.getTitulo());
			
			for (int ptr = 0; ptr < titulosBusca.length; ptr++) {
				hqlComum.append(" AND (  a.tituloAscii  like :titulo"+ptr+" ) ");
			}
		}
		
		if( StringUtils.notEmpty( assinatura.getCodigo()) )
			hqlComum.append(" AND  a.codigo = :codigo ");
		
		if( assinatura.getUnidadeDestino().getId() != -1  )
			hqlComum.append(" AND a.unidadeDestino.id  =  :idUnidadeDestino  ");
		
		if( assinatura.getModalidadeAquisicao() != -1  )
			hqlComum.append(" AND a.modalidadeAquisicao  =  :idModalidadeAquisicao ");
		
		if( assinatura.getFrequenciaPeriodicos() != null &&assinatura.getFrequenciaPeriodicos().getId() != -1  )
			hqlComum.append(" AND frequenciaPeriodicos  =  :idFrequenciaPeriodicos  ");
		
		if( assinatura.getInternacional() != null )
			hqlComum.append(" AND a.internacional =  :internacional  ");
		
		
		if(assinatura.getDataInicioAssinatura() != null){
			hqlComum.append(" AND ( a.dataInicioAssinatura >= :dataInicioAssinatura ) ");
		}
		
		if(assinatura.getDataTerminoAssinatura() != null){
			hqlComum.append(" AND ( a.dataTerminoAssinatura <= :dataTerminoAssinatura ) ");
		}
		
		if ( StringUtils.notEmpty(assinatura.getIssn()) ) {				
			// Aqui os caracteres s�o substitu�dos e s� restam os n�meros, pois a forma de guardar
			// o ISSN n�o foi padronizada.
			// A fun��o translate � usada porque ela � do padr�o SQL.
			// Note que o 'X' tamb�m pode fazer parte do ISSN
			
			hqlComum.append(
					"	AND " +
					"		translate(upper(a.issn), ' ABCDEFGHIJKLMNOPQRSTUVWYZ�����������������������.-,;:?/''!\"@#$%&*()[]{}', '') = " +
					"		translate(upper(:issn),  ' ABCDEFGHIJKLMNOPQRSTUVWYZ�����������������������.-,;:?/''!\"@#$%&*()[]{}', '') " +
					"	AND a.issn IS NOT NULL " +
					"	AND char_length(translate(upper(a.issn), ' ABCDEFGHIJKLMNOPQRSTUVWYZ�����������������������.-,;:?/''!\\\"@#$%&*()[]{}', '')) > 0 " );
		}
		
		// Ordena os resultados sempre pela unidades destino da assinatura e por um segundo campo escolhido pelo usu�rio.
		hqlComum.append(" ORDER BY a.unidadeDestino.descricao "+ ( campoOrdenacao != null ? ", "+campoOrdenacao.getCampoOrdenacao() : " " ));
		
		Query q = getSession().createQuery( hqlSelect.append(hqlComum).toString());
		
		if(StringUtils.notEmpty(assinatura.getTitulo())){
			
			String[] titulosBusca = BibliotecaUtil.retornaPalavrasBusca(assinatura.getTitulo());
			
			for (int ptr = 0; ptr < titulosBusca.length; ptr++) {
				q.setString("titulo"+ptr, "% "+ StringUtils.toAsciiAndUpperCase(titulosBusca[ptr])+" %");
			}
		}
		
		if(StringUtils.notEmpty(assinatura.getCodigo()))
			q.setString("codigo", assinatura.getCodigo());
	
		if( assinatura.getUnidadeDestino().getId() != -1  )
			q.setInteger("idUnidadeDestino", assinatura.getUnidadeDestino().getId() );
		
		if( assinatura.getModalidadeAquisicao() != -1  )
			q.setInteger("idModalidadeAquisicao", assinatura.getModalidadeAquisicao() );
		
		if( assinatura.getFrequenciaPeriodicos() != null && assinatura.getFrequenciaPeriodicos().getId() != -1  )
			q.setInteger("idFrequenciaPeriodicos", assinatura.getFrequenciaPeriodicos().getId() );
		
		if( assinatura.getInternacional() != null )
			q.setBoolean("internacional", assinatura.getInternacional() );
		
		if(assinatura.getDataInicioAssinatura() != null)
			q.setDate("dataInicioAssinatura", assinatura.getDataInicioAssinatura() );
			
		if(assinatura.getDataTerminoAssinatura() != null)
			q.setDate("dataTerminoAssinatura", assinatura.getDataTerminoAssinatura() );
			
		if(StringUtils.notEmpty(assinatura.getIssn()) )
			q.setString("issn", assinatura.getIssn() );
		
		
		// A RESTRI��O PASSADA DE OUTROS M�TODO PARA SER ADICIONADA A CONSULTA POSSUI PAR�METROS
		if(nomeParametroRestricao != null && valorParametroRestricao != null && tipoPatrametroRestricao != null){
			
			switch (tipoPatrametroRestricao) {
			case PARAMETRO_DATA:
				q.setDate(nomeParametroRestricao, (Date) valorParametroRestricao );
				break;
			case PARAMETRO_LISTA_INTEIROS:
				@SuppressWarnings("unchecked")
				List<Integer> inteiros = (List<Integer> )valorParametroRestricao;
				q.setParameterList(nomeParametroRestricao, inteiros);
				break;
			/* NOVOS PAR�METRO CRIAR AQUI */
			//case PARAMETRO_INTERIO:
			//	q.setDate(nomeParametroRestricao, (Date) valorPatrametroRestricao );
			//	break;
			}
			
		}
		
		q.setMaxResults(LIMITE_RESULTADOS);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return  new ArrayList<Assinatura>(HibernateUtils.parseTo(lista, projecao, Assinatura.class, "a"));
		
	}
	
	/**
	 *     Busca as assinaturas de acordo com o exemplo de assinaturas passado para a unidade do usu�rio.
	 *     S�o as assinaturas que o usu�rio tem permiss�o para alterar, se ele n�o for administrador geral.
	 */
	public List<Assinatura> findAssinaturasAtivasByExemploByBibliotecas(Assinatura assinatura, List<Integer> idsBiblioteca
			, CampoOrdenacaoConsultaAssinatura campoOrdenacao) throws DAOException{
		return findAssinaturasAtivasByExemplo(assinatura," AND a.unidadeDestino.id in (:idsBiblioteca) ", 
				"idsBiblioteca", idsBiblioteca, PARAMETRO_LISTA_INTEIROS, campoOrdenacao);
	}
	
	/**
	 *     Busca as assinaturas de acordo com o exemplo de assinatura passado que n�o possuem
	 *   T�tulos associados a elas ainda.
	 */
	public List<Assinatura> findAssinaturaAtivasByExemploNaoAssociadasATitulo(Assinatura assinatura, CampoOrdenacaoConsultaAssinatura campoOrdenacao ) throws DAOException{
		
		return findAssinaturasAtivasByExemplo(assinatura, " AND a.tituloCatalografico is null ", null, null, null, campoOrdenacao);
	}
	
	
	/**
	 *     Busca as assinaturas de acordo com o exemplo de assinatura passado que n�o possuem
	 *   T�tulos associados a elas ainda e s�o da biblioteca passada.
	 */
	public List<Assinatura> findAssinaturaAtivasByExemploNaoAssociadasATituloByBibliotecas(
			Assinatura assinatura, List<Integer> idsBiblioteca, CampoOrdenacaoConsultaAssinatura campoOrdenacao) throws DAOException{
		
		return findAssinaturasAtivasByExemplo(assinatura, " AND a.unidadeDestino.id in (:idsBiblioteca) AND a.tituloCatalografico is null ",
				"idsBiblioteca", idsBiblioteca, PARAMETRO_LISTA_INTEIROS, campoOrdenacao);
	}
	
	
	/**
	 *    Conta as assinaturas criadas com os c�digos passados
	 */
	public long countAssinaturaPorCodigo(List<String> codigos) throws DAOException{
		
		String hql =" SELECT count(assinatura.id) FROM Assinatura assinatura "
			+" WHERE assinatura.codigo in (:codigos) ";
		
		Query q = getSession().createQuery(hql);
		q.setParameterList("codigos", codigos);
		return (Long) q.uniqueResult();
	}
	
	/**
	 *    Conta as assinaturas criadas com o c�digo passado
	 */
	public long countAssinaturaPorCodigo(String codigo) throws DAOException{
		
		List<String> codigos = new ArrayList<String>();
		codigos.add(codigo);
		
		return countAssinaturaPorCodigo(codigos);
		
	}
	
	
	

	/**
	 * <p>Atualiza a assinatura do fasc�culo passado </p>
	 * 
	 * <p> <strong> Esse m�todo existe principalmente porque n�o d� para utilizar o m�todo <code>updateField</code> com a classe abstratra MaterialInformacional </strong> </p>
	 * 
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws HibernateException 
	 */
	public void mudarFasciculoDeAssinatura(int idFasciculo, int idAssinatura, int idBiblioteca) throws DAOException {
		Query q1 = getSession().createSQLQuery("UPDATE biblioteca.fasciculo SET id_assinatura = :idAssinatura  WHERE id_fasciculo = :idFasciculo ");
		q1.setInteger("idFasciculo", idFasciculo);
		q1.setInteger("idAssinatura", idAssinatura);
		
		Query q2 = getSession().createSQLQuery("UPDATE biblioteca.material_informacional SET id_biblioteca = :idBiblioteca  WHERE id_material_informacional  = :idFasciculo ");
		q2.setInteger("idFasciculo", idFasciculo);
		q2.setInteger("idBiblioteca", idBiblioteca);
		
		if (q1.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao atualizar a assinatura do fasc�culos.");
		
		if (q2.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao atualizar a biblioteca do fasc�culos.");
	}
	
	
}
