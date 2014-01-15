<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Dados do Pólo</h2>

	<h:outputText value="#{ regiaoMatriculaBean.create }"/>

	<table class="formulario" width="95%">
		<caption>Dados da Região de Campus para Matrícula</caption>
		<tr>
			<th width="50%"><b>Nome:</b> </th>
			<td  width="50%"> ${ regiaoMatriculaBean.obj.nome } </td>
		</tr>
		<tr>
			<th><b>Nível de Ensino:</b> </th>
			<td> ${ regiaoMatriculaBean.obj.nivelDesc } </td>
		</tr>
		
		<tr>
			<th valign="top"><b>Campus:</b></th>
			<td> 
				<c:forEach var="rmc" items="${regiaoMatriculaBean.obj.regioesMatriculaCampus}">
				${ rmc.campusIes.nome }<br/>
				</c:forEach> 
			</td>
		</tr>
	</table>
	<br/>
	<h:form>
		<p align="center">
			<h:commandLink value="Voltar" action="#{regiaoMatriculaBean.listar}" id="botaoVoltar"/>
		</p>	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
