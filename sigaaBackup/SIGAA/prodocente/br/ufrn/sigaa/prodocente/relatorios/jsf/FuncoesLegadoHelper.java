/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

/**
 * Funções que vieram do sistema anterior
 * @author Gleydson
 *
 */
public class FuncoesLegadoHelper {


	public static int calcMeses(String dataIni, String dataFim, int ano){
        try{
            int mes1 = 1;
            int mes2= 12;
            int ano1 = ano;
            int ano2 = ano;
            int meses = 0;

            if(!dataIni.equals("")) {
                ano1 = Integer.parseInt(dataIni.substring(6,10));
            }

            if(!dataFim.equals("")) {
                ano2 = Integer.parseInt(dataFim.substring(6,10));
            }

            if( ((ano1 == ano) || (ano1 == ano-1)) && (!dataIni.equals(""))){
                mes1 = Integer.parseInt(dataIni.substring(3,5));
            }

            if(((ano2 == ano) || (ano2 == ano-1)) && (!dataFim.equals(""))) {
                mes2 = Integer.parseInt(dataFim.substring(3,5));
            }

            if(ano1 < ano){
                if(ano2 > ano){
                    meses = Math.min((12-mes1+1),2);
                    meses = meses + 10;
                } else if(ano2==ano){
                    meses = Math.min((12-mes1+1),2);
                    meses = meses + Math.min(mes2,10);
                } else if(ano2==ano-1 && mes2 >= 11){
                    if(ano1==ano-1 && mes1 >= 11){
                        meses = Math.max((mes2-mes1)+1,0);
                    } else {
                        meses = Math.max((mes2-11)+1,0);
                    }
                }
            } else if(ano1==ano){
                if(ano2 > ano){
                    meses = Math.min(10-mes1+1, 10);
                } else if(ano2==ano){
                    meses = mes2-mes1+1;
                }
            }
            return meses;
        } catch(Exception e){
            return 0;
        }
    }
}
