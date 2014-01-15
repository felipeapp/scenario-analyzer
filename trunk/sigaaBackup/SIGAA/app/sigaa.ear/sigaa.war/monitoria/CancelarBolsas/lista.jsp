<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
    <h:messages/>
    <h2><ufrn:subSistema /> > Relat�rios de Atividade dos Monitores</h2>
    <h:outputText value="#{atividadeMonitor.create}"/>

    <div class="infoAltRem">
        <h:graphicImage value="/img/view.gif"   style="overflow: visible;"/>: Visualizar Relat�rio do Monitor               
    </div>

    <br/>

    <h:form id="form">
        <table class="listagem" width="100">
            <caption>Lista de Relat�rios de Atividades Encontrados (${fn:length(atividadeMonitor.atividades)})</caption>
            <thead>
                <tr>
                    <th>Monitor</th>
                    <th>M�s/Ano</th>
                    <th>Freq.</th>
                    <th>Validado</th>
                    <th>V�nculo</th>
                    <th></th>           
                    <th></th>                       
                </tr>
            </thead>
        
            <c:if test="${empty atividadeMonitor.atividades}">
                    <tr> <td colspan="5" align="center"> <font color="red">N�o h� Relat�rios de Atividades pendentes de avalia��o com os crit�rios informados!</font> </td></tr>
            </c:if>
        
            <c:if test="${not empty atividadeMonitor.atividades}">
        
                <c:forEach items="#{atividadeMonitor.atividades}" var="atividade" varStatus="status">
                       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
                
                            <td> ${atividade.discenteMonitoria.discente.matriculaNome} </td>            
                            <td> <fmt:formatNumber value="${atividade.mes}" pattern="00" />/${ atividade.ano } </td>
                            <td>
                                <c:if test="${(atividade.dataValidacaoOrientador != null)}">
                                    <c:set var="cor" value="${(atividade.frequencia < 100) ? 'red' : 'black'}"/>
                                    <font color="${cor}">${atividade.frequencia}%</font>
                                </c:if>
                            </td>
                                                
                            <td> <fmt:formatDate pattern="dd/MM/yyyy" value="${atividade.dataValidacaoOrientador}"/> </td>
                            <td> ${atividade.discenteMonitoria.tipoVinculo.descricao} </td>         
        
                            <td>
                                <h:commandLink id="btnVisualizar" action="#{atividadeMonitor.visualizarRelatorioMonitor}" style="border: 0;">
                                      <f:param name="id" value="#{atividade.id}"/>
                                      <h:graphicImage url="/img/view.gif" />
                                </h:commandLink>
                            </td>
                            
                        </tr>           
                </c:forEach>                
            </c:if>
            
            <tfoot>
                <tr>
                    <td colspan="8" align="center"><input type="button" onclick="javascript:history.back();" value=" << Voltar" /></td>
                </tr>
            </tfoot>
        </table>
    </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>