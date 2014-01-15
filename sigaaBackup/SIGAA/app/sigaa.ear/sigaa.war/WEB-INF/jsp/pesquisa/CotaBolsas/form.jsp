<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Definição do Período de Cotas
</h2>


<html:form action="/pesquisa/cotaBolsas" method="post" focus="obj.descricao">
<html:hidden property="obj.id" />
<table class="formulario" width="95%">
    <caption>Cadastrar novo período</caption>
    <tbody>
       <tr>
           <th class="obrigatorio">Descrição da Cota:</th>
           <td>
           		<html:text property="obj.descricao" size="20" maxlength="50"/>
           </td>
		</tr>
		<tr>
           <th class="obrigatorio">Período de Vigência dos Planos de Trabalho:</th>
           <td>
           		<ufrn:calendar property="dataInicio" /> a <ufrn:calendar property="dataFim" />
           </td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
		<td colspan="2">
	   		<html:button dispatch="cadastrar" value="Confirmar"/>
	   		<html:button dispatch="cancelar" value="Cancelar"/>
		</td>
		</tr>
	</tfoot>
</table>
</html:form>

<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

<c:choose>
	<c:when test="${!empty cotas}">
		<br/>
		<div class="infoAltRem">
		    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
		    : Alterar dados da Cota
		</div>
		<table class="listagem">
			<caption> Períodos Cadastrados </caption>
			<thead>
				<tr>
					<th align="left"> Descrição da Cota </th>
					<th> Início </th>
					<th> Fim </th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="cota" items="${cotas}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> ${cota.descricao}</td>
					<td > <ufrn:format type="data" name="cota" property="inicio" /></td>
					<td > <ufrn:format type="data" name="cota" property="fim" /></td>
					<td width="15">
						<html:link action="/pesquisa/cotaBolsas?dispatch=popular&id=${cota.id}">
							<img src="<%= request.getContextPath() %>/img/alterar.gif" alt="Alterar dados da Cota" title="Alterar dados da Cota" border="0"/>
						</html:link>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<br/>
		<p style="text-align: center; font-style: italic;">
			Nenhum período cadastrado!
		</p>
	</c:otherwise>
</c:choose>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>