<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> >
	Avaliação de Planos de Trabalhos de Iniciação Científica
</h2>

<html:form action="/pesquisa/avaliarPlanoTrabalho" method="post" focus="obj.parecerConsultor">
<html:hidden property="obj.id"/>
<input type="hidden" name="fromAvaliacaoProjeto" value="${formPlanoTrabalho.fromAvaliacaoProjeto}" />

<style>
	.formulario p {
		padding: 2px 8px 10px;
		line-height: 1.25em;
	}
</style>

<center>
	<div class="infoAltRem">
		<img src="${ctx}/img/view.gif" alt="Visualizar Relatório" title="Visualizar Projeto de Pesquisa"/> Visualizar Projeto de Pesquisa
        <img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Plano de Trabalho" title="Visualizar Plano de Trabalho"/> Visualizar Plano de Trabalho
		<img src="${ctx}/img/listar.gif" alt="Visualizar Relatório Parcial" title="Visualizar Relatório Parcial"/> Visualizar Relatório Parcial
	</div>
</center>

<table class="formulario" width="90%">
<caption> Avaliação de Plano de Trabalho de Iniciação Científica</caption>
<tbody>
	<tr>
		<th width="25%"> <b> Projeto de Pesquisa:</b> </th>
		<td> ${formPlanoTrabalho.obj.projetoPesquisa.codigoTitulo} 
			<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${formPlanoTrabalho.obj.projetoPesquisa.id}">
				<img src="${ctx}/img/view.gif" alt="Visualizar Relatório" title="Visualizar Projeto de Pesquisa"/>
			</html:link>
		</td>
	</tr>
	<tr>
		<th> <b> Orientador: </b> </th>
		<td height="30"> ${formPlanoTrabalho.obj.orientador.pessoa.nome }</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario" style="text-align: center">Corpo do Plano de Trabalho</td>
	</tr>
	<tr>
		<td> <b> Título </b> </td>
	</tr>
	<tr>
		<td colspan="2">
			<p> <ufrn:format type="texto" name="formPlanoTrabalho" property="obj.titulo"/></p>
		</td>
	</tr>
	<tr>
		<td> <b>Objetivos</b></td>
	</tr>
	<tr> <td colspan="2"> <p><ufrn:format type="texto" name="formPlanoTrabalho" property="obj.objetivos"/></p></td> </tr>
	<tr>
		<td> <b>Metodologia </b> </td>
	</tr>
	<tr>
		<td colspan="2"> <p> <ufrn:format type="texto" name="formPlanoTrabalho" property="obj.metodologia"/> </p></td>
	</tr>
	<tr>
		<td> <b>Referências </b> </td>
	</tr>
	<tr>
		<td colspan="2"> <p> <ufrn:format type="texto" name="formPlanoTrabalho" property="obj.referencias"/> </p></td>
	</tr>
<tr> <td colspan="2" style="margin:0; padding: 0;">
			<div style="overflow: auto; width: 99%">
			<table id="cronograma" class="subFormulario" width="100%">
			<caption style="text-align: center"> Cronograma de Atividades </caption>
			<thead>
				<tr>
					<th width="30%" rowspan="2"> Atividade </th>
					<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano">
					<th colspan="${fn:length(ano.value)}" style="text-align: center" class="inicioAno fimAno">
						${ano.key}
					</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano">
						<c:forEach items="${ano.value}" var="mes" varStatus="status">
						<c:set var="classeCabecalho" value=""/>
						<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
						<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>

						<th class="${classeCabecalho}" style="text-align: center"> ${mes}	</th>
						</c:forEach>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades" value="${fn:length(formPlanoTrabalho.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes" value=",${fn:join(formPlanoTrabalho.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
				<tr>
					<th> ${formPlanoTrabalho.telaCronograma.atividade[statusAtividades.index-1]} </th>
					<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
						<c:forEach items="${ano.value}" var="mes">
							<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
							<c:set var="classeCelula" value=""/>
							<c:if test="${ fn:contains(valoresCheckboxes, valorCheckbox) }">
								<c:set var="classeCelula" value="selecionado"/>
							</c:if>
							<td align="center" class="${classeCelula}" >
								&nbsp;
							</td>
						</c:forEach>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>
			</table>
			 </div>
			</td>
		</tr>
	<tr><td height="10"> </td> </tr>
	
	<c:if test="${not empty formPlanoTrabalho.obj.projetoPesquisa.projetoOriginal}">
	<tr> <td colspan="2" style="margin:0; padding: 0;">
    <table class="subFormulario" width="100%">
	<caption>Planos de trabalho vinculados ao projeto anterior</caption>
        <thead>
        	<tr>
			    <th style="text-align: left"> Título </th>
			    <th style="text-align: left"> Tipo de Bolsa </th>
			    <th style="text-align: left">  </th>
	       </tr>
        </thead>
        <tbody>
		<c:forEach var="plano" items="${formPlanoTrabalho.obj.projetoPesquisa.projetoOriginal.planosTrabalho}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${plano.titulo}</td>
				<td>${plano.tipoBolsaString}</td>
				<td width="7%">
					<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=view&obj.id=${plano.id}">
						<img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Plano de Trabalho" title="Visualizar Plano de Trabalho"/>
					</html:link>&nbsp;&nbsp;&nbsp;
					<ufrn:link action="/pesquisa/relatorioBolsaParcial" param="idRelatorio=${plano.relatorioBolsaParcial.id}&dispatch=view">
						<img src="${ctx}/img/listar.gif" alt="Visualizar Relatório Parcial" title="Visualizar Relatório Parcial"/>
					</ufrn:link>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</td></tr>
	<tr><td height="10"> </td> </tr>
	</c:if>
	
	<tr>
		<td colspan="2" class="subFormulario"> Parecer
			<html:img page="/img/required.gif" style="overflow: visible;"/> 
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<html:textarea property="obj.parecerConsultor" rows="6" style="width: 95%;" />
		</td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="2">
			<html:button dispatch="aprovar" value="Aprovado"/>
			<html:button dispatch="aprovarComRestricao" value="Aprovado Com Restrições"/>
			<html:button dispatch="reprovar" value="Não Aprovado"/>
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
		</td>
	</tr>
</tfoot>
</table>
</html:form>

<br /><br />
<div class="obrigatorio">Campos de preenchimento obrigatório</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>