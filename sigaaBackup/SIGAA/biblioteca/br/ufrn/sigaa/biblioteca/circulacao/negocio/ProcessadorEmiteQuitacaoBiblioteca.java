/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.ResourceBundle;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.ProcessadorGeracaoEmissao;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InformacaoEmprestimosPorVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 * <p> Processador que emite o comprovante de quita��o </p>
 *
 * <p> <i> Ao emitir o comprovante, caso a conta do usu�rio biblioteca cujo comprovante foi emitido n�o esteja quitado, deve-se quit�-lo. </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorEmiteQuitacaoBiblioteca extends ProcessadorCadastro{

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException,ArqException, RemoteException {
	
		validate(movimento);
		
		MovimentoEmiteQuitacaoBiblioteca mov = (MovimentoEmiteQuitacaoBiblioteca) movimento;
		
		InformacaoEmprestimosPorVinculoUsuarioBiblioteca info = mov.getInformacaoEmprestimosPorVinculoUsuarioBiblioteca();
		UsuarioBiblioteca usuarioEmissao = null;
		
		int quantidadeEmprestimosTotais = 0;
		
		InformacoesUsuarioBiblioteca infoComprovante;
		
		if(! mov.isEmitindoDocumentoSemVinculo()){
			
			infoComprovante = info.getInfoUsuarioBiblioteca();
			quantidadeEmprestimosTotais = info.getQtdEmprestimosTotaisDoVinculo();
			
			usuarioEmissao = info.getUsuarioBiblioteca();
		}else{
			infoComprovante = mov.getInformacoesUsuarioBiblioteca();
		}
		
		
		EmissaoDocumentoAutenticado comprovante = null;
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO(movimento);
			
			
			if(usuarioEmissao != null){  // Usu�rio tem v�nculo para utilizar a biblioteca
				
				// Se o v�nculo do usu�rio n�o est� quitado ainda, realiza a quita��o  //
				if(usuarioEmissao.getId() == 0){ // V�nculo ainda n�o utilizado, mas cujo documento foi emitido
					
					usuarioEmissao.setQuitado(true);
					usuarioEmissao.setDataQuitacao(new Date());
					usuarioEmissao.setUsuarioRealizouQuitacao(  (Usuario) movimento.getUsuarioLogado());
					dao.create(usuarioEmissao);
					
					infoComprovante.setIdUsuarioBiblioteca(usuarioEmissao.getId()); // caso esteja emitindo para um v�nculo ainda n�o existente.
					
				}else{ // V�nculo utilizado pelo usu�rio
					if(usuarioEmissao.isQuitado() == false){
						dao.updateFields(UsuarioBiblioteca.class, usuarioEmissao.getId(), new String[]{"quitado", "dataQuitacao", "usuarioRealizouQuitacao"}, new Object[]{true, new Date(), (Usuario) movimento.getUsuarioLogado()});
					}
				}
			
			}
			
			/// Gera e salva no banco o documento de quita��o
				
			EmissaoDocumentoAutenticado emissao = new EmissaoDocumentoAutenticado();
			emissao.setDataEmissao(new Date());
			emissao.setIdentificador(infoComprovante.getCodigoIdentificacaoUsuario());
			emissao.setTipoDocumento(TipoDocumentoAutenticado.DECLARACAO_QUITACAO_BIBLIOTECA);
			
			boolean controleNumero = false;
			
			if ( !controleNumero )
				emissao.setPrng(UFRNUtils.toSHA1Digest(String.valueOf(Math.random())));
			else {
				ResourceBundle bundle = ResourceBundle.getBundle("br.ufrn.arq.seguranca.autenticacao.validadores");
				emissao.setPrng(bundle.getString("prng_documento").trim());
			}
			emissao.setCodigoSeguranca(AutenticacaoUtil.geraCodigoValidacao(emissao, infoComprovante.getNomeUsuario()+quantidadeEmprestimosTotais));
			emissao.setSubTipoDocumento(SubTipoDocumentoAutenticado.DECLARACAO_QUITACAO_BIBLIOTECA);
			emissao.setEmissaoDocumentoComNumero(controleNumero);
			
			// informa��es complementares para conseguir reimprimir o documento de quita��o //
			if(! mov.isEmitindoDocumentoSemVinculo()){
				emissao.setDadosAuxiliares("#"+infoComprovante.getIdUsuarioBiblioteca());   
				
			}else{
				
				if(mov.getIdPessoaEmissaoComprovante() != null)
					emissao.setDadosAuxiliares("$"+mov.getIdPessoaEmissaoComprovante());
			}
			
			MovimentoCadastro cad = new MovimentoCadastro();
			cad.setCodMovimento(ArqListaComando.GERAR_EMISSAO_DOCUMENTO_AUTENTICADO);
			cad.setObjMovimentado(emissao);
			cad.setUsuarioLogado(movimento.getUsuarioLogado());
			cad.setRegistroEntrada(movimento.getUsuarioLogado().getRegistroEntrada());
			cad.setSistema(Sistema.COMUM);

			ProcessadorGeracaoEmissao processador = new ProcessadorGeracaoEmissao();
			comprovante =  (EmissaoDocumentoAutenticado)  processador.execute(cad);	
			
			
		}finally{
			if(dao != null)  dao.close();
		}
		
		
		
		return comprovante;
	}

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		/*
		 *  Verifica a principal regras para poder quitar o v�nculo: se o usu�rio n�o possui empr�stimos em aberto !!!
		 */
		
		MovimentoEmiteQuitacaoBiblioteca mov = (MovimentoEmiteQuitacaoBiblioteca) movimento;
		
		InformacaoEmprestimosPorVinculoUsuarioBiblioteca info = mov.getInformacaoEmprestimosPorVinculoUsuarioBiblioteca();
		UsuarioBiblioteca usuarioEmissao = null;
		
		EmprestimoDao dao = null;
		
		try{
			dao = getDAO(EmprestimoDao.class, mov);
		
		
			if(! mov.isEmitindoDocumentoSemVinculo()){
				
				usuarioEmissao = info.getUsuarioBiblioteca();
				
				if(usuarioEmissao != null && usuarioEmissao.getId() > 0){
					Integer qtdEmprestimosAtivos = dao.countEmprestimosAtivosByUsuario(usuarioEmissao);
					if(qtdEmprestimosAtivos != null && qtdEmprestimosAtivos >0)
						throw new NegocioException("N�o � poss�vel quitar o v�nculo selecionado porque ele ainda possui empr�stimos abertos.");
				}	
					
			}
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	
}
