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
 *         Processador que Contém as regras de negócio para desfazer uma 
 * operação de empréstimo, renovação ou devolução feita no sistema desktop da biblioteca.
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
	
			// a sequência das operações é: EMPRESTIMO > [RENOVACAO] > DEVOLUCAO
			// se uma for desfeita as outras na sequência também são.
	
			switch(myMov.getTipoOperacao()){
	
				case OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO:  
		
					// desfazer um empréstimo é simples, basta estornar o empréstimo e colocar a situação do maaterial para disponível			
		
					MaterialInformacional material = dao.refresh(emprestimo.getMaterial());
					
					// Atualiza a situação do material para DISPONÍVEL //
					List<Integer> idMaterial = new ArrayList<Integer>();
					idMaterial.add( material.getId());
					mDao.atualizaSituacaoDeMateriais(idMaterial, situacaoDisponivel.getId());
					
					
					// Remove todas as prorrogações deste empréstimo.
					prorrogacoes = (List<ProrrogacaoEmprestimo>) dao.findByExactField(ProrrogacaoEmprestimo.class, "emprestimo.id", emprestimo.getId());
					for (ProrrogacaoEmprestimo p : prorrogacoes)
						dao.remove(p);				
					
					// ESTORNA o empréstimo
					dao.updateFields(Emprestimo.class, emprestimo.getId(), new String []{"dataEstorno", "usuarioRealizouEstorno", "situacao", "ativo"}, new Object [] {new Date(), (Usuario) mov.getUsuarioLogado(), Emprestimo.CANCELADO, false});
					
					dao.registraOperacaoDesfeita(emprestimo , myMov.getIdOperador(), myMov.getIdAutorizador(), "EMPRÉSTIMO DESFEITO");
					
					enviarEmail(OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO, emprestimo, emprestimo.getDataEmprestimo(), myMov);
		
				break;
				case OperacaoBibliotecaDto.OPERACAO_RENOVACAO:
		
					pDao = getDAO(ProrrogacaoEmprestimoDao.class, myMov);
					
					// Se o empréstimo não foi devolvido ainda.
					//
					// Desfazer uma renovação é apagar a data de renovação, colocar renovável "true"
					// e voltar o prazo para o prazo anterior.
					
					// Procura as prorrogações de renovação deste empréstimo para poder voltar o prazo //
					prorrogacoes = pDao.findProrrogacoesByEmprestimoTipo(emprestimo, TipoProrrogacaoEmprestimo.RENOVACAO);
					
					ProrrogacaoEmprestimo p = prorrogacoes.remove(prorrogacoes.size() - 1); //Remove apenas a última renovação feita

					Date dataUltimaRenovacao = p.getDataCadastro();
					emprestimo.setPrazo(p.getDataAnterior());
					
					pDao.remove(p);
		
					dao.updateFields(Emprestimo.class, emprestimo.getId(), new String []{"prazo"}, new Object [] {emprestimo.getPrazo()});
					
					dao.registraOperacaoDesfeita(emprestimo , myMov.getIdOperador(), myMov.getIdAutorizador(), "RENOVAÇÃO DO EMPRÉSTIMO DESFEITA");

					enviarEmail(OperacaoBibliotecaDto.OPERACAO_RENOVACAO, emprestimo, dataUltimaRenovacao, myMov);
		
				break;
				case OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO:
					
					// retira a data de devolução, 
					// ativa novamente o empréstimo
					// Coloca o material como Emprestado novamente
					// retira a suspensão caso o usuário tenha ficado suspenso por causa da devolução
					
					// se numa possibilidade muito remota o empréstimo já tenha sido devolvido desfaz a 
					// suspensão caso o usuário tenha ficado suspenso
					if (emprestimo.isFinalizadoComAtraso()){  // foi devolvido
							
						List<PunicaoAtrasoEmprestimoStrategy> punicoesStrategy = new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiasPunicao();
						
						for (PunicaoAtrasoEmprestimoStrategy punicaoStrategy : punicoesStrategy) {
							punicaoStrategy.desfazPunicoesUsuario(emprestimo.getId());
						}
					}
						
					// Atualiza a situação do material para EMPRESTADO //
					List<Integer> idMaterialDevolucao = new ArrayList<Integer>();
					idMaterialDevolucao.add( emprestimo.getMaterial().getId());
					mDao.atualizaSituacaoDeMateriais(idMaterialDevolucao, situacaoEmprestado.getId());
					
					// Voltar o empréstimo para ativo //
					dao.updateFields(Emprestimo.class, emprestimo.getId(), new String []{"dataDevolucao", "usuarioRealizouDevolucao", "situacao", "ativo"}, new Object [] {null, null, Emprestimo.EMPRESTADO, true});
	
					dao.registraOperacaoDesfeita(emprestimo , myMov.getIdOperador(), myMov.getIdAutorizador(), "DEVOLUÇÃO DO EMPRÉSTIMO DESFEITA");
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
	 * Envia um e-mail para o usuário do empréstimo, de acordo com o tipo de operação.
	 * 
	 * @param operacaoEmprestimo
	 * @param emprestimo
	 * @param data
	 * @param movimento
	 * @throws DAOException
	 */
	private void enviarEmail(int operacaoEmprestimo, Emprestimo emprestimo, Date data, MovimentoDesfazOperacao movimento) throws DAOException {
		EnvioEmailBiblioteca envioEmail = new EnvioEmailBiblioteca();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy ' às ' HH:mm");
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
					mensagem = "<p>O empréstimo abaixo, realizado no dia " + sdf.format(data) + ", foi estornado no sistema. Por favor, " +
							"ignore o email de confirmação recebido anteriormente, ele não é mais válido.</p>" +
							"<br/><br/>" +
							"<span>" + dadosMaterial + "</span>";
					assunto = "Empréstimo Estornado";
					break;
				case OperacaoBibliotecaDto.OPERACAO_RENOVACAO:
					mensagem = "<p>A renovação do empréstimo abaixo, realizada no dia " + sdf.format(data) + ", foi estornada no sistema. Por favor, " +
							"ignore o email de confirmação recebido anteriormente, ele não é mais válido.</p>" +
							"<br/><br/>" +
							"<span>" + dadosMaterial + "</span>";
					assunto = "Renovação de Empréstimo Estornada";
					break;
				case OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO:
					mensagem = "<p>A devolução do empréstimo abaixo, realizada no dia " + sdf.format(data) + ", foi estornada no sistema. Por favor, " +
							"ignore o email de confirmação recebido anteriormente, ele não é mais válido.</p>" +
							"<br/><br/>" +
							"<span>" + dadosMaterial + "</span>";
					assunto = "Devolução de Empréstimo Estornada";
					break;
				default:
					throw new IllegalArgumentException("Tipo de operação de empréstimo inválida.");
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
	 * Ver comentários da classe pai.<br/>
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
			
			// a sequência das operações é: EMPRESTIMO > [RENOVACAO] > DEVOLUCAO
			// se uma for desfeita as outras na sequência também são.
	
			switch(myMov.getTipoOperacao()){
	
				case OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO:  
		
					// desfazer um empréstimo é simples, basta estornar o empréstimo e colocar a situação do maaterial para disponível			
		
					if( emprestimo.isFinalizado())
						lista.addErro("O empréstimo foi finalizado, ele não pode ser desfeito, pois o material que pertencia a ele pode estar pertencendo a outro empréstimo");
		
					if( dao.isMaterialComComunicacaoPerdaAtiva(emprestimo.getMaterial().getId()) ){
						throw new NegocioException ("O empréstimo do material de código de barras: "+ emprestimo.getMaterial().getCodigoBarras() +" não pode ser desfeito, pois existe uma comunicação de perda do material no sistema.");
					}
					
					// Se permitir estornar vai bagunçar a fila de espera //
					if( ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas() ) { // para não ter consultas extras ao banco se o sistema não utilizar reserva.
						
						int idTitulo = emprestimo.getMaterial().getTituloCatalografico().getId();
						
						if (rDao.countReservasAtivasDoTitulo(idTitulo) > 0)
							throw new NegocioException ("Impossível estornar o empréstimo, pois o Título possui reservas ativas.");
					}
					
				break;
				case OperacaoBibliotecaDto.OPERACAO_RENOVACAO:
		
					// Se o empréstimo não foi devolvido ainda.
					//
					// Desfazer uma renovação é apagar a data de renovação, colocar renovável "true"
					// e voltar o prazo para o prazo anterior.
					if (emprestimo == null) {
						lista.addErro(" O empréstimo foi desfeito, ele não existe mais na base por isso "
								+"a operação de renovação não pôde ser desfeita.");
					} else if (emprestimo.isFinalizado()) {
						lista.addErro(" O empréstimo foi finalizado, a renovação não pode ser "
								+"desfeita, pois o material que pertencia a ele pode estar pertencendo a outro empréstimo.");
					}
		
				break;
				case OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO:
					
					// retira a data de devolução, 
					// ativa novamente o empréstimo
					// Coloca o material como Emprestado novamente
					// retira a suspensão caso o usuário tenha ficado suspenso por causa da devolução

					

					SituacaoMaterialInformacional situacaoDisponivel = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoDisponivel", true, true);
					
					if (emprestimo == null) {
						lista.addErro(" O empréstimo foi desfeito, ele não existe mais na base por isso "
								+"a operação de devolução não pôde ser desfeita.");
					} else {
						int idTitulo = emprestimo.getMaterial().getTituloCatalografico().getId();
						
						if (emprestimo.getMaterial().getSituacao().getId() != situacaoDisponivel.getId())
							lista.addErro("Impossível desfazer a devolução, pois o material que pertencia ao empréstimo não está mais disponível");
						
						if (emprestimo.getUsuarioBiblioteca().isQuitado())
							lista.addErro("Impossível desfazer a devolução, pois o vínculo que o usuário realizou o emprétimos foi quitado.");
						
						
						if( ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas() ) { // para não ter consultas extras ao banco se o sistema não utilizar reserva.
							if (rDao.countReservasEmEsperaDoTitulo(idTitulo) > 0)
								lista.addErro("Impossível desfazer a devolução, pois o material possui reserva em espera.");
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