<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>
	

	<jsp:useBean id="hoje" class="java.util.Date"/>
	<c:set var="hoje" scope="request">
		<fmt:formatDate value="${hoje}" type="DATE" pattern="yyyy-MM-dd"/>
	</c:set>
	
	<h2><ufrn:subSistema /> > Solicita��o de Reconsidera��o da Avalia��o</h2>

			<div class="descricaoOperacao">
				<b>Bem-vindo ao cadastro de solicita��es de reconsidera��o.</b> <br/><br/>
	
				Para cada a��o de extens�o listada abaixo voc� pode cadastrar uma solicita��o de reconsidera��o caso 
				discorde da avalia��o realizada pelos membros do comit� de extens�o. 
				Se o icone para realiza��o da solicita��o n�o estiver presente para sua a��o de extens�o, verifique se ela possui as seguintes caracter�sticas:<br/>
				
				<ul>
				    <li>Se est� sob sua coordena��o.</li>
				    <li>Se possui status igual a APROVADO COM RECURSOS, APROVADO SEM RECURSOS ou N�O APROVADA.</li>
				    <li>Se o prazo para solicita��o n�o expirou.</li>
				</ul>
			</div>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;"/>: Solicitar Reconsidera��o
			    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar A��o de Extens�o
			    <h:graphicImage value="/img/extensao/financiamento_faex.png" width="16px" height="16px" style="overflow: visible;"/>: Visualizar Or�amento Aprovado
			    <h:graphicImage url="/img/table_go.png" style="overflow: visible;"/>: Listar Avalia��es
			    <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>: Listar Solicita��es
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Lista de a��es pass�veis de solicita��o de reconsidera��o</caption>

			      <thead>
			      	<tr>
			      		<th width="8%">C�digo</th>
			        	<th>T�tulo da a��o</th>
			        	<th>Situa��o</th>
			        	<th>Prazo para Solicita��o</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        </tr>
			        

				<c:set var="lista" value="#{solicitacaoReconsideracao.acoesExtensaoReconsideraveis}" />

			        <tbody>
				<c:if test="${empty lista}">
	                    <tr> <td colspan="6" align="center"> <font color="red">N�o h� a��es de extens�o passivas de reconsidera��o <br/> ou o usu�rio atual n�o � coordenador de a��es ativas</font> </td></tr>
				</c:if>

				        <c:forEach items="#{lista}" var="atividade" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

											<td width="5%"> ${atividade.codigo} </td>
						                    <td width="60%"> ${atividade.titulo} </td>
						                    <td  width="20%"> ${atividade.situacaoProjeto.descricao} </td>
						                    <td> 
						                    	<fmt:formatDate value="${atividade.editalExtensao.edital.dataFimReconsideracao}" pattern="dd/MM/yyyy"/>
						                    	<h:outputText value="--" rendered="#{empty atividade.editalExtensao.edital.dataFimReconsideracao}" /> 
						                    </td>
											<td width="2%">
													<h:commandLink title="Solicitar Reconsidera��o" action="#{solicitacaoReconsideracao.solicitarReconsideracaoExtensao}"
														rendered="#{atividade.projeto.permitidoSolicitarReconsideracao}" 
														style="border: 0;">
													   	<f:param name="id" value="#{atividade.id}"/>				    	
														<h:graphicImage url="/img/arrow_undo.png"  />
													</h:commandLink>													
											</td>
											<td>
													<h:commandLink id="visualizaAcao" title="Visualizar A��o de Extens�o" action="#{ atividadeExtensao.view }" immediate="true">
														<f:param name="id" value="#{atividade.id}" />
													    <h:graphicImage url="/img/view.gif" />
													</h:commandLink>
											</td>
											<td width="2%">		
													<h:commandLink title="Visualizar Or�amento Aprovado" action="#{atividadeExtensao.view}" immediate="true">
														<f:param name="id" value="#{atividade.id}"/>
						   								<f:param name="orcamentoAprovado" value="true"/>
													    <h:graphicImage url="/img/extensao/financiamento_faex.png" width="16px" height="16px" />
													</h:commandLink>
											</td>
											<td width="2%">
													<h:commandLink id="visualizarAvaliacoes" title="Listar Avalia��es" action="#{ atividadeExtensao.iniciarVisualizarAvaliacaoAtividade }" immediate="true" rendered="#{atividade.projeto.permitidoVisualizarAvaliacao}">
												        <f:param name="id" value="#{atividade.id}"/>
											    		<h:graphicImage url="/img/table_go.png" />
													</h:commandLink>
											</td>
											<td width="2%">
													<h:commandLink title="Listar Solicita��es" action="#{solicitacaoReconsideracao.visualizarSolicitacoesExtensao}"
														rendered="#{not empty atividade.solicitacoesReconsideracao}" 
														style="border: 0;" immediate="true">
													   	<f:param name="id" value="#{atividade.id}"/>				    	
														<h:graphicImage url="/img/listar.gif"  />
													</h:commandLink>													
											</td>
					              </tr>

				        </c:forEach>
				</tbody>

		</table>
		</h:form>

<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>