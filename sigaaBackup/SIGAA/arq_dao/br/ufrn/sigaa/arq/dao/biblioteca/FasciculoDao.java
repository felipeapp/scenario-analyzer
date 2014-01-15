/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 24/03/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *     <p>Classe da camada de persistência, usada para realizar as buscas que envolvam informações específicas
 *     de fascículos, no sistema de bibliotecas do sigaa. <p/>
 *
 * @author Jadson
 * @since 24/03/2009
 * @version 1.0 criação da classe
 *
 */
public class FasciculoDao extends GenericSigaaDAO{

	/**
	 * O limite de resultado na busca de fascículos
	 */
	public static final int LIMITE_RESULTADOS = 300;
	
	/** Encontra todos os anexos ativos do exemplar passado. */
	public Long countSuplementosAtivosDoFasciculo(int idFasciculo) throws DAOException{
		
		String hql = new String( " SELECT count(f) FROM Fasciculo f "
				+" WHERE f.fasciculoDeQuemSouSuplemento.id = :idFasciculo AND f.situacao.situacaoDeBaixa = falseValue() AND f.ativo = trueValue() ");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idFasciculo", idFasciculo);
		
		return (Long) q.uniqueResult();
	}
	
	
	
	/**
	 *    Retorna os registros de movimentação de fascículos feitos para um biblioteca específica que não possuía
	 * uma assinatura para os fascículos.
	 *
	 * @param idBiblioteca
	 * @return as assinaturas com fascículos pendentes de transferência.
	 * @throws DAOException
	 */
	public List<Object> encontraFasciculosPendentesTransferenciaByBiblioteANDTitulo(int idBibliotecaDestino, int idTituloFasciculo) throws  DAOException {
	
		StringBuilder hql = new StringBuilder(" SELECT m, reg.id ");
		hql.append(" FROM RegistroMovimentacaoMaterialInformacional reg ");
		hql.append(" INNER JOIN reg.material m ");
		hql.append(" WHERE reg.pendente =  trueValue() AND reg.assinturaDestino is null "
				+" AND reg.assinaturaOrigem is not null AND reg.bibliotecaDestino.id = :idBiblioteca AND m.assinatura.tituloCatalografico.id = :idTituloFasciculo");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idBiblioteca", idBibliotecaDestino);
		q.setInteger("idTituloFasciculo", idTituloFasciculo);
		
		
		@SuppressWarnings("unchecked")
		List<Object> list = q.list();
		return list;
	}
	
	
	
	
	
	/**
	 *    Retorna os fascículos que estão pendentes para a autorização da transferência entre bibliotecas.<br/>
	 *
	 * @return retorna uma lista de array na qual cada objeto array da lista contém: <br/>
	 *            na posição 0: o fascículo <br/>
	 *            na posição 1: id do registro de movimentação <br/>
	 *            na posição 2: o usuário que fez a transferência <br/>
	 *            na posição 3: a assinatura de origem <br/>
	 */
	public List<Object> encontraFasciculosPendentesTraferenciaDaAssinaturaSelecionada(int idAssinaturaDestino, int idBibliotecaDestino) throws  DAOException {
		
		StringBuilder hql = new StringBuilder(" SELECT reg.material, reg.id, reg.usuarioMovimentouMaterial.pessoa.nome ");
		hql.append(" FROM RegistroMovimentacaoMaterialInformacional reg ");
		hql.append(" WHERE reg.pendente = trueValue() AND reg.bibliotecaDestino.id = :idBiblioteca AND reg.assinturaDestino.id  = :idAssinatura");
		
		Query q = getSession().createQuery( hql.toString());
		q.setInteger("idBiblioteca", idBibliotecaDestino);
		q.setInteger("idAssinatura", idAssinaturaDestino);
		
		@SuppressWarnings("unchecked")
		List<Object> list = q.list();
		
		return list;
	}
	
	
	
	
	/**
	 *    Retorna os dados dos suplementos do fascículo passado
	 */
	public List<Fasciculo> findSuplementosDoFasciculo(int idFasciculoPrincipal) throws DAOException {
		
		String projecao = " f.id, f.codigoBarras, f.anoCronologico, f.ano, f.numero, f.volume, f.edicao ";
		
		String hql =" SELECT  "+projecao
				+" FROM Fasciculo f "
				+" WHERE f.fasciculoDeQuemSouSuplemento.id = :idFasciculoPrincipal ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idFasciculoPrincipal", idFasciculoPrincipal);
		
		@SuppressWarnings("unchecked")
		List<Fasciculo> lista = (List<Fasciculo>) HibernateUtils.parseTo(q.list(), projecao, Fasciculo.class, "f") ;
		return lista;
	}
	
	
	
	/**
	 *    Conta a quantidade de fascículos ativos no acervo (não baixados e não removidos) e registrados
	 * (criados mas não estão no acervo ainda) para a assinatura passada.</br>
	 *    Geralmente usado para remover uma assinatura criada errada. <br/>
	 */
	public long countFasciculosAtivosNoAcervoOURegistradosDaAssinatura(int idAssinatura) throws DAOException{
		
		String hql =" SELECT count(f.id) FROM Fasciculo f "
			+" LEFT JOIN f.situacao s "                       // Se não foi catalogado não tem situação ainda, por isso left join
			+" WHERE f.assinatura.id = :idAssinatura AND ( f.incluidoAcervo = falseValue() OR ( f.incluidoAcervo = trueValue() AND s.situacaoDeBaixa = falseValue() ) ) AND f.ativo = trueValue() ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAssinatura", idAssinatura);
		return (Long) q.uniqueResult();
	}
	
	
	
	
	/**
	 *    Busca fascículos ativo no acervo (não baixados) para a assinatura passada.
	 */
	public List<Fasciculo> findFasciculosAtivosNoAcervoDaAssinatura(int idAssinatura) throws DAOException{
		
		String hql =" SELECT f FROM Fasciculo f "
			+" LEFT JOIN f.situacao s "                       // Se não foi catalogado não tem situação ainda, por isso left join
			+" WHERE f.assinatura.id = :idAssinatura AND ( f.incluidoAcervo = trueValue() AND s.situacaoDeBaixa = falseValue() ) AND f.ativo = trueValue() )";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAssinatura", idAssinatura);
		
		@SuppressWarnings("unchecked")
		List<Fasciculo> lista = q.list();
		return lista;
	}
	
	
	
	
	/**
	 *    Conta a quantidade de fascículos ativos no acervo (não baixados) para a assinatura passada.
	 */
	public Long countFasciculosAtivosNoAcervoDaAssinatura(int idAssinatura) throws DAOException{
		
		String hql =" SELECT count(f.id) FROM Fasciculo f "
			+" LEFT JOIN f.situacao s "                       // Se não foi catalogado não tem situação ainda, por isso left join
			+" WHERE f.assinatura.id = :idAssinatura AND ( f.incluidoAcervo = trueValue() AND s.situacaoDeBaixa = falseValue() ) AND f.ativo = trueValue()  )";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAssinatura", idAssinatura);
		return (Long) q.uniqueResult();
	}
	
	
	/**
	 *    Conta a quantidade de fascículos ativos no acervo (não baixados) para a assinatura passada.
	 */
	public Long countFasciculosAtivosNoAcervoDoTitulo(int idTitulo) throws DAOException{
		
		String hql =" SELECT count(f.id) FROM Fasciculo f "
			+" LEFT JOIN f.situacao s "                       // Se não foi catalogado não tem situação ainda, por isso left join
			+" WHERE f.assinatura.tituloCatalografico.id = :idTitulo AND ( f.incluidoAcervo = trueValue() AND s.situacaoDeBaixa = falseValue() ) AND f.ativo = trueValue()  )";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idTitulo", idTitulo);
		return (Long) q.uniqueResult();
	}
	
	
	
	/**
	 *   Busca todos os fascículos registrados em aquisição para a assinatura passada.
	 */
	public List<Fasciculo> findFasciculosRegistradosDaAssinatura(int idAssinatura) throws DAOException{
		
		String hql =" SELECT f FROM Fasciculo f "
			+" LEFT JOIN f.situacao s "                       // Se não foi catalogado não tem situação ainda, por isso left join
			+" WHERE f.assinatura.id = :idAssinatura AND  f.incluidoAcervo = falseValue() AND f.ativo = trueValue() ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAssinatura", idAssinatura);
		
		@SuppressWarnings("unchecked")
		List<Fasciculo> lista = q.list();
		return lista;
	}
	
	
	/**
	 *   <p>Busca todos os fascículos incluídos no acervo e também os que foram apenas registrados em aquisição para a assinatura passada, 
	 *   ordenados pela data de criação.</p>
	 */
	public List<Fasciculo> findAllFasciculosCriadosDaAssinatura(int idAssinatura) throws DAOException{
		
		String hql =" SELECT f FROM Fasciculo f "
			+" LEFT JOIN f.situacao s "                       // Se não foi catalogado não tem situação ainda, por isso left join
			+" WHERE f.assinatura.id = :idAssinatura AND f.ativo = trueValue() "
			+" ORDER BY f.dataCriacao, f.id ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAssinatura", idAssinatura);
		
		@SuppressWarnings("unchecked")
		List<Fasciculo> lista = q.list();
		return lista;
	}
	
	
	
	
	/**
	 *  <p>Retorna todos os fascículos que estão em outra assinatura, mas possuem o código de barras começando com o código de barras da assunatura passada.</p>
	 *  
	 *  <p>Normalmente são fascículos que foram transferidos entre assinaturas, esse método é utilizado na reorganização os códigos 
	 *  dos fascículos, para não deixar que ele coincidam com o código de fascículos em outras assinaturas</p>
	 *  
	 */
	public List<Fasciculo> findAllFasciculosEmOutraAssinaturaComOCodigoAssinaturaPassada(String codigoAssinatura, int idAssinatura) throws DAOException{
		
		String hql =" SELECT f FROM Fasciculo f "
			+" LEFT JOIN f.situacao s "                       // Se não foi catalogado não tem situação ainda, por isso left join
			+" WHERE f.codigoBarras like :codigoAssinatura "
			+" AND f.assinatura.id <> :idAssinatura AND f.ativo = trueValue() "
			+" ORDER BY f.dataCriacao ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAssinatura", idAssinatura);
		q.setString("codigoAssinatura", codigoAssinatura+"%");
		
		@SuppressWarnings("unchecked")
		List<Fasciculo> lista = q.list();
		return lista;
	}
	
	
	
	
	
	/**
	 *    Método que verifica se já existe outro fascículo com o mesmo anoCronologico, ano, numero, volume e edição
	 * para a assinatura passada.<br/><br/>
	 */
	public Long countFasciculoIguaisAssinatura(int idAssinatura, String anoCronologico, String ano
			, String volume, String numero, String edicao, String diaMes) throws DAOException{
	
		StringBuilder hql = new StringBuilder("Select count(f.id) FROM Fasciculo f ");
		hql.append(" WHERE f.assinatura.id = :idAssinatura AND f.ativo = trueValue()  ");
		
		hql.append(montaParteComumVerificaFasciculosIguais( anoCronologico,  ano,  volume,  numero,  edicao, diaMes));
		
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idAssinatura", idAssinatura);
		
		if(StringUtils.notEmpty(anoCronologico))       q.setString("anoCronologico", anoCronologico);
		if(StringUtils.notEmpty(ano))                  q.setString("ano", ano);
		if(StringUtils.notEmpty(volume)) q.setString("volume", volume);
		if(StringUtils.notEmpty(numero)) q.setString("numero", numero);
		if(StringUtils.notEmpty(edicao)) q.setString("edicao", edicao);
		if(StringUtils.notEmpty(diaMes)) q.setString("diaMes", diaMes);
		
		return (Long) q.uniqueResult();
	}
	
	
	/**
	 * 
	 *    Método que verifica se já existe outro fascículo com o mesmo anoCronologico, ano, número, volume e edição
	 * para a assinatura passada que não seja o fascículo principal.<br/><br/>
	 * 
	 *    <strong>Observação: Se um fascículo for suplemento, ele pode ter os mesmos dados do fascículo principal. <br/></strong>
	 */
	public Long countFasciculoIguaisAssinaturaSuplemento(int idAssinatura, String anoCronologico, String ano
			, String volume, String numero, String edicao, String diaMes, int idPrincipal) throws DAOException{
	
		// Conte os fascículos iguais que não sejam o principal, nem os suplementos do principal
		
		StringBuilder hql = new StringBuilder("Select count(f.id) FROM Fasciculo f ");
		hql.append(" WHERE f.assinatura.id = :idAssinatura AND f.id != :idPrincipal AND f.fasciculoDeQuemSouSuplemento.id != :idPrincipal AND f.ativo = trueValue() ");
		
		hql.append(montaParteComumVerificaFasciculosIguais( anoCronologico,  ano,  volume,  numero,  edicao, diaMes));
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idAssinatura", idAssinatura);
		q.setInteger("idPrincipal", idPrincipal);
		
		if(StringUtils.notEmpty(anoCronologico))       q.setString("anoCronologico", anoCronologico);
		if(StringUtils.notEmpty(ano))                  q.setString("ano", ano);
		if(StringUtils.notEmpty(volume)) q.setString("volume", volume);
		if(StringUtils.notEmpty(numero)) q.setString("numero", numero);
		if(StringUtils.notEmpty(edicao)) q.setString("edicao", edicao);
		if(StringUtils.notEmpty(diaMes)) q.setString("diaMes", diaMes);
		
		return (Long) q.uniqueResult();
	}
	
	
	
	/**
	 * 
	 *    Método que que verifica se já existe outro fascículo com o mesmo anoCronologico, ano, numero, volume e edição
	 * para a assinatura passada como parâmetro, que não seja ele próprio. <br/>
	 *    Esse método geralmente é usado para fazer a checagem no momento da alteração dos fascículos. <br/><br/>
	 *
	 *    <strong>Observação: Se um fascículo for suplemento, ele pode ter os mesmos dados do fascículo principal. <br/></strong>
	 */
	public Long countFasciculoIguaisAssinaturaParaAlteracao(int idAssinatura, String anoCronologico, String ano
			, String volume, String numero, String edicao, String diaMes, int idFasciculo) throws DAOException{
	
		// "Conte os fascículos iguais da minha assinatura que não seja eu mesmo e um dos meus suplementos" //
		
		StringBuilder hql = new StringBuilder("Select count(f.id) FROM Fasciculo f ");
		hql.append(" WHERE f.assinatura.id = :idAssinatura  AND f.id != :idFasciculo ");
		hql.append(" AND f.fasciculoDeQuemSouSuplemento.id != :idFasciculo AND f.ativo = trueValue() ");
		
		hql.append(montaParteComumVerificaFasciculosIguais( anoCronologico,  ano,  volume,  numero,  edicao, diaMes));
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idAssinatura", idAssinatura);
		q.setInteger("idFasciculo", idFasciculo);
		
		if(StringUtils.notEmpty(anoCronologico))       q.setString("anoCronologico", anoCronologico);
		if(StringUtils.notEmpty(ano))                  q.setString("ano", ano);
		if(StringUtils.notEmpty(volume))               q.setString("volume", volume);
		if(StringUtils.notEmpty(numero))               q.setString("numero", numero);
		if(StringUtils.notEmpty(edicao))               q.setString("edicao", edicao);
		if(StringUtils.notEmpty(diaMes))               q.setString("diaMes", diaMes);
		
		return (Long) q.uniqueResult();
	}
	
	/**
	 * 
	 *    Método que verifica se já existe outro fascículo com o mesmo anoCronologico, ano, número, volume e edição
	 * para a assinatura passada como parâmetro, que não seja o próprio fascículo e o fascículo principal dele.<br/>
	 *    Esse método geralmente é usado para fazer a checagem no momento da alteração dos suplementos. <br/><br/>
	 *
	 *    <strong>Observação: Se um fascículo for suplemento, ele pode ter os mesmos dados do fascículo principal. <br/></strong>
	 */
	public Long countFasciculoIguaisAssinaturaParaAlteracaoSuplemento(int idAssinatura, String anoCronologico, String ano,
			String volume, String numero, String edicao, String diaMes, int idFasciculo, int idPrincipal) throws DAOException{
	
		// "Conte os fascículos iguais da minha assinatura que não seja eu mesmo, o meu principal e os suplementos dele" //
		StringBuilder hql = new StringBuilder("Select count(f.id) FROM Fasciculo f ");
		hql.append(" WHERE f.assinatura.id = :idAssinatura  AND f.id != :idFasciculo AND f.id != :idPrincipal ");
		hql.append(" AND f.fasciculoDeQuemSouSuplemento.id != :idPrincipal AND f.ativo = trueValue()  ");
		
		hql.append(montaParteComumVerificaFasciculosIguais( anoCronologico,  ano,  volume,  numero,  edicao, diaMes));
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idAssinatura", idAssinatura);
		q.setInteger("idFasciculo", idFasciculo);
		q.setInteger("idPrincipal", idPrincipal);
		
		if(StringUtils.notEmpty(anoCronologico))       q.setString("anoCronologico", anoCronologico);
		if(StringUtils.notEmpty(ano))                  q.setString("ano", ano);
		if(StringUtils.notEmpty(volume))               q.setString("volume", volume);
		if(StringUtils.notEmpty(numero))               q.setString("numero", numero);
		if(StringUtils.notEmpty(edicao))               q.setString("edicao", edicao);
		if(StringUtils.notEmpty(diaMes))               q.setString("diaMes", diaMes);
		
		return (Long) q.uniqueResult();
	}
	
	
	
	/**
	 *    Método que monta a parte comum da consulta que estava se repetindo na verificação de fascículos
	 * iguais.
	 */
	private String montaParteComumVerificaFasciculosIguais(String anoCronologico, String ano
			, String volume, String numero, String edicao, String diaMes){
		
		StringBuilder hqlComum = new StringBuilder();
		
		if(StringUtils.notEmpty(anoCronologico))
			hqlComum.append(" AND f.anoCronologico = :anoCronologico ");
		else
			hqlComum.append(" AND ( f.anoCronologico is  null OR length(f.anoCronologico) = 0 ) ");
		
		if(StringUtils.notEmpty(ano))
			hqlComum.append(" AND f.ano = :ano ");
		else
			hqlComum.append(" AND ( f.ano is  null OR length(f.ano) = 0 )  ");
		
		if(StringUtils.notEmpty(volume))
			hqlComum.append(" AND f.volume = :volume ");
		else
			hqlComum.append(" AND ( f.volume is  null OR length(f.volume) = 0 ) ");
		
		if(StringUtils.notEmpty(numero))
			hqlComum.append(" AND f.numero = :numero ");
		else
			hqlComum.append(" AND ( f.numero is  null OR length(f.numero) = 0 ) ");
		
		if(StringUtils.notEmpty(edicao))
			hqlComum.append(" AND f.edicao = :edicao ");
		else
			hqlComum.append(" AND ( f.edicao is  null OR length(f.edicao) = 0 ) ");
		
		if(StringUtils.notEmpty(diaMes))
			hqlComum.append(" AND f.diaMes = :diaMes ");
		else
			hqlComum.append(" AND ( f.diaMes is  null OR length(f.diaMes) = 0 ) ");
		
		return hqlComum.toString();
	}
	
	
	/**
	 *    Método que encontra o fascículo ativo pelo id, inicializando os seus relacionamentos.<br/>
	 * 
	 * 
	 *    Método que DEVE SER SEMPRE CHAMADO se desejar atualizar as informações do fascículo.<br/>
	 *    Tem que trazer os relacionamentos inicializados (biblioteca, coleção, etc...) senão o
	 *    hibernate não vai atualizar.<br/>
	 *
	 * @param id o id do fascículo
	 */
	public Fasciculo findFasciculoAtivosByIDInicializandoRelacionamentos(int id) throws DAOException{
	
		StringBuilder hql = new StringBuilder(" SELECT f FROM Fasciculo f ");
		hql.append(" INNER JOIN FETCH f.colecao ");
		hql.append(" INNER JOIN FETCH f.biblioteca ");
		hql.append(" INNER JOIN FETCH f.situacao ");
		hql.append(" INNER JOIN FETCH f.status ");
		hql.append(" INNER JOIN FETCH f.tipoMaterial ");
		hql.append(" LEFT JOIN FETCH f.formasDocumento ");
		
		hql.append(" WHERE f.id = :idFasciculo ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idFasciculo", id);
		
		return (Fasciculo) q.uniqueResult();
	}
	
	/**
	 *    <p>Encontra um fascículo (ativo e incluído no acervo) pelos campos passados</p>
	 *    <p>OBS.: Esse método retorna os fascículos baixados do acervo.</p>
	 */
	public List <Fasciculo> findAllFasciculosAtivosByExemplo(Fasciculo fasciculo, Date dataCriacaoInicio,
			Date dataCriacaoFinal, short possuiArtigos ) throws DAOException{
		
		boolean possuiCriterioBusca = false;
		
		StringBuilder hqlSelect = new StringBuilder(" SELECT DISTINCT fasciculo FROM Fasciculo fasciculo ");
		hqlSelect.append(" LEFT JOIN fasciculo.formasDocumento formaDocumento ");
		
			
		hqlSelect.append(" WHERE fasciculo.ativo = trueValue() AND fasciculo.incluidoAcervo = trueValue()");
		
		// TODOS os filtros de busca são opcionais //
	
		if(StringUtils.notEmpty( fasciculo.getAssinatura().getTitulo() )){
			
			String[] titulosBusca = BibliotecaUtil.retornaPalavrasBusca( fasciculo.getAssinatura().getTitulo());
			
			for (int ptr = 0; ptr < titulosBusca.length; ptr++) {
				hqlSelect.append(" AND (  fasciculo.assinatura.tituloAscii  like :titulo"+ptr+" ) ");
			}
			
			possuiCriterioBusca = true;
		}
		
		if(StringUtils.notEmpty( fasciculo.getAssinatura().getCodigo() )){
			hqlSelect.append(" AND fasciculo.assinatura.codigo = :codigo ");
			possuiCriterioBusca = true;
		}
		
		if( StringUtils.notEmpty(fasciculo.getCodigoBarras() )){
			hqlSelect.append(" AND fasciculo.codigoBarras like :codigoBarras ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getAnoCronologico() !=  null){
			hqlSelect.append(" AND fasciculo.anoCronologico = :anoCronologico ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getAno() != null){
			hqlSelect.append(" AND fasciculo.ano = :ano ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getDiaMes() != null){
			hqlSelect.append(" AND fasciculo.diaMes = :diaMes ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getVolume() != null){
			hqlSelect.append(" AND fasciculo.volume = :volume ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getNumero() != null){
			hqlSelect.append(" AND  fasciculo.numero = :numero ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getEdicao() != null){
			hqlSelect.append(" AND  fasciculo.edicao = :edicao ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getBiblioteca().getId() > 0){
			hqlSelect.append(" AND  fasciculo.biblioteca.id = :idBiblioteca ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getColecao().getId() > 0){
			hqlSelect.append(" AND  fasciculo.colecao.id = :idColecao ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getTipoMaterial().getId() > 0){
			hqlSelect.append(" AND  fasciculo.tipoMaterial.id = :idTipoMaterial ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getStatus().getId() > 0){
			hqlSelect.append(" AND  fasciculo.status.id = :idStatus ");
			possuiCriterioBusca = true;
		}
		
		if(fasciculo.getSituacao().getId() > 0){
			hqlSelect.append(" AND  fasciculo.situacao.id = :idSituacao");
			possuiCriterioBusca = true;
		}
		
		if(isPeriodoBuscaValido(dataCriacaoInicio, dataCriacaoFinal) ){
			hqlSelect.append(" AND  fasciculo.dataCriacao between :dataCriacaoInicio AND :dataCriacaoFinal ");
			possuiCriterioBusca = true;
		}
	
		if( !isEmpty( fasciculo.getFormasDocumento()) ){
			hqlSelect.append(" AND  formaDocumento.id IN " + UFRNUtils.gerarStringIn(fasciculo.getFormasDocumento()));
			possuiCriterioBusca = true;
		}
		
		hqlSelect.append(" ORDER BY  fasciculo.biblioteca.id, fasciculo.codigoBarras ");
		
		if(possuiCriterioBusca){
			
			Query qReal = getSession().createQuery(hqlSelect.toString());
			
			if(StringUtils.notEmpty( fasciculo.getAssinatura().getTitulo() )){
				
				String[] titulosBusca = BibliotecaUtil.retornaPalavrasBusca(fasciculo.getAssinatura().getTitulo());
				
				for (int ptr = 0; ptr < titulosBusca.length; ptr++) {
					qReal.setString("titulo"+ptr, "% "+ StringUtils.toAsciiAndUpperCase(titulosBusca[ptr])+" %");
				}
			}
			
			if(StringUtils.notEmpty( fasciculo.getAssinatura().getCodigo() )){
				qReal.setString("codigo", fasciculo.getAssinatura().getCodigo());
			}
			
			if(StringUtils.notEmpty(fasciculo.getCodigoBarras())){
				qReal.setString("codigoBarras", fasciculo.getCodigoBarras()+"%");
			}
			
			if(fasciculo.getAnoCronologico() != null){
				qReal.setString("anoCronologico", fasciculo.getAnoCronologico());
			}
			
			if(fasciculo.getAno() != null){
				qReal.setString("ano", fasciculo.getAno());
			}
			
			if(fasciculo.getDiaMes() != null){
				qReal.setString("diaMes", fasciculo.getDiaMes());
			}
			
			if(fasciculo.getVolume() != null){
				qReal.setString("volume", fasciculo.getVolume());
			}
			
			if(fasciculo.getNumero() != null){
				qReal.setString("numero", fasciculo.getNumero());
			}
			
			if(fasciculo.getEdicao() != null){
				qReal.setString("edicao", fasciculo.getEdicao());
			}
			
			if(fasciculo.getBiblioteca().getId() > 0){
				qReal.setInteger("idBiblioteca", fasciculo.getBiblioteca().getId());
			}
			
			if(fasciculo.getColecao().getId() > 0){
				qReal.setInteger("idColecao", fasciculo.getColecao().getId());
			}
			
			if(fasciculo.getTipoMaterial().getId() > 0){
				qReal.setInteger("idTipoMaterial", fasciculo.getTipoMaterial().getId());
			}
			
			if(fasciculo.getStatus().getId() > 0){
				qReal.setInteger("idStatus", fasciculo.getStatus().getId());
			}
			
			if(fasciculo.getSituacao().getId() > 0){
				qReal.setInteger("idSituacao", fasciculo.getSituacao().getId());
			}
			
			if(isPeriodoBuscaValido(dataCriacaoInicio, dataCriacaoFinal)){
				qReal.setDate("dataCriacaoInicio", dataCriacaoInicio);
				qReal.setDate("dataCriacaoFinal", dataCriacaoFinal);
			}
			
			qReal.setMaxResults(LIMITE_RESULTADOS);
			
			@SuppressWarnings("unchecked")
			List<Fasciculo> lista = qReal.list();
			return lista;
			
		}else{
			return new ArrayList<Fasciculo>();
		}
	}
	
	
	
	
	/**
	 *       Encontra todos os fascículos incluídos no sistema (pela sessão de catalogação) e ativos
	 *   da assinatura.
	 *
	 * @return os fascículos ativos da assinatura
	 */
	public List<Fasciculo> findTodosFasciculosAtivosDaAssinatura(int idAssinatura, Integer anoCronologico) throws DAOException{
		
		String hql = "SELECT f FROM Fasciculo f " +
					" WHERE  f.situacao.situacaoDeBaixa = falseValue() " +
					" AND f.ativo = trueValue()  AND f.incluidoAcervo = trueValue() " +
					" AND f.assinatura.id = :idAssinatura ";
		
		if(anoCronologico != null && anoCronologico > 0)
			hql += " AND f.anoCronologico = :anoCronologico ";
				
		hql += " ORDER BY f.anoCronologico DESC, f.codigoBarras ";
		
		Query q = getSession().createQuery(hql);
		
		if(anoCronologico != null && anoCronologico > 0)
			q.setString("anoCronologico", ""+anoCronologico);
		
		q.setInteger("idAssinatura", idAssinatura);
		
		@SuppressWarnings("unchecked")
		List<Fasciculo> lista = q.list();
		return lista;
	}
	
	
	
	
	
	/**
	 *      <p> Esse método é utilizado na visualização dos materiais da busca do acervo. </p>
	 * 
	 *      <p> Encontra todos os fascículos incluídos no sistema (pela sessão de catalogação) e ativos
	 *   da assinatura e já traz os dados dos fascículos que vão ser exibidos para o usuário na mesma
	 *   consulta </p>
	 *
	 *   <p><strong> Como está usando LEFT JOIN FETCH em uma coleção, os fascículos que possuírem
	 *   artigos serão trazidos repetidos, a quantidade de artigos que possuírem.<br/>
	 *   Por exemplo, se um fascículo tiver 10 artigos, a consulta trará 10 vezes esse mesmo fascículo.
	 *   Se usar esse método lembre de retirar esses fascículos repetidos, por exemplo, jogando num <code>SET</code> </strong>
	 *   </p>
	 *
	 * @see this{@link #countTodosFasciculosAtivosDasAssinaturas(List, Integer, boolean)}
	 *
	 * @param idAssinatura
	 * @param anoCronologico o fascículos de um determinado ano
	 * @return os fascículos ativos da assinatura com ou sem artigos.
	 */
	public List<Fasciculo> findTodosFasciculosAtivosDasAssinaturas(List<Integer> idsAssinatura, Integer anoCronologico, boolean apenasSituacaoVisivelUsuarioFinal, int pagina, int limite ) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		if(idsAssinatura == null || idsAssinatura.size() == 0 )
			return new ArrayList<Fasciculo>();

		String hqlProjecaoAdicional = "biblioteca.unidade.id";
		String hqlInnerJoinAdicional = "INNER JOIN biblioteca.unidade unidade";
		String hqlWhere = "f.situacao.situacaoDeBaixa = falseValue() " +
				"AND f.ativo = trueValue() " +
				"AND f.incluidoAcervo = trueValue() " +
				"AND f.assinatura.id in (:listaIdsAssinatura)";
		
		if(apenasSituacaoVisivelUsuarioFinal)
			hqlWhere += " AND f.situacao.visivelPeloUsuario = trueValue() ";
		
		
		if(anoCronologico != null && anoCronologico > 0)
			hqlWhere += " AND f.anoCronologico = :anoCronologico ";
		
		StringBuilder hql = new StringBuilder(MaterialInformacionalDao.getHQLPadraoFasciculo(hqlProjecaoAdicional, hqlInnerJoinAdicional, hqlWhere));

		hql.append(" ORDER BY biblioteca.id, f.anoCronologico DESC, f.ano DESC, f.volume DESC, f.numero DESC, f.edicao DESC  ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setParameterList("listaIdsAssinatura", idsAssinatura);
		
		if(anoCronologico != null && anoCronologico > 0)
			q.setString("anoCronologico", ""+anoCronologico);	
		
		q.setFirstResult((pagina-1) * limite);
		q.setMaxResults(limite);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<Fasciculo> fasciculos = new ArrayList<Fasciculo>();

		if (lista != null) {
			fasciculos.addAll(HibernateUtils.parseTo(lista, MaterialInformacionalDao.getProjecaoHQLPadraoFasciculo(hqlProjecaoAdicional), Fasciculo.class, "f"));
		}
		
		System.out.println(">>>>>>>>>>>  Consultar todas informações dos fascículos demorou: "+ (System.currentTimeMillis()-tempo)+" ms");
			
		return fasciculos;
		
	}
	
	
	/**
	 * 	  <p> Esse método é utilizado na visualização dos materiais da busca do acervo. </p>
	 * 
	 *    <p> Conta a quantidade de fascículos para realizar a paginação </p>
	 *
	 *  @see this{@link #findTodosFasciculosAtivosDasAssinaturas(List, Integer, boolean, int, int)}
	 *
	 * @param idAssinatura
	 * @param anoCronologico o fascículos de um determinado ano
	 * @return os fascículos ativos da assinatura com ou sem artigos.
	 */
	public Integer countTodosFasciculosAtivosDasAssinaturas(List<Integer> idsAssinatura, Integer anoCronologico, boolean apenasSituacaoVisivelUsuarioFinal) throws DAOException{
		
		
		if(idsAssinatura == null || idsAssinatura.size() == 0 )
			return 0;
		
		StringBuilder hql = new StringBuilder("SELECT ");
		
		hql.append( " count(DISTINCT f.id) " );
		
		hql.append(getHQLPadraoFindTodosFasciculos());
		
		if(apenasSituacaoVisivelUsuarioFinal)
			hql.append(" AND f.situacao.visivelPeloUsuario = trueValue() ");
		
		if(anoCronologico != null && anoCronologico > 0)
			hql.append(" AND f.anoCronologico = :anoCronologico ");
			
		Query q = getSession().createQuery(hql.toString());
		q.setParameterList("listaIdsAssinatura", idsAssinatura);
		
		if(anoCronologico != null && anoCronologico > 0)
			q.setString("anoCronologico", ""+anoCronologico);
	
		return  ((Long) q.uniqueResult()).intValue();	
		
	}
	
	
	private static String getHQLPadraoFindTodosFasciculos(){
		return " FROM Fasciculo f "+
		" INNER JOIN f.colecao c "+
		" INNER JOIN f.biblioteca b "+
		" INNER JOIN f.situacao s "+
		" INNER JOIN f.status st "+
		" INNER JOIN f.tipoMaterial t"+
		" WHERE  f.situacao.situacaoDeBaixa = falseValue() AND f.ativo = trueValue()  AND f.incluidoAcervo = trueValue() AND f.assinatura.id in ( :listaIdsAssinatura ) ";
	}
	
	
	
	/**
	 *      <p> Encontra todos os fascículos incluídos no sistema (pela sessão de catalogação)
	 *   da assinatura e já traz os dados dos fascículos que vão ser exibidos para o usuário na mesma
	 *   consulta </p>
	 *
	 *   <p><strong> Como está usando LEFT JOIN FETCH em uma coleção, os fascículos que possuírem
	 *   artigos serão trazidos repetidos, a quantidade de artigos que possuírem.<br/>
	 *   Por exemplo, se um fascículo tiver 10 artigos, a consulta trará 10 vezes esse mesmo fascículo.
	 *   Se usar esse método lembre de retirar esses fascículos repetidos, por exemplo, jogando num <code>SET</code> </strong>
	 *   </p>
	 *
	 * @param idAssinatura
	 * @param anoCronologico o fascículos de um determinado ano
	 * @return os fascículos ativos da assinatura com ou sem artigos.
	 */
	public Fasciculo findTodosDadosFasciculo(Integer idFasciculo) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		
		StringBuilder hql = new StringBuilder("SELECT f.id, f.codigoBarras, f.numeroChamada, f.segundaLocalizacao, f.notaGeral, f.notaUsuario, f.suplemento,"
				+" f.anoCronologico, f.ano, f.diaMes, f.numero, f.volume, f.edicao, f.descricaoSuplemento, f.ativo, "
				+" c.id, c.descricao, b.id, b.descricao, s.id, s.descricao, s.situacaoDisponivel, s.situacaoEmprestado, s.situacaoDeBaixa, st.id, st.descricao, t.id, t.descricao,  "
				+" formaDocumento.denominacao, artigo.id "
				+" FROM Fasciculo f ");
			hql.append(" INNER JOIN f.colecao c ");
			hql.append(" INNER JOIN f.biblioteca b ");
			hql.append(" INNER JOIN f.situacao s ");
			hql.append(" INNER JOIN f.status st ");
			hql.append(" INNER JOIN f.tipoMaterial t");
			hql.append(" LEFT JOIN  f.formasDocumento formaDocumento ");
			hql.append(" LEFT JOIN  f.artigos artigo "); // nem todos fascículos possuem artigos
			hql.append(" WHERE f.ativo = trueValue()  AND f.incluidoAcervo = trueValue() AND f.id = :idFasciculo ");
			
			
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idFasciculo", idFasciculo);
		
		@SuppressWarnings("unchecked")
		List<Object[]> listaDadosFasciculo = q.list();
		
		
		int contadorIndex = 0;

		if(listaDadosFasciculo != null && listaDadosFasciculo.size() > 0){
		
			Object[] dadosFasciculo	= listaDadosFasciculo.get(0);
		
			Fasciculo f = new Fasciculo( (Integer) dadosFasciculo[contadorIndex++]);
			f.setCodigoBarras( (String) dadosFasciculo[contadorIndex++]);
			f.setNumeroChamada( (String) dadosFasciculo[contadorIndex++]);
			f.setSegundaLocalizacao( (String) dadosFasciculo[contadorIndex++]);
			f.setNotaGeral( (String) dadosFasciculo[contadorIndex++]);
			f.setNotaUsuario( (String) dadosFasciculo[contadorIndex++]);
			f.setSuplemento( (Boolean) dadosFasciculo[contadorIndex++]);
			f.setAnoCronologico( (String) dadosFasciculo[contadorIndex++]);
			f.setAno( (String) dadosFasciculo[contadorIndex++]);
			f.setDiaMes( (String) dadosFasciculo[contadorIndex++]);
			f.setNumero( (String) dadosFasciculo[contadorIndex++]);
			f.setVolume( (String) dadosFasciculo[contadorIndex++]);
			f.setEdicao( (String) dadosFasciculo[contadorIndex++]);
			f.setDescricaoSuplemento( (String) dadosFasciculo[contadorIndex++]);
			f.setAtivo( (Boolean) dadosFasciculo[contadorIndex++]);
			f.setColecao( new Colecao((Integer) dadosFasciculo[contadorIndex++], (String) dadosFasciculo[contadorIndex++]));
			f.setBiblioteca( new Biblioteca( (Integer) dadosFasciculo[contadorIndex++], (String) dadosFasciculo[contadorIndex++]));
			SituacaoMaterialInformacional s = new SituacaoMaterialInformacional((Integer) dadosFasciculo[contadorIndex++], (String) dadosFasciculo[contadorIndex++], (Boolean) dadosFasciculo[contadorIndex++], (Boolean) dadosFasciculo[contadorIndex++], (Boolean) dadosFasciculo[contadorIndex++]);
			f.setSituacao( s );
			f.setStatus( new StatusMaterialInformacional((Integer) dadosFasciculo[contadorIndex++], (String) dadosFasciculo[contadorIndex++]) );
			f.setTipoMaterial( new TipoMaterial((Integer) dadosFasciculo[contadorIndex++], (String) dadosFasciculo[contadorIndex++]) );
			
			//f.setAtivo(true); // Só busca os ativos, então pode setar ativo no material
			
			
			for (Object[] dadosFasciculo2 : listaDadosFasciculo) {
				if(StringUtils.notEmpty( (String) dadosFasciculo2[28]) ){
					f.adicionaFormasDocumento( new FormaDocumento((String) dadosFasciculo2[28]));
				}
				
				if((Integer) dadosFasciculo2[29] != null ){
					f.setQuantidadeArtigos(f.getQuantidadeArtigos()+1);
					f.addArtigo(new ArtigoDePeriodico((Integer) dadosFasciculo2[29]));
				}
			}
	
			return f;
			
		}
		
		System.out.println(">>>>>>>>>>>  Consultar todas informações dos fascículos demorou: "+ (System.currentTimeMillis()-tempo)+" ms");
		
		return null;
	}
	
	
	/**
	 * Método que compara se o intervalo de tempo da pesquisa é valido
	 */
	private boolean isPeriodoBuscaValido(Date dataInicio, Date dataFinal){
		
		if (dataInicio != null && dataFinal != null && dataInicio.getTime() < dataFinal.getTime()){
			return true;
		}else{
			return false;
		}
	}
}