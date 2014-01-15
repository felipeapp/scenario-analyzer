/*
 * ProcessadorDesfazOperacao.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ProrrogacaoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *         Processador que Cont�m as regras de neg�cio para desfazer uma 
 * opera��o de empr�stimo, renova��o ou devolu��o feita no sistema desktop da biblioteca.
 *
 * @author jadson
 * @since 26/11/2008
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorDesfazOperacao extends ProcessadorCadastro{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		
		EmprestimoDao dao = null;
		ProrrogacaoEmprestimoDao pDao = null;
		MaterialInformacionalDao mDao = null;
		ReservaMaterialBibliotecaDao rDao = null;
		
		try {
			MovimentoDesfazOperacao myMov = (MovimentoDesfazOperacao) mov;
	
			dao = getDAO(EmprestimoDao.class, myMov);
			mDao = getDAO(MaterialInformacionalDao.class, myMov);
			rDao = getDAO(ReservaMaterialBibliotecaDao.class, myMov);
			
			Emprestimo emprestimo = dao.findByPrimaryKey(myMov.getIdEmprestimo(), Emprestimo.class);
			
			SituacaoMaterialInformacional situacaoDisponivel = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoDisponivel", true, true);
			SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			List <ProrrogacaoEmprestimo> prorrogacoes = null;
	
			// a sequ�ncia das opera��es �: EMPRESTIMO > [RENOVACAO] > DEVOLUCAO
			// se uma for desfeita as outras na sequ�ncia tamb�m s�o.
	
			switch(myMov.getTipoOperacao()){
	
				case OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO:  
		
					// desfazer um empr�stimo � simples, basta estornar o empr�stimo e colocar a situa��o do maaterial para dispon�vel			
		
					MaterialInformacional material = dao.refresh(emprestimo.getMaterial());
					
					// Atualiza a situa��o do material para DISPON�VEL //
					List<Integer> idMaterial = new ArrayList<Integer>();
					idMaterial.add( material.getId());
					mDao.atualizaSituacaoDeMateriais(idMaterial, situacaoDisponivel.getId());
					
					
					// Remove todas as prorroga��es deste empr�stimo.
					prorrogacoes = (List<ProrrogacaoEmprestimo>) dao.findByExactField(ProrrogacaoEmprestimo.class, "emprestimo.id", emprestimo.getId());
					for (ProrrogacaoEmprestimo p : prorrogacoes)
						dao.remove(p);				
					
					// ESTORNA o empr�stimo
					dao.updateFields(Emprestimo.class, emprestimo.getId(), new String []{"dataEstorno", "usuarioRealizouEstorno", "situacao", "ativo"}, new Object [] {new Date(), (Usuario) mov.getUsuarioLogado(), Emprestimo.CANCELADO, false});
					
					dao.registraOperacaoDesfeita(emprestimo , myMov.getIdOperador(), myMov.getIdAutorizador(), "EMPR�STIMO DESFEITO");
					
					enviarEmail(OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO, emprestimo, emprestimo.getDataEmprestimo(), myMov);
		
				break;
				case OperacaoBibliotecaDto.OPERACAO_RENOVACAO:
		
					pDao = getDAO(ProrrogacaoEmprestimoDao.class, myMov);
					
					// Se o empr�stimo n�o foi devolvido ainda.
					//
					// Desfazer uma renova��o � apagar a data de renova��o, colocar renov�vel "true"
					// e voltar o prazo para o prazo anterior.
					
					// Procura as prorroga��es de renova��o deste empr�stimo para poder voltar o prazo //
					prorrogacoes = pDao.findProrrogacoesByEmprestimoTipo(emprestimo, TipoProrrogacaoEmprestimo.RENOVACAO);
					
					ProrrogacaoEmprestimo p = prorrogacoes.remove(prorrogacoes.size() - 1); //Remove apenas a �ltima renova��o feita

					Date dataUltimaRenovacao = p.getDataCadastro();
					emprestimo.setPrazo(p.getDataAnterior());
					
					pDao.remove(p);
		
					dao.updateFields(Emprestimo.class, emprestimo.getId(), new String []{"prazo"}, new Object [] {emprestimo.getPrazo()});
					
					dao.registraOperacaoDesfeita(emprestimo , myMov.getIdOperador(), myMov.getIdAutorizador(), "RENOVA��O DO EMPR�STIMO DESFEITA");

					enviarEmail(OperacaoBibliotecaDto.OPERACAO_RENOVACAO, emprestimo, dataUltimaRenovacao, myMov);
		
				break;
				case OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO:
					
					// retira a data de devolu��o, 
					// ativa novamente o empr�stimo
					// Coloca o material como Emprestado novamente
					// retira a suspens�o caso o usu�rio tenha ficado suspenso por causa da devolu��o
					
					// se numa possibilidade muito remota o empr�stimo j� tenha sido devolvido desfaz a 
					// suspens�o caso o usu�rio tenha ficado suspenso
					if (emprestimo.isFinalizadoComAtraso()){  // foi devolvido
							
						List<PunicaoAtrasoEmprestimoStrategy> punicoesStrategy = new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiasPunicao();
						
						for (PunicaoAtrasoEmprestimoStrategy punicaoStrategy : punicoesStrategy) {
							punicaoStrategy.desfazPunicoesUsuario(emprestimo.getId());
						}
					}
						
					// Atualiza a situa��o do material para EMPRESTADO //
					List<Integer> idMaterialDevolucao = new ArrayList<Integer>();
					idMaterialDevolucao.add( emprestimo.getMaterial().getId());
					mDao.atualizaSituacaoDeMateriais(idMaterialDevolucao, situacaoEmprestado.getId());
					
					// Voltar o empr�stimo para ativo //
					dao.updateFields(Emprestimo.class, emprestimo.getId(), new String []{"dataDevolucao", "usuarioRealizouDevolucao", "situacao", "ativo"}, new Object [] {null, null, Emprestimo.EMPRESTADO, true});
	
					dao.registraOperacaoDesfeita(emprestimo , myMov.getIdOperador(), myMov.getIdAutorizador(), "DEVOLU��O DO EMPR�STIMO DESFEITA");
					//EmprestimoDao.registraOperacaoDesfeita(emprestimo.getUsuarioBiblioteca().getId() , myMov.getUsuarioLogado().getId(), "", emprestimo.getMaterial().getId());

					enviarEmail(OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO, emprestimo, emprestimo.getDataDevolucao(), myMov);
					
				break;
			}
	
			return null;
		
		} finally {
			if (dao != null) dao.close();
			
			if (pDao != null) pDao.close();
			
			if (mDao != null) mDao.close();
			
			if (rDao != null) rDao.close();
			
		}
	}

	/**
	 * Envia um e-mail para o usu�rio do empr�stimo, de acordo com o tipo de opera��o.
	 * 
	 * @param operacaoEmprestimo
	 * @param emprestimo
	 * @param data
	 * @param movimento
	 * @throws DAOException
	 */
	private void enviarEmail(int operacaoEmprestimo, Emprestimo emprestimo, Date data, MovimentoDesfazOperacao movimento) throws DAOException {
		EnvioEmailBiblioteca envioEmail = new EnvioEmailBiblioteca();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy ' �s ' HH:mm");
		UsuarioBibliotecaDao dao = null;
		
		try{
			dao = getDAO(UsuarioBibliotecaDao.class, movimento);
			
			Object[] informacoesUsuario = dao.findNomeEmailUsuarioBiblioteca(emprestimo.getUsuarioBiblioteca());
			String dadosMaterial = BibliotecaUtil.obtemDadosMaterialInformacional(emprestimo.getMaterial().getId());
			
			String email = (String)informacoesUsuario[1];
			String mensagem = null;
			String assunto = null;
			String titulo = null;
			
			switch(operacaoEmprestimo) {
				case OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO:
					mensagem = "<p>O empr�stimo abaixo, realizado no dia " + sdf.format(data) + ", foi estornado no sistema. Por favor, " +
							"ignore o email de confirma��o recebido anteriormente, ele n�o � mais v�lido.</p>" +
							"<br/><br/>" +
							"<span>" + dadosMaterial + "</span>";
					assunto = "Empr�stimo Estornado";
					break;
				case OperacaoBibliotecaDto.OPERACAO_RENOVACAO:
					mensagem = "<p>A renova��o do empr�stimo abaixo, realizada no dia " + sdf.format(data) + ", foi estornada no sistema. Por favor, " +
							"ignore o email de confirma��o recebido anteriormente, ele n�o � mais v�lido.</p>" +
							"<br/><br/>" +
							"<span>" + dadosMaterial + "</span>";
					assunto = "Renova��o de Empr�stimo Estornada";
					break;
				case OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO:
					mensagem = "<p>A devolu��o do empr�stimo abaixo, realizada no dia " + sdf.format(data) + ", foi estornada no sistema. Por favor, " +
							"ignore o email de confirma��o recebido anteriormente, ele n�o � mais v�lido.</p>" +
							"<br/><br/>" +
							"<span>" + dadosMaterial + "</span>";
					assunto = "Devolu��o de Empr�stimo Estornada";
					break;
				default:
					throw new IllegalArgumentException("Tipo de opera��o de empr�stimo inv�lida.");
			}
			
			titulo = assunto;
			
			envioEmail.enviaEmailSimples(
					(String)informacoesUsuario[0], 
					email, 
					assunto, 
					titulo, 
					EnvioEmailBiblioteca.AVISO_EMPRESTIMO, 
					mensagem);
		
		}finally{
			if(dao != null) dao.close();
		}
		
	}

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		EmprestimoDao dao = null;
		ReservaMaterialBibliotecaDao rDao = null;
		
		try {
			MovimentoDesfazOperacao myMov = (MovimentoDesfazOperacao) mov;

			ListaMensagens lista = new ListaMensagens();
			
			dao = getDAO(EmprestimoDao.class, myMov);
			rDao = getDAO(ReservaMaterialBibliotecaDao.class, myMov);
			
			Emprestimo emprestimo = dao.findByPrimaryKey(myMov.getIdEmprestimo(), Emprestimo.class);
			
			// a sequ�ncia das opera��es �: EMPRESTIMO > [RENOVACAO] > DEVOLUCAO
			// se uma for desfeita as outras na sequ�ncia tamb�m s�o.
	
			switch(myMov.getTipoOperacao()){
	
				case OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO:  
		
					// desfazer um empr�stimo � simples, basta estornar o empr�stimo e colocar a situa��o do maaterial para dispon�vel			
		
					if( emprestimo.isFinalizado())
						lista.addErro("O empr�stimo foi finalizado, ele n�o pode ser desfeito, pois o material que pertencia a ele pode estar pertencendo a outro empr�stimo");
		
					if( dao.isMaterialComComunicacaoPerdaAtiva(emprestimo.getMaterial().getId()) ){
						throw new NegocioException ("O empr�stimo do material de c�digo de barras: "+ emprestimo.getMaterial().getCodigoBarras() +" n�o pode ser desfeito, pois existe uma comunica��o de perda do material no sistema.");
					}
					
					// Se permitir estornar vai bagun�ar a fila de espera //
					if( ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas() ) { // para n�o ter consultas extras ao banco se o sistema n�o utilizar reserva.
						
						int idTitulo = emprestimo.getMaterial().getTituloCatalografico().getId();
						
						if (rDao.countReservasAtivasDoTitulo(idTitulo) > 0)
							throw new NegocioException ("Imposs�vel estornar o empr�stimo, pois o T�tulo possui reservas ativas.");
					}
					
				break;
				case OperacaoBibliotecaDto.OPERACAO_RENOVACAO:
		
					// Se o empr�stimo n�o foi devolvido ainda.
					//
					// Desfazer uma renova��o � apagar a data de renova��o, colocar renov�vel "true"
					// e voltar o prazo para o prazo anterior.
					if (emprestimo == null) {
						lista.addErro(" O empr�stimo foi desfeito, ele n�o existe mais na base por isso "
								+"a opera��o de renova��o n�o p�de ser desfeita.");
					} else if (emprestimo.isFinalizado()) {
						lista.addErro(" O empr�stimo foi finalizado, a renova��o n�o pode ser "
								+"desfeita, pois o material que pertencia a ele pode estar pertencendo a outro empr�stimo.");
					}
		
				break;
				case OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO:
					
					// retira a data de devolu��o, 
					// ativa novamente o empr�stimo
					// Coloca o material como Emprestado novamente
					// retira a suspens�o caso o usu�rio tenha ficado suspenso por causa da devolu��o

					

					SituacaoMaterialInformacional situacaoDisponivel = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoDisponivel", true, true);
					
					if (emprestimo == null) {
						lista.addErro(" O empr�stimo foi desfeito, ele n�o existe mais na base por isso "
								+"a opera��o de devolu��o n�o p�de ser desfeita.");
					} else {
						int idTitulo = emprestimo.getMaterial().getTituloCatalografico().getId();
						
						if (emprestimo.getMaterial().getSituacao().getId() != situacaoDisponivel.getId())
							lista.addErro("Imposs�vel desfazer a devolu��o, pois o material que pertencia ao empr�stimo n�o est� mais dispon�vel");
						
						if (emprestimo.getUsuarioBiblioteca().isQuitado())
							lista.addErro("Imposs�vel desfazer a devolu��o, pois o v�nculo que o usu�rio realizou o empr�timos foi quitado.");
						
						
						if( ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas() ) { // para n�o ter consultas extras ao banco se o sistema n�o utilizar reserva.
							if (rDao.countReservasEmEsperaDoTitulo(idTitulo) > 0)
								lista.addErro("Imposs�vel desfazer a devolu��o, pois o material possui reserva em espera.");
						}
						
					}
					
				break;
			}

			checkValidation(lista);
		
		} finally {
			if (dao != null) dao.close();
			
			if (rDao != null) rDao.close();
		}
		
	}
	
}