/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/12/2006'
 *
 */
package br.ufrn.sigaa.negocio;

import java.util.Collection;
import java.util.Date;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.projetos.HistoricoSituacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe auxiliar para as operações sobre a entidade Projeto e derivadas 
 * @author Victor Hugo
 *
 */
public class ProjetoHelper {

	/**
	 * Método para alterar a situação do projeto.
	 * Ele altera a situação do projeto e registra no histórico a mudança
	 * 
	 * @param dao 
	 * @param situacao
	 * @param projeto
	 * @throws DAOException
	 */
	public static void gravarHistoricoSituacaoProjeto(int idSituacaoAtual, int idProjeto, RegistroEntrada registroEntrada) throws DAOException{
		HistoricoSituacaoProjetoDao dao = DAOFactory.getInstance().getDAO(HistoricoSituacaoProjetoDao.class);
		try{
			
			TipoSituacaoProjeto situacaoAtual = dao.findByPrimaryKey(idSituacaoAtual, TipoSituacaoProjeto.class);
			
			Projeto projeto = new Projeto(idProjeto);
			HistoricoSituacaoProjeto historico = new HistoricoSituacaoProjeto();
			historico.setSituacaoProjeto(situacaoAtual);
			historico.setData(new Date());
			historico.setRegistroEntrada(registroEntrada);
			historico.setProjeto(projeto);
			
				Integer quantidadeRegistrosHistorico = dao.quantidadeRegistrosHistorico(projeto.getId());
				TipoSituacaoProjeto situacaoAnterior = dao.findUltimaSituacaoHistorico(projeto.getId());
				
				if((quantidadeRegistrosHistorico > 0) && (situacaoAnterior.getId() != idSituacaoAtual)) {
					
					projeto = dao.findByPrimaryKey(idProjeto, Projeto.class, 
							"id", "titulo", "coordenador.id", "coordenador.servidor.id", 
							"coordenador.servidor.pessoa.id", "coordenador.servidor.pessoa.nome", "coordenador.servidor.pessoa.email");
					
					if (ValidatorUtil.isNotEmpty(projeto)) {
						projeto.setSituacaoProjeto(situacaoAtual);
						
						if (ValidatorUtil.isNotEmpty(projeto.getCoordenador()) && ValidatorUtil.isNotEmpty(projeto.getCoordenador().getServidor())) {
							notificarSituacaoHistoricoCoordenador(projeto.getTitulo(), projeto.getCoordenador().getServidor().getPessoa(), situacaoAnterior.getDescricao(), situacaoAtual.getDescricao());
						}
					}
				}
				
			dao.create(historico);
		}finally{
			dao.close();	
		}
	}
	
	/**
	 * Método utilizado notificar a situação do histórico do coordenador
	 * 
	 * @param historico
	 * @param projeto
	 * @param pessoa
	 * @param situacaoAnterior
	 */
	private static void notificarSituacaoHistoricoCoordenador(String titulo, Pessoa coordenador, String situacaoAnterior, String situacaoAtual){
		String mensagem = "Caro(a) Coordenador(a) " + coordenador.getNome() + ", <br /><br /> " +
		"O projeto '" + titulo + "' passou da situação " + situacaoAnterior + " para a seguinte situação: <b>" + situacaoAtual + ".</b> " +
		"<br /><br /> ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA SIGAA. POR FAVOR, NÃO RESPONDÊ-LO.";
		
		// enviando e-mail.
		MailBody email = new MailBody();
		email.setAssunto("[SIGAA] Notificação de Mudança da Situação do Projeto");
		email.setContentType(MailBody.HTML);
		email.setNome(coordenador.getNome());
		email.setEmail(coordenador.getEmail());
		email.setMensagem(mensagem);
		Mail.send(email);
		
	}
	
	/**
	 * Atualiza os campos transientes que informam se os projetos vinculados
	 * foram enviados para aprovação.
	 * 
	 * @param projeto
	 * @param dao
	 * @throws DAOException
	 */
	public static void atualizarPendenciasProjetoBase(Projeto projeto, ProjetoDao dao) throws DAOException {
		//Extensão
		projeto.setCursoExtensaoSubmetido(dao.isProjetoExtensaoSubmetido(projeto.getId(), TipoAtividadeExtensao.CURSO));
		projeto.setEventoExtensaoSubmetido(dao.isProjetoExtensaoSubmetido(projeto.getId(), TipoAtividadeExtensao.EVENTO));
		projeto.setProgramaExtensaoSubmetido(dao.isProjetoExtensaoSubmetido(projeto.getId(), TipoAtividadeExtensao.PROGRAMA));
		projeto.setProjetoExtensaoSubmetido(dao.isProjetoExtensaoSubmetido(projeto.getId(), TipoAtividadeExtensao.PROJETO));
		
		//Ensino
		projeto.setProgramaMonitoriaSubmetido(dao.isProjetoEnsinoSubmetido(projeto.getId(), TipoProjetoEnsino.PROJETO_DE_MONITORIA));
		projeto.setMelhoriaQualidadeEnsinoSubmetido(dao.isProjetoEnsinoSubmetido(projeto.getId(), TipoProjetoEnsino.PROJETO_PAMQEG));
		
		//Pesquisa
		projeto.setIniciacaoCientificaSubmetido(dao.isProjetoPesquisaSubmetido(projeto.getId()));		
		projeto.setApoioGrupoPesquisaSubmetido(true);
		projeto.setApoioNovosPesquisadoresSubmetido(true);
		//obj.setApoioGrupoPesquisaSubmetido(dao.isProjetoPesquisaSubmetido(obj.getId()));
		//obj.setApoioNovosPesquisadoresSubmetido(dao.isProjetoPesquisaSubmetido(obj.getId()));	

	}
	
	
	/**
	 * Sincroniza a situação do projeto base com o projeto de monitoria informado.
	 * 
	 * @param projeto
	 * @param dao
	 * @throws DAOException
	 */
	public static void sincronizarSituacaoComProjetoBase(GenericDAO dao, ProjetoEnsino pm) throws DAOException {
	    /** @negocio: no projeto isolado o projeto base recebe a mesma situação do projeto de monitoria. */
	    if (pm.isProjetoIsolado()) {
		dao.updateFields(Projeto.class, pm.getProjeto().getId(), 
			new String[] {"situacaoProjeto.id","ativo"}, 
			new Object[] {pm.getSituacaoProjeto().getId(), pm.isAtivo()});
	    }
	}
	
	/**
	 * Sincroniza a situação do projeto base com a ação de extensao informada.
	 * 
	 * @param acao
	 * @param dao
	 * @throws DAOException
	 */
	public static void sincronizarSituacaoComProjetoBase(GenericDAO dao, AtividadeExtensao acao) throws DAOException {
		dao.updateFields(Projeto.class, acao.getProjeto().getId(), 
			new String[] {"situacaoProjeto.id","ativo"}, 
			new Object[] {acao.getSituacaoProjeto().getId(), acao.isAtivo()});
	}
	
	
	/**
	 * Gera um número institucional para o projeto informado.
	 * 
	 * @param atividade
	 * @throws DataAccessException
	 * @throws DAOException
	 */
	public static void gerarNumeroInstitucional(ProjetoDao dao, Projeto projeto) throws DataAccessException, DAOException {
	    Integer numeroInstitucional;
	    try {
		numeroInstitucional = (Integer)dao.getJdbcTemplate()
		.queryForObject(" SELECT proj.numeroInstitucional FROM projetos.projeto as proj" +
			" WHERE proj.id_projeto = ? ", new Object[] {projeto.getId()}, Integer.class);			
	    } catch (EmptyResultDataAccessException e) {
		numeroInstitucional = null;
	    }

	    if (numeroInstitucional == null) {
		projeto.setNumeroInstitucional(dao.findNextNumeroInstitucional(projeto.getAno()));
	    }
	}

	
	/**
	 * Verifica se o servidor informado é chefe da Unidade Proponente do Projeto.
	 */
	public static boolean isChefeUnidadeProponenteProjeto(UsuarioGeral usuario, Projeto projeto) throws DAOException {
		Boolean chefe = usuario.isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.CHEFE_UNIDADE);
		Boolean diretor = usuario.isUserInRole(SigaaPapeis.DIRETOR_CENTRO);
		boolean result = false;

		if (chefe || diretor) {	
			UnidadeDAOImpl undDao = new UnidadeDAOImpl(Sistema.COMUM);
			try {
				//todas as unidades onde o usuário atual é chefe
				Collection<UnidadeGeral> unidadesQueChefia = undDao.findUnidadesByResponsavel(usuario.getIdServidor(), new Character[] {NivelResponsabilidade.CHEFE, NivelResponsabilidade.VICE, NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO});			
				if ((unidadesQueChefia != null) && (!unidadesQueChefia.isEmpty())) {				
					for (UnidadeGeral unidadeGeral : unidadesQueChefia) {
						if (unidadeGeral.equals(projeto.getUnidade())) {								
							result = true;
							break;
						}
					}				
				}			
			}finally {
				undDao.close();
			}
		}
		return result;
	}

}