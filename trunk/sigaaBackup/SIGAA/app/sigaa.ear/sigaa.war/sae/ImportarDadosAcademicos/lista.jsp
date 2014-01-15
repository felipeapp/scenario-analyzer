<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Dados Acad�micos Consolidados</h2>
	<br>

	<h:form id="form">
	
			<center>
			<div class="infoAltRem">
		        <h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>: Visualizar �ndices
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;" /> Remover �ndices
			</div>
		</center>
	
		<table class="formulario" width="70%">
			<caption class="listagem">Consolida��o dos Dados Acad�micos</caption>
				<thead>
					<tr>
						<th style="text-align: center;"> Ano Refer�ncia </th>
						<th style="text-align: center;"> Data Cadastro </th>
						<th> Usu�rio Cadastro </th>
						<th></th>
						<th></th>
					</tr>
				</thead>
			
			<c:forEach items="#{ dadosIndiceAcaMBean.allAtivos }" var="linha">
				<tr>
					<td style="text-align: center;"> ${ linha.anoReferencia } </td>
					<td style="text-align: center;"> <ufrn:format type="data" valor="${ linha.dataCadastro }"></ufrn:format> </td>
					<td> ${ linha.pessoaCadastro } </td>
					<td width="20">
						<h:commandLink action="#{ dadosIndiceAcaMBean.visualizar }" >
							<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar �ndices" />
							<f:param name="ano" value="#{ linha.anoReferencia }"/>
						</h:commandLink>
					</td>
					<td width="20">
						<h:commandLink action="#{ dadosIndiceAcaMBean.remover }" onclick="#{confirmDelete}" >
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover �ndices" />
							<f:param name="ano" value="#{ linha.anoReferencia }"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>