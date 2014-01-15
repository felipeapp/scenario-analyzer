/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Interface respons�vel pela comunica��o remota entre o aplicativo
 * desktop de Chamada Biom�trica e o SIGAA.   
 * 
 * @author agostinho campos
 */
@WebService
public interface ChamadaBiometricaController extends Serializable {
    
	/**
	 * Verifica os dados e senha da turma est�o corretos. Caso existam subturmas para essa turma elas ser�o retornadas no DTO 
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
     * Verifica se a senha da {@link EstacaoBiometrica} est� correta, permitindo assim que o usu�rio possa cadastrar uma nova turma a partir do app desktop
     *  
     * @param codigoComponente c�digo do componente, por exemplo: ECT1103
     * @param codigoTurma c�digo da turma, por exemplo: 02
     * @param senha senha definida pela turma virtual para essa turma em espec�fico, diferente da senha do SIGAA
     * @return
     * @throws Exception
     */
    public boolean verificarSenhaEstacaoBiometrica(String codigoTurma, String codigoComponente, String senha);
    
    /**
     * Verifica se a hora de in�cio da aula inserido pelo usu�rio no app desktop � v�lida, comparando com o servidor
     * 
     * @param horarioInicioAula hora enviada pelo app desktop
     * @return
     */
    public boolean verificarValidadeInicioAula(Date horarioInicioAula);

    /**
     * Carrega os alunos da turma ou subturma e envia para o app dektop os discentes que fazem parte da mesma, com
     * suas respectivas digitais, dessa forma a verifica��o da digital � feita localmente, evitando acesso desnecess�rio ao server. 
     *  
     * @param codigoTurma c�digo da turma, por exemplo: 02
     * @param idTurma referencia a turma ou subturma selecionada para qual a presen�a ser� registrada
     * @return
     * @throws NegocioRemotoException 
     * @throws Exception
     */
    public FrequenciaEletronicaDTO iniciarNovaChamada(String codigo, String idTurma) throws NegocioRemotoException;

    /**
     * Registra a hora de fechamento do app desktop em {@link FrequenciaEletronica}  
     * 
     * @param idFrequencia referencia a entidade {@link FrequenciaEletronica} para qual a chamada foi inicialmente aberta   
     * @param codigoComponente c�digo do componente, por exemplo: ECT1103
     * @param codigoTurma c�digo da turma, por exemplo: 02
     * @param senha senha definida pela turma virtual para essa turma em espec�fico, diferente da senha do SIGAA
     * @return
     * @throws Exception
     */
    public boolean fecharChamada(int idFrequencia, String codigoTurma, String codigoComponente, String senha, boolean turmaFerias);

    /**
     * Envia para o desktop a hora atual do servidor para que o dekstop possa us�-la como ponto de partida para seu pr�prio incremento de tempo local.
     * 
     * @return
     * @throws Exception
     */
    public Date sincronizarDataHoraServidor();
    
    /**
     * Registra efetivamente a presen�a ou falta de um aluno em {@link FrequenciaAluno}
     * 
     * @param idDiscente
     * @param codigoTurmaOuSubTurma referencia a turma ou subturma selecionada para qual a presen�a ser� registrada
     * @param codigoTurma c�digo da turma que foi aberta, a turma principal independente se existem ou n�o subturmas, por exemplo: 02
     * @param codigoComponente c�digo da disciplina que foi aberta, a turma principal independente se existem ou n�o subturmas, por exemplo: ECT1103
     * @param horarios quantidade de hor�rios que a turma possui
     * @param horaInicialAula hora que a aula iniciou, usado para calcular quantas presen�as o aluno vai receber
     * @param duracaoAula dura��o de uma aula, usado para calcular quantas presen�as o aluno vai receber
     * @param minTolerAula minutos de toler�ncia da aula, usado para calcular quantas presen�as o aluno vai receber
     * @return
     * @throws Exception
     */
    public boolean registrarFrequenciaDiscente(int idDiscente, int codigoTurmaOuSubturma, String codigoTurma, String codigoComponente, int horarios, Date horaInicialAula, 
    		int duracaoAula, int minTolerAula, boolean iniciouSegundaAula);

    /**
     * Cadastra uma nova turma a partir do pr�prio app desktop, habilitando a turma virtual para que o docente defina sua senha  
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
     * Retorna os discentes com suas respectivas faltas/presen�as para exibir em uma listagem no app desktop
     * 
     * @param codigoComponente c�digo do componente, por exemplo: ECT1103
     * @param codigoTurma c�digo da turma, por exemplo: 02
     * @param temSubturma indica se vai realizar exibir os alunos com presen�a/falta de alguma subturma 
     * @param codigoSubturma usado no caso de estar buscando alunos em alguma subturma
     * @return
     * @throws Exception
     */
    public List<DiscenteDTO> ultimasChamadasRealizadas(String codigoComponente, String codigoTurma, boolean temSubturma, String codigoSubturma);

    /**
     * Registra faltas para os alunos da turma. O n�mero de faltas � o mesmo da quantidade de hor�rio.
     *  
     * @param idTurmaOuSubturma referencia a turma ou subturma selecionada para qual a presen�a ser� registrada
     * @param horarios quantidade de aulas que a turma possui
     * @throws Exception
     */
    public void registrarFaltaParaAlunos(int idTurmaOuSubturma, int horarios, String codigoTurma, String codigoComponente);
    
}