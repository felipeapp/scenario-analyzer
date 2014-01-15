<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Consultoria Especial
</h2>

<html:form action="/pesquisa/cadastroConsultoriaEspecial" method="post" focus="obj.consultor.nome" styleId="form">
	<html:hidden property="obj.id" />

	<table class="formulario" width="95%">
        <caption>Consultoria Especial</caption>
        <tbody>
			<tr>
				<th width="25%" style="vertical-align: middle;"> Consultor: </th>
				<td colspan="3">
					<c:set var="idAjax" value="obj.consultor.id"/>
					<c:set var="nomeAjax" value="obj.consultor.nome"/>
					<%@include file="/WEB-INF/jsp/include/ajax/consultor.jsp" %>
				</td>
			</tr>
			<tr>
				<th>Período da Consultoria:
					<span class="obrigatorio"></span>
				</th>
				<td>
					<ufrn:calendar property="dataInicio"/> a <ufrn:calendar property="dataFim"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<html:button dispatch="persist" value="Confirmar"/>
					<html:button dispatch="cancelar" value="Cancelar" cancelar="true" />
	    		</td>
		    </tr>
		</tfoot>
	</table>
</html:form>

<p style="line-height: 3em; margin: 10px 25%; background: #F1F1F1; border: 1px solid #DDD;">
	<ufrn:link action="/pesquisa/notificarConsultores" param="dispatch=notificarConsultoresEspeciais"
		style="background: url(${ctx}/img/email_go.png) no-repeat 10px 50%; display: block; padding-left: 40px;">
		Notificar consultores especiais externos
	</ufrn:link>
</p>

<table class="listagem">
	<caption> Comissão Especial </caption>
	<thead>
	<tr>
		<th> Área de Conhecimento </th>
		<th> Consultor </th>
		<th> Tipo </th>
		<th> Período da Consultoria </th>
		<th> </th>
	</tr>
	</thead>

	<tbody>
		<c:forEach var="consultoria" items="${consultorias}" varStatus="loop">
		<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>
				${ consultoria.consultor.areaConhecimentoCnpq.nome }
			</td>
			<td>
				${ consultoria.consultor.nome }  <br />
				<em>${ consultoria.consultor.email }</em>
			</td>
			<td> ${ consultoria.consultor.interno ? "INTERNO" : "EXTERNO"  }</td>
			<td>
				<ufrn:format type="data" name="consultoria" property="dataInicio"/> a <ufrn:format type="data" name="consultoria" property="dataFim"/>
			</td>
			<td>
				<ufrn:link action="/pesquisa/cadastroConsultoriaEspecial" param="id=${consultoria.id}&dispatch=edit">
					<img src="${ctx}/img/alterar.gif"
						alt="Alterar Consultoria Especial"
						title="Alterar Consultoria Especial" />
				</ufrn:link>
				<ufrn:link action="/pesquisa/cadastroConsultoriaEspecial" param="id=${consultoria.id}&dispatch=remove">
					<img src="${ctx}/img/delete.gif"
						alt="Remover Consultoria Especial"
						title="Remover Consultoria Especial" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>

	<tfoot>
	</tfoot>
</table>

<br />
<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>