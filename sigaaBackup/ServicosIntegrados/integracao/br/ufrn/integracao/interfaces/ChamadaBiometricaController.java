/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/08/2009
 *
 */	
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.DiscenteDTO;
import br.ufrn.integracao.dto.FrequenciaEletronicaDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface responsável pela comunicação remota entre o aplicativo
 * desktop de Chamada Biométrica e o SIGAA.   
 * 
 * @author agostinho campos
 */
@WebService
public interface ChamadaBiometricaController extends Serializable {
    
	/**
	 * Verifica os dados e senha da turma estão corretos. Caso existam subturmas para essa turma elas serão retornadas no DTO 
	 * 
	 * @param codigoTurma
	 * @param codigoComponente
	 * @param senha
	 * @param inetAdd
	 * @return
	 * @throws Exception
	 */
    public FrequenciaEletronicaDTO logarSistemaChamadaBiometria(String codigoTurma, String codigoComponente, String senha, String hostName, String hostAddress, boolean turmaFerias);
    
    /**
     * Verifica se a senha da {@link EstacaoBiometrica} está correta, permitindo assim que o usuário possa cadastrar uma nova turma a partir do app desktop
     *  
     * @param codigoComponente código do componente, por exemplo: ECT1103
     * @param codigoTurma código da turma, por exemplo: 02
     * @param senha senha definida pela turma virtual para essa turma em específico, diferente da senha do SIGAA
     * @return
     * @throws Exception
     */
    public boolean verificarSenhaEstacaoBiometrica(String codigoTurma, String codigoComponente, String senha);
    
    /**
     * Verifica se a hora de início da aula inserido pelo usuário no app desktop é válida, comparando com o servidor
     * 
     * @param horarioInicioAula hora enviada pelo app desktop
     * @return
     */
    public boolean verificarValidadeInicioAula(Date horarioInicioAula);

    /**
     * Carrega os alunos da turma ou subturma e envia para o app dektop os discentes que fazem parte da mesma, com
     * suas respectivas digitais, dessa forma a verificação da digital é feita localmente, evitando acesso desnecessário ao server. 
     *  
     * @param codigoTurma código da turma, por exemplo: 02
     * @param idTurma referencia a turma ou subturma selecionada para qual a presença será registrada
     * @return
     * @throws NegocioRemotoException 
     * @throws Exception
     */
    public FrequenciaEletronicaDTO iniciarNovaChamada(String codigo, String idTurma) throws NegocioRemotoException;

    /**
     * Registra a hora de fechamento do app desktop em {@link FrequenciaEletronica}  
     * 
     * @param idFrequencia referencia a entidade {@link FrequenciaEletronica} para qual a chamada foi inicialmente aberta   
     * @param codigoComponente código do componente, por exemplo: ECT1103
     * @param codigoTurma código da turma, por exemplo: 02
     * @param senha senha definida pela turma virtual para essa turma em específico, diferente da senha do SIGAA
     * @return
     * @throws Exception
     */
    public boolean fecharChamada(int idFrequencia, String codigoTurma, String codigoComponente, String senha, boolean turmaFerias);

    /**
     * Envia para o desktop a hora atual do servidor para que o dekstop possa usá-la como ponto de partida para seu próprio incremento de tempo local.
     * 
     * @return
     * @throws Exception
     */
    public Date sincronizarDataHoraServidor();
    
    /**
     * Registra efetivamente a presença ou falta de um aluno em {@link FrequenciaAluno}
     * 
     * @param idDiscente
     * @param codigoTurmaOuSubTurma referencia a turma ou subturma selecionada para qual a presença será registrada
     * @param codigoTurma código da turma que foi aberta, a turma principal independente se existem ou não subturmas, por exemplo: 02
     * @param codigoComponente código da disciplina que foi aberta, a turma principal independente se existem ou não subturmas, por exemplo: ECT1103
     * @param horarios quantidade de horários que a turma possui
     * @param horaInicialAula hora que a aula iniciou, usado para calcular quantas presenças o aluno vai receber
     * @param duracaoAula duração de uma aula, usado para calcular quantas presenças o aluno vai receber
     * @param minTolerAula minutos de tolerância da aula, usado para calcular quantas presenças o aluno vai receber
     * @return
     * @throws Exception
     */
    public boolean registrarFrequenciaDiscente(int idDiscente, int codigoTurmaOuSubturma, String codigoTurma, String codigoComponente, int horarios, Date horaInicialAula, 
    		int duracaoAula, int minTolerAula, boolean iniciouSegundaAula);

    /**
     * Cadastra uma nova turma a partir do próprio app desktop, habilitando a turma virtual para que o docente defina sua senha  
     * 
     * @param codigoTurma
     * @param codigoComponente
     * @param inetAdd
     * @return
     * @throws NegocioRemotoException 
     * @throws Exception
     */
    public boolean cadastrarTurma(String codigoTurma, String codigoComponente, String hostMac, String hostAddress) throws NegocioRemotoException;
    

    /**
     * Retorna os discentes com suas respectivas faltas/presenças para exibir em uma listagem no app desktop
     * 
     * @param codigoComponente código do componente, por exemplo: ECT1103
     * @param codigoTurma código da turma, por exemplo: 02
     * @param temSubturma indica se vai realizar exibir os alunos com presença/falta de alguma subturma 
     * @param codigoSubturma usado no caso de estar buscando alunos em alguma subturma
     * @return
     * @throws Exception
     */
    public List<DiscenteDTO> ultimasChamadasRealizadas(String codigoComponente, String codigoTurma, boolean temSubturma, String codigoSubturma);

    /**
     * Registra faltas para os alunos da turma. O número de faltas é o mesmo da quantidade de horário.
     *  
     * @param idTurmaOuSubturma referencia a turma ou subturma selecionada para qual a presença será registrada
     * @param horarios quantidade de aulas que a turma possui
     * @throws Exception
     */
    public void registrarFaltaParaAlunos(int idTurmaOuSubturma, int horarios, String codigoTurma, String codigoComponente);
    
}