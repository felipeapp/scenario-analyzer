/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 12/04/2011
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
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.SolicitacaoServicoDocumentoDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.FichaCatalografica;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServico.TipoSituacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.util.EmailsNotificacaoServicosBibliotecaUtil;

/**
 * Processador que contém as regras de negócio para o gerenciamento (Criar, Alterar, Validar, Atender ou Cancelar.)
 * das solicitações de catalogação na fonte. 
 *
 * @author Felipe Rivas
 */
public class ProcessadorSolicitacaoCatalogacao extends AbstractProcessador {
	
	/**
	 * Executa as operações para a solicitação de catalogação 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoSolicitacaoCatalogacao mov = (MovimentoSolicitacaoCatalogacao) movimento;
		GenericDAO dao = null; 
		
		try {
			dao = getGenericDAO(mov);
		
			SolicitacaoCatalogacao obj = mov.getObjMovimentado();
			
			validate(movimento);
			
			if (SigaaListaComando.CADASTRAR_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento())) {
				Integer idTrabalhoDigitalizado = salvaArquivoDigitalizadoNaBase(mov.getArquivoTrabalho());
				
				obj.setIdTrabalhoDigitalizado(idTrabalhoDigitalizado);
				obj.setSituacao(TipoSituacao.SOLICITADO); // Cria a solicitação.
				obj.setNumeroSolicitacao( dao.getNextSeq("biblioteca", "numero_indentificador_solicitacao_sequence"));
				
				dao.create(obj);
				
				enviarEmailNovaSolicitacaoCatalogacao(mov, obj);
				
			} else if (SigaaListaComando.ALTERAR_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento())) {
				
				if (mov.getArquivoTrabalho() != null) {  // O usuário informou um outro arquivo para substituir o anterior
					// Remove o arquiv anterior do banco //
					SolicitacaoCatalogacao objetoBanco = dao.findByPrimaryKey(obj.getId(), SolicitacaoCatalogacao.class, "idTrabalhoDigitalizado");
					removeArquivoDigitalizadoNaBase(objetoBanco.getIdTrabalhoDigitalizado());
					
					dao.detach(objetoBanco);
					
					// Salva o novo enviado pelo usuário //
					Integer idTrabalhoDigitalizado = salvaArquivoDigitalizadoNaBase(mov.getArquivoTrabalho());
					obj.setIdTrabalhoDigitalizado(idTrabalhoDigitalizado);
				}
				
				dao.update(mov.getObjMovimentado());
				
			} else if (SigaaListaComando.REENVIAR_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento())) {
				obj.setSituacao(TipoSituacao.REENVIADO);
				
				dao.update(mov.getObjMovimentado());
				
				enviarEmailSolicitacaoCatalogacaoReenviada(mov, obj);
				
			} else if (SigaaListaComando.SALVAR_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento()) || 
					SigaaListaComando.ATENDER_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento())) {
				
				boolean fichaFoiModificada = mov.getFichaCatalograficaAnterior() != null || mov.getIdFichaDigitalizadaAnterior() != null;
				//SolicitacaoCatalogacao solicitacao = null;
				FichaCatalografica ficha = null;

				ficha = obj.getFichaGerada();
				
				
				if (fichaFoiModificada) {
					if (ficha != null) {
						
						ficha.setDataCriacao(new Date());
						
						obj.setFichaGerada(ficha);
						obj.setIdFichaDigitalizada(null);
						
						dao.create(ficha);
					} else {
						Integer idFichaDigitalizada = salvaArquivoDigitalizadoNaBase(mov.getArquivoFichaDigitalizada());
						
						obj.setIdFichaDigitalizada(idFichaDigitalizada);
						obj.setFichaGerada(null);
					}
					
					if (SigaaListaComando.ATENDER_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento())) {
						if (obj.getSituacao() == TipoSituacao.SOLICITADO) {
							obj.setSituacao(TipoSituacao.ATENDIDO);
						} else if (obj.getSituacao() == TipoSituacao.REENVIADO) {
							obj.setSituacao(TipoSituacao.ATENDIDO_FINALIZADO);
						}
						
						obj.setDataAtendimento(new Date());
						obj.setRegistroAtendimento(mov.getUsuarioLogado().getRegistroEntrada());
					}
					
					dao.update(obj);
					
					if (mov.getFichaCatalograficaAnterior() != null) {
						dao.remove(mov.getFichaCatalograficaAnterior());
					} else {
						removeArquivoDigitalizadoNaBase(mov.getIdFichaDigitalizadaAnterior());
					}
					
				} else {
					if (ficha != null) {
						
						ficha.setDataCriacao(new Date());
						
						if (ficha.getId() == 0) {
							obj.setFichaGerada(ficha);
							
							dao.create(ficha);
						} else {
							dao.update(ficha);
						}
					} else {
						if (mov.getArquivoFichaDigitalizada() != null) {
							Integer idFichaDigitalizada = salvaArquivoDigitalizadoNaBase(mov.getArquivoFichaDigitalizada());
							
							obj.setIdFichaDigitalizada(idFichaDigitalizada);
						}
					}
					
					if (SigaaListaComando.ATENDER_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento())) {
						if (obj.getSituacao() == TipoSituacao.SOLICITADO) {
							obj.setSituacao(TipoSituacao.ATENDIDO);
						} else if (obj.getSituacao() == TipoSituacao.REENVIADO) {
							obj.setSituacao(TipoSituacao.ATENDIDO_FINALIZADO);
						}
						
						obj.setDataAtendimento(new Date());
						obj.setRegistroAtendimento(mov.getUsuarioLogado().getRegistroEntrada());
					}
					
					dao.update(obj);
				}
				
				// O email é enviado a partir do MBean
				
			} else if (SigaaListaComando.CANCELAR_SOLICITACAO_CATALOGACAO.equals(mov.getCodMovimento())) {				
				SolicitacaoCatalogacao solicitacao = dao.refresh(obj);
				solicitacao.setSituacao(TipoSituacao.CANCELADO);
				solicitacao.setDataCancelamento(new Date());
				solicitacao.setRegistroCancelamento(mov.getUsuarioLogado().getRegistroEntrada());
				dao.update(solicitacao);
				
				// O email é enviado a partir do MBean
				
			}
		}
		finally{
			if (dao != null) {
				dao.close();
			}
		}
		
		return null;
	}

	/**
	 * Envia um email informando aos bibliotecários de informação e referência sobre a nova catalogação criada.
	 */
	private void enviarEmailNovaSolicitacaoCatalogacao(MovimentoSolicitacaoCatalogacao mov, SolicitacaoCatalogacao solicitacao) throws DAOException{
		
		String assunto = " Aviso de Nova Solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase());
		String titulo = " Nova Solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase());
		
		String mensagemNivel1Email =  " O usuário "+solicitacao.getPessoa().getNome()
										+" realizou uma solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase())
										+" para essa biblioteca. ";
		
		String mensagemNivel3Email =  " Essa solicitação está pedente de atendimento.";
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE, solicitacao.getBiblioteca().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmail( solicitacao.getBiblioteca().getDescricao(), email, assunto, titulo
					, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, null, mensagemNivel1Email, null, mensagemNivel3Email, null
					, null, null,  null, null);
		}
		
	}
	
	
	
	/**
	 * Envia um email informando aos bibliotecários de informação e referência sobre a nova catalogação criada.
	 */
	private void enviarEmailSolicitacaoCatalogacaoReenviada(MovimentoSolicitacaoCatalogacao mov, SolicitacaoCatalogacao solicitacao) throws DAOException{
		
		String assunto = " Aviso de Reenvio da Solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase());
		String titulo = " Reportado erro no atendimento da Solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase());
		
		String mensagemNivel1Email =  " O usuário "+solicitacao.getPessoa().getNome()
										+" reportou um erro na solicitação de "+WordUtils.capitalize(solicitacao.getTipoServico().toLowerCase())
										+" atendida por essa biblioteca. ";
		
		String mensagemNivel3Email =  " Motivo do reenvio: "+solicitacao.getMotivoReenvio();
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE, solicitacao.getBiblioteca().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmail( solicitacao.getBiblioteca().getDescricao(), email, assunto, titulo
				, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, null, mensagemNivel1Email, null, mensagemNivel3Email, null
				, null, null,  null, null);
		}
	}
	
	
	
	
	
	/**
	 * Salva um arquivo pdf,docx,doc ou  odt da solicitação caso o usuário tenha submetido um.
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
	 * Remove um arquivo pdf,docx,doc ou  odt que não será mais utilizado.
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
	 * Valida os Atributos.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		MovimentoSolicitacaoCatalogacao mov = (MovimentoSolicitacaoCatalogacao) movimento;
		SolicitacaoCatalogacao obj = mov.getObjMovimentado();
		UploadedFile arquivoFichaDigitalizada = mov.getArquivoFichaDigitalizada();
		
		ListaMensagens mensagens = new ListaMensagens(); 
		
		SolicitacaoServicoDocumentoDAO dao = null;
		
		try{
		
			dao = getDAO(SolicitacaoServicoDocumentoDAO.class, mov);
			
			if (SigaaListaComando.CADASTRAR_SOLICITACAO_CATALOGACAO.equals(movimento.getCodMovimento())) {
				mensagens.addAll(obj.validate());
				
				if (mov.getArquivoTrabalho() == null) {
					mensagens.addErro("O arquivo digital da obra deve ser informado.");
				}
				
				if(dao.contaSolicitacoesDoUsuaro(TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE, obj.getPessoa().getId(), TipoSituacao.SOLICITADO, TipoSituacao.REENVIADO) > 0){
					mensagens.addErro("Não é possível realizar duas solicitações de "+TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE.getDescricao()+" ao mesmo tempo.");
				}
				
			}  else if (SigaaListaComando.SALVAR_SOLICITACAO_CATALOGACAO.equals(movimento.getCodMovimento())) {
				
				/* *******************************************************
				 *  O bibliotecário está salvando a catalogação na fonte
				 * *******************************************************/
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser salva, pois ela foi removida pelo solicitante.");
				}
				
				if (!obj.isSolicitado() && !obj.isReenviado()) {
					mensagens.addErro("A Solicitação não pode ser salva, pois ela não foi solicitada.");
				}
				
				if (obj.getFichaGerada() != null) {
					ListaMensagens lista = obj.getFichaGerada().validate();
					
					mensagens.addAll(lista.getMensagens());
				}
				
				if(arquivoFichaDigitalizada != null) {
					if( !arquivoFichaDigitalizada.getName().endsWith(".pdf") && !arquivoFichaDigitalizada.getName().endsWith(".doc")
						&& !arquivoFichaDigitalizada.getName().endsWith(".docx") && !arquivoFichaDigitalizada.getName().endsWith(".odt") ){
						mensagens.addErro("O arquivo digitalizado precisa ser do formato pdf,doc,docx ou odt.");
					}
					
					if(arquivoFichaDigitalizada.getName().length() > 100) {
						mensagens.addErro("O tamanho máximo permito para o nome de arquivo é de 100 caracteres.");
					}
				}
			} else if (SigaaListaComando.ATENDER_SOLICITACAO_CATALOGACAO.equals(movimento.getCodMovimento())) {
				
				/* *******************************************************
				 *  O bibliotecário está salvando atendendo a solicitação do usuário
				 * *******************************************************/
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser atendida, pois ela foi removida pelo solicitante.");
				}
				
				if (!obj.isSolicitado() && !obj.isReenviado()) {
					mensagens.addErro("A Solicitação não pode ser atendida, pois ela não foi solicitada.");
				}
				
				if (obj.getFichaGerada() == null && (arquivoFichaDigitalizada == null && obj.getIdFichaDigitalizada() == null) ) {
					mensagens.addErro("É obrigatório cadastrar a ficha catalográfica para atender a solicitação.");
				} else {
					if (obj.getFichaGerada() != null) {
						ListaMensagens lista = obj.getFichaGerada().validate();
						
						mensagens.addAll(lista.getMensagens());
					}
				}
				
				if(arquivoFichaDigitalizada != null) {
					if(!arquivoFichaDigitalizada.getName().endsWith(".pdf") && !arquivoFichaDigitalizada.getName().endsWith(".doc") 
						&& !arquivoFichaDigitalizada.getName().endsWith(".docx") && !arquivoFichaDigitalizada.getName().endsWith(".odt")){
						mensagens.addErro("O arquivo digitalizado precisa ser do formato pdf,doc,docx ou odt.");
					}
					
					if(arquivoFichaDigitalizada.getName().length() > 100) {
						mensagens.addErro("O tamanho máximo permito para o nome de arquivo é de 100 caracteres.");
					}
				}
			} else if (SigaaListaComando.CANCELAR_SOLICITACAO_CATALOGACAO.equals(movimento.getCodMovimento())) {
				
				/* *******************************************************
				 *  O bibliotecário está cancelando o atendimento
				 * *******************************************************/
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser cancelada, pois ela foi removida pelo solicitante.");
				}
				
				if (obj.isAtendido()) {
					mensagens.addErro("Esta solicitação de catalogação na fonte já foi atendida e portanto não pode ser cancelada.");
				}
				
				if (obj.isCancelado()) {
					mensagens.addErro("Esta solicitação de catalogação na fonte já foi cancelada e portanto não pode ser cancelada novamente.");
				}
			} else if (SigaaListaComando.ALTERAR_SOLICITACAO_CATALOGACAO.equals(movimento.getCodMovimento())) {
				
				/* *******************************************************
				 *  O usuário está alterando a solicitação feita
				 * *******************************************************/
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser alterada, pois ela foi removida pelo solicitante.");
				}
				
				if (obj.isAtendido()) {
					mensagens.addErro("Esta solicitação de catalogação na fonte já foi atendida por um bibliotecário e portanto não pode ser mais alterada.");
				} else {
					mensagens.addAll(obj.validate());
				}
			} else if (SigaaListaComando.REENVIAR_SOLICITACAO_CATALOGACAO.equals(movimento.getCodMovimento())) {
				
				if(dao.contaSolicitacoesDoUsuaro(TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE, obj.getPessoa().getId(), TipoSituacao.SOLICITADO, TipoSituacao.REENVIADO) > 0){
					mensagens.addErro("Não é possível realizar duas solicitações de "+TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE.getDescricao()+" ao mesmo tempo.");
				}
				
			
				if( StringUtils.isEmpty(obj.getMotivoReenvio()) ){
					mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo de Reenvio da Solicitação");
				}else{
					if( obj.getMotivoReenvio().length() > 400)
						mensagens.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Motivo de Reenvio da Solicitação", 400);
				}
				
				if (!obj.isAtendido()) {
					if (obj.isReenviado()) {
						mensagens.addErro("Não é possível reportar problemas para esta solicitação de catalogação na fonte, pois esta operação já foi realizada anteriormente.");
					} else {
						mensagens.addErro("Não é possível reportar problemas para esta solicitação de catalogação na fonte, pois a mesma não foi atendida.");
					}
				}
			}
			
		}finally{
			if(dao != null) dao.close();
		}	
			
		
		checkValidation(mensagens);		
	}

}
