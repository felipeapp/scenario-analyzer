<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.AtividadeExtensao"%>

<f:view>

<h2><ufrn:subSistema /> > Reativar Ação de Extensão</h2>
<h:messages showDetail="true" showSummary="true"/>

<h:form id="form">

<table class=formulario width="100%">
    <caption class="listagem">Dados da Ação de Extensão</caption>
    <%@include file="/extensao/Atividade/include/dados_atividade.jsp"%>
    
    <tr>
            <td colspan="2"><h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Requisitos para Reativação da Proposta</h3></td>
    </tr>
    
    <tr>
            <td colspan="2">
              <center>
                <table class="formulario" width="50%">
                    <caption class="listagem">Dados para Reativação</caption>
                    <tbody>
                        <tr>
                            <th><b> Data de finalização atual: </b></th>
                            <td><h:outputText value="#{atividadeExtensao.obj.dataFim}"/></td>
                        </tr>                    
                        <tr>
                            <th><b> Nova data de finalização da ação: </b></th>
                            <td>
                                <t:inputCalendar  id="dataFim" value="#{atividadeExtensao.dataNovaFinalizacao}" 
                                    renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
                                    size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
                                    <f:converter converterId="convertData" />
                                </t:inputCalendar>
                                <br/>
                            </td>
                        </tr>
                    </tbody>
                  </table>
                </center>
            </td>           
    </tr>
        
    <tfoot>
        <tr>
            <td colspan="2">
                <h:commandButton value="Confirmar Reativação" action="#{atividadeExtensao.reativarAcaoExtensao}" id="bt_confirmar_reativar"/>
                <h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="bt_cancelar"/>
            </td>
        </tr>
    </tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>