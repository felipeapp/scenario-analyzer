/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/07/2006
 */
package br.ufrn.arq.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.dominio.RegistroEntradaDevice;
import br.ufrn.arq.seguranca.log.LogOperacao;

/**
 * DAO para a realização de buscas associadas à entidade RegistroEntrada
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public interface RegistroEntradaDAO extends GenericDAO {

	/**
	 * Busca por registros de entradas que tiveram atividades nos ultimos
	 * minutos
	 *
	 * @param minutos
	 * @return
	 */
	public Collection<RegistroEntrada> findByAtividade(int minutos);

	/**
	 * Busca por logs de operação de acordo com as datas passadas como parâmetro.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public Collection<LogOperacao> findByDatas(Date dataInicio, Date dataFim);

	/**
	 * Busca por logs de operação de acordo com as datas passadas como parâmetro. Pode retornar
	 * apenas os logs que possuem erro, caso o parâmetro apenasErros seja passado como true.
	 * @param dataInicio
	 * @param dataFim
	 * @param apenasErros
	 * @return
	 */
	public Collection<LogOperacao> findByDatas(Date dataInicio, Date dataFim, boolean apenasErros);
	
	/**
	 * Busca todos os registros de entrada que possuírem o IP passado como parâmetro.
	 * @param ip
	 * @return
	 */
	public Collection<RegistroEntrada> findByIP(String ip);
	
	/**
	 * Busca todos os registros de entrada do usuário cujo login foi passado como parâmetro.
	 * @param login
	 * @return
	 */
	public Collection<RegistroEntrada> findByLogin(String login);
	
	/**
	 * Busca os últimos n registros de entrada do usuário cujo login foi passado como parâmetro, onde
	 * n é o parâmetro maxResults.
	 * @param login
	 * @param maxResults
	 * @return
	 */
	public Collection<RegistroEntrada> findByLogin(String login, int maxResults);

	/**
	 * Busca um log de operação por id;
	 * @param idOperacao
	 * @return
	 */
	public LogOperacao findByOperacao(int idRegistroEntrada);
	
	/**
	 * Busca registros de entrada por um período (data inicial e data final).
	 * @param periodoInicio
	 * @param periodoFim
	 * @return
	 */
	public Collection<RegistroEntrada> findByPeriodo(Date periodoInicio, Date periodoFim);

	/**
	 * Busca os registros de entrada que foram abertos nos últimos minutos.
	 * @param minutos
	 * @return
	 */
	public Collection<RegistroEntrada> findByTempo(int minutos);

	/**
	 * Busca pela hora da ultima atividade realizada pelo usuário
	 * cujo registro de entrada foi passado como parâmetro. 
	 * @param idRegistroEntrada
	 * @return
	 */
	public Date findByUltimaAtividade(int idRegistroEntrada);

	/**
	 * Busca os log de operações do usuário de acordo com
	 * o registro de entrada passado como parâmetro.
	 *
	 * @param idRegistroEntrada
	 * @return
	 */
	public Collection<LogOperacao> findLogByRegistro(int idRegistroEntrada);

	/**
	 * Busca os logs de operação em que ocorreram erros nos últimos minutos.
	 * @param minutos
	 * @return
	 */
	public Collection<LogOperacao> findLogErro(int minutos);

	/**
	 * Busca logs de operações de acordo com os parâmetros passados.
	 * @param sistemaSelecionado
	 * @param data
	 * @param url
	 * @param idRegistroEntrada
	 * @param idRegistroAcessoPublico
	 * @param excecao
	 * @return 
	 */	
	public List<LogOperacao> findLogOperacoes(int sistemaSelecionado, Date data, String url, Integer idRegistroEntrada, Integer idRegistroAcessoPublico, String excecao);

	/**
	 * Busca um registro de entrada pelo seu id.
	 * @param idRegistro
	 * @return
	 */
	public RegistroEntrada findRegistroById(int idRegistro);
	
	/**
	 * Busca por registros de entrada que possuam referencia aos parâmetros informados em seu log de operações
	 * @param parametros
	 * @param periodoInicio
	 * @param periodoFim
	 * @return
	 */
	public Collection<RegistroEntrada> findByParametros(String parametros, Date periodoInicio, Date periodoFim);
	
	/**
	 * Busca por registros de entrada que possuam referencia a action informada em seu log de operações
	 * @param parametros
	 * @param periodoInicio
	 * @param periodoFim
	 * @return
	 */
	public Collection<RegistroEntrada> findByAction(String action, Date periodoInicio, Date periodoFim);

	/**
	 *
	 * Busca quantidade de requisições por minuto usando como o log operação como base.
	 *
	 * @param data
	 * @param sistemas
	 * @return
	 */
	public Map<Integer, Double> findRequisicoesDiario(Date data, List<Integer> sistemas);

	/**
	 * Busca quantidade de requisições por mês usando como o log operação como base.
	 * 
	 * @param ano
	 * @param sistema
	 * @return
	 */
	public Map<Integer, Double> findRequisicoesAnual(Integer ano, List<Integer> sistemas);

	/**
	 * Busca quantidade de requisições por dia em um mês usando como o log operação 
	 * como base.
	 * 
	 * @param mes
	 * @param ano
	 * @param sistema
	 * @return
	 */
	public Map<Integer, Double> findRequisicoesMensal(Integer mes, Integer ano, List<Integer> sistemas);

	/**
	 * Busca quantidade de requisições por semana usando como o log operação 
	 * como base.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @param sistema
	 * @return
	 */
	public Map<Integer, Double> findRequisicoesSemanal(Date dataInicio, Date dataFim, List<Integer> sistemas);

	/**
	 * Busca quantidade de requisições por minuto em uma hora de um dia usando como o log operação 
	 * como base.
	 * 
	 * @param data
	 * @param hora
	 * @param sistema
	 * @return
	 */
	public Map<Integer, Double> findRequisicoesHorario(Date data, Integer hora, List<Integer> sistemas);
	
	
	/**
	 * Método criado para persistir informações do dispositivo móvel.
	 * @param registroDevice
	 */
	public void registrarDeviceInfo(RegistroEntradaDevice registroDevice);
}
