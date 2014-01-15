/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/04/2011
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.MensagemOrientacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao utilizado para consultas na entidade {@link MensagemOrientacao}.
 * 
 * @author bernardo
 *
 */
public class MensagemOrientacaoDao extends GenericSigaaDAO {

	/** Define a quantidade mostrada na listagem de últimas mensagens enviadas. */
	private static final int NUMERO_ULTIMAS_MENSAGENS = 5;
	
	/**
	 * Retorna todas as mensagens cadastradas por um orientador.
	 * 
	 * @param servidor
	 * @param paging
	 * @return
	 * @throws DAOException 
	 */
	public Collection<MensagemOrientacao> findAllByOrientador(Servidor servidor, PagingInformation paging) throws DAOException {
		return findByOrientando(servidor, null, paging, false);
	}
	
	/**
	 * Retorna as últimas mensagens enviadas pelo orientador a qualquer um de seus orientandos.
	 * A quantidade de registros retornados é definida pela constante {@link #NUMERO_ULTIMAS_MENSAGENS}
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<MensagemOrientacao> getUltimasMensagens(Servidor servidor) throws DAOException {
		return findByOrientando(servidor, null, null, true);
	}
	
	/**
	 * Retorna as mensagens de orientação de acordo com os dados informados.
	 * 
	 * @param servidor
	 * @param discente
	 * @param paging
	 * @param ultimas
	 * 	indica se o retorno deve conter apenas as últimas mensagens enviadas, ou listar todas
	 * @return
	 * @throws DAOException
	 */
	public Collection<MensagemOrientacao> findByOrientando(Servidor servidor, DiscenteAdapter discente, PagingInformation paging, boolean ultimas) throws DAOException {
		String projecao = "d.matricula, p.nome, m.mensagem, m.dataCadastro";
		
		String hql = "select " + projecao +
		" FROM MensagemOrientacao m " +
		" INNER JOIN m.orientacaoAcademica o" +
		" INNER JOIN o.discente d" +
		" INNER JOIN d.pessoa p" +
		" WHERE o.servidor.id = :idServidor";
		if(!isEmpty(discente))
			hql += " AND o.discente.id = :idDiscente";
		hql += " ORDER BY m.dataCadastro desc";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idServidor", servidor.getId());
		if(!isEmpty(discente))
			q.setInteger("idDiscente", discente.getId());
		
		if(ultimas)
			q.setMaxResults(NUMERO_ULTIMAS_MENSAGENS);
		
		if(paging != null && !ultimas){
			String countSql = "from graduacao.mensagem_orientacao m " +
									"inner join graduacao.orientacao_academica o on m.id_orientacao_academica=o.id_orientacao_academica " +
									"inner join discente d on o.id_discente=d.id_discente " +
									"inner join comum.pessoa p on d.id_pessoa=p.id_pessoa " +
								"where o.id_servidor = " + servidor.getId();
			if(!isEmpty(discente))
				countSql += " and o.id_discente = " + discente.getId();
			paging.setTotalRegistros(count(countSql));
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		Collection<MensagemOrientacao> mensagens = new ArrayList<MensagemOrientacao>();
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			
			// O Método HibernateUtils.parseTo não funcionou corretamente para o caso
			for (Object[] linha : list) {
				int cont = 0;
				MensagemOrientacao mensagemOrientacao = new MensagemOrientacao();
				mensagemOrientacao.setOrientacaoAcademica(new OrientacaoAcademica());
				Long matricula = (Long) linha[cont++];
				String nome = linha[cont++].toString();
				mensagemOrientacao.getOrientacaoAcademica().setDiscente(new Discente(0, matricula, nome));
				mensagemOrientacao.setMensagem(linha[cont++].toString());
				mensagemOrientacao.setDataCadastro((Date) (linha[cont++]));
				
				mensagens.add(mensagemOrientacao);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
		return mensagens;
	}
}
