/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/03/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.biblioteca.CampoRemotoDTO;
import br.ufrn.integracao.dto.biblioteca.ExemplarResumidoDTO;
import br.ufrn.integracao.dto.biblioteca.TituloResumidoDTO;
import br.ufrn.integracao.interfaces.BibliotecaCatalogacaoRemoteService;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;

/**
 *
 *     Implementa os métodos remotos que o SIPAC vai chamar no SIGAA.
 *
 * @author jadson
 * @since 04/03/2009
 * @version 1.0 criacao da classe
 *
 */
@WebService
@Component("bibliotecaCatalogacaoRemoteService")
@Scope("prototype")
public class BibliotecaCatalogacaoRemoteServiceImpl implements BibliotecaCatalogacaoRemoteService {

	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.br.ufrn.comum.remoto.biblioteca.CatalogacaoController#buscaMultiCampoTitulo(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	public List <TituloResumidoDTO> buscaMultiCampoTituloRemota(String titulo, String assunto, String autor, 
			String localPublicacao, String editora, Integer anoInicial, Integer anoFinal) {

		TituloCatalograficoDao daoTitulo = null;

		// o que volta do DAO
		List<CacheEntidadesMarc> titulos = new ArrayList<CacheEntidadesMarc>();

		// o que eh retornado para o sistema remoto
		List<TituloResumidoDTO> titulosDTO = new ArrayList<TituloResumidoDTO>();

		GeraPesquisaTextual geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
		
		try {

			daoTitulo = DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class); 

			titulos = daoTitulo.buscaMultiCampo(geradorPesquisa, CampoOrdenacaoConsultaAcervo.TITULO, titulo, assunto, autor, localPublicacao
					, editora, null, null, null, anoInicial, anoFinal, -1, -1, -1, -1, false, null, false, false);

			// faz a conversão do retorno
			for (CacheEntidadesMarc tituloCache : titulos) {

				titulosDTO.add(new TituloResumidoDTO(tituloCache.getIdTituloCatalografico(), tituloCache.getNumeroDoSistema()
						, tituloCache.getTitulo(), tituloCache.getAutor(), tituloCache.getAno(), tituloCache.getEdicao()
						, tituloCache.getNumeroChamada(), tituloCache.getQuantidadeMateriaisAtivosTitulo()) );
			}


			return titulosDTO;

		} catch (DAOException e){
			e.printStackTrace();
			throw new RemoteAccessException(e.getMessage()); // Encapsula a exceção para o sistema remoto
		} finally {
			if (daoTitulo != null)
				daoTitulo.close();
		}

	}

	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.br.ufrn.comum.remoto.biblioteca.CatalogacaoController#buscaExemplaresPorTitulo(int)
	 */
	public List<ExemplarResumidoDTO> buscaExemplaresPorTitulo(int idTitulo) {

		List<ExemplarResumidoDTO> retorno = new ArrayList<ExemplarResumidoDTO>();

		GenericDAO dao = null;

		try{	

			dao = DAOFactory.getGeneric(Sistema.SIGAA);

			TituloCatalografico titulo  = dao.refresh(new TituloCatalografico(idTitulo));


			// Testa se o titulo tem exemplares

			List<Exemplar> exemplares =  titulo.getExemplares();

			if(exemplares != null && exemplares.size() > 0){

				for (Exemplar exemplar : exemplares) {

					ExemplarResumidoDTO exemplarResumido = new ExemplarResumidoDTO();
					exemplarResumido.codigoBarras = exemplar.getCodigoBarras();
					exemplarResumido.descricaoBiblioteca = exemplar.getBiblioteca().getDescricao();
					exemplarResumido.localizacao = exemplar.getNumeroChamada();

					retorno.add(exemplarResumido);

				}

				// Se não tiver procura se o item tem uma assinatura de periódicos	
			}else{

				List<Assinatura> assinaturas = titulo.getAssinaturas();

				if(assinaturas != null){

					for (Assinatura assi : assinaturas) {
						
						for (Fasciculo fasciculo : assi.getFasciculos()) {
							ExemplarResumidoDTO exemplarResumido = new ExemplarResumidoDTO();
							exemplarResumido.codigoBarras = fasciculo.getCodigoBarras();
							exemplarResumido.descricaoBiblioteca = fasciculo.getBiblioteca().getDescricao();
							retorno.add(exemplarResumido);
						}
					}
				}

			}

		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		}finally{
			if(dao != null) dao.close();
		}

		return retorno;
	}

	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.br.ufrn.comum.remoto.biblioteca.CatalogacaoController#mostaInformacoesCompletasTitulo(int)
	 */
	public List<CampoRemotoDTO> mostaInformacoesCompletasTitulo(int idTitulo) {


		GenericDAO dao = null;

		List<CampoRemotoDTO> retorno = new ArrayList<CampoRemotoDTO>();

		try{	

			dao = DAOFactory.getGeneric(Sistema.SIGAA);

			TituloCatalografico titulo  = dao.refresh(new TituloCatalografico(idTitulo));

			retorno.add(new CampoRemotoDTO("Nº do Sistema", ""+titulo.getNumeroDoSistema()));
			retorno.add(new CampoRemotoDTO("FMT", titulo.getFormatoMaterial().getDescricaoCompleta()));


			for (CampoControle controle : titulo.getCamposControleOrdenadosByEtiqueta()) {
				retorno.add(new CampoRemotoDTO(controle.getEtiqueta().getTag(), controle.getDadoParaExibicao()));
			}

			for (CampoDados dados : titulo.getCamposDadosOrdenadosByEtiqueta()) {

				CampoRemotoDTO campo = new CampoRemotoDTO(dados.getEtiqueta().getTag() 
						+ ""+dados.getIndicador1()  + ""+dados.getIndicador2()   ) ;

				for (SubCampo sub : dados.getSubCampos()) {

					campo.addDado(" $"+sub.getCodigo()+" "+sub.getDado());
				}

				retorno.add(campo);

			}


		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally {
			if(dao != null) dao.close();
		}	

		return retorno;
	}


}
