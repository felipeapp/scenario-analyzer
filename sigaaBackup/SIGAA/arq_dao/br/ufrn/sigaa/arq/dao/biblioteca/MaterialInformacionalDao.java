/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/05/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.HistoricoAlteracaoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 * DAO para as pesquisas que retornam um material catalográfico.
 *
 * @author Fred_Castro
 * @since 30/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class MaterialInformacionalDao extends GenericSigaaDAO {
	
	
	
	/**
	 *   <p>Método recupera o nome e o email do usuário que está com o empréstimos ativo do material.</p>
	 * 
	 * @param idMaterial o id do material que está em posse do usuário.
	 * @return um arrays onde: [0] = nome o usuário <br/>
	 *                         [1] = email do usuário <br/> 
	 *         Caso a pessoa tenha mais de um usuário, vai retorna uma lista de arrays [nome o usuário, email do usuário] para cada usuário.                
	 *                       
	 * @throws DAOException
	 */
	public List<Object> findInformacoesDoUsuarioDoEmprestimoMaterial(int idMaterial) throws DAOException {
		
		String sql = " select p.nome as nome_usuario, u.email as email_usuario"
			+" from biblioteca.emprestimo emp "
			+" inner join biblioteca.usuario_biblioteca ub on emp.id_usuario_biblioteca = ub.id_usuario_biblioteca "
			+" inner join comum.pessoa p on (p.id_pessoa = ub.id_pessoa) "
			+" inner join comum.usuario u on (p.id_pessoa = u.id_pessoa) "
			+" inner join biblioteca.material_informacional m on emp.id_material = m.id_material_informacional "
			+" WHERE emp.ativo = trueValue() AND emp.data_devolucao is null AND emp.data_estorno is null"
			+" AND m.id_material_informacional = "+idMaterial;
		
		Query q = getSession().createSQLQuery(sql);
		
		@SuppressWarnings("unchecked")
		List<Object> temp = q.list();
		
		return temp;
	}
	
	
	
	/**
	 *   <p>Método recupera o id do título do material passado.</p>
	 * 
	 * @param idMaterial o id do material
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdTituloMaterial(int idMaterial) throws DAOException {
		
		String sql = " SELECT  t.id_titulo_catalografico FROM biblioteca.exemplar e "
			+" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = e.id_exemplar "
			+" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = e.id_titulo_catalografico "
			+" WHERE e.id_exemplar = "+idMaterial;
	
		Query q = getSession().createSQLQuery(sql);
		Integer temp = (Integer) q.uniqueResult();
		
		if(temp == null){  // Não é exemplar
		
			sql = " SELECT t.id_titulo_catalografico FROM biblioteca.fasciculo f "
				+" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = f.id_fasciculo"
				+" INNER JOIN biblioteca.assinatura a on f.id_assinatura = a.id_assinatura "
				+" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = a.id_titulo_catalografico "
				+" WHERE f.id_fasciculo = "+idMaterial;
		
			q = getSession().createSQLQuery(sql);
			temp =  (Integer) q.uniqueResult();
		}
		
		return temp;
	}
	
	
	
	
	/**
	 *      Método que retorna o id e a descrição da biblioteca do material passado.
	 */
	public Biblioteca findBibliotecaDoMaterial(int idMaterial) throws DAOException {
		
		
		String projecao = " m.biblioteca.id, m.biblioteca.descricao ";
		
		String hql = " SELECT "+projecao+" FROM MaterialInformacional m WHERE m.id = :idMaterial ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
		
		Object[] dados = (Object[]) q.uniqueResult();
		
		Biblioteca b = new Biblioteca((Integer)dados[0], (String) dados[1]);
		
		return b;
	}
	
	
	
	/**
	 *   <p>Método recupera o ids dos títulos dos materiais passados.</p>
	 * 
	 * @param idMaterial o id do material
	 * @return
	 * @throws DAOException
	 */
	
	public List<Integer> findIdsTitulosMateriais(List<Integer> idsMateriais) throws DAOException {
		
		String sql = " SELECT  DISTINCT t.id_titulo_catalografico FROM biblioteca.exemplar e "
			+" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = e.id_exemplar "
			+" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = e.id_titulo_catalografico "
			+" WHERE e.id_exemplar in "+UFRNUtils.gerarStringIn(idsMateriais);
	
		Query q = getSession().createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Integer> temp =  q.list();
		
		if(temp == null){  // Não é exemplar
		
			sql = " SELECT DISTINCT t.id_titulo_catalografico FROM biblioteca.fasciculo f "
				+" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = f.id_fasciculo"
				+" INNER JOIN biblioteca.assinatura a on f.id_assinatura = a.id_assinatura "
				+" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = a.id_titulo_catalografico "
				+" WHERE f.id_fasciculo = "+UFRNUtils.gerarStringIn(idsMateriais);
		
			q = getSession().createSQLQuery(sql);
			temp =  q.list();
		}
		
		return temp;
	}
	
	/**
	 *  Retorna a informações sobre a baixa de um material, por exemplo o motivo da baixa, quem baixou, etc..
	 *
	 * @param idMaterial
	 * @return
	 * @throws DAOException
	 */
	public Object[] findInformacoesBaixaMaterial(int idMaterial) throws DAOException {
		String sql = " SELECT m.motivo_baixa, m.informacoes_titulo_material_baixado " +
				", p.nome,  m.data_ultima_atualizacao "
				+" FROM biblioteca.material_informacional m " 
				+" INNER JOIN comum.registro_entrada r on m.id_registro_ultima_atualizacao = r.id_entrada "
				+" INNER JOIN comum.usuario u on u.id_usuario =  r.id_usuario "
				+" INNER JOIN comum.pessoa p on p.id_pessoa = u.id_pessoa "
				+" WHERE m.id_material_informacional = :idMaterial ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idMaterial", idMaterial);
		return (Object[]) q.uniqueResult();
	}
	
	
	
	
	/**
	 *   <p>Método recupera o código de barras o título e o autor do material passado.</p>
	 * 
	 * @param idMaterial o id do material
	 * @return um arrays onde: [0] = código barras do material <br/>
	 *                         [1] = título do Título do material <br/>
	 *                         [2] = autor do Título do material <br/>
	 *                         [3] = a descrição da biblioteca do material <br/>
	 * @throws DAOException
	 */
	public Object findInformacoesDoMaterial(int idMaterial) throws DAOException {
		
		String sql = " SELECT m.codigo_barras, c.titulo, c.autor, b.descricao FROM biblioteca.exemplar e "
			+" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = e.id_exemplar "
			+" INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca "
			+" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = e.id_titulo_catalografico "
			+" INNER JOIN biblioteca.cache_entidades_marc c on t.id_titulo_catalografico = c.id_titulo_catalografico "
			+" WHERE e.id_exemplar = "+idMaterial;
	
		Query q = getSession().createSQLQuery(sql);
		Object temp = q.uniqueResult();
		
		if(temp == null){  // Não é exemplar
		
			sql = " SELECT m.codigo_barras, c.titulo, c.autor, b.descricao FROM biblioteca.fasciculo f "
				+" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = f.id_fasciculo"
				+" INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca "
				+" INNER JOIN biblioteca.assinatura a on f.id_assinatura = a.id_assinatura "
				+" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = a.id_titulo_catalografico "
				+" INNER JOIN biblioteca.cache_entidades_marc c on t.id_titulo_catalografico = c.id_titulo_catalografico "
				+" WHERE f.id_fasciculo = "+idMaterial;
		
			q = getSession().createSQLQuery(sql);
			temp =  q.uniqueResult();
		}
		
		if(temp == null){ // título foi removido
			
			sql = " SELECT m.codigo_barras, m.informacoes_titulo_material_baixado, CAST( '' as text )as autor, b.descricao FROM biblioteca.material_informacional m "
				+" INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca "
				+" WHERE m.id_material_informacional = "+idMaterial;
		
			q = getSession().createSQLQuery(sql);
			temp =  q.uniqueResult();
		}
		
		return temp;
	}
	
	
	
	/**
	 *   <p>Método recupera apenas os "Ids" da biblioteca, coleção e tipo do material passado, sem trazer 
	 * as demais informações dessas classes do banco.</p>
	 * 
	 * @param idMaterial o id do material
	 * @return um arrays onde: [0] = idBiblioteca <br/>
	 *                         [1] = idColecao <br/>
	 *                         [2] = idTipoMaterial <br/>
	 * @throws DAOException
	 */
	public Object findIdsBibliotecaColecaoETipoDoMaterial(int idMaterial) throws DAOException {
		
		String hql = " SELECT m.biblioteca.id, m.colecao.id, m.tipoMaterial.id FROM MaterialInformacional m "
			+" WHERE m.id = :idMaterial  ";
	
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
		return q.uniqueResult();
	}
	
	/**
	 * Método que verifica se existem alguma pendência de transferência em biblioteca para o material passado.
	 * 
	 * @param idMaterial o id do material
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean materialEstaPendenteDeTransferenciaEntreBibliotecas(int idMaterial) throws DAOException {
		
		String hql = " SELECT count(r.id) FROM RegistroMovimentacaoMaterialInformacional r "
			+" WHERE r.pendente = trueValue() AND r.material.id = :idMaterial  ";
	
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
		Long quantidade  = (Long) q.uniqueResult();
		
		if(quantidade.compareTo(new Long(0)) > 0)
			return true;
		else
			return false;
	}
	
	
	
	
	
	/**
	 * <p>Retorna o id da unidade da biblioteca interna ativa onde o material está situado.</p>
	 * <p>Serve para auxiliar na verificação se um usuário tem permissão de alterar um material específico.</p>
	 * 
	 * @param idBiblioteca a biblioteca do material, se for nulo trás de todas das bibliotecas
	 * @param idMaterial id do material, pode ser fascículo ou exemplar
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdUnidadeDoMaterialDaBibliotecaInternaAtiva(int idMaterial) throws DAOException {
		
		String hql = " SELECT b.unidade.id FROM MaterialInformacional m "
			+" INNER JOIN  m.biblioteca b "
			+" WHERE b.ativo = trueValue() AND b.unidade is not null "
			+" AND m.id = :idMaterial ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
		
		return  (Integer) q.uniqueResult();
	}
	
	/**
	 * 
	 * Registra as alterações de um material na tabela historico_alteracao_material
	 * 
	 * @param titulo
	 * @param descricao
	 * @throws DAOException
	 */
	public void registraAlteracaoMaterial(MaterialInformacional  material, String descricaoOperacao
			, boolean atualizando) throws DAOException{
		
		RegistroEntrada registro = null;
		
		StringBuilder descricaoMaterial = new StringBuilder();
		
		if(atualizando)
			registro = material.getRegistroUltimaAtualizacao();
		else
			registro = material.getRegistroCriacao();
			
		////////////////////////////////////////////////////////////////////////////////////////
		// atualiza para pegar a descrição atual e poder salvar no histórico                  //
		// porque só tinha atualizado o id dos objetos, mas a descrição permanecia a antiga.  //
		////////////////////////////////////////////////////////////////////////////////////////
		
		if(descricaoOperacao == null){
			
			descricaoMaterial.append(
					" <strong>Código de Barras:</strong> "+material.getCodigoBarras()+" \n"
					+" <strong>Situação:</strong> "+ findByPrimaryKey( material.getSituacao().getId(), SituacaoMaterialInformacional.class, "descricao").getDescricao()+" \n"
				    +" <strong>Status:</strong> "+  findByPrimaryKey( material.getStatus().getId(), StatusMaterialInformacional.class, "descricao").getDescricao()+" \n"
				    +" <strong>Número Chamada:</strong> "+  (material.getNumeroChamada() != null ? material.getNumeroChamada(): "")+" \n" 
				    +" <strong>Segunda Localização:</strong> "+  (material.getSegundaLocalizacao() != null ? material.getSegundaLocalizacao() : "")+" \n"
				    +" <strong>Biblioteca:</strong> "+  findByPrimaryKey( material.getBiblioteca().getId(), Biblioteca.class, "descricao" ).getDescricao()+" \n" 
				    +" <strong>Coleção:</strong> "+   findByPrimaryKey( material.getColecao().getId(), Colecao.class, "descricao").getDescricao()+" \n"
				    +" <strong>Tipo Material:</strong> "+  findByPrimaryKey( material.getTipoMaterial().getId(), TipoMaterial.class, "descricao").getDescricao()+" \n" );
			
		   
	       if(material instanceof Exemplar){
				Exemplar temp = (Exemplar) material;
				if(temp.getNumeroVolume() != null)
					descricaoMaterial.append(" <strong>Número do Volume:</strong> "+temp.getNumeroVolume()+" \n");
				if(StringUtils.notEmpty(temp.getNotaConteudo()))
					descricaoMaterial.append(" <strong>Nota de Conteúdo:</strong> "+temp.getNotaConteudo()+" \n");
				if(StringUtils.notEmpty(temp.getNotaTeseDissertacao()))
					descricaoMaterial.append(" <strong>Nota de Tese e Dissertação:</strong> "+temp.getNotaTeseDissertacao()+" \n");
			}
			
			if(material instanceof Fasciculo){
				Fasciculo temp = (Fasciculo) material;
				if(temp.getAnoCronologico() != null)
					descricaoMaterial.append(" <strong>Ano Cronológico:</strong> "+temp.getAnoCronologico()+" \n");
				if(temp.getAno() != null)
					descricaoMaterial.append(" <strong>Ano:</strong> "+temp.getAno()+" \n");
				if(temp.getVolume() != null)
					descricaoMaterial.append(" <strong>Volume:</strong> "+temp.getVolume()+" \n");
				if(temp.getNumero() != null)
					descricaoMaterial.append(" <strong>Número:</strong> "+temp.getNumero()+" \n");
				if(temp.getEdicao() != null)
					descricaoMaterial.append(" <strong>Edição:</strong> "+temp.getEdicao()+" \n");
				if(StringUtils.notEmpty(temp.getDescricaoSuplemento()))
					descricaoMaterial.append(" <strong>Descrição do Suplemento:</strong> "+temp.getDescricaoSuplemento()+" \n");
			}
			
			
			if(StringUtils.notEmpty(material.getNotaGeral()))
				descricaoMaterial.append(" <strong>Nota Geral:</strong> "+material.getNotaGeral()+" \n");
			
			if(StringUtils.notEmpty(material.getNotaUsuario()))
				descricaoMaterial.append(" <strong>Nota ao Usuário:</strong> "+material.getNotaUsuario()+" \n");

			descricaoOperacao = descricaoMaterial.toString();
		}
		
		
		this.create(new HistoricoAlteracaoMaterial(material.getId(), registro, descricaoOperacao, new Date()));
		
	}
	
	
	
	
	/**
	 * 
	 *     Busca os materiais ativos por faixa de código de barras. 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List <MaterialInformacional> findMateriaisAtivosByCodigosBarras(String codigoBarrasInicial, String codigoBarrasFinal) throws DAOException {

		String hqlProjecaoAdicional = "biblioteca.unidade.id";
		String hqlInnerJoinAdicional = "INNER JOIN biblioteca.unidade unidade";
		String hqlWhere = "e.codigoBarras BETWEEN :codigoBarrasInicial AND :codigoBarrasFinal " +
				"AND e.ativo = trueValue() " +
				"AND situacao.situacaoDeBaixa = falseValue() ";
		String hql = getHQLPadraoExemplar(hqlProjecaoAdicional, hqlInnerJoinAdicional, hqlWhere);
		Query q = getSession().createQuery(hql);
		q.setString("codigoBarrasInicial", codigoBarrasInicial);
		q.setString("codigoBarrasFinal", codigoBarrasFinal);
		List<Object[]> result = q.list();
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();

		if (result != null) {
			materiais.addAll(HibernateUtils.parseTo(result, getProjecaoHQLPadraoExemplar(hqlProjecaoAdicional), Exemplar.class, "e"));
		}
		
		hqlWhere = "f.codigoBarras BETWEEN :codigoBarrasInicial AND :codigoBarrasFinal " +
				"AND f.ativo = trueValue() " +
				"AND f.incluidoAcervo = trueValue() " +
				"AND situacao.situacaoDeBaixa = falseValue() ";
		hql = getHQLPadraoFasciculo(hqlProjecaoAdicional, hqlInnerJoinAdicional, hqlWhere);
		q = getSession().createQuery(hql);
		q.setString("codigoBarrasInicial", codigoBarrasInicial);
		q.setString("codigoBarrasFinal", codigoBarrasFinal);
		
		result = q.list();

		if (result != null) {
			materiais.addAll(HibernateUtils.parseTo(result, getProjecaoHQLPadraoFasciculo(hqlProjecaoAdicional), Fasciculo.class, "f"));
		}
		
		return materiais;
		
	}



	/**
	 * 
	 *     Busca os materiais baixados por faixa de código de barras. 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MaterialInformacional> findMateriaisBaixadosByCodigosBarras(String codigoBarrasInicial, String codigoBarrasFinal) throws DAOException {

		String hqlProjecaoAdicional = "biblioteca.unidade.id, e.informacoesTituloMaterialBaixado";
		String hqlInnerJoinAdicional = "INNER JOIN biblioteca.unidade unidade";
		String hqlWhere = "e.codigoBarras BETWEEN :codigoBarrasInicial AND :codigoBarrasFinal " +
				"AND e.ativo = trueValue() " +
				"AND situacao.situacaoDeBaixa = trueValue() ";
		String hql = getHQLPadraoExemplar(hqlProjecaoAdicional, hqlInnerJoinAdicional, hqlWhere);
		Query q = getSession().createQuery(hql);
		q.setString("codigoBarrasInicial", codigoBarrasInicial);
		q.setString("codigoBarrasFinal", codigoBarrasFinal);
		List<Object[]> result = q.list();
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();

		if (result != null) {
			materiais.addAll(HibernateUtils.parseTo(result, getProjecaoHQLPadraoExemplar(hqlProjecaoAdicional), Exemplar.class, "e"));
		}
		
		hqlProjecaoAdicional = "biblioteca.unidade.id, f.informacoesTituloMaterialBaixado";
		hqlWhere = "f.codigoBarras BETWEEN :codigoBarrasInicial AND :codigoBarrasFinal " +
				"AND f.ativo = trueValue() " +
				"AND f.incluidoAcervo = trueValue() " +
				"AND situacao.situacaoDeBaixa = trueValue() ";
		hql = getHQLPadraoFasciculo(hqlProjecaoAdicional, hqlInnerJoinAdicional, hqlWhere);
		q = getSession().createQuery(hql);
		q.setString("codigoBarrasInicial", codigoBarrasInicial);
		q.setString("codigoBarrasFinal", codigoBarrasFinal);
		
		result = q.list();

		if (result != null) {
			materiais.addAll(HibernateUtils.parseTo(result, getProjecaoHQLPadraoFasciculo(hqlProjecaoAdicional), Fasciculo.class, "f"));
		}
		
		return materiais;
		
	}

	/**
	 * 
	 *     Conta os materiais ativos por faixa de código de barras. 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Integer countMateriaisAtivosByCodigosBarras(String codigoBarrasInicial, String codigoBarrasFinal) throws DAOException {
		
		Integer quantidade = 0;
		
		Criteria c = getSession().createCriteria(Exemplar.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.between("codigoBarras", codigoBarrasInicial, codigoBarrasFinal));
		c.add(Restrictions.eq("ativo", true));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		quantidade = (Integer) c.list().get(0);
		
		c = getSession().createCriteria(Fasciculo.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.between("codigoBarras", codigoBarrasInicial, codigoBarrasFinal));
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("incluidoAcervo", true));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		quantidade += (Integer) c.list().get(0);
		
		return quantidade;
		
	}


	/**
	 * 
	 *     Conta os materiais baixados por faixa de código de barras. 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Integer countMateriaisBaixadosByCodigosBarras(String codigosBarrasInicial, String CodigoBarrasFinal) throws DAOException {

		Integer quantidade = 0;
		
		Criteria c = getSession().createCriteria(Exemplar.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.between("codigoBarras", codigosBarrasInicial, CodigoBarrasFinal));
		c.add(Restrictions.eq("ativo", true));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", true));
		quantidade = (Integer) c.list().get(0);
		
		c = getSession().createCriteria(Fasciculo.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.between("codigoBarras", codigosBarrasInicial, CodigoBarrasFinal));
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("incluidoAcervo", true));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", true));
		quantidade += (Integer) c.list().get(0);
		
		return quantidade;
		
	}
	
	/**
	 * 
	 *     <p>Conta os materiais pelo código de barras passado.</p> 
	 *     <p>Usado quando se deseja verificar a quantidade de materiais com o mesmo código de barras no acervo </p> 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Integer countMateriaisByCodigosBarras(String codigosBarras) throws DAOException {
		
		Integer quantidade = 0;
		
		if( StringUtils.isEmpty(codigosBarras))
			return 0;
		
		Criteria c = getSession().createCriteria(Exemplar.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.eq("codigoBarras", codigosBarras.toUpperCase()));
		quantidade = (Integer) c.list().get(0);
		
		c = getSession().createCriteria(Fasciculo.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.eq("codigoBarras", codigosBarras.toUpperCase()));
		quantidade += (Integer) c.list().get(0);
		
		return quantidade;
		
	}
	
	

	
	
	
	/**
	 * 
	 *     <p>Verifica se existem os materiais pelo código de barras passado.</p> 
	 *     <p>Usado quando se deseja verificar se o material passo já foi catalogado ou não. </p> 
	 *
	 * @param codigosBarras o código de barras do material
	 * @param idMaterial o id do material, para verificar materiais que não seja ele próprio. Se for passado <code>null</code>
	 *   essa verificação não é feita. 
	 * @return
	 * @throws DAOException
	 */
	public boolean existeMateriaisByCodigosBarras(String codigosBarras, Integer idMaterial) throws DAOException {
		
		Integer quantidade = 0;
		
		if( StringUtils.isEmpty(codigosBarras))
			return false;
		
		Criteria c = getSession().createCriteria(Exemplar.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.eq("codigoBarras", codigosBarras.toUpperCase()));
		
		if(idMaterial != null)
			c.add(Restrictions.ne("id", idMaterial));
		
		quantidade = (Integer) c.list().get(0);
		
		c = getSession().createCriteria(Fasciculo.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.eq("codigoBarras", codigosBarras.toUpperCase()));
		
		if(idMaterial != null)
			c.add(Restrictions.ne("id", idMaterial));
		
		quantidade += (Integer) c.list().get(0);
		
		if(quantidade.intValue() > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Busca por todos os materiais cujas ids são as passadas por parâmetro.
	 * 
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	public List <MaterialInformacional> findMateriaisAtivosByPrimaryKeyIn(List <Integer> ids) throws DAOException {

		List <MaterialInformacional> ms = new ArrayList <MaterialInformacional> ();
		
		Criteria c = getSession().createCriteria (Exemplar.class);
		c.add(Restrictions.in("id", ids));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		c.add(Restrictions.eq("ativo", true));
		
		@SuppressWarnings("unchecked")
		List<MaterialInformacional> lista = c.list();
		ms.addAll(lista);
		
		c = getSession().createCriteria(Fasciculo.class);
		c.add(Restrictions.in("id", ids));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		c.add(Restrictions.eq("ativo", true));
		
		@SuppressWarnings("unchecked")
		List<MaterialInformacional> lista2 = c.list();
		ms.addAll(lista2);
		
		return ms;
	}
	
	
	
	/**
	 * Conta a quantidade de materiais ativos do Título
	 * 	
	 * @param titulo
	 * @return
	 * @throws DAOException
	 */
	
	public Long countMateriaisAtivosByTitulo (int idTitulo) throws DAOException{
		
		String hql = " SELECT COUNT( DISTINCT e.id )" 
			+" FROM Exemplar e "
			+" WHERE e.tituloCatalografico.id = " + idTitulo+" AND e.ativo = trueValue() "
			+" AND e.situacao.situacaoDeBaixa = falseValue() ";
		Query q = getSession().createQuery(hql);
		
		Long quantidade = (Long) q.uniqueResult();
		
		if(quantidade == 0 ){ // Se não tem exemplares, verifica se os materiais são fascículos
			
			hql = " SELECT COUNT( DISTINCT f.id )" 
				+" FROM Fasciculo f "
				+" WHERE f.assinatura.tituloCatalografico.id = " + idTitulo+" AND f.incluidoAcervo = trueValue()" 
				+" AND f.ativo = trueValue()  AND f.situacao.situacaoDeBaixa = falseValue() ";
			
			q = getSession().createQuery(hql);
			
			quantidade = (Long) q.uniqueResult();
		}
		
		return quantidade;
	}
	
	
	
	/**
	 * Busca as informações dos materiais de um determinado Título
	 * 	
	 * @param titulo
	 * @return
	 * @throws DAOException
	 */
	
	public List<MaterialInformacional> findInfoMateriaisAtivosByTitulo (int idTitulo) throws DAOException{
		
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
		
		String hql = " SELECT e.id, e.codigoBarras, e.situacao, e.status " 
			+" FROM Exemplar e "
			+" WHERE e.tituloCatalografico.id = " + idTitulo+" AND e.ativo = trueValue() " 
			+" AND e.situacao.situacaoDeBaixa = falseValue() ";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Object> list = q.list();
		
		for (Object object : list) {
			
			Object[] temp = (Object[]) object;
			MaterialInformacional m = new Exemplar();
			m.setId( (Integer) temp [0]);
			m.setCodigoBarras( (String) temp [1]);
			m.setSituacao( (SituacaoMaterialInformacional) temp [2]);
			m.setStatus( (StatusMaterialInformacional) temp [3]);
			materiais.add(m);
		}
		
		
		
		if(materiais.size() == 0 ){ // Se não tem exemplares, verifica se os materiais são fascículos
			
			hql = " SELECT f.id, f.codigoBarras, f.situacao, f.status" 
				+" FROM Fasciculo f "
				+" WHERE f.assinatura.tituloCatalografico.id = " + idTitulo+" AND f.incluidoAcervo = trueValue() " 
				+" AND f.ativo = trueValue() AND f.situacao.situacaoDeBaixa = falseValue() ";
			
			q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			List<Object> lista = q.list();
			
			for (Object object : lista) {
				
				Object[] temp = (Object[]) object;
				MaterialInformacional m = new Fasciculo();
				m.setId( (Integer) temp [0]);
				m.setCodigoBarras( (String) temp [1]);
				m.setSituacao( (SituacaoMaterialInformacional) temp [2]);
				m.setStatus( (StatusMaterialInformacional) temp [3]);
				materiais.add(m);
			}
		}
		
		return materiais;
	}
	
	
	/**
	 * Conta a quantidade de materiais ativos do Título que podem ser emprestados, 
	 * ou seja, estão na situação "Disponível" e o status permite empréstimos.
	 * 	
	 * @param titulo
	 * @return
	 * @throws DAOException
	 */
	
	public Long countMateriaisAtivosQuePodemSerEmprestadosByTitulo (int idTitulo) throws DAOException{
		
		String hql = " SELECT COUNT( DISTINCT e.id )" 
			+" FROM Exemplar e "
			+" WHERE e.tituloCatalografico.id = " + idTitulo+" AND e.ativo = trueValue() "
			+" AND e.situacao.situacaoDisponivel = trueValue() AND e.status.permiteEmprestimo = trueValue() ";
		Query q = getSession().createQuery(hql);
		
		Long quantidade = (Long) q.uniqueResult();
		
		if(quantidade == 0 ){ // Se não tem exemplares, verifica se os materiais são fascículos
			
			hql = " SELECT COUNT( DISTINCT f.id )" 
				+" FROM Fasciculo f "
				+" WHERE f.assinatura.tituloCatalografico.id = " + idTitulo+" AND f.incluidoAcervo = trueValue()" 
				+" AND f.ativo = trueValue()  AND f.situacao.situacaoDisponivel = trueValue() AND f.status.permiteEmprestimo = trueValue()  ";
			
			q = getSession().createQuery(hql);
			
			quantidade = (Long) q.uniqueResult();
		}
		
		return quantidade;
	}
	
	
	
	
	/**
	 *    Método que encontra um material ativo, seja ele exemplar ou fascículos ativo pelo código 
	 *  de barras.
	 *
	 * @param codigoBarras
	 * @return
	 * @throws DAOException
	 */
	public MaterialInformacional findMaterialAtivoByCodigoBarras(String codigoBarras) throws DAOException {
		
		String hqlWhere = "e.codigoBarras = :codigoBarras " +
				"AND situacao.situacaoDeBaixa = falseValue() " +
				"AND e.ativo = trueValue() ";
		String hql = getHQLPadraoExemplar(hqlWhere);
		Query q = getSession().createQuery(hql);
		q.setString("codigoBarras", codigoBarras);
		Object[] result = (Object[]) q.uniqueResult();
		MaterialInformacional material = null;
		ArrayList<Object[]> valores = new ArrayList<Object[]>();
		
		if (result == null) {
			String hqlProjecaoAdicional = "f.assinatura.titulo, f.assinatura.tituloCatalografico";
			hqlWhere = "f.codigoBarras = :codigoBarras " +
					"AND situacao.situacaoDeBaixa = falseValue() " +
					"AND f.incluidoAcervo = trueValue() " +
					"AND f.ativo = trueValue() ";
			hql = getHQLPadraoFasciculo(hqlProjecaoAdicional,null,hqlWhere);
			q = getSession().createQuery(hql);
			q.setString("codigoBarras", codigoBarras);
			
			result = (Object[]) q.uniqueResult();

			if (result != null) {	
				valores.add(result);

				material = (MaterialInformacional) HibernateUtils.parseTo(valores, getProjecaoHQLPadraoFasciculo(hqlProjecaoAdicional), Fasciculo.class, "f").toArray()[0];
			}
		}
		else {
			valores.add(result);
			
			material = (MaterialInformacional) HibernateUtils.parseTo(valores, getProjecaoHQLPadraoExemplar(), Exemplar.class, "e").toArray()[0];
		}
		
		return material;
	}



	/**
	 *    Método que encontra um material ativo, seja ele exemplar ou fascículos ativo pelo código 
	 *  de barras.
	 *
	 * @param codigoBarras
	 * @return
	 * @throws  
	 * @throws DAOException
	 */
	public MaterialInformacional findMaterialBaixadoByCodigoBarras(String codigoBarras) throws DAOException {

		String hqlProjecaoAdicional = "e.informacoesTituloMaterialBaixado";
		String hqlWhere = "e.codigoBarras = :codigoBarras " +
				"AND situacao.situacaoDeBaixa = trueValue() " +
				"AND e.ativo = trueValue() ";
		String hql = getHQLPadraoExemplar(hqlProjecaoAdicional, null, hqlWhere);
		Query q = getSession().createQuery(hql);
		q.setString("codigoBarras", codigoBarras);
		Object[] result = (Object[]) q.uniqueResult();
		MaterialInformacional material = null;
		ArrayList<Object[]> valores = new ArrayList<Object[]>();
		
		if (result == null) {
			hqlProjecaoAdicional = "f.informacoesTituloMaterialBaixado";
			hqlWhere = "f.codigoBarras = :codigoBarras " +
					"AND situacao.situacaoDeBaixa = trueValue() " +
					"AND f.incluidoAcervo = trueValue() " +
					"AND f.ativo = trueValue() ";
			hql = getHQLPadraoFasciculo(hqlProjecaoAdicional, null, hqlWhere);
			q = getSession().createQuery(hql);
			q.setString("codigoBarras", codigoBarras);
			
			result = (Object[]) q.uniqueResult();

			if (result != null) {	
				valores.add(result);

				material = (MaterialInformacional) HibernateUtils.parseTo(valores, getProjecaoHQLPadraoFasciculo(hqlProjecaoAdicional), Fasciculo.class, "f").toArray()[0];
			}
		}
		else {
			valores.add(result);
			
			material = (MaterialInformacional) HibernateUtils.parseTo(valores, getProjecaoHQLPadraoExemplar(hqlProjecaoAdicional), Exemplar.class, "e").toArray()[0];
		}
		
		return material;
		
	}

	
	/**
	 * Retorna a lista de alterações de um material catalográfico em um período.
	 * 
	 * @param id
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Object[]> findAlteracoesByMaterialPeriodo(int idMaterial, Date dataInicio, Date dataFim) throws  DAOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		List <Object []> resultado = new ArrayList <Object []> ();
		
		String sql = "select p.nome, h.descricao_operacao, h.data_operacao " +
					"from biblioteca.historico_alteracao_material h " +
					"join comum.registro_entrada r on r.id_entrada = h.id_registro_entrada " +
					"join comum.usuario u on u.id_usuario = r.id_usuario " +
					"join comum.pessoa p on p.id_pessoa = u.id_pessoa " +
					"and h.id_material = " + idMaterial;
					
		if (dataInicio != null)
			sql += " and data_operacao >= '"+sdf.format(dataInicio)+"' ";
		if (dataFim != null)
			sql += "and data_operacao <= '"+sdf.format(dataFim)+" 23:59:59' ";

		sql += "order by data_operacao";
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql).list();
		resultado.addAll(lista);
		
		return resultado;
	}




	/**
	 * <p>Atualiza a situação de vários materiais informacionais de forma otimizada.</p>
	 * 
	 * <p> <strong> Esse método existe principalmente porque não dá para utilizar o método <code>updateField</code> com a classe abstratra MaterialInformacional </strong> </p>
	 * 
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws HibernateException 
	 */
	public void atualizaSituacaoDeMateriais(List<Integer> idsMateriais, int idSituacao) throws DAOException {
		Query q = getSession().createSQLQuery("UPDATE biblioteca.material_informacional SET id_situacao_material_informacional = :idSituacao WHERE id_material_informacional IN "+UFRNUtils.gerarStringIn(idsMateriais));
		q.setInteger("idSituacao", idSituacao);
		if (q.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao atualizar a situação dos materiais.");
	}
	
	
	/**
	 * Retorna uma listagem contendo as informações dos materiais perdidos para exibir ao bibliotecário.
     *
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Object[]> findListagemMateriaisPerdidosPessoas(List<Integer> idsUnidades, boolean administradorGeral) throws DAOException {
		
		String filtraPorBiblioteca = "";
		
		if (!administradorGeral)
			filtraPorBiblioteca = "and bibliotecaMaterial.unidade.id in ( :idsUnidades )";
		
			String hql = "select e.id, ub.id, m.codigoBarras, pe.dataAnterior, pe.dataAtual, ub.pessoa.nome, bibliotecaMaterial.descricao from ProrrogacaoEmprestimo pe " + 
						"join pe.emprestimo e " + 
						"join e.material m " + 
						"join m.biblioteca bibliotecaMaterial " + 
						"join e.usuarioBiblioteca ub " + 
						"where e.dataDevolucao is null AND e.dataEstorno is null " + 
						"and pe.tipo = " + TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL + " " + 
						filtraPorBiblioteca + " " +
						"order by bibliotecaMaterial.id, ub.pessoa.nome, m.codigoBarras, pe.dataAtual";
		
		Query q = getSession().createQuery(hql);
		if (!administradorGeral)
			q.setParameterList("idsUnidades", idsUnidades);
		
		q.setMaxResults(100);
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		return rs;
	}
	
	
	/**
	 * Retorna uma listagem contendo as informações dos materiais perdidos para exibir ao bibliotecário.
     *
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Object[]> findListagemMateriaisPerdidosBibliotecas(List<Integer> idsUnidades, boolean administradorGeral) throws DAOException {
		
		String filtraPorBiblioteca = "";
		
		if (!administradorGeral)
			filtraPorBiblioteca = "and bibliotecaMaterial.unidade.id in ( :idsUnidades )";
		
			String hql = "select e.id, ub.id, m.codigoBarras, pe.dataAnterior, pe.dataAtual, ub.biblioteca.descricao, bibliotecaMaterial.descricao from ProrrogacaoEmprestimo pe " + 
						"join pe.emprestimo e " + 
						"join e.material m " + 
						"join m.biblioteca bibliotecaMaterial " + 
						"join e.usuarioBiblioteca ub " + 
						"where e.dataDevolucao is null AND e.dataEstorno is null " + 
						"and pe.tipo = " + TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL + " " + 
						filtraPorBiblioteca + " " +
						"order by bibliotecaMaterial.id, ub.biblioteca.descricao, m.codigoBarras, pe.dataAtual";
		
		Query q = getSession().createQuery(hql);
		if (!administradorGeral)
			q.setParameterList("idsUnidades", idsUnidades);
		
		q.setMaxResults(100);
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		return rs;
	}
	
	/**
	 * Retorna informações sobre o material que o materail passo está substituíndo no sistema.
     *
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public MaterialInformacional findMaterialSubstituido(int idMaterialSubstituidor, boolean exemplar) throws DAOException {
		
		String hql = 	" SELECT m.materialQueEuSubstituo.id, m.materialQueEuSubstituo.codigoBarras" +
						" FROM MaterialInformacional m " + 
						" WHERE m.id = :idMaterialSubstituidor ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterialSubstituidor", idMaterialSubstituidor);
		
		
		Object[] dados = (Object[]) q.uniqueResult();
		
		MaterialInformacional m = null;
		
		if(exemplar)
			m = new Exemplar();
		else
			m = new Fasciculo();
		
		m.setId( (Integer)  (dados[0]));
		m.setCodigoBarras( (String)  (dados[1]));
		return m;
	}
	
	

	/**
	 * Método que monta o HQL padrão das consultas de exemplares
	 * 
	 * @param where Os filtros a serem aplicados à consulta padrão
	 * @return
	 */
	public static String getHQLPadraoExemplar(String where) {
		return getHQLPadraoExemplar(null, null, where);
	}

	/**
	 * Método que monta o HQL padrão das consultas de exemplares
	 * 
	 * @param projecaoAdicional Projeção que se deseja adicionar à projeção padrão
	 * @param innerJoinAdicional Joins que se deseja adicionar à consulta padrão
	 * @param where Os filtros a serem aplicados à consulta padrão
	 * @return
	 */
	public static String getHQLPadraoExemplar(String projecaoAdicional, String innerJoinAdicional, String where) {
		return "SELECT "+
				getProjecaoHQLPadraoExemplar(projecaoAdicional)+
				" FROM Exemplar e " +
				" INNER JOIN e.colecao colecao "+
				" INNER JOIN e.biblioteca biblioteca "+
				" INNER JOIN e.situacao situacao "+
				" INNER JOIN e.status status "+
				" INNER JOIN e.tipoMaterial tipoMaterial " +
				(!StringUtils.isEmpty(innerJoinAdicional) ? innerJoinAdicional : "")+
				" WHERE "+
				(!StringUtils.isEmpty(where) ? where : "");
	}

	/**
	 * Método que monta o HQL padrão das consultas de fascículos
	 * 
	 * @param where Os filtros a serem aplicados à consulta padrão
	 * @return
	 */
	public static String getHQLPadraoFasciculo(String where) {
		return getHQLPadraoFasciculo(null, null, where);
	}

	/**
	 * Método que monta o HQL padrão das consultas de fascículos
	 * 
	 * @param projecaoAdicional Projeção que se deseja adicionar à projeção padrão
	 * @param innerJoinAdicional Joins que se deseja adicionar à consulta padrão
	 * @param where Os filtros a serem aplicados à consulta padrão
	 * @return
	 */
	public static String getHQLPadraoFasciculo(String projecaoAdicional, String innerJoinAdicional, String where) {
		return "SELECT " +
				getProjecaoHQLPadraoFasciculo(projecaoAdicional)+
				" FROM Fasciculo f " +
				" INNER JOIN f.colecao colecao "+
				" INNER JOIN f.biblioteca biblioteca "+
				" INNER JOIN f.situacao situacao "+
				" INNER JOIN f.status status "+
				" INNER JOIN f.tipoMaterial tipoMaterial "+
				(!StringUtils.isEmpty(innerJoinAdicional) ? innerJoinAdicional : "")+
				" WHERE "+
				(!StringUtils.isEmpty(where) ? where : "");
	}
	
	/**
	 * Método que monta o HQL de projeção da consulta padrão de exemplares
	 * 
	 * @return
	 */
	public static String getProjecaoHQLPadraoExemplar() {
		return getProjecaoHQLPadraoExemplar(null);
	}
	
	/**
	 * Método que monta o HQL de projeção da consulta padrão de exemplares
	 * 
	 * @param projecaoAdicional Projeção que se deseja adicionar à projeção padrão
	 * @return
	 */
	public static String getProjecaoHQLPadraoExemplar(String projecaoAdicional) {
		return "e.id, e.codigoBarras, e.numeroChamada, e.anexo, e.segundaLocalizacao, e.notaGeral, e.notaUsuario, e.motivoBaixa, " +
				"e.notaTeseDissertacao, e.notaConteudo, e.numeroVolume, e.ativo, colecao.id, colecao.descricao, biblioteca.id, " +
				"biblioteca.descricao, situacao.id, situacao.descricao, situacao.situacaoDisponivel, situacao.situacaoEmprestado, " +
				"situacao.situacaoDeBaixa, status.id, status.descricao, tipoMaterial.id, tipoMaterial.descricao " +
				(!StringUtils.isEmpty(projecaoAdicional) ? ", " + projecaoAdicional : "");
	}
	
	/**
	 * Método que monta o HQL de projeção da consulta padrão de fascículos
	 * 
	 * @return
	 */
	public static String getProjecaoHQLPadraoFasciculo() {
		return getProjecaoHQLPadraoFasciculo(null);
	}
	
	/**
	 * Método que monta o HQL de projeção da consulta padrão de fascículos
	 * 
	 * @param projecaoAdicional Projeção que se deseja adicionar à projeção padrão
	 * @return
	 */
	public static String getProjecaoHQLPadraoFasciculo(String projecaoAdicional) {
		return "f.id, f.codigoBarras, f.numeroChamada, f.segundaLocalizacao, f.notaGeral, f.notaUsuario, f.descricaoSuplemento, " +
				"f.suplemento, f.anoCronologico, f.ano, f.volume, f.numero, f.edicao, f.ativo, f.motivoBaixa, colecao.id, " +
				"colecao.descricao, biblioteca.id, biblioteca.descricao, situacao.id, situacao.descricao, situacao.situacaoDisponivel, " +
				"situacao.situacaoEmprestado, situacao.situacaoDeBaixa, status.id, status.descricao, tipoMaterial.id, " +
				"tipoMaterial.descricao " +
				(!StringUtils.isEmpty(projecaoAdicional) ? ", " + projecaoAdicional : "");
	}
	
	
	
}
