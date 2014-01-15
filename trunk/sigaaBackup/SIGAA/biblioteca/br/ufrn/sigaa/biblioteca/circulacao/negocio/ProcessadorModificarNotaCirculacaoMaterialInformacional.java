/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/06/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.NotaCirculacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 * <p> Processador que contém as regras de negócio para incluir notas de circulaçãoe m um material informacional da biblioteca </p>
 *
 * <p> <i> Não é permitido bloquear o material no último dia em que ele vai ser renovado pelo usuário, para não gerar o transtorno do usuário 
 * tentar renovar e não conseguir e ficar suspenso. Tem que dar um prazo de pelo menos 1 dia para o usuário poder entregar o material. 
 * <br/>
 *        Se o material estiver emprestado, deve ser enviado um email ao usuário para informar que o material que ele possui foi bloqueado 
 *        e não poderá mais ser renovado.</i> 
 * </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorModificarNotaCirculacaoMaterialInformacional extends AbstractProcessador {

	
	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoModificarNotaCirculacao movimento = (MovimentoModificarNotaCirculacao) mov;
		List<NotaCirculacao> notas = movimento.getNotas();
		List<MaterialInformacional> materiais = movimento.getMateriais();
	
		NotaCirculacaoDao dao = null;
		EmprestimoDao empDao = null;
		MaterialInformacionalDao daoMaterial = null;
		
		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa"); 
		String textoRodape = RepositorioDadosInstitucionais.get("rodapeSistema");
		
		try{
			
			dao = getDAO(NotaCirculacaoDao.class, movimento);
			empDao = getDAO(EmprestimoDao.class, movimento);
			daoMaterial = getDAO(MaterialInformacionalDao.class, movimento);
			
			for (NotaCirculacao notaCirculacao : notas) {
				
				MaterialInformacional material = notaCirculacao.getMaterial();
				
				if (SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento())) {
					if( ! dao.existeNotaCirculacaoAtivasDoMaterial( material.getId()) ){

						if (notaCirculacao.isBloquearMaterial()) {
							notaCirculacao.setMostrarEmprestimo(false);
							notaCirculacao.setMostrarRenovacao(false);
							notaCirculacao.setMostrarDevolucao(false);
						}
						
						if(! material.isEmprestado()){ 
							
							dao.create(notaCirculacao);

							// Não precisa registrar alteração no material quando uma nota de circulaçao é incuída ou removida
							// Além disso tava dando erro quando era acessado pela aba de circulação
							//daoMaterial.registraAlteracaoMaterial(material, null, true);
							
						}else{
							
							if(! notaCirculacao.isBloquearMaterial()){
								
								dao.create(notaCirculacao);
								
								// Não precisa registrar alteração no material quando uma nota de circulaçao é incuída ou removida
								// Além disso tava dando erro quando era acessado pela aba de circulação
								// daoMaterial.registraAlteracaoMaterial(material, null, true);
								
							}else{
								erros.addAll( verificaRegrasBloqueioMaterialEmprestadoECriaNota(empDao, daoMaterial, siglaSigaa, textoRodape, notaCirculacao, movimento, erros, true));
							}
						}
						
					}else{
						erros.addErro("A nota de circulação para o material \""+material.getCodigoBarras()+"\" não pôde ser criada porque ele já possui uma nota de circulação ativa.");
					}
				} else if (SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento())) {

						materiais.remove(material);
						
						if (notaCirculacao.isBloquearMaterial()) {
							notaCirculacao.setMostrarEmprestimo(false);
							notaCirculacao.setMostrarRenovacao(false);
							notaCirculacao.setMostrarDevolucao(false);
						}
						
						
						
						if (material.isEmprestado() && notaCirculacao.isBloquearMaterial() ) { // Se o material está emprestado e sua nota passou a ser bloqueante, as regras devem ser verificadas.
							erros.addAll(verificaRegrasBloqueioMaterialEmprestadoECriaNota(empDao, daoMaterial, siglaSigaa, textoRodape, notaCirculacao, movimento, erros, false));
						} else {
							/**
							 * Atualiza os campos da nota 
							 */
							dao.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{ "nota", "bloquearMaterial", "mostrarEmprestimo", "mostrarRenovacao", "mostrarDevolucao" }, new Object [] { notaCirculacao.getNota(), notaCirculacao.isBloquearMaterial(), notaCirculacao.isMostrarEmprestimo(), notaCirculacao.isMostrarRenovacao(), notaCirculacao.isMostrarDevolucao() });
						}
					
				} else {

					materiais.remove(material);
					
					/**
					 * Desativa todos os campos da nota 
					 */
					dao.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"bloquearMaterial", "mostrarEmprestimo", "mostrarRenovacao", "mostrarDevolucao"}, new Object [] {false, false, false, false});
										
				}
			}

			if (SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento()) || 
					SigaaListaComando.REMOVER_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento())) {
				for (MaterialInformacional material : materiais) {
					erros.addErro("Não existe nota de circulação ativa para o material \""+material.getCodigoBarras()+"\".");
				}
			}
			
		}finally{
			if(dao != null) dao.close();
			
			if(empDao != null) empDao.close();
			
			if(daoMaterial != null) daoMaterial.close();
		}
		
		return erros;
	}

	
	
	/**
	 * Método que verifica as regras para incluir notas de circulação em materiais emprestados.
	 *
	 * @param nota
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private ListaMensagens verificaRegrasBloqueioMaterialEmprestadoECriaNota(EmprestimoDao empDao, MaterialInformacionalDao daoMaterial, String siglaSigaa, String textoRodape
			, NotaCirculacao notaCirculacao, MovimentoModificarNotaCirculacao movimento, ListaMensagens erros, boolean incluir) throws DAOException, NegocioException{
		
		
		MaterialInformacional material = notaCirculacao.getMaterial();
		
		Emprestimo e = empDao.findEmAbertoByIdMaterial(material.getId());
		
		if(e != null){ // Obs.: Alguns materiais migrados estão emprestados mas não possuem empréstimos.
			
			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
			
			// Se a nota é bloqueante e é o último dia para a renovação do material
			if( e.podeRenovar() && formatador.format(e.getPrazo()).equals( formatador.format(new Date())) && ! e.isAtrasado() ){
				erros.addErro("Nota para o material "+material.getCodigoBarras()+" não foi incluída. O material não pode ser bloqueado no último dia do prazo da sua renovação.");
			}else{
				
				if (incluir) {
					empDao.create(notaCirculacao);  // Cria a nota de bloqueio do material
				} else {
					// Atualiza para nota de bloqueio de material
					empDao.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{ "nota", "bloquearMaterial", "mostrarEmprestimo", "mostrarRenovacao", "mostrarDevolucao" }, new Object [] { notaCirculacao.getNota(), notaCirculacao.isBloquearMaterial(), notaCirculacao.isMostrarEmprestimo(), notaCirculacao.isMostrarRenovacao(), notaCirculacao.isMostrarDevolucao() });
				}
				
				if(! e.isAtrasado() && e.podeRenovar() ){ // Se está atrasado não precisa enviar email de aviso ao usuário porque ele não ia poder renovar o empréstimos mesmo
					
					List<Object> usuariosDaPessoa = daoMaterial.findInformacoesDoUsuarioDoEmprestimoMaterial(material.getId());
					
					// Envia emial para todos os emails que a pessoa tenha.
					for (Object object : usuariosDaPessoa) {
						Object[] temp = (Object[]) object;
						enviaEmailInformacionalBloqueio(siglaSigaa, textoRodape, (String)temp[0], (String)temp[1], material.getInformacao(), notaCirculacao.getId(), notaCirculacao.getDataCadastro(), notaCirculacao.getNota());
					}
					
				}
				
			}
		}
		
		return erros;
		
	}
	
	
	
	/**
	 * Método que envia um email ao usuário para informar que o material foi bloqueado.
	 * @throws DAOException 
	 *
	 */
	private void enviaEmailInformacionalBloqueio(String siglaSigaa, String textoRodape, String nomePessoa, String emailUsuario, String infoMaterial, int idNota, Date dataCasdastroNota, String notaBloqueio) throws DAOException{
		
		String assunto = " Aviso de Bloqueio do Material ";
		String titulo = " Bloqueio do Material ";
		String mensagemUsuario = "O material: <i>\""+infoMaterial+"\"</i> , que estava emprestado para o(a) senhor(a), foi bloqueado pelo seguinte motivo: ";
		
		String mensagemNivel3Email =  notaBloqueio;
		
		String codigoAutenticacao = BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idNota, dataCasdastroNota);
		
		String mensagemAlertaCorpo = "Não será mais possível renovar o seu empréstimo. Ele deve ser devolvido na biblioteca no prazo atual.";
		
		String mensagemAgradecimento = "Agradecemos a sua compreensão.";
		
		new EnvioEmailBiblioteca().enviaEmail( nomePessoa, emailUsuario, assunto, titulo
				, EnvioEmailBiblioteca.AVISO_BLOQUEIO_MATERIAL_EMPRESTADO, mensagemUsuario, null, null, mensagemNivel3Email, null
				,mensagemAlertaCorpo, mensagemAgradecimento, codigoAutenticacao, null);
	}
	
	
	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoModificarNotaCirculacao movimento = (MovimentoModificarNotaCirculacao) mov;
		
		List<NotaCirculacao> notas = movimento.getNotas();
		
		if(SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento()) || 
				SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento())){
			
			//  Só precisa testar com o primerio pois todas as notas são iguais, só muda porque o usuário  // 
			//  pode incluir a mesma nota para vários materiais                                            //
			erros.addAll(   notas.get(0).validate()   );
		}
			
		for (NotaCirculacao notaCirculacao : notas) {
			
			if(SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento())){
				
				if ( notaCirculacao.getMaterial().getSituacao().isSituacaoDeBaixa() ){
					erros.addErro("Não pode ser incluída uma nota no material "+notaCirculacao.getMaterial().getCodigoBarras()+", pois ele está baixado ");
				}
				
				if( ! notaCirculacao.getMaterial().isAtivo())
					erros.addErro("Não pode ser incluída uma nota no material "+notaCirculacao.getMaterial().getCodigoBarras()+",  pois ele foi removido " );
			
			}
			
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				
				try{
					checkRole(notaCirculacao.getMaterial().getBiblioteca().getUnidade(), movimento
							, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
							, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				}catch (SegurancaException se) {
					
					if(SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento())){
						erros.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()+ " não tem permissão para incluir notas no material da biblioteca: "
								+notaCirculacao.getMaterial().getBiblioteca().getDescricao());
					}else if(SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL.equals(movimento.getCodMovimento())){
						erros.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()+ " não tem permissão para editar notas no material da biblioteca: "
								+notaCirculacao.getMaterial().getBiblioteca().getDescricao());
					}else{
						erros.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()+ " não tem permissão para remover notas no material da biblioteca: "
								+notaCirculacao.getMaterial().getBiblioteca().getDescricao());
					}
				}
			
			}
			
		}
		
		checkValidation(erros);
		
	}

}
