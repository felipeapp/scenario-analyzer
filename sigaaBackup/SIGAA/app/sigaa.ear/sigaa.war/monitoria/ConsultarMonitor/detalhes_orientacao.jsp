<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:messages/>
	<h2><ufrn:subSistema /> > Dados da Orientação</h2>
	<br>

	<h:outputText value="#{consultarMonitor.create}"/>
 
	<center>
	
		<h3>Dados da Orientação</h3>
		<br/>
		<br/>
		
		<table class="formulario" width="90%">
			<tr>
				<th><b>Matrícula:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.discenteMonitoria.discente.matricula }" /></td>
			</tr>

			<tr>
				<th><b>Nome:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.discenteMonitoria.discente.pessoa.nome }" /></td>
			</tr>

			<tr>
				<th><b>Telefone Fixo:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.discenteMonitoria.discente.pessoa.endereco.telefoneFixo}" /></td>
			</tr>

			<tr>
				<th><b>Telefone Celular:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.discenteMonitoria.discente.pessoa.endereco.telefoneCelular}" /></td>
			</tr>


			<tr>
				<th><b>E-Mail:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.discenteMonitoria.discente.pessoa.email}" /></td>
			</tr>

			<tr>
				<th><b>Bolsista:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.discenteMonitoria.bolsista==true ? 'SIM':'NÃO' }" /></td>
			</tr>


			<tr>
				<th><b>Discente Ativo:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.discenteMonitoria.ativo==true ? 'Sim':'Não' }" /></td>
			</tr>


			<tr>
				<th><b>Orientador:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.equipeDocente.servidor.pessoa.nome }" /></td>
			</tr>

			<tr>
				<th><b>Início:</b></th>
				<td><fmt:formatDate value="${consultarMonitor.orientacao.dataInicio }"  pattern="dd/MM/yyyy"/></td>
			</tr>


			<tr>
				<th><b>Fim:</b></th>
				<td><fmt:formatDate value="${consultarMonitor.orientacao.dataFim }" pattern="dd/MM/yyyy"/></td>
			</tr>


			<tr>
				<th><b>Status:</b></th>
				<td><c:out value="${consultarMonitor.orientacao.status }" /></td>
			</tr>

			
		</table>
		
		<br/>
		<a href="javascript:history.go(-1)">Voltar</a>
		<br/>
				
	</center>	 

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>