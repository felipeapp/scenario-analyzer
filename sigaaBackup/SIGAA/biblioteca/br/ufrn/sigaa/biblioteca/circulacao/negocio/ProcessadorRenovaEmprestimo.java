/*
 * ProcessadorRenovaEmprestimos.java
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.NotaCirculacaoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * Classe que cont�m as regras de neg�cio para a renova��o de um empr�stimo.
 *
 * @author Fred de Castro
 * @since 06/10/2008
 * @version 1.0 cria��o da classe
 *
 */
public class ProcessadorRenovaEmprestimo extends ProcessadorCadastro{

	
	/**
	 * Ver coment�rios na classe pai.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		long tempo = System.currentTimeMillis();
		
		EmprestimoDao dao = null;
		NotaCirculacaoDao daoNota = null;
		ConsultasEmprestimoDao consultaEmprestimoDao = null;
		
		/**
		 * Objeto que por padr�o deve ser retornado quando � realizado alguma opera��o relacionado a empr�stimos em circula��o.
		 */
		RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao = new RetornoOperacoesCirculacaoDTO();
		
		try {
			
			MovimentoRenovaEmprestimo movimentoRenovaEmprestimo = (MovimentoRenovaEmprestimo) mov;
			
			dao = getDAO(EmprestimoDao.class, movimentoRenovaEmprestimo);
			daoNota = getDAO(NotaCirculacaoDao.class, movimentoRenovaEmprestimo);
			consultaEmprestimoDao = getDAO(ConsultasEmprestimoDao.class, movimentoRenovaEmprestimo);
			
			validate(movimentoRenovaEmprestimo);
			
			// Carrega alguns dados necess�rios.
			List <Emprestimo> emprestimosAtivos = consultaEmprestimoDao.findEmprestimosAtivosByUsuario(movimentoRenovaEmprestimo.getUsuarioBiblioteca().getId());
			
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosAtrazados(emprestimosAtivos);
			
			// Valida se o usu�rio est� com puni��o na biblioteca. Se possuir, j� lan�a exece��o e n�o verifica mais nada
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoes(
					movimentoRenovaEmprestimo.getUsuarioBiblioteca().getIdentificadorPessoa()
					, movimentoRenovaEmprestimo.getUsuarioBiblioteca().getIdentificadorBiblioteca()
					);
	

			// Guarda temporariamente os empr�stimos renovados.
			List<Emprestimo> emprestimosRenovados = new ArrayList<Emprestimo>();
			
			
			for (Integer idMaterialRenovando : movimentoRenovaEmprestimo.getIdsMateriais()) {
				
				// Como est� processando v�rios empr�stimos de uma vez, vai parar no primeiro que der erro.
				// Salva a id material atual para saber em qual deu erro e poder avisar corretamente ao usu�rio.
				movimentoRenovaEmprestimo.setIdMaterialProcessando(idMaterialRenovando);
				
				Emprestimo e = retornaEmprestimoASerRenovado(emprestimosAtivos, idMaterialRenovando);
				
				if (e == null)
					throw new NegocioException("O Empr�stimo n�o pode ser renovado pois o material n�o est� emprestado a esse usu�rio.");
				else{
				
					if( dao.isMaterialComComunicacaoPerdaAtiva(e.getMaterial().getId()) ){
						throw new NegocioException ("O material de c�digo de barras: "+ e.getMaterial().getCodigoBarras() +" n�o pode ser renovado, pois existe uma comunica��o de perda do material no sistema.");
					}
					
					PoliticaEmprestimo politicaEmprestimo = dao.refresh(e.getPoliticaEmprestimo());
					
					// Verifica se o tipo do empr�stimo n�o � fotoc�pia
					if (! e.podeRenovar())
						throw new NegocioException("Este empr�stimo n�o pode ser renovado, pois a quantidade de renova��es permitidas foi ultrapassada.");
					
					// Verifica se o servi�o de empr�stimos e renova��o est�o ativos na biblioteca
					CirculacaoUtil.verificaServicoEmprestimosEstaoAtivos(e.getMaterial().getId(), e.getMaterial().getCodigoBarras());
					
					
					// Verifica se o material tem alguma nota de circula��o
					verificaNotasDeCirculacao(daoNota, e, retornoProcessadorCirculacao);
					
					// Inicializa o tipo de material antes de chamar a classe que calcula as datas.
					Hibernate.initialize(e.getMaterial().getTipoMaterial());
					
					// O prazo agora vai ser calculado denovo como se fosse um empr�stimo e � adicionado ao prazo atual.
					// Calcula e seta o prazo do empr�stimo.
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
	
					Date novoPrazo = null;
					
					if (politicaEmprestimo.isPrazoContadoEmDias()){
						c.add(Calendar.DAY_OF_MONTH, politicaEmprestimo.getPrazoEmprestimo());
						novoPrazo = new Date (CalendarUtils.configuraTempoDaData(c.getTime(), 23, 59, 59, 999).getTime());
					} else {
						c.add(Calendar.HOUR_OF_DAY, politicaEmprestimo.getPrazoEmprestimo());
						novoPrazo = new Date(c.getTime().getTime());
					}
					
					// Cria a prorroga��o por renova��o do empr�stimo //
					ProrrogacaoEmprestimo prorrogacaoRenovacao = new ProrrogacaoEmprestimo(e, TipoProrrogacaoEmprestimo.RENOVACAO, e.getPrazo());
					prorrogacaoRenovacao.setDataAtual(novoPrazo);
					
					//configuraTipoUsuarioRenovacao(personalMov, prorrogacaoRenovacao);
					
					e.setPrazo(novoPrazo);
					
					//Calcula o prazo correto para o empr�stimo, livrando feriados e finais de semana, dependendo da biblioteca do material emprestado.
					List <ProrrogacaoEmprestimo> prorrogacoes = CirculacaoUtil.geraProrrogacoesEmprestimo(e, null);
					prorrogacoes.add(prorrogacaoRenovacao);
					
					//dao.update(e);
					dao.updateFields(Emprestimo.class, e.getId(), new String []{"prazo"}, new Object [] {e.getPrazo()});
					
					emprestimosRenovados.add(e);
					
					// Registra as prorroga��es que o empr�stimo sofreu por cair em feriados ou finais de semana, dependendo da biblioteca.
					if (!prorrogacoes.isEmpty())
						for (ProrrogacaoEmprestimo p : prorrogacoes)
							dao.create(p);
			
				} // else existe emprestimo ativo para o material a ser renovado.
			
			} // for lista de materiais a serem renovados
			
			
			// Retorna as opera��es que foram feitas para depois poder desfazer no m�dulo de circula��o, se for necess�rio.
			List <OperacaoBibliotecaDto> emprestimosRenovadosOp = new ArrayList<OperacaoBibliotecaDto>();
			
			List<Integer> idsMateriaisRenovados = new ArrayList<Integer>();
			
			for (int i = 0 ; i < emprestimosRenovados.size(); i++) {
				OperacaoBibliotecaDto op = new OperacaoBibliotecaDto(OperacaoBibliotecaDto.OPERACAO_RENOVACAO
						, emprestimosRenovados.get(i).getMaterial().getId(), emprestimosRenovados.get(i).getId()
						, movimentoRenovaEmprestimo.getUsuarioBiblioteca().getVinculo().getValor(), emprestimosRenovados.get(i).getMaterial().getCodigoBarras()
						,  BibliotecaUtil.obtemDadosMaterialInformacional(emprestimosRenovados.get(i).getMaterial().getId()),  new Date(),  emprestimosRenovados.get(i).getPrazo());
				
				idsMateriaisRenovados.add(emprestimosRenovados.get(i).getMaterial().getId());
				
				emprestimosRenovadosOp.add(op);
			}
			
			
			// Registra o empr�stimo dos T�tulos desses materiais  /// 
			RegistraEstatisticasBiblioteca.getInstance().registrarTitulosEmprestados(idsMateriaisRenovados);
			
			
			
			retornoProcessadorCirculacao.addOperacoesRealizadas(emprestimosRenovadosOp);
		
			// Toda renova��o vai enviar um email de confirma��o
			enviaEmailConfirmacaoRenovacao(movimentoRenovaEmprestimo, emprestimosRenovadosOp);
			
			return retornoProcessadorCirculacao;
			
		} finally {
			if (dao != null) dao.close();
			
			if (daoNota != null) daoNota.close();
			
			if (consultaEmprestimoDao != null) consultaEmprestimoDao.close();
			
			System.out.println("    ***    Renovar empr�stimos demorou: "+ (System.currentTimeMillis()-tempo) +" ms       ***     ");   // tempo <= 1s
		}
	}

	
	/** Retorna o empr�stimos que vai ser renovado agora */
	private Emprestimo retornaEmprestimoASerRenovado(List<Emprestimo> emprestimosAtivos, int idMaterialRenovando){
		if(emprestimosAtivos != null)
		for (Emprestimo e : emprestimosAtivos){
			if (e.getMaterial().getId() == idMaterialRenovando)
				return e;
		}
		return null;
	}
	
	
	
	/**
	 *   Envia um email para comprovar a renova��o
	 *
	 * @param usuarioBibliotecaRenovacao
	 * @throws DAOException
	 */
	private void enviaEmailConfirmacaoRenovacao(MovimentoRenovaEmprestimo movimento, List <OperacaoBibliotecaDto> emprestimosRenovadosOp) throws DAOException{
		
		/* 
		 * informacoesUsuario[0] == nome Usuario
		 * informacoesUsuario[1] == email Usuario
		 */
		Object[] informacoesUsuario =  getDAO( UsuarioBibliotecaDao.class, movimento).findNomeEmailUsuarioBiblioteca(movimento.getUsuarioBiblioteca());
		
		String assunto = " Confirma��o de Renova��o dos Empr�stimos ";
		String titulo = "  Empr�stimos Renovados ";
		String mensagemUsuario = " Os empr�stimos abaixo foram renovados com sucesso: ";
		
		List<String> listaInformacoesEmail = new ArrayList<String>();
		
		for (OperacaoBibliotecaDto operacaoEmprestimo : emprestimosRenovadosOp) {
			listaInformacoesEmail.add("<strong>Data da Renova��o: </strong>"+operacaoEmprestimo.getDataRealizacaoFormatada()
					+" <strong> Prazo para Devolu��o: </strong>"+ operacaoEmprestimo.getPrazoFormatado()
					+", "+operacaoEmprestimo.getInfoMaterial() );
		}
		
		String codigoAutenticacao = CirculacaoUtil.getCodigoAutenticacaoRenovacao(emprestimosRenovadosOp);
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, null, null, null, listaInformacoesEmail
				, null, null, codigoAutenticacao, null);
	}
	
	
	
	/**
	 * M�todo que verifica se o material tem alguma nota de circula��o.
	 * Caso seja uma nota bloqueante, lan�a uma exe��o para n�o conseguir realizar a renova��o, 
	 * caso contr�rio apenas adiciona a lista que ser� mostrada ao operador/usu�rio
	 * 
	 * @param daoNota
	 * @param retorno
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void verificaNotasDeCirculacao(NotaCirculacaoDao  daoNota, Emprestimo e, RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao) throws DAOException, NegocioException{
		
		MaterialInformacional material = e.getMaterial();
		
		List<NotaCirculacao> notas = daoNota.getNotasAtivasDoMaterial(material.getId());
		
		for (NotaCirculacao notaCirculacao : notas) {
			if(notaCirculacao.isBloquearMaterial()){  // O material est� bloqueado, n�o pode ser realizado o empr�stimo 
				throw new NegocioException("O material de c�digo de barras " + material.getCodigoBarras() + " est� bloqueado e n�o pode ser renovado. O motivo: " + notaCirculacao.getNota());
			}else{
				if(notaCirculacao.isMostrarRenovacao()){
					retornoProcessadorCirculacao.addMensagemAosUsuarios("Nota de Circula��o do material "+material.getCodigoBarras()+" : "+notaCirculacao.getNota());
					
					//Desativa a nota para n�o ser mais mostrada nem no empr�stimo nem na renova��o //
					if(e.getPoliticaEmprestimo().getQuantidadeRenovacoes() < e.getQuantidadeRenovacoes() ) // AINDA VAI PODER RENOVAR
						daoNota.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"mostrarEmprestimo"}, new Object [] {false});
					else
						daoNota.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"mostrarEmprestimo", "mostrarRenovacao"}, new Object [] {false, false});
				}
			}
		}  
	}

	
	
	
	
	
	

	/**
	 * Ver coment�rios na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate (Movimento mov) throws NegocioException, ArqException {
		
		MovimentoRenovaEmprestimo personalMov = (MovimentoRenovaEmprestimo) mov;
		UsuarioBiblioteca usuarioBiblioteca = personalMov.getUsuarioBiblioteca();
		
		
		UsuarioBibliotecaDao  dao = null;
		
		try{
			
			dao = getDAO(UsuarioBibliotecaDao.class, personalMov);
			
			if (personalMov.getIdsMateriais() == null || personalMov.getIdsMateriais().isEmpty())
				throw new IllegalArgumentException("Erro no envio dos materiais dos Empr�stimos. Contate o suporte");
	
			if (usuarioBiblioteca == null || usuarioBiblioteca.getId() <= 0)
				throw new IllegalArgumentException("Erro no Envio do Usu�rio, contate o suporte");
		
			// VERIFICA SE A SENHA EST� CORRETA  ///
			if (! usuarioBiblioteca.ehBiblioteca() && !usuarioBiblioteca.getSenha().equals(personalMov.getSenhaDigitada())){
					throw new NegocioException("Senha do Usu�rio Incorreta");
			}
			
			// VERIFICA SE O V�NCULO N�O FOI ENCERRADO
			if(usuarioBiblioteca.isQuitado())
				throw new NegocioException("O v�nculo atual foi quitado, n�o � mais poss�vel realizar empr�stimos com ele");
			
			// VERIFICA SE O V�NCULO ATRIBUIDO NO CADASTRO CONTINUA ATIVO //
			VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioBiblioteca);
			
			
			//// VERIFICA SE O USU�RIO FOI BLOQUEADO PARA EMPR�STIMO ////////
			if( usuarioBiblioteca.ehBiblioteca() )
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(null, usuarioBiblioteca.getBiblioteca().getId());
			else
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(usuarioBiblioteca.getPessoa().getId(), null);
			
			
			//// VERIFICA SE O USU�RIO N�O POSSUI CADASTRO DUPLICADO NA BIBLIOTECA ////////
			if( ! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
				if(new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getPessoa().getId())) < 0)
					throw new NegocioException("O usu�rio possui mais de um v�nculo ativo na biblioteca, quite um dos v�nculo para poder realizar novos empr�stimos. ");
			} else{
				if (new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getBiblioteca().getId())) < 0)
					throw new NegocioException("O usu�rio possui mais de um v�nculo ativo na biblioteca, quite um dos v�nculo para poder realizar novos empr�stimos. ");
			}
					
			//// VERIFICA SE A PESSOA DO USU�RIO BIBLIOTECA POSSUI CPF OU PASSAPORTE  ////////	
			if(! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
				Pessoa pessoa= dao.findByPrimaryKey(usuarioBiblioteca.getPessoa().getId(), Pessoa.class, "cpf_cnpj", "tipo", "valido", "passaporte", "internacional");
				pessoa.setId(usuarioBiblioteca.getPessoa().getId());
				VerificaSituacaoUsuarioBibliotecaUtil.verificaDadosPessoaCorretosUtilizarBiblioteca(pessoa);
			}
			
		} finally {
			if(dao != null) dao.close();
		}
	}
}
