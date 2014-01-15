package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO com informaï¿½ï¿½es sobre os membros de uma fila de atendimento,<br>
 * usado na integraï¿½ï¿½o com o sistema de atendimento<br>
 * do departamento de administraï¿½ï¿½o de pessoal.
 * @author Rafael Moreira
 *
 */
public class MembroFilaDto implements Serializable {

    private static final String SEM_NOME = "NÃƒO INFORMADO";
    public static final String AGUARDANDO_ATENDIMENTO = "AGUARDANDO";
    public static final String EM_ATENDIMENTO = "EM ATENDIMENTO";
    public static final String FINALIZADO = "FINALIZADO";
    public static final String DESISTIU = "DESISTIU";
    /** Atributo de serializaï¿½ï¿½o da classe */
    private static final long serialVersionUID = -1L;
    /** Demais atributos */
    private int id;
    private ServidorDTO servidorDTO;
    private int sequencia;
    private int statusAtendimento;
    private String descricaoFicha;
    private int idFila;
    private Date dataHoraEntradaFila;
    private Date dataHoraChamada;
    private Date dataHoraTermino;
    private boolean preferencial;

    public MembroFilaDto() {
        servidorDTO = new ServidorDTO();
    }

    public ServidorDTO getServidorDTO() {
        return servidorDTO;
    }

    public void setServidorDTO(ServidorDTO servidorDTO) {
        this.servidorDTO = servidorDTO;
    }

    public String getDescricaoFicha() {
        return descricaoFicha;
    }

    public void setStatusAtendimento(int statusAtendimento) {
        this.statusAtendimento = statusAtendimento;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setIdFila(int idFila) {
        this.idFila = idFila;
    }

    public int getIdFila() {
        return idFila;
    }

    public void setDataHoraEntradaFila(Date dataHoraEntradaFila) {
        this.dataHoraEntradaFila = dataHoraEntradaFila;
    }

    public Date getDataHoraEntradaFila() {
        return dataHoraEntradaFila;
    }

    public boolean isPreferencial() {
        return preferencial;
    }

    public void setPreferencial(boolean preferencial) {
        this.preferencial = preferencial;
    }

    public Date getDataHoraChamada() {
        return dataHoraChamada;
    }

    public void setDataHoraChamada(Date dataHoraChamada) {
        this.dataHoraChamada = dataHoraChamada;
    }

    public Date getDataHoraTermino() {
        return dataHoraTermino;
    }

    public void setDataHoraTermino(Date dataHoraTermino) {
        this.dataHoraTermino = dataHoraTermino;
    }

    public boolean isMembroServidor() {
        if (servidorDTO != null) {
            if (servidorDTO.getPessoa() != null) {
                if (servidorDTO.getPessoa().getNome() == null) {
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public String getNomeSiape() {
        if (isMembroServidor()) {
            return servidorDTO.getPessoa().getNome() + " ( " + servidorDTO.getSiape() + " )";
        } else {
            return SEM_NOME;
        }
    }

    public int getStatusAtendimento() {
        return statusAtendimento;
    }
    public String getDescStatusAtendimento () {
    	if (statusAtendimento == 1) {
            return AGUARDANDO_ATENDIMENTO;
        } else if (statusAtendimento == 2) {
            return EM_ATENDIMENTO;
        } else if (statusAtendimento == 3) {
            return FINALIZADO;
        } else if (statusAtendimento == 4) {
            return DESISTIU;
        } else {
            return "Não informado";
        }
    }
}
