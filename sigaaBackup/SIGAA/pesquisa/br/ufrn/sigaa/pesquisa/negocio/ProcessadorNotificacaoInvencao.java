/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.InteressadoDTO;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.integracao.dto.UnidadeDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.ProtocoloRemoteService;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CodigoNotificacaoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.Invencao;
import br.ufrn.sigaa.pesquisa.dominio.Inventor;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusNotificacaoInvencao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável pelas operações sobre as notificações de invenção.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorNotificacaoInvencao extends ProcessadorCadastro {

	/**
	 * Responsável pela execução do processamento de Notificação de Invenção
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);		
		
		Invencao invencao = null;
		
		if(mov.getCodMovimento().equals(SigaaListaComando.GRAVAR_INVENCAO)){
			invencao = gravarInvencao((MovimentoCadastro) mov);
		} else if(mov.getCodMovimento().equals(SigaaListaComando.REMOVER_INVENCAO)){
			invencao = removerInvencao((MovimentoCadastro) mov);
		} else if(mov.getCodMovimento().equals(SigaaListaComando.NOTIFICAR_INVENCAO)){
			invencao = notificarInvencao((MovimentoCadastro) mov);
		}
		
		return invencao;
	}
	/**
	 * Método responsável pela notificação da Invenção
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Invencao notificarInvencao(MovimentoCadastro mov) throws ArqException, NegocioException {
		Invencao invencao = (Invencao) mov.getObjMovimentado();
		
		if(invencao.getNumeroProtocolo() == null && invencao.getAnoProtocolo() == null && Sistema.isSipacAtivo()){
			ProcessoDTO processo = criarProcesso(mov, invencao);
			invencao.setNumeroProtocolo(processo.getNumProtocolo());
			invencao.setAnoProtocolo(processo.getAno());
		}
		
		return (Invencao) alterar(mov);
	}

	/**
	 * Responsável pela criação de um novo processo de Notificação Invenção 
	 * 
	 * @param mov
	 * @param invencao
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private ProcessoDTO criarProcesso(MovimentoCadastro mov, Invencao invencao)
			throws ArqException, NegocioException {
		ParametroHelper params = ParametroHelper.getInstance();
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		
		ProcessoDTO processo = new ProcessoDTO();
		processo.setAssunto(invencao.getCodigo().toString());
		processo.setObservacao("[NOTIFICAÇÃO DE INVENÇÃO SUBMETIDA ATRAVÉS DO SIGAA]");
		UnidadeDTO unidadeOrigem = new UnidadeDTO();
		unidadeOrigem.setId( ((Usuario)mov.getUsuarioLogado()).getVinculoAtivo().getUnidade().getId());
		processo.setUnidadeOrigem(unidadeOrigem);
		processo.setIdUsuario(usuario.getId());
		processo.setIdtipoProcesso(params.getParametroInt(ParametrosPesquisa.ID_TIPO_PROCESSO_NOTIFICACAO_INVENCAO));
		
		InteressadoDTO interessado = new InteressadoDTO();
		interessado.setAtivo(true);
		interessado.setIdentificador(String.valueOf(usuario.getServidorAtivo().getSiape()));
		interessado.setNome(usuario.getNome());
		interessado.setIdTipo(params.getParametroInt(ParametrosPesquisa.ID_TIPO_INTERESSADO_SERVIDOR));
		
		GenericDAO dao = getGenericDAO(mov);
		Connection con = null;
		try {
			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com SIPAC!");
			
			Pessoa p = dao.findByPrimaryKey(usuario.getPessoa().getId(), Pessoa.class, "cpf_cnpj");
			ResultSet rs = con.createStatement().executeQuery("select id_pessoa from comum.pessoa where cpf_cnpj = "+p.getCpf_cnpj() + " " + BDUtils.limit(1));
			if(rs.next())
				interessado.setIdPessoa(rs.getInt(1));
			else
				throw new NegocioException("Não foi possível criar a capa de processo pois não foi localizada a pessoa do interessado com CPF: "+ Formatador.getInstance().formatarCPF(p.getCpf_cnpj()));
		} catch (SQLException e) {
			throw new ArqException(e);
		} finally {
			dao.close();
		}
		
		interessado.setIdServidor(usuario.getServidorAtivo().getId());
		
		processo.setInteressados(new HashSet<InteressadoDTO>());
		processo.getInteressados().add(interessado);
		
		br.ufrn.integracao.dto.MovimentoDTO movimento = new br.ufrn.integracao.dto.MovimentoDTO();
		movimento.setUsuarioOrigem(mov.getUsuarioLogado().getId());
		
		UnidadeDTO unidadeOrigemMov = new UnidadeDTO();
		unidadeOrigemMov.setId( ((Usuario)mov.getUsuarioLogado()).getVinculoAtivo().getUnidade().getId());
		movimento.setUndOrigem(unidadeOrigemMov);
		
		UnidadeDTO unidadeDestinoMov = new UnidadeDTO();
		unidadeDestinoMov.setId(params.getParametroInt(ParametrosPesquisa.ID_UNIDADE_NIT));
		movimento.setUndDestino(unidadeDestinoMov);
		
		try {
			ProtocoloRemoteService serviceProtocolo = getBean("protocoloInvoker", mov);
			processo = serviceProtocolo.cadastrarProcesso(processo, movimento);
		} catch (NegocioRemotoException e) {
			throw new NegocioException(e.getMessage());			
		} catch (Exception e) {
			//Caso onde o SIPAC esta ativo no banco mas mesmo assim ocorre erro na comunicação.
			throw new NegocioException("Não foi possível criar o processo devido a uma falha de comunicação com o " + RepositorioDadosInstitucionais.get("siglaSipac") +   ". Entre em contato com a administração do sistema.");
		}
		
		return processo;
	}

	/**
	 * Método esse responsável pela gravação de uma nova notificação Invenção
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private Invencao gravarInvencao(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		Invencao invencao = (Invencao) mov.getObjMovimentado();
		
		Collection<Inventor> inventores = invencao.getInventores();
		if(!ValidatorUtil.isEmpty(inventores)) {
			for(Inventor i : inventores) {
				if(ValidatorUtil.isEmpty(i.getDocente())) {
					i.setDocente(null);
				}
				if(ValidatorUtil.isEmpty(i.getDiscente())) {
					i.setDiscente(null);
				}
				if(ValidatorUtil.isEmpty(i.getServidor())) {
					i.setServidor(null);
				}
				if(i.getDocenteExterno()!= null && ValidatorUtil.isEmpty(i.getDocenteExterno().getServidor())) {
					i.getDocenteExterno().setServidor(null);
				}
			}
		}
		
		if(invencao.getCodigo() == null)
			invencao.setCodigo(InvencaoHelper.gerarCodigoNotificacao(invencao));
		if(invencao.getId() == 0){
			InvencaoHelper.alterarSituacaoNotificacao(mov, TipoStatusNotificacaoInvencao.GRAVADA, invencao);
			return (Invencao) criar(mov);
		}else{
			return (Invencao) alterar(mov);
		}
	}
	
	private Invencao removerInvencao(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		Invencao invencao = (Invencao) mov.getObjMovimentado();
		if(mov.getUsuarioLogado().isUserInRole(SigaaPapeis.NIT)){
			if(invencao.getCodigo() == null){
				CodigoNotificacaoInvencao codigo = new CodigoNotificacaoInvencao();
				codigo.setAno(CalendarUtils.getAnoAtual());
				codigo.setNumero(0);
				codigo.setPrefixo("XXX");
				invencao.setCodigo(codigo);
				getGenericDAO(mov).clearSession();
				alterar(mov);
			}
			return (Invencao) remover(mov);
		} else {
			InvencaoHelper.alterarSituacaoNotificacao(mov, TipoStatusNotificacaoInvencao.REMOVIDA, invencao);
			invencao.setAtivo(false);
			return (Invencao) alterar(mov);
		}
	}

	/**
	 * Método responsável pela validação de um processo de Notificação Invenção
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		

	}

}
