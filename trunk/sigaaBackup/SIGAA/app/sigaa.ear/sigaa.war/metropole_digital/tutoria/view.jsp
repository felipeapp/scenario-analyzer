<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>

<a4j:keepAlive beanName="tutoriaIMD"/>




<h:form id="form">
	<table class="tabelaRelatorio" style="width: 100%">
	  <caption>Dados da Turma ${tutoriaIMD.obj.turmaEntrada.especializacao.descricao}</caption>
		<h:inputHidden value="#{tutoriaIMD.obj.id}"/>
		<h:inputHidden value="#{tutoriaIMD.modulo.id}"/>
		<tbody>
		
			<!-- DADOS GERAIS -->
			<!--Lista de Módulos -->
			<tr>
				<th>Módulo:</th>
				<td colspan="4">${tutoriaIMD.modulo.descricao}</td>
			</tr>
			
			<!-- Cronograma de Execução -->
			<tr>
				<th>Cronograma Execução:</th>
				<td colspan="4">${tutoriaIMD.cronograma.descricao}</td>
			</tr>
			
			<!-- Opção Polo/Grupo -->
			<tr>
				<th>Polo/Grupo:</th>
				<td colspan="4">${tutoriaIMD.opcaoPoloGrupo.descricao}</td>
			</tr>		
			
			<!-- Nome da Turma -->
			<tr>
				<th>Nome da Turma:</th>
				<td>
					${tutoriaIMD.obj.turmaEntrada.especializacao.descricao} 
				</td>
			</tr>
			<tr>
				<th >Horário:</th>
				<td>
					${tutoriaIMD.obj.turmaEntrada.dadosTurmaIMD.horario} 
				</td>
			</tr>
			
			<tr>
				<th>Local:</th>
				<td>
					${tutoriaIMD.obj.turmaEntrada.dadosTurmaIMD.local} 
				</td>
			</tr>
			
			<tr>
				<th>Capacidade:</th>
				<td>
					${tutoriaIMD.obj.turmaEntrada.capacidade} 
				</td>
			</tr>
			
			<!-- Tutor IMD -->
			<tr>
				<th>Tutor:</th>
				<td>${tutoriaIMD.obj.tutor.pessoa.nome}	</td>
			</tr>
		</tbody>
		<tr>
	</table>
	
	<a4j:outputPanel id="disciplinas">
	<table class="listagem" style="width: 100%">
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
		</c:if> 
		
	</table>
	</a4j:outputPanel>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>