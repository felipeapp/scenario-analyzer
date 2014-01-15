<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="form">
	
			<h2> <ufrn:subSistema /> &gt; Matrícula &gt; Seleção da Turma </h2>
			
			<div class="descricaoOperacao">
				<p>	<strong>Bem vindo à matrícula de alunos!</strong> </p>
				<p> 
					Para iniciar, selecione a turma na qual deseja matricular 
					os alunos dentre as turmas abertas listadas abaixo.
				</p>
			</div>
		
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Selecionar Turma
			</div>
			<c:if test="${not empty matriculaInfantilMBean.modelTurmas}">
			
			<table class="formulario" width="100%">
			<caption>Turmas Abertas</caption>
			<tr>
			<td>
			<rich:dataTable id="dtTableTurmas" value="#{matriculaInfantilMBean.modelTurmas}" var="t" width="100%">
				<f:facet name="header">
		            <rich:columnGroup>
		                <rich:column>
		                	<h:outputText value="Turma" />
		                </rich:column>
		                <rich:column style="text-align: right;">
		                	<h:outputText value="Matriculados" />
		                </rich:column>
		                <rich:column style="text-align: right;">
		                	<h:outputText value="Capacidade" />
		                </rich:column>
		                <rich:column>
		                	<f:facet name="verbatim">&nbsp;</f:facet>
		                </rich:column>
		          	</rich:columnGroup>
		        </f:facet>
		        <rich:column>
		        	<h:outputText value="#{t.descricaoTurmaInfantil }" escape="false"/>
		        </rich:column>
		        <rich:column style="text-align: right;">
		        	<h:outputText value="#{t.qtdMatriculados }" />
		        </rich:column>
		        <rich:column style="text-align: right;">
		        	<h:outputText value="#{t.capacidadeAluno}" />
		        </rich:column>
		        <rich:column>
		        	<h:commandButton id="selecionar" action="#{matriculaInfantilMBean.selecionarTurma}" image="/img/seta.gif" />
		        </rich:column>
		    </rich:dataTable>
		    </td>
		    </tr>
		    </table>
		<%-- 	
				<table class="listagem">
					<caption>Turmas Abertas</caption>
					<thead>
						<tr>
							<th>Turma</th>
							<th>Matriculados</th>
							<th>Capacidade</th>
							<th></th>
						</tr>
					</thead>
					<c:forEach items="#{matriculaInfantilMBean.turmas}" var="turma" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${ turma.descricaoTurmaInfantil }</td>
							<td>${ turma.qtdMatriculados }</td>
							<td>${ turma.capacidadeAluno }</td>
							<td>
								<h:commandLink title="Alterar Turma" action="#{ matriculaInfantilMBean.selecionarTurma }" >
							        <f:param name="id" value="#{turma.id}"/>
						    		<h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</table>
		--%>
			</c:if>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>