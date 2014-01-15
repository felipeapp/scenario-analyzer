/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.biblioteca.MultaUsuariosBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca.StatusMulta;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.MultaUsuarioBibliotecaUtil;

/**
 * <p> Classe que contém as regras de negócio para quitar as multas dos usuários da biblioteca. </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorQuitaMultaUsuarioBiblioteca extends AbstractProcessador{

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
	
		MovimentoQuitaMultaUsuarioBiblioteca movi = (MovimentoQuitaMultaUsuarioBiblioteca) mov;
		
		UsuarioBibliotecaDao dao = null;
		MultaUsuariosBibliotecaDao daoMulta = null;
		GenericDAO  daoComum = null;
		
		Date dataQuitacao = new Date();
		
		try{
			dao = getDAO(UsuarioBibliotecaDao.class, mov);
			daoMulta = getDAO(MultaUsuariosBibliotecaDao.class, mov);
			daoComum = DAOFactory.getGeneric(Sistema.COMUM);
			
			if(movi.isPagamentoManual()){
				
				validate(movi);
				
				MultaUsuarioBiblioteca multaASerPaga = movi.getMultaASerPaga();
				Integer idPessoa = multaASerPaga.isManual() ? multaASerPaga.getUsuarioBiblioteca().getIdentificadorPessoa() : multaASerPaga.getEmprestimo().getUsuarioBiblioteca().getIdentificadorPessoa();
				Integer idBiblioteca = multaASerPaga.isManual() ? multaASerPaga.getUsuarioBiblioteca().getIdentificadorBiblioteca() : multaASerPaga.getEmprestimo().getUsuarioBiblioteca().getIdentificadorBiblioteca();
				
				
				if(multaASerPaga.getIdGRUQuitacao() != null){ // Se a GRU da multa foi emitada, quita todas que tenham a mesma GRU.
					
					// Usado para quando o usuário emite uma única GRU para pagar várias multas //
					List<MultaUsuarioBiblioteca> multasGRU = daoMulta.findAllMultasUsuarioAtivasComMesmaGRUSistema(idPessoa, idBiblioteca, multaASerPaga.getIdGRUQuitacao());
					
					// Dá baixa em todas as multas do usuário que possuem a mesma GRU quando a confirmação é manual //
					for (MultaUsuarioBiblioteca multaUsuarioBiblioteca : multasGRU) {
					
						dao.updateFields(MultaUsuarioBiblioteca.class, multaUsuarioBiblioteca.getId(), new String[]{"status", "usuarioQuitacao", "dataQuitacao", "observacaoPagamento"}, 
							new Object[]{StatusMulta.QUITADA_MANUALMENTE, movi.getUsuarioLogado(),  dataQuitacao, 
							multaASerPaga.getObservacaoPagamento()}); // a observação digitada pelo usuário vem na multa passada no movimento, vai ser a mesma para todas as multas
					
						multaUsuarioBiblioteca.setInfoIdentificacaoMultaGerada( MultaUsuarioBibliotecaUtil.montaInformacoesMultaComprovante(multaUsuarioBiblioteca, true));
						
						enviaEmailAvisoPagamentoMulta(dao, multaUsuarioBiblioteca.getId(), multaUsuarioBiblioteca.getValorFormatado(), movi.getUsuarioDaMulta(), multaUsuarioBiblioteca.getInfoIdentificacaoMultaGerada() , dataQuitacao, movi.getNumeroReferencia());
					}
					
				}else{ // se não gerou GRU só pode quitar a própria multa.
					
					dao.updateFields(MultaUsuarioBiblioteca.class, multaASerPaga.getId(), new String[]{"status", "usuarioQuitacao", "dataQuitacao", "observacaoPagamento"}, 
							new Object[]{StatusMulta.QUITADA_MANUALMENTE, movi.getUsuarioLogado(),  dataQuitacao, multaASerPaga.getObservacaoPagamento()});
				
					enviaEmailAvisoPagamentoMulta(dao, multaASerPaga.getId(), multaASerPaga.getValorFormatado(), movi.getUsuarioDaMulta(), multaASerPaga.getInfoIdentificacaoMultaGerada() , dataQuitacao, movi.getNumeroReferencia());
					
				}
				
				
			}else{

				/**
				 * Essa parte e chamada a partir da classe @see{VerificaMultasPagasBibliotecaTimer}
				 */
				
				List<MultaUsuarioBiblioteca>  multasPagasAutomaticamente = movi.getMultasPagaAutomaticamente();
				
				List<Integer> idsMultasPagasAutomaticamente = new ArrayList<Integer>();
				
				for (MultaUsuarioBiblioteca multaQuitada : multasPagasAutomaticamente) {
					idsMultasPagasAutomaticamente.add(multaQuitada.getId());
				}
				
				if(idsMultasPagasAutomaticamente.size() > 0){
					daoMulta.darBaixaMultasAbertas(idsMultasPagasAutomaticamente, dataQuitacao);
				}
				
				
				// Para cada multa é preciso enviar o email de inforamção da quitação para o usuário //
				for (MultaUsuarioBiblioteca multaQuitada : multasPagasAutomaticamente) {
					
					enviaEmailAvisoPagamentoMulta(dao, 
							multaQuitada.getId(), 
							multaQuitada.getValorFormatado(), 
							(multaQuitada.getUsuarioBiblioteca() != null ? multaQuitada.getUsuarioBiblioteca() : multaQuitada.getEmprestimo().getUsuarioBiblioteca() ),
							MultaUsuarioBibliotecaUtil.montaInformacoesMultaComprovante(multaQuitada, true),
							dataQuitacao, 
							String.valueOf( multaQuitada.getNumeroReferencia() )  );
				}
					
			}
		
		
		}finally{
			if(dao != null ) dao.close();
			if(daoMulta != null ) daoMulta.close();
			if(daoComum != null ) daoComum.close();
		}
		
		
		return null;
	}

	
	
	
	/**
	 *   <p>Formata os dados para enviar o email de aviso de pagamento da multa realizadado.</p>
	 *
	 *   <p>Esse email de aviso deve sempre ser enviado, tanto para pagamentos manuais ou automáticos</p>
	 *
	 * @param daoUsuario
	 * @param emp
	 * @param dataEstorno
	 * @param usuarioLocado
	 * @throws DAOException
	 */
	private void enviaEmailAvisoPagamentoMulta(UsuarioBibliotecaDao daoUsuario, int idMulta, String valorMultaFormatado
			, UsuarioBiblioteca usuarioDaMulta, String dadosIdentificacaoMulta, Date dataQuitacaoMulta, String numeroReferencia) throws DAOException{
		
		Object[] informacoesUsuario =  daoUsuario.findNomeEmailUsuarioBiblioteca(usuarioDaMulta);
		
		String codigoAutenticacao =  BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idMulta, dataQuitacaoMulta);
		
		String assunto = " Confirmação de Pagamento de Multa ";
		String titulo = " Pagamento de multa confirma no sistema ";
		String mensagemUsuario = " O pagamento da multa abaixo foi confirmada com sucesso: ";
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, "Valor da Multa: "+valorMultaFormatado, dadosIdentificacaoMulta, null, null
				, numeroReferencia!= null ? "Número de referência GRU: " +numeroReferencia : "", null,  codigoAutenticacao, null);
	}
	
	
	
	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		final int QTD_CARACTERES_MAXIMO_OBSERVACAO = 300;
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoQuitaMultaUsuarioBiblioteca movi = (MovimentoQuitaMultaUsuarioBiblioteca) mov;
		
		UsuarioBibliotecaDao dao = null;
		
		
		try{
			
			dao = getDAO(UsuarioBibliotecaDao.class, mov);
		
			if(movi.isPagamentoManual()){
				
				MultaUsuarioBiblioteca multaBanco = dao.findByPrimaryKey(movi.getMultaASerPaga().getId(), MultaUsuarioBiblioteca.class, new String[]{"status", "ativo"});
				
				if( ! multaBanco.isAtivo() ){
					lista.addErro("A multa não pode ter seu pagamento confirmado porque ela foi estornada.");
				}
				
				if(multaBanco.getStatus() == StatusMulta.QUITADA_MANUALMENTE ||
						multaBanco.getStatus() == StatusMulta.QUITADA_AUTOMATICAMENTE){
					lista.addErro("A multa já foi quitada.");
				}
				
				// Erros do programador //
				if(movi.getMultaASerPaga() == null){
					throw new IllegalArgumentException("A multa a ser paga não foi informada");
				}
				
				if(StringUtils.isEmpty(movi.getMultaASerPaga().getInfoIdentificacaoMultaGerada() )  ){
					throw new IllegalArgumentException("A multa precisa ter informações que identifiquem porque ela foi gerada. ");
				}
				
				if(StringUtils.notEmpty( movi.getMultaASerPaga().getObservacaoPagamento() )){ // Se informou alguma observação
					if(movi.getMultaASerPaga().getObservacaoPagamento().length() > QTD_CARACTERES_MAXIMO_OBSERVACAO)
						lista.addErro("A observação do pagamento pode ter no máximo "+QTD_CARACTERES_MAXIMO_OBSERVACAO+" caracteres");
				}
				
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)
						&& ! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO)
						&& ! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO)){
					lista.addErro("Caro usuário(a), você não tem permissão para confirmar pagamentos de multas no sistema");
				}
				
			}else{
				
				// Pagamento automático da multa não é validado
			}
		
		}finally{
			checkValidation(lista);
			if(dao != null ) dao.close();
		}
		
		
		
	}

	
	
	
	
	
}
