
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<a4j:keepAlive beanName="tutoriaIMD"/>

<h2> <ufrn:subSistema /> > Vincular/Alterar Tutor </h2>

<h:form id="form">
	<table class="formulario" style="width: 100%">
	  <caption>Dados da Turma</caption>
		<h:inputHidden value="#{tutoriaIMD.obj.id}"/>
		<h:inputHidden value="#{tutoriaIMD.modulo.id}"/>
		
			<!-- DADOS GERAIS -->
			<!-- Cursos do IMD -->
			<tr>
				<th width="25%" class="rotulo">Curso:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.cronograma.curso.descricao}"/>
				</td>
			</tr>
			
			<!--Lista de Módulos -->
			<tr>
				<th class="rotulo">Módulo:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.modulo.descricao}"/>
				</td>
			</tr>
			
			<!-- Cronograma de Execução -->
			<tr>
				<th class="rotulo">Cronograma Execução:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.cronograma.descricao}"/>
				</td>
			</tr>
			
			<!-- Opção Polo/Grupo -->
			<tr>
				<th class="rotulo">Polo/Grupo:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.obj.turmaEntrada.opcaoPoloGrupo.descricao}"/>
				</td>
			</tr>		
			
			<!-- Nome da Turma -->
			<tr>
				<th class="rotulo">Nome da Turma:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.obj.turmaEntrada.especializacao.descricao}" /> 
				</td>
			</tr>
			<tr>
				<th class="rotulo">Horário:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.obj.turmaEntrada.dadosTurmaIMD.horario}" /> 
				</td>
			</tr>
			
			<tr>
				<th class="rotulo">Local:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.obj.turmaEntrada.dadosTurmaIMD.local}"/> 
				</td>
			</tr>
			
			<tr>
				<th class="rotulo">Capacidade:</th>
				<td>
					<h:outputText value="#{tutoriaIMD.obj.turmaEntrada.capacidade}"/> 
				</td>
			</tr>
			
			<%--Tutor IMD --%> 
			<tr>
				<th class="obrigatorio">Tutor:</th>
				<td>
					<h:selectOneMenu value="#{tutoriaIMD.obj.tutor.id}" id="tutor" required="true" >
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{tutoriaIMD.tutoresCombo}" /> 
					</h:selectOneMenu>	
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<a4j:outputPanel id="disciplinas">
					<table class="subFormulario" style="width: 100%">
					<c:if test="${!empty tutoriaIMD.listaDisciplinas}">
				 	  <caption class="subFormulario">Disciplinas vinculadas (${fn:length(tutoriaIMD.listaDisciplinas)})</caption>
				 	   
						<thead>
							<tr>
								<th style="text-align: left;">Código</th>
								<th style="text-align: left;">Disciplina</th>
								<th style="text-align: left;">Carga Horária</th>
								
							</tr>
						</thead>
						
						<tbody>		   
							   <c:forEach var="linha" items="#{tutoriaIMD.listaDisciplinas}" varStatus="status" >
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
										<td width="15%" style="text-align: left;">${linha.disciplina.codigo}</td>
										<td width="15%" style="text-align: left;">${linha.disciplina.nome}</td>
										<td width="15%" style="text-align: left;">${linha.disciplina.chTotal}</td>	
									</tr>
							   </c:forEach>
						</tbody>
						
						
						<tfoot>
						   
						</tfoot>
						</c:if> 
					</table>
					</a4j:outputPanel>
				</td>
			</tr>
		
		
		<tfoot>
			<tr>
				<td colspan="2">
									
					<c:choose>
						<c:when test="${tutoriaIMD.cadastro}">
							<h:commandButton value="Cadastrar" action="#{tutoriaIMD.cadastrar}" id="cadastrar"/>
						</c:when>
						<c:otherwise>
							<h:commandButton value="Alterar" action="#{tutoriaIMD.alterar}" id="alterar" />
						</c:otherwise>
					</c:choose>

					<h:commandButton value="Cancelar" action="#{tutoriaIMD.cancelarVinculo}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		   
		</tfoot>
		
			
	</table>
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>