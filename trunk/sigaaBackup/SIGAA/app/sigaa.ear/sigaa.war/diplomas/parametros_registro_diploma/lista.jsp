<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Parâmetros do Registro de Diplomas</h2>

	<h:form id="form">
		<table class="listagem">
			<caption>Parâmetros</caption>
			<thead>
				<tr>
					<th>Código</th>
					<th>Nome</th>
					<th>Valor</th>
					<th></th>
				</tr>
			</thead>
			<c:forEach var="param" items="#{ parametrosRegistroDiploma.listaParametros }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td valign="top" align="right">${ param.codigo }</td>
					<td valign="top">
						<strong><ufrn:format type="texto" valor="${ param.nome }" lineWrap="30" /></strong><br />
						<em><ufrn:format type="texto" valor="${ param.descricao }" lineWrap="40" /></em>
					</td>
					<td valign="top">
						<ufrn:format type="texto" valor="${ param.valor }" lineWrap="25" />
					</td>
					<td valign="top" width="5%" align="right">
						<h:commandLink action="#{ parametrosRegistroDiploma.atualizar }">
							<f:param name="codigo" value="#{ param.codigo }" />
							<f:verbatim>
								<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
							</f:verbatim>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>