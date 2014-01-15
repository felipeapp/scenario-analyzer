<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Dados do P�lo</h2>

	<h:outputText value="#{ poloBean.create }"/>

	<table class="formulario" width="95%">
	<caption>Dados do P�lo</caption>
	<tr>
		<th width="50%"><b>P�lo:</b> </th>
		<td  width="50%"> ${ poloBean.obj.cidade.nomeUF } </td>
	</tr>
	<tr>
		<th><b>Endere�o:</b> </th>
		<td> ${poloBean.obj.endereco} </td>
	</tr>
	<tr>
		<th><b>Telefone:</b></th>
		<td> ${poloBean.obj.telefone} </td>
	</tr>
	<tr>
		<th><b>CEP:</b></th>
		<td> ${poloBean.obj.cep} </td>
	</tr>
	<tr>
		<th><b>Hor�rio de Funcionamento:</b></th>
		<td> ${poloBean.obj.horarioFuncionamento} </td>
	</tr>
	<tr>
		<th valign="top"><b>Cursos:</b></th>
		<td> 
			<c:forEach var="pc" items="${poloBean.obj.polosCursos}">
			${ pc.curso.descricao }<br/>
			</c:forEach> 
		</td>
	</tr>
	
	</table>
	
	<p align="center"><a href="${ctx}/ead/Polo/lista.jsf">Voltar</a></p>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
