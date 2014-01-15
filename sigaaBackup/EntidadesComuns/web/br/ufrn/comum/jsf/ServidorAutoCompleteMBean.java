/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 28/10/2009
 */
package br.ufrn.comum.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.FORMATO_INVALIDO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dao.ServidorDAO;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.Servidor;

/**
 * Managed Bean para possibilitar a criação de autocompletes de servidores.
 * 
 * @author David Pereira
 */
@Component
@Scope("session")
public class ServidorAutoCompleteMBean extends AbstractController {

	public static final int ATIVO = 1;
	public static final int APOSENTADO = 2;
	public static final int CEDIDO = 7;
	
	/**
	 * Autocomplete utilizado pelo suggestion box do richfaces.
	 * A pessoa pode digitar o siape ou o nome do servidor.
	 * 
	 * @param event
	 * @return
	 * @throws DAOException   
	 */
	public List<Servidor> autocompleteNomeServidor(Object event) throws DAOException {
		
		// Apenas servidores ativos
		boolean apenasAtivos = false;
		
		// Apenas servidores docentes
		boolean apenasDocentes = false;
		
		// Adiciona usuario logado ao resultado
		boolean adicionaUsuarioLogado = false;
		
		
		if (getParameterBoolean("apenasAtivos")){
			apenasAtivos = getParameterBoolean("apenasAtivos");
		}
		
		if (getParameterBoolean("apenasDocentes"))
			apenasDocentes = getParameterBoolean("apenasDocentes");
		
		if (getParameterBoolean("adicionaUsuarioLogado"))
			adicionaUsuarioLogado = getParameterBoolean("adicionaUsuarioLogado");
		
		
		int[] ativos = null;

		//Tipo especifico de ativo
		String ativoStr = getParameter("ativo");
		if (!isEmpty(ativoStr) && !"null".equals(ativoStr)) {
			if ( ativoStr.contains(";") ) {
				StringTokenizer st = new StringTokenizer(ativoStr, ";");
				ativos = new int[st.countTokens()];
				int count = 0;
				while (st.hasMoreTokens()) {
					ativos[count++] = new Integer(st.nextToken());
				}

			} else {
				ativos = new int[1] ;
				ativos[0] = new Integer(ativoStr);
			}
		}
		
		//Para informar a unidade da qual deverão ser filtrados servidores lotados
		String idUnidadeStr = getParameter("idUnidade");
		int[] unidades = null;
		if (!isEmpty(idUnidadeStr) && !"null".equals(idUnidadeStr)){

			//Considera servidores lotados nas unidades passadas como argumento e nas unidades filhas desta unidade

			if ( idUnidadeStr.contains(";") ) {
				StringTokenizer st = new StringTokenizer(idUnidadeStr, ";");
				unidades = new int[st.countTokens()];
				int count = 0;
				while (st.hasMoreTokens()) {
					Integer id  = new Integer(st.nextToken());

					if(id > 0)
						unidades[count++] = id;
				}

			} else if(!isEmpty(idUnidadeStr)){
				int idUnidade = Integer.parseInt(idUnidadeStr);
				if(idUnidade > 0){
					unidades = new int[1] ;
					unidades[0] = idUnidade;
				}
			}
		}
		
		//Para informar os níveis de responsabilidade dos servidores a serem filtrados
		String niveisResponsabilidadeStr = getParameter("niveisResponsabilidade");
		Character[] niveisResponsabilidade = null;
		if (!isEmpty(niveisResponsabilidadeStr) && !"null".equals(niveisResponsabilidadeStr)) {
			
			if (niveisResponsabilidadeStr.contains(";")) {
				StringTokenizer st = new StringTokenizer(niveisResponsabilidadeStr, ";");
				niveisResponsabilidade = new Character[st.countTokens()];
				int count = 0;
				while (st.hasMoreTokens()) {
					char nivel = st.nextToken().charAt(0);
					
					if(nivel != ' ') {
						niveisResponsabilidade[count++] = nivel;
					}
				}
				
			} else if(!isEmpty(niveisResponsabilidadeStr)){
				char nivel = niveisResponsabilidadeStr.charAt(0);
				if(nivel != ' ') {
					niveisResponsabilidade = new Character[1] ;
					niveisResponsabilidade[0] = nivel;
				}
			}
		}
		
		//Para informar as categorias dos servidores que deverão ser filtrados
		String categoriasStr = getParameter("categorias");
		int[] categorias = null;
		if (!isEmpty(categoriasStr) && !"null".equals(categoriasStr)) {
			if (categoriasStr.contains(";")) {
				StringTokenizer st = new StringTokenizer(categoriasStr, ";");
				categorias = new int[st.countTokens()];
				int count = 0;
				while (st.hasMoreTokens()) {
					categorias[count++] = new Integer(st.nextToken());
				}
			} else {
				int idCategoria = Integer.parseInt(categoriasStr);
				if (idCategoria > 0) {
					categorias = new int[1] ;
					categorias[0] = idCategoria;
				}
			}
		}
		
		//Para informar as situações dos servidores que deverão ser filtrados
		String situacoesStr = getParameter("situacoes");
		int[] situacoes = null;
		if (!isEmpty(situacoesStr) && !"null".equals(situacoesStr)) {
			if (situacoesStr.contains(";")) {
				StringTokenizer st = new StringTokenizer(situacoesStr, ";");
				situacoes = new int[st.countTokens()];
				int count = 0;
				while (st.hasMoreTokens()) {
					situacoes[count++] = new Integer(st.nextToken());
				}
			} else {
				int idSituacao = Integer.parseInt(situacoesStr);
				if (idSituacao > 0) {
					situacoes = new int[1] ;
					situacoes[0] = idSituacao;
				}
			}
		}
		
		String nome = event.toString(); //Nome da unidade digitada no autocomplete
		boolean buscaPorNome = (StringUtils.getNumerosIniciais(nome) == null);
		List<Servidor> servidoresBuscados = new ArrayList<Servidor>();
				
		if (buscaPorNome) {
			//buscar pelo nome e pelo nome de guerra
			boolean qualquerParteDoNome = ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.PERMITE_CONSULTA_SERVIDOR_QUALQUER_PARTE_NOME);
			servidoresBuscados = getServidores(null, null, nome, qualquerParteDoNome, apenasAtivos, apenasDocentes, ativos, unidades, niveisResponsabilidade, categorias, situacoes);
		} else {
			//consulta por numeros
			ListaMensagens erros = new ListaMensagens();
			Long numero = StringUtils.extractLong(nome);
			
			if (isEmpty(numero))
				erros.addMensagem(FORMATO_INVALIDO, "Servidor");
			else {
				ValidatorUtil.validateCPF_CNPJ(numero, "CPF", erros);
				
				if (erros.isEmpty()) {
					//busca por cpf
					servidoresBuscados = getServidores(null, numero, null, null, apenasAtivos,apenasDocentes, ativos, unidades, niveisResponsabilidade, categorias, situacoes);
				} else if (nome.length() < 10) {
					//busca por siape ou matricula interna
					servidoresBuscados = getServidores(StringUtils.extractInteger(nome), null, null, null, apenasAtivos,apenasDocentes, ativos, unidades, niveisResponsabilidade, categorias, situacoes);
				}
			}
		}
		
		/*
		 * Adiciona o usuário logado na listagem, caso seja um servidor
		 */
		if (adicionaUsuarioLogado) {
			servidoresBuscados = adicionarUsuarioLogado(servidoresBuscados, nome);
		}
		
		return servidoresBuscados;
	}
	
	/**
	 * Adiciona o usuário logado na listagem dos servidores consultados, caso este contenha o nome, nome de identificação, cpf, matrícula siape ou matrícula interna consultados no autocomplete.
	 * 
	 * @param servidoresBuscados
	 * @param padraoBuscado
	 * @return
	 */
	private List<Servidor> adicionarUsuarioLogado(List<Servidor> servidoresBuscados, String padraoBuscado) {
		
		UsuarioGeral usuarioLogado = getUsuarioLogado();		
		
		if (usuarioLogado != null && usuarioLogado.getIdServidor() != null && usuarioLogado.getIdServidor() > 0) {
			
			ServidorDAO dao = getDAO(ServidorDAO.class);
			Servidor servidorLogado = dao.findByPrimaryKey(usuarioLogado.getIdServidor());
			
			if (!isEmpty(servidorLogado) && !servidoresBuscados.contains(servidorLogado)) {
				
				boolean adicionarServidor = false;
				boolean qualquerParteDoNome = ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.PERMITE_CONSULTA_SERVIDOR_QUALQUER_PARTE_NOME);
				// Obtem indicativo de busca por nome
				boolean buscaPorNome = (StringUtils.getNumerosIniciais(padraoBuscado) == null);
				
				if (buscaPorNome) {
					// Adiciona o servidor na lista caso tenha o nome ou nome de identificação buscado
					String nomeServidor = servidorLogado.getPessoa().getNome();
					String nomeIdentificacao = servidorLogado.getNomeIdentificacao();
					// Verifica se o nome pessoal ou nome de identificação do servidor logado é o a string buscada
					adicionarServidor = nomeServidor.startsWith(padraoBuscado.toUpperCase()) 
										|| (!isEmpty(nomeIdentificacao) && nomeIdentificacao.startsWith(padraoBuscado.toUpperCase()));
					// Faz a verificação considerando qualquer parte do nome pessoal e de identificação
					if(qualquerParteDoNome) {
						adicionarServidor = StringUtils.containsIgnoreCase(nomeServidor, padraoBuscado) || StringUtils.containsIgnoreCase(nomeIdentificacao, padraoBuscado);
					}
					
				} else {
					// Extrai o número "long" do padrão buscado
					long numero = StringUtils.extractLong(padraoBuscado);
					// Verifica se é um CPF válido. Caso não seja um CPF válido, considera como uma matrícula interna ou siape 
					if (ValidatorUtil.validateCPF_CNPJ(numero, "CPF", new ListaMensagens())) {
						// Adiciona o servidor na lista caso tenha o cpf buscado
						adicionarServidor = servidorLogado.getPessoa().getCpf_cnpj().equals(numero);
					} else if (padraoBuscado.length() < 10) {
						// Extrai o número "int" do padrão buscado
						int matricula = StringUtils.extractInteger(padraoBuscado);
						// Adiciona o servidor na lista caso tenha a matrícula siape ou interna buscada
						adicionarServidor = servidorLogado.getMatriculaInterna() == matricula || servidorLogado.getSiape() == matricula;
					}
					
				}
				
				// Caso seja para adicionar o servidor
				if (adicionarServidor){
					servidoresBuscados.add(servidorLogado);
					
					// Ordena lista
					Collections.sort(servidoresBuscados, new Comparator<Servidor>() {
						@Override
						public int compare(Servidor serv1, Servidor serv2) {
							return serv1.getPessoa().getNome().compareTo(serv2.getPessoa().getNome());
						}
					});
				}
			}
		}
		
		return servidoresBuscados;
	}
	
	/**
	 * Consulta servidores no banco de dados de acordo com as informações de siape, nome, CPF ou lotação.
	 *  
	 * @param siape
	 * @param nome
	 * @param cpf
	 * @param categorias 
	 * @param situacoes 
	 * @param lotacao
	 * @return
	 * @throws DAOException 
	 */
	private List<Servidor> getServidores(Integer siape, Long cpf, String nome, Boolean qualquerParteDoNome, Boolean apenasAtivos,
			Boolean apenasDocentes, int[] ativos, int[] unidades, Character[] niveisResponsabilidade, int[] categorias, int[] situacoes) throws DAOException {
		ServidorDAO dao = getDAO(ServidorDAO.class);
		dao.setSistema(getSistema());
		
		List<Servidor> servidores = new ArrayList<Servidor>();
		
		boolean permiteHomologacaoHierarquia = getParameterBoolean("permiteHomologacaoHierarquia");
		boolean permiteHomologacaoUnidadeResponsabilidade = getParameterBoolean("permiteHomologacaoUnidadeResponsabilidade");
		
		if(getParameterBoolean("unidadeDesignacao") && Boolean.TRUE.equals(getParameterBoolean("unidadeDesignacao"))) {
			servidores.addAll(dao.findServidoresDesignacoes(siape, cpf, nome, qualquerParteDoNome, apenasAtivos,apenasDocentes, ativos, 
					unidades, categorias, situacoes, permiteHomologacaoHierarquia, permiteHomologacaoUnidadeResponsabilidade));
		} else {
			servidores.addAll(dao.findServidores(siape, cpf, nome, qualquerParteDoNome, apenasAtivos,apenasDocentes, ativos, unidades, niveisResponsabilidade, categorias, situacoes));
		}
		
		return servidores;
	}
}