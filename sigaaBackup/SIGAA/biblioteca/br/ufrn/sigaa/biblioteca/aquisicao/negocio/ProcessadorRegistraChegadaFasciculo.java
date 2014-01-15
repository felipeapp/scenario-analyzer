/*
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em:  07/04/2009
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *     <p>Processador que registra a chegada de um fasc�culo.</p>
 *     <p><i>Cria o fasc�culos no sistema, mas ele n�o aparece no acervo ainda, para isso ainda �
 * necess�rio que ele seja inclu�do no acervo pelo setor de cataloga��o. </i></p>
 *
 * @author jadson
 * @since 07/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRegistraChegadaFasciculo extends AbstractProcessador{

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoRegistraChegadaFasciculo movimento = ( MovimentoRegistraChegadaFasciculo ) mov;
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO(movimento);
			
			Assinatura a = movimento.getAssinatura();
			
			// Lock para ser algu�m tentar registrar 2 fasc�culos ao mesmo tempo para a mesma assinatura.
			// Vai gerar problemas, porque podem ser feitos 2 update na assinatura, um sobre escrevendo o outro.
			a = dao.findByPrimaryKeyLock(a.getId(), Assinatura.class); 
			
			Fasciculo f = movimento.getFasciculo();
			
			Integer proximoNumeroGerado = a.getNumeroGeradorFasciculo();
			
			f.geraCodigoBarrasFasciculo(a.getCodigo(), ++proximoNumeroGerado);
			
			Fasciculo suplemento = movimento.getSuplemento();
			
			if(suplemento != null){ // usu�rio incluiu o suplemento
				int proximoNumeroGerador = suplemento.criaCodigoBarrasSuplemento(f);
				suplemento.setSuplemento(true); // Importante, lembrar de setar o suplemento com "true"
				
				f.setNumeroGeradorCodigoBarrasAnexos(proximoNumeroGerador); // IMPORTANTE ATUALIZA O N�MERO GERADOR
			}
			
			validate(mov);    // Tem que ser chamado depois de setado o c�digo de barras
			
			try{
				Integer numeroFasciculoAtual = Integer.parseInt(f.getNumero());
				a.setNumeroFasciculoAtual(numeroFasciculoAtual);
			}catch(NumberFormatException nfe){
				// Se n�o era inteiro n�o h� o que fazer, deixa os valores atuais.
			}
			
			try{
				Integer numeroVolumeAtual = Integer.parseInt(f.getVolume());
				a.setNumeroVolumeAtual(numeroVolumeAtual);
			}catch(NumberFormatException nfe){
				// Se n�o era inteiro n�o h� o que fazer, deixa os valores atuais.
			}
			
			
			a.addFasciculo(f);
			f.setAssinatura(a);
			
			a.setNumeroGeradorFasciculo(proximoNumeroGerado);
			a.setDataUltimaChegadaFasciculo(new Date());
			
			dao.create(f);
			
			if(suplemento != null){
				a.addFasciculo(suplemento);
				suplemento.setAssinatura(a);
				suplemento.setFasciculoDeQuemSouSuplemento(f);
				dao.create(suplemento);
			}
			
			dao.update(a);
			
			
		}finally{
			if(dao!= null) dao.close();
		}
		
		return null;
	}
	
	

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoRegistraChegadaFasciculo movimento = ( MovimentoRegistraChegadaFasciculo ) mov;
		
		MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class, movimento);
		FasciculoDao daoFasciculo = getDAO(FasciculoDao.class, movimento);
		
		try{
		
			Assinatura a = movimento.getAssinatura();
			Fasciculo f = movimento.getFasciculo();
			
			/*
			 * APENAS AQUISI��O PODEM REGISTRAR	A CHEGADA DE FASC�CULOS DE COMPRA PARA HAVER UM MAIOR CONTROLE
			 */
			if(a.isAssinaturaDeCompra()){
				if(! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
						, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					throw new NegocioException(" Usu�rio n�o tem permiss�o para registra a chegada de um fasc�culo de compra ");
				}
			}
			
			/*
			 * AQUISI��O E CATALOGA��O PODEM REGISTRAR A CHEGADA DE FASC�CULOS
			 */
			try{
				// Sen�o � administrador geral, checa a unidade de registro da chegado do fasc�culo.
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					checkRole(a.getUnidadeDestino().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
				}
			}catch(SegurancaException se){
				throw new NegocioException("Usu�rio: "+movimento.getUsuarioLogado().getNome()+" n�o tem permiss�o para registrar fasc�culos para a biblioteca: "+a.getUnidadeDestino().getDescricao());
			}
			
			if(dao.countMateriaisByCodigosBarras(f.getCodigoBarras()) > 0){
				throw new NegocioException("J� existe um outro material com o mesmo c�digo de barras no sistema: "
						+f.getCodigoBarras()+", por isso esse fasc�culo n�o p�de ser inclu�do.");
			}
			
			Long qtdFasciculo = daoFasciculo.countFasciculoIguaisAssinatura(a.getId(),
					f.getAnoCronologico(), f.getAno(), f.getVolume(), f.getNumero(), f.getEdicao(), f.getDiaMes());
					
			if(qtdFasciculo > 0 ){
				throw new NegocioException("J� existe um fasc�culo com os mesmos ano cronol�gico, ano, dia/m�s, volume, n�mero e edi��o para essa assinatura.");
			}
		
			Fasciculo suplemento = movimento.getSuplemento();
			
			if(suplemento != null){ // usu�rio incluiu o suplemento
				
				if(dao.countMateriaisByCodigosBarras(suplemento.getCodigoBarras()) > 0){
					throw new NegocioException("J� existe um outro material com o mesmo c�digo de barras no sistema: "
							+suplemento.getCodigoBarras()+", por isso o suplemento n�o p�de ser inclu�do.");
				}
				
				qtdFasciculo = daoFasciculo.countFasciculoIguaisAssinaturaSuplemento(a.getId(),
						suplemento.getAnoCronologico(), suplemento.getAno(), suplemento.getVolume(), suplemento.getNumero(), suplemento.getEdicao(), suplemento.getDiaMes() , f.getId());
						
				if(qtdFasciculo > 0 ){
					throw new NegocioException("J� existe um fasc�culo com os mesmos ano cronol�gico, ano, dia/m�s, volume, n�mero e edi��o do suplemento para essa assinatura.");
				}
				
			}
			
		}finally{
			dao.close();
			daoFasciculo.close();
		}
		
	}

}
