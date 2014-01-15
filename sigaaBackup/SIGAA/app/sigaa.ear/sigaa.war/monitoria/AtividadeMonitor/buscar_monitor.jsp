<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<a4j:keepAlive beanName="consultarMonitor" />
    <h2><ufrn:subSistema /> > Cadastrar Novo Relatório de Atividades</h2>
    
    <c:if test="${acesso.monitoria}">
        <%@include file="/monitoria/ConsultarMonitor/include/buscar.jsp"%>
    </c:if>
	
	<c:if test="${not empty monitores}">
	    <div class="infoAltRem">
	        <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
	        <h:graphicImage value="/img/monitoria/document_new.png" rendered="#{acesso.monitoria}"/><h:outputText value=": Novo Relatório" rendered="#{acesso.monitoria}"/>
	        <h:graphicImage value="/img/view.gif" rendered="#{acesso.monitoria}"/><h:outputText value=": Listar Relatórios" rendered="#{acesso.monitoria}"/>
	    </div>
	</c:if>
	
    <br/>
    <c:if test="${empty monitores && consultarMonitor.buscaRealizada}">
       <center><i> Nenhum monitor localizado </i></center>
    </c:if>

    <c:if test="${not empty monitores}">
        <h:form id="form">
           <table class="listagem">
            <caption>Monitores Encontrados (${ fn:length(monitores) })</caption>
    
              <thead>
                <tr>
                    <th>Discente</th>
                    <th>Vínculo</th>
                    <th>Situação</th>
                    <th style="text-align: center">Início</th>
                    <th style="text-align: center">Fim</th>                
                    <th>&nbsp;</th>
                </tr>
            </thead>
            <tbody>
    
            <c:set var="projeto" value=""/>     
            <c:forEach items="#{monitores}" var="monitor" varStatus="status">
    
                    <c:if test="${ projeto != monitor.projetoEnsino.id }">
                        <c:set var="projeto" value="${ monitor.projetoEnsino.id }"/>
                        <tr>
                            <td colspan="6" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
                                ${ monitor.projetoEnsino.anoTitulo }
                            </td>
                        </tr>                   
                    </c:if>
    
                   <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
                        <td width="50%"> ${monitor.discente.matriculaNome} </td>
                        <td> ${monitor.tipoVinculo.descricao} </td>
                        <td> 
                            <c:set var="cor" value="${((monitor.assumiuMonitoria) and (monitor.ativo)) ? 'blue':'red'}"/>
                            <font color="${cor}">${monitor.situacaoDiscenteMonitoria.descricao}</font> 
                        </td>
                        <c:choose>
                        	<c:when test="${ not empty monitor.dataInicio}"><td width="2%" style="text-align: center"> <fmt:formatDate value="${monitor.dataInicio}" pattern="dd/MM/yyyy" /> </td></c:when>
                        	<c:when test="${ empty monitor.dataInicio}"> <td width="2%" style="text-align: center"> - </td></c:when>
                       </c:choose>
                       <c:choose>
                        <c:when test="${ not empty monitor.dataInicio}"><td width="2%" style="text-align: center"> <fmt:formatDate value="${monitor.dataFim}" pattern="dd/MM/yyyy" /> </td></c:when>
                        <c:when test="${ empty monitor.dataInicio}"><td width="2%" style="text-align: center"> - </td></c:when>
                       </c:choose>
                        <td width="6%">
                            <h:commandLink  title="Visualizar Dados do Monitor" action="#{ consultarMonitor.view }" 
                                     styleClass="noborder" id="link_visualizar_dados_monitor_">
                                   <f:param name="id" value="#{monitor.id}" id="idMonitorView"/>
                                   <h:graphicImage url="/img/monitoria/user1_view.png" id="img_view_monitor_"/>                                    
                            </h:commandLink>

                            <h:commandLink title="Novo Relatório"
								action="#{ atividadeMonitor.iniciarCadastroAtividadeGestor }"
								id="cadastrar_frequencia_" rendered="#{acesso.monitoria && monitor.assumiuMonitoria}">
								<f:param name="id" value="#{monitor.id}" />
								<h:graphicImage url="/img/monitoria/document_new.png" />
							</h:commandLink>
							
                            <h:commandLink  title="Listar Relatórios" action="#{ atividadeMonitor.listarAtividades }" id="listar_frequencias" rendered="#{acesso.monitoria}">                         
                                  <f:param name="idDiscenteMonitoria" value="#{monitor.id}"/>
                                  <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>
                            </h:commandLink>    
                        </td>
    
                   </tr>
                  </c:forEach>
             </tbody>
           </table>
         </h:form>
    </c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>