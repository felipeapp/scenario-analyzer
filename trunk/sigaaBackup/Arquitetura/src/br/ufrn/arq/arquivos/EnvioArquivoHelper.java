/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/10/2007
 */
package br.ufrn.arq.arquivos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.util.ValidatorUtil;

/**
 *
 * Classe utilitária para manipulação dos arquivos. Permite
 * inserção, remoção e recuperação de arquivos do banco de dados.
 * Pode dividir a base de arquivos em vários clusters, que armazenam 
 * os arquivos divididos em pedaços (ou itens). Na hora da recuperação, os
 * pedaços dos arquivos são juntados novamente.
 *
 * @author Gleydson Lima
 * @author David Pereira 
 *
 */
public class EnvioArquivoHelper {

	/** Tamanho de cada item do arquivo. */
	private static final int TAM_BUFFER = 50 * 1024; // 50 Kb

	/** Lista dos nós do cluster de arquivos disponíveis para utilização */
	private static final List<ClusterArquivo> CLUSTERS = new ArrayList<ClusterArquivo>();
		
	private static String schema = obterSchema();
	
	private static final String ESQUEMA_PADRAO_BASE_ARQUIVOS = "public";
	
	/** 
	 * Mapa contendo um cache para os diversos JdbcTemplate que serão utilizados nesta classe (um JdbcTemplate
	 * por DataSource, o que implica em um JdbcTemplate por nó do cluster. 
	 */
	private static final Map<DataSource, JdbcTemplate> CACHE_JDBC_TEMPLATE = new HashMap<DataSource, JdbcTemplate>();

	/**
	 * Retorna um JdbcTemplate que está no cache, se existir, ou cria
	 * um novo e o coloca no cache. Tudo isso com base no DataSource
	 * passado como parâmetro.
	 * 
	 * @param ds
	 * @return
	 */
	private static JdbcTemplate getJdbcTemplate(DataSource ds) {
		if (CACHE_JDBC_TEMPLATE.get(ds) == null)
			CACHE_JDBC_TEMPLATE.put(ds, new JdbcTemplate(ds));
		return CACHE_JDBC_TEMPLATE.get(ds);
	}
	
	/**
	 * Retorna um JdbcTemplate associado ao DataSource do cluster
	 * do arquivo cujo id foi passado como parâmetro.
	 * 
	 * @param idArquivo
	 * @return
	 */
	private static JdbcTemplate getArquivoTemplate(int idArquivo) {
		int numeroCluster = getArquivosTemplate().queryForInt("select c.numero_cluster from " + schema +"."+ "cluster_arquivos c, arquivos a where c.numero_cluster = a.numero_cluster and a.id_arquivo = ?", new Object[] { idArquivo });
		DataSource ds = getClusterDs(numeroCluster);
		return getJdbcTemplate(ds);
	}

	/**
	 * Retorna o JdbcTemplate que aponta pra a base de arquivos.
	 * @return
	 */
	private static JdbcTemplate getArquivosTemplate() {
		return getJdbcTemplate(Database.getInstance().getArquivosDs());
	}
	
	/**
	 * Recupera o próximo Id de arquivo disponivel. Este
	 * método deve ser chamado antes da inserção de um novo arquivo.
	 *
	 * @return
	 */
	public static int getNextIdArquivo() {
		return getArquivosTemplate().queryForInt("select nextval('" + schema +"."+ "arquivo_seq')");
	}
	
	/**
	 * Decide em que nó do cluster um próximo arquivo será inserido.
	 * Retorna o nó escolhido.
	 * @return
	 */
	private static List<ClusterArquivo> getClusterNodes() {
		refreshClusterNodes();
		return CLUSTERS;

	}
	
	/**
	 * Retorna um datasource associado com o Cluster Node através
	 * do número do cluster passado como parâmetro.
	 *
	 * @param numeroCluster
	 * @return
	 */
	private static DataSource getClusterDs(int numeroCluster) {
		try {
			String datasource = (String) getArquivosTemplate().queryForObject("select datasource from "  + schema +"."+  "cluster_arquivos c where c.numero_cluster = ?", new Object[] { numeroCluster }, String.class);
			return (DataSource) new InitialContext().lookup("java:/" + datasource);
		} catch(EmptyResultDataAccessException e) {
			throw new ConfiguracaoAmbienteException("Base Arquivos: cluster de arquivos não encontrado!");
		} catch (NamingException e) {
			throw new ConfiguracaoAmbienteException("Base Arquivos: Erro na leitura da fonte de dados do cluster " + numeroCluster, e);
		}
	}
	
	/**
	 * Carrega os clusters ativos do banco de dados na lista estática
	 * de clusters existente nesta classe.
	 */
	private static void refreshClusterNodes() {
		getArquivosTemplate().query("select * from " + schema +"."+"cluster_arquivos where ativo = trueValue()", new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					ClusterArquivo node = new ClusterArquivo();
					node.setNumeroCluster(rs.getInt("numero_cluster"));

					if (CLUSTERS.contains(node)) {
						node = CLUSTERS.get(CLUSTERS.indexOf(node));
					} else {
						CLUSTERS.add(node);
					}

					node.setDataSource(rs.getString("datasource"));
					node.setIp(rs.getString("ip"));
					node.setPeso(rs.getShort("peso"));
					node.setQtdArquivos(rs.getInt("qtd_arquivos"));
					node.setCapacidade(rs.getLong("capacidade"));
					node.setUtilizado(rs.getLong("utilizado"));
				}
				
				return CLUSTERS;
			}
		});
	}
	
	/**
	 * Após inserir um arquivo, atualiza as informações do cluster com o tamanho do arquivo
	 * inserido.
	 * 
	 * @param tamanhoArquivo
	 * @param clusterNumero
	 */
	private static void updateClusterNode(long tamanhoArquivo, int clusterNumero) {
		//getArquivosTemplate().update("update " + schema +"."+  "cluster_arquivos set utilizado = utilizado + ?, qtd_arquivos = qtd_arquivos + 1 where numero_cluster = ?", new Object[] { tamanhoArquivo, clusterNumero });
	}
	
	



	/**
	 * Insere um arquivo na base de arquivos, escolhendo um nó do cluster
	 * para armazenar os seus itens, dividindo o arquivo em itens e inserindo
	 * os itens no nó escolhido.
	 * 
	 * @param idArquivo
	 * @param conteudo
	 * @param contentType
	 * @param nome
	 * @throws IOException
	 */
	public static void inserirArquivo(final int idArquivo, final byte[] conteudo, final String contentType, final String nome) {
		
		List<ClusterArquivo> clusters = getClusterNodes();
		final String nomeArquivo = nome.substring(nome.lastIndexOf("\\") + 1);
		final String contentTypeCorrigido = corrigeContentType(nomeArquivo, contentType);
		
		// Por padrão, comprime o arquivo.
		boolean compressed = true;
		
		final boolean compressedFinal = compressed; 
		
		for (final ClusterArquivo cluster : clusters) {
			getArquivosTemplate().query("select id_arquivo from " + schema +"."+  "arquivos where id_arquivo = ?", new Object[] { idArquivo }, new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					
					String sqlInclusao = null;
	
					if (rs.next()) {
						sqlInclusao = "update " + schema +"."+  "arquivos set content_type = ?, nome = ?, length = ?, numero_cluster=?, compressed = ? where id_arquivo = ?";
						getArquivoTemplate(idArquivo).update("delete from arquivo_itens where id_arquivo = ?", new Object[] { idArquivo });
					} else {
						sqlInclusao = "insert into " + schema +"."+ "arquivos (content_type, nome, length, numero_cluster, compressed, id_arquivo) VALUES (?, ?, ?, ?, ?, ?)";
					}
	
	
					byte[] buffer;
	
					// Divide o arquivo em fragmentos do tamanho de TAM_BUFFER
					int fragmentNumber = 1;
					int tamanhoArquivo = conteudo.length;
					int totalGravados = 0;
					byte[] arquivo = conteudo; // redeclara variável para acesso no inner method
					
					while (totalGravados < tamanhoArquivo) {
						
						int fragmentSize = TAM_BUFFER;
						
						if ( arquivo.length - totalGravados >= TAM_BUFFER ) {
							buffer = new byte[TAM_BUFFER];
						} else {
							fragmentSize = arquivo.length - totalGravados;
							buffer = new byte[fragmentSize];
							
						}
						
						System.arraycopy(conteudo,totalGravados,buffer,0,fragmentSize);
						
						insereItemArquivo(cluster, idArquivo, buffer, compressedFinal, fragmentNumber++);
						
						totalGravados+=fragmentSize;
					}
	
					Object[] args = new Object[] { contentTypeCorrigido, nomeArquivo, conteudo.length, cluster.getNumeroCluster(), compressedFinal, idArquivo };
					getArquivosTemplate().update(sqlInclusao, args);
					return null;
					
				}
			});
			
			updateClusterNode(conteudo.length, cluster.getNumeroCluster());
		}
	}
	
	/**
	 * Corrige o content type de alguns tipos de arquivo que dão problema.
	 * @param nomeArquivo
	 * @param contentType
	 * @return
	 */
	private static String corrigeContentType(String nomeArquivo, String contentType) {
		if (nomeArquivo.endsWith(".pdf") && !"application/pdf".equals(contentType)) {
			return "application/pdf";
		} else {
			return contentType;
		}
	}

	/**
	 * Insere os itens do arquivo para prover buferização na hora de baixar o
	 * mesmo. Os itens são inseridos no nó do cluster cujo DataSource foi passado
	 * como parâmetro.
	 *
	 * @param idArquivo
	 * @param buffer
	 * @param compressed
	 * @param fragmentNumber
	 */
	private static void insereItemArquivo(ClusterArquivo cluster, int idArquivo, byte[] buffer, boolean compressed, int fragmentNumber) {
		JdbcTemplate jt = getJdbcTemplate(getClusterDs(cluster.getNumeroCluster()));
		
		if (compressed) {
			try {
				buffer = compactaFluxo(buffer);
			} catch (IOException e) {
				throw new InvalidDataAccessResourceUsageException(e.getMessage(), e);
			}
		}

		jt.update("insert into " + schema +"."+"arquivo_itens (id_arquivo, conteudo, size, item) VALUES (?, ?, ?, ?)", new Object[] { idArquivo, buffer, buffer.length, fragmentNumber });
	}
	
	
	
	/**
	 * Recebe um array de bytes e o retorna compactado.
	 *
	 * @param fluxo
	 * @return
	 * @throws IOException
	 */
	private static byte[] compactaFluxo(byte[] fluxo) throws IOException {
		ByteArrayOutputStream o1 = new ByteArrayOutputStream();
		GZIPOutputStream out = new GZIPOutputStream(o1);
		out.write(fluxo);
		out.close();
		return o1.toByteArray();

	}

	/**
	 * Recebe um array de bytes e retorna ele descompactado.
	 *
	 * @param fluxo
	 * @return
	 * @throws IOException
	 */
	private static byte[] descompactaFluxo(byte[] fluxo) throws IOException {
		ByteArrayInputStream i1 = new ByteArrayInputStream(fluxo);
		GZIPInputStream in = new GZIPInputStream(i1);

		ByteArrayOutputStream outPlain = new ByteArrayOutputStream();

		int total = 0;
		byte[] buffer = new byte[TAM_BUFFER];

		while ((total = in.read(buffer)) != -1) {
			outPlain.write(buffer, 0, total);
		}

		in.close();
		return outPlain.toByteArray();

	}

	/**
	 * Lê o arquivo armazenado em um nó do cluster e joga o seu conteúdo
	 * para o OutputStream de response.
	 *
	 * @param response
	 * @param idArquivo
	 * @throws IOException 
	 */
	public static void recuperaArquivo(HttpServletResponse response, int idArquivo, boolean save) throws IOException {
		Map<String, Object> info = infoArquivo(idArquivo);
		if (info != null) {
			String nome = (String) info.get("nome");
			Integer length = (Integer) info.get("length");
			String contentType = (String) info.get("content_type");
			Date data = (Date) info.get("data");
			
			response.setContentType(contentType);
			response.setContentLength(length);
			response.addHeader("Last-Modified", new SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'").format(data));
	
			if (save) {
				response.setHeader("Content-disposition", "attachment; filename=\"" + nome + "\"");
			} else {
				response.setHeader("Content-disposition", "inline; filename=\"" + nome + "\"");
			}
			
			recuperaArquivo(response.getOutputStream(), idArquivo);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> infoArquivo(int idArquivo) {
		try {
			return getArquivosTemplate().queryForMap("select * from " + schema +"."+"arquivos where id_arquivo = ?", new Object[] { idArquivo });
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Lê o arquivo armazenado em um nó do cluster e joga o seu conteúdo
	 * para o OutputStream passado como parâmetro.
	 * 
	 * @param os
	 * @param idArquivo
	 * @throws Exception
	 */
	public static void recuperaArquivo(final OutputStream os, final int idArquivo) {
		getArquivosTemplate().query("select * from " + schema +"."+"arquivos where id_arquivo = ?", new Object[] { idArquivo }, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					final boolean compressed = rs.getBoolean("compressed");
					getArquivoTemplate(idArquivo).query("select conteudo from " + schema +"."+  "arquivo_itens where id_arquivo = ? order by item", new Object[] { idArquivo }, new ResultSetExtractor() {
						public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
							try {
								while (rs.next()) {
									byte[] conteudo = rs.getBytes("CONTEUDO");
									if (compressed)
										conteudo = descompactaFluxo(conteudo);
									os.write(conteudo);
								}
							} catch(IOException e) {
								throw new InvalidDataAccessResourceUsageException(e.getMessage(), e);
							}
							
							return null;
						}
					});
				}
				return null;
			}
		});
	}
	
	/**
	 * Dado o id de um arquivo, recupera o seu content type.
	 * @return 
	 */
	public static String recuperaContentTypeArquivo(int idArquivo) {
		try {
			return (String) getArquivosTemplate().queryForObject("select content_type from " + schema +"."+ "arquivos where id_arquivo = ?", new Object[] { idArquivo }, String.class);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Dado o id de um arquivo, recupera o seu nome.
	 * @return 
	 */
	public static String recuperaNomeArquivo(int idArquivo) {
		try {
			return (String) getArquivosTemplate().queryForObject("select nome from " + schema +"."+  "arquivos where id_arquivo = ?", new Object[] { idArquivo }, String.class);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Remove o arquivo da base de arquivos e os seus itens do
	 * nó do cluster associado ao arquivo. 
	 *
	 * @param idArquivo
	 */
	public static void removeArquivo(int idArquivo) {
		try {
			getArquivoTemplate(idArquivo).update("delete from "  + schema +"."+  "arquivo_itens where id_arquivo = ?", new Object[] { idArquivo });
			getArquivosTemplate().update("delete from "+ schema +"."+ "arquivos where id_arquivo = ?", new Object[] { idArquivo });
		} catch(EmptyResultDataAccessException e) {
			// Arquivo não existe, portanto não precisa ser excluído.
		}
	}
	
	
	public static String obterSchema()
	{	
		
		try {
			ResourceBundle config = ResourceBundle.getBundle("br.ufrn.arq.dao.banco");		
			schema = ValidatorUtil.isEmpty(config.getString("base_arquivos_schema")) ? ESQUEMA_PADRAO_BASE_ARQUIVOS : config.getString("base_arquivos_schema");
		} catch (Exception e) {
			schema = ESQUEMA_PADRAO_BASE_ARQUIVOS;
			e.printStackTrace();
		}
		
		return schema;		
	}

}
