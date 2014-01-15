<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Finalização de Bolsista </h2>

<c:if test="${ not empty formPlanoTrabalho.referenceData.diaLimite }">
	<div class="descricaoOperacao">
		<p>
			Caro Coordenador,
		</p>
		<c:forEach items="${ formPlanoTrabalho.referenceData.diaLimite }" var="entry">
			<p style="margin: 10px 20px; text-indent: 40px;"> O bolsista cuja bolsa seja <b>${entry.key}</b> deve ser finalizado até o <b>${entry.value}º</b> dia do mês, 
				para que a bolsa seja finalizada no mês corrente. </p>
		</c:forEach>
	</div>
</c:if>

<html:form action="/pesquisa/indicarBolsista" method="post">

<table class="formulario" width="90%">
<caption> Dados da Finalização </caption>
<tbody>
	<tr>
		<th width="25%" style="font-weight: bold"> Projeto de Pesquisa: </th>
		<td colspan="3"> ${formPlanoTrabalho.obj.projetoPesquisa.codigo} - ${formPlanoTrabalho.obj.projetoPesquisa.titulo} </td>
	</tr>
	<tr>
		<th style="font-weight: bold"> Orientador: </th>
		<td colspan="3">
			${formPlanoTrabalho.obj.orientador.pessoa.nome}
		</td>
	</tr>
	<tr>
		<th style="font-weight: bold"> Plano de Trabalho: </th>
		<td colspan="3">
			<ufrn:link action="/pesquisa/planoTrabalho/wizard" param="obj.id=${formPlanoTrabalho.obj.id}&dispatch=view">
			<c:choose>
				<c:when test="${ not empty  formPlanoTrabalho.obj.titulo}">
					${formPlanoTrabalho.obj.titulo}
				</c:when>
				<c:otherwise>
					<em> Título não definido </em>
				</c:otherwise>
			</c:choose>
			</ufrn:link>
		</td>
	</tr>
	<tr>
		<th style="font-weight: bold"> Tipo de Bolsa: </th>
		<td colspan="3">
			${formPlanoTrabalho.obj.tipoBolsaString}
		</td>
	</tr>

	<c:if test="${not empty formPlanoTrabalho.bolsistaAnterior}">
	<tr>
		<th style="font-weight: bold">Bolsista Anterior:</th>
		<td colspan="3">
		<c:choose>
			<c:when test="${not empty formPlanoTrabalho.bolsistaAnterior.discente}">
				${formPlanoTrabalho.bolsistaAnterior.discente}
			</c:when>
			<c:otherwise>
				<i> Não definido </i>
			</c:otherwise>
		</c:choose>
		</td>
	</tr>
	
	<tr>
		<th style="font-weight: bold"> Motivo da Substituição:</th>
		<td colspan="3"> ${ formPlanoTrabalho.bolsistaAnterior.motivoSubstituicao } </td>
	</tr>
	</c:if>
	
	
	<tr>
		<th style="font-weight: bold"> Bolsista Atual: </th>
		<td colspan="3">
		<c:choose>
			<c:when test="${not empty formPlanoTrabalho.bolsistaAtual.discente}">
				${formPlanoTrabalho.bolsistaAtual.discente}
			</c:when>
			<c:otherwise>
				<i> Não definido </i>
			</c:otherwise>
		</c:choose>
		</td>
	</tr>
	<tr>
		<c:choose>
			<c:when test="${formPlanoTrabalho.permissaoGestor}">
					<th class="required"> Data da Finalização: </th>
					<td colspan="3">
						<ufrn:calendar property="dataFinalizacao" />
					</td>
				</c:when>
				<c:otherwise>
					<th> Data da Finalização: </th>
					<td colspan="3">
						${formPlanoTrabalho.dataFinalizacao}
					</td>
				</c:otherwise>
		</c:choose>
	</tr>
	
	<tr>
		<th class="required"> Motivo da Finalização:</th>
		<td>
			<html:select property="motivo">
				<html:option value=""> -- SELECIONE O MOTIVO -- </html:option>
				<html:option value="SAÚDE"> SAÚDE </html:option>
				<html:option value="VÍNCULO EMPREGATÍCIO"> VÍNCULO EMPREGATÍCIO </html:option>
				<html:option value="MUDANÇA DE PROJETO"> MUDANÇA DE PROJETO </html:option>
				<html:option value="CONCLUSÃO DA GRADUAÇÃO"> CONCLUSÃO DA GRADUAÇÃO </html:option>
				<html:option value="A PEDIDO DO ALUNO"> A PEDIDO DO ALUNO </html:option>
				<html:option value="FALECIMENTO"> FALECIMENTO </html:option>
				<html:option value="OUTROS"> OUTROS </html:option>
			</html:select>
		</td>
	</tr>
	<tr>
		<th> Justificativa:</th>
		<td colspan="2">
			<html:text property="bolsistaAtual.motivoSubstituicao" size="45"/>
		</td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="4">
			<html:button dispatch="removerBolsista" value="Finalizar"/>
			<html:button dispatch="popular" value="Cancelar" cancelar="true"/>
		</td>
	</tr>
</tfoot>
</table>

<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>