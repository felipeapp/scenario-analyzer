package br.ufrn.arq.caixa_postal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * DAO de Mensagem
 * 
 * @author Gleydson
 * @author Johnny Marçal
 * 
 */
@SuppressWarnings("unchecked")
public class MensagemDAO extends GenericSharedDBDao {

	/*Identificador de operação na caixa de entrada*/
	private static final int CAIXA_ENTRADA = 1;
	
	/*Identificador de operação na caixa de saída*/
	private static final int CAIXA_SAIDA = 2;
	
	/*Identificador de operação na lixeira*/
	private static final int LIXEIRA = 3;
	
	/*Consulta para obter apenas informações necessárias para visualização das mensagens da caixa postal de acordo com o remetente da mensagem */
	private static final String HQL_CONSULTA_PROJECAO_BOX_CAIXA_POSTAL_REMETENTE = "select msg.id, msg.titulo, msg.remetente.login, "
			+ "msg.remetente.unidade.codigo, msg.remetente.unidade.sigla, msg.dataCadastro, "
			+ "msg.lida, msg.leituraObrigatoria, msg.idArquivo ";
	
	/**
	 * Retorna mensagens de um determinado usuário
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findByUsuario(Integer usuario) throws DAOException {
		return findByUsuario(usuario, Mensagem.MENSAGEM);
	}
	
	/**
	 * Retorna mensagens de um determinado usuário
	 * @param usuario
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findByUsuario(Integer usuario, int tipo) throws DAOException {
		Criteria c = getCriteria(Mensagem.class);
		c.add(Restrictions.eq("usuarioDestino.id", usuario));
		c.add(Restrictions.or(Restrictions.eq("removidaRemetente", Boolean.FALSE),
		      Restrictions.isNull("removidaRemetente")));
		if (tipo > 0)
			c.add(Restrictions.eq("tipo", tipo));

		c.addOrder(Order.desc("dataCadastro"));
		
		return c.list();
	}
	
	/**
	 * Retorna mensagens enviadas por um determinado usuário
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findEnviadasByUsuario(Integer usuario) throws DAOException {
		Criteria c = getSession().createCriteria(Mensagem.class);
		c.add(Restrictions.eq("remetente.id", usuario));
		c.add(Restrictions.eq("removida", Boolean.FALSE));
		c.addOrder(Order.desc("dataCadastro"));

		return c.list();
	}
	
	/**
	 * Retorna o número de mensagens não lidas por um determinado usuário
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public int findByTotalNaoLidas(int usuario) throws DAOException {
		Query q = getSession().createQuery("select count(*) from Mensagem m where m.usuario = :usuario and m.lida = falseValue() and m.removida = falseValue()");
		q.setInteger("usuario", usuario);
		return ((Number) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna o próximo número sequencial dos chamados
	 * @return
	 * @throws DAOException
	 */
	public int getNextChamado() throws DAOException {
		return getJdbcTemplate().queryForInt("SELECT NEXTVAL('chamado_seq')");
	}
	
	/**
	 * Retorna próximo número do chamado patrimonial
	 * @return
	 * @throws DAOException
	 * @Deprecated utilize BemImpl#getNextChamadoPatrimonial
	 */
	@Deprecated
	public int getNextChamadoPatrimonio() throws DAOException {
		return getJdbcTemplate().queryForInt("SELECT NEXTVAL('num_chamado_patrimonial_seq')");
		//return getJdbcTemplate().queryForInt("SELECT NEXTVAL('patrimonio.num_chamado_patrimonial_seq')");
	}
	

	/**
	 * Remove mensagens selecionadas de um determinado usuário.
	 * @param idMensagens
	 * @param remetente
	 * @throws DAOException
	 */
	public void removerMensagens(int[] idMensagens, boolean remetente) throws DAOException {
		
		ArrayList<Integer> listaMsgMesmoUsuario = new ArrayList<Integer>();
		ArrayList<Integer> listaMsg = new ArrayList<Integer>();
		
		for (int idMensagem : idMensagens) {
			Mensagem mensagem = findMensagemCaixaPostalByPKProjecao(idMensagem);
			if(mensagem != null && mensagem.getUsuarioDestino().getId() == mensagem.getRemetente().getId())
				listaMsgMesmoUsuario.add(idMensagem);
			else
				listaMsg.add(idMensagem);
		}
		
		if (!listaMsgMesmoUsuario.isEmpty())
			removerMsgParaLixeiraMesmoUsuario(listaMsgMesmoUsuario);
	
		if(!listaMsg.isEmpty())
			removerMsgParaLixeira(remetente, listaMsg);		
	}

	/**
	 * Remove em lote as mensagens que foram selecionadas para remoção.
	 * @param listaMsg
	 * @param remetente
	 * @throws DAOException
	 */
	private void removerMsgParaLixeira(boolean remetente, ArrayList<Integer> listaMsg) {

		String inIdsMsg = UFRNUtils.gerarStringIn(listaMsg.toArray());
		String sql;
		
		if (remetente) {
			sql = "update comum.mensagem set removida_remetente = trueValue() where id_mensagem in " + inIdsMsg;
		} else {
			sql = "update comum.mensagem set removida_destinatario = trueValue() where id_mensagem in "	+ inIdsMsg;
		}
		
		getJdbcTemplate().update(sql);

	}

	/**
	 * Remove em lote as mensagens que foram selecionadas para remoção.
	 * Específico para tratar o caso de mensagens onde o remetente e destinatário é o mesmo, ouseja, 
	 * usuário enviou mensagem para ele mesmo. 
	 * 
	 * @param listaMsg
	 * @param remetente
	 * @throws DAOException
	 */
	private void removerMsgParaLixeiraMesmoUsuario(ArrayList<Integer> listaMsgMesmoUsuario) {
	
		String inIdsMsgMesmoUsuario = UFRNUtils.gerarStringIn(listaMsgMesmoUsuario.toArray());
			
		String sql = "update comum.mensagem set removida_remetente = trueValue(), removida_destinatario = trueValue() where id_mensagem in " + inIdsMsgMesmoUsuario;
		getJdbcTemplate().update(sql);
		
	}
	
	
		
	
	public void removerTodasMensagensLixeira(int usuario) throws DAOException {

		//Removendo todas mensagens da lixeira que foram enviadas para esse usuário
		String sql = "update comum.mensagem set rem_lix_destinatario = trueValue() where (id_usuario = " + usuario + "and removida_destinatario = true)";
		getJdbcTemplate().update(sql);
		
		//Removendo todas mensagens da lixeira que foram enviadas por esse usuário
		sql = "update comum.mensagem set rem_lix_remetente = trueValue() where (id_remetente = " + usuario + "and removida_remetente = true)";
		getJdbcTemplate().update(sql);

		
	}
	
	
	/**
	 * Remove mensagens da lixeira de um determinado usuário.
	 * @param idMensagens
	 * @param remetente
	 * @throws DAOException
	 */
	public void removerMensagensLixeira(int[] idMensagens, boolean remetente) throws DAOException {
		String in = UFRNUtils.gerarStringIn(idMensagens);
		String sql;
		if (remetente)
			sql = "update comum.mensagem set rem_lix_remetente = trueValue() where id_mensagem in " + in;
		else
			sql = "update comum.mensagem set rem_lix_destinatario = trueValue() where id_mensagem in " + in;

		getJdbcTemplate().update(sql);
	}
	
	/**
	 * Retorna o número de mensagens não lidas por um determinado usuário de acordo com
	 * o tipo do box (entrada, saida ou lixeira) da caixa postal
	 * @param idusuario
	 * @param tipoBox
	 * @return
	 * @throws DAOException
	 */
	public int getTotalMensagemByUsuarioTipoBox(int idUsuario, int tipoBox) throws DAOException {
		
		StringBuffer hql = new StringBuffer("select count(*) from Mensagem as msg where ");
		
		switch (tipoBox) {
		case CAIXA_ENTRADA:
			hql.append(" msg.usuarioDestino.id = :idUsuario and msg.removidaDestinatario = false and (msg.removidaRemetente = false or msg.removidaRemetente is NULL)");
			break;
		case CAIXA_SAIDA:
			hql.append(" msg.remetente.id = :idUsuario and automatica = false and (msg.removidaRemetente = false or msg.removidaRemetente is NULL)");
			break;	
		case LIXEIRA:
			hql.append(" ((msg.usuarioDestino.id = :idUsuario and msg.remetente.id = :idUsuario and (msg.removidaDestinatario = true or msg.removidaRemetente = true) and ((msg.removidaLixeiraDestinatario = false and msg.removidaLixeiraRemetente = false) or (msg.removidaLixeiraDestinatario is null and msg.removidaLixeiraRemetente is null) ) )");
			hql.append(" or (msg.remetente.id = :idUsuario and msg.usuarioDestino.id <> :idUsuario and msg.removidaRemetente = true and (msg.removidaLixeiraRemetente is null or msg.removidaLixeiraRemetente = false))");
			hql.append(" or (msg.usuarioDestino.id = :idUsuario and msg.remetente.id <> :idUsuario and msg.removidaDestinatario = true and (msg.removidaLixeiraDestinatario is null or msg.removidaLixeiraDestinatario = false)))");
			break;
		default:
			hql.append(" msg.usuarioDestino.id = :idUsuario and msg.removidaDestinatario = false and (msg.removidaRemetente = false or msg.removidaRemetente is NULL)");
			break;
		}
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUsuario", idUsuario);
		
		return ((Number) q.uniqueResult()).intValue();
	}
	
	
	/**
	 * Retorna uma mensagem apenas com as informações necessárias para a visualização na caixa postal (consulta com projeção)
	 * 
	 * @param idMensagem
	 * @return
	 * @throws DAOException
	 */	
	public Mensagem findMensagemCaixaPostalByPKProjecao(int idMensagem) throws HibernateException, DAOException{
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("select msg.id, msg.titulo, msg.mensagem, msg.usuarioDestino.id, msg.usuarioDestino.login, msg.usuarioDestino.pessoa.id, msg.usuarioDestino.pessoa.nome, msg.usuarioDestino.unidade.id, msg.usuarioDestino.unidade.nome, ");
		hql.append("msg.usuarioDestino.unidade.codigo, msg.dataCadastro, msg.remetente.id, msg.remetente.login, msg.remetente.pessoa.id, msg.remetente.pessoa.nome, msg.remetente.unidade.id, msg.remetente.unidade.nome, msg.remetente.unidade.codigo, ");
		hql.append("msg.lida, msg.removidaRemetente, msg.removidaDestinatario, msg.automatica, msg.confLeitura, msg.tipo, msg.idArquivo, msg.leituraObrigatoria, msg.nomeArquivo, msg.removidaLixeiraDestinatario, msg.removidaLixeiraRemetente, ");
		hql.append("msg.usuarioDestino.unidade.sigla, msg.remetente.unidade.sigla ");
		hql.append("from Mensagem as msg ");
		hql.append("left join msg.usuarioDestino ");
		hql.append("left join msg.usuarioDestino.pessoa ");
		hql.append("left join msg.usuarioDestino.unidade ");
		hql.append("left join msg.remetente ");
		hql.append("left join msg.remetente.pessoa ");
		hql.append("left join msg.remetente.unidade " );
		hql.append("where msg.id = :idMensagem ");
				
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idMensagem", idMensagem);
		
		Object[] objProjecao = (Object[]) query.uniqueResult();

		if(!ValidatorUtil.isEmpty(objProjecao)) {
	
			Mensagem msg = extractMensagemProjecao(objProjecao);
			msg.setSelecionada(false);				
			
			return msg;
		}
	
		
		return null;
						

	}
	
	/**
	 * Retorna as mensagens da caixa de entrada de um determinado usuário (consulta com projeção)
	 * 
	 * @param idUsuario
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findCaixaEntradaProjecao(int idUsuario, PagingInformation paginacao, boolean incluirChamados) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		
		hql.append(HQL_CONSULTA_PROJECAO_BOX_CAIXA_POSTAL_REMETENTE);
		hql.append("from Mensagem as msg ");
		hql.append("left join msg.remetente ");
		hql.append("left join msg.remetente.unidade ");
		hql.append("where msg.usuarioDestino.id = :idUsuario ");		
		hql.append("and msg.removidaDestinatario = false ");
		hql.append("order by msg.dataCadastro desc");
				
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idUsuario", idUsuario);

		paginacao.setTotalRegistros(getTotalMensagemByUsuarioTipoBox(idUsuario, CAIXA_ENTRADA));
		
		query.setFirstResult(paginacao.getPaginaAtual()* Mensagem.TAM_PAGINA);
		query.setMaxResults(Mensagem.TAM_PAGINA);
		
		
		Collection<Object[]> projecao = query.list();
	
		Collection<Mensagem> listaMensagem = new ArrayList<Mensagem>();

		Iterator<Object[]> it = projecao.iterator();

		while (it.hasNext()) {
	
			Mensagem msg = extractMensagemBoxProjecaoRemetente(it.next());
			msg.setSelecionada(false);
						
			listaMensagem.add(msg);			
		}
						
		return listaMensagem;		
		
	}
	
	/**
	 * Retorna as mensagens da caixa de saída de um determinado usuário (consulta com projeção)
	 * @param idUsuario
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findCaixaSaidaProjecao(int idUsuario, PagingInformation paginacao) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		hql.append("select msg.id, msg.titulo, msg.usuarioDestino.login, msg.usuarioDestino.unidade.codigo, msg.usuarioDestino.unidade.sigla, ");
		hql.append("msg.dataCadastro, msg.lida, msg.leituraObrigatoria, msg.idArquivo ");
		hql.append("from Mensagem as msg ");
		hql.append("left join msg.usuarioDestino ");
		hql.append("left join msg.usuarioDestino.unidade ");	
		hql.append("where msg.remetente.id = :idUsuario ");
		hql.append("and msg.automatica = false ");
		hql.append("and (msg.removidaRemetente = false or msg.removidaRemetente is NULL) ");
		hql.append("order by msg.dataCadastro desc");
				
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idUsuario", idUsuario);

		paginacao.setTotalRegistros(getTotalMensagemByUsuarioTipoBox(idUsuario, CAIXA_SAIDA));
		
		query.setFirstResult(paginacao.getPaginaAtual()* Mensagem.TAM_PAGINA);
		query.setMaxResults(Mensagem.TAM_PAGINA);
		
		
		Collection<Object[]> projecao = query.list();
	
		Collection<Mensagem> listaMensagem = new ArrayList<Mensagem>();

		Iterator<Object[]> it = projecao.iterator();

		while (it.hasNext()) {
	
			Mensagem msg = extractMensagemBoxSaidaProjecaoDestinatario(it.next());
			msg.setSelecionada(false);
						
			listaMensagem.add(msg);			
		}
						
		return listaMensagem;		
	}
	
	/**
	 * Retorna as mensagem da lixeira de um usuario (consulta com projecao)
	 * 
	 * @param idUsuario
	 * @param paginacao
     *
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findLixeiraProjecao(int idUsuario, PagingInformation paginacao) throws DAOException {


		StringBuffer hql = new StringBuffer();
		hql.append(HQL_CONSULTA_PROJECAO_BOX_CAIXA_POSTAL_REMETENTE);
		hql.append("from Mensagem as msg ");
		hql.append("left join msg.usuarioDestino ");
		hql.append("left join msg.usuarioDestino.unidade ");			
		hql.append("where ((msg.usuarioDestino.id = :idUsuario and msg.remetente.id = :idUsuario and (msg.removidaDestinatario = true or msg.removidaRemetente = true) and ((msg.removidaLixeiraDestinatario = false and msg.removidaLixeiraRemetente = false) or (msg.removidaLixeiraDestinatario is null and msg.removidaLixeiraRemetente is null) ) ) ");
		hql.append("or (msg.remetente.id = :idUsuario and msg.usuarioDestino.id <> :idUsuario and msg.removidaRemetente = true and (msg.removidaLixeiraRemetente is null or msg.removidaLixeiraRemetente = false)) ");
		hql.append("or (msg.usuarioDestino.id = :idUsuario and msg.remetente.id <> :idUsuario and msg.removidaDestinatario = true and (msg.removidaLixeiraDestinatario is null or msg.removidaLixeiraDestinatario = false))) order by msg.dataCadastro desc");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idUsuario", idUsuario);

		paginacao.setTotalRegistros(getTotalMensagemByUsuarioTipoBox(idUsuario, LIXEIRA));
		
		query.setFirstResult(paginacao.getPaginaAtual()* Mensagem.TAM_PAGINA);
		query.setMaxResults(Mensagem.TAM_PAGINA);		
		
		Collection<Object[]> projecao = query.list();
	
		Collection<Mensagem> listaMensagem = new ArrayList<Mensagem>();

		Iterator<Object[]> it = projecao.iterator();

		while (it.hasNext()) {
	
			Mensagem msg = extractMensagemBoxProjecaoRemetente(it.next());
			msg.setSelecionada(false);
						
			listaMensagem.add(msg);			
		}
						
		return listaMensagem;		

	}

	/**
	 * Recebe um Object gerado por uma consulta com projeção da caixa postal
     * e retorna um objeto mensagem para visualização na abertura da mensagem
	 *
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	private Mensagem extractMensagemProjecao(Object[] obj) {
		
		Mensagem mensagem = new Mensagem();
		
		mensagem.setUsuarioDestino(new UsuarioGeral());
		mensagem.getUsuarioDestino().setPessoa(new PessoaGeral());
		mensagem.setRemetente(new UsuarioGeral());
		mensagem.getRemetente().setPessoa(new PessoaGeral());
		mensagem.getUsuarioDestino().setUnidade(new UnidadeGeral());			
		mensagem.getRemetente().setPessoa(new PessoaGeral());
		mensagem.getRemetente().setUnidade(new UnidadeGeral());
		
		int i = 0;
		mensagem.setId((Integer)obj[i++]);
		mensagem.setTitulo((String)obj[i++]);
		mensagem.setMensagem((String)obj[i++]);
		mensagem.getUsuarioDestino().setId((Integer)obj[i++]);
		mensagem.getUsuarioDestino().setLogin((String)obj[i++]);
		mensagem.getUsuarioDestino().getPessoa().setId((Integer)obj[i++]);
		mensagem.getUsuarioDestino().getPessoa().setNome((String)obj[i++]);
		mensagem.getUsuarioDestino().getUnidade().setId((Integer)obj[i++]);
		mensagem.getUsuarioDestino().getUnidade().setNome((String)obj[i++]);
		mensagem.getUsuarioDestino().getUnidade().setCodigo((Long)obj[i++]);
		mensagem.setDataCadastro((Date)obj[i++]);
		mensagem.getRemetente().setId((Integer)obj[i++]);
		mensagem.getRemetente().setLogin((String)obj[i++]);
		mensagem.getRemetente().getPessoa().setId((Integer)obj[i++]);
		mensagem.getRemetente().getPessoa().setNome((String)obj[i++]);
		mensagem.getRemetente().getUnidade().setId((Integer)obj[i++]);
		mensagem.getRemetente().getUnidade().setNome((String)obj[i++]);
		mensagem.getRemetente().getUnidade().setCodigo((Long)obj[i++]);
		mensagem.setLida(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setRemovidaRemetente(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setRemovidaDestinatario(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setAutomatica(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setConfLeitura(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setTipo((Integer)obj[i++]);
		mensagem.setIdArquivo((Integer)obj[i++]);
		mensagem.setLeituraObrigatoria(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setNomeArquivo((String)obj[i++]);
		mensagem.setRemovidaLixeiraDestinatario(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setRemovidaLixeiraRemetente(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.getUsuarioDestino().getUnidade().setSigla((String)obj[i++]);
		mensagem.getRemetente().getUnidade().setSigla((String)obj[i++]);

		return mensagem;
		
	}
	
	/**
	 * Recebe um Object gerado por uma consulta com projeção da caixa postal
     * e retorna um objeto mensagem com informações do remetente para exibição 
	 * na caixa de entrada e lixeira.
	 *
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	private Mensagem extractMensagemBoxProjecaoRemetente(Object[] obj) {
		
		Mensagem mensagem = new Mensagem();
		
		mensagem.setRemetente(new UsuarioGeral());
		mensagem.getRemetente().setUnidade(new UnidadeGeral());	

		int i = 0;
		mensagem.setId((Integer)obj[i++]);
		mensagem.setTitulo((String)obj[i++]);
		mensagem.getRemetente().setLogin((String)obj[i++]);
		mensagem.getRemetente().getUnidade().setCodigo((Long)obj[i++]);
		mensagem.getRemetente().getUnidade().setSigla((String)obj[i++]);
		mensagem.setDataCadastro((Date)obj[i++]);
		mensagem.setLida(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setLeituraObrigatoria(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setIdArquivo((Integer)obj[i++]);
		
		return mensagem;
		
	}
	
	/**
	 * Recebe um Object gerado por uma consulta com projeção da caixa postal
     * e retorna um objeto mensagem com informações do destinatario para exibição 
	 * na caixa de entrada e lixeira.
	 *
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	private Mensagem extractMensagemBoxSaidaProjecaoDestinatario(Object[] obj) {
		
		Mensagem mensagem = new Mensagem();
		
		mensagem.setUsuarioDestino(new UsuarioGeral());
		mensagem.getUsuarioDestino().setUnidade(new UnidadeGeral());	

		int i = 0;
		mensagem.setId((Integer)obj[i++]);
		mensagem.setTitulo((String)obj[i++]);
		mensagem.getUsuarioDestino().setLogin((String)obj[i++]);
		mensagem.getUsuarioDestino().getUnidade().setCodigo((Long)obj[i++]);
		mensagem.getUsuarioDestino().getUnidade().setSigla((String)obj[i++]);
		mensagem.setDataCadastro((Date)obj[i++]);
		mensagem.setLida(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setLeituraObrigatoria(BooleanUtils.toBoolean((Boolean)obj[i++]));
		mensagem.setIdArquivo((Integer)obj[i++]);

		return mensagem;
		
	}
	

	/**
	 * Retorna as mensagem da caixa de entrada de um usuário. O parâmetro
	 * tipoMensagem serve para buscar os chamados do SIPAC ou SIGAA. Os chamados
	 * só serão listados se o usuário for administrador
	 * 
	 * @param usuario
	 * @param page
	 * @param adm
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findCaixaEntrada(int usuario, int page, boolean incluirChamados) throws DAOException {
		Criteria c = getSession().createCriteria(Mensagem.class);

		if (incluirChamados) { // Busca chamados
			c.add(Restrictions.or(Restrictions.eq("usuarioDestino.id", usuario),
					Restrictions.or(Restrictions.or(Restrictions.eq("tipo",
							Mensagem.CHAMADO_SIGAA), Restrictions.eq("tipo",
									Mensagem.CHAMADO_SIGRH)), Restrictions.eq("tipo",
											Mensagem.CHAMADO_SIPAC))));
		} else {
			c.add(Restrictions.eq("usuarioDestino.id", usuario));
		}

		c.add(Restrictions.eq("removidaDestinatario", Boolean.FALSE));
		c.add(Restrictions.or(Restrictions.eq("removidaRemetente", Boolean.FALSE), Restrictions.isNull("removidaRemetente")));
		c.addOrder(Order.desc("dataCadastro"));
		// se incluir os chamados é pq o usuario é administrador. Sendo
		// administrador a caixa postal listará 50 msgs
		c.setFirstResult((page - 1)
				* (incluirChamados ? Mensagem.TAM_PAGINA_ADM
						: Mensagem.TAM_PAGINA));
		c.setMaxResults(incluirChamados ? Mensagem.TAM_PAGINA_ADM
				: Mensagem.TAM_PAGINA);

		return c.list();
	}

	/**
	 * Retorna mensagens da caixa de saída de um determinado usuário
	 * @param usuario
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findCaixaSaida(int usuario, int page) throws DAOException {
		Criteria c = getSession().createCriteria(Mensagem.class);
		c.add(Restrictions.eq("remetente.id", usuario));
		c.add(Restrictions.or(Restrictions.isNull("removidaRemetente"), Restrictions.eq("removidaRemetente", false)));
		c.add(Restrictions.eq("automatica", false));
		c.addOrder(Order.desc("dataCadastro"));

		c.setFirstResult((page - 1) * Mensagem.TAM_PAGINA);
		c.setMaxResults(Mensagem.TAM_PAGINA);

		return c.list();
	}
	
	/**
	 * Retorna as mensagem de um usuário na lixeira, se o usuário for
	 * administrador os chamados serão listados (é necessário passar como
	 * parâmetro o id do administrador do seu sistema)
	 * 
	 * @param usuario
	 * @param page
	 * @param administrador
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findLixeira(int usuario, int page, boolean incluirChamados) throws DAOException {
		String hql = "from Mensagem mensagem where (remetente.id =:usr and removidaDestinatario = trueValue() and usuarioDestino.id = :usr) or ( remetente.id = :usr and removidaRemetente = trueValue()) or" +
			"(usuarioDestino.id = :usr and (removidaRemetente is not null or removidaRemetente = trueValue())) or (usuarioDestino.id =:usr and removidaDestinatario = trueValue()) " +
			"order by dataCadastro desc";

		Query q = getSession().createQuery(hql);
		q.setInteger("usr", usuario);

		q.setFirstResult((page - 1) * Mensagem.TAM_PAGINA);
		q.setMaxResults(Mensagem.TAM_PAGINA);
		return q.list();
	}
	
	/**
	 * Retorna o número total de mensagens na pasta de um usuário
	 * @param pasta
	 * @param usuario
	 * @param incluirChamados
	 * @return
	 * @throws DAOException
	 */
	public int findTotalMensagensPasta(String pasta, int usuario, boolean incluirChamados) throws DAOException {
		StringBuffer hql = new StringBuffer("select count(*) from Mensagem m where ");

		if ("inbox".equals(pasta)) {
			if (incluirChamados) {
				hql.append("(m.usuarioDestino.id = :idUsuario or m.tipo = "
						+ Mensagem.CHAMADO_SIGAA + " or m.tipo = "
						+ Mensagem.CHAMADO_SIPAC + " or m.tipo = "
						+ Mensagem.CHAMADO_SIGRH + ")");
			} else {
				hql.append("m.usuarioDestino.id = :idUsuario ");
			}

			hql.append("and m.removidaDestinatario = falseValue() ");

		} else if ("sent".equals(pasta)) {
			hql.append("m.remetente.id = :idUsuario");
		} else {

			if (incluirChamados) {
				hql.append("(m.usuarioDestino.id = :idUsuario or m.tipo = "
						+ Mensagem.CHAMADO_SIGAA + " or m.tipo = "
						+ Mensagem.CHAMADO_SIPAC + " or m.tipo = "
						+ Mensagem.CHAMADO_SIGRH + ")");
			} else {
				hql.append("m.usuarioDestino.id = :idUsuario ");
			}

			hql.append("and (m.removidaRemetente = null or m.removidaRemetente = falseValue())  and m.removidaDestinatario = falseValue() ");

		}

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUsuario", usuario);
		return ((Number) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna todas as mensagens não lidas por um usuário
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findNaoLidasByUsuario(int usuario) throws DAOException {
		Criteria c = getCriteria(Mensagem.class);
		c.add(Restrictions.eq("usuarioDestino.id", usuario));
		c.add(Restrictions.eq("lida", Boolean.FALSE));
		c.add(Restrictions.eq("removidaDestinatario", Boolean.FALSE));
		return c.list();
	}

	/**
	 * Retorna as mensagens de resposta de um determinado usuário
	 * @param mensagem
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findRespostas(Mensagem mensagem) throws DAOException {
		Criteria c = getCriteria(Mensagem.class);
		c.add(Restrictions.eq("replyFrom", mensagem));
		c.addOrder(Order.desc("dataCadastro"));
		return c.list();
	}

	/**
	 * Este método busca o número de msgs pela data passada como argumento
	 * excluindo os chamados.
	 * 
	 * @param usuario
	 * @param data
	 * @param automatica -
	 *            Para informar se deve contar as mensagens geradas
	 *            automaticamente pelo sistema ou não
	 * @return
	 * @throws DAOException
	 */
	public int findTotalNaoLidaByUsuarioByData(int usuario, Date data, boolean automatica) throws DAOException {
		String hql = "select count(*) from Mensagem where usuarioDestino.id = :idUsuarioDestino "
			+ "and lida = :lida and removidaDestinatario = :removida and dataCadastro >= :dataCadastro "
			+ "and numChamado is null and automatica = :automatica";
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuarioDestino", usuario);
		q.setBoolean("lida", Boolean.FALSE);
		q.setBoolean("removida", Boolean.FALSE);
		q.setDate("dataCadastro", data);
		q.setBoolean("automatica", automatica);
		return ((Number) q.uniqueResult()).intValue();
	}

	/**
	 * Busca todas as mensagens ainda não lidas do usuário
	 * 
	 * @param usuario
	 * @param data
	 * @param automatica
	 * @return
	 * @throws DAOException
	 */
	public int findTotalNaoLidaByUsuario(int usuario) throws DAOException {
		String hql = "select count(*) from Mensagem where usuarioDestino.id = :idUsuarioDestino "
			+ "and lida = :lida and removidaDestinatario = :removida  ";
		Query q = getSession().createQuery(hql);

		q.setInteger("idUsuarioDestino", usuario);
		q.setBoolean("lida", Boolean.FALSE);
		q.setBoolean("removida", Boolean.FALSE);
		return Integer.parseInt(((Long) q.uniqueResult()).toString());
	}
	
	public List<Object[]> findAllTiposChamados() throws HibernateException, DAOException {
		return getSession().createSQLQuery("select * from iproject.tipochamado").list();
	}
	
	/**
	 * Retorna uma lista com os chamados patrimoniais não lidos.
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findChamadosPatrimoniaisNaoLidosByUsuario(int usuario) throws DAOException {
		Criteria c = getCriteria(Mensagem.class);
		c.add(Restrictions.eq("usuarioDestino.id", usuario));
		c.add(Restrictions.eq("lida", Boolean.FALSE));
		c.add(Restrictions.eq("removidaDestinatario", Boolean.FALSE));
		c.add(Restrictions.isNotNull("numChamadoPatrimonial"));
		return c.list();
	}
	
	/**
	 * Retorna os chamados patrimoniais de um usuário na lixeira
	 * 
	 * @param usuario
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findChamadosPatrimoniaisLidas(int usuario, int page) throws DAOException {
		String hql = "from Mensagem where usuarioDestino.id = :usr and numChamadoPatrimonial != null and lida != falseValue() and removidaDestinatario != trueValue() and (removidaRemetente = falseValue() or removidaRemetente is null) " +
			"order by dataCadastro desc";

		Query q = getSession().createQuery(hql);
		q.setInteger("usr", usuario);

		q.setFirstResult((page - 1) * Mensagem.TAM_PAGINA);
		q.setMaxResults(Mensagem.TAM_PAGINA);
		return q.list();
	}
	
	/**
	 * Retorna os chamados patrimoniais enviados
	 * @param usuario
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findChamadoPatrimonioCaixaSaida(int usuario, int page) throws DAOException {
		Criteria c = getSession().createCriteria(Mensagem.class);
		c.add(Restrictions.eq("remetente.id", usuario));
		c.add(Restrictions.or(Restrictions.isNull("removidaRemetente"), Restrictions.eq("removidaRemetente", false)));
		c.add(Restrictions.isNotNull("numChamadoPatrimonial"));
		c.addOrder(Order.desc("dataCadastro"));

		c.setFirstResult((page - 1) * Mensagem.TAM_PAGINA);
		c.setMaxResults(Mensagem.TAM_PAGINA);

		return c.list();
	}
	
	
	/**
	 * Retorna a quantidade de chamados patrimoniais existentes nas pastas
	 * @param pasta
	 * @param usuario
	 * @param incluirChamados
	 * @return
	 * @throws DAOException
	 */
	public int findTotalChamadosPatrimoniais(String pasta, int usuario) throws DAOException {
		StringBuffer hql = new StringBuffer("select count(*) from Mensagem m where ");
		if ("inbox".equals(pasta)) {
			hql.append("(m.usuarioDestino.id = :idUsuario and m.numChamadoPatrimonial != null and lida = falseValue())");
			hql.append("and m.removidaDestinatario = falseValue() ");
			hql.append("and (m.removidaRemetente = falseValue() or m.removidaRemetente is null)");
		} else if ("sent".equals(pasta)) {
			hql.append("m.remetente.id = :idUsuario");
		} else {
			hql.append("m.usuarioDestino.id = :idUsuario and m.numChamadoPatrimonial != null and m.removidaDestinatario = falseValue() and lida = trueValue() ");
			hql.append("and (removidaRemetente = falseValue() or removidaRemetente is null)");
		}

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUsuario", usuario);
		return ((Number) q.uniqueResult()).intValue();
	}
	
	//******NOVOS MÉTODOS SIGADMIN***********
	
	/**
	 * Retorna as mensagem da caixa de entrada de um usuário. O parâmetro
	 * tipoMensagem serve para buscar os chamados do SIPAC ou SIGAA. Os chamados
	 * só serão listados se o usuário for administrador
	 * 
	 * @param usuario
	 * @param page
	 * @param adm
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findCaixaEntradaAdmin(int usuario, PagingInformation information, boolean incluirChamados) throws DAOException {
		
		Criteria c = getSession().createCriteria(Mensagem.class);

		c.add(Restrictions.eq("usuarioDestino.id", usuario));

		c.add(Restrictions.eq("removidaDestinatario", Boolean.FALSE));
		c.add(Restrictions.or(Restrictions.eq("removidaRemetente", Boolean.FALSE), Restrictions.isNull("removidaRemetente")));
		c.addOrder(Order.desc("dataCadastro"));
		
		//dados da paginação
		if (information != null){

			Criteria cPaginacao = getSession().createCriteria(Mensagem.class);

			cPaginacao.add(Restrictions.eq("usuarioDestino.id", usuario));
			cPaginacao.add(Restrictions.eq("removidaDestinatario", Boolean.FALSE));
			cPaginacao.add(Restrictions.or(Restrictions.eq("removidaRemetente", Boolean.FALSE), Restrictions.isNull("removidaRemetente")));
			cPaginacao.setProjection(Projections.count("id"));
			
			information.setTotalRegistros(((Integer) cPaginacao.uniqueResult()));
						
			c.setFirstResult(information.getPaginaAtual() * information.getTamanhoPagina());
			c.setMaxResults(information.getTamanhoPagina());
		}
		
		return c.list();
	}
	
	/**
	 * Retorna mensagens da caixa de saída de um determinado usuário
	 * @param usuario
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findCaixaSaidaAdmin(int usuario, PagingInformation information) throws DAOException {
		Criteria c = getSession().createCriteria(Mensagem.class);
		c.add(Restrictions.eq("remetente.id", usuario));
		c.add(Restrictions.or(Restrictions.isNull("removidaRemetente"), Restrictions.eq("removidaRemetente", false)));
		c.add(Restrictions.eq("automatica", false));
		c.addOrder(Order.desc("dataCadastro"));

		//dados da paginação
		if (information != null){

			Criteria cPaginacao = getSession().createCriteria(Mensagem.class);

			cPaginacao.add(Restrictions.eq("remetente.id", usuario));
			cPaginacao.add(Restrictions.or(Restrictions.isNull("removidaRemetente"), Restrictions.eq("removidaRemetente", false)));
			cPaginacao.add(Restrictions.eq("automatica", false));
			cPaginacao.setProjection(Projections.count("id"));
			
			
			information.setTotalRegistros(((Integer) cPaginacao.uniqueResult()));
						
			c.setFirstResult(information.getPaginaAtual() * information.getTamanhoPagina());
			c.setMaxResults(information.getTamanhoPagina());
		}

		return c.list();
	}
	
	/**
	 * Retorna as mensagem de um usuário na lixeira, se o usuário for
	 * administrador os chamados serão listados (é necessário passar como
	 * parâmetro o id do administrador do seu sistema)
	 * 
	 * @param usuario
	 * @param page
	 * @param administrador
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findLixeiraAdmin(int usuario, PagingInformation information, boolean incluirChamados) throws DAOException {

// 		Antiga consulta, sem considerar mensagens removidas da lixeira		
//		String consulta = "from Mensagem mensagem where (remetente.id =:usr and removidaDestinatario = trueValue() and usuarioDestino.id = :usr) or ( remetente.id = :usr and removidaRemetente = trueValue()) or" +
//		"(usuarioDestino.id = :usr and (removidaRemetente is not null or removidaRemetente = trueValue())) or (usuarioDestino.id =:usr and removidaDestinatario = trueValue()) ";

//     Nova consulta, considerando mensagens removidas da lixeira
	   String consulta = "from Mensagem mensagem where ( (usuarioDestino.id =:usr and remetente.id =:usr and (removidaDestinatario = trueValue() or removidaRemetente = trueValue()) and ((removidaLixeiraDestinatario = falseValue() and removidaLixeiraRemetente = falseValue()) or (removidaLixeiraDestinatario is null and removidaLixeiraRemetente is null) ) ) " +
				" or (remetente.id =:usr and usuarioDestino.id <>:usr and removidaRemetente = trueValue() and (removidaLixeiraRemetente is null or removidaLixeiraRemetente = falseValue())) " +
				"or (usuarioDestino.id =:usr and remetente.id <>:usr and removidaDestinatario = trueValue() and (removidaLixeiraDestinatario is null or removidaLixeiraDestinatario = falseValue())) )";
		
		Query q = getSession().createQuery(consulta + " order by dataCadastro desc ");
		q.setInteger("usr", usuario);

		//dados da paginação
		if (information != null){

			String hqlPaginacao = "select count(mensagem.id) " + consulta;
			Query count = getSession().createQuery(hqlPaginacao);
			count.setInteger("usr", usuario);
			information.setTotalRegistros(((Long) count.uniqueResult()).intValue());
			q.setFirstResult(information.getPaginaAtual() * information.getTamanhoPagina());
			q.setMaxResults(information.getTamanhoPagina());
			
		}
		
		return q.list();
	}

	/**
	 * Busca todas as mensagens que são chamados criados pelo usuário
	 * cujo id foi passado como parâmetro.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<Mensagem> findChamadosByUsuario(int id) throws DAOException {
		Criteria criteria = getCriteria(Mensagem.class);
		criteria.add(Restrictions.eq("remetente.id", id)); 
		criteria.add(Restrictions.in("tipo", new Integer[] { Mensagem.CHAMADO_SIGAA, Mensagem.CHAMADO_SIGRH, Mensagem.CHAMADO_SIPAC, Mensagem.CHAMADO_IPROJECT, Mensagem.CHAMADO_SIGADMIN }));
		criteria.addOrder(Order.desc("dataCadastro"));
		
		List<Mensagem> lista = criteria.list();
		
		SqlRowSet rowSetTiposChamado = getJdbcTemplate().queryForRowSet("select id, denominacao from iproject.tipochamado");
		Map<Integer, String> tiposChamadoMap = new HashMap<Integer, String>();
		while (rowSetTiposChamado.next()) {
			String denominacao = rowSetTiposChamado.getString("denominacao");
			Integer idTipo = rowSetTiposChamado.getInt("id");
			tiposChamadoMap.put(idTipo, denominacao);
		}
		
		for (Mensagem msg : lista) {
			String denominacao = tiposChamadoMap.get(msg.getTipoChamado());
			if (!ValidatorUtil.isAllEmpty(denominacao)) {
				msg.setTipoChamadoDesc(denominacao);
			} else {
				msg.setTipoChamadoDesc("");
			}
		}
		
		SqlRowSet rowSetLogs = getJdbcTemplate().queryForRowSet("select log.log, tarefa.numchamado from iproject.tarefa tarefa inner join iproject.log_tarefa log on log.id_tarefa = tarefa.id_tarefa where id_tipo_log = 14 order by log.data desc");
		Map<Integer, List<String>> logsMap = new HashMap<Integer, List<String>>();
		while (rowSetLogs.next()) {
			String log = rowSetLogs.getString("log");
			Integer numChamado = rowSetLogs.getInt("numchamado");
			
			List<String> logs = logsMap.get(numChamado);
			if (logs == null) logs = new ArrayList<String>();
			logs.add(log);
			logsMap.put(numChamado, logs);
		}
		
		for (Mensagem msg : lista) {
			List<String> logs = logsMap.get(msg.getNumChamado());
			if (!ValidatorUtil.isEmpty(logs)) {
				msg.setRespostasCriadorChamado(logs);
			}
		}
		
		return lista;
	}

	/**
	 * Busca os tipos de chamado existentes.
	 * @return
	 */
	public Map<Integer, String> findTiposChamado() {
		return (Map<Integer, String>) getJdbcTemplate().query("select id, denominacao from iproject.tipochamado order by denominacao", MAP_EXTRACTOR);
	}

	
	public void marcarMsgComoLidaLote(int[] idMensagens) {
			
		String inIdsMsgMarcarComoLida = UFRNUtils.gerarStringIn(idMensagens);
			
		String sql = "update comum.mensagem set lida = trueValue(), data_leitura = "+SQLDialect.now()+" where id_mensagem in " + inIdsMsgMarcarComoLida;
		getJdbcTemplate().update(sql);
		
	}

	public void marcarTodasMsgComoLidas(int idUsuario) {
		
		String sql = "update comum.mensagem set lida = trueValue(), data_leitura = "+SQLDialect.now()+" where id_usuario = " + idUsuario;
		getJdbcTemplate().update(sql);
		
	}

	/**
	 * @param id
	 * @param idMensagem
	 */
	public void marcarComoNaoLiada(int id, int[] idMensagem) {
					
		String inIdsMsgMarcarComoLida = UFRNUtils.gerarStringIn(idMensagem);
			
		String sql = "update comum.mensagem set lida = falseValue(), data_leitura = NULL where id_mensagem in " + inIdsMsgMarcarComoLida;
		getJdbcTemplate().update(sql);
		
		
	}
	
}
