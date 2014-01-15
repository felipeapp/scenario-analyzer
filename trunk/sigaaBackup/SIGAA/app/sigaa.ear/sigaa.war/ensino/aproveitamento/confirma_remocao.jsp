<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema /> > Cancelar aproveitamento de estudos &gt; Dados do aproveitamento</h2>

	<h:messages showDetail="true"></h:messages>
	<table class="visualizacao" width="80%">
	<tr>
		<th> Discente: </th>
		<td> ${aproveitamento.obj.discente.matricula } - ${aproveitamento.obj.discente.nome }</td>
	</tr>
	<tr>
		<th> Curso: </th>
		<td> ${aproveitamento.obj.discente.curso.descricao} </td>
	</tr>
	<ufrn:subSistema teste="tecnico">
		<tr>
			<th> Turma de Entrada: </th>
			<td> ${aproveitamento.obj.discente.turmaEntradaTecnico.descricao} </td>
		</tr>
	</ufrn:subSistema>
	<ufrn:subSistema teste="graduacao">
		<tr>
			<th> Currículo: </th>
			<td> ${aproveitamento.obj.discente.curriculo.descricao} </td>
		</tr>
	</ufrn:subSistema>
	</table>
	<br />
	
	<h:form>
	<table class="formulario" width="100%">

		<tr><td>
		<table class="subFormulario" width="100%">

	<!--  -->
	
			<caption class="listagem">Dados do Aproveitamento
			${(aproveitamento.cancelar)?" a ser Cancelado":""}
			</caption>

	
			<tr><td>
			<h:dataTable width="100%" value="#{aproveitamento.listaDisciplinasParaRemocao}" var="item" rowClasses="linhaPar, linhaImpar">
							
				<h:column>				
				        <f:facet name="header">
                            <h:outputText value="Atividade/Disciplina"/>
                        </f:facet>						
				 		<h:outputText value="#{item.componente}" />		 					  						
				</h:column>
												
				<h:column>
						<f:facet name="header">
                            <h:outputText value="Tipo de Aproveitamento" />
                        </f:facet>					
					 	<h:outputText value="#{item.situacaoMatricula.descricao}" />						 					  						
				</h:column>
								
				<h:column>				
						<f:facet name="header">
                            <h:outputText value="Ano-Período" />
                        </f:facet>					
						 <h:outputText value="#{item.ano}" />-<h:outputText value="#{item.periodo}" />						 					  						
				</h:column>
				
				<h:column>
						<f:facet name="header">
                            <h:outputText value="Resultado"/>
                        </f:facet>
                        
						<h:outputText value="#{item.mediaFinal}" rendered="#{item.metodoNota}"/>						 					  						
						<h:outputText value="#{item.conceitoChar}" rendered="#{item.metodoConceito}"/>
						<c:if test="${item.metodoAptidao}"><ufrn:format type="bool_sn" valor="${item.nota}"/></c:if>
				</h:column>
								
				<h:column>					
						<f:facet name="header">
                            <h:outputText value="Número Faltas" />
                        </f:facet>				
						 <h:outputText value="#{item.numeroFaltas}" />						 					  						
				</h:column>
																
			</h:dataTable>
			</td></tr>


		</table>
		</td></tr>


		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Confirmar Exclusão"	action="#{aproveitamento.confirmar}" id="btConfirmar" />
					<h:commandButton value="Cancelar" onclick="#{confirm}"	action="#{aproveitamento.cancelar}" id="btCancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<c:set var="exibirApenasSenha" value="true" scope="request"/>
	<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>

	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
