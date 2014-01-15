/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MateriaisMarcadosParaGerarEtiquetas;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *  <p>Processador que inclui um novo fasc�culo no acervo.</p> 
 *  
 *  <p>Nesse ponto o fasc�culo j� foi criado no sistema no setor de aquisi��o com o registro da sua 
 *  chegada na biblioteca. A partir desse momento � que o bibliotec�rio de cataloga��o vai catalogar 
 *  todas as suas informa��es e ele est� vis�vel para o usu�rio final.
 *  </p>    
 *     
 *  <p>
 *  <i>
 *  Incluir um fasc�culo no acervo implica em atualizar a sua assinatura para apontar para o T�tulo. 
 *  Porque s� aqui a assinatura passar� a ter um t�tulo, antes ela tinha sido criada pelo
 *  setor de compras (sem informa��es sobre cataloga��o).
 *  </i>
 *  </p>
 *  
 * @author jadson
 * @since 26/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorIncluiFasciculoNoAcervo extends AbstractProcessador{

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		
		MovimentoIncluiFasciculoNoAcervo movimento = (MovimentoIncluiFasciculoNoAcervo) mov;
		
		validate(movimento);
		
		Fasciculo fasciculo = movimento.getFasciculo();
		
		Assinatura assinatura = movimento.getAssinatura();
		TituloCatalografico titulo = movimento.getTitulo();
		
		fasciculo.setIncluidoAcervo(true); // PARA APARECER NAS PESQUISAS DO SISTEMA
		
		
		
		/* *****************************************************************************************
		 * Se � o primeiro fasc�culo que est� sendo inclu�do, a assinatura n�o vai est� associada ao
		 * T�tulo ainda, ent�o associa aqui.
		 *******************************************************************************************/
		if(assinatura.getTituloCatalografico() == null){
			assinatura.setTituloCatalografico(titulo);
		}
		
		
		ProcessadorAtualizaFasciculo processadorAtualizaFasciculo = new ProcessadorAtualizaFasciculo();
		MovimentoAtualizaFasciculo movAuxiliar1 = new MovimentoAtualizaFasciculo(fasciculo, null, true);
		movAuxiliar1.setCodMovimento(SigaaListaComando.ATUALIZA_FASCICULO);
		movAuxiliar1.setUsuarioLogado(movimento.getUsuarioLogado());
		movAuxiliar1.setSistema(movimento.getSistema());
		processadorAtualizaFasciculo.execute(movAuxiliar1);
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(mov);
			
			// ATUALIZA A QUANTIDADE DE MATERIAIS NO CACHE
			CacheEntidadesMarc cache =  BibliotecaUtil.obtemDadosTituloCache(titulo.getId());
			cache.setQuantidadeMateriaisAtivosTitulo(cache.getQuantidadeMateriaisAtivosTitulo()+1);
			
			dao.update(assinatura);
			dao.update(cache);
			
		    // REGISTRA O FASC�CULO PARA IMPRESS�O DA ETIQUETA.
			MateriaisMarcadosParaGerarEtiquetas material
				= new MateriaisMarcadosParaGerarEtiquetas(fasciculo, (Usuario) movimento.getUsuarioLogado());
			dao.create(material);
		
			
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
		
		MovimentoIncluiFasciculoNoAcervo movimento = (MovimentoIncluiFasciculoNoAcervo) mov;
		TituloCatalografico titulo = movimento.getTitulo();
		
		FasciculoDao daoFasciculo = getDAO(FasciculoDao.class, movimento);
		
		ListaMensagens lista = new ListaMensagens();
		
		try{
			
			daoFasciculo = getDAO(FasciculoDao.class, mov);
		
			Assinatura assinatura = movimento.getAssinatura();
			Fasciculo f = movimento.getFasciculo();
			
			assinatura.setTituloCatalografico( daoFasciculo.refresh(assinatura.getTituloCatalografico()));
			
			if( StringUtils.isEmpty( f.getNumero() ) && StringUtils.isEmpty( f.getVolume() ) && f.getAnoCronologico() == null && StringUtils.isEmpty( f.getAno() ) && StringUtils.isEmpty( f.getEdicao() ) ){
				lista.addErro("Para incluir um fasc�culo no acervo, pelo menos um dos seguintes campos deve ser informado: Ano Cronol�gico, Ano, Volume, N�mero ou Edi��o.");
			}

			
			if( assinatura.getTituloCatalografico() != null && assinatura.getTituloCatalografico().getId() != titulo.getId()){
				
				CacheEntidadesMarc cache =  BibliotecaUtil.obtemDadosTituloCache(titulo.getId());
				
				throw new NegocioException("A assinatura com o c�digo "+assinatura.getCodigo()
						+" j� est� associada ao T�tulo: "+cache.getNumeroDoSistema()+" - "+cache.getAutor()+" "+cache.getTitulo()
						+" Os fasc�culo dessa assinatura n�o podem ser inclu�dos em outro T�tulo");
			}
			
			if(! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
				try{
					checkRole(f.getBiblioteca().getUnidade() , movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
				}catch (SegurancaException se) {
					lista.addErro(" O usu�rio(a): "+ mov.getUsuarioLogado().getNome()
							+ " n�o tem permiss�o para incluir exemplares na biblioteca "+f.getBiblioteca().getDescricao());
				}
			}
			
			Long qtdFasciculo = 0L;
			
			if(! f.isSuplemento()){
				 qtdFasciculo = daoFasciculo.countFasciculoIguaisAssinaturaParaAlteracao(assinatura.getId(),
							f.getAnoCronologico(), f.getAno(), f.getVolume(), f.getNumero(), f.getEdicao(), f.getDiaMes(), f.getId());
			}else{
				
				Fasciculo fTemp = daoFasciculo.refresh(f);
				
				qtdFasciculo = daoFasciculo.countFasciculoIguaisAssinaturaParaAlteracaoSuplemento(assinatura.getId(),
						f.getAnoCronologico(), f.getAno(), f.getVolume(), f.getNumero(), f.getEdicao(), f.getDiaMes(), f.getId(), fTemp.getFasciculoDeQuemSouSuplemento().getId());
				
			}
			
			
			if(qtdFasciculo > 0 ){
				throw new NegocioException(" J� existe um fasc�culo com os mesmos ano cronol�gico, ano, volume, n�mero e edi��o para essa assinatura.");
			}
			
		}finally{

			if(daoFasciculo!= null) daoFasciculo.close();

			checkValidation(lista);
			
		}
			
	}
	
	
}
