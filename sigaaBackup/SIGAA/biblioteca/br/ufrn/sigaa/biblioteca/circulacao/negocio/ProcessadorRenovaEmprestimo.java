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
 * Classe que contém as regras de negócio para a renovação de um empréstimo.
 *
 * @author Fred de Castro
 * @since 06/10/2008
 * @version 1.0 criação da classe
 *
 */
public class ProcessadorRenovaEmprestimo extends ProcessadorCadastro{

	
	/**
	 * Ver comentários na classe pai.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		long tempo = System.currentTimeMillis();
		
		EmprestimoDao dao = null;
		NotaCirculacaoDao daoNota = null;
		ConsultasEmprestimoDao consultaEmprestimoDao = null;
		
		/**
		 * Objeto que por padrão deve ser retornado quando é realizado alguma operação relacionado a empréstimos em circulação.
		 */
		RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao = new RetornoOperacoesCirculacaoDTO();
		
		try {
			
			MovimentoRenovaEmprestimo movimentoRenovaEmprestimo = (MovimentoRenovaEmprestimo) mov;
			
			dao = getDAO(EmprestimoDao.class, movimentoRenovaEmprestimo);
			daoNota = getDAO(NotaCirculacaoDao.class, movimentoRenovaEmprestimo);
			consultaEmprestimoDao = getDAO(ConsultasEmprestimoDao.class, movimentoRenovaEmprestimo);
			
			validate(movimentoRenovaEmprestimo);
			
			// Carrega alguns dados necessários.
			List <Emprestimo> emprestimosAtivos = consultaEmprestimoDao.findEmprestimosAtivosByUsuario(movimentoRenovaEmprestimo.getUsuarioBiblioteca().getId());
			
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosAtrazados(emprestimosAtivos);
			
			// Valida se o usuário está com punição na biblioteca. Se possuir, já lança execeção e não verifica mais nada
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoes(
					movimentoRenovaEmprestimo.getUsuarioBiblioteca().getIdentificadorPessoa()
					, movimentoRenovaEmprestimo.getUsuarioBiblioteca().getIdentificadorBiblioteca()
					);
	

			// Guarda temporariamente os empréstimos renovados.
			List<Emprestimo> emprestimosRenovados = new ArrayList<Emprestimo>();
			
			
			for (Integer idMaterialRenovando : movimentoRenovaEmprestimo.getIdsMateriais()) {
				
				// Como está processando vários empréstimos de uma vez, vai parar no primeiro que der erro.
				// Salva a id material atual para saber em qual deu erro e poder avisar corretamente ao usuário.
				movimentoRenovaEmprestimo.setIdMaterialProcessando(idMaterialRenovando);
				
				Emprestimo e = retornaEmprestimoASerRenovado(emprestimosAtivos, idMaterialRenovando);
				
				if (e == null)
					throw new NegocioException("O Empréstimo não pode ser renovado pois o material não está emprestado a esse usuário.");
				else{
				
					if( dao.isMaterialComComunicacaoPerdaAtiva(e.getMaterial().getId()) ){
						throw new NegocioException ("O material de código de barras: "+ e.getMaterial().getCodigoBarras() +" não pode ser renovado, pois existe uma comunicação de perda do material no sistema.");
					}
					
					PoliticaEmprestimo politicaEmprestimo = dao.refresh(e.getPoliticaEmprestimo());
					
					// Verifica se o tipo do empréstimo não é fotocópia
					if (! e.podeRenovar())
						throw new NegocioException("Este empréstimo não pode ser renovado, pois a quantidade de renovações permitidas foi ultrapassada.");
					
					// Verifica se o serviço de empréstimos e renovação estão ativos na biblioteca
					CirculacaoUtil.verificaServicoEmprestimosEstaoAtivos(e.getMaterial().getId(), e.getMaterial().getCodigoBarras());
					
					
					// Verifica se o material tem alguma nota de circulação
					verificaNotasDeCirculacao(daoNota, e, retornoProcessadorCirculacao);
					
					// Inicializa o tipo de material antes de chamar a classe que calcula as datas.
					Hibernate.initialize(e.getMaterial().getTipoMaterial());
					
					// O prazo agora vai ser calculado denovo como se fosse um empréstimo e é adicionado ao prazo atual.
					// Calcula e seta o prazo do empréstimo.
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
					
					// Cria a prorrogação por renovação do empréstimo //
					ProrrogacaoEmprestimo prorrogacaoRenovacao = new ProrrogacaoEmprestimo(e, TipoProrrogacaoEmprestimo.RENOVACAO, e.getPrazo());
					prorrogacaoRenovacao.setDataAtual(novoPrazo);
					
					//configuraTipoUsuarioRenovacao(personalMov, prorrogacaoRenovacao);
					
					e.setPrazo(novoPrazo);
					
					//Calcula o prazo correto para o empréstimo, livrando feriados e finais de semana, dependendo da biblioteca do material emprestado.
					List <ProrrogacaoEmprestimo> prorrogacoes = CirculacaoUtil.geraProrrogacoesEmprestimo(e, null);
					prorrogacoes.add(prorrogacaoRenovacao);
					
					//dao.update(e);
					dao.updateFields(Emprestimo.class, e.getId(), new String []{"prazo"}, new Object [] {e.getPrazo()});
					
					emprestimosRenovados.add(e);
					
					// Registra as prorrogações que o empréstimo sofreu por cair em feriados ou finais de semana, dependendo da biblioteca.
					if (!prorrogacoes.isEmpty())
						for (ProrrogacaoEmprestimo p : prorrogacoes)
							dao.create(p);
			
				} // else existe emprestimo ativo para o material a ser renovado.
			
			} // for lista de materiais a serem renovados
			
			
			// Retorna as operações que foram feitas para depois poder desfazer no módulo de circulação, se for necessário.
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
			
			
			// Registra o empréstimo dos Títulos desses materiais  /// 
			RegistraEstatisticasBiblioteca.getInstance().registrarTitulosEmprestados(idsMateriaisRenovados);
			
			
			
			retornoProcessadorCirculacao.addOperacoesRealizadas(emprestimosRenovadosOp);
		
			// Toda renovação vai enviar um email de confirmação
			enviaEmailConfirmacaoRenovacao(movimentoRenovaEmprestimo, emprestimosRenovadosOp);
			
			return retornoProcessadorCirculacao;
			
		} finally {
			if (dao != null) dao.close();
			
			if (daoNota != null) daoNota.close();
			
			if (consultaEmprestimoDao != null) consultaEmprestimoDao.close();
			
			System.out.println("    ***    Renovar empréstimos demorou: "+ (System.currentTimeMillis()-tempo) +" ms       ***     ");   // tempo <= 1s
		}
	}

	
	/** Retorna o empréstimos que vai ser renovado agora */
	private Emprestimo retornaEmprestimoASerRenovado(List<Emprestimo> emprestimosAtivos, int idMaterialRenovando){
		if(emprestimosAtivos != null)
		for (Emprestimo e : emprestimosAtivos){
			if (e.getMaterial().getId() == idMaterialRenovando)
				return e;
		}
		return null;
	}
	
	
	
	/**
	 *   Envia um email para comprovar a renovação
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
		
		String assunto = " Confirmação de Renovação dos Empréstimos ";
		String titulo = "  Empréstimos Renovados ";
		String mensagemUsuario = " Os empréstimos abaixo foram renovados com sucesso: ";
		
		List<String> listaInformacoesEmail = new ArrayList<String>();
		
		for (OperacaoBibliotecaDto operacaoEmprestimo : emprestimosRenovadosOp) {
			listaInformacoesEmail.add("<strong>Data da Renovação: </strong>"+operacaoEmprestimo.getDataRealizacaoFormatada()
					+" <strong> Prazo para Devolução: </strong>"+ operacaoEmprestimo.getPrazoFormatado()
					+", "+operacaoEmprestimo.getInfoMaterial() );
		}
		
		String codigoAutenticacao = CirculacaoUtil.getCodigoAutenticacaoRenovacao(emprestimosRenovadosOp);
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, null, null, null, listaInformacoesEmail
				, null, null, codigoAutenticacao, null);
	}
	
	
	
	/**
	 * Método que verifica se o material tem alguma nota de circulação.
	 * Caso seja uma nota bloqueante, lança uma exeção para não conseguir realizar a renovação, 
	 * caso contrário apenas adiciona a lista que será mostrada ao operador/usuário
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
			if(notaCirculacao.isBloquearMaterial()){  // O material está bloqueado, não pode ser realizado o empréstimo 
				throw new NegocioException("O material de código de barras " + material.getCodigoBarras() + " está bloqueado e não pode ser renovado. O motivo: " + notaCirculacao.getNota());
			}else{
				if(notaCirculacao.isMostrarRenovacao()){
					retornoProcessadorCirculacao.addMensagemAosUsuarios("Nota de Circulação do material "+material.getCodigoBarras()+" : "+notaCirculacao.getNota());
					
					//Desativa a nota para não ser mais mostrada nem no empréstimo nem na renovação //
					if(e.getPoliticaEmprestimo().getQuantidadeRenovacoes() < e.getQuantidadeRenovacoes() ) // AINDA VAI PODER RENOVAR
						daoNota.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"mostrarEmprestimo"}, new Object [] {false});
					else
						daoNota.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"mostrarEmprestimo", "mostrarRenovacao"}, new Object [] {false, false});
				}
			}
		}  
	}

	
	
	
	
	
	

	/**
	 * Ver comentários na classe pai.
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
				throw new IllegalArgumentException("Erro no envio dos materiais dos Empréstimos. Contate o suporte");
	
			if (usuarioBiblioteca == null || usuarioBiblioteca.getId() <= 0)
				throw new IllegalArgumentException("Erro no Envio do Usuário, contate o suporte");
		
			// VERIFICA SE A SENHA ESTÁ CORRETA  ///
			if (! usuarioBiblioteca.ehBiblioteca() && !usuarioBiblioteca.getSenha().equals(personalMov.getSenhaDigitada())){
					throw new NegocioException("Senha do Usuário Incorreta");
			}
			
			// VERIFICA SE O VÍNCULO NÃO FOI ENCERRADO
			if(usuarioBiblioteca.isQuitado())
				throw new NegocioException("O vínculo atual foi quitado, não é mais possível realizar empréstimos com ele");
			
			// VERIFICA SE O VÍNCULO ATRIBUIDO NO CADASTRO CONTINUA ATIVO //
			VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioBiblioteca);
			
			
			//// VERIFICA SE O USUÁRIO FOI BLOQUEADO PARA EMPRÉSTIMO ////////
			if( usuarioBiblioteca.ehBiblioteca() )
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(null, usuarioBiblioteca.getBiblioteca().getId());
			else
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(usuarioBiblioteca.getPessoa().getId(), null);
			
			
			//// VERIFICA SE O USUÁRIO NÃO POSSUI CADASTRO DUPLICADO NA BIBLIOTECA ////////
			if( ! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
				if(new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getPessoa().getId())) < 0)
					throw new NegocioException("O usuário possui mais de um vínculo ativo na biblioteca, quite um dos vínculo para poder realizar novos empréstimos. ");
			} else{
				if (new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getBiblioteca().getId())) < 0)
					throw new NegocioException("O usuário possui mais de um vínculo ativo na biblioteca, quite um dos vínculo para poder realizar novos empréstimos. ");
			}
					
			//// VERIFICA SE A PESSOA DO USUÁRIO BIBLIOTECA POSSUI CPF OU PASSAPORTE  ////////	
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
