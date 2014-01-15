<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h2><ufrn:subSistema /> > Consultar Discentes de Projetos</h2>

	<%@include file="/projetos/form_busca_discentes.jsp"%>	
	<c:set var="discentes" value="#{discenteProjetoBean.listDiscentes}"/>
	<br/>
	
	<c:if test="${not empty discentes}">
		<div class="infoAltRem">
		    <h:graphicImage value="/img/projetos/user1_view.png" style="overflow: visible;"/>: Dados do Discente
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Detalhes do Projeto
		    <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Plano de Trabalho
		    <h:graphicImage value="/img/email_go.png" style="overflow: visible;"/>: Enviar Email
		</div>	
	</c:if>


	<c:if test="${not empty discentes}">

		<h:form id="formLista">

			 <table class="listagem">
			    <caption>Discentes Encontrados (${ fn:length(discentes) })</caption>
		
			      <thead>
			      	<tr>
			      		<th>Matrícula</th>
			        	<th width="35%">Discente</th>
			        	<th>Vínculo</th>
			        	<th>Situação</th>		        	
			        	<th>Início</th>
			        	<th>Fim</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>			        
			        	<th>&nbsp;</th>
			        </tr>
			 	</thead>
			 	<tbody>
	
			 	<c:set var="atividade" value=""/>
		       	<c:forEach items="#{discentes}" var="dp" varStatus="status">
		
			       			<c:if test="${ projeto != dp.projeto.id }">
								<c:set var="projeto" value="${ dp.projeto.id }"/>
								<tr>
										<td colspan="8" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
											${dp.projeto.anoTitulo}
										</td>
										<td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
											<h:commandLink title="Detalhes do Projeto" action="#{ projetoBase.view }" immediate="true">
												        <f:param name="id" value="#{dp.projeto.id}"/>
											    		<h:graphicImage url="/img/view.gif" />
											</h:commandLink>
										</td>
								</tr>						
							</c:if>
		
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		                    
		                    <td> ${dp.discente.matricula}</td>
		                    <td> ${dp.discente.nome}<br/>
		                    <i>${dp.discente.curso.nomeCompleto}</i></td>
							<td> ${dp.tipoVinculo.descricao} </td>
							<td> ${dp.situacaoDiscenteProjeto.descricao} </td>
							<td> <fmt:formatDate value="${dp.dataInicio}" pattern="dd/MM/yyyy"/> </td> 
							<td> <fmt:formatDate value="${dp.dataFim}" pattern="dd/MM/yyyy"/> </td>
							
							<td width="2%">
								<h:commandLink title="Dados do Discente" action="#{ discenteProjetoBean.view }" id="btnDiscenteView">
								      <f:param name="idDiscenteProjeto" value="#{dp.id}"/>
								      <h:graphicImage url="/img/projetos/user1_view.png" />
								</h:commandLink>
							</td>
							
							<td  width="2%">
								<h:commandLink action="#{planoTrabalhoProjeto.view}" id="btnPlanoView"
									title="Plano de Trabalho" style="border: 0;" rendered="#{not empty dp.planoTrabalhoProjeto}">
								      <f:param name="id" value="#{dp.id}"/>
						              <h:graphicImage url="/img/report.png" />
								</h:commandLink>
							</td>

							<td  width="2%">
								<h:commandLink action="#{discenteProjetoBean.enviarMensagem}" id="btnEnviarEmail"
									title="Enviar Email" style="border: 0;">
								      <f:param name="id" value="#{dp.id}"/>
						              <h:graphicImage value="/img/email_go.png"/>
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