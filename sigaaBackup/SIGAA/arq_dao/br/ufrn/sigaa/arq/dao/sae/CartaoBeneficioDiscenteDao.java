package br.ufrn.sigaa.arq.dao.sae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.CartaoBeneficioDiscente;
import br.ufrn.sigaa.assistencia.dominio.CartaoBolsaAlimentacao;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.StatusCartaoBeneficio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por consultas relativas 
 * aos cartões de benefício discente.
 * 
 * @author geyson
 */
public class CartaoBeneficioDiscenteDao extends GenericSigaaDAO {
	
	/**
	 * busca discentes com bolsa alimentacao
	 * @param idDiscente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("rawtypes")
	public Collection<BolsaAuxilio> bolsasByDiscente(Integer idDiscente, Long matricula, Integer ano, Integer periodo) throws HibernateException, DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" select bol.id, bol.tipoBolsaAuxilio.id, bol.tipoBolsaAuxilio.denominacao, dis.id, dis.matricula, pessoa.nome  " +
				" from BolsaAuxilioPeriodo bolPe inner join bolPe.bolsaAuxilio bol " +
				" inner join bol.discente dis " +
				" inner join dis.pessoa pessoa " +
				" where dis.status in (1, 2, 8, 9) " +
				" and  bol.situacaoBolsa.id in  " + UFRNUtils.gerarStringIn(new int[]{SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA, SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA}) +
				" and bol.tipoBolsaAuxilio.id in ("+TipoBolsaAuxilio.ALIMENTACAO+", "+TipoBolsaAuxilio.RESIDENCIA_GRADUACAO+", "+TipoBolsaAuxilio.RESIDENCIA_POS+") ");
		hql.append(" and bolPe.ano = "+ano+" and bolPe.periodo = "+periodo+" ");
		if(idDiscente != null){
			hql.append(" and dis.id = "+ idDiscente +" ");
		}
		if(matricula != null){
			hql.append(" and dis.matricula = "+matricula+" ");
		}
		hql.append(" order by pessoa.nome ");
		Query q = getSession().createQuery(hql.toString());
		List lista = q.list();
		ArrayList<BolsaAuxilio> result = new ArrayList<BolsaAuxilio>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			BolsaAuxilio bolsa = new BolsaAuxilio();
			bolsa.setId( (Integer) colunas[col++]);
			bolsa.setTipoBolsaAuxilio(new TipoBolsaAuxilio());
			bolsa.getTipoBolsaAuxilio().setId( (Integer) colunas[col++]);
			bolsa.getTipoBolsaAuxilio().setDenominacao( colunas[col++].toString());
			bolsa.setDiscente(new Discente());
			Integer idDis = (Integer) colunas[col++];
			bolsa.getDiscente().setId(idDis.intValue());
			bolsa.getDiscente().setMatricula((Long) colunas[col++]);
			bolsa.getDiscente().getPessoa().setNome((String) colunas[col++]);
			result.add(bolsa);
		}
		return result;
		
	}
	
	/** busca todos os cartão não bloqueados 
	 * @throws DAOException 
	 * @throws HibernateException */
	@SuppressWarnings("rawtypes")
	public Collection<CartaoBeneficioDiscente> findCartoesDesbloqueados(Integer idDiscente, Long matricula) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" select cardBen.id, status.id, status.descricao, dis.id, dis.matricula, pessoa.id, pessoa.nome, cardAli.id, cardAli.codigo, cardAli.bloqueado ");
		hql.append(" from CartaoBeneficioDiscente cardBen ");
		hql.append(" inner join cardBen.discente dis ");
		hql.append(" inner join cardBen.statusCartaoBeneficio status ");
		hql.append(" inner join cardBen.cartaoBolsaAlimentacao cardAli ");
		hql.append(" inner join dis.pessoa pessoa ");
		hql.append("  where cardBen.id > 0 ");
		
		if(idDiscente != null){
			hql.append(" and dis.id = "+ idDiscente +" ");
		}
		if(matricula != null){
			hql.append(" and dis.matricula = "+matricula+" ");
		}
		hql.append(" order by pessoa.nome ");
		Query q = getSession().createQuery(hql.toString());
		List lista = q.list();
		ArrayList<CartaoBeneficioDiscente> result = new ArrayList<CartaoBeneficioDiscente>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			CartaoBeneficioDiscente cartao = new CartaoBeneficioDiscente();
			cartao.setId( (Integer) colunas[col++]);
			cartao.setStatusCartaoBeneficio(new StatusCartaoBeneficio());
			cartao.getStatusCartaoBeneficio().setId((Integer) colunas[col++]);
			cartao.getStatusCartaoBeneficio().setDescricao((String) colunas[col++]);
			cartao.setDiscente(new Discente());
			cartao.getDiscente().setId( (Integer) colunas[col++]);
			cartao.getDiscente().setMatricula( (Long) colunas[col++]);
			cartao.getDiscente().setPessoa(new Pessoa());
			cartao.getDiscente().getPessoa().setId((Integer) colunas[col++]);
			cartao.getDiscente().getPessoa().setNome( (String) colunas[col++]);
			cartao.setCartaoBolsaAlimentacao(new CartaoBolsaAlimentacao());
			cartao.getCartaoBolsaAlimentacao().setId((Integer) colunas[col++]);
			cartao.getCartaoBolsaAlimentacao().setCodigo((Integer)colunas[col++]);
			cartao.getCartaoBolsaAlimentacao().setBloqueado( (Boolean) colunas[col++]);
			result.add(cartao);
			
		}
		return result;
	}
	
	/** Verifica cartão existente por código de barras */
	public boolean verificaCartaoExistente(String codBarras) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(cardAli.id) from CartaoBolsaAlimentacao cardAli where cardAli.codBarras = '"+codBarras+"' ");
		Query q = getSession().createQuery(hql.toString());
		int resultado = ((Number) q.uniqueResult()).intValue();
		if(resultado == 1){
			return true;
		}else{
			return false;
		}
			
	}
	
	/** Verifica cartão existente por código de varras e código do cartão */
	public boolean verificaCartaoExistente(String codBarras, Integer cod) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(cardAli.id) from CartaoBolsaAlimentacao cardAli where cardAli.codigo = "+cod+" and cardAli.codBarras = '"+codBarras+"' ");
		Query q = getSession().createQuery(hql.toString());
		int resultado = ((Number) q.uniqueResult()).intValue();
		if(resultado == 1){
			return true;
		}else{
			return false;
		}
			
	}
	
	/** Verifica cartão existente por código */
	public boolean verificaCartaoExistente(Integer cod) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(cardAli.id) from CartaoBolsaAlimentacao cardAli where cardAli.codigo = "+cod+" ");
		Query q = getSession().createQuery(hql.toString());
		int resultado = ((Number) q.uniqueResult()).intValue();
		if(resultado == 1){
			return true;
		}else{
			return false;
		}
			
	}
	
	/** retorna cartões beneficio com o mesmo cartao bolsa alimentação cadastrado */
	@SuppressWarnings("rawtypes")
	public Collection<CartaoBeneficioDiscente> cartoesBeneficioDiscente (Integer idCartaoAlimentacao) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" select cardBen.id, cardBen.registroAtribuicao, cardBen.data, cardBen.ativo, cardAli.id, cardAli.bloqueado, dis.id, status.id, status.descricao ");
		hql.append(" from CartaoBeneficioDiscente cardBen ");
		hql.append(" inner join cardBen.discente dis ");
		hql.append(" inner join cardBen.cartaoBolsaAlimentacao cardAli ");
		hql.append(" inner join cardBen.statusCartaoBeneficio status ");
		hql.append(" where cardAli.id = "+ idCartaoAlimentacao +" ");
		
		Query q = getSession().createQuery(hql.toString());
		
		List lista = q.list();
		ArrayList<CartaoBeneficioDiscente> result = new ArrayList<CartaoBeneficioDiscente>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			CartaoBeneficioDiscente cartao = new CartaoBeneficioDiscente();
			cartao.setId( (Integer) colunas[col++]);
			cartao.setRegistroAtribuicao((Integer) colunas[col++]);
			cartao.setData( (Date) colunas[col++]);
			cartao.setAtivo( (Boolean) colunas[col++]);
			cartao.setCartaoBolsaAlimentacao(new CartaoBolsaAlimentacao());
			cartao.getCartaoBolsaAlimentacao().setId((Integer) colunas[col++]);
			cartao.getCartaoBolsaAlimentacao().setBloqueado( (Boolean) colunas[col++]);
			cartao.setDiscente(new Discente());
			cartao.getDiscente().setId( (Integer) colunas[col++]);
			cartao.setStatusCartaoBeneficio(new StatusCartaoBeneficio());
			cartao.getStatusCartaoBeneficio().setId( (Integer) colunas[col++]);
			cartao.getStatusCartaoBeneficio().setDescricao( (String) colunas[col++]);
			
			result.add(cartao);
			
		}
		return result;
	}
	
	/** inativa cartões de beneficio passando cartão bolsa alimentaçao */
	public void inativarCartoes (CartaoBolsaAlimentacao cartaoAli){
		if(cartaoAli.getId() > 0){
			update("UPDATE sae.cartao_beneficio_discente SET id_status_cartao_beneficio = "+StatusCartaoBeneficio.ENCERRADO+" where id_cartao_bolsa_alimentacao =?", new Object[] {cartaoAli.getId()} );
		}
		
	}
	
	/** busca todos os cartões e os discentes vinculados 
	 * @throws DAOException 
	 * @throws HibernateException */
	@SuppressWarnings("rawtypes")
	public Collection<CartaoBeneficioDiscente> findCartoesDiscentes(Integer idDiscente, Long matricula) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" select cardBen.id, status.id, status.descricao, dis.id, dis.matricula, curso.nome,  pessoa.id, pessoa.nome, cardAli.id, cardAli.codigo, cardAli.bloqueado ");
		hql.append(" from CartaoBeneficioDiscente cardBen ");
		hql.append(" inner join cardBen.discente dis ");
		hql.append(" inner join dis.curso curso ");
		hql.append(" inner join cardBen.statusCartaoBeneficio status ");
		hql.append(" inner join cardBen.cartaoBolsaAlimentacao cardAli ");
		hql.append(" inner join dis.pessoa pessoa ");
		hql.append("  where cardBen.id > 0 ");
		
		if(idDiscente != null){
			hql.append(" and dis.id = "+ idDiscente +" ");
		}
		if(matricula != null){
			hql.append(" and dis.matricula = "+matricula+" ");
		}
		hql.append(" order by curso.nome, pessoa.nome ");
		Query q = getSession().createQuery(hql.toString());
		List lista = q.list();
		ArrayList<CartaoBeneficioDiscente> result = new ArrayList<CartaoBeneficioDiscente>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			CartaoBeneficioDiscente cartao = new CartaoBeneficioDiscente();
			cartao.setId( (Integer) colunas[col++]);
			cartao.setStatusCartaoBeneficio(new StatusCartaoBeneficio());
			cartao.getStatusCartaoBeneficio().setId((Integer) colunas[col++]);
			cartao.getStatusCartaoBeneficio().setDescricao((String) colunas[col++]);
			cartao.setDiscente(new Discente());
			cartao.getDiscente().setId( (Integer) colunas[col++]);
			cartao.getDiscente().setMatricula( (Long) colunas[col++]);
			cartao.getDiscente().setCurso(new Curso());
			cartao.getDiscente().getCurso().setNome((String) colunas[col++]);
			cartao.getDiscente().setPessoa(new Pessoa());
			cartao.getDiscente().getPessoa().setId((Integer) colunas[col++]);
			cartao.getDiscente().getPessoa().setNome( (String) colunas[col++]);
			cartao.setCartaoBolsaAlimentacao(new CartaoBolsaAlimentacao());
			cartao.getCartaoBolsaAlimentacao().setId((Integer) colunas[col++]);
			cartao.getCartaoBolsaAlimentacao().setCodigo((Integer) colunas[col++]);
			cartao.getCartaoBolsaAlimentacao().setBloqueado( (Boolean) colunas[col++]);
			result.add(cartao);
			
		}
		return result;
	}

}
