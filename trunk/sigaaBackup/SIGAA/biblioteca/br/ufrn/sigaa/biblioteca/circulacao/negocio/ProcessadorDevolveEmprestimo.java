/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/04/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.RegistroEntradaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.NotaCirculacaoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca.StatusReserva;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * Classe que contém as regras de negócio para a devolução de um empréstimo.
 *
 * @author Fred
 * @since 15/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorDevolveEmprestimo extends ProcessadorCadastro{

	
	/**
	 * Ver comentário na classe pai
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		long tempo = System.currentTimeMillis();
		
		MovimentoDevolveEmprestimo movimento = (MovimentoDevolveEmprestimo) mov;
		UsuarioGeral operador = movimento.getUsuarioLogado();
		
		EmprestimoDao dao = null;
		PermissaoDAO pDao = null;
		RegistroEntradaDao rEDao = null;
		MaterialInformacionalDao materialDao = null;
		NotaCirculacaoDao daoNota = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		/**
		 * Objeto que por padrão deve ser retornado quando é realizado alguma operação relacionado a empréstimos em circulação.
		 */
		RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao = new RetornoOperacoesCirculacaoDTO();
		
		try {
			
			dao = getDAO(EmprestimoDao.class, movimento);
			pDao = getDAO(PermissaoDAO.class, movimento, Sistema.COMUM);
			rEDao = getDAO(RegistroEntradaDao.class, movimento);
			materialDao = getDAO(MaterialInformacionalDao.class, movimento);
			daoNota = getDAO(NotaCirculacaoDao.class, movimento);
			
			SituacaoMaterialInformacional situacaoDisponivel = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoDisponivel", true, true);
			
			// Descobre as permissões do usuário.
			//usuario = dao.refresh(usuario);
			operador.setPermissoes(pDao.findPermissoesByUsuario(operador));
			
			operador.setPapeis(new ArrayList <Papel>());
			
			for (Permissao p : operador.getPermissoes())
				operador.getPapeis().add(p.getPapel());
			
			validate(movimento);
			
			
			// Pega o último empréstimo ativo do material, livrando os empréstimos estornados
			Emprestimo e = dao.findEmprestimoCirculacao(movimento.getIdMaterial(), true, true);		
			
			if (e != null){
				
				if( dao.isMaterialComComunicacaoPerdaAtiva(e.getMaterial().getId()) ){
					throw new NegocioException ("O material de código de barras: "+ e.getMaterial().getCodigoBarras() +" não pode ser devolvido, pois existe uma comunicação de perda do material no sistema.");
				}
				
				verificaPermissaoEBibliotecaEmprestimo(operador, e.getMaterial(), movimento);
				
				verificaNotasDeCirculacao(daoNota, e.getMaterial(), retornoProcessadorCirculacao);
				
				e.setDataDevolucao(new Date());
				
				// Se atrasou a entrega
				boolean atrasou = false;
				
				PoliticaEmprestimo politicaEmprestimo = e.getPoliticaEmprestimo();
				
				if (!politicaEmprestimo.isPrazoContadoEmHoras())
					atrasou = CalendarUtils.estorouPrazo(e.getPrazo(), new Date());
				else
					atrasou = CalendarUtils.estorouPrazoConsiderandoHoras(e.getPrazo(), new Date());
					
				// Se atrasou o empréstimo, deve-se criar uma suspensão para o mesmo, impedindo o usuário
				// de realizar empréstimos ou renovações por um determinado tempo.
				List<PunicaoAtrasoEmprestimoStrategy> punicoesStrategy = null;
				
				List<String> mensagensComprovanteDevolucao = new ArrayList<String>();
				List<PunicaoAtrasoEmprestimoBiblioteca> punicoesSofridas = new ArrayList<PunicaoAtrasoEmprestimoBiblioteca>(); 
				
				if (atrasou){
					
					punicoesStrategy = new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiasPunicao();
					
					for (PunicaoAtrasoEmprestimoStrategy punicaoStrategy : punicoesStrategy) {
						PunicaoAtrasoEmprestimoBiblioteca punicao = punicaoStrategy.criarPunicaoAutomatica(e, e.getPrazo(), new Date());
						mensagensComprovanteDevolucao.add(punicao.getMensagemComprovante());
						punicoesSofridas.add(punicao);
						dao.create(punicao);
					}
				}
				
				e.setUsuarioRealizouDevolucao( (Usuario) operador );
				
				e.setSituacao(Emprestimo.DEVOLVIDO);
				
				// Atualiza o empréstimo, salvando a data da devolução, o usuário que realizou a devolução e a nova situação do empréstimo.
				dao.updateFields(Emprestimo.class, e.getId(), new String []{"dataDevolucao", "usuarioRealizouDevolucao", "situacao"}, new Object [] {e.getDataDevolucao(), e.getUsuarioRealizouDevolucao(), e.getSituacao()});
				
				////// Atualiza a situação do material para DISPONÍVEL //////
				List<Integer> idMaterialDevolucao = new ArrayList<Integer>();
				idMaterialDevolucao.add(  e.getMaterial().getId());
				materialDao.atualizaSituacaoDeMateriais(idMaterialDevolucao, situacaoDisponivel.getId());

				
				/* ***********************************************************************************
				 *  Caso o sistema trabalhe com reservas eCaso exista reserva para o título do material
				 *  
				 *  - Muda a próxima reserva para o status EM_ESPERA
				 *  - Avisa aos usuário que sua reserva se encontra disponível
				 *  
				 * ************************************************************************************/
				
				if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
				
					reservaDao = getDAO(ReservaMaterialBibliotecaDao.class, mov);
					ReservaMaterialBiblioteca proximaReserva = reservaDao.findProximaReservaSolicitadaDoTituloDoMaterial(e.getMaterial().getId());
					
					if(proximaReserva != null){
						Date prazoRetirarMaterial  = CalendarUtils.adicionaDias(new Date(), ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA));
						
						prazoRetirarMaterial = CirculacaoUtil.prorrogaPrazoConsiderandoFimDeSemanaEInterrupcoesDaBiblioteca( prazoRetirarMaterial, e.getMaterial().getBiblioteca());
						
						reservaDao.updateFields(ReservaMaterialBiblioteca.class, proximaReserva.getId(), new String []{"status", "dataEmEspera", "prazoRetiradaMaterial"}, new Object [] { StatusReserva.EM_ESPERA, new Date(), prazoRetirarMaterial } );
						enviaEmailAvisoReservaDisponivel(movimento, proximaReserva, prazoRetirarMaterial);
						
						retornoProcessadorCirculacao.addMensagemAosUsuarios("ATENÇÃO o material "+e.getMaterial().getCodigoBarras()+" está reservado !  "
								+"( próximo usuário: "+proximaReserva.getUsuarioReserva().getNome()+","
								+" prazo de retirada do material: "+new SimpleDateFormat("dd/MM/yyyy").format(prazoRetirarMaterial)+" )");
						
					}
				}

				
				retornoProcessadorCirculacao.emprestimoRetornado =  CirculacaoUtil.montaInformacoesEmprestimoDevolvido(e, punicoesSofridas );
				
				retornoProcessadorCirculacao.addOperacaoRealizada( new OperacaoBibliotecaDto (OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO, e.getMaterial().getId(), e.getId() , e.getMaterial().getCodigoBarras()
						,  BibliotecaUtil.obtemDadosMaterialInformacional( e.getMaterial().getId() ) , e.getDataDevolucao(),  e.getPrazo()  )    );
				
				retornoProcessadorCirculacao.mensagensComprovanteDevolucao = mensagensComprovanteDevolucao;
				
				enviaEmailConfirmacaoDevolucao(movimento,  retornoProcessadorCirculacao.getOperacoesRealizadas(), retornoProcessadorCirculacao.emprestimoRetornado, retornoProcessadorCirculacao.mensagensComprovanteDevolucao);
				
				return retornoProcessadorCirculacao;
			}

			throw new NegocioException("Empréstimo não encontrado.");

		} catch ( NegocioException re){           // Erros nas regras de negócio.
			throw re;
		} catch (DAOException de){                // Erros de acesso ao banco
			de.printStackTrace();
			throw de;
		} catch (Exception e){                   // Erros não imaginados
			e.printStackTrace();
			throw new ArqException(e.getMessage());	
		} finally {
			if (dao != null )  dao.close();
			if (rEDao != null) rEDao.close();
			if (pDao != null)  pDao.close();
			if (materialDao != null)  materialDao.close();
			if (daoNota != null) daoNota.close();
			if (reservaDao != null) reservaDao.close();
			
			System.out.println("    ***    Devolver empréstimos demorou: "+ (System.currentTimeMillis()-tempo) +" ms       ***     ");  // tempo <= 1s
		}
	}
	
	
	
	
	/** 
	 * <p> Verifica se o material pertence à biblioteca do operador para permitir empréstimo </p> 
	 * <p> <i> ( setor de informação e referência pode fazer empréstimos institucionais de materiais ) </i> </p>
	 *
	 * @throws SegurancaException
	 */
	private void verificaPermissaoEBibliotecaEmprestimo(UsuarioGeral operador,  MaterialInformacional material, MovimentoDevolveEmprestimo movimento)throws NegocioException{
		
		try {
			
			if(! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){ // Administradores gerais podem operar em qualquer biblioteca
			
				UFRNUtils.checkRole(
						operador,
						material.getBiblioteca().getUnidade(),
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
				);
			}
			
		} catch (SegurancaException ex){
			throw new NegocioException ("O material de código de barras: "+ material.getCodigoBarras() +" não pertence a uma biblioteca onde o senhor(a) possua permissão para operar.");
		}
		
		
		try {
			
			//////////////////////////////////////////////////////////////////////////////
			// Verifica se o usuário está no momento operando na biblioteca do material //
			// Utilizado para os empréstimos feitos no módulo de circulação desktop,    //
			// onde o usuário opera em uma biblioteca por vez                           //   
			//////////////////////////////////////////////////////////////////////////////

			if( movimento.isOperandoBibliotecaEspecifica() ){
				if(! movimento.getIdBibliotecaOperacao().equals(material.getBiblioteca().getId())){
					throw new SegurancaException();
				}
			}
			
		} catch (SegurancaException ex){
			throw new NegocioException ("O material de código de barras: "+ material.getCodigoBarras() +" não pertence a biblioteca onde o senhor(a) está operando no momento.");
		}
		
	}
	
	
	
	
	/**
	 *   <p>Envia um email para comprovar a renovação. </p> 
	 *
	 *   <p>IMPORTANTE: Chamado também de @see {@link ProcessadorDevolveEmprestimoMaterialPerdido}</p> 
	 *
	 * @param usuarioBibliotecaRenovacao
	 * @throws DAOException
	 */
	public static void enviaEmailConfirmacaoDevolucao(MovimentoCadastro movimento, List <OperacaoBibliotecaDto> devolucoes, EmprestimoDto emprestimoDevolvido, List<String> mensagensComprovanteDevolucao) throws DAOException{
		
		/* 
		 * informacoesUsuario[0] == nome Usuario
		 * informacoesUsuario[1] == email Usuario
		 */
		Object[] informacoesUsuario = getDAO( UsuarioBibliotecaDao.class, movimento).findNomeEmailUsuarioBiblioteca( new UsuarioBiblioteca(emprestimoDevolvido.usuario.idUsuarioBiblioteca));
		
		String assunto = " Confirmação de Devolução dos Empréstimos ";
		String titulo = " Empréstimos Devolvidos ";
		String mensagemUsuario = " Os empréstimos abaixo foram devolvidos com sucesso: ";
		
		List<String> listaInformacoesEmail = new ArrayList<String>();
		
		for (OperacaoBibliotecaDto devolucao : devolucoes) {
			
			listaInformacoesEmail.add(
					" <p> "
				    + " <strong> Prazo: </strong>"+ devolucao.getPrazoFormatado() 
					+ " <strong> Data da Devolução: </strong>"+devolucao.getDataRealizacaoFormatada() 
					+"  <strong>Material:</strong> " + devolucao.getInfoMaterial()
					+" </p>");
		}
		
		StringBuilder mensagemPunicaoUsuario = new StringBuilder();
		
		for (String mensagem : mensagensComprovanteDevolucao) {
			mensagemPunicaoUsuario.append(" "+ mensagem+" <br/> ");
		}
		
		String codigoAutenticacao = emprestimoDevolvido.numeroAutenticacao;
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, null, null, null, listaInformacoesEmail
				, mensagemPunicaoUsuario.toString(), null, codigoAutenticacao, null);
	}
	
	
	/**
	 *  Envia um email avisando a reserva
	 * 
	 * @throws DAOException 
	 */
	public static void enviaEmailAvisoReservaDisponivel(Movimento mov, ReservaMaterialBiblioteca reservaDisponivel, Date prazoRetirarMaterial) throws DAOException {
		
			// informacoesUsuario[0] == nome Usuario
			// informacoesUsuario[1] == email Usuario
			Object[] informacoesUsuario = getDAO( UsuarioBibliotecaDao.class, mov).findNomeEmailUsuarioBiblioteca(reservaDisponivel.getUsuarioReserva());
			
			ReservaMaterialBibliotecaUtil.enviaEmailReservaDisponivel(new EnvioEmailBiblioteca(), (String)informacoesUsuario[0], (String)informacoesUsuario[1], reservaDisponivel.getId(), reservaDisponivel.getTituloReservado().getId(), prazoRetirarMaterial);
	}

	
	
	
	/**
	 * <p>Método que verifica se o material tem alguma nota de circulação.
	 * Caso seja uma nota bloqueante, lança uma exeção para não conseguir realizar o empréstimo, 
	 * caso contrário apenas adiciona a lista que será mostrada ao operador/usuário</p>
	 * 
	 * <p>Método chamado também do @see {@link ProcessadorDevolveEmprestimoMaterialPerdido}<p>
	 * 
	 * @param daoNota
	 * @param retorno
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public static void verificaNotasDeCirculacao(NotaCirculacaoDao  daoNota, MaterialInformacional material, RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao) throws DAOException{
		
		
		List<NotaCirculacao> notas = daoNota.getNotasAtivasDoMaterial(material.getId());
		
		for (NotaCirculacao notaCirculacao : notas) {
			
			// No bloqueio não faz sentido impedir a devolução do material, então mostra como se fosse uma nota não bloqueante
			if(notaCirculacao.isBloquearMaterial()){
				retornoProcessadorCirculacao.addMensagemAosUsuarios("Nota de Circulação do material "+material.getCodigoBarras()+" : "+notaCirculacao.getNota());
			}else{
				// Se tem uma nota para mostrar da devolução, mostra a mensagem ao usuário
				if( notaCirculacao.isMostrarDevolucao()){  	
					retornoProcessadorCirculacao.addMensagemAosUsuarios("Nota de Circulação do material "+material.getCodigoBarras()+" : "+notaCirculacao.getNota());
				
					//Desativa a nota para não ser mais mostrada para o próximo usuário que emprestar o material.
					daoNota.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"mostrarEmprestimo", "mostrarRenovacao", "mostrarDevolucao"}, new Object [] {false, false, false});	
				}
			}
			
		}  
		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Ver comentário na classe pai
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate (Movimento mov) throws NegocioException, ArqException {

		MovimentoDevolveEmprestimo personalMov = (MovimentoDevolveEmprestimo) mov;

		if (personalMov.getIdMaterial() <= 0)
			throw new IllegalArgumentException("Erro no envio do Material para Devolução.");
	}
	
	
}
