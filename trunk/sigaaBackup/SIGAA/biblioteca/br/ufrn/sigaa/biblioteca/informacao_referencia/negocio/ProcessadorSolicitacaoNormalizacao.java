/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 08/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.SolicitacaoServicoDocumentoDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoNormalizacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServico.TipoSituacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.util.EmailsNotificacaoServicosBibliotecaUtil;

/**
 * Processador que contém as regras de negócio para o gerenciamento (Criar, Alterar, Validar, Atender ou Cancelar.)
 * das solicitações de normalização. 
 *
 * @author Felipe Rivas
 */
public class ProcessadorSolicitacaoNormalizacao extends AbstractProcessador {

	/**
	 * Executa as operações para a solicitação de normalização 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoSolicitacaoDocumento mov = (MovimentoSolicitacaoDocumento) movimento;
		GenericDAO dao = null; 
		
		try{
			dao = getGenericDAO(mov);
		
			SolicitacaoNormalizacao obj = mov.getObjMovimentado();
			
			validate(movimento);
			
			
			if( SigaaListaComando.CADASTRAR_SOLICITACAO_NORMALIZACAO.equals( mov.getCodMovimento() ) ){

				Integer idTrabalhoDigitalizado = salvaArquivoDigitalizadoNaBase(mov.getArquivoTrabalho());
				
				obj.setIdTrabalhoDigitalizado(idTrabalhoDigitalizado);
				obj.setSituacao( TipoSituacao.SOLICITADO ); // Cria a solicitação.
				obj.setNumeroSolicitacao( dao.getNextSeq("biblioteca", "numero_indentificador_solicitacao_sequence"));
				
				dao.create( obj );
				
				enviarEmailNovaSolicitacaoNormalizacao(mov, obj);
				
			} else if( SigaaListaComando.ALTERAR_SOLICITACAO_NORMALIZACAO.equals( mov.getCodMovimento() ) ){
				
				if (mov.getArquivoTrabalho() != null) { // O usuário informou um outro arquivo para substituir o anterior
					
					// Remove o arquiv anterior do banco //
					SolicitacaoNormalizacao objetoBanco = dao.findByPrimaryKey(obj.getId(), SolicitacaoNormalizacao.class, "idTrabalhoDigitalizado");
					removeArquivoDigitalizadoNaBase(objetoBanco.getIdTrabalhoDigitalizado());
					
					dao.detach(objetoBanco);
					
					// Salva o novo enviado pelo usuário //
					Integer idTrabalhoDigitalizado = salvaArquivoDigitalizadoNaBase(mov.getArquivoTrabalho());
					obj.setIdTrabalhoDigitalizado(idTrabalhoDigitalizado);
				}
				
				dao.update(mov.getObjMovimentado());
				
			}  else if( SigaaListaComando.ATENDER_SOLICITACAO_NORMALIZACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoNormalizacao solicitacao = null;
				
				solicitacao = dao.refresh(obj);
				
				solicitacao.setSituacao( TipoSituacao.ATENDIDO );
				solicitacao.setDataAtendimento( new Date() );
				solicitacao.setRegistroAtendimento( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
				
			} else if( SigaaListaComando.CANCELAR_SOLICITACAO_NORMALIZACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoNormalizacao solicitacao = dao.refresh(obj);
				solicitacao.setSituacao( TipoSituacao.CANCELADO );
				solicitacao.setDataCancelamento( new Date() );
				solicitacao.setRegistroCancelamento( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
				
			}
			
		}finally{
			if (dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * Salva um arquivo .pdf da solicitação caso o usuário tenha submetido um.
	 * 
	 */
	private Integer salvaArquivoDigitalizadoNaBase(UploadedFile arquivoObraDigitalizada) throws ArqException {
		if (arquivoObraDigitalizada != null) {		
			Integer idObraDigitalizada = null;
			
			try {
				idObraDigitalizada = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idObraDigitalizada, 
						arquivoObraDigitalizada.getBytes(),
						arquivoObraDigitalizada.getContentType(),
						arquivoObraDigitalizada.getName());
				
			} catch (Exception e) {
				e.printStackTrace();
				
				throw new ArqException(e);
			} 
			
			return idObraDigitalizada;		
		} else {
			return null;  // valor padrão para titulos que não tem títulos digitalizados
		}
	}

	/**
	 * Remove um arquivo .pdf que não será mais utilizado.
	 * @throws ArqException 
	 * 
	 */
	private void removeArquivoDigitalizadoNaBase(Integer idObraDigitalizada) throws ArqException {
		if (idObraDigitalizada != null) {
			try {
				EnvioArquivoHelper.removeArquivo(idObraDigitalizada);
			} catch (Exception e) {
				e.printStackTrace();
				
				throw new ArqException(e);
			}
		}
	}
	
	/**
	 * Envia um email informando aos bibliotecários de informação e referência sobre o novo agendamento criado.
	 */
	private void enviarEmailNovaSolicitacaoNormalizacao(MovimentoSolicitacaoDocumento mov, SolicitacaoNormalizacao solicitacao) throws DAOException{
		
		String assunto = " Aviso de Nova Solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase());
		String titulo = " Nova Solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase());
		
		String mensagemNivel1Email =  " O usuário "+solicitacao.getPessoa().getNome()+" realizou uma solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase())+" para a biblioteca de sua responsabilidade.";
		String mensagemNivel3Email =  " Essa solicitação está pedente de atendimento.";
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.NORMALIZACAO, solicitacao.getBiblioteca().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmail( solicitacao.getBiblioteca().getDescricao(), email, assunto, titulo
				, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, null, mensagemNivel1Email, null, mensagemNivel3Email, null
				, null, null,  null, null);
		}
		
	}
	
	/**
	 * Valida os Atributos.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		MovimentoSolicitacaoDocumento mov = (MovimentoSolicitacaoDocumento) movimento;
		SolicitacaoNormalizacao obj = mov.getObjMovimentado();
		
		ListaMensagens mensagens = new ListaMensagens(); 
		
		
		SolicitacaoServicoDocumentoDAO dao = null;
		
		try{
		
			dao = getDAO(SolicitacaoServicoDocumentoDAO.class, mov);
		
		
			if( SigaaListaComando.CADASTRAR_SOLICITACAO_NORMALIZACAO.equals( movimento.getCodMovimento() ) ){
				
				mensagens.addAll( obj.validate());
				
				if (mov.getArquivoTrabalho() == null) {
					mensagens.addErro("O arquivo digital da obra deve ser informado.");
				}
				
				if(dao.contaSolicitacoesDoUsuaro(TipoServicoInformacaoReferencia.NORMALIZACAO, obj.getPessoa().getId(), TipoSituacao.SOLICITADO) > 0){
					mensagens.addErro("Não é possível realizar duas solicitações de "+TipoServicoInformacaoReferencia.NORMALIZACAO.getDescricao()+" ao mesmo tempo.");
				}
				
				
			} else if( SigaaListaComando.ATENDER_SOLICITACAO_NORMALIZACAO.equals( movimento.getCodMovimento() ) ){
		
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser atendida, pois ela foi removida pelo solicitante.");
				}
				
				if( !obj.isSolicitado() ){
					mensagens.addErro("A Solicitação não pode ser atendida, pois ela não foi solicitada.");
				}
				
			} else if( SigaaListaComando.CANCELAR_SOLICITACAO_NORMALIZACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser cancelada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isAtendido() ){
					mensagens.addErro("Esta solicitação de normalização já foi atendida e portanto não pode ser cancelada.");
				}
				
				if( obj.isCancelado() ){
					mensagens.addErro("Esta solicitação de normalização já foi cancelada e portanto não pode ser cancelada novamente.");
				}
			} else if( SigaaListaComando.ALTERAR_SOLICITACAO_NORMALIZACAO.equals( movimento.getCodMovimento() ) ){				
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser alterada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isAtendido()){
					mensagens.addErro("Esta solicitação de normalização já foi atendida por um bibliotecário e portanto não pode ser mais alterada.");
				} else {
					mensagens.addAll( obj.validate() );
				}
			}
		}finally{
			if(dao != null) dao.close();
		}	
		
		checkValidation(mensagens);
		
	}

}
