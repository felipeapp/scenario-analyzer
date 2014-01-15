/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.text.ParseException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dao.SistemaDao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador para realizar a indicação/substituição/finalização de bolsistas de pesquisa.
 * 
 * @author Victor Hugo
 * @author Leonardo Campos
 */
public class ProcessadorIndicarBolsista extends AbstractProcessador {

	/** 
	 * Responsável pela execução do processador de Indicação de Bolsista.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException {

		MovimentoIndicarBolsista movIndicarBolsista = (MovimentoIndicarBolsista) mov;
		PlanoTrabalho planoNovo = movIndicarBolsista.getPlanoTrabalho();

		GenericDAO dao = getGenericDAO(mov);
		StringBuffer mensagem = new StringBuffer();
		String operacao = "Finalização";
		

		try {
			if( mov.getCodMovimento() == SigaaListaComando.INDICAR_BOLSISTA ){
				validate(mov);

				operacao = "Indicação";
				String bolsistaSubstituido = null;
				String nomeBanco = null;
				String agencia = null;
				String conta = null;
				String operacaoConta = null;
				
				// Buscar plano de trabalho e verificar se existia um bolsista associado.
				// Caso encontre, finalizar sua bolsa.
				PlanoTrabalho plano = dao.findByPrimaryKey(planoNovo.getId(), PlanoTrabalho.class);
				if( plano!= null && plano.getMembroProjetoDiscente() != null ){
					MembroProjetoDiscente membroAnterior = plano.getMembroProjetoDiscente();
					membroAnterior.setDataFim( movIndicarBolsista.getDataFinalizacao() );
					membroAnterior.setMotivoSubstituicao( movIndicarBolsista.getBolsistaAnterior().getMotivoSubstituicao() );
					membroAnterior.setDataFinalizacao(new Date());
					bolsistaSubstituido = "\n\nBolsista Substituído: " + membroAnterior.getDiscente().getMatriculaNome();
					dao.update( membroAnterior );
				}
				
				mensagem.append("Dados da "+ operacao +" de Bolsista\n");			
				mensagem.append("\nProjeto: " + planoNovo.getProjetoPesquisa().getTitulo());
				mensagem.append("\nPlano de Trabalho: " + planoNovo.getTitulo());
				mensagem.append("\nModalidade da Bolsa: " + planoNovo.getTipoBolsaString());
				if(planoNovo.getOrientador() != null){
					mensagem.append("\nOrientador: " +  planoNovo.getOrientador().getNome() );
					mensagem.append("\nDepartamento: " + planoNovo.getOrientador().getLotacao());
				} else {
					mensagem.append("\nOrientador (Docente Externo): " + planoNovo.getExterno().getNome());
				}
				
				if(bolsistaSubstituido != null)
					mensagem.append(bolsistaSubstituido);
				
				Discente discente = planoNovo.getMembroProjetoDiscente().getDiscente();

				// Atualizar os dados da conta bancária do discente
				ContaBancaria contaNova = planoNovo.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria();
				if( contaNova.getBanco().getId() > 0 && !"".equals(contaNova.getAgencia()) && !"".equals(contaNova.getNumero())){
					discente = dao.findByPrimaryKey(discente.getId(), Discente.class);
					Pessoa pessoa = discente.getPessoa();
					pessoa = dao.findByPrimaryKey(pessoa.getId(), Pessoa.class);
					if( pessoa.getContaBancaria() == null ){
						dao.create( contaNova );
						pessoa.setContaBancaria( contaNova );
					}else{
						pessoa.getContaBancaria().setBanco( contaNova.getBanco() );
						pessoa.getContaBancaria().setNumero( contaNova.getNumero() );
						pessoa.getContaBancaria().setAgencia( contaNova.getAgencia() );
						pessoa.getContaBancaria().setOperacao( contaNova.getOperacao() );
						dao.update( pessoa.getContaBancaria() );
					}
					
					Banco banco = dao.findByPrimaryKey(pessoa.getContaBancaria().getBanco().getId(), Banco.class);
					
					nomeBanco = "\nBanco: " + banco.getCodigoNome();
					agencia = "\nAgência: " + pessoa.getContaBancaria().getAgencia();
					if(discente.getPessoa().getContaBancaria().getOperacao() != null) operacaoConta =  "\nOperaçao: " + discente.getPessoa().getContaBancaria().getOperacao();
					conta = "\nConta: " + discente.getPessoa().getContaBancaria().getNumero();

					dao.update( pessoa );
				}

				mensagem.append("\n\nNovo Bolsista: " + discente.getMatriculaNome());
				if(nomeBanco != null) mensagem.append(nomeBanco);
				if(agencia != null) mensagem.append(agencia);
				if(operacaoConta != null) mensagem.append(operacaoConta);
				if(conta != null) mensagem.append(conta);
				
				
				// Criar nova associação entre discente e o plano de trabalho
				MembroProjetoDiscente membro = new MembroProjetoDiscente();
				membro.setDiscente( discente );
				membro.setBolsistaAnterior( movIndicarBolsista.getBolsistaAnterior() );
				membro.setDataInicio( movIndicarBolsista.getDataIndicacao() );
				membro.setPlanoTrabalho(plano);
				membro.setDataIndicacao(new Date());
				membro.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				membro.setTipoBolsa( planoNovo.getTipoBolsa() );
				membro.setTipoConta( planoNovo.getMembroProjetoDiscente().getTipoConta() );
				if(plano != null){
					plano.setMembroProjetoDiscente( membro );
					plano.setStatus(TipoStatusPlanoTrabalho.EM_ANDAMENTO);
					
					if ( plano.getTipoBolsa().getId() == TipoBolsaPesquisa.A_DEFINIR ) {
						plano.setTipoBolsa( planoNovo.getTipoBolsa() );
					}
					dao.update(plano);
				}

				Sistema sistema = getDAO(SistemaDao.class, mov).findByPrimaryKey(mov.getSistema(), Sistema.class);
				
				enviarEmail(mensagem, operacao, sistema);

				if ( plano.getProjetoPesquisa().getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.APROVADO ) {
					plano.setProjetoPesquisa( dao.findByPrimaryKey(plano.getProjetoPesquisa().getId(), ProjetoPesquisa.class) );
					plano.getProjetoPesquisa().getProjeto().setUsuarioLogado( movIndicarBolsista.getUsuarioLogado() );
					ProjetoPesquisaHelper.gravarAlterarSituacaoProjeto(dao, TipoSituacaoProjeto.EM_ANDAMENTO, plano.getProjetoPesquisa());
				}

				if ( plano.getProjetoPesquisa().getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.SUBMETIDO ) {
					plano.setProjetoPesquisa( dao.findByPrimaryKey(plano.getProjetoPesquisa().getId(), ProjetoPesquisa.class) );
					plano.getProjetoPesquisa().getProjeto().setUsuarioLogado( movIndicarBolsista.getUsuarioLogado() );
					ProjetoPesquisaHelper.gravarAlterarSituacaoProjeto(dao, TipoSituacaoProjeto.APROVADO, plano.getProjetoPesquisa());
					ProjetoPesquisaHelper.gravarAlterarSituacaoProjeto(dao, TipoSituacaoProjeto.EM_ANDAMENTO, plano.getProjetoPesquisa());
				}

			} else if( mov.getCodMovimento() == SigaaListaComando.REMOVER_BOLSISTA ){

				mensagem.append("Dados da "+ operacao +" de Bolsista\n");
				planoNovo = dao.refresh(planoNovo);
				mensagem.append("\nProjeto: " + planoNovo.getProjetoPesquisa().getTitulo());
				mensagem.append("\nPlano de Trabalho: " + planoNovo.getTitulo());
				mensagem.append("\nModalidade da Bolsa: " + planoNovo.getTipoBolsaString());
				mensagem.append("\nOrientador: " + planoNovo.getOrientador().getNome());
				mensagem.append("\nDepartamento: " + planoNovo.getOrientador().getLotacao());
				
				IndicarBolsistaValidator.validaRemocao( movIndicarBolsista.getPlanoTrabalho(), null, new ListaMensagens() );
				planoNovo = dao.refresh( planoNovo );

				MembroProjetoDiscente membro = planoNovo.getMembroProjetoDiscente();
				MembroProjetoDiscente membroFinalizado = dao.refresh( membro );

				planoNovo.setMembroProjetoDiscente( null );
				if (planoNovo.getTipoBolsa().isVinculadoCota() && planoNovo.getEdital() != null)
					planoNovo.setTipoBolsa(new TipoBolsaPesquisa(TipoBolsaPesquisa.A_DEFINIR));
				
				membroFinalizado.setDataFim( movIndicarBolsista.getDataFinalizacao() );
				membroFinalizado.setDataFinalizacao( new Date() );
				membroFinalizado.setMotivoSubstituicao( movIndicarBolsista.getBolsistaAnterior().getMotivoSubstituicao() );
				membroFinalizado.setIgnorar(false);
				
				mensagem.append("\nBolsista Finalizado: " + membroFinalizado.getDiscente().getMatriculaNome());
				
				dao.update(planoNovo);
				dao.update(membroFinalizado);
				
				Sistema sistema = getDAO(SistemaDao.class, mov).findByPrimaryKey(mov.getSistema(), Sistema.class);
				
				enviarEmail(mensagem, operacao, sistema);
				
			} else if(mov.getCodMovimento() == SigaaListaComando.IGNORAR_BOLSISTA){
				/* Comando utilizado para ignorar o registro de indicações e/ou finalizações de bolsistas
				   julgadas incoerentes ou incorretas pelo gestor de pesquisa. Dessa forma, tais indicações/finalizações
				   não serão apresentadas nas operações de homologação e/ou finalização de bolsistas no SIPAC. */
				
				dao.updateField(MembroProjetoDiscente.class, movIndicarBolsista.getBolsistaAnterior().getId(), "ignorar", true);
				
			} else if(mov.getCodMovimento() == SigaaListaComando.INATIVAR_BOLSISTA) {
				
				dao.updateField(MembroProjetoDiscente.class, movIndicarBolsista.getBolsistaAnterior().getId(), "inativo", true);
			}
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return planoNovo;
	}

	/**
	 * Envia uma mensagem automática para o e-mail do responsável pelo gerenciamento
	 * das bolsas de pesquisa da instituição. 
	 * 
	 * @param mensagem
	 * @param operacao
	 */
	private void enviarEmail(StringBuffer mensagem, String operacao, Sistema sistema) {
		// Enviando email para o gestor de bolsas
		MailBody mail = new MailBody();
		mail.setAssunto("Mensagem Automática: " + operacao + " de Bolsista realizada no " + sistema.getNome());
		mail.setContentType(MailBody.TEXT_PLAN);
		
		mail.setNome( "Gestor de Bolsas de Pesquisa" );
		mail.setEmail( ParametroHelper.getInstance().getParametro(ConstantesParametro.EMAIL_NOTIFICACAO_BOLSISTA) );
		mail.setMensagem( mensagem.toString() );
		
		Mail.send(mail);
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoIndicarBolsista movIndicarBolsista = (MovimentoIndicarBolsista) mov;
		ListaMensagens lista = new ListaMensagens();
		IndicarBolsistaValidator.validaIndicacao( movIndicarBolsista.getPlanoTrabalho(), null, lista );
//		PlanoTrabalhoValidator.validarNumeroDeBolsistas(movIndicarBolsista.getPlanoTrabalho(), erros );
		checkValidation(lista);
	}

}
