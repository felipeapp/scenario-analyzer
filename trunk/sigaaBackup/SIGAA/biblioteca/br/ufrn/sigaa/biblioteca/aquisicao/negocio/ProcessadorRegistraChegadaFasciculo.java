/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
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
 *     <p>Processador que registra a chegada de um fascículo.</p>
 *     <p><i>Cria o fascículos no sistema, mas ele não aparece no acervo ainda, para isso ainda é
 * necessário que ele seja incluído no acervo pelo setor de catalogação. </i></p>
 *
 * @author jadson
 * @since 07/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRegistraChegadaFasciculo extends AbstractProcessador{

	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
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
			
			// Lock para ser alguém tentar registrar 2 fascículos ao mesmo tempo para a mesma assinatura.
			// Vai gerar problemas, porque podem ser feitos 2 update na assinatura, um sobre escrevendo o outro.
			a = dao.findByPrimaryKeyLock(a.getId(), Assinatura.class); 
			
			Fasciculo f = movimento.getFasciculo();
			
			Integer proximoNumeroGerado = a.getNumeroGeradorFasciculo();
			
			f.geraCodigoBarrasFasciculo(a.getCodigo(), ++proximoNumeroGerado);
			
			Fasciculo suplemento = movimento.getSuplemento();
			
			if(suplemento != null){ // usuário incluiu o suplemento
				int proximoNumeroGerador = suplemento.criaCodigoBarrasSuplemento(f);
				suplemento.setSuplemento(true); // Importante, lembrar de setar o suplemento com "true"
				
				f.setNumeroGeradorCodigoBarrasAnexos(proximoNumeroGerador); // IMPORTANTE ATUALIZA O NÚMERO GERADOR
			}
			
			validate(mov);    // Tem que ser chamado depois de setado o código de barras
			
			try{
				Integer numeroFasciculoAtual = Integer.parseInt(f.getNumero());
				a.setNumeroFasciculoAtual(numeroFasciculoAtual);
			}catch(NumberFormatException nfe){
				// Se não era inteiro não há o que fazer, deixa os valores atuais.
			}
			
			try{
				Integer numeroVolumeAtual = Integer.parseInt(f.getVolume());
				a.setNumeroVolumeAtual(numeroVolumeAtual);
			}catch(NumberFormatException nfe){
				// Se não era inteiro não há o que fazer, deixa os valores atuais.
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
	 * Ver comentários da classe pai.<br/>
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
			 * APENAS AQUISIÇÃO PODEM REGISTRAR	A CHEGADA DE FASCÍCULOS DE COMPRA PARA HAVER UM MAIOR CONTROLE
			 */
			if(a.isAssinaturaDeCompra()){
				if(! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
						, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					throw new NegocioException(" Usuário não tem permissão para registra a chegada de um fascículo de compra ");
				}
			}
			
			/*
			 * AQUISIÇÃO E CATALOGAÇÃO PODEM REGISTRAR A CHEGADA DE FASCÍCULOS
			 */
			try{
				// Senão é administrador geral, checa a unidade de registro da chegado do fascículo.
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					checkRole(a.getUnidadeDestino().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
				}
			}catch(SegurancaException se){
				throw new NegocioException("Usuário: "+movimento.getUsuarioLogado().getNome()+" não tem permissão para registrar fascículos para a biblioteca: "+a.getUnidadeDestino().getDescricao());
			}
			
			if(dao.countMateriaisByCodigosBarras(f.getCodigoBarras()) > 0){
				throw new NegocioException("Já existe um outro material com o mesmo código de barras no sistema: "
						+f.getCodigoBarras()+", por isso esse fascículo não pôde ser incluído.");
			}
			
			Long qtdFasciculo = daoFasciculo.countFasciculoIguaisAssinatura(a.getId(),
					f.getAnoCronologico(), f.getAno(), f.getVolume(), f.getNumero(), f.getEdicao(), f.getDiaMes());
					
			if(qtdFasciculo > 0 ){
				throw new NegocioException("Já existe um fascículo com os mesmos ano cronológico, ano, dia/mês, volume, número e edição para essa assinatura.");
			}
		
			Fasciculo suplemento = movimento.getSuplemento();
			
			if(suplemento != null){ // usuário incluiu o suplemento
				
				if(dao.countMateriaisByCodigosBarras(suplemento.getCodigoBarras()) > 0){
					throw new NegocioException("Já existe um outro material com o mesmo código de barras no sistema: "
							+suplemento.getCodigoBarras()+", por isso o suplemento não pôde ser incluído.");
				}
				
				qtdFasciculo = daoFasciculo.countFasciculoIguaisAssinaturaSuplemento(a.getId(),
						suplemento.getAnoCronologico(), suplemento.getAno(), suplemento.getVolume(), suplemento.getNumero(), suplemento.getEdicao(), suplemento.getDiaMes() , f.getId());
						
				if(qtdFasciculo > 0 ){
					throw new NegocioException("Já existe um fascículo com os mesmos ano cronológico, ano, dia/mês, volume, número e edição do suplemento para essa assinatura.");
				}
				
			}
			
		}finally{
			dao.close();
			daoFasciculo.close();
		}
		
	}

}
