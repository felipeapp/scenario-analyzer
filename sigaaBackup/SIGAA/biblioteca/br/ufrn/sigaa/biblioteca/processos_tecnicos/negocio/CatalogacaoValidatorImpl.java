/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on DD/MM/YYYY
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FormatoMaterialEtiquetaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoVariavel;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CategoriaMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorCampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorSubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterialEtiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorDescritorCampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorDescritorSubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorIndicador;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 * 
 *       Classe que realiza as valida��es do padr�o MARC utilizadas na UFRN, tanto para t�tulos quanto para autoridades.
 *       
 *       Pode ser chamada para validar o t�tulo/autoriade inteiro, ou apenas um campo MARC espec�fico do t�tulo/autoridade.
 * 
 * @author Victor Hugo
 * @jadson
 */
public class CatalogacaoValidatorImpl implements CatalogacaoValidator{

	
	
	
	
	/**
	 *     Verifica os campos obrigat�rios do T�tulo. LDR, 008, 080, 090, 245.
	 *     
	 *     Deve ser chamado apenas se o t�tulo estiver sendo finalizado ou um t�tulo j� catalogado
	 *  sendo atualizado.
	 *
	 * @param tituloCatalogafico
	 * @param listaErros
	 * @return
	 * @throws DAOException
	 */
	public  ListaMensagens verificaCamposObrigatoriosTitulo(TituloCatalografico tituloCatalogafico, List<ClassificacaoBibliografica> classificacoesUtilizada, ListaMensagens listaErros) throws DAOException {
		
		boolean cLiderPresente = false; // os campo lider deve est� presentes
		boolean c008Presente = false;   // os campo   008 deve est� presentes
		
		boolean c090Presente = false;   // o n�mero de chamada deve est� presente
		boolean c245Presente = false;   // o t�tulo deve est� preenchido
		
		// pelo menos 1 das classifica��es deve ser preenchida //
		boolean cClassifcacao1Presente = false;
		boolean cClassifcacao2Presente = false;
		boolean cClassifcacao3Presente = false;
		
		// Percorre os campos de controle
		if(tituloCatalogafico.getCamposControle() != null){
			for (CampoControle c : tituloCatalogafico.getCamposControle()) {
				if(c.getEtiqueta().equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO))
					cLiderPresente = true;
				if(c.getEtiqueta().equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO))
					c008Presente = true;
			}
		}
		
		
		// Percorre os campos de dados de classifica��es bibliogr�ficas
		
		for (ClassificacaoBibliografica classificacaoUtilizada : classificacoesUtilizada) {
			if(classificacaoUtilizada.isPrimeiraClassificacao() ){
				cClassifcacao1Presente = ClassificacoesBibliograficasUtil.verificaSeCampoCorrepondendeClassificacaoPreenchido(tituloCatalogafico, classificacaoUtilizada);
				if(cClassifcacao1Presente)
					validaDadosClassificacao1Preenchidos(tituloCatalogafico, classificacaoUtilizada, listaErros);
			}	
			else if(classificacaoUtilizada.isSegundaClassificacao() ){
				cClassifcacao2Presente = ClassificacoesBibliograficasUtil.verificaSeCampoCorrepondendeClassificacaoPreenchido(tituloCatalogafico, classificacaoUtilizada);
				if(cClassifcacao2Presente)
					validaDadosClassificacao2Preenchidos(tituloCatalogafico, classificacaoUtilizada, listaErros);
			}	
			else if(classificacaoUtilizada.isTerceiraClassificacao() ){
				cClassifcacao3Presente = ClassificacoesBibliograficasUtil.verificaSeCampoCorrepondendeClassificacaoPreenchido(tituloCatalogafico, classificacaoUtilizada);
				if(cClassifcacao3Presente)
					validaDadosClassificacao3Preenchidos(tituloCatalogafico, classificacaoUtilizada, listaErros);
			}
		}
		
		
		// Percorre os campos de dados
		if(tituloCatalogafico.getCamposDados() != null){
			
			for (CampoDados dados : tituloCatalogafico.getCamposDados()) {
				
				if(dados.getEtiqueta().equals(Etiqueta.NUMERO_CHAMADA)){
					if(dados.getSubCampos() != null)
						for (SubCampo sub : dados.getSubCampos()) { // se tem o sub campo 'a' (CDU) com dados ok
							if(StringUtils.notEmpty( sub.getDado())  && sub.getCodigo() != null && sub.getCodigo().equals(SubCampo.SUB_CAMPO_A)) 
								c090Presente = true;
						}
				}
					
				if(dados.getEtiqueta().equals(Etiqueta.TITULO)){
					if(dados.getSubCampos() != null)
						for (SubCampo sub : dados.getSubCampos()) { // se tem algum sub campo com dados ok
							if(StringUtils.notEmpty( sub.getDado()) && sub.getCodigo() != null && sub.getCodigo().equals(SubCampo.SUB_CAMPO_A)) 
								c245Presente = true;
						}
				}
					
			} 
		}
	
		if(! cLiderPresente ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Para finalizar a cataloga��o o T�tulo precisa possuir o campo L�der", TipoMensagemUFRN.ERROR));
		}
		
		if( ! c008Presente ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Para finalizar a cataloga��o o T�tulo precisa possuir o campo de controle 008", TipoMensagemUFRN.ERROR));
		}
		
		if( ! cClassifcacao1Presente && ! cClassifcacao2Presente && ! cClassifcacao3Presente ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Para finalizar a cataloga��o o T�tulo precisa possuir alguma Classifica��o Bibliogr�fica", TipoMensagemUFRN.ERROR));
		}
		
		if( ! c090Presente ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Para finalizar a cataloga��o o T�tulo precisa possuir o N�mero de Chamada", TipoMensagemUFRN.ERROR));
		}
		
		
		if( ! c245Presente){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Para finalizar a cataloga��o o T�tulo precisa possuir um t�tulo.", TipoMensagemUFRN.ERROR));
		}
		
		
		return listaErros;
	}
	
	
	
	/**
	 * Valida se os dados da classifica��o, classe principal e �reas CNPq foram calculados pelo sistema ou preenchidos pelo usu�rio para aprimeira classifica��o utilizada no sistema.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 */
	private void validaDadosClassificacao1Preenchidos(TituloCatalografico titulo, ClassificacaoBibliografica classificacoesUtilizada, ListaMensagens listaErros) {
		
		if( StringUtils.isEmpty( titulo.getClassificacao1() ) ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe a classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}
		
		if( StringUtils.isEmpty( titulo.getClassePrincipalClassificacao1()) ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe a classe principal da classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}else{
			if( ! ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao1().contains(titulo.getClassePrincipalClassificacao1())){
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("A classe principal '"+titulo.getClassePrincipalClassificacao1()+"' � inv�lida para a classifica��o "+classificacoesUtilizada.getDescricao()+".", TipoMensagemUFRN.ERROR));
			}
		}
		
		if( titulo.getAreaConhecimentoCNPQClassificacao1() == null || titulo.getAreaConhecimentoCNPQClassificacao1().getId() == -1 ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe a �reas do CNPq correspondente a classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}
	}


	/**
	 * Valida se os dados da classifica��o, classe principal e �reas CNPq foram calculados pelo sistema ou preenchidos pelo usu�rio para aprimeira classifica��o utilizada no sistema.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 */
	private void validaDadosClassificacao2Preenchidos(TituloCatalografico titulo, ClassificacaoBibliografica classificacoesUtilizada, ListaMensagens listaErros) {
		
		if( StringUtils.isEmpty( titulo.getClassificacao2() ) ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe a classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}
		
		if( StringUtils.isEmpty( titulo.getClassePrincipalClassificacao2()) ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe a classe principal da classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}else{
			if( ! ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao2().contains(titulo.getClassePrincipalClassificacao2())){
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("A classe principal '"+titulo.getClassePrincipalClassificacao2()+"' � inv�lida para a classifica��o "+classificacoesUtilizada.getDescricao()+".", TipoMensagemUFRN.ERROR));
			}
		}
		
		if( titulo.getAreaConhecimentoCNPQClassificacao2() == null || titulo.getAreaConhecimentoCNPQClassificacao2().getId() == -1 ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe a �reas do CNPq correspondente a classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}
	}
	
	
	
	/**
	 * Valida se os dados da classifica��o, classe principal e �reas CNPq foram calculados pelo sistema ou preenchidos pelo usu�rio para aprimeira classifica��o utilizada no sistema.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 */
	private void validaDadosClassificacao3Preenchidos(TituloCatalografico titulo, ClassificacaoBibliografica classificacoesUtilizada, ListaMensagens listaErros) {
		
		if( StringUtils.isEmpty( titulo.getClassificacao3() ) ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe os dados da classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}
		
		if( StringUtils.isEmpty( titulo.getClassePrincipalClassificacao3()) ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe os dados da classe principal da classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}else{
			if( ! ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao3().contains(titulo.getClassePrincipalClassificacao3())){
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("A classe principal '"+titulo.getClassePrincipalClassificacao3()+"' � inv�lida para a classifica��o "+classificacoesUtilizada.getDescricao()+".", TipoMensagemUFRN.ERROR));
			}
		}
		
		if( titulo.getAreaConhecimentoCNPQClassificacao3() == null || titulo.getAreaConhecimentoCNPQClassificacao3().getId() == -1 ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Por favor informe a �reas do CNPq correspondente a classifica��o "+classificacoesUtilizada.getDescricao()+" .", TipoMensagemUFRN.ERROR));
		}
		
	}
	
	

	/**
	 *     Para ser finalizada uma autoridade deve ter uma entrada autorizada e uma remissiva.
	 *     Al�m dos campo de controle L�der e 008
	 *
	 * @param tituloCatalogafico
	 * @param listaErros
	 * @return
	 * @throws DAOException
	 */
	public  ListaMensagens verificaCamposObrigatoriosAutoridade(Autoridade autoridade, 
						ListaMensagens listaErros) throws DAOException {
		
		boolean cLiderPresente = false;
		boolean c008Presente = false;
		boolean entraAutorizadaPresente = false;
		//boolean entradaRemissivaPresente = false;
		
		if(autoridade.getCamposControle() != null){
			for (CampoControle c : autoridade.getCamposControle()) {
				if(c.getEtiqueta().equals(Etiqueta.CAMPO_LIDER_AUTORIDADE))
					cLiderPresente = true;
				if(c.getEtiqueta().equals(Etiqueta.CAMPO_008_AUTORIDADE))
					c008Presente = true;
			}
		}
		
		if(autoridade.getCamposDados() != null)
		for (CampoDados d : autoridade.getCamposDados()) {
			
			if(d.getEtiqueta().equals(Etiqueta.NOME_PESSOAL)){
				
				if(d.getSubCampos() != null)
				for (SubCampo s : d.getSubCampos()){
					
					if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						entraAutorizadaPresente = true;
						continue;
					}
				}
				
			}
			
			if(d.getEtiqueta().equals(Etiqueta.NOME_CORPORATIVO)){
				
				if(d.getSubCampos() != null)
					for (SubCampo s : d.getSubCampos()){
						
						if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
							entraAutorizadaPresente = true;
							continue;
						}
					}
			}
			
			if(d.getEtiqueta().equals(Etiqueta.NOME_EVENTO)){
				
				if(d.getSubCampos() != null)
					for (SubCampo s : d.getSubCampos()){
						
						if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
							entraAutorizadaPresente = true;
							continue;
						}
					}
			}

			if(d.getEtiqueta().equals(Etiqueta.CABECALHO_TOPICOS)){
				
				if(d.getSubCampos() != null)
					for (SubCampo s : d.getSubCampos()){
						
						if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
							entraAutorizadaPresente = true;
							continue;
						}
					}
			}
			
			if(d.getEtiqueta().equals(Etiqueta.CABECALHO_NOME_GEOGRAFICO)){
				
				if(d.getSubCampos() != null)
					for (SubCampo s : d.getSubCampos()){
						
						if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
							entraAutorizadaPresente = true;
							continue;
						}
					}
			}
			
			if(d.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO)){
				
				if(d.getSubCampos() != null)
					for (SubCampo s : d.getSubCampos()){
						
						if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_X)){
							entraAutorizadaPresente = true;
							continue;
						}
					}
			}
			
		}
	
		if(! cLiderPresente || ! c008Presente || ! entraAutorizadaPresente /* || ! entradaRemissivaPresente */ ){
			adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Para finalizar a cataloga��o a Autoridade precisa possuir pelo menos os campos: LDR, 008 e uma entra autorizada (100$a ou 110$a ou 111$a ou 150$a ou 151$a ou 180$x) ", TipoMensagemUFRN.ERROR));
		}
		
		return listaErros;
	}
	
	
	
	
	/**
	 *     <p>Verifica se j� existem outro T�tulo Catalogr�fico na base com o mesmos valores para
	 *  os campos "t�tulo", "autor", "ano" e "edi��o".</p>
	 *  
	 *     <p>Lembrando que todo esses campos s�o opcionais, ent�o se um campo n�o existir compara
	 *   somente os outros. Exemplo: Se s� tiver o t�tulo, compara apensa se existe outro T�tulo como o 
	 *   mesmo t�tulo, se tiver t�tulo e autor, compara se existe outro T�tulo como mesmo t�tulo e autor,
	 *   e assim por diante...</p>
	 *   
	 *     OBS.: Esse aqui � uma valida��o adotada no sistema de bibliotecas da UFRN, n�o � valida��o MARC.
	 *   
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public ListaMensagens verificaExisteTituloIgual(TituloCatalografico tituloCatalogafico, ListaMensagens listaErros) throws DAOException {
		
		TituloCatalograficoDao dao = null;

		try {
			dao = DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
			
			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>(); 
			cache = CatalogacaoUtil.sincronizaTituloCatalograficoCache(tituloCatalogafico, cache, classificacoesUtilizadas);
			
			int quantidadeTituloCatalogados = dao.countTitulosDuplicados(cache.getTitulo(), cache.getSubTitulo(), cache.getAutor(), cache.getAutoresSecundarios(), cache.getAno(), cache.getEdicao(), cache.getEditora(), tituloCatalogafico.getNumeroDoSistema());
			
			if(quantidadeTituloCatalogados >=1){
				
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("J� existe(m) "+quantidadeTituloCatalogados+" T�tulo(s) " +
						"como os mesmos "+"t�tulo, autor, autores secund�rios, ano, edi��o e editora no sistema.", TipoMensagemUFRN.ERROR));
				
			}
		} finally {
			if (dao != null) dao.close();
		}

		return listaErros;
		
	}
	
	
	/** 
	 *    Verifica a exist�ncia de outra autoridade com e mesmo nome pessoal. N�o pode deixar a inser��o
	 * de autoridades duplicadas.
	 *
	 * @param autoridade
	 * @param listaErros
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public  ListaMensagens verificaExiteAutoridadeIgual(Autoridade autoridade, ListaMensagens listaErros) throws DAOException {

		String autorAutorizado = null;
		String assuntoAutorizado = null;
		
		if(autoridade.getCamposDados() != null)	
		for (CampoDados dados : autoridade.getCamposDados()) {
			
			// Entra em apenas 1 se desses porque uma autoridade n�o pode possuir mais de uma entrada autorizada
			
			// 100 110  e 111 (todos os campos)
			if(dados.getEtiqueta().equals(Etiqueta.NOME_PESSOAL) 
					|| dados.getEtiqueta().equals(Etiqueta.NOME_CORPORATIVO)
					|| dados.getEtiqueta().equals(Etiqueta.NOME_EVENTO)){
				
				for (SubCampo sub : dados.getSubCampos()) {
					
					if(autorAutorizado == null)
						autorAutorizado = sub.getDado();
					else
						autorAutorizado = autorAutorizado +" "+sub.getDado();
				}	
			}
			
			// 150 151 (todos os campos)
			if(dados.getEtiqueta().equals(Etiqueta.CABECALHO_TOPICOS) 
					|| dados.getEtiqueta().equals(Etiqueta.CABECALHO_NOME_GEOGRAFICO)
					|| dados.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO)){
				
				for (SubCampo sub : dados.getSubCampos()) {
					
					if(assuntoAutorizado == null)
						assuntoAutorizado = sub.getDado();
					else
						assuntoAutorizado = assuntoAutorizado +" "+sub.getDado();
						
				}	
			}
			
			// 180$x
			if(dados.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO)){
				for (SubCampo sub : dados.getSubCampos()) {
									
					if(sub.getCodigo().equals(SubCampo.SUB_CAMPO_X)){
						assuntoAutorizado = sub.getDado();
					}	
				}	
			}
			
		}
		

		AutoridadeDao dao = null;

		try {
			dao = DAOFactory.getInstance().getDAO(AutoridadeDao.class);

			int quantidadeAutoridades = 0;
			
			if( StringUtils.notEmpty(autorAutorizado)){
				quantidadeAutoridades = dao.countAutoridadesAutorDuplicadas(autorAutorizado, autoridade.getNumeroDoSistema());
			}else{
				if( StringUtils.notEmpty(assuntoAutorizado)){
					quantidadeAutoridades = dao.countAutoridadesAssuntoDuplicadas(assuntoAutorizado, autoridade.getNumeroDoSistema());
				}
			}
			
			if (quantidadeAutoridades >= 1) {
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("J� existe(m) "+quantidadeAutoridades
										+ " autoridade(s) cadastradas no sistema como a mesma entrada autorizada", TipoMensagemUFRN.ERROR));
			}
			
		} finally {
			if (dao != null) dao.close();
		}

		return listaErros;

	}
	
	
	
	/////////////////////////  PARTE QUE VALIDA APENAS UM CAMPO MARC //////////////////////////
	
	
	
	
	/**
	 *      M�todo que valida apenas 1 campo MARC. Usado nas telas de edi��o de campos de controle.
	 *
	 * @param campoVariavel
	 * @param formatoMaterial
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public ListaMensagens validaCampoMarcBibliografico(CampoVariavel campoVariavel, FormatoMaterial formatoMaterial) throws NegocioException, DAOException{

		ListaMensagens listaErros = new ListaMensagens();
		
		GenericDAO dao = null;
		
		try {
		
			dao =  DAOFactory.getGeneric(Sistema.SIGAA);
			
			List<CampoVariavel> camposVariaveis = new ArrayList<CampoVariavel>();
			camposVariaveis.add(campoVariavel);
			
			if(campoVariavel instanceof CampoControle)
				validaCamposMarcBibliografico(dao, camposVariaveis, formatoMaterial, listaErros, false);
			else
				validaCamposMarcBibliografico(dao, camposVariaveis, formatoMaterial, listaErros, true);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return listaErros;
	}

	
	
	
	/**
	 *      M�todo que valida apenas 1 campo MARC de autoridade. Usado nas telas de edi��o de 
	 *  campos de controle de autoridades.
	 *
	 * @param campoVariavel
	 * @param formatoMaterial
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public ListaMensagens validaCampoMarcAutoridade(CampoVariavel campoVariavel) 
															throws NegocioException, DAOException{

		ListaMensagens listaErros = new ListaMensagens();
		
		GenericDAO dao = null;
		
		try {
		
			dao =  DAOFactory.getGeneric(Sistema.SIGAA);
			
			List<CampoVariavel> camposVariaveis = new ArrayList<CampoVariavel>();
			camposVariaveis.add(campoVariavel);
			validaCamposMarcAutoridade(dao, camposVariaveis, listaErros);
		
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return listaErros;
	}
	
	
	
	//////  PARTE QUE VALIDA TODOS OS CAMPOS MARC DE UMA ENTIDADE (T�TULO, AUTORIDADE, ETC...) //////////
	
	
	
	/**
	 * 
	 *  M�todo que valida as informa��es MARC de todos os campos do t�tulo passado.
	 *
	 * @param titulo T�tulo que vai ser validado
	 * @param formatoMaterial Campos de controle precisam do tipo de material par 
	 *                 serem validados. ex.: 006 e 008
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public  ListaMensagens validaCamposMarcTitulo(TituloCatalografico titulo, FormatoMaterial formatoMaterial, ListaMensagens listaErros) throws DAOException, NegocioException{
		
		long tempo  = System.currentTimeMillis();
		
		GenericDAO dao = null;

		try {
			
			dao =  DAOFactory.getGeneric(Sistema.SIGAA);

			// Valida todos os campos de controle
			if(titulo.getCamposControle() != null){
				List<CampoVariavel> lista = new ArrayList<CampoVariavel>();
				lista.addAll(titulo.getCamposControle());
				validaCamposMarcBibliografico(dao, lista, formatoMaterial, listaErros, false);
			}
			
			// Um t�tulo pode possuir precisa possuir 1 e somente autor por vez. campos: 100, 110, 111
			
			boolean possuiAutor = false;
			boolean possuiMaisDeUmAutor = false;
			
			if(titulo.getCamposDados() != null)	
			for (CampoDados d : titulo.getCamposDados()) {
			
				if(d.getEtiqueta().equals(Etiqueta.AUTOR)){
					
					if(! possuiAutor ){
						possuiAutor = true;
					}else{
						possuiMaisDeUmAutor = true;
					}
				}
				
				if(d.getEtiqueta().equals(Etiqueta.AUTOR_COOPORATIVO)){
					
					if(! possuiAutor ){
						possuiAutor = true;
					}else{
						possuiMaisDeUmAutor = true;
					}
				}
				
				if(d.getEtiqueta().equals(Etiqueta.AUTOR_EVENTO)){
					
					if(! possuiAutor ){
						possuiAutor = true;
					}else{
						possuiMaisDeUmAutor = true;
					}
				}
			}
			
			if(possuiMaisDeUmAutor){
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Um t�tulo n�o pode possuir mais de uma entra principal, deve possuir no m�ximo um dos seguintes campos: 100$a, 110$a ou 111$a", TipoMensagemUFRN.ERROR));
				return listaErros;
			}	
			

			// Valida todos os campo de dados
			if(titulo.getCamposDados() != null)	{
				List<CampoVariavel> lista2 = new ArrayList<CampoVariavel>();
				lista2.addAll(titulo.getCamposDados());
				validaCamposMarcBibliografico(dao, lista2, formatoMaterial, listaErros, true);
			}

		} finally {
			if (dao != null){
				dao.close();
			}
		}

		System.out.println("Valida��o T�tulo demorou >>>>> "+((System.currentTimeMillis()-tempo)/1000)+" ms.");
		
		return listaErros;
	}

	
	
	/**
	 * 
	 *   M�todo que valida as informa��es MARC de todos os campos da autoridade passada.
	 *
	 * @param titulo t�tulo que vai ser validado
	 * @param formatoMaterial campos de controle precisam do tipo de material par 
	 *                 serem validados 006 e 008
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public ListaMensagens validaCamposMarcAutoridade(Autoridade autoridade, ListaMensagens listaErros) throws DAOException, NegocioException{
		
		long tempo  = System.currentTimeMillis();
		
		GenericDAO dao = null;

		try {
			dao =  DAOFactory.getGeneric(Sistema.SIGAA);
			
			// Valida todos os campos de controle
			if(autoridade.getCamposControle() != null){
				List<CampoVariavel> lista = new ArrayList<CampoVariavel>();
				lista.addAll(autoridade.getCamposControle());
				validaCamposMarcAutoridade(dao, lista, listaErros);
			}
			

//			// Valida todos os campo de dados -> Foi para o processador, porque precisa ser depois de remover os campos vazios
//			if(autoridade.getCamposDados() == null || autoridade.getCamposDados().size() == 0){
//				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Para ser salva uma autoridade precisa possuir pelo menos um campo de dados", TipoMensagemUFRN.ERROR));
//				return listaErros;
//			}else{
				
			// Uma autoridade precisa possuir 1 e somente um dos campos de entrada autorizada
			//  100a, 110a, 111a, 150a, 151a, 180x
			
			boolean possuiCampoAutorizado = false;
			boolean possuiMaisDeUmCampoAutorizado = false;
			
			if(autoridade.getCamposDados() != null)
			for (CampoDados d : autoridade.getCamposDados()) {
			
				if(d.getEtiqueta().equals(Etiqueta.NOME_PESSOAL)){
					
					if(d.getSubCampos() != null)
					for (SubCampo s : d.getSubCampos()){
						
						if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
							if(! possuiCampoAutorizado ){
								possuiCampoAutorizado = true;
							}else{
								possuiMaisDeUmCampoAutorizado = true;
							}
						}
					}
					
				}
				
				if(d.getEtiqueta().equals(Etiqueta.NOME_CORPORATIVO)){
					
					if(d.getSubCampos() != null)
						for (SubCampo s : d.getSubCampos()){
							
							if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
								if(! possuiCampoAutorizado ){
									possuiCampoAutorizado = true;
								}else{
									possuiMaisDeUmCampoAutorizado = true;
								}
							}
						}
				}
				
				if(d.getEtiqueta().equals(Etiqueta.NOME_EVENTO)){
					
					if(d.getSubCampos() != null)
						for (SubCampo s : d.getSubCampos()){
							
							if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
								if(! possuiCampoAutorizado ){
									possuiCampoAutorizado = true;
								}else{
									possuiMaisDeUmCampoAutorizado = true;
								}
							}
						}
				}

				if(d.getEtiqueta().equals(Etiqueta.CABECALHO_TOPICOS)){
					
					if(d.getSubCampos() != null)
						for (SubCampo s : d.getSubCampos()){
							
							if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
								if(! possuiCampoAutorizado ){
									possuiCampoAutorizado = true;
								}else{
									possuiMaisDeUmCampoAutorizado = true;
								}
							}
						}
				}
				
				if(d.getEtiqueta().equals(Etiqueta.CABECALHO_NOME_GEOGRAFICO)){
					
					if(d.getSubCampos() != null)
						for (SubCampo s : d.getSubCampos()){
							
							if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
								if(! possuiCampoAutorizado ){
									possuiCampoAutorizado = true;
								}else{
									possuiMaisDeUmCampoAutorizado = true;
								}
							}
						}
				}
				
				if(d.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO)){
					
					if(d.getSubCampos() != null)
						for (SubCampo s : d.getSubCampos()){
							
							if(s.getCodigo() != null && s.getCodigo().equals(SubCampo.SUB_CAMPO_X)){
								if(! possuiCampoAutorizado ){
									possuiCampoAutorizado = true;
								}else{
									possuiMaisDeUmCampoAutorizado = true;
								}
							}
						}
				}
				
				
			}
				
			if(possuiMaisDeUmCampoAutorizado){
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Uma autoridade n�o pode conter mais de uma entrada autorizada, deve possuir no m�ximo um dos campos: 100$a, 110$a, 111$a, 150$a, 151$a ou 180$x.", TipoMensagemUFRN.ERROR));
				return listaErros;
			}	
				
//				if(! possuiCampoAutorizado){
//					adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso("Uma autoridade deve conter pelo menos uma entrada autorizada. Campos de entrada autorizada: 100$a, 110$a, 111$a, 150$a, 151$a ou 180$x.", TipoMensagemUFRN.ERROR));
//					return listaErros;
//				}
				
			
			if(autoridade.getCamposDados() != null)	{
				List<CampoVariavel> lista2 = new ArrayList<CampoVariavel>();
				lista2.addAll(autoridade.getCamposDados());
				
				///////// VALIDA��O DOS CAMPOS DE DADOS DE AUTORIDADES ///////////
				validaCamposMarcAutoridade(dao, lista2, listaErros);
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}

		System.out.println("Valida��o Autoridade demorou >>>>> "+((System.currentTimeMillis()-tempo)/1000)+" ms.");
		
		return listaErros;
	}
	
	///////////////////////  FIM DA PARTE P�BLICA DA VALIDA��O ///////////////////////////////////////	
	
	/**
	 * 
	 * M�todo que cont�m as regras para validar os campos do padr�o MARC
	 *            
	 * @param camposVariaveisos campos para serem validados
	 * @param tipoMaterial
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private static void validaCamposMarcBibliografico(GenericDAO dao, List<CampoVariavel> camposVariaveis, 
			FormatoMaterial formatoMaterial, ListaMensagens listaErros, boolean validacaoCampoDados) throws NegocioException, DAOException{

		
		// Usado para contar as etiqueta e saber se foi repetida ou n�o
		List<String> listaEtiquetas = new ArrayList<String>(); 
		
		if(camposVariaveis != null){
		
			/*      Para TODOS as campos passados, valide-os    */
			externo:
			for (CampoVariavel campo : camposVariaveis) {
				
				if( campo.possuiEtiquetaValida()){ 
					
					// buscou a etiqueta, mas por algum motivo n�o foi adicionada corretamente ao campo,
					// isso pode acontecer quando se digita 'tab' muito r�pido antes do resultado da pesquisa voltar.
					if(campo.getEtiqueta().getId() <= 0){ 
						adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo "+campo.getEtiqueta().getTag()+" n�o foi encontrado, por favor busque-o novamente.", TipoMensagemUFRN.ERROR));
						continue externo;
					}
					
					if(! campo.getEtiqueta().isAtiva() && campo.getEtiqueta().isEquetaLocal()){
						
						adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo local "+campo.getEtiqueta().getTag()+" foi removido do sistema e n�o pode mais ser usado na cataloga��o.", TipoMensagemUFRN.ERROR));
					
					}else{
						
						if(campo.getEtiqueta().getTipo() != TipoCatalogacao.BIBLIOGRAFICA && campo.getEtiqueta().isEquetaLocal()){
							adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo local "+campo.getEtiqueta().getTag()+" foi alterado para campo de autoridades e n�o pode mais fazer parte de um T�tulo .", TipoMensagemUFRN.ERROR));
						}else{
							
							if(! listaEtiquetas.contains(campo.getEtiqueta().getTag())){ // primeira ocorr�ncia da etiqueta
								listaEtiquetas.add(campo.getEtiqueta().getTag());
								
							}else{  // se j� cont�m a etiqueta
								
								if( ! campo.getEtiqueta().isRepetivel()){ // e ela n�o pode ser repetida
									
									adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo "+campo.getEtiqueta().getTag()+" n�o pode ser repetido. ", TipoMensagemUFRN.ERROR));
								}
								
							}
							
							/* Separa em dois m�todos porque as regras de valida��o s�o diferentes 
							 * dependendo do tipo de campo */
			
							if(campo.isCampoControle()){
								validaCampoControleBibliografico(dao,  (CampoControle) campo, formatoMaterial, listaErros);
							}else{
								validaCampoDados(dao,   (CampoDados) campo, listaErros);
							}
			
				
						}// ELSE etiqueta do tipo certo.
						
					} // ELSE etiqueta local est� ativa
					
				} // IF  campo.possuiEtiquetaValida()
		
			} // for
		}
		
	
	}


	/** 
	 *    M�todo que cont�m as regras para validar os campos do padr�o MARC de autoridades. 
	 *    A diferen�a para o campos MARC da base bibliogr�fica � que todos os campos de controle
	 * s�o <strong>independentes</strong> do formato do material.
	 *            
	 * @param camposVariaveisos campos para serem validados
	 * @param tipoMaterial
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private static void validaCamposMarcAutoridade(GenericDAO dao, List<CampoVariavel> camposVariaveis, ListaMensagens listaErros) 
				throws NegocioException, DAOException{

		if(camposVariaveis == null || camposVariaveis.size() == 0){
			throw new NegocioException("N�o existem campos do padr�o MARC para serem validados");
		}
		
		// Usado para contar as etiqueta e saber se foi repetida ou n�o
		List<Integer> listaEtiquetas = new ArrayList<Integer>(); 
		
		/*      Para TODOS as campos passados, valide-os    */
		externo:
		for (CampoVariavel campo : camposVariaveis) {
			// o campo tem que vim com uma etiqueta para poder ser validado

			if( campo.possuiEtiquetaValida()){

				// buscou a etiqueta, mas por algum motivo n�o foi adicionada corretamente ao campo,
				// isso pode acontecer quando se digita 'tab' muito r�pido antes do resultado da pesquisa voltar.
				if(campo.getEtiqueta().getId() <= 0){ 
					adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo "+campo.getEtiqueta().getTag()+" n�o foi encontrado, por favor busque-o novamente.", TipoMensagemUFRN.ERROR));
					continue externo;
				}
				
				if(! campo.getEtiqueta().isAtiva() && campo.getEtiqueta().isEquetaLocal()){
					adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo local "+campo.getEtiqueta().getTag()+" foi removido do sistema e n�o pode mais ser usado na cataloga��o.", TipoMensagemUFRN.ERROR));
				}else{
				
					if(campo.getEtiqueta().getTipo() != TipoCatalogacao.AUTORIDADE && campo.getEtiqueta().isEquetaLocal()){
						adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo local "+campo.getEtiqueta().getTag()+" foi alterado para campo bibliogr�fico e n�o pode mais fazer parte de uma Autoridade .", TipoMensagemUFRN.ERROR));
					}else{
					
						if(! listaEtiquetas.contains(campo.getEtiqueta().getId())){ // primeira ocorr�ncia da etiqueta
							listaEtiquetas.add(campo.getEtiqueta().getId());
							
						}else{  // Se j� cont�m a etiqueta
							
							if( ! campo.getEtiqueta().isRepetivel()){ // e ela n�o pode ser repetida
								
								adicionaMensagemErroCatalogacao(listaErros,  new MensagemAviso(" O campo "+campo.getEtiqueta().getTag()+" n�o pode ser repetido. ", TipoMensagemUFRN.ERROR));
							}
							
						}
						
						/* Separa em dois m�todos porque as regras de valida��o s�o diferentes 
						 * dependendo do tipo de campo */
		
						if(campo.isCampoControle()){
							
							// SO LIDER e 008 tem valida��es.
							if(campo.getEtiqueta().equals(Etiqueta.CAMPO_LIDER_AUTORIDADE)) {
								CampoControle c = (CampoControle) campo;
								
								if( c.getDado().length() != CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE.length() )
									adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso( "O campo: '"+c.getEtiqueta().getTag()+"' deve conter "
											+CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE.length()+" posi��es", TipoMensagemUFRN.ERROR));
								else
									validaCamposControleIndepentes(dao,   (CampoControle) campo, listaErros);
							
							}else{
								
								if( campo.getEtiqueta().equals(Etiqueta.CAMPO_008_AUTORIDADE)  ){
									CampoControle c = (CampoControle) campo;
									
									if( c.getDado().length() != CampoControle.DADOS_CAMPO_008_AUTORIDADE.length() )
										adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso( "O campo: '"+c.getEtiqueta().getTag()+"' deve conter "
											+CampoControle.DADOS_CAMPO_008_AUTORIDADE.length()+" posi��es", TipoMensagemUFRN.ERROR));
									else
										validaCamposControleIndepentes(dao,   (CampoControle) campo, listaErros);
								}
							}	
								
					
						}else{
							validaCampoDados(dao,   (CampoDados) campo, listaErros);
						}
					
					} //ELSE etiqueta possui tipo certo
					
				} // else etiqueta do campo est� ativa
				
			}// ELSE possui etiqueta valida
		}
	}


	/**
	 * M�todo especifico para validar os campos de controle
	 */
	private static void validaCampoControleBibliografico(GenericDAO dao, CampoControle c, 
			FormatoMaterial formatoMaterial, ListaMensagens listaErros) throws DAOException{	

		/*    Para validar um campo de controle pega os valores dos descritores 
		 * da etiqueta, se existirem, e compara com o que foi digitado pelo usu�rio para saber se
		 * est� entre os valores poss�veis */

		if(c.getEtiqueta().getTag().equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO.getTag()) ){
			
			if( c.getDado().length() != CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO.length() )
				adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso( "O campo: '"+c.getEtiqueta().getTag()+"' deve conter "
						+CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO.length()+" posi��es", TipoMensagemUFRN.ERROR));
			else
				validaCamposDependeFormatoMaterial(dao, c, formatoMaterial, listaErros);
		}else{

			if( c.getEtiqueta().getTag().equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTag()) ){
				
				if( c.getDado().length() != CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO.length() )
					adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso( "O campo: '"+c.getEtiqueta().getTag()+"' deve conter "
							+CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO.length()+" posi��es", TipoMensagemUFRN.ERROR));
				else
					validaCamposDependeFormatoMaterial(dao, c, formatoMaterial, listaErros);
				
			}else{
			
				if(c.getEtiqueta().getTag().equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO.getTag())){
					
					if( c.getDado().length() != CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO.length() )
						adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso( "O campo: '"+c.getEtiqueta().getTag()+"' deve conter "
								+CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO.length()+" posi��es", TipoMensagemUFRN.ERROR));
					else
						validaCamposDependenteCategoriaMateral(dao, c,  listaErros);
	
				}else{	
					if(c.getEtiqueta().getTag().equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag())){
						if( c.getDado().length() != CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.length() )
							adicionaMensagemErroCatalogacao(listaErros, new MensagemAviso( "O campo: '"+c.getEtiqueta().getTag()+"' deve conter "
									+CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.length()+" posi��es", TipoMensagemUFRN.ERROR));
						else
							validaCamposControleIndepentes(dao, c, listaErros);
					}else{
						// 003 e 005 at� agora n�o s�o validados
						
					}
				}
			}
		}
	}


	/**
	 * M�todo que implementa as regras para validar os campos 006 e 008
	 */
	private static void validaCamposDependeFormatoMaterial(GenericDAO dao, CampoControle c, 
			FormatoMaterial formatoMaterial, ListaMensagens erros)throws DAOException{

		// Primeiro busca os TipoMaterialCatalograficoEtiqueta para a etiqueta do campo e
		// o tipo de material escolhido. � nessa classe onde v�o est� os descritores para validar 
		// as informa��es. O resto do procedimento � igual a dos outros campos.

		FormatoMaterialEtiquetaDao daoFormato = null;

		try {
			
			if(formatoMaterial == null){
				adicionaMensagemErroCatalogacao(erros,  new MensagemAviso(" Formato do Material Inv�lido ", TipoMensagemUFRN.ERROR));
				return;
			}
			
			daoFormato = DAOFactory.getInstance().getDAO(FormatoMaterialEtiquetaDao.class, null, null);
				
			FormatoMaterialEtiqueta formato = daoFormato.buscaFormatoMaterialEtiqueta(c.getEtiqueta(), formatoMaterial);

			if(formato == null){
				adicionaMensagemErroCatalogacao(erros,  new MensagemAviso(" Formato do Material Inv�lido ", TipoMensagemUFRN.ERROR));
			}else{
			
				List <DescritorCampoControle> descritores = formato.getDescritoresCampoControle();
				
				// Daqui para frente � as regras s�o iguais para todos os campos de controle
				validaDescritoresCampoControle(c, descritores, erros);
			}
			
		} finally {
			if (daoFormato != null)
				daoFormato.close();
		}
	}


	
	/**
	 * M�todo que implementa as regras para validar apenas o campo 007 cujos descritores dependem
	 * do caracter que est� na posi��o 0 dos dados
	 */
	private static void validaCamposDependenteCategoriaMateral(GenericDAO dao, 
			CampoControle c, ListaMensagens listaErros)throws DAOException{

		// a categoria do material vem na posi��o 0.
		char categoriaMaterial = c.getDado().charAt(0);

		CategoriaMaterial categoria = dao.findByExactField(CategoriaMaterial.class, "codigo", categoriaMaterial, true);

		validaDescritoresCampoControle(c, categoria.getDescritoresCampoControle(), listaErros);
	
	}

	
	
	
	/**
	 * M�todo que implementa as regras para validar os campos de controle que possuem apenas um
	 * conjunto de descritores que n�o dependem de mais nada. � o caso do campo LIDER.
	 */
	private static void validaCamposControleIndepentes(GenericDAO dao, CampoControle c,
			ListaMensagens erros) throws DAOException {

		List<FormatoMaterialEtiqueta> formatos = (List<FormatoMaterialEtiqueta>) dao.findByExactField(FormatoMaterialEtiqueta.class, "etiqueta.id",  c.getEtiqueta().getId());

		// Daqui para frente � as regras s�o iguais para todos os campos de controle
        // O campo de controle LIDER so deveria ter um formato cadastrado
		
		validaDescritoresCampoControle(c, formatos.get(0).getDescritoresCampoControle(), erros);
	}

	
	
	
	/**
	 * A valida��o dos campos de controle s� � diferente at� se chegar nos descritores. A partir desse
	 * ponto � tudo igual para qualquer campo de controle, Ent�o esse m�todo guarda essa l�gica comum */
	private static void validaDescritoresCampoControle(CampoControle c,  List<DescritorCampoControle> descritores, 
			ListaMensagens erros){

		/* Para todos os descritores daquela etiqueta */
		for (DescritorCampoControle descritorCampoControle : descritores) {

			int posicaoI = descritorCampoControle.getPosicaoInicio();
			int posicaoF = descritorCampoControle.getPosicaoFim();

			/* Pega os poss�veis valores se existirem */
			List<ValorDescritorCampoControle> valores = descritorCampoControle.getValoresDescritorCampoControle();

			boolean valorValido = false;

			if(descritorCampoControle.possuiMultiploValores()){ // o valor precisa ser igual a parte do campo apenas

				valorValido = true;

				// Recebe os dados da posi��o que vai ser validada
				String dadosMultiplos = c.getDado().substring(posicaoI, posicaoF + 1); 

				// Para dados m�ltiplos o usu�rio n�o precisa preencher todas as posi��es
				// se o usu�rio n�o preencher vai ficar com espa�o em branco por causa da
				// constante CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO, ent�o � preciso retirar 
				// esses espa�os
				dadosMultiplos = dadosMultiplos.trim(); 
				
				// para cada caractere
				for (int pt = 0; pt < dadosMultiplos.length(); pt++) {

					boolean caracterValido = false;

					// Se esse caractere n�o esta entre os valores poss�veis ent�o, n�o � valido
					forValores:
					for (ValorDescritorCampoControle valorDescritorCampoControle : valores) {

						if(valorDescritorCampoControle.getValor().equals( Character.toString(dadosMultiplos.charAt(pt)) )){
							caracterValido = true;
							break forValores;
						}
					}

					// Se tem algum caractere inv�lido, ent�o o valor n�o � valido
					if(!caracterValido){
						valorValido = false;
					}
				}

			}else{ // O valor tem que ser exatamente igual, � o caso mais comum e mais simples  

				/* Para todos os valores que esse campo pode assumir */
				for (ValorDescritorCampoControle valorDescritorCampoControle : valores) {

					// Testa se o valor digitado na posi��o descrita pelo descritor
					// est� entre os poss�veis valores que ele pode assumir.
					if (c.getDado().substring(posicaoI, posicaoF + 1).equals(
							valorDescritorCampoControle.getValor())) {
						valorValido = true;
					}

				}
			}

			// Se possui um conjunto de valores e o valor digitado n�o est� contido entre eles 
			if (valores.size() > 0 && valorValido == false) {
				if (posicaoI == posicaoF) {
					adicionaMensagemErroCatalogacao(erros,  new MensagemAviso( "O valor: '"+c.getDado().substring(posicaoI, posicaoF + 1)+"' para a posi��o: "
							+ " ("+ posicaoI+ ") " + descritorCampoControle.getDescricao()+" do campo :    "+c.getEtiqueta().getTag()
							+ " � inv�lido ", TipoMensagemUFRN.ERROR));
				} else {
					adicionaMensagemErroCatalogacao(erros,  new MensagemAviso("O valor: '"+c.getDado().substring(posicaoI, posicaoF + 1)+"' para as posi��es: "
							+ " ("+ posicaoI+ ","+ posicaoF+ ") "+ descritorCampoControle.getDescricao()+" do campo :    "+c.getEtiqueta().getTag()
							+ " � inv�lido ", TipoMensagemUFRN.ERROR));
				}
			}

		}
	}
	
	
	

	/**
	 * M�todo espec�fico para validar os campos de dados
	 * 
	 */
	private static void validaCampoDados(GenericDAO dao, CampoDados d, ListaMensagens erros) throws DAOException{

		if( ! d.isCampoVazio() )  {    ///// N�o valida campos vazios, eles ser�o apagados depois
		
			////////////////////////  Valida os indicadores        /////////////////
			List<ValorIndicador> valoresIndicador = (List<ValorIndicador>) dao
				.findByExactField(ValorIndicador.class, "etiqueta.id", d.getEtiqueta().getId());
	
			ValorIndicador valor1 = new ValorIndicador((short)1, d.getIndicador1());
			ValorIndicador valor2 = new ValorIndicador((short)2, d.getIndicador2());
			
			if(! valoresIndicador.contains(valor1)){
				adicionaMensagemErroCatalogacao(erros, new MensagemAviso( "O valor: '"+d.getIndicador1()+"' para o 1� indicador do campo "
						+d.getEtiqueta().getTag()+" � inv�lido", TipoMensagemUFRN.ERROR));
			}
	
			if(! valoresIndicador.contains(valor2)){
				adicionaMensagemErroCatalogacao(erros, new MensagemAviso( "O valor: '"+d.getIndicador2()+"' para o 2� indicador do campo "
						+d.getEtiqueta().getTag()+" � inv�lido", TipoMensagemUFRN.ERROR));
			}
			
			List<DescritorSubCampo> descritoresSubCampo = (List<DescritorSubCampo>) dao
					.findByExactField(DescritorSubCampo.class, "etiqueta.id", d.getEtiqueta().getId());
	
	
			validaDescritoresSubCampo( d, descritoresSubCampo, erros);
		}
	}

	
	/**
	 * Valida se os dados contidos nos subcampos s�o dados v�lidos
	 */
	private static void validaDescritoresSubCampo(CampoDados d,  List<DescritorSubCampo> descritoresSubCampo, 
			ListaMensagens erros){
		
		// Usado para contar os subcampos de um campo para saber se ele pode ser repetido ou n�o
		List<Character> listaSubCampos = new ArrayList<Character>(); 
		
		// Para cada subcampo 
		if(d.getSubCampos() != null)
			for (SubCampo subCampo : d.getSubCampos()) {
				
				if(! subCampo.isSubCampoVazio()) { // Se o subCampo for vazio n�o valida, ele ser� removido quando for salvo.
				
					DescritorSubCampo descritorTemp = new DescritorSubCampo(subCampo.getCodigo(), d.getEtiqueta()); // descritor criado com os dados do subcampo
					
					if(descritoresSubCampo.contains(descritorTemp)){
						
						// O descritor do sub campo
						
						DescritorSubCampo descritorReal = descritoresSubCampo.get(descritoresSubCampo.indexOf(descritorTemp));
						
						if(! listaSubCampos.contains(subCampo.getCodigo())){ // primeira ocorr�ncia do sub campo
							listaSubCampos.add(subCampo.getCodigo());
							
						}else{  // se j� cont�m o sub campo
							if( ! descritorReal.isRepetivel() ){ // e ele n�o pode ser repetido
								adicionaMensagemErroCatalogacao(erros,  new MensagemAviso(" O sub campo '"+subCampo.getCodigo()+"' do campo "+subCampo.getCampoDados().getEtiqueta().getTag()+" n�o pode ser repetido. ", TipoMensagemUFRN.ERROR));
							}
						}
						
						// Se o descritor tiver valores � preciso validar
						List<ValorDescritorSubCampo> valorDescritor = new ArrayList<ValorDescritorSubCampo>(descritorReal.getValoresDescritorSubCampo());
						
						if(valorDescritor != null && valorDescritor.size() > 0 ){
							for (int ptr = 0; ptr < subCampo.getDado().length(); ptr++  ) {
								
								ValorDescritorSubCampo valorTemp = new ValorDescritorSubCampo(String.valueOf(subCampo.getDado().charAt(ptr)));
								
								if( ! valorDescritor.contains(valorTemp)){ // se o descritor n�o cont�m o valor no campo dados
									 adicionaMensagemErroCatalogacao(erros, new MensagemAviso( "O valor: '"+subCampo.getDado().charAt(ptr)+"' n�o � v�lido para o sub campo '"
											+subCampo.getCodigo()+"' do campo: "+d.getEtiqueta().getTag(), TipoMensagemUFRN.ERROR));
								}
							}
						}
					}else{ // Se n�o cont�m o descritor 'a' '100' � porque o subcampo 'a' n�o pode existir no campo da etiqueta '100'
						adicionaMensagemErroCatalogacao(erros, new MensagemAviso( "O sub campo: '"+subCampo.getCodigo()+"' n�o � v�lido para o campo "
								+d.getEtiqueta().getTag(), TipoMensagemUFRN.ERROR));
					}
				} // if(subCampo.getCodigo() != null)
			}
	}
	
	
	
	
	
	
	
	
	/**
	 *    M�todo que impede que mensagens de erros iguais sejam mostradas ao usu�rio. 
	 *  
	 *    Estava ocorrendo muito quando tinha algum dado errado em campos que podem ser repetidos, 
	 * ficava mostrando v�rias mensagens informando a mesma coisa.
	 */
	private static void adicionaMensagemErroCatalogacao(ListaMensagens listaErro, MensagemAviso m){
		
		if(! listaErro.getMensagens().contains(m)){
			listaErro.addErro(m.getMensagem());
		}
	}
}
