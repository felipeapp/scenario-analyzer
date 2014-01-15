/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 *  Created on 26/0/2007
 */
package br.ufrn.sigaa.caixa_postal.dominio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.web.Progressable;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe utilizada para seta as informações das mensagens para listar na caixa
 * de entrada. <br />
 * Significado das siglas: <br />
 * ARQ - Arquitetura <br/>
 * SIPAC - Sistema Integrado de Patrimônio, Administração e Contratos
 * 
 * @author Gleydson Lima
 * 
 */
public class MensagensHelper {

	/**
	 * Método responsável por preencher a caixa de entrada do usuário com
	 * mensagens do sistema. Retorna 'true' se tiver mensagens.
	 * 
	 * @throws DAOException
	 * @throws ParseException
	 */
	public static boolean preencheCaixaEntrada(HttpServletRequest req,
			UsuarioGeral usuario) throws DAOException, ParseException {

		MensagemDAO mensagemDAO = new MensagemDAO();

		boolean temMensagens = true;
		boolean incluirChamado = false;

		try {
			Progressable progress = getLogonProgress(req);

			// Mensagens de Arq (sem os objetos Usuário,Papel, Unidade ...)
			Collection<br.ufrn.arq.caixa_postal.Mensagem> msgs = mensagemDAO
					.findCaixaEntrada(usuario.getId(), 1, incluirChamado);
			progress.increment();

			// conversão da mensagem de Arq para a mensagem do SIPAC
			Collection<Mensagem> mensagens = decorateMensagem(msgs, false);
			progress.increment();

			int novas = mensagemDAO.findTotalNaoLidaByUsuario(usuario.getId());
			progress.increment();
			if (novas == 0)
				temMensagens = false;

			// Cálculos para paginação
			int total = mensagemDAO.findTotalMensagensPasta("inbox", usuario
					.getId(), incluirChamado);
			progress.increment();
			int ultimaMsg = (incluirChamado ? Mensagem.TAM_PAGINA_ADM
					: Mensagem.TAM_PAGINA);

			if (ultimaMsg > total)
				ultimaMsg = total;

			req.setAttribute("primeiraMsgPagina", 1);
			req.setAttribute("ultimaMsgPagina", ultimaMsg);
			req.setAttribute("totalMsgPasta", total);
			req.setAttribute("mensagens", mensagens);

			req.setAttribute("totalPaginas", (int) Math.ceil(((double) total)
					/ (incluirChamado ? Mensagem.TAM_PAGINA_ADM
							: Mensagem.TAM_PAGINA)));

			// Total de mensagens não lidas pelo usuário
			req.getSession().setAttribute("qtdNovas", novas);
			// Total de mensagens não lidas pelo usuário a partir de 2007,
			// desconsiderando chamados e mensagens automáticas
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 31);
			c.set(Calendar.MONTH, 12);
			c.set(Calendar.YEAR, 2006);
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);

			Date data = c.getTime();
			int total2 = mensagemDAO.findTotalNaoLidaByUsuarioByData(usuario
					.getId(), data, false);
			progress.increment();
			req.getSession().setAttribute("qtdNaoLida", total2);

			return temMensagens;

		} finally {
			mensagemDAO.close();
		}

	}

	/**
	 * Método responsável por retornar a quantidade de mensagens não lidas de um
	 * determinado usuário.
	 * 
	 */
	public static int qtdNaoLidaCaixaEntrada(int idUsuario) {
		return new JdbcTemplate(Database.getInstance().getComumDs())
				.queryForInt("select count(*) from comum.mensagem where id_usuario = "
						+ idUsuario
						+ " and removida_destinatario = falseValue() "
						+ "and (removida_remetente = falseValue() OR removida_remetente is NULL) and lida = falseValue()");
	}

	/**
	 * Verifica se o usuário poderá acessar a caixa postal. Apenas usuários
	 * ativos no banco comum poderão fazer o acesso. Chamado por
	 * /include/cabecalho.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public static Boolean acessoCaixaPostal(int idUsuario) throws DAOException {
		if( idUsuario > 0 ){
			return (Boolean) new JdbcTemplate(Database.getInstance().getComumDs()).queryForObject("select inativo from comum.usuario where id_usuario = ?",new Object[]{idUsuario},new RowMapper() {
				
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getBoolean("inativo");
				}
			});
		}else{
			return true;
		}
	}

	/**
	 * Método responsável por preencher a caixa de saída do usuário com
	 * mensagens do sistema.
	 * 
	 * @throws DAOException
	 * 
	 */
	public static void preencheCaixaSaida(HttpServletRequest req,
			UsuarioGeral usuario) throws DAOException {

		MensagemDAO mensagemDAO = new MensagemDAO();
		try {

			// Mensagens de Arq (sem os objetos Usuário,Papel, Unidade ...)
			Collection<br.ufrn.arq.caixa_postal.Mensagem> msgs = mensagemDAO
					.findEnviadasByUsuario(usuario.getId());

			// conversão da mensagem de Arq para a mensagem do SIPAC
			Collection<Mensagem> mensagens = decorateMensagem(msgs, false);

			req.setAttribute("mensagens", mensagens);

			if (msgs.size() == 0) {
				req.setAttribute("msgEmpty", Boolean.TRUE);
			}

			int naoLidas = 0;

			for (Mensagem msg : mensagens) {
				if (!msg.isLida()) {
					naoLidas++;
				}
			}

			req.getSession().removeAttribute("novasMensagens");

			req.getSession().setAttribute("qtdNovas", naoLidas);

		} finally {
			mensagemDAO.close();
		}

	}

	/**
	 * Método responsável por persistir uma mensagem do SIGAA.
	 * 
	 * @throws DAOException
	 */
	public void mensagem(String assunto, String mensagem, Usuario destino,
			Usuario remetente) throws DAOException {

		Mensagem msg = new Mensagem();
		msg.setPapel(null);
		msg.setMensagem(mensagem);
		msg.setRemetente(remetente);
		msg.setDataCadastro(new Date());
		msg.setTipo(br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM);
		msg.setTitulo(assunto);

		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		try {
			dao.create(msg);
		} finally {
			dao.close();
		}

	}

	/**
	 * Converte as mensagens de Arq para as do Sipac
	 * 
	 * @param msgs
	 * @return
	 * @throws DAOException
	 */
	public static Collection<Mensagem> decorateMensagem(
			Collection<br.ufrn.arq.caixa_postal.Mensagem> msgs,
			boolean incluirRespostas) throws DAOException {

		Collection<Mensagem> mensagens = new ArrayList<Mensagem>();
		Iterator<br.ufrn.arq.caixa_postal.Mensagem> it = msgs.iterator();

		Mensagem mensagem = null;
		while (it.hasNext()) {
			br.ufrn.arq.caixa_postal.Mensagem msg = it.next();

			mensagem = decorateMensagem(msg, incluirRespostas);

			mensagens.add(mensagem);
		}
		return mensagens;
	}

	/**
	 * Converte a mensagem de Arq para a do Sipac
	 * 
	 * @param msgs
	 * @return
	 * @throws DAOException
	 */
	public static Mensagem decorateMensagem(
			br.ufrn.arq.caixa_postal.Mensagem msg, boolean incluirRespostas)
			throws DAOException {

		Mensagem mensagem = null;

		mensagem = new Mensagem();

		mensagem.setConfLeitura(msg.isConfLeitura());
		mensagem.setDataCadastro(msg.getDataCadastro());
		mensagem.setDataLeitura(msg.getDataLeitura());
		mensagem.setDescricao(msg.getDescricao());
		mensagem.setDestinatarios(msg.getDestinatarios());
		mensagem.setId(msg.getId());
		mensagem.setIdMensagem(msg.getIdMensagem());
		mensagem.setLeituraObrigatoria(msg.isLeituraObrigatoria());
		mensagem.setLida(msg.isLida());
		mensagem.setMensagem(msg.getMensagem());
		mensagem.setNumChamado(msg.getNumChamado());
		mensagem.setRemovidaDestinatario(msg.isRemovidaDestinatario());
		mensagem.setRemovidaRemetente(msg.isRemovidaRemetente());
		mensagem.setSistema(msg.getSistema());
		mensagem.setSubsistema(msg.getSubsistema());
		mensagem.setTipo(msg.getTipo());
		mensagem.setTitulo(msg.getDescricao());
		mensagem.setUsuarioLogado(msg.getUsuario());
		mensagem.setAutomatica(msg.isAutomatica());

		if (msg.getReplyFrom() != null) {
			Mensagem mensagemResposta = new Mensagem();
			mensagemResposta.setId(msg.getReplyFrom().getId());
			mensagem.setReplyFrom(mensagemResposta);
		}

		UsuarioDao userDAO = DAOFactory.getInstance().getDAO(UsuarioDao.class);
		try {
			// Busca os objetos que possuem apenas o inteiro que representa o
			// id.

			mensagem.setUsuario(userDAO.findUsuarioLeve(msg.getUsuarioDestino()
					.getId()));
			if (msg.getPapel() != null && msg.getPapel().getId() != 0) {
				mensagem.setPapel(userDAO.findByPrimaryKey(msg.getIdPapel(),
						Papel.class));
			}
			mensagem.setRemetente(userDAO.findUsuarioLeve(msg.getRemetente()
					.getId()));
			if (incluirRespostas) {
				// Se for para incluir respostas da mensagem
				mensagem.setRespostas(decorateMensagem(msg.getRespostasArq(),
						true));
			}
		} finally {
			userDAO.close();
		}

		return mensagem;
	}

	/**
	 * Converte as mensagens do Sipac para as de Arq
	 * 
	 * @param msgs
	 * @return
	 * @throws DAOException
	 */
	public static Collection<br.ufrn.arq.caixa_postal.Mensagem> msgSigaaToMsgArq(
			Collection<Mensagem> msgs) throws DAOException {

		Collection<br.ufrn.arq.caixa_postal.Mensagem> mensagens = new ArrayList<br.ufrn.arq.caixa_postal.Mensagem>();
		if (msgs != null && !msgs.isEmpty()) {
			Iterator<Mensagem> it = msgs.iterator();

			br.ufrn.arq.caixa_postal.Mensagem mensagem = null;
			while (it.hasNext()) {
				Mensagem msg = it.next();

				mensagem = msgSigaaToMsgArq(msg);

				mensagens.add(mensagem);
			}
		}
		return mensagens;
	}

	/**
	 * Converte a mensagem do Sipac para a ARQ
	 * 
	 * @param msgs
	 * @return
	 * @throws DAOException
	 */
	public static br.ufrn.arq.caixa_postal.Mensagem msgSigaaToMsgArq(
			Mensagem msg) throws DAOException {

		br.ufrn.arq.caixa_postal.Mensagem mensagem = null;

		mensagem = new br.ufrn.arq.caixa_postal.Mensagem();

		mensagem.setConfLeitura(msg.isConfLeitura());
		mensagem.setCodMovimento(msg.getCodMovimento());
		mensagem.setDataCadastro(msg.getDataCadastro());
		mensagem.setDataLeitura(msg.getDataLeitura());
		mensagem.setDescricao(msg.getDescricao());
		mensagem.setDestinatarios(msg.getDestinatarios());
		mensagem.setId(msg.getId());
		mensagem.setIdMensagem(msg.getIdMensagem());
		mensagem.setLeituraObrigatoria(msg.isLeituraObrigatoria());
		mensagem.setLida(msg.isLida());
		mensagem.setMensagem(msg.getMensagem());
		mensagem.setNumChamado(msg.getNumChamado());
		mensagem.setRemovidaDestinatario(msg.isRemovidaDestinatario());
		mensagem.setRemovidaRemetente(msg.isRemovidaRemetente());
		mensagem.setSistema(msg.getSistema());
		mensagem.setSubsistema(msg.getSubsistema());
		mensagem.setTipo(msg.getTipo());
		mensagem.setTitulo(msg.getDescricao());
		mensagem.setSistema(Sistema.SIGAA);
		mensagem.setAutomatica(msg.isAutomatica());
		mensagem.setTipoChamado(msg.getTipoChamado());

		if (msg.getReplyFrom() != null) {
			br.ufrn.arq.caixa_postal.Mensagem mensagemResposta = new br.ufrn.arq.caixa_postal.Mensagem();
			mensagemResposta.setId(msg.getReplyFrom().getId());
			mensagem.setReplyFrom(mensagemResposta);
		}

		if (msg.getUsuario() != null) {
			mensagem.setUsuarioDestino(msg.getUsuario());
		}
		if (msg.getPapel() != null) {
			mensagem.setIdPapel(msg.getPapel().getId());
		}
		if (msg.getRemetente() != null) {
			mensagem.setRemetente(msg.getRemetente());
		}

		mensagem.setRespostasArq(msgSigaaToMsgArq(msg.getRespostas()));

		return mensagem;
	}

	/**
	 * Método responsável por retornar uma interface que será implementada por
	 * objetos cujo estado será visualizado através de uma barra de progresso.
	 * 
	 */
	public static Progressable getLogonProgress(HttpServletRequest req) {
		WebApplicationContext ac = WebApplicationContextUtils
				.getWebApplicationContext(req.getSession().getServletContext());
		Progressable progress = (Progressable) ac.getBean("logonProgress");
		if (progress == null) {
			progress = new Progressable() {
				public int getCurrent() {
					return 0;
				}

				public int getCurrentPercent() {
					return 0;
				}

				public int getTotal() {
					return 0;
				}

				public void increment() {
				}

				public boolean isFinished() {
					return false;
				}

				public void reset() {
				}
			};
		}

		return progress;
	}

}