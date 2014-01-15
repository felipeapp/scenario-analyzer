/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ArquivoLevantamentoInfra;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoInfraEstrutura;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoInfraEstrutura.Situacao;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador que controla os levantamentos de infra-estrutura.
 * 
 * @see LevantamentoInfraEstrutura
 *
 * @author Bráulio
 */
public class ProcessadorLevantamentoInfra extends AbstractProcessador {

	
	/**
	 * Solicita o levantamento de infra-estrutura passado e envia um email para notificar
	 * a biblioteca da nova solicitação.
	 */
	public LevantamentoInfraEstrutura solicitar( MovimentoLevantamentoInfra mov )
			throws SegurancaException, ArqException, NegocioException {
		
		// Permissões
		
		checkRole(SigaaPapeis.BIBLIOTECA_SOLICITAR_LEVANTAMENTO_INFRA, mov);
		
		///////
		
		GenericDAO dao = null;
		PermissaoDAO pDao = null;
		
		checkValidation(mov.getLevInfra().validate());
		
		try {
			
			dao = getGenericDAO(mov);
			
			mov.getLevInfra().setNumeroLevantamentoInfra(dao.getNextSeq("biblioteca", "numero_levantamento_infra_sequence") );
			
			// salva
			dao.create( mov.getLevInfra() );
			
			// Envia email para todos bibliotecários do setor de informação e referência
			// da biblioteca onde será realizado o levantamento
			
			Biblioteca b = dao.findByPrimaryKey(mov.getLevInfra().getBiblioteca().getId(), Biblioteca.class);
			
			pDao = getDAO(PermissaoDAO.class, mov);
			Collection<UsuarioGeral> usuarios = pDao.findUsuariosByPapelEUnidadadePermissao(
					new Papel(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO),
					b.getUnidade() );
			
			String nomePessoa = dao.findByPrimaryKey(mov.getLevInfra().getSolicitante().getId(), Pessoa.class, "nome").getNome();
			String msg = "O(a) usuário(a) " + nomePessoa + " fez uma solicitação de levantamento de infra-estrutura. Acesse o sistema " +
					"para visualizá-la e atendê-la.";
			
			EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
			
			if ( ! usuarios.isEmpty() ) {
				for ( UsuarioGeral u : usuarios ) {
					
					sender.enviaEmail( u.getNome(), u.getEmail(), "Levantamento de Infra-Estrutura - Nova Solicitação", "Levantamento de Infra-Estrutura"
							, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, msg, null, null, "O senhor(a) está recebendo esta mensagem pois possui papéis no setor de Informação e Referência", null
							,null, null, null, null);
				}
			}
			
			// Se não há usuários, envia o email para o email da biblioteca
			else {
				sender.enviaEmail( b.getDescricao(), b.getEmail(), "Levantamento de Infra-Estrutura - Nova Solicitação", "Levantamento de Infra-Estrutura"
						, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, msg, null, null, null, null
						,null, null, null, null);
			}
			
		} finally {
			if ( dao != null ) dao.close();
			if ( pDao != null ) pDao.close();
		}
		
		return mov.getLevInfra();
	}
	
	/**
	 * Salva os dados de um levantamento, mas não conclui o levantamento e nem envia email
	 * para o usuário.
	 */
	public LevantamentoInfraEstrutura salvar( MovimentoLevantamentoInfra mov )
			throws SegurancaException, ArqException, NegocioException {
		
		checkRole( new int[]{
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }, mov);
		
		LevantamentoInfraEstrutura lev = mov.getLevInfra();
		
		// testa se o funcionário tem permissão para a biblioteca selecionada
		
		BibliotecaDao bDao = null;
		
		try {
			bDao = getDAO(BibliotecaDao.class, mov);
			
			Collection<Biblioteca> bibs = new ArrayList<Biblioteca>();
			
			UsuarioGeral u = mov.getUsuarioLogado();
		
			if(! u.isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				if( u.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO) ||
						u.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF)){
				
					List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
							u, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
			
					bibs.addAll( bDao.findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
					
					idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
							u, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
					
					bibs.addAll( bDao.findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
				}
			
			} else {
				bibs.addAll( bDao.findAllBibliotecasInternasAtivas() );
			}
			
			if ( ! bibs.contains(lev.getBiblioteca()) )
				throw new SegurancaException("Você não tem permissão para atender levantamentos de infra-estrutura nesta biblioteca.");
		} finally {
			if ( bDao != null ) bDao.close();
		}
		
		checkValidation(lev.validate());
		
		// Testa tamanho e tipo de cada novo arquivo inserido
		
		ListaMensagens erros = new ListaMensagens();
		for ( ArquivoLevantamentoInfra arq : lev.getArquivos() ) {
			if ( arq.getConteudo() != null &&
					arq.getConteudo().length > ArquivoLevantamentoInfra.TAMANHO_MAXIMO_ARQUIVO * (1 << 20) )
				erros.addErro("O arquivo " + arq.getNome() + " passou do limite de " +
						ArquivoLevantamentoInfra.TAMANHO_MAXIMO_ARQUIVO + "MiB.");
			
			if ( arq.getTipo() != null &&
					! ArquivoLevantamentoInfra.ARQUIVOS_PERMITIDOS.contains(arq.getTipo()) )
				erros.addErro("O tipo do arquivo " + arq.getNome() + " não é válido.");
		}
		
		if ( ! erros.isEmpty() )
			throw new NegocioException(erros);
		
		GenericDAO dao = null;
		
		try {
			
			dao = getGenericDAO( mov );
			
			// Remove arquivos que foram excluídos do levantamento, i.e., não estão mais
			// na lista de arquivos do levantamento.
			
			LevantamentoInfraEstrutura levAntes = dao.findByPrimaryKey( lev.getId(), LevantamentoInfraEstrutura.class );
			
			for ( ArquivoLevantamentoInfra arqAntes : levAntes.getArquivos() ) {
				boolean achou = false;
				for ( ArquivoLevantamentoInfra arqDepois : lev.getArquivos() )
					if ( arqAntes.getIdArquivo() == arqDepois.getIdArquivo() ) {
						achou = true;
						break;
					}
				if ( ! achou ) {
					EnvioArquivoHelper.removeArquivo( arqAntes.getIdArquivo() );
					dao.remove( arqAntes );
				}
			}
			
			// Salva o levantamento
			
			dao.detach( levAntes );
			dao.update( lev );
			
			// Salva os novos arquivos inseridos
			
			for ( ArquivoLevantamentoInfra arq : lev.getArquivos() ) {
				if ( arq.getId() == 0 ) {
					int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
					EnvioArquivoHelper.inserirArquivo( idArquivo,
							arq.getConteudo(), arq.getTipo(), arq.getNome() );
					arq.setIdArquivo(idArquivo);
					arq.setLevantamentoInfra(new LevantamentoInfraEstrutura());
					arq.getLevantamentoInfra().setId(lev.getId());
					dao.create(arq);
				}
			}
			
		} finally {
			if ( dao != null ) dao.close();
		}
		
		return mov.getLevInfra();
	}
	
	/**
	 * Salva e conclui um levantamento de infra-estrutura. Um email notificando o usuário
	 * que o levantamento foi concluído é enviado.
	 */
	public LevantamentoInfraEstrutura atender( MovimentoLevantamentoInfra mov )
			throws SegurancaException, ArqException, NegocioException {
		
		checkRole( new int[]{
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }, mov);
		
		LevantamentoInfraEstrutura lev = mov.getLevInfra();
		
		// Verificar se já foi concluído ou cancelado enquanto o usuário o editava
		if ( lev.getSituacao() != Situacao.SOLICITADO.v ) {
			if ( lev.getSituacao() == Situacao.CANCELADO.v )
				throw new NegocioException("O levantamento foi cancelado por outro bibliotecário ou pelo usuário.");
			else if ( lev.getSituacao() == Situacao.CONCLUIDO.v )
				throw new NegocioException("O levantamento já foi realizado por outro bibliotecário.");
		}
		
		// Insere os dados que são consequência do atendimento da solicitação de levantamento
		
		lev.setSituacao( Situacao.CONCLUIDO.v );
		lev.setRegistroEntradaConclusao( mov.getUsuarioLogado().getRegistroEntrada() );
		lev.setDataConclusao( new Date() );
		
		///////
		
		checkValidation( lev.validate() );
		
		// Salva todas as modificações no levantamento, inclusive os arquivos anexos
		salvar( mov );
		
		// Envia email avisando sobre a conclusão do levantamento
		EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
		
		sender.enviaEmail( lev.getSolicitante().getNome(), lev.getSolicitante().getEmail(), "Levantamento de Infra-Estrutura - CONCLUÍDO", "Levantamento de Infra-Estrutura"
				, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, "Seu levantamento de infra-estrutura número "+lev.getNumeroLevantamentoInfra()+" foi concluído. Os resultados já estão disponíveis no Sistema.", null, null, null, null
				,null, null, null, null);
		
		return mov.getLevInfra();
	}
	
	/**
	 * Cancela um levantamento. Um email é enviado para o usuário indicando que a sua
	 * solicitação foi cancelada.
	 */
	public LevantamentoInfraEstrutura cancelar( MovimentoLevantamentoInfra mov )
			throws SegurancaException, ArqException, NegocioException {
		
		checkRole(ArrayUtils.addAll(new int[]{
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL },
				SigaaPapeis.BIBLIOTECA_SOLICITAR_LEVANTAMENTO_INFRA), mov);
		
		LevantamentoInfraEstrutura lev = mov.getLevInfra();
		UsuarioGeral u = mov.getUsuarioLogado();
		
		lev.setSituacao( Situacao.CANCELADO.v );
		lev.setDataCancelamento( new Date() );
		lev.setRegistroEntradaCancelamento( u.getRegistroEntrada() );
		
		ListaMensagens msgs = lev.validate();
		if ( ! msgs.isEmpty() )
			throw new NegocioException(msgs);
		
		GenericDAO dao = null;
		
		try {
			
			dao = getGenericDAO(mov);
			
			dao.update(lev);
			
			// remove arquivos do levantamento
			for ( ArquivoLevantamentoInfra arq : lev.getArquivos() ) {
				dao.remove(arq);
			}
			
			EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
			
			// se foi cancelada um bibliotecário, envia email para o usuário //
			if ( ! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_SOLICITAR_LEVANTAMENTO_INFRA ) ){
				sender.enviaEmail(  lev.getSolicitante().getNome(), lev.getSolicitante().getEmail(), "Levantamento de Infra-Estrutura - CANCELADO", "Levantamento de Infra-Estrutura"
						, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, "A sua solicitação de levantamento de infra-estrutura número "+lev.getNumeroLevantamentoInfra()+" foi cancelada. ", "Motivo: <br/> " + lev.getMotivoCancelamento(), null, null, null
						,null, null, null, null);
			}
				
		} finally {
			if ( dao != null ) dao.close();
		}
		
		return mov.getLevInfra();
	}
	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoLevantamentoInfra movLevInfra = (MovimentoLevantamentoInfra) mov;
		
		validate(mov);
		
		if ( SigaaListaComando.CADASTRAR_SOLICITACAO_LEVANTAMENTO_INFRA.equals(mov.getCodMovimento()) )
			return solicitar(movLevInfra);
		else if ( SigaaListaComando.SALVAR_SOLICITACAO_LEVANTAMENTO_INFRA.equals(mov.getCodMovimento()) )
			return salvar(movLevInfra);
		else if ( SigaaListaComando.ATENDER_SOLICITACAO_LEVANTAMENTO_INFRA.equals(mov.getCodMovimento()) )
			return atender(movLevInfra);
		else if ( SigaaListaComando.CANCELAR_SOLICITACAO_LEVANTAMENTO_INFRA.equals(mov.getCodMovimento()) )
			return cancelar(movLevInfra);
		else
			throw new NegocioException("Comando desconhecido");
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		if ( ! (mov instanceof MovimentoLevantamentoInfra) ) {
			throw new ArqException("Estava esperando um MovimentoLevantamentoInfra");
		}
		
		MovimentoLevantamentoInfra movLevInfra = (MovimentoLevantamentoInfra) mov;
		LevantamentoInfraEstrutura lev = movLevInfra.getLevInfra();
		
		if ( lev == null )
			throw new ArqException("Levantamento de infra-estrutura vazio");
		
	}

}
