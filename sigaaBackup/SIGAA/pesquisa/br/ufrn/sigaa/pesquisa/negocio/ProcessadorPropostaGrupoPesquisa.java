package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.InteressadoDTO;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.integracao.dto.UnidadeDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.ProtocoloRemoteService;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.sigaa.arq.dao.pesquisa.LinhaPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroGrupoPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.StatusGrupoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável por cadastrar Grupos de Pesquisa.
 * 
 * @author Jean Guerethes
 */
public class ProcessadorPropostaGrupoPesquisa extends AbstractProcessador {

	/** Armazena os erros encontrados durante as validações */
	private ListaMensagens lista;
	
	/**
	 *  Método responsável pela execução do processamento do grupo de pesquisa
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoCadastro movc = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);
		MembroGrupoPesquisaDao membroDao = getDAO(MembroGrupoPesquisaDao.class, mov);
		LinhaPesquisaDao linhaDao = getDAO(LinhaPesquisaDao.class, mov);
		Comando comando = mov.getCodMovimento();
		
		lista = new ListaMensagens();
		
		try {
			GrupoPesquisa grupoPesquisa = movc.getObjMovimentado();
				
			if ( comando.equals(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA) ) {
				validaEquipeGrupoPesquisa( grupoPesquisa, membroDao );
				validaLinhaPesquisa(grupoPesquisa, linhaDao);
				gravarTemporariamente( grupoPesquisa, movc, membroDao );
				if ( isEmpty( grupoPesquisa.getId() ))
					gerarEntradaHistorico(grupoPesquisa, movc, linhaDao);
			}
			
			if ( comando.equals(SigaaListaComando.ENVIAR_EMAIL_MEMBROS_PROPOSTA_GRUPO_PESQUISA) ) {
				enviarEmail( movc, membroDao );
			}
			if ( comando.equals(SigaaListaComando.ASSINAR_PROPOSTA_GRUPO_PESQUISA) ) {
				assinar( movc, dao );
			}
			if ( comando.equals(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA) ) {
				submeterPropGrupoPesquisa( movc, dao );
			}
			
		} finally {
			dao.close();
			membroDao.close();
			linhaDao.close();
		}
		
		return null;
	}
	
	/**
	 * Gerar um objeto que representa uma entrada no histórico de um plano de trabalho
	 *
	 * @param planoTrabalho
	 * @param mov
	 * @return
	 * @throws DAOException 
	 */
	public static void gerarEntradaHistorico(GrupoPesquisa grupoPesquisa, Movimento mov, GenericDAO dao) throws DAOException {
		GrupoPesquisa grupoPesquisaBD = dao.findByPrimaryKey(grupoPesquisa.getId(), GrupoPesquisa.class);
		Collection<HistoricoGrupoPesquisa> historicos = dao.findByExactField(HistoricoGrupoPesquisa.class, "grupoPesquisa.id", grupoPesquisa.getId(), "desc", "data"); 

		if ( historicos.isEmpty() || grupoPesquisa.getStatus() != grupoPesquisaBD.getStatus() ) {
			HistoricoGrupoPesquisa historico = new HistoricoGrupoPesquisa();
			historico.setGrupoPesquisa( grupoPesquisa );
			historico.setStatus( grupoPesquisa.getStatus() );
			historico.setData( new Date() );
			historico.setRegistroEntrada(  mov.getUsuarioLogado().getRegistroEntrada() );
			historico.setParecer( grupoPesquisa.getParecer() );
			persist(historico, dao);
			if (!historicos.isEmpty())
				enviarEmailCoordenacao(grupoPesquisa);
		}
	}
	
	/**
	 * Realizar a mudança do status da proposta de grupo de pesquisa.
	 * @param movc
	 * @param dao
	 * @throws DAOException
	 */
	private void submeterPropGrupoPesquisa(MovimentoCadastro movc, GenericDAO dao) throws ArqException, NegocioException {

		GrupoPesquisa grupoPesquisa = movc.getObjMovimentado();
		Collection<HistoricoGrupoPesquisa> historico = dao.findByExactField(HistoricoGrupoPesquisa.class, "grupoPesquisa.id", grupoPesquisa.getId(), "desc", "data"); 
		boolean gestor = movc.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && movc.getSubsistema() == SigaaSubsistemas.PESQUISA.getId();
		if ( grupoPesquisa.getStatus() != StatusGrupoPesquisa.NECESSITA_CORRECAO && !historico.isEmpty() && !gestor) {
			int maiorStatus = 0;
			for (HistoricoGrupoPesquisa h : historico) {
				if ( h.getStatus() > maiorStatus ) 
					maiorStatus = h.getStatus();
			}
			if(maiorStatus > grupoPesquisa.getStatus())
				grupoPesquisa.setStatus(maiorStatus);
		}
		
		if ( !isEmpty(grupoPesquisa.getId()) )
			gerarEntradaHistorico(grupoPesquisa, movc, dao);
		
		if (grupoPesquisa.getStatus() == StatusGrupoPesquisa.APROVACAO_DEPARTAMENTO
				&& grupoPesquisa.getNumeroProtocolo() == null
				&& grupoPesquisa.getAnoProtocolo() == null
				&& Sistema.isSipacAtivo()) {
			ProcessoDTO processo = criarProcesso(movc, grupoPesquisa);
			grupoPesquisa.setNumeroProtocolo(processo.getNumProtocolo());
			grupoPesquisa.setAnoProtocolo(processo.getAno());
		} 
		
		dao.updateFields(
				GrupoPesquisa.class,
				grupoPesquisa.getId(),
				new String[] { "status", "parecer", "numeroProtocolo",
						"anoProtocolo" },
				new Object[] { grupoPesquisa.getStatus(),
						grupoPesquisa.getParecer(),
						grupoPesquisa.getNumeroProtocolo(),
						grupoPesquisa.getAnoProtocolo() });
	}

	/**
	 * Responsável pela criação de um novo processo de criação de grupo de
	 * pesquisa no sistema de protocolos.
	 * 
	 * @param mov
	 * @param invencao
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private ProcessoDTO criarProcesso(MovimentoCadastro mov, GrupoPesquisa grupoPesquisa)
			throws ArqException, NegocioException {
		ParametroHelper params = ParametroHelper.getInstance();
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		
		ProcessoDTO processo = new ProcessoDTO();
		processo.setAssunto(grupoPesquisa.getNome());
		processo.setObservacao("[PROPOSTA DE CRIAÇÃO DE GRUPO DE PESQUISA SUBMETIDA ATRAVÉS DO SIGAA]");
		UnidadeDTO unidadeOrigem = new UnidadeDTO();
		unidadeOrigem.setId( ((Usuario)mov.getUsuarioLogado()).getVinculoAtivo().getUnidade().getId());
		processo.setUnidadeOrigem(unidadeOrigem);
		processo.setIdUsuario(usuario.getId());
		processo.setIdtipoProcesso(params.getParametroInt(ParametrosPesquisa.ID_TIPO_PROCESSO_GRUPO_PESQUISA));
		
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
		unidadeDestinoMov.setId(((Usuario)mov.getUsuarioLogado()).getVinculoAtivo().getUnidade().getId());
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
	 * Serve para realizar a assinatura do grupo de pesquisa.
	 * @param movc
	 * @param dao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void assinar( MovimentoCadastro movc, GenericDAO dao ) throws DAOException, NegocioException {
		GrupoPesquisa grupoPesquisa = movc.getObjMovimentado();
		MembroGrupoPesquisa membroGrupo = (MembroGrupoPesquisa) movc.getObjAuxiliar();
		boolean assinado = false;
		boolean assinaturaSelecioanda = membroGrupo.isSelecionado(); 
		
		membroGrupo = dao.findAndFetch(membroGrupo.getId(), MembroGrupoPesquisa.class, "pessoa");
		
		encontrado : for (MembroGrupoPesquisa membro : grupoPesquisa.getEquipesGrupoPesquisaCol()) {
			if ( 
				 (
					(!membro.getPessoa().isInternacional() && membro.getPessoa().getCpf_cnpj().equals( membroGrupo.getPessoa().getCpf_cnpj() )) 
					|| (membro.getPessoa().isInternacional() && membro.getPessoa().getPassaporte().equals(membroGrupo.getPessoa().getPassaporte())) 
				 ) 
				 && membro.getSenhaConfirmacao().equals(membroGrupo.getSenhaConfirmacao()) ) {
				dao.updateFields(MembroGrupoPesquisa.class, membro.getId(), new String [] {"assinado", "dataAssinatura"},
						new Object [] {assinaturaSelecioanda, new Date() });
				assinado = true;
				break encontrado;
			}
		}
		if (!assinado) {
			lista = new ListaMensagens();
			lista.addErro("CPF e/ou senha inválidos.");
			checkValidation(lista);
		}
	}

	/**
	 * Realiza o envio de um email para o corodenador do grupo de pesquisa informando sobre a mudança de situação.
	 * @param movc
	 * @param dao
	 * @throws DAOException
	 */
	private static void enviarEmailCoordenacao(GrupoPesquisa grupoPesq) {
		MailBody mail = new MailBody();
	    mail.setContentType(MailBody.HTML);

	    // Definir remetente
	    mail.setFromName(RepositorioDadosInstitucionais.get("siglaSigaa"));
	    mail.setEmail( grupoPesq.getCoordenador().getPessoa().getEmail() ) ;
	    mail.setAssunto("Mudança de Situação da Proposta de Criação de Grupo de Pesquisa");
	    
	    String mensagem = "Caro(a) " + grupoPesq.getCoordenador().getPessoa().getNome() + ", o grupo de pesquisa <b><i>" + grupoPesq.getNome() + "</i></b>" + 
	    				  " do qual o senhor(a) é o(a) líder teve o status alterado para <b><i>" + grupoPesq.getStatusString() + "</i></b>.";
	    
	    if(!isEmpty(grupoPesq.getParecer()))
	    	mensagem += "<br/><br/> <b>Parecer do Avaliador:</b> <br/><p>" + grupoPesq.getParecer() +"</p>";

	    mail.setMensagem(mensagem);
	    Mail.send(mail);
	}
	
	/**
	 * Realiza o envio de uma email para todos os membros do grupo de pesquisa, para coletar a assinatura de todos.
	 * @param movc
	 * @param dao
	 * @throws DAOException
	 */
	private void enviarEmail( MovimentoCadastro movc, MembroGrupoPesquisaDao dao ) throws DAOException {
		GrupoPesquisa grupoPesquisa = movc.getObjMovimentado();
		
		for (MembroGrupoPesquisa membro : grupoPesquisa.getEquipesGrupoPesquisaCol() ) {

			if ( membro.isSelecionado() ) {
				MailBody mail = new MailBody();
			    mail.setContentType(MailBody.HTML);
	
			    // Definir remetente
			    mail.setFromName( membro.getGrupoPesquisa().getCoordenador().getPessoa().getNome() );
	
			    mail.setEmail( membro.getPessoa().getEmail() ) ;
			    mail.setAssunto("Solicitação de Assinatura para Grupo de Pesquisa");
			    
			    String mensagem = "Caro(a) " + membro.getPessoa().getNome() + ", você foi inserido como " + membro.getTipoMembroGrupoPesqString() + 
			    				  " no Grupo de Pesquisa <b>" + membro.getGrupoPesquisa().getNome() + "</b>"+ 
			    				  " líderado pelo(a) Prof(a) " + membro.getGrupoPesquisa().getCoordenador().getPessoa().getNome() + ". <br /><br />";
			    
			    String senha = UFRNUtils.geraSenhaAleatoria();
			    while ( dao.haSenha(senha) )
			    	senha = UFRNUtils.geraSenhaAleatoria();

			    membro.setCodigoAcesso( UFRNUtils.toSHA1Digest(UFRNUtils.toSHA1Digest(String.valueOf( membro.getId() + membro.getPessoa().getId() + senha ) )) );
			    
				dao.updateFields(MembroGrupoPesquisa.class, membro.getId(), new String [] {"codigoAcesso", "assinado"},
						new Object [] {membro.getCodigoAcesso(), null });
			    
			    mensagem += " <br/ ><br/ >Para confirmar a sua participação no Grupo de Pesquisa, <a href='"+ RepositorioDadosInstitucionais.getLinkSigaa() +"/sigaa/public/pesquisa/assinatura_digital_grupo_pesquisa/"+membro.getCodigoAcesso()+".jsf'>clique aqui</a>" +
	    		   			" para realizar a sua assinatura digital, para fins de cumprimento do Inciso IV, do Art. 7º do Anexo da resolução nº 162/2008_CONSEPE.";

			    mensagem += " <br /><br />Utilize a seguinte senha para a confirmação da assinatura digital: <b><font color='red'>" + senha + "</font></b>";
			    
			    dao.updateField(MembroGrupoPesquisa.class, membro.getId(), "senhaConfirmacao", senha);	   
			    	   
			    mail.setMensagem(mensagem);
			    Mail.send(mail);
		    }
		}
	}

	/**
	 * Grava a proposta de criação do grupo de pesquisa, ao avançar em cada tela.
	 * @param grupoPesquisa
	 * @param movc
	 * @param membroDao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void gravarTemporariamente( GrupoPesquisa grupoPesquisa, MovimentoCadastro movc, MembroGrupoPesquisaDao membroDao ) throws DAOException, NegocioException {
		grupoPesquisa.setAtivo(Boolean.TRUE);
		if (grupoPesquisa.getId() == 0) {
			grupoPesquisa.setDataCriacao( new Date() );
			grupoPesquisa.setUsuarioCriacao( (Usuario) movc.getUsuarioLogado() );
		}
		
		UFRNUtils.anularAtributosVazios(grupoPesquisa, "viceCoordenador");
		persist(grupoPesquisa, membroDao);
	}

	/**
	 * Realiza as validações na equipe do grupo de pesquisa.
	 * @param grupoPesquisa
	 * @param dao
	 * @throws DAOException
	 */
	private void validaEquipeGrupoPesquisa( GrupoPesquisa grupoPesquisa, MembroGrupoPesquisaDao dao ) throws DAOException {

		if (grupoPesquisa.getId() > 0) {
			Collection<MembroGrupoPesquisa> membros = dao.findMembroByGrupoPesquisa(grupoPesquisa);
			
			for (MembroGrupoPesquisa membro : membros) {
				if ( !grupoPesquisa.getEquipesGrupoPesquisaCol().contains( membro ) ) {
					if ( !isEmpty(membro.getServidor()) && membro.getServidor().getId() == grupoPesquisa.getViceCoordenador().getId() )
						dao.updateField(GrupoPesquisa.class, grupoPesquisa.getId(), "viceCoordenador", new Servidor());
					dao.remove(membro);
				}
				
				if ( membro.getClassificacao() == MembroGrupoPesquisa.COORDENADOR 
						&& membro.getServidor().getId() != grupoPesquisa.getCoordenador().getId() ) {
					dao.updateField(MembroGrupoPesquisa.class, membro.getId(), "classificacao", MembroGrupoPesquisa.MEMBRO);
				}
			}
			
			for (MembroGrupoPesquisa membro : grupoPesquisa.getEquipesGrupoPesquisaCol()) {
				if ( isEmpty( membro.getDataInicio() ) ) {
					if ( containsMembro(membros, membro) )
						grupoPesquisa.getEquipesGrupoPesquisa().remove(membro);
					else 
						membro.setDataInicio(new Date());
				}
			}

			for (MembroGrupoPesquisa membro : membros) {
				dao.detach(membro);
				dao.detach(membro.getGrupoPesquisa());
			}
			
			Collection<MembroGrupoPesquisa> novosMembros = getRecemCadastrados(grupoPesquisa);
			grupoPesquisa.setEquipesGrupoPesquisa( new HashSet<MembroGrupoPesquisa>( dao.findMembroByGrupoPesquisa(grupoPesquisa)) );
			if ( !isEmpty( novosMembros ) )
				grupoPesquisa.getEquipesGrupoPesquisa().addAll( novosMembros );
			
		} else {
			for (MembroGrupoPesquisa membro : grupoPesquisa.getEquipesGrupoPesquisa()) {
				if ( isEmpty( membro.getDataInicio() ) ) {
					membro.setDataInicio(new Date());
				}
			}
		}
		
	}

	/**
	 * Retorna os membros recém adicionados ao grupo de pesquisa, ou seja, que ainda 
	 * não foram persistidos no banco de dados.
	 * @param grupoPesquisa
	 * @return
	 */
	private Collection<MembroGrupoPesquisa> getRecemCadastrados(GrupoPesquisa grupoPesquisa) {
		Collection<MembroGrupoPesquisa> novosMembros = new ArrayList<MembroGrupoPesquisa>();
		for (MembroGrupoPesquisa membroGrupoPesquisa : grupoPesquisa.getEquipesGrupoPesquisaCol()) {
			if ( isEmpty( membroGrupoPesquisa.getId() ) ) {
				novosMembros.add(membroGrupoPesquisa);
			}
		}
		return novosMembros;
	}
	
	/**
	 * Verifica a duplicidade dos membros do grupo de pesquisa.
	 */
	private boolean containsMembro(Collection<MembroGrupoPesquisa> membros, MembroGrupoPesquisa membro) {
		for (MembroGrupoPesquisa membroAtual : membros) {
			if ( membroAtual.getPessoa().getId() == membro.getPessoa().getId() )
				return true; 
		}
		return false;
	}

	/**
	 * Realiza a validação das linhas de pesquisa. 
	 * @param grupoPesquisa
	 * @param dao
	 * @throws DAOException
	 */
	private void validaLinhaPesquisa( GrupoPesquisa grupoPesquisa, LinhaPesquisaDao dao ) throws DAOException {
		if (grupoPesquisa.getId() > 0) {
			Collection<LinhaPesquisa> linhas = dao.findByTrecho(null, grupoPesquisa.getId());
			for (LinhaPesquisa linha : linhas) {
				if ( !grupoPesquisa.getLinhasPesquisa().contains(linha) ) {
					dao.updateField(LinhaPesquisa.class, linha.getId(), "inativa", Boolean.TRUE);
				}
			}
		}
	}

	/**
	 * Validação padrão
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

	/**
	 * Atualiza ou cria um novo objeto no banco dependendo se ele possui ou não id
	 * @param obj
	 * @throws DAOException
	 */
	private static void persist(PersistDB obj, GenericDAO dao) throws DAOException {
		if( obj.getId() > 0 )
			dao.update(obj);
		else
			dao.create(obj);
	}
	
}